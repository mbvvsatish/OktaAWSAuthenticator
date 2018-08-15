package com.aws.sts.outh.okta;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.aws.sts.auth.utils.CookieHelper;
import com.aws.sts.outh.Environment;


public class OktaSaml { 

    private Environment environment;

    private OktaAuthentication authentication;

    public OktaSaml(Environment environment) {
        this.environment = environment;
    }

    public String getSamlResponse() throws IOException {
        authentication = new OktaAuthentication(environment);

        if (!reuseSession()) {
            String oktaSessionToken = authentication.getOktaSessionToken();
            return getSamlResponseForAws(oktaSessionToken);
        } else {
            return getSamlResponseForAwsRefresh();
        }
    }

    private String getSamlResponseForAws(String oktaSessionToken) throws IOException {
        Document document = launchOktaAwsAppWithSessionToken(environment.getOktaAwsAppUrl(), oktaSessionToken);
        Elements samlResponseInputElement = document.select("form input[name=SAMLResponse]");
        if (samlResponseInputElement.isEmpty()) {
            throw new RuntimeException("You do not have access to AWS through Okta. \nPlease contact your administrator.");
        }
        return samlResponseInputElement.attr("value");
    }

    private String getSamlResponseForAwsRefresh() throws IOException {
        Document document = launchOktaAwsApp(environment.getOktaAwsAppUrl());
        Elements samlResponseInputElement = document.select("form input[name=SAMLResponse]");
        if (samlResponseInputElement.isEmpty()) {
            throw new RuntimeException("You do not have access to AWS through Okta. \nPlease contact your administrator.");
        }
        return samlResponseInputElement.attr("value");
    }

    private Document launchOktaAwsAppWithSessionToken(String appUrl, String oktaSessionToken) throws IOException {
        return launchOktaAwsApp(appUrl + "?onetimetoken=" + oktaSessionToken);
    }

    private Document launchOktaAwsApp(String appUrl) throws IOException {
        CookieStore cookieStore = new BasicCookieStore();
        Properties loadedProperties = new Properties();

        HttpGet httpget = new HttpGet("https://"+environment.getOktaOrg()+appUrl);
        loadedProperties.load(new FileReader(CookieHelper.getCookies().toFile()));
        loadedProperties.entrySet().stream().map(entry -> {
            BasicClientCookie basicClientCookie = new BasicClientCookie(entry.getKey().toString(), entry.getValue().toString());
            basicClientCookie.setDomain(environment.getOktaOrg());

            return basicClientCookie;
        }).forEach(cookieStore::addCookie);

        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).useSystemProperties().build();
             CloseableHttpResponse oktaAwsAppResponse = httpClient.execute(httpget)) {

            if (oktaAwsAppResponse.getStatusLine().getStatusCode() >= 500) {
                throw new RuntimeException("Server error when loading Okta AWS App: "
                        + oktaAwsAppResponse.getStatusLine().getStatusCode());
            } else if (oktaAwsAppResponse.getStatusLine().getStatusCode() >= 400) {
                throw new RuntimeException("Client error when loading Okta AWS App: "
                        + oktaAwsAppResponse.getStatusLine().getStatusCode());
            }

            Properties properties = new Properties();
            cookieStore.getCookies().stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue))
                    .forEach(properties::setProperty);
            properties.store(new FileWriter(CookieHelper.getCookies().toFile()), "");

            return Jsoup.parse(
                    oktaAwsAppResponse.getEntity().getContent(),
                    StandardCharsets.UTF_8.name(),
                    appUrl
            );
        }
    }

    private boolean reuseSession() throws JSONException, IOException {
        CookieStore cookieStore = new BasicCookieStore();
        Properties loadedProperties = new Properties();
        loadedProperties.load(new FileReader(CookieHelper.getCookies().toFile()));
        loadedProperties.entrySet().stream().map(entry -> {
            BasicClientCookie basicClientCookie = new BasicClientCookie(entry.getKey().toString(), entry.getValue().toString());
            basicClientCookie.setDomain(environment.getOktaOrg());

            return basicClientCookie;
        }).forEach(cookieStore::addCookie);

        Optional<String> sidCookie = cookieStore.getCookies().stream().filter(cookie -> "sid".equals(cookie.getName())).findFirst().map(Cookie::getValue);

        if (!sidCookie.isPresent()) {
            return false;
        }

        HttpPost httpPost = new HttpPost("https://" + environment.getOktaOrg() + "/api/v1/sessions/me/lifecycle/refresh");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Cookie", "sid=" + sidCookie.get());

        try (CloseableHttpClient httpClient = HttpClients.createSystem()) {
            CloseableHttpResponse authnResponse = httpClient.execute(httpPost);

            return authnResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        }
    }
}

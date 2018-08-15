package com.aws.sts.outh;

import java.io.Serializable;
import java.util.Date;

public class Environment implements Serializable {

    private String user;
    private String pwd;
    private Boolean saveCreds;
    private Boolean autoRefresh;
    private String oktaOrg;
    private String oktaAwsAppUrl;
    private String roleArn;
    private String idpArn;
    private String region;
    
    private Boolean writeProfile;
    private String profile;
    private String ec2MetadataUrl;
    private Boolean hostEc2AMi;
    
	private Date lastRunTime;
	private Date expiryTime;

    
	public Environment(String oktaOrg, String user, String pwd, Boolean saveCreds, Boolean autoRefresh, 
			String oktaAwsAppUrl, String roleArn, String idpArn, String region, String profile, Boolean writeProfile, String ec2MetadataUrl, Boolean hostEc2AMi) {
		this.oktaOrg = oktaOrg;
        this.user = user;
        this.pwd = pwd;
        this.saveCreds = saveCreds;
        this.oktaAwsAppUrl = oktaAwsAppUrl;
        this.roleArn = roleArn;
        this.idpArn = idpArn;
        this.region = region;
        this.autoRefresh = autoRefresh;
        this.profile = profile;
        this.ec2MetadataUrl = ec2MetadataUrl;
        this.hostEc2AMi = hostEc2AMi;
        this.writeProfile = writeProfile;
	}

	public String getOktaOrg() {
		return oktaOrg;
	}

	public void setOktaOrg(String oktaOrg) {
		this.oktaOrg = oktaOrg;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Boolean getSaveCreds() {
		return saveCreds;
	}

	public void setSaveCreds(Boolean saveCreds) {
		this.saveCreds = saveCreds;
	}

	public Boolean getAutoRefresh() {
		return autoRefresh;
	}

	public void setAutoRefresh(Boolean autoRefresh) {
		this.autoRefresh = autoRefresh;
	}

	public String getOktaAwsAppUrl() {
		return oktaAwsAppUrl;
	}

	public void setOktaAwsAppUrl(String oktaAwsAppUrl) {
		this.oktaAwsAppUrl = oktaAwsAppUrl;
	}

	public String getRoleArn() {
		return roleArn;
	}

	public void setRoleArn(String roleArn) {
		this.roleArn = roleArn;
	}

	public String getIdpArn() {
		return idpArn;
	}

	public void setIdpArn(String idpArn) {
		this.idpArn = idpArn;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Date getLastRunTime() {
		return lastRunTime;
	}

	public void setLastRunTime(Date lastRunTime) {
		this.lastRunTime = lastRunTime;
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getEc2MetadataUrl() {
		return ec2MetadataUrl;
	}

	public void setEc2MetadataUrl(String ec2MetadataUrl) {
		this.ec2MetadataUrl = ec2MetadataUrl;
	}

	public Boolean getHostEc2AMi() {
		return hostEc2AMi;
	}

	public void setHostEc2AMi(Boolean hostEc2AMi) {
		this.hostEc2AMi = hostEc2AMi;
	}

	public Boolean getWriteProfile() {
		return writeProfile;
	}

	public void setWriteProfile(Boolean writeProfile) {
		this.writeProfile = writeProfile;
	}

}

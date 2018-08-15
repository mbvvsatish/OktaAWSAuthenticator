package com.aws.sts.auth.utils;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSAnyImpl;
import org.opensaml.saml.saml2.core.Assertion;

import java.util.Collection;
import java.util.stream.Collectors;

final class AssertionUtils {
    static Collection<String> getAttributeValues(Assertion assertion, String attributeName) {
        return assertion.getAttributeStatements()
                .stream()
                .flatMap(x -> x.getAttributes().stream())
                .filter(x -> attributeName.equals(x.getName()))
                .flatMap(x -> x.getAttributeValues().stream())
                .map(AssertionUtils::getAttributeValue)
                .filter(x -> x != null && !x.isEmpty())
                .collect(Collectors.toList());
    }

    private static String getAttributeValue(XMLObject attributeValue) {
        return
                attributeValue == null ?
                        null :
                        attributeValue instanceof XSString ?
                                ((XSString) attributeValue).getValue() :
                                attributeValue instanceof XSAnyImpl ?
                                        ((XSAnyImpl) attributeValue).getTextContent() :
                                        attributeValue.toString();
    }
}
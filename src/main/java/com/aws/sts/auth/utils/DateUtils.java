package com.aws.sts.auth.utils;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtils {

	public static XMLGregorianCalendar dateToXMLGregorianCalendar(Date date, TimeZone zone) {
	    XMLGregorianCalendar xmlGregorianCalendar = null;
	    GregorianCalendar gregorianCalendar = new GregorianCalendar();
	    gregorianCalendar.setTime(date);
	    gregorianCalendar.setTimeZone(zone);
	    try {
	      DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();
	      xmlGregorianCalendar = dataTypeFactory.newXMLGregorianCalendar(gregorianCalendar);
	    }
	    catch (Exception e) {
	      System.out.println("Exception in conversion of Date to XMLGregorianCalendar" + e);
	    }
	    
	    return xmlGregorianCalendar;
	  }
	  
	  public static Date xmlGregorianCalendarToDate(XMLGregorianCalendar xmlGregorianCalendar, TimeZone zone) {
	    TimeZone.setDefault(zone);
	    return new Date(xmlGregorianCalendar.toGregorianCalendar().getTimeInMillis());
	  }
}

package com.gemalto.eziomobile.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ConvertDateToStringDate {
	
	
	
	/** Convert Data type data to String type date
	 * @param dateToConvert
	 * @return String date in dd/MM/yyyy format
	 */
	public static String convertDateToString(Date dateToConvert) {
		SimpleDateFormat dateformater_Java = new SimpleDateFormat("dd/MM/yyyy");
	    String dateStr = dateformater_Java.format(dateToConvert);
	    
		return dateStr;
	}
	
	/** Convert Data type data to String type date
	 * @param dateToConvert
	 * @return String date in dd-MM-yyyy HH:mm:ss format
	 */
	public static String convertDateToStringOther(Date dateToConvert) {
		SimpleDateFormat dateformater_Java = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    String dateStr = dateformater_Java.format(dateToConvert);
	    
		return dateStr;
	}
	
	/**
	 * @return Current time of the system, dd/MM/yyyy hh:mm a format
	 */
	public static String getCurrentLocalDateTimeStamp() {
	    return LocalDateTime.now()
	       .format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
	}
	
	/**
	 * @return Converted date object
	 */
	public static Date convertStringToDate(String strDate) {
		Date date = null; 
		try {
			SimpleDateFormat formatter =new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			date=formatter.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Error : Unable to convert String date to Date!");
		}
		return date;  
	}
}

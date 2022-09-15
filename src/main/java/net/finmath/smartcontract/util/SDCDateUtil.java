/*
 * SDCDateUtil 
 *
 * @author Dietmar Schnabel
 */
package net.finmath.smartcontract.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Set;


/**
 * The Class SDCDateUtil.
 */
public class SDCDateUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(SDCDateUtil.class);
	
	public static String MARKET_DATA_AS_JSON_1 = "marketDataAsJson1";
	public static String MARKET_DATA_AS_JSON_2 = "marketDataAsJson2";
	public static String TRADE_AS_FPML = "tradeAsFPML";
	public static String DATE_FORMAT_yyyyMMdd ="yyyyMMdd";
	public static String DATE_FORMAT_yyyy_MM_dd ="yyyy-MM-dd";
	
	/**
	 * Checks if is following business days.
	 *
	 * @param day1 the day 1
	 * @param day2 the day 2
	 * @param calendar the calendar
	 * @return true, if is following business day
	 */
	public static boolean isFollowingBusinessDays(LocalDate day1, LocalDate day2, BusinessdayCalendar calendar) {
		LocalDate firstDay;
		LocalDate secondDay;
		LocalDate compare;
		
		if (day1.isEqual(day2)) {
			logger.error("Both dates are equal. they need to be T and T-1! " + "Day1= " + day1 + ", Day2 = " +day2);
			return false;
		}
		
		if (!calendar.isBusinessday(day1)) {
			logger.error(day1 + " is not a business day!");
			return false;
		}
		
		if (!calendar.isBusinessday(day2)) {
			logger.error(day2 + " is not a business day!");
			return false;
		}
		
		if (day1.isAfter(day2)) {
			firstDay = day2;
			secondDay = day1;
		} else {
			firstDay = day1;
			secondDay = day2;
		}
		
		compare = getPreviousBusinessDay(secondDay, calendar);
		if(!firstDay.isEqual(compare)) {
			logger.error("Both dates are not T and T-1 in terms of business days! " + "Day1= " + day1 + ", Day2 = " +day2);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the previous business day.
	 *
	 * @param day the day
	 * @param calendar the calendar
	 * @return the previous business day
	 */
	public static LocalDate getPreviousBusinessDay(LocalDate day, BusinessdayCalendar calendar) {
		LocalDate compare;
		compare = day.plusDays(-1);
		
		if(calendar.isBusinessday(compare)) {
			if (logger.isDebugEnabled()) {logger.debug("The business day before " +  day + " is: " + compare);}
			return compare;	
		}
		
		while(!calendar.isBusinessday(compare)) {
			compare = compare.plusDays(-1);
			
			if(calendar.isBusinessday(compare)) {
				if (logger.isDebugEnabled()) {logger.debug("The business day before " +  day + " is: " + compare);}
				return compare;	
			}
		}
		return compare;
	}
	
	/**
	 * Gets the date from JSON.
	 *
	 * @param json the json
	 * @param formatString the format string
	 * @return the date from JSON
	 */
	public static LocalDate getDateFromJSON (String json, String formatString) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(formatString);
		String dateString =  getDateStringFromJSON( json);
		LocalDate date = LocalDate.parse(dateString,dateFormat);
		
		return date;
	}
	
	/**
	 * Gets the date from a string.
	 *
	 * @param dateString the date string
	 * @param formatString the format string
	 * @return the date 
	 */
	public static LocalDate getDateFromString (String dateString, String formatString) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(formatString);
		LocalDate date = LocalDate.parse(dateString,dateFormat);
		return date;
	}
	
	
	/**
	 * Gets the date string from JSON Curve.
	 *
	 * @param json the json
	 * @return the date string from JSON
	 */
	public static String getDateStringFromJSON (String json) {
		JsonElement jsonElement = new JsonParser().parse(json);        
        JsonObject jsonObject = jsonElement.getAsJsonObject();
		
        Set<String> keys = jsonObject.keySet();
		Object[] dateString =  keys.toArray();
				
		return (String)dateString[0];
	}
	
	/**
	 * Gets the string from date.
	 *
	 * @param date the date
	 * @param dateFormat the date format
	 * @return the string from date
	 */
	public static String getStringFromDate(Date date, String dateFormat) {
		Objects.requireNonNull(date);
		Objects.requireNonNull(dateFormat);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		return simpleDateFormat.format(date);
	}
	
	/**
	 * Gets the string from date.
	 *
	 * @param date the date
	 * @param dateFormat the date format
	 * @return the string from date
	 */
	public static String getStringFromDate(LocalDate date, String dateFormat) {
		Objects.requireNonNull(date);
		Objects.requireNonNull(dateFormat);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SDCDateUtil.DATE_FORMAT_yyyy_MM_dd);
		Date dateS = null;
		try {
			dateS = simpleDateFormat.parse(date.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getStringFromDate(dateS, dateFormat);
	}
	
	public static boolean isMatured(String date, String format) {
		LocalDate now = LocalDate.now();
		LocalDate maturity = getDateFromString(date, format);
		return now.isAfter(maturity.minusDays(1));
	}

}

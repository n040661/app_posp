package xdt.quickpay.jbb.entity.xml.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义日期操作类
 * 
 * @author 刘剑
 * 
 */
public class DateUtil {
	private static DateFormat format8 = new SimpleDateFormat("yyyyMMdd");
	private static DateFormat format14 = new SimpleDateFormat("yyyyMMddHHmmss");
	private static Pattern pattern = Pattern.compile("-|:|\\s");
	private static Matcher match;
	private static Map<String,Integer> timeUnit = null;
	
	static{
		
		if(timeUnit == null){

			timeUnit = new HashMap<String,Integer>();
			
			timeUnit.put("year",Calendar.YEAR);
			
			timeUnit.put("month",Calendar.MONTH);
			
			timeUnit.put("day",Calendar.DATE);
			
			timeUnit.put("week",Calendar.WEEK_OF_YEAR);

		}
	}
	/**
	 * 日期操作
	 * 
	 * @param date
	 * 
	 * @param field �?��作的时间字段,例如Calendar.YEAR
	 * 
	 * @param value 正数为加,负数为减
	 * 
	 * @return
	 * 
	 */
	public static Date add(Date date,String field,int value){
		
		Calendar ca = Calendar.getInstance();
		
		ca.setTime(date);
		
		ca.add(timeUnit.get(field),value);
		
		return ca.getTime();

	}
	
	/**
	 * 获取日期,格式yyyyMMddHHmmss
	 * 
	 * @return String
	 */
	public static String getLongDate() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	/**
	 * 获取日期，格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return String
	 */
	public static String getFormateDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/**
	 * 获取日期，自定义格式
	 * 
	 * @param formate
	 * 
	 * @return
	 */
	public static String getFormateDate(String formate){
		return new SimpleDateFormat(formate).format(new Date());
	}
	
	/**
	 * 获取日期，格式：yyyyMMdd
	 * 
	 * @return String
	 */
	public static String getDate() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}
	public static String getDate(String formate) {
		return new SimpleDateFormat("yyyyMMdd").format(formate);
	}

	/**
	 * 时间累加
	 * 
	 * @param date
	 *            当前日期 格式必须�?yyyyMMddHHmmss
	 * @param second
	 *            累加的时�?单位是秒
	 * @return String
	 */
	public static String dateAddSecond(String date, Long second) {
		try {
			return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date((new SimpleDateFormat("yyyyMMddHHmmss").parse(date).getTime() + second * 1000)));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 时间相减
	 * 
	 * @param date
	 *            当前日期 格式必须�?yyyyMMdd
	 * @param day
	 *            累加的时�?单位是天
	 * @return String
	 */
	public static String dateSub(String date, int day) {
		try {
			return new SimpleDateFormat("yyyyMMdd").format(new Date((new SimpleDateFormat("yyyyMMdd").parse(date).getTime() - day * 24 * 60 * 60 * 1000)));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 时间相加
	 * 
	 * @param date
	 *            当前日期 格式必须�?yyyyMMdd
	 * @param day
	 *            累加的时�?单位是天
	 * @return String
	 */
	public static String dateAdd(String date, int day) {
		try {
			return new SimpleDateFormat("yyyyMMdd").format(new Date((new SimpleDateFormat("yyyyMMdd").parse(date).getTime() + day * 24 * 60 * 60 * 1000)));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param value
	 *            String
	 * @return String
	 */
	public static String formateDate(String value) {
		if (value == null) {
			return null;
		}
		match = pattern.matcher(value);
		return match.replaceAll("");
	}

	/**
	 * 
	 * @param value
	 *            value
	 * @param a
	 *            a
	 * @return String
	 */
	public static String formateDate(String value, int a) {
		if (value == null) {
			return null;
		}
		match = pattern.matcher(value);
		return match.replaceAll("").substring(0, a);
	}

	/**
	 * 
	 * @param a
	 *            a
	 * @param field
	 *            field
	 * @param amount
	 *            amount
	 * @return Date
	 */
	public static Date addTime(Date a, int field, String amount) {
		if (amount == null)
			return null;

		try {
			int am = Integer.parseInt(amount);
			Calendar gre = GregorianCalendar.getInstance();
			gre.setTime(a);
			gre.add(field, am);
			return gre.getTime();
		} catch (NumberFormatException e) {

			return null;
		}

	}

	/**
	 * 
	 * @param value
	 *            value
	 * @param formate
	 *            formate
	 * @return Date Date
	 */
	public static Date formateStringToDate(String value, String formate) {
		if (value == null)
			return null;
		DateFormat format = new SimpleDateFormat(formate);
		try {
			return format.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 
	 * @param d
	 *            d
	 * @return String
	 */
	public static String formDateToString8(Date d) {
		if (d == null)
			return null;
		return format8.format(d);
	}

	/**
	 * 
	 * @param d
	 *            d
	 * @param format
	 *            format
	 * @return String
	 */
	public static String formDateToString(Date d, String format) {
		if (d == null)
			return null;
		DateFormat formatDate = new SimpleDateFormat(format);
		return formatDate.format(d);
	}

	/**
	 * 
	 * @param d
	 *            d
	 * @return String
	 */
	public static String formDateToString14(Date d) {
		if (d == null)
			return null;
		return format14.format(d);
	}

	/**
	 * 返回格式化日�?
	 * 
	 * @param format
	 *            format
	 * @return String
	 */
	public static String getFormatterDate(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}
	
	public static int getDiffDate(java.util.Date date, java.util.Date date1) {
    	return (int) ((date.getTime() - date1.getTime()) / (24 * 3600 * 1000));
    }
}

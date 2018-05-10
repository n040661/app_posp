/**   
 * @{#} DateUtil.java Create on 2009-10-28 下午03:17:51   
 *   
 * Copyright (c) 2007 by JIN.   
 */    
  
/**   
* @author <a href="mailto:zhaojinabc@yahoo.com.cn">mm</a>  
* @version 1.0   
*/
package xdt.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import xdt.model.PmsAgentInfo;




/**
 * @author jzhao
 *
 * 2009-10-28下午03:17:51
 */
public class DateUtil {
    protected static DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINA);
    public static final String MINUTE_PATTERN="yyyy-MM-dd HH:mm";
    public static final String DEFAULT_PATTERN="yyyyMMddHHmmss";
    
    /**
     * Format a Date object to a String. Now use the Medium format of Locale
     * CHINA. The format is YYYY-MM-DD
     */
    public static String format(Date date) {
        //return df.format(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(date);
    }

    public static String format(Date date,String pattern) {
        //return df.format(date);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
        return sdf.format(date);
    }
    
    public static String formatTime(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(time);
    }

    /**
     * Format a Date object to a String. Now use the Medium format of Locale
     * CHINA. The format is YYYYMMDD
     */
    public static String formatWithoutSlash(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * Format a Date object to a String. Now use the Medium format of Locale
     * CHINA. The format is YYYYMMDD
     */
    public static String formatShortWithoutSlash(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.CHINA);
        return sdf.format(date);
    }

    /**
     * Parse a Date object from a String. Now use the Medium format of Locale
     * CHINA. The format is YYYY-MM-DD
     *
     */
    public static Date parse(String str) {
        try {
            return df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Date parseDateTime(String date,String time) {
        try {
        	Calendar c = Calendar.getInstance(Locale.CHINA);
        	
        	Calendar c1 = Calendar.getInstance(Locale.CHINA);
        	c1.setTime(java.sql.Time.valueOf(time));
        	Date dt = parse(date);       	
        	c.setTime( dt);
//
//        	c.set(Calendar.HOUR ,c1.get(Calendar.HOUR));
//        	c.set(Calendar.MINUTE ,c1.get(Calendar.MINUTE));
//        	c.set(Calendar.SECOND ,c1.get(Calendar.SECOND));
        	c1.set(Calendar.YEAR ,c.get(Calendar.YEAR));
        	c1.set(Calendar.MONTH ,c.get(Calendar.MONTH));
        	c1.set(Calendar.DATE ,c.get(Calendar.DATE));    
//        	System.out.println(c);
//        	System.out.println(c1.getTime());        	
            return c1.getTime();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /**
     * Add specified number of days to a date.
     */
    public static Date addDays(Date _date, int _dayNum) {
        Calendar c = Calendar.getInstance();
        c.setTime(_date);
        c.add(Calendar.DATE, _dayNum);
        return c.getTime();
    }

    /**
     * Add specified number of MONTH to a date.
     */
    public static Date addMonths(Date _date, int _monthNum) {
        Calendar c = Calendar.getInstance();
        c.setTime(_date);
        c.add(Calendar.MONTH, _monthNum);
        return c.getTime();
    }
    
    public static Date addMinutes(Date _date, int _minuteNum) {
        Calendar c = Calendar.getInstance();
        c.setTime(_date);
        c.add(Calendar.MINUTE, _minuteNum);
        return c.getTime();
    }
    
    /**
     * Set hour, minute, second, millisecond of _c to 0.
     */
    public static Calendar roundCalendar(Calendar _c) {
        _c.set(Calendar.HOUR_OF_DAY, 0);
        _c.set(Calendar.MINUTE, 0);
        _c.set(Calendar.SECOND, 0);
        _c.set(Calendar.MILLISECOND, 0);
        return _c;
    }

    /**
     * Set hour, minute, second, millisecond of _c to 0.
     */
    public static Date roundDate(Date _d) {
        Calendar c = Calendar.getInstance();
        c.setTime(_d);
        return roundCalendar(c).getTime();
    }

    /**
     * Set hour, minute, second, millisecond of _c to 0.
     */
    public static Date roundDateToMonth(Date _d) {
        Date day = roundDate(_d);
        Calendar c = Calendar.getInstance();
        c.setTime(day);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    public static int getWeek(Date _d) {
        Calendar c = Calendar.getInstance();
        c.setTime(_d);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Get date2 - date1, the result measurement is day
     *
     * @param date1 The first date
     * @param date2 The second date
     * @return The days between date1 and date2,
     *         if date2 is after date1 the result is positive,
     *         if date2 is before date1 the result is negative.
     *         if date2 and date1 are euqal, the result is 0;
     */
    public static double daysBetween(Date date1, Date date2) {
        long date1Time = date1.getTime();
        long date2Time = date2.getTime();
        return ((double) (date2Time - date1Time)) / (1000 * 3600 * 24);
    }
    
    
    public static java.sql.Date getLastMonthFirstDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);        
        c.add(Calendar.MONTH,-1);
        int d = c.getActualMinimum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH,d);
    	return new java.sql.Date(c.getTime().getTime());
    }
    
    public static java.sql.Date getLastMonthEndDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH,-1);
        int d = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH,d);
        c.set(Calendar.HOUR_OF_DAY,23);
        c.set(Calendar.MINUTE,59);
        c.set(Calendar.SECOND,59);
        
    	return new java.sql.Date(c.getTime().getTime());
    }
    
    public static java.sql.Date getMonthFirstDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);        
        //c.add(Calendar.MONTH,-1);
        int d = c.getActualMinimum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH,d);
    	return new java.sql.Date(c.getTime().getTime());
    }
    
    public static java.sql.Date getMonthEndDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //c.add(Calendar.MONTH,-1);
        int d = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH,d);
        c.set(Calendar.HOUR_OF_DAY,23);
        c.set(Calendar.MINUTE,59);
        c.set(Calendar.SECOND,59);
        
    	return new java.sql.Date(c.getTime().getTime());
    }
    
    public static java.sql.Date getLastWeekFirstDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);        
        c.add(Calendar.WEDNESDAY,-1);
        int d = c.getActualMinimum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_WEEK,d);
    	return new java.sql.Date(c.getTime().getTime());
    }
    public static java.sql.Date getLastWeekEndDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.WEDNESDAY,-1);
        int d = c.getActualMaximum(Calendar.DAY_OF_WEEK);
        c.set(Calendar.DAY_OF_WEEK,d);
        
    	return new java.sql.Date(c.getTime().getTime());
    } 
    

    
    
    public static Date parseDateTime(String dateTime) {
    	SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	try {
			return sdf.parse(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
    /**
     * 格式化时间，错误返回null
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date parseByPattern(String dateStr, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);

		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			//throw new UnexpectedException(e);
			return null;
		}catch(Exception e) {
			e.printStackTrace();
			//throw new UnexpectedException(e);
			return null;
		}
	}
}
    

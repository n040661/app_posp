package xdt.dto.ys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 类说明:日期处理类
 */
public class DateUtil {

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
	/**
	 * yyyyMMddHHmmss
	 */
	public static final String DATE_FORMAT_2 = "yyyyMMddHHmmss";
	/**
	 * yyyyMMdd
	 */
	public static final String DATE_FORMAT_3 = "yyyyMMdd";
	/**
	 * HHmmss
	 */
	public static final String DATE_FORMAT_4 = "HHmmss";
	/**
	 * HH
	 */
	public static final String DATE_FORMAT_5 = "HH";
	/**
	 * yyyy-MM-dd
	 */
	public static final String DATE_FORMAT_6 = "yyyy-MM-dd";
	/**
	 * yyyyMMddHHmm
	 */
	public static final String DATE_FORMAT_7 = "yyyyMMddHHmm";
	/**
	 * MMddHHmmss
	 */
	public static final String DATE_FORMAT_8 = "MMddHHmmss";
	/**
	 * yyyyMMdd HH:mm:ss
	 */
	public static final String DATE_FORMAT_9 = "yyyyMMdd HH:mm:ss";
	/**
	 * yyyyMMddHHmmssSSS
	 * 
	 */
	public static final String DATE_FORMAT_10 = "yyyyMMddHHmmssSSS";

	private static Map<String, ThreadLocal<SimpleDateFormat>> mapThreadLocal = new HashMap<String, ThreadLocal<SimpleDateFormat>>();
	private static final Object lockObj = new Object();
	private static DateUtil classInstance = new DateUtil();

	public static DateUtil getInstance() {
		return classInstance;
	}

	public static SimpleDateFormat getSimpleDateFormat(final String pattern) {
		ThreadLocal<SimpleDateFormat> df = mapThreadLocal.get(pattern);
		if (df == null) {
			synchronized (lockObj) {
				df = mapThreadLocal.get(pattern);
				if (df == null) {
					df = new ThreadLocal<SimpleDateFormat>() {
						@Override
						protected SimpleDateFormat initialValue() {
							return new SimpleDateFormat(pattern);
						}
					};
					mapThreadLocal.put(pattern, df);
				}
			}
		}
		return df.get();
	}

	/**
	 * 日期转字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date date, String pattern) {
		String riqi = null;
		if (date != null && StringUtils.isNotBlank(pattern)) {
			SimpleDateFormat dateFormat = getSimpleDateFormat(pattern);
			riqi = dateFormat.format(date);
		}
		return riqi;
	}

	/**
	 * 字符串转日期
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 * @throws ParseException
	 */
	public static Date formatDate(String date, String pattern)
			throws ParseException {
		Date rq = null;
		if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(pattern)) {
			SimpleDateFormat dateFormat = getSimpleDateFormat(pattern);
			try {
				rq = dateFormat.parse(date);
			} catch (ParseException e) {
				throw e;
			}
		}
		return rq;
	}
	
	/**
	 * 日期分加减操作  
	 * @param date 
	 * @param pattern : 返回数据格式
	 * @param value : 正数相加、负数相减
	 * @return
	 */
	public static String getSubDate_minute(Date date,String pattern,int value){
		Calendar nowTime = Calendar.getInstance();
		nowTime.setTime(date);
		nowTime.add(Calendar.MINUTE, value);
		return getSimpleDateFormat(pattern).format(nowTime.getTime());
	}

	/**
	 * 获取当前时间
	 * 
	 * @param pattern
	 *            : 格式化
	 * @return
	 */
	public static String getTime(String pattern) {
		return getSimpleDateFormat(pattern).format(new Date());
	}

	public static void main(String[] args) {
		try {
			String source = "2017-10-19 00:00:00";
			Date date = getSimpleDateFormat(DATE_FORMAT_1).parse(source);
			int value = -20;
			String str = DateUtil.getSubDate_minute(date, DateUtil.DATE_FORMAT_6, value);
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

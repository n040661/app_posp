
package xdt.quickpay.clearQuickPay.util;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/* *
 *类名：UtilDate
 *功能：自定义订单类
 *详细：工具类，可以用作获取系统日期、订单编号等
 *版本：3.3
 *日期：2012-08-17
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
public class UtilDate {
	/**
	 * 年月日时分秒(无下划线) yyyyMMddHHmmss
	 */
	public static final String dtLong = "yyyyMMddHHmmss";
	/**
	 * 年月日时分秒毫秒(无下划线) yyyyMMddHHmmssSSS
	 */
	public static final String dateandtime = "yyyyMMddHHmmssSSS";

	/**
	 * 完整时间 yyyy-MM-dd HH:mm:ss
	 */
	public static final String simple = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 年月日(无下划线) yyyyMMdd
	 */
	public static final String dtShort = "yyyyMMdd";

	/**
	 * 月日(无下划线) MMdd
	 */
	public static final String monthDay = "MMdd";

	/**
	 * 时间 HH:mm:ss
	 */
	public static final String dtTime = "HH:mm:ss";
	/**
	 * 时间 HHmmss
	 */
	public static final String dateTime = "HHmmss";

	/**
	 * 年月日 yyyy-MM-dd
	 */
	public static final String dtDate = "yyyy-MM-dd";
	/**
	 * 时间 HHmm
	 */
	public static final String txTime = "HHmm";

	/**
	 * 获取系统时间，格式：HHmm
	 * 
	 * @return
	 */
	public static String getTXDateTime() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(txTime);
		return df.format(date);
	}

	/**
	 * 返回系统当前时间(精确到毫秒)
	 *
	 * @return 以yyyyMMddHHmmssSSS为格式的当前系统时间
	 */
	public static String getDateAndTimes() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(dateandtime);
		return df.format(date);
	}

	/**
	 * 返回系统当前时间,作为一个唯一的订单编号
	 *
	 * @return 以yyyyMMddHHmmss为格式的当前系统时间
	 */
	public static String getOrderNum() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(dtLong);
		return df.format(date);
	}

	/**
	 * 获取系统当前日期(精确到毫秒)，格式：yyyy-MM-dd HH:mm:ss
	 *
	 * @return
	 */
	public static String getDateFormatter() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(simple);
		return df.format(date);
	}

	/**
	 * 获取系统当期年月日(精确到天)，格式：yyyyMMdd
	 *
	 * @return
	 */
	public static String getDate() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(dtShort);
		return df.format(date);
	}

	/**
	 * 获取系统当期月日(精确到天)，格式：MMdd
	 *
	 * @return
	 */
	public static String getMonthDay() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(monthDay);
		return df.format(date);
	}

	/**
	 * 获取系统当期年月日(精确到天带下划线-)，格式：yyyy-MM-dd
	 *
	 * @return
	 */
	public static String getDayDate() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(dtDate);
		return df.format(date);
	}

	/**
	 * 获取系统时间，格式：HH:mm:ss
	 *
	 * @return
	 */
	public static String getTime() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(dtTime);
		return df.format(date);
	}

	/**
	 * 获取系统时间，格式：HHmmss
	 *
	 * @return
	 */
	public static String getDateTime() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat(dateTime);
		return df.format(date);
	}

	/**
	 * 产生随机的三位数
	 *
	 * @return
	 */
	public static String getThree() {
		Random rad = new Random();
		return rad.nextInt(1000) + "";
	}

	/**
	 * @param time
	 * @return
	 * @author Jeff 将yyyy-MM-dd HH:mm:ss 转换为 yyyyMMddHHmmss
	 */
	public static String transDate(String time) {
		String result = "";
		if (StringUtils.isNotBlank(time)) {
			result = time.replace("-", "").replace(":", "").replace(" ", "");
		}
		return result;
	}

	/**
	 * @param dateTime
	 * @return
	 * @author Jeff 将 2015-05-07 11:23:36字符串截取为 2015-05-07
	 */
	public static String formatDateTimeToDate(String dateTime) {
		String result = "";
		if (StringUtils.isNotBlank(dateTime)) {
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
				result = new SimpleDateFormat("yyyy-MM-dd").format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * @param dateTime
	 * @return
	 * @author Jeff 将 2015-05-07字符串截取为 2015-05
	 */
	public static String formatDateToMonth(String dateTime) {
		String result = "";
		if (StringUtils.isNotBlank(dateTime)) {
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateTime);
				result = new SimpleDateFormat("yyyy-MM").format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 将日期字符串转换日期为格式 yyyyMMdd
	 * 
	 * @param str
	 * @return
	 */
	public static Date stringToDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 将日期字符串（yyyy-MM-dd）转为日期
	 * 
	 * @param
	 * @return Date
	 */
	public static Date strToDate(String str) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 取得日期date以后某日的日期，如果要的得到以前的日期，参数传负值
	 * 
	 * @param date
	 *            基准日期 days 要加的天数
	 * @return
	 */
	public static String addDay(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);
		date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date).toString();
	}

	/**
	 * 是否在给定的分钟内
	 * 
	 * @defineTime 给定的时间
	 * @return
	 */
	public static boolean isInDefiMinit(String defineTime, int minit) {
		if (minit < 0 || StringUtils.isBlank(defineTime)) {
			return false;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
			try {
				Date d = sdf.parse(defineTime);

				if (System.currentTimeMillis() - d.getTime() > minit * 1000 * 60) {
					return false;
				} else {
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}

		}
	}

	public static void main(String[] arg) {
		System.out.println(UtilDate.getDateFormatter());
		System.out.print(UtilDate.addDay(UtilDate.strToDate("2016-01-31"), 1));
	}

	/**
	 * 生成商户订单号
	 * 
	 * @return 返回订单号
	 */
	public static String randomOrder() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

		StringBuffer sb = new StringBuffer();

		sb.append(fmt.format(new Date()));
		sb.append("-");
		sb.append("12906");
		sb.append("-");
		Random rand = new Random();
		int i;
		i = rand.nextInt(1000000);
		sb.append(i);
		return sb.toString();
	}

	/**
	 * 北农商生成商户订单号
	 * 
	 * @return 返回订单号
	 */
	public static String bnsrandomOrder() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		StringBuffer sb = new StringBuffer();

		sb.append("ORDER");
		sb.append(fmt.format(new Date()));
		return sb.toString();
	}
   public String Rands(int start, int stop) {
			Date date = new Date();
			SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
			String number = df1.format(date);
			Long l = null;
			try {
				l = df1.parse(number).getTime();
				int i = 0;
				int c = (int) (Math.random() * 10);
				for (int j = 0; j < 10; j++) {
					i = (int) (Math.random() * 900) + 100;
					l = l + ((i + ((int) (Math.random() * 12233))) * ((int) (Math.random() * 23))); // 加上八位随机数
					l = l - ((i + ((int) (Math.random() * 899)))
							* ((int) (Math.random() * 999))) << ((int) (Math.random() * 10));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // 取得毫秒数
			String a = l.toString(); // 转换成String
			String n = a.substring(start, stop); // 截取八位作为随机数
			return n;
		}
	/**
	 * 快捷生成商户订单号
	 * 
	 * @return 返回订单号
	 */
	public static String PayRandomOrder() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");

		StringBuffer sb = new StringBuffer();

		sb.append("QP");

		sb.append(fmt.format(new Date()));
		
		Integer num= (int) ((Math.random() * 9 + 1) * 100000);
		
		sb.append(num.toString());

		return sb.toString();
	}
	/**
	 * 快捷生成商户订单号
	 * 
	 * @return 返回订单号
	 */
	public static String PayRandomOrders() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");

		StringBuffer sb = new StringBuffer();

		sb.append("QP");

		sb.append(fmt.format(new Date()));
		return sb.toString();
	}
	/**
	 * 网关生成商户订单号
	 * 
	 * @return 返回订单号
	 */
	public static String GateWayRandomOrder() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");

		StringBuffer sb = new StringBuffer();

		sb.append("GW");

		sb.append(fmt.format(new Date()));
		
		Integer num= (int) ((Math.random() * 9 + 1) * 100000);
		
		sb.append(num.toString());

		return sb.toString();
	}
	/**
	 * 代付生成商户订单号
	 * 
	 * @return 返回订单号
	 */
	public static String AccountRandomOrder() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");

		StringBuffer sb = new StringBuffer();

		sb.append("A");

		sb.append(fmt.format(new Date()));
		
		Integer num= (int) ((Math.random() * 9 + 1) * 100000);
		
		sb.append(num.toString());

		return sb.toString();
	}

}

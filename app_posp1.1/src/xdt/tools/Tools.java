package xdt.tools;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * 描述：基础操作函数集合
 * 
 */
public class Tools {
  public Tools() {
  }

  /**
   * 检查字符串是否为空；如果字符串为null,或空串，或全为空格，返回true;否则返回false
   * @param str
   * @return
   */
  public static boolean isStrEmpty(String str) {
    if ((str != null) && (str.trim().length() > 0)) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * 去除字符串的前后空格；如果字符串为null,返回空串;
   * @param str   输入字符串
   * @return      处理的后字符串
   */
  public static String ruleStr(String str) {
    if (str == null) {
      return "";
    } else {
      return str.trim();
    }
  }
  
  /**
   * 字符串转码，把GBK转ISO-8859-1
   * @param str   GBK编码的字符串
   * @return      ISO-8859-1编码的字符串
   */
  public static String GBK2Unicode(String str) {
    try {
      str = new String(str.getBytes("GBK"), "ISO-8859-1");
    } catch (java.io.UnsupportedEncodingException e) {}
    ;
    return str;
  }

  /**
   * 字符串转码，把GBK转ISO-8859-1
   * @param str   ISO-8859-1编码的字符串
   * @return      GBK编码的字符串
   */
  public static String Unicode2GBK(String str) {
    try {
      str = new String(str.getBytes("ISO-8859-1"), "GBK");
    } catch (java.io.UnsupportedEncodingException e) {}
    ;
    return str;
  }

  /**
   *  以字符串的格式取系统时间;格式：YYYYMMDDHHMMSS
   * @return    时间字符串
   */
  public static String getSysTime() {
    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
    return df.format(new java.util.Date());
  }

  /**
   *  以字符串的格式取系统日期;格式：YYYYMMDD
   * @return    日期字符串
   */
  public static String getSysDate() {
    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
    return df.format(new java.util.Date());
  }
  
  /**
   * 按输入的时间格式获取时间串
   * @param format ： 时间的格式 ， 如：yyyy-MM-dd HH:mm:ss ， yyyyMMddHHmmss
   * @return ： 时间字符串
   */
  public static String getSysTimeFormat(String format) {
    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(format);
    return df.format(new java.util.Date());
  }
  
  /**
   * 判断字符串是否是有效的日期字符
   * @param d 需要判断的日期字符串
   * @param format java日期格式 如：yyyy-MM-dd HH:mm:ss ， yyyyMMddHHmmss
   * @return true:有效日期 false：无效日期
   */
  public static boolean isDay(String d, String format){
    try{
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      sdf.setLenient(false);
      sdf.parse(d);
    }catch (Exception e){
      return false;
    }
    return true;
  }
 
  /**
   * 检查字符串是否表示金额，此金额小数点后最多带2位
   * @param str   需要被检查的字符串
   * @return ： true－表示金额，false-不表示金额
   */
  public static boolean checkAmount(String amount){
	  if(amount==null){
	  		return false;
	  	}
	  String checkExpressions;
		checkExpressions="^([1-9]\\d*|[0])\\.\\d{1,2}$|^[1-9]\\d*$|^0$";
		return Pattern.matches(checkExpressions, amount);
	}
  
  /**
   * 获取XML报文元素，只支持单层的XML，若是存在嵌套重复的元素，只返回开始第一个
   * @param srcXML  ： xml串
   * @param element ： 元素
   * @return        ： 元素对应的值
   */
  public static String getXMLValue(String srcXML, String element) {
    String ret = "";
    try {
      String begElement = "<" + element + ">";
      String endElement = "</" + element + ">";
      int begPos = srcXML.indexOf(begElement);
      int endPos = srcXML.indexOf(endElement);
      if (begPos != -1 && endPos != -1 && begPos <= endPos) {
        begPos += begElement.length();
        ret = srcXML.substring(begPos, endPos);
      } else {
        ret = "";
      }
    } catch (Exception e) {
      ret = "";
    }
    return ret;
  }  
}

package xdt.dto.mb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

public class BASEUtil {
	
	
	
	static  Logger  loger=Logger.getLogger(BASEUtil.class);
	 /**  
     * BASE编码  (UTF-8)
     * @param bstr  
     * @return String  
     */    
    public static String encode(String  data){    
    try {
    	BASE64Encoder  en= new BASE64Encoder();
		return  en.encode(data.getBytes("UTF-8"));
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
		loger.error("base64 utf8 编码错误",e);
		return  null;
	}    
    }    
    
    /**  
     * BASE解码  (UTF-8)
     * @param str  
     * @return string  
     */    
    public static String decode(String str){    
    byte[] bt = null;    
    try {    
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();    
        bt = decoder.decodeBuffer(str.replace(" ","+"));  
        return new String(bt,"UTF-8");
    } catch (Exception e) {
        e.printStackTrace(); 
        return null;
    }       
    }   
    
    
    
	 /**  
     * BASE编码  (无编码)
     * @param bstr  
     * @return String  
     */    
    public static String Encode(String  data){    
    try {
		return new sun.misc.BASE64Encoder().encode(data.getBytes());
	} catch (Exception e) {
		e.printStackTrace();
		loger.error("base64 utf8 编码错误",e);
		return  null;
	}    
    }    
    
    /**  
     * BASE解码  (无编码)
     * @param str  
     * @return string  
     */    
    public static String Decode(String str){    
    byte[] bt = null;    
    try {    
        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();    
        bt = decoder.decodeBuffer(str);  
        return new String(bt);
    } catch (Exception e) {
        e.printStackTrace(); 
        return null;
    }       
    }  
    
    
//	 /**  
//     * BASE编码  (无编码)
//     * @param bstr  
//     * @return String  
//     */    
//    public static String Encode(byte[]  data){    
//    try {
//		return new sun.misc.BASE64Encoder().encode(data);
//	} catch (Exception e) {
//		e.printStackTrace();
//		loger.error("base64 utf8 编码错误",e);
//		return  null;
//	}    
//    }
    
    
	 /**  
     * BASE编码  (无编码)
     * @param bstr  
     * @return String  
     */    
    public static String Encode(byte[]  data){    
    try {
		return new String(Base64.encodeBase64(data));
	} catch (Exception e) {
		e.printStackTrace();
		loger.error("base64 utf8 编码错误",e);
		return  null;
	}    
    }
    
	 /**  
     * BASE编码  (无编码)
     * @param bstr  
     * @return String  
     */    
    public static byte[] Dncode(String  data){    
    try {
		return Base64.decodeBase64(data);
	} catch (Exception e) {
		e.printStackTrace();
		loger.error("base64 utf8 编码错误",e);
		return  null;
	}    
    }

}

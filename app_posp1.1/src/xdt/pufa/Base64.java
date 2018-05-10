package xdt.pufa;

import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 
 *********************************************************.<br>
 * [类名] Base64 <br>
 * [描述] (数据加密) <br>
 * [作者] wsx <br>
 * [时间] 2015-7-26 上午11:49:13 <br>
 *********************************************************.<br>
 */
public class Base64 {
	 /**
	  * 
	  *********************************************************.<br>
	  * [方法] getBase64 <br>
	  * [描述] (加密  ) <br>
	  * [参数] (对参数的描述) <br>
	  * [返回] String <br>
	  * [时间] 2015-7-26 上午11:49:21 <br>
	  *********************************************************.<br>
	  */
    public static String getBase64(String str) {  
        byte[] b = null;  
        String s = null;  
        try {  
            b = str.getBytes("utf-8");  
        } catch (UnsupportedEncodingException e) {  
           e.printStackTrace();
        }  
        if (b != null) {  
            s = new BASE64Encoder().encode(b).replaceAll("[\\s*\t\n\r]", "");  
        }  
        return s;  
    }  
  
    /**
     * 
     *********************************************************.<br>
     * [方法] getFromBase64 <br>
     * [描述] (解密  ) <br>
     * [参数] (对参数的描述) <br>
     * [返回] String <br>
     * [时间] 2015-7-26 上午11:49:28 <br>
     *********************************************************.<br>
     */
    public static String getFromBase64(String s) {  
        byte[] b = null;  
        String result = null;  
        if (s != null) {  
            BASE64Decoder decoder = new BASE64Decoder();  
            try {  
                b = decoder.decodeBuffer(s);  
                result = new String(b, "utf-8");  
            } catch (Exception e) {  
            	e.printStackTrace();
            }  
        }  
        return result;
    }  
}

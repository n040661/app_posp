package xdt.dto.hlb;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 
 * @ClassName: Disguiser
 * @Description: 哈希算法的工具类，提供SHA MD5 HMAC等算法
 *
 */
public class Disguiser {

	public static final String ENCODE = "UTF-8";
	private static final String KEY = "8data998mnwepxugnk03-2zirb";

	public static String disguiseMD5(String message) {

		if (null == message) {
			return null;
		}
		return disguiseMD5(message, ENCODE);
	}
	
	public static String disguise(String message){
    	return disguise(message+KEY,ENCODE);
    	
    }
	public static String disguise(String message,String encoding){
    	message = message.trim();
        byte value[];
        try{
            value = message.getBytes(encoding);
        }
        catch(UnsupportedEncodingException e){
            value = message.getBytes();
        }
        MessageDigest md = null;
        try{
            md = MessageDigest.getInstance("SHA");
        }catch(NoSuchAlgorithmException e){
        	e.printStackTrace();
            return null;
        }
        return ConvertUtils.toHex(md.digest(value));
    }

	public static String disguiseMD5(String message, String encoding) {

		if (null == message || null == encoding) {

			return null;
		}

		message = message.trim();
		byte value[];
		try {
			value = message.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			value = message.getBytes();
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return ConvertUtils.toHex(md.digest(value));
	}

	public static void main(String[] args) {
		String sign1 = disguise("&QuickPaySendValidateCode&8001&手机号码与下单时手机号码不一致&C1800000002&p_20170415160738&18520594621&jIa3imBUwh2JFRlMURedREoKPcwikjFy");
		String sign2 = disguise("&QuickPaySendValidateCode&8001&手机号码与下单时手机号码不一致&null&p_20170415160738&18520594621&jIa3imBUwh2JFRlMURedREoKPcwikjFy");
		System.out.println("sign1："+sign1);
		System.out.println("sign2："+sign2);
		System.out.println(sign1.equals(sign2));
	}

}

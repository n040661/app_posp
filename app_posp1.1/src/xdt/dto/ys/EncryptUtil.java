package xdt.dto.ys;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;


/**
 * 加密工具类 
 */
public class EncryptUtil {
	
	protected static Logger logger = Logger.getLogger(EncryptUtil.class);	
	/**
	 * SHA256加密
	 * 
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String sha2Encrypt(String text) {
		MessageDigest sha2 = null;
		try {
			sha2 = MessageDigest.getInstance("SHA-256");
			//System.out.println(bytesToHexFun3(sha2.digest(text.getBytes("utf-8"))));
			return hex(sha2.digest(text.getBytes("utf-8")));
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		return null;
	}
	
	/**
	 * MD5加密
	 * 
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String md5Encrypt(String text) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			return hex(md5.digest(text.getBytes("utf-8")));
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		return null;
	}
	

	/**
	 * 返回16进制字符串
	 * 
	 * @param arr
	 * @return
	 */
	private static String hex(byte[] arr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; ++i) {
			sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1,
					3));
		}
		return sb.toString();
	}
	
//	public static String hex(byte[] bytes) {
//        StringBuilder buf = new StringBuilder(bytes.length * 2);
//        for(byte b : bytes) { // 使用String的format方法进行转换
//            buf.append(String.format("%02x", new Integer(b & 0xff)));
//        }
//
//        return buf.toString();
//    }
	
	
//	public static void main(String args[]){
//		System.out.println(sha2Encrypt("channel_key=Jgl1w5w4wd0pFgFA9hTaOxBR3Ur5K4M9service=requestCheckServiceshiYi_287_K1Y"));
//	}
}

package xdt.quickpay.hengfeng.util;

import java.io.IOException;

public class HFSignUtil {

	/**
	 * 验证签名
	 * 
	 * @param dataString
	 *            待签名
	 * @param signString
	 *            签名字符串
	 * @param key
	 *            商户key
	 * @return
	 * @throws IOException 
	 */
	public static boolean verify(String dataString, String signString, String key) throws IOException {
		dataString = dataString.replace(" ", "+");
		signString = signString.replace(" ", "+");
		String destsrc = sign(dataString, key);
		System.out.println("获取的key值:"+key);
		System.out.println("========================================================");
		System.out.println(destsrc);
		System.out.println("========================================================");
		if (destsrc.equals(signString))
			return true;
		return false;

	}

	public static String sign(String dataString, String key) throws IOException {
		String sign = percentEncodeRfc3986(dataString + key);
		return sign;
	}

	private static String percentEncodeRfc3986(String hmac) throws IOException {
		MD5 md5 = new MD5();
		return md5.md5s(hmac);
	}
	public static void main(String[] args) throws IOException {
		
		String concat="merchantId=10041014345&payType=20&txnAmt=50000&orderId=2018010510210250&txnTime=20180105113911&backUrl=http://www.al.com/notify/cpay.php?orderId=2018010510210250&accNo=111111111111111111&frontUrl=http://www.al.com/notify/cpay.php";
		String key="cd66ab06b5464ebd873bee62d4243cf4";
		
		String sign=sign(concat,key);
		
		System.out.println(sign);
	}
}

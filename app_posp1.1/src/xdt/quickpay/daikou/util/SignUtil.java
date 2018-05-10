package xdt.quickpay.daikou.util;

import java.io.IOException;

import org.apache.log4j.Logger;

import xdt.quickpay.hengfeng.util.MD5;

public class SignUtil {
	
	/**
	 * 日志记录
	 */
	private static Logger logger = Logger.getLogger(SignUtil.class);

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
			logger.info("获取的key值:"+key);
			logger.info("========================================================");
			logger.info(destsrc);
			logger.info("========================================================");
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
			
			String concat="merchantId=10035038819&name=尚延超&phoneNo=18902195076&cardNo=6212260302026649095&idCardNo=410324199203231912&startDate=20171113&endDate=20171113";
			String key="aa385cea4981461d8776c68d8a425872";
			
			String sign=sign(concat,key);
			
			logger.info(sign);
		}

}

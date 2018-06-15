package xdt.quickpay.clearQuickPay.util;
import java.security.MessageDigest;

/**
 * MD5加密类
 * 
 * @author 
 * @date 2016年11月11日 下午7:20:09
 * @version v1.0
 */
public class MD5Util {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"a", "b", "c", "d", "e", "f" };

	/**
	 * 转换字节数组为16进制字串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字串
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuilder resultSb = new StringBuilder();
		for (byte aB : b) {
			resultSb.append(byteToHexString(aB));
		}
		return resultSb.toString();
	}

	/**
	 * 转换byte到16进制
	 * 
	 * @param b
	 *            要转换的byte
	 * @return 16进制格式
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
	public static String bytes2HexStr(byte[] byteArr) {
		if (null == byteArr || byteArr.length < 1)
			return "";
		StringBuilder sb = new StringBuilder();
		for (byte t : byteArr) {
			if ((t & 0xF0) == 0)
				sb.append("0");
			sb.append(Integer.toHexString(t & 0xFF)); // t & 0xFF 操作是为去除Integer高位多余的符号位（java数据是用补码表示）
		}
		return sb.toString();
	}
	// hex串转为byte
	public static byte[] hexStr2Bytes(String hexStr) {
		if (null == hexStr || hexStr.length() < 1)
			return null;

		int byteLen = hexStr.length() / 2;
		byte[] result = new byte[byteLen];
		char[] hexChar = hexStr.toCharArray();
		for (int i = 0; i < byteLen; i++) {
			result[i] = (byte) (Character.digit(hexChar[i * 2], 16) << 4 | Character.digit(hexChar[i * 2 + 1], 16));
		}

		return result;
	}

	/**
	 * MD5编码
	 * 
	 * @param origin
	 *            原始字符串
	 * @return 经过MD5加密之后的结果
	 */
	public static String MD5Encode(String origin) {
		String resultString = null;
		try {
			resultString = origin;
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultString;
	}
	public static void main(String[] args) {
		
		String str="merchantId=2120180507170850001&merchantUrl=http://60.28.24.164:8102/app_posp/gateWay/ysb_notifyUrl.action&responseMode=2&orderId=1526348798334&currencyType=CNY&amount=100&assuredPay=false&time=20180515094638&remark=担担面&merchantKey=liangshuai123";
		
		String sign=MD5Encode(str);
		
		System.out.println(sign);
		
		String a="ABC";
		System.out.println(a.toLowerCase());
	}

}

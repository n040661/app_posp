package xdt.quickpay.syys;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5签名
 * 
 * @author liyi
 * 
 */

public class MD5 {

	/**
	 * 签名字符串
	 * 
	 * @param text
	 *          需要签名的字符串
	 * @param key
	 *          密钥
	 * @param input_charset
	 *          编码格式
	 * @return 签名结果
	 */
	public static String sign(String text, String key, String input_charset) {
		text = text + key;
		return DigestUtils.md5Hex(getContentBytes(text, input_charset));
	}

	/**
	 * 签名字符串
	 * 
	 * @param text
	 *          需要签名的字符串
	 * @param key
	 *          密钥
	 * @param input_charset
	 *          编码格式
	 * @return 签名结果
	 */
	public static String sign(String text, String input_charset) {
		return DigestUtils.md5Hex(getContentBytes(text, input_charset));
	}

	/**
	 * 签名字符串
	 * 
	 * @param text
	 *          需要签名的字符串
	 * @param sign
	 *          签名结果
	 * @param key
	 *          密钥
	 * @param input_charset
	 *          编码格式
	 * @return 签名结果
	 */
	public static boolean verify(String text, String sign, String key, String input_charset) {
		text = text + key;
		String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
		if (mysign.equals(sign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param content
	 * @param charset
	 * @return
	 * @throws SignatureException
	 * @throws UnsupportedEncodingException
	 */
	private static byte[] getContentBytes(String content, String charset) {
		if ((charset == null) || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
		}
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *          需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, ?> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			Object value = params.get(key);

			if ((value == null) || value.equals("") || key.equalsIgnoreCase("sign_info") || key.equalsIgnoreCase("sign_type")) {
				continue;
			}

			if (i == (keys.size() - 1)) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value.toString();
			} else {
				prestr = prestr + key + "=" + value.toString() + "&";
			}
		}

		return prestr;
	}
}
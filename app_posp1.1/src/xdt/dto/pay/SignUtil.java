package xdt.dto.pay;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * 签名工具类
 * 
 * @author tinn
 *
 */
public class SignUtil {

	/**
	 * 比较签名是否正确
	 * 
	 * @param origSign
	 * @return
	 */
	public static boolean signVerifyByObject(String channelKey, String origSign, Object... signObjs) {
		if (origSign == null) {
			return false;
		}
		String newSign = signByObject(channelKey, signObjs);
		if (origSign.equalsIgnoreCase(newSign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 生成签名
	 * 
	 * @param signObjs
	 *            签名所需元素
	 * @return
	 */
	public static String signByObject(String channelKey, Object... signObjs) {
		// 计算签名所需要的内容
		StringBuilder signContent = new StringBuilder();
		signContent.append(channelKey);
		// 有参数的情况下，签名内容需要拼接参数
		if (signObjs != null && signObjs.length > 0) {
			for (Object signElement : signObjs) {
				if (signElement != null) {
					signContent.append(signElement);
				}
			}
		}
		System.out.println(signContent.toString());
		// 生成签名
		String sign = EncryptUtil.md5Encrypt(signContent.toString());
		return sign;
	}

	/**
	 * 签名
	 * 
	 * @param map
	 * @return
	 */
	public static String signByMap(String channelKey, TreeMap<String, Object> map) {
		System.out.println("sign start !");
		try {
			StringBuilder sb = new StringBuilder();
			Iterator<String> iterator = map.keySet().iterator();
			sb.append(channelKey);
			while (iterator.hasNext()) {
				Object key = iterator.next();
				Object valueObj = map.get(key);
				if (valueObj != null) {
					// 并将获取的值进行拼接
					String value = valueObj.toString();
					System.out.println("map:" + key + ":" + value);
					sb.append(value);
				}
			}
			System.out.println("SignData orig Data : " + sb.toString());
			String signData = EncryptUtil.md5Encrypt(sb.toString());
			System.out.println("SignData : " + signData);
			return signData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 验证签名
	 * 
	 * @param map
	 * @param sign
	 * @return
	 */
	public static boolean signVerifyByMap(String channelKey, TreeMap<String, Object> map, String sign) {
		System.out.println("verify sign start ! sign :" + sign);
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(channelKey);
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				// 并将获取的值进行拼接
				Object valueObj = map.get(key);
				if (valueObj != null) {
					String value = valueObj.toString();
					System.out.println("map:" + key + ":" + value);
					sb.append(value);
				}
			}
			String md5Result = EncryptUtil.md5Encrypt(sb.toString());
			if (sign.equalsIgnoreCase(md5Result)) {
				System.out.println("verify success");
				return true;
			} else {
				System.out.println("verify failure");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}

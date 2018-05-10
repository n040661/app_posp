package xdt.pufa.security;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import xdt.pufa.Base64;
import xdt.util.BeanToMapUtil;
import xdt.util.MD5;

/**
 * 
 * @Description 验签
 * @author Shiwen .Li
 * @date 2016年9月26日 下午11:14:47
 * @version V1.3.1
 */
public class PuFaSignUtil {

	/**
	 * 
	 * @Description 签名
	 * @author Administrator
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	public static String sign(Map<String, String> param) throws Exception {

		String result = commSign(param);
		return result;
	}

	/**
	 * 公用
	 * 
	 * @Description
	 * @author Administrator
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	public static String commSign(Map<String, String> param) throws Exception {
		String result = null;
		param = BeanToMapUtil.map2TreeMap(param);
		StringBuffer sb = new StringBuffer();
		Set<Entry<String, String>> set = param.entrySet();
		Iterator<Entry<String, String>>  interator = set.iterator();
		while (interator.hasNext()) {
			Entry<String, String> entry =  interator.next();
			if (!"sign".equals(entry.getKey())) {
				if(!entry.getValue().isEmpty()){
					sb.append(entry.getKey());
					sb.append("=");
					sb.append(entry.getValue());
					sb.append("&");
				}
			}
		}
		result = Base64.getBase64(MD5.encryption(sb.toString().substring(0,sb.toString().length() - 1)));
		System.out.println("sign:"+result);
		
		return result;
	}

	/**
	 * 
	 * @Description 验证签名
	 * @author Administrator
	 * @param param
	 * @param sign
	 * @return
	 * @throws Exception 
	 */
	public static boolean signVerify(Map<String, String> param, String sign) throws Exception {
		String result = commSign(param);
		return result.equals(sign);
	}

	public static void main(String[] args) throws Exception {
		
		String result="body=test&device_info=android&merchantId=10036042543&nonce_str=1505661541718&notify_url=http://www.abc.com/test.html&out_trade_no=17091723190169401000000001265584&total_fee=10000";
		
		System.out.println(result.toString().length());
		System.out.println(result.toString().substring(0,result.toString().length() - 1));
		String sign=Base64.getBase64(MD5.encryption(result.toString().substring(0,result.toString().length() - 1)));
		
		System.out.println(sign);
	}
}

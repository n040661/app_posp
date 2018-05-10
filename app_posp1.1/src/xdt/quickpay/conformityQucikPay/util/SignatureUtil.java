package xdt.quickpay.conformityQucikPay.util;



import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xdt.controller.BaseAction;

/**
 * 签名工具类
 *
 * @author zhang.hui@pufubao.net
 * @date 2016年10月20日 上午10:58:24
 * @version v1.0
 */
public class SignatureUtil {
	
	public static Logger logger = Logger.getLogger(SignatureUtil.class);

	/**
	 * 签名算法
	 *
	 * @param map
	 * @param key
	 * @param log
	 * @return
	 * @author zhang.hui@pufubao.net
	 * @date 2016年11月11日 下午2:53:33
	 */
	public static String getSign(Map<String, Object> map, String key) {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null && StringUtil.isNotBlank(String.valueOf(entry.getValue()))) {
				list.add(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = sb.toString();	
		result = result.substring(0, result.length() - 1);
		result += key;
		logger.info("Sign Before MD5:"+result);
		System.out.println("Sign Before MD5:"+result);
		result = MD5Util.MD5Encode(result).toUpperCase();
		logger.info("Sign Result: {}"+result);
		System.out.println("Sign Result:"+result);
		return result;
	}
	/**
	 * 签名前排序
	 * @param param
	 * @return
	 */
	public static String getSignPlainText(Map<String, String> param) {
        try {
            Map<String, String> md5Map = new HashMap<String, String>();
            md5Map.putAll(param);
            List arrayList = new ArrayList(md5Map.entrySet());
            Collections.sort(arrayList, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Map.Entry obj1 = (Map.Entry) o1;
                    Map.Entry obj2 = (Map.Entry) o2;
                    return (obj1.getKey()).toString().compareTo((String) obj2.getKey());
                }
            });
            StringBuilder md5key = new StringBuilder("");
            for (Iterator iter = arrayList.iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if("".equals(value) || value == null) {
                    continue;
                }
                if (md5key.length() <=0) {
                    md5key.append(key).append("=").append(value);
                } else {
                    md5key.append("&").append(key).append("=").append(value);
                }
            }
            return md5key.toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }
	/**
	 * 校验签名
	 *
	 * @param map
	 * @param key
	 * @param log
	 * @return
	 * @author zhang.hui@pufubao.net
	 * @date 2016年11月11日 下午5:21:21
	 */
	public static boolean checkSign(Map<String, Object> map, String key) {
		logger.info("校验签名的数据:{}"+ map.toString());

		String signFromAPIResponse = map.get("v_sign").toString();
		if (signFromAPIResponse == "" || signFromAPIResponse == null) {
			logger.info("API返回的数据签名数据不存在，有可能被第三方篡改!!!");
			return false;
		}
		logger.info("服务器回包里面的签名是:{}"+signFromAPIResponse);
		// 清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
		map.remove("v_sign");
		map.remove("v_fileName");
		// 将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
		String signForAPIResponse = SignatureUtil.getSign(map, key);
		
		if (!signForAPIResponse.equals(signFromAPIResponse)) {
			// 签名验不过，表示这个API返回的数据有可能已经被篡改了
			logger.info("API返回的数据签名验证不通过，有可能被第三方篡改!!!");
			return false;
		}
		logger.info("恭喜，API返回的数据签名验证通过!!!");
		return true;
	}
	public static void main(String[] args) {
		
		String aa="v_attach=BWJ20180319183532786751&v_bankAddr=CEB&v_cardType=1&v_channel=0&v_currency=1&v_expire_time=60&v_mid=10043046611&v_notify_url=https://member.goldwang.cn/notify/changjiezhifu?ext1=3bafqxFzfkkLY1TVpmMHiTOYAmUec7Knq4eHuH_2BJ12s6QlNLGYd8bRWishetTJMocDiMAaC0&v_oid=BWJ20180319183532786751&v_productDesc=用户充值&v_productName=BWJ20180319183532786751&v_productNum=1&v_time=20180319183536&v_txnAmt=641.23&v_type=0&v_url=https:///member.goldwang.cn/member/changjiezhifuback&v_version=1.0.0.07d2feb66b6474964adcbfebe82e29744";
		
		//String result=aa+"060800f2001d413d813a532ea84fa08a";
		String sign=MD5Util.MD5Encode(aa).toUpperCase();
		System.out.println(sign);
		
	}

}

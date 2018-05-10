package xdt.quickpay.qianlong.util;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;



/**
 * 签名工具类
 * @author Jerry
 * @date 2015.08.13
 */
public class SignatureUtil {
	
	private static Logger logger = Logger.getLogger(SignatureUtil.class);
	
	/**
	 * 排序并组装签名明文串
	 * @param map
	 * @return
	 */
	public static String hex(Map<String,String> map){
		String[] strs = new String[map.size()];
		map.keySet().toArray(strs);
		Arrays.sort(strs);
		StringBuffer source = new StringBuffer();
		for(String str:strs){
			if(StringUtils.isEmpty(map.get(str))){
				continue;
			}
			source.append(str+"="+map.get(str)+"&");
		}
		String bigstr = source.substring(0,source.length()-1);
		logger.debug("sign bigstr="+bigstr);
		return bigstr;
	}
	
}

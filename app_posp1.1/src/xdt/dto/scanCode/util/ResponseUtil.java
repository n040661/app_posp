package xdt.dto.scanCode.util;

import java.util.Map;

import com.ielpm.mer.sdk.secret.CertUtil;
import com.ielpm.mer.sdk.secret.Secret;

public class ResponseUtil {
	
	/**
	 * 解析返回数据
	 * @param response
	 * @return
	 */
	public static Map parseResponse(String response,Secret secret){
		//解析返回信息到map中
		Map transMap = ParamUtil.getParamsMap(response, "utf-8");
		//获取签名
		if("0000".equals((String) transMap.get("rtnCode"))) {
			String sign = (String) transMap.get("sign");
			System.out.println(sign);
			sign = sign.replaceAll(" ", "+");
			transMap.remove("sign");
			//验签
			String transData = ParamUtil.getSignMsg(transMap);
			boolean result = false;
			try {
				secret.verify(transData, sign);
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(!result){
				transMap.clear();
				transMap.put("tranData", transData);
				transMap.put("sign", sign);
				transMap.put("msg", "验签失败");
			}
			transMap.put("sign", sign);
		}
		
		return transMap;
	}

}

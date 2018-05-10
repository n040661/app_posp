package xdt.quickpay.hf.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class EffersonPayService {

	public static Map<String,String> post(
			Map<String, String> reqData,String reqUrl,String encoding) {
		Map<String, String> rspData = new HashMap<String,String>();
		//发送后台请求数据
		PlatHttpClient hc = new PlatHttpClient(reqUrl, 30000, 30000);
		try {
			int status = hc.send(reqData, encoding);
			if (200 == status) {
				String resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					// 将返回结果转换为map
					Map<String,String> tmpRspData  = PlatSDKUtil.convertResultStringToMap(resultString);
					rspData.putAll(tmpRspData);
				}
			}else{
				PlatLogUtil.writeLog("返回http状态码["+status+"]，请检查请求报文或者请求地址是否正确");
			}
		} catch (Exception e) {
			PlatLogUtil.writeErrorLog(e.getMessage(), e);
		}
		return rspData;
	}
	
	public static String postAsString(
            Map<String, String> reqData,String reqUrl,String encoding) {
        //发送后台请求数据
        PlatHttpClient hc = new PlatHttpClient(reqUrl, 30000, 30000);
        try {
            int status = hc.send(reqData, encoding);
            if (200 == status) {
                String resultString = hc.getResult();
                if (null != resultString && !"".equals(resultString)) {
                    // 将返回结果转换为map
                   return resultString;
                }
            }else{
                PlatLogUtil.writeLog("返回http状态码["+status+"]，请检查请求报文或者请求地址是否正确");
            }
        } catch (Exception e) {
            PlatLogUtil.writeErrorLog(e.getMessage(), e);
        }
        return "查询失败";
    }
	
	public static String get(String reqUrl,String encoding) {
		
		PlatLogUtil.writeLog("请求银联地址:" + reqUrl);
		//发送后台请求数据
		PlatHttpClient hc = new PlatHttpClient(reqUrl, 30000, 30000);
		try {
			int status = hc.sendGet(encoding);
			if (200 == status) {
				String resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					return resultString;
				}
			}else{
				PlatLogUtil.writeLog("返回http状态码["+status+"]，请检查请求报文或者请求地址是否正确");
			}
		} catch (Exception e) {
			PlatLogUtil.writeErrorLog(e.getMessage(), e);
		}
		return null;
	}
	
	public static String createAutoFormHtml(String reqUrl, Map<String, String> hiddens,String encoding) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="+encoding+"\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + reqUrl
				+ "\" method=\"post\">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Entry<String, String>> set = hiddens.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}
}

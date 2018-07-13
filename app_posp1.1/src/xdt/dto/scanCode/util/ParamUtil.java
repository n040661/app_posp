package xdt.dto.scanCode.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

public class ParamUtil {
	private static final String SIGN_PARAM_SEPARATOR = "&";
	
	/**
	 * 解析request param到map中
	 * @param request
	 * @return
	 */
	public static TreeMap<String, String> getParamMap(HttpServletRequest request){
		TreeMap<String, String> transMap = new TreeMap<String, String>();
		Enumeration<String> enu = request.getParameterNames();
		String t = null;
		while(enu.hasMoreElements()){
			t = enu.nextElement();
			transMap.put(t, request.getParameter(t));
		}
		return transMap;
	}
	
	 /**
     * 解析queryString
     * @param queryString 请求参数字符串
     * @param enc		     编码
     * @return
     */
    public static Map<String, String> getParamsMap(String queryString, String enc) {
        Map<String, String> paramsMap = new TreeMap<String, String>();
        if (queryString != null && queryString.length() > 0) {
            int ampersandIndex, lastAmpersandIndex = 0, tmpIndex = 0;
            String subStr, param, value;
            do {
                ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;
                if (ampersandIndex > 0) {
                    subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);
                    lastAmpersandIndex = ampersandIndex;
                } else {
                    subStr = queryString.substring(lastAmpersandIndex);
                }
                
                tmpIndex = subStr.indexOf('=');
                param = subStr.substring(0,tmpIndex);
                value = subStr.substring(tmpIndex+1);
                try {
                    value = URLDecoder.decode(value, enc);
                } catch (UnsupportedEncodingException ignored) {
                }
//                if (paramsMap.containsKey(param)) {
//                    values = (String[])paramsMap.get(param);
//                    int len = values.length;
//                    newValues = new String[len + 1];
//                    System.arraycopy(values, 0, newValues, 0, len);
//                    newValues[len] = value;
//                } else {
//                    newValues = new String[] { value };
//                }
                if(!"".equals(param))
                    paramsMap.put(param, value);
            } while (ampersandIndex > 0);
        }
        return paramsMap;
    }

	/**
     * 组织签名需要的交易数据
     * @param map
     * @return
     */
    public static String getSignMsg(Map map){
        StringBuffer sb = new StringBuffer();
        String key = "";
        for(Iterator<String> it = map.keySet().iterator(); it.hasNext(); ){
            key = it.next();
            sb.append(key).append("=").append(map.get(key)).append(SIGN_PARAM_SEPARATOR);
        }
        if(sb.indexOf(SIGN_PARAM_SEPARATOR) > -1)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    
    public static void main(String[] args) {
		String str = "k1=v1&k2=v2===&k3=v3";
		
		System.out.println(ParamUtil.getParamsMap(str, "utf-8"));
	}

}

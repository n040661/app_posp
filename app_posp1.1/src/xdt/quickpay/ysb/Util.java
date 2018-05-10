package xdt.quickpay.ysb;

import java.util.LinkedHashMap;

import xdt.quickpay.ysb.model.CallBack;

public class Util {
	
	public static String getMab(Request request)
	  {
	    if (!check(request)) {
	      return null;
	    }
	    return foreachMap(request, "mac");
	  }
	  
	  private static boolean check(LinkedHashMap<String, String> request)
	  {
	    if (request == null) {
	      return false;
	    }
	    return true;
	  }
	  
	  private static String foreachMap(LinkedHashMap<String, String> request, String excludeKey)
	  {
	    StringBuffer str = new StringBuffer();
	    for (String key : request.keySet()) {
	      if (!excludeKey.equals(key))
	      {
	        String value = (String)request.get(key);
	        if (isNotBlank(value)) {
	          str.append("&").append(key).append("=").append((String)request.get(key));
	        }
	      }
	    }
	    if (str.length() > 1) {
	      return str.toString().substring(1);
	    }
	    return str.toString();
	  }
	  
	  public static String getMac(String str)
	  {
	    return Md5Encrypt.md5(str);
	  }
	  
	  public static String getSendData(Request request)
	  {
	    if (!check(request)) {
	      return null;
	    }
	    return foreachMap(request, "key");
	  }
	  
	  public static String getMabCallBack(CallBack callBack)
	  {
	    StringBuffer buffer = new StringBuffer();
	    buffer.append("accountId=");
	    if (isNotBlank(callBack.getAccountId())) {
	      buffer.append(callBack.getAccountId());
	    }
	    buffer.append("&orderId=");
	    if (isNotBlank(callBack.getOrderId())) {
	      buffer.append(callBack.getOrderId());
	    }
	    buffer.append("&amount=");
	    if (isNotBlank(callBack.getAmount())) {
	      buffer.append(callBack.getAmount());
	    }
	    buffer.append("&result_code=");
	    if (isNotBlank(callBack.getResult_code())) {
	      buffer.append(callBack.getResult_code());
	    }
	    buffer.append("&result_msg=");
	    if (isNotBlank(callBack.getResult_msg())) {
	      buffer.append(callBack.getResult_msg());
	    }
	    buffer.append("&key=");
	    if (isNotBlank(callBack.getKey())) {
	      buffer.append(callBack.getKey());
	    }
	    return buffer.toString();
	  }
	  
	  public static boolean isNotBlank(CharSequence cs)
	  {
	    return !isBlank(cs);
	  }
	  
	  public static boolean isBlank(CharSequence cs)
	  {
	    int strLen;
	    if ((cs == null) || ((strLen = cs.length()) == 0)) {
	      return true;
	    }
	    for (int i = 0; i < strLen; i++) {
	      if (!Character.isWhitespace(cs.charAt(i))) {
	        return false;
	      }
	    }
	    return true;
	  }

}

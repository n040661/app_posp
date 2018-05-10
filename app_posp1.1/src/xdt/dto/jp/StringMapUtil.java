package xdt.dto.jp;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class StringMapUtil {

    public static String changeMapToString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static Map<String, String> changeStringToMap(String result) {
        String[] resArr = StringUtils.split(result, "&");
        Map<String, String> map = new HashMap<>();

        for (String data : resArr) {
            int index = StringUtils.indexOf(data, '=');
            String key = StringUtils.substring(data, 0, index);
            String value = StringUtils.substring(data, index + 1);
            map.put(key, value);
        }
        return map;
    }
    
    public static Map<String, String> stringToDataFieldMap(String str){
   	   JSONObject jsonObject=JSONObject.parseObject(str);
   	   Map<String, JSONObject> map=(Map)jsonObject;
   	   JSONObject value=map.get("data");
   	   Map<String, String> map2 = (Map)value;
   	   for (Entry<String, String> entry : map2.entrySet()) {
			System.out.println(entry.getKey()+":"+entry.getValue());
		 }
   	   return map2;
    }
    
    public static Map<String, String> stringToErrorFieldMap(String str){
   	   JSONObject jsonObject=JSONObject.parseObject(str);
   	   Map<String, JSONObject> map=(Map)jsonObject;
   	   JSONObject value=map.get("error");
   	   Map<String, String> map2 = (Map)value;
   	      for (Entry<String, String> entry : map2.entrySet()) {
			System.out.println(entry.getKey()+":"+entry.getValue());
		   }
   	     return map2;
    }
}

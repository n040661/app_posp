package xdt.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;

public class JsonUtil {

	public static Map jsonToMap(String json) {
		Map classMap = new HashMap();
		if(StringUtils.isEmpty(json)){
			return classMap;
		}
		classMap.put("map", Map.class);
		Map map = (Map)JSONObject.toBean(JSONObject.fromObject(json), Map.class, classMap);
		// 转换null
		Iterator it=map.keySet().iterator();
		while(it.hasNext()){
			String key = (String)it.next();
			Object value = map.get(key);
			if (value instanceof JSONNull) {
				map.put(key, null);
			}
		}
		return map;
	} 
	
	public static String map2Json(Map map) {
		JSONObject j = JSONObject.fromObject(map);
		return j.toString();
	}
	
}

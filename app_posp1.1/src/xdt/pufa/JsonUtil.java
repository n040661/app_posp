package xdt.pufa;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @Description json 工具 
 * @author Shiwen .Li
 * @date 2017年3月30日 下午9:42:48 
 * @version V1.3.1
 */
public final class JsonUtil {
	
	public static  String map2JsonStr(Map<String,Object> map){
		StringBuffer sb=new StringBuffer();
		
		sb.append("{");
		
		for (String key: map.keySet()) {
			sb.append("\\\"");
			sb.append(key);
			sb.append("\\\"");
			
			sb.append(":");
			
			sb.append("\\\"");
			sb.append(map.get(key));
			sb.append("\\\"");

			sb.append(",");
			
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("}");
		
		return sb.toString();
	}
	public static void main(String[] args) {
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("name", "lishiwen");
		map.put("age", "18");
		System.out.println(map2JsonStr(map));
	}
}

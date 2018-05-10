package xdt.dto.yf;

import com.alibaba.fastjson.JSON;




/**
 * 各格式报文解析工具类
 * 
 * @author guoyanjiang
 * @since 2015-7-21
 */
public class DTOUtil {
	
	public static <T> T parseDTO(String context, Class<T> c, String type) throws Exception{
		if(type == null || "xml".equals(type)){
			return JaxbUtils.parseXML(context, c);
		}else if("json".equals(type)){
			return JSON.parseObject(context, c);
		}
		return null;
	}
	
	public static String deParse(Object o, String type) throws Exception{
		if(type == null || "xml".equals(type)){
			return JaxbUtils.deParseXML(o);
		}else if("json".equals(type)){
			return JSON.toJSONString(o);
		}
		return null;
	}
}

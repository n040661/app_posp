package xdt.dto.sxf;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;


public class JsonUtils {

	private static final SerializerFeature[] features = { 
													
	SerializerFeature.WriteNullListAsEmpty,
			SerializerFeature.WriteNullNumberAsZero,
			SerializerFeature.WriteNullStringAsEmpty,
			SerializerFeature.DisableCircularReferenceDetect };

	private static final SerializeConfig config = new SerializeConfig();
	static {
		config.put(java.sql.Date.class, new SimpleDateFormatSerializer(
				"yyyy-MM-dd"));
	}


	public static <T> T fromJson(String json, Class<T> clazz) {
		T t = null;
		t = JSON.parseObject(json, clazz);
		return t;
	}


	@SuppressWarnings("unchecked")
	public static Map<String, Object> fromJson(String json) {
		Map<String, Object> obj = null;
		obj = (Map<String, Object>) JSON.parse(json);
		return obj;
	}



	public static String toJson(Object object) {
		String result = JSON.toJSONString(object, config, features);
		return result;
	}


}

package xdt.dto.yf;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @className:GsonUtil.java
 * @classDescription: gson工具类
 */

public class GsonUtil {

    /**
	* 时间格式
	*/
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static final Gson gsonInstance = new GsonBuilder().serializeNulls().setDateFormat(DATE_FORMAT).create(); 

	/**
	* 创建GSON
	* @return
	*/
	public static Gson getGson(){
		return gsonInstance;
	}

	/**
     * 将对象转化为字符串
     */
    public static String objToJson(Object obj){
    	return gsonInstance.toJson(obj);
    }

    /**
     * 将对象转化为字符串(泛型实现)
     */

    public static <T> String objToJson2(T t){
    	return gsonInstance.toJson(t);
    }

    /**
     * 将字符转化为对象
     */
    public static <T> T jsonToObj(String jsonString , Class<T> clazz){
        return gsonInstance.fromJson(jsonString,clazz);
    }

    /**
     * 将字符串数组转化为对象集合
     */
    public static <T> List<T> jsonToList(String jsonStr,Type type){
        return gsonInstance.fromJson(jsonStr,type);
    }
}

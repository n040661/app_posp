package xdt.dto.ys;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *  swp服务对下流渠道的sign计算 
 *  
 *  @author heqq
 *  @date 2017-8-17
 **/
public class SwpHashUtil {
	
	private static final Logger logger = Logger.getLogger(SwpHashUtil.class);

	/**
	 * 通过获取所有bean对象中的public属性参数，拼接key=value格式的字符串，按照ascii升序排序
	 * 
	 * @param Object bean 需要计算前面的对象，类成员字段必须是public，否则抛出异常IllegalArgumentException，IllegalAccessException
	 * 
	 * @return eg:aa1=2017-08-04 09:03:31&channelid=channelid&d1=123.12&i1=1234&l1=43221&opt=tisign&key=f8e7wqgh9wq8ef9efh74th9yh43gjr22

	 **/
	public static String getSignData(Object bean, String channelKey) throws IllegalArgumentException, IllegalAccessException{
		
		Map<String, String> map = new TreeMap<String, String>();
		
		Class<?> _class = bean.getClass();
		Field[] fields = _class.getFields();
		for(Field _field : fields){
			String fieldType = _field.getType().toString();
			if(fieldType.endsWith("String")){
				if(_field.get(bean) != null && StringUtils.isNotBlank((String)_field.get(bean)))
					map.put(_field.getName(), (String)_field.get(bean));
			}else if(fieldType.endsWith("double") || fieldType.endsWith("Double")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(((Double)_field.get(bean)).toString()))
					map.put(_field.getName(), ((Double)_field.get(bean)).toString());
			}else if(fieldType.endsWith("int") || fieldType.endsWith("Integer")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(((Integer)_field.get(bean)).toString()))
					map.put(_field.getName(), ((Integer)_field.get(bean)).toString());
			}else if(fieldType.endsWith("long") || fieldType.endsWith("Long")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(((Long)_field.get(bean)).toString()))
					map.put(_field.getName(), ((Long)_field.get(bean)).toString());
			}else if(fieldType.endsWith("Date")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(DateUtil.formatDate((Date)_field.get(bean), DateUtil.DATE_FORMAT_1)))
					map.put(_field.getName(), DateUtil.formatDate((Date)_field.get(bean), DateUtil.DATE_FORMAT_1));
			}
			
		}
		
		StringBuilder sb = new StringBuilder();
		
        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
        	String key = iter.next();
        	String value = map.get(key);
        	
        	if(!StringUtils.equalsIgnoreCase("sign", key) && StringUtils.isNotBlank(value)){
        		sb.append(key);
        		sb.append("=");
        		sb.append(value);
        		sb.append("&");
            }
        }
        sb.append("key=");
        sb.append(channelKey);
        
        logger.info("sign data:"+sb.toString());
        return sb.toString();
	}
	
	
	/**
	 * 通过获取所有bean对象中的public属性参数，拼接key=value格式的字符串，按照ascii升序排序
	 * 
	 * @param Object bean 需要计算前面的对象，类成员字段必须是public，否则抛出异常IllegalArgumentException，IllegalAccessException
	 * 
	 * @return md5的摘要,大写字符串. eg:BC197670903D2607B44E97BC876421CA
	 **/
	public static String getSign(Object bean, String channelKey, String method) throws IllegalArgumentException, IllegalAccessException{
		
		Map<String, String> map = new TreeMap<String, String>();
		
		Class<?> _class = bean.getClass();
		Field[] fields = _class.getFields();
		for(Field _field : fields){
			String fieldType = _field.getType().toString();
			if(fieldType.endsWith("String")){
				if(_field.get(bean) != null && StringUtils.isNotBlank((String)_field.get(bean)))
					map.put(_field.getName(), (String)_field.get(bean));
			}else if(fieldType.endsWith("double") || fieldType.endsWith("Double")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(((Double)_field.get(bean)).toString()))
					map.put(_field.getName(), ((Double)_field.get(bean)).toString());
			}else if(fieldType.endsWith("int") || fieldType.endsWith("Integer")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(((Integer)_field.get(bean)).toString()))
					map.put(_field.getName(), ((Integer)_field.get(bean)).toString());
			}else if(fieldType.endsWith("long") || fieldType.endsWith("Long")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(((Long)_field.get(bean)).toString()))
					map.put(_field.getName(), ((Long)_field.get(bean)).toString());
			}else if(fieldType.endsWith("Date")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(DateUtil.formatDate((Date)_field.get(bean), DateUtil.DATE_FORMAT_1)))
					map.put(_field.getName(), DateUtil.formatDate((Date)_field.get(bean), DateUtil.DATE_FORMAT_1));
			}
			
		}
		
		StringBuilder sb = new StringBuilder();
		
        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
        	String key = iter.next();
        	String value = map.get(key);
        	
        	if(!StringUtils.equalsIgnoreCase("sign", key) && StringUtils.isNotBlank(value)){
        		sb.append(key);
        		sb.append("=");
        		sb.append(value);
        		sb.append("&");
            }
        }
        sb.append("key=");
        sb.append(channelKey);
        
        logger.info("sign data:"+sb.toString());
        
        if(StringUtils.equalsIgnoreCase(method, "md5")){
        	return EncryptUtil.md5Encrypt(sb.toString()).toUpperCase();
        }else if(StringUtils.equalsIgnoreCase(method, "sha2") || StringUtils.equalsIgnoreCase(method, "sha256")){
        	return EncryptUtil.sha2Encrypt(sb.toString()).toUpperCase();
        }else
        	throw new IllegalArgumentException("无法识别的签名方法");
	}
	
	/**
	 * 通过获取所有bean对象中的public属性参数，拼接key=value格式的字符串，按照ascii升序排序
	 * 
	 * @param String data 需要计算的数据
	 * 
	 * @return md5的摘要,大写字符串. eg:BC197670903D2607B44E97BC876421CA
	 **/
	public static String getSign(String data, String channelKey, String method){
		if(StringUtils.equalsIgnoreCase(method, "md5")){
        	return EncryptUtil.md5Encrypt(data).toUpperCase();
        }else if(StringUtils.equalsIgnoreCase(method, "sha2") || StringUtils.equalsIgnoreCase(method, "sha256")){
        	return EncryptUtil.sha2Encrypt(data).toUpperCase();
        }else
        	throw new IllegalArgumentException("无法识别的签名方法");
	}
	
	/**
	 * 通过获取所有bean对象中的public属性参数，拼接key<--->value格式的Map，按照ascii升序排序
	 * 
	 * @param Object bean 需要计算前面的对象，类成员字段必须是public，否则抛出异常IllegalArgumentException，IllegalAccessException
	 * 
	 * @return Map<String, String>
	 **/
	public static Map<String, String> getBeanToMap(Object bean) throws IllegalArgumentException, IllegalAccessException{
		Map<String, String> map = new TreeMap<String, String>();
		
		Class<?> _class = bean.getClass();
		Field[] fields = _class.getFields();
		for(Field _field : fields){
			String fieldType = _field.getType().toString();
			if(fieldType.endsWith("String")){
				if(_field.get(bean) != null && StringUtils.isNotBlank((String)_field.get(bean)))
					map.put(_field.getName(), (String)_field.get(bean));
			}else if(fieldType.endsWith("double") || fieldType.endsWith("Double")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(((Double)_field.get(bean)).toString()))
					map.put(_field.getName(), ((Double)_field.get(bean)).toString());
			}else if(fieldType.endsWith("int") || fieldType.endsWith("Integer")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(((Integer)_field.get(bean)).toString()))
					map.put(_field.getName(), ((Integer)_field.get(bean)).toString());
			}else if(fieldType.endsWith("long") || fieldType.endsWith("Long")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(((Long)_field.get(bean)).toString()))
					map.put(_field.getName(), ((Long)_field.get(bean)).toString());
			}else if(fieldType.endsWith("Date")){
				if(_field.get(bean) != null && StringUtils.isNotBlank(DateUtil.formatDate((Date)_field.get(bean), DateUtil.DATE_FORMAT_1)))
					map.put(_field.getName(), DateUtil.formatDate((Date)_field.get(bean), DateUtil.DATE_FORMAT_1));
			}
			
		}
        
        return map;
	}
	
}

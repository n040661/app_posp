package xdt.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * @Description 工具类
 * @author YanChao .Shang
 * @date 2017年3月5日 下午2:03:32
 * @version V1.3.1
 */
public class JsdsUtil {

	/**
	 * 签名
	 * @param params
	 * @param key
	 * @return
	 * @throws Exception 
	 */
	public static String sign(Map<String, String> params, String key) throws Exception {
		String sign = "";
        System.out.println("生成签名前的数据:"+params);
 		String valueStr = getValueStr(params);
		System.out.println("valueStr:"+valueStr);
		sign = MD5.encryption(valueStr + key);
		System.out.println(sign);
		return sign;
	}

	public static String getValueStr(Map<String, String> params) {

		StringBuffer sb=new StringBuffer();
		
		Map<String, String> sortMap =new TreeMap<String, String>(new Comparator<String>() {
			@Override
			public int compare(String str1, String str2) {
				return str1.compareTo(str2);
			}
		});
		
		sortMap.putAll(params);
		
		for (String key:sortMap.keySet()) {
			if(sortMap.get(key)!=null&&!("".equals(sortMap.get(key)))){
				System.out.println(key+":"+sortMap.get(key));
				sb.append(sortMap.get(key));
			}
		}
		return sb.toString();
	}
	
	/**
	 * bean 转化为实体
	 * @param bean
	 * @return
	 */
	public static HashMap<String,String> beanToMap(Object bean){
		HashMap<String,String> map = new HashMap<String,String>();
		if(null == bean){
			return map;
		}
		Class<?> clazz = bean.getClass();
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		for(PropertyDescriptor descriptor : descriptors){
			String propertyName = descriptor.getName();
			if(!"class".equals(propertyName)){
				Method method = descriptor.getReadMethod();
				String result;
				try {
					result = (String) method.invoke(bean);
					if(null != result){
						map.put(propertyName, result);
					}else{
						map.put(propertyName, "");
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return map;
	}
	/**
	 * map 转化为 bean
	 * @param clazz
	 * @param map
	 * @return
	 */
	public static Object mapToBean(Class clazz,HashMap map){
		Object object = null;
		try {
			object = clazz.newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			
			PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
			for(PropertyDescriptor descriptor : descriptors){
				String propertyName = descriptor.getName();
				if(map.containsKey(propertyName)){
					Object value = map.get(propertyName);
					Object[] args = new Object[1];
					args[0] = value;
				    descriptor.getWriteMethod().invoke(object, args);
				}
			}
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	/**
	 * 遍历并移除pl_ 上游返回
	 * @param result
	 */
	public static void process(Map<String, String> result) {
		
		Set<String> keys=new HashSet<String>();
		//剔除值为pl_的
		for(String key :result.keySet()){
			if(key.indexOf("pl_")!=-1){
				keys.add(key);
			}
		}
		for (String key : keys) {
			result.remove(key);
		}
	}
	public static void main(String[] args) throws Exception {
		
		System.out.println(MD5.encryption("北京向锦中里科技有限公司10013026547北京向锦中里科技有限公司北京向锦中里科技有限公司http://127.0.0.1:3306/recharge/notify/changjiewechatpayqr3005383568895770624_0cj0022000095b36ffe72bb44b4afb6e716dc2cd092"));
	}
	
}

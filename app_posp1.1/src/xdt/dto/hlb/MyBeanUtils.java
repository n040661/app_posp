package xdt.dto.hlb;

import org.apache.commons.beanutils.BeanUtils;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by heli50 on 2017/4/14.
 */
public class MyBeanUtils extends BeanUtils{
    public static Map convertBean(Object bean, Map retMap)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Class clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
        }
        for (Field f : fields) {
            String key = f.toString().substring(f.toString().lastIndexOf(".") + 1);
            Object value = f.get(bean);
            if(value == null)
                value = "";
            retMap.put(key, value);
        }
        return retMap;
    }

    public static String getSigned(LinkedHashMap<String, String> map, String[] excludes,String keys){
        StringBuffer sb = new StringBuffer();
        Set<String> excludeSet = new HashSet<String>();
        excludeSet.add("sign");
        if(excludes != null){
            for(String exclude : excludes){
                excludeSet.add(exclude);
            }
        }
        for(String key : map.keySet()){
            if(!excludeSet.contains(key)){
                String value = map.get(key);
                value = (value == null ? "" : value);
                sb.append("&");
                sb.append(value);
            }
        }
        sb.append("&");
        sb.append(keys);
        return sb.toString();
    }

    public static String getSigned(Object bean, String[] excludes,String key) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        Map map  = convertBean(bean, new LinkedHashMap());
        String signedStr = getSigned(map, excludes,key);
        return signedStr;
    }

}

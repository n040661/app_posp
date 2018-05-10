package xdt.dto.mb;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;



/** 
 * 使用 Map按key进行排序 
 * @param map 
 * @return 
 */  
public class MapKeyComparator implements Comparator<String> {
    public int compare(String str1, String str2) {  
        return str1.compareTo(str2);  
    }  
    
    
    /** 
     * 使用 Map按key进行排序 Map<String,Object>
     * @param map 
     * @return 
     */  
    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {  
        if (map == null || map.isEmpty()) {  
            return null;  
        }  
        Map<String, Object> sortMap = new TreeMap<String, Object>(new MapKeyComparator());  
        sortMap.putAll(map);  
        return sortMap;  
    }
    /** 
     * 使用 Map按key进行排序 Map<String,String>
     * @param map 
     * @return 
     */ 
    public static Map<String, String> sortMapByKey1(Map<String, String> map) {  
        if (map == null || map.isEmpty()) {  
            return null;  
        }  
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());  
        sortMap.putAll(map);  
        return sortMap;  
    }
    
    
    public static void main(String[] args) {  
//        Map map = new TreeMap<String,String>();  
//        map.put("KFC", "kfc");  
//        map.put("WNBA", "wnba");  
//        map.put("NBA", "nba");  
//        map.put("CBA", "cba");  
//        Map<String, String> resultMap = sortMapByKey(map);    //按Key进行排序  
//        for (Map.Entry<String, String> entry : resultMap.entrySet()) {  
//            System.out.println(entry.getKey() + " " + entry.getValue());  
//        }  
    } 
    

}

package xdt.util;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 16-3-8
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
public class EncodeUtil {


    /****
     * 将字符串转换为加密的串
     * @param transMap
     * @return
     */
	public  static String getUrlStr(Map<String,String> transMap){
		//组织需要加密的字符串
		String transStr="";
		int flag=0;
		for(String key:transMap.keySet()) 
		{
			if((transMap.size()-1)==flag){
				transStr=transStr+key+"="+transMap.get(key);
			}else{
				transStr=transStr+key+"="+transMap.get(key)+"&";
			}
			flag++;
		} 
		return 	transStr;
	}

}

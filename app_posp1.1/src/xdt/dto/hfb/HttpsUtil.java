package xdt.dto.hfb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/**          
* 
* 描    述：httpsClient工具类
*
* 创 建 者： ld
* 创建时间： 2016年11月1日 上午10:55:15 
* 创建描述：封装httpsclient常用方法
* 
* 修 改 者：  
* 修改时间： 
* 修改描述： 
* 
* 审 核 者：
* 审核时间：
* 审核描述：
*
*/

public class HttpsUtil {
	
	public static String sendHttpsRequestWithParam(String url, List<NameValuePair> nvPairs) throws Exception{
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpClient httpClient = HttpClients.createDefault();
//		httpPost.setConfig(requestConfig);
		httpPost.setEntity(new UrlEncodedFormEntity(nvPairs, "UTF-8"));
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		String retStr = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		System.out.println(httpResponse.getStatusLine());
		
		httpResponse.close();
		httpClient.close();
		
		
		return retStr;
	}
	
	
	public static List<NameValuePair> createNVPairs(Map<String, String> params){
        List<NameValuePair> nvPairs = new ArrayList<>();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for(Map.Entry<String, String> entry : entries){
            nvPairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return nvPairs;
    }
	
	public static String createSign(Map<String, String> preSign,String key){
		    String signString ="";
	        Set<String> keySet = preSign.keySet();
	        List<String> keys = new ArrayList<>();
	        keys.addAll(keySet);
	        Collections.sort(keys);

	        StringBuilder signStr = new StringBuilder();
	        for(String elemKey : keys){
	            signStr.append(elemKey).append("=").append(preSign.get(elemKey)).append("&");
	        }
	        signStr.append("key=").append(key);

	        try {
	            System.out.println("构造签名串---加密前："+signStr.toString());
	            signString = Md5.encode(signStr.toString().getBytes("UTF-8"));
	            System.out.println("构造签名串---加密后："+signString);
	        } catch (Exception e){
	            e.printStackTrace();
	            return null;
	        }

	        return signString;
	    }

}

package xdt.dto.scanCode.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年4月24日 下午4:44:08 
* 类说明 
*/
public class RequestUtil {

	
	/**
	 * get请求
	 * @param url
	 * @return
	 */
	public static String doGetStr(String url){
		CloseableHttpClient httpClient  =HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		String result ="";
	    try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity =response.getEntity();
			if(entity!=null){
				
			 result=EntityUtils.toString(entity,"UTF-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}

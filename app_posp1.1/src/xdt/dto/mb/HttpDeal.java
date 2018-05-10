package xdt.dto.mb;

import java.io.IOException;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
import java.util.Set;  
  
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;  
import org.apache.http.client.ClientProtocolException;  
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.client.methods.CloseableHttpResponse;  
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;  
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;  
import org.apache.http.message.BasicNameValuePair;  
import org.apache.http.util.EntityUtils;  
  
  
  
public class HttpDeal {  
	
    /** 
     * 处理get请求. 
     * @param url  请求路径 
     * @return  json 
     */  
    public String get(String url){  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        HttpGet httpget = new HttpGet(url);   
        CloseableHttpResponse response = null;  
        String content ="";  
        try {  
            response = httpclient.execute(httpget);  
            if(response.getStatusLine().getStatusCode()==200){  
                content = EntityUtils.toString(response.getEntity(),"utf-8");  
                System.out.println(content);  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return content;  
    }  
    
    /** 
     * 处理post请求. 
     * @param url  请求路径 
     * @param params  参数 
     * @return  json 
     */  
    public String post(String url,Map<String, String> params){  
//        CloseableHttpClient httpclient = HttpClients.createDefault(); 
    	HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);   
        List<NameValuePair> nvps = new ArrayList <NameValuePair>();    
        Set<String> keySet = params.keySet();    
        for(String key : keySet) {    
            nvps.add(new BasicNameValuePair(key, params.get(key)));    
        }    
//        StringEntity sEntity = new StringEntity("user=asd");
//        CloseableHttpResponse response = null;  
        HttpResponse response = null; 
        String content="";  
        try {  
            UrlEncodedFormEntity uefEntity  = new UrlEncodedFormEntity(nvps, "GBK");  
            httpPost.setEntity(uefEntity);  
            response = httpclient.execute(httpPost);  
            if(response.getStatusLine().getStatusCode()==200){  
                content = EntityUtils.toString(response.getEntity(),"GBK");  
                //System.out.println(content);  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }   
        return content;  
    }  
  
}
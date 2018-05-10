package xdt.dto.sxf;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
//import org.apache.commons.httpclient.HttpClient;


/*
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
*/

public class HttpClientUtil {
	

	/**
     * HTTP GET请求
     * 
     * @param url
     * @param params
     * @param encoding
     * @return
     */
    public static String doGet(String url, String value) {
    	String encoding="utf-8";
        HttpClient httpClient = new DefaultHttpClient();
        String body = null;
        HttpEntity entity = null;
        try {
        	URL strUrl = new URL(url);
        	URI uri = new URI(strUrl.getProtocol(), strUrl.getHost(), strUrl.getPath(), strUrl.getQuery(), null);
            HttpGet httpget = new HttpGet(uri);
            httpget.setHeader("Content-Type","application/json;charset=utf-8");
            /*
             * 设置参数
             */
            httpget.setURI(new URI(httpget.getURI().toString() + "?" + value));
            /*
             * 发送请求
             */
            org.apache.http.HttpResponse httpresponse = httpClient.execute(httpget);
            /*
             * 获取返回数据
             */
            entity = httpresponse.getEntity();
            body = EntityUtils.toString(entity, encoding);
       
        } catch (Exception e) {
        	//logger.info("随行付请求异常，异常详情：{}", e);
        	e.printStackTrace();
        } 
        return body;
    }
    
    public static String doPost(String url, String json) {
    	 String body = null;
         HttpEntity entity1 = null;
         HttpClient httpClient = new DefaultHttpClient();
		try{
			HttpPost post = new HttpPost(url);
			StringEntity entity = new StringEntity(json, "UTF-8");
			post.setEntity(entity);
			post.setHeader("Content-Type", "application/json;charset=utf-8");
			//logger.info("post:" + post);

	        org.apache.http.HttpResponse httpresponse = httpClient.execute(post);
	        
	        entity1 = httpresponse.getEntity();
	        body = EntityUtils.toString(entity1, "UTF-8");
	        System.out.println(body);
		}catch (Exception ex) {
			ex.printStackTrace();
			return "exception";
		 } finally {
	            try {
	                EntityUtils.consume(entity1);
	            } catch (IOException e) {
	            }
	            httpClient.getConnectionManager().shutdown();
	        }
		return body;
	}
}

package xdt.dto.mb;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import com.google.gson.Gson;


public class DemoBase {

	static	HttpClient   client = new HttpClient();
	
	
	public static String   requestBody(String merId,String transData,String testUrl){
	PostMethod  method= new PostMethod(testUrl);
	method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk");    
	method.setParameter("merId",merId);
	method.setParameter("transData",transData);

	client.setConnectionTimeout(8000);
	client.setTimeout(60000);
	
	try {
		int statusCode = client.executeMethod(method);
		if (statusCode != 200) {
			System.out.println("statusCode=" + statusCode);
			return null;
	    }else{
	    	String resp = method.getResponseBodyAsString();
	    	System.out.println("resp="+resp);
	    	return resp;
	    }
	} catch (HttpException e){
		e.printStackTrace();
		return ""+e.getMessage();
	} catch (IOException e) {
		e.printStackTrace();
		return ""+e.getMessage();
	}
	}
	
	
	/***
	 * 
	 * @param url
	 * @param map
	 * @param charSet
	 * @return
	 */
	public static  String POSTReturnString(String url, Map<String, String> map,String charSet) {
		PostMethod method = new PostMethod(url);
		method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=" + charSet);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			method.setParameter(entry.getKey(), entry.getValue());
		}
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != 200) {
				System.out.println("statusCode=" + statusCode);
				return null;
			} else {
				String resp = method.getResponseBodyAsString();
				System.out.println("resp=" + resp);
				return resp;
			}
		} catch (HttpException e) {
			e.printStackTrace();
			return "" + e.getMessage();
		} catch (IOException e) {
			e.printStackTrace();
			return "" + e.getMessage();
		}
	}
}

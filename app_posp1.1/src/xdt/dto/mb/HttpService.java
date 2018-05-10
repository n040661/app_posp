package xdt.dto.mb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;

public class HttpService {
	
		private HttpClient client = new HttpClient();
		private   Logger loger = Logger.getLogger(HttpService.class);
		
		/***
		 * POST 方法
		 * @param url  连接地址
		 * @param map  map<String,String> 参数
		 * @param charSet  字符编码格式
		 * @return  String 字符串
		 */
		public   String POSTReturnString(String url, Map<String, String> map,String charSet) {
			loger.info("map1:"+map);
			PostMethod method = new PostMethod(url);
			client.getParams().setBooleanParameter("http.protocol.expect-continue", false);  
			method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=" + charSet);
			for (Map.Entry<String, String> entry :map.entrySet()) {
				loger.info(entry.getKey()+"***********"+entry.getValue());
				method.setParameter(entry.getKey(), entry.getValue());
			}
			try {
				method.addRequestHeader("Connection", "close");  
				int statusCode = client.executeMethod(method);
				if (statusCode != 200) {
					loger.info("statusCode=" + statusCode);
					return null;
				} else {
					String resp = method.getResponseBodyAsString();
					loger.info("resp=" + resp);
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
		
		/***
		 * POST 方法 
		 * @param url
		 * @param map
		 * @param charSet
		 * @return  返回map格式数据
		 */
		public Map<String,String> POSTReturnMap(String url, Map<String, String> map,String charSet) {
			loger.info("map2:"+map);
			PostMethod method = new PostMethod(url);
			method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=" + charSet);
			
			for (Map.Entry<String, String> entry : map.entrySet()) {
				loger.info(entry.getKey()+"========"+entry.getValue());
				method.setParameter(entry.getKey(), entry.getValue());
			}
			try {
				int statusCode = client.executeMethod(method);
				if (statusCode != 200) {
					loger.info("statusCode=" + statusCode);
					return null;
				} else {
					map.clear();
					
				    BufferedReader in = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()),1024*1024);
				    List result = new ArrayList();
				    String line="";
				    while (true){
				    	line= in.readLine();
				        if (line == null) {
				        	break;
				        }
				        else {
				      	  	result.add(line);
				        }
				    }
				    loger.info("锟斤拷锟截憋拷锟斤拷"+line);
				    
				    in.close();
				    Iterator iter	= result.iterator();
				    while(iter.hasNext()) {
				    	String temp =((String)iter.next());
				    	if(temp.equals("")) {
				    		continue;
				    	}
						int i = temp.indexOf("=");
						int j = temp.length();
						if(i >= 0) {
								String tempKey		= temp.substring(0, i);
								String tempValue	= URLDecoder.decode(temp.substring(i+1, j), "GBK");
								loger.info(tempKey+"========="+tempValue);
								map.put(tempKey, tempValue);
						}
				    }
					return map;
				}
			} catch (HttpException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		/***
		 * POST 方法   
		 * @param url 地址
		 * @param map 流的格式
		 * @param charSet
		 * @return 返回 String 
		 */
		public   String POSTReturnString(String url,String xml,String charSet,String contype) {
			PostMethod method = new PostMethod(url);
//			method.setRequestHeader("Content-Type",contype);
//			method.setRequestHeader("Content-Type",contype+",charset=gbk");
			method.setRequestHeader("Accept","image/gif,image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*");
			method.setRequestHeader("Content-Length",xml.length()+"");
			method.setRequestHeader("Accept-Language","zh-cn");
//			method.setRequestHeader("Proxcept-Language","zh-cn");
			method.setRequestHeader("Pry-Connection","Keep-Alive");
			
			method.setRequestHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			method.setRequestBody(xml);
			try {
				int statusCode = client.executeMethod(method);
				if (statusCode != 200) {
					loger.info("statusCode=" + statusCode);
					return null;
				} else {
					String resp = new String(method.getResponseBodyAsString().getBytes("iso-8859-1"));
					loger.info(resp);
					return resp;
				}
			} catch (HttpException e) {
				e.printStackTrace();
				return "" + e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				return "" + e.getMessage();
			}finally{
				//关闭连接
				method.releaseConnection();
			    ((Category)client.getHttpConnectionManager()).shutdown();  
			}
		}
			
}

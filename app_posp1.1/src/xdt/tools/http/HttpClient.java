package xdt.tools.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.SortedMap;

import xdt.tools.http.ssl.SslConnection;

/**
 * 描述： http通讯基础类
 * 
 */
public class HttpClient {
	
	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
	public static String REQUEST_METHOD_GET = "GET";
	public static String REQUEST_METHOD_POST = "POST";
	
	public  String send(String postURL, String requestBody,
			String sendCharset, String readCharset) {
		return send(postURL,requestBody,sendCharset,readCharset,300,300);
	}
	
	
	public  String send(String postURL, String requestBody,
			String sendCharset, String readCharset,String RequestMethod) {
		return send(postURL,requestBody,sendCharset,readCharset,300,300,RequestMethod);
	}
	
	/**
	 * @param postURL  访问地址
	 * @param requestBody  paramName1=paramValue1&paramName2=paramValue2
	 * @param sendCharset  发送字符编码
	 * @param readCharset  返回字符编码
	 * @param connectTimeout  连接主机的超时时间 单位:秒
	 * @param readTimeout 从主机读取数据的超时时间 单位:秒
	 * @return 通讯返回
	 */
	public  String send(String url, String requestBody,
			String sendCharset, String readCharset,int connectTimeout,int readTimeout) {
		try {
			return connection(url,requestBody,sendCharset,readCharset,connectTimeout,readTimeout,REQUEST_METHOD_POST);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.print("发送请求[" + url + "]失败，" + ex.getMessage());
			return null;
		} 
	}
	
	/**
	 * @param postURL  访问地址
	 * @param requestBody  paramName1=paramValue1&paramName2=paramValue2
	 * @param sendCharset  发送字符编码
	 * @param readCharset  返回字符编码
	 * @param connectTimeout  连接主机的超时时间 单位:秒
	 * @param readTimeout 从主机读取数据的超时时间 单位:秒
	 * @param RequestMethod GET或POST
	 * @return 通讯返回
	 */
	public  String send(String url, String requestBody,
			String sendCharset, String readCharset,int connectTimeout,int readTimeout,String RequestMethod) {
		try {
			return connection(url,requestBody,sendCharset,readCharset,connectTimeout,readTimeout,RequestMethod);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.print("发送请求[" + url + "]失败，" + ex.getMessage());
			return null;
		} 
	}
	
	public  String connection(String postURL, String requestBody,
			String sendCharset, String readCharset,int connectTimeout,int readTimeout,String RequestMethod)throws Exception {
		if(REQUEST_METHOD_POST.equals(RequestMethod)){
			return postConnection(postURL,requestBody,sendCharset,readCharset,connectTimeout,readTimeout,null);
		}else if(REQUEST_METHOD_GET.equals(RequestMethod)){
			return getConnection(postURL,requestBody,sendCharset,readCharset,connectTimeout,readTimeout,null);
		}else{
			return "";
		}
		 
	}
	
	@SuppressWarnings("rawtypes")
	public  String getConnection(String url, String requestBody,
			String sendCharset, String readCharset,int connectTimeout,int readTimeout,SortedMap reqHead)throws Exception {
		// Post请求的url，与get不同的是不需要带参数
		HttpURLConnection httpConn = null;
		try {
			
			if (!url.contains("https:")) {
				URL postUrl = new URL(url);
				// 打开连接
				httpConn = (HttpURLConnection) postUrl.openConnection();
			} else {
				SslConnection urlConnect = new SslConnection();
				httpConn = (HttpURLConnection) urlConnect.openConnection(url);
			}
			
			httpConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=" + sendCharset);
			
			if(reqHead!=null&&reqHead.size()>0){
				Iterator iterator =reqHead.keySet().iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					String val = (String)reqHead.get(key);
					httpConn.setRequestProperty(key,val);
				}
			}
			
			// 设定传送的内容类型是可序列化的java对象 
			// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException) httpUrlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object"); 
//			连接主机的超时时间（单位：毫秒）
			httpConn.setConnectTimeout(1000 * connectTimeout);
//			从主机读取数据的超时时间（单位：毫秒） 
			httpConn.setReadTimeout(1000 * readTimeout);
			// 连接，从postUrl.openConnection()至此的配置必须要在 connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行 connect。
			httpConn.connect();
		
			int status = httpConn.getResponseCode();
			setStatus(status);
			if (status != HttpURLConnection.HTTP_OK) {
				System.out.print("发送请求失败，状态码：[" + status + "] 返回信息："
						+ httpConn.getResponseMessage());
				return null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn
					.getInputStream(), readCharset));
			StringBuffer responseSb = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				responseSb.append(line.trim());
			}
			reader.close();
			return responseSb.toString().trim();
		} finally {
			httpConn.disconnect();
		}
	}
	
	
	@SuppressWarnings("rawtypes")
	public  String postConnection(String postURL, String requestBody,
			String sendCharset, String readCharset,int connectTimeout,int readTimeout,SortedMap reqHead)throws Exception {
		// Post请求的url，与get不同的是不需要带参数
		HttpURLConnection httpConn = null;
		try {
			
			if (!postURL.contains("https:")) {
				URL postUrl = new URL(postURL);
				// 打开连接
				httpConn = (HttpURLConnection) postUrl.openConnection();
			} else {
				SslConnection urlConnect = new SslConnection();
				httpConn = (HttpURLConnection) urlConnect.openConnection(postURL);
			}
			
//			 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在 
//			 http正文内，因此需要设为true, 默认情况下是false; 
			httpConn.setDoOutput(true);
			// 设置是否从httpUrlConnection读入，默认情况下是true; 
			httpConn.setDoInput(true);
			// 设定请求的方法为"POST"，默认是GET 
			httpConn.setRequestMethod("POST");
			// Post 请求不能使用缓存 
			httpConn.setUseCaches(false);
			//进行跳转
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=" + sendCharset);
			
			if(reqHead!=null&&reqHead.size()>0){
				Iterator iterator =reqHead.keySet().iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					String val = (String)reqHead.get(key);
					httpConn.setRequestProperty(key,val);
				}
			}
			
			// 设定传送的内容类型是可序列化的java对象 
			// (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException) httpUrlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object"); 
//			连接主机的超时时间（单位：毫秒）
			httpConn.setConnectTimeout(1000 * connectTimeout);
//			从主机读取数据的超时时间（单位：毫秒） 
			httpConn.setReadTimeout(1000 * readTimeout);
			// 连接，从postUrl.openConnection()至此的配置必须要在 connect之前完成，
			// 要注意的是connection.getOutputStream会隐含的进行 connect。
			httpConn.connect();
			DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());
			out.write(requestBody.getBytes(sendCharset));
			out.flush();
			out.close();
			int status = httpConn.getResponseCode();
			setStatus(status);
			if (status != HttpURLConnection.HTTP_OK) {
				System.out.print("发送请求失败，状态码：[" + status + "] 返回信息："
						+ httpConn.getResponseMessage());
				return null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn
					.getInputStream(), readCharset));
			StringBuffer responseSb = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				responseSb.append(line.trim());
			}
			reader.close();
			return responseSb.toString().trim();
		} finally {
			httpConn.disconnect();
		}
	}
	
	public static void main(String[] args) {
	
		
	}

}

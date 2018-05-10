package xdt.dto.yf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * HttpClient工具类
 * http://blog.itpub.net/374079/viewspace-131152/
 * http://blog.csdn.net/jdsjlzx/article/details/8644382
 * https://yq.aliyun.com/articles/13291
 * http://www.cnblogs.com/Scott007/p/3817285.html --ok
 * http://www.cnblogs.com/Scott007/p/3849677.html --Spark
 * http://www.cnblogs.com/Scott007/p/3320938.html --Storm框架入门
 */
public class HttpClientUtils {
	private static Logger log = LoggerFactory.getLogger(HttpClientUtils.class);
	
	public static final String URL_PARAM_CHARSET_UTF8 = "UTF-8"; // 定义编码格式 UTF-8
	public static final String URL_PARAM_CHARSET_GBK = "GBK"; // 定义编码格式 GBK
	private static final String EMPTY = "";
	
	private static MultiThreadedHttpConnectionManager connectionManager = null;
	private static int connectionTimeOut = Integer.valueOf("20000");
	private static int socketTimeOut = Integer.valueOf("30000");
	private static int maxConnectionPerHost = 20;
	private static int maxTotalConnections = 20;
	private static HttpClient client;
	
	static {
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setConnectionTimeout(connectionTimeOut);
		connectionManager.getParams().setSoTimeout(socketTimeOut);
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
		connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);
		client = new HttpClient(connectionManager);
	}
	
	/**
	 * POST方式提交数据
	 * 
	 * @param url
	 *            待请求的URL
	 * @param params
	 *            要提交的数据
	 * @param charset
	 *            编码
	 * @return 响应结果
	 * @throws IOException
	 *             IO异常
	 */
	public static String sendPost(String url, Map<String, String> params, String charset) throws Exception {
		url = url.replace("https", "http");
		String response = EMPTY;
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		// 将表单的值放入postMethod中
		log.debug("---组装发送信息---");
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			String value = params.get(key);
			postMethod.addParameter(key, value);
		}
		log.debug("---开始发送信息---");
		try {
			// 执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				response = postMethod.getResponseBodyAsString();
			} else {
				throw new RuntimeException("响应状态码 = " + postMethod.getStatusCode());
			}
			log.debug("---结束返回信息---");
		} catch (HttpException e) {
			throw new HttpException("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
		} catch (IOException e) {
			throw new IOException("发生网络异常", e);
		} catch (Exception e) {
			throw new Exception(String.format("POST方式提交数据异常（%s）", e.getMessage()), e);
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
		return response;
	}
	
	
	
	public static String sendPostInstream(String url, Map<String, String> params, String charset,String downloadFilePath) throws Exception {
		url = url.replace("https", "http");
		String response = EMPTY;
		InputStream inputStream = null;
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		FileOutputStream output = null;
		// 将表单的值放入postMethod中
		log.debug("---组装发送信息---");
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			String value = params.get(key);
			postMethod.addParameter(key, value);
		}
		log.debug("---开始发送信息---");
		try {
			// 执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				File storeFile = new File(downloadFilePath);
				if(!storeFile.exists()){
					storeFile.getParentFile().mkdirs();
				}
				output = new FileOutputStream(storeFile);
				output.write(postMethod.getResponseBody());
			//	response = postMethod.getRequestHeader("filename").getValue();
			} else {
				throw new RuntimeException("响应状态码 = " + postMethod.getStatusCode());
			}
			log.debug("---结束返回信息---");
		} catch (HttpException e) {
			throw new HttpException("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
		} catch (IOException e) {
			throw new IOException("发生网络异常", e);
		} catch (Exception e) {
			throw new Exception(String.format("POST方式提交数据异常（%s）", e.getMessage()), e);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
			output.close();
		}
		return response;
	}
	
	public static String sendMutiPost(String url, Map<String, String> params, String charset, String filePath) throws Exception {
		url = url.replace("https", "http");
		String response = EMPTY;
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		// 将表单的值放入postMethod中
		log.debug("---组装发送信息---");
		Set<String> keySet = params.keySet();
		
		int len = keySet.size()+1;
		Part[] parts = new Part[len];
		int count = 0;
		for (String key : keySet) {
			String value = params.get(key);
			StringPart strPart = new StringPart(key, value);
			parts[count++] =  strPart;
		}
		File file =  new File(filePath);
		parts[count++] = new FilePart(URLEncoder.encode(file.getName(),"utf-8"), file);
		
//		Part[] parts = {new FilePart(URLEncoder.encode(file.getName(),"utf-8"), file)};
		/*if(filePaths !=null && filePaths.size()>0){
			Part[] parts = new Part[filePaths.size()];
			int i = 0;
			for(String fpath: filePaths){
				File file =  new File(fpath);
				parts[i] =  new FilePart(file.getName(), file);
				i++;
			}
			postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
		}*/
		postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
		log.debug("---开始发送信息---");
		try {
			// 执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				response = postMethod.getResponseBodyAsString();
			} else {
				throw new RuntimeException("响应状态码 = " + postMethod.getStatusCode());
			}
			log.debug("---结束返回信息---");
		} catch (HttpException e) {
			throw new HttpException("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
		} catch (IOException e) {
			throw new IOException("发生网络异常", e);
		} catch (Exception e) {
			throw new Exception(String.format("POST方式提交数据异常（%s）", e.getMessage()), e);
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
		return response;
	}
	
	/**
	 * POST方式提交数据
	 * 
	 * @param url
	 *            待请求的URL
	 * @param params
	 *            要提交的数据
	 * @param charset
	 *            编码
	 * @return 响应结果
	 * @throws IOException
	 *             IO异常
	 */
	public static int sendPostGetStatus(String url, Map<String, String> params, String charset) throws Exception {
		url = url.replace("https", "http");
		int statusCode = 300;
		String response = EMPTY;
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		// 将表单的值放入postMethod中
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			String value = params.get(key);
			if(value == null){
				postMethod.addParameter(key, "");
			}else{
				postMethod.addParameter(key, value);
			}
		}
		try {
			// 执行postMethod
			statusCode = client.executeMethod(postMethod);
			return statusCode;
			
		} catch (HttpException e) {
			throw new HttpException("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
		} catch (IOException e) {
			throw new IOException("发生网络异常", e);
		} catch (Exception e) {
			throw new Exception(String.format("POST方式提交数据异常（%s）", e.getMessage()), e);
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
		//		return statusCode;
	}
	
	public static String sendGet(String url, String charset) throws Exception {
		
		String response = EMPTY;
		GetMethod getMethod = new GetMethod(url);
		getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		try {
			int statusCode = client.executeMethod(getMethod);
			log.debug("裕福商城的返回响应状态："+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				response = getMethod.getResponseBodyAsString();
			} else {
				throw new RuntimeException("响应状态码 = " + getMethod.getStatusCode());
			}
		} catch (HttpException e) {
			throw new HttpException("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
		} catch (IOException e) {
			throw new IOException("发生网络异常", e);
		} catch (Exception e) {
			throw new Exception(String.format("GET方式提交数据异常（%s）", e.getMessage()), e);
		} finally {
			if (getMethod != null) {
				getMethod.releaseConnection();
			}
		}
		return response;
	}
	
	/**
	 * POST方式提交数据
	 * 
	 * @param url
	 *            待请求的URL
	 * @param body
	 *            要提交的数据
	 * @param charset
	 *            编码
	 * @return 响应结果
	 * @throws IOException
	 *             IO异常
	 */
	public static String sendPost2Body(String url, String body, String charset) throws Exception {
		url = url.replace("https", "http");
		
		StringBuffer response = new StringBuffer(EMPTY);
		InputStream inputStream = null;
		BufferedReader reader = null;
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		// 将要提交的数据放入postMethod中
		postMethod.setRequestBody(body);
		postMethod.setRequestHeader("Accept","application/json");
		try {
			// 执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				inputStream = postMethod.getResponseBodyAsStream();
				reader = new BufferedReader(new InputStreamReader(inputStream, postMethod.getResponseCharSet()));
				String inputLine = null;
				while ((inputLine = reader.readLine()) != null) {
					response.append(inputLine);
				}
			} else {
				throw new Exception("响应状态码 = " + postMethod.getStatusCode());
			}
		} catch (HttpException e) {
			throw new HttpException("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
		} catch (IOException e) {
			throw new IOException("发生网络异常", e);
		} catch (Exception e) {
			throw new Exception(String.format("POST方式提交数据异常（%s）", e.getMessage()), e);
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
		return response.toString();
	}
	
	/**
	 * POST方式提交数据
	 * 
	 * @param url
	 *            待请求的URL
	 * @param body
	 *            要提交的数据
	 * @param charset
	 *            编码
	 * @param contentType
	 *            MIME类型
	 * @return 响应结果
	 * @throws IOException
	 *             IO异常
	 */
	public static String sendPost2Body(String url, String body, String charset, String contentType) throws Exception {
		url = url.replace("https", "http");
		
		StringBuffer response = new StringBuffer(EMPTY);
		InputStream inputStream = null;
		BufferedReader reader = null;
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestHeader("Content-Type", contentType + ";charset=" + charset);
		// 将要提交的数据放入postMethod中
		postMethod.setRequestBody(body);
		try {
			// 执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				inputStream = postMethod.getResponseBodyAsStream();
				reader = new BufferedReader(new InputStreamReader(inputStream, postMethod.getResponseCharSet()));
				String inputLine = null;
				while ((inputLine = reader.readLine()) != null) {
					response.append(inputLine);
				}
			} else {
				throw new Exception("响应状态码 = " + postMethod.getStatusCode());
			}
		} catch (HttpException e) {
			throw new HttpException("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
		} catch (IOException e) {
			throw new IOException("发生网络异常", e);
		} catch (Exception e) {
			throw new Exception(String.format("POST方式提交数据异常（%s）", e.getMessage()), e);
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (postMethod != null) {
				postMethod.releaseConnection();
			}
		}
		return response.toString();
	}
	
}

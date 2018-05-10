package xdt.dto.yf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.spi.LoggerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdt.controller.BaseAction;
import com.yeepay.shade.org.apache.http.HttpRequest;



/**
 * HttpClient工具类
 */
public class HttpClientUtilBuilds {

	public static final String URL_PARAM_CHARSET_UTF8 = "UTF-8"; // 定义编码格式 UTF-8
	public static final String URL_PARAM_CHARSET_GBK = "GBK"; // 定义编码格式 GBK
	
	public org.apache.log4j.Logger loggers = org.apache.log4j.Logger.getLogger(BaseAction.class);
	private static final String EMPTY = "";

	private static MultiThreadedHttpConnectionManager connectionManager = null;
	private static int connectionTimeOut = 2000;
	private static int socketTimeOut = 4000;
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
	 * POST方式提交数据 https方式
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
	/*
	public static String sendHTTPSPost(String url, Map<String, String> params, String charset) throws Exception {
	//	url = url.replace("https", "http");
		
		String response = EMPTY;
		Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);   
		Protocol.registerProtocol("https", myhttps);   
		PostMethod postMethod = new PostMethod(url);  
		
	//	PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
		// 将表单的值放入postMethod中
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			String value = params.get(key);
			postMethod.addParameter(key, value);
		}
		try {
			// 执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				response = postMethod.getResponseBodyAsString();
			} else {
				throw new RuntimeException("响应状态码 = " + postMethod.getStatusCode());
			}
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
	*/
	/**
	 * POST方式提交数据
	 *
	 * @param url
	 *            待请求的URL
	 * @param params
	 *            要提交的数据
	 *            
	 * @param filedata
	 * 			文件数据
	 * @param charset
	 *            编码
	 * @return 响应结果
	 * @throws IOException
	 *             IO异常
	 */
	public static String sendPost(String url, Map<String, String> params,List<HTTPFile> filedata ,String charset) throws Exception {
		System.out.println("到了1111");
		url = url.replace("https", "http");	
		System.out.println("裕富httpclient请求进来了");
		System.out.println("到了2222");
	    org.apache.http.client.HttpClient httpclient = HttpClients.createDefault();
	    System.out.println("裕富httpclient请求出去了");
	    System.out.println("到了3333");
	    String result = "";
	    HttpPost postMethod = new HttpPost(url);
	    System.out.println("到了4444");
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    System.out.println("到了555");
	    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    builder.seContentType(ContentType.MULTIPART_FORM_DATA);
	    builder.setCharset(Charset.forName("UTF-8")); 
	    if (params != null) {
	      Set<String> keySet = params.keySet();
	      for (String key : keySet) {
	   //     String value = URLEncoder.encode((String)params.get(key), "UTF-8");
	        builder.addTextBody(key, params.get(key), ContentType.TEXT_PLAIN);
	      }
	      System.out.println("到了666");
	    }
	    for (HTTPFile file : filedata)
	      builder.addBinaryBody(file.getInputname(), file.getFiledata(), ContentType.MULTIPART_FORM_DATA, file.getFilename());
	    try
	    {
	    	 System.out.println("到了777");
	      builder.setCharset(Charset.forName("UTF-8"));
	      HttpEntity entity = builder.build();
	      System.out.println("到了888");
	      postMethod.setEntity(entity);
	      System.out.println("到了999");
	      HttpResponse response = httpclient.execute(postMethod);
	      System.out.println("到了9999");
	      int statusCode = response.getStatusLine().getStatusCode();
	      if (statusCode == 200) {
	        HttpEntity responseEntity = response.getEntity();
	        if (responseEntity != null)
	        {
	          result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
	        }
	      } else {
	        throw new RuntimeException("响应状态码 = " + statusCode);
	      }
	    } catch (HttpException e) {
	      throw new HttpException("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
	    } catch (IOException e) {
	      throw new IOException("发生网络异常", e);
	    } catch (Exception e) {
	      throw new Exception(String.format("POST方式提交数据异常（%s）", new Object[] { e.getMessage() }), e);
	    } finally {
	      if (postMethod != null) {
	        postMethod.releaseConnection();
	      }
	    }
	    return result;
	}
	
	public static String sendGetFile(String url, String fileName,String charset) throws Exception {
		url = url.replace("https", "http");
		String result="";
		GetMethod getMethod=new GetMethod(url);
		getMethod.setRequestHeader("Content-Type", "application/octet-stream;charset=" + charset);
		FileOutputStream output = null;
		try {
			int statusCode = client.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				File storeFile = new File(fileName);
				output = new FileOutputStream(storeFile);
				output.write(getMethod.getResponseBody());
				result = getMethod.getResponseBodyAsString();
			} else {
				throw new RuntimeException("响应状态码 = " +statusCode);
			}
		} catch (HttpException e) {
			throw new HttpException("发生致命的异常，可能是协议不对或者返回的内容有问题", e);
		} catch (IOException e) {
			throw new IOException("发生网络异常", e);
		} catch (Exception e) {
			throw new Exception(String.format("POST方式提交数据异常（%s）", e.getMessage()), e);
		} finally {
			if (getMethod != null) {
				getMethod.releaseConnection();
			}
			try {
				if(output != null){
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
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
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			String value = params.get(key);
			postMethod.addParameter(key, value);
		}
		try {
			// 执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				response = postMethod.getResponseBodyAsString();
			} else {
				throw new RuntimeException("响应状态码 = " + postMethod.getStatusCode());
			}
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
//		postMethod.setFollowRedirects(true);
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

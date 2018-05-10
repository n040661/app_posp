package xdt.quickpay.conformityQucikPay.util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import xdt.service.impl.TmhServiceImpl;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by dell on 2015/1/13.
 */
public class HttpClientUtil {

	private static final String pattern = "yyyy-MM-dd HH:mm:ss:SSS";
	private HttpClient httpClient = null;
	private HttpPost method = null;
	private long startTime = 0L;
	private long endTime = 0L;
	private int status = 0;

	private Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	public static String URL = "http://180.166.114.155:58082/delegate-collect-front/subcontract/signSimpleSubContractJson";

	/**
	 * 封装HTTP POST方法
	 * 
	 * @param
	 * @param
	 * @return
	 * @throws ClientProtocolException
	 * @throws java.io.IOException
	 */
	public static String post(String url, Map paramMap) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		/** 忽略SSL证书校验 */
		try {
			// Secure Protocol implementation.
			SSLContext ctx = SSLContext.getInstance("SSL");
			// Implementation of a trust manager for X509 certificates
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ClientConnectionManager ccm = httpClient.getConnectionManager();
			// register https protocol in httpclient's scheme registry
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", 443, ssf));
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> formparams = setHttpParams(paramMap);
		UrlEncodedFormEntity param = new UrlEncodedFormEntity(formparams, "UTF-8");
		httpPost.setEntity(param);
		HttpResponse response = httpClient.execute(httpPost);
		String httpEntityContent = getHttpEntityContent(response);
		httpPost.abort();
		return httpEntityContent;
	}

	/**
	 * 封装HTTP POST方法
	 * 
	 * @param
	 * @param （如JSON串）
	 * @return
	 * @throws ClientProtocolException
	 * @throws java.io.IOException
	 */
	public static String post(String url, String data) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "text/json; charset=utf-8");
		// httpPost.setEntity(new StringEntity(URLEncoder.encode(data, "UTF-8")));
		httpPost.setEntity(new StringEntity(data, "UTF-8"));
		HttpResponse response = httpClient.execute(httpPost);
		String httpEntityContent = getHttpEntityContent(response);
		httpPost.abort();
		return httpEntityContent;
	}

	/**
	 * 封装HTTP GET方法
	 * 
	 * @param
	 * @return
	 * @throws ClientProtocolException
	 * @throws java.io.IOException
	 */
	public static String get(String url) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(URI.create(url));
		HttpResponse response = httpClient.execute(httpGet);
		String httpEntityContent = getHttpEntityContent(response);
		httpGet.abort();
		return httpEntityContent;
	}

	/**
	 * 封装HTTP GET方法
	 * 
	 * @param
	 * @param
	 * @return
	 * @throws ClientProtocolException
	 * @throws java.io.IOException
	 */
	public static String get(String url, Map<String, String> paramMap) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet();
		List<NameValuePair> formparams = setHttpParams(paramMap);
		String param = URLEncodedUtils.format(formparams, "UTF-8");
		httpGet.setURI(URI.create(url + "?" + param));
		HttpResponse response = httpClient.execute(httpGet);
		String httpEntityContent = getHttpEntityContent(response);
		httpGet.abort();
		return httpEntityContent;
	}

	/**
	 * 封装HTTP PUT方法
	 * 
	 * @param
	 * @param
	 * @return
	 * @throws ClientProtocolException
	 * @throws java.io.IOException
	 */
	public static String put(String url, Map<String, String> paramMap) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPut httpPut = new HttpPut(url);
		List<NameValuePair> formparams = setHttpParams(paramMap);
		UrlEncodedFormEntity param = new UrlEncodedFormEntity(formparams, "UTF-8");
		httpPut.setEntity(param);
		HttpResponse response = httpClient.execute(httpPut);
		String httpEntityContent = getHttpEntityContent(response);
		httpPut.abort();
		return httpEntityContent;
	}

	/**
	 * 封装HTTP DELETE方法
	 * 
	 * @param
	 * @return
	 * @throws ClientProtocolException
	 * @throws java.io.IOException
	 */
	public static String delete(String url) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete();
		httpDelete.setURI(URI.create(url));
		HttpResponse response = httpClient.execute(httpDelete);
		String httpEntityContent = getHttpEntityContent(response);
		httpDelete.abort();
		return httpEntityContent;
	}

	/**
	 * 封装HTTP DELETE方法
	 * 
	 * @param
	 * @param
	 * @return
	 * @throws ClientProtocolException
	 * @throws java.io.IOException
	 */
	public static String delete(String url, Map<String, String> paramMap) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete();
		List<NameValuePair> formparams = setHttpParams(paramMap);
		String param = URLEncodedUtils.format(formparams, "UTF-8");
		httpDelete.setURI(URI.create(url + "?" + param));
		HttpResponse response = httpClient.execute(httpDelete);
		String httpEntityContent = getHttpEntityContent(response);
		httpDelete.abort();
		return httpEntityContent;
	}

	/**
	 * 设置请求参数
	 * 
	 * @param
	 * @return
	 */
	private static List<NameValuePair> setHttpParams(Map<String, String> paramMap) {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		Set<Map.Entry<String, String>> set = paramMap.entrySet();
		for (Map.Entry<String, String> entry : set) {
			formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return formparams;
	}

	/**
	 * 获得响应HTTP实体内容
	 * 
	 * @param response
	 * @return
	 * @throws java.io.IOException
	 * @throws java.io.UnsupportedEncodingException
	 */
	private static String getHttpEntityContent(HttpResponse response) throws IOException, UnsupportedEncodingException {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream is = entity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			while (line != null) {
				sb.append(line + "\n");
				line = br.readLine();
			}
			return sb.toString();
		}
		return "";
	}

	// public String post(String parameters) {
	// String body = null;
	// logger.info("parameters:" + parameters);
	//
	// if (method != null & parameters != null
	// && !"".equals(parameters.trim())) {
	// try {
	//
	// // 建立一个NameValuePair数组，用于存储欲传送的参数
	// method.addHeader("Content-type","application/json; charset=utf-8");
	// method.setHeader("Accept", "application/json");
	// method.setEntity(new StringEntity(parameters, "UTF-8"));
	// startTime = System.currentTimeMillis();
	//
	// HttpResponse response = httpClient.execute(method);
	//
	// endTime = System.currentTimeMillis();
	// int statusCode = response.getStatusLine().getStatusCode();
	//
	// logger.info("statusCode:" + statusCode);
	// logger.info("调用API 花费时间(单位：毫秒)：" + (endTime - startTime));
	// if (statusCode != 200) {
	// logger.error("Method failed:" + response.getStatusLine());
	// status = 1;
	// }
	//
	// // Read the response body
	// body = EntityUtils.toString(response.getEntity());
	//
	// } catch (IOException e) {
	// // 网络错误
	// status = 3;
	// } finally {
	// logger.info("调用接口状态：" + status);
	// }
	//
	// }
	// return body;
	// }
	public static String post(String url,JSONObject json) {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		post.setHeader("Content-Type", "application/json");
		post.addHeader("Authorization", "Basic YWRtaW46");
		String result = "";

		try {

			StringEntity s = new StringEntity(json.toString(), "utf-8");
			s.setContentEncoding((Header) new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(s);

			// 发送请求
			HttpResponse httpResponse = client.execute(post);

			// 获取响应输入流
			InputStream inStream = httpResponse.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
			StringBuilder strber = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
				strber.append(line + "\n");
			inStream.close();

			result = strber.toString();
			System.out.println(result);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				System.out.println("请求服务器成功，做相应处理");

			} else {

				System.out.println("请求服务端失败");

			}

		} catch (Exception e) {
			System.out.println("请求异常");
			throw new RuntimeException(e);
		}

		return result;
	}
    public static String parseParams(Map<String, String> params)  {
        int i = 0;
        String param = "";
        if (params.containsKey("content")) {
            params.put("content", params.get("content"));
        }
        for (String key : params.keySet()) {
            if (i > 0) {
                param += "&";
            }
            param += key + "=" + params.get(key);
            i++;
        }
        return param;
    }
	/**
	 * bean 转查询串
	 * @param obj
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static  String bean2QueryStr(Object obj) throws IllegalArgumentException, IllegalAccessException{
		StringBuffer sb=new StringBuffer();
		if(obj==null){
			return null;
		}
		
		System.out.println(obj);
		
		Class clazz=obj.getClass();
		System.out.println(obj.getClass().getName());
		Field[] fields=clazz.getDeclaredFields();
		System.out.println("字段个数："+fields.length);
		for (Field field : fields) {
			field.setAccessible(true);
			if(field.get(obj)==null){
				sb.append(field.getName()+"=&");
			}else{
				sb.append(field.getName()+"="+field.get(obj)+"&");
			}
			
		}
		return sb.substring(0, sb.toString().length()-1);
	}

}

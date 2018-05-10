package xdt.quickpay.daikou.util;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 功能：http工具类 
 * 说明： 该代码是示例代码，供研究和开发联通支付接口使用，商户可以按接口自己编写。
 * 该代码只是提供一个参考，并非一定要使用。
 * 
 * 作者 联通支付平台接口开发团队
 * 版本：1.0 
 * 日期：2011-10-25 
 * 
 */
public class HttpUtils {

	/**
	 * send http by post method
	 * 
	 * @param url
	 * @param map
	 * @return List
	 * @throws Exception
	 */
	public static List URLPost(String url, Map map) throws Exception {
		URL sendurl = new URL(url);

		HttpURLConnection httpURLConnection = (HttpURLConnection) sendurl
				.openConnection();
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setUseCaches(false);
		httpURLConnection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded;charset=utf-8");

		StringBuffer sb = new StringBuffer();

		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			sb.append(pairs.getKey()).append("=").append(
					URLEncoder.encode(pairs.getValue().toString(),
							"utf-8")).append("&");
		}
		if (sb.length() > 0)// delete last & char
		{
			sb.setLength(sb.length() - 1);
		}

		// send data
		String sendData = sb.toString();

		OutputStream out = httpURLConnection.getOutputStream();
		out.write(sendData.getBytes("iso8859-1"));
		out.flush();
		out.close();

		InputStream in = httpURLConnection.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		List<String> result = new ArrayList<String>();
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			} else {
				result.add(line);
			}
		}

		reader.close();
		in.close();

		return result;
	}
	
	public static List HTTPSURLPost(String url, Map map) throws Exception {
		
		
		SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
        URL console = new URL(url);
        
        StringBuffer postData = new StringBuffer();

		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			postData.append(pairs.getKey()).append("=").append(
					URLEncoder.encode(pairs.getValue().toString(),
							"utf-8")).append("&");
		}
        
        HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
        conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
       
        conn.setDoInput(true);
        conn.setDoOutput(true);
        BufferedOutputStream hurlBufOus=new BufferedOutputStream(conn.getOutputStream());
        hurlBufOus.write(postData.toString().getBytes("iso8859-1"));//这里面已经将RequestMethod设置为POST.前面设置无效
        hurlBufOus.flush();
       
        conn.connect();
        System.out.println(conn.getResponseCode());
        InputStream in=conn.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		List<String> result = new ArrayList<String>();
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			} else {
				result.add(line);
			}
		}

		reader.close();
		in.close();

		return result;
	}
	
	 private static class TrustAnyTrustManager implements X509TrustManager {
		   
	        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	        }
	   
	        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	        }
	   
	        public X509Certificate[] getAcceptedIssuers() {
	            return new X509Certificate[]{};
	        }
	    }
	   
	    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
	        public boolean verify(String hostname, SSLSession session) {
	            return true;
	        }
	    }
	    
	    public static void main(String[] args) {
	    	HttpUtils h = new HttpUtils();
	    	try {
				System.out.println(h.HTTPSURLPost("https://epay.10010.com/pay/query/order.htm?reqCharSet=UTF-8", new HashMap()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
}

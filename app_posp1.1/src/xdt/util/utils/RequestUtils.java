package xdt.util.utils;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;

import xdt.model.ChannleMerchantConfigKey;
import xdt.service.IClientCollectionPayService;



public class RequestUtils {
	
	Logger log =Logger.getLogger(this.getClass());
	
	public static void main(String[] args) {
		System.out.println("java.nio.charset.Charset.defaultCharset():" + java.nio.charset.Charset.defaultCharset());
        String s = "中文";
        try {
            System.out.println("GBK:" + new String(s.getBytes(), "GBK"));
            System.out.println("UTF-8:" + new String(s.getBytes(), "UTF-8"));
            System.out.println("ISO8859-1:" + new String(s.getBytes(), "ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("java.nio.charset.Charset.defaultCharset():" + java.nio.charset.Charset.defaultCharset());
    }

	public static TreeMap<String, String> Dom2Map(String xml) throws DocumentException {
        Document doc = DocumentHelper.parseText(xml);
        TreeMap<String, String> map = new TreeMap<String, String>();
        if (doc == null)
            return map;
        Element root = doc.getRootElement();
        for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
            Element e = (Element) iterator.next();
            List list = e.elements();
            map.put(e.getName(), e.getText());
        }
        return map;
    }
	
	
	/**
     * 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
     */
    public static String getParamSrc(TreeMap<String, String> paramsMap) {
        StringBuffer paramstr = new StringBuffer();
        for (String pkey : paramsMap.keySet()) {
            String pvalue = paramsMap.get(pkey);
            if (null != pvalue && "" != pvalue && !pkey.equals("sign") && !pkey.equals("retcode")
                    && !pkey.equals("retmsg") && !pkey.equals("sign_type")) {// 空值不传递，不签名
                paramstr.append(pkey + "=" + pvalue + "&"); // 签名原串，不url编码
            }
        }
        // 去掉最后一个&
        String result = paramstr.substring(0, paramstr.length() - 1);
        return result;
    }
    
    
    public static String getParamSrcs(LinkedHashMap<String, String> paramsMap) {
        StringBuffer paramstr = new StringBuffer();
        for (String pkey : paramsMap.keySet()) {
            String pvalue = paramsMap.get(pkey);
            if (null != pvalue && "" != pvalue && !pkey.equals("sign")) {// 空值不传递，不签名
                paramstr.append(pkey + "=" + pvalue + "&"); // 签名原串，不url编码
            }
        }
        // 去掉最后一个&
        String result = paramstr.substring(0, paramstr.length() - 1);
        return result;
    }
    public static String getEncodeParamSrc(TreeMap<String, String> paramsMap,String serverEncodeType) throws UnsupportedEncodingException {
        StringBuffer paramstr = new StringBuffer();
        for (String pkey : paramsMap.keySet()) {
            String pvalue = paramsMap.get(pkey);
            if (null != pvalue && "" != pvalue && !pkey.equals("sign") && !pkey.equals("retcode")
                    && !pkey.equals("retmsg") && !pkey.equals("sign_type")) {// 空值不传递，不签名
                paramstr.append(pkey + "=" + URLEncoder.encode(pvalue, serverEncodeType) + "&"); // 签名原串，不url编码
            }
        }
        // 去掉最后一个&
        String result = paramstr.substring(0, paramstr.length() - 1);
        return result;
    }
    /**
     * 分解解密后的字符串，保存为map
     */
    public static HashMap<String, String> parseString(String responseData) {
        HashMap<String, String> map = new HashMap<String, String>();
        String[] s1 = responseData.split("&");
        String[] s2 = new String[2];
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s1.length; i++) {
            s2 = s1[i].split("=", 2);
            map.put(s2[0], s2[1]);
            if (!s2[0].equals("sign")) {
                sb.append(s2[0] + "=" + s2[1] + "&");
            }
        }
        String source = sb.substring(0, sb.length() - 1);
        //map.put("source", source);
        return map;
    }

    /**
     * 解析xml
     */
    public static String getXmlElement(String responseData, String element) {
        String result = null;
        try {
            Document dom = DocumentHelper.parseText(responseData);
            Element root = dom.getRootElement();
            result = root.element(element).getText();
            
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
        return result;
    }

    public static String sendPost(String url,String param) throws Exception{
    	  
    	byte[] requestBytes;  
    	requestBytes = param.getBytes("utf-8");  
    	  
    	HttpClient httpClient = new HttpClient();  
    	PostMethod postMethod = new PostMethod(url);  
    	postMethod.setRequestHeader("SOAPAction", "http://tempuri.org/GetMiscInfo");//Soap Action Header!  
    	postMethod.setRequestHeader("accept", "*/*");
    	postMethod.setRequestHeader("connection", "Keep-Alive");
    	postMethod.setRequestHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
    	postMethod.setRequestHeader("Content-Type", "application/json");
    	InputStream inputStream = new ByteArrayInputStream(requestBytes, 0, requestBytes.length);  
    	RequestEntity requestEntity = new InputStreamRequestEntity(inputStream, requestBytes.length, "application/soap+xml; charset=utf-8");  
    	postMethod.setRequestEntity(requestEntity);  
    	  
    	int state = httpClient.executeMethod(postMethod);  
    	  
    	InputStream soapResponseStream = postMethod.getResponseBodyAsStream();  
    	InputStreamReader inputStreamReader = new InputStreamReader(soapResponseStream);  
    	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
    	  
    	String responseLine = "";  
    	String soapResponseInfo = "";  
    	while((responseLine = bufferedReader.readLine()) != null) {  
    	    soapResponseInfo = soapResponseInfo + responseLine;  
    	}
		return soapResponseInfo;  
    }
    public static String sendPost(String url,String param,String serverEncodeType) throws Exception{
  	  
    	byte[] requestBytes;  
    	requestBytes = param.getBytes("utf-8");  
    	  
    	HttpClient httpClient = new HttpClient();  
    	PostMethod postMethod = new PostMethod(url);  
    	postMethod.setRequestHeader("SOAPAction", "http://tempuri.org/GetMiscInfo");//Soap Action Header!  
    	postMethod.setRequestHeader("accept", "*/*");
    	postMethod.setRequestHeader("connection", "Keep-Alive");
    	postMethod.setRequestHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
    	postMethod.setRequestHeader("Content-Type", "application/json");
    	InputStream inputStream = new ByteArrayInputStream(requestBytes, 0, requestBytes.length);  
    	RequestEntity requestEntity = new InputStreamRequestEntity(inputStream, requestBytes.length, "application/soap+xml; charset=utf-8");  
    	postMethod.setRequestEntity(requestEntity);  
    	  
    	int state = httpClient.executeMethod(postMethod);  
    	  
    	InputStream soapResponseStream = postMethod.getResponseBodyAsStream();  
    	InputStreamReader inputStreamReader =null;
    	 if(serverEncodeType!=null&&serverEncodeType!=""){
    		  inputStreamReader = new InputStreamReader(soapResponseStream,serverEncodeType); 
         }else{
        	  inputStreamReader = new InputStreamReader(soapResponseStream); 
         }
    	 
    	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
    	  
    	String responseLine = "";  
    	String soapResponseInfo = "";  
    	while((responseLine = bufferedReader.readLine()) != null) {  
    	    soapResponseInfo = soapResponseInfo + responseLine;  
    	}
		return soapResponseInfo;  
    }
    public static String doPost(String url, String param,String serverEncodeType) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //请求http
            //conn.addRequestProperty("Content-Type", "application/json");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            System.out.println("param："+param);
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            if(serverEncodeType!=null&&serverEncodeType!=""){
            	in = new BufferedReader(new InputStreamReader(conn.getInputStream(), serverEncodeType));
            }else{
            	in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
	 * 加密得到cipherData
	 * 
	 * @param paramstr
	 * @return
     * @throws IOException 
	 */
	public static String encrypt(String paramstr,String url) throws IOException {

		String publickey = RSAUtils.loadPublicKey(url);
		System.out.println("publickey:"+publickey);
		String cipherData = null;
		try {
			cipherData = RSAUtils.encryptByPublicKey1(paramstr.getBytes("UTF-8"), publickey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherData;
	}
	public static String parseXml(String responseData) {
		String cipher_data = null;

		try {
			Document dom = DocumentHelper.parseText(responseData);
			Element root = dom.getRootElement();
			String retcode=root.element("retcode").getText();
			System.out.println("retcode:"+retcode);
			if("00".equals(retcode)){
				cipher_data = root.element("cipher_data").getText();
			}else{
				cipher_data="失败";
			}
			
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		return cipher_data;
	}
	/**
	 * rsa解密
	 * 
	 * @param cipherData
	 *            the data to be decrypt
	 * @return
	 */
	public static String decryptResponseData(String cipherData,String privateKey) {
		
		String privatekey = RSAUtils.loadPrivateKey(privateKey);
		String result;
		try {
			result = RSAUtils.decryptByPrivateKey1(Base64.decode(cipherData), privatekey);
			//result = new String(result.getBytes("UTF-8"), "UTF-8");
			System.out.println("解密结果:" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Map<String, Object> beanToMap(Object obj) { 
		Map<String, Object> params = new HashMap<String, Object>(0); 
		try { 
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean(); 
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj); 
			for (int i = 0; i < descriptors.length; i++) { 
				String name = descriptors[i].getName(); 
				if (!StringUtils.equals(name, "class")) { 
					params.put(name, propertyUtilsBean.getNestedProperty(obj, name)); 
				} 
			} 
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return params; 
}
	
	public static String MD5(IClientCollectionPayService clientCollectionPayService,String spid,String paramSrc){
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(spid);
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		System.out.println("key1:"+key);
		String md5 =MD5Utils.sign(paramSrc, key, "UTF-8");
		return md5;
	}
}

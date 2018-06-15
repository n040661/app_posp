package xdt.util;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import xdt.model.SessionInfo;

import javax.servlet.http.HttpSession;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * http第三方接口调用 wumeng 20150504
 */
public class HttpURLConection {
	private static Logger logger = Logger.getLogger(HttpURLConection.class);
    /**
     * 接口调用 GET
     */
    public static String httpURLConectionGET(String GET_URL,String resultCharset) {
    	StringBuffer  sb=null;
    	BufferedReader br = null;
    	HttpURLConnection connection = null;
        try {
            URL url = new URL(GET_URL);    // 把字符串转换为URL请求地址
            connection = (HttpURLConnection) url.openConnection();// 打开连接
            connection.connect();// 连接会话
            // 获取输入流
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(),resultCharset));
            String line;
             sb = new StringBuffer ();
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
            connection.disconnect();// 断开连接
            
        } catch (Exception e) {
            logger.debug("调用第三方接口(get)失败!", e);
        }finally{
        	 try {
                 HttpSession session = null;
                if(RequestContextHolder.getRequestAttributes() != null){
                      session = (((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()).getSession();
                }
                 if(session != null){
                     SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
                     if(sessionInfo != null){
                             logger.info(" 用户："+sessionInfo.getMobilephone()+"     get请求："+GET_URL);
                             logger.info(" 用户："+sessionInfo.getMobilephone()+"     get返回："+sb);
                     }
                 }

				br.close();
				connection.disconnect();// 断开连接
			} catch (IOException e) {
				logger.debug("资源关闭(get)失败!", e);
			}
            
        }
        return sb.toString();
    }
    
    /**
     * 接口调用  POST
     */
    public static String httpURLConnectionPOST (String pospUrl,String content) {
    	String result = "";
    	BufferedReader bf  = null;
    	HttpURLConnection connection = null;
        try {
            URL url = new URL(pospUrl);
            
            // 将url 以 open方法返回的urlConnection  连接强转为HttpURLConnection连接  (标识一个url所引用的远程对象连接)
            connection = (HttpURLConnection) url.openConnection();// 此时cnnection只是为一个连接对象,待连接中
            // 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
            connection.setDoOutput(true);
            
            // 设置连接输入流为true
            connection.setDoInput(true);
            
            // 设置请求方式为post
            connection.setRequestMethod("POST");
            
            // post请求缓存设为false
            connection.setUseCaches(false);
            
            // 设置该HttpURLConnection实例是否自动执行重定向
            connection.setInstanceFollowRedirects(true);
            
            connection.setConnectTimeout(5000);//2分钟
            
    		connection.setReadTimeout(5000);
            // 设置请求头里面的各个属性 (以下为设置内容的类型,设置为经过urlEncoded编码过的from参数)
            // application/x-javascript text/xml->xml数据 application/x-javascript->json对象 application/x-www-form-urlencoded->表单数据
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            
    		connection.setRequestProperty("Content-type","application/x-www-form-urlencoded;charset=UTF-8");
            
            // 建立连接 (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            connection.connect();


            // 创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)
            DataOutputStream dataout = new DataOutputStream(connection.getOutputStream());
            // 将参数输出到连接
            //dataout.writeBytes(content);
            dataout.write(content.toString().getBytes("UTF-8"));
            // 输出完成后刷新并关闭流
            dataout.flush();
            dataout.close(); // 重要且易忽略步骤 (关闭流,切记!) 

            logger.info(connection.getResponseCode());
            
            // 连接发起请求,处理服务器响应  (从连接获取到输入流并包装为bufferedReader)
            bf = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line;
            StringBuffer  sb = new StringBuffer (); // 用来存储响应数据
            
            // 循环读取流,若不到结尾处
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
            bf.close();    // 重要且易忽略步骤 (关闭流,切记!) 
            connection.disconnect(); // 销毁连接
            result = sb.toString();
            
        } catch (Exception e) {
        	logger.debug("调用第三方接口(post)失败!", e);
        }finally{
       	 try {

       		 if(bf!=null){
       			bf.close();    // 重要且易忽略步骤 (关闭流,切记!) 
       		 }
       		 if(connection!=null){
       			 connection.disconnect(); // 销毁连接
       		 }
			} catch (IOException e) {
				logger.debug("资源关闭(post)失败!", e);
			}
     }
        return result;
    }



    public static String connectURL(String commString,String sendsmsaddress) {
        String rec_string = "";
        URL url = null;
        HttpURLConnection urlConn = null;
        try {
        	System.out.println(commString);
        	System.out.println(sendsmsaddress);
            url = new URL(sendsmsaddress);  //根据数据的发送地址构建URL
            urlConn = (HttpURLConnection) url.openConnection(); //打开链接
            urlConn.setConnectTimeout(30000); //链接超时设置为30秒
            urlConn.setReadTimeout(30000);	//读取超时设置30秒
            urlConn.setRequestMethod("POST");	//链接相应方式为post
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);

            OutputStream out = urlConn.getOutputStream();
            out.write(commString.getBytes("UTF-8"));
            out.flush();
            out.close();

            BufferedReader rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
            StringBuffer sb = new StringBuffer();
            int ch;
            while ((ch = rd.read()) > -1) {
                sb.append((char) ch);
            }

            rec_string = sb.toString().trim();
            rec_string = URLDecoder.decode(rec_string, "UTF-8");
            rd.close();
        } catch (Exception e) {
        	System.err.println(e);
            rec_string = "-107";
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }

        return rec_string;
    }
    public static  String sendPost(String url,Map<String, String> params){
    	return httpURLConnectionPOST(url,parseParams(params));
    }
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param)
    {
    	OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try
        {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream());
            // 发送请求参数
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        }
        catch (Exception e)
        {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return result;
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
    
    public static String post(String url, String request) {
   	 logger.info("HTTP请求" + ",url=" + url + ",request=" +
   	 request);

   	OutputStream oos = null;
   	InputStream iis = null;
   	String response = null;
   	try {
   	    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
   	    httpURLConnection.setRequestMethod("POST");
   	    httpURLConnection.setDoOutput(true);
   	    httpURLConnection.setDoInput(true);
   	    httpURLConnection.setAllowUserInteraction(true);

   	    oos = httpURLConnection.getOutputStream();
   	    oos.write(request.toString().getBytes("UTF-8"));
   	    oos.flush();

   	    iis = httpURLConnection.getInputStream();
   	    //response = IOUtil.readInputStream(iis, "UTF-8");
   	} catch (Exception e) {
   	    logger.info("HTTP发生异常"//
   		    + ",message=" + e.getLocalizedMessage());

   	    e.printStackTrace();
   	} finally {
   	    if (oos != null) {
   		try {
   		    oos.close();
   		} catch (IOException e) {
   		    e.printStackTrace();
   		}
   	    } // 关闭OutputStream[END]
   	    if (iis != null) {
   		try {
   		    iis.close();
   		} catch (IOException e) {
   		    e.printStackTrace();
   		}
   	    } // 关闭InputStream[END]
   	}
   	logger.info(("HTTP返回" + ",response=" + response));

   	return response;
       }
    public static String toJson3(Map<String,String> map){
    	Set<Map.Entry<String, String>> entrys = map.entrySet();
    	Map.Entry<String, String> entry = null;
    	String key = "";
    	String value = "";
    	StringBuffer jsonBuffer = new StringBuffer();
    	jsonBuffer.append("{");    
    	for(Iterator<Map.Entry<String, String>> it = entrys.iterator();it.hasNext();){
    		entry =  (Map.Entry<String, String>)it.next();
    		key = entry.getKey();
    		value = entry.getValue();
    		jsonBuffer.append("\""+key+"\":\""+value+"\"");
    		if(it.hasNext()){
    			jsonBuffer.append(",");
    		}
    	}
    	jsonBuffer.append("}");
    	return jsonBuffer.toString();
    }
    
}

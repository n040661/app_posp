package xdt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Http工具
 * 
 * @author Hsu。
 * @date 2013-10-16
 */
public class HttpUtil {

    public static synchronized String sendPost(String url,Map<String, String> params)
    {
    	OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try
        {
            URL realUrl = new URL(url);
            System.out.println("打开和URL之间的连接");
            System.out.println(url);
            URLConnection conn = realUrl.openConnection();
            System.out.println("设置通用的请求属性");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            System.out.println("发送POST请求必须设置如下两行");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            System.out.println("获取URLConnection对象对应的输出流");
            out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
            System.out.println("发送请求参数");
            String paramStr=parseParams(params);
            //System.out.println("paramStr:"+paramStr);
            out.write(url);
           // System.out.println(paramStr);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
            System.err.println("响应结果:"+result);
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
    public static String sendPost(String url)
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
            out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
            // 发送请求参数
           // String paramStr=parseParams(param5);
            System.out.println(url);
            out.write(url);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
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
    public static String sendPosts(String url)
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
            out = new OutputStreamWriter(conn.getOutputStream(),"GBK");
            // 发送请求参数
           // String paramStr=parseParams(param5);
            System.out.println(url);
            out.write(url);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"GBK"));
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



    public static String parseParams(Map<String, String> params)  {
        int i = 0;
        String param = "";
        if (params.containsKey("content")) {
            params.put("content", params.get("content"));
        }
        for (String key : params.keySet()) {
            
            if(params.get(key)!=null && params.get(key) !=""){
            	if (i > 0) {
                    param += "&";
                }
            	param += key + "=" + params.get(key);
            	i++;
            }
        }
        return param;
    }
    public static void main(String[] args) {
    	Map<String, String> params=new HashMap<String, String>();
    	params.put("a", "123");
    	params.put("c", "sdfa");
    	params.put("f", "sdfa");
    	params.put("g", "sdfa");
    	params.put("h", "sdfa");
    	System.out.println(parseParams(params));
	}
	  //转换成Json字符串
    public static String toJson(Map<String,String> map){
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
	        jsonBuffer.append(key+":"+value);
	        if(it.hasNext()){
	             jsonBuffer.append(",");
	        }
	    }
	    jsonBuffer.append("}");
	    return jsonBuffer.toString();
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

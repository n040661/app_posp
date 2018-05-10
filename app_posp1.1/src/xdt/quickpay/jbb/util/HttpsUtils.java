package xdt.quickpay.jbb.util;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpsUtils {

    public static InputStream doPost(String URL, String Parameter) throws IOException {      	
        URL url = new URL(URL);

        //URLConnection connection = url.openConnection();
        
        //HttpsURLConnection httpUrlConnection = (HttpsURLConnection) connection;
        
        //httpUrlConnection.setHostnameVerifier(new MyHostnameVerifier());

        //httpUrlConnection.setRequestMethod("POST");

        URLConnection httpUrlConnection = url.openConnection();


        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setUseCaches(false);
        httpUrlConnection.setRequestProperty("Content-Type", "application/xml");
        httpUrlConnection.setConnectTimeout(30000);


        OutputStream outStrm = httpUrlConnection.getOutputStream();
        if (Parameter!=null && !"".equals(Parameter)){
        	outStrm.write(Parameter.getBytes());
        }
        outStrm.flush();
        outStrm.close();
        
        InputStream inStrm = httpUrlConnection.getInputStream(); 
       
        
        return inStrm;
    }   
	
	public static void test() throws IOException{
		  // 创建URL对象
        URL myURL = new URL("https://www.sun.com");
 
        // 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
        HttpsURLConnection httpsConn = (HttpsURLConnection) myURL.openConnection();
 
        // 取得该连接的输入流，以读取响应内容
        InputStreamReader insr = new InputStreamReader(httpsConn.getInputStream());
 
        // 读取服务器的响应内容并显示
        int respInt = insr.read();
        while (respInt != -1) {
            System.out.print((char) respInt);
            respInt = insr.read();
        }
	}

    public static String convertStreamToString(InputStream is){   
    	StringBuilder sb = new StringBuilder();
    	try{   
        	BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));   
            sb = new StringBuilder();   
            String line = null;   
            while ((line = reader.readLine()) != null){   
                sb.append(line + "/n");   
            }   
	    }catch(IOException e) {   
	        e.printStackTrace();   
	    }finally{   
	        try {   
	            is.close();   
	        }catch(IOException e) {   
	            e.printStackTrace();   
	        }   
	    }   
	    return sb.toString();   
	}   
	
	public static void main(String[] args) throws IOException {
		test();
	}
	
}

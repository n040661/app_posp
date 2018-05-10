package xdt.quickpay.qianlong.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;



public class HttpClientHelper{
	
    public static final String GET = "GET";
    public static final String POST = "POST";
    
    public static String getNvPairs(List<String[]> list, String charSet){
        if(list==null || list.size()==0){
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0; i<list.size(); i++){
            String[] nvPairStr = list.get(i);
            try{
                if(i>0){
                    stringBuffer.append("&");
                }
                stringBuffer.append(URLEncoder.encode(nvPairStr[0], charSet)).append("=").append(URLEncoder.encode(nvPairStr[1], charSet));
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
                return null;
            }
        }
        return stringBuffer.toString();
    }
    
    public static HttpResponse doHttp(String urlStr, String method, String charSet, String postStr, String timeOut){
        if(method==null || (!GET.equalsIgnoreCase(method) && !POST.equalsIgnoreCase(method))){
            return null;
        }
        URL url = null;
        try{
            url = new URL(urlStr);
        }catch(MalformedURLException e){
            e.printStackTrace();
            return null;
        }
        if("https".equalsIgnoreCase(urlStr.substring(0, 5))){
            SSLContext sslContext = null;
            try
            {
                sslContext = SSLContext.getInstance("TLS");
                X509TrustManager xtmArray[] = {
                    new HttpX509TrustManager()
                };
                sslContext.init(null, xtmArray, new SecureRandom());
            }
            catch(GeneralSecurityException gse)
            {
                gse.printStackTrace();
            }
            if(sslContext != null)
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HttpHostnameVerifier());
        }
        HttpURLConnection httpURLConnection = null;
        try{
            httpURLConnection = (HttpURLConnection)url.openConnection ();
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        httpURLConnection.setConnectTimeout(Integer.parseInt(timeOut));
        httpURLConnection.setReadTimeout(Integer.parseInt(timeOut));
        try{
            httpURLConnection.setRequestMethod(method.toUpperCase());
        }catch(ProtocolException e){
            e.printStackTrace();
            return null;
        }
        if(POST.equalsIgnoreCase(method)){
            httpURLConnection.setDoOutput(true);  
            PrintWriter printWriter = null;
            try{
                printWriter = new PrintWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), charSet));
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
                return null;
            }catch(IOException e){
                e.printStackTrace();
                return null;
            }  
            printWriter.write(postStr);
            printWriter.flush();
        }
        InputStream inputStream = null;  
        try{
            inputStream = httpURLConnection.getInputStream();
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int data = 0;
        int statusCode = 0;
        try{
 			statusCode = httpURLConnection.getResponseCode();
            if(statusCode<HttpURLConnection.HTTP_OK || statusCode>=HttpURLConnection.HTTP_MULT_CHOICE){
            	 HttpResponse httpRsponse = new HttpResponse();
            	 httpRsponse.setStatusCode(statusCode);
                 return httpRsponse;
            }
            while((data=inputStream.read())!=-1){
                byteArrayOutputStream.write(data);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        byte[] returnBytes = byteArrayOutputStream.toByteArray();
        String returnStr = null;
        try{
            returnStr = new String(returnBytes, charSet);
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
        HttpResponse httpRsponse = new HttpResponse();
        httpRsponse.setStatusCode(statusCode);
        httpRsponse.setRspStr(returnStr);
        return httpRsponse;
    }
    
    public static String doHttp(String urlStr, String method, List<String[]> headers, String reqCharSet, String postStr, String timeOut, String rspCharSet){
        if(method==null || (!GET.equalsIgnoreCase(method) && !POST.equalsIgnoreCase(method))){
            return null;
        }
        URL url = null;
        try{
            url = new URL(urlStr);
        }catch(MalformedURLException e){
            e.printStackTrace();
            return null;
        }
        if("https".equalsIgnoreCase(urlStr.substring(0, 5))){
            SSLContext sslContext = null;
            try
            {
                sslContext = SSLContext.getInstance("TLS");
                X509TrustManager xtmArray[] = {
                    new HttpX509TrustManager()
                };
                sslContext.init(null, xtmArray, new SecureRandom());
            }
            catch(GeneralSecurityException gse)
            {
                gse.printStackTrace();
            }
            if(sslContext != null)
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HttpHostnameVerifier());
        }
        HttpURLConnection httpURLConnection = null;
        try{
            httpURLConnection = (HttpURLConnection)url.openConnection ();
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        System.setProperty("sun.net.client.defaultConnectTimeout", timeOut);
        System.setProperty("sun.net.client.defaultReadTimeout", timeOut);
        try{
            if(headers!=null && headers.size()>0){
                for(int i=0; i<headers.size(); i++){
                    String[] nvPairStr = headers.get(i);
                    if(nvPairStr.length==2){
                        httpURLConnection.setRequestProperty(nvPairStr[0], nvPairStr[1]);
                    }
                }
            }
            httpURLConnection.setRequestMethod(method.toUpperCase());
        }catch(ProtocolException e){
            e.printStackTrace();
            return null;
        }
        if(POST.equalsIgnoreCase(method)){
            
            httpURLConnection.setDoOutput(true);  
            PrintWriter printWriter = null;
            try{
                printWriter = new PrintWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), reqCharSet));
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
                return null;
            }catch(IOException e){
                e.printStackTrace();
                return null;
            }  
            printWriter.write(postStr);
            printWriter.flush();
        }
        InputStream inputStream = null;  
        try{
            inputStream = httpURLConnection.getInputStream();
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int data = 0;
        try{
            int statusCode = httpURLConnection.getResponseCode();
            if(statusCode<HttpURLConnection.HTTP_OK || statusCode>=HttpURLConnection.HTTP_MULT_CHOICE){
                return null;
            }
            while((data=inputStream.read())!=-1){
                byteArrayOutputStream.write(data);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        byte[] returnBytes = byteArrayOutputStream.toByteArray();
        String returnStr = null;
        try{
            returnStr = new String(returnBytes, rspCharSet);
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
        return returnStr;
    }
    
    public static HttpResponse doHttp(String urlStr, String method, List<String[]> headers, String charSet, String postStr, String timeOut){
        if(method==null || (!GET.equalsIgnoreCase(method) && !POST.equalsIgnoreCase(method))){
            return null;
        }
        URL url = null;
        try{
            url = new URL(urlStr);
        }catch(MalformedURLException e){
            e.printStackTrace();
            return null;
        }
        if("https".equalsIgnoreCase(urlStr.substring(0, 5))){
            SSLContext sslContext = null;
            try
            {
                sslContext = SSLContext.getInstance("TLS");
                X509TrustManager xtmArray[] = {
                    new HttpX509TrustManager()
                };
                sslContext.init(null, xtmArray, new SecureRandom());
            }
            catch(GeneralSecurityException gse)
            {
                gse.printStackTrace();
            }
            if(sslContext != null)
                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HttpHostnameVerifier());
        }
        HttpURLConnection httpURLConnection = null;
        try{
            httpURLConnection = (HttpURLConnection)url.openConnection ();
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        System.setProperty("sun.net.client.defaultConnectTimeout", timeOut);
        System.setProperty("sun.net.client.defaultReadTimeout", timeOut);
        try{
            if(headers!=null && headers.size()>0){
                for(int i=0; i<headers.size(); i++){
                    String[] nvPairStr = headers.get(i);
                    if(nvPairStr.length==2){
                        httpURLConnection.setRequestProperty(nvPairStr[0], nvPairStr[1]);
                    }
                }
            }
            httpURLConnection.setRequestMethod(method.toUpperCase());
        }catch(ProtocolException e){
            e.printStackTrace();
            return null;
        }
        if(POST.equalsIgnoreCase(method)){
            httpURLConnection.setDoOutput(true);  
            PrintWriter printWriter = null;
            try{
                printWriter = new PrintWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), charSet));
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
                return null;
            }catch(IOException e){
                e.printStackTrace();
                return null;
            }  
            printWriter.write(postStr);
            printWriter.flush();
        }
        InputStream inputStream = null;  
        try{
            inputStream = httpURLConnection.getInputStream();
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int data = 0;
        int statusCode = HttpURLConnection.HTTP_OK;
        try{
            statusCode = httpURLConnection.getResponseCode();
            if(statusCode<HttpURLConnection.HTTP_OK || statusCode>=HttpURLConnection.HTTP_MULT_CHOICE){
                HttpResponse httpRsp = new HttpResponse();
                httpRsp.setStatusCode(statusCode);
                return httpRsp;
            }
            while((data=inputStream.read())!=-1){
                byteArrayOutputStream.write(data);
            }
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        byte[] returnBytes = byteArrayOutputStream.toByteArray();
        String returnStr = null;
        try{
            returnStr = new String(returnBytes, charSet);
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
        HttpResponse httpRsp = new HttpResponse();
        httpRsp.setStatusCode(statusCode);
        httpRsp.setRspStr(returnStr);
        return httpRsp;
    }

}

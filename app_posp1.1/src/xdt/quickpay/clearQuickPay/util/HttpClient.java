package xdt.quickpay.clearQuickPay.util;
import javax.net.ssl.HttpsURLConnection;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


public class HttpClient {
    /**
     * 目标地址
     */
    private static URL url;

    /**
     * 通信连接超时时间
     */
    private static int connectionTimeout;

    /**
     * 通信读超时时�?
     */
    private static int readTimeOut;

    /**
     * 通信结果
     */
    private static String result;

    /**
     * 获取通信结果
     *
     * @return
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置通信结果
     *
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * 构�?函数
     *
     * @param url               目标地址
     * @param connectionTimeout HTTP连接超时时间
     * @param readTimeOut       HTTP读写超时时间
     */
    public HttpClient(String url, int connectionTimeout, int readTimeOut) {
        try {
            this.url = new URL(url);
            this.connectionTimeout = connectionTimeout;
            this.readTimeOut = readTimeOut;
        } catch (MalformedURLException e) {
        }
    }

    /**
     * 发�?信息到服务端
     *
     * @param data
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String send(Map<String, String> data, String encoding) throws Exception {
        try {
            HttpURLConnection httpURLConnection = createConnection(encoding);
            if (null == httpURLConnection) {
                throw new Exception("创建联接失败");
            }
            String sendData = getRequestParamString(data, encoding);
            requestServer(httpURLConnection, sendData,
                    encoding);
            System.out.println("请求报文:[" + data + "]");
            result = response(httpURLConnection, encoding);
            System.out.println("返回报文:[" + result + "]");
            return result;
        } catch (Exception e) {
            throw e;
        }
    }



    /**
     * HTTP Post发�?消息
     *
     * @param connection
     * @param message
     * @throws IOException
     */
    private static void requestServer(final URLConnection connection, String message, String encoder)
            throws Exception {
        PrintStream out = null;
        try {
            connection.connect();
            out = new PrintStream(connection.getOutputStream(), false, encoder);
            out.print(message);
            out.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }

    /**
     * 显示Response消息
     *
     * @param connection
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private static String response(final HttpURLConnection connection, String encoding)
            throws Exception {
        InputStream in = null;
        StringBuilder sb = new StringBuilder(1024);
        BufferedReader br = null;
        try {
            if (200 == connection.getResponseCode()) {
                in = connection.getInputStream();
                sb.append(new String(read(in), encoding));
            } else {
                in = connection.getErrorStream();
                sb.append(new String(read(in), encoding));
            }
            return sb.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != br) {
                br.close();
            }
            if (null != in) {
                in.close();
            }
            if (null != connection) {
                connection.disconnect();
            }
        }
    }

    public static byte[] read(InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        int length = 0;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        while ((length = in.read(buf, 0, buf.length)) > 0) {
            bout.write(buf, 0, length);
        }
        bout.flush();
        return bout.toByteArray();
    }

    /**
     * 创建连接
     *
     * @return
     * @throws ProtocolException
     */
    private static HttpURLConnection createConnection(String encoding) throws ProtocolException {
        HttpURLConnection httpURLConnection = null;
        try {
            if ("https".equalsIgnoreCase(url.getProtocol())) {
                String urlStr = url.toString();
                url = new URL(null, urlStr, new sun.net.www.protocol.https.Handler());//https请求用sun.net.www.protocol.https.Handler
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } else {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            }
        } catch (IOException e) {
            return null;
        }
        httpURLConnection.setConnectTimeout(connectionTimeout);// 连接超时时间
        httpURLConnection.setReadTimeout(readTimeOut);// 读取结果超时时间
        httpURLConnection.setDoInput(true); // 可读
        httpURLConnection.setDoOutput(true); // 可写
        httpURLConnection.setUseCaches(false);// 取消缓存
        httpURLConnection.setRequestProperty("Content-type",
                "application/x-www-form-urlencoded;charset=" + encoding);
        httpURLConnection.setRequestMethod("POST");
        if ("https".equalsIgnoreCase(url.getProtocol())) {
            HttpsURLConnection husn = (HttpsURLConnection) httpURLConnection;
            husn.setSSLSocketFactory(new BaseHttpSSLSocketFactory());
            husn.setHostnameVerifier(new BaseHttpSSLSocketFactory.TrustAnyHostnameVerifier());//解决由于服务器证书问题导致HTTPS无法访问的情�?
            return husn;
        }
        return httpURLConnection;
    }


    /**
     * 将Map存储的对象，转换为key=value&key=value的字�?
     *
     * @param requestParam
     * @param coder
     * @return
     */
    private static String getRequestParamString(Map<String, String> requestParam, String coder) {
        if (null == coder || "".equals(coder)) {
            coder = "UTF-8";
        }
        StringBuffer sf = new StringBuffer("");
        String reqstr = "";
        if (null != requestParam && 0 != requestParam.size()) {
            for (Entry<String, String> en : requestParam.entrySet()) {
                try {
                    sf.append(en.getKey()
                            + "="
                            + (null == en.getValue() || "".equals(en.getValue()) ? "" : URLEncoder
                            .encode(en.getValue(), coder)) + "&");
                } catch (UnsupportedEncodingException e) {
                    return "";
                }
            }
            reqstr = sf.substring(0, sf.length() - 1);
        }
        return reqstr;
    }
    
    
    
    public static String getStringDate() {
	     Date currentTime = new Date();
	     SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     String dateString = formatter.format(currentTime);
	     String year = dateString.substring(0, 4);
	     String month = dateString.substring(5, 7);
	     String day = dateString.substring(8, 10);
	     String hour = dateString.substring(11, 13);
	     String min = dateString.substring(14, 16);
	     String sec = dateString.substring(17);
	     String date = year+month+day+hour+min+sec;
	     return date;
    }
    
    public static void main(String[] args) throws Exception{
    	Map<String,String> mapData = new HashMap<String,String>();
    	String encode = "utf-8";
    	String url = "https://cashier.etonepay.com/NetPay/SynonymNamePay.action";
    	HttpClient client = new HttpClient(url, 60000, 60000);
    	String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 14) + "_CS";
    	System.out.println("merOrderNum:"+uuid);
    	String dateString = getStringDate();
    	mapData.put("version", "1.0.0");
    	mapData.put("transCode", "8888");
    	mapData.put("merchantId", "888201711310120");
    	mapData.put("merOrderNum", uuid);
    	mapData.put("bussId", "ONL0017");
    	mapData.put("tranAmt", "52846");
    	mapData.put("sysTraceNum", dateString);
    	mapData.put("tranDateTime", dateString);
    	mapData.put("currencyType", "156");
    	mapData.put("merURL", "http://192.168.101.27:8080/NetPay/test/RcvTxn.jsp");
    	mapData.put("backURL", "http://192.168.101.27:8080/NetPay/test/RcvTxn.jsp");
    	mapData.put("orderInfo", "");
    	mapData.put("userId", "");
    	mapData.put("userNameHF", "e5b09ae5bbb6e8b685");//请用真实姓名16进制转换
    	mapData.put("quickPayCertNo", "410324199203231912");//请用真实的身份证号
    	mapData.put("arrviedAcctNo", "6228480028542136370");//请用真实的入账卡号
    	mapData.put("arrviedPhone", "18902195076");//入账卡手机号
    	mapData.put("arrviedBankName", "农业银行");//开户行
    	mapData.put("userPhoneHF", "18902195076");//请用真实的交易卡手机号
    	mapData.put("userAcctNo", "6258081698129372");//请用真实的交易卡卡号
    	mapData.put("cardCvn2", "269");
    	mapData.put("cardExpire", "1122");
    	mapData.put("userIp", "");
    	mapData.put("bankId", "888880170122900");
    	mapData.put("stlmId", "");
    	mapData.put("entryType", "1");
    	mapData.put("attach", "");
    	mapData.put("reserver1", "257");
    	mapData.put("reserver2", "");
    	mapData.put("reserver3", "");
    	mapData.put("reserver4", "7");
    	String datakey = "TLM3O9zGu69lP411";
    	String txnString = mapData.get("version") + "|" + mapData.get("transCode") + "|" + mapData.get("merchantId") + "|" + mapData.get("merOrderNum") + "|" + mapData.get("bussId")+ "|" + mapData.get("tranAmt")+ "|" + mapData.get("sysTraceNum")+ "|" + mapData.get("tranDateTime")+ "|" + mapData.get("currencyType")+ "|" + mapData.get("merURL")+ "|" + mapData.get("backURL")+ "|" + mapData.get("orderInfo")+ "|" + mapData.get("userId");
    	String signVal = MD5.getInstance().getMD5ofStr(txnString + datakey);
    	System.out.println("txnString:"+ txnString);
    	System.out.println("signValue:"+signVal);
    	mapData.put("signValue", signVal);
    	client.send(mapData, encode);
//    	Map<String,String> mapData = new HashMap<String,String>();
//    	String encode = "utf-8";
//    	String url = "https://cashier.etonepay.com/NetPay/MerOrderQuery.action";
//    	String dateString = getStringDate();
//    	mapData.put("merchantId", "888201711310120");
//    	mapData.put("merOrderNum", "QP20180615163707424304");
//    	mapData.put("tranDate", dateString);
//    	String datakey = "TLM3O9zGu69lP411";
//    	String txnString = mapData.get("merchantId") + "|" + mapData.get("merOrderNum") + "|" + mapData.get("tranDate");
//    	String signVal = MD5.getInstance().getMD5ofStr(txnString + datakey);
//    	System.out.println("txnString:"+ txnString);
//    	System.out.println("signValue:"+signVal);
//    	mapData.put("signValue", signVal);
//    	HttpClient client = new HttpClient(url, 10000, 10000);
//    	client.send(mapData, encode);
    	
    }
    
}

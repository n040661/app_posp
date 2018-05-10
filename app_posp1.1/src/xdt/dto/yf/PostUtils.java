package xdt.dto.yf;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
/**
 * post请求辅助类
 * @PostUtil.java
 * @author WANG AN ZHEN
 * @2016年10月25日 下午7:21:51  www.yufusoft.com Inc. All rights reserved.
 */
public class PostUtils {
    /**
     * 更是化参数
     * @param map
     * @return
     */
    public static String getParameter(Map<String, String> map ) {
        StringBuilder sb = new StringBuilder();
        //Map<String, String> map = BeanUtil.objectToMap(obj);
        map.remove("class");
        for (String key : map.keySet()) {
            sb.append(key + "=" + map.get(key) + "&");
        }
        return sb.toString();
    }
    /**
     * 
     * @param urladdr
     * @param map
     * @return
     * @throws IOException
     */
    public static String doPost(String urladdr, Map<String, String> map) throws IOException {
        HttpURLConnection urlconn = null;
        try {
            trustAllHttpsCertificates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);

        URL url = new URL(urladdr);
        urlconn = (HttpURLConnection) url.openConnection();
        urlconn.setDoInput(true);
        urlconn.setDoOutput(true);
        urlconn.setRequestMethod("POST");
        urlconn.setUseCaches(false);
        urlconn.setInstanceFollowRedirects(false);
        urlconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlconn.connect();
        DataOutputStream out = new DataOutputStream(urlconn.getOutputStream());
        out.writeBytes(getParameter(map));
        out.flush();
        out.close();
        InputStream is = urlconn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String result = "";
        String readLine = null;
        while ((readLine = br.readLine()) != null) {
            result = result + readLine;
        }
        is.close();
        br.close();
        urlconn.disconnect();
        return result;
    }

    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }

}

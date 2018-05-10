package xdt.util.client;
import java.io.BufferedInputStream;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import java.net.HttpURLConnection;
//import org.apache.xerces.impl.dv.util.Base64;
import java.util.LinkedList;
import java.util.ListIterator;


/**
*
* @author: XieminQuan
* @time  : 2007-11-20 下午04:10:22
*
* DNAPAY
*/

public class SslConnection {

    public static void main(String[] args) throws Exception {
        System.out.println("alipay:" + new SslConnection().connect("https://ylgw.alipay.com/home/bank_ack.htm?ENCODING=GBK"));
        System.out.println("dnapay:" + new SslConnection().connect("http://www.dna-pay.com/services/OrderServerWS?wsdl"));
    }

     public HttpURLConnection openConnection(String strUrl) throws Exception {

        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);

        URL url = new URL(strUrl);
        return (HttpURLConnection) url.openConnection();
    }

    public String connect(String strUrl) throws Exception {

        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);

        URL url = new URL(strUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
       
        byte[] bts = new byte[100];
        urlConn.getInputStream().read(bts);
        String result = new String(bts).trim();
        return result;

    }
    HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session) {
            System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
            return true;
        }
    };

    private static void trustAllHttpsCertificates() throws Exception {

        //  Create a trust manager that does not validate certificate chains:

        javax.net.ssl.TrustManager[] trustAllCerts =
                new javax.net.ssl.TrustManager[1];

        javax.net.ssl.TrustManager tm = new miTM();

        trustAllCerts[0] = tm;

        javax.net.ssl.SSLContext sc =
                javax.net.ssl.SSLContext.getInstance("SSL");

        sc.init(null, trustAllCerts, null);

        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(
                sc.getSocketFactory());

    }

    public static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) throws
                java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) throws
                java.security.cert.CertificateException {
            return;
        }
    }
    
	public static String read(HttpURLConnection connect) throws Exception {

		BufferedInputStream in = new BufferedInputStream(connect.getInputStream());

		return SslConnection.read(in);
	}
    
	public static String read(BufferedInputStream in) throws Exception {

		LinkedList<Httpbuf> bufList = new LinkedList<Httpbuf>();
		int size = 0;
		byte buf[];
		
		do {
			buf = new byte[128];
			int num = in.read(buf);
			if (num == -1)
				break;
			size += num;
			bufList.add(new Httpbuf(buf, num));
		} while (true);
		
		buf = new byte[size];
		int pos = 0;
		for (ListIterator<Httpbuf> p = bufList.listIterator(); p.hasNext();) {
			
			Httpbuf b = p.next();
			for (int i = 0; i < b.size;) {
				buf[pos] = b.buf[i];
				i++;
				pos++;
			}

		}

		return new String(buf,"UTF-8");
	}

}

class Httpbuf
{

	public byte buf[];
	public int size;

	public Httpbuf(byte b[], int s)
	{
		buf = b;
		size = s;
	}
}
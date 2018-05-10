package xdt.quickpay.qianlong.util;

import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class HttpX509TrustManager implements X509TrustManager{

	public HttpX509TrustManager(){

	}

	public void checkClientTrusted(X509Certificate ax509certificate[], String s){

	}

	public void checkServerTrusted(X509Certificate ax509certificate[], String s){

	}

	public X509Certificate[] getAcceptedIssuers(){
		return null;
	}
}

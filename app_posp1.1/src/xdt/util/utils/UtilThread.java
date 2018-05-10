package xdt.util.utils;

import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;

public class UtilThread extends Thread{

	
	private String  param;
	
	private String url;
	

	public UtilThread(String url, String param) {
		super();
		this.param = param;
		this.url = url;
	}


	@Override
	public void run() {
		try {
			sleep(2000);
			for (int i = 0; i < 10; i++) {
				String path ="";
				if (url.indexOf("?") != -1) {
					path = url.replaceAll(",", "&") + "&" + param;
				} else {
					path = url + "?" + param;
				}
				//String result1=RequestUtils.doPost(url, param, "UTF-8");
				//String result1=HttpUtil.sendPost(path.replace(" ", "+"));
				// String result1=HttpUtil.sendPost(path.replace(" ", ""));
				String result1=HttpURLConection.httpURLConnectionPOST(url, param);
				if("SUCCESS".equals(result1)){
					break;
				}
				sleep(5000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

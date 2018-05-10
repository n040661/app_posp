package xdt.dto.quickPay.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;

public class MbUtilThread extends Thread{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private String  param;
	
	private String url;
	

	public MbUtilThread(String url, String param) {
		super();
		this.param = param;
		this.url = url;
	}


	@Override
	public void run() {
		try {
			sleep(2000);
			for (int i = 0; i < 10; i++) {
				
				String result = HttpClientUtil.post(url,param);
				logger.info("进入线程后下游第"+i+"次返回状态" + result);
				JSONObject ob = JSONObject.fromObject(result);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						logger.info("进入线程后解析下游返回的结果:" + "\t" + value);
						map.put("success", value);
						break;
					}
				}
				sleep(5000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

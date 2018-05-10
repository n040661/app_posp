package xdt.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import xdt.dto.jsds.JsdsResponseDto;
import xdt.model.PmsAppTransInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.qianlong.model.PayResponseEntity;
import xdt.util.HttpURLConection;

public class JsPostThread extends Thread {
	
	public static final Logger logger=Logger.getLogger(JsPostThread .class);
	
	private String url;
	
	private JsdsResponseDto entity;

	public JsPostThread (String url, JsdsResponseDto entity) {
		this.url = url;
		this.entity = entity;
	}
	@Override
	public synchronized void run() {
		
		//线程处理
		//1、先查询本地库订单是否是完成状态
		//2、如果是完成状态跳过第三方查询并结束；   否则进行第三方查询     分隔时间进行查询在进行下一步处理 
		//3、第三方查询结果后根据结果处理本地订单并结束
		
			
		try {
			    Thread.sleep(2000);
				for(int i=0;i<1000;i++){
					logger.info("进入线程中");
					Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
					// 给下游主动返回支付结果
					logger.info("开始进行异步数据通知");
					String path = url + "?" + bean2Util.bean2QueryStr(entity);
					logger.info("线程中需要发送的数据：" + path);
					String json = HttpURLConection.sendPost(url, bean2Util.bean2QueryStr(entity));
					JSONObject ob1 = JSONObject.fromObject(json);
					Iterator it1 = ob1.keys();
					Map<String, String> map = new HashMap<>();
					while (it1.hasNext()) {
						String key1 = (String) it1.next();
						if (key1.equals("success")) {
							String value = ob1.getString(key1);
							logger.info("异步回馈的结果:" + "\t" + value);
							map.put("success", value);
						}
					}
					if (map.get("success").equals("true")) {
						break;
					}
					Thread.sleep(5000);
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		

	}
	
	

}


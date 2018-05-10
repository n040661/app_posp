package xdt.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import xdt.util.HttpURLConection;
import xdt.util.PlatBase64Utils;
import xdt.util.PlatKeyGenerator;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

import com.alibaba.fastjson.JSON;

/*
 * 钱龙快捷
 */
public class QianLongServiceImpl {
	// 配置信息
	private final String appId = "ff8080815b281d2e015b2822b6b3001b";
	private final String appCode = "ff8080815b281d2e015b2822b6b3001c";
	//公钥
	private final String pub_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbEpVHqY2fpRAwWROkGZlvhgnaU8FIniRNzfOFwbvNqTkmZ164QbYHFZcwdsRsXkABvzyhf2XOaRrANQuLoGg8FFShM/tyl3R/gPgGBZykf6dXdGmp9OkVFkVyHQ/uxmjIb4nkvXSxx+onfjr5SZyup3Us4wgB90acLbaUsGD+BQIDAQAB/tyl3R/gPgGBZykf6dXdGmp9OkVFkVyHQ/uxmjIb4nkvXSxx+onfjr5SZyup3Us4wgB90acLbaUsGD+BQIDAQAB";
	//银行卡
	private final String card_no = "6217003760027926781";
	//手机号
	private final String phone = "13206027068";
	/**
	 * 前端开通
	 */
	@Test
	public void testOpenCard() {
		Map<String,String> params=new HashMap<String, String>();
		params.put("appId", appId);
		
		Map<String,String> data=new HashMap<String, String>();
		data.put("appId", appId);
		data.put("appCode", appCode);
		data.put("orderId", UtilDate.getOrderNum());
		data.put("txnTime", UtilDate.getOrderNum());
		data.put("accNo", card_no);
		data.put("backUrl", "http://test.rytpay.com.cn/RytPayTest/servlet/Form4GatewayConsumeFrontCallback");
		
		//编码内容
		String dataStr=encode(JSON.toJSONString(data));
		params.put("data", dataStr);
		//发送请求
		System.out.println("请求报文:"+params);
		String respStr=HttpURLConection.sendPost("http://unionpay.rytpay.com.cn/rytpay/unionpay/wtz/token.do?api/v1/open/card/front/service", params);
		//响应信息
		System.out.println(respStr);
		
		
	}

	/**
	 * 消费短信
	 */
	@Test
	public void testConsumeSms() {
		Map<String,String> params=new HashMap<String, String>();
		params.put("appId", appId);
		
		Map<String,String> data=new HashMap<String, String>();
		data.put("appId", appId);
		data.put("appCode", appCode);
		data.put("orderId", UtilMethod.getOrderid("185"));
		data.put("txnTime", UtilDate.getOrderNum());
		data.put("txnAmt", "1");// 支付金额，单位为分

		data.put("token", "");
		data.put("phoneNo", phone);
		
		//编码内容
		String dataStr=encode(JSON.toJSONString(data));
		params.put("data", dataStr);
		//发送请求
		System.out.println("请求报文:"+params);
		String respStr=HttpURLConection.sendPost("http://unionpay.rytpay.com.cn/rytpay/unionpay/wtz/token.do?api/v1/consume/sms/service", params);
		//响应信息
		System.out.println(respStr);
	}

	/**
	 * 消费
	 */
	@Test
	public void testConsume() {
		Map<String,String> params=new HashMap<String, String>();
		params.put("appId", appId);
		
		Map<String,String> data=new HashMap<String, String>();
		data.put("appId", appId);
		data.put("appCode", appCode);
		data.put("orderId", UtilMethod.getOrderid("185"));
		data.put("txnAmt", "1");// 支付金额，单位为分
		data.put("txnTime", UtilDate.getOrderNum());
		data.put("token", card_no);
		data.put("smsCod", "1231");//短信验证码
		data.put("backUrl", "http://192.168.1.1/backUrl");
		
		//编码内容
		String dataStr=encode(JSON.toJSONString(data));
		params.put("data", dataStr);
		//发送请求
		

		System.out.println("请求报文:"+params);
		String respStr=HttpURLConection.sendPost("http://unionpay.rytpay.com.cn/rytpay/unionpay/wtz/token.do?api/v1/consume/service", params);
		//响应信息
		System.out.println(respStr);
	}

	/**
	 * 查询
	 */
	@Test
	public void testQuery() {
		Map<String,String> params=new HashMap<String, String>();
		params.put("appId", appId);
		
		Map<String,String> data=new HashMap<String, String>();
		data.put("appId", appId);
		data.put("appCode", appCode);
		data.put("orderId", UtilMethod.getOrderid("185"));
		data.put("txnTime", UtilDate.getOrderNum());
		
		//编码内容
		String dataStr=encode(JSON.toJSONString(data));
		params.put("data", dataStr);
		//发送请求
		System.out.println("请求报文:"+params);
		
		String respStr=HttpURLConection.sendPost("http://unionpay.rytpay.com.cn/rytpay/unionpay/wtz/token.do?api/v1/order/query/service", params);
		//响应信息
		System.out.println(respStr);
	}

	private String encode(String jsonString) {
		byte[] encodeData;
		String data = "";
		try {
			encodeData = PlatKeyGenerator.encryptByPublicKey(
					jsonString.getBytes("UTF-8"), pub_key);
			data = PlatBase64Utils.encode(encodeData);
			System.out.println(data);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
}

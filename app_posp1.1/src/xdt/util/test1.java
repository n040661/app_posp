package xdt.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import xdt.model.ChannleMerchantConfigKey;
import xdt.quickpay.hengfeng.util.HttpClientUtil;

public class test1 {
	
	
	/*public static int stringTest(byte[] a,int num)
	{
		int number=0;
		
		boolean hanzi=false;
		for (int i = 0; i < num; i++) {
			
			if(a[i]<0&&!hanzi)
			{
				hanzi=true;
			}else{
				number++;
				hanzi=false;
			}
		}
		return number;
	}
	*/
	public static void main(String[] args) throws Exception {
		
//         String str="我adsqw中撒气无";
//         String strName="我ABC汗";
//         
//         int n=stringTest(str.getBytes("GBK"),5);
//        System.out.println(str.substring(0, n));
		
//		String aa="http://localhost:8102/app_posp/pay/bns/wechat/webpay/webpay_result.jsp";
//		
//		System.out.println(aa.length());
		
//		String catent="{\"attributes\":{\"respCode\":\"00\",\"origRespMsg\":\"%e6%88%90%e5%8a%9f%5b0000000%5d\",\"origRespCode\":\"00\",\"txnSubType\":\"01\",\"txnAmt\":\"1000\",\"version\":\"5.1.0\",\"signMethod\":\"01\",\"settleAmt\":\"1000\",\"encoding\":\"UTF-8\",\"traceTime\":\"0611093707\",\"respMsg\":\"%e6%88%90%e5%8a%9f%5b0000000%5d\",\"encryptCertId\":\"-52137265817034091409003080250704381423\",\"queryId\":\"201706110937071302058\",\"orderId\":\"1706110937070850686\",\"signature\":\"QFY6L%2booAYimXPvTUmUtrxltQPOldDOToKy0uQ8QFzSCXEIj9MjdD%2fAEcJFBIvBI%2bFqSOnKYNpTwkuuSi4P4hF%2bN3HAmID7HQzKoNO0ILxwXonFG8BOKrxf0bzhpfV6MAfTdlVOVHye9tOhJhZWWotIZ0tCRCwlxpI%2f85xMBde0%3d\",\"txnType\":\"01\",\"currencyCode\":\"156\",\"merId\":\"170425132706014\",\"settleDate\":\"0611\",\"certId\":\"8741447889853773652031021739353848686\",\"settleCurrencyCode\":\"156\",\"bizType\":\"000902\",\"reqReserved\":\"170425132706014\",\"traceNo\":\"130205\",\"issuerIdentifyMode\":\"0\",\"accessType\":\"0\",\"txnTime\":\"20170611093707\"},\"success\":true,\"msg\":\"操作成功\"}";
//		JSONObject ob1 = JSONObject.fromObject(catent);
//		Iterator it1 = ob1.keys();
//		Map<String, String> map = new HashMap<>();
//		while (it1.hasNext()) {
//			String key1 = (String) it1.next();
//			if (key1.equals("attributes")) {
//				String value = ob1.getString(key1);
//				System.out.println("异步回馈的结果:" + "\t" + value);
//				JSONObject ob = JSONObject.fromObject(value);
//				Iterator it = ob.keys();
//				Map<String, String> param = new HashMap<>();
//				while (it.hasNext()) {
//					String key = (String) it.next();
//					if (key.equals("origRespCode")) {
//						String value1 = ob.getString(key);
//						System.out.println("异步回馈的结果1:" + "\t" + value1);
//				//map.put("success", value);
//			}
//				}
//			}
//		}
		
//		String url="http://116.62.147.249:7001/callback.do?sp=PINGTAI_QIANBAO&im=64&callback_type=callback_trade";
//		String content="";
//		String result=HttpClientUtil.post(url, content);
		//"sign":"bba2dce5ab6d9caf55382841772798d3","respCode":"0000","pl_orderNumber":"1881502334920265","payStatus":"2","orderNum":"1502334232820","respMsg":"下单成功，等待支付"
		/*Map<String, String> result1 = new HashMap<String, String>();*/
//		result1.put("respCode", "0000");
//		result1.put("pl_url", "weixin://wxpay/bizpayurl?pr=LNmGWYo");
//		result1.put("pl_datetime", "20170810114040");
//		result1.put("pl_message", "交易成功");
//		result1.put("orderNum", "1502336427566");
		/*result1.put("orderNum", "PAY_170918113553232387710");
		result1.put("pl_orderNum", "252017091811354952374525");
		result1.put("pl_payState", "4");
		result1.put("pl_payMessage", "支付成功");
		result1.put("pl_amount", "10.00");
		String sign=JsdsUtil.sign(result1,"9d446e970fb84b9bbb5c5806758edef6");
		System.out.println("签名:"+sign);
		String url="http://112.74.136.64:9090/api/changJiePayResult.action";
		String content="serialVersionUID=1&orderNum=PAY_170918113553232387710&pl_orderNum=252017091811354952374525&pl_payState=4&pl_payMessage=支付成功&pl_bankCardType=&pl_amount=10.00&sign="+sign;
		System.out.println(content);
		String result=HttpClientUtil.post(url, content);
		System.out.println(result);*/
		
//		String url="http://60.28.24.164:8101/app_posp/payeasy/queryPayResult.action";
//		
//		 Map<String, String> params = new HashMap<String, String>();
//			
//		 params.put("merchantId", "10061045043");
//		 params.put("v_oid", "20170709-13240-cz2017070921350551");
//		 String param=HttpUtil.toJson3(params);
//		 PmsWeixinMerchartInfo entity = gson.fromJson(param, PmsWeixinMerchartInfo.class);
//		 Map map = BeanToMapUtil.convertBean(entity);
//		 String sign = PuFaSignUtil.sign(map);
//		 System.out.println("签名:"+sign);
//		 params.put("sign", sign);
//		 String url="http://60.28.24.164:8102/app_posp/ql/register.action";
//		
//		 String json=HttpUtil.toJson3(params);
//		 System.out.println("上送的参数:"+json);
//		 Map<String, String> result = new HashMap<String, String>();
//		 System.out.println(json);
//		 result.put("requestData", json);
//		
//		 String recode=HttpUtil.sendPost(url,result);
//		
//		 System.out.println("返回的数据:"+recode);
//		
//		String result=HttpURLConection.httpURLConnectionPOST(url,content);
//		
//		System.out.println(result);
		
//		String url="http://wapi.yikunpay.com/static/app/quickment_success.html";
		
//	 String aa="{token=6235240000280745015&trId=170428110913015&tokenLevel=40&tokenBegin=20170719155426&tokenEnd=20220718155426&tokenType=01}";
//		List<String> ls = new ArrayList<String>();
//		Pattern pattern = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
//		Matcher matcher = pattern.matcher(aa);
//		while (matcher.find()) {
//			ls.add(matcher.group());
//		}
//		for (String string : ls) {
//			System.out.println("解析后的token字符串:" + string);
//			String[] name = string.split("\\&");
//			String[] list = name[5].split("\\=");
//			//logger.info("解析后的token名称:" + list[0].toString());
//			//logger.info("解析后的token值:" + list[1].toString());
//			//param.put("token", list[1].toString());
//			System.out.println(list[1].toString());
//		}
		
		/*String url="ALIPAY_SERVICEWINDOW|C148482140920610354|ORDER20170907162619645|1000|测试商户|测试产品||||192.168.0.116|http://60.28.24.164:8102/app_posp/wechat/bgPayResult.action||bns|5B039EBC02E5FEE23A4B174092914880";
		
		String[] list=url.split("\\|");
		
		String notify_url=list[0];
		String service_type=list[1];
		String mch_id=list[2];
		String out_trade_no=list[3];
		String total_fee=list[4];
		String subject=list[5];
		String body=list[6];
		String time_start=list[7];
		String time_expire=list[8];
		String device_info=list[9];
		String spbill_create_ip=list[10];
		String callback_url=list[11];
		String nonce_str=list[12];
		String sign=list[13];
		
		System.out.println(notify_url);*/
		
//		String aa="adfsfdsfjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjoqweuuoygeewougqewquiwhibdfcd";
//		
//		String bb=URLEncoder.encode(aa);
//		System.out.println("加密之后:"+bb);
		
//		String url="http://zgy.tunnel.qydev.com/qiaomeiqianbao/CjWxZfbCallBackNew.action";
//		String content="account=18919751920&amount=1000&fee=4&orderDt=20170818&orderNo=15030349737030947549&orgId=000073&orgOrderNo=1708181342496851001&paySt=2&respCode=200&subject=hdx";
//		
//	
		//http://112.74.136.64:9090/api/changJieNewPayResult.action
		///merchantId=10034299001&merchantOrderNo=PAY_171012114203134712621&payAmount=10.00&respCode=00&respMsg=支付成功&sign=3dd2b21b03382b2f0b5154c81741b39d
		String path="http://119.23.246.110:9010/mps/changjie/notify";//https://119.23.246.110:9001/mps/changjie/notify
		String str ="merchantId=10034299001&merchantOrderNo=PAY_171012114203134712621&payAmount=10.00&respCode=00&respMsg=支付成功&sign=3dd2b21b03382b2f0b5154c81741b39d";//merchantId=10034128984&merchantOrderNo=201710121152912117&payAmount=29.70&respCode=00&respMsg=支付成功&sign=bc7e0df5014fe93dec7471d755ded5f4
		//String s =HttpUtil.sendPost(path+"?"+str);
		String s =HttpURLConection.httpURLConnectionPOST(path, str);
		System.out.println("异步返回数据："+s);
		
	}

	
}

package xdt.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;




import xdt.dto.nbs.webpay.WechatWebPayResponse;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.service.HfQuickPayService;
import xdt.service.IClientService;
import xdt.tools.Client;
import xdt.tools.Constants;
import xdt.tools.Tools;
import xdt.tools.Xml;


@Controller
@RequestMapping("/clientController")
public class ClientController  extends BaseAction {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private HfQuickPayService payService;
	@Resource 
	private  IClientService clientServiceImpl;
	/**
	 * 商户下订单接口测试
	 * @throws Exception 
	 */
	@RequestMapping(value="merchantOrderTest")
	public void MerchantOrderTest(Client client,HttpServletResponse response) throws Exception {
		// 设置参数
		//必填
		log.info("下游上传参数{}"+gson.toJson(client));
		String url =client.getNotifyUrl();
		log.info("下游上传异步url"+client.getNotifyUrl());
		client.setUrl(url);
		//client.setMerchantId1("100120242118015");
		PmsBusinessPos busInfo=clientServiceImpl.selectKey(client.getMerchantId1());
		//client.setMerchantId(Constants.MERCHANT_ID);
		client.setMerchantId(busInfo.getBusinessnum());
		
		String amount = client.getAmount().replaceAll(",", "");//client.getAmount();
		log.info("下游上传的金额"+amount);
		client.setAmount(amount);
		//client.setOrderDesc("通用商户充值");//通用商户充值
		//String orderDesc ="通用商户充值";
		//必填
		//client.setExtData("");//充值测试
		//以下扩展参数是按互联网金融行业填写的；其他行业请参考接口文件说明进行填写
		//client.setMiscData(""); "13922897656|0||张三|440121197511140912|62220040001154868428||PAYECO201510285445||2|";  //互联网金融client.getMiscData();
		if(client.getMiscData()==null||client.getMiscData().equals("")){
			client.setMiscData("");
		}
		String ss=""+System.currentTimeMillis();
		log.info("订单号："+ss);
		//client.setMerchOrderId(ss);//String merchOrderId = "" + System.currentTimeMillis(); // 订单号
		client.setMerchantId(busInfo.getBusinessnum());//String merchantId = Constants.MERCHANT_ID;
		client.setNotifyUrl(Constants.MERCHANT_NOTIFY_URL);//String notifyUrl = Constants.MERCHANT_NOTIFY_URL; // 封装的API会自动做URLEncode
		client.setTradeTime(Tools.getSysTime());//String tradeTime = Tools.getSysTime();
		client.setExpTime("");//String expTime = ""; // 采用系统默认的订单有效时间
		client.setNotifyFlag("");//String notifyFlag = "0";
		//System.out.println(busInfo.getKek());
		client.setPriKey(busInfo.getKek());
		//client.setPriKey(Constants.MERCHANT_RSA_PRIVATE_KEY);
		client.setPubKey(Constants.PAYECO_RSA_PUBLIC_KEY);
		client.setPayecoUrl(Constants.PAYECO_URL);
		// 调用下单接口
		Xml retXml = new Xml();
		String retMsgJson = "";
		boolean bOK = true;
		System.out.println("-------订单下单接口测试-------------------------");
		try {
			
			//接口参数请参考TransactionClient的参数说明
			String ret = clientServiceImpl.ReceiveInformation( client, retXml);
			if(!"0000".equals(ret)){
				System.out.println("商户下单接口测试失败！：retCode="+ret+"; msg="+retXml.getRetMsg());
				bOK=false;
				retMsgJson = "{\"RetCode\":\""+ret+"\",\"RetMsg\":\"下订单接口返回错误!\"}";
				//return;
			}
		} catch (Exception e) {
			System.out.println("商户下单接口测试失败！：");
			e.printStackTrace();
			bOK=false;
			String errCode  = e.getMessage();
			if("E101".equalsIgnoreCase(errCode)){
				retMsgJson = "{\"RetCode\":\"E101\",\"RetMsg\":\"下订单接口无返回数据!\"}";
			}else if("E102".equalsIgnoreCase(errCode)){
				retMsgJson = "{\"RetCode\":\"E102\",\"RetMsg\":\"验证签名失败!\"}";
			}else if("E103".equalsIgnoreCase(errCode)){
				retMsgJson = "{\"RetCode\":\"E103\",\"RetMsg\":\"进行订单签名失败!\"}";
			}else{
				retMsgJson = "{\"RetCode\":\"E100\",\"RetMsg\":\"下订单通讯失败!\"}";
			}
			//return;
		}
		//设置返回给手机Json数据
				if(bOK){
					String jsonTemplet = "{\"RetCode\":\"0000\",\"RetMsg\":\"下单成功\",\"Version\":\"%s\",\"MerchOrderId\":\"%s\",\"MerchantId\":\"%s\",\"Amount\":\"%s\",\"TradeTime\":\"%s\",\"OrderId\":\"%s\",\"Sign\":\"%s\"}";
					retMsgJson = String.format(jsonTemplet,retXml.getVersion(),retXml.getMerchOrderId(),retXml.getMerchantId(),retXml.getAmount(),
							retXml.getTradeTime(), retXml.getOrderId(), retXml.getSign());
				}
				System.out.println("3333333:"+retMsgJson);
				//返回数据
			    //PrintWriter out = response.getWriter();
			    //response.setContentType("text/html; charset=UTF-8");
			    //out.println(retMsgJson);
			   // out.close(); // for HTTP1.1
				outString(response,retMsgJson);
		System.out.println("商户下单接口测试----ok");
		System.out.println("------------------------------------------------");
	}
	/**
	 * 和上游交互 支付完成后同步返回支付结果
	 * 
	 * @param request
	 *            requet对象
	 * @param response
	 *            response对象
	 * @param temp
	 *            银联返回的数据
	 * @throws Exception
	 */
	@RequestMapping("Notify")
	public void payClient(HttpServletRequest request, HttpServletResponse response, Client client )
			throws Exception {
		log.info("异步通知返回结果1：{}"+gson.toJson(client));
		log.info("**********进入异步通知的地址中***********");
		
		log.info("准备进入修改订单信息！" );
		
		clientServiceImpl.otherInvoke(client);
		
		log.info("修改订单成功！");
		
		// 查询商户上送原始信息
		OriginalOrderInfo originalInfo =payService.getOriginOrderInfo(client.getOrderId());
		log.info("上游返回的异步通知地址originalInfo:"+gson.toJson(originalInfo));
		Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
		String path=originalInfo.getBgUrl() + "?" + queryUtil.bean2QueryStr(client);
		log.info("demo 重定向："+path);
		response.sendRedirect(path.replace(" ", ""));
	}
	
}

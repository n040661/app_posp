package xdt.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONObject;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dto.BaseUtil;
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.MsgBean;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.HFUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IClientH5Service;
import xdt.service.IPmsMerchantInfoService;
import xdt.service.impl.ClientH5ServiceImpl;
import xdt.tools.Client;
import xdt.tools.Constants;
import xdt.tools.Tools;
import xdt.tools.Xml;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;

@Controller
@RequestMapping("/clientH5Controller")
public class ClientH5Controller extends BaseAction {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Resource
	private HfQuickPayService payService;
	@Resource
	private IClientH5Service ClientH5ServiceImpl;
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	/**
	 * 商户下订单接口测试
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "merchantOrderTestH5")
	public void MerchantOrderTestH5(Client client, HttpServletResponse response) throws Exception {
		// 设置参数
		// 必填
		log.info("下游上传参数{}" + gson.toJson(client));
		String url = client.getNotifyUrl();// "http://tjekuaitongs.server.ngrok.cc/app_posp/merchantOrderTestH5/payClienty.action";
		log.info("下游上传异步url" + client.getNotifyUrl());
		String reUrl = client.getReturnUrl();
		client.setReUrl(reUrl);
		client.setUrl(url);
		// client.setMerchantId1("100120242118015");
		PmsBusinessPos busInfo = ClientH5ServiceImpl.selectKey(client.getMerchantId1());
		// client.setMerchantId(Constants.MERCHANT_ID);
		client.setMerchantId(busInfo.getBusinessnum());
		// 必填
		// client.setExtData("");//充值测试
		if (client.getMiscData() == null || client.getMiscData().equals("")) {
			client.setMiscData("");
		} else {
			client.setMiscData(client.getMiscData() + "||0|");
		}

		// 以下扩展参数是按互联网金融行业填写的；其他行业请参考接口文件说明进行填写
		// client.setMiscData("");//
		// "13922897656|0||张三|440121197511140912|62220040001154868428||PAYECO201510285445||2|";
		// //互联网金融client.getMiscData();
		String ss = "" + System.currentTimeMillis();
		log.info("订单号：" + ss);
		// client.setMerchOrderId(ss);//String merchOrderId = "" +
		// System.currentTimeMillis(); // 订单号
		client.setMerchantId(busInfo.getBusinessnum());// String merchantId =
														// ;busInfo.getMerchantId()
		client.setNotifyUrl(Constants.MERCHANT_NOTIFY_URL1);// String notifyUrl
		client.setTradeTime(Tools.getSysTime());// String tradeTime =
												// Tools.getSysTime();
		client.setExpTime("");// String expTime = ""; // 采用系统默认的订单有效时间
		client.setNotifyFlag("");// String notifyFlag = "0";
		// client.setClientIp("");
		client.setReturnUrl(Constants.MERCHANT_RETURN_URL);
		// System.out.println(busInfo.getKek());
		client.setPriKey(busInfo.getKek());
		// client.setPriKey(Constants.MERCHANT_RSA_PRIVATE_KEY);
		client.setPubKey(Constants.PAYECO_RSA_PUBLIC_KEY);
		client.setPayecoUrl(Constants.PAYECO_URL);
		// 调用下单接口
		Xml retXml = new Xml();
		String retMsgJson = "";
		boolean bOK = true;
		System.out.println("-------订单下单接口测试-------------------------");
		try {

			// 接口参数请参考TransactionClient的参数说明
			String ret = ClientH5ServiceImpl.ReceiveInformationH5(client, retXml);
			if (!"0000".equals(ret)) {
				System.out.println("商户下单接口测试失败！：retCode=" + ret + "; msg=" + retXml.getRetMsg());
				bOK = false;
				retMsgJson = "{\"RetCode\":\"" + ret + "\",\"msg\":\"" + retXml.getRetMsg()
						+ "\",\"RetMsg\":\"下订单接口返回错误!\"}";
			}
		} catch (Exception e) {
			System.out.println("商户下单接口测试失败！：");
			e.printStackTrace();
			bOK = false;
			String errCode = e.getMessage();
			if ("E101".equalsIgnoreCase(errCode)) {
				retMsgJson = "{\"RetCode\":\"E101\",\"RetMsg\":\"下订单接口无返回数据!\"}";
			} else if ("E102".equalsIgnoreCase(errCode)) {
				retMsgJson = "{\"RetCode\":\"E102\",\"RetMsg\":\"验证签名失败!\"}";
			} else if ("E103".equalsIgnoreCase(errCode)) {
				retMsgJson = "{\"RetCode\":\"E103\",\"RetMsg\":\"进行订单签名失败!\"}";
			} else {
				retMsgJson = "{\"RetCode\":\"E100\",\"RetMsg\":\"下订单通讯失败!\"}";
			}

		}
		// 设置返回给手机Json数据
		if (bOK) {
			String redirectUrl = ClientH5ServiceImpl.getPayInitRedirectUrl(client, retXml);
			log.info("PayURL : " + redirectUrl);

			/*
			 * "<html><head><title>易联支付H5测试-支付请求</title></head><body>支付请求URL: "
			 * +redirectUrl+"<br/>"
			 */
			retMsgJson = redirectUrl;/*
										 * " <a href=\""+redirectUrl+
										 * "\">立即支付</a></body></html>";
										 */
		}
		System.out.println("3333333:" + retMsgJson);
		outString(response, retMsgJson);
		System.out.println("商户下单接口测试----ok");
		System.out.println("------------------------------------------------");

	}
	@RequestMapping(value="merchantOrderParameter")
	public void MerchantOrderParameter(Client client, HttpServletResponse response){
		log.info("client:"+JSON.toJSON(client));
		TreeMap<String, String> result = new TreeMap<String, String>();
		//获取商户秘钥
		result.putAll(JsdsUtil.beanToMap(client));
		String paramSrc =RequestUtils.getParamSrc(result);
		log.info("paramSrc1:"+paramSrc);
		String md5= RequestUtils.MD5(clientCollectionPayService, client.getMerchantId(), paramSrc);
		try {
			outString(response, md5);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="merchantOrderApi")
	public void MerchantOrderApi(Client client, HttpServletResponse response) throws Exception{
		log.info("下游上传参数{}" + JSON.toJSON(client));
		Map<String, Object> results =new HashMap<>();
		String str;
		try {
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(client.getMerchantId());
		 PmsBusinessPos busInfo = ClientH5ServiceImpl.selectKey(client.getMerchantId());
		 
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(client));
		String paramSrc =RequestUtils.getParamSrc(result);
		log.info("paramSrc："+paramSrc);
		System.out.println("key2:"+key);
		boolean b =MD5Utils.verify(paramSrc, client.getSign(), key, "UTF-8");
		Xml retXml = new Xml();
		String retMsgJson = "";
		boolean bOK = true;
		System.out.println("-------订单下单接口测试-------------------------");
		if(b){
			log.info("签名正确！");
			client.setPriKey(busInfo.getKek());
			 client.setPayecoUrl(Constants.PAYECO_URL);
			//写逻辑
			results =ClientH5ServiceImpl.ReceiveInformationApi(client, retXml);
			log.info("results"+JSON.toJSON(results));
			log.info("商户下单接口测试----ok");
		}else{
			log.info("签名错误！");
			str="{\"retcode\":\"01\",\"retmsg\":\"签名错误！\"}";
			results.put("retcode", "01");
			results.put("retmsg", "签名错误！");
		}
		
			outString(response, JSON.toJSON(results));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	@RequestMapping(value="merchantOrderApiPay")
	public void MerchantOrderApiPay(Client client, HttpServletResponse response) throws Exception{
		log.info("下游上传参数{}" + JSON.toJSON(client));
		Map<String, Object> results =new HashMap<>();
		String str;
		try {
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(client.getMerchantId());
		 PmsBusinessPos busInfo = ClientH5ServiceImpl.selectKey(client.getMerchantId());
		 
		//获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(client));
		String paramSrc =RequestUtils.getParamSrc(result);
		log.info("paramSrc："+paramSrc);
		System.out.println("key2:"+key);
		boolean b =MD5Utils.verify(paramSrc, client.getSign(), key, "UTF-8");
		Xml retXml = new Xml();
		String retMsgJson = "";
		boolean bOK = true;
		System.out.println("-------订单下单接口测试-------------------------");
		if(b){
			log.info("签名正确！");
			 client.setPriKey(busInfo.getKek());
			 client.setPayecoUrl(Constants.PAYECO_URL);
			//写逻辑
			results =ClientH5ServiceImpl.ReceiveInformationApiPay(client);
			log.info("results"+JSON.toJSON(results));
			log.info("商户下单接口测试----ok");
		}else{
			log.info("签名错误！");
			str="{\"retcode\":\"01\",\"retmsg\":\"签名错误！\"}";
			results.put("retcode", "01");
			results.put("retmsg", "签名错误！");
		}
		
			outString(response, JSON.toJSON(results));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	@RequestMapping(value = "Notify")
	public void payClient(HttpServletRequest request, HttpServletResponse response, Client client) throws Exception {
		
		this.log.info("异步通知返回结果33：{}" + this.gson.toJson(client));
		outString(response, "0000");
		this.log.info("**********进入异步通知的地址中***********");
		String path = "";
		this.log.info("订单号:"+client.getMerchOrderId());
		this.ClientH5ServiceImpl.otherInvokeH5(client);
		this.log.info("修改订单成功！");
		OriginalOrderInfo originalInfo = this.payService.getOriginOrderInfo(client.getMerchOrderId());
		this.log.info("上又返回的异步通知地址originalInfo:" + JSON.toJSON(originalInfo));
		Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
		this.log.info("下游的一步地址" + originalInfo.getBgUrl());
		if (client.getStatus().equals("02")) {
			int i = this.ClientH5ServiceImpl.UpdatePmsMerchantInfo(originalInfo);

			this.log.info("实时填金状态失败0成功1：" + i);
		}
		if (originalInfo.getBgUrl() != null) {
			if (originalInfo.getBgUrl().indexOf("?") != -1) {
				path = originalInfo.getBgUrl().replaceAll(",", "&") + "&" + queryUtil.bean2QueryStr(client);
				this.log.info("给下游返回的地址1" + path);
			} else {
				path = originalInfo.getBgUrl() + "?" + queryUtil.bean2QueryStr(client);
				this.log.info("给下游返回的地址2" + path);
			}
			this.log.info("demo 重定向：" + path);
			//HttpURLConection.httpURLConnectionPOST(originalInfo.getPageUrl(), queryUtil.bean2QueryStr(client));
			//response.sendRedirect(path.replace(" ", ""));
			String result1=HttpUtil.sendPost(path.replace(" ", "+"));
			//String result1=	RequestUtils.doPost(originalInfo.getBgUrl(), queryUtil.bean2QueryStr(client), "UTF-8");
			//String result1=HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(client));
			log.info("下游返回状态"+result1);
			if(!"SUCCESS".equals(result1)){
				ThreadPool.executor(new UtilThread(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(client)));
			}
			this.log.info(" 重定向成功！");
		}
		this.log.info("准备进入修改订单信息！");
		
		
	}
	@RequestMapping(value = "ReturnH5")
	public void payClients(HttpServletRequest request, HttpServletResponse response, Client client) throws Exception {
		String path = "";
		this.log.info("同步通知返回结果2：{}" + this.gson.toJson(client));
		this.log.info("**********进入同步通知的地址中***********");
		OriginalOrderInfo originalInfo = this.payService.getOriginOrderInfo(client.getMerchOrderId());
		Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
		if (originalInfo.getPageUrl() != null) {
			if (originalInfo.getBgUrl().indexOf("?") != -1) {
				path = originalInfo.getPageUrl().replaceAll(",", "&") + "&" + queryUtil.bean2QueryStr(client);
				this.log.info("给下游返回的地址3" + path);
			} else {
				path = originalInfo.getPageUrl() + "?" + queryUtil.bean2QueryStr(client);
				this.log.info("给下游返回的地址4" + path);
			}
			this.log.info("demo 重定向1：" + path);
			//HttpURLConection.httpURLConnectionPOST(originalInfo.getPageUrl(), queryUtil.bean2QueryStr(client));
			response.sendRedirect(path.replace(" ", ""));
			this.log.info(" 重定向成功1！");
		}

		this.log.info("修改订单成功!!!!!!!!!!!");
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
	@RequestMapping(value = "bgPayResult")
	public void bgPayResult(HttpServletRequest request, HttpServletResponse response, Client client) throws Exception {
		log.info("第三方返回的数据：{}" + gson.toJson(client));
		log.info("准备进入修改订单信息！");
		// ClientH5ServiceImpl.otherInvokeH5(client);
		// log.info("修改订单成功！");
		if ("02".equals(client.getStatus())) {
			OriginalOrderInfo originalInfo = payService.getOriginOrderInfo(client.getMerchOrderId());
			PmsAppTransInfo pmsAppTransInfo = payService.getFeeInfo(originalInfo.getOrderId());
			Double paymout = Double.parseDouble(pmsAppTransInfo.getPayamount()) / 100 - 2;
			log.info("上又返回的异步通知地址originalInfo:" + gson.toJson(originalInfo));
			Map<String, String> result = new HashMap<String, String>();
			if (originalInfo.getPayType().equals("1")) {
				result.put("name", pmsAppTransInfo.getBankname());
			} else if (originalInfo.getPayType().equals("0")) {
				result.put("realName", originalInfo.getByUser());
				result.put("cardNo", originalInfo.getBankNo());
			}
			result.put("merid", originalInfo.getPid());
			result.put("orderid", client.getMerchOrderId());
			result.put("paymount", paymout.toString());
			String str = HttpUtil.parseParams(result);
			log.info("代付接口返回的数据:" + str);
			String url = "";
			if (originalInfo.getPayType().equals("1")) {

				request.setAttribute("merid", originalInfo.getPid());
				request.setAttribute("paymount", paymout.toString());
				request.setAttribute("name", pmsAppTransInfo.getBankname());
				request.setAttribute("orderid", client.getMerchOrderId());
				request.getRequestDispatcher("/pay/yilian/customer_daifu1.jsp").forward(request, response);
			} else if (originalInfo.getPayType().equals("0")) {

				MsgBean req_bean = new MsgBean();
				req_bean.setMERCHANT_ID(result.get("merid"));
				String batch_no = HFUtil.randomOrder();
				req_bean.setBATCH_NO(batch_no);
				String sn = HFUtil.HFrandomOrder();
				String amount = paymout.toString();
				String acc_name = originalInfo.getByUser();
				String acc_no = originalInfo.getBankNo();
				String acc_province = "";
				String acc_city = "";
				String bank_name = "";
				String acc_prop = "";
				String bank_no = "";
				String acc_type = "";
				String map = sn + "?" + amount + "?" + acc_no + "?" + acc_name + "?" + acc_province + "?" + acc_city
						+ "?" + bank_name + "?" + acc_prop + "?" + acc_province + "?" + acc_type + "1,";
				map = map.substring(0, map.length() - 1);
				log.info("上送的map数据:" + map);
				req_bean.setMAP(map);
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(req_bean.getMERCHANT_ID());
				String merchantKey = keyinfo.getMerchantkey();
				String sign = HFSignUtil.sign(PreSginUtil.paydaifuResultString(req_bean), merchantKey);
				req_bean.setSIGN(sign);
				log.info("SN:"+req_bean.getMAP());
				log.info("JSON-SN:"+JSON.toJSON(req_bean));
				//String message = "0:initialize";
				String jsonString = null;
				SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();
				req_bean.setVERSION("2.1");
				req_bean.setMSG_TYPE("100001");
				req_bean.setUSER_NAME(Constants.user_name);
				log.info("上送的参数:"+JSON.toJSON(req_bean));
				String res_bean = null;
				res_bean = clientCollectionPayService.pay(req_bean,responseDTO, jsonString);
				log.info("res_bean:"+JSON.toJSON(res_bean));
				log.info("响应的信息:"+JSON.toJSON(res_bean));
				if (JSON.toJSON(res_bean) != null) {
					JSONObject ob = JSONObject.fromObject(JSON.toJSON(res_bean));
					Iterator it = ob.keys();
					while (it.hasNext()) {
						String key = (String) it.next();
						log.info("第一次获取的名称:"+key);
						if (key.equals("BODYS")) {
							String value = ob.getString(key);
							log.info("第一次获取的值:"+value);
							JSONObject ob1 = JSONObject.fromObject(value.substring(1, value.length() - 1));
							Iterator it1 = ob1.keys();
							while (it1.hasNext()) {				
								String key1 = (String) it1.next();
								log.info("第一次获取的名称:"+key1);
								if (key1.equals("PAY_STATE")) {
									log.info("提交状态:" + "\t" + ob1.getString(key1));
									if (ob1.getString(key1).equals("0000")) {
										result.put("batch_no", batch_no);
										String str1 = HttpUtil.parseParams(result);
										url = BaseUtil.url+"/pay/yilian/autoDaifuSuccess.jsp";
										String path1 = url + "?" + str1;
										response.sendRedirect(path1.replace(" ", ""));
									}else {

										result.put("batch_no", batch_no);
										String str1 = HttpUtil.parseParams(result);
										url = BaseUtil.url+"/app_posp/pay/yilian/autoDaifuFail.jsp";
										String path1 = url + "?" + str1;
										response.sendRedirect(path1.replace(" ", ""));
									}
								}
							}
						}
					}
				} else {

					result.put("batch_no", batch_no);
					String str1 = HttpUtil.parseParams(result);
					url = BaseUtil.url+"/pay/yilian/autoDaifuFail.jsp";
					String path1 = url + "?" + str1;
					response.sendRedirect(path1.replace(" ", ""));
				}

			}

		}
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
	@RequestMapping(value = "bgPayResult1")
	public void bgPayResult1(HttpServletRequest request, HttpServletResponse response, Client client) throws Exception {
		log.info("第三方返回的数据：{}" + gson.toJson(client));
		log.info("准备进入修改订单信息！");
		// ClientH5ServiceImpl.otherInvokeH5(client);
		// log.info("修改订单成功！");
		if ("02".equals(client.getStatus())) {
			OriginalOrderInfo originalInfo = payService.getOriginOrderInfo(client.getMerchOrderId());
			PmsAppTransInfo pmsAppTransInfo = payService.getFeeInfo(originalInfo.getOrderId());
			Double paymout = Double.parseDouble(pmsAppTransInfo.getPayamount()) / 100 - 2;
			log.info("上又返回的异步通知地址originalInfo:" + gson.toJson(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			Map<String, String> result = new HashMap<String, String>();
			result.put("merid", originalInfo.getPid());
			result.put("orderid", client.getMerchOrderId());
			result.put("paymount", paymout.toString());
			String str = HttpUtil.parseParams(result);
			log.info("代付接口返回的数据:" + str);
			String url = BaseUtil.url+"/pay/yilian/success1.jsp";
			String path = url + "?" + str;
			response.sendRedirect(path.replace(" ", ""));

		}
	}
	private String createJsonString(SubmitOrderNoCardPayResponseDTO responseDTO) {
		return gson.toJson(responseDTO);
	}

}

package xdt.controller.hfb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import xdt.controller.BaseAction;
import xdt.dto.hfb.HFBPayRequest;
import xdt.dto.hfb.HFBPayResponse;
import xdt.dto.hfb.HfbRequest;
import xdt.dto.hfb.HfbResponse;
import xdt.dto.sxf.SXFResponse;
import xdt.dto.sxf.SXFUtil;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IHFBService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.JsonUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.PaymentUtils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;

@Controller
@RequestMapping("/HFBController")
public class HFBController extends BaseAction {

	Logger log =Logger.getLogger(this.getClass());
	
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource
	private IHFBService service;
	@Resource
	private HfQuickPayService payService;
	/**
	 * 签名
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value="paySign")
	public void paySign(HfbRequest hfbRequest,HttpServletResponse response){
		
		log.info("汇付宝--网关发来的参数："+JSON.toJSONString(hfbRequest));
		
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hfbRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(hfbRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据**********汇付宝支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名**********汇付宝支付:" + md5);
		try {
			outString(response, md5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 签名
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value="codeSign")
	public void codeSign(HFBPayRequest hfbRequest,HttpServletResponse response){
		
		log.info("汇付宝--代付发来的参数："+JSON.toJSONString(hfbRequest));
		
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hfbRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(hfbRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据**********汇付宝支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名**********汇付宝支付:" + md5);
		Map<String, String> map =new HashMap<>();
		map.put("sign", md5);
		try {
			outString(response, JSON.toJSONString(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 获取银行信息
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value="payApplyParameter")
	public void onlineBankList(HfbRequest hfbRequest,HttpServletResponse response){
		
		log.info("汇付宝***获取银行参数："+JSON.toJSON(hfbRequest));
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hfbRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hfbRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		boolean b = MD5Utils.verify(paramSrc, hfbRequest.getSign(), key,
				"UTF-8");
		if(b){
			log.info("签名正确");
			paramSrc  =service.onlineBankList(hfbRequest, result);
			
			log.info("返回的参数:"+JSON.toJSON(result));
		}else{
			result.put("respCode", "0001");
			result.put("respMsg","签名错误！");
			paramSrc =JSON.toJSONString(result);
		}
		try {
			outString(response, JSON.toJSON(paramSrc));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="cardPay")
	public void cardPay(HfbRequest hfbRequest,HttpServletResponse response,HttpServletRequest request){
		
		log.info("汇付宝---网关支付参数:"+JSON.toJSONString(hfbRequest));
		
		
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hfbRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hfbRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		boolean b = MD5Utils.verify(paramSrc, hfbRequest.getSign(), key,
				"UTF-8");
		if(b){
			log.info("签名正确");
			hfbRequest.setUrl(hfbRequest.getNotifyUrl());
			hfbRequest.setReUrl(hfbRequest.getCallBackUrl());
			result  =service.cardPay(hfbRequest, result);
			
			if("00".equals(result.get("respCode"))){
				try {
				request.setAttribute("callBackUrl",result.get("callBackUrl"));
				request.setAttribute("merchantId",result.get("merchantId"));
				request.setAttribute("merchantOrderNo", result.get("merchantOrderNo"));
				request.setAttribute("merchantUserId", result.get("merchantUserId"));
				request.setAttribute("notifyUrl", result.get("notifyUrl"));
				request.setAttribute("onlineType", result.get("onlineType"));
				request.setAttribute("payAmount",result.get("payAmount"));
				request.setAttribute("productCode", result.get("productCode"));
				request.setAttribute("requestTime", result.get("requestTime"));
				request.setAttribute("version", result.get("version"));
				request.setAttribute("signString", result.get("signString"));
				if("simple".equals(result.get("onlineType"))){
					request.setAttribute("bankId",result.get("bankId"));
					request.setAttribute("bankName", result.get("bankName"));
					request.setAttribute("bankCardType", result.get("bankCardType"));
				}
				
					request.getRequestDispatcher("../pay/hfb/cardPost.jsp").forward(request, response);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
		}else{
			result.put("respCode", "0001");
			result.put("respMsg","签名错误！");
		}
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="wxpayParameter")
	public void WZpay(HfbRequest hfbRequest,HttpServletResponse response){
		
		log.info("汇付宝---微信支付宝参数："+JSON.toJSONString(hfbRequest));
		
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hfbRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hfbRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("微信支付宝参数签名前参数:"+paramSrc);
		boolean b = MD5Utils.verify(paramSrc, hfbRequest.getSign(), key,"UTF-8");
		if(b){
			log.info("签名正确");
			hfbRequest.setUrl(hfbRequest.getNotifyUrl());
			hfbRequest.setReUrl(hfbRequest.getCallBackUrl());
			result  =service.WZpay(hfbRequest, result);
			
		}else{
			result.put("respCode", "0001");
			result.put("respMsg","签名错误！");
		}
		results1.putAll(result);
		System.out.println("results1:"+results1);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("汇付宝---返回数据签名签的数据:"+JSON.toJSONString(paramSrc1));
		String md5 = MD5Utils.sign(paramSrc1, key, "UTF-8");
		result.put("sign", md5);
		try {
			outString(response, JSON.toJSON(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value="select")
	public void select(HfbRequest hfbRequest,HttpServletResponse response){
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hfbRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hfbRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		boolean b = MD5Utils.verify(paramSrc, hfbRequest.getSign(), key,
				"UTF-8");
		String str ="";
		if(b){
			log.info("签名正确");
			str  =service.WZSelect(hfbRequest, result);
			JSONObject json =JSONObject.parseObject(str);
			result.put("merchantId", hfbRequest.getMerchantId());
			result.put("merchantOrderNo", json.getString("merchantBillNo"));
			result.put("tradeType", json.getString("tradeType"));
			result.put("payAmount", json.getString("payAmt"));
			if("1000".equals(json.getString("retCode"))){
				result.put("respCode", "00");
				result.put("respMsg", "成功");
			}else if("-1".equals(json.getString("retCode"))){
				result.put("respCode", "01");
				result.put("respMsg", "失败");
			}
			
		}else{
			result.put("respCode", "0001");
			result.put("respMsg","签名错误！");
		}
		results1.putAll(result);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("汇付宝---返回数据签名签的数据:"+JSON.toJSONString(paramSrc1));
		String md5 = MD5Utils.sign(paramSrc1, key, "UTF-8");
		result.put("sign", md5);
		try {
			outString(response, JSON.toJSON(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value="pay")
	public void pay(HFBPayRequest hfbPayRequest,HttpServletResponse response){
		
		log.info("汇付宝---代付参数："+JSON.toJSONString(hfbPayRequest));
		
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hfbPayRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hfbPayRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		boolean b = MD5Utils.verify(paramSrc, hfbPayRequest.getSign(), key,"UTF-8");
		if(b){
			log.info("签名正确");
			hfbPayRequest.setUrl(hfbPayRequest.getNotifyUrl());
			result  =service.pay(hfbPayRequest, result);
			
		}else{
			result.put("respCode", "0001");
			result.put("respMsg","签名错误！");
		}
		results1.putAll(result);
		System.out.println("results1:"+results1);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("汇付宝---返回数据签名签的数据:"+JSON.toJSONString(paramSrc1));
		String md5 = MD5Utils.sign(paramSrc1, key, "UTF-8");
		result.put("sign", md5);
		try {
			outString(response, JSON.toJSON(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value="notifyUrl")
	public void notifyUrl(HfbResponse hfbResponse, HttpServletResponse response){
		log.info("汇付宝异步通知来了");
		log.info("汇付宝异步参数："+JSON.toJSONString(hfbResponse));
		HfbResponse hfbResponses =new HfbResponse();
		try {
			String str;
			if (hfbResponse !=null) {
				str = "ok";
				outString(response, str);
					OriginalOrderInfo originalInfo = null;
					if (hfbResponse.getMerchantOrderNo() != null && hfbResponse.getMerchantOrderNo()!= "") {
						originalInfo = this.payService.getOriginOrderInfo(hfbResponse.getMerchantOrderNo());
					}
					log.info("订单数据:" + JSON.toJSON(originalInfo));
					Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
					log.info("下游的异步地址" + originalInfo.getBgUrl());
					log.info("汇付宝异步返回解析参数"+JSON.toJSON(hfbResponse));
					service.update(hfbResponse);
					//---------------------------------------------------
					//返回参数
					hfbResponses.setMerchantId(originalInfo.getPid());
					hfbResponses.setPayAmount(hfbResponse.getPayAmount());
					if("1000".equals(hfbResponse.getResult())){
						hfbResponses.setRespCode("00");
						hfbResponses.setRespMsg("支付成功");
					}else if("1002".equals(hfbResponse.getResult())){
						hfbResponses.setRespCode("01");
						hfbResponses.setRespMsg("支付失败");
					}
					hfbResponses.setMerchantOrderNo(hfbResponse.getMerchantOrderNo());
					//和下面的签名
					//---------------------------------------------------
					TreeMap<String, String> result = new TreeMap<String, String>();
					ChannleMerchantConfigKey keyinfo = clientCollectionPayService
							.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					result.putAll(JsdsUtil.beanToMap(hfbResponses));
					String paramSrc = RequestUtils.getParamSrc(result);
					log.info("签名前数据**********汇付宝支付:" + paramSrc);
					String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
					hfbResponses.setSign(md5);
					
					String result1=HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(hfbResponses));
					log.info("下游返回状态" + result1);
					if (!"SUCCESS".equals(result1)) {
						ThreadPool.executor(new UtilThread(originalInfo
								.getBgUrl(), queryUtil
								.bean2QueryStr(hfbResponses)));
					}

			} else {
				str = "FAIL";
				outString(response, str);
			}
		} catch (Exception e) {
			log.info("汇付宝异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="notifyUrls")
	public void notifyUrls(HFBPayResponse hfbPayResponse, HttpServletResponse response){
		log.info("汇付宝转账异步通知来了");
		log.info("汇付宝异步参数："+JSON.toJSONString(hfbPayResponse));
		HFBPayResponse hfbResponses =new HFBPayResponse();
		try {
			String str;
			if (hfbPayResponse !=null&&hfbPayResponse.getTransferDetails()!=null) {
				str = "ok";
				outString(response, str);
					OriginalOrderInfo originalInfo = null;
					if (hfbPayResponse.getMerchantBatchNo() != null && hfbPayResponse.getMerchantBatchNo()!= "") {
						originalInfo = this.service.getOriginOrderInfo(hfbPayResponse.getMerchantBatchNo());
					}
					log.info("订单数据:" + JSON.toJSON(originalInfo));
					Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
					log.info("下游的异步地址" + originalInfo.getBgUrl());
					log.info("随行付异步返回解析参数"+JSON.toJSON(hfbPayResponse));
					
					String transferDetails= hfbPayResponse.getTransferDetails();
					JSONArray json =JSONArray.fromObject(transferDetails);
					String map =json.get(0).toString();
					JSONObject jsons =JSONObject.parseObject(map);
					if("1000".equals(jsons.getString("status"))){
						service.UpdateDaifu(hfbPayResponse.getMerchantBatchNo(), "00");
						hfbResponses.setRespCode("00");
						hfbResponses.setRespMsg("代付成功");
					}else if("2000".equals(jsons.getString("status"))){
						service.UpdateDaifu(hfbPayResponse.getMerchantBatchNo(), "200");
					}else if("3000".equals(jsons.getString("status"))){
						service.UpdateDaifu(hfbPayResponse.getMerchantBatchNo(), "01");
						hfbResponses.setRespCode("01");
						hfbResponses.setRespMsg("代付失败");
					}
					
					//---------------------------------------------------
					//返回参数
					hfbResponses.setMerchantId(originalInfo.getPid());
					hfbResponses.setAmount(hfbPayResponse.getSuccessAmount());
					hfbResponses.setMerchantBatchNo(hfbPayResponse.getMerchantBatchNo());
					//和下面的签名
					//---------------------------------------------------
					TreeMap<String, String> result = new TreeMap<String, String>();
					ChannleMerchantConfigKey keyinfo = clientCollectionPayService
							.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					result.putAll(JsdsUtil.beanToMap(hfbResponses));
					String paramSrc = RequestUtils.getParamSrc(result);
					log.info("签名前数据**********汇付宝支付:" + paramSrc);
					String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
					hfbResponses.setSign(md5);
					String result1=HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(), queryUtil.bean2QueryStr(hfbResponses));
					//String result1=HttpUtil.sendPost(originalInfo.getBgUrl()+"?"+queryUtil.bean2QueryStr(hfbResponses));
					log.info("下游返回状态" + result1);
					if (!"SUCCESS".equals(result1)) {
						ThreadPool.executor(new UtilThread(originalInfo
								.getBgUrl(), queryUtil
								.bean2QueryStr(hfbResponses)));
					}

			} else {
				str = "FAIL";
				outString(response, str);
			}
		} catch (Exception e) {
			log.info("汇付宝异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value ="transferBankQuery")
	public void transferBankQuery(HttpServletResponse response){
		String s =HttpUtil.sendPost("https://open.heepay.com/transferBankQuery.do");
		try {
			outString(response, s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value ="transferCityQuery")
	public void transferCityQuery(HttpServletResponse response){
		String s =HttpUtil.sendPost("https://open.heepay.com/transferCityQuery.do");
		try {
			outString(response, s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package xdt.controller.hj;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import xdt.controller.BaseAction;
import xdt.dto.hfb.HfbRequest;
import xdt.dto.hfb.HfbResponse;
import xdt.dto.hj.HJPayRequest;
import xdt.dto.hj.HJRequest;
import xdt.dto.hj.HJResponse;
import xdt.dto.hj.HJUtil;
import xdt.dto.lhzf.LhzfResponse;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IHJService;
import xdt.service.ITFBService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;
@RequestMapping("/HJController")
@Controller
public class HJController extends BaseAction{

	Logger log =Logger.getLogger(this.getClass());
	
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource
	private HfQuickPayService payService;
	@Resource
	private IHJService service;
	/**
	 * 签名
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value="paySign")
	public void paySign(HJRequest hjRequest,HttpServletResponse response){
		
		log.info("汇聚--签名发来的参数："+JSON.toJSONString(hjRequest));
		
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hjRequest.getMerchantNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(hjRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据**********汇聚支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名**********汇聚支付:" + md5);
		Map<String, String> map =new HashMap<>();
		map.put("sign", md5);
		try {
			outString(response, JSON.toJSON(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="codeSign")
	public void codeSign(HJPayRequest hjPayRequest,HttpServletResponse response){
		
		log.info("汇聚--签名发来的参数："+JSON.toJSONString(hjPayRequest));
		
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hjPayRequest.getMerchantNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(hjPayRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据**********汇聚支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名**********汇聚支付:" + md5);
		Map<String, String> map =new HashMap<>();
		map.put("sign", md5);
		try {
			outString(response, JSON.toJSON(map));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="cardPay")
	public void cadePay(HJRequest hjRequest,HttpServletResponse response,HttpServletRequest request){
		
		log.info("汇聚网关上传参数"+JSON.toJSONString(hjRequest));
		
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hjRequest.getMerchantNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hjRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串"+paramSrc);
		boolean b = MD5Utils.verify(paramSrc, hjRequest.getSign(), key,
				"UTF-8");
		if(b){
			log.info("签名正确");
			
			if("".equals(hjRequest.getFrpCode())||null==hjRequest.getFrpCode()){
				result.put("respCode", "0001");
				result.put("respMsg","frpCode不能为null！");
			}else{
				hjRequest.setUrl(hjRequest.getNotifyUrl());
				hjRequest.setReUrl(hjRequest.getReturnUrl());
				result  =service.cardPay(hjRequest, result);
				log.info("汇聚返回参数"+JSON.toJSON(result));
			
			
			if("00".equals(result.get("respCode"))){
				try {
				request.setAttribute("p1_MerchantNo",result.get("p1_MerchantNo"));
				request.setAttribute("p2_OrderNo",result.get("p2_OrderNo"));
				request.setAttribute("p3_Amount", result.get("p3_Amount"));
				request.setAttribute("p4_Cur", result.get("p4_Cur"));
				request.setAttribute("p5_ProductName", result.get("p5_ProductName"));
				request.setAttribute("p6_Mp", result.get("p6_Mp"));
				request.setAttribute("p7_ReturnUrl",result.get("p7_ReturnUrl"));
				request.setAttribute("p8_NotifyUrl", result.get("p8_NotifyUrl"));
				request.setAttribute("p9_FrpCode", result.get("p9_FrpCode"));
				request.setAttribute("pa_OrderPeriod", result.get("pa_OrderPeriod"));
				request.setAttribute("hmac", result.get("hmac"));
				request.setAttribute("cardPayUrl",result.get("cardPayUrl"));
				request.getRequestDispatcher("../pay/hj/cardPost.jsp").forward(request, response);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
	public void scanCode(HJRequest hjRequest,HttpServletResponse response,HttpServletRequest request){
		log.info("汇聚扫码上传参数"+JSON.toJSONString(hjRequest));
		
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hjRequest.getMerchantNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hjRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串"+paramSrc);
		boolean b = MD5Utils.verify(paramSrc, hjRequest.getSign(), key,
				"UTF-8");
		if(b){
			log.info("签名正确");
			hjRequest.setUrl(hjRequest.getNotifyUrl());
			hjRequest.setReUrl(hjRequest.getReturnUrl());
			result  =service.scanCode(hjRequest, result);
			log.info("汇聚返回参数"+JSON.toJSON(result));
			
		}else{
			result.put("respCode", "0001");
			result.put("respMsg","签名错误！");
		}
		results1.putAll(result);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("汇聚---返回数据签名签的数据:"+JSON.toJSONString(paramSrc1));
		String md5 = MD5Utils.sign(paramSrc1, key, "UTF-8");
		result.put("sign", md5);
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="select")
	public void select(HJRequest hjRequest,HttpServletResponse response,HttpServletRequest request){
		log.info("汇聚查询接口上传参数"+JSON.toJSONString(hjRequest));
		
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hjRequest.getMerchantNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hjRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串"+paramSrc);
		boolean b = MD5Utils.verify(paramSrc, hjRequest.getSign(), key,
				"UTF-8");
		if(b){
			log.info("签名正确");
			result  =service.select(hjRequest, result);
			log.info("汇聚返回参数"+JSON.toJSON(result));
		}else{
			result.put("respCode", "0001");
			result.put("respMsg","签名错误！");
		}
		results1.putAll(result);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("汇聚---返回数据签名签的数据:"+JSON.toJSONString(paramSrc1));
		String md5 = MD5Utils.sign(paramSrc1, key, "UTF-8");
		result.put("sign", md5);
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@RequestMapping(value="pay")
	public void pay(HJPayRequest hjPayRequest,HttpServletResponse response,HttpServletRequest request){
		log.info("汇聚代付下有参数："+hjPayRequest);
		
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hjPayRequest.getMerchantNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hjPayRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串"+paramSrc);
		boolean b = MD5Utils.verify(paramSrc, hjPayRequest.getSign(), key,
				"UTF-8");
		if(b){
			log.info("签名正确");
			/*if("10044076033".equals(keyinfo.getMercid())||"10066544800".equals(keyinfo.getMercid())||"10044018124".equals(keyinfo.getMercid())) {
				String ip =getIpAddr(request);//客户端ip
				log.info("1终端ip："+ip);
				if(ip.equals(keyinfo.getModifyreason())) {
					result  =service.pay(hjPayRequest, result);
					log.info("汇聚返回参数"+JSON.toJSON(result));
				}else {
					result.put("respCode", "0001");
					result.put("respMsg","IP不在白名单内");
				}
			}else {*/
				result  =service.pay(hjPayRequest, result);
				log.info("汇聚返回参数"+JSON.toJSON(result));
			//}
			
			
		}else{
			result.put("respCode", "0001");
			result.put("respMsg","签名错误！");
		}
		results1.putAll(result);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("汇聚---返回数据签名签的数据:"+JSON.toJSONString(paramSrc1));
		String md5 = MD5Utils.sign(paramSrc1, key, "UTF-8");
		result.put("sign", md5);
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value="notifyUrl")
	public void notifyUrl(HJResponse hjResponse,HttpServletResponse response){
		
		log.info("汇聚----异步通知返回数据:"+JSON.toJSONString(hjResponse));
		
		log.info("汇聚异步通知来了");
		HJResponse hjResponses =new HJResponse();
		try {
			String str;
			if (hjResponse.getR2_OrderNo() !=null) {
				str = "success";
				outString(response, str);
					OriginalOrderInfo originalInfo = null;
					if (hjResponse.getR2_OrderNo() != null && hjResponse.getR2_OrderNo()!= "") {
						originalInfo = this.payService.getOriginOrderInfo(hjResponse.getR2_OrderNo());
					}
					log.info("订单数据:" + JSON.toJSON(originalInfo));
					Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
					log.info("下游的异步地址" + originalInfo.getBgUrl());
					service.update(hjResponse,originalInfo);
					if("100".equals(hjResponse.getR6_Status())){
						if(!"10052270614".equals(originalInfo.getPid())) {
							int ii =service.UpdatePmsMerchantInfo(originalInfo);
							System.out.println(ii);
						}
					}
					//---------------------------------------------------
					//返回参数
					hjResponses.setMerchantNo(originalInfo.getPid());
					hjResponses.setAmount(Double.parseDouble(hjResponse.getR3_Amount())*100+"");
					hjResponses.setOrderNo(hjResponse.getR2_OrderNo());
					if(hjResponse.getR5_Mp()!=null){
						hjResponses.setMp(hjResponse.getR5_Mp());
					}
					if("100".equals(hjResponse.getR6_Status())){
						hjResponses.setRespCode("00");
						hjResponses.setRespMsg("支付成功");
					}else {
						hjResponses.setRespCode("01");
						hjResponses.setRespMsg("支付失败");
					}
					//和下面的签名
					//---------------------------------------------------
					TreeMap<String, String> result = new TreeMap<String, String>();
					ChannleMerchantConfigKey keyinfo = clientCollectionPayService
							.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					result.putAll(JsdsUtil.beanToMap(hjResponses));
					String paramSrc = RequestUtils.getParamSrc(result);
					log.info("签名前数据**********汇聚支付:" + paramSrc);
					String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
					hjResponses.setSign(md5);
					
					String result1=HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(hjResponses));
					log.info("下游返回状态" + result1);
					if (!"SUCCESS".equals(result1)) {
						ThreadPool.executor(new UtilThread(originalInfo
								.getBgUrl(), queryUtil
								.bean2QueryStr(hjResponses)));
					}
			} else {
				str = "FAIL";
				outString(response, str);
			}
		} catch (Exception e) {
			log.info("汇聚异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value="returnUrl")
	public void returnUrl(HJResponse hjResponse,HttpServletResponse response,HttpServletRequest request){
		
		try {
			log.info("汇聚同步数据返回参数:"+JSON.toJSONString(hjResponse));
			HJResponse hjResponses =new HJResponse();
			OriginalOrderInfo originalInfo = null;
			if (hjResponse.getR2_OrderNo() != null && hjResponse.getR2_OrderNo()!= "") {
				originalInfo = this.payService.getOriginOrderInfo(hjResponse.getR2_OrderNo());
			}
			log.info("订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			log.info("下游的同步地址" + originalInfo.getPageUrl());
			log.info("汇聚同步返回解析参数"+JSON.toJSON(hjResponse));
				//---------------------------------------------------
				//返回参数
				hjResponses.setMerchantNo(originalInfo.getPid());
				hjResponses.setAmount(Double.parseDouble(hjResponse.getR3_Amount())*100+"");
				hjResponses.setOrderNo(hjResponse.getR2_OrderNo());	
				if(hjResponses.getR5_Mp()!=null){
					hjResponses.setR5_Mp(hjResponses.getR5_Mp());
				}
				if("100".equals(hjResponse.getR6_Status())){
					hjResponse.setRespCode("00");
					hjResponse.setRespMsg("支付成功");
				}else{
					hjResponse.setRespCode("01");
					hjResponse.setRespMsg("支付失败");
				}
			//和下面的签名
			//---------------------------------------------------
			TreeMap<String, String> result = new TreeMap<String, String>();
			ChannleMerchantConfigKey keyinfo = clientCollectionPayService
					.getChannelConfigKey(originalInfo.getPid());
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			result.putAll(JsdsUtil.beanToMap(hjResponse));
			String paramSrc = RequestUtils.getParamSrc(result);
			log.info("签名前数据**********汇聚支付:" + paramSrc);
			String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
			hjResponse.setSign(md5);
			String path=queryUtil.bean2QueryStr(hjResponse);
			log.info("发送前的参数："+path);
			/*request.setAttribute("merchantNo", hjResponse.getMerchantNo());
			request.setAttribute("amount", hjResponse.getAmount());
			request.setAttribute("orderNo", hjResponse.getOrderNo());
			request.setAttribute("respCode", hjResponse.getRespCode());
			request.setAttribute("respMsg", hjResponse.getRespMsg());
			request.getRequestDispatcher(originalInfo.getPageUrl()).forward(request, response);*/
			response.sendRedirect(originalInfo.getPageUrl()+"?"+path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return;
		
	}
	
	public static void main(String[] args) {
		
		String s ="amount=1000.0&merchantNo=10035012557&orderNo=Z1801301136543H08K14739cc4f8&respCode=00&respMsg=支付成功&sign=d2e2b08cc6502897f0bbbcca61c9876f";
		String result1=HttpURLConection.httpURLConnectionPOST("http://pay.zhijet.com/receive/ljlqqcode" ,s);
		System.out.println(result1);
	}
}

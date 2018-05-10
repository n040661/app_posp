package xdt.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;

import xdt.dto.jp.JpUtil;
import xdt.dto.pay.PayRequest;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IHFBService;
import xdt.service.IPayService;
import xdt.util.HttpURLConection;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月8日 下午5:26:19 
* 类说明 
*/
@Controller
@RequestMapping("/JPController")
public class JPController extends BaseAction{

	Logger log =Logger.getLogger(this.getClass());
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource
	private IPayService service;
	@Resource
	private IHFBService ihfbService;
	@Resource
	private HfQuickPayService payService;
	
	
	@RequestMapping(value="cardPay")
	public void cadePay(PayRequest payRequest,HttpServletResponse response,HttpServletRequest request){
		
		log.info("九派网关上传参数"+JSON.toJSONString(payRequest));
		
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(payRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(payRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串"+paramSrc);
		boolean b = MD5Utils.verify(paramSrc, payRequest.getSign(), key,
				"UTF-8");
		if(b){
			log.info("签名正确");
			
			payRequest.setUrl(payRequest.getNotifyUrl());
			payRequest.setReUrl(payRequest.getReturnUrl());
			result  =service.cardPay(payRequest, result);
			log.info("九派返回参数"+JSON.toJSON(result));
			
			
			if("00".equals(result.get("respCode"))){
				try {
				request.setAttribute("charset",result.get("charset"));
				request.setAttribute("version",result.get("version"));
				request.setAttribute("service", result.get("service"));
				request.setAttribute("signType", result.get("signType"));
				request.setAttribute("merchantId",result.get("merchantId"));
				request.setAttribute("requestTime",result.get("requestTime"));
				request.setAttribute("requestId",result.get("requestId"));
				request.setAttribute("pageReturnUrl",result.get("pageReturnUrl"));
				request.setAttribute("notifyUrl",result.get("notifyUrl"));
				request.setAttribute("merchantName",result.get("merchantName"));
				request.setAttribute("subMerchantId",result.get("subMerchantId"));
				request.setAttribute("memberId",result.get("memberId"));
				request.setAttribute("orderTime",result.get("orderTime"));
				request.setAttribute("orderId",result.get("orderId"));
				request.setAttribute("totalAmount",result.get("totalAmount"));
				request.setAttribute("currency",result.get("currency"));
				request.setAttribute("bankAbbr",result.get("bankAbbr"));
				request.setAttribute("cardType",result.get("cardType"));
				request.setAttribute("payType",result.get("payType"));
				request.setAttribute("validUnit",result.get("validUnit"));
				request.setAttribute("validNum",result.get("validNum"));
				request.setAttribute("goodsName",result.get("goodsName"));
				request.setAttribute("goodsId",result.get("goodsId"));
				request.setAttribute("goodsDesc",result.get("goodsDesc"));
				request.setAttribute("merchantSign",result.get("merchantSign"));
				request.setAttribute("merchantCert",result.get("merchantCert"));
				request.setAttribute("cardUrl", result.get("cardUrl"));
				request.getRequestDispatcher("../pay/jp/cardPost.jsp").forward(request, response);
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
	
	@RequestMapping(value="pay")
	public void pay(PayRequest payRequest,HttpServletResponse response,HttpServletRequest request) {
		log.info("九派网关上传参数"+JSON.toJSONString(payRequest));
		
		Map<String, String> result =new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(payRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(payRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串"+paramSrc);
		boolean b = MD5Utils.verify(paramSrc, payRequest.getSign(), key,
				"UTF-8");
		if(b){
			log.info("签名正确");
			
			payRequest.setUrl(payRequest.getNotifyUrl());
			payRequest.setReUrl(payRequest.getReturnUrl());
			payRequest.setType("jpPay");
			result  =service.pay(payRequest, result);
			log.info("九派返回参数"+JSON.toJSON(result));
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
	
	@RequestMapping(value="notifyUrl")
	public void notifyUrl(HttpServletRequest request,HttpServletResponse response) {
		try {
		String orderId=request.getParameter("orderId");
		String charset=request.getParameter("charset");
		String version=request.getParameter("version");
		String merchanId=request.getParameter("merchanId");
		String payType=request.getParameter("payType");
		String signType=request.getParameter("signType");
		String serverCert=request.getParameter("serverCert");
		String serverSign=request.getParameter("serverSign");
		String memberId=request.getParameter("memberId");
		String amount=request.getParameter("amount");
		String orderTime=request.getParameter("orderTime");
		String orderSts=request.getParameter("orderSts");
		String bankAbbr=request.getParameter("bankAbbr");
		String payTime=request.getParameter("payTime");
		String acDate=request.getParameter("acDate");
		String fee=request.getParameter("fee");
		log.info("九派异步返回参数：orderId="+orderId+",charset="+charset+",version="+version+",merchanId="+merchanId
				+",payType="+payType+",signType="+signType+",serverCert="+serverCert+",serverSign="+serverSign
				+",memberId="+memberId+",amount="+amount+",orderTime="+orderTime+",orderSts="+orderSts+",bankAbbr="+bankAbbr
				+",payTime="+payTime+",acDate="+acDate+",fee="+fee);
		Map<String, String> map =new HashMap<>();
		if(orderId !=null &&orderId !="") {
			map.put("result", "SUCCESS");
			OriginalOrderInfo originalInfo = null;
				originalInfo = this.ihfbService.getOriginOrderInfo(orderId);
			log.info("订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			log.info("下游的异步地址" + originalInfo.getBgUrl());
			if("PD".equals(orderSts)) {
				String orderStatus ="2";
				service.updateByOrderId(orderId, orderStatus, map);
			}
			//---------------------------------------------------
			//返回参数
			PayRequest payRequest =new PayRequest();
			payRequest.setMerchantId(originalInfo.getPid());
			payRequest.setAmount(amount);
			payRequest.setOrderId(orderId);
			payRequest.setRespCode("00");
			payRequest.setRespMsg("支付成功");
			//和下面的签名
			//---------------------------------------------------
			TreeMap<String, String> result = new TreeMap<String, String>();
			ChannleMerchantConfigKey keyinfo = clientCollectionPayService
					.getChannelConfigKey(originalInfo.getPid());
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			result.putAll(JsdsUtil.beanToMap(payRequest));
			String paramSrc = RequestUtils.getParamSrc(result);
			log.info("签名前数据**********九派支付:" + paramSrc);
			String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
			payRequest.setSign(md5);
			
			String result1=HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(payRequest));
			log.info("下游返回状态" + result1);
			if (!"SUCCESS".equals(result1)) {
				ThreadPool.executor(new UtilThread(originalInfo
						.getBgUrl(), queryUtil
						.bean2QueryStr(payRequest)));
			}
			
		}else {
			map.put("result", "FAILED");
		}
		
		
			outString(response, map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="returnUrl")
	public void returnUrl(HttpServletRequest request,HttpServletResponse response){
		try {
		String orderId=request.getParameter("orderId");
		String charset=request.getParameter("charset");
		String version=request.getParameter("version");
		String merchanId=request.getParameter("merchanId");
		String payType=request.getParameter("payType");
		String signType=request.getParameter("signType");
		String serverCert=request.getParameter("serverCert");
		String serverSign=request.getParameter("serverSign");
		String memberId=request.getParameter("memberId");
		String amount=request.getParameter("amount");
		String orderTime=request.getParameter("orderTime");
		String orderSts=request.getParameter("orderSts");
		String bankAbbr=request.getParameter("bankAbbr");
		String payTime=request.getParameter("payTime");
		String acDate=request.getParameter("acDate");
		String fee=request.getParameter("fee");
		log.info("九派同步返回参数：orderId="+orderId+",charset="+charset+",version="+version+",merchanId="+merchanId
				+",payType="+payType+",signType="+signType+",serverCert="+serverCert+",serverSign="+serverSign
				+",memberId="+memberId+",amount="+amount+",orderTime="+orderTime+",orderSts="+orderSts+",bankAbbr="+bankAbbr
				+",payTime="+payTime+",acDate="+acDate+",fee="+fee);
		OriginalOrderInfo originalInfo = null;
		if (orderId != null && orderId!= "") {
			originalInfo = this.ihfbService.getOriginOrderInfo(orderId);
		}
		log.info("订单数据:" + JSON.toJSON(originalInfo));
		Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
		log.info("下游的异步地址" + originalInfo.getBgUrl());
			//返回参数
		PayRequest payRequest =new PayRequest();
		payRequest.setMerchantId(originalInfo.getPid());
		payRequest.setAmount(amount);
		payRequest.setOrderId(orderId);
		payRequest.setRespCode("00");
		payRequest.setRespMsg("支付成功");
		//和下面的签名
		//---------------------------------------------------
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(originalInfo.getPid());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(payRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据**********九派支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		payRequest.setSign(md5);
		String path=queryUtil.bean2QueryStr(payRequest);
		response.sendRedirect(originalInfo.getPageUrl()+"?"+path);
		//String result1=HttpURLConection.httpURLConnectionPOST(originalInfo.getPageUrl(),queryUtil.bean2QueryStr(payRequest));
		//log.info("下游返回状态" + result1);
		//if (!"SUCCESS".equals(result1)) {
			//ThreadPool.executor(new UtilThread(originalInfo
			//		.getPageUrl(), queryUtil
			//		.bean2QueryStr(payRequest)));
		//}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@RequestMapping(value="responseUrl")
	public void responseUrl(HttpServletRequest request,HttpServletResponse response) {
		try {
		String charset=request.getParameter("charset");
		String version=request.getParameter("version");
		String merchantId=request.getParameter("merchantId");
		String txTyp=request.getParameter("txTyp");
		String orderNo=request.getParameter("orderNo");
		String accType=request.getParameter("accType");
		String accNo=request.getParameter("accNo");
		String accName=request.getParameter("accName");
		String stlMercId=request.getParameter("stlMercId");
		String amount=request.getParameter("amount");
		String fee=request.getParameter("fee");
		String crdType=request.getParameter("crdType");
		String lBnkNo=request.getParameter("lBnkNo");
		String lBankNam=request.getParameter("lBankNam");
		String idType=request.getParameter("idType");
		String idInfo=request.getParameter("idInfo");
		String valiPeriod=request.getParameter("valiPeriod");
		String cvv2=request.getParameter("cvv2");
		String cellPhone=request.getParameter("cellPhone");
		String txDesc=request.getParameter("txDesc");
		String bnkRsv=request.getParameter("bnkRsv");
		String orderSts=request.getParameter("orderSts");
		
		log.info("九派代付返回参数：");
		log.info("charset"+charset);
		log.info("version"+version);
		log.info("merchantId"+merchantId);
		log.info("txTyp"+txTyp);
		log.info("orderNo"+orderNo);
		log.info("accType"+accType);
		log.info("accNo"+accNo);
		log.info("accName"+accName);
		log.info("stlMercId"+stlMercId);
		log.info("amount"+amount);
		log.info("fee"+fee);
		log.info("crdType"+crdType);
		log.info("lBnkNo"+lBnkNo);
		log.info("lBankNam"+lBankNam);
		log.info("idType"+idType);
		log.info("idInfo"+idInfo);
		log.info("valiPeriod"+valiPeriod);
		log.info("cvv2"+cvv2);
		log.info("cellPhone"+cellPhone);
		log.info("txDesc"+txDesc);
		log.info("bnkRsv"+bnkRsv);
		log.info("状态"+orderSts);
		String str="";
		PayRequest payRequest =new PayRequest();
		
		if(orderNo !=null && orderNo !="") {
			str="result=SUCCESS";
			outString(response,str);
			OriginalOrderInfo originalInfo = null;
			originalInfo = this.ihfbService.getOriginOrderInfo(orderNo);
			log.info("订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			log.info("下游的异步地址" + originalInfo.getBgUrl());
			payRequest.setAmount(amount);
			payRequest.setMerchantId(originalInfo.getPid());
			payRequest.setOrderId(orderNo);
			if("S".equals(orderSts)) {
				int i =service.UpdateDaifu(orderNo, "00");
				log.info("ii:"+i);
				payRequest.setRespCode("00");
				payRequest.setRespMsg("代付成功");
				log.info("2222:"+payRequest.getRespMsg());
				log.info("来了111");
			}else if("F".equals(orderSts)) {
				service.UpdateDaifu(orderNo, "01");
				payRequest.setRespCode("01");
				payRequest.setRespMsg("代付失败");
			}
			log.info("来了222");
			log.info("来了333："+payRequest);
			TreeMap<String, String> result = new TreeMap<String, String>();
			ChannleMerchantConfigKey keyinfo = clientCollectionPayService
					.getChannelConfigKey(originalInfo.getPid());
			// 获取商户秘钥
			log.info("来了444:"+keyinfo);
			String key = keyinfo.getMerchantkey();
			result.putAll(JsdsUtil.beanToMap(payRequest));
			String paramSrc = RequestUtils.getParamSrc(result);
			log.info("签名前数据**********九派代付异步:" + paramSrc);
			String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
			payRequest.setSign(md5);
			
			String result1=HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),queryUtil.bean2QueryStr(payRequest));
			log.info("下游返回状态" + result1);
			if (!"SUCCESS".equals(result1)) {
				ThreadPool.executor(new UtilThread(originalInfo
						.getBgUrl(), queryUtil
						.bean2QueryStr(payRequest)));
			}
		}else {
			str="result=FAILED";
			outString(response, str);
		}
		
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}

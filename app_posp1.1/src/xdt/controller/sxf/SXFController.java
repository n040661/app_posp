package xdt.controller.sxf;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import xdt.controller.BaseAction;
import xdt.dto.sxf.PayRequsest;
import xdt.dto.sxf.SXFRequest;
import xdt.dto.sxf.SXFResponse;
import xdt.dto.sxf.SXFUtil;
import xdt.dto.sxf.SxfThread;
import xdt.dto.tfb.TFBConfig;
import xdt.dto.tfb.WxPayApplyResponse;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IClientCollectionPayService;
import xdt.service.IClientH5Service;
import xdt.service.ISxfService;
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
@RequestMapping("/SXFController")
public class SXFController extends BaseAction {

	Logger log = Logger.getLogger(this.getClass());

	@Resource
	private IClientCollectionPayService clientCollectionPayService;

	@Resource
	private HfQuickPayService payService;

	@Resource
	private IClientH5Service ClientH5ServiceImpl;
	@Resource
	private ISxfService sxfServiceImpl;

	@RequestMapping(value = "cardPayParameter")
	public void cardPayParameter(HttpServletResponse response,
			SXFRequest sxfRequest) {

		log.info("网关支付获取参数" + JSON.toJSON(sxfRequest));
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(sxfRequest.getMercNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(sxfRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名:" + md5);
		try {
			outString(response, md5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@RequestMapping(value = "paySign")
	public void paySign(HttpServletResponse response,
			PayRequsest payRequsest) {

		log.info("网关支付获取参数" + JSON.toJSON(payRequsest));
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(payRequsest.getClientId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(payRequsest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名:" + md5);
		try {
			outString(response, md5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@RequestMapping(value = "cardPay")
	public void cardPay(HttpServletResponse response, SXFRequest sxfRequest) {
		
		log.info("支付参数:" + JSON.toJSON(sxfRequest));
		Map<String, String> results = new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(sxfRequest.getMercNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(sxfRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		boolean b = MD5Utils.verify(paramSrc, sxfRequest.getSign(), key,
				"UTF-8");
		if (b) {
			log.info("签名成功");
			// 写逻辑
			sxfRequest.setUrl(sxfRequest.getNotifyUrl());
			sxfRequest.setReUrl(sxfRequest.getRetUrl());
			results = sxfServiceImpl.cardPay(sxfRequest, results);
			log.info("results" + results);
		} else {
			log.info("签名错误！");
			results.put("retcode", "1");
			results.put("retmsg", "签名错误");
		}

		try {
			outString(response, JSON.toJSON(results));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value="pay")
	public void pay(HttpServletResponse response,PayRequsest payRequsest) throws Exception{
		
		log.info("支付参数:" + JSON.toJSON(payRequsest));
		Map<String, String> results = new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(payRequsest.getClientId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(payRequsest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据："+paramSrc);
		boolean b = MD5Utils.verify(paramSrc, payRequsest.getSign(), key,
				"UTF-8");
		if (b) {
			log.info("签名成功");
			// 写逻辑
			payRequsest.setPayAmt(Double.parseDouble(payRequsest.getPayAmt())*100+"");
			results = sxfServiceImpl.pay(payRequsest, results);
			
			log.info("results" + results);
		} else {
			log.info("签名错误！");
			results.put("retcode", "1");
			results.put("retmsg", "签名错误");
		}

		try {
			outString(response, JSON.toJSON(results));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@RequestMapping(value = "paySelect")
	public void paySelect(HttpServletResponse response, SXFRequest sxfRequest) {

		log.info("查询上传参数:" + JSON.toJSON(sxfRequest));
		Map<String, String> results = new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(sxfRequest.getMercNo());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> result = new TreeMap<String, String>();
		result.putAll(JsdsUtil.beanToMap(sxfRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		try {
			boolean b = MD5Utils.verify(paramSrc, sxfRequest.getSign(), key,
					"UTF-8");

			if (b) {
				log.info("签名成功");

				results = sxfServiceImpl.paySelect(sxfRequest, results);
				log.info("results：" + results);
			} else {
				log.info("签名错误！");
				results.put("retcode", "1");
				results.put("retmsg", "签名错误");
			}

			outString(response, JSON.toJSON(results));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 异步通知返回的地址
	 * 
	 * @param response
	 * @param cardPayApplyＲesponse
	 * @throws Exception
	 */
	@RequestMapping(value = "notifyUrl")
	public void notifyUrl(HttpServletResponse response,
			HttpServletRequest request)
			{
		try {
			log.info("异步通知来了");
			 String _t =new String(request.getParameter("_t").getBytes("ISO-8859-1"),"UTF-8");
			//String _t = sxfResponse.get_t();
			 
			 SXFResponse sxfResponse =new SXFResponse();
			log.info("异步通知数据:" + _t);
			String str;
			Map<String, String> map = new HashMap<>();
			map = JsonUtil.jsonToMap(_t);
			if (_t != null && _t != "") {
				str = "SUCCESS";
				outString(response, str);
				if ("000000".equals(map.get("resCode"))) {
					OriginalOrderInfo originalInfo = null;
					if (map.get("orderNo") != null && map.get("orderNo") != "") {
						originalInfo = this.payService.getOriginOrderInfo(map
								.get("orderNo"));
					}
					log.info("订单数据:" + JSON.toJSON(originalInfo));
					Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
					log.info("下游的异步地址" + originalInfo.getBgUrl());
					log.info("随行付异步回调参数:" + JSON.toJSON(map));
					// 后面要改这个方法没有支付中这个判断
					String stsr = PaymentUtils.decrypt(map.get("resData"),
							SXFUtil.mercPrivateKey);
					log.info("stsr：" + JSON.toJSON(stsr));
					// SXFResponse sxfResponse=(SXFResponse)
					// JsonUtil.jsonToMap(stsr);
					System.out.println(JSON.toJSON(stsr));
					sxfResponse = (SXFResponse) BeanToMapUtil.convertMap(
							SXFResponse.class, JsonUtil.jsonToMap(stsr));
					sxfResponse.setMercNo(originalInfo.getPid());
					log.info("随行付异步返回解析参数"+JSON.toJSON(sxfResponse));
					sxfServiceImpl.update(sxfResponse);
					String result1=HttpUtil.sendPost(originalInfo.getBgUrl()+"?"+queryUtil.bean2QueryStr(sxfResponse));
					log.info("下游返回状态" + result1);
					if (!"SUCCESS".equals(result1)) {
						ThreadPool.executor(new UtilThread(originalInfo
								.getBgUrl(), queryUtil
								.bean2QueryStr(sxfResponse)));
					}
				} else {
					log.info("交易错误码:" + sxfResponse.getResCode() + ",错误信息:"
							+ sxfResponse.getResMsg());
				}

			} else {
				str = "FAIL";
				outString(response, str);
			}
		} catch (Exception e) {
			log.info("随行付异步回调异常:" + e);
			e.printStackTrace();
		}

	}

	/**
	 * 同步返回的地址
	 * 
	 * @param response
	 * @param cardPayApplyＲesponse
	 */
	@RequestMapping(value = "returnUrl")
	public void returnUrl(HttpServletResponse response, SXFResponse sxfResponse) {

		log.info("同步数据来了！！");
	}
}

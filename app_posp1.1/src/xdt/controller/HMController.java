package xdt.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.dao.IPmsMerchantInfoDao;
import xdt.dto.hfb.HfbRequest;
import xdt.dto.hm.AesEncryption;
import xdt.dto.hm.HMRequest;
import xdt.dto.hm.HMResponse;
import xdt.dto.hm.HMUtil;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsMerchantInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.schedule.ThreadPool;
import xdt.service.IClientCollectionPayService;
import xdt.service.IHFBService;
import xdt.service.IHMService;
import xdt.util.HttpURLConection;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/HMController")
public class HMController extends BaseAction {

	Logger log = Logger.getLogger(this.getClass());

	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource
	private IHMService service;
	@Resource
	private IHFBService ihfbService;
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	/**
	 * 签名
	 * 
	 * @param hfbRequest
	 * @param response
	 */
	@RequestMapping(value = "paySign")
	public void paySign(HMRequest hmRequest, HttpServletResponse response) {

		log.info("--签名发来的参数：" + JSON.toJSONString(hmRequest));

		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hmRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.putAll(JsdsUtil.beanToMap(hmRequest));
		String paramSrc = RequestUtils.getParamSrc(result);
		log.info("签名前数据**********恒明支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
		log.info("签名**********恒明支付:" + md5);
		try {
			outString(response, md5);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "shortcutAlipay")
	public void shortcutAlipay(HMRequest hmRequest, HttpServletResponse response) {
		log.info("--标准快捷发来的参数");
		Map<String, String> result = new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hmRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hmRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串" + paramSrc);
		boolean b = MD5Utils
				.verify(paramSrc, hmRequest.getSign(), key, "UTF-8");
		if (b) {
			log.info("签名正确");
			log.info("签名正确");
			hmRequest.setUrl(hmRequest.getNotifyUrl());
			hmRequest.setReUrl(hmRequest.getReturnUrl());
			result = service.shortcutAlipay(hmRequest, result);

			log.info("返回的参数:" + JSON.toJSON(result));
		} else {
			result.put("respCode", "0001");
			result.put("respMsg", "签名错误！");
		}
		results1.putAll(result);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("---返回数据签名签的数据:" + JSON.toJSONString(paramSrc1));
		String md5 = MD5Utils.sign(paramSrc1, key, "UTF-8");
		result.put("sign", md5);
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "pay")
	public void pay(HMRequest hmRequest, HttpServletResponse response) {

		log.info("------代付上传参数：" + JSON.toJSONString(hmRequest));
		Map<String, String> result = new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hmRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hmRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串" + paramSrc);
		boolean b = MD5Utils
				.verify(paramSrc, hmRequest.getSign(), key, "UTF-8");
		if (b) {
			log.info("签名正确");
			log.info("签名正确");
			hmRequest.setUrl(hmRequest.getNotifyUrl());
			result = service.pay(hmRequest, result);

			log.info("返回的参数:" + JSON.toJSON(result));
		} else {
			result.put("respCode", "0001");
			result.put("respMsg", "签名错误！");
		}
		results1.putAll(result);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("---返回数据签名签的数据:" + JSON.toJSONString(paramSrc1));
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
	public void select(HMRequest hmRequest, HttpServletResponse response){
		Map<String, String> result = new HashMap<>();
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService
				.getChannelConfigKey(hmRequest.getMerchantId());
		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		TreeMap<String, String> results = new TreeMap<String, String>();
		TreeMap<String, String> results1 = new TreeMap<String, String>();
		results.putAll(JsdsUtil.beanToMap(hmRequest));
		String paramSrc = RequestUtils.getParamSrc(results);
		log.info("验证签名前字符串" + paramSrc);
		boolean b = MD5Utils
				.verify(paramSrc, hmRequest.getSign(), key, "UTF-8");
		if (b) {
			log.info("签名正确");
			log.info("签名正确");
			hmRequest.setUrl(hmRequest.getNotifyUrl());
			result = service.select(hmRequest, result);

			log.info("返回的参数:" + JSON.toJSON(result));
		} else {
			result.put("respCode", "0001");
			result.put("respMsg", "签名错误！");
		}
		results1.putAll(result);
		String paramSrc1 = RequestUtils.getParamSrc(results1);
		log.info("---返回数据签名签的数据:" + JSON.toJSONString(paramSrc1));
		String md5 = MD5Utils.sign(paramSrc1, key, "UTF-8");
		result.put("sign", md5);
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "notifyUrl")
	public void notifyUrl(HMResponse hmResponse, HttpServletResponse response) {
		log.info("data:" + hmResponse.getData());
		log.info("timestamp:" + hmResponse.getTimestamp());
		HMRequest hmRequest =new HMRequest();
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		try {
			Map<String, String> result = new HashMap<>();
			Map<String, String> maps = new HashMap<>();// 填金
			String str;
			if (hmResponse != null && hmResponse.getData() != null) {
				str = "success";
				outString(response, str);
				log.info("恒明异步返回解析参数" + JSON.toJSON(hmResponse));
				String dedata = AesEncryption.Desencrypt(hmResponse.getData(),
						HMUtil.aeskey, HMUtil.aeskey);
				log.info("解析参数：" + dedata);
				JSONObject json = JSONObject.parseObject(dedata);
				
				log.info("恒明异步订单号" + json.getString("ordernumber"));
				OriginalOrderInfo originalInfo = null;
				if (json.getString("ordernumber") != null
						&& json.getString("ordernumber") != "") {
					originalInfo = this.ihfbService.getOriginOrderInfo(json
							.getString("ordernumber"));
				}
				hmRequest.setMerchantId(originalInfo.getPid());
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				log.info("下游的异步地址" + originalInfo.getBgUrl());
				log.info("状态："+json.get("respcode").toString());
				json.getInteger("respcode").toString();
				if ("0".equals(json.get("respcode").toString())) {
					service.UpdateDaifu(json.getString("ordernumber"), "00");
					hmRequest.setRespCode("00");
					hmRequest.setRespMsg("代付成功");
					hmRequest.setOrderNumber(originalInfo.getOrderId());
					hmRequest.setAmount(Double.parseDouble(originalInfo.getOrderAmount())*100+"");
				} else if ("9".equals(json.getString("respcode"))) {
					service.UpdateDaifu(json.getString("ordernumber"), "200");
					hmRequest.setRespCode("200");
					hmRequest.setRespMsg("代付中");
					hmRequest.setOrderNumber(originalInfo.getOrderId());
					hmRequest.setAmount(Double.parseDouble(originalInfo.getOrderAmount())*100+"");
				} else {
					log.info("来了3");
					service.UpdateDaifu(json.getString("ordernumber"), "01");
					hmRequest.setRespCode("01");
					hmRequest.setRespMsg("代付失败");
					hmRequest.setOrderNumber(originalInfo.getOrderId());
					hmRequest.setAmount(Double.parseDouble(originalInfo.getOrderAmount())*100+"");
					//Map<String, String> map =new HashMap<>();
					//map.put("payMoney", Double.parseDouble(originalInfo.getOrderAmount())*100+"");
					//map.put("machId", originalInfo.getPid());
					//int i =pmsMerchantInfoDao.updataPay(map);
					//if(i==1){
						log.info("恒明 ----代付失败补款成功!");
						merchantinfo.setMercId(originalInfo.getPid());
						//List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
						//merchantinfo = (PmsMerchantInfo) merchantList.get(0);
						hmRequest.setOrderNumber(originalInfo.getOrderId()+"/A");
						//service.add(hmRequest, merchantinfo, result, "00");
						hmRequest.setOrderNumber(originalInfo.getOrderId());
					//}
					
				}
				// ---------------------------------------------------
				TreeMap<String, String> results = new TreeMap<String, String>();
				ChannleMerchantConfigKey keyinfo = clientCollectionPayService
						.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				results.putAll(JsdsUtil.beanToMap(hmRequest));
				String paramSrc = RequestUtils.getParamSrc(results);
				log.info("签名前数据**********恒明支付:" + paramSrc);
				String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
				result.put("sign", md5);
				String result1 = HttpURLConection.httpURLConnectionPOST(
						originalInfo.getBgUrl(),
						queryUtil.bean2QueryStr(hmRequest));
				// String
				// result1=HttpUtil.sendPost(originalInfo.getBgUrl()+"?"+queryUtil.bean2QueryStr(hfbResponses));
				log.info("下游返回状态" + result1);
				if (!"SUCCESS".equals(result1)) {
					ThreadPool.executor(new UtilThread(originalInfo.getBgUrl(),
							queryUtil.bean2QueryStr(result)));
				}

			} else {
				str = "FAIL";
				outString(response, str);
			}
		} catch (Exception e) {
			log.info("恒明异步回调异常:" + e);
			e.printStackTrace();
		}
		log.info("异步参数1：" + JSON.toJSONString(hmResponse));

	}
	
}

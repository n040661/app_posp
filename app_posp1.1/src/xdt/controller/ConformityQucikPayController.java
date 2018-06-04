package xdt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yeepay.g3.facade.yop.ca.dto.DigitalEnvelopeDTO;
import com.yeepay.g3.facade.yop.ca.enums.CertTypeEnum;
import com.yeepay.g3.frame.yop.ca.DigitalEnvelopeUtils;
import com.yeepay.g3.sdk.yop.utils.InternalConfig;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.dto.quickPay.entity.QueryResponseEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.conformityQucikPay.entity.CallbackEntity;
import xdt.quickpay.conformityQucikPay.entity.ConformityQucikPayRequestEntity;
import xdt.quickpay.conformityQucikPay.entity.ConformityQucikPayResponseEntity;
import xdt.quickpay.conformityQucikPay.entity.ConformityQuickPayQueryRequestEntity;
import xdt.quickpay.conformityQucikPay.entity.MBResponse;
import xdt.quickpay.conformityQucikPay.thread.QuickPayThread;
import xdt.quickpay.conformityQucikPay.util.BeanToMapUtil;
import xdt.quickpay.conformityQucikPay.util.EffersonPayService;
import xdt.quickpay.conformityQucikPay.util.HttpClientUtil;
import xdt.quickpay.conformityQucikPay.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.IConformityQucikPayService;
import xdt.util.HttpURLConection;
import xdt.util.RSAUtil;

@Controller
@RequestMapping("conformity")
public class ConformityQucikPayController extends BaseAction {
	
	@Resource
	private IConformityQucikPayService conformityService;

	/**
	 * 快捷(WAP版)支付生成签名
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "wap_pay_sign")
	  public void wapPayScan(ConformityQucikPayRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	  {
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=utf-8");
	    logger.info("下游上送的参数:" + entity);
	    
	    ChannleMerchantConfigKey keyinfo =conformityService.getChannelConfigKey(entity.getV_mid());
	    String merchantKey = keyinfo.getMerchantkey();
	    logger.info("下游商户密钥:" + keyinfo);
	    String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey);
	    entity.setV_sign(sign);
	    
	    request.setCharacterEncoding("UTF-8");
	    request.setAttribute("temp", entity);
	    request.getRequestDispatcher("/quick/quick_conformity_submit.jsp").forward(request, response);
	  }
	/**
	 * 快捷(WAP版)查询生成签名
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "wap_pay__query_sign")
	  public void wapPayQueryScan(ConformityQuickPayQueryRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	  {
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=utf-8");
	    logger.info("下游上送的参数:" + entity);
	    
	    ChannleMerchantConfigKey keyinfo =conformityService.getChannelConfigKey(entity.getV_mid());
	    String merchantKey = keyinfo.getMerchantkey();
	    logger.info("下游商户密钥:" + keyinfo);
	    String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey);
	    entity.setV_sign(sign);
	    
	    request.setCharacterEncoding("UTF-8");
	    request.setAttribute("temp", entity);
	    request.getRequestDispatcher("/quick/quick_conformity_query_submit.jsp").forward(request, response);
	  }
	/**
	 * 快捷(WAP版)支付请求
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	  @ResponseBody
	  @RequestMapping(value = "quickPay/wap/submit")
	  public void PayScanParam(ConformityQucikPayRequestEntity param, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	  {
	    logger.info("############快捷(WAP)支付##################");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=UTF-8");
	    Map<String, String> result = new HashMap();
	    PmsBusinessPos pmsBusinessPos = conformityService.selectKey(param.getV_mid());
	    
	    ChannleMerchantConfigKey keyinfo = conformityService.getChannelConfigKey(param.getV_mid());
	    
	    String merchantKey = keyinfo.getMerchantkey();
	    logger.info("下游上送参数:{}" + param);
	    String html = "";
	    if (!StringUtils.isEmpty(param.getV_mid()))
	    {
	      logger.info("下游上送签名串{}" + param.getV_sign());
	      SignatureUtil signUtil = new SignatureUtil();
	      Map map = BeanToMapUtil.convertBean(param);
	      if (SignatureUtil.checkSign(map, merchantKey))
	      {
	        logger.info("对比签名成功");
	        result = conformityService.payHandle(param);
	        if ("00".equals(result.get("v_code")))
	        {
				switch (pmsBusinessPos.getChannelnum()) {
				case "MBXHF": // 摩宝快捷银联
					result.remove("v_code");
					logger.info("摩宝快捷上送的数据:" + result);
					String params = HttpURLConection.parseParams(result);
					logger.info("摩宝快捷上送的数据:" + params);
					String path = "http://hanyipay.com/ks_netbank/mpay.c?" + params;
					logger.info("demo 重定向：" + path);
					request.getSession();
					response.setCharacterEncoding("GBK");
					response.sendRedirect(path.replace(" ", " "));
					break;
				case "YBLS":// 易宝快捷

					String url = result.get("path");
					logger.info("URL 重定向：" + url);
					// path = url.replace("https://cash.yeepay.com/cashier/std",
					// "http://www.lssc888.com/shop/control/yibao_request.php");
					path = url.replace("https://cash.yeepay.com/cashier/std",
							"http://www.lssc888.com/shop/control/yibao_request_vt3.php");
					logger.info("demo 重定向：" + path);
					request.getSession();
					response.setCharacterEncoding("UTF-8");
					response.sendRedirect(path.replace(" ", " "));
					break;									
				case "YSB":// 银生宝快捷
					result.remove("v_code");
					logger.info("银生宝快捷上送的数据:" + result);
					html = EffersonPayService.createAutoFormHtml(
							"http://wap.unspay.com:8082/quickpay-front/quickPayWap/prePay", result, "UTF-8");
					logger.info("返回结果:{}"+html);
					outString(response, html);
					break;
				case "JS100669"://江苏电商快捷
					result.remove("v_code");
					result.remove("v_msg");
					url = result.get("pl_url");
					logger.info("银江苏电商快捷上送的数据:" + url);
					outString(response, url);
					break;
				default:						
					break;
				}
			}
	        else
	        {
	          ConformityQucikPayResponseEntity consume = (ConformityQucikPayResponseEntity)
	            BeanToMapUtil.convertMap(ConformityQucikPayResponseEntity.class, result);
	          
	          String sign = SignatureUtil.getSign(beanToMap(consume), merchantKey);
	          result.put("v_sign", sign);
	          this.logger.info("返回结果:{}" + result);
	          outString(response, this.gson.toJson(result));
	        }
	      }
	      else
	      {
	        this.logger.error("签名错误!");
	        result.put("v_code", "02");
	        result.put("v_msg", "签名错误!");
	        ConformityQucikPayResponseEntity consume = (ConformityQucikPayResponseEntity)
	          BeanToMapUtil.convertMap(ConformityQucikPayResponseEntity.class, result);
	        
	        String sign = SignatureUtil.getSign(beanToMap(consume), merchantKey);
	        result.put("v_sign", sign);
	        this.logger.info("返回结果:{}" + result);
	        outString(response, this.gson.toJson(result));
	      }
	    }
	    else
	    {
	      this.logger.error("上送交易参数空!");
	      result.put("v_code", "01");
	      result.put("v_msg", "上送交易参数空");
	      ConformityQucikPayResponseEntity consume = (ConformityQucikPayResponseEntity)
	        BeanToMapUtil.convertMap(ConformityQucikPayResponseEntity.class, result);
	      
	      String sign = SignatureUtil.getSign(beanToMap(consume), merchantKey);
	      result.put("v_sign", sign);
	      this.logger.info("返回结果:{}" + result);
	      outString(response, this.gson.toJson(result));
	    }
	  }
		/**
		 * 快捷(WAP版)查询请求
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
	  @RequestMapping(value = "quickPay/wap/query")
		public void query(ConformityQuickPayQueryRequestEntity query,HttpServletRequest request, HttpServletResponse response) throws Exception {
			logger.info("------快捷查询上传参数：" + JSON.toJSONString(query));
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("text/html;charset=utf-8");
			Map<String, String> result = new HashMap<>();
			if (!StringUtils.isEmpty(query.getV_mid())) {

				// 检验数据是否合法
				logger.info("下游上送签名串{}" + query.getV_sign());
				// 查询商户密钥
				ChannleMerchantConfigKey keyinfo = conformityService.getChannelConfigKey(query.getV_mid());
				// ------------------------需要改签名
				String merchantKey = keyinfo.getMerchantkey();
				SignatureUtil signUtil = new SignatureUtil();

				Map map = BeanToMapUtil.convertBean(query);
				if (signUtil.checkSign(map, merchantKey)) {

					logger.info("对比签名成功");
					result = conformityService.quickQuery(query);
					QueryResponseEntity queryconsume = (QueryResponseEntity) BeanToMapUtil
							.convertMap(QueryResponseEntity.class, result);
					logger.info("---返回数据签名签的数据:" + beanToMap(queryconsume));
					String sign = SignatureUtil.getSign(beanToMap(queryconsume), merchantKey);
					logger.info("---返回数据签名:" + sign);
					result.put("v_sign", sign);

				} else {
					logger.error("签名错误!");
					result.put("v_code", "02");
					result.put("v_msg", "签名错误!");
				}

			} else {
				logger.error("上送交易参数空!");
				result.put("v_code", "01");
				result.put("v_msg", "上送交易参数空");
			}
			try {
				outString(response, JSON.toJSONString(result));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.logger.info("向下游 发送数据成功");

		}

	  /**
		 * 易宝同步响应信息
		 *
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "ybReturnUrl")
		public void ybReturnUrl(HttpServletRequest request, HttpServletResponse response) {
			try {
				logger.info("############易宝同步##################");
				String orderId = request.getParameter("orderId");
				logger.info("易宝同步返回的订单号" + orderId);
				OriginalOrderInfo originalInfo = null;
				if (orderId != null && orderId != "") {
					originalInfo = conformityService.getOriginOrderInfo(orderId);
				}
				logger.info("易宝同步原始订单数据:" + JSON.toJSON(originalInfo));
				logger.info("易宝给下游的同步地址" + originalInfo.getPageUrl());
				TreeMap<String, String> result = new TreeMap<String, String>();
				String params = "";
				if (!StringUtils.isEmpty(orderId)) {
					ChannleMerchantConfigKey keyinfo = conformityService.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					result.put("v_oid", originalInfo.getOrderId());
					result.put("v_txnAmt", originalInfo.getOrderAmount());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_time", originalInfo.getOrderTime());
					result.put("v_mid", originalInfo.getPid());
					ConformityQucikPayResponseEntity consumeResponseEntity = (ConformityQucikPayResponseEntity) BeanToMapUtil
							.convertMap(ConformityQucikPayResponseEntity.class, result);
					String sign = SignatureUtil.getSign(beanToMap(consumeResponseEntity), key);
					result.put("v_sign", sign);
					params = HttpClientUtil.parseParams(result);
					logger.info("易宝同步给下游的数据:" + params);
					request.getSession();
					try {
						// 给下游手动返回支付结果
						if (originalInfo.getPageUrl().indexOf("?") == -1) {

							String path = originalInfo.getPageUrl() + "?" + params;
							logger.info("易宝 重定向地址：" + path);

							response.sendRedirect(path.replace(" ", ""));
						} else {
							logger.info("易宝 重定向地址：" + originalInfo.getPageUrl());
							String path = originalInfo.getPageUrl() + "&" + params;
							logger.info("易宝 重定向地址：" + path);
							response.sendRedirect(path.replace(" ", ""));
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				} else {
					logger.info("没有收到易宝的同步数据");
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		/**
		 * 易宝异步响应信息
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "ybNotifyUrl")
		public void ybNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
			try {
				logger.info("############易宝异步##################");
				String appMsg = request.getParameter("response");
				Map<String, String> result = new HashMap<String, String>();
				logger.info("异步异步获取参数：" + appMsg);
				if (!StringUtils.isEmpty(appMsg)) {
					response.getWriter().write("SUCCESS");
					// String[] str = appMsg.split("\\&");
					// logger.info("拆分数据:" + str);
					// String results = str[0].replaceAll("response=", "");
					// String results = appMsg;
					logger.info("易宝解密数据:" + appMsg);
					// 开始解密
					Map<String, String> jsonMap = new HashMap<>();
					DigitalEnvelopeDTO dto = new DigitalEnvelopeDTO();
					dto.setCipherText(appMsg);
					PrivateKey privateKey = InternalConfig.getISVPrivateKey(CertTypeEnum.RSA2048);
					logger.info("privateKey: " + privateKey);
					PublicKey publicKey = InternalConfig.getYopPublicKey(CertTypeEnum.RSA2048);
					logger.info("publicKey: " + publicKey);

					dto = DigitalEnvelopeUtils.decrypt(dto, privateKey, publicKey);
					logger.info("-------:" + dto.getPlainText());
					jsonMap = JSON.parseObject(dto.getPlainText(), new TypeReference<TreeMap<String, String>>() {
					});
					logger.info("易宝异步解析之后的数据:" + jsonMap);

					String status = jsonMap.get("status");
					logger.info("易宝支付异步返回的订单状态:" + jsonMap);
					String orderId = jsonMap.get("orderId");
					logger.info("易宝支付异步返回的订单号:" + orderId);
					OriginalOrderInfo originalInfo = null;
					if (orderId != null && orderId != "") {
						originalInfo = conformityService.getOriginOrderInfo(orderId);
					}
					logger.info("易宝支付异步原始订单交易时间:" + originalInfo.getOrderTime());
					result.put("v_mid", originalInfo.getPid());
					result.put("v_oid", originalInfo.getOrderId());
					result.put("v_txnAmt", originalInfo.getOrderAmount());
					result.put("v_time", originalInfo.getOrderTime());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_attach", originalInfo.getAttach());				
					if ("SUCCESS".equals(status)) {

						result.put("v_payStatus", "0000");
						result.put("v_payMsg", "支付成功");

					} else {
						result.put("v_payStatus", "1001");
						result.put("v_payMsg", "支付失败:"+URLDecoder.decode(request.getParameter("payMsg")));
						logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
								+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
					}
					ChannleMerchantConfigKey keyinfo =conformityService.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					CallbackEntity consume = (CallbackEntity ) BeanToMapUtil
							.convertMap(CallbackEntity.class, result);
					// 修改订单状态
					conformityService.otherInvoke(orderId,result.get("v_payStatus"));
					logger.info("易宝支付异步回调地址:" + originalInfo.getBgUrl());
					// 生成签名
					String sign = SignatureUtil.getSign(beanToMap(consume), key);
					result.put("v_sign", sign);

					logger.info("易宝支付异步封装前参数：" + result);
					CallbackEntity consumeResponseEntity = (CallbackEntity) BeanToMapUtil
							.convertMap(CallbackEntity.class, result);
					logger.info("易宝支付异步封装后参数：" + HttpClientUtil.bean2QueryStr(consumeResponseEntity));
					String html = HttpClientUtil.post(originalInfo.getBgUrl(),
							HttpClientUtil.bean2QueryStr(consumeResponseEntity));
					logger.info("易宝支付下游响应信息:" + html);
					JSONObject ob = JSONObject.fromObject(html);
					Iterator it = ob.keys();
					Map<String, String> map = new HashMap<>();
					while (it.hasNext()) {
						String keys = (String) it.next();
						if (keys.equals("success")) {
							String value = ob.getString(keys);
							logger.info("易宝支付回馈的结果:" + "\t" + value);
							map.put("success", value);
						}
					}
					if (map.get("success").equals("false")) {

						logger.info("易宝支付启动线程进行异步通知");
						// 启线程进行异步通知
						ThreadPool.executor(new QuickPayThread(originalInfo.getBgUrl(), HttpClientUtil.bean2QueryStr(consumeResponseEntity)));
					}
					logger.info("易宝支付向下游 发送数据成功");

				} else {
					response.getWriter().write("FAIL");
					logger.error("回调的参数为空!");
					result.put("v_code", "15");
					result.put("v_msg", "请求失败");
				}
				// outString(response, str);
			} catch (Exception e) {
				logger.info("易宝异步回调异常:" + e);
				e.printStackTrace();
			}
		}
		/**
		 * 摩宝同步响应信息
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "mbReturnUrl")
		public void mbReturnUrl(HttpServletResponse response, MBResponse mbResponse, HttpServletRequest request) {
			
			logger.info("############摩宝同步##################");
			TreeMap<String, String> result = new TreeMap<>();
			try {

				mbResponse.setRefcode(request.getParameter("payStatus"));
				logger.info("摩宝支付异步状态码:" + request.getParameter("payStatus"));
				mbResponse.setOrderId(request.getParameter("orderId"));
				logger.info("摩宝支付异步订单号:" + request.getParameter("orderId"));
				mbResponse.setRefMsg(URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
				if(!StringUtils.isEmpty(request.getParameter("orderId"))) {
					// outString(response, str);
					OriginalOrderInfo originalInfo = null;
					if (mbResponse.getOrderId() != null && mbResponse.getOrderId() != "") {
						originalInfo = conformityService.getOriginOrderInfo(mbResponse.getOrderId());
					}
					logger.info("摩宝同步原始订单数据:" + JSON.toJSON(originalInfo));
					logger.info("摩宝给下游的同步地址" + originalInfo.getPageUrl());
					ChannleMerchantConfigKey keyinfo = conformityService.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					result.put("v_oid", originalInfo.getOrderId());
					result.put("v_txnAmt", originalInfo.getOrderAmount());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_time", originalInfo.getOrderTime());
					result.put("v_mid", originalInfo.getPid());
					ConformityQucikPayResponseEntity consumeResponseEntity = (ConformityQucikPayResponseEntity) BeanToMapUtil
							.convertMap(ConformityQucikPayResponseEntity.class, result);
					String sign = SignatureUtil.getSign(beanToMap(consumeResponseEntity), key);
					result.put("v_sign", sign);
					String params = HttpClientUtil.parseParams(result);
					logger.info("摩宝同步给下游的数据:" + params);
					request.getSession();
						// 给下游手动返回支付结果
						if (originalInfo.getPageUrl().indexOf("?") == -1) {

							String path = originalInfo.getPageUrl() + "?" + params;
							logger.info("摩宝 重定向地址：" + path);

							response.sendRedirect(path.replace(" ", ""));
						} else {
							logger.info("摩宝 重定向地址：" + originalInfo.getPageUrl());
							String path = originalInfo.getPageUrl() + "&" + params;
							logger.info("摩宝 重定向地址：" + path);
							response.sendRedirect(path.replace(" ", ""));
						}

				} else {
					logger.info("没有收到魔宝的同步数据");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * 摩宝异步响应信息
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "mbNotifyUrl")
		public void mbNotifyUrl(HttpServletResponse response, MBResponse mbResponse, HttpServletRequest request) {
			
			logger.info("############摩宝异步##################");
			String str;
			TreeMap<String, String> result = new TreeMap<>();
			try {

				mbResponse.setRefcode(request.getParameter("payStatus"));
				logger.info("摩宝支付异步状态码:" + request.getParameter("payStatus"));
				mbResponse.setOrderId(request.getParameter("orderId"));
				logger.info("摩宝支付异步订单号:" + request.getParameter("orderId"));
				mbResponse.setRefMsg(URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
				if (!StringUtils.isEmpty(request.getParameter("orderId"))) {
					response.getWriter().write("OK");
					OriginalOrderInfo originalInfo = null;
					if (mbResponse.getOrderId() != null && mbResponse.getOrderId() != "") {
						originalInfo = conformityService.getOriginOrderInfo(mbResponse.getOrderId());
					}
					logger.info("摩宝支付异步原始订单信息:" + originalInfo.getOrderTime());
					logger.info("摩宝支付异步回调地址:" + originalInfo.getBgUrl());
					result.put("v_mid", originalInfo.getPid());
					result.put("v_oid", originalInfo.getOrderId());
					result.put("v_userId", originalInfo.getUserId());
					result.put("v_txnAmt", originalInfo.getOrderAmount());
					result.put("v_time", originalInfo.getOrderTime());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_attach", originalInfo.getAttach());				
					if ("00".equals(request.getParameter("payStatus"))) {

						result.put("v_payStatus", "0000");
						result.put("v_payMsg", "支付成功");
					} else {
						result.put("v_payStatus", "1001");
						result.put("v_payMsg", "支付失败:"+URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
						logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
								+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
					}
					ChannleMerchantConfigKey keyinfo = conformityService.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					CallbackEntity consume = (CallbackEntity ) BeanToMapUtil
							.convertMap(CallbackEntity.class, result);
					// 修改订单状态
					conformityService.otherInvoke(request.getParameter("orderId"),result.get("v_payStatus"));
				 	logger.info("摩宝支付异步回调地址:" + originalInfo.getBgUrl());
					// 生成签名
					String sign = SignatureUtil.getSign(beanToMap(consume), key);
					result.put("v_sign", sign);

					logger.info("摩宝支付异步封装前参数：" + result);
					CallbackEntity consumeResponseEntity = (CallbackEntity) BeanToMapUtil
							.convertMap(CallbackEntity.class, result);
					logger.info("摩宝支付异步封装后参数：" + HttpClientUtil.bean2QueryStr(consumeResponseEntity));
					String html = HttpClientUtil.post(originalInfo.getBgUrl(),
							HttpClientUtil.bean2QueryStr(consumeResponseEntity));
					logger.info("摩宝支付下游响应信息:" + html);
					JSONObject ob = JSONObject.fromObject(html);
					Iterator it = ob.keys();
					Map<String, String> map = new HashMap<>();
					while (it.hasNext()) {
						String keys = (String) it.next();
						if (keys.equals("success")) {
							String value = ob.getString(keys);
							logger.info("摩宝支付回馈的结果:" + "\t" + value);
							map.put("success", value);
						}
					}
					if (map.get("success").equals("false")) {

						logger.info("摩宝支付启动线程进行异步通知");
						// 启线程进行异步通知
						ThreadPool.executor(new QuickPayThread(originalInfo.getBgUrl(), HttpClientUtil.bean2QueryStr(consumeResponseEntity)));
					}
					logger.info("摩宝支付向下游 发送数据成功");
				} else {
					logger.error("回调的参数为空!");
					result.put("v_code", "15");
					result.put("v_msg", "请求失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		/**
		 * 银生宝同步响应信息
		 *
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "ysbReturnUrl")
		public void ysbReturnUrl(HttpServletRequest request, HttpServletResponse response) {
			try {
				logger.info("############银生宝同步##################");
				String orderId = request.getParameter("orderNo");
				String userId = request.getParameter("userId");
				logger.info("银生宝同步返回的订单号:" + JSON.toJSON(orderId));
				logger.info("银生宝同步返回的用户ID:" + userId);
				OriginalOrderInfo originalInfo = null;
				if (orderId != null && orderId != "") {
					originalInfo = conformityService.getOriginOrderInfo(orderId);
				}
				logger.info("银生宝同步原始订单数据:" + JSON.toJSON(originalInfo));
				logger.info("银生宝给下游的同步地址" + originalInfo.getPageUrl());
				TreeMap<String, String> result = new TreeMap<String, String>();
				String params = "";
				if (!StringUtils.isEmpty(orderId)) {
					ChannleMerchantConfigKey keyinfo = conformityService.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					result.put("v_oid", originalInfo.getOrderId());
					result.put("v_txnAmt", originalInfo.getOrderAmount());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_time", originalInfo.getOrderTime());
					result.put("v_mid", originalInfo.getPid());
					ConformityQucikPayResponseEntity consumeResponseEntity = (ConformityQucikPayResponseEntity) BeanToMapUtil
							.convertMap(ConformityQucikPayResponseEntity.class, result);
					String sign = SignatureUtil.getSign(beanToMap(consumeResponseEntity), key);
					result.put("v_sign", sign);
					params = HttpClientUtil.parseParams(result);
					logger.info("银生宝同步给下游的数据:" + params);
					request.getSession();
					try {
						// 给下游手动返回支付结果
						if (originalInfo.getPageUrl().indexOf("?") == -1) {

							String path = originalInfo.getPageUrl() + "?" + params;
							logger.info("银生宝 重定向地址：" + path);

							response.sendRedirect(path.replace(" ", ""));
						} else {
							logger.info("银生宝 重定向地址：" + originalInfo.getPageUrl());
							String path = originalInfo.getPageUrl() + "&" + params;
							logger.info("银生宝 重定向地址：" + path);
							response.sendRedirect(path.replace(" ", ""));
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				} else {
					logger.info("没有收到银生宝的同步数据");
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		/**
		 * 银生宝支付异步响应信息
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "ysbNotifyUrl")
		public void ysbNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
			try {
				logger.info("###########银生宝支付异步#############");
				String result_code = request.getParameter("result_code");
				String result_msg = request.getParameter("result_msg");
				String orderNo = request.getParameter("orderNo");
				String userId = request.getParameter("userId");
				logger.info("银生宝支付异步响应用户ID：" + userId);
				logger.info("银生宝支付异步响应订单号：" + orderNo);
				logger.info("银生宝支付异步响应描述：" + result_msg);
				logger.info("银生宝支付异步响应状态码：" + result_code);
				OriginalOrderInfo originalInfo = null;

				Map<String, String> result = new HashMap<String, String>();
				if (!StringUtils.isEmpty(orderNo)) {
					// 查询原始订单信息
					if (orderNo != null && orderNo != "") {
						originalInfo = conformityService.getOriginOrderInfo(orderNo);
					}
					logger.info("银生宝支付异步原订单数据:" + JSON.toJSON(originalInfo));
					result.put("v_mid", originalInfo.getPid());
					result.put("v_userId", userId);
					result.put("v_oid", originalInfo.getOrderId());
					result.put("v_txnAmt", originalInfo.getOrderAmount());
					result.put("v_time", originalInfo.getOrderTime());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_attach", originalInfo.getAttach());
					if ("0000".equals(result_code)) {

						result.put("v_payStatus", "0000");
						result.put("v_payMsg", "支付成功");

					} else {
						result.put("v_payStatus", "1001");
						result.put("v_payMsg", "支付失败:"+request.getParameter("result_msg"));
						logger.info("交易错误码:" + request.getParameter("result_code") + ",错误信息:"+request.getParameter("result_msg"));
					}
					ChannleMerchantConfigKey keyinfo = conformityService.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					CallbackEntity consume = (CallbackEntity) BeanToMapUtil
							.convertMap(CallbackEntity.class, result);
					// 修改订单状态
					conformityService.otherInvoke(orderNo,result.get("v_payStatus"));

					// 生成签名
					String sign = SignatureUtil.getSign(beanToMap(consume), key);
					result.put("v_sign", sign);

					logger.info("银生宝异步之前的参数：" + result);
					CallbackEntity consumeResponseEntity = (CallbackEntity) BeanToMapUtil
							.convertMap(CallbackEntity.class, result);
					logger.info("银生宝支付异步封装后参数：" + HttpClientUtil.bean2QueryStr(consumeResponseEntity));
					String html = HttpClientUtil.post(originalInfo.getBgUrl(),
							HttpClientUtil.bean2QueryStr(consumeResponseEntity));
					logger.info("银生宝支付下游响应信息:" + html);
					JSONObject ob = JSONObject.fromObject(html);
					Iterator it = ob.keys();
					Map<String, String> map = new HashMap<>();
					while (it.hasNext()) {
						String keys = (String) it.next();
						if (keys.equals("success")) {
							String value = ob.getString(keys);
							logger.info("银生宝支付回馈的结果:" + "\t" + value);
							map.put("success", value);
						}
					}
					if (map.get("success").equals("false")) {

						logger.info("银生宝支付启动线程进行异步通知");
						// 启线程进行异步通知
						ThreadPool.executor(new QuickPayThread(originalInfo.getBgUrl(), HttpClientUtil.bean2QueryStr(consumeResponseEntity)));
					}
					logger.info("银生宝支付向下游 发送数据成功");

				} else {
					logger.error("回调的参数为空!");
					result.put("v_code", "15");
					result.put("v_msg", "请求失败");
				}
				outString(response, gson.toJson(result));
			} catch (Exception e) {
				logger.info("银生宝异步回调异常:" + e);
				e.printStackTrace();
			}
		}
		/**
		 * 江苏电商快捷同步响应信息
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "jsReturnUrl")
		public void jsReturnUrl(HttpServletRequest request, HttpServletResponse response) {
			try {
				response.setHeader("content-type","text/html;charset=utf-8");
				response.setContentType("text/html;charset=utf-8");
				logger.info("###########江苏电商支付同步#############");
				String pl_sign=request.getParameter("pl_sign");
                logger.info("江苏电商同步返回的签名:"+pl_sign);
				String baseSign= URLDecoder.decode(pl_sign, "UTF-8");

				baseSign = baseSign.replace(" ", "+");

				byte[] a = RSAUtil.verify("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSUnSUG5I3Xh2ANLpC5xLe96WCVQG+A5iPBKPqRKBcF2OCdCtwNs8X40nyqYnVWqhkZwGiItT4+wFc04boL1Az01UJiZBLqmOumU0mxyyKCqGwFZakl3LWI4u2IBDuwyde3muXZDWtSDBH1k2BKzOHju3eeSicZu5D7SQ1Hol7AwIDAQAB",RSAUtil.base64Decode(baseSign));;

				String Str = new String(a);

				logger.info("江苏电商解析之后的数据:" + Str);
				String[] array = Str.split("\\&");

				logger.info("江苏电商拆分数据:" + array);
				String[] list = array[0].split("\\=");
				String orderNum = array[0].replace("orderNum=", "");
				String pl_orderNum = array[1].replace("pl_orderNum=", "");
				String pl_payState = array[2].replace("pl_payState=", "");
				String pl_payMessage = array[3].replace("pl_payMessage=", "");
				logger.info("江苏电商支付同步响应用户ID：" + pl_orderNum);
				logger.info("江苏电商支付同步响应订单号：" + orderNum);
				logger.info("江苏电商支付同步响应描述：" + pl_payMessage);
				logger.info("江苏电商支付同步响应状态码：" + pl_payState);
				OriginalOrderInfo originalInfo = null;
				if (orderNum != null && orderNum != "") {
					originalInfo = conformityService.getOriginOrderInfo(orderNum);
				}
				logger.info("江苏电商同步原始订单数据:" + JSON.toJSON(originalInfo));
				logger.info("江苏电商给下游的同步地址" + originalInfo.getPageUrl());
				TreeMap<String, String> result = new TreeMap<String, String>();
				String params = "";
				if (!StringUtils.isEmpty(orderNum)) {
					ChannleMerchantConfigKey keyinfo = conformityService.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					result.put("v_oid", originalInfo.getOrderId());
					result.put("v_txnAmt", originalInfo.getOrderAmount());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_time", originalInfo.getOrderTime());
					result.put("v_mid", originalInfo.getPid());
					ConformityQucikPayResponseEntity consumeResponseEntity = (ConformityQucikPayResponseEntity) BeanToMapUtil
							.convertMap(ConformityQucikPayResponseEntity.class, result);
					String sign = SignatureUtil.getSign(beanToMap(consumeResponseEntity), key);
					result.put("v_sign", sign);
					params = HttpClientUtil.parseParams(result);
					logger.info("江苏电商同步给下游的数据:" + params);
					request.getSession();
					try {
						// 给下游手动返回支付结果
						if (originalInfo.getPageUrl().indexOf("?") == -1) {

							String path = originalInfo.getPageUrl() + "?" + params;
							logger.info("江苏电商重定向地址：" + path);

							response.sendRedirect(path.replace(" ", ""));
						} else {
							logger.info("江苏电商 重定向地址：" + originalInfo.getPageUrl());
							String path = originalInfo.getPageUrl() + "&" + params;
							logger.info("江苏电商 重定向地址：" + path);
							response.sendRedirect(path.replace(" ", ""));
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				} else {
					logger.info("没有收到江苏电商的同步数据");
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		/**
		 * 江苏电商支付异步响应信息
		 * 
		 * @param request
		 * @param response
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping(value = "jsNotifyUrl")
		public void jsNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
			try {
				logger.info("#############江苏电商支付异步##############");
				TreeMap<String, String> result = new TreeMap<>();
				String pl_sign=request.getParameter("pl_sign");
				String baseSign= URLDecoder.decode(pl_sign, "UTF-8");

				baseSign = baseSign.replace(" ", "+");

				byte[] a = RSAUtil.verify("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSUnSUG5I3Xh2ANLpC5xLe96WCVQG+A5iPBKPqRKBcF2OCdCtwNs8X40nyqYnVWqhkZwGiItT4+wFc04boL1Az01UJiZBLqmOumU0mxyyKCqGwFZakl3LWI4u2IBDuwyde3muXZDWtSDBH1k2BKzOHju3eeSicZu5D7SQ1Hol7AwIDAQAB",RSAUtil.base64Decode(baseSign));

				String Str = new String(a);

				System.out.println("江苏电商解析之后的数据:" + Str);
				String[] array = Str.split("\\&");

				System.out.println("江苏电商拆分数据:" + array);
				String[] list = array[0].split("\\=");
				String orderNum = array[0].replace("orderNum=", "");
				String pl_orderNum = array[1].replace("pl_orderNum=", "");
				String pl_payState = array[2].replace("pl_payState=", "");
				String pl_payMessage = array[3].replace("pl_payMessage=", "");
				logger.info("江苏电商支付异步响应用户ID：" + pl_orderNum);
				logger.info("江苏电商支付异步响应订单号：" + orderNum);
				logger.info("江苏电商支付异步响应描述：" + pl_payMessage);
				logger.info("江苏电商支付异步响应状态码：" + pl_payState);
				OriginalOrderInfo originalInfo = null;

				if (!StringUtils.isEmpty(orderNum)) {
					response.getWriter().write("SUCCESS");
					if (orderNum != null && orderNum != "") {
						originalInfo = conformityService.getOriginOrderInfo(orderNum);
					}
					logger.info("江苏电商支付异步原始订单信息:" + originalInfo.getOrderTime());
					logger.info("江苏电商支付异步回调地址:" + originalInfo.getBgUrl());
					result.put("v_mid", originalInfo.getPid());
					result.put("v_oid", originalInfo.getOrderId());
					result.put("v_txnAmt", originalInfo.getOrderAmount());
					result.put("v_time", originalInfo.getOrderTime());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_attach", originalInfo.getAttach());				
					if ("4".equals(pl_payState)) {

						result.put("v_payStatus", "0000");
						result.put("v_payMsg", "支付成功");
						int i = conformityService.updatePmsMerchantInfo80(originalInfo);
						if (i > 0) {
							logger.info("江苏电商*****实时入金完成");
						} else {
							logger.info("江苏电商*****实时入金失败");
						}

					} else {
						result.put("v_payStatus", "1001");
						result.put("v_payMsg", "支付失败:"+pl_payMessage);
						logger.info("交易错误码:" + pl_payMessage + ",错误信息:"
								+ pl_payMessage);
					}
					ChannleMerchantConfigKey keyinfo = conformityService.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					String key = keyinfo.getMerchantkey();
					CallbackEntity consume = (CallbackEntity ) BeanToMapUtil
							.convertMap(CallbackEntity.class, result);
					// 修改订单状态
					conformityService.otherInvoke(orderNum ,result.get("v_payStatus"));
					logger.info("江苏电商支付异步回调地址:" + originalInfo.getBgUrl());
					// 生成签名
					String sign = SignatureUtil.getSign(beanToMap(consume), key);
					result.put("v_sign", sign);

					logger.info("江苏电商支付异步封装前参数：" + result);
					CallbackEntity consumeResponseEntity = (CallbackEntity) BeanToMapUtil
							.convertMap(CallbackEntity.class, result);
					logger.info("江苏电商支付异步封装后参数：" + HttpClientUtil.bean2QueryStr(consumeResponseEntity));
					String html = HttpClientUtil.post(originalInfo.getBgUrl(),
							HttpClientUtil.bean2QueryStr(consumeResponseEntity));
					logger.info("江苏电商支付下游响应信息:" + html);
					JSONObject ob = JSONObject.fromObject(html);
					Iterator it = ob.keys();
					Map<String, String> map = new HashMap<>();
					while (it.hasNext()) {
						String keys = (String) it.next();
						if (keys.equals("success")) {
							String value = ob.getString(keys);
							logger.info("江苏电商支付回馈的结果:" + "\t" + value);
							map.put("success", value);
						}
					}
					if (map.get("success").equals("false")) {

						logger.info("江苏电商支付启动线程进行异步通知");
						// 启线程进行异步通知
						ThreadPool.executor(new QuickPayThread(originalInfo.getBgUrl(), HttpClientUtil.bean2QueryStr(consumeResponseEntity)));
					}
					logger.info("江苏电商支付向下游 发送数据成功");
				} else {
					logger.error("回调的参数为空!");
					result.put("v_code", "15");
					result.put("v_msg", "请求失败");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}

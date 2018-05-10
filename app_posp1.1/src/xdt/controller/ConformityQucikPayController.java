package xdt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.google.gson.Gson;
import com.yeepay.g3.facade.yop.ca.dto.DigitalEnvelopeDTO;
import com.yeepay.g3.facade.yop.ca.enums.CertTypeEnum;
import com.yeepay.g3.frame.yop.ca.DigitalEnvelopeUtils;
import com.yeepay.g3.sdk.yop.utils.InternalConfig;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.conformityQucikPay.entity.CallbackEntity;
import xdt.quickpay.conformityQucikPay.entity.ConformityQucikPayRequestEntity;
import xdt.quickpay.conformityQucikPay.entity.ConformityQucikPayResponseEntity;
import xdt.quickpay.conformityQucikPay.entity.MBResponse;
import xdt.quickpay.conformityQucikPay.thread.QuickPayThread;
import xdt.quickpay.conformityQucikPay.util.BeanToMapUtil;
import xdt.quickpay.conformityQucikPay.util.EffersonPayService;
import xdt.quickpay.conformityQucikPay.util.HttpClientUtil;
import xdt.quickpay.conformityQucikPay.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.IConformityQucikPayService;
import xdt.util.HttpURLConection;

@Controller
@RequestMapping("conformity")
public class ConformityQucikPayController extends BaseAction {
	@Resource
	private IConformityQucikPayService conformityService;

	/**
	 * 快捷短信生成签名
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "messageScan")
	  public void wapPayScan(ConformityQucikPayRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	  {
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=utf-8");
	    this.logger.info("下游上送的参数:" + entity);
	    
	    ChannleMerchantConfigKey keyinfo = this.conformityService.getChannelConfigKey(entity.getV_mid());
	    String merchantKey = keyinfo.getMerchantkey();
	    this.logger.info("下游商户密钥:" + keyinfo);
	    String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey);
	    entity.setV_sign(sign);
	    
	    request.setCharacterEncoding("UTF-8");
	    request.setAttribute("temp", entity);
	    request.getRequestDispatcher("/quick/quick_conformity_submit.jsp").forward(request, response);
	  }

	@ResponseBody
	  @RequestMapping({"quickPay/wap/submit"})
	  public void PayScanParam(ConformityQucikPayRequestEntity param, HttpServletRequest request, HttpServletResponse response)
	    throws Exception
	  {
	    this.logger.info("############快捷(WAP)支付##################");
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setContentType("text/html;charset=UTF-8");
	    Map<String, String> result = new HashMap();
	    PmsBusinessPos pmsBusinessPos = this.conformityService.selectKey(param.getV_mid());
	    
	    ChannleMerchantConfigKey keyinfo = this.conformityService.getChannelConfigKey(param.getV_mid());
	    
	    String merchantKey = keyinfo.getMerchantkey();
	    this.logger.info("下游上送参数:{}" + param);
	    String html = "";
	    if (!StringUtils.isEmpty(param.getV_mid()))
	    {
	      this.logger.info("下游上送签名串{}" + param.getV_sign());
	      SignatureUtil signUtil = new SignatureUtil();
	      Map map = BeanToMapUtil.convertBean(param);
	      if (SignatureUtil.checkSign(map, merchantKey))
	      {
	        this.logger.info("对比签名成功");
	        result = this.conformityService.payHandle(param);
	        if ("00".equals(result.get("v_code")))
	        {
				switch (pmsBusinessPos.getBusinessnum()) {
				case "936640995770001": // 摩宝快捷收银台
				case "936640995770002": // 摩宝快捷银联
					logger.info("摩宝快捷上送的数据:" + result);
					String params = HttpURLConection.parseParams(result);
					logger.info("摩宝快捷上送的数据:" + params);
					String path = "http://hanyipay.com/ks_netbank/mpay.c?" + params;
					logger.info("demo 重定向：" + path);
					request.getSession();
					response.setCharacterEncoding("GBK");
					response.sendRedirect(path.replace(" ", " "));
					break;
				case "10000466938":// 易宝快捷

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
				case "1120180427134034001":// 银生宝快捷
					result.remove("v_code");
					logger.info("银生宝快捷上送的数据:" + result);
					html = EffersonPayService.createAutoFormHtml(
							"http://180.166.114.155:18083/quickpay-front/quickPayWap/prePay", result, "UTF-8");
					logger.info("返回结果:{}"+html);
					outString(response, html);
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

	@ResponseBody
	@RequestMapping({ "ybReturnUrl" })
	public void ybReturnUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			this.logger.info("############易宝同步##################");
			String orderId = request.getParameter("orderId");
			this.logger.info("易宝同步返回的订单号" + orderId);
			OriginalOrderInfo originalInfo = null;
			if ((orderId != null) && (orderId != "")) {
				originalInfo = this.conformityService.getOriginOrderInfo(orderId);
			}
			this.logger.info("易宝同步原始订单数据:" + JSON.toJSON(originalInfo));
			this.logger.info("易宝给下游的同步地址" + originalInfo.getPageUrl());
			TreeMap<String, String> result = new TreeMap();
			String params = "";
			if (!StringUtils.isEmpty(orderId)) {
				ChannleMerchantConfigKey keyinfo = this.conformityService.getChannelConfigKey(originalInfo.getPid());

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
				this.logger.info("易宝同步给下游的数据:" + params);
				request.getSession();
				try {
					if (originalInfo.getPageUrl().indexOf("?") == -1) {
						String path = originalInfo.getPageUrl() + "?" + params;
						this.logger.info("易宝 重定向地址：" + path);

						response.sendRedirect(path.replace(" ", ""));
					} else {
						this.logger.info("易宝 重定向地址：" + originalInfo.getPageUrl());
						String path = originalInfo.getPageUrl() + "&" + params;
						this.logger.info("易宝 重定向地址：" + path);
						response.sendRedirect(path.replace(" ", ""));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				this.logger.info("没有收到易宝的同步数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ResponseBody
	  @RequestMapping({"ybNotifyUrl"})
	  public void ybNotifyUrl(HttpServletRequest request, HttpServletResponse response)
	  {
	    try
	    {
	      this.logger.info("############易宝异步##################");
	      String appMsg = request.getParameter("response");
	      Map<String, String> result = new HashMap();
	      this.logger.info("异步异步获取参数：" + appMsg);
	      if (!StringUtils.isEmpty(appMsg))
	      {
	        response.getWriter().write("SUCCESS");
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
			logger.info("解析之后的数据:" + jsonMap);

			String status = jsonMap.get("status");
			logger.info("易宝支付异步返回的订单状态:" + jsonMap);
			String orderId = jsonMap.get("orderId");
			logger.info("易宝支付异步返回的订单号:" + orderId);
	        OriginalOrderInfo originalInfo = null;
	        if ((orderId != null) && (orderId != "")) {
	          originalInfo = this.conformityService.getOriginOrderInfo(orderId);
	        }
	        this.logger.info("易宝支付异步原始订单交易时间:" + originalInfo.getOrderTime());
	        result.put("v_mid", originalInfo.getPid());
	        result.put("v_oid", originalInfo.getOrderId());
	        result.put("v_txnAmt", originalInfo.getOrderAmount());
	        result.put("v_time", originalInfo.getOrderTime());
	        result.put("v_code", "00");
	        result.put("v_msg", "请求成功");
	        result.put("v_attach", originalInfo.getAttach());
	        if ("SUCCESS".equals(status))
	        {
	          result.put("v_payStatus", "0000");
	          result.put("v_payMsg", "支付成功");
	        }
	        else
	        {
	          result.put("v_payStatus", "1001");
	          result.put("v_payMsg", "支付失败:" + URLDecoder.decode(request.getParameter("payMsg")));
	          this.logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:" + 
	            URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
	        }
	        ChannleMerchantConfigKey keyinfo = this.conformityService.getChannelConfigKey(originalInfo.getPid());
	        
	        String key = keyinfo.getMerchantkey();
	        CallbackEntity consume = (CallbackEntity)
	          BeanToMapUtil.convertMap(CallbackEntity.class, result);
	        
	        this.conformityService.otherInvoke(orderId, (String)result.get("v_payStatus"));
	        this.logger.info("易宝支付异步回调地址:" + originalInfo.getBgUrl());
	        
	        String sign = SignatureUtil.getSign(beanToMap(consume), key);
	        result.put("v_sign", sign);
	        
	        this.logger.info("易宝支付异步封装前参数：" + result);
	        CallbackEntity consumeResponseEntity = (CallbackEntity)
	          BeanToMapUtil.convertMap(CallbackEntity.class, result);
	        this.logger.info("易宝支付异步封装后参数：" + HttpClientUtil.bean2QueryStr(consumeResponseEntity));
	        String html = HttpClientUtil.post(originalInfo.getBgUrl(), 
	          HttpClientUtil.bean2QueryStr(consumeResponseEntity));
	        this.logger.info("易宝支付下游响应信息:" + html);
	        JSONObject ob = JSONObject.fromObject(html);
	        Iterator it = ob.keys();
	        Map<String, String> map = new HashMap();
	        while (it.hasNext())
	        {
	          String keys = (String)it.next();
	          if (keys.equals("success"))
	          {
	            String value = ob.getString(keys);
	            this.logger.info("易宝支付回馈的结果:\t" + value);
	            map.put("success", value);
	          }
	        }
	        if (((String)map.get("success")).equals("false"))
	        {
	          this.logger.info("易宝支付启动线程进行异步通知");
	          
	          ThreadPool.executor(new QuickPayThread(originalInfo.getBgUrl(), HttpClientUtil.bean2QueryStr(consumeResponseEntity)));
	        }
	        this.logger.info("易宝支付向下游 发送数据成功");
	      }
	      else
	      {
	        response.getWriter().write("FAIL");
	        this.logger.error("回调的参数为空!");
	        result.put("v_code", "15");
	        result.put("v_msg", "请求失败");
	      }
	    }
	    catch (Exception e)
	    {
	      this.logger.info("易宝异步回调异常:" + e);
	      e.printStackTrace();
	    }
	  }

	@ResponseBody
	@RequestMapping({ "mbReturnUrl" })
	public void mbReturnUrl(HttpServletResponse response, MBResponse mbResponse, HttpServletRequest request) {
		this.logger.info("############摩宝同步##################");
		TreeMap<String, String> result = new TreeMap();
		try {
			mbResponse.setRefcode(request.getParameter("payStatus"));
			this.logger.info("摩宝支付异步状态码:" + request.getParameter("payStatus"));
			mbResponse.setOrderId(request.getParameter("orderId"));
			this.logger.info("摩宝支付异步订单号:" + request.getParameter("orderId"));
			mbResponse.setRefMsg(URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
			if (!StringUtils.isEmpty(request.getParameter("orderId"))) {
				OriginalOrderInfo originalInfo = null;
				if ((mbResponse.getOrderId() != null) && (mbResponse.getOrderId() != "")) {
					originalInfo = this.conformityService.getOriginOrderInfo(mbResponse.getOrderId());
				}
				this.logger.info("摩宝同步原始订单数据:" + JSON.toJSON(originalInfo));
				this.logger.info("摩宝给下游的同步地址" + originalInfo.getPageUrl());
				ChannleMerchantConfigKey keyinfo = this.conformityService.getChannelConfigKey(originalInfo.getPid());

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
				this.logger.info("摩宝同步给下游的数据:" + params);
				request.getSession();
				if (originalInfo.getPageUrl().indexOf("?") == -1) {
					String path = originalInfo.getPageUrl() + "?" + params;
					this.logger.info("摩宝 重定向地址：" + path);

					response.sendRedirect(path.replace(" ", ""));
				} else {
					this.logger.info("摩宝 重定向地址：" + originalInfo.getPageUrl());
					String path = originalInfo.getPageUrl() + "&" + params;
					this.logger.info("摩宝 重定向地址：" + path);
					response.sendRedirect(path.replace(" ", ""));
				}
			} else {
				this.logger.info("没有收到魔宝的同步数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ResponseBody
	@RequestMapping({ "mbNotifyUrl" })
	public void mbNotifyUrl(HttpServletResponse response, MBResponse mbResponse, HttpServletRequest request) {
		this.logger.info("############摩宝异步##################");

		TreeMap<String, String> result = new TreeMap();
		try {
			mbResponse.setRefcode(request.getParameter("payStatus"));
			this.logger.info("摩宝支付异步状态码:" + request.getParameter("payStatus"));
			mbResponse.setOrderId(request.getParameter("orderId"));
			this.logger.info("摩宝支付异步订单号:" + request.getParameter("orderId"));
			mbResponse.setRefMsg(URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
			if (!StringUtils.isEmpty(request.getParameter("orderId"))) {
				response.getWriter().write("OK");
				OriginalOrderInfo originalInfo = null;
				if ((mbResponse.getOrderId() != null) && (mbResponse.getOrderId() != "")) {
					originalInfo = this.conformityService.getOriginOrderInfo(mbResponse.getOrderId());
				}
				this.logger.info("摩宝支付异步原始订单信息:" + originalInfo.getOrderTime());
				this.logger.info("摩宝支付异步回调地址:" + originalInfo.getBgUrl());
				result.put("v_mid", originalInfo.getPid());
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				result.put("v_attach", originalInfo.getAttach());
				if ("00".equals(request.getParameter("payStatus"))) {
					result.put("v_payStatus", "0000");
					result.put("v_payMsg", "支付成功");
					int i = this.conformityService.updatePmsMerchantInfo(originalInfo);
					if (i > 0) {
						this.logger.info("魔宝*****实时入金完成");
					} else {
						this.logger.info("魔宝*****实时入金失败");
					}
				} else {
					result.put("v_payStatus", "1001");
					result.put("v_payMsg", "支付失败:" + URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
					this.logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
							+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
				}
				ChannleMerchantConfigKey keyinfo = this.conformityService.getChannelConfigKey(originalInfo.getPid());

				String key = keyinfo.getMerchantkey();
				CallbackEntity consume = (CallbackEntity) BeanToMapUtil.convertMap(CallbackEntity.class, result);

				this.conformityService.otherInvoke(request.getParameter("orderId"), (String) result.get("v_payStatus"));
				this.logger.info("摩宝支付异步回调地址:" + originalInfo.getBgUrl());

				String sign = SignatureUtil.getSign(beanToMap(consume), key);
				result.put("v_sign", sign);

				this.logger.info("摩宝支付异步封装前参数：" + result);
				CallbackEntity consumeResponseEntity = (CallbackEntity) BeanToMapUtil.convertMap(CallbackEntity.class,
						result);
				this.logger.info("摩宝支付异步封装后参数：" + HttpClientUtil.bean2QueryStr(consumeResponseEntity));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(),
						HttpClientUtil.bean2QueryStr(consumeResponseEntity));
				this.logger.info("摩宝支付下游响应信息:" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						this.logger.info("摩宝支付回馈的结果:\t" + value);
						map.put("success", value);
					}
				}
				if (((String) map.get("success")).equals("false")) {
					this.logger.info("摩宝支付启动线程进行异步通知");

					ThreadPool.executor(new QuickPayThread(originalInfo.getBgUrl(),
							HttpClientUtil.bean2QueryStr(consumeResponseEntity)));
				}
				this.logger.info("摩宝支付向下游 发送数据成功");
			} else {
				this.logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ResponseBody
	@RequestMapping({ "ysbReturnUrl" })
	public void ysbReturnUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			this.logger.info("############银生宝同步##################");
			String orderId = request.getParameter("orderNo");
			String userId = request.getParameter("userId");
			this.logger.info("银生宝同步返回的订单号:" + JSON.toJSON(orderId));
			this.logger.info("银生宝同步返回的用户ID:" + userId);
			OriginalOrderInfo originalInfo = null;
			if ((orderId != null) && (orderId != "")) {
				originalInfo = this.conformityService.getOriginOrderInfo(orderId);
			}
			this.logger.info("银生宝同步原始订单数据:" + JSON.toJSON(originalInfo));
			this.logger.info("银生宝给下游的同步地址" + originalInfo.getPageUrl());
			TreeMap<String, String> result = new TreeMap();
			String params = "";
			if (!StringUtils.isEmpty(orderId)) {
				ChannleMerchantConfigKey keyinfo = this.conformityService.getChannelConfigKey(originalInfo.getPid());

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
				this.logger.info("银生宝同步给下游的数据:" + params);
				request.getSession();
				try {
					if (originalInfo.getPageUrl().indexOf("?") == -1) {
						String path = originalInfo.getPageUrl() + "?" + params;
						this.logger.info("银生宝 重定向地址：" + path);

						response.sendRedirect(path.replace(" ", ""));
					} else {
						this.logger.info("银生宝 重定向地址：" + originalInfo.getPageUrl());
						String path = originalInfo.getPageUrl() + "&" + params;
						this.logger.info("银生宝 重定向地址：" + path);
						response.sendRedirect(path.replace(" ", ""));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				this.logger.info("没有收到银生宝的同步数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ResponseBody
	@RequestMapping({ "ysbNotifyUrl" })
	public void ysbNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			this.logger.info("###########银生宝支付异步#############");
			String result_code = request.getParameter("result_code");
			String result_msg = request.getParameter("result_msg");
			String orderNo = request.getParameter("orderNo");
			String userId = request.getParameter("userId");
			this.logger.info("银生宝支付异步响应用户ID：" + userId);
			this.logger.info("银生宝支付异步响应订单号：" + orderNo);
			this.logger.info("银生宝支付异步响应描述：" + result_msg);
			this.logger.info("银生宝支付异步响应状态码：" + result_code);
			OriginalOrderInfo originalInfo = null;

			Map<String, String> result = new HashMap();
			if (!StringUtils.isEmpty(orderNo)) {
				if ((orderNo != null) && (orderNo != "")) {
					originalInfo = this.conformityService.getOriginOrderInfo(orderNo);
				}
				this.logger.info("银生宝支付异步原订单数据:" + JSON.toJSON(originalInfo));
				result.put("v_mid", originalInfo.getPid());
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				result.put("v_userId", originalInfo.getByUser());
				result.put("v_attach", originalInfo.getAttach());
				if ("0000".equals(result_code)) {
					result.put("v_payStatus", "0000");
					result.put("v_payMsg", "支付成功");
				} else {
					result.put("v_payStatus", "1001");
					result.put("v_payMsg", "支付失败:" + request.getParameter("result_msg"));
					this.logger.info("交易错误码:" + request.getParameter("result_code") + ",错误信息:"
							+ request.getParameter("result_msg"));
				}
				ChannleMerchantConfigKey keyinfo = this.conformityService.getChannelConfigKey(originalInfo.getPid());

				String key = keyinfo.getMerchantkey();
				CallbackEntity consume = (CallbackEntity) BeanToMapUtil.convertMap(CallbackEntity.class, result);

				this.conformityService.otherInvoke(orderNo, (String) result.get("v_payStatus"));

				String sign = SignatureUtil.getSign(beanToMap(consume), key);
				result.put("v_sign", sign);

				this.logger.info("银生宝异步之前的参数：" + result);
				CallbackEntity consumeResponseEntity = (CallbackEntity) BeanToMapUtil.convertMap(CallbackEntity.class,
						result);
				this.logger.info("银生宝支付异步封装后参数：" + HttpClientUtil.bean2QueryStr(consumeResponseEntity));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(),
						HttpClientUtil.bean2QueryStr(consumeResponseEntity));
				this.logger.info("银生宝支付下游响应信息:" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						this.logger.info("银生宝支付回馈的结果:\t" + value);
						map.put("success", value);
					}
				}
				if (((String) map.get("success")).equals("false")) {
					this.logger.info("银生宝支付启动线程进行异步通知");

					ThreadPool.executor(new QuickPayThread(originalInfo.getBgUrl(),
							HttpClientUtil.bean2QueryStr(consumeResponseEntity)));
				}
				this.logger.info("银生宝支付向下游 发送数据成功");
			} else {
				this.logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}
			outString(response, this.gson.toJson(result));
		} catch (Exception e) {
			this.logger.info("银生宝异步回调异常:" + e);
			e.printStackTrace();
		}
	}

	@ResponseBody
	@RequestMapping({ "jsReturnUrl" })
	public void jsReturnUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			this.logger.info("###########江苏电商支付同步#############");
			String orderNum = request.getParameter("orderNum");
			String pl_payState = request.getParameter("pl_payState");
			String pl_payMessage = request.getParameter("pl_payMessage");
			String pl_orderNum = request.getParameter("pl_orderNum");
			this.logger.info("江苏电商支付异步响应用户ID：" + pl_orderNum);
			this.logger.info("江苏电商支付异步响应订单号：" + orderNum);
			this.logger.info("江苏电商支付异步响应描述：" + pl_payMessage);
			this.logger.info("江苏电商支付异步响应状态码：" + pl_payState);
			OriginalOrderInfo originalInfo = null;
			if ((orderNum != null) && (orderNum != "")) {
				originalInfo = this.conformityService.getOriginOrderInfo(orderNum);
			}
			this.logger.info("江苏电商同步原始订单数据:" + JSON.toJSON(originalInfo));
			this.logger.info("江苏电商给下游的同步地址" + originalInfo.getPageUrl());
			TreeMap<String, String> result = new TreeMap();
			String params = "";
			if (!StringUtils.isEmpty(orderNum)) {
				ChannleMerchantConfigKey keyinfo = this.conformityService.getChannelConfigKey(originalInfo.getPid());

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
				this.logger.info("江苏电商同步给下游的数据:" + params);
				request.getSession();
				try {
					if (originalInfo.getPageUrl().indexOf("?") == -1) {
						String path = originalInfo.getPageUrl() + "?" + params;
						this.logger.info("江苏电商重定向地址：" + path);

						response.sendRedirect(path.replace(" ", ""));
					} else {
						this.logger.info("江苏电商 重定向地址：" + originalInfo.getPageUrl());
						String path = originalInfo.getPageUrl() + "&" + params;
						this.logger.info("江苏电商 重定向地址：" + path);
						response.sendRedirect(path.replace(" ", ""));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				this.logger.info("没有收到江苏电商的同步数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ResponseBody
	@RequestMapping({ "jsNotifyUrl" })
	public void jsNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			this.logger.info("#############江苏电商支付异步##############");
			TreeMap<String, String> result = new TreeMap();
			String orderNum = request.getParameter("orderNum");
			String pl_payState = request.getParameter("pl_payState");
			String pl_payMessage = request.getParameter("pl_payMessage");
			String pl_orderNum = request.getParameter("pl_orderNum");
			this.logger.info("江苏电商支付异步响应用户ID：" + pl_orderNum);
			this.logger.info("江苏电商支付异步响应订单号：" + orderNum);
			this.logger.info("江苏电商支付异步响应描述：" + pl_payMessage);
			this.logger.info("江苏电商支付异步响应状态码：" + pl_payState);
			OriginalOrderInfo originalInfo = null;
			if (!StringUtils.isEmpty(request.getParameter("orderNum"))) {
				response.getWriter().write("SUCCESS");
				if ((orderNum != null) && (orderNum != "")) {
					originalInfo = this.conformityService.getOriginOrderInfo(orderNum);
				}
				this.logger.info("江苏电商支付异步原始订单信息:" + originalInfo.getOrderTime());
				this.logger.info("江苏电商支付异步回调地址:" + originalInfo.getBgUrl());
				result.put("v_mid", originalInfo.getPid());
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				result.put("v_attach", originalInfo.getAttach());
				if ("4".equals(request.getParameter("pl_payState"))) {
					result.put("v_payStatus", "0000");
					result.put("v_payMsg", "支付成功");
					int i = this.conformityService.updatePmsMerchantInfo80(originalInfo);
					if (i > 0) {
						this.logger.info("江苏电商*****实时入金完成");
					} else {
						this.logger.info("江苏电商*****实时入金失败");
					}
				} else {
					result.put("v_payStatus", "1001");
					result.put("v_payMsg", "支付失败:" + pl_payMessage);
					this.logger.info("交易错误码:" + pl_payMessage + ",错误信息:" + pl_payMessage);
				}
				ChannleMerchantConfigKey keyinfo = this.conformityService.getChannelConfigKey(originalInfo.getPid());

				String key = keyinfo.getMerchantkey();
				CallbackEntity consume = (CallbackEntity) BeanToMapUtil.convertMap(CallbackEntity.class, result);

				this.conformityService.otherInvoke(orderNum, (String) result.get("v_payStatus"));
				this.logger.info("江苏电商支付异步回调地址:" + originalInfo.getBgUrl());

				String sign = SignatureUtil.getSign(beanToMap(consume), key);
				result.put("v_sign", sign);

				this.logger.info("江苏电商支付异步封装前参数：" + result);
				CallbackEntity consumeResponseEntity = (CallbackEntity) BeanToMapUtil.convertMap(CallbackEntity.class,
						result);
				this.logger.info("江苏电商支付异步封装后参数：" + HttpClientUtil.bean2QueryStr(consumeResponseEntity));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(),
						HttpClientUtil.bean2QueryStr(consumeResponseEntity));
				this.logger.info("江苏电商支付下游响应信息:" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						this.logger.info("江苏电商支付回馈的结果:\t" + value);
						map.put("success", value);
					}
				}
				if (((String) map.get("success")).equals("false")) {
					this.logger.info("江苏电商支付启动线程进行异步通知");

					ThreadPool.executor(new QuickPayThread(originalInfo.getBgUrl(),
							HttpClientUtil.bean2QueryStr(consumeResponseEntity)));
				}
				this.logger.info("江苏电商支付向下游 发送数据成功");
			} else {
				this.logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

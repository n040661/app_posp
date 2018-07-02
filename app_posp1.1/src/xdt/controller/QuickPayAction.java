package xdt.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.etonepay.b2c.utils.MD5;
import com.yeepay.g3.facade.yop.ca.dto.DigitalEnvelopeDTO;
import com.yeepay.g3.facade.yop.ca.enums.CertTypeEnum;
import com.yeepay.g3.frame.yop.ca.DigitalEnvelopeUtils;
import com.yeepay.g3.sdk.yop.utils.InternalConfig;

import net.sf.json.JSONObject;
import xdt.dto.lhzf.LhzfResponse;
import xdt.dto.mb.MBResponse;
import xdt.dto.quickPay.entity.ConsumeRequestEntity;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.entity.MessAgeResponseEntity;
import xdt.dto.quickPay.entity.MessageRequestEntity;
import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.dto.quickPay.entity.QueryResponseEntity;
import xdt.dto.quickPay.util.MbUtilThread;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.hf.util.EffersonPayService;
import xdt.quickpay.jbb.util.Base64;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.syys.PayCore;
import xdt.schedule.ThreadPool;
import xdt.service.ILhzfService;
import xdt.service.IMBService;
import xdt.service.IQuickPayService;
import xdt.service.OriginalOrderInfoService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.service.impl.QuickpayServiceImpl;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.UtilDate;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("quickPayAction")
public class QuickPayAction extends BaseAction {

	private Logger logger = Logger.getLogger(QuickPayAction.class);

	@Resource
	private IQuickPayService quickPayService;
	@Resource
	private IMBService imbService; // 摩宝
	@Resource
	private ILhzfService iLhzfService; // 赢酷
	@Resource
	private OriginalOrderInfoService Origi; // 原始信息Service

	@Resource
	private PmsWeixinMerchartInfoService weixinService;
	
	@Resource
	private OriginalOrderInfoService originalOrderInfoService;

	/**
	 * 查看当前卡号是否在数据库中已经存在
	 * 
	 * @param response
	 * @param session
	 */
	@ResponseBody
	@RequestMapping(value = "checkLocalCardRecord")
	public void checkLocalCardRecord(HttpServletResponse response, HttpSession session, HttpServletRequest request) {
		String param = requestClient(request);
		try {
			outPrint(response, quickPayService.checkLocalCardRecord(session, param));
		} catch (Exception e) {
			setSession(session, request.getRemoteAddr(), true);
			logger.info("[app_exception]" + e.fillInStackTrace());
			e.printStackTrace();
		}
	}

	/**
	 * 快捷短信生成签名
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "messageScan")
	public void messageScan(MessageRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		logger.info("下游上送的参数:" + entity);
		Map<String, Object> map = new HashMap<String, Object>();
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(entity.getV_mid());
		String merchantKey = keyinfo.getMerchantkey();
		logger.info("下游商户密钥:" + keyinfo);
		String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey, logger);
		entity.setV_sign(sign);
		// 返回页面参数
		request.setCharacterEncoding("UTF-8");
		request.setAttribute("temp", entity);
		request.getRequestDispatcher("/quick/quick_message_submit.jsp").forward(request, response);
	}

	@RequestMapping(value = "sign")
	public void sign(MessageRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		logger.info("下游上送的参数:" + entity);
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(entity.getV_mid());
		String merchantKey = keyinfo.getMerchantkey();
		logger.info("下游商户密钥:" + keyinfo);
		String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey, logger);
		// 返回页面参数
		outString(response, sign);
	}

	/**
	 * 快捷支付生成签名
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "payScan")
	public void payScan(ConsumeRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		logger.info("下游上送的参数:" + entity);
		Map<String, Object> map = new HashMap<String, Object>();
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(entity.getV_mid());
		String merchantKey = keyinfo.getMerchantkey();
		logger.info("下游商户密钥:" + keyinfo);
		String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey, logger);
		entity.setV_sign(sign);
		// 返回页面参数
		request.setCharacterEncoding("UTF-8");
		request.setAttribute("temp", entity);
		request.getRequestDispatcher("/quick/quick_pay_submit.jsp").forward(request, response);
	}

	/**
	 * 快捷支付生成签名
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "queryScan")
	public void queryScan(QueryRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		logger.info("下游上送的参数:" + JSON.toJSONString(entity));
		Map<String, Object> map = new HashMap<String, Object>();
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(entity.getV_mid());
		String merchantKey = keyinfo.getMerchantkey();
		logger.info("下游商户密钥:" + keyinfo);
		String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey, logger);
		entity.setV_sign(sign);
		// 返回页面参数
		request.setCharacterEncoding("UTF-8");
		request.setAttribute("temp", entity);
		request.getRequestDispatcher("/quick/quick_pay_query_submit.jsp").forward(request, response);
	}

	/**
	 * 上游短信 快捷短信请求参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "message")
	public void messageScanParam(MessageRequestEntity param, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("快捷短信进来了");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();

		logger.info("下游上送参数:{}"+ param);
		if (!StringUtils.isEmpty(param.getV_mid())) {
			logger.info("下游上送签名串{}" + param.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(param.getV_mid());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(param);

			if (signUtil.checkSign(map, merchantKey, logger)) {
				logger.info("对比签名成功");
				result = quickPayService.updateHandle(param);
				if ("00000".equals(result.get("v_code"))) {
					PmsBusinessPos pmsBusinessPos = quickPayService.selectKey(param.getV_mid());
					switch (pmsBusinessPos.getBusinessnum()) {
					case "1711030001":// 沈阳银盛
						logger.info("上游返回的地址:" + result.get("url"));
						request.getSession();
						response.setCharacterEncoding("UTF-8");
						response.sendRedirect(result.get("url").replace(" ", " "));
						break;
					case "888888888888888":// 聚佰宝签约
						String url = result.get("html");
						String params = url.replaceAll("\\[n]", "");
						logger.info("聚佰宝签约:" + params);
						String ToSubmitHTML = new String(Base64.decode(params), "UTF-8");
						logger.info("URL 重定向：" + ToSubmitHTML);
						outString(response, ToSubmitHTML);
						break;
					default:
						break;
					}
				}
				logger.info("短信响应信息:" + result);
				MessAgeResponseEntity message = (MessAgeResponseEntity) BeanToMapUtil
						.convertMap(MessAgeResponseEntity.class, result);
				String sign = SignatureUtil.getSign(beanToMap(message), merchantKey, logger);
				result.put("v_sign", sign);
				outString(response, gson.toJson(result));
			} else {
				logger.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
				outString(response, gson.toJson(result));
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
			outString(response, gson.toJson(result));
		}

	}

	/**
	 * 快捷支付求参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "pay")
	public void PayScanParam(ConsumeRequestEntity param, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("快捷短信进来了");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=UTF-8");
		Map<String, String> result = new HashMap<String, String>();
		PmsBusinessPos pmsBusinessPos = quickPayService.selectKey(param.getV_mid());
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(param.getV_mid());
		// ------------------------需要改签名
		String merchantKey = keyinfo.getMerchantkey();
		logger.info("下游上送参数:{}"+ JSON.toJSONString(param));
		String html = "";
		if (!StringUtils.isEmpty(param.getV_mid())) {

			logger.info("下游上送签名串{}" + param.getV_sign());
			SignatureUtil signUtil = new SignatureUtil();
			Map map = BeanToMapUtil.convertBean(param);
			if (signUtil.checkSign(map, merchantKey, logger)) {
				logger.info("对比签名成功");
				result = quickPayService.payHandle(param);
				if ("00".equals(result.get("v_code"))) {
					switch (pmsBusinessPos.getChannelnum()) {

					//case "053211180":// 钱龙快捷
					case "QDQLKJ":// 钱龙快捷
						logger.info("钱龙快捷上送的数据:" + result);
						html = EffersonPayService.createAutoFormHtml("", result, "GBK");
						logger.info("返回结果:{}"+ html);
						outString(response, html);
						break;
					case "MBXHF": // 摩宝快捷收银台
					//case "936640995770002": // 摩宝快捷银联
						logger.info("摩宝快捷上送的数据:" + result);
						String params = HttpURLConection.parseParams(result);
						logger.info("摩宝快捷上送的数据:" + params);
						String path = "http://hanyipay.com/ks_netbank/mpay.c?" + params;
						logger.info("demo 重定向：" + path);
						request.getSession();
						response.setCharacterEncoding("GBK");
						response.sendRedirect(path.replace(" ", " "));
						break;
					case "YK": // 赢酷快捷
						result.remove("v_code");
						logger.info("赢酷快捷上送的数据:" + result);
						html = EffersonPayService.createAutoFormHtml(
								"https://service.blueseapay.com/gateway/transaction/request", result, "UTF-8");
						logger.info("返回结果:{}"+ html);
						outString(response, html);
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
					case "YSKJ":// 易生快捷
						url = result.get("html");
						logger.info("URL 重定向：" + url);
						outString(response, url);
						break;
					case "JBB":// 聚佰宝快捷
						url = result.get("path");
						logger.info("URL 重定向：" + url);
						outString(response, url);
						break;										
					case "YSB":// 银生宝快捷
						result.remove("v_code");
						logger.info("银生宝快捷上送的数据:" + result);
						html = EffersonPayService.createAutoFormHtml(
								"http://180.166.114.155:18083/quickpay-front/quickPayWap/prePay", result, "UTF-8");
						logger.info("返回结果:{}"+ html);
						outString(response, html);
						break;
					default:	
						ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
						String sign = SignatureUtil.getSign(beanToMap(consume), merchantKey, logger);
						result.put("v_sign", sign);
						outString(response, JSON.toJSON(result));
						break;
					}
				}else {
					outString(response, JSON.toJSON(result));
				}

			} else {
				logger.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
				outString(response, JSON.toJSON(result));
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
			outString(response, JSON.toJSON(result));
		}
//		ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil.convertMap(ConsumeResponseEntity.class,
//				result);
//		// 生成签名
//		String sign = SignatureUtil.getSign(beanToMap(consume), merchantKey, logger);
//		result.put("v_sign", sign);
//		logger.info("返回结果:{}", result);
//		outString(response, gson.toJson(result));

	}

	/**
	 * 贷还生成签名
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "loanStillSign")
	public void loanStillSign(MessageRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		logger.info("下游上送的参数:" + entity);
		Map<String, Object> map = new HashMap<String, Object>();
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(entity.getV_mid());
		String merchantKey = keyinfo.getMerchantkey();
		logger.info("下游商户密钥:" + keyinfo);
		String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey, logger);
		entity.setV_sign(sign);
		// 返回页面参数
		request.setCharacterEncoding("UTF-8");
		request.setAttribute("temp", entity);
		request.getRequestDispatcher("/quick/daihuan_message_submit.jsp").forward(request, response);
	}

	/**
	 * 贷还请求参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "loanStillPay")
	public void loanStillPay(MessageRequestEntity param, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("贷还进来了");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=UTF-8");
		Map<String, String> result = new HashMap<String, String>();
		PmsBusinessPos pmsBusinessPos = quickPayService.selectKey(param.getV_mid());
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(param.getV_mid());
		// ------------------------需要改签名
		String merchantKey = keyinfo.getMerchantkey();
		logger.info("下游上送参数:{}"+JSON.toJSON(param));
		/*if (param.getV_sign() == null) {
			String sign = SignatureUtil.getSign(beanToMap(param), merchantKey, logger);
			param.setV_sign(sign);
		}*/
		if (!StringUtils.isEmpty(param.getV_mid())) {

			logger.info("下游上送签名串{}" + param.getV_sign());
			SignatureUtil signUtil = new SignatureUtil();
			Map map = BeanToMapUtil.convertBean(param);
			if (signUtil.checkSign(map, merchantKey, logger)) {
				logger.info("对比签名成功");
				result = quickPayService.loanStillPay(param);
				switch (pmsBusinessPos.getBusinessnum()) {
				case "0008136":// 高汇通代还
					ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
							.convertMap(ConsumeResponseEntity.class, result);
					// 生成签名
					String sign = SignatureUtil.getSign(beanToMap(consume), merchantKey, logger);
					result.put("v_sign", sign);
					logger.info("返回结果:{}"+result);
					outString(response, JSON.toJSON(result));
					break;
				case "12345678":// 上海漪雷代还
					String url = result.get("html");
					logger.info("URL 重定向：" + url);
					request.getSession();
					response.setCharacterEncoding("GBK");
					response.sendRedirect(url.replace(" ", " "));
					break;
				default:
					break;
				}
				logger.info("贷还action层返回参数：" + JSON.toJSONString(result));
			} else {
				logger.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
				outString(response, JSON.toJSON(result));
			}

		} else {
			logger.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
			outString(response, JSON.toJSON(result));
		}
	}

	@RequestMapping(value = "query")
	public void query(QueryRequestEntity query, HttpServletResponse response) throws Exception {
		logger.info("------快捷查询上传参数：" + JSON.toJSONString(query));
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<>();
		if (!StringUtils.isEmpty(query.getV_mid())) {

			// 检验数据是否合法
			logger.info("下游上送签名串{}" + query.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(query.getV_mid());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(query);
			if (signUtil.checkSign(map, merchantKey, logger)) {

				logger.info("对比签名成功");
				result = quickPayService.quickQuery(query);
				QueryResponseEntity queryconsume = (QueryResponseEntity) BeanToMapUtil
						.convertMap(QueryResponseEntity.class, result);
				logger.info("---返回数据签名签的数据:" + beanToMap(queryconsume));
				String sign = SignatureUtil.getSign(beanToMap(queryconsume), merchantKey, logger);
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
	 * 摩宝异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "mbNotifyUrl")
	public void mbNotifyUrl(HttpServletResponse response, MBResponse mbResponse, HttpServletRequest request) {
		logger.info("魔宝快捷异步响应信息：" + JSON.toJSON(mbResponse));
		String str;
		TreeMap<String, String> result = new TreeMap<>();
		try {

			mbResponse.setRefcode(request.getParameter("payStatus"));
			mbResponse.setOrderId(request.getParameter("orderId"));
			mbResponse.setRefMsg(URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
			if (mbResponse != null) {
				str = "OK";
				// outString(response, str);
				OriginalOrderInfo originalInfo = null;
				if (mbResponse.getOrderId() != null && mbResponse.getOrderId() != "") {
					originalInfo = this.quickPayService.getOriginOrderInfo(mbResponse.getOrderId());
				}
				logger.info("订单数据**魔宝:" + JSON.toJSON(originalInfo));
				Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				logger.info("下游的异步地址**魔宝" + originalInfo.getBgUrl());
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				imbService.update1(mbResponse);
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_attach", originalInfo.getAttach());
				result.put("v_mid", originalInfo.getPid());
				if ("00".equals(request.getParameter("payStatus"))) {

					result.put("v_status", "0000");
					result.put("v_msg", "支付成功");
					int i = imbService.UpdatePmsMerchantInfo(originalInfo);
					if (i > 0) {
						logger.info("魔宝*****实时入金完成");
					} else {
						logger.info("魔宝*****实时入金失败");
					}
				} else {
					result.put("v_status", "1001");
					result.put("v_msg", "支付失败");
					logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
							+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
				}
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				result.put("v_sign", sign);

				logger.info("异步之前的参数：" + beanToMap(consume));
				ConsumeResponseEntity consumeResponseEntity = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				String html = HttpClientUtil.post(originalInfo.getBgUrl(),
						bean2Util.bean2QueryStr(consumeResponseEntity));
				logger.info("下游返回状态" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						logger.info("异步回馈的结果:" + "\t" + value);
						map.put("success", value);
					}
				}
				if (!result.get("success").equals("true")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(
							new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(consumeResponseEntity)));
				}
				logger.info("向下游 发送数据成功");
			} else {
				str = "FALL";
				logger.info("没有收到魔宝的异步数据");
				outString(response, str);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 赢酷异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "lhzfNotifyUrl")
	public void lhzfNotifyUrl(LhzfResponse lhzfResponse, HttpServletResponse response) {
		try {
			logger.info("蓝海异步通知来了");
			logger.info("蓝海异步参数：" + JSON.toJSONString(lhzfResponse));
			LhzfResponse lhzfResponses = new LhzfResponse();
			String str;
			if (lhzfResponse.getOrderNo() != null) {
				str = "SUCCESS";
				OriginalOrderInfo originalInfo = null;
				if (lhzfResponse.getOrderNo() != null && lhzfResponse.getOrderNo() != "") {
					originalInfo = this.quickPayService.getOriginOrderInfo(lhzfResponse.getOrderNo());
				}
				logger.info("订单数据:" + JSON.toJSON(originalInfo));
				Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				logger.info("下游的异步地址" + originalInfo.getBgUrl());
				logger.info("蓝海异步返回解析参数" + JSON.toJSON(lhzfResponse));
				ConsumeResponseEntity consume = new ConsumeResponseEntity();
				consume.setV_mid(originalInfo.getPid());
				consume.setV_oid(originalInfo.getOrderId());
				consume.setV_txnAmt(originalInfo.getOrderAmount());
				consume.setV_code("00");
				consume.setV_attach(originalInfo.getAttach());
				if ("0000".equals(lhzfResponse.getRespCode())) {
					iLhzfService.update(lhzfResponse);
					// ---------------------------------------------------
					// 返回参数
					if ("SUCCESS".equals(lhzfResponse.getStatus())) {
						consume.setV_msg("支付成功");
						consume.setV_status("0000");
					} else {
						consume.setV_msg("支付失败");
						consume.setV_status("1001");
					}
				} else {
					consume.setV_msg("支付失败");
					consume.setV_status("1001");
				}
				// 和下面的签名
				// ---------------------------------------------------
				TreeMap<String, String> result = new TreeMap<String, String>();
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				// 生成签名
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				logger.info("赢酷回调生成的签名:" + sign);
				consume.setV_sign(sign);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				logger.info("赢酷回调向下游返回的数据:" + bean2Util.bean2QueryStr(consume));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(consume));
				logger.info("下游返回状态" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						logger.info("异步回馈的结果:" + "\t" + value);
						map.put("success", value);
					}
				}
				if (!result.get("success").equals("true")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(consume)));
				}
				logger.info("向下游 发送数据成功");
			} else {
				str = "FAIL";
			}
			outString(response, str);
		} catch (Exception e) {
			logger.info("蓝海异步回调异常:" + e);
			e.printStackTrace();
		}
	}

	/**
	 * 沈阳银盛绑卡异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "syysNotifyUrl")
	public void syysNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("沈阳银盛绑卡异步通知来了");
			Map<String, String> result = new HashMap<String, String>();
			BufferedReader br = new BufferedReader(
					new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String key = "";
			String appMsg = sb.toString();
			logger.info("请求参数：" + appMsg);
			if (!StringUtils.isEmpty(appMsg)) {
				JSONObject ob = JSONObject.fromObject(appMsg);
				logger.info("封装之后的数据:{}"+ ob);
				Iterator it = ob.keys();
				String order_num = "";
				String auth_id = "";
				String ret_code = "";
				while (it.hasNext()) {
					key = (String) it.next();
					if (key.equals("ret_code")) {
						ret_code = ob.getString(key);
						logger.info("沈阳银盛绑卡返回的异步状态码:" + "\t" + ret_code);
					}
					if (key.equals("order_num")) {
						order_num = ob.getString(key);
						logger.info("沈阳银盛绑卡返回的原始订单号:" + "\t" + order_num);
					}
					if (key.equals("auth_id")) {

						auth_id = ob.getString(key);
						logger.info("沈阳银盛绑卡返回的授权码:" + "\t" + auth_id);

					}
				}
			} else {
				logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}
			ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
					.convertMap(ConsumeResponseEntity.class, result);
			String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
			result.put("v_sign", sign);
			logger.info("给下游同步的数据:" + result);
		} catch (Exception e) {
			logger.info("沈阳银盛绑卡异步回调异常:" + e);
			e.printStackTrace();
		}
	}

	/**
	 * 沈阳银盛绑卡异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "syNotifyUrl")
	public void syNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("沈阳银盛支付异步通知来了");
			Map<String, String> result = new HashMap<String, String>();
			BufferedReader br = new BufferedReader(
					new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String key = "";
			String appMsg = sb.toString();
			logger.info("请求参数：" + appMsg);
			if (!StringUtils.isEmpty(appMsg)) {
				JSONObject ob = JSONObject.fromObject(appMsg);
				logger.info("封装之后的数据:{}"+ob);
				Iterator it = ob.keys();
				String order_num = "";
				String ret_code = "";
				while (it.hasNext()) {
					key = (String) it.next();
					if (key.equals("ret_code")) {
						ret_code = ob.getString(key);
						logger.info("沈阳银盛支付返回的异步状态码:" + "\t" + ret_code);
					}
					if (key.equals("order_no")) {

						order_num = ob.getString(key);
						logger.info("沈阳银盛支付返回的原始订单号:" + "\t" + order_num);
					}
					if ("0000".equals(ret_code)) {
						OriginalOrderInfo originalInfo = null;
						originalInfo = Origi.get(order_num);
						logger.info("沈阳银盛支付异步订单数据:" + JSON.toJSON(originalInfo));
						result.put("v_oid", originalInfo.getOrderId());
						result.put("v_txnAmt", originalInfo.getOrderAmount());
						result.put("v_code", "00");
						result.put("v_attach", originalInfo.getAttach());
						result.put("v_mid", originalInfo.getPid());
						result.put("v_status", "0000");
						result.put("v_msg", "支付成功");
						ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
						// 获取商户秘钥
						key = keyinfo.getMerchantkey();
						ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
								.convertMap(ConsumeResponseEntity.class, result);
						// 修改订单状态
						quickPayService.otherInvoke(consume);

						// 生成签名
						String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
						result.put("v_sign", sign);

						logger.info("异步之前的参数：" + result);
						ConsumeResponseEntity consumeResponseEntity = (ConsumeResponseEntity) BeanToMapUtil
								.convertMap(ConsumeResponseEntity.class, result);
						Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
						logger.info("异步给下游返回的参数：" + bean2Util.bean2QueryStr(consumeResponseEntity));
						String html = HttpClientUtil.post(originalInfo.getBgUrl(),
								bean2Util.bean2QueryStr(consumeResponseEntity));
						logger.info("下游返回状态" + html);
						ob = JSONObject.fromObject(html);
						it = ob.keys();
						Map<String, String> map = new HashMap<>();
						while (it.hasNext()) {
							String keys = (String) it.next();
							if (keys.equals("success")) {
								String value = ob.getString(keys);
								logger.info("异步回馈的结果:" + "\t" + value);
								map.put("success", value);
							}
						}
						if (!result.get("success").equals("true")) {

							logger.info("启动线程进行异步通知");
							// 启线程进行异步通知
							ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),
									bean2Util.bean2QueryStr(consumeResponseEntity)));
						}
						logger.info("向下游 发送数据成功");
					}

				}
			} else {
				logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}
		} catch (Exception e) {
			logger.info("沈阳银盛绑卡异步回调异常:" + e);
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
			logger.info("易宝异步通知来了");
			String appMsg = request.getParameter("response");
			Map<String, String> result = new HashMap<String, String>();
			logger.info("请求参数：" + appMsg);
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
				logger.info("解析之后的数据:" + jsonMap);

				String status = jsonMap.get("status");
				logger.info("易宝支付异步返回的订单状态:" + jsonMap);
				String orderId = jsonMap.get("orderId");
				logger.info("易宝支付异步返回的订单号:" + orderId);
				OriginalOrderInfo originalInfo = null;
				if (orderId != null && orderId != "") {
					originalInfo = this.quickPayService.getOriginOrderInfo(orderId);
				}
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_attach", originalInfo.getAttach());
				result.put("v_mid", originalInfo.getPid());
				result.put("v_time", UtilDate.getTXDateTime());
				if ("SUCCESS".equals(status)) {

					result.put("v_status", "0000");
					result.put("v_msg", "支付成功");
					// if ("0".equals(originalInfo.getPayType())) {
					// int i = imbService.UpdatePmsMerchantInfo(originalInfo);
					// if (i > 0) {
					// logger.info("易宝*****实时入金完成");
					// } else {
					// logger.info("易宝*****实时入金失败");
					// }
					// }

				} else {
					result.put("v_status", "1001");
					result.put("v_msg", "支付失败");
					logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
							+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
				}
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				// 修改订单状态
				quickPayService.otherInvoke(consume);

				// 生成签名
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				result.put("v_sign", sign);

				logger.info("异步之前的参数：" + result);
				ConsumeResponseEntity consumeResponseEntity = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				logger.info("异步给下游传的数据参数：" + bean2Util.bean2QueryStr(consumeResponseEntity));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(),
						bean2Util.bean2QueryStr(consumeResponseEntity));
				logger.info("下游返回状态" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						logger.info("异步回馈的结果:" + "\t" + value);
						map.put("success", value);
					}
				}
				if (!result.get("success").equals("true")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(
							new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(consumeResponseEntity)));
				}
				logger.info("向下游 发送数据成功");

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
	 * 创新异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "cxNotifyUrl")
	public void cxNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("创新异步通知来了");
			String orderNo = request.getParameter("orderNo");
			String merchantNo = request.getParameter("merchantNo");
			String cxOrderNo = request.getParameter("cxOrderNo");
			String version = request.getParameter("version");
			String payChannelCode = request.getParameter("payChannelCode");
			String productName = request.getParameter("productName");
			String orderAmount = request.getParameter("orderAmount");
			String curCode = request.getParameter("curCode");
			String orderTime = request.getParameter("orderTime");
			String dealTime = request.getParameter("dealTime");
			String ext1 = request.getParameter("ext1");
			String dealCode = request.getParameter("dealCode");
			String fee = request.getParameter("fee");
			String dealMsg = request.getParameter("dealMsg");

			logger.info("返回参数：dealCode" + dealCode + ",dealMsg=" + dealMsg + ",orderNo=" + orderNo + ",merchantNo="
					+ merchantNo + ",cxOrderNo=" + cxOrderNo + ",orderAmount=" + orderAmount);
			Map<String, String> result = new HashMap<String, String>();
			logger.info("返回订单号：" + orderNo);
			if (!StringUtils.isEmpty(orderNo)) {
				response.getWriter().write("SUCCESS");
				logger.info("创新解密数据:" + orderNo);
				// 开始
				OriginalOrderInfo originalInfo = null;
				if (orderNo != null && orderNo != "") {
					originalInfo = this.quickPayService.getOriginOrderInfo(orderNo);
				}
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_attach", ext1);
				result.put("v_mid", originalInfo.getPid());
				if ("10000".equals(dealCode)) {

					result.put("v_status", "0000");
					result.put("v_msg", "支付成功");
					int i = quickPayService.UpdatePmsMerchantInfo(originalInfo);
					if (i > 0) {
						logger.info("创新*****实时入金完成");
					} else {
						logger.info("创新*****实时入金失败");
					}
				} else {
					result.put("v_status", "1001");
					result.put("v_msg", "支付失败:"+URLDecoder.decode(dealMsg, "UTF-8"));
					logger.info("交易错误码:" + dealMsg + ",错误信息:" + URLDecoder.decode(dealMsg, "UTF-8"));
				}
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				// 修改订单状态
				quickPayService.otherInvoke(consume);

				// 生成签名
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				result.put("v_sign", sign);

				logger.info("异步之前的参数：" + beanToMap(consume));
				ConsumeResponseEntity consumeResponseEntity = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				String html = HttpClientUtil.post(originalInfo.getBgUrl(),
						bean2Util.bean2QueryStr(consumeResponseEntity));
				logger.info("下游返回状态" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						logger.info("异步回馈的结果:" + "\t" + value);
						map.put("success", value);
					}
				}
				if (!result.get("success").equals("true")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(
							new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(consumeResponseEntity)));
				}
				logger.info("向下游 发送数据成功");

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
	 * 聚佰宝绑卡异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "jbbNotifyUrl")
	public void jbbNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("聚佰宝绑卡异步通知来了");
			String acctNo = request.getParameter("acctNo");
			String merOrderNum = request.getParameter("merOrderNum");
			String certNo = request.getParameter("certNo");
			String protocolNo = request.getParameter("protocolNo");
			String userPhone = request.getParameter("userPhone");
			String bankName = request.getParameter("bankName");
			String date = request.getParameter("date");
			String respMsg = request.getParameter("respMsg");
			String respCode = request.getParameter("respCode");
			logger.info("聚佰宝绑卡异步响应订单号：" + merOrderNum);
			logger.info("聚佰宝绑卡异步响应证件号：" + certNo);
			logger.info("聚佰宝绑卡异步响应协议号：" + protocolNo);
			logger.info("聚佰宝绑卡异步响应状态码：" + respCode);
			logger.info("聚佰宝绑卡异步响应状态码描述：" + respMsg);
			OriginalOrderInfo originalInfo = null;

			Map<String, String> result = new HashMap<String, String>();
			if (!StringUtils.isEmpty(protocolNo)) {
				if ("0000".equals(respCode)) {
					// 查询原始订单信息
					if (merOrderNum != null && merOrderNum != "") {
						originalInfo = this.quickPayService.getOriginOrderInfo(merOrderNum);
					}
					logger.info("聚佰宝绑卡异步订单数据:" + JSON.toJSON(originalInfo));
					PmsWeixinMerchartInfo merchartInfo = new PmsWeixinMerchartInfo();
					merchartInfo.setAccount(protocolNo);// 账号
					merchartInfo.setMerchartId(originalInfo.getPid());
					merchartInfo.setMerchartName(originalInfo.getProcdutName());
					merchartInfo.setMerchartNameSort(originalInfo.getProcdutDesc());
					merchartInfo.setCertNo(originalInfo.getCertNo());// 证件号
					merchartInfo.setCardNo(originalInfo.getBankNo());// 卡号
					merchartInfo.setRealName(originalInfo.getRealName());// 姓名
					merchartInfo.setMobile(originalInfo.getPhone());// 手机号
					merchartInfo.setoAgentNo("100333");
					int i = weixinService.updateRegister(merchartInfo);
					if (i > 0) {
						logger.info("##################聚佰宝获取短信接口##########");
						// 商户编号
						String merchantId = "888201711290115";
						// 业务代码
						String bussId = "ONL0003";
						Integer amount = (int) (Double.parseDouble(originalInfo.getOrderAmount()) * 100);

						String tranAmt = amount.toString();

						// 订单信息
						merOrderNum = originalInfo.getOrderId();
						// 签名数据
						String txnString = "bussId=" + bussId + "&merchantId=" + merchantId + "&merOrderNum="
								+ merOrderNum + "&protocolNo=" + protocolNo + "&tranAmt=" + tranAmt + "&";
						logger.info("聚佰宝上送的数据加密字符串:" + txnString);
						MD5 md = new MD5();
						String signValue = md.getMD5ofStr(txnString + "675FC1ctf2Y6zVm3");

						String txn = "bussId=" + bussId + "&merchantId=" + merchantId + "&merOrderNum=" + merOrderNum
								+ "&protocolNo=" + protocolNo + "&tranAmt=" + tranAmt.toString() + "&signValue="
								+ signValue;

						logger.info("聚佰宝上送的数据:" + txn);

						String url = "https://cashier.etonepay.com/NetPay/quickPaySms.action?" + txn;

						HttpURLConection http = new HttpURLConection();

						HttpUtil h = new HttpUtil();

						String resonpe = http.httpURLConnectionPOST("https://cashier.etonepay.com/NetPay/quickPaySms.action",
								txn);
						logger.info("响应结果:" + resonpe);
						net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(resonpe);
						Iterator it = ob.keys();
						String transId="";
						while (it.hasNext()) {
							String key = (String) it.next();
							if (key.equals("respCode")) {

								respCode = ob.getString(key);

								logger.info("聚佰宝签约响应状态码:" + respCode);

							}
							if (key.equals("merOrderNum")) {

								merOrderNum = ob.getString(key);

								logger.info("聚佰宝签约html:" + merOrderNum);
							}
							if (key.equals("transId")) {

								transId = ob.getString(key);

								logger.info("聚佰宝签约transId:" + transId);
							}
						}
						if ("0000".equals(respCode)) {
							OriginalOrderInfo info = new OriginalOrderInfo();
							info.setOrderId(merOrderNum);
							info.setByUser(transId);// 商户号
							info.setBankId(protocolNo);
							int number = originalOrderInfoService.update(info);
							if(number>0)
							{
								result.put("v_oid", originalInfo.getOrderId());
								result.put("v_txnAmt", originalInfo.getOrderAmount());
								result.put("v_code", "00");
								result.put("v_msg", "请求成功");
								result.put("v_time", originalInfo.getOrderTime());
								result.put("v_mid", originalInfo.getPid());
								// 查询商户密钥
								ChannleMerchantConfigKey keyinfo = quickPayService
										.getChannelConfigKey(originalInfo.getPid());
								// ------------------------需要改签名
								String merchantKey = keyinfo.getMerchantkey();
								MessAgeResponseEntity message = (MessAgeResponseEntity) BeanToMapUtil
										.convertMap(MessAgeResponseEntity.class, result);
								String sign = SignatureUtil.getSign(beanToMap(message), merchantKey, logger);
								result.put("v_sign", sign);
							}
						
						}

					}
				}

				logger.info("向下游 发送数据成功");

			} else {
				logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败:"+respMsg);
			}
			outString(response, gson.toJson(result));
		} catch (Exception e) {
			logger.info("聚佰宝异步回调异常:" + e);
			e.printStackTrace();
		}
	}

	/**
	 * 聚佰宝支付异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "payNotifyUrl")
	public void payNotifyUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("聚佰宝支付异步通知来了");
			String respCode = request.getParameter("respCode");
			String sysTraceNu = request.getParameter("sysTraceNu");
			String merOrderNum = request.getParameter("merOrderNum");
			logger.info("聚佰宝支付异步响应订单号：" + merOrderNum);
			logger.info("聚佰宝支付异步响应流水号：" + sysTraceNu);
			logger.info("聚佰宝支付异步响应状态码：" + respCode);
			OriginalOrderInfo originalInfo = null;

			Map<String, String> result = new HashMap<String, String>();
			if (!StringUtils.isEmpty(respCode)) {
				// 查询原始订单信息
				if (merOrderNum != null && merOrderNum != "") {
					originalInfo = this.quickPayService.getOriginOrderInfo(merOrderNum);
				}
				logger.info("聚佰宝绑卡异步订单数据:" + JSON.toJSON(originalInfo));
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_attach", originalInfo.getAttach());
				result.put("v_mid", originalInfo.getPid());
				result.put("v_time", UtilDate.getTXDateTime());
				if ("0000".equals(respCode)) {

					result.put("v_status", "0000");
					result.put("v_msg", "支付成功");
					// if ("0".equals(originalInfo.getPayType())) {
					// int i = imbService.UpdatePmsMerchantInfo(originalInfo);
					// if (i > 0) {
					// logger.info("易宝*****实时入金完成");
					// } else {
					// logger.info("易宝*****实时入金失败");
					// }
					// }

				} else {
					result.put("v_status", "1001");
					result.put("v_msg", "支付失败");
					logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
							+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
				}
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				// 修改订单状态
				quickPayService.otherInvoke(consume);

				// 生成签名
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				result.put("v_sign", sign);

				logger.info("异步之前的参数：" + result);
				ConsumeResponseEntity consumeResponseEntity = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				logger.info("异步给下游传的数据参数：" + bean2Util.bean2QueryStr(consumeResponseEntity));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(),
						bean2Util.bean2QueryStr(consumeResponseEntity));
				logger.info("下游返回状态" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						logger.info("异步回馈的结果:" + "\t" + value);
						map.put("success", value);
					}
				}
				if (!result.get("success").equals("true")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(
							new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(consumeResponseEntity)));
				}

				logger.info("向下游 发送数据成功");

			} else {
				logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}
			outString(response, gson.toJson(result));
		} catch (Exception e) {
			logger.info("聚佰宝异步回调异常:" + e);
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
			logger.info("银生宝支付异步通知来了");
			String respCode = request.getParameter("respCode");
			String sysTraceNu = request.getParameter("sysTraceNu");
			String merOrderNum = request.getParameter("merOrderNum");
			logger.info("聚佰宝支付异步响应订单号：" + merOrderNum);
			logger.info("聚佰宝支付异步响应流水号：" + sysTraceNu);
			logger.info("聚佰宝支付异步响应状态码：" + respCode);
			OriginalOrderInfo originalInfo = null;

			Map<String, String> result = new HashMap<String, String>();
			if (!StringUtils.isEmpty(respCode)) {
				// 查询原始订单信息
				if (merOrderNum != null && merOrderNum != "") {
					originalInfo = this.quickPayService.getOriginOrderInfo(merOrderNum);
				}
				logger.info("聚佰宝绑卡异步订单数据:" + JSON.toJSON(originalInfo));
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_attach", originalInfo.getAttach());
				result.put("v_mid", originalInfo.getPid());
				result.put("v_time", UtilDate.getTXDateTime());
				if ("0000".equals(respCode)) {

					result.put("v_status", "0000");
					result.put("v_msg", "支付成功");
					// if ("0".equals(originalInfo.getPayType())) {
					// int i = imbService.UpdatePmsMerchantInfo(originalInfo);
					// if (i > 0) {
					// logger.info("易宝*****实时入金完成");
					// } else {
					// logger.info("易宝*****实时入金失败");
					// }
					// }

				} else {
					result.put("v_status", "1001");
					result.put("v_msg", "支付失败");
					logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
							+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
				}
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				// 修改订单状态
				quickPayService.otherInvoke(consume);

				// 生成签名
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				result.put("v_sign", sign);

				logger.info("异步之前的参数：" + result);
				ConsumeResponseEntity consumeResponseEntity = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				logger.info("异步给下游传的数据参数：" + bean2Util.bean2QueryStr(consumeResponseEntity));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(),
						bean2Util.bean2QueryStr(consumeResponseEntity));
				logger.info("下游返回状态" + html);
				JSONObject ob = JSONObject.fromObject(html);
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					String keys = (String) it.next();
					if (keys.equals("success")) {
						String value = ob.getString(keys);
						logger.info("异步回馈的结果:" + "\t" + value);
						map.put("success", value);
					}
				}
				if (!result.get("success").equals("true")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(
							new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(consumeResponseEntity)));
				}

				logger.info("向下游 发送数据成功");

			} else {
				logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}
			outString(response, gson.toJson(result));
		} catch (Exception e) {
			logger.info("聚佰宝异步回调异常:" + e);
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
		logger.info("魔宝前台通知进来了，参数：" + JSON.toJSON(mbResponse));
		TreeMap<String, String> result = new TreeMap<>();
		try {

			mbResponse.setRefcode(request.getParameter("payStatus"));
			mbResponse.setOrderId(request.getParameter("orderId"));
			mbResponse.setRefMsg(URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
			if (mbResponse != null) {
				// outString(response, str);
				OriginalOrderInfo originalInfo = null;
				if (mbResponse.getOrderId() != null && mbResponse.getOrderId() != "") {
					originalInfo = this.quickPayService.getOriginOrderInfo(mbResponse.getOrderId());
				}
				logger.info("订单数据**魔宝:" + JSON.toJSON(originalInfo));
				Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				logger.info("下游的同步地址**魔宝" + originalInfo.getBgUrl());
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_mid", originalInfo.getPid());
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				result.put("v_sign", sign);
				String params = HttpURLConection.parseParams(result);
				logger.info("给下游同步的数据:" + params);
				request.getSession();
				try {
					// 给下游手动返回支付结果
					if (originalInfo.getPageUrl().indexOf("?") == -1) {

						String path = originalInfo.getPageUrl() + "?" + params;
						logger.info("pageUrl 商户页面 重定向：" + path);

						response.sendRedirect(path.replace(" ", ""));
					} else {
						logger.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
						String path = originalInfo.getPageUrl() + "&" + params;
						logger.info("pageUrl 商户页面 重定向：" + path);
						response.sendRedirect(path.replace(" ", ""));
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else {
				logger.info("没有收到魔宝的同步数据");
				// outString(response, str);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 赢酷同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "lhzfReturnUrl")
	public void lhzfReturnUrl(HttpServletRequest request, LhzfResponse lhzfResponse, HttpServletResponse response) {
		try {
			logger.info("易宝同步数据返回参数:" + JSON.toJSONString(lhzfResponse));
			LhzfResponse lhzfResponses = new LhzfResponse();
			OriginalOrderInfo originalInfo = null;
			if (lhzfResponse.getOrderNo() != null && lhzfResponse.getOrderNo() != "") {
				originalInfo = this.quickPayService.getOriginOrderInfo(lhzfResponse.getOrderNo());
			}
			logger.info("订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			logger.info("下游的异步地址" + originalInfo.getBgUrl());
			logger.info("易宝同步返回解析参数" + JSON.toJSON(lhzfResponse));
			if (!StringUtils.isEmpty(lhzfResponse.getRespCode())) {
				// 和下面的签名
				// ---------------------------------------------------
				TreeMap<String, String> result = new TreeMap<String, String>();
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_mid", originalInfo.getPid());
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				result.put("v_sign", sign);
				String params = HttpURLConection.parseParams(result);
				logger.info("给下游同步的数据:" + params);
				request.getSession();
				try {
					// 给下游手动返回支付结果
					if (originalInfo.getPageUrl().indexOf("?") == -1) {

						String path = originalInfo.getPageUrl() + "?" + params;
						logger.info("pageUrl 商户页面 重定向：" + path);

						response.sendRedirect(path.replace(" ", ""));
					} else {
						logger.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
						String path = originalInfo.getPageUrl() + "&" + params;
						logger.info("pageUrl 商户页面 重定向：" + path);
						response.sendRedirect(path.replace(" ", ""));
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			} else {
				logger.info("没有收到蓝海的同步数据");
				// outString(response, str);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

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
			logger.info("易宝同步数据返回参数:");
			String orderId = request.getParameter("orderId");

			OriginalOrderInfo originalInfo = null;
			if (orderId != null && orderId != "") {
				originalInfo = this.quickPayService.getOriginOrderInfo(orderId);
			}
			logger.info("订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			logger.info("下游的同步地址" + originalInfo.getPageUrl());
			TreeMap<String, String> result = new TreeMap<String, String>();
			String params = "";
			if (!StringUtils.isEmpty(orderId)) {
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_mid", originalInfo.getPid());
				ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
						.convertMap(ConsumeResponseEntity.class, result);
				String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
				result.put("v_sign", sign);
				params = HttpURLConection.parseParams(result);
				logger.info("给下游同步的数据:" + params);
				request.getSession();
				try {
					// 给下游手动返回支付结果
					if (originalInfo.getPageUrl().indexOf("?") == -1) {

						String path = originalInfo.getPageUrl() + "?" + params;
						logger.info("pageUrl 商户页面 重定向：" + path);

						response.sendRedirect(path.replace(" ", ""));
					} else {
						logger.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
						String path = originalInfo.getPageUrl() + "&" + params;
						logger.info("pageUrl 商户页面 重定向：" + path);
						response.sendRedirect(path.replace(" ", ""));
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			} else {
				logger.info("没有收到易宝的同步数据");
				// outString(response, str);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 沈阳银盛同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "syysReturnUrl")
	public void syysReturnUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("沈阳银盛同步通知来了");
			String orderId = request.getParameter("order_num");
			logger.info("沈阳银盛同步返回的订单号:" + orderId);
			OriginalOrderInfo originalInfo = Origi.get(orderId);
			logger.info("沈阳银盛绑卡同步订单数据:" + JSON.toJSON(originalInfo));
			Thread.currentThread().sleep(3000);
			Map<String, String> requestMap = new HashMap<String, String>();
			requestMap.put("order_num", orderId); // 流水号
			requestMap.put("app_id", originalInfo.getByUser()); // appId
			requestMap.put("auth_id", originalInfo.getSumCode()); // 银行账号
			Double amount = Double.parseDouble(originalInfo.getOrderAmount());
			Integer num = amount.intValue();
			requestMap.put("amount", num.toString());
			requestMap.put("encrypt", "T0"); // 卡类

			String key = "6c67b76e651e4c49bda4874289a187d2";// md5key
			// 得到带签名数据
			Map<String, ?> filterMap = PayCore.paraFilter(requestMap);

			String linkStr = PayCore.createLinkString(filterMap);
			logger.info("沈阳银盛绑卡异步签名公钥" + key);
			logger.info("沈阳银盛绑卡异步待签数据" + linkStr);
			String hexSign = PayCore.md5Sign(linkStr, key);
			logger.info("沈阳银盛绑卡异步签名数据:" + hexSign);
			requestMap.put("sign_type", "MD5"); // md5签名
			requestMap.put("sign_info", hexSign);
			String requestStr = JSON.toJSONString(requestMap);

			String url = "http://test.unvpay.com/pay-adapter/services/fastpay/submitOrder";
			String respStr = xdt.quickpay.syys.HttpClientUtil.post(url, "UTF-8", requestStr);

			logger.info("沈阳银盛绑卡短信返回值：" + respStr);
			com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(respStr);
			if ("0000".equals(json.getString("ret_code"))) {

				com.alibaba.fastjson.JSONObject jb = json.getJSONObject("ret_data");
				String param = jb.getString("parmMap");
				logger.info("沈阳银盛短信返回的ret_data：" + param);
				com.alibaba.fastjson.JSONObject jb1 = com.alibaba.fastjson.JSONObject.parseObject(param);
				String token = jb1.getString("token");
				logger.info("沈阳银盛短信返回的token：" + token);
				OriginalOrderInfo oo = new OriginalOrderInfo();
				oo.setOrderId(orderId);
				oo.setSumCode(token);
				oo.setByUser(originalInfo.getByUser());
				;
				oo.setProcdutNum("0000");
				num = Origi.update(oo);
				if (num > 0) {
					TreeMap<String, String> result = new TreeMap<String, String>();
					ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
					// 获取商户秘钥
					key = keyinfo.getMerchantkey();
					result.put("v_oid", originalInfo.getOrderId());
					result.put("v_txnAmt", originalInfo.getOrderAmount());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					result.put("v_time", originalInfo.getOrderTime());
					result.put("v_mid", originalInfo.getPid());
					ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
							.convertMap(ConsumeResponseEntity.class, result);
					String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
					result.put("v_sign", sign);
					String params = HttpURLConection.parseParams(result);
					logger.info("给下游同步的数据:" + params);
					request.getSession();
					try {
						// 给下游手动返回支付结果
						if (originalInfo.getPageUrl().indexOf("?") == -1) {

							String path = originalInfo.getPageUrl() + "?" + params;
							logger.info("pageUrl 商户页面 重定向：" + path);

							response.sendRedirect(path.replace(" ", ""));
						} else {
							logger.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
							String path = originalInfo.getPageUrl() + "&" + params;
							logger.info("pageUrl 商户页面 重定向：" + path);
							response.sendRedirect(path.replace(" ", ""));
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 聚佰宝同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "payReturnUrl")
	public void payReturnUrl(HttpServletRequest request, HttpServletResponse response) {
		try {
			logger.info("聚佰宝同步通知来了");
			String orderId = request.getParameter("merOrderNum");
			logger.info("聚佰宝同步返回的订单号:" + orderId);
			OriginalOrderInfo originalInfo = Origi.get(orderId);
			logger.info("聚佰宝同步订单数据:" + JSON.toJSON(originalInfo));

			TreeMap<String, String> result = new TreeMap<String, String>();
			ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(originalInfo.getPid());
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			result.put("v_oid", originalInfo.getOrderId());
			result.put("v_txnAmt", originalInfo.getOrderAmount());
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_time", originalInfo.getOrderTime());
			result.put("v_mid", originalInfo.getPid());
			ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil
					.convertMap(ConsumeResponseEntity.class, result);
			String sign = SignatureUtil.getSign(beanToMap(consume), key, logger);
			result.put("v_sign", sign);
			String params = HttpURLConection.parseParams(result);
			logger.info("给下游同步的数据:" + params);
			request.getSession();
			try {
				// 给下游手动返回支付结果
				if (originalInfo.getPageUrl().indexOf("?") == -1) {

					String path = originalInfo.getPageUrl() + "?" + params;
					logger.info("pageUrl 商户页面 重定向：" + path);

					response.sendRedirect(path.replace(" ", ""));
				} else {
					logger.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
					String path = originalInfo.getPageUrl() + "&" + params;
					logger.info("pageUrl 商户页面 重定向：" + path);
					response.sendRedirect(path.replace(" ", ""));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * bean 转化为实体
	 * 
	 * @param bean
	 * @return
	 */
	public static HashMap<String, Object> beanToMap(Object bean) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (null == bean) {
			return map;
		}
		Class<?> clazz = bean.getClass();
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor descriptor : descriptors) {
			String propertyName = descriptor.getName();
			if (!"class".equals(propertyName)) {
				Method method = descriptor.getReadMethod();
				String result;
				try {
					result = (String) method.invoke(bean);
					if (null != result) {
						map.put(propertyName, result);
					} else {
						map.put(propertyName, "");
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return map;
	}

	public static Object convertMap(Class type, Map map)
			throws IntrospectionException, IllegalAccessException, InstantiationException, InvocationTargetException {
		BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
		Object obj = type.newInstance(); // 创建 JavaBean 对象

		// 给 JavaBean 对象的属性赋值
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor descriptor = propertyDescriptors[i];
			String propertyName = descriptor.getName();

			if (map.containsKey(propertyName)) {
				// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
				Object value = map.get(propertyName);

				Object[] args = new Object[1];
				args[0] = value;

				descriptor.getWriteMethod().invoke(obj, args);
			}
		}
		return obj;
	}

	public void write(HttpServletResponse response, String params) throws IOException {
		PrintWriter out = null;

		out = response.getWriter();

		out.print(params);
	}

	@RequestMapping(value = "quickVerifyId")
	public void quickVerifyId(MessageRequestEntity entity, HttpServletResponse response) {

		logger.info("裕福查询卡进来了");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();
		Map<String, Object> maps = new HashMap<>();
		try {
			logger.info("下游上送参数:{}"+JSON.toJSONString(entity));
			if (!StringUtils.isEmpty(entity.getV_mid())) {
				logger.info("下游上送签名串{}" + entity.getV_sign());
				// 查询商户密钥
				ChannleMerchantConfigKey keyinfo = quickPayService.getChannelConfigKey(entity.getV_mid());
				// ------------------------需要改签名
				String merchantKey = keyinfo.getMerchantkey();
				SignatureUtil signUtil = new SignatureUtil();

				Map map = BeanToMapUtil.convertBean(entity);

				if (signUtil.checkSign(map, merchantKey, logger)) {
					logger.info("对比签名成功");
					result = quickPayService.selectCard(entity);
					logger.info("短信响应信息:" + result);
					maps.putAll(result);
					String sign = SignatureUtil.getSign(maps, merchantKey, logger);
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
			outString(response, JSON.toJSON(result));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}

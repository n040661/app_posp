/**
 * 
 */
package xdt.controller;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PospTransInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.hf.comm.PlatOutboundMsg;
import xdt.quickpay.hf.comm.SampleConstant;
import xdt.quickpay.hf.entity.PayQueryRequestEntity;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.hf.entity.PayResponseEntity;
import xdt.quickpay.hf.util.EffersonPayService;
import xdt.quickpay.hf.util.PlatBase64Utils;
import xdt.quickpay.hf.util.PlatKeyGenerator;
import xdt.quickpay.hf.util.PreSignUtil;
import xdt.service.HfQPayService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

/**
 * @ClassName: HFQuickPayAction
 * @Description: 恒丰 快捷支付
 * @author LiShiwen
 * @date 2016年6月14日 下午1:43:10
 *
 */
@Controller
@RequestMapping("hfquick")
public class HFQPayAction extends BaseAction {

	/**
	 * 日志记录
	 */
	private Logger log = Logger.getLogger(HFQPayAction.class);

	// T1获取token地址
	private static final String xsnanopay_url1 = "rytpay/unionpay/wtz/token.do?api/v1/open/card/front/service";

	// T1获取验证码地址
	private static final String message_url1 = "rytpay/unionpay/wtz/token.do?api/v1/consume/sms/service";

	// T1获取支付地址
	private static final String consume_url1 = "rytpay/unionpay/wtz/token.do?api/v1/consume/service";

	// T1查询支付地址
	private static final String query_url1 = "rytpay/unionpay/wtz/token.do?api/v1/order/query/service";

	// T0获取token地址
	private static final String xsnanopay_url0 = "rytpay/unionpay/wtz/token/to.do?api/v1/open/card/front/service";

	// T0获取验证码地址
	private static final String message_url0 = "rytpay/unionpay/wtz/token/to.do?api/v1/consume/sms/service";

	// T0获取支付地址
	private static final String consume_url0 = "rytpay/unionpay/wtz/token/to.do?api/v1/consume/service";

	// T0查询支付地址
	private static final String query_url0 = "rytpay/unionpay/wtz/token/to.do?api/v1/order/query/service";

	@Resource
	private HfQPayService payService;

	// /**
	// * 和上游交互
	// *
	// * @param param
	// * 获取token信息
	// * @param request
	// * 请求对象
	// * @param response
	// * 响应对象
	// * @throws Exception
	// *
	// */
	// @RequestMapping(value = "hfpay")
	// public void pay(PayRequestEntity temp, HttpServletRequest request,
	// HttpServletResponse response) throws Exception {
	// // 原始数据交易id
	// String originalOrderId = temp.getOrderId();
	// logger.info("原始订单号:" + originalOrderId);
	// PayRequestEntity param = new PayRequestEntity();// 上送参数
	//
	// // 所有的流程通过 就发起支付 上送数据
	// String json = payService.payHandle(temp);
	//
	// SubmitOrderNoCardPayResponseDTO respDto = new Gson().fromJson(json,
	// SubmitOrderNoCardPayResponseDTO.class);
	//
	// log.info("支付…………");
	//
	// log.info("支付上送原始信息");
	//
	// log.info(temp);
	//
	// if (0 != respDto.getRetCode()) {
	// // 返回页面参数
	// Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
	// response.sendRedirect(temp.getFrontUrl() + "?" +
	// queryUtil.bean2QueryStr(temp));
	// } else {
	//
	// OriginalOrderInfo queryWhere = new OriginalOrderInfo();
	// queryWhere.setMerchantOrderId(originalOrderId);
	// queryWhere.setPid(temp.getMerchantId());
	// Map<String, String> params = new HashMap<String, String>();
	//
	// // 设置上送信息
	// if (temp.getTranTp().equals("1")) {
	// params.put("appId", SampleConstant.APP_ID1);
	// params.put("appCode", SampleConstant.APP_CODE1);
	//
	// } else if (temp.getTranTp().equals("0")) {
	// params.put("appId", SampleConstant.APP_ID0);
	// params.put("appCode", SampleConstant.APP_CODE0);
	// }
	//
	// params.put("orderId", temp.getOrderId());
	// params.put("txnTime", temp.getTxnTime());
	// params.put("accNo", temp.getAccNo());
	// params.put("frontUrl",
	// "http://60.28.24.164:8102/app_posp/hfquick/hfpagePayResult.action");
	//
	// if (temp.getTranTp().equals("1")) {
	// params.put("backUrl", SampleConstant.BACK_URLT1);
	// } else if (temp.getTranTp().equals("0")) {
	// params.put("backUrl", SampleConstant.BACK_URLT0);
	// }
	//
	// String jsonString = JSON.toJSONString(params);
	// System.out.println(jsonString);
	// log.info("上送的数据:" + jsonString);
	// byte[] encodeData = null;
	// log.info("交易类型:" + temp.getTranTp());
	// if (temp.getTranTp().equals("1")) {
	//
	// encodeData =
	// PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
	// SampleConstant.PUB_KEY1);
	// } else if (temp.getTranTp().equals("0")) {
	//
	// encodeData =
	// PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
	// SampleConstant.PUB_KEY0);
	// }
	//
	// String data = PlatBase64Utils.encode(encodeData);
	//
	// Map<String, String> result = new HashMap<String, String>();
	// result.put("data", data);
	// if (temp.getTranTp().equals("1")) {
	// result.put("appId", SampleConstant.APP_ID1);
	// } else if (temp.getTranTp().equals("0")) {
	// result.put("appId", SampleConstant.APP_ID0);
	// }
	// String html = "";
	// if (temp.getTranTp().equals("1")) {
	// html = EffersonPayService.createAutoFormHtml(SampleConstant.REMOTE_PATH +
	// xsnanopay_url1, result,
	// "UTF-8");
	// } else if (temp.getTranTp().equals("0")) {
	// html = EffersonPayService.createAutoFormHtml(SampleConstant.REMOTE_PATH +
	// xsnanopay_url0, result,
	// "UTF-8");
	// }
	//
	// response.setContentType("text/html");
	// PrintWriter out = response.getWriter();
	// HttpSession seesion = request.getSession();
	// seesion.setAttribute("html", html);
	// out.println(html);
	// out.flush();
	// out.close();
	// }
	// }
	//
	// /**
	// * 和上游交互
	// *
	// * @param param
	// * 获取token信息
	// * @param request
	// * 请求对象
	// * @param response
	// * 响应对象
	// * @throws Exception
	// *
	// */
	// @RequestMapping(value = "hfpay1")
	// public void pay1(PayRequestEntity temp, HttpServletRequest request,
	// HttpServletResponse response) throws Exception {
	// // 原始数据交易id
	// String originalOrderId = temp.getOrderId();
	// logger.info("原始订单号:" + originalOrderId);
	// PayRequestEntity param = new PayRequestEntity();// 上送参数
	//
	// // 所有的流程通过 就发起支付 上送数据
	// String json = payService.payHandle1(temp);
	//
	// SubmitOrderNoCardPayResponseDTO respDto = new Gson().fromJson(json,
	// SubmitOrderNoCardPayResponseDTO.class);
	//
	// log.info("支付…………");
	//
	// log.info("支付上送原始信息");
	//
	// log.info(temp);
	//
	// if (0 != respDto.getRetCode()) {
	// // 返回页面参数
	// Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
	// response.sendRedirect(temp.getFrontUrl() + "?" +
	// queryUtil.bean2QueryStr(temp));
	// } else {
	//
	// OriginalOrderInfo queryWhere = new OriginalOrderInfo();
	// queryWhere.setMerchantOrderId(originalOrderId);
	// queryWhere.setPid(temp.getMerchantId());
	// Map<String, String> params = new HashMap<String, String>();
	//
	// // 设置上送信息
	// if (temp.getTranTp().equals("1")) {
	// params.put("appId", SampleConstant.APP_ID1);
	// params.put("appCode", SampleConstant.APP_CODE1);
	//
	// } else if (temp.getTranTp().equals("0")) {
	// params.put("appId", SampleConstant.APP_ID0);
	// params.put("appCode", SampleConstant.APP_CODE0);
	// }
	//
	// params.put("orderId", temp.getOrderId());
	// params.put("txnTime", temp.getTxnTime());
	// params.put("accNo", temp.getAccNo());
	// params.put("frontUrl",
	// "http://221.122.101.25:8109/app_posp/hfquick/hfpagePayResult.action");
	//
	// if (temp.getTranTp().equals("1")) {
	// params.put("backUrl", SampleConstant.BACK_URLT1);
	// } else if (temp.getTranTp().equals("0")) {
	// params.put("backUrl", SampleConstant.BACK_URLT0);
	// }
	//
	// String jsonString = JSON.toJSONString(params);
	// System.out.println(jsonString);
	// log.info("上送的数据:" + jsonString);
	// byte[] encodeData = null;
	// log.info("交易类型:" + temp.getTranTp());
	// if (temp.getTranTp().equals("1")) {
	//
	// encodeData =
	// PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
	// SampleConstant.PUB_KEY1);
	// } else if (temp.getTranTp().equals("0")) {
	//
	// encodeData =
	// PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
	// SampleConstant.PUB_KEY0);
	// }
	//
	// String data = PlatBase64Utils.encode(encodeData);
	//
	// Map<String, String> result = new HashMap<String, String>();
	// result.put("data", data);
	// if (temp.getTranTp().equals("1")) {
	// result.put("appId", SampleConstant.APP_ID1);
	// } else if (temp.getTranTp().equals("0")) {
	// result.put("appId", SampleConstant.APP_ID0);
	// }
	// String html = "";
	// if (temp.getTranTp().equals("1")) {
	// html = EffersonPayService.createAutoFormHtml(SampleConstant.REMOTE_PATH +
	// xsnanopay_url1, result,
	// "UTF-8");
	// } else if (temp.getTranTp().equals("0")) {
	// html = EffersonPayService.createAutoFormHtml(SampleConstant.REMOTE_PATH +
	// xsnanopay_url0, result,
	// "UTF-8");
	// }
	//
	// response.setContentType("text/html");
	// PrintWriter out = response.getWriter();
	// out.println(html);
	// out.flush();
	// out.close();
	// }
	// }
	//
	// /**
	// * 和上游交互
	// *
	// * @param param
	// * 获取短信验证码
	// * @param request
	// * 请求对象
	// * @param response
	// * 响应对象
	// * @throws Exception
	// *
	// */
	// @RequestMapping(value = "hfmessage")
	// public void hfmessage(PayRequestEntity temp, HttpServletRequest request,
	// HttpServletResponse response)
	// throws Exception {
	// // 原始数据交易id
	// String originalOrderId = temp.getOrderId();
	//
	// PayRequestEntity param = new PayRequestEntity();// 上送参数
	//
	// // 所有的流程通过 就发起支付 上送数据
	// String json1 = payService.pay(temp);
	//
	// SubmitOrderNoCardPayResponseDTO respDto = new Gson().fromJson(json1,
	// SubmitOrderNoCardPayResponseDTO.class);
	//
	// log.info("支付…………");
	//
	// log.info("支付上送原始信息");
	//
	// log.info(temp);
	//
	// if (0 != respDto.getRetCode()) {
	// // 返回页面参数
	// Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
	// response.sendRedirect(temp.getFrontUrl() + "?" +
	// queryUtil.bean2QueryStr(temp));
	// } else {
	//
	// OriginalOrderInfo queryWhere = new OriginalOrderInfo();
	// queryWhere.setMerchantOrderId(originalOrderId);
	// queryWhere.setPid(temp.getMerchantId());
	// // 上送原始记录信息
	// OriginalOrderInfo originInfo = payService.selectByOriginal(queryWhere);
	// // 本地订单id
	// String orderId = originInfo.getOrderId();
	// // 流水信息
	// PospTransInfo transinfo = payService.getTransInfo(orderId);
	// // 上送订单id
	// String transOrderId = transinfo.getOrderId();
	//
	// // 查询费率
	// PmsAppTransInfo app = payService.getFeeInfo(transOrderId);
	//
	// String fee = app.getPoundage();
	// DecimalFormat df = new DecimalFormat("#.#");
	//
	// String fee1 = df.format(Double.parseDouble(fee));
	// // 设置上送的手续费
	// String userfee = "";
	// if (!"".equals(temp.getUserfee()) && temp.getUserfee() != null) {
	// if (Double.parseDouble(fee1) >= Double.parseDouble(temp.getUserfee())) {
	// userfee = temp.getUserfee();
	// } else {
	//
	// userfee = fee1;
	// }
	// log.info("上送的手续费:" + userfee);
	//
	// }
	//
	// Map<String, String> params = new HashMap<String, String>();
	//
	// // 设置上送信息
	// if (temp.getTranTp().equals("1")) {
	// params.put("appId", SampleConstant.APP_ID1);
	// params.put("appCode", SampleConstant.APP_CODE1);
	// } else if (temp.getTranTp().equals("0")) {
	// params.put("appId", SampleConstant.APP_ID0);
	// params.put("appCode", SampleConstant.APP_CODE0);
	// params.put("name", temp.getName());
	// params.put("certNo", temp.getCertNo());
	// params.put("toBankNo", temp.getAccNo());
	// params.put("userfee", userfee);
	//
	// }
	// params.put("orderId", temp.getOrderId());
	// params.put("txnTime", temp.getTxnTime());
	// params.put("txnAmt", temp.getTxnAmt());
	// params.put("phoneNo", temp.getPhoneNo());
	// params.put("token", temp.getToken());
	//
	// String jsonString = JSON.toJSONString(params);
	// log.info("上送的数据:" + jsonString);
	// byte[] encodeData = null;
	// if (temp.getTranTp().equals("1")) {
	// encodeData =
	// PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
	// SampleConstant.PUB_KEY1);
	// } else if (temp.getTranTp().equals("0")) {
	// encodeData =
	// PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
	// SampleConstant.PUB_KEY0);
	// }
	// String data = PlatBase64Utils.encode(encodeData);
	//
	// Map<String, String> result = new HashMap<String, String>();
	// result.put("data", data);
	// if (temp.getTranTp().equals("1")) {
	// result.put("appId", SampleConstant.APP_ID1);
	// } else if (temp.getTranTp().equals("0")) {
	// result.put("appId", SampleConstant.APP_ID0);
	// }
	//
	// // 设置转发页面
	// String returnJson = "";
	// if (temp.getTranTp().equals("1")) {
	// returnJson = EffersonPayService.postAsString(result,
	// SampleConstant.REMOTE_PATH + message_url1,
	// "UTF-8");
	// } else if (temp.getTranTp().equals("0")) {
	// returnJson = EffersonPayService.postAsString(result,
	// SampleConstant.REMOTE_PATH + message_url0,
	// "UTF-8");
	// }
	// log.info("响应信息:" + returnJson);
	// PlatOutboundMsg json = JSON.parseObject(returnJson,
	// PlatOutboundMsg.class);
	// log.info("json:" + JSON.toJSON(json));
	// if (json != null) {
	// if (!json.isSuccess()) {
	// response.setContentType("text/html");
	// response.setCharacterEncoding("UTF-8");
	// PrintWriter out = response.getWriter();
	// out.println(json.getMsg());
	// out.flush();
	// out.close();
	// } else {
	// Map<String, Object> returnMap = json.getAttributes();
	// log.info("returnMap:" + JSON.toJSONString(returnMap));
	// if (!returnMap.isEmpty()) {// responseMessage
	// if (!returnMap.isEmpty() && "00".equals(returnMap.get("respCode"))) {
	// log.info("真的进来了！！");
	// returnMap.put("merid", temp.getMerchantId());
	// returnMap.put("orderId", temp.getOrderId());
	// returnMap.put("txnTime", temp.getTxnTime());
	// returnMap.put("txnAmt", temp.getTxnAmt());
	// returnMap.put("token", temp.getToken());
	// // returnMap.put("token", temp.getToken());
	// /*
	// * response.setContentType("text/html"); PrintWriter
	// * out = response.getWriter();
	// * out.println(JSON.toJSON(returnMap)); out.flush();
	// * out.close();
	// */
	// outString(response, JSON.toJSON(returnMap));
	// } else {
	// /*
	// * response.setContentType("text/html"); PrintWriter
	// * out = response.getWriter(); out.println((String)
	// * json.getAttributes().get("respMsg"));
	// * out.flush(); out.close();
	// */
	// returnMap.put("respMsg", "操作频繁");
	// outString(response, returnMap);
	// }
	// }
	// }
	// } else {
	// response.setContentType("text/html");
	// PrintWriter out = response.getWriter();
	// out.println(json);
	// out.flush();
	// out.close();
	// }
	// }
	// }

	// /**
	// * 和上游交互
	// *
	// * @param param
	// * 支付信息
	// * @param request
	// * 请求对象
	// * @param response
	// * 响应对象
	// * @throws Exception
	// *
	// */
	// @RequestMapping(value = "hfconsume")
	// public void consume(PayRequestEntity temp, HttpServletRequest request,
	// HttpServletResponse response)
	// throws Exception {
	// // 原始数据交易id
	// String originalOrderId = temp.getOrderId();
	//
	// PayRequestEntity param = new PayRequestEntity();// 上送参数
	//
	// // 所有的流程通过 就发起支付 上送数据
	// String json = payService.consumepayHandle(temp);
	//
	// SubmitOrderNoCardPayResponseDTO respDto = new Gson().fromJson(json,
	// SubmitOrderNoCardPayResponseDTO.class);
	//
	// log.info("支付…………");
	//
	// log.info("支付上送原始信息");
	//
	// log.info(temp);
	//
	// if (0 != respDto.getRetCode()) {
	// // 返回页面参数
	// Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
	// response.sendRedirect(temp.getFrontUrl() + "?" +
	// queryUtil.bean2QueryStr(temp));
	// } else {
	//
	// OriginalOrderInfo queryWhere = new OriginalOrderInfo();
	// queryWhere.setMerchantOrderId(originalOrderId);
	// queryWhere.setPid(temp.getMerchantId());
	// Map<String, String> params = new HashMap<String, String>();
	//
	// // 设置上送信息
	// if (temp.getTranTp().equals("1")) {
	// params.put("appId", SampleConstant.APP_ID1);
	// params.put("appCode", SampleConstant.APP_CODE1);
	// } else if (temp.getTranTp().equals("0")) {
	// params.put("appId", SampleConstant.APP_ID0);
	// params.put("appCode", SampleConstant.APP_CODE0);
	// }
	// params.put("orderId", temp.getOrderId());
	// params.put("txnTime", temp.getTxnTime());
	// params.put("txnAmt", temp.getTxnAmt());
	// params.put("token", temp.getToken());
	// params.put("smsCode", temp.getSmsCode());
	// if (temp.getTranTp().equals("1")) {
	// params.put("backUrl", SampleConstant.BACK_URLT1);
	// } else if (temp.getTranTp().equals("0")) {
	// params.put("backUrl", SampleConstant.BACK_URLT0);
	// }
	//
	// String jsonString = JSON.toJSONString(params);
	// System.out.println(jsonString);
	// log.info("上送的数据:" + jsonString);
	//
	// byte[] encodeData = null;
	// if (temp.getTranTp().equals("1")) {
	// encodeData =
	// PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
	// SampleConstant.PUB_KEY1);
	// } else if (temp.getTranTp().equals("0")) {
	// encodeData =
	// PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"),
	// SampleConstant.PUB_KEY0);
	// }
	// String data = PlatBase64Utils.encode(encodeData);
	//
	// Map<String, String> result = new HashMap<String, String>();
	// result.put("data", data);
	// if (temp.getTranTp().equals("1")) {
	// result.put("appId", SampleConstant.APP_ID1);
	// } else if (temp.getTranTp().equals("0")) {
	// result.put("appId", SampleConstant.APP_ID0);
	// }
	// System.out.println(result);
	// String html = "";
	// if (temp.getTranTp().equals("1")) {
	// html = EffersonPayService.postAsString(result, SampleConstant.REMOTE_PATH
	// + consume_url1, "UTF-8");
	// } else if (temp.getTranTp().equals("0")) {
	// html = EffersonPayService.postAsString(result, SampleConstant.REMOTE_PATH
	// + consume_url0, "UTF-8");
	// }
	// /*
	// * response.setContentType("text/html"); PrintWriter out =
	// * response.getWriter(); out.println(html); out.flush();
	// * out.close();
	// */
	// outString(response, html);
	// }
	// }
	/**
	 * 和上游交互
	 * 
	 * @param param
	 *            支付信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value = "hfconsume")
	public void consume(PayRequestEntity temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// 原始数据交易id
		String originalOrderId = temp.getOrderId();

		PayRequestEntity param = new PayRequestEntity();// 上送参数

		// 所有的流程通过 就发起支付 上送数据
		String json = payService.consumepayHandle(temp);

		SubmitOrderNoCardPayResponseDTO respDto = new Gson().fromJson(json, SubmitOrderNoCardPayResponseDTO.class);

		log.info("支付…………");

		log.info("支付上送原始信息");

		log.info(temp);

		if (0 != respDto.getRetCode()) {
			// 返回页面参数
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			response.sendRedirect(temp.getFrontUrl() + "?" + queryUtil.bean2QueryStr(temp));
		} else {

			OriginalOrderInfo queryWhere = new OriginalOrderInfo();
			queryWhere.setMerchantOrderId(originalOrderId);
			queryWhere.setPid(temp.getMerchantId());
			// 上送原始记录信息
			OriginalOrderInfo originInfo = payService.selectByOriginal(queryWhere);
			// 本地订单id
			String orderId = originInfo.getOrderId();
			// 流水信息
			PospTransInfo transinfo = payService.getTransInfo(orderId);
			// 上送订单id
			String transOrderId = transinfo.getOrderId();
			Map<String, String> params = new HashMap<String, String>();

			// 设置上送信息
			if (temp.getPayType().equals("20")) {
				params.put("appId", SampleConstant.APP_ID1);
				params.put("version", "2.0.1");
				params.put("userfee", temp.getUserfee());
			} else if (temp.getPayType().equals("10")) {
				params.put("appId", SampleConstant.APP_ID0);
				params.put("version", "3.0.1");
				params.put("phoneNo", temp.getPhoneNo());
				params.put("setPhoneNo", temp.getSetPhoneNo());
				params.put("bankName", temp.getBankName());
				params.put("toBankNo", temp.getToBankNo());
				params.put("name", temp.getName());
				params.put("certNo", temp.getCertNo());
				params.put("userfee", temp.getUserfee());
			}
			params.put("payType", temp.getPayType());
			params.put("txnAmt", temp.getTxnAmt());
			params.put("orderId", temp.getOrderId());
			params.put("txnTime", temp.getTxnTime());
			
			if (temp.getPayType().equals("20")) {
				params.put("backUrl", SampleConstant.BACK_URLT1);
			} else if (temp.getPayType().equals("10")) {
				params.put("backUrl", SampleConstant.BACK_URLT0);
			}
			params.put("accNo", temp.getAccNo());
			params.put("frontUrl", SampleConstant.FRONT_URL);
			String jsonString = JSON.toJSONString(params);
			log.info("上送的数据:" + jsonString);

			byte[] encodeData = null;
			if (temp.getPayType().equals("20")) {
				encodeData = PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"), SampleConstant.PUB_KEY1);
			} else if (temp.getPayType().equals("10")) {
				encodeData = PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"), SampleConstant.PUB_KEY0);
			}
			String data = PlatBase64Utils.encode(encodeData);

			Map<String, String> result = new HashMap<String, String>();
			result.put("data", data);
			if (temp.getPayType().equals("20")) {
				result.put("appId", SampleConstant.APP_ID1);
			} else if (temp.getPayType().equals("10")) {
				result.put("appId", SampleConstant.APP_ID0);
			}
			log.info("上送的数据为:" + result);

			String html = EffersonPayService.createAutoFormHtml(
					"http://unionpay.rytpay.com.cn/rytpay-business/v2/quick/pay.html", result, "UTF-8");

			outString(response, html);
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
	@RequestMapping(value = "hfbgPayResultT0")
	public void payResultT0(HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setHeader("Content-type", "text/html;charset=UTF-8");
		String sign = request.getParameter("signature");
		String encryptedData = request.getParameter("data");
		log.info("异步返回的签名:" + sign);
		log.info("异步返回的数据:" + encryptedData);
		String returnMsg = "failed";
		// 解密报文
		byte[] data = PlatKeyGenerator.decryptByPublicKey(PlatBase64Utils.decode(encryptedData),
				SampleConstant.PUB_KEY0);
		// 验签
		boolean flag = PlatKeyGenerator.verify(data, SampleConstant.PUB_KEY0, sign);
		if (flag) {
			returnMsg = "success";
			response.getWriter().write("success");
			Map<String, String> resultMap = JSON.parseObject(new String(data), Map.class);
			String param = HttpUtil.toJson3(resultMap);
			PayResponseEntity pay = gson.fromJson(param, PayResponseEntity.class);
			// 处理这笔交易 修改订单表中的交易表
			payService.otherInvoke(pay);
			// 交易id
			String tranId = pay.getOrderId();
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = payService.getOriginOrderInfo(tranId);

			// 给下游主动返回支付结果
			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
			String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(pay);
			log.info("bgUrl 平台服务器重定向：" + path);

			String result = HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),
					bean2Util.bean2QueryStr(pay));
		} else {
			System.out.println("验签失败");
		}
		log.info("向下游 发送数据成功");

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
	@RequestMapping(value = "hfbgPayResultT1")
	public void payResultT1(HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setHeader("Content-type", "text/html;charset=UTF-8");
		String sign = request.getParameter("signature");
		String encryptedData = request.getParameter("data");
		log.info("异步返回的签名:" + sign);
		log.info("异步返回的数据:" + encryptedData);
		String returnMsg = "failed";
		// 解密报文
		byte[] data = PlatKeyGenerator.decryptByPublicKey(PlatBase64Utils.decode(encryptedData),
				SampleConstant.PUB_KEY1);
		// 验签
		boolean flag = PlatKeyGenerator.verify(data, SampleConstant.PUB_KEY1, sign);
		if (flag) {
			returnMsg = "success";
			response.getWriter().write("success");
			Map<String, String> resultMap = JSON.parseObject(new String(data), Map.class);
			String param = HttpUtil.toJson3(resultMap);
			PayResponseEntity pay = gson.fromJson(param, PayResponseEntity.class);
			// 处理这笔交易 修改订单表中的交易表
			payService.otherInvoke(pay);
			// 交易id
			String tranId = pay.getOrderId();
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = payService.getOriginOrderInfo(tranId);

			// 给下游主动返回支付结果
			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
			String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(pay);
			log.info("bgUrl 平台服务器重定向：" + path);

			String result = HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),
					bean2Util.bean2QueryStr(pay));
		} else {
			System.out.println("验签失败");
		}
		log.info("向下游 发送数据成功");

	}

	/**
	 * 和上游交互 支付完成后 同步返回支付结果
	 *
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @param temp
	 *            订单信息
	 * @throws Exception
	 */
	@RequestMapping(value = "hfpagePayResult")
	public void payBgResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-type", "text/html;charset=UTF-8");

		Map<String, Object> paramMap = new HashMap<String, Object>();

		Map<String, String[]> requestMap = request.getParameterMap();

		log.info("获取前台数据:{}" + requestMap);

		Iterator<Entry<String, String[]>> it = requestMap.entrySet().iterator();
		while (it.hasNext()) {

			Entry<String, String[]> entry = it.next();

			if (entry.getValue().length == 1) {

				log.info("第一个值:" + entry.getKey());
				paramMap.put(entry.getKey(), entry.getValue()[0]);

			} else {

				String[] values = entry.getValue();
				String value = "";
				for (int i = 0; i < values.length; i++) {
					value = values[i] + ",";
				}
				value = value.substring(0, value.length() - 1);
				paramMap.put(entry.getKey(), value);
			}
		}
		// StringBuilder buffer = new StringBuilder();
		Map<String, String> param = new HashMap<String, String>();
		for (Entry<String, Object> entry : paramMap.entrySet()) {

			if (entry.getKey().equals("txnType")) {
				log.info("类型:" + entry.getValue().toString());
				param.put("txnType", entry.getValue().toString());
			}
			if (entry.getKey().equals("respCode")) {
				log.info("返回码:" + entry.getValue().toString());
				param.put("respCode", entry.getValue().toString());
			}
			if (entry.getKey().equals("tokenPayData")) {
				log.info("tokenPayData:" + entry.getValue().toString());
				param.put("tokenPayData", entry.getValue().toString());
			}
			if (entry.getKey().equals("txnSubType")) {
				log.info("txnSubType:" + entry.getValue().toString());
				param.put("txnSubType", entry.getValue().toString());
			}
			if (entry.getKey().equals("customerInfo")) {
				log.info("customerInfo:" + entry.getValue().toString());
				param.put("customerInfo", entry.getValue().toString());
			}
			if (entry.getKey().equals("version")) {
				log.info("version:" + entry.getValue().toString());
				param.put("version", entry.getValue().toString());
			}
			if (entry.getKey().equals("accNo")) {
				log.info("accNo:" + entry.getValue().toString());
				param.put("accNo", entry.getValue().toString());
			}
			if (entry.getKey().equals("signMethod")) {
				log.info("signMethod:" + entry.getValue().toString());
				param.put("signMethod", entry.getValue().toString());
			}
			if (entry.getKey().equals("certId")) {
				log.info("certId:" + entry.getValue().toString());
				param.put("certId", entry.getValue().toString());
			}
			if (entry.getKey().equals("encoding")) {
				log.info("encoding:" + entry.getValue().toString());
				param.put("encoding", entry.getValue().toString());
			}
			if (entry.getKey().equals("respMsg")) {
				log.info("respMsg:" + entry.getValue().toString());
				param.put("respMsg", entry.getValue().toString());
			}
			if (entry.getKey().equals("reqReserved")) {
				log.info("reqReserved:" + entry.getValue().toString());
				param.put("reqReserved", entry.getValue().toString());
			}
			if (entry.getKey().equals("bizType")) {
				log.info("bizType:" + entry.getValue().toString());
				param.put("bizType", entry.getValue().toString());
			}
			if (entry.getKey().equals("encryptCertId")) {
				log.info("encryptCertId:" + entry.getValue().toString());
				param.put("encryptCertId", entry.getValue().toString());
			}
			if (entry.getKey().equals("orderId")) {
				log.info("orderId:" + entry.getValue().toString());
				param.put("orderId", entry.getValue().toString());
			}
			if (entry.getKey().equals("activateStatus")) {
				log.info("activateStatus:" + entry.getValue().toString());
				param.put("activateStatus", entry.getValue().toString());
			}
			if (entry.getKey().equals("signature")) {
				log.info("signature:" + entry.getValue().toString());
				param.put("signature", entry.getValue().toString());
			}
			if (entry.getKey().equals("accessType")) {
				log.info("accessType:" + entry.getValue().toString());
				param.put("accessType", entry.getValue().toString());
			}
			if (entry.getKey().equals("txnTime")) {
				log.info("txnTime:" + entry.getValue().toString());
				param.put("txnTime", entry.getValue().toString());
			}
		}
		// 解析token
		String tokenPayData = param.get("tokenPayData").toString();
		logger.info("上游获取的token字符串:" + tokenPayData);
		List<String> ls = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
		Matcher matcher = pattern.matcher(tokenPayData);
		while (matcher.find()) {
			ls.add(matcher.group());
		}
		for (String string : ls) {
			logger.info("解析后的token字符串:" + string);
			String[] name = string.split("\\&");
			String[] list = name[0].split("\\=");
			logger.info("解析后的token名称:" + list[0].toString());
			logger.info("解析后的token值:" + list[1].toString());
			param.put("token", list[1].toString());
		}
		// 交易id
		String tranId = param.get("orderId");
		// 查询商户上送原始信息
		OriginalOrderInfo originalInfo = payService.getOriginalOrderInfoByMerchanOrderId(tranId);

		param.put("merid", originalInfo.getPid());
		param.put("tranTp", originalInfo.getPayType());
		param.put("txnAmt", originalInfo.getOrderAmount());
		// 给下游主动返回支付结果
		log.info("向下游发送的数据:" + param);
		log.info("向下游发送的地址:" + originalInfo.getPageUrl());
		String str = HttpUtil.parseParams(param);
		log.info("拼接之后的数据:" + str);
		try {
			// 给下游手动返回支付结果
			if (originalInfo.getPageUrl().indexOf("?") == -1) {

				String path = originalInfo.getPageUrl() + "?" + str;
				log.info("pageUrl 商户页面 重定向：" + path);

				response.sendRedirect(path.replace(" ", ""));
			} else {
				log.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
				String path = originalInfo.getPageUrl() + "&" + str;
				log.info("pageUrl 商户页面 重定向：" + path);
				response.sendRedirect(path.replace(" ", ""));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		log.info("向下游发送数据成功");

	}

	/**
	 * 查询支付结果
	 *
	 * @param request
	 *            HttpServletRequest对象
	 * @param response
	 *            HttpServletResponse对象
	 * @param queryInfo
	 *            查询信息
	 * @throws Exception
	 */
	@RequestMapping("hfqueryPayResult")
	public void queryPayResult(HttpServletRequest request, HttpServletResponse response, PayRequestEntity queryInfo)
			throws Exception {
		log.info("查询支付结果：" + queryInfo);
		Map<String, String> param = new HashMap<String, String>();
		// 商户key
		String merchantkey = payService.getChannelConfigKey(queryInfo.getMerchantId()).getMerchantkey();
		HFSignUtil signUtil = new HFSignUtil();
		if (!signUtil.verify(PreSignUtil.hfconsumeSignString(queryInfo), queryInfo.getSignmsg(), merchantkey)) {
			param.put("0001", "签名错误!");
		} else {

			// 设置上送信息
			if (queryInfo.getPayType().equals("1")) {
				param.put("appId", SampleConstant.APP_ID1);

			} else if (queryInfo.getPayType().equals("0")) {
				param.put("appId", SampleConstant.APP_ID0);
			}
			param.put("payType", queryInfo.getPayType());
			param.put("orderId", queryInfo.getOrderId());
			param.put("txnTime", queryInfo.getTxnTime());
			param.put("version", "2.0.1");
			String jsonString = JSON.toJSONString(param);
			log.info("上送的数据:" + jsonString);
			byte[] encodeData = null;
			if (queryInfo.getPayType().equals("20")) {

				encodeData = PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"), SampleConstant.PUB_KEY1);
			} else if (queryInfo.getPayType().equals("10")) {

				encodeData = PlatKeyGenerator.encryptByPublicKey(jsonString.getBytes("UTF-8"), SampleConstant.PUB_KEY0);
			}
			String data = PlatBase64Utils.encode(encodeData);

			Map<String, String> result = new HashMap<String, String>();
			result.put("data", data);
			if (queryInfo.getPayType().equals("20")) {
				result.put("appId", SampleConstant.APP_ID1);
			} else if (queryInfo.getPayType().equals("10")) {
				result.put("appId", SampleConstant.APP_ID0);
			}

			// 设置转发页面
			log.info("向上游发送的数据:" + result);
			String returnJson = EffersonPayService.postAsString(result,
					"http://unionpay.rytpay.com.cn/rytpay-business/v2/trade/order/query.html", "UTF-8");
			;
			log.info("同步返回的数据:" + returnJson);
//			PlatOutboundMsg json = JSON.parseObject(returnJson, PlatOutboundMsg.class);
			// 返回查询结果
			outString(response, JSON.toJSON(returnJson));
		}
	}

	// /**
	// * 下游接入 demo
	// *
	// * @param param
	// * @param request
	// * @param response
	// * @throws Exception
	// */
	// @RequestMapping(value = "hfsignForWap")
	// public void merSignServletForWap(PayRequestEntity params,
	// HttpServletRequest request, HttpServletResponse response)
	// throws Exception {
	//
	// log.info("原始订单信息：" + params);
	//
	// // 根据商户号查询key
	// ChannleMerchantConfigKey keyinfo =
	// payService.getChannelConfigKey(params.getMerchantId());
	// if (keyinfo != null) {
	//
	// String merchantKey = keyinfo.getMerchantkey();
	//
	// HFSignUtil signUtil = new HFSignUtil();
	// // 生成签名
	// String signmsg = signUtil.sign(PreSignUtil.hfpaySigiString(params),
	// merchantKey);
	// log.info("生成签名：" + signmsg);
	// // params.setSignmsg(signmsg);
	// //
	// // outString(response, signmsg);
	// params.setSignmsg(signmsg);
	// // 返回页面参数
	// request.setAttribute("temp", params);
	// request.getRequestDispatcher("/pay/hengfeng/token_submit.jsp").forward(request,
	// response);
	// } else {
	// // 返回页面参数
	// Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
	// String path = params.getFrontUrl() + "?" +
	// queryUtil.bean2QueryStr(params);
	// log.info("demo 重定向：" + path);
	// response.sendRedirect(path.replace(" ", ""));
	// }
	//
	// }
	//
	// /**
	// * 下游接入 demo
	// *
	// * @param param
	// * @param request
	// * @param response
	// * @throws Exception
	// */
	// @RequestMapping(value = "hfsignmessage")
	// public void messageSignServletForWap(PayRequestEntity params,
	// HttpServletRequest request,
	// HttpServletResponse response) throws Exception {
	//
	// log.info("原始订单信息：" + params);
	//
	// // 根据商户号查询key
	// ChannleMerchantConfigKey keyinfo =
	// payService.getChannelConfigKey(params.getMerchantId());
	// if (keyinfo != null) {
	//
	// String merchantKey = keyinfo.getMerchantkey();
	//
	// HFSignUtil signUtil = new HFSignUtil();
	// // 生成签名
	// String signmsg = signUtil.sign(PreSignUtil.hfmessageSignString(params),
	// merchantKey);
	// log.info("生成签名：" + signmsg);
	//
	// params.setSignmsg(signmsg);
	//
	// log.info("生成签名：" + signmsg);
	//
	// outString(response, signmsg);
	// } else {
	// // 返回页面参数
	// outString(response, "商户号找不到Key");
	// }
	//
	// }
	//
	// /**
	// * 下游接入 demo
	// *
	// * @param param
	// * @param request
	// * @param response
	// * @throws Exception
	// */
	// @RequestMapping(value = "hfsignconsume")
	// public void consumeSignServletForWap(PayRequestEntity params,
	// HttpServletRequest request,
	// HttpServletResponse response) throws Exception {
	//
	// log.info("原始订单信息：" + params);
	//
	// // 根据商户号查询key
	// ChannleMerchantConfigKey keyinfo =
	// payService.getChannelConfigKey(params.getMerchantId());
	// if (keyinfo != null) {
	//
	// String merchantKey = keyinfo.getMerchantkey();
	//
	// HFSignUtil signUtil = new HFSignUtil();
	// // 生成签名
	// String signmsg = signUtil.sign(PreSignUtil.hfconsumeSignString(params),
	// merchantKey);
	// log.info("生成签名：" + signmsg);
	//
	// params.setSignmsg(signmsg);
	//
	// log.info("生成签名：" + signmsg);
	//
	// params.setSignmsg(signmsg);
	// // 返回页面参数
	// outString(response, signmsg);
	// } else {
	// // 返回页面参数
	// outString(response, "签名错误");
	// }
	// request.getParameter("accNO");
	// }
	//
	/**
	 * 下游接入 demo
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "hfsignconsume")
	public void consumeSignServletForWap(PayRequestEntity params, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = payService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			HFSignUtil signUtil = new HFSignUtil();
			// 生成签名
			String signmsg = signUtil.sign(PreSignUtil.hfconsumeSignString(params), merchantKey);
			log.info("生成签名：" + signmsg);

			params.setSignmsg(signmsg);

			log.info("生成签名：" + signmsg);

			params.setSignmsg(signmsg);
			// 返回页面参数
			request.setAttribute("temp", params);
			if (params.getBackUrl() != null) {
				request.getRequestDispatcher("/pay/hengfeng/hengfeng_pay_submit.jsp").forward(request, response);
			} else {
				request.getRequestDispatcher("/pay/hengfeng/query_submit.jsp").forward(request, response);
			}

		} else {
			// 返回页面参数
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			String path = params.getFrontUrl() + "?" + queryUtil.bean2QueryStr(params);
			log.info("demo 重定向：" + path);
			response.sendRedirect(path.replace(" ", ""));
		}
	}
	//
	// /**
	// * 和上游交互 支付完成后同步返回支付结果
	// *
	// * @param request
	// * requet对象
	// * @param response
	// * response对象
	// * @param temp
	// * 银联返回的数据
	// * @throws Exception
	// */
	// @RequestMapping(value = "hengfengPayResult")
	// public void hengfengPayResult(PayResponseEntity pay, HttpServletRequest
	// request, HttpServletResponse response)
	// throws Exception {
	//
	// response.setHeader("Content-type", "text/html;charset=UTF-8");
	// log.info("产品异步返回的结果:" + pay);
	// // 查询结果成功
	// if ("00".equals(pay.getRespCode())) {
	//
	// // 返回页面参数
	// request.setAttribute("temp", pay);
	// request.getRequestDispatcher("/pay/hengfeng/success.jsp").forward(request,
	// response);
	// } else {
	//
	// // 返回页面参数
	// request.setAttribute("temp", pay);
	// request.getRequestDispatcher("/pay/hengfeng/fail.jsp").forward(request,
	// response);
	// }
	//
	// log.info("向下游 发送数据成功");
	//
	// }

}

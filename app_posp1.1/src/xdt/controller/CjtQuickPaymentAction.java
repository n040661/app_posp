package xdt.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.quickpay.cjt.comm.Constant;
import xdt.quickpay.cjt.entity.CjtRequestEntity;
import xdt.quickpay.cjt.entity.CjtResponseEntity;
import xdt.quickpay.cjt.temp.ChanpayGatewayQpayDemo;
import xdt.quickpay.hengfeng.entity.PayResponseEntity;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.service.CjtQuickPaymentService;
import xdt.util.HttpURLConection;

import com.google.gson.Gson;

/**
 * @ClassName: CjtQuickPaymentAction
 * @Description: 畅捷 快捷支付
 * @author 尚延超
 * @date 2016年10月25日
 * 
 */
@Controller
@RequestMapping("cjt")
public class CjtQuickPaymentAction {
	/**
	 * 日志记录
	 */
	private Logger log = Logger.getLogger(CjtQuickPaymentAction.class);

	@Resource
	private CjtQuickPaymentService paymentService;
	/**
	 * 编码类型
	 */
	private static String charset = "UTF-8";

	/**
	 * 和上游交互
	 * 
	 * @param name
	 *            普通快捷
	 * @param param
	 *            支付信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "pay")
	public void pay(CjtRequestEntity temp, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ChannleMerchantConfigKey keyinfo = paymentService
				.getChannelConfigKey(temp.getPid());
		PmsMerchantInfo pms = paymentService.selectByPmsMerchantInfo(temp
				.getPid());
		PospRouteInfo posp = paymentService.selectByPospRouteInfo(Integer
				.parseInt(pms.getId()));
		PmsBusinessInfo bus = paymentService.selectByPmsBusinessInfo(posp
				.getMerchantId().toString());
		String number = bus.getBusinessNum();
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();
			HFSignUtil signUtil = new HFSignUtil();
			// 生成签名
			String signmsg = signUtil.sign(PreSginUtil.paySigiString(temp),
					merchantKey);
			log.info("*******************进入pay.action**************************");
			log.info("生成签名：" + signmsg);

			temp.setSignmsg(signmsg);
			// 原始数据交易id
			String originalOrderId = temp.getTransactionid();

			CjtRequestEntity param = new CjtRequestEntity();// 上送参数
			ChanpayGatewayQpayDemo ch = new ChanpayGatewayQpayDemo();

			log.info("*******************开始执行paymentService.payHandle**************************");
			// 所有的流程通过 就发起支付 上送数据
			String json = paymentService.payHandle(temp);
			Map<String, String> origMap = null;

			SubmitOrderNoCardPayResponseDTO respDto = new Gson().fromJson(json,
					SubmitOrderNoCardPayResponseDTO.class);
			log.info("*******************开始执行paymentService.payHandle**************************");
			log.info("支付…………");

			log.info("支付上送原始信息");

			log.info(temp);

			if (0 != respDto.getRetCode()) {
				PayResponseEntity resp = new PayResponseEntity();

				temp.setBank_code(param.getBank_code());
				temp.setPid(param.getPid());
				temp.setTransactionid(param.getTransactionid());
				temp.setOrdertime(param.getOrdertime());
				temp.setOrderamount(param.getOrderamount());
				resp.setErrcode(respDto.getRetCode() + "");
				// 返回页面参数
				Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				response.sendRedirect(temp.getPageurl() + "?"
						+ queryUtil.bean2QueryStr(resp));
			} else {

				param.setPageurl(Constant.XDT_PAGE_URL);
				param.setBgurl(Constant.XDT_BG_URL);

				OriginalOrderInfo queryWhere = new OriginalOrderInfo();
				queryWhere.setMerchantOrderId(originalOrderId);
				queryWhere.setPid(temp.getPid());

				// 上送原始记录信息
				OriginalOrderInfo originInfo = paymentService
						.selectByOriginal(queryWhere);
				// 本地订单id
				String orderId = originInfo.getOrderId();
				// 流水信息
				PospTransInfo transinfo = paymentService.getTransInfo(orderId);
				// 上送订单id
				String transOrderId = transinfo.getTransOrderId();
				// 设置上送信息
				// param.setPid(bus.getBusinessNum());
				param.setTransactionid(transOrderId);
				param.setOrderamount(originInfo.getOrderAmount());
				param.setOrdertime(originInfo.getOrderTime());
				param.setProductname(originInfo.getProcdutName());
				param.setPay_type(originInfo.getPayType());
				param.setBankno(originInfo.getBankNo());
				origMap = new HashMap<String, String>();
				origMap.put("version", "1.0");
				origMap.put("partner_id", number); // 畅捷支付分配的商户号
				origMap.put("_input_charset", charset);// 字符??
				origMap.put("service", "cjt_quick_payment");// 支付的接口名
				// 2.18快捷支付api 业务参数
				origMap.put("out_trade_no", temp.getTransactionid());// 订单??
				//
				origMap.put("trade_amount", temp.getOrderamount());// 金额
				// 2.18快捷支付api 支付相关参数
				origMap.put("card_type", temp.getCard_type());// 卡类??
				origMap.put("pay_type", temp.getPay_type());// 对公对私
				origMap.put("bank_code", temp.getBank_code());// 含义看文??跳收银台此??为空
				origMap.put("payer_name", ch.encrypt(temp.getPayer_name(),
						Constant.MERCHANT_PUBLIC_KEY, charset));// 含义看文??收银台写2
				// 直连网银1
				origMap.put("payer_card_no", ch.encrypt(temp.getBankno(),
						Constant.MERCHANT_PUBLIC_KEY, charset));// 含义看文??收银台写2
				// 直连网银1
				origMap.put("id_number", ch.encrypt(temp.getId_number(),
						Constant.MERCHANT_PUBLIC_KEY, charset));// 含义看文??收银台写2
																// 直连网银1
				origMap.put("phone_number", ch.encrypt(temp.getPhone_number(),
						Constant.MERCHANT_PUBLIC_KEY, charset));// 含义看文??收银台写2
				// 直连网银1
				origMap.put("notify_url", param.getBgurl());// 前台跳转url
				origMap.put("product_name", temp.getProductname());
				origMap.put("order_time", temp.getOrdertime());

			}
			@SuppressWarnings("static-access")
			Map<String, String> sPara = ch.buildRequestPara(origMap, "RSA",
					Constant.MERCHANT_PRIVATE_KEY1, charset);
			String sign = ch.createLinkString(sPara, true);
			String path = Constant.SUBMIT_URL + "?" + sign;
			log.info("*******************开始执行buidRequest**************************");
			String resultString = ch.buildRequest(origMap, "RSA",
					Constant.MERCHANT_PRIVATE_KEY1, charset,
					Constant.SUBMIT_URL);
			log.info("返回的结果：" + resultString);
			JSONObject ob = JSONObject.fromObject(resultString);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = ob.getString(key);
				if (key.equals("outer_trade_no")) {
					log.info("订单号:" + "\t" + value);
					temp.setTransactionid(value);
				}
				if (key.equals("authenticate_status")) {
					log.info("鉴权是否成功:" + "\t" + value);
					temp.setPayresult(value);
				}
				if (key.equals("err_msg")) {
					log.info("错误信息:" + "\t" + value);
					temp.setError(value);
				}
			}
			if (!StringUtils.isEmpty(resultString)) {
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setContentType("text/html;charset=utf-8");
				PrintWriter print = response.getWriter();
				print.write(resultString.toString());
				print.flush();
				print.close();
			}
		} else {
			PayResponseEntity temp1 = new PayResponseEntity();
			temp1.setBankid(temp.getBank_code());
			temp1.setPid(temp.getPid());
			temp1.setTransactionid(temp.getTransactionid());
			temp1.setOrdertime(temp.getOrdertime());
			temp1.setOrderamount(temp.getOrderamount());
			temp1.setPayamount(temp.getOrderamount());
			temp1.setErrcode("1115");
			// 返回页面参数
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			String path = temp.getPageurl() + "?"
					+ queryUtil.bean2QueryStr(temp);
			log.info("*******************执行完毕**************************");
			log.info("demo 重定向：" + path);
			response.sendRedirect(path.replace(" ", " "));
		}
	}

	/**
	 * 和上游交互
	 * 
	 * @param name
	 *            大额认证借记卡快捷
	 * @param param
	 *            支付信息
	 * @param request
	 *            请求对象
	 * @param response
	 *            响应对象
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value = "pay1")
	public void pay1(CjtRequestEntity temp, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 原始数据交易id
		ChannleMerchantConfigKey keyinfo = paymentService
				.getChannelConfigKey(temp.getPid());
		PmsMerchantInfo pms = paymentService.selectByPmsMerchantInfo(temp
				.getPid());
		PospRouteInfo posp = paymentService.selectByPospRouteInfo(Integer
				.parseInt(pms.getId()));
		PmsBusinessInfo bus = paymentService.selectByPmsBusinessInfo(posp
				.getMerchantId().toString());
		String number = bus.getBusinessNum();
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();
			HFSignUtil signUtil = new HFSignUtil();
			// 生成签名
			String signmsg = signUtil.sign(PreSginUtil.paySigiString(temp),
					merchantKey);
			log.info("*******************进入pay.action**************************");
			log.info("生成签名：" + signmsg);

			temp.setSignmsg(signmsg);
			// 原始数据交易id
			String originalOrderId = temp.getTransactionid();

			CjtRequestEntity param = new CjtRequestEntity();// 上送参数
			ChanpayGatewayQpayDemo ch = new ChanpayGatewayQpayDemo();

			log.info("*******************开始执行paymentService.payHandle**************************");
			// 所有的流程通过 就发起支付 上送数据
			String json = paymentService.payHandle(temp);
			Map<String, String> origMap = null;

			SubmitOrderNoCardPayResponseDTO respDto = new Gson().fromJson(json,
					SubmitOrderNoCardPayResponseDTO.class);
			log.info("*******************开始执行paymentService.payHandle**************************");
			log.info("支付…………");

			log.info("支付上送原始信息");

			log.info(temp);

			if (0 != respDto.getRetCode()) {
				PayResponseEntity resp = new PayResponseEntity();

				temp.setBank_code(param.getBank_code());
				temp.setPid(param.getPid());
				temp.setTransactionid(param.getTransactionid());
				temp.setOrdertime(param.getOrdertime());
				temp.setOrderamount(param.getOrderamount());
				resp.setErrcode(respDto.getRetCode() + "");
				// 返回页面参数
				Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
				response.sendRedirect(temp.getPageurl() + "?"
						+ queryUtil.bean2QueryStr(resp));
			} else {

				param.setPageurl(Constant.XDT_PAGE_URL);
				param.setBgurl(Constant.XDT_BG_URL);

				OriginalOrderInfo queryWhere = new OriginalOrderInfo();
				queryWhere.setMerchantOrderId(originalOrderId);
				queryWhere.setPid(temp.getPid());

				// 上送原始记录信息
				OriginalOrderInfo originInfo = paymentService
						.selectByOriginal(queryWhere);
				// 本地订单id
				String orderId = originInfo.getOrderId();
				// 流水信息
				PospTransInfo transinfo = paymentService.getTransInfo(orderId);
				// 上送订单id
				String transOrderId = transinfo.getTransOrderId();
				// 设置上送信息
				// param.setPid(bus.getBusinessNum());
				param.setTransactionid(transOrderId);
				param.setOrderamount(originInfo.getOrderAmount());
				param.setOrdertime(originInfo.getOrderTime());
				param.setProductname(originInfo.getProcdutName());
				param.setPay_type(originInfo.getPayType());
				param.setBankno(originInfo.getBankNo());
				origMap = new HashMap<String, String>();
				origMap.put("version", "1.0");
				origMap.put("partner_id", number); // 畅捷支付分配的商户号
				origMap.put("_input_charset", charset);// 字符??
				origMap.put("service", "cjt_quick_payment");// 支付的接口名
				// 2.18快捷支付api 业务参数
				origMap.put("out_trade_no", temp.getTransactionid());// 订单??
				//
				origMap.put("trade_amount", temp.getOrderamount());// 金额
				// 2.18快捷支付api 支付相关参数
				origMap.put("card_type", temp.getCard_type());// 卡类??
				origMap.put("pay_type", temp.getPay_type());// 对公对私
				origMap.put("bank_code", temp.getBank_code());// 含义看文??跳收银台此??为空
				origMap.put("payer_name", ch.encrypt(temp.getPayer_name(),
						Constant.MERCHANT_PUBLIC_KEY, charset));// 含义看文??收银台写2
				// 直连网银1
				origMap.put("payer_card_no", ch.encrypt(temp.getBankno(),
						Constant.MERCHANT_PUBLIC_KEY, charset));// 含义看文??收银台写2
				// 直连网银1
				origMap.put("id_number", ch.encrypt(temp.getId_number(),
						Constant.MERCHANT_PUBLIC_KEY, charset));// 含义看文??收银台写2
																// 直连网银1
				origMap.put("phone_number", ch.encrypt(temp.getPhone_number(),
						Constant.MERCHANT_PUBLIC_KEY, charset));// 含义看文??收银台写2
				// 直连网银1
				origMap.put("notify_url", param.getBgurl());// 前台跳转url
				origMap.put("product_name", temp.getProductname());
				origMap.put("order_time", temp.getOrdertime());

			}
			@SuppressWarnings("static-access")
			Map<String, String> sPara = ch.buildRequestPara(origMap, "RSA",
					Constant.MERCHANT_PRIVATE_KEY, charset);
			String sign = ch.createLinkString(sPara, true);
			String path = Constant.SUBMIT_URL + "?" + sign;
			log.info("*******************开始执行buidRequest**************************");
			String resultString = ch
					.buildRequest(origMap, "RSA",
							Constant.MERCHANT_PRIVATE_KEY, charset,
							Constant.SUBMIT_URL);
			log.info("返回的结果：" + resultString);
			JSONObject ob = JSONObject.fromObject(resultString);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = ob.getString(key);
				if (key.equals("outer_trade_no")) {
					log.info("订单号:" + "\t" + value);
					temp.setTransactionid(value);
				}
				if (key.equals("authenticate_status")) {
					log.info("鉴权是否成功:" + "\t" + value);
					temp.setPayresult(value);
				}
				if (key.equals("err_msg")) {
					log.info("错误信息:" + "\t" + value);
					temp.setError(value);
				}
			}
			if (!StringUtils.isEmpty(resultString)) {
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setContentType("text/html;charset=utf-8");
				PrintWriter print = response.getWriter();
				print.write(resultString.toString());
				print.flush();
				print.close();
			}
		} else {
			PayResponseEntity temp1 = new PayResponseEntity();
			temp1.setBankid(temp.getBank_code());
			temp1.setPid(temp.getPid());
			temp1.setTransactionid(temp.getTransactionid());
			temp1.setOrdertime(temp.getOrdertime());
			temp1.setOrderamount(temp.getOrderamount());
			temp1.setPayamount(temp.getOrderamount());
			temp1.setErrcode("1115");
			// 返回页面参数
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			String path = temp.getPageurl() + "?"
					+ queryUtil.bean2QueryStr(temp);
			log.info("*******************执行完毕**************************");
			log.info("demo 重定向：" + path);
			response.sendRedirect(path.replace(" ", " "));
		}
	}

	/**
	 * 将验证码和订单号发送到服务器
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
	@ResponseBody
	@RequestMapping(value = "congirmpay")
	public void congirmpay(CjtRequestEntity temp, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 获取订单号
		String id = request.getParameter("transactionid");
		String check = request.getParameter("checknode");
		String pid = request.getParameter("pid");
		ChanpayGatewayQpayDemo ch = new ChanpayGatewayQpayDemo();
		PmsMerchantInfo pms = paymentService.selectByPmsMerchantInfo(pid);
		PospRouteInfo posp = paymentService.selectByPospRouteInfo(Integer
				.parseInt(pms.getId()));
		PmsBusinessInfo bus = paymentService.selectByPmsBusinessInfo(posp
				.getMerchantId().toString());
		Map<String, String> origMap = new HashMap<String, String>();
		// 2.1 基本参数
		origMap.put("version", "1.0");
		origMap.put("partner_id", bus.getBusinessNum()); // 畅捷支付分配的商户号
		origMap.put("_input_charset", charset);// 字符集
		origMap.put("service", "cjt_quick_payment_confirm");// 支付的接口名
		// 2.19快捷api确认
		origMap.put("out_trade_no", id);// 订单号
		origMap.put("verification_code", check);// 短信验证码
		Map<String, String> sPara = ch.buildRequestPara(origMap, "RSA",
				Constant.MERCHANT_PRIVATE_KEY1, charset);
		String sign = ch.createLinkString(sPara, true);
		// 设置转发页面
		// String path=Constant.SUBMIT_URL + "?" + sign;
		// log.info("重定向 第三方："+path);
		// response.sendRedirect(path.replace(" ", " "));
		// PayResponseEntity temp1=new PayResponseEntity();
		String resultString = ch.buildRequest(origMap, "RSA",
				Constant.MERCHANT_PRIVATE_KEY1, charset, Constant.SUBMIT_URL);
		log.info("返回的结果：" + resultString);
		JSONObject ob = JSONObject.fromObject(resultString);
		Iterator it = ob.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = ob.getString(key);
			if (key.equals("trade_status")) {
				log.info("支付状态:" + "\t" + value);
				temp.setPayresult(value);
			}
			if (key.equals("outer_trade_no")) {
				log.info("订单号:" + "\t" + value);
				temp.setTransactionid(value);
			}
			if (key.equals("err_msg")) {
				log.info("错误信息:" + "\t" + value);
				temp.setError(value);
			}
		}
		if (!StringUtils.isEmpty(resultString)) {
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("text/html;charset=utf-8");
			PrintWriter print = response.getWriter();
			print.write(resultString.toString());
			print.flush();
			print.close();
		}

	}

	/**
	 * 将验证码和订单号发送到服务器
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
	@ResponseBody
	@RequestMapping(value = "congirmpay1")
	public void congirmpay1(CjtRequestEntity temp, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 获取订单号
		String id = request.getParameter("transactionid");
		String check = request.getParameter("checknode");
		String pid = request.getParameter("pid");
		ChanpayGatewayQpayDemo ch = new ChanpayGatewayQpayDemo();
		PmsMerchantInfo pms = paymentService.selectByPmsMerchantInfo(pid);
		PospRouteInfo posp = paymentService.selectByPospRouteInfo(Integer
				.parseInt(pms.getId()));
		PmsBusinessInfo bus = paymentService.selectByPmsBusinessInfo(posp
				.getMerchantId().toString());
		Map<String, String> origMap = new HashMap<String, String>();
		// 2.1 基本参数
		origMap.put("version", "1.0");
		origMap.put("partner_id", bus.getBusinessNum()); // 畅捷支付分配的商户号
		origMap.put("_input_charset", charset);// 字符集
		origMap.put("service", "cjt_quick_payment_confirm");// 支付的接口名
		// 2.19快捷api确认
		origMap.put("out_trade_no", id);// 订单号
		origMap.put("verification_code", check);// 短信验证码
		Map<String, String> sPara = ch.buildRequestPara(origMap, "RSA",
				Constant.MERCHANT_PRIVATE_KEY, charset);
		String sign = ch.createLinkString(sPara, true);
		// 设置转发页面
		// String path=Constant.SUBMIT_URL + "?" + sign;
		// log.info("重定向 第三方："+path);
		// response.sendRedirect(path.replace(" ", " "));
		// PayResponseEntity temp1=new PayResponseEntity();
		String resultString = ch.buildRequest(origMap, "RSA",
				Constant.MERCHANT_PRIVATE_KEY, charset, Constant.SUBMIT_URL);
		log.info("返回的结果：" + resultString);
		JSONObject ob = JSONObject.fromObject(resultString);
		Iterator it = ob.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = ob.getString(key);
			if (key.equals("trade_status")) {
				log.info("支付状态:" + "\t" + value);
				temp.setPayresult(value);
			}
			if (key.equals("outer_trade_no")) {
				log.info("订单号:" + "\t" + value);
				temp.setTransactionid(value);
			}
			if (key.equals("err_msg")) {
				log.info("错误信息:" + "\t" + value);
				temp.setError(value);
			}
		}
		if (!StringUtils.isEmpty(resultString)) {
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("text/html;charset=utf-8");
			PrintWriter print = response.getWriter();
			print.write(resultString.toString());
			print.flush();
			print.close();
		}

	}

	/**
	 * 将验证码和订单号发送到服务器
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
	@ResponseBody
	@RequestMapping(value = "query")
	public void query(CjtRequestEntity temp, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 获取订单号
		String id = request.getParameter("transactionid");
		String pid = request.getParameter("pid");
		ChanpayGatewayQpayDemo ch = new ChanpayGatewayQpayDemo();
		PmsMerchantInfo pms = paymentService.selectByPmsMerchantInfo(pid);
		PospRouteInfo posp = paymentService.selectByPospRouteInfo(Integer
				.parseInt(pms.getId()));
		PmsBusinessInfo bus = paymentService.selectByPmsBusinessInfo(posp
				.getMerchantId().toString());
		Map<String, String> origMap = new HashMap<String, String>();
		// 2.1 基本参数
		origMap.put("version", "1.0");
		origMap.put("partner_id", bus.getBusinessNum()); // 畅捷支付分配的商户号
		origMap.put("_input_charset", charset);// 字符集
		origMap.put("service", "cjt_query_pay");// 支付的接口名
		// 2.19快捷api确认
		origMap.put("outer_trade_no", id);// 订单号
		Map<String, String> sPara = ch.buildRequestPara(origMap, "RSA",
				Constant.MERCHANT_PRIVATE_KEY1, charset);
		String sign = ch.createLinkString(sPara, true);
		// 设置转发页面
		// String path=Constant.SUBMIT_URL + "?" + sign;
		// log.info("重定向 第三方："+path);
		// response.sendRedirect(path.replace(" ", " "));
		// PayResponseEntity temp1=new PayResponseEntity();
		String resultString = ch.buildRequest(origMap, "RSA",
				Constant.MERCHANT_PRIVATE_KEY1, charset, Constant.SUBMIT_URL);
		log.info("返回的结果：" + resultString);
		JSONObject ob = JSONObject.fromObject(resultString);
		Iterator it = ob.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = ob.getString(key);
			if (key.equals("pay_infos")) {
				log.info("支付信息列表:" + "\t" + value);
				temp.setPayresult(value);
				JSONArray array = (JSONArray)JSONSerializer.toJSON(value);
				List list = (List)JSONSerializer.toJava(array);
				for(Object object : list){
					//得到json对象
					JSONObject jsonObject = JSONObject.fromObject(object);
					Iterator it1 = jsonObject.keys();
					while (it1.hasNext()) {
						String key1 = (String) it1.next();
						String value1 = jsonObject.getString(key1);
						if (key1.equals("paymentStatus")) {
							log.info("支付状态:" + "\t" + value1);
							temp.setPayresult(value1);
						}
					}
				}
			}
		}
		paymentService.otherInvoke(temp);
		if (!StringUtils.isEmpty(resultString)) {
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("text/html;charset=utf-8");
			PrintWriter print = response.getWriter();
			print.write(resultString.toString());
			print.flush();
			print.close();
		}

	}

	/**
	 * 将验证码和订单号发送到服务器
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
	@ResponseBody
	@RequestMapping(value = "query1")
	public void query1(CjtRequestEntity temp, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 获取订单号
		String id = request.getParameter("transactionid");
		String pid = request.getParameter("pid");
		ChanpayGatewayQpayDemo ch = new ChanpayGatewayQpayDemo();
		PmsMerchantInfo pms = paymentService.selectByPmsMerchantInfo(pid);
		PospRouteInfo posp = paymentService.selectByPospRouteInfo(Integer
				.parseInt(pms.getId()));
		PmsBusinessInfo bus = paymentService.selectByPmsBusinessInfo(posp
				.getMerchantId().toString());
		Map<String, String> origMap = new HashMap<String, String>();
		// 2.1 基本参数
		origMap.put("version", "1.0");
		origMap.put("partner_id", bus.getBusinessNum()); // 畅捷支付分配的商户号
		origMap.put("_input_charset", charset);// 字符集
		origMap.put("service", "cjt_query_pay");// 支付的接口名
		// 2.19快捷api确认
		origMap.put("outer_trade_no", id);// 订单号
		Map<String, String> sPara = ch.buildRequestPara(origMap, "RSA",
				Constant.MERCHANT_PRIVATE_KEY, charset);
		String sign = ch.createLinkString(sPara, true);
		// 设置转发页面
		// String path=Constant.SUBMIT_URL + "?" + sign;
		// log.info("重定向 第三方："+path);
		// response.sendRedirect(path.replace(" ", " "));
		// PayResponseEntity temp1=new PayResponseEntity();
		String resultString = ch.buildRequest(origMap, "RSA",
				Constant.MERCHANT_PRIVATE_KEY, charset, Constant.SUBMIT_URL);
		log.info("返回的结果：" + resultString);
		JSONObject ob = JSONObject.fromObject(resultString);
		Iterator it = ob.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = ob.getString(key);
			if (key.equals("pay_infos")) {
				log.info("支付信息列表:" + "\t" + value);
				temp.setPayresult(value);
				JSONArray array = (JSONArray)JSONSerializer.toJSON(value);
				List list = (List)JSONSerializer.toJava(array);
				for(Object object : list){
					//得到json对象
					JSONObject jsonObject = JSONObject.fromObject(object);
					Iterator it1 = jsonObject.keys();
					while (it1.hasNext()) {
						String key1 = (String) it1.next();
						String value1 = jsonObject.getString(key1);
						if (key1.equals("paymentStatus")) {
							log.info("支付状态:" + "\t" + value1);
							temp.setPayresult(value1);
						}
					}
				}
			}
		}
		paymentService.otherInvoke(temp);
		if (!StringUtils.isEmpty(resultString)) {
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setContentType("text/html;charset=utf-8");
			PrintWriter print = response.getWriter();
			print.write(resultString.toString());
			print.flush();
			print.close();
		}

	}

	/**
	 * 和上游交互 支付完成后返回支付结果
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
	public String payResult(HttpServletRequest request,
			HttpServletResponse response, CjtRequestEntity temp)
			throws Exception {

		log.info("支付结果信息：" + temp);
		log.info("请求参数：" + request.getQueryString());

		// 处理这笔交易 修改订单表中的交易表
		paymentService.otherInvoke(temp);
		// 交易id
		String tranId = temp.getTransactionid();
		ChanpayGatewayQpayDemo ch = new ChanpayGatewayQpayDemo();
		// 查询商户上送原始信息
		OriginalOrderInfo originalInfo = paymentService
				.getOriginOrderInfo(tranId);

		// 替换成下游商户的
		temp.setTransactionid(originalInfo.getMerchantOrderId());
		temp.setOrdertime(originalInfo.getOrderTime());
		temp.setPid(originalInfo.getPid());
		Map<String, String> origMap = new HashMap<String, String>();
		// 2.1 基本参数
		origMap.put("version", "1.0");
		origMap.put("partner_id", Constant.MERCHANT_NO); // 畅捷支付分配的商户号
		origMap.put("_input_charset", charset);// 字符集
		origMap.put("service", "cjt_query_trade");// 支付的接口名
		// 2.19快捷api确认
		origMap.put("outer_trade_no", tranId);// 订单号
		origMap.put("trade_type", "INSTANT");// 短信验证码
		Map<String, String> sPara = ch.buildRequestPara(origMap, "RSA",
				Constant.MERCHANT_PRIVATE_KEY, charset);
		String sign = ch.createLinkString(sPara, true);
		String resultString = ch.buildRequest(origMap, "RSA",
				Constant.MERCHANT_PRIVATE_KEY, charset, Constant.SUBMIT_URL);
		JSONObject ob = JSONObject.fromObject(resultString);

		request.setAttribute("pay", temp);
		return "pay/cjt/pay";
	}

}

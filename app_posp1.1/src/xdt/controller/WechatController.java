package xdt.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONObject;
import xdt.dto.BaseUtil;
import xdt.dto.nbs.alipay.AlipayParamRequest;
import xdt.dto.nbs.micro.WechatMicroRequest;
import xdt.dto.nbs.micro.WechatMicroResponse;
import xdt.dto.nbs.orderquery.WechatOrderQueryRequest;
import xdt.dto.nbs.orderquery.WechatOrderQueryResponse;
import xdt.dto.nbs.scan.WechatScannedRequest;
import xdt.dto.nbs.scan.WechatScannedResponse;
import xdt.dto.nbs.settle.SettleQueryWebPayRequest;
import xdt.dto.nbs.settle.SettleQueryWebPayResponse;
import xdt.dto.nbs.settle.SettleWebPayRequest;
import xdt.dto.nbs.settle.SettleWebPayResponse;
import xdt.dto.nbs.webpay.WechatWebPay;
import xdt.dto.nbs.webpay.WechatWebPayRequest;
import xdt.dto.nbs.webpay.WechatWebPayResponse;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.pufa.security.PuFaSignUtil;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.nbs.common.constant.Constant;
import xdt.quickpay.nbs.common.util.DateUtil;
import xdt.quickpay.nbs.common.util.RandomUtil;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.ThertClien;
import xdt.schedule.ThreadPool;
import xdt.service.IClientCollectionPayService;
import xdt.service.IWechatService;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;

@Controller
@RequestMapping("wechat")
public class WechatController extends BaseAction {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Resource
	private IWechatService wechatService;
	@Resource
	private IClientCollectionPayService clientCollectionPayService;

	/**
	 * 扫码参数
	 *
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("scan_param")
	public void scanParam(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Date now = new Date();
		String key = Constant.BRCB_KEY;
		String service_type = Constant.BRCB_SERVICE_TYPE_SCANNED;
		String appid = "";
		String mch_id = Constant.BRCB_MCH_ID;
		String out_trade_no = RandomUtil.getOrderNum(Constant.ORDER_PREFIX);
		String device_info = "SN1234567890098765";
		String body = "Ipad_mini_16G_白色";
		String detail = "This is the body's description information";
		String attach = "北京分店";
		String fee_type = Constant.BRCB_FEE_TYPE;
		String total_fee = String.valueOf(1000);
		String spbill_create_ip = null;
		try {
			spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String notify_url = Constant.BRCB_NOTIFY_URL;
		String time_start = DateUtil.format(now, DateUtil.DATE_STYLE_YYYYMMDDHHMMSS);
		String time_expire = DateUtil.format(DateUtil.addMinutes(now, 10), DateUtil.DATE_STYLE_YYYYMMDDHHMMSS);
		String op_user_id = mch_id;
		String goods_tag = Constant.BRCB_ORDER_TYPE_WECHAT;
		String product_id = RandomUtil.getRandomStringByLength(10);
		String nonce_str = RandomUtil.randomUUID();
		String limit_pay = "";
		WechatScannedRequest scannedRequest = new WechatScannedRequest(key, service_type, appid, mch_id, device_info,
				nonce_str, body, detail, attach, out_trade_no, fee_type, total_fee, spbill_create_ip, time_start,
				time_expire, op_user_id, goods_tag, notify_url, product_id, limit_pay, log);
		scannedRequest.setMerchantId("10012014483");
		request.setAttribute("key", key);
		request.setAttribute("scan", scannedRequest);

		log.info("===> scannedResponse: {}", scannedRequest);
		try {
			request.getRequestDispatcher("/pay/bns/wechat/scan/scan_param.jsp").forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 扫码请求
	 *
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@ResponseBody
	@RequestMapping("scan")
	public void scan(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, String> result = new HashMap<String, String>();
		String param = requestClient(request);
		log.info("下游上送参数", param);
		if (!StringUtils.isEmpty(param)) {
			WechatScannedRequest wechatScannedRequest = gson.fromJson(param, WechatScannedRequest.class);
			// 返回结果
			// wechatScannedRequest.setDevice_info("SN1234567890098765");
			WechatScannedResponse wechatScannedResponse = null;
			// Map<String, Object> result = new HashMap<String, Object>();
			log.info("生成二维码");
			log.info("下游上送的签名{}", wechatScannedRequest.getSign());
			if (signVerify(wechatScannedRequest, wechatScannedRequest.getSign())) {
				log.info("开始处理二维码");
				wechatScannedResponse = wechatService.updateTwoDimensionCode(wechatScannedRequest);
				result.put("return_code", wechatScannedResponse.getReturn_code());
				result.put("return_msg", wechatScannedResponse.getReturn_msg());
				result.put("result_code", wechatScannedResponse.getResult_code());
				result.put("code_url", wechatScannedResponse.getCode_url());
				log.info("处理完成生成二维码");
			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误!");
			}

		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		try {
			outString(response, gson.toJson(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * D0清算请求
	 *
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@ResponseBody
	@RequestMapping("settle")
	public void settle(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, String> result = new HashMap<String, String>();
		String param = requestClient(request);
		log.info("下游上送参数", param);
		if (!StringUtils.isEmpty(param)) {
			SettleWebPayRequest settle = gson.fromJson(param, SettleWebPayRequest.class);
			Map map = BeanToMapUtil.convertBean(settle);
			SignatureUtil signature = new SignatureUtil();
			// 返回结果
			// wechatScannedRequest.setDevice_info("SN1234567890098765");
			WechatScannedResponse wechatScannedResponse = null;
			// Map<String, Object> result = new HashMap<String, Object>();
			log.info("生成二维码");
			log.info("下游上送的签名{}", settle.getSign());
			// 根据商户号查询key
			ChannleMerchantConfigKey keyinfo = wechatService.getChannelConfigKey(settle.getMch_id());
			if (keyinfo != null) {
				String merchantKey = keyinfo.getMerchantkey();
				if (signature.checkSign(map, merchantKey, log)) {
					log.info("开始处理二维码");
					String merchantId = settle.getMch_id();
					// 查询上游商户号和密钥
					PmsBusinessPos busInfo = wechatService.selectKey(merchantId);
					String key = busInfo.getKek();
					String order_num = settle.getOrder_num();
					String order_count = settle.getOrder_count();
					String out_trade_no = settle.getOut_trade_no();
					String mch_id = "C" + busInfo.getBusinessnum();
					SettleWebPayRequest settles = new SettleWebPayRequest(key, mch_id, order_num, order_count,
							out_trade_no, log);
					SettleWebPayResponse Payresponse = wechatService.doScanned(settles, log);
					log.info("处理完成生成二维码");
				} else {
					log.error("签名错误!");
					result.put("respCode", "15");
					result.put("respMsg", "签名错误!");
				}
			}

		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		try {
			outString(response, gson.toJson(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 公众号入口
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("createWechatQrcodeUrl")
	public void createWechatQrcodeUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, String> result = new HashMap<String, String>();
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		if (!StringUtils.isEmpty(param)) {
			WechatWebPay wechat = gson.fromJson(param, WechatWebPay.class);
			log.info("下游上送的签名{}", wechat.getSign());
			if (signVerify(wechat, wechat.getSign())) {

				PmsBusinessPos busInfo = wechatService.selectKey(wechat.getMerchantId());
				log.info("查询的商户信息{}:", busInfo);
				if (busInfo != null) {
					result.put("return_code", "SUCCESS");
				} else {
					result.put("return_code", "FAIL");
				}

			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误!");
			}
		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		try {
			outString(response, gson.toJson(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * D0清算查询请求
	 *
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@ResponseBody
	@RequestMapping("settleQuery")
	public void settleQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, String> result = new HashMap<String, String>();
		String param = requestClient(request);
		log.info("下游上送参数", param);
		if (!StringUtils.isEmpty(param)) {
			SettleQueryWebPayRequest settle = gson.fromJson(param, SettleQueryWebPayRequest.class);
			Map map = BeanToMapUtil.convertBean(settle);
			SignatureUtil signature = new SignatureUtil();
			// 返回结果
			SettleQueryWebPayResponse wechatScannedResponse = null;
			// Map<String, Object> result = new HashMap<String, Object>();
			log.info("生成二维码");
			log.info("下游上送的签名{}", settle.getSign());
			// 根据商户号查询key
			ChannleMerchantConfigKey keyinfo = wechatService.getChannelConfigKey(settle.getMch_id());
			if (keyinfo != null) {
				String merchantKey = keyinfo.getMerchantkey();
				if (signature.checkSign(map, merchantKey, log)) {
					log.info("开始处理二维码");
					String merchantId = settle.getMch_id();
					// 查询上游商户号和密钥
					PmsBusinessPos busInfo = wechatService.selectKey(merchantId);
					String key = busInfo.getKek();
					String settle_num = settle.getSettle_num();
					String out_trade_no = settle.getOut_trade_no();
					String settle_mode = settle.getSettle_mode();
					String mch_id = "C" + busInfo.getBusinessnum();
					SettleQueryWebPayRequest settles = new SettleQueryWebPayRequest(key, mch_id, settle_num,
							out_trade_no, settle_mode, log);
					SettleQueryWebPayResponse Payresponse = wechatService.doScanned(settles, log);
					log.info("处理完成生成二维码");
				} else {
					log.error("签名错误!");
					result.put("respCode", "15");
					result.put("respMsg", "签名错误!");
				}
			}

		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		try {
			outString(response, gson.toJson(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发起公众号支付
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("wechatWebPay")
	public void wechatWebPay(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 返回值定义

		Map<String, String> result = new HashMap<String, String>();
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		if (!StringUtils.isEmpty(param)) {
			WechatWebPay wechat = gson.fromJson(param, WechatWebPay.class);
			log.info("下游上送的签名{}", wechat.getSign());
			if (signVerify(wechat, wechat.getSign())) {
				String merchantId = wechat.getMerchantId();
				// 查询上游商户号和密钥
				PmsBusinessPos busInfo = wechatService.selectKey(merchantId);
				// Date now = new Date();
				String key = busInfo.getKek();
				String service_type = Constant.BRCB_SERVICE_TYPE_WEBPAY;
				String mch_id = "C" + busInfo.getBusinessnum();// Constant.BRCB_MCH_ID;
				String out_trade_no = wechat.getOut_trade_no();
				String body = wechat.getBody();
				String total_fee = wechat.getTotal_fee();
				String spbill_create_ip = null;
				try {
					spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				log.info("ip地址:" + spbill_create_ip);
				String notify_url = BaseUtil.url+"/wechat/bgPayResult.action";
				String callback_url = "";
				if (wechat.getCallback_url() != null) {
					callback_url = wechat.getCallback_url();
				}
				log.info("回调地址为:" + notify_url);
				String nonce_str = RandomUtil.randomUUID();
				// String callback_url = Constant.BRCB_CALLBACK_URL;
				String brcb_gateway_url = Constant.BRCB_GATEWAY_URL;
				String attach = wechat.getAttach();
				WechatWebPayRequest webPayRequest = null;
				if (wechat.getCallback_url() != null) {
					log.info("有前台通知地址");
					webPayRequest = new WechatWebPayRequest(key, service_type, mch_id, out_trade_no, body, total_fee,
							spbill_create_ip, notify_url, callback_url, nonce_str, attach, log);

				} else {
					log.info("没有前台通知地址");
					webPayRequest = new WechatWebPayRequest(key, service_type, mch_id, out_trade_no, body, total_fee,
							spbill_create_ip, notify_url, nonce_str, attach, log);
				}
				// 返回结果
				if (wechat.getMerchantId() != null) {
					log.info("生成二维码");
					log.info("下游上送参数:{}", wechat);
					// wechat.setBody(new
					// String(wechat.getBody().getBytes("UTF-8")));
					result = wechatService.pay(wechat, log);
					result.put("service_type", webPayRequest.getService_type());
					result.put("mch_id", webPayRequest.getMch_id());
					result.put("out_trade_no", webPayRequest.getOut_trade_no());
					result.put("body", webPayRequest.getBody());
					result.put("total_fee", webPayRequest.getTotal_fee());
					result.put("spbill_create_ip", webPayRequest.getSpbill_create_ip());
					result.put("notify_url", notify_url);
					if (wechat.getCallback_url() != null) {
						result.put("callback_url", callback_url);
					}
					result.put("nonce_str", webPayRequest.getNonce_str());
					result.put("attach", webPayRequest.getAttach());
					result.put("sign", webPayRequest.getSign());
				} else {
					log.error("上送交易参数空!");
					result.put("respCode", "01");
					result.put("respMsg", "fail");
				}
			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误!");
			}
		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		try {
			log.info("整理后的map集合{}:", result);
			outString(response, gson.toJson(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 刷卡(小额)参数
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("micro_param")
	@ResponseBody
	public void microParam(HttpServletRequest request, HttpServletResponse response) {
		Date now = new Date();
		String key = Constant.BRCB_KEY;
		String service_type = Constant.BRCB_SERVICE_TYPE_MICRO;
		String appid = "";
		String mch_id = Constant.BRCB_MCH_ID;
		String out_trade_no = RandomUtil.getOrderNum(Constant.ORDER_PREFIX);
		String device_info = "SN1234567890098765";
		String body = "Ipad_mini_16G_白色";
		String detail = "This is the body's description information";
		String attach = "北京分店";
		String fee_type = Constant.BRCB_FEE_TYPE;
		String total_fee = String.valueOf(1);
		String spbill_create_ip = null;
		try {
			spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String time_start = DateUtil.format(now, DateUtil.DATE_STYLE_YYYYMMDDHHMMSS);
		String time_expire = DateUtil.format(DateUtil.addMinutes(now, 10), DateUtil.DATE_STYLE_YYYYMMDDHHMMSS);
		String op_user_id = mch_id;
		String goods_tag = Constant.BRCB_ORDER_TYPE_WECHAT;
		String product_id = RandomUtil.getRandomStringByLength(10);
		String nonce_str = RandomUtil.randomUUID();
		String limit_pay = "";
		String auth_code = "";

		WechatMicroRequest microRequest = new WechatMicroRequest(key, service_type, appid, mch_id, nonce_str,
				out_trade_no, auth_code, body, total_fee, attach, detail, device_info, fee_type, goods_tag, product_id,
				spbill_create_ip, op_user_id, time_expire, time_start, log);
		request.setAttribute("key", key);
		request.setAttribute("micro", microRequest);
		microRequest.setMerchantId("100120154114115");
		log.info("===> microRequest: {}", microRequest);
		try {
			request.getRequestDispatcher("/pay/bns/wechat/micro/micro_param.jsp").forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 刷卡(小额)请求
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	@RequestMapping("micro")
	public void micro(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		if (!StringUtils.isEmpty(param)) {
			WechatMicroResponse wechatMicroResponse = null;
			WechatMicroRequest wechatMicroRequest = gson.fromJson(param, WechatMicroRequest.class);
			log.info("下游上送的签名{}", wechatMicroRequest.getSign());
			if (signVerify(wechatMicroRequest, wechatMicroRequest.getSign())) {
				log.info("开始处理扫码支付");
				wechatMicroResponse = wechatService.payByCard(wechatMicroRequest);
				result.put("return_code", wechatMicroResponse.getReturn_code() + "");
				result.put("return_msg", wechatMicroResponse.getReturn_msg() + "");
				result.put("result_code", wechatMicroResponse.getResult_code() + "");
				result.put("appid", wechatMicroResponse.getAppid() + "");
				result.put("mch_id", wechatMicroResponse.getMerchantId() + "");
				result.put("device_info", wechatMicroResponse.getDevice_info() + "");
				result.put("nonce_str", wechatMicroResponse.getNonce_str() + "");
				result.put("err_code", wechatMicroResponse.getErr_code() + "");
				result.put("err_code_des", wechatMicroResponse.getErr_code_des() + "");
				result.put("is_subscribe", wechatMicroResponse.getIs_subscribe() + "");
				result.put("nonce_str", wechatMicroResponse.getNonce_str() + "");
				result.put("trade_type", wechatMicroResponse.getTrade_type() + "");
				result.put("bank_type", wechatMicroResponse.getBank_type() + "");
				result.put("fee_type", wechatMicroResponse.getFee_type() + "");
				result.put("transaction_id", wechatMicroResponse.getTransaction_id() + "");
				result.put("out_trade_no", wechatMicroResponse.getOut_trade_no() + "");
				result.put("sign", wechatMicroResponse.getSign() + "");
				result.put("need_query", wechatMicroResponse.getNeed_query() + "");

				log.info("处理完成扫码支付");
			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误!");
			}

		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		try {
			outString(response, gson.toJson(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 支付宝支付扫码参数
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("alipayScan")
	public void alipayScan(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		AlipayParamRequest entity = gson.fromJson(param, AlipayParamRequest.class);
		log.info("下游上送的参数:" + entity);
		Map<String, Object> map = new HashMap<String, Object>();
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = clientCollectionPayService.getChannelConfigKey(entity.getMerchantId());
		String merchantKey = keyinfo.getMerchantkey();
		log.info("下游商户密钥:" + keyinfo);
		String service_type = entity.getService_type();
		String mch_id="";
		String merchantId = entity.getMerchantId();
		String out_trade_no = entity.getOut_trade_no();
		int total_fee = entity.getTotal_fee();
		String subject = entity.getSubject();
		String body = entity.getBody();
		String time_start =entity.getTime_start();;
		String time_expire=entity.getTime_expire();
		String device_info = entity.getDevice_info();
		String notify_url = entity.getNotify_url();
		String nonce_str = entity.getNonce_str();
		String attach=entity.getAttach();
		/**
		 * 支付宝扫码和刷卡请求参数
		 */
		String op_user_id=entity.getOp_user_id();
		String store_id=entity.getStore_id();
		String limit_pay=entity.getLimit_pay();
		/**
		 * 支付宝刷卡请求参数
		 */
		String auth_code = entity.getAuth_code();
		String detail = entity.getDetail();
		String scene = entity.getScene();
		String spbill_create_ip=entity.getSpbill_create_ip();
		String callback_url = entity.getCallback_url();
		/**
		 * 支付宝退款请求参数
		 */
		String transaction_id = entity.getTransaction_id();
		String out_refund_no = entity.getOut_refund_no();
		String refund_fee = entity.getRefund_fee();
		String refund_reason = entity.getRefund_reason();
		String out_request_no = entity.getOut_refund_no();
		AlipayParamRequest alipay=new AlipayParamRequest(merchantKey, service_type,mch_id,merchantId,out_trade_no,total_fee, subject,body, time_start,time_expire, device_info, notify_url, nonce_str, attach,
			 op_user_id,store_id,limit_pay,auth_code,detail,scene,spbill_create_ip,callback_url,transaction_id,out_refund_no,refund_fee,refund_reason,out_request_no, log);
		String sign = alipay.getSign();
		outString(response, sign);
	}

	/**
	 * 支付宝服务窗支付
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "alipayScanParam")
	public void alipayScanParam(HttpServletRequest request,  HttpServletResponse response)
			throws Exception {
		log.info("支付宝支付进来了");
		Map<String, Object> result = new HashMap<String, Object>();
		logger.info("生成二维码");

		String param = requestClient(request);
		log.info("下游上送参数:{}", param);
		//AlipayParamResponse alipayresponse=new AlipayParamResponse();
		if (!StringUtils.isEmpty(param)) {
			AlipayParamRequest entity = gson.fromJson(param, AlipayParamRequest.class);
			log.info("json转换扫码反扫对象{}", entity);
			log.info("下游上送签名串{}",entity.getSign());
			//查询商户密钥
			ChannleMerchantConfigKey keyinfo = clientCollectionPayService
					.getChannelConfigKey(entity.getMerchantId());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil=new SignatureUtil();
			
			Map map=BeanToMapUtil.convertBean(entity);
			
			if(signUtil.checkSign(map, merchantKey, log)){
				log.info("对比签名成功");
				result= wechatService.updateHandle(entity);
				log.info("支付宝支付生成二维码成功");
			}else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误!");
			}
			
		}else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}

		log.info("返回结果:{}", result);
		outString(response, gson.toJson(result));

		
	}
	/**
	 * 订单查询
	 *
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	@RequestMapping("orderquery_param")
	public void orderQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		if (!StringUtils.isEmpty(param)) {
			WechatOrderQueryRequest orderQueryRequest = gson.fromJson(param, WechatOrderQueryRequest.class);
			log.info("下游上送的签名{}", orderQueryRequest.getSign());
			if (signVerify(orderQueryRequest, orderQueryRequest.getSign())) {
				// 查询上游商户号和密钥
				PmsBusinessPos busInfo = wechatService.selectKey(orderQueryRequest.getMerchantId());
				String key = busInfo.getKek();
				String service_type = Constant.BRCB_SERVICE_TYPE_ORDERQUERY;
				String mch_id = "C" + busInfo.getBusinessnum();// Constant.BRCB_MCH_ID;
				String out_trade_no = orderQueryRequest.getOut_trade_no();
				String nonce_str = RandomUtil.randomUUID();

				orderQueryRequest = new WechatOrderQueryRequest(key, service_type, mch_id, out_trade_no, nonce_str,
						log);
				// 请求
				WechatOrderQueryResponse orderQueryResponse = wechatService.doOrderQuery(orderQueryRequest, log);
				result = BeanToMapUtil.convertBean(orderQueryResponse);
				if (result != null) {
					log.info("给下游返回的数据{}:", result);
					try {
						outString(response, gson.toJson(result));
					} catch (IOException e) {
						e.printStackTrace();
					}
	
				} else {
					log.info("查询订单失败");
				}
			} else {
				log.error("签名错误!");
				result.put("respCode", "15");
				result.put("respMsg", "签名错误!");
			}

		} else {
			log.error("上送交易参数空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
		}
		try {
			outString(response, gson.toJson(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("orderSign")
	public void orderSign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		WechatOrderQueryRequest orderQueryRequest = gson.fromJson(param, WechatOrderQueryRequest.class);
		Map map = BeanToMapUtil.convertBean(orderQueryRequest);
		log.info("支付签名");
		String sign = PuFaSignUtil.sign(map);
		outString(response, sign);
	}

	@RequestMapping("codeSign")
	public void paySign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		WechatScannedRequest wechatScannedRequest = gson.fromJson(param, WechatScannedRequest.class);
		Map map = BeanToMapUtil.convertBean(wechatScannedRequest);
		log.info("支付签名");
		String sign = PuFaSignUtil.sign(map);
		outString(response, sign);
	}

	@RequestMapping("microSign")
	public void microSign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		WechatMicroRequest wechatMicroRequest = gson.fromJson(param, WechatMicroRequest.class);
		Map map = BeanToMapUtil.convertBean(wechatMicroRequest);
		log.info("支付签名");
		String sign = PuFaSignUtil.sign(map);
		outString(response, sign);
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
	@RequestMapping("bgPayResult")
	public void payResult(HttpServletRequest request, HttpServletResponse response, WechatWebPayResponse temp)
			throws Exception {

		log.info("**********进入异步通知的地址中***********");
		Map<String, String> result = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(
				new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		String appMsg = sb.toString();
		log.info("请求参数：" + appMsg);
		if (!StringUtils.isEmpty(appMsg)) {
			response.getWriter().write("SUCCESS");
			JSONObject ob = JSONObject.fromObject(appMsg);
			log.info("封装之后的数据:{}", ob);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (key.equals("appid")) {
					String value = ob.getString(key);
					log.info("公众号ID:" + "\t" + value);
					temp.setAppid(value);
				}
				if (key.equals("bank_type")) {
					String value = ob.getString(key);
					log.info("付款银行:" + "\t" + value);
					temp.setBank_type(value);
				}
				if (key.equals("fee_type")) {
					String value = ob.getString(key);
					log.info("货币种类:" + "\t" + value);
					temp.setFee_type(value);
				}
				if (key.equals("return_msg")) {
					String value = ob.getString(key);
					log.info("返回信息:" + "\t" + value);
					temp.setReturn_msg(value);
				}
				if (key.equals("attach")) {
					String value = ob.getString(key);
					log.info("附加信息:" + "\t" + value);
					temp.setAttach(value);
				}
				if (key.equals("err_code")) {
					String value = ob.getString(key);
					log.info("错误码:" + "\t" + value);
					temp.setErr_code_des(value);
				}
				if (key.equals("err_code_des")) {
					String value = ob.getString(key);
					log.info("错误信息描述:" + "\t" + value);
					temp.setErr_code_des(value);
				}
				if (key.equals("is_subscribe")) {
					String value = ob.getString(key);
					log.info("是否关注公众账号:" + "\t" + value);
					temp.setIs_subscribe(value);
				}
				if (key.equals("nonce_str")) {
					String value = ob.getString(key);
					log.info("随机字符串:" + "\t" + value);
					temp.setNonce_str(value);
				}
				if (key.equals("openid")) {
					String value = ob.getString(key);
					log.info("用户标识:" + "\t" + value);
					temp.setOpenid(value);
				}
				if (key.equals("out_trade_no")) {
					String value = ob.getString(key);
					log.info("商户订单号:" + "\t" + value);
					temp.setOut_trade_no(value);
				}
				if (key.equals("result_code")) {
					String value = ob.getString(key);
					log.info("业务结果:" + "\t" + value);
					temp.setResult_code(value);
				}
				if (key.equals("return_code")) {
					String value = ob.getString(key);
					log.info("返回状态码:" + "\t" + value);
					temp.setReturn_code(value);
				}
				if (key.equals("time_end")) {
					String value = ob.getString(key);
					log.info("支付完成时间:" + "\t" + value);
					temp.setTime_end(value);
				}
				if (key.equals("total_fee")) {
					String value = ob.getString(key);
					log.info("订单金额:" + "\t" + value);
					temp.setTotal_fee(value);
				}
				if (key.equals("trade_state")) {
					String value = ob.getString(key);
					log.info("交易状态:" + "\t" + value);
					temp.setTrade_state(value);
				}
				if (key.equals("trade_type")) {
					String value = ob.getString(key);
					log.info("交易类型:" + "\t" + value);
					temp.setTrade_type(value);
				}
				if (key.equals("wechat_transaction_id")) {
					String value = ob.getString(key);
					log.info("微信订单号:" + "\t" + value);
					temp.setWechat_transaction_id(value);
				}
				if (key.equals("alipay_transaction_id")) {
					String value = ob.getString(key);
					log.info("支付宝订单号:" + "\t" + value);
					temp.setAlipay_transaction_id(value);
				}
			}
			log.info("支付后返回的信息：" + temp);
			// 处理这笔交易 修改订单表中的交易表
			wechatService.otherInvoke(temp);
			// 交易id
			String tranId = temp.getOut_trade_no();
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = wechatService.getOriginOrderInfo(tranId);
			log.info("查询上送的原始数据{}:" + originalInfo);
			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
			String path = "";
			if (originalInfo.getBgUrl().indexOf("?") == -1) {

				path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(temp);
			} else {

				path = originalInfo.getBgUrl() + "&" + bean2Util.bean2QueryStr(temp);
			}
			log.info("bgUrl 平台服务器重定向：" + path);
			HttpUtil http = new HttpUtil();
			// Map map = BeanToMapUtil.convertBean(temp);
			// response.sendRedirect(path.replace(" ", ""));
			String result1 = HttpURLConection.httpURLConnectionPOST(originalInfo.getBgUrl(),
					bean2Util.bean2QueryStr(temp));
			log.info("下游返回状态" + result1);
			if (!"SUCCESS".equals(result1)) {
				ThreadPool.executor(new ThertClien(temp, originalInfo, bean2Util));
			}
			response.getWriter().write("SUCCESS");
			log.info("向下游 发送数据成功");

		} else {
			response.getWriter().write("FAIL");
			log.error("回调的参数为空!");
			result.put("respCode", "01");
			result.put("respMsg", "fail");
			response.getWriter().write("FAIL");
		}

	}

	@RequestMapping("paySign")
	public void paySigns(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		WechatWebPay wechatMicroRequest = gson.fromJson(param, WechatWebPay.class);
		Map map = BeanToMapUtil.convertBean(wechatMicroRequest);
		log.info("支付签名");
		String sign = PuFaSignUtil.sign(map);
		outString(response, sign);
	}

	@RequestMapping("settleSign")
	public void settleSign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		SettleWebPayRequest settle = gson.fromJson(param, SettleWebPayRequest.class);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = wechatService.getChannelConfigKey(settle.getMch_id());
		if (keyinfo != null) {
			String merchantKey = keyinfo.getMerchantkey();
			Map map = BeanToMapUtil.convertBean(settle);
			SignatureUtil signature = new SignatureUtil();
			String sign = signature.getSign(map, merchantKey, log);
			outString(response, sign);
		}
	}

	@RequestMapping("settleQuerySign")
	public void settleQuerySign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		String param = requestClient(request);
		log.info("下游上送的参数：" + param);
		SettleQueryWebPayRequest settle = gson.fromJson(param, SettleQueryWebPayRequest.class);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = wechatService.getChannelConfigKey(settle.getMch_id());
		if (keyinfo != null) {
			String merchantKey = keyinfo.getMerchantkey();
			Map map = BeanToMapUtil.convertBean(settle);
			SignatureUtil signature = new SignatureUtil();
			String sign = signature.getSign(map, merchantKey, log);
			outString(response, sign);
		}
	}

	@RequestMapping("baiduOrderCallBack")
	public void success(HttpServletResponse response, WechatWebPayResponse temp) {

		log.info("北农商公众号扫码支付异步通知进来了！");
		log.info("北农商公众号扫码支付异步通知数据:" + JSON.toJSON(temp));

		try {
			outString(response, "SUCCESS");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

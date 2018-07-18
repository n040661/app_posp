package xdt.controller;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.interfaces.RSAPublicKey;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.capinfo.crypt.Md5;
import com.etonepay.b2c.utils.MD5;
import com.kspay.MD5Util;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.yufusoft.payplatform.security.cipher.YufuCipher;
import com.yufusoft.payplatform.security.vo.ParamPacket;
import cn.com.sandpay.cashier.sdk.SandpayClient;
import cn.com.sandpay.cashier.sdk.SandpayRequestHead;
import cn.com.sandpay.cashier.sdk.SandpayResponseHead;
import cn.com.sandpay.cashier.sdk.util.CertUtil;
import cn.com.sandpay.cashier.sdk.util.DateUtil;
import cn.com.sandpay.cashier.sdk.util.SandpayConstants;
import net.sf.json.JSONObject;
import xdt.dao.IPayBankInfoDao;
import xdt.dto.BaseUtil;
import xdt.dto.gateway.common.SampleConstant;
import xdt.dto.gateway.entity.GateWayQueryRequestEntity;
import xdt.dto.gateway.entity.GateWayQueryResponseEntity;
import xdt.dto.gateway.entity.GateWayRequestEntity;
import xdt.dto.gateway.entity.GateWayResponseEntity;
import xdt.dto.gateway.entity.GatrWayGefundEntity;
import xdt.dto.hfb.HfbUtil;
import xdt.dto.hfb.HttpsUtil;
import xdt.dto.hj.HJResponse;
import xdt.dto.hj.HJUtil;
import xdt.dto.jp.JpUtil;
import xdt.dto.jp.RSASignUtil;
import xdt.dto.nbs.alipay.AlipayParamRequest;
import xdt.dto.payeasy.PayEasyRequestEntity;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.entity.MessageRequestEntity;
import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.dto.quickPay.entity.QueryResponseEntity;
import xdt.dto.quickPay.util.MbUtilThread;
import xdt.dto.sxf.SXFUtil;
import xdt.dto.tfb.TFBConfig;
import xdt.dto.yf.DoYf;
import xdt.dto.yf.YFUtil;
import xdt.dto.yf.YufuCipherSupport;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PayBankInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.hf.util.EffersonPayService;
import xdt.quickpay.jbb.util.RSAEncrypt;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.StringUtil;
import xdt.quickpay.sd.entity.GatewayOrderPayRequest;
import xdt.quickpay.sd.entity.GatewayOrderPayRequest.GatewayOrderPayRequestBody;
import xdt.quickpay.sd.entity.GatewayOrderPayResponse;
import xdt.quickpay.sd.entity.GatewayOrderPayResponse.GatewayOrderPayResponseBody;
import xdt.quickpay.yy.util.EmaxPlusUtil;
import xdt.schedule.ThreadPool;
import xdt.service.IGateWayService;
import xdt.service.IHJService;
import xdt.service.IPayBankInfoService;
import xdt.service.IPmsAppTransInfoService;
import xdt.service.ITotalPayService;
import xdt.service.OriginalOrderInfoService;
import xdt.service.impl.PayBankInfoServiceImpl;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.RSAUtil;
import xdt.util.UtilDate;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.PaymentUtils;
import xdt.util.utils.RSAUtils;
import xdt.util.utils.RequestUtils;
import xdt.util.utils.UtilThread;

@Controller
@RequestMapping("/gateWay")
public class GateWayController extends BaseAction {

	/**
	 * 日志记录
	 */
	Logger log = Logger.getLogger(this.getClass());

	@Resource
	private IGateWayService gateWayService;
	@Resource
	private IHJService service;// 汇聚
	@Resource
	private OriginalOrderInfoService Origi; // 原始信息Service
	@Resource
	private ITotalPayService iTotalPayService;
	@Resource
	private IPayBankInfoService PayBankInfoService;

	@Resource
	private IPmsAppTransInfoService pmsAppTransInfoService;

	/**
	 * 网关支付生成签名
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "GateWayScan")
	public void alipayScan(GateWayRequestEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		log.info("下游上送的参数:" + entity);
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(entity.getV_mid());
		String merchantKey = keyinfo.getMerchantkey();
		log.info("下游商户密钥:" + keyinfo);
		String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey, log);
		entity.setV_sign(sign);
		
		// 返回页面参数
		request.setCharacterEncoding("UTF-8");
		request.setAttribute("temp", entity);
		request.getRequestDispatcher("/gateway_submit.jsp").forward(request, response);
	}

	@RequestMapping(value = "sign")
	public void sign(GatrWayGefundEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		logger.info("下游上送的参数:" + entity);
		// 查询商户密钥
		ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(entity.getV_mid());
		String merchantKey = keyinfo.getMerchantkey();
		logger.info("下游商户密钥:" + keyinfo);
		String sign = SignatureUtil.getSign(beanToMap(entity), merchantKey, logger);
		// 返回页面参数
		outString(response, sign);
	}

	
	/**
	 * 网关支付请求参数
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "way")
	public void alipayScanParam(GateWayRequestEntity param, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.info("网关支付进来了");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<String, String>();
		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		log.info("下游上送参数:{}"+ JSON.toJSONString(param));
		String html = "";
		if (!StringUtils.isEmpty(param.getV_mid())) {
			log.info("下游上送签名串{}" + param.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(param.getV_mid());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(param);

			if (signUtil.checkSign(map, merchantKey, log)) {
				log.info("对比签名成功");
				result = gateWayService.updateHandle(param);

				if ("00".equals(result.get("v_code"))) {

					// 查询上游商户号
					PmsBusinessPos busInfo = gateWayService.selectKey(param.getV_mid());

					logger.info("上游通道信息:" + busInfo);
					switch (busInfo.getChannelnum()) {// busInfo.getBusinessnum()
					case "SXYWG":
						logger.info("************************首信易----网关支付----处理 开始");
						// 设置上送信息
						String v_oid = UtilDate.getDate() + "-" + busInfo.getBusinessnum() + "-" + param.getV_oid();
						PayEasyRequestEntity param1 = new PayEasyRequestEntity();// 上送参数
						param1.setV_mid(busInfo.getBusinessnum());
						param1.setV_oid(v_oid);
						param1.setV_rcvname(busInfo.getBusinessnum());
						param1.setV_rcvaddr(busInfo.getBusinessnum());
						param1.setV_rcvtel(busInfo.getBusinessnum());
						param1.setV_rcvpost(busInfo.getBusinessnum());
						param1.setV_amount(param.getV_txnAmt());
						param1.setV_ymd(UtilDate.getDate());
						param1.setV_orderstatus("1");
						param1.setV_ordername(UtilDate.getDate());
						param1.setV_moneytype(busInfo.getBusinessnum());
						param1.setV_url(param.getV_url());
						if ("2".equals(param.getV_type())) {
							param1.setV_pmode(param1.getV_pmode());
						}
						// 生成签名
						Client cc = Client.create();
						WebResource rr = cc.resource(SampleConstant.STANDARD_URL);
						MultivaluedMap queryParams = new MultivaluedMapImpl();
						Md5 md5 = new Md5("");
						// String v_oid = temp.getV_oid();

						String str = param1.getV_moneytype() + param1.getV_ymd() + param1.getV_amount()
								+ param1.getV_rcvname() + v_oid + param.getV_mid() + param.getV_url();
						log.info("拼接后的字符串:" + str);
						logger.info("向上游发送的秘钥:" + busInfo.getKek());
						md5.hmac_Md5(str, busInfo.getKek());
						byte b[] = md5.getDigest();
						String digestString = md5.stringify(b);
						log.info("加密后的字符串:" + digestString);

						param1.setV_md5info(digestString);
						// 设置上送参数
						String queryString = "";
						// Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
						// 设置转发页面
						String path = "";
						if ("1".equals(param.getV_type())) {

							queryString = "v_mid=" + param1.getV_mid() + "&v_oid=" + v_oid + "&v_rcvname="
									+ param1.getV_rcvname() + "&v_rcvaddr=" + param1.getV_rcvaddr() + "&v_rcvtel="
									+ param1.getV_rcvtel() + "&v_rcvpost=" + param1.getV_rcvpost() + "&v_amount="
									+ param1.getV_amount() + "&v_ymd=" + param1.getV_ymd() + "&v_orderstatus="
									+ param1.getV_orderstatus() + "&v_ordername=" + param1.getV_ordername()
									+ "&v_moneytype=" + param1.getV_moneytype() + "&v_url=" + param1.getV_url()
									+ "&v_md5info=" + param1.getV_md5info();
							log.info("上送的参数:" + queryString);
							path = SampleConstant.STANDARD_URL + "?" + queryString;

						} else if ("2".equals(param.getV_type())) {

							queryString = "v_mid=" + param1.getV_mid() + "&v_oid=" + v_oid + "&v_rcvname="
									+ param1.getV_rcvname() + "&v_rcvaddr=" + param1.getV_rcvaddr() + "&v_rcvtel="
									+ param1.getV_rcvtel() + "&v_rcvpost=" + param1.getV_rcvpost() + "&v_amount="
									+ param1.getV_amount() + "&v_ymd=" + param1.getV_ymd() + "&v_orderstatus="
									+ param1.getV_orderstatus() + "&v_ordername=" + param1.getV_ordername()
									+ "&v_moneytype=" + param1.getV_moneytype() + "&v_pmode=" + param1.getV_pmode()
									+ "&v_url=" + param1.getV_url() + "&v_md5info=" + param1.getV_md5info();
							log.info("上送的参数:" + queryString);
							path = SampleConstant.DIRECT_URL + "?" + queryString;

						} else if ("3".equals(param.getV_type())) {

							queryString = "v_mid=" + param1.getV_mid() + "&v_oid=" + v_oid + "&v_rcvname="
									+ param1.getV_rcvname() + "&v_rcvaddr=" + param1.getV_rcvaddr() + "&v_rcvtel="
									+ param1.getV_rcvtel() + "&v_rcvpost=" + param1.getV_rcvpost() + "&v_amount="
									+ param1.getV_amount() + "&v_ymd=" + param1.getV_ymd() + "&v_orderstatus="
									+ param1.getV_orderstatus() + "&v_ordername=" + param1.getV_ordername()
									+ "&v_moneytype=" + param1.getV_moneytype() + "&v_url=" + param1.getV_url()
									+ "&v_md5info=" + param1.getV_md5info();
							log.info("上送的参数:" + queryString);
							path = SampleConstant.MEMBER_URL + "?" + queryString;

						} else {
							log.info("没有上送的通道!");
						}
						break;
					case "JS100669":
						logger.info("************************江苏电商----网关支付----处理 开始");
						Map<String, String> params = new HashMap<String, String>();
						params.put("merchantCode", busInfo.getBusinessnum());
						params.put("terminalCode", busInfo.getPosnum());
						params.put("orderNum", param.getV_oid());
						params.put("transMoney", param.getV_txnAmt());
						if ("105962".equals(busInfo.getDepartmentnum())) {
							params.put("notifyUrl",
									BaseUtil.url+"/test/qrcode/gatewayResult1.action");
						} else if ("107382".equals(busInfo.getDepartmentnum())) {
							params.put("notifyUrl",
									BaseUtil.url+"/test/qrcode/gatewayResult.action");
						}
						params.put("returnUrl", BaseUtil.url+"/test/qrcode/gatewayResult.action");

						params.put("commodityName", param.getV_productName());
						params.put("bankCode", param.getV_bankAddr());
						String apply = HttpUtil.parseParams(params);
						logger.info("生成签名前的数据:" + apply);
						byte[] sign = RSAUtil.encrypt(busInfo.getKek(), apply.getBytes());
						logger.info("上送的签名:" + sign);
						Map<String, String> map1 = new HashMap<String, String>();
						map1.put("groupId", busInfo.getDepartmentnum());
						map1.put("service", "WGZF001");
						map1.put("signType", "RSA");
						map1.put("sign", RSAUtil.base64Encode(sign));
						map1.put("datetime", UtilDate.getOrderNum());
						String jsonmap = HttpUtil.parseParams(map1);
						logger.info("上送数据:" + jsonmap);
						String respJson = HttpURLConection
								.httpURLConnectionPOST("http://180.96.28.8:8044/TransInterface/TransRequest", jsonmap);
						logger.info("**********江苏电商响应报文:{}" + respJson);
						if (respJson != null) {
							JSONObject ob = JSONObject.fromObject(respJson);
							logger.info("封装之后的数据:{}" + ob);
							Iterator it = ob.keys();
							while (it.hasNext()) {
								String key = (String) it.next();
								if (key.equals("pl_code")) {
									String value = ob.getString(key);
									logger.info("提交状态:" + "\t" + value);
									result.put("respCode", value);
								}
								if (key.equals("pl_sign")) {
									String value = ob.getString(key);
									logger.info("签名:" + "\t" + value);
									result.put("sign", value);
								}
								if (key.equals("pl_datetime")) {
									String value = ob.getString(key);
									logger.info("交易时间:" + "\t" + value);
									result.put("pl_datetime", value);
								}
								if (key.equals("pl_message")) {
									String value = ob.getString(key);
									logger.info("交易描述:" + "\t" + value);
									result.put("pl_message", value);
								}

							}
							if (result.get("respCode").equals("0000")) {

								String sign1 = result.get("sign");
								String baseSign = URLDecoder.decode(sign1, "UTF-8");

								baseSign = baseSign.replace(" ", "+");

								byte[] a = RSAUtil.verify(busInfo.getKek(), RSAUtil.base64Decode(baseSign));

								String Str = new String(a);

								logger.info("解析之后的数据:" + Str);

								String[] array = Str.split("\\&");

								logger.info("拆分数据:" + array);
								String[] list = array[0].split("\\=");
								if (list[0].equals("orderNum")) {
									logger.info("合作商订单号:" + list[1]);

									result.put("orderNum", list[1]);

								}
								String[] list1 = array[1].split("\\=");
								if (list1[0].equals("pl_orderNum")) {
									logger.info("平台订单号:" + list1[1]);
									result.put("pl_orderNum", list1[1]);

								}
								result.put("pl_url", array[2].replaceAll("pl_url=", ""));
								html = result.get("pl_url").toString();
								logger.info("页面地址:" + html);
								// 解析html页面
								Document doc = Jsoup.parse(html);
								Elements element = doc.body().getElementsByTag("form");
								List array1 = new ArrayList<>();
								Elements ele = doc.getElementsByTag("input");
								for (Element e1 : ele) {
									if (e1.val() != null) {
										array1.add(e1.val());
									}
								}
								result.put("url", element.attr("action"));
								result.put("sign", array1.get(0).toString());
								result.put("valid_order", array1.get(1).toString());
								result.put("no_order", array1.get(2).toString());
								result.put("oid_partner", array1.get(3).toString());
								result.put("pay_type", array1.get(4).toString());
								result.put("url_return", array1.get(5).toString());
								result.put("notify_url", array1.get(6).toString());
								result.put("name_goods", array1.get(7).toString());
								result.put("dt_order", array1.get(8).toString());
								result.put("user_id", array1.get(9).toString());
								result.put("money_order", array1.get(10).toString());
								result.put("bank_code", array1.get(11).toString());

							}
						}
						break;
					case "SXF":
						logger.info("************************随行付----网关支付----处理 开始");
						net.sf.json.JSONObject json = new net.sf.json.JSONObject();
						Double d = Double.parseDouble(param.getV_txnAmt()) / 100;
						json.put("orderNo", param.getV_oid());
						json.put("tranAmt", d);
						json.put("ccy", SXFUtil.ccy);
						json.put("pname", param.getV_productName());
						json.put("pnum", SXFUtil.pnum);
						json.put("pdesc", param.getV_productDesc());
						json.put("retUrl", SXFUtil.returnUrl);
						json.put("notifyUrl", SXFUtil.notifyUrl);
						json.put("bankWay", param.getV_bankAddr());
						json.put("period", param.getV_expire_time());
						json.put("desc", param.getV_productDesc());
						json.put("userId", param.getV_mid());
						json.put("payWay", "2");
						json.put("payChannel", "1");
						log.info("json：" + json);
						String key = busInfo.getKek();// "12345";
						String data = PaymentUtils.encrypt(json.toString(), SXFUtil.publicKey);//

						net.sf.json.JSONObject main = new net.sf.json.JSONObject();
						String mercNo = busInfo.getBusinessnum();
						log.info("随行付查询上游商户号：" + mercNo);
						main.put("mercNo", mercNo);
						main.put("tranCd", SXFUtil.tranCd);
						main.put("version", SXFUtil.version);
						main.put("reqData", data);
						main.put("ip", SXFUtil.ip);
						log.info("main：" + main.toString());
						// 加签名，注意参数顺序
						String sign1 = PaymentUtils.sign(main.toString(), key);// SXFUtil.mercPrivateKey
						log.info("sign：" + sign1);

						net.sf.json.JSONObject jsons = new net.sf.json.JSONObject();
						jsons.put("sign", sign1);
						jsons.put("reqData", data);
					/*case "888101600000961":
					case "888101700004594":*/
					case "HJZF":
						logger.info("************************汇聚----网关支付----处理 开始");
						StringBuilder str1 = new StringBuilder();
						str1.append(busInfo.getBusinessnum());// HJUtil.merchantNo
						str1.append(param.getV_oid());
						str1.append(Double.parseDouble(param.getV_txnAmt()) + "");
						str1.append("1");
						str1.append(param.getV_productName());
						str1.append(param.getV_attach() == null ? "" : param.getV_attach());
						str1.append(BaseUtil.url+"/gateWay/hj_returnUrl.action");
						str1.append(BaseUtil.url+"/gateWay/hj_notifyUrl.action");
						str1.append(param.getV_bankAddr() == null ? "" : param.getV_bankAddr() + "_NET_B2C");
						str1.append("0");
						log.info("汇聚待签名数据:" + str1.toString());
						// String hmac =MD5Utils.sign(str.toString(), HJUtil.privateKey,
						// "UTF-8");//RSAUtils.sign(str.toString().getBytes("UTF-8"),
						// HJUtil.privateKey);
						String hmac = DigestUtils.md5Hex(str1.toString() + busInfo.getKek());// HJUtil.privateKey
						result.put("p1_MerchantNo", busInfo.getBusinessnum());
						result.put("p2_OrderNo", param.getV_oid());
						result.put("p3_Amount", Double.parseDouble(param.getV_txnAmt()) + "");
						result.put("p4_Cur", "1");
						result.put("p5_ProductName", param.getV_productName());
						if (param.getV_attach() != null) {
							result.put("p6_Mp", param.getV_attach());
						}
						result.put("p7_ReturnUrl", BaseUtil.url+"/gateWay/hj_returnUrl.action");
						result.put("p8_NotifyUrl", BaseUtil.url+"/app_posp/gateWay/hj_notifyUrl.action");
						if (param.getV_bankAddr() != null) {
							result.put("p9_FrpCode", param.getV_bankAddr() + "_NET_B2C");
						} else {

							result.put("p9_FrpCode", "_NET_B2C");
						}

						result.put("pa_OrderPeriod", "0");
						result.put("hmac", hmac);
						break;
					case "TXZF":
						logger.info("************************天下支付----网关支付----处理 开始");
						paramsMap.put("spid", busInfo.getBusinessnum());// 商户号 "1800071515"
						paramsMap.put("sp_userid", "1800689259");// 用户号
						paramsMap.put("spbillno", param.getV_oid());// 商户订单号
						paramsMap.put("money", param.getV_txnAmt());// 交易金额
						paramsMap.put("cur_type", "1");// 币种类型
						paramsMap.put("notify_url", TFBConfig.notifyUrl);// 通知回调URL
						paramsMap.put("return_url", TFBConfig.returnUrl);// 成功跳转URL
						// paramsMap.put("errpage_url", param.getV_errorUrl());// 错误页面
						paramsMap.put("memo", param.getV_productName());// 商品名称
						if ("0".equals(param.getV_cardType())) {
							paramsMap.put("card_type", "1");// 借记卡

						} else if ("1".equals(param.getV_cardType())) {
							paramsMap.put("card_type", "2");// 贷记卡
						}
						String bankCode = "";
						String[] array = { "CCB", "ABC", "ICBC", "BOC", "SPDB", "CEB", "PINGAN", "CIB", "POST",
								"ECITIC", "HXB", "CMBCHINA", "CGB", "BCCB", "SHB", "CMBC", "BOCO", "BJRCB" };

						String[] array1 = { "1004", "1002", "1001", "1003", "1014", "1008", "1011", "1013", "1006",
								"1007", "1009", "1012", "1017", "1016", "1025", "1010", "1005", "1103" };

						for (int i = 0; i < array.length; i++) {

							if (param.getV_bankAddr().equals(array[i])) {
								bankCode = array1[i];
								paramsMap.put("bank_segment", bankCode);// 银行代号
								break;
							}

						}
						paramsMap.put("user_type", "1");// 用户类型
						// paramsMap.put("expire_time", param.getV_expire_time());// 订单有效时长
						paramsMap.put("channel", param.getV_channel());// 渠道类型
						paramsMap.put("encode_type", "MD5");
						// paramsMap.put("risk_ctrl",cardPayApplyＲequest.getRisk_ctrl());
						String paramSrc = RequestUtils.getParamSrc(paramsMap);
						log.info("上传上游前生成签名字符串:" + paramSrc);
						key = busInfo.getKek();// "12345";
						log.info("此商户对应上游秘钥:" + key);
						String sign2 = MD5Utils.sign(paramSrc, key, "GBK");
						log.info("此商户生成签名:" + sign2);
						paramSrc = paramSrc + "&sign=" + sign2;// +"&input_charset=UTF-8"
						log.info("加上签名之后的数据:" + paramSrc);
						String url = new File(this.getClass().getResource("/").getPath()).getParentFile()
								.getParentFile().getCanonicalPath() + "/ky" + TFBConfig.GC_PUBLIC_KEY_PATH;
						String publickey = RSAUtils.loadPublicKey(url);
						log.info("url:" + url);
						String cipherData = RSAUtils.encrypt(paramSrc.toString(), "GBK", publickey);
						log.info("加密结果:" + cipherData);

						result.put("cipher_data", URLEncoder.encode(cipherData, "GBK"));
						result.remove("respCode");
						result.remove("respMsg");

						break;
					case "HFB":
						logger.info("************************汇付宝----网关支付----处理 开始");
						Map<String, String> map2 = new HashMap<>();
						map2.put("merchantId", "100381");
						map2.put("merchantOrderNo", param.getV_oid());
						map2.put("merchantUserId", "100089282");
						map2.put("notifyUrl", HfbUtil.notifyUrl);
						map2.put("payAmount", Double.parseDouble(param.getV_txnAmt()) / 100 + "");
						map2.put("productCode", HfbUtil.productCode);
						map2.put("version", HfbUtil.version);
						String signString = HttpsUtil.createSign(map, busInfo.getKek());
						map2.put("requestTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
						map2.put("callBackUrl", param.getV_url() == null ? "" : param.getV_url());
						map2.put("onlineType", HfbUtil.onlineType);
						map2.put("signString", signString);
						break;
					case "JP":
						logger.info("************************九派----网关支付----处理 开始");
						Map<String, String> dataMap = new LinkedHashMap<String, String>();
						dataMap.put("charset", "02");// 字符集02：utf-8
						dataMap.put("version", "1.0");// 版本号
						dataMap.put("service", JpUtil.service);
						dataMap.put("signType", "RSA256");
						dataMap.put("merchantId", busInfo.getBusinessnum());// "800001400010085"
						dataMap.put("requestTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
						dataMap.put("requestId", String.valueOf(System.currentTimeMillis()));

						dataMap.put("pageReturnUrl", JpUtil.returnUrl);
						dataMap.put("notifyUrl", JpUtil.notifyUrl);
						dataMap.put("merchantName", "大饼鸡蛋");
						// dataMap.put("subMerchantId", "");
						dataMap.put("memberId", String.valueOf(System.currentTimeMillis()));
						dataMap.put("orderTime",
								param.getV_time() == "" ? new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
										: param.getV_time());
						dataMap.put("orderId", param.getV_oid());
						dataMap.put("totalAmount", param.getV_txnAmt());
						dataMap.put("currency", "CNY");
						dataMap.put("bankAbbr", param.getV_bankAddr());// "CMB"
						dataMap.put("cardType", "0");
						dataMap.put("payType", "B2C");// B2B个人/B2C公司

						dataMap.put("validUnit", "01");// 00分钟，01小时，02日，03月
						dataMap.put("validNum", "1");// 有效期数量
						// dataMap.put("showUrl", "");
						dataMap.put("goodsName", param.getV_productName());// 商品名称
						// dataMap.put("goodsId", "");
						// dataMap.put("goodsDesc", "");

						String merchantCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
								.getParentFile().getCanonicalPath() + "//ky//" + busInfo.getBusinessnum() + ".p12";
						String merchantCertPass = busInfo.getKek();// "nknEuX"; //秘钥
						result.putAll(dataMap);
						result.remove("respCode");
						result.remove("respMsg");
						Set set = dataMap.keySet();
						Iterator iterator = set.iterator();
						while (iterator.hasNext()) {
							key = (String) iterator.next();
							if ((dataMap.get(key) == null) || dataMap.get(key).toString().trim().length() == 0) {
								result.remove(key);
							}
						}
						RSASignUtil util = new RSASignUtil(merchantCertPath, merchantCertPass);

						String reqData = util.coverMap2String(result);
						util.setService(JpUtil.service);
						String merchantSign = util.sign(reqData, "UTF-8");
						String merchantCert = util.getCertInfo();
						// 请求报文
						String buf = reqData + "&merchantSign=" + merchantSign + "&merchantCert=" + merchantCert;
						log.info("给九派网关请求参数：" + buf);
						result.put("merchantSign", merchantSign);
						result.put("merchantCert", merchantCert);
						break;
					case "YY":
						logger.info("************************甬易----网关支付----处理 开始");
						// HashMap <String,String> payParam=new HashMap<String,String>();

						result.put("orderNo", param.getV_oid());// 订单号
						result.put("orderAmount", new BigDecimal(param.getV_txnAmt().toString())
								.multiply(new BigDecimal("100")).intValue() + "");// 订单金额
						result.put("returnAddress",
								BaseUtil.url+"/gateWay/yy_returnUrl.action?orderNo="
										+ param.getV_oid());// 跳转地址
						result.put("backAddress", BaseUtil.url+"/app_posp/gateWay/yy_notifyUrl.action");// 返回地址
						result.put("bankCode", param.getV_bankAddr());
						result.put("payType", "26");
						result.put("bankCardType", "1");
						result.put("merCode", "9001002015");// 商户编号
						result.put("dateTime", param.getV_time());// 交易时间
						result.remove("v_msg");
						result.remove("v_code");
						String str2 = EmaxPlusUtil.getSignPlainText(result);// 注意签名顺序
						log.info("甬易生成签名前的数据:" + str2);
						String sign3 = null;
						try {
							sign3 = EmaxPlusUtil._md5Encode(str2);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// 签名
						result.put("sign", sign3);
						break;
					case "YFWG":
						logger.info("************************----裕福  网关支付----处理 开始");
						DecimalFormat df1 = new DecimalFormat("######0"); //四色五入转换成整数
						Map<String, String> params1 = new HashMap<String, String>();
						params1.put("version", "1.0.0");
						params1.put("merchantId", busInfo.getBusinessnum());
						params1.put("merchantOrderId", param.getV_oid());
						params1.put("merchantOrderTime", param.getV_time());
						BigDecimal payAmt=new BigDecimal(param.getV_txnAmt()).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
						//Double dd = Double.parseDouble(param.getV_txnAmt()) * 100;
						params1.put("merchantOrderAmt", df1.format(payAmt));
						params1.put("merchantOrderCurrency", "156");
						//if ("1".equals(param.getV_channel())) {
							params1.put("gwType", "02");
						//} else if ("2".equals(param.getV_channel())) {
						//	params1.put("gwType", "01");
						//}
						params1.put("frontUrl", YFUtil.reistUrl);
						params1.put("backUrl", YFUtil.notifyUrl);// 商户异步通知接口
						params1.put("bankId", param.getV_bankAddr() == null ? "" : param.getV_bankAddr());
						params1.put("userType", "01");
						params1.put("merchantUserId", param.getV_oid());
						params1.put("merchantOrderDesc",
								param.getV_productDesc() == null ? "商品描述" : param.getV_productDesc());
						params1.put("merchantSettleInfo",
								"[{\"sumGoodsName\":\"" + param.getV_productDesc() + "\",\"merchantId\":\""
										+ busInfo.getBusinessnum() + "\"," + "\"merchantName\":\""
										+ param.getV_productName() + "\",\"orderAmt\":\"" + df1.format(payAmt) + "\"}]");
						if (!"".equals(param.getV_attach())) {
							params1.put("msgExt", param.getV_attach());
						}
						System.out.println("网银-置单上送报文：" + params1.toString());
						final String merCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
								.getParentFile().getCanonicalPath() + "/ky/" + params1.get("merchantId") + ".cer";
						final String pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
								.getParentFile().getCanonicalPath() + "//ky//" + params1.get("merchantId") + ".pfx";
						final String pfxPwd = busInfo.getKek();
						String urls = "";
						if ("000001110100000812".equals(busInfo.getBusinessnum())) {
							urls = "http://malltest.yfpayment.com/payment/payset.do";
						} else {
							urls = "http://www.yfpayment.com/payment/payset.do";
						}
						String retString = DoYf.doPaySet(urls, params1, merCertPath, pfxPath, pfxPwd);

						System.out.println("WEB-置单应答报文：" + retString.toString());
						result = JSON.parseObject(retString, new TypeReference<TreeMap<String, String>>() {
						});
						log.info("置单返回数据：" + JSON.toJSONString(result));

						break;
					case "SD":
						logger.info("************************----杉德  网关支付----处理 开始");
						// 组后台报文
						SandpayRequestHead head = new SandpayRequestHead();
						GatewayOrderPayRequestBody body = new GatewayOrderPayRequestBody();
						head.setVersion("1.0");
						head.setMethod("sandpay.trade.pay");
						head.setProductId("00000007");
						head.setAccessType("1");
						head.setMid("14270526");
						head.setChannelType("07");
						head.setReqTime(DateUtil.getCurrentDate14());
						DecimalFormat df = new DecimalFormat("000000000000");
						body.setOrderCode(param.getV_oid());
						body.setTotalAmount(df.format(Double.parseDouble(param.getV_txnAmt()) * 100));
						body.setSubject(param.getV_productName());
						body.setBody(param.getV_productDesc());
						body.setPayMode("bank_pc");
						body.setClientIp(InetAddress.getLocalHost().getHostAddress());
						body.setNotifyUrl(BaseUtil.url+"/gateWay/sd_notifyUrl.action");
						body.setFrontUrl(BaseUtil.url+"/gateWay/sd_returnUrl.action");
						logger.info("下游上送的银行编码:" + param.getV_bankAddr());
						PayBankInfo bank = new PayBankInfo();
						bank.setBank_code(param.getV_bankAddr());
						List<PayBankInfo> list = PayBankInfoService.selectBankCodes(bank);
						bank = list.get(0);
						String bancode = bank.getBank_code_id();
						logger.info("查询银行信息:" + bancode);
						String jsonss = "{\"bankCode\":\"" + bancode + "\",\"payType\":\"3\"}";
						body.setPayExtra(jsonss);
						GatewayOrderPayRequest gwOrderPayReq = new GatewayOrderPayRequest();
						gwOrderPayReq.setHead(head);
						gwOrderPayReq.setBody(body);
						try {
							// 加载证书
							CertUtil.init("classpath:sand.cer", "classpath:14270526.pfx", "5TPM7C3CVM");
							// 外网测试
							GatewayOrderPayResponse gwPayResponse = SandpayClient.execute(gwOrderPayReq,
									"https://cashier.sandpay.com.cn/gateway/api/order/pay");
							SandpayResponseHead respHead = gwPayResponse.getHead();

							if (SandpayConstants.SUCCESS_RESP_CODE.equals(respHead.getRespCode())) {
								logger.info("txn success.");

								GatewayOrderPayResponseBody respBody = gwPayResponse.getBody();
								String credential = respBody.getCredential();
								logger.info("杉德返回的信息:" + credential);
								request.setAttribute("JWP_ATTR", credential);
								// String url = "jsp/middle.jsp";
								request.getRequestDispatcher("/middle.jsp").forward(request, response);
							} else {
								logger.error("txn fail respCode[{}],respMsg[{}]." + respHead.getRespCode()
										+ respHead.getRespMsg());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "JBB":
						logger.info("************************----聚佰宝  网关支付----处理 开始");

						String version = "1.0.0";

						String transCode = "8888";

						// 商户编号
						String merchantId = "888201711290115";

						// 订单信息
						String merOrderNum = param.getV_oid();
						// 业务代码
						String bussId = "ONL0001";

						Integer amount = (int) (Double.parseDouble(param.getV_txnAmt()) * 100);

						String tranAmt = amount.toString();

						String sysTraceNum = param.getV_oid();

						String tranDateTime = param.getV_time();

						String currencyType = "156";

						String merURL = BaseUtil.url+"/gateWay/jbb_returnUrl.action?orderId="
								+ param.getV_oid();

						String backURL = BaseUtil.url+"/gateWay/jbb_notifyUrl.action";

						String orderInfo = "";

						String userId = UtilDate.getDateTime();

						String userIp = "";

						String bankId = param.getV_bankAddr();

						if ("CCB".equals(bankId)) {
							bankId = "888880160132907";
						} else if ("ICBC".equals(bankId)) {
							bankId = "888880160132903";
						} else if ("CEB".equals(bankId)) {
							bankId = "888880160132905";
						} else if ("POST".equals(bankId)) {
							bankId = "888880160132906";
						} else if ("BOB".equals(bankId)) {
							bankId = "888880160132912";
						} else if ("ABC".equals(bankId)) {
							bankId = "888880160132913";
						} else {
							logger.info("聚佰宝上送的银行编码为空!");
							return;
						}

						String stlmId = "";

						String entryType = "1";

						String attach = param.getV_attach();

						String reserver1 = "";

						String reserver2 = "";

						String reserver3 = "";

						String reserver4 = "";
						// 签名数据
						String txnString = version + "|" + transCode + "|" + merchantId + "|" + merOrderNum + "|"
								+ bussId + "|" + tranAmt + "|" + sysTraceNum + "|" + tranDateTime + "|" + currencyType
								+ "|" + merURL + "|" + backURL + "|" + orderInfo + "|" + userId;
						logger.info("聚佰宝上送的数据加密字符串:" + txnString);
						MD5 md = new MD5();
						String signValue = md.getMD5ofStr(txnString + "675FC1ctf2Y6zVm3");

						result.put("version", version);
						result.put("transCode", transCode);
						result.put("merchantId", merchantId);
						result.put("merOrderNum", merOrderNum);
						result.put("bussId", bussId);
						result.put("tranAmt", tranAmt);
						result.put("sysTraceNum", sysTraceNum);
						result.put("tranDateTime", tranDateTime);
						result.put("currencyType", currencyType);
						result.put("merURL", merURL);
						result.put("backURL", backURL);
						result.put("orderInfo", orderInfo);
						result.put("userId", userId);
						result.put("bankId", bankId);
						result.put("stlmId", stlmId);
						result.put("entryType", entryType);
						result.put("attach", attach);
						result.put("reserver1", reserver1);
						result.put("reserver2", reserver2);
						result.put("reserver3", reserver3);
						result.put("reserver4", reserver4);
						result.put("signValue", signValue);
						logger.info("聚佰宝上送的数据:" + result);
						break;
					case "YSB":
						logger.info("#############银生宝网关支付处理 开始#############");
						version = "3.0.0";
					    merchantId = busInfo.getBusinessnum();
					    String merchantUrl=BaseUtil.url+"/gateWay/ysb_notifyUrl.action";
					    String responseMode="2";
						String orderId = param.getV_oid();
						currencyType="CNY";
				        String amounts=param.getV_txnAmt();
				        String assuredPay="false";
				        String time=param.getV_time();
				        String remark=param.getV_attach();
				        bankCode=param.getV_bankAddr().toLowerCase();
				        String b2b="";
						String commodity = param.getV_productDesc();
						String orderUrl = "";
						String cardAssured="";
						String frontURL = BaseUtil.url+"/gateWay/ysb_returnUrl.action?orderId="+orderId;
						String keys = busInfo.getKek();
						String strss="merchantId=" + merchantId + "&merchantUrl=" + merchantUrl + "&responseMode="
								+ responseMode + "&orderId=" + orderId + "&currencyType=" + currencyType
								+ "&amount=" + amounts + "&assuredPay=" + assuredPay +"&time=" + time
								+ "&remark=" + remark + "&merchantKey=" + keys;
						logger.info("银生宝生成签名前的数据:" + strss);
						String signs = xdt.quickpay.conformityQucikPay.util.MD5Util.MD5Encode(strss.replaceAll("", "")).toUpperCase();
						logger.info("银生宝生成的签名:" + signs);
						result.put("version", version);
						result.put("merchantId", merchantId);
						result.put("merchantUrl", merchantUrl);
						result.put("responseMode", responseMode);
						result.put("orderId", orderId);
						result.put("currencyType", currencyType);
						result.put("amount", amounts);
						result.put("assuredPay", assuredPay);
						result.put("time", time);
						result.put("remark", remark);
						result.put("bankCode", bankCode);
						result.put("commodity", commodity);
						result.put("merchantKey", keys);
						result.put("frontURL", frontURL);
						result.put("mac", signs);
						break;
					default:
						break;

					}
					switch (busInfo.getChannelnum()) {
					case "SXYWG":
						logger.info("************************首信易----网关支付----请求开始");
						html = EffersonPayService.createAutoFormHtml(
								"https://www.joinpay.com/gateway/gateway_init.action", result, "UTF-8");
						outString(response, html);
						break;
					case "JS100669":
						logger.info("************************江苏电商----网关支付----请求开始");
						html = EffersonPayService.createAutoFormHtml(
								"https://www.joinpay.com/gateway/gateway_init.action", result, "UTF-8");
						outString(response, html);
						break;
					case "SXF":
						logger.info("************************随行付----网关支付----请求开始");
						html = EffersonPayService.createAutoFormHtml(
								"https://www.joinpay.com/gateway/gateway_init.action", result, "UTF-8");
						outString(response, html);
						break;
					case "HJZF":
						logger.info("************************汇聚----网关支付----请求开始");
						result.remove("v_msg");
						result.remove("v_code");
						logger.info("汇聚网关上送的数据:" + result);
						html = EffersonPayService.createAutoFormHtml(
								"https://www.joinpay.com/gateway/gateway_init.action", result, "UTF-8");
						outString(response, html);
						break;
					case "TXZF":
						logger.info("************************天下支付----网关支付----请求开始");
						html = result.get("cipher_data");
						String path = TFBConfig.cardPayApplyApi + "?cipher_data=" + html;
						log.info("demo 重定向：" + path);
						response.sendRedirect(path.replace(" ", " "));
						log.info("*******************执行完毕**************************");
						break;
					case "HFB":
						logger.info("************************汇付宝----网关支付----请求开始");
						html = EffersonPayService.createAutoFormHtml(
								"https://www.joinpay.com/gateway/gateway_init.action", result, "UTF-8");
						outString(response, html);
						break;
					case "JP":
						logger.info("************************九派----网关支付----请求开始");
						String url = "";
						if ("800001400010085".equals(busInfo.getBusinessnum())) {
							url = JpUtil.cardUrlTest;
						} else {
							url = JpUtil.cardUrl;
						}
						logger.info("给九派网关请求参数：" + result);
						html = EffersonPayService.createAutoFormHtml(url, result, "UTF-8");
						outString(response, html);
						break;
					case "YY":
						logger.info("************************甬易----网关支付----请求开始");
						result.remove("v_msg");
						result.remove("v_code");
						html = EffersonPayService.createAutoFormHtml("http://payment.emaxcard.com/DirectPay/Index",
								result, "UTF-8");
						outString(response, html);
						break;
					case "YFWG":
						logger.info("************************裕福----网关支付----请求开始");
						//
						String urls = "";
						if ("000001110100000812".equals(busInfo.getBusinessnum())) {
							urls = "http://malltest.yfpayment.com/payment/payshow.do";
						} else {
							urls = "http://www.yfpayment.com/payment/payshow.do";
						}
						html = EffersonPayService.createAutoFormHtml(urls, result, "UTF-8");

						logger.info("跳转的组装页面：" + html);
						outString(response, html);
						break;
					case "JBB":// 聚佰宝网关
						logger.info("************************聚佰宝----网关支付----请求开始");
						result.remove("v_msg");
						result.remove("v_code");
						logger.info("上送的数据:" + result);
						html = EffersonPayService.createAutoFormHtml(
								"https://cashier.etonepay.com/NetPay/BankSelect.action", result, "UTF-8");
						outString(response, html);
						break;
					case "YSB"://银生宝网关
						logger.info("************************银生宝----网关支付----请求开始");
						result.remove("v_msg");
						result.remove("v_code");
						logger.info("上送的数据:" + result);
						html = EffersonPayService.createAutoFormHtml(
								"https://www.unspay.com/unspay/page/linkbank/payRequest.do", result, "UTF-8");
						outString(response, html);
						break;
					default:
						break;
					}

				} else {
					result.put("v_code", result.get("v_code"));
					result.put("v_msg", result.get("v_msg"));
					outString(response, JSON.toJSON(result));
				}
			} else {
				log.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
				outString(response, JSON.toJSON(result));
			}

		} else {
			log.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
			outString(response, JSON.toJSON(result));
		}
		log.info("返回结果:{}"+ html);

	}

	@RequestMapping(value = "gateWay_query")
	public void query(GateWayQueryRequestEntity query, HttpServletResponse response) throws Exception {
		logger.info("------快捷查询上传参数：" + JSON.toJSONString(query));
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<>();
		if (!StringUtils.isEmpty(query.getV_mid())) {

			// 检验数据是否合法
			log.info("下游上送签名串{}" + query.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(query.getV_mid());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(query);
			if (signUtil.checkSign(map, merchantKey, log)) {

				log.info("对比签名成功");
				result = gateWayService.gateWayQuery(query);
				GateWayQueryResponseEntity queryconsume = (GateWayQueryResponseEntity) BeanToMapUtil
						.convertMap(GateWayQueryResponseEntity.class, result);
				log.info("---返回数据签名签的数据:" + beanToMap(queryconsume));
				String sign = SignatureUtil.getSign(beanToMap(queryconsume), merchantKey, log);
				log.info("---返回数据签名:" + sign);
				result.put("v_sign", sign);

			} else {
				log.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
			}

		} else {
			log.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		try {
			outString(response, JSON.toJSONString(result));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.log.info("向下游 发送数据成功");

	}
	@RequestMapping(value="yftk")
	public void  yftk(GatrWayGefundEntity entity, HttpServletRequest request, HttpServletResponse response) {
		log.info("裕福网关退款");
		try {
			
		logger.info("------裕福网关退款上传参数：" + JSON.toJSONString(entity));
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		Map<String, String> result = new HashMap<>();
		if (!StringUtils.isEmpty(entity.getV_mid())) {

			// 检验数据是否合法
			log.info("下游上送签名串{}" + entity.getV_sign());
			// 查询商户密钥
			ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(entity.getV_mid());
			// ------------------------需要改签名
			String merchantKey = keyinfo.getMerchantkey();
			SignatureUtil signUtil = new SignatureUtil();

			Map map = BeanToMapUtil.convertBean(entity);
			if (signUtil.checkSign(map, merchantKey, log)) {

				log.info("对比签名成功");
				result =gateWayService.gatYftk(entity, result);
				GateWayQueryResponseEntity queryconsume = (GateWayQueryResponseEntity) BeanToMapUtil
						.convertMap(GateWayQueryResponseEntity.class, result);
				log.info("---返回数据签名签的数据:" + beanToMap(queryconsume));
				String sign = SignatureUtil.getSign(beanToMap(queryconsume), merchantKey, log);
				log.info("---返回数据签名:" + sign);
				result.put("v_sign", sign);

			} else {
				log.error("签名错误!");
				result.put("v_code", "02");
				result.put("v_msg", "签名错误!");
			}

		} else {
			log.error("上送交易参数空!");
			result.put("v_code", "01");
			result.put("v_msg", "上送交易参数空");
		}
		
			outString(response, JSON.toJSONString(result));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.log.info("向下游 发送数据成功");
	}
	
	/**
	 * 汇聚网关异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "hj_notifyUrl")
	public void HjnotifyUrl(HJResponse hjResponse, HttpServletResponse response, HttpServletRequest request) {
		request.getSession();
		log.info("汇聚----异步通知返回数据:" + JSON.toJSONString(hjResponse));

		log.info("汇聚异步通知来了");
		Map<String, String> result = new HashMap<String, String>();
		// HJResponse hjResponses =new HJResponse();
		try {
			String str;
			if (hjResponse.getR2_OrderNo() != null) {
				str = "success";
				outString(response, str);
				OriginalOrderInfo originalInfo = null;
				if (hjResponse.getR2_OrderNo() != null && hjResponse.getR2_OrderNo() != "") {
					originalInfo = gateWayService.getOriginOrderInfo(hjResponse.getR2_OrderNo());
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				log.info("下游的异步地址" + originalInfo.getBgUrl());
				service.update(hjResponse, originalInfo);
				if ("100".equals(hjResponse.getR6_Status())) {
					if (!"10052270614".equals(originalInfo.getPid())) {
						int ii = service.UpdatePmsMerchantInfo(originalInfo);
						log.info("汇聚异步订单信息:" + ii);
					}
				}
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_attach", originalInfo.getAttach());
				result.put("v_mid", originalInfo.getPid());
				result.put("v_msg", "请求成功");
				if ("100".equals(hjResponse.getR6_Status())) {
					result.put("v_status", "0000");
					result.put("v_status_msg", "支付成功");
				} else {
					result.put("v_status", "1001");
					result.put("v_status_msg", "支付失败");
				}
				// 和下面的签名
				// ---------------------------------------------------
				logger.info("汇聚给下游异步前的数据:" + result);
				ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				logger.info("汇聚给下游异步的key:" + key);
				GateWayQueryResponseEntity gatewey = (GateWayQueryResponseEntity) BeanToMapUtil
						.convertMap(GateWayQueryResponseEntity.class, result);
				// 修改订单状态
				gateWayService.otherInvoke(gatewey);
				String sign = SignatureUtil.getSign(beanToMap(gatewey), key, log);
				logger.info("汇聚给下游异步的签名:" + sign);
				result.put("v_sign", sign);
				GateWayQueryResponseEntity gatewey1 = (GateWayQueryResponseEntity) BeanToMapUtil
						.convertMap(GateWayQueryResponseEntity.class, result);
				// String params = HttpURLConection.parseParams(result);
				;
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				logger.info("汇聚给下游异步后的数据:" + bean2Util.bean2QueryStr(gatewey1));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(gatewey1));
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
				if (!map.get("success").equals("true")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(gatewey1)));
				}
				logger.info("向下游 发送数据成功");
				request.getSession();
			} else {
				str = "FAIL";
				outString(response, str);
			}
		} catch (Exception e) {
			log.info("汇聚异步回调异常:" + e);
			e.printStackTrace();
		}
	}

	/**
	 * 裕福网关异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "yf_notifyUrl")
	public void YFNotifyUrl(HttpServletResponse response, HttpServletRequest request) {
		request.getSession();
		log.info("裕福异步通知来了");
		Map<String, String> result = new HashMap<String, String>();
		// HJResponse hjResponses =new HJResponse();
		YufuCipher cipher = null;
		YufuCipherSupport instance = null;
		try {
			final String merCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
					.getParentFile().getCanonicalPath() + "//ky//" + request.getParameter("merchantId") + ".cer";
			final String pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile()
					.getCanonicalPath() + "//ky//" + request.getParameter("merchantId") + ".pfx";
			final String pfxPwd = "123456";
			String str;
			cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd, cipher, instance);
			Map<String, String> synchNotifyParams = cipher.unPack(new ParamPacket(request.getParameter("data"),
					request.getParameter("enc"), request.getParameter("sign")));
			log.info("裕福----异步通知返回数据:" + JSON.toJSONString(synchNotifyParams));
			String orderId = synchNotifyParams.get("merchantOrderId");
			OriginalOrderInfo originalInfo = null;
			if (orderId != null) {
				str = "success";
				outString(response, str);
				if (orderId != null && orderId != "") {
					originalInfo = gateWayService.getOriginOrderInfo(orderId);
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				log.info("下游的异步地址" + originalInfo.getBgUrl());

				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_attach", synchNotifyParams.get("bpSerialNum"));
				result.put("v_mid", originalInfo.getPid());
				result.put("v_msg", "请求成功");
				if ("01".equals(synchNotifyParams.get("transStatus"))) {
					if("000001110100000663".equals(request.getParameter("merchantId"))) {
						int ii =0;
						if("10036048439".equals(originalInfo.getPid())||"10032051737".equals(originalInfo.getPid())||"10032057782".equals(originalInfo.getPid())) {
							ii =gateWayService.UpdatePmsMerchantInfo(originalInfo,1.0);
						}else {
							ii =gateWayService.UpdatePmsMerchantInfo(originalInfo,0.5);
						}
						
						 log.info("裕福实时入金状态:"+ii);
					}
					result.put("v_status", "0000");
					result.put("v_status_msg", "支付成功");
				} else if ("02".equals(synchNotifyParams.get("transStatus"))) {
					result.put("v_status", "1001");
					result.put("v_status_msg", "支付失败");
				}
				// 和下面的签名
				// ---------------------------------------------------
				ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				GateWayQueryResponseEntity gatewey = (GateWayQueryResponseEntity) BeanToMapUtil
						.convertMap(GateWayQueryResponseEntity.class, result);
				// 修改订单状态
				gateWayService.otherInvoke(gatewey);
				gatewey.setV_attach(originalInfo.getAttach());
				/*result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_attach", originalInfo.getAttach());
				result.put("v_mid", originalInfo.getPid());
				result.put("v_msg", "请求成功");
				if ("01".equals(synchNotifyParams.get("transStatus"))) {
					result.put("v_status", "0000");
					result.put("v_status_msg", "支付成功");
				} else if ("02".equals(synchNotifyParams.get("transStatus"))) {
					result.put("v_status", "1001");
					result.put("v_status_msg", "支付失败");
				}*/
				result.put("v_attach", originalInfo.getAttach());
				// 和下面的签名
				// ---------------------------------------------------
				logger.info("裕福给下游异步前的数据:" + result);
				String sign = SignatureUtil.getSign(beanToMap(gatewey), keyinfo.getMerchantkey(), log);
				logger.info("裕福给下游异步的签名:" + sign);
				result.put("v_sign", sign);
				gatewey.setV_sign(sign);
				//GateWayQueryResponseEntity gatewey1 = (GateWayQueryResponseEntity) BeanToMapUtil
				//		.convertMap(GateWayQueryResponseEntity.class, result);
				// String params = HttpURLConection.parseParams(result);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				logger.info("裕福给下游异步后的数据:" + bean2Util.bean2QueryStr(gatewey));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(gatewey));
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
				if (!map.get("success").equals("true")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(gatewey)));
				}
				logger.info("向下游 发送数据成功");
				request.getSession();
			} else {
				str = "FAIL";
				outString(response, str);
			}
		} catch (Exception e) {
			log.info("裕福异步回调异常:" + e);
			e.printStackTrace();
		}
	}

	/**
	 * 勇易网关异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "yy_notifyUrl")
	public void YynotifyUrl(HttpServletResponse response, HttpServletRequest request) {
		request.getSession();
		log.info("甬易异步通知来了");
		Map<String, String> result = new HashMap<String, String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream(), "UTF-8"));

			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			String key = "";
			String appMsg = sb.toString();
			logger.info("请求参数：" + appMsg);
			String str = "";
			if (!StringUtils.isEmpty(appMsg)) {
				str = "success";
				outString(response, str);
				JSONObject ob = JSONObject.fromObject(appMsg);
				logger.info("封装之后的数据:{}" + ob);
				Iterator it = ob.keys();
				String orderNo = "";
				String resultStatus = "";
				String resultCode = "";
				while (it.hasNext()) {
					key = (String) it.next();
					if (key.equals("resultCode")) {
						resultCode = ob.getString(key);
						logger.info("甬易返回的异步状态码:" + "\t" + resultCode);
					}
					if (key.equals("orderNo")) {
						orderNo = ob.getString(key);
						logger.info("甬易返回的原始订单号:" + "\t" + orderNo);
					}
					if (key.equals("resultStatus")) {

						resultStatus = ob.getString(key);
						logger.info("甬易返回的异步状态信息:" + "\t" + resultStatus);
					}
				}
				OriginalOrderInfo originalInfo = null;
				if (orderNo != null && orderNo != "") {
					originalInfo = gateWayService.getOriginOrderInfo(orderNo);
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				log.info("下游的异步地址" + originalInfo.getBgUrl());
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_attach", originalInfo.getAttach());
				result.put("v_mid", originalInfo.getPid());
				result.put("v_msg", "请求成功");
				if ("000000".equals(resultCode)) {
					int ii = gateWayService.UpdatePmsMerchantInfo(originalInfo,1.0);
					log.info("汇聚异步订单信息:" + ii);
					result.put("v_status", "0000");
					result.put("v_status_msg", "支付成功");
				} else {
					result.put("v_status", "1001");
					result.put("v_status_msg", "支付失败");
				}
				// 和下面的签名
				// ---------------------------------------------------
				logger.info("甬易给下游异步前的数据:" + result);
				ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				key = keyinfo.getMerchantkey();
				logger.info("甬易给下游异步的key:" + key);
				GateWayQueryResponseEntity gatewey = (GateWayQueryResponseEntity) BeanToMapUtil
						.convertMap(GateWayQueryResponseEntity.class, result);
				// 修改订单状态
				gateWayService.otherInvoke(gatewey);
				String sign = SignatureUtil.getSign(beanToMap(gatewey), key, log);
				logger.info("甬易给下游异步的签名:" + sign);
				result.put("v_sign", sign);
				GateWayQueryResponseEntity gatewey1 = (GateWayQueryResponseEntity) BeanToMapUtil
						.convertMap(GateWayQueryResponseEntity.class, result);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				logger.info("甬易给下游异步后的数据:" + bean2Util.bean2QueryStr(gatewey1));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(gatewey1));
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
				if (!map.get("success").equals("true")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(gatewey1)));
					logger.info("向下游 发送数据成功");
					request.getSession();
				}
			} else {
				logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 杉德网关异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "sd_notifyUrl")
	public void sdnotifyUrl(HttpServletResponse response, HttpServletRequest request) {
		request.getSession();
		log.info("杉德异步通知来了");
		try {
			Map<String, String[]> parameterMap = request.getParameterMap();
			Map<String, String> result = new HashMap<String, String>();

			String str = "";
			if (parameterMap == null || parameterMap.isEmpty()) {
				logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			} else {
				str = "success";
				for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {

					logger.info("key:" + entry.getKey() + "value：" + entry.getValue());

				}
				String orderCode = parameterMap.get("orderCode").toString();
				String status = parameterMap.get("orderStatus").toString();
				logger.error("杉德异步回调返回的状态码:" + parameterMap.get("orderStatus"));
				logger.error("杉德异步回调返回的订单号:" + orderCode);
				OriginalOrderInfo originalInfo = null;
				if (orderCode != null && orderCode != "") {
					originalInfo = gateWayService.getOriginOrderInfo(orderCode);
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				log.info("下游的异步地址" + originalInfo.getBgUrl());
				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_attach", originalInfo.getAttach());
				result.put("v_mid", originalInfo.getPid());
				result.put("v_msg", "请求成功");
				if ("1".equals(status)) {
					int ii = gateWayService.UpdatePmsMerchantInfo(originalInfo,1.0);
					log.info("杉德异步订单信息:" + ii);
					result.put("v_status", "0000");
					result.put("v_status_msg", "支付成功");
				} else {
					result.put("v_status", "1001");
					result.put("v_status_msg", "支付失败");
				}
				// 和下面的签名
				// ---------------------------------------------------
				logger.info("杉德给下游异步前的数据:" + result);
				ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
				// 获取商户秘钥
				String key = keyinfo.getMerchantkey();
				logger.info("杉德给下游异步的key:" + key);
				GateWayQueryResponseEntity gatewey = (GateWayQueryResponseEntity) BeanToMapUtil
						.convertMap(GateWayQueryResponseEntity.class, result);
				// 修改订单状态
				gateWayService.otherInvoke(gatewey);
				String sign = SignatureUtil.getSign(beanToMap(gatewey), key, log);
				logger.info("杉德给下游异步的签名:" + sign);
				result.put("v_sign", sign);
				GateWayQueryResponseEntity gatewey1 = (GateWayQueryResponseEntity) BeanToMapUtil
						.convertMap(GateWayQueryResponseEntity.class, result);
				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
				logger.info("杉德给下游异步后的数据:" + bean2Util.bean2QueryStr(gatewey1));
				String html = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(gatewey1));
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
				if (!map.get("success").equals("true")) {

					logger.info("启动线程进行异步通知");
					// 启线程进行异步通知
					ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(gatewey1)));
					logger.info("向下游 发送数据成功");
					request.getSession();
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 聚佰宝网关异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "jbb_notifyUrl")
	public void jbbnotifyUrl(HttpServletResponse response, HttpServletRequest request) {
		request.getSession();
		try {
			logger.info("聚佰宝网关异步通知来了");
			String respCode = request.getParameter("respCode");
			String sysTraceNu = request.getParameter("sysTraceNu");
			String merOrderNum = request.getParameter("merOrderNum");
			logger.info("聚佰宝网关异步响应订单号：" + merOrderNum);
			logger.info("聚佰宝网关异步响应流水号：" + sysTraceNu);
			logger.info("聚佰宝网关异步响应状态码：" + respCode);
			OriginalOrderInfo originalInfo = null;

			Map<String, String> result = new HashMap<String, String>();
			if (!StringUtils.isEmpty(respCode)) {
				response.getWriter().write("success");
				// 查询原始订单信息
				if (merOrderNum != null && merOrderNum != "") {
					originalInfo = this.gateWayService.getOriginOrderInfo(merOrderNum);
				}
				logger.info("聚佰宝绑卡异步订单数据:" + JSON.toJSON(originalInfo));
				// 订单信息
				PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoService.searchOrderInfo(originalInfo.getOrderId());
				if (pmsAppTransInfo != null) {
					logger.info("回调订单信息数据：" + JSON.toJSON(pmsAppTransInfo));
					if (!"0".equals(pmsAppTransInfo.getStatus())) {
						logger.info("订单表信息" + pmsAppTransInfo);
						result.put("v_oid", originalInfo.getOrderId());
						result.put("v_txnAmt", originalInfo.getOrderAmount());
						result.put("v_code", "00");
						result.put("v_attach", originalInfo.getAttach());
						result.put("v_mid", originalInfo.getPid());
						result.put("v_time", UtilDate.getTXDateTime());
						if ("0000".equals(respCode)) {
							result.put("v_status", "0000");
							result.put("v_msg", "支付成功");
							if ("0".equals(originalInfo.getPayType())) {
								int i = gateWayService.UpdatePmsMerchantInfo(originalInfo,1.0);
								if (i > 0) {
									logger.info("聚佰宝*****实时入金完成");
								} else {
									logger.info("聚佰宝*****实时入金失败");
								}
							}

						} else {
							result.put("v_status", "1001");
							result.put("v_msg", "支付失败");
							logger.info("交易错误码:" + request.getParameter("payStatus") + ",错误信息:"
									+ URLDecoder.decode(request.getParameter("payMsg"), "UTF-8"));
						}
						ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
						// 获取商户秘钥
						String key = keyinfo.getMerchantkey();
						GateWayQueryResponseEntity gatewey = (GateWayQueryResponseEntity) BeanToMapUtil
								.convertMap(GateWayQueryResponseEntity.class, result);
						// 修改订单状态
						gateWayService.otherInvoke(gatewey);

						// 生成签名
						String sign = SignatureUtil.getSign(beanToMap(gatewey), key, log);
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
						if (!map.get("success").equals("true")) {

							logger.info("启动线程进行异步通知");
							// 启线程进行异步通知
							ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),
									bean2Util.bean2QueryStr(consumeResponseEntity)));
						}

						logger.info("向下游 发送数据成功");

					} else {
						logger.error("回调的参数为空!");
						result.put("v_code", "15");
						result.put("v_msg", "请求失败");
					}
					outString(response, gson.toJson(result));
				}

			}

		} catch (

		Exception e) {
			logger.info("聚佰宝异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	/**
	 * 银生宝网关异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ysb_notifyUrl")
	public void ysbnotifyUrl(HttpServletResponse response, HttpServletRequest request) {
		request.getSession();
		try {
			logger.info("银生宝网关异步通知来了");
			Map<String, String> result = new HashMap<String, String>();	
			String merchantId = request.getParameter("merchantId");
			String merchantKey = "liangshuai123"; //密钥
			String responseMode = request.getParameter("responseMode");
			 String orderId = request.getParameter("orderId");
			 String currencyType = request.getParameter("currencyType");
			 String amount = request.getParameter("amount");
			String returnCode = request.getParameter("returnCode");
			String returnMessage = request.getParameter("returnMessage");
			logger.info("银生宝网关异步响应订单号：" + orderId);
			logger.info("银生宝网关异步响应状态描述：" + returnMessage);
			logger.info("银生宝网关异步响应状态码：" + returnCode);
			String mac = request.getParameter("mac") ;
			boolean success = "0000".equals(returnCode);
			boolean paid = "0001".equals(returnCode);
			StringBuffer s = new StringBuffer(50);
			//拼成数据串
			s.append("merchantId=").append(merchantId);
			s.append("&responseMode=").append(responseMode);
			s.append("&orderId=").append(orderId);
			s.append("&currencyType=").append(currencyType);
			s.append("&amount=").append(amount);
			s.append("&returnCode=").append(returnCode);
			s.append("&returnMessage=").append(returnMessage);
			s.append("&merchantKey=").append(merchantKey);
			//md5加密
			String nowMac = new MD5().getMD5ofStr(s.toString());
			if(nowMac.equals(mac)){ //若mac校验匹配
				response.getWriter().write("success");
				OriginalOrderInfo originalInfo = null;
							
					// 查询原始订单信息
					if (orderId != null && orderId != "") {
						originalInfo = this.gateWayService.getOriginOrderInfo(orderId);
					}
					logger.info("银生宝绑卡异步订单数据:" + JSON.toJSON(originalInfo));
					// 订单信息
					PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoService.searchOrderInfo(originalInfo.getOrderId());
					if (pmsAppTransInfo != null) {
						logger.info("回调订单信息数据：" + JSON.toJSON(pmsAppTransInfo));
						if (!"0".equals(pmsAppTransInfo.getStatus())) {
							logger.info("订单表信息" + pmsAppTransInfo);
							result.put("v_oid", originalInfo.getOrderId());
							result.put("v_txnAmt", originalInfo.getOrderAmount());
							result.put("v_code", "00");
							result.put("v_attach", originalInfo.getAttach());
							result.put("v_mid", originalInfo.getPid());
							result.put("v_time", UtilDate.getTXDateTime());
							if ("0000".equals(returnCode)) {
								result.put("v_status", "0000");
								result.put("v_msg", "支付成功");
							} else {
								result.put("v_status", "1001");
								result.put("v_msg", "支付失败:"+request.getParameter("returnMessage"));
								logger.info("交易错误码:" + request.getParameter("returnCode") + ",错误信息:"
										+ request.getParameter("returnMessage"));
							}
							ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
							// 获取商户秘钥
							String key = keyinfo.getMerchantkey();
							GateWayQueryResponseEntity gatewey = (GateWayQueryResponseEntity) BeanToMapUtil
									.convertMap(GateWayQueryResponseEntity.class, result);
							// 修改订单状态
							gateWayService.otherInvoke(gatewey);

							// 生成签名
							String sign = SignatureUtil.getSign(beanToMap(gatewey), key, log);
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
							if (!map.get("success").equals("true")) {

								logger.info("启动线程进行异步通知");
								// 启线程进行异步通知
								ThreadPool.executor(new MbUtilThread(originalInfo.getBgUrl(),
										bean2Util.bean2QueryStr(consumeResponseEntity)));
							}
							logger.info("银生宝网关向下游 发送数据成功");					
					}
				}				
			}else{ //若mac校验不匹配
				logger.error("回调的参数为空!");
				result.put("v_code", "15");
				result.put("v_msg", "请求失败");
			}	
		outString(response, gson.toJson(result));
		} catch (

		Exception e) {
			logger.info("银生宝异步回调异常:" + e);
			e.printStackTrace();
		}
		
	}


	
	/**
	 * 裕福网关退款异步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "yftk_notifyUrl")
	public void yftk_notifyUrl(HttpServletResponse response, HttpServletRequest request) {
		request.getSession();
		log.info("裕福退款异步通知来了");
		Map<String, String> result = new HashMap<String, String>();
		// HJResponse hjResponses =new HJResponse();
		YufuCipher cipher= null;
		YufuCipherSupport instance = null;
		try {
			final String merCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
					.getParentFile().getCanonicalPath() + "//ky//" + request.getParameter("merchantId") + ".cer";
			final String pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile()
					.getCanonicalPath() + "//ky//" + request.getParameter("merchantId") + ".pfx";
			final String pfxPwd = "123456";
			String str;
			cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd, cipher, instance);
			Map<String, String> synchNotifyParams = cipher.unPack(new ParamPacket(request.getParameter("data"),
					request.getParameter("enc"), request.getParameter("sign")));
			log.info("裕福----退款异步通知返回数据:" + JSON.toJSONString(synchNotifyParams));
			String orderId = synchNotifyParams.get("merchantOrderId");
			OriginalOrderInfo originalInfo = null;
			if (orderId != null) {
				str = "success";
				outString(response, str);
				/*if (orderId != null && orderId != "") {
					originalInfo = gateWayService.getOriginOrderInfo(orderId);
				}
				log.info("订单数据:" + JSON.toJSON(originalInfo));
				log.info("下游的异步地址" + originalInfo.getBgUrl());

				result.put("v_oid", originalInfo.getOrderId());
				result.put("v_txnAmt", originalInfo.getOrderAmount());
				result.put("v_code", "00");
				result.put("v_time", originalInfo.getOrderTime());
				result.put("v_attach", synchNotifyParams.get("bpSerialNum"));
				result.put("v_mid", originalInfo.getPid());
				result.put("v_msg", "请求成功");
				if ("01".equals(synchNotifyParams.get("transStatus"))) {
					result.put("v_status", "1004");
					result.put("v_status_msg", "退款成功");
				} 
				// 和下面的签名
				// ---------------------------------------------------
				// 获取商户秘钥
				GateWayQueryResponseEntity gatewey = (GateWayQueryResponseEntity) BeanToMapUtil
						.convertMap(GateWayQueryResponseEntity.class, result);
				// 修改订单状态
				gateWayService.otherInvoke(gatewey);*/

			} else {
				str = "FAIL";
				outString(response, str);
			}
		} catch (Exception e) {
			log.info("裕福异步回调异常:" + e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 汇聚网关同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "hj_returnUrl")
	public void HjreturnUrl(HJResponse hjResponse, HttpServletResponse response, HttpServletRequest request) {
		request.getSession();
		try {
			log.info("汇聚同步数据返回参数:" + JSON.toJSONString(hjResponse));
			Map<String, String> result = new HashMap<String, String>();
			// HJResponse hjResponses =new HJResponse();
			OriginalOrderInfo originalInfo = null;
			if (hjResponse.getR2_OrderNo() != null && hjResponse.getR2_OrderNo() != "") {
				originalInfo = gateWayService.getOriginOrderInfo(hjResponse.getR2_OrderNo());
			}
			log.info("订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			log.info("下游的同步地址" + originalInfo.getPageUrl());
			log.info("汇聚同步返回解析参数" + JSON.toJSON(hjResponse));
			// ---------------------------------------------------
			// 返回参数
			ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
			// 获取商户秘钥
			String key = keyinfo.getMerchantkey();
			result.put("v_oid", originalInfo.getOrderId());
			result.put("v_txnAmt", originalInfo.getOrderAmount());
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
			result.put("v_time", originalInfo.getOrderTime());
			result.put("v_mid", originalInfo.getPid());
			GateWayResponseEntity gatewey = (GateWayResponseEntity) BeanToMapUtil
					.convertMap(GateWayResponseEntity.class, result);
			String sign = SignatureUtil.getSign(beanToMap(gatewey), key, log);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;

	}

	/**
	 * 甬易网关同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "yy_returnUrl")
	public void YyreturnUrl(HttpServletResponse response, HttpServletRequest request) {

		log.info("甬易同步结果来了");
		TreeMap<String, String> result = new TreeMap<String, String>();
		String orderId = request.getParameter("orderNo");
		log.info("沈阳银盛同步返回的订单号:" + orderId);
		OriginalOrderInfo originalInfo = new OriginalOrderInfo();
		ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
		try {
			originalInfo = Origi.get(orderId);
			keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.info("甬易同步订单数据:" + JSON.toJSON(originalInfo));

		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.put("v_oid", originalInfo.getOrderId());
		result.put("v_txnAmt", originalInfo.getOrderAmount());
		result.put("v_code", "00");
		result.put("v_msg", "请求成功");
		result.put("v_time", originalInfo.getOrderTime());
		result.put("v_mid", originalInfo.getPid());
		ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil.convertMap(ConsumeResponseEntity.class,
				result);
		String sign = SignatureUtil.getSign(beanToMap(consume), key, log);
		result.put("v_sign", sign);
		String params = HttpURLConection.parseParams(result);
		log.info("给下游同步的数据:" + params);
		request.getSession();
		try {
			// 给下游手动返回支付结果
			if (originalInfo.getPageUrl().indexOf("?") == -1) {

				String path = originalInfo.getPageUrl() + "?" + params;
				log.info("pageUrl 商户页面 重定向：" + path);

				response.sendRedirect(path.replace(" ", ""));
			} else {
				log.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
				String path = originalInfo.getPageUrl() + "&" + params;
				log.info("pageUrl 商户页面 重定向：" + path);
				response.sendRedirect(path.replace(" ", ""));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 杉德网关同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "sd_returnUrl")
	public void sdreturnUrl(HttpServletResponse response, HttpServletRequest request) {

		log.info("甬易同步结果来了");
		TreeMap<String, String> result = new TreeMap<String, String>();
		String orderId = request.getParameter("orderNo");
		log.info("沈阳银盛同步返回的订单号:" + orderId);
		OriginalOrderInfo originalInfo = new OriginalOrderInfo();
		ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
		try {
			originalInfo = Origi.get(orderId);
			keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.info("甬易同步订单数据:" + JSON.toJSON(originalInfo));

		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.put("v_oid", originalInfo.getOrderId());
		result.put("v_txnAmt", originalInfo.getOrderAmount());
		result.put("v_code", "00");
		result.put("v_msg", "请求成功");
		result.put("v_time", originalInfo.getOrderTime());
		result.put("v_mid", originalInfo.getPid());
		ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil.convertMap(ConsumeResponseEntity.class,
				result);
		String sign = SignatureUtil.getSign(beanToMap(consume), key, log);
		result.put("v_sign", sign);
		String params = HttpURLConection.parseParams(result);
		log.info("给下游同步的数据:" + params);
		request.getSession();
		try {
			// 给下游手动返回支付结果
			if (originalInfo.getPageUrl().indexOf("?") == -1) {

				String path = originalInfo.getPageUrl() + "?" + params;
				log.info("pageUrl 商户页面 重定向：" + path);

				response.sendRedirect(path.replace(" ", ""));
			} else {
				log.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
				String path = originalInfo.getPageUrl() + "&" + params;
				log.info("pageUrl 商户页面 重定向：" + path);
				response.sendRedirect(path.replace(" ", ""));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 裕福网关同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "yf_returnUrl")
	public void YFReturnUrl(HttpServletResponse response, HttpServletRequest request) {

		YufuCipher cipher = null;
		YufuCipherSupport instance = null;
		OriginalOrderInfo originalInfo = null;
		log.info("裕福同步结果来了");
		TreeMap<String, String> result = new TreeMap<String, String>();
		ChannleMerchantConfigKey keyinfo = new ChannleMerchantConfigKey();
		Map<String, String> synchNotifyParams = new HashMap<>();
		PmsBusinessPos busInfo = null;
		try {
			busInfo = iTotalPayService.selectMer(request.getParameter("merchantId"));
			final String merCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
					.getParentFile().getCanonicalPath() + "//ky//" + request.getParameter("merchantId") + ".cer";
			final String pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile()
					.getCanonicalPath() + "//ky//" + request.getParameter("merchantId") + ".pfx";
			final String pfxPwd = busInfo.getKek();
			cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath, pfxPwd, cipher, instance);
			synchNotifyParams = cipher.unPack(new ParamPacket(request.getParameter("data"), request.getParameter("enc"),
					request.getParameter("sign")));
			log.info("裕福----同步通知返回数据:" + JSON.toJSONString(synchNotifyParams));
			String orderId = synchNotifyParams.get("merchantOrderId");
			String bpSerialNum = synchNotifyParams.get("bpSerialNum");
			// int ii =gateWayService.updateBusinfo(orderId, bpSerialNum);
			// log.info("ii:"+ii);
			originalInfo = Origi.get(orderId);
			keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		log.info("裕福同步订单数据:" + JSON.toJSON(originalInfo));

		// 获取商户秘钥
		String key = keyinfo.getMerchantkey();
		result.put("v_oid", originalInfo.getOrderId());
		result.put("v_txnAmt", originalInfo.getOrderAmount());
		if ("01".equals(synchNotifyParams.get("transStatus"))) {
			result.put("v_code", "0000");
			result.put("v_msg", "支付成功");
		} else if ("02".equals(synchNotifyParams.get("transStatus"))) {
			result.put("v_code", "1001");
			result.put("v_msg", "支付失败");
		} else if ("03".equals(synchNotifyParams.get("transStatus"))) {
			result.put("v_code", "1002");
			result.put("v_msg", "支付中");
		} else if ("04".equals(synchNotifyParams.get("transStatus"))) {
			result.put("v_code", "1003");
			result.put("v_msg", "支付风险审核中");
		}
		result.put("v_time", originalInfo.getOrderTime());
		result.put("v_mid", originalInfo.getPid());
		ConsumeResponseEntity consume = (ConsumeResponseEntity) BeanToMapUtil.convertMap(ConsumeResponseEntity.class,
				result);
		String sign = SignatureUtil.getSign(beanToMap(consume), key, log);
		result.put("v_sign", sign);
		String params = HttpURLConection.parseParams(result);
		log.info("给下游同步的数据:" + params);
		request.getSession();
		try {
			// 给下游手动返回支付结果
			if (originalInfo.getPageUrl().indexOf("?") == -1) {

				String path = originalInfo.getPageUrl() + "?" + params;
				log.info("pageUrl 商户页面 重定向：" + path);

				response.sendRedirect(path.replace(" ", ""));
			} else {
				log.info("pageUrl 商户页面 重定向：" + originalInfo.getPageUrl());
				String path = originalInfo.getPageUrl() + "&" + params;
				log.info("pageUrl 商户页面 重定向：" + path);
				response.sendRedirect(path.replace(" ", ""));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 聚佰宝网关同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "jbb_returnUrl")
	public void jbbReturnUrl(HttpServletResponse response, HttpServletRequest request) {

		try {
			logger.info("聚佰宝同步数据返回参数:");
			String orderId = request.getParameter("orderId");

			OriginalOrderInfo originalInfo = null;
			if (orderId != null && orderId != "") {
				originalInfo = this.gateWayService.getOriginOrderInfo(orderId);
			}
			logger.info("订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			logger.info("下游的同步地址" + originalInfo.getPageUrl());
			TreeMap<String, String> result = new TreeMap<String, String>();
			String params = "";
			if (!StringUtils.isEmpty(orderId)) {
				ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
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
				String sign = SignatureUtil.getSign(beanToMap(consume), key, log);
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
				logger.info("没有收到聚佰宝的同步数据");
				// outString(response, str);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	/**
	 * 银生宝网关同步响应信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ysb_returnUrl")
	public void ysbReturnUrl(HttpServletResponse response, HttpServletRequest request) {

		try {
			logger.info("##########银生宝同步数据返回参数##############");
			String orderId = request.getParameter("orderId");

			OriginalOrderInfo originalInfo = null;
			if (orderId != null && orderId != "") {
				originalInfo = this.gateWayService.getOriginOrderInfo(orderId);
			}
			logger.info("订单数据:" + JSON.toJSON(originalInfo));
			Bean2QueryStrUtil queryUtil = new Bean2QueryStrUtil();
			logger.info("下游的同步地址" + originalInfo.getPageUrl());
			TreeMap<String, String> result = new TreeMap<String, String>();
			String params = "";
			if (!StringUtils.isEmpty(orderId)) {
				ChannleMerchantConfigKey keyinfo = gateWayService.getChannelConfigKey(originalInfo.getPid());
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
				String sign = SignatureUtil.getSign(beanToMap(consume), key, log);
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
				logger.info("没有收到聚佰宝的同步数据");
				// outString(response, str);
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

	public static String toHexString(byte[] b) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < b.length; ++i) {
			buffer.append(toHexString1(b[i]));
		}
		return buffer.toString();
	}

	public static String toHexString1(byte b) {
		String s = Integer.toHexString(b & 0xFF);
		if (s.length() == 1) {
			return "0" + s;
		} else {
			return s;
		}
	}

	public static void main(String[] args) {
		Double dd = Double.parseDouble("10045.55") * 100;
		System.out.println(dd);
		Integer ii = dd.intValue();
		System.out.println(ii);
	}
}

package xdt.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.uns.inf.api.model.CallBack;
import com.uns.inf.api.model.Request;
import com.uns.inf.api.service.Service;
import net.sf.json.JSONObject;
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.daikou.model.DaiKouResponseEntity;
import xdt.quickpay.daikou.model.DaikouRequsetEntity;
import xdt.quickpay.daikou.util.HttpUtils;
import xdt.quickpay.daikou.util.SignUtil;
import xdt.quickpay.daikou.util.SignUtilEntity;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.hf.comm.SampleConstant;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.hf.entity.PayResponseEntity;
import xdt.quickpay.hf.util.PlatBase64Utils;
import xdt.quickpay.hf.util.PlatKeyGenerator;
import xdt.quickpay.hf.util.PreSignUtil;
import xdt.quickpay.nbs.common.util.MD5Util;
import xdt.quickpay.wzf.UniPaySignUtils;
import xdt.quickpay.wzf.WzfSignUtil;
import xdt.quickpay.ysb.model.YsbRequsetEntity;
import xdt.quickpay.ysb.util.YsbSignUtil;
import xdt.service.IWZFPayService;
import xdt.service.IDaiKouService;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.UtilDate;

@Controller
@RequestMapping("dk")
public class DaiKouAction extends BaseAction {

	private String accountId = null;
	private String key = null;
	/**
	 * 日志记录
	 */
	private Logger log = Logger.getLogger(DaiKouAction.class);

	@Resource
	private IDaiKouService dkService;

	/**
	 * 1.1子协议录入接口
	 * 
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "signSimpleSubContract")
	public void signSimpleSubContract(DaikouRequsetEntity temp, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String merchantId = temp.getMerchantId();
		logger.info("商户号:" + merchantId);

		Map<String, String> retMap = new HashMap<String, String>();

		// 查询上游商户号
		PmsBusinessPos busInfo = dkService.selectKey(merchantId);
		switch (busInfo.getBusinessnum()) {
		case "2120170904150304003":
		case "2120170904150304002":
		case "2120170904150304001":
			logger.info("************************银生宝----代扣录入----处理 开始");
			if (temp.getSign() == null) {
				// 生成签名
				String ysign = merSignServletForWap(temp, request, response);
				temp.setSign(ysign);
			}
			break;
		case "301101910008364":
		case "301101910008365":
		case "301101910008366":
			logger.info("************************沃支付----代扣录入----处理 开始");
			if (temp.getSign() == null) {
				// 生成签名
				String wsign = wzfAgreeSign(temp, request, response);
				temp.setSign(wsign);
			}
			break;
		default:
			break;
		}
		retMap = dkService.customerRegister(temp);
		outString(response, gson.toJson(retMap));
	}

	/**
	 * 1.2委托代扣接口(子协议号)
	 *
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "collect")
	public void collect(DaikouRequsetEntity temp, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String merchantId = temp.getMerchantId();
		logger.info("商户号:" + merchantId);
		// 查询上游商户号
		PmsBusinessPos busInfo = dkService.selectKey(merchantId);
		switch (busInfo.getBusinessnum()) {
		case "2120170904150304003":
		case "2120170904150304002":
		case "2120170904150304001":
			logger.info("************************银生宝----代扣录入----处理 开始");
			if (temp.getSign() == null) {
				// 生成签名
				String ysign = messageSignServletForWap(temp, request, response);
				temp.setSign(ysign);
			}
			break;
		case "301101910008364":
		case "301101910008365":
		case "301101910008366":
			logger.info("************************沃支付----代扣录入----处理 开始");
			if (temp.getSign() == null) {
				// 生成签名
				String wsign = wzfPaySign(temp, request, response);
				temp.setSign(wsign);
			}
			break;
		default:
			break;
		}
		// 所有的流程通过 就发起支付 上送数据
		Map<String, String> retMap = dkService.payHandle(temp);
		outString(response, gson.toJson(retMap));
	}
	/**
	 * 1.4子协议号查询接口
	 *
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "querySubContractId")
	public void querySubContractId(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, String> retMap = new HashMap<String, String>();
		// 验证签名
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());
		String merchantKey = keyinfo.getMerchantkey();
		String accountId="2120180110100540001";
		String name=params.getName();
		String cardNo=params.getCardNo();
		String idCardNo=params.getIdCardNo();
		String key="cjzfysb123abc";
		if (params.getSign() == null) {
			// 生成签名
			SignUtil signUtil = new SignUtil();
			String ysign = signUtil.sign(SignUtilEntity.ybsdaikouSigimerchant(params), merchantKey);
			params.setSign(ysign);
		}
//		Request dcRequest = new Request();
//		dcRequest.put("accountId", "2120170904150304001");
//		dcRequest.put("name", params.getName());
//		dcRequest.put("cardNo", params.getCardNo());
//		dcRequest.put("idCardNo", params.getIdCardNo());
//		dcRequest.put("key", "30eccdd59dbee2");

		SignUtil signUtil = new SignUtil();
		if (!signUtil.verify(YsbSignUtil.ybsdaikouSigimerchant(params), params.getSign(), merchantKey)) {
			log.error("签名错误!");
			retMap.put("respCode", "15");
			retMap.put("respMsg", "签名错误");
		} else {
			JSONObject jsobj1 = new JSONObject();
			jsobj1.put("accountId", accountId);
			jsobj1.put("name", name);
			jsobj1.put("cardNo", cardNo);
			jsobj1.put("idCardNo", idCardNo);
			String paramSrc = "accountId=" + accountId + "&name=" + name +"&cardNo="+cardNo+"&idCardNo="+idCardNo+ "&key=" + key;
			logger.info("签名前数据**********支付:" + paramSrc);

			String mac = MD5Util.MD5Encode(paramSrc).toUpperCase();
			logger.info("支付生成的签名：" + mac);
			jsobj1.put("mac", mac);
			String result = HttpClientUtil.post(
					"http://114.80.54.68/delegate-collect-front/subcontract/querySubContractIdJson",
					jsobj1);
			logger.info("银生宝响应的数据" + result);
//			String result = Service.sendPost(dcRequest,
//					"http://114.80.54.73:8081/unspay-external/subcontract/querySubContractId");
//			log.info("result:" + result);
			JSONObject jb = JSONObject.fromObject(result);
			String resultCode = (String) jb.get("result_code");
			String result_msg = (String) jb.get("result_msg");
			String status = (String) jb.get("status");
			String subContractId = (String) jb.get("subContractId");
			retMap.put("result_code", resultCode);
			retMap.put("result_msg", result_msg);
			retMap.put("status", status);
			retMap.put("subContractId", subContractId);
		}
		outString(response, gson.toJson(retMap));
	}
	
//	 /**
//	 * 1.5子协议延期接口
//	 *
//	 * @param request
//	 * @param dcRequest
//	 * @return
//	 * @throws Exception
//	 */
//	 @RequestMapping(value = "subConstractExtension")
//	 public void subConstractExtension(YsbRequsetEntity params,
//	 HttpServletRequest request, HttpServletResponse response)
//	 throws Exception {
//	
//	 Map<String, String> retMap = new HashMap<String, String>();
//	 Request dcRequest = new Request();
//	 dcRequest.put("accountId", "2120170904150304001");
//	 dcRequest.put("contractId", "2120170904150304001");
//	 dcRequest.put("subContractId", params.getSubContractId());
//	 dcRequest.put("startDate", params.getStartDate());
//	 dcRequest.put("endDate", params.getEndDate());
//	 dcRequest.put("key", "30eccdd59dbee2");
//	 // 验证签名
//	 ChannleMerchantConfigKey keyinfo =
//			 dkService.getChannelConfigKey(params.getMerchantId());
//	 String merchantKey = keyinfo.getMerchantkey();
//	 SignUtil signUtil = new SignUtil();
//	 if (!signUtil.verify(YsbSignUtil.ybsdaikouSigitime(params),
//	 params.getSign(), merchantKey)) {
//	 log.error("签名错误!");
//	 retMap.put("respCode", "15");
//	 retMap.put("respMsg", "签名错误");
//	 } else {
//	 String result = Service.sendPost(dcRequest,
//	 "http://114.80.54.73:8081/unspay-external/subcontract/subConstractExtension");
//	 log.info("result:" + result);
//	 JSONObject jb = JSONObject.fromObject(result);
//	 String resultCode = (String) jb.get("result_code");
//	 String result_msg = (String) jb.get("result_msg");
//	 retMap.put("result_code", resultCode);
//	 retMap.put("result_msg", result_msg);
//	 }
//	 outString(response, gson.toJson(retMap));
//	 }

	/**
	 * 银生宝代扣录入签名生成
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "hfsignForWap")
	public String merSignServletForWap(DaikouRequsetEntity params, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);

		String signmsg = "";

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			signmsg = signUtil.sign(SignUtilEntity.ybsdaifuSigiString(params), merchantKey);
			log.info("生成签名：" + signmsg);

		}
		return signmsg;

	}

	/**
	 * 沃支付代扣录入签名生成
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "wzfAgreeSign")
	public String wzfAgreeSign(DaikouRequsetEntity params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());

		String signmsg = "";
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			signmsg = signUtil.sign(SignUtilEntity.wzfdaifuSigiString(params), merchantKey);
			log.info("生成签名：" + signmsg);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}

		return signmsg;
	}

	/**
	 * 
	 * 银生宝代扣交易签名生成
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "hfsignmessage")
	public String messageSignServletForWap(DaikouRequsetEntity params, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		log.info("原始订单信息：" + params);
		String signmsg = "";
		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			signmsg = signUtil.sign(SignUtilEntity.ybsdaikouSigiString(params), merchantKey);
			log.info("生成签名：" + signmsg);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}
		return signmsg;
	}

	/**
	 * 沃支付代扣交易签名生成
	 * 
	 * @param param
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "wzfPaySign")
	public String wzfPaySign(DaikouRequsetEntity params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("原始订单信息：" + params);

		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());

		String signmsg = "";
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			signmsg = signUtil.sign(SignUtilEntity.wzfdaikouSigiString(params), merchantKey);
			log.info("生成签名：" + signmsg);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}

		return signmsg;
	}

	/**
	 * 1.3代扣订单状态查询接口
	 *
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "queryOrderStatusdaiKou")
	public void queryOrderStatusdaiKou(YsbRequsetEntity params, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, String> retMap = new HashMap<String, String>();
		String merchantId = params.getMerchantId();
		logger.info("商户号:" + merchantId);
		// 查询上游商户号
		PmsBusinessPos busInfo = dkService.selectKey(merchantId);
		SignUtil signUtil = new SignUtil();
		// 获取密钥
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());
		String merchantKey = keyinfo.getMerchantkey();
		switch (busInfo.getBusinessnum()) {
		case "2120170904150304003":
		case "2120170904150304002":
		case "2120170904150304001":
			logger.info("************************银生宝----代扣订单查询----处理 开始");
			if (params.getSign() == null) {
				// 生成签名
				String ysign = ysbsignquery(params, request, response);
				params.setSign(ysign);
			}
			String accountId = "2120180110100540001";
			String orderId = params.getOrderId();
			String key="cjzfysb123abc";
//			Request dcRequest = new Request();
//			dcRequest.put("accountId", "2120170904150304001");
//			dcRequest.put("orderId", params.getOrderId());
//			dcRequest.put("key", "30eccdd59dbee2");
			// 验证签名

			if (!signUtil.verify(SignUtilEntity.ybsdaikouSigiquery(params), params.getSign(), merchantKey)) {
				log.error("签名错误!");
				retMap.put("respCode", "15");
				retMap.put("respMsg", "签名错误");
			} else {
				JSONObject jsobj1 = new JSONObject();
				jsobj1.put("accountId", accountId);
				jsobj1.put("orderId", orderId);
				String paramSrc = "accountId=" + accountId + "&orderId=" + orderId + "&key=" + key;
				logger.info("签名前数据**********支付:" + paramSrc);

				String mac = MD5Util.MD5Encode(paramSrc).toUpperCase();
				logger.info("支付生成的签名：" + mac);
				jsobj1.put("mac", mac);
				String result = HttpClientUtil.post(
						"http://114.80.54.68/delegate-collect-front/delegateCollect/queryJson",
						jsobj1);
				logger.info("银生宝响应的数据" + result);
//				String result = Service.sendPost(dcRequest,
//						"http://114.80.54.73:8081/unspay-external/delegateCollect/queryOrderStatus");
//				log.info("result:" + result);
				JSONObject jb = JSONObject.fromObject(result);
				String resultCode = (String) jb.get("result_code");
				String result_msg = (String) jb.get("result_msg");
				String amount = (String) jb.get("amount");
				String desc = (String) jb.get("desc");
				String status = (String) jb.get("status");
				retMap.put("result_code", resultCode);
				retMap.put("result_msg", result_msg);
				retMap.put("amount", amount);
				retMap.put("desc", desc);
			}
			break;
		case "301101910008364":
		case "301101910008365":
		case "301101910008366":
			logger.info("************************沃支付----代扣订单查询接口----处理 开始");
			if (params.getSign() == null) {
				// 生成签名
				String wsign = wzfsignquery(params, request, response);
				params.setSign(wsign);
			}
			if (!signUtil.verify(SignUtilEntity.wzfdaikouSigiquery(params), params.getSign(), merchantKey)) {
				log.error("签名错误!");
				retMap.put("respCode", "15");
				retMap.put("respMsg", "签名错误");
			} else {
				String interfaceVersion = "1.0.0.0";

				String merNo = "301101910008366";

				String orderType = params.getOrderType();

				String orderDate = params.getOrderDate();

				String orderNo = params.getOrderId();

				String charSet = "UTF-8";

				String signType = "RSA_SHA256";
				Map<String, String> map = new HashMap<String, String>();
				map.put("charSet", charSet);
				map.put("interfaceVersion", interfaceVersion);
				map.put("merNo", merNo);
				map.put("orderDate", orderDate);
				map.put("orderNo", orderNo);
				map.put("orderType", orderType);
				map.put("signType", signType);
				// 商户的签名
				String sign = UniPaySignUtils.merSign(map, "RSA_SHA256");
				map.put("signMsg", sign);
				logger.info("向上游发送的签名:" + sign);
				List list = HttpUtils.URLPost(
						"http://mertest.unicompayment.com/WithhGw_XT/servlet/SingleWithhQueryServlet.htm", map);
				logger.info("响应的数据:" + list.get(0).toString());
				String[] array = list.get(0).toString().split("\\&");

				if (array[0] != null) {
					String[] acountDate = array[0].split("\\=");
					retMap.put("acountDate", acountDate[1]);
				}
				if (array[1] != null) {
					String[] amount = array[1].split("\\=");
					retMap.put("amount", amount[1]);
				}
				if (array[2] != null) {
					String[] charSet1 = array[2].split("\\=");
					retMap.put("charSet", charSet1[1]);
				}
				if (array[5] != null) {
					String[] orderDate1 = array[3].split("\\=");
					retMap.put("orderDate", orderDate1[1]);
				}
				if (array[6] != null) {
					String[] orderNo1 = array[6].split("\\=");
					retMap.put("orderId", orderNo1[1]);
				}

				if (array[7] != null) {
					String[] orderState = array[7].split("\\=");
					retMap.put("orderState", orderState[1]);
				}
				if (array[8] != null) {
					String[] orderType1 = array[8].split("\\=");
					retMap.put("orderType", orderType1[1]);
				}
				if (array[9] != null) {
					String[] payJournl = array[9].split("\\=");
					retMap.put("payJournl", payJournl[1]);
				}
				if (array[12] != null) {
					String[] queryResult = array[12].split("\\=");
					retMap.put("queryResult", queryResult[1]);
				}

				if (array[19] != null) {
					String[] tradeMode = array[19].split("\\=");
					retMap.put("tradeMode", tradeMode[1]);
				}
			}
			break;
		default:
			break;
		}
		outString(response, gson.toJson(retMap));
	}

	/**
	 * 1.3代扣子协议解约接口
	 *
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "term")
	public void termStatusdaiKou(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, String> retMap = new HashMap<String, String>();
		String merchantId = params.getMerchantId();
		logger.info("商户号:" + merchantId);
		// 查询上游商户号
		PmsBusinessPos busInfo = dkService.selectKey(merchantId);
		SignUtil signUtil = new SignUtil();
		// 获取密钥
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());
		String merchantKey = keyinfo.getMerchantkey();
		switch (busInfo.getBusinessnum()) {
		case "301101910008364":
		case "301101910008365":
		case "301101910008366":
			logger.info("************************沃支付----代扣子协议解约接口----处理 开始");
			if (params.getSign() == null) {
				// 生成签名
				String wsign = wzfsigntime(params, request, response);
				params.setSign(wsign);
			}
			if (!signUtil.verify(SignUtilEntity.wzfdaikoujySigiquery(params), params.getSign(), merchantKey)) {
				log.error("签名错误!");
				retMap.put("respCode", "15");
				retMap.put("respMsg", "签名错误");
			} else {
				String interfaceVersion = "1.0.0.0";

				String merNo = "301101910008366";

				String orderType = params.getOrderType();

				String orderDate = params.getOrderDate();

				String orderNo = params.getOrderId();

				String charSet = "UTF-8";

				String signType = "RSA_SHA256";
				Map<String, String> map = new HashMap<String, String>();
				map.put("charSet", charSet);
				map.put("interfaceVersion", interfaceVersion);
				map.put("merNo", merNo);
				map.put("orderDate", orderDate);
				map.put("orderNo", orderNo);
				map.put("orderType", orderType);
				map.put("signType", signType);
				// 商户的签名
				String sign = UniPaySignUtils.merSign(map, "RSA_SHA256");
				map.put("signMsg", sign);
				logger.info("向上游发送的签名:" + sign);
				List list = HttpUtils.URLPost(
						"http://mertest.unicompayment.com/WithhGw_XT/servlet/SingleWithhQueryServlet.htm", map);
				logger.info("响应的数据:" + list.get(0).toString());
				String[] array = list.get(0).toString().split("\\&");

				if (array[0] != null) {
					String[] acountDate = array[0].split("\\=");
					retMap.put("acountDate", acountDate[1]);
				}
				if (array[1] != null) {
					String[] amount = array[1].split("\\=");
					retMap.put("amount", amount[1]);
				}
				if (array[2] != null) {
					String[] charSet1 = array[2].split("\\=");
					retMap.put("charSet", charSet1[1]);
				}
				if (array[5] != null) {
					String[] orderDate1 = array[3].split("\\=");
					retMap.put("orderDate", orderDate1[1]);
				}
				if (array[6] != null) {
					String[] orderNo1 = array[6].split("\\=");
					retMap.put("orderId", orderNo1[1]);
				}

				if (array[7] != null) {
					String[] orderState = array[7].split("\\=");
					retMap.put("orderState", orderState[1]);
				}
				if (array[8] != null) {
					String[] orderType1 = array[8].split("\\=");
					retMap.put("orderType", orderType1[1]);
				}
				if (array[9] != null) {
					String[] payJournl = array[9].split("\\=");
					retMap.put("payJournl", payJournl[1]);
				}
				if (array[12] != null) {
					String[] queryResult = array[12].split("\\=");
					retMap.put("queryResult", queryResult[1]);
				}

				if (array[19] != null) {
					String[] tradeMode = array[19].split("\\=");
					retMap.put("tradeMode", tradeMode[1]);
				}
			}
			break;
		default:
			break;
		}
		outString(response, gson.toJson(retMap));
	}
	/**
	 * 1.3代扣退款接口
	 *
	 * @param request
	 * @param dcRequest
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "refund")
	public void refundStatusdaiKou(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, String> retMap = new HashMap<String, String>();
		String merchantId = params.getMerchantId();
		logger.info("商户号:" + merchantId);
		// 查询上游商户号
		PmsBusinessPos busInfo = dkService.selectKey(merchantId);
		SignUtil signUtil = new SignUtil();
		// 获取密钥
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());
		String merchantKey = keyinfo.getMerchantkey();
		switch (busInfo.getBusinessnum()) {
		case "301101910008364":
		case "301101910008365":
		case "301101910008366":
			logger.info("************************沃支付----代扣退款接口----处理 开始");
			if (params.getSign() == null) {
				// 生成签名
				String wsign = wzfsigntime(params, request, response);
				params.setSign(wsign);
			}
			if (!signUtil.verify(SignUtilEntity.wzfdaikoutkSigiquery(params), params.getSign(), merchantKey)) {
				log.error("签名错误!");
				retMap.put("respCode", "15");
				retMap.put("respMsg", "签名错误");
			} else {
				String interfaceVersion = "1.0.0.0";

				String merNo = "301101910008366";

				String orderType = params.getOrderType();

				String orderDate = params.getOrderDate();

				String orderNo = params.getOrderId();

				String charSet = "UTF-8";

				String signType = "RSA_SHA256";
				Map<String, String> map = new HashMap<String, String>();
				map.put("charSet", charSet);
				map.put("interfaceVersion", interfaceVersion);
				map.put("merNo", merNo);
				map.put("orderDate", orderDate);
				map.put("orderNo", orderNo);
				map.put("orderType", orderType);
				map.put("signType", signType);
				// 商户的签名
				String sign = UniPaySignUtils.merSign(map, "RSA_SHA256");
				map.put("signMsg", sign);
				logger.info("向上游发送的签名:" + sign);
				List list = HttpUtils.URLPost(
						"http://mertest.unicompayment.com/WithhGw_XT/servlet/SingleWithhQueryServlet.htm", map);
				logger.info("响应的数据:" + list.get(0).toString());
				String[] array = list.get(0).toString().split("\\&");

				if (array[0] != null) {
					String[] acountDate = array[0].split("\\=");
					retMap.put("acountDate", acountDate[1]);
				}
				if (array[1] != null) {
					String[] amount = array[1].split("\\=");
					retMap.put("amount", amount[1]);
				}
				if (array[2] != null) {
					String[] charSet1 = array[2].split("\\=");
					retMap.put("charSet", charSet1[1]);
				}
				if (array[5] != null) {
					String[] orderDate1 = array[3].split("\\=");
					retMap.put("orderDate", orderDate1[1]);
				}
				if (array[6] != null) {
					String[] orderNo1 = array[6].split("\\=");
					retMap.put("orderId", orderNo1[1]);
				}

				if (array[7] != null) {
					String[] orderState = array[7].split("\\=");
					retMap.put("orderState", orderState[1]);
				}
				if (array[8] != null) {
					String[] orderType1 = array[8].split("\\=");
					retMap.put("orderType", orderType1[1]);
				}
				if (array[9] != null) {
					String[] payJournl = array[9].split("\\=");
					retMap.put("payJournl", payJournl[1]);
				}
				if (array[12] != null) {
					String[] queryResult = array[12].split("\\=");
					retMap.put("queryResult", queryResult[1]);
				}

				if (array[19] != null) {
					String[] tradeMode = array[19].split("\\=");
					retMap.put("tradeMode", tradeMode[1]);
				}
			}
			break;
		default:
			break;
		}
		outString(response, gson.toJson(retMap));
	}

	/**
	 * 沃支付退款demo
	 *
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "wzfsignmerchant")
	public String wzfsignmerchant(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("原始订单信息：" + params);
		String signmsg = "";
		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			signmsg = signUtil.sign(SignUtilEntity.wzfdaikoutkSigiquery(params), merchantKey);
			log.info("生成签名：" + signmsg);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}
		return signmsg;
	}

	/**
	 * 沃支付解除协议demo
	 *
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "wzfsigntime")
	public String wzfsigntime(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("原始订单信息：" + params);
		String signmsg = "";
		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			signmsg = signUtil.sign(SignUtilEntity.wzfdaikoujySigiquery(params), merchantKey);
			log.info("生成签名：" + signmsg);
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}

		return signmsg;

	}

	/**
	 * 银生宝代扣查询签名接口
	 *
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "ysbsignquery")
	public String ysbsignquery(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("原始订单信息：" + params);

		String signmsg = "";
		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			signmsg = signUtil.sign(SignUtilEntity.ybsdaikouSigiquery(params), merchantKey);
			log.info("生成签名：" + signmsg);
			;
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}
		return signmsg;
	}

	/**
	 * 沃支付代扣查询签名接口
	 *
	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "wzfsignquery")
	public String wzfsignquery(YsbRequsetEntity params, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		log.info("原始订单信息：" + params);

		String signmsg = "";
		// 根据商户号查询key
		ChannleMerchantConfigKey keyinfo = dkService.getChannelConfigKey(params.getMerchantId());
		if (keyinfo != null) {

			String merchantKey = keyinfo.getMerchantkey();

			SignUtil signUtil = new SignUtil();
			// 生成签名
			signmsg = signUtil.sign(SignUtilEntity.wzfdaikouSigiquery(params), merchantKey);
			log.info("生成签名：" + signmsg);
			;
		} else {
			// 返回页面参数
			outString(response, "商户号找不到Key");
		}
		return signmsg;
	}

	// /**
	// * 下游接入 demo
	// *
	// * @param param
	// * @param request
	// * @param response
	// * @throws Exception
	// */
	// @RequestMapping(value = "ysbsignPay")
	// public void ysbsignPay(YsbRequsetEntity params, HttpServletRequest
	// request, HttpServletResponse response)
	// throws Exception {
	//
	// log.info("原始订单信息：" + params);
	//
	// // 根据商户号查询key
	// ChannleMerchantConfigKey keyinfo =
	// ysbService.getChannelConfigKey(params.getMerchantId());
	// if (keyinfo != null) {
	//
	// String merchantKey = keyinfo.getMerchantkey();
	//
	// SignUtil signUtil = new SignUtil();
	// // 生成签名
	// String signmsg = signUtil.sign(YsbSignUtil.ybsdaifuSigiPay(params),
	// merchantKey);
	// log.info("生成签名：" + signmsg);
	// params.setSign(signmsg);
	// // 返回页面参数
	// request.setAttribute("temp", params);
	// request.getRequestDispatcher("/pay/ysb/daifu/pay_submit.jsp").forward(request,
	// response);
	// } else {
	// // 返回页面参数
	// outString(response, "商户号找不到Key");
	// }
	//
	// }
	//
	/**
	 * 和上游银生宝交互 支付完成后异步返回支付结果
	 *
	 * @param request
	 *            requet对象
	 * @param response
	 *            response对象
	 * @param temp
	 *            返回的数据
	 * @throws Exception
	 */
	@RequestMapping(value = "ysbbgPayResult")
	public void bgPayResult(CallBack callBack, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setHeader("Content-type", "text/html;charset=UTF-8");
		String result_code = "";
		log.info("异步返回的数据:" + callBack);
		callBack.setAccountId("2120180110100540001");
		callBack.setKey("cjzfysb123abc");
		if (Service.validMac(callBack)) {
			log.info("验证成功");
			result_code = "200";
			// 处理这笔交易 修改订单表中的交易表
			dkService.otherInvoke(callBack);
			// 交易id
			String tranId = callBack.getOrderId();
			// 查询商户上送原始信息
			OriginalOrderInfo originalInfo = dkService.getOriginOrderInfo(tranId);
			// 给下游主动返回支付结果
			Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
			String path = originalInfo.getBgUrl() + "?" + bean2Util.bean2QueryStr(callBack);
			log.info("bgUrl 平台服务器重定向：" + path);
			String result = HttpClientUtil.post(originalInfo.getBgUrl(), bean2Util.bean2QueryStr(callBack));

			log.info("响应信息:" + result);
		} else {
			log.info("验证失败");
			result_code = "202";
		}

		log.info("向下游 发送数据成功");

	}
	// /**
	// * 和上游银生宝交互 支付完成后异步返回支付结果
	// *
	// * @param request
	// * requet对象
	// * @param response
	// * response对象
	// * @param temp
	// * 返回的数据
	// * @throws Exception
	// */
	// @RequestMapping(value = "wzfbgPayResult")
	// public void wzfbgPayResult(DaiKouResponseEntity callBack,
	// HttpServletRequest request, HttpServletResponse response)
	// throws Exception {
	//
	// response.setHeader("Content-type", "text/html;charset=UTF-8");
	// String result_code = "";
	// log.info("异步返回的数据:" + callBack);
	//// callBack.setAccountId("2120170904150304001");
	//// callBack.setKey("30eccdd59dbee2");
	//// if (Service.validMac(callBack)) {
	//// log.info("验证成功");
	//// result_code = "200";
	//// // 处理这笔交易 修改订单表中的交易表
	//// dkService.otherInvoke(callBack);
	//// // 交易id
	//// String tranId = callBack.getOrderId();
	//// // 查询商户上送原始信息
	//// OriginalOrderInfo originalInfo = dkService.getOriginOrderInfo(tranId);
	////
	//// // 给下游主动返回支付结果
	//// Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();
	//// String path = originalInfo.getBgUrl() + "?" +
	// bean2Util.bean2QueryStr(callBack);
	//// log.info("bgUrl 平台服务器重定向：" + path);
	//// String result = HttpClientUtil.post(originalInfo.getBgUrl(),
	// bean2Util.bean2QueryStr(callBack));
	////
	//// log.info("响应信息:" + result);
	//// } else {
	//// log.info("验证失败");
	//// result_code = "202";
	//// }
	//
	// log.info("向下游 发送数据成功");
	//
	// }

}

package xdt.service.impl;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;
import javax.annotation.Resource;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IPmsAddressDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsGoodsDao;
import xdt.dao.IPmsGoodsOrderDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPmsMessageDao;
import xdt.dao.IPmsOrderHelpDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.IQuickpayPreRecordDao;
import xdt.dao.IQuickpayRecordDao;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.BaseUtil;
import xdt.dto.jsds.CustomerRegister;
import xdt.dto.jsds.JsPayThread;
import xdt.dto.jsds.JsThread;
import xdt.dto.jsds.JsdsRequestDto;
import xdt.dto.jsds.JsdsResponseDto;
import xdt.dto.jsds.JsdsUtils;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.schedule.ThreadPool;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsDaifuMerchantInfoService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.JsdsQrCodeService;
import xdt.servlet.AppPospContext;
import xdt.util.Global;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.RSAUtil;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

/**
 * @Description
 * @author Shiwen .Li
 * @date 2017年3月19日 下午8:12:37
 * @version V1.3.1
 */
@Service
@Transactional
public class JsdsQrcodeServiceImpl extends BaseServiceImpl implements JsdsQrCodeService {

	private Logger logger = Logger.getLogger(JsdsQrcodeServiceImpl.class);
	private static String jsdsMerchNum = Global.getConfig("jsds.merch.num");
	private static String jsdsClientNum = Global.getConfig("jsds.client.num");
	private static String jsdsNum = Global.getConfig("jsds.num");
	private static String jsdsKey = Global.getConfig("jsds.key");
	private static String jsdsUrl = Global.getConfig("jsds.url");
	private static String jsdscallurl = Global.getConfig("jsds.callurl");

	private static String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCO7xXuSiElgVmtpqo4cAiBHw8FundlfBkhlI3CZA8ZyNPgh3TczUz3DoDKT4NEJccbDaSykfP/rZVTvdAm5Gitrce8WiE7fOGCbqsiEHhQsOGMXVi/mVK59vDshOvJO0rZg2e9i31EpoGIxv+m5vQWbUBvtJKJzaJ9EJJ5yi9YZwIDAQAB";

	private static String fact_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIRuTinaFSATJFYnjeS5LTkdZB/Q35YrFVb5J3QrTRHIOERJ6I9kC0I0Iao3epVUVw657Ib0VwOtBDUrGmma4Hbz5Ybt56W7eJEyyv/VYWFteTzJYhpUCqc+WfnXYOw9aRmSKqkzedykqblxsnrQGOsv/jjoHBHpNW5FNr161XVQIDAQAB";

	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	@Resource
	private OriginalOrderInfoDao originalDao;
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	@Resource
	IPmsGoodsDao pmsGoodsDao;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	private IPmsGoodsOrderDao pmsGoodsOrderDao;
	@Resource
	private IPmsAddressDao pmsAddressDao;
	@Resource
	private IPmsOrderHelpDao pmsOrderHelpDao;
	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	@Resource
	IPayCmmtufitDao iPayCmmtufitDao;
	@Resource
	MobaoPayHandel mobaoPayHandel;
	@Resource
	IQuickpayRecordDao quickpayRecordDao;
	@Resource
	IPospTransInfoDAO pospTransInfoDAO;
	@Resource
	IMerchantCollectMoneyService merchantCollectMoneyService;
	@Resource
	IPmsMessageService pmsMessageService;
	@Resource
	IPmsMessageDao pmsMessageDao;
	@Resource
	private IAppRateConfigDao appRateConfigDao;
	@Resource
	private IQuickpayPreRecordDao quickpayPreRecordDao;
	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;

	@Resource
	private IPmsDaifuMerchantInfoService daifuMerchantInfoService;
	/**
	 * 分发请求
	 * 
	 * @param reqData
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, String> updateHandle(JsdsRequestDto reqData) throws Exception {

		Map<String, String> result = new HashMap<String, String>();

		logger.info("************************江苏电商----二维码----处理转发 开始");

		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(reqData.getMerchantCode());
		try {

			switch (reqData.getService() == null ? reqData.getPl_service() : reqData.getService()) {
			case "cj001":
				logger.info("************************江苏电商----支付宝二维码----处理 开始");
				result = this.handleAliPay(reqData, result, busInfo);
				break;
			case "cj002":
				logger.info("************************江苏电商----微信二维码----处理 开始");
				result = this.handleWxPay(reqData, result, busInfo);
				break;
			case "cj003":
				logger.info("************************江苏电商----微信公众号----处理 开始");
				result = this.handleOfficialAccounts(reqData, result, busInfo);
				break;
			case "cj004":
				logger.info("************************江苏电商----查询----处理 开始");
				result = this.handleQuery(reqData, result, busInfo);
				break;
			case "cj005":
				logger.info("************************江苏电商----QQ钱包----处理 开始");
				result = this.QQhandleQuery(reqData, result, busInfo);
				break;
			case "cj006":
				logger.info("************************江苏电商----额度代付----处理 开始");
				result = this.handlePay(reqData, result, busInfo);
				break;
			case "cj007":
				logger.info("************************江苏电商----网关支付----处理 开始");
				result = this.gatewayhandlePay(reqData, result, busInfo);
				break;
			case "cj008":
				logger.info("************************江苏电商----京东扫码----处理 开始");
				result = this.JDhandleQuery(reqData, result, busInfo);
				break;	
			case "cj009":
				logger.info("************************江苏电商----京东H5----处理 开始");
				result = this.JDH5handleQuery(reqData, result, busInfo);
				break;	
			default:
				break;
			}

		} catch (Exception e) {
			logger.info("************************江苏电商----二维码----处理转发 失败", e);
			throw new RuntimeException("系统错误");
		}

		logger.info("************************江苏电商----二维码----处理转发 结果:{}" + result);

		return result;
	}

	public synchronized Map<String, String> handleNofity(JsdsResponseDto result) throws Exception {
		logger.info("上游返回的数据" + result);

		Map<String, String> params = new HashMap<String, String>();
		String sign1 = result.getPl_sign();
		String baseSign = URLDecoder.decode(sign1, "UTF-8");

		baseSign = baseSign.replace(" ", "+");

		byte[] a = RSAUtil.verify(
				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSUnSUG5I3Xh2ANLpC5xLe96WCVQG+A5iPBKPqRKBcF2OCdCtwNs8X40nyqYnVWqhkZwGiItT4+wFc04boL1Az01UJiZBLqmOumU0mxyyKCqGwFZakl3LWI4u2IBDuwyde3muXZDWtSDBH1k2BKzOHju3eeSicZu5D7SQ1Hol7AwIDAQAB",
				RSAUtil.base64Decode(baseSign));
		
		String Str = new String(a);

		logger.info("解析响应数据:" + Str);
		String[] array = Str.split("\\&");
		logger.info("拆分数据:" + array);
		String[] list = array[0].split("\\=");
		if (list[0].equals("orderNum")) {
			logger.info("合作商订单号:" + list[1]);

			params.put("orderNum", list[1]);

		}
		String[] list3 = array[1].split("\\=");
		if (list3[0].equals("pl_orderNum")) {
			logger.info("合作商订单号:" + list3[1]);

			params.put("pl_orderNum", list3[1]);

		}
		String[] list1 = array[2].split("\\=");
		if (list1[0].equals("pl_payState")) {
			logger.info("交易状态:" + list1[1]);
			params.put("pl_payState", list1[1]);

		}
		String[] list2 = array[3].split("\\=");
		if (list2[0].equals("pl_payMessage")) {
			logger.info("交易描述:" + list2[1]);
			params.put("pl_payMessage", list2[1]);
		}
		// 流水表transOrderId
		String transOrderId = params.get("orderNum");

		OriginalOrderInfo orig = getOriginOrderInfo(transOrderId);

		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		logger.info("流水表信息" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		logger.info("订单表信息" + pmsAppTransInfo);
		// 查询结果成功
		if ("4".equals(params.get("pl_payState").toString())) {
			/*Calendar cal1 = Calendar.getInstance();
			TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));
			java.text.SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");*/
			UpdatePmsMerchantInfo(orig);
			/*if (sdf.parse(sdf.format(cal1.getTime())).getTime() > sdf.parse("03:00:00").getTime()
					&& sdf.parse(sdf.format(cal1.getTime())).getTime() < sdf.parse("22:30:00").getTime()) {
				logger.info("D0订单号:" + transOrderId);

				
			}*/
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// log.info("修改余额");
				// 修改余额
				logger.info(pmsAppTransInfo);
				// updateMerchantBanlance(pmsAppTransInfo);
				// 更新流水表
				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("5".equals(params.get("pl_payState").toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("2".equals(params.get("pl_payState").toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("20");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
		return params;

	}

	private void updateOrderSuccess(PmsAppTransInfo pmsAppTransInfo, PospTransInfo pospTranInfo) {
		pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
		pmsAppTransInfo.setStatus("0");
		logger.info("查询原始订单表");
		pospTranInfo.setResponsecode("0000");
		logger.info("修改流水状态响应码" + pospTranInfo);
		try {
			pospTransInfoDAO.update(pospTranInfo);
			pmsAppTransInfoDao.update(pmsAppTransInfo);
		} catch (Exception e) {
			logger.error("修改订单为失败", e);
		}

	}

	private void updateOrderFail(PmsAppTransInfo pmsAppTransInfo) {
		logger.info("修改订单为失败");
		pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
		pmsAppTransInfo.setStatus("1");
		try {
			if (!UtilDate.isInDefiMinit(pmsAppTransInfo.getTradetime(), 20)) {
				logger.info("订单在20分未完成支付  修改为失败");
				pmsAppTransInfoDao.update(pmsAppTransInfo);
			}
		} catch (Exception e) {
			logger.error("修改订单为失败", e);
		}
	}

	private void updateOrderWaitPay(PmsAppTransInfo orderInfo) {
		orderInfo.setStatus("2");
		logger.info("查询原始订单表");
		try {
			pmsAppTransInfoDao.update(orderInfo);
		} catch (Exception e) {
			logger.error("修改订单为失败", e);
		}
	}

	/**
	 * 查询订单状态
	 * 
	 * @param reqData
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> handleQuery(JsdsRequestDto reqData, Map<String, String> result, PmsBusinessPos busInfo)
			throws Exception {
		logger.info("********************************查询订单状态------开始:{}" + reqData);
		if (this.verify(reqData, result)) {
			logger.info("****************************查询订单状态------签名错误");
			return result;
		} else {
			OriginalOrderInfo origin = new OriginalOrderInfo();
			origin.setMerchantOrderId(reqData.getOrderNum());
			origin.setPid(reqData.getMerchantCode());
			origin = originalDao.selectByOriginal(origin);
			PmsDaifuMerchantInfo daifuMerchantInfo = new PmsDaifuMerchantInfo();
			daifuMerchantInfo.setBatchNo(reqData.getOrderNum());
			daifuMerchantInfo.setMercId(reqData.getMerchantCode());
			daifuMerchantInfo = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(daifuMerchantInfo);
			if (origin != null) {
				result.put("respCode", "0000");

				PmsAppTransInfo orderinfo = pmsAppTransInfoDao.searchOrderInfo(origin.getOrderId());
//				if ("200".equals(orderinfo.getStatus())) {
//					logger.info("****************888当前订单没有接收到异步通知");
//					result = handleOrder(orderinfo);
//					orderinfo = pmsAppTransInfoDao.searchOrderInfo(origin.getOrderId());
//				}

				switch (orderinfo.getStatus()) {
				case "0":
					result.put("payStatus", "4");
					result.put("respMsg", "支付成功");
					result.put("pl_amount", origin.getOrderAmount());
					result.put("orderNum", origin.getMerchantOrderId());
					result.put("pl_orderNumber", UtilMethod.getOrderid("188"));
					break;
				case "1":
					result.put("payStatus", "5");
					result.put("respMsg", "支付失败");
					result.put("pl_amount", origin.getOrderAmount());
					result.put("orderNum", origin.getMerchantOrderId());
					result.put("pl_orderNumber", UtilMethod.getOrderid("188"));
					break;
				case "2":
					result.put("payStatus", "2");
					result.put("respMsg", "下单成功，等待支付");
					result.put("pl_amount", origin.getOrderAmount());
					result.put("orderNum", origin.getMerchantOrderId());
					result.put("pl_orderNumber", UtilMethod.getOrderid("188"));
					break;
				case "200":
					result.put("payStatus", "200");
					result.put("respMsg", "处理中");
					result.put("pl_amount", origin.getOrderAmount());
					result.put("orderNum", origin.getMerchantOrderId());
					result.put("pl_orderNumber", UtilMethod.getOrderid("188"));
					break;
				default:
					break;
				}

			} else if (daifuMerchantInfo != null) {
				switch (daifuMerchantInfo.getResponsecode()) {
				case "00":					
					result.put("payStatus", "4");
					result.put("respMsg", "代付成功");
					result.put("pl_amount",  Math.abs(Double.parseDouble(daifuMerchantInfo.getPayamount()))+"");
					result.put("orderNum", daifuMerchantInfo.getBatchNo());
					result.put("pl_orderNumber", UtilMethod.getOrderid("188"));
					break;
				case "01":
					result.put("payStatus", "5");
					result.put("respMsg", "代付失败");
					result.put("pl_amount", Math.abs(Double.parseDouble(daifuMerchantInfo.getPayamount()))+"");
					result.put("orderNum", daifuMerchantInfo.getBatchNo());
					result.put("pl_orderNumber", UtilMethod.getOrderid("188"));
					break;
				case "200":
					result.put("payStatus", "6");
					result.put("respMsg", "代付中");
					result.put("pl_amount", Math.abs(Double.parseDouble(daifuMerchantInfo.getPayamount()))+"");
					result.put("orderNum", daifuMerchantInfo.getBatchNo());
					result.put("pl_orderNumber", UtilMethod.getOrderid("188"));
					break;
				}

			} else {
				result.put("respCode", "0001");
				result.put("respCode", "订单不存在");
			}
			logger.info("****************************查询订单状态------处理完成");
			return result;
		}
	}

	/**
	 * 处理上游主动 通知的订单
	 * 
	 * @param orderinfo
	 * @throws Exception
	 */
	public Map<String, String> handleOrder(PmsAppTransInfo orderinfo) throws Exception {
		logger.info("*****************处理上游主动 通知的订单{}" + orderinfo);

		PospTransInfo transInfo = pospTransInfoDAO.searchByOrderId(orderinfo.getOrderid());
		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(transInfo.getMerchantcode());
		String private_key = busInfo.getKek();
		logger.info("秘钥:" + private_key);
		String merchantCode = busInfo.getBusinessnum();
		logger.info("商户号:" + merchantCode);
		String orderid = transInfo.getTransOrderId();
		logger.info("订单号:" + orderid);
		Map<String, String> params = new HashMap<String, String>();
		params.put("merchantCode", busInfo.getBusinessnum());
		params.put("orderNum", transInfo.getTransOrderId());
		String apply = HttpUtil.parseParams(params);
		logger.info("生成签名前的数据:" + apply);
		byte[] sign = RSAUtil.encrypt(private_key, apply.getBytes());
		logger.info("上送的签名:" + sign);
		Map<String, String> map = new HashMap<String, String>();
		map.put("groupId", busInfo.getDepartmentnum());
		map.put("service", "SMZF006");
		map.put("signType", "RSA");
		map.put("sign", RSAUtil.base64Encode(sign));
		map.put("datetime", UtilDate.getOrderNum());
		String jsonmap = HttpUtil.parseParams(map);
		logger.info("上送数据:" + jsonmap);
		String respJson = HttpURLConection.httpURLConnectionPOST("http://180.96.28.8:8044/TransInterface/TransRequest",
				jsonmap);
		logger.info("**********江苏电商响应报文:{}" + respJson);
		Map<String, String> result = new HashMap<String, String>();
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
				String[] list2 = array[2].split("\\=");
				if (list2[0].equals("pl_payMessage")) {
					logger.info("支付状态:" + list2[1]);
					result.put("pl_payMessage", list2[1]);
				}
				String[] list3 = array[3].split("\\=");
				if (list3[0].equals("pl_payState")) {
					logger.info("支付状态描述:" + list3[1]);
					result.put("payStatus", list3[1]);
				}
			} else {

				result.put("pl_msg", "交易失败");
				return result;
			}
		}
		return result;

	}

	private Map<String, String> handleWxPay(JsdsRequestDto reqData, Map<String, String> result, PmsBusinessPos busInfo)
			throws Exception {
		logger.info("********************************微信支付------开始:{}" + reqData);
		if (this.verify(reqData, result)) {
			logger.info("****************************微信支付------签名错误");
			return result;
		} else {
			logger.info("验证当前是否已经下单");
			OriginalOrderInfo origin = new OriginalOrderInfo();
			origin.setPid(reqData.getMerchantCode());
			origin.setMerchantOrderId(reqData.getOrderNum());
			if (this.originalDao.selectByOriginal(origin) != null) {
				result.put("respCode", "0006");
				result.put("respMsg", "下单失败");
				logger.info("**********************生成支付二维码 下单失败:{}");
			} else {
				String orderNumber = reqData.getOrderNum();
				origin.setOrderId(orderNumber);
				origin.setMerchantOrderId(reqData.getOrderNum());
				origin.setPid(reqData.getMerchantCode());
				origin.setOrderTime(reqData.getDatetime());
				NumberFormat nbf = NumberFormat.getInstance();
				DecimalFormat df =new DecimalFormat("#.##");
				nbf.setMinimumFractionDigits(2);
				Double orderAmount = Double.valueOf(reqData.getTransMoney());
				String c = df.format(orderAmount.doubleValue() / 100.0D);
				origin.setOrderAmount(c);
				origin.setPayType(reqData.getService());
				origin.setPageUrl(reqData.getNotifyUrl());
				origin.setBgUrl(reqData.getNotifyUrl());

				try {
					label104: {
						this.originalDao.insert(origin);
						logger.info("********************江苏电商-------------根据商户号查询");
						String e = reqData.getMerchantCode();
						PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
						merchantinfo.setMercId(e);
						List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
						if (merchantList.size() != 0 && !merchantList.isEmpty()) {
							merchantinfo = (PmsMerchantInfo) merchantList.get(0);
							String oAgentNo = merchantinfo.getoAgentNo();
							logger.info("***********江苏电商*************商户信息:" + merchantinfo);
							if (StringUtils.isBlank(oAgentNo)) {
								throw new RuntimeException("系统错误----------------o单编号为空");
							}

							if ("60".equals(merchantinfo.getMercSts())) {
								logger.info("***********江苏电商*************实际金额");
								String factAmount = reqData.getTransMoney();
								logger.info("***********江苏电商*************校验欧单金额限制");
								ResultInfo payCheckResult = this.iPublicTradeVerifyService.amountVerifyOagent(
										(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);
								if (!payCheckResult.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
											+ PaymentCodeEnum.weixinPay.getTypeCode());
									throw new RuntimeException("交易不支付");
								}

								logger.info("***********江苏电商*************校验欧单模块是否开启");
								ResultInfo payCheckResult1 = this.iPublicTradeVerifyService
										.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);
								if (payCheckResult1.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************校验商户模块是否开启");
									ResultInfo payCheckResult3 = this.iPublicTradeVerifyService
											.moduelVerifyMer(TradeTypeEnum.merchantCollect, e);
									if (!payCheckResult3.getErrCode().equals("0")) {
										logger.info("***********江苏电商*************商户模块限制，oAagentNo:" + oAgentNo
												+ ",payType:" + PaymentCodeEnum.weixinPay.getTypeCode());
										throw new RuntimeException("交易不支付");
									}

									logger.info("***********江苏电商*************校验商户金额限制");
									HashMap paramMap = new HashMap();
									paramMap.put("mercid", e);
									paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
									paramMap.put("oAgentNo", oAgentNo);
									logger.info("***********江苏电商*************商户 商城 业务信息 ");
									Map resultMap = this.merchantMineDao.queryBusinessInfo(paramMap);
									if (resultMap != null && resultMap.size() != 0) {
										String maxTransMoney = (String) resultMap.get("MAX_AMOUNT");
										String minTransMoney = (String) resultMap.get("MIN_AMOUNT");
										if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(maxTransMoney)) == 1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额超限");
										} else if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(minTransMoney)) == -1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额小于最小交易金额");
											logger.info("***********江苏电商*************交易金额小于最小金额");
										} else {
											logger.info("***********江苏电商*************组装订单数据");
											PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
											logger.info("写入欧单编号");
											pmsAppTransInfo.setoAgentNo(oAgentNo);
											pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
											pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());
											logger.info("***********江苏电商*************网购");
											pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
											pmsAppTransInfo.setMercid(merchantinfo.getMercId());
											pmsAppTransInfo
													.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());
											pmsAppTransInfo.setOrderid(orderNumber);
											pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay.getTypeName());
											pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay.getTypeCode());
											BigDecimal factBigDecimal = new BigDecimal(factAmount);
											BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
											pmsAppTransInfo
													.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setOrderamount(
													orderAmountBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setDrawMoneyType("1");
											logger.info("***********江苏电商*************插入订单信息");
											Integer insertAppTrans = Integer
													.valueOf(this.pmsAppTransInfoDao.insert(pmsAppTransInfo));
											if (insertAppTrans.intValue() == 1) {
												logger.info("***********江苏电商*************查询订单信息");
												pmsAppTransInfo = this.pmsAppTransInfoDao
														.searchOrderInfo(pmsAppTransInfo.getOrderid());
												String quickRateType = ((String) resultMap.get("QUICKRATETYPE"))
														.toString();
												logger.info("***********江苏电商*************获取o单第三方支付的费率");
												AppRateConfig appRate = new AppRateConfig();
												appRate.setRateType(quickRateType);
												appRate.setoAgentNo(oAgentNo);
												AppRateConfig appRateConfig = this.appRateConfigDao
														.getByRateTypeAndoAgentNo(appRate);
												if (appRateConfig == null) {
													throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
												}

												String isTop = appRateConfig.getIsTop();
												String rate = appRateConfig.getRate();
												String topPoundage = appRateConfig.getTopPoundage();
												String paymentAmount = pmsAppTransInfo.getFactamount();
												String minPoundageStr = appRateConfig.getBottomPoundage();
												Double minPoundage = Double.valueOf(0.0D);
												if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
														&& appRateConfig.getIsBottom().equals("1")) {
													if (StringUtils.isNotBlank(minPoundageStr)) {
														minPoundage = Double
																.valueOf(Double.parseDouble(minPoundageStr));
													} else {
														result.put("respCode", "0005");
														result.put("respMsg", "没有查到相关费率附加费");
														logger.info("***********江苏电商*************没有查到相关费率附加费（最低手续费）："
																+ merchantinfo.getMobilephone());
													}
												} else {
													BigDecimal payAmount = null;
													BigDecimal dfactAmount = new BigDecimal(
															pmsAppTransInfo.getFactamount());
													new BigDecimal(0);
													BigDecimal b = new BigDecimal(0);
													BigDecimal f = new BigDecimal(100);
													String rateStr = "";
													BigDecimal fee;
													if ("1".equals(isTop)) {
														rateStr = rate + "-" + topPoundage;
														logger.info("***********江苏电商*************是封顶费率类型");
														fee = (new BigDecimal(rate)).multiply(dfactAmount).divide(f)
																.setScale(2, BigDecimal.ROUND_UP).multiply(f);
														if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
															logger.info(
																	"***********江苏电商*************手续费大于封顶金额，按封顶金额处理");
															payAmount = dfactAmount
																	.subtract((new BigDecimal(topPoundage)).subtract(
																			new BigDecimal(minPoundage.doubleValue())));
															fee = (new BigDecimal(topPoundage))
																	.add(new BigDecimal(minPoundage.doubleValue()));
														} else {
															logger.info("***********江苏电商*************按当前费率处理");
															rateStr = rate;
															fee.add(new BigDecimal(minPoundage.doubleValue()));
															payAmount = dfactAmount.subtract(fee);
														}
													} else {
														logger.info("***********江苏电商*************按当前费率处理");
														rateStr = rate;
														fee = (new BigDecimal(rate)).multiply(dfactAmount)
																.add(new BigDecimal(minPoundage.doubleValue()))
																.divide(f).setScale(2, BigDecimal.ROUND_UP).multiply(f);
														double paymentAmountInt = fee.doubleValue();
														if (merchantinfo.getCounter() != null) {
															double channelInfo = Double
																	.parseDouble(merchantinfo.getCounter()) * 100;
															if (paymentAmountInt < channelInfo) {
																logger.info("手续费小于商户手续费");
																result.put("respCode", "手续费小于商户手续费");
																return result;

															} else {
																b = fee;
																payAmount = dfactAmount.subtract(fee);
															}
														}
													}
													logger.info("***********江苏电商*************设置结算金额");
													pmsAppTransInfo.setPayamount(payAmount.toString());
													pmsAppTransInfo.setRate(rateStr);
													pmsAppTransInfo.setPoundage(b.toString());
													pmsAppTransInfo.setDrawMoneyType("1");
													Integer paymentAmountInt1 = Integer
															.valueOf((int) Double.parseDouble(paymentAmount));
													logger.info("***********江苏电商*************验证支付方式是否开启");
													payCheckResult = this.iPublicTradeVerifyService.totalVerify(
															paymentAmountInt1.intValue(), TradeTypeEnum.merchantCollect,
															PaymentCodeEnum.weixinPay, oAgentNo,
															merchantinfo.getMercId());
													if (!payCheckResult.getErrCode().equals("0")) {
														logger.info("***********江苏电商*************交易不支持");
														throw new RuntimeException("交易不支持");
													}

													logger.info("***********江苏电商*************设置通道信息");
													ViewKyChannelInfo channelInfo1 = (ViewKyChannelInfo) AppPospContext.context
															.get("JS100669");
													logger.info("设置通道信息");
													if (channelInfo1 != null) {
														pmsAppTransInfo.setBusinessNum(channelInfo1.getBusinessnum());
														pmsAppTransInfo.setChannelNum(channelInfo1.getChannelNum());
													}

													logger.info("***********江苏电商*************查看当前交易是否已经生成了流水表");
													PospTransInfo pospTransInfo = null;
													logger.info(
															"***********江苏电商*************流水表是否需要更新的标记 0 insert，1：update");
													logger.info("***********江苏电商*************生成上送流水号");
													// String transOrderId =
													// this.generateTransOrderId(
													// TradeTypeEnum.merchantCollect,
													// PaymentCodeEnum.weixinPay);
													logger.info("***********江苏电商*************不存在流水，生成一个流水");
													pospTransInfo = this.generateTransFromAppTrans(pmsAppTransInfo);
													logger.info("***********江苏电商*************设置上送流水号");
													String transOrderId = reqData.getOrderNum();
													pospTransInfo.setPospsn(transOrderId);
													pospTransInfo.setTransOrderId(transOrderId);
													if (this.pospTransInfoDAO.insert(pospTransInfo) == 1) {
														pospTransInfo.setSysseqno(transOrderId);
														this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
														logger.info("修改订单信息");
														logger.info("" + pmsAppTransInfo);
														this.pmsAppTransInfoDao.update(pmsAppTransInfo);
														// 处理生成二维码
														this.twoDimensionCodeProcess(
																reqData,
																result,
																busInfo);
													} else {
														result.put("respCode", "0006");
														result.put("respMsg", "下单失败");
													}
												}
											} else {
												result.put("respCode", "0006");
												result.put("respMsg", "下单失败");
											}
										}
										break label104;
									}

									logger.info(
											"***********江苏电商*************没有查到相关费率配置：" + merchantinfo.getMobilephone());
									throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
								}

								logger.info("***********江苏电商*************欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"
										+ PaymentCodeEnum.weixinPay.getTypeCode());
								throw new RuntimeException("交易不支付");
							}

							throw new RuntimeException("系统错误----------------当前商户非正式商户");
						}

						throw new RuntimeException();
					}
				} catch (Exception var43) {
					logger.error("****************************生成二维码错误：", var43);
					throw var43;
				}
			}

			logger.info("***********江苏电商*********************微信支付------处理完成");
			return result;
		}
	}

	private Map<String, String> handleOfficialAccounts(JsdsRequestDto reqData, Map<String, String> result,
			PmsBusinessPos busInfo) throws Exception {
		logger.info("********************************公众号支付------开始:{}" + reqData);
		if (this.verify(reqData, result)) {
			logger.info("****************************公众号支付------签名错误");
			return result;
		} else {
			logger.info("验证当前是否已经下单");
			OriginalOrderInfo origin = new OriginalOrderInfo();
			origin.setPid(reqData.getMerchantCode());
			origin.setMerchantOrderId(reqData.getOrderNum());
			if (this.originalDao.selectByOriginal(origin) != null) {
				result.put("respCode", "0006");
				result.put("respMsg", "下单失败");
				logger.info("**********************生成支付二维码 下单失败:{}");
			} else {
				String orderNumber = UtilMethod.getOrderid("188");
				origin.setOrderId(orderNumber);
				origin.setMerchantOrderId(reqData.getOrderNum());
				origin.setPid(reqData.getMerchantCode());
				origin.setOrderTime(reqData.getDatetime());
				NumberFormat nbf = NumberFormat.getInstance();
				DecimalFormat df =new DecimalFormat("#.##");
				nbf.setMinimumFractionDigits(2);
				Double orderAmount = Double.valueOf(reqData.getTransMoney());
				String c = df.format(orderAmount.doubleValue() / 100.0D);
				origin.setOrderAmount(c);
				origin.setPayType(reqData.getService());
				origin.setPageUrl(reqData.getNotifyUrl());
				origin.setBgUrl(reqData.getNotifyUrl());

				try {
					label104: {
						this.originalDao.insert(origin);
						logger.info("********************江苏电商-------------根据商户号查询");
						String e = reqData.getMerchantCode();
						PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
						merchantinfo.setMercId(e);
						List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
						if (merchantList.size() != 0 && !merchantList.isEmpty()) {
							merchantinfo = (PmsMerchantInfo) merchantList.get(0);
							String oAgentNo = merchantinfo.getoAgentNo();
							logger.info("***********江苏电商*************商户信息:" + merchantinfo);
							if (StringUtils.isBlank(oAgentNo)) {
								throw new RuntimeException("系统错误----------------o单编号为空");
							}

							if ("60".equals(merchantinfo.getMercSts())) {
								logger.info("***********江苏电商*************实际金额");
								String factAmount = reqData.getTransMoney();
								logger.info("***********江苏电商*************校验欧单金额限制");
								ResultInfo payCheckResult = this.iPublicTradeVerifyService.amountVerifyOagent(
										(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);
								if (!payCheckResult.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
											+ PaymentCodeEnum.PNCodePay.getTypeCode());
									throw new RuntimeException("交易不支付");
								}

								logger.info("***********江苏电商*************校验欧单模块是否开启");
								ResultInfo payCheckResult1 = this.iPublicTradeVerifyService
										.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);
								if (payCheckResult1.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************校验商户模块是否开启");
									ResultInfo payCheckResult3 = this.iPublicTradeVerifyService
											.moduelVerifyMer(TradeTypeEnum.merchantCollect, e);
									if (!payCheckResult3.getErrCode().equals("0")) {
										logger.info("***********江苏电商*************商户模块限制，oAagentNo:" + oAgentNo
												+ ",payType:" + PaymentCodeEnum.PNCodePay.getTypeCode());
										throw new RuntimeException("交易不支付");
									}

									logger.info("***********江苏电商*************校验商户金额限制");
									HashMap paramMap = new HashMap();
									paramMap.put("mercid", e);
									paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
									paramMap.put("oAgentNo", oAgentNo);
									logger.info("***********江苏电商*************商户 商城 业务信息 ");
									Map resultMap = this.merchantMineDao.queryBusinessInfo(paramMap);
									if (resultMap != null && resultMap.size() != 0) {
										String maxTransMoney = (String) resultMap.get("MAX_AMOUNT");
										String minTransMoney = (String) resultMap.get("MIN_AMOUNT");
										if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(maxTransMoney)) == 1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额超限");
										} else if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(minTransMoney)) == -1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额小于最小交易金额");
											logger.info("***********江苏电商*************交易金额小于最小金额");
										} else {
											logger.info("***********江苏电商*************组装订单数据");
											PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
											logger.info("写入欧单编号");
											pmsAppTransInfo.setoAgentNo(oAgentNo);
											pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
											pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());
											logger.info("***********江苏电商*************网购");
											pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
											pmsAppTransInfo.setMercid(merchantinfo.getMercId());
											pmsAppTransInfo
													.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());
											pmsAppTransInfo.setOrderid(orderNumber);
											pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.PNCodePay.getTypeName());
											pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.PNCodePay.getTypeCode());
											BigDecimal factBigDecimal = new BigDecimal(factAmount);
											BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
											pmsAppTransInfo
													.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setOrderamount(
													orderAmountBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setDrawMoneyType("1");
											logger.info("***********江苏电商*************插入订单信息");
											Integer insertAppTrans = Integer
													.valueOf(this.pmsAppTransInfoDao.insert(pmsAppTransInfo));
											if (insertAppTrans.intValue() == 1) {
												logger.info("***********江苏电商*************查询订单信息");
												pmsAppTransInfo = this.pmsAppTransInfoDao
														.searchOrderInfo(pmsAppTransInfo.getOrderid());
												String quickRateType = ((String) resultMap.get("QUICKRATETYPE"))
														.toString();
												logger.info("***********江苏电商*************获取o单第三方支付的费率");
												AppRateConfig appRate = new AppRateConfig();
												appRate.setRateType(quickRateType);
												appRate.setoAgentNo(oAgentNo);
												AppRateConfig appRateConfig = this.appRateConfigDao
														.getByRateTypeAndoAgentNo(appRate);
												if (appRateConfig == null) {
													throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
												}

												String isTop = appRateConfig.getIsTop();
												String rate = appRateConfig.getRate();
												String topPoundage = appRateConfig.getTopPoundage();
												String paymentAmount = pmsAppTransInfo.getFactamount();
												String minPoundageStr = appRateConfig.getBottomPoundage();
												Double minPoundage = Double.valueOf(0.0D);
												if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
														&& appRateConfig.getIsBottom().equals("1")) {
													if (StringUtils.isNotBlank(minPoundageStr)) {
														minPoundage = Double
																.valueOf(Double.parseDouble(minPoundageStr));
													} else {
														result.put("respCode", "0005");
														result.put("respMsg", "没有查到相关费率附加费");
														logger.info("***********江苏电商*************没有查到相关费率附加费（最低手续费）："
																+ merchantinfo.getMobilephone());
													}
												} else {
													BigDecimal payAmount = null;
													BigDecimal dfactAmount = new BigDecimal(
															pmsAppTransInfo.getFactamount());
													new BigDecimal(0);
													BigDecimal b = new BigDecimal(0);
													String rateStr = "";
													BigDecimal fee;
													if ("1".equals(isTop)) {
														rateStr = rate + "-" + topPoundage;
														logger.info("***********江苏电商*************是封顶费率类型");
														fee = (new BigDecimal(rate)).multiply(dfactAmount);
														if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
															logger.info(
																	"***********江苏电商*************手续费大于封顶金额，按封顶金额处理");
															payAmount = dfactAmount
																	.subtract((new BigDecimal(topPoundage)).subtract(
																			new BigDecimal(minPoundage.doubleValue())));
															fee = (new BigDecimal(topPoundage))
																	.add(new BigDecimal(minPoundage.doubleValue()));
														} else {
															logger.info("***********江苏电商*************按当前费率处理");
															rateStr = rate;
															fee.add(new BigDecimal(minPoundage.doubleValue()));
															payAmount = dfactAmount.subtract(fee);
														}
													} else {
														logger.info("***********江苏电商*************按当前费率处理");
														rateStr = rate;
														fee = (new BigDecimal(rate)).multiply(dfactAmount)
																.add(new BigDecimal(minPoundage.doubleValue()));
														int paymentAmountInt = fee.setScale(1, 4).intValue();
														if (merchantinfo.getCounter() != null) {
															double channelInfo = Double
																	.parseDouble(merchantinfo.getCounter()) * 100;
															if (paymentAmountInt < channelInfo) {
																b = new BigDecimal(Double.toString(channelInfo));
																payAmount = dfactAmount.subtract(b);
															} else {
																b = fee;
																payAmount = dfactAmount.subtract(fee);
															}
														}
													}

													logger.info("***********江苏电商*************设置结算金额");
													pmsAppTransInfo.setPayamount(payAmount.toString());
													pmsAppTransInfo.setRate(rateStr);
													pmsAppTransInfo.setPoundage(b.toString());
													pmsAppTransInfo.setDrawMoneyType("1");
													Integer paymentAmountInt1 = Integer
															.valueOf((int) Double.parseDouble(paymentAmount));
													logger.info("***********江苏电商*************验证支付方式是否开启");
													payCheckResult = this.iPublicTradeVerifyService.totalVerify(
															paymentAmountInt1.intValue(), TradeTypeEnum.merchantCollect,
															PaymentCodeEnum.PNCodePay, oAgentNo,
															merchantinfo.getMercId());
													if (!payCheckResult.getErrCode().equals("0")) {
														logger.info("***********江苏电商*************交易不支持");
														throw new RuntimeException("交易不支持");
													}

													logger.info("***********江苏电商*************设置通道信息");
													ViewKyChannelInfo channelInfo1 = (ViewKyChannelInfo) AppPospContext.context
															.get("JS100669");
													logger.info("设置通道信息");
													if (channelInfo1 != null) {
														pmsAppTransInfo.setBusinessNum(channelInfo1.getBusinessnum());
														pmsAppTransInfo.setChannelNum(channelInfo1.getChannelNum());
													}

													logger.info("***********江苏电商*************查看当前交易是否已经生成了流水表");
													PospTransInfo pospTransInfo = null;
													logger.info(
															"***********江苏电商*************流水表是否需要更新的标记 0 insert，1：update");
													logger.info("***********江苏电商*************生成上送流水号");
													String transOrderId = this.generateTransOrderId(
															TradeTypeEnum.merchantCollect, PaymentCodeEnum.PNCodePay);
													logger.info("***********江苏电商*************不存在流水，生成一个流水");
													pospTransInfo = this.generateTransFromAppTrans(pmsAppTransInfo);
													logger.info("***********江苏电商*************设置上送流水号");
													pospTransInfo.setPospsn(transOrderId);
													if (this.pospTransInfoDAO.insert(pospTransInfo) == 1) {
														Map<String, String> params = new HashMap<String, String>();
														params.put("merchantCode", busInfo.getBusinessnum());
														params.put("terminalCode", busInfo.getPosnum());
														params.put("orderNum", transOrderId);
														params.put("transMoney", reqData.getTransMoney());
														if ("105962".equals(busInfo.getDepartmentnum())) {
															params.put("notifyUrl",
																	BaseUtil.url+"/test/qrcode/JsdsgPayResult.action");
														} else if ("107382".equals(busInfo.getDepartmentnum())) {
															params.put("notifyUrl",
																	BaseUtil.url+"/test/qrcode/JsbgPayResult.action");
														}

														params.put("merchantName", reqData.getMerchantName() == null
																? "天津畅捷支付" : reqData.getMerchantName());
														params.put("merchantNum", UtilDate.getOrderNum());
														params.put("terminalNum",
																UUID.randomUUID().toString().replace("-", ""));
														String apply = HttpUtil.parseParams(params);
														logger.info("生成签名前的数据:" + apply);
														byte[] sign = RSAUtil.encrypt(busInfo.getKek(),
																apply.getBytes());
														logger.info("上送的签名:" + sign);
														Map<String, String> map = new HashMap<String, String>();
														map.put("groupId", busInfo.getDepartmentnum());
														if ("1".equals(reqData.getType())) {
															map.put("service", "SMZF014");
														} else if ("0".equals(reqData.getType())) {
															map.put("service", "SMZF015");
														}

														map.put("signType", "RSA");
														map.put("sign", RSAUtil.base64Encode(sign));
														map.put("datetime", UtilDate.getOrderNum());
														String jsonmap = HttpUtil.parseParams(map);
														logger.info("上送数据:" + jsonmap);
														String respJson = HttpURLConection.httpURLConnectionPOST(
																"http://180.96.28.8:8044/TransInterface/TransRequest",
																jsonmap);
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

																byte[] a = RSAUtil.verify(busInfo.getKek(),
																		RSAUtil.base64Decode(baseSign));

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
																	 result.put("pl_orderNum",
																	 list1[1]);

																}
																// String[]
																// list2 =
																// array[2].split("\\=");
																if (array[2] != null) {
																	logger.info("公众号URL:"
																			+ array[2].replaceAll("pl_url=", ""));
																	result.put("pl_url",
																			array[2].replaceAll("pl_url=", ""));

																}

															} else {

																result.put("pl_msg", "交易失败");
															}
														}
														if ("0000".equals(result.get("respCode"))) {
															pospTransInfo.setSysseqno(
																	((String) result.get("orderNum")).toString());
														}
														this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
														logger.info("修改订单信息");
														logger.info("" + pmsAppTransInfo);
														this.pmsAppTransInfoDao.update(pmsAppTransInfo);
													} else {
														result.put("respCode", "0006");
														result.put("respMsg", "下单失败");
													}
												}
											} else {
												result.put("respCode", "0006");
												result.put("respMsg", "下单失败");
											}
										}
										break label104;
									}

									logger.info(
											"***********江苏电商*************没有查到相关费率配置：" + merchantinfo.getMobilephone());
									throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
								}

								logger.info("***********江苏电商*************欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"
										+ PaymentCodeEnum.PNCodePay.getTypeCode());
								throw new RuntimeException("交易不支付");
							}

							throw new RuntimeException("系统错误----------------当前商户非正式商户");
						}

						throw new RuntimeException();
					}
				} catch (Exception var43) {
					logger.error("****************************生成二维码错误：", var43);
					throw var43;
				}
			}

			logger.info("***********江苏电商*********************微信支付------处理完成");
			return result;
		}
	}

	public Map<String, String> handleAliPay(JsdsRequestDto reqData, Map<String, String> result, PmsBusinessPos busInfo)
			throws Exception {

		logger.info("********************************支付宝------开始:{}" + reqData);

//		if (this.verify(reqData, result)) {//
//			logger.info("****************************支付宝------签名错误");
//			return result;
//		} else {
			logger.info("验证当前是否已经下单");
			OriginalOrderInfo origin = new OriginalOrderInfo();
			origin.setPid(reqData.getMerchantCode());
			origin.setMerchantOrderId(reqData.getOrderNum());
			if (this.originalDao.selectByOriginal(origin) != null) {
				result.put("respCode", "0006");
				result.put("respMsg", "下单失败");
				logger.info("**********************生成支付二维码 下单失败:{}");
			} else {
				String orderNumber = reqData.getOrderNum();
				origin.setOrderId(orderNumber);
				origin.setMerchantOrderId(reqData.getOrderNum());
				origin.setPid(reqData.getMerchantCode());
				origin.setOrderTime(reqData.getDatetime());
				NumberFormat nbf = NumberFormat.getInstance();
				DecimalFormat df =new DecimalFormat("#.##");
				nbf.setMinimumFractionDigits(2);
				Double orderAmount = Double.valueOf(reqData.getTransMoney());
				String c = df.format(orderAmount/100);
				origin.setOrderAmount(c);
				origin.setPayType(reqData.getService());
				origin.setPageUrl(reqData.getNotifyUrl());
				origin.setBgUrl(reqData.getNotifyUrl());

				try {
					label104: {
						this.originalDao.insert(origin);
						logger.info("********************江苏电商-------------根据商户号查询");
						String e = reqData.getMerchantCode();
						PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
						merchantinfo.setMercId(e);
						List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
						if (merchantList.size() != 0 && !merchantList.isEmpty()) {
							merchantinfo = (PmsMerchantInfo) merchantList.get(0);
							String oAgentNo = merchantinfo.getoAgentNo();
							logger.info("***********江苏电商*************商户信息:" + merchantinfo);
							if (StringUtils.isBlank(oAgentNo)) {
								throw new RuntimeException("系统错误----------------o单编号为空");
							}

							if ("60".equals(merchantinfo.getMercSts())) {
								logger.info("***********江苏电商*************实际金额");
								String factAmount = reqData.getTransMoney();
								logger.info("***********江苏电商*************校验欧单金额限制");
								ResultInfo payCheckResult = this.iPublicTradeVerifyService.amountVerifyOagent(
										(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);
								if (!payCheckResult.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
											+ PaymentCodeEnum.zhifubaoPay.getTypeCode());
									throw new RuntimeException("交易不支付");
								}

								logger.info("***********江苏电商*************校验欧单模块是否开启");
								ResultInfo payCheckResult1 = this.iPublicTradeVerifyService
										.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);
								if (payCheckResult1.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************校验商户模块是否开启");
									ResultInfo payCheckResult3 = this.iPublicTradeVerifyService
											.moduelVerifyMer(TradeTypeEnum.merchantCollect, e);
									if (!payCheckResult3.getErrCode().equals("0")) {
										logger.info("***********江苏电商*************商户模块限制，oAagentNo:" + oAgentNo
												+ ",payType:" + PaymentCodeEnum.zhifubaoPay.getTypeCode());
										throw new RuntimeException("交易不支付");
									}

									logger.info("***********江苏电商*************校验商户金额限制");
									HashMap paramMap = new HashMap();
									paramMap.put("mercid", e);
									paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
									paramMap.put("oAgentNo", oAgentNo);
									logger.info("***********江苏电商*************商户 商城 业务信息 ");
									Map resultMap = this.merchantMineDao.queryBusinessInfo(paramMap);
									if (resultMap != null && resultMap.size() != 0) {
										String maxTransMoney = (String) resultMap.get("MAX_AMOUNT");
										String minTransMoney = (String) resultMap.get("MIN_AMOUNT");
										if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(maxTransMoney)) == 1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额超限");
										} else if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(minTransMoney)) == -1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额小于最小交易金额");
											logger.info("***********江苏电商*************交易金额小于最小金额");
										} else {
											logger.info("***********江苏电商*************组装订单数据");
											PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
											logger.info("写入欧单编号");
											pmsAppTransInfo.setoAgentNo(oAgentNo);
											pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
											pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());
											logger.info("***********江苏电商*************网购");
											pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
											pmsAppTransInfo.setMercid(merchantinfo.getMercId());
											pmsAppTransInfo
													.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());
											pmsAppTransInfo.setOrderid(orderNumber);
											pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
											pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
											BigDecimal factBigDecimal = new BigDecimal(factAmount);
											BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
											pmsAppTransInfo
													.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setOrderamount(
													orderAmountBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setDrawMoneyType("1");
											logger.info("***********江苏电商*************插入订单信息");
											Integer insertAppTrans = Integer
													.valueOf(this.pmsAppTransInfoDao.insert(pmsAppTransInfo));
											if (insertAppTrans.intValue() == 1) {
												logger.info("***********江苏电商*************查询订单信息");
												pmsAppTransInfo = this.pmsAppTransInfoDao
														.searchOrderInfo(pmsAppTransInfo.getOrderid());
												String quickRateType = ((String) resultMap.get("QUICKRATETYPE"))
														.toString();
												logger.info("***********江苏电商*************获取o单第三方支付的费率");
												AppRateConfig appRate = new AppRateConfig();
												appRate.setRateType(quickRateType);
												appRate.setoAgentNo(oAgentNo);
												AppRateConfig appRateConfig = this.appRateConfigDao
														.getByRateTypeAndoAgentNo(appRate);
												if (appRateConfig == null) {
													throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
												}

												String isTop = appRateConfig.getIsTop();
												String rate = appRateConfig.getRate();
												String topPoundage = appRateConfig.getTopPoundage();
												String paymentAmount = pmsAppTransInfo.getFactamount();
												String minPoundageStr = appRateConfig.getBottomPoundage();
												Double minPoundage = Double.valueOf(0.0D);
												if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
														&& appRateConfig.getIsBottom().equals("1")) {
													if (StringUtils.isNotBlank(minPoundageStr)) {
														minPoundage = Double
																.valueOf(Double.parseDouble(minPoundageStr));
													} else {
														result.put("respCode", "0005");
														result.put("respMsg", "没有查到相关费率附加费");
														logger.info("***********江苏电商*************没有查到相关费率附加费（最低手续费）："
																+ merchantinfo.getMobilephone());
													}
												} else {
													BigDecimal payAmount = null;
													BigDecimal dfactAmount = new BigDecimal(
															pmsAppTransInfo.getFactamount());
													new BigDecimal(0);
													BigDecimal b = new BigDecimal(0);
													BigDecimal f = new BigDecimal(100);
													String rateStr = "";
													BigDecimal fee;
													if ("1".equals(isTop)) {
														rateStr = rate + "-" + topPoundage;
														logger.info("***********江苏电商*************是封顶费率类型");
														fee = (new BigDecimal(rate)).multiply(dfactAmount).divide(f)
																.setScale(2, BigDecimal.ROUND_UP).multiply(f);
														if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
															logger.info(
																	"***********江苏电商*************手续费大于封顶金额，按封顶金额处理");
															payAmount = dfactAmount
																	.subtract((new BigDecimal(topPoundage)).subtract(
																			new BigDecimal(minPoundage.doubleValue())));
															fee = (new BigDecimal(topPoundage))
																	.add(new BigDecimal(minPoundage.doubleValue()));
														} else {
															logger.info("***********江苏电商*************按当前费率处理");
															rateStr = rate;
															fee.add(new BigDecimal(minPoundage.doubleValue()));
															payAmount = dfactAmount.subtract(fee);
														}
													} else {
														logger.info("***********江苏电商*************按当前费率处理");
														rateStr = rate;
														fee = (new BigDecimal(rate)).multiply(dfactAmount)
																.add(new BigDecimal(minPoundage.doubleValue()))
																.divide(f).setScale(2, BigDecimal.ROUND_UP).multiply(f);
														double paymentAmountInt = fee.doubleValue();
														if (merchantinfo.getCounter() != null) {
															double channelInfo = Double
																	.parseDouble(merchantinfo.getCounter()) * 100;
															if (paymentAmountInt < channelInfo) {
																logger.info("手续费小于商户手续费");
																result.put("respCode", "手续费小于商户手续费");
																return result;

															} else {
																b = fee;
																payAmount = dfactAmount.subtract(fee);
															}
														}
													}

													logger.info("***********江苏电商*************设置结算金额");
													pmsAppTransInfo.setPayamount(payAmount.toString());
													pmsAppTransInfo.setRate(rateStr);
													pmsAppTransInfo.setPoundage(b.toString());
													pmsAppTransInfo.setDrawMoneyType("1");
													Integer paymentAmountInt1 = Integer
															.valueOf((int) Double.parseDouble(paymentAmount));
													logger.info("***********江苏电商*************验证支付方式是否开启");
													payCheckResult = this.iPublicTradeVerifyService.totalVerify(
															paymentAmountInt1.intValue(), TradeTypeEnum.merchantCollect,
															PaymentCodeEnum.zhifubaoPay, oAgentNo,
															merchantinfo.getMercId());
													if (!payCheckResult.getErrCode().equals("0")) {
														logger.info("***********江苏电商*************交易不支持");
														throw new RuntimeException("交易不支持");
													}

													logger.info("***********江苏电商*************设置通道信息");
													ViewKyChannelInfo channelInfo1 = (ViewKyChannelInfo) AppPospContext.context
															.get("JS100669");
													logger.info("设置通道信息");
													if (channelInfo1 != null) {
														pmsAppTransInfo.setBusinessNum(channelInfo1.getBusinessnum());
														pmsAppTransInfo.setChannelNum(channelInfo1.getChannelNum());
													}

													logger.info("***********江苏电商*************查看当前交易是否已经生成了流水表");
													PospTransInfo pospTransInfo = new PospTransInfo();
													logger.info(
															"***********江苏电商*************流水表是否需要更新的标记 0 insert，1：update");
													logger.info("***********江苏电商*************生成上送流水号");

													logger.info("***********江苏电商*************不存在流水，生成一个流水");
													pospTransInfo = this.generateTransFromAppTrans(pmsAppTransInfo);
													logger.info("***********江苏电商*************设置上送流水号");
													String transOrderId = reqData.getOrderNum();
													pospTransInfo.setPospsn(transOrderId);
													pospTransInfo.setTransOrderId(transOrderId);
													if (this.pospTransInfoDAO.insert(pospTransInfo) == 1) {					
														pospTransInfo.setSysseqno(transOrderId);
														this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
														logger.info("修改订单信息");
														logger.info("" + pmsAppTransInfo);
														this.pmsAppTransInfoDao.update(pmsAppTransInfo);
														// 处理生成二维码
														this.twoDimensionCodeProcess(
																reqData,
																result,
																busInfo);
													} else {
														result.put("respCode", "0006");
														result.put("respMsg", "下单失败");
														return result;
													}
												}
											} else {
												result.put("respCode", "0006");
												result.put("respMsg", "下单失败");
												return result;
											}
										}
										break label104;
									}

									logger.info(
											"***********江苏电商*************没有查到相关费率配置：" + merchantinfo.getMobilephone());
									throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
								}

								logger.info("***********江苏电商*************欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"
										+ PaymentCodeEnum.zhifubaoPay.getTypeCode());
								throw new RuntimeException("交易不支付");
							}

							throw new RuntimeException("系统错误----------------当前商户非正式商户");
						}

						throw new RuntimeException();
					}
				} catch (Exception var43) {
					logger.error("****************************生成二维码错误：", var43);
					throw var43;
				}
			}

			logger.info("***********江苏电商*********************微信支付------处理完成");
			return result;
		//}
	}

	/**
	 * 额度代付
	 * 
	 * @param result
	 * @param reqData
	 */
	public synchronized Map<String, String> handlePay(JsdsRequestDto reqData, Map<String, String> result,
			PmsBusinessPos busInfo) throws Exception {

		logger.info("********************************代付------开始:{}" + reqData);
		BigDecimal b1;// 下游上传的金额
		BigDecimal b2;// 系统代付余额
		BigDecimal b3;// 单笔交易手续费
		BigDecimal min;// 代付最小金额
		BigDecimal max;// 代付最大金额
		Double surplus;// 代付剩余金额
		/*if (this.verify(reqData, result)) {
			logger.info("****************************代付------签名错误");
			return result;
		} else {*/
			logger.info("当前代付订单是否存在");

			PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
			model.setMercId(reqData.getMerchantCode());
			model.setBatchNo(reqData.getOrderNum());
			if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
				result.put("respCode", "0006");
				result.put("respMsg", "下单失败,订单存在");
				logger.info("**********************代付 下单失败:{}");
				logger.info("订单存在");
				return result;
			}
			try {
				label104: {

					logger.info("********************江苏电商-------------根据商户号查询");
					String e = reqData.getMerchantCode();
					PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
					PmsMerchantInfo merchantinfo1 = new PmsMerchantInfo();
					merchantinfo.setMercId(e);
					merchantinfo1.setMercId(e);
					List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
					if (merchantList.size() != 0 && !merchantList.isEmpty()) {
						merchantinfo = (PmsMerchantInfo) merchantList.get(0);
						String oAgentNo = merchantinfo.getoAgentNo();
						logger.info("***********江苏电商*************商户信息:" + merchantinfo);
						if (StringUtils.isBlank(oAgentNo)) {
							throw new RuntimeException("系统错误----------------o单编号为空");
						}

						if ("60".equals(merchantinfo.getMercSts())) {
							logger.info("***********江苏电商*************实际金额");
							// 分
							String factAmount = reqData.getTransMoney();
							logger.info("***********江苏电商*************校验欧单金额限制");
							b1 = new BigDecimal(factAmount);
							logger.info("下游上传代付金额:" + b1.doubleValue());
							// 判断交易类型
							if (reqData.getType().equals("0")) {
								b2 = new BigDecimal(merchantinfo.getPosition());
								logger.info("系统剩余可用额度D0:" + b2.doubleValue());
								b3 = new BigDecimal(merchantinfo.getPoundage());
								logger.info("系统商户代付手续费:" + b3.doubleValue());
								min = new BigDecimal(merchantinfo.getMinDaiFu());
								logger.info("系统代付最小金额:" + min.doubleValue());
								max = new BigDecimal(merchantinfo.getMaxDaiFu());
								logger.info("系统代付最大金额:" + max.doubleValue());
								if (b1.doubleValue() + b3.doubleValue()*100 > b2.doubleValue()) {
									result.put("respCode", "0006");
									result.put("respMsg", "下单失败,代付金额高于剩余额度");
									logger.info("**********************代付金额高于剩余额度");
									int i = add(reqData, merchantinfo, result);
									if (i == 1) {
										logger.info("添加失败订单成功");
									}
									return result;
								}
								if (b1.doubleValue() < min.doubleValue() * 100) {
									result.put("respCode", "0006");
									result.put("respMsg", "下单失败,代付金额小于代付最小金额");
									logger.info("**********************代付金额小于代付最小金额");
									int i = add(reqData, merchantinfo, result);
									if (i == 1) {
										logger.info("添加失败订单成功");
									}
									return result;
								}
								if (b1.doubleValue() > max.doubleValue() * 100) {
									result.put("respCode", "0006");
									result.put("respMsg", "下单失败,代付金额大于代付D0最大金额");
									logger.info("**********************代付金额大于代付D0最大金额");
									int i = add(reqData, merchantinfo, result);
									if (i == 1) {
										logger.info("添加失败订单成功");
									}
									return result;
								}
								surplus = b2.subtract(b1).doubleValue() - b3.doubleValue() * 100;
								merchantinfo.setPosition(surplus.toString());
								int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
								if (num == 1) {
									logger.info("扣款成功！！");
								}
								// 代付插入数据
								model.setCount("1");
								model.setIdentity(reqData.getOrderNum());
								model.setBatchNo(reqData.getOrderNum());
								model.setAmount(Double.parseDouble(factAmount) / 100 + "");
								model.setCardno(reqData.getAccountName());
								model.setRealname(reqData.getBankName());
								model.setPayamount("-" + Double.parseDouble(factAmount) / 100);
								model.setPmsbankno(reqData.getBankLinked());
								model.setTransactionType("代付");
								model.setPosition(String.valueOf(surplus));
								model.setRemarks("D0");
								model.setRecordDescription("批次号:" + reqData.getOrderNum());
								model.setResponsecode("200");
								model.setOagentno("100333");
								model.setPayCounter(b3.doubleValue() + "");
								int iii = pmsDaifuMerchantInfoDao.insert(model);
								if (iii == 1) {
									logger.info("代付订单添加成功");
									JsdsRequestDto req = new JsdsRequestDto();
									logger.info("1");
									req.setMerchantCode(busInfo.getBusinessnum());// 平台商户编号
									logger.info("2");
									req.setTerminalCode(busInfo.getPosnum());// 平台商户终端编号
									logger.info("3");
									req.setTransDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));// 交易日期（YYYYMMDD）
									logger.info("4");
									req.setTransTime(new SimpleDateFormat("HHmmss").format(new Date()));// 交易时间（HH24mmss）
									logger.info("5");
									req.setOrderNum(reqData.getOrderNum());// 合作商订单号，全局唯一
									logger.info("6");
									req.setAccountName(reqData.getAccountName());// 收款人账户名
									logger.info("7");
									req.setBankCard(reqData.getBankCard());// 收款人账户号
									logger.info("8");
									req.setBankName(reqData.getBankName());// 收款人账户开户行名称
									logger.info("9");
									req.setBankLinked(reqData.getBankLinked());// 收款人账户开户行联行号
									logger.info("10");
									req.setTransMoney(reqData.getTransMoney());// 交易金额

									logger.info("11");
									String str = entityToString(req);
									logger.info("str:" + str);
									logger.info("12");
									// String path =new
									// File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky/public.key";
									// String path
									// ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCO7xXuSiElgVmtpqo4cAiBHw8FundlfBkhlI3CZA8ZyNPgh3TczUz3DoDKT4NEJccbDaSykfP/rZVTvdAm5Gitrce8WiE7fOGCbqsiEHhQsOGMXVi/mVK59vDshOvJO0rZg2e9i31EpoGIxv+m5vQWbUBvtJKJzaJ9EJJ5yi9YZwIDAQAB";
									logger.info("13");
									logger.info("14");
									byte[] a = RSAUtil.encrypt(busInfo.getKek(), str.getBytes());
									logger.info("15");
									String sign = RSAUtil.base64Encode(a);
									logger.info("加密结果:" + RSAUtil.base64Encode(a));
									JsdsRequestDto requestDto = new JsdsRequestDto();
									requestDto.setGroupId(busInfo.getDepartmentnum());
									requestDto.setService("SMZF008");
									requestDto.setSignType("RSA");
									requestDto.setSign(sign);
									requestDto.setDatetime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
									logger.info("上送参数：" + JSON.toJSON(requestDto));
									// 返回的数据
									Map<String, String> results = this.sends(requestDto);
									logger.info("上游返回的数据:" + JSON.toJSON(results));
									logger.info("pl_code:" + results.get("pl_code"));
									if (results.get("msg").equals("1")) {
										logger.info("1未知结果进来了！！！");
										UpdateDaifu(reqData.getOrderNum(), "200");
										logger.info("2未知结果进来了！！！");
										this.updateSelect(reqData,result,merchantinfo);
										logger.info("3未知结果结束了！！！");
									} else {
										if ("0000".equals(results.get("pl_code"))) {
											logger.info("1进来了！");
											// 解析签名
											String baseSign = URLDecoder.decode(results.get("pl_sign"), "UTF-8");
											baseSign = baseSign.replace(" ", "+");
											byte[] b = RSAUtil.verify(busInfo.getKek(), RSAUtil.base64Decode(baseSign));
											String signs = new String(b);
											logger.info("signs:" + signs);
											String[] signs1 = signs.split("&");
											logger.info("signs1" + signs1);
											String ss[] = signs1[0].split("=");
											if ("pl_transState=1".equals(signs1[3])) {
												UpdateDaifu(reqData.getOrderNum(), "00");
												result.put("respCode", "0000");
												result.put("respMsg", "请求成功");
												result.put("pl_service", "cj006");
												result.put("orderNum", ss[1]);
											} else if ("pl_transState=3".equals(signs1[3])) {
												UpdateDaifu(reqData.getOrderNum(), "200");
												result.put("respCode", "0000");
												result.put("respMsg", "请求成功");
												result.put("pl_service", "cj006");
												result.put("orderNum", ss[1]);
												
											} else {
												logger.info("又失败了3");
												UpdateDaifu(reqData.getOrderNum(), "01");
												Map<String, String> map = new HashMap<>();
												map.put("mercId", reqData.getMerchantCode());
												map.put("payMoney", b1.doubleValue() + "");
												int nus = pmsMerchantInfoDao.updataPayT1(map);
												if (nus == 1) {
													logger.info("加款成功！！");
													// 代付钱的总金额
													Double sa = surplus + Double.parseDouble(factAmount);
													model.setCount("1");
													model.setIdentity(reqData.getOrderNum());
													model.setBatchNo(reqData.getOrderNum() + "/A");
													model.setAmount(Double.parseDouble(factAmount) / 100 + "");
													model.setCardno(reqData.getAccountName());
													model.setRealname(reqData.getBankName());
													model.setPayamount(Double.parseDouble(factAmount) / 100 + "");
													model.setPmsbankno(reqData.getBankLinked());
													model.setTransactionType("代付补款");
													model.setPosition(sa.toString());
													model.setRemarks("D0");
													model.setRecordDescription("批次号:" + reqData.getOrderNum());
													model.setResponsecode("00");
													model.setOagentno("100333");
													model.setPayCounter("");
													int ii = pmsDaifuMerchantInfoDao.insert(model);
													if (ii == 1) {
														logger.info("添加代付补款记录成功！");
													}
												}
												result.put("respCode", "0006");
												result.put("respMsg", "下单失败");
											}

										} else {
											logger.info("2进来了！");
											UpdateDaifu(reqData.getOrderNum(), "01");
											Map<String, String> map = new HashMap<>();
											map.put("mercId", reqData.getMerchantCode());
											map.put("payMoney", b1.doubleValue() + "");
											int nus = pmsMerchantInfoDao.updataPayT1(map);
											if (nus == 1) {
												logger.info("加款成功！！");
												// 代付钱的总金额
												Double sa = surplus + Double.parseDouble(factAmount);
												model.setCount("1");
												model.setIdentity(reqData.getOrderNum());
												model.setBatchNo(reqData.getOrderNum() + "/A");
												model.setAmount(Double.parseDouble(factAmount) / 100 + "");
												model.setCardno(reqData.getAccountName());
												model.setRealname(reqData.getBankName());
												model.setPayamount(Double.parseDouble(factAmount) / 100 + "");
												model.setPmsbankno(reqData.getBankLinked());
												model.setTransactionType("代付补款");
												model.setPosition(sa.toString());
												model.setRemarks("D0");
												model.setRecordDescription("批次号:" + reqData.getOrderNum());
												model.setResponsecode("00");
												model.setOagentno("100333");
												model.setPayCounter("");
												int ii = pmsDaifuMerchantInfoDao.insert(model);
												if (ii == 1) {
													logger.info("添加代付补款记录成功！");

												}
											}
											result.put("respCode", "0006");
											result.put("respMsg", "下单失败");
										}

									}
								}

							} else if (reqData.getType().equals("1")) {
								b2 = new BigDecimal(merchantinfo.getPositionT1());
								logger.info("系统剩余可用额度T1:" + b2.doubleValue());
								b3 = new BigDecimal(merchantinfo.getPoundage());
								logger.info("系统商户代付手续费:" + b3.doubleValue());
								min = new BigDecimal(merchantinfo.getMinDaiFu());
								logger.info("系统代付最小金额:" + min.doubleValue());
								max = new BigDecimal(merchantinfo.getMaxDaiFu());
								logger.info("系统代付最大金额:" + max.doubleValue());
								if (b1.doubleValue() + b3.doubleValue()*100 > b2.doubleValue()) {
									result.put("respCode", "0006");
									result.put("respMsg", "下单失败,代付金额高于剩余额度");
									logger.info("**********************代付金额高于剩余额度");
									int i = add(reqData, merchantinfo, result);
									if (i == 1) {
										logger.info("添加失败订单成功");
									}
									return result;
								}
								if (b1.doubleValue() < min.doubleValue() * 100) {
									result.put("respCode", "0006");
									result.put("respMsg", "下单失败,代付金额小于代付最小金额");
									logger.info("**********************代付金额小于代付最小金额");
									int i = add(reqData, merchantinfo, result);
									if (i == 1) {
										logger.info("添加失败订单成功");
									}
									return result;
								}
								if (b1.doubleValue() > max.doubleValue() * 100) {
									result.put("respCode", "0006");
									result.put("respMsg", "下单失败,代付金额大于代付最大金额");
									logger.info("**********************代付金额大于代付最大金额");
									int i = add(reqData, merchantinfo, result);
									if (i == 1) {
										logger.info("添加失败订单成功");
									}
									return result;
								}
								surplus =b2.subtract(b1).doubleValue() - b3.doubleValue() * 100;
								merchantinfo.setPositionT1(surplus.toString());
								int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
								if (num == 1) {
									logger.info("扣款成功！！");
								}
								// 代付插入数据
								model.setCount("1");
								model.setIdentity(reqData.getOrderNum());
								model.setBatchNo(reqData.getOrderNum());
								model.setAmount(Double.parseDouble(factAmount) / 100 + "");
								model.setCardno(reqData.getAccountName());
								model.setRealname(reqData.getBankName());
								model.setPayamount("-" + Double.parseDouble(factAmount) / 100);
								model.setPmsbankno(reqData.getBankLinked());
								model.setTransactionType("代付");
								model.setPosition(String.valueOf(surplus));
								model.setRemarks("T1");
								model.setRecordDescription("批次号:" + reqData.getOrderNum());
								model.setResponsecode("200");
								model.setOagentno("100333");
								model.setPayCounter(b3.doubleValue() + "");
								int iii = pmsDaifuMerchantInfoDao.insert(model);
								if (iii == 1) {
									logger.info("代付订单添加成功");
									JsdsRequestDto req = new JsdsRequestDto();
									logger.info("1");
									req.setMerchantCode(busInfo.getBusinessnum());// 平台商户编号
									logger.info("2");
									req.setTerminalCode(busInfo.getPosnum());// 平台商户终端编号
									logger.info("3");
									req.setTransDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));// 交易日期（YYYYMMDD）
									logger.info("4");
									req.setTransTime(new SimpleDateFormat("HHmmss").format(new Date()));// 交易时间（HH24mmss）
									logger.info("5");
									req.setOrderNum(reqData.getOrderNum());// 合作商订单号，全局唯一
									logger.info("6");
									req.setAccountName(reqData.getAccountName());// 收款人账户名
									logger.info("7");
									req.setBankCard(reqData.getBankCard());// 收款人账户号
									logger.info("8");
									req.setBankName(reqData.getBankName());// 收款人账户开户行名称
									logger.info("9");
									req.setBankLinked(reqData.getBankLinked());// 收款人账户开户行联行号
									logger.info("10");
									req.setTransMoney(reqData.getTransMoney());// 交易金额

									logger.info("11");
									String str = entityToString(req);
									logger.info("str:" + str);
									logger.info("12");
									// String path =new
									// File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky/public.key";
									// String path
									// ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCO7xXuSiElgVmtpqo4cAiBHw8FundlfBkhlI3CZA8ZyNPgh3TczUz3DoDKT4NEJccbDaSykfP/rZVTvdAm5Gitrce8WiE7fOGCbqsiEHhQsOGMXVi/mVK59vDshOvJO0rZg2e9i31EpoGIxv+m5vQWbUBvtJKJzaJ9EJJ5yi9YZwIDAQAB";
									logger.info("13");
									logger.info("14");
									byte[] a = RSAUtil.encrypt(busInfo.getKek(), str.getBytes());
									logger.info("15");
									String sign = RSAUtil.base64Encode(a);
									logger.info("加密结果:" + RSAUtil.base64Encode(a));
									JsdsRequestDto requestDto = new JsdsRequestDto();
									requestDto.setGroupId(busInfo.getDepartmentnum());
									requestDto.setService("SMZF009");
									requestDto.setSignType("RSA");
									requestDto.setSign(sign);
									requestDto.setDatetime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
									logger.info("上送参数：" + JSON.toJSON(requestDto));
									// 返回的数据
									Map<String, String> results = this.sends(requestDto);
									logger.info("上游返回的数据:" + JSON.toJSON(results));
									if (results.get("msg").equals("1")) {
										logger.info("1未知结果进来了！！！");
										UpdateDaifu(reqData.getOrderNum(), "200");
										logger.info("2未知结果进来了！！！");
//										this.updateSelect(reqData,result,merchantinfo);
										logger.info("3未知结果结束了！！！");
									} else {
										if ("0000".equals(results.get("pl_code"))) {
											logger.info("1进来了！");
											// 解析签名
											String baseSign = URLDecoder.decode(results.get("pl_sign"), "UTF-8");
											baseSign = baseSign.replace(" ", "+");
											byte[] b = RSAUtil.verify(busInfo.getKek(), RSAUtil.base64Decode(baseSign));
											String signs = new String(b);

											logger.info("signs:" + signs);
											String[] signs1 = signs.split("&");
											logger.info("signs1" + signs1);
											String ss[] = signs1[0].split("=");
											if ("pl_transState=1".equals(signs1[3])) {
												UpdateDaifu(reqData.getOrderNum(), "00");
												result.put("respCode", "0000");
												result.put("respMsg", "请求成功");
												result.put("pl_service", "cj006");
												result.put("orderNum", ss[1]);
											} else if ("pl_transState=3".equals(signs1[3])) {
												UpdateDaifu(reqData.getOrderNum(), "200");
												result.put("respCode", "0000");
												result.put("respMsg", "请求成功");
												result.put("pl_service", "cj006");
												result.put("orderNum", ss[1]);
											} else {
												logger.info("又失败了3");
												UpdateDaifu(reqData.getOrderNum(), "01");
												Map<String, String> map = new HashMap<>();
												map.put("mercId", reqData.getMerchantCode());
												map.put("payMoney", b1.doubleValue() + "");
												int nus = pmsMerchantInfoDao.updataPayT1(map);
												if (nus == 1) {
													logger.info("加款成功！！");
													// 代付钱的总金额
													Double sa = surplus + Double.parseDouble(factAmount);
													model.setCount("1");
													model.setIdentity(reqData.getOrderNum());
													model.setBatchNo(reqData.getOrderNum() + "/A");
													model.setAmount(Double.parseDouble(factAmount) / 100 + "");
													model.setCardno(reqData.getAccountName());
													model.setRealname(reqData.getBankName());
													model.setPayamount(Double.parseDouble(factAmount) / 100 + "");
													model.setPmsbankno(reqData.getBankLinked());
													model.setTransactionType("代付补款");
													model.setPosition(sa.toString());
													model.setRemarks("T1");
													model.setRecordDescription("批次号:" + reqData.getOrderNum());
													model.setResponsecode("00");
													model.setOagentno("100333");
													model.setPayCounter("");
													int ii = pmsDaifuMerchantInfoDao.insert(model);
													if (ii == 1) {
														logger.info("添加代付补款记录成功！");
													}
												}
												result.put("respCode", "0006");
												result.put("respMsg", "下单失败");
											}
										} else {
											logger.info("2进来了！");
											UpdateDaifu(reqData.getOrderNum(), "01");
											Map<String, String> map = new HashMap<>();
											map.put("mercId", reqData.getMerchantCode());
											map.put("payMoney", b1.doubleValue() + "");
											int nus = pmsMerchantInfoDao.updataPayT1(map);
											if (nus == 1) {
												logger.info("加款成功！！");
												// 代付钱的总金额
												Double sa = surplus + Double.parseDouble(factAmount);
												model.setCount("1");
												model.setIdentity(reqData.getOrderNum());
												model.setBatchNo(reqData.getOrderNum() + "/A");
												model.setAmount(Double.parseDouble(factAmount) / 100 + "");
												model.setCardno(reqData.getAccountName());
												model.setRealname(reqData.getBankName());
												model.setPayamount(Double.parseDouble(factAmount) / 100 + "");
												model.setPmsbankno(reqData.getBankLinked());
												model.setTransactionType("代付补款");
												model.setPosition(sa.toString());
												model.setRemarks("T1");
												model.setRecordDescription("批次号:" + reqData.getOrderNum());
												model.setResponsecode("00");
												model.setOagentno("100333");
												model.setPayCounter("");
												int ii = pmsDaifuMerchantInfoDao.insert(model);
												if (ii == 1) {
													logger.info("添加代付补款记录成功！");
												}
											}
											result.put("respCode", "0006");
											result.put("respMsg", "下单失败");
										}
									}

								}
							}

						} else {
							throw new RuntimeException("系统错误----------------当前商户非正式商户");
						}

					} else {
						throw new RuntimeException("系统错误----------------商户不存在");
					}
					break label104;
				}

			} catch (Exception var43) {
				logger.error("****************************代付错误", var43);
				throw var43;
			}
		//}

		logger.info("***********江苏电商*********************代付------处理完成");
		return result;

	}

	private boolean verify(JsdsRequestDto reqData, Map<String, String> result) {
		boolean signResult = false;
		logger.info("****************************开始签名处理");

		try {
			String e = reqData.getSign();
			System.out.println("上传的签名："+e);
			HashMap<String, String> signMap = JsdsUtil.beanToMap(reqData);
			signMap.remove("sign");
			Set<String> keys = new TreeSet<String>();
			// 剔除值为空的
			for (String key : signMap.keySet()) {
				if ("".equals(signMap.get(key)) || signMap.get(key) == null) {
					keys.add(key);
				}
			}
			for (String key : keys) {
				signMap.remove(key);
			}
			String merchNo = reqData.getMerchantCode();
			logger.info("********************江苏电商-----------------当前商户号:" + merchNo);
			ChannleMerchantConfigKey channerKey = this.cmckeyDao.get(merchNo);
			logger.info("********************江苏电商-----------------商户密钥:" + channerKey);
			String key = channerKey.getMerchantkey();
			System.out.println("生成签名的数据:" + signMap);
			System.out.println("秘钥:" + key);
			String e1=JsdsUtil.sign(signMap, key);
			System.out.println("生成的前名："+e1);
			boolean b =e.equals(e1);
			System.out.println("b:"+b);
			if (!e.equals(e1)) {
				result.put("respCode", "0008");
				result.put("respMsg", "签名错误");
				signResult = true;
			}
		} catch (Exception var9) {
			result.put("respCode", "0008");
			result.put("respMsg", "签名错误");
			signResult = true;
		}

		return signResult;
	}

	/**
	 * 发送数据
	 * 
	 * @param req
	 * @return
	 */
	public Map<String, String> send(JsdsRequestDto req) {
		logger.info("**********************江苏电商-----生成码 开始:");
		HashMap<String, String> result = new HashMap<String, String>();
		HashMap<String, String> params = JsdsUtil.beanToMap(req);
		JsdsUtil.process(params);
		try {
			params.put("sign", JsdsUtil.sign(params, "cf91ec"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("**********江苏电商上送报文:{}" + params);
		String respJson = HttpUtil.sendPost("http://121.41.121.164:8044/TransInterface/TransRequest", params);

		logger.info("**********江苏电商响应报文:{}" + respJson);
		if (respJson != null) {
			JSONObject json = JSONObject.fromObject(respJson);
			result.putAll(json);
		}
		return result;
	}

	public Map<String, String> sends(JsdsRequestDto req) {
		logger.info("**********************江苏电商-----生成码 开始:");
		HashMap<String, String> result = new HashMap<String, String>();
		HashMap<String, String> params = JsdsUtil.beanToMap(req);
		String paramStr = HttpUtil.parseParams(params);
		logger.info("上送字符串：" + paramStr);
		String respJson = HttpURLConection.httpURLConnectionPOST("http://180.96.28.8:8044/TransInterface/TransRequest",
				paramStr);
		logger.info("上游返回字符串数据：" + respJson);
		if (respJson != null && respJson != "") {
			JSONObject json = JSONObject.fromObject(respJson);
			result.put("msg", "0");
			result.putAll(json);
		} else {
			result.put("msg", "1");
		}
		return result;
	}

	/**
	 * 返回设置签名
	 * 
	 * @param result
	 * @param reqData
	 */
	private void setCmmon(Map<String, String> result, JsdsRequestDto reqData) {
		result.putAll(JsdsUtil.beanToMap(reqData));
		Set<String> keys = new TreeSet<String>();
		// 剔除值为空的
		for (String key : result.keySet()) {
			if ("".equals(result.get(key)) || result.get(key) == null) {
				keys.add(key);
			}
		}
		for (String key : keys) {
			result.remove(key);
		}
		String key;
		try {
			// 设置签名
			String merchNo = reqData.getMerchantCode();
			logger.info("********************江苏电商-----------------当前商户号:" + merchNo);
			ChannleMerchantConfigKey channerKey = cmckeyDao.get(merchNo);
			logger.info("********************江苏电商-----------------商户密钥:" + channerKey);
			key = channerKey.getMerchantkey();
			result.put("sign", JsdsUtil.sign(result, key));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 京东扫码
	 * 
	 * @param result
	 * @param reqData
	 */
	private Map<String, String> JDhandleQuery(JsdsRequestDto reqData, Map<String, String> result,
			PmsBusinessPos busInfo) throws Exception {

		logger.info("********************************京东扫码------开始:{}" + reqData);

		if (this.verify(reqData, result)) {
			logger.info("****************************京东扫码------签名错误");
			return result;
		} else {
			logger.info("验证当前是否已经下单");
			OriginalOrderInfo origin = new OriginalOrderInfo();
			origin.setPid(reqData.getMerchantCode());
			origin.setMerchantOrderId(reqData.getOrderNum());
			if (this.originalDao.selectByOriginal(origin) != null) {
				result.put("respCode", "0006");
				result.put("respMsg", "下单失败");
				logger.info("**********************生成支付二维码 下单失败:{}");
				return result;
			} else {
				String orderNumber = reqData.getOrderNum();
				origin.setOrderId(orderNumber);
				origin.setMerchantOrderId(reqData.getOrderNum());
				origin.setPid(reqData.getMerchantCode());
				origin.setOrderTime(reqData.getDatetime());
				NumberFormat nbf = NumberFormat.getInstance();
				nbf.setMinimumFractionDigits(2);
				Double orderAmount = Double.valueOf(reqData.getTransMoney());
				String c = nbf.format(orderAmount.doubleValue() / 100.0D);
				origin.setOrderAmount(c);
				origin.setPayType(reqData.getService());
				origin.setPageUrl(reqData.getNotifyUrl());
				origin.setBgUrl(reqData.getNotifyUrl());

				try {
					label104: {
						this.originalDao.insert(origin);
						logger.info("********************江苏电商-------------根据商户号查询");
						String e = reqData.getMerchantCode();
						PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
						merchantinfo.setMercId(e);
						List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
						if (merchantList.size() != 0 && !merchantList.isEmpty()) {
							merchantinfo = (PmsMerchantInfo) merchantList.get(0);
							String oAgentNo = merchantinfo.getoAgentNo();
							logger.info("***********江苏电商*************商户信息:" + merchantinfo);
							if (StringUtils.isBlank(oAgentNo)) {
								throw new RuntimeException("系统错误----------------o单编号为空");
							}

							if ("60".equals(merchantinfo.getMercSts())) {
								logger.info("***********江苏电商*************实际金额");
								String factAmount = reqData.getTransMoney();
								logger.info("***********江苏电商*************校验欧单金额限制");
								ResultInfo payCheckResult = this.iPublicTradeVerifyService.amountVerifyOagent(
										(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);
								if (!payCheckResult.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
											+ PaymentCodeEnum.JingDong.getTypeCode());
									throw new RuntimeException("交易不支付");
								}

								logger.info("***********江苏电商*************校验欧单模块是否开启");
								ResultInfo payCheckResult1 = this.iPublicTradeVerifyService
										.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);
								if (payCheckResult1.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************校验商户模块是否开启");
									ResultInfo payCheckResult3 = this.iPublicTradeVerifyService
											.moduelVerifyMer(TradeTypeEnum.merchantCollect, e);
									if (!payCheckResult3.getErrCode().equals("0")) {
										logger.info("***********江苏电商*************商户模块限制，oAagentNo:" + oAgentNo
												+ ",payType:" + PaymentCodeEnum.JingDong.getTypeCode());
										throw new RuntimeException("交易不支付");
									}

									logger.info("***********江苏电商*************校验商户金额限制");
									HashMap paramMap = new HashMap();
									paramMap.put("mercid", e);
									paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
									paramMap.put("oAgentNo", oAgentNo);
									logger.info("***********江苏电商*************商户 商城 业务信息 ");
									Map resultMap = this.merchantMineDao.queryBusinessInfo(paramMap);
									if (resultMap != null && resultMap.size() != 0) {
										String maxTransMoney = (String) resultMap.get("MAX_AMOUNT");
										String minTransMoney = (String) resultMap.get("MIN_AMOUNT");
										if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(maxTransMoney)) == 1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额超限");
											return result;
										} else if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(minTransMoney)) == -1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额小于最小交易金额");
											logger.info("***********江苏电商*************交易金额小于最小金额");
											return result;
										} else {
											logger.info("***********江苏电商*************组装订单数据");
											PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
											logger.info("写入欧单编号");
											pmsAppTransInfo.setoAgentNo(oAgentNo);
											pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
											pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());
											logger.info("***********江苏电商*************网购");
											pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
											pmsAppTransInfo.setMercid(merchantinfo.getMercId());
											pmsAppTransInfo
													.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());
											pmsAppTransInfo.setOrderid(orderNumber);
											pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.JingDong.getTypeName());
											pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.JingDong.getTypeCode());
											BigDecimal factBigDecimal = new BigDecimal(factAmount);
											BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
											pmsAppTransInfo
													.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setOrderamount(
													orderAmountBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setDrawMoneyType("1");
											logger.info("***********江苏电商*************插入订单信息");
											Integer insertAppTrans = Integer
													.valueOf(this.pmsAppTransInfoDao.insert(pmsAppTransInfo));
											if (insertAppTrans.intValue() == 1) {
												logger.info("***********江苏电商*************查询订单信息");
												pmsAppTransInfo = this.pmsAppTransInfoDao
														.searchOrderInfo(pmsAppTransInfo.getOrderid());
												String quickRateType = ((String) resultMap.get("QUICKRATETYPE"))
														.toString();
												logger.info("***********江苏电商*************获取o单第三方支付的费率");
												AppRateConfig appRate = new AppRateConfig();
												appRate.setRateType(quickRateType);
												appRate.setoAgentNo(oAgentNo);
												AppRateConfig appRateConfig = this.appRateConfigDao
														.getByRateTypeAndoAgentNo(appRate);
												if (appRateConfig == null) {
													throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
												}

												String isTop = appRateConfig.getIsTop();
												String rate = appRateConfig.getRate();
												String topPoundage = appRateConfig.getTopPoundage();
												String paymentAmount = pmsAppTransInfo.getFactamount();
												String minPoundageStr = appRateConfig.getBottomPoundage();
												Double minPoundage = Double.valueOf(0.0D);
												if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
														&& appRateConfig.getIsBottom().equals("1")) {
													if (StringUtils.isNotBlank(minPoundageStr)) {
														minPoundage = Double
																.valueOf(Double.parseDouble(minPoundageStr));
													} else {
														result.put("respCode", "0005");
														result.put("respMsg", "没有查到相关费率附加费");
														logger.info("***********江苏电商*************没有查到相关费率附加费（最低手续费）："
																+ merchantinfo.getMobilephone());
														return result;
													}
												} else {
													BigDecimal payAmount = null;
													BigDecimal dfactAmount = new BigDecimal(
															pmsAppTransInfo.getFactamount());
													new BigDecimal(0);
													BigDecimal b = new BigDecimal(0);
													BigDecimal f = new BigDecimal(100);
													String rateStr = "";
													BigDecimal fee;
													if ("1".equals(isTop)) {
														rateStr = rate + "-" + topPoundage;
														logger.info("***********江苏电商*************是封顶费率类型");
														fee = (new BigDecimal(rate)).multiply(dfactAmount).divide(f)
																.setScale(2, BigDecimal.ROUND_UP).multiply(f);
														if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
															logger.info(
																	"***********江苏电商*************手续费大于封顶金额，按封顶金额处理");
															payAmount = dfactAmount
																	.subtract((new BigDecimal(topPoundage)).subtract(
																			new BigDecimal(minPoundage.doubleValue())));
															fee = (new BigDecimal(topPoundage))
																	.add(new BigDecimal(minPoundage.doubleValue()));
														} else {
															logger.info("***********江苏电商*************按当前费率处理");
															rateStr = rate;
															fee.add(new BigDecimal(minPoundage.doubleValue()));
															payAmount = dfactAmount.subtract(fee);
														}
													} else {
														logger.info("***********江苏电商*************按当前费率处理");
														rateStr = rate;
														fee = (new BigDecimal(rate)).multiply(dfactAmount)
																.add(new BigDecimal(minPoundage.doubleValue()))
																.divide(f).setScale(2, BigDecimal.ROUND_UP).multiply(f);
														double paymentAmountInt = fee.doubleValue();
														if (merchantinfo.getCounter() != null) {
															double channelInfo = Double
																	.parseDouble(merchantinfo.getCounter()) * 100;
															if (paymentAmountInt < channelInfo) {
																logger.info("手续费小于商户手续费");
																result.put("respCode", "手续费小于商户手续费");
																return result;

															} else {
																b = fee;
																payAmount = dfactAmount.subtract(fee);
															}
														}
													}
													logger.info("***********江苏电商*************设置结算金额");
													pmsAppTransInfo.setPayamount(payAmount.toString());
													pmsAppTransInfo.setRate(rateStr);
													pmsAppTransInfo.setPoundage(b.toString());
													pmsAppTransInfo.setDrawMoneyType("1");
													Integer paymentAmountInt1 = Integer
															.valueOf((int) Double.parseDouble(paymentAmount));
													logger.info("***********江苏电商*************验证支付方式是否开启");
													payCheckResult = this.iPublicTradeVerifyService.totalVerify(
															paymentAmountInt1.intValue(), TradeTypeEnum.merchantCollect,
															PaymentCodeEnum.JingDong, oAgentNo,
															merchantinfo.getMercId());
													if (!payCheckResult.getErrCode().equals("0")) {
														logger.info("***********江苏电商*************交易不支持");
														throw new RuntimeException("交易不支持");
													}

													logger.info("***********江苏电商*************设置通道信息");
													ViewKyChannelInfo channelInfo1 = (ViewKyChannelInfo) AppPospContext.context
															.get("JS100669");
													logger.info("设置通道信息");
													if (channelInfo1 != null) {
														pmsAppTransInfo.setBusinessNum(channelInfo1.getBusinessnum());
														pmsAppTransInfo.setChannelNum(channelInfo1.getChannelNum());
													}

													logger.info("***********江苏电商*************查看当前交易是否已经生成了流水表");
													PospTransInfo pospTransInfo = new PospTransInfo();
													logger.info(
															"***********江苏电商*************流水表是否需要更新的标记 0 insert，1：update");
													logger.info("***********江苏电商*************生成上送流水号");

													logger.info("***********江苏电商*************不存在流水，生成一个流水");
													pospTransInfo = this.generateTransFromAppTrans(pmsAppTransInfo);
													logger.info("***********江苏电商*************设置上送流水号");
													String transOrderId = reqData.getOrderNum();
													pospTransInfo.setPospsn(transOrderId);
													pospTransInfo.setTransOrderId(transOrderId);
													if (this.pospTransInfoDAO.insert(pospTransInfo) == 1) {
														pospTransInfo.setSysseqno(transOrderId);
														this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
														logger.info("修改订单信息");
														logger.info("" + pmsAppTransInfo);
														this.pmsAppTransInfoDao.update(pmsAppTransInfo);
														// 处理生成二维码
														this.twoDimensionCodeProcess(
																reqData,
																result,
																busInfo);

													} else {
														result.put("respCode", "0006");
														result.put("respMsg", "下单失败");
														return result;
													}
												}
											} else {
												result.put("respCode", "0006");
												result.put("respMsg", "下单失败");
												return result;
											}
										}
										break label104;
									}

									logger.info(
											"***********江苏电商*************没有查到相关费率配置：" + merchantinfo.getMobilephone());
									throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
								}

								logger.info("***********江苏电商*************欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"
										+ PaymentCodeEnum.JingDong.getTypeCode());
								throw new RuntimeException("交易不支付");
							}

							throw new RuntimeException("系统错误----------------当前商户非正式商户");
						}

						throw new RuntimeException();
					}
				} catch (Exception var43) {
					logger.error("****************************生成二维码错误：", var43);
					throw var43;
				}
			}

			logger.info("***********江苏电商*********************微信支付------处理完成");
			return result;
		}
	}
	/**
	 * 
	 * 京东H5
	 * 
	 * @param result
	 * @param reqData
	 */
	private Map<String, String> JDH5handleQuery(JsdsRequestDto reqData, Map<String, String> result,
			PmsBusinessPos busInfo) throws Exception {

		logger.info("********************************京东H5------开始:{}" + reqData);

		if (this.verify(reqData, result)) {
			logger.info("****************************京东H5------签名错误");
			return result;
		} else {
			logger.info("验证当前是否已经下单");
			OriginalOrderInfo origin = new OriginalOrderInfo();
			origin.setPid(reqData.getMerchantCode());
			origin.setMerchantOrderId(reqData.getOrderNum());
			if (this.originalDao.selectByOriginal(origin) != null) {
				result.put("respCode", "0006");
				result.put("respMsg", "下单失败");
				logger.info("**********************生成京东H5下单失败:{}");
				return result;
			} else {
				String orderNumber = reqData.getOrderNum();
				origin.setOrderId(orderNumber);
				origin.setMerchantOrderId(reqData.getOrderNum());
				origin.setPid(reqData.getMerchantCode());
				origin.setOrderTime(reqData.getDatetime());
				NumberFormat nbf = NumberFormat.getInstance();
				nbf.setMinimumFractionDigits(2);
				Double orderAmount = Double.valueOf(reqData.getTransMoney());
				String c = nbf.format(orderAmount.doubleValue() / 100.0D);
				origin.setOrderAmount(c);
				origin.setPayType(reqData.getService());
				origin.setPageUrl(reqData.getNotifyUrl());
				origin.setBgUrl(reqData.getNotifyUrl());

				try {
					label104: {
						this.originalDao.insert(origin);
						logger.info("********************江苏电商-------------根据商户号查询");
						String e = reqData.getMerchantCode();
						PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
						merchantinfo.setMercId(e);
						List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
						if (merchantList.size() != 0 && !merchantList.isEmpty()) {
							merchantinfo = (PmsMerchantInfo) merchantList.get(0);
							String oAgentNo = merchantinfo.getoAgentNo();
							logger.info("***********江苏电商*************商户信息:" + merchantinfo);
							if (StringUtils.isBlank(oAgentNo)) {
								throw new RuntimeException("系统错误----------------o单编号为空");
							}

							if ("60".equals(merchantinfo.getMercSts())) {
								logger.info("***********江苏电商*************实际金额");
								String factAmount = reqData.getTransMoney();
								logger.info("***********江苏电商*************校验欧单金额限制");
								ResultInfo payCheckResult = this.iPublicTradeVerifyService.amountVerifyOagent(
										(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);
								if (!payCheckResult.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
											+ PaymentCodeEnum.JingDong.getTypeCode());
									throw new RuntimeException("交易不支付");
								}

								logger.info("***********江苏电商*************校验欧单模块是否开启");
								ResultInfo payCheckResult1 = this.iPublicTradeVerifyService
										.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);
								if (payCheckResult1.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************校验商户模块是否开启");
									ResultInfo payCheckResult3 = this.iPublicTradeVerifyService
											.moduelVerifyMer(TradeTypeEnum.merchantCollect, e);
									if (!payCheckResult3.getErrCode().equals("0")) {
										logger.info("***********江苏电商*************商户模块限制，oAagentNo:" + oAgentNo
												+ ",payType:" + PaymentCodeEnum.JingDong.getTypeCode());
										throw new RuntimeException("交易不支付");
									}

									logger.info("***********江苏电商*************校验商户金额限制");
									HashMap paramMap = new HashMap();
									paramMap.put("mercid", e);
									paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
									paramMap.put("oAgentNo", oAgentNo);
									logger.info("***********江苏电商*************商户 商城 业务信息 ");
									Map resultMap = this.merchantMineDao.queryBusinessInfo(paramMap);
									if (resultMap != null && resultMap.size() != 0) {
										String maxTransMoney = (String) resultMap.get("MAX_AMOUNT");
										String minTransMoney = (String) resultMap.get("MIN_AMOUNT");
										if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(maxTransMoney)) == 1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额超限");
											return result;
										} else if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(minTransMoney)) == -1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额小于最小交易金额");
											logger.info("***********江苏电商*************交易金额小于最小金额");
											return result;
										} else {
											logger.info("***********江苏电商*************组装订单数据");
											PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
											logger.info("写入欧单编号");
											pmsAppTransInfo.setoAgentNo(oAgentNo);
											pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
											pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());
											logger.info("***********江苏电商*************网购");
											pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
											pmsAppTransInfo.setMercid(merchantinfo.getMercId());
											pmsAppTransInfo
													.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());
											pmsAppTransInfo.setOrderid(orderNumber);
											pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.JingDong.getTypeName());
											pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.JingDong.getTypeCode());
											BigDecimal factBigDecimal = new BigDecimal(factAmount);
											BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
											pmsAppTransInfo
													.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setOrderamount(
													orderAmountBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setDrawMoneyType("1");
											logger.info("***********江苏电商*************插入订单信息");
											Integer insertAppTrans = Integer
													.valueOf(this.pmsAppTransInfoDao.insert(pmsAppTransInfo));
											if (insertAppTrans.intValue() == 1) {
												logger.info("***********江苏电商*************查询订单信息");
												pmsAppTransInfo = this.pmsAppTransInfoDao
														.searchOrderInfo(pmsAppTransInfo.getOrderid());
												String quickRateType = ((String) resultMap.get("QUICKRATETYPE"))
														.toString();
												logger.info("***********江苏电商*************获取o单第三方支付的费率");
												AppRateConfig appRate = new AppRateConfig();
												appRate.setRateType(quickRateType);
												appRate.setoAgentNo(oAgentNo);
												AppRateConfig appRateConfig = this.appRateConfigDao
														.getByRateTypeAndoAgentNo(appRate);
												if (appRateConfig == null) {
													throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
												}

												String isTop = appRateConfig.getIsTop();
												String rate = appRateConfig.getRate();
												String topPoundage = appRateConfig.getTopPoundage();
												String paymentAmount = pmsAppTransInfo.getFactamount();
												String minPoundageStr = appRateConfig.getBottomPoundage();
												Double minPoundage = Double.valueOf(0.0D);
												if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
														&& appRateConfig.getIsBottom().equals("1")) {
													if (StringUtils.isNotBlank(minPoundageStr)) {
														minPoundage = Double
																.valueOf(Double.parseDouble(minPoundageStr));
													} else {
														result.put("respCode", "0005");
														result.put("respMsg", "没有查到相关费率附加费");
														logger.info("***********江苏电商*************没有查到相关费率附加费（最低手续费）："
																+ merchantinfo.getMobilephone());
														return result;
													}
												} else {
													BigDecimal payAmount = null;
													BigDecimal dfactAmount = new BigDecimal(
															pmsAppTransInfo.getFactamount());
													new BigDecimal(0);
													BigDecimal b = new BigDecimal(0);
													BigDecimal f = new BigDecimal(100);
													String rateStr = "";
													BigDecimal fee;
													if ("1".equals(isTop)) {
														rateStr = rate + "-" + topPoundage;
														logger.info("***********江苏电商*************是封顶费率类型");
														fee = (new BigDecimal(rate)).multiply(dfactAmount).divide(f)
																.setScale(2, BigDecimal.ROUND_UP).multiply(f);
														if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
															logger.info(
																	"***********江苏电商*************手续费大于封顶金额，按封顶金额处理");
															payAmount = dfactAmount
																	.subtract((new BigDecimal(topPoundage)).subtract(
																			new BigDecimal(minPoundage.doubleValue())));
															fee = (new BigDecimal(topPoundage))
																	.add(new BigDecimal(minPoundage.doubleValue()));
														} else {
															logger.info("***********江苏电商*************按当前费率处理");
															rateStr = rate;
															fee.add(new BigDecimal(minPoundage.doubleValue()));
															payAmount = dfactAmount.subtract(fee);
														}
													} else {
														logger.info("***********江苏电商*************按当前费率处理");
														rateStr = rate;
														fee = (new BigDecimal(rate)).multiply(dfactAmount)
																.add(new BigDecimal(minPoundage.doubleValue()))
																.divide(f).setScale(2, BigDecimal.ROUND_UP).multiply(f);
														double paymentAmountInt = fee.doubleValue();
														if (merchantinfo.getCounter() != null) {
															double channelInfo = Double
																	.parseDouble(merchantinfo.getCounter()) * 100;
															if (paymentAmountInt < channelInfo) {
																logger.info("手续费小于商户手续费");
																result.put("respCode", "手续费小于商户手续费");
																return result;

															} else {
																b = fee;
																payAmount = dfactAmount.subtract(fee);
															}
														}
													}
													logger.info("***********江苏电商*************设置结算金额");
													pmsAppTransInfo.setPayamount(payAmount.toString());
													pmsAppTransInfo.setRate(rateStr);
													pmsAppTransInfo.setPoundage(b.toString());
													pmsAppTransInfo.setDrawMoneyType("1");
													Integer paymentAmountInt1 = Integer
															.valueOf((int) Double.parseDouble(paymentAmount));
													logger.info("***********江苏电商*************验证支付方式是否开启");
													payCheckResult = this.iPublicTradeVerifyService.totalVerify(
															paymentAmountInt1.intValue(), TradeTypeEnum.merchantCollect,
															PaymentCodeEnum.JingDong, oAgentNo,
															merchantinfo.getMercId());
													if (!payCheckResult.getErrCode().equals("0")) {
														logger.info("***********江苏电商*************交易不支持");
														throw new RuntimeException("交易不支持");
													}

													logger.info("***********江苏电商*************设置通道信息");
													ViewKyChannelInfo channelInfo1 = (ViewKyChannelInfo) AppPospContext.context
															.get("JS100669");
													logger.info("设置通道信息");
													if (channelInfo1 != null) {
														pmsAppTransInfo.setBusinessNum(channelInfo1.getBusinessnum());
														pmsAppTransInfo.setChannelNum(channelInfo1.getChannelNum());
													}

													logger.info("***********江苏电商*************查看当前交易是否已经生成了流水表");
													PospTransInfo pospTransInfo = new PospTransInfo();
													logger.info(
															"***********江苏电商*************流水表是否需要更新的标记 0 insert，1：update");
													logger.info("***********江苏电商*************生成上送流水号");

													logger.info("***********江苏电商*************不存在流水，生成一个流水");
													pospTransInfo = this.generateTransFromAppTrans(pmsAppTransInfo);
													logger.info("***********江苏电商*************设置上送流水号");
													String transOrderId = reqData.getOrderNum();
													pospTransInfo.setPospsn(transOrderId);
													pospTransInfo.setTransOrderId(transOrderId);
													if (this.pospTransInfoDAO.insert(pospTransInfo) == 1) {
														pospTransInfo.setSysseqno(transOrderId);
														this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
														logger.info("修改订单信息");
														logger.info("" + pmsAppTransInfo);
														this.pmsAppTransInfoDao.update(pmsAppTransInfo);
														// 处理生成二维码
														this.twoDimensionCodeProcess(
																reqData,
																result,
																busInfo);

													} else {
														result.put("respCode", "0006");
														result.put("respMsg", "下单失败");
														return result;
													}
												}
											} else {
												result.put("respCode", "0006");
												result.put("respMsg", "下单失败");
												return result;
											}
										}
										break label104;
									}

									logger.info(
											"***********江苏电商*************没有查到相关费率配置：" + merchantinfo.getMobilephone());
									throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
								}

								logger.info("***********江苏电商*************欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"
										+ PaymentCodeEnum.JingDong.getTypeCode());
								throw new RuntimeException("交易不支付");
							}

							throw new RuntimeException("系统错误----------------当前商户非正式商户");
						}

						throw new RuntimeException();
					}
				} catch (Exception var43) {
					logger.error("****************************生成二维码错误：", var43);
					throw var43;
				}
			}

			logger.info("***********江苏电商*********************微信支付------处理完成");
			return result;
		}
	}
	
	/**
	 * QQ钱包
	 * 
	 * @param result
	 * @param reqData
	 */
	private Map<String, String> QQhandleQuery(JsdsRequestDto reqData, Map<String, String> result,
			PmsBusinessPos busInfo) throws Exception {

		logger.info("********************************QQ钱包------开始:{}" + reqData);

		if (this.verify(reqData, result)) {
			logger.info("****************************QQ钱包------签名错误");
			return result;
		} else {
			logger.info("验证当前是否已经下单");
			OriginalOrderInfo origin = new OriginalOrderInfo();
			origin.setPid(reqData.getMerchantCode());
			origin.setMerchantOrderId(reqData.getOrderNum());
			if (this.originalDao.selectByOriginal(origin) != null) {
				result.put("respCode", "0006");
				result.put("respMsg", "下单失败");
				logger.info("**********************生成支付二维码 下单失败:{}");
				return result;
			} else {
				String orderNumber = reqData.getOrderNum();
				origin.setOrderId(orderNumber);
				origin.setMerchantOrderId(reqData.getOrderNum());
				origin.setPid(reqData.getMerchantCode());
				origin.setOrderTime(reqData.getDatetime());
				NumberFormat nbf = NumberFormat.getInstance();
				nbf.setMinimumFractionDigits(2);
				Double orderAmount = Double.valueOf(reqData.getTransMoney());
				String c = nbf.format(orderAmount.doubleValue() / 100.0D);
				origin.setOrderAmount(c);
				origin.setPayType(reqData.getService());
				origin.setPageUrl(reqData.getNotifyUrl());
				origin.setBgUrl(reqData.getNotifyUrl());

				try {
					label104: {
						this.originalDao.insert(origin);
						logger.info("********************江苏电商-------------根据商户号查询");
						String e = reqData.getMerchantCode();
						PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
						merchantinfo.setMercId(e);
						List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
						if (merchantList.size() != 0 && !merchantList.isEmpty()) {
							merchantinfo = (PmsMerchantInfo) merchantList.get(0);
							String oAgentNo = merchantinfo.getoAgentNo();
							logger.info("***********江苏电商*************商户信息:" + merchantinfo);
							if (StringUtils.isBlank(oAgentNo)) {
								throw new RuntimeException("系统错误----------------o单编号为空");
							}

							if ("60".equals(merchantinfo.getMercSts())) {
								logger.info("***********江苏电商*************实际金额");
								String factAmount = reqData.getTransMoney();
								logger.info("***********江苏电商*************校验欧单金额限制");
								ResultInfo payCheckResult = this.iPublicTradeVerifyService.amountVerifyOagent(
										(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);
								if (!payCheckResult.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
											+ PaymentCodeEnum.QQCodePay.getTypeCode());
									throw new RuntimeException("交易不支付");
								}

								logger.info("***********江苏电商*************校验欧单模块是否开启");
								ResultInfo payCheckResult1 = this.iPublicTradeVerifyService
										.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);
								if (payCheckResult1.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************校验商户模块是否开启");
									ResultInfo payCheckResult3 = this.iPublicTradeVerifyService
											.moduelVerifyMer(TradeTypeEnum.merchantCollect, e);
									if (!payCheckResult3.getErrCode().equals("0")) {
										logger.info("***********江苏电商*************商户模块限制，oAagentNo:" + oAgentNo
												+ ",payType:" + PaymentCodeEnum.QQCodePay.getTypeCode());
										throw new RuntimeException("交易不支付");
									}

									logger.info("***********江苏电商*************校验商户金额限制");
									HashMap paramMap = new HashMap();
									paramMap.put("mercid", e);
									paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
									paramMap.put("oAgentNo", oAgentNo);
									logger.info("***********江苏电商*************商户 商城 业务信息 ");
									Map resultMap = this.merchantMineDao.queryBusinessInfo(paramMap);
									if (resultMap != null && resultMap.size() != 0) {
										String maxTransMoney = (String) resultMap.get("MAX_AMOUNT");
										String minTransMoney = (String) resultMap.get("MIN_AMOUNT");
										if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(maxTransMoney)) == 1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额超限");
											return result;
										} else if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(minTransMoney)) == -1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额小于最小交易金额");
											logger.info("***********江苏电商*************交易金额小于最小金额");
											return result;
										} else {
											logger.info("***********江苏电商*************组装订单数据");
											PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
											logger.info("写入欧单编号");
											pmsAppTransInfo.setoAgentNo(oAgentNo);
											pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
											pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());
											logger.info("***********江苏电商*************网购");
											pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
											pmsAppTransInfo.setMercid(merchantinfo.getMercId());
											pmsAppTransInfo
													.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());
											pmsAppTransInfo.setOrderid(orderNumber);
											pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.QQCodePay.getTypeName());
											pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.QQCodePay.getTypeCode());
											BigDecimal factBigDecimal = new BigDecimal(factAmount);
											BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
											pmsAppTransInfo
													.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setOrderamount(
													orderAmountBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setDrawMoneyType("1");
											logger.info("***********江苏电商*************插入订单信息");
											Integer insertAppTrans = Integer
													.valueOf(this.pmsAppTransInfoDao.insert(pmsAppTransInfo));
											if (insertAppTrans.intValue() == 1) {
												logger.info("***********江苏电商*************查询订单信息");
												pmsAppTransInfo = this.pmsAppTransInfoDao
														.searchOrderInfo(pmsAppTransInfo.getOrderid());
												String quickRateType = ((String) resultMap.get("QUICKRATETYPE"))
														.toString();
												logger.info("***********江苏电商*************获取o单第三方支付的费率");
												AppRateConfig appRate = new AppRateConfig();
												appRate.setRateType(quickRateType);
												appRate.setoAgentNo(oAgentNo);
												AppRateConfig appRateConfig = this.appRateConfigDao
														.getByRateTypeAndoAgentNo(appRate);
												if (appRateConfig == null) {
													throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
												}

												String isTop = appRateConfig.getIsTop();
												String rate = appRateConfig.getRate();
												String topPoundage = appRateConfig.getTopPoundage();
												String paymentAmount = pmsAppTransInfo.getFactamount();
												String minPoundageStr = appRateConfig.getBottomPoundage();
												Double minPoundage = Double.valueOf(0.0D);
												if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
														&& appRateConfig.getIsBottom().equals("1")) {
													if (StringUtils.isNotBlank(minPoundageStr)) {
														minPoundage = Double
																.valueOf(Double.parseDouble(minPoundageStr));
													} else {
														result.put("respCode", "0005");
														result.put("respMsg", "没有查到相关费率附加费");
														logger.info("***********江苏电商*************没有查到相关费率附加费（最低手续费）："
																+ merchantinfo.getMobilephone());
														return result;
													}
												} else {
													BigDecimal payAmount = null;
													BigDecimal dfactAmount = new BigDecimal(
															pmsAppTransInfo.getFactamount());
													new BigDecimal(0);
													BigDecimal b = new BigDecimal(0);
													BigDecimal f = new BigDecimal(100);
													String rateStr = "";
													BigDecimal fee;
													if ("1".equals(isTop)) {
														rateStr = rate + "-" + topPoundage;
														logger.info("***********江苏电商*************是封顶费率类型");
														fee = (new BigDecimal(rate)).multiply(dfactAmount).divide(f)
																.setScale(2, BigDecimal.ROUND_UP).multiply(f);
														if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
															logger.info(
																	"***********江苏电商*************手续费大于封顶金额，按封顶金额处理");
															payAmount = dfactAmount
																	.subtract((new BigDecimal(topPoundage)).subtract(
																			new BigDecimal(minPoundage.doubleValue())));
															fee = (new BigDecimal(topPoundage))
																	.add(new BigDecimal(minPoundage.doubleValue()));
														} else {
															logger.info("***********江苏电商*************按当前费率处理");
															rateStr = rate;
															fee.add(new BigDecimal(minPoundage.doubleValue()));
															payAmount = dfactAmount.subtract(fee);
														}
													} else {
														logger.info("***********江苏电商*************按当前费率处理");
														rateStr = rate;
														fee = (new BigDecimal(rate)).multiply(dfactAmount)
																.add(new BigDecimal(minPoundage.doubleValue()))
																.divide(f).setScale(2, BigDecimal.ROUND_UP).multiply(f);
														double paymentAmountInt = fee.doubleValue();
														if (merchantinfo.getCounter() != null) {
															double channelInfo = Double
																	.parseDouble(merchantinfo.getCounter()) * 100;
															if (paymentAmountInt < channelInfo) {
																logger.info("手续费小于商户手续费");
																result.put("respCode", "手续费小于商户手续费");
																return result;

															} else {
																b = fee;
																payAmount = dfactAmount.subtract(fee);
															}
														}
													}
													logger.info("***********江苏电商*************设置结算金额");
													pmsAppTransInfo.setPayamount(payAmount.toString());
													pmsAppTransInfo.setRate(rateStr);
													pmsAppTransInfo.setPoundage(b.toString());
													pmsAppTransInfo.setDrawMoneyType("1");
													Integer paymentAmountInt1 = Integer
															.valueOf((int) Double.parseDouble(paymentAmount));
													logger.info("***********江苏电商*************验证支付方式是否开启");
													payCheckResult = this.iPublicTradeVerifyService.totalVerify(
															paymentAmountInt1.intValue(), TradeTypeEnum.merchantCollect,
															PaymentCodeEnum.QQCodePay, oAgentNo,
															merchantinfo.getMercId());
													if (!payCheckResult.getErrCode().equals("0")) {
														logger.info("***********江苏电商*************交易不支持");
														throw new RuntimeException("交易不支持");
													}

													logger.info("***********江苏电商*************设置通道信息");
													ViewKyChannelInfo channelInfo1 = (ViewKyChannelInfo) AppPospContext.context
															.get("JS100669");
													logger.info("设置通道信息");
													if (channelInfo1 != null) {
														pmsAppTransInfo.setBusinessNum(channelInfo1.getBusinessnum());
														pmsAppTransInfo.setChannelNum(channelInfo1.getChannelNum());
													}

													logger.info("***********江苏电商*************查看当前交易是否已经生成了流水表");
													PospTransInfo pospTransInfo = new PospTransInfo();
													logger.info(
															"***********江苏电商*************流水表是否需要更新的标记 0 insert，1：update");
													logger.info("***********江苏电商*************生成上送流水号");

													logger.info("***********江苏电商*************不存在流水，生成一个流水");
													pospTransInfo = this.generateTransFromAppTrans(pmsAppTransInfo);
													logger.info("***********江苏电商*************设置上送流水号");
													String transOrderId = reqData.getOrderNum();
													pospTransInfo.setPospsn(transOrderId);
													pospTransInfo.setTransOrderId(transOrderId);
													if (this.pospTransInfoDAO.insert(pospTransInfo) == 1) {
														pospTransInfo.setSysseqno(transOrderId);
														this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
														logger.info("修改订单信息");
														logger.info("" + pmsAppTransInfo);
														this.pmsAppTransInfoDao.update(pmsAppTransInfo);
														// 处理生成二维码
														this.twoDimensionCodeProcess(
																reqData,
																result,
																busInfo);

													} else {
														result.put("respCode", "0006");
														result.put("respMsg", "下单失败");
														return result;
													}
												}
											} else {
												result.put("respCode", "0006");
												result.put("respMsg", "下单失败");
												return result;
											}
										}
										break label104;
									}

									logger.info(
											"***********江苏电商*************没有查到相关费率配置：" + merchantinfo.getMobilephone());
									throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
								}

								logger.info("***********江苏电商*************欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"
										+ PaymentCodeEnum.QQCodePay.getTypeCode());
								throw new RuntimeException("交易不支付");
							}

							throw new RuntimeException("系统错误----------------当前商户非正式商户");
						}

						throw new RuntimeException();
					}
				} catch (Exception var43) {
					logger.error("****************************生成二维码错误：", var43);
					throw var43;
				}
			}

			logger.info("***********江苏电商*********************微信支付------处理完成");
			return result;
		}
	}
	/**
	 * 网关
	 * 
	 * @param result
	 * @param reqData
	 */
	private Map<String, String> gatewayhandlePay(JsdsRequestDto reqData, Map<String, String> result,
			PmsBusinessPos busInfo) throws Exception {

		logger.info("********************************网关支付------开始:{}" + reqData);

		if (this.verify(reqData, result)) {
			logger.info("****************************网关支付------签名错误");
			return result;
		} else {
			logger.info("验证当前是否已经下单");
			OriginalOrderInfo origin = new OriginalOrderInfo();
			origin.setPid(reqData.getMerchantCode());
			origin.setMerchantOrderId(reqData.getOrderNum());
			if (this.originalDao.selectByOriginal(origin) != null) {
				result.put("respCode", "0006");
				result.put("respMsg", "下单失败");
				logger.info("**********************生成支付二维码 下单失败:{}");
				return result;
			} else {
				String orderNumber = reqData.getOrderNum();
				origin.setOrderId(orderNumber);
				origin.setMerchantOrderId(reqData.getOrderNum());
				origin.setPid(reqData.getMerchantCode());
				origin.setOrderTime(reqData.getDatetime());
				NumberFormat nbf = NumberFormat.getInstance();
				DecimalFormat df =new DecimalFormat("#.##");
				nbf.setMinimumFractionDigits(2);
				Double orderAmount = Double.valueOf(reqData.getTransMoney());
				String c = df.format(orderAmount/100);
				origin.setOrderAmount(c);
				origin.setPayType(reqData.getService());
				origin.setPageUrl(reqData.getReturnUrl());
				origin.setBgUrl(reqData.getNotifyUrl());
				if (reqData.getBankCard() != null) {
					origin.setBankNo(reqData.getBankCard());
				}
				origin.setBankId(reqData.getBankCode());

				try {
					label104: {
						this.originalDao.insert(origin);
						logger.info("********************江苏电商-------------根据商户号查询");
						String e = reqData.getMerchantCode();
						PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
						merchantinfo.setMercId(e);
						List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
						if (merchantList.size() != 0 && !merchantList.isEmpty()) {
							merchantinfo = (PmsMerchantInfo) merchantList.get(0);
							String oAgentNo = merchantinfo.getoAgentNo();
							logger.info("***********江苏电商*************商户信息:" + merchantinfo);
							if (StringUtils.isBlank(oAgentNo)) {
								throw new RuntimeException("系统错误----------------o单编号为空");
							}

							if ("60".equals(merchantinfo.getMercSts())) {
								logger.info("***********江苏电商*************实际金额");
								String factAmount = reqData.getTransMoney();
								logger.info("***********江苏电商*************校验欧单金额限制");
								ResultInfo payCheckResult = this.iPublicTradeVerifyService.amountVerifyOagent(
										(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);
								if (!payCheckResult.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
											+ PaymentCodeEnum.GatewayCodePay.getTypeCode());
									throw new RuntimeException("交易不支付");
								}

								logger.info("***********江苏电商*************校验欧单模块是否开启");
								ResultInfo payCheckResult1 = this.iPublicTradeVerifyService
										.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);
								if (payCheckResult1.getErrCode().equals("0")) {
									logger.info("***********江苏电商*************校验商户模块是否开启");
									ResultInfo payCheckResult3 = this.iPublicTradeVerifyService
											.moduelVerifyMer(TradeTypeEnum.merchantCollect, e);
									if (!payCheckResult3.getErrCode().equals("0")) {
										logger.info("***********江苏电商*************商户模块限制，oAagentNo:" + oAgentNo
												+ ",payType:" + PaymentCodeEnum.GatewayCodePay.getTypeCode());
										throw new RuntimeException("交易不支付");
									}

									logger.info("***********江苏电商*************校验商户金额限制");
									HashMap paramMap = new HashMap();
									paramMap.put("mercid", e);
									paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
									paramMap.put("oAgentNo", oAgentNo);
									logger.info("***********江苏电商*************商户 商城 业务信息 ");
									Map resultMap = this.merchantMineDao.queryBusinessInfo(paramMap);
									if (resultMap != null && resultMap.size() != 0) {
										String maxTransMoney = (String) resultMap.get("MAX_AMOUNT");
										String minTransMoney = (String) resultMap.get("MIN_AMOUNT");
										if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(maxTransMoney)) == 1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额超限");
											return result;
										} else if ((new BigDecimal(factAmount))
												.compareTo(new BigDecimal(minTransMoney)) == -1) {
											result.put("respCode", "0007");
											result.put("respMsg", "金额小于最小交易金额");
											logger.info("***********江苏电商*************交易金额小于最小金额");
											return result;
										} else {
											logger.info("***********江苏电商*************组装订单数据");
											PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
											logger.info("写入欧单编号");
											pmsAppTransInfo.setoAgentNo(oAgentNo);
											pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
											pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());
											logger.info("***********江苏电商*************网购");
											pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
											pmsAppTransInfo.setMercid(merchantinfo.getMercId());
											pmsAppTransInfo
													.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());
											pmsAppTransInfo.setOrderid(orderNumber);
											pmsAppTransInfo
													.setPaymenttype(PaymentCodeEnum.GatewayCodePay.getTypeName());
											pmsAppTransInfo
													.setPaymentcode(PaymentCodeEnum.GatewayCodePay.getTypeCode());
											BigDecimal factBigDecimal = new BigDecimal(factAmount);
											BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
											pmsAppTransInfo
													.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setOrderamount(
													orderAmountBigDecimal.stripTrailingZeros().toPlainString());
											pmsAppTransInfo.setDrawMoneyType("1");
											logger.info("***********江苏电商*************插入订单信息");
											Integer insertAppTrans = Integer
													.valueOf(this.pmsAppTransInfoDao.insert(pmsAppTransInfo));
											if (insertAppTrans.intValue() == 1) {
												logger.info("***********江苏电商*************查询订单信息");
												pmsAppTransInfo = this.pmsAppTransInfoDao
														.searchOrderInfo(pmsAppTransInfo.getOrderid());
												String quickRateType = ((String) resultMap.get("QUICKRATETYPE"))
														.toString();
												logger.info("***********江苏电商*************获取o单第三方支付的费率");
												AppRateConfig appRate = new AppRateConfig();
												appRate.setRateType(quickRateType);
												appRate.setoAgentNo(oAgentNo);
												AppRateConfig appRateConfig = this.appRateConfigDao
														.getByRateTypeAndoAgentNo(appRate);
												if (appRateConfig == null) {
													throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
												}

												String isTop = appRateConfig.getIsTop();
												String rate = appRateConfig.getRate();
												String topPoundage = appRateConfig.getTopPoundage();
												String paymentAmount = pmsAppTransInfo.getFactamount();
												String minPoundageStr = appRateConfig.getBottomPoundage();
												Double minPoundage = Double.valueOf(0.0D);
												if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
														&& appRateConfig.getIsBottom().equals("1")) {
													if (StringUtils.isNotBlank(minPoundageStr)) {
														minPoundage = Double
																.valueOf(Double.parseDouble(minPoundageStr));
													} else {
														result.put("respCode", "0005");
														result.put("respMsg", "没有查到相关费率附加费");
														logger.info("***********江苏电商*************没有查到相关费率附加费（最低手续费）："
																+ merchantinfo.getMobilephone());
														return result;
													}
												} else {
													BigDecimal payAmount = null;
													BigDecimal dfactAmount = new BigDecimal(
															pmsAppTransInfo.getFactamount());
													new BigDecimal(0);
													BigDecimal b = new BigDecimal(0);
													BigDecimal f = new BigDecimal(100);
													String rateStr = "";
													BigDecimal fee;
													if ("1".equals(isTop)) {
														rateStr = rate + "-" + topPoundage;
														logger.info("***********江苏电商*************是封顶费率类型");
														fee = (new BigDecimal(rate)).multiply(dfactAmount).divide(f)
																.setScale(2, BigDecimal.ROUND_UP).multiply(f);
														if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
															logger.info(
																	"***********江苏电商*************手续费大于封顶金额，按封顶金额处理");
															payAmount = dfactAmount
																	.subtract((new BigDecimal(topPoundage)).subtract(
																			new BigDecimal(minPoundage.doubleValue())));
															fee = (new BigDecimal(topPoundage))
																	.add(new BigDecimal(minPoundage.doubleValue()));
														} else {
															logger.info("***********江苏电商*************按当前费率处理");
															rateStr = rate;
															fee.add(new BigDecimal(minPoundage.doubleValue()));
															payAmount = dfactAmount.subtract(fee);
														}
													} else {
														logger.info("***********江苏电商*************按当前费率处理");
														rateStr = rate;
														fee = (new BigDecimal(rate)).multiply(dfactAmount)
																.add(new BigDecimal(minPoundage.doubleValue()))
																.divide(f).setScale(2, BigDecimal.ROUND_UP).multiply(f);
														double paymentAmountInt = fee.doubleValue();
														if (merchantinfo.getCounter() != null) {
															double channelInfo = Double
																	.parseDouble(merchantinfo.getCounter()) * 100;
															if (paymentAmountInt < channelInfo) {
																logger.info("手续费小于商户手续费");
																result.put("respCode", "手续费小于商户手续费");
																return result;

															} else {
																b = fee;
																payAmount = dfactAmount.subtract(fee);
															}
														}
													}

													logger.info("***********江苏电商*************设置结算金额");
													pmsAppTransInfo.setPayamount(payAmount.toString());
													pmsAppTransInfo.setRate(rateStr);
													pmsAppTransInfo.setPoundage(b.toString());
													pmsAppTransInfo.setDrawMoneyType("1");
													Integer paymentAmountInt1 = Integer
															.valueOf((int) Double.parseDouble(paymentAmount));
													logger.info("***********江苏电商*************验证支付方式是否开启");
													payCheckResult = this.iPublicTradeVerifyService.totalVerify(
															paymentAmountInt1.intValue(), TradeTypeEnum.merchantCollect,
															PaymentCodeEnum.GatewayCodePay, oAgentNo,
															merchantinfo.getMercId());
													if (!payCheckResult.getErrCode().equals("0")) {
														logger.info("***********江苏电商*************交易不支持");
														throw new RuntimeException("交易不支持");
													}

													logger.info("***********江苏电商*************设置通道信息");
													ViewKyChannelInfo channelInfo1 = (ViewKyChannelInfo) AppPospContext.context
															.get("JS100669");
													logger.info("设置通道信息");
													if (channelInfo1 != null) {
														pmsAppTransInfo.setBusinessNum(channelInfo1.getBusinessnum());
														pmsAppTransInfo.setChannelNum(channelInfo1.getChannelNum());
													}

													logger.info("***********江苏电商*************查看当前交易是否已经生成了流水表");
													PospTransInfo pospTransInfo = new PospTransInfo();
													logger.info(
															"***********江苏电商*************流水表是否需要更新的标记 0 insert，1：update");
													logger.info("***********江苏电商*************生成上送流水号");

													logger.info("***********江苏电商*************不存在流水，生成一个流水");
													pospTransInfo = this.generateTransFromAppTrans(pmsAppTransInfo);
													logger.info("***********江苏电商*************设置上送流水号");
													String transOrderId = reqData.getOrderNum();
													pospTransInfo.setPospsn(transOrderId);
													pospTransInfo.setTransOrderId(transOrderId);
													if (this.pospTransInfoDAO.insert(pospTransInfo) == 1) {
														Map<String, String> params = new HashMap<String, String>();
														params.put("merchantCode", busInfo.getBusinessnum());
														params.put("terminalCode", busInfo.getPosnum());
														params.put("orderNum", transOrderId);
														params.put("transMoney", reqData.getTransMoney());
														if ("105962".equals(busInfo.getDepartmentnum())) {
															params.put("notifyUrl",
																	BaseUtil.url+"/test/qrcode/gatewayResult1.action");
														} else if ("107382".equals(busInfo.getDepartmentnum())) {
															params.put("notifyUrl",
																	BaseUtil.url+"/test/qrcode/gatewayResult.action");
														}
														params.put("returnUrl",reqData.getReturnUrl());
												
														params.put("commodityName", reqData.getMerchantName() == null? "天津畅捷支付" : reqData.getCommodityName());
														params.put("bankCode", reqData.getBankCode());
														String apply = HttpUtil.parseParams(params);
														logger.info("生成签名前的数据:" + apply);
														byte[] sign = RSAUtil.encrypt(busInfo.getKek(),
																apply.getBytes());
														logger.info("上送的签名:" + sign);
														Map<String, String> map = new HashMap<String, String>();
														map.put("groupId", busInfo.getDepartmentnum());
														map.put("service", "WGZF001");
														map.put("signType", "RSA");
														map.put("sign", RSAUtil.base64Encode(sign));
														map.put("datetime", UtilDate.getOrderNum());
														String jsonmap = HttpUtil.parseParams(map);
														logger.info("上送数据:" + jsonmap);
														String respJson = HttpURLConection.httpURLConnectionPOST(
																"http://180.96.28.8:8044/TransInterface/TransRequest",
																jsonmap);
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

																byte[] a = RSAUtil.verify(busInfo.getKek(),
																		RSAUtil.base64Decode(baseSign));

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
																	 result.put("pl_orderNum",
																	 list1[1]);

																}
																result.put("pl_url", array[2].replaceAll("pl_url=",""));
																String html = result.get("pl_url").toString();
																logger.info("页面地址:"+html);
															    //解析html页面
																Document doc = Jsoup.parse(html);
																Elements element=doc.body().getElementsByTag("form");
																List array1 = new ArrayList<>();
																 Elements ele=doc.getElementsByTag("input");
															        for(Element e1 :ele)
															        {          
															        	  if(e1.val()!=null)
															        	  {
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
															} else {

																result.put("pl_msg", "交易失败");
															}
														}
														if ("0000".equals(result.get("respCode"))) {
															pospTransInfo.setSysseqno(
																	((String) result.get("orderNum")).toString());
														}
														this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
														logger.info("修改订单信息");
														logger.info("" + pmsAppTransInfo);
														this.pmsAppTransInfoDao.update(pmsAppTransInfo);
													} else {
														result.put("respCode", "0006");
														result.put("respMsg", "下单失败");
														return result;
													}
												}
											} else {
												result.put("respCode", "0006");
												result.put("respMsg", "下单失败");
												return result;
											}
										}
										break label104;
									}

									logger.info(
											"***********江苏电商*************没有查到相关费率配置：" + merchantinfo.getMobilephone());
									throw new RuntimeException("没有查到相关费率配置，请联系客服人员");
								}

								logger.info("***********江苏电商*************欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"
										+ PaymentCodeEnum.GatewayCodePay.getTypeCode());
								throw new RuntimeException("交易不支付");
							}

							throw new RuntimeException("系统错误----------------当前商户非正式商户");
						}

						throw new RuntimeException();
					}
				} catch (Exception var43) {
					logger.error("****************************生成二维码错误：", var43);
					throw var43;
				}
			}

			logger.info("***********江苏电商*********************网关支付------处理完成");
			return result;
		}
	}

	// 实体转化String
	public String entityToString(JsdsRequestDto req) {
		HashMap<String, String> params = JsdsUtil.beanToMap(req);
		logger.info("老子进来了，看不到吗？");
		String valueStr = HttpUtil.parseParams(params);
		logger.info("老子到了，看不到吗？");
		return valueStr;
	}

	// 修改代付状态
	public synchronized int UpdateDaifu(String batchNo, String responsecode) throws Exception {

		logger.info("原始数据:" + batchNo);

		PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

		logger.info("上送的批次号:" + batchNo);

		pdf.setBatchNo(batchNo);
		pdf.setResponsecode(responsecode);
		return pmsDaifuMerchantInfoDao.update(pdf);
	}

	public synchronized Map<String, String> otherInvoke(JsdsResponseDto result) throws Exception {

		logger.info("上游返回的数据" + result);

		Map<String, String> params = new HashMap<String, String>();
		String sign1 = result.getPl_sign();
		String baseSign = URLDecoder.decode(sign1, "UTF-8");

		baseSign = baseSign.replace(" ", "+");

		byte[] a = RSAUtil.verify(
				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIRuTinaFSATJFYnjeS5LTkdZB/Q35YrFVb5J3QrTRHIOERJ6I9kC0I0Iao3epVUVw657Ib0VwOtBDUrGmma4Hbz5Ybt56W7eJEyyv/VYWFteTzJYhpUCqc+WfnXYOw9aRmSKqkzedykqblxsnrQGOsv/jjoHBHpNW5FNr161XVQIDAQAB",
				RSAUtil.base64Decode(baseSign));

		String Str = new String(a);

		logger.info("解析响应数据:" + Str);
		String[] array = Str.split("\\&");
		logger.info("拆分数据:" + array);
		String[] list = array[0].split("\\=");
		if (list[0].equals("orderNum")) {
			logger.info("合作商订单号:" + list[1]);

			params.put("orderNum", list[1]);

		}
		String[] list3 = array[1].split("\\=");
		if (list3[0].equals("pl_orderNum")) {
			logger.info("合作商订单号:" + list3[1]);

			params.put("pl_orderNum", list3[1]);

		}
		String[] list1 = array[2].split("\\=");
		if (list1[0].equals("pl_payState")) {
			logger.info("交易状态:" + list1[1]);
			params.put("pl_payState", list1[1]);

		}
		String[] list2 = array[3].split("\\=");
		if (list2[0].equals("pl_payMessage")) {
			logger.info("交易描述:" + list2[1]);
			params.put("pl_payMessage", list2[1]);
		}
		// 流水表transOrderId
		String transOrderId = params.get("orderNum");

		OriginalOrderInfo orig = getOriginOrderInfo(transOrderId);

		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		logger.info("流水表信息" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		logger.info("订单表信息" + pmsAppTransInfo);
		// 查询结果成功
		if ("4".equals(params.get("pl_payState").toString())) {
//			Calendar cal1 = Calendar.getInstance();
//			TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));
//			java.text.SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
//
//			if (sdf.parse(sdf.format(cal1.getTime())).getTime() > sdf.parse("03:00:00").getTime()
//					&& sdf.parse(sdf.format(cal1.getTime())).getTime() < sdf.parse("22:30:00").getTime()) {
//				logger.info("D0订单号:" + transOrderId);
//
//				UpdatePmsMerchantInfo(orig);
//			}
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// log.info("修改余额");
				// 修改余额
				logger.info(pmsAppTransInfo);
				// updateMerchantBanlance(pmsAppTransInfo);
				// 更新流水表
				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("5".equals(params.get("pl_payState").toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("2".equals(params.get("pl_payState").toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("20");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}

		return params;

	}

	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {

		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		if(transInfo!=null) {
			String oderId = transInfo.getOrderId();
			logger.info("根据上送订单号  查询商户上送原始信息");
			original = originalDao.getOriginalOrderInfoByOrderid(oderId);
			if(original!=null) {
				return original;
			}
		}
		return original;
	}


	public synchronized int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo) throws Exception {
		logger.info("代付实时填金:" + JSON.toJSON(originalInfo));
		DecimalFormat df = new DecimalFormat("#.0");
		PmsMerchantInfo pmsMerchantInfo = new PmsMerchantInfo();
	
		logger.info("代付商户号:" + originalInfo.getPid());
		PmsMerchantInfo merchantInfo = pmsMerchantInfoDao.selectMercByMercId(originalInfo.getPid());
		logger.info("代付订单号:" + originalInfo.getOrderId());
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(originalInfo.getOrderId());
		PmsDaifuMerchantInfo df1 = new PmsDaifuMerchantInfo();
		df1.setBatchNo(originalInfo.getOrderId());
		logger.info("查询的批次号:" + df);
		PmsDaifuMerchantInfo daifu = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(df1);
		if (daifu == null) {
			if ("0".equals(merchantInfo.getOpenPay())) {
				// 手续费
				logger.info("*************非标准代付接口******************");
				logger.info("手续费" + pmsAppTransInfo.getPoundage());
				Double poundage = Double.parseDouble(pmsAppTransInfo.getPoundage());
				logger.info("手续费" + poundage);
				String position = merchantInfo.getPosition();
				logger.info("额度" + position);
				Double amount = Double.parseDouble(originalInfo.getOrderAmount().replaceAll(",", "").toString());
				logger.info("金额" + amount);
				Double ds = Double.parseDouble(position);
				logger.info("额度1" + ds);
				Double factamount = (amount * 100 - poundage) * 0.8;
				logger.info("实际金额" + factamount);
				Double dd = factamount + ds;
				logger.info("额度" + dd);
				logger.info("来了1---------");
				pmsMerchantInfo.setMercId(originalInfo.getPid());
				pmsMerchantInfo.setPosition(df.format(dd));
				PmsDaifuMerchantInfo pmsDaifuMerchantInfo = new PmsDaifuMerchantInfo();
				// 商户号
				pmsDaifuMerchantInfo.setMercId(originalInfo.getPid());
				// 订单号
				pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
				// 总金额
				pmsDaifuMerchantInfo.setAmount(
						(Double.parseDouble(originalInfo.getOrderAmount().replaceAll(",", "").toString())) + "");
				// 状态
				pmsDaifuMerchantInfo.setResponsecode("00");
				// 备注
				pmsDaifuMerchantInfo.setRemarks("D0");
				// 记录描述
				pmsDaifuMerchantInfo.setRecordDescription("订单号:" + originalInfo.getOrderId() + "交易金额:"
						+ originalInfo.getOrderAmount().replaceAll(",", "").toString());
				// 交易类型
				pmsDaifuMerchantInfo.setTransactionType(pmsAppTransInfo.getPaymenttype());
				// 发生额
				pmsDaifuMerchantInfo.setPayamount(
						(Double.parseDouble(originalInfo.getOrderAmount().replaceAll(",", "").toString())) + "");
				// 账户余额
				pmsDaifuMerchantInfo.setPosition(df.format(dd));
				// 手续费
				pmsDaifuMerchantInfo.setPayCounter(poundage / 100 + "");
				pmsDaifuMerchantInfo.setOagentno("100333");
				logger.info("来了2---------");
				// 交易时间
				// pmsDaifuMerchantInfo.setCreationdate(new
				// SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				int s = pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
				logger.info("---s:" + s);
				logger.info("来了3---------");
				int i = pmsMerchantInfoDao.UpdatePmsMerchantInfo(pmsMerchantInfo);
				logger.info("---i:" + i);
				return i;
			} else {
				logger.info("此商户未开通代付！！");
			}
		}
		return 0;
	}

	public synchronized int UpdatePmsMerchantInfo449(OriginalOrderInfo originalInfo) throws Exception {
		logger.info("代付实时填金:" + JSON.toJSON(originalInfo));
		DecimalFormat df = new DecimalFormat("#.0");
		PmsMerchantInfo pmsMerchantInfo = new PmsMerchantInfo();
		
		logger.info("代付商户号:" + originalInfo.getPid());
		PmsMerchantInfo merchantInfo = pmsMerchantInfoDao.selectMercByMercId(originalInfo.getPid());
		logger.info("代付订单号:" + originalInfo.getOrderId());
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(originalInfo.getOrderId());
		PmsDaifuMerchantInfo df1 = new PmsDaifuMerchantInfo();
		df1.setBatchNo(originalInfo.getOrderId());
		logger.info("查询的批次号:" + df);
		PmsDaifuMerchantInfo daifu = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(df1);
		if (daifu == null) {
			if ("0".equals(merchantInfo.getOpenPay())) {
				// 手续费
				logger.info("*************标准代付接口******************");
				logger.info("手续费" + pmsAppTransInfo.getPoundage());
				Double poundage = Double.parseDouble(pmsAppTransInfo.getPoundage());
				logger.info("手续费" + poundage);
				String position = merchantInfo.getPosition();
				logger.info("额度" + position);
				Double amount = Double.parseDouble(originalInfo.getOrderAmount().replaceAll(",", "").toString());
				logger.info("金额" + amount);
				Double ds = Double.parseDouble(position);
				logger.info("额度1" + ds);
				Double factamount = (amount * 100 - poundage);
				logger.info("实际金额" + factamount);
				Double dd = factamount + ds;
				logger.info("额度" + dd);
				logger.info("来了1---------");
				PmsDaifuMerchantInfo pmsDaifuMerchantInfo = new PmsDaifuMerchantInfo();
				pmsMerchantInfo.setMercId(originalInfo.getPid());
				pmsMerchantInfo.setPosition(dd.toString());
				// 商户号
				pmsDaifuMerchantInfo.setMercId(originalInfo.getPid());
				// 订单号
				pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
				// 总金额
				pmsDaifuMerchantInfo.setAmount(
						(Double.parseDouble(originalInfo.getOrderAmount().replaceAll(",", "").toString())) + "");
				// 状态
				pmsDaifuMerchantInfo.setResponsecode("00");
				// 备注
				pmsDaifuMerchantInfo.setRemarks("D0");
				// 记录描述
				pmsDaifuMerchantInfo.setRecordDescription("订单号:" + originalInfo.getOrderId() + "交易金额:"
						+ originalInfo.getOrderAmount().replaceAll(",", "").toString());
				// 交易类型
				pmsDaifuMerchantInfo.setTransactionType(pmsAppTransInfo.getPaymenttype());
				// 发生额
				pmsDaifuMerchantInfo.setPayamount(
						(Double.parseDouble(originalInfo.getOrderAmount().replaceAll(",", "").toString())) + "");
				// 账户余额
				pmsDaifuMerchantInfo.setPosition(df.format(dd));
				// 手续费
				pmsDaifuMerchantInfo.setPayCounter(poundage / 100 + "");
				pmsDaifuMerchantInfo.setOagentno("100333");
				logger.info("来了2---------");
				// 交易时间
				// pmsDaifuMerchantInfo.setCreationdate(new
				// SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				int s = pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
				logger.info("---s:" + s);
				logger.info("来了3---------");
				int i = pmsMerchantInfoDao.UpdatePmsMerchantInfo(pmsMerchantInfo);
				logger.info("---i:" + i);
				return i;
			} else {
				logger.info("此商户未开通代付！！");
			}
		}
		return 0;
	}

	public Map<String, String> Register(CustomerRegister reqData) throws Exception {
		// TODO Auto-generated method stub
		Map<String, String> result = new HashMap<String, String>();
		logger.info("验证当前商户号是否已经注册");
		OriginalOrderInfo origin = new OriginalOrderInfo();
		origin.setPid(reqData.getMerchantCode());
		origin.setMerchantOrderId(reqData.getIdentidy());
		if (this.originalDao.selectByOriginal(origin) != null) {
			result.put("respCode", "0006");
			result.put("respMsg", "商户已注册过");
			logger.info("**********************该商户已经注册过");
		} else {
			String orderNumber = UtilMethod.getOrderid("188");
			origin.setOrderId(orderNumber);
			origin.setMerchantOrderId(reqData.getIdentidy());
			origin.setPid(reqData.getMerchantCode());
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = format.format(date);
			origin.setOrderTime(time);
			origin.setPayType(reqData.getPayType());
			if ("1".equals(reqData.getPayType())) {
				origin.setBankId(reqData.getCardNo());
				origin.setBankNo(reqData.getPmsBankNo());
				origin.setByUser(reqData.getRealName());
				origin.setProcdutDesc(reqData.getBankCardName());
				origin.setProcdutNum(reqData.getCertNo());
			}
			origin.setBgUrl(reqData.getUrl());
			origin.setProcdutName(reqData.getProduct());
			String e = reqData.getMerchantCode();
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(e);
			List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
			if (merchantList.size() != 0 && !merchantList.isEmpty()) {
				merchantinfo = (PmsMerchantInfo) merchantList.get(0);
				String oAgentNo = merchantinfo.getoAgentNo();
				logger.info("***********江苏电商*************商户信息:" + merchantinfo);
				if (StringUtils.isBlank(oAgentNo)) {
					throw new RuntimeException("系统错误----------------o单编号为空");
				}

				if ("60".equals(merchantinfo.getMercSts())) {

					int num = this.originalDao.insert(origin);
					if (num == 1) {
						result.put("url", reqData.getUrl());
					}

				} else {
					// 请求参数为空
					logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
					result.put("01", "还没有进行实名认证，请先去进行实名认证，或者等待客服审核！");
					return result;
				}
			}

		}
		return result;
	}

	public OriginalOrderInfo selectKeyUrl(CustomerRegister reqeustInfo) throws Exception {
		OriginalOrderInfo origin = new OriginalOrderInfo();
		origin.setPid(reqeustInfo.getMerchantCode());
		origin.setMerchantOrderId(reqeustInfo.getIdentidy());
		return originalDao.selectByOriginal(origin);
	}

	public synchronized int add(JsdsRequestDto reqData, PmsMerchantInfo merchantinfo, Map<String, String> result)
			throws Exception {
		logger.info("进来添加失败余额了");
		String type = "";
		String positions = "";
		int iii = 0;
		if (reqData.getType().equals("0")) {
			type = "D0";
			positions = merchantinfo.getPosition();
		} else {
			type = "T1";
			positions = merchantinfo.getPositionT1();
		}

		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		String factAmount = reqData.getTransMoney();
		model.setMercId(reqData.getMerchantCode());
		model.setCount("1");
		model.setIdentity(reqData.getOrderNum());
		model.setBatchNo(reqData.getOrderNum());
		model.setAmount(Double.parseDouble(factAmount) / 100 + "");
		model.setCardno(reqData.getAccountName());
		model.setRealname(reqData.getBankName());
		model.setPayamount("-" + Double.parseDouble(factAmount) / 100);
		model.setPmsbankno(reqData.getBankLinked());
		model.setTransactionType("代付");
		model.setPosition(String.valueOf(positions));
		model.setRemarks(type);
		model.setRecordDescription("批次号:" + reqData.getOrderNum() + "错误原因:" + result.get("respMsg"));
		model.setResponsecode("01");
		model.setOagentno("100333");
		model.setPayCounter(new BigDecimal(merchantinfo.getPoundage()).doubleValue() + "");
		PmsDaifuMerchantInfo daifu = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model);
		if (daifu == null) {
			iii = pmsDaifuMerchantInfoDao.insert(model);
			logger.info("iii:" + iii);
		}
		return iii;
		
	}
	
	public synchronized void updateSelect(JsdsRequestDto reqData, Map<String, String> result,PmsMerchantInfo merchantinfo) throws Exception{
		logger.info("江苏代付进来了~~~");
		logger.info("------------------江苏代付查询reqData参数----------------"+JSON.toJSON(reqData));
		logger.info("----------------江苏代付查询merchantinfo参数----------------"+JSON.toJSON(merchantinfo));
		ThreadPool.executor(new JsThread(daifuMerchantInfoService, reqData, this, pmsDaifuMerchantInfoDao, pmsMerchantInfoDao, merchantinfo));
		result.put("result", "0001");
		result.put("respMsg", "支付未知,请看平台");
		result.put("pl_service", "cj006");

	}
	/**
	 * 
	 * @Description 处理生成二维码
	 * @author Administrator
	 * @param reqeustInfo
	 * @param result
	 * @param pospTransInfo
	 * @throws Exception
	 */
	private void twoDimensionCodeProcess(JsdsRequestDto reqData, Map<String, String> result, PmsBusinessPos busInfo)
			throws Exception {
		

		logger.info("下游上送的参数:"+reqData);
		logger.info("上游通道信息:"+busInfo);
		Map<String, String> params = new HashMap<String, String>();
		String transOrderId = reqData.getOrderNum();
		logger.info("下游上送的订单号:"+transOrderId);
		params.put("merchantCode", busInfo.getBusinessnum());//JsdsUtils.merchantCode
		params.put("terminalCode", JsdsUtils.terminalCode);
		params.put("orderNum", transOrderId);
		params.put("transMoney", reqData.getTransMoney());
		params.put("notifyUrl",JsdsUtils.notifyUrl);
		String[] stry = { "300fa33635394cf1bb9fcbfebac32259",
				"625f2f03d82b43528cef7d5d9a89a59e",
				"855ea3ecafff457a826fc83520f460d6",
				"6513cad27fd3497ea4a4a0cd643dbb1a",
				"88d30464873847d297b89532486aa38b",
				"93145a745fa24cc2a7e453c5f4dea00f",
				"881850857dac4e07a50b8443e3664123",
				"bf20c371f4c6410d8a0010b70d38d9f1",
				"0215445411e24e9a9da749d151ab6a1d",
				"36656655db6340fdae04be245c179845",
				"4d19ccd88b8e486593e3907b5a82d1cc",
				"7468186ed26144a9876a3d31514f9c41",
				"c4488bfe30f44e9ea051d657012077f4",
				"b2e7b8d93ba944e38da92ad0046ad57f",
				"18dd081ce2ae4a5185fb8ad6eefe6cad",
				"a314e3afd0c240cb8eabb443a4dedb00",
				"d102780837da423782566af102635c3b",
				"b73cbedb3de346c0960eac12b04d7123",
				"7d8ada9f4b9f4c50958186099d4108eb",
				"bb39f8052bc6482591909fdf18852dbc" };
		String[] str = { "20100024000751", "20110124011752",
				"20120224022753", "20130324033754", "20140424044755",
				"20150524055756", "20160624066757", "20170724077758",
				"20180824088759", "20190924099750", "20011024012749",
				"20021124023748", "20031224034747", "20041324045746",
				"20051424056745", "20061524067744", "20071624078743",
				"20081724090742", "20091824090741", "20201924001740" };
		String terminalNum = "";
		String merchantNum = "";
		Random random = new Random();
		int num = random.nextInt(20);
		for (int i = 0; i < 20; i++) {
			if (num == i) {
				terminalNum = stry[i];
				merchantNum = str[i];
				break;
			}
		}
		logger.info(
				"终端号:" + terminalNum + "\t" + "门店编号:" + merchantNum);
		params.put("merchantName", reqData.getMerchantName() == null
				? "天津畅捷支付" : reqData.getMerchantName());
		params.put("merchantNum", merchantNum);
		params.put("terminalNum", terminalNum);
		String apply = HttpUtil.parseParams(params);
		logger.info("生成签名前的数据:" + apply);
		byte[] sign = RSAUtil.encrypt(busInfo.getKek(),
				apply.getBytes());
		logger.info("上送的签名:" + sign);
		Map<String, String> map = new HashMap<String, String>();
		map.put("groupId",JsdsUtils.groupId);
		switch (reqData.getService() == null ? reqData.getPl_service() : reqData.getService()) {
		case "cj001":
			logger.info("************************江苏电商----支付宝二维码----处理 开始");
			map.put("service", "SMZF005");
			break;
		case "cj002":
			logger.info("************************江苏电商----微信二维码----处理 开始");
			map.put("service", "SMZF004");
			break;
		case "cj005":
			logger.info("************************江苏电商----QQ钱包----处理 开始");
			map.put("service", "SMZF016");
			break;
		case "cj008":
			logger.info("************************江苏电商----京东----处理 开始");
			map.put("service", "SMZF021");
			break;
		case "cj009":
			logger.info("************************江苏电商----京东H5----处理 开始");
			map.put("service", "SMZF025");
			break;
		default:
			break;
		}
		
		map.put("signType", "RSA");
		map.put("sign", RSAUtil.base64Encode(sign));
		map.put("datetime", UtilDate.getOrderNum());
		logger.info("map:"+map);
		String jsonmap = HttpUtil.parseParams(map);
		logger.info("上送数据:" + jsonmap);
		String respJson = HttpURLConection.httpURLConnectionPOST(
				JsdsUtils.url,//http://121.41.121.164:8044/TransInterface/TransRequest
				jsonmap);
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
				
				if("cj005".equals(reqData.getService()))
				{
					String sign1 = result.get("sign");
					String baseSign = URLDecoder.decode(sign1, "UTF-8");

					baseSign = baseSign.replace(" ", "+");

					byte[] a = RSAUtil.verify(busInfo.getKek(),
							RSAUtil.base64Decode(baseSign));

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
						 result.put("pl_orderNum",
						 list1[1]);

					}
					String list2 = array[2].replaceAll("pl_url=", "");
					logger.info("上游返回的url:"+list2);
					logger.info("URL:" + URLDecoder.decode(list2, "UTF-8") );
					result.put("pl_url", URLDecoder.decode(list2, "UTF-8"));
				}else
				{
					String sign1 = result.get("sign");
					String baseSign = URLDecoder.decode(sign1, "UTF-8");

					baseSign = baseSign.replace(" ", "+");

					byte[] a = RSAUtil.verify(busInfo.getKek(),
							RSAUtil.base64Decode(baseSign));

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
						 result.put("pl_orderNum",
						 list1[1]);

					}
					String list2 = array[2].replaceAll("pl_url=", "");
					logger.info("URL:" + list2);
					result.put("pl_url", list2);
				}

				
			} else {

				result.put("pl_msg", "交易失败");
			}
			if (result.get("respCode").equals("0000")) {
				// 启线程查询订单状态
				ThreadPool.executor(new JsPayThread(this,pmsAppTransInfoDao,pospTransInfoDAO,reqData,cmckeyDao));
			}else{
				logger.info("生成二维码失败");
			}
		}	
	}

	@Override
	public Map<String, String> gatewayNofity(JsdsResponseDto result) throws Exception {
		// TODO Auto-generated method stub


		logger.info("上游返回的数据" + result);

		Map<String, String> params = new HashMap<String, String>();
		String sign1 = result.getPl_sign();
		String baseSign = URLDecoder.decode(sign1, "UTF-8");

		baseSign = baseSign.replace(" ", "+");

		byte[] a = RSAUtil.verify(
				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIRuTinaFSATJFYnjeS5LTkdZB/Q35YrFVb5J3QrTRHIOERJ6I9kC0I0Iao3epVUVw657Ib0VwOtBDUrGmma4Hbz5Ybt56W7eJEyyv/VYWFteTzJYhpUCqc+WfnXYOw9aRmSKqkzedykqblxsnrQGOsv/jjoHBHpNW5FNr161XVQIDAQAB",
				RSAUtil.base64Decode(baseSign));

		String Str = new String(a);

		logger.info("解析响应数据:" + Str);
		String[] array = Str.split("\\&");
		logger.info("拆分数据:" + array);
		String[] list = array[0].split("\\=");
		if (list[0].equals("orderNum")) {
			logger.info("合作商订单号:" + list[1]);

			params.put("orderNum", list[1]);

		}
		String[] list3 = array[1].split("\\=");
		if (list3[0].equals("pl_orderNum")) {
			logger.info("合作商订单号:" + list3[1]);

			params.put("pl_orderNum", list3[1]);

		}
		String[] list1 = array[2].split("\\=");
		if (list1[0].equals("pl_payState")) {
			logger.info("交易状态:" + list1[1]);
			params.put("pl_payState", list1[1]);

		}
		String[] list2 = array[3].split("\\=");
		if (list2[0].equals("pl_payMessage")) {
			logger.info("交易描述:" + list2[1]);
			params.put("pl_payMessage", list2[1]);
		}
		// 流水表transOrderId
		String transOrderId = params.get("orderNum");

		OriginalOrderInfo orig = getOriginOrderInfo(transOrderId);

		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		logger.info("流水表信息" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		logger.info("订单表信息" + pmsAppTransInfo);
		// 查询结果成功
		if ("4".equals(params.get("pl_payState").toString())) {
			Calendar cal1 = Calendar.getInstance();
			TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));
			java.text.SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");

			if (sdf.parse(sdf.format(cal1.getTime())).getTime() > sdf.parse("03:00:00").getTime()
					&& sdf.parse(sdf.format(cal1.getTime())).getTime() < sdf.parse("22:30:00").getTime()) {
				logger.info("D0订单号:" + transOrderId);

				UpdatePmsMerchantInfo(orig);
			}
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// log.info("修改余额");
				// 修改余额
				logger.info(pmsAppTransInfo);
				// updateMerchantBanlance(pmsAppTransInfo);
				// 更新流水表
				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("5".equals(params.get("pl_payState").toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("2".equals(params.get("pl_payState").toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("20");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}

		return params;

	
	}

	@Override
	public Map<String, String> gatewayNofity1(JsdsResponseDto result) throws Exception {
		// TODO Auto-generated method stub

		logger.info("上游返回的数据" + result);

		Map<String, String> params = new HashMap<String, String>();
		String sign1 = result.getPl_sign();
		String baseSign = URLDecoder.decode(sign1, "UTF-8");

		baseSign = baseSign.replace(" ", "+");

		byte[] a = RSAUtil.verify(
				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgrvWMXt4cQ6BBBNloWhM6TYX39EFFJc981G0NZbM2knD9FZI4XIaX9PFDhL9CYYpH6vHsfEIbIU8UquTT/7dxPTvwDNXpwqXsBavqoY8wgkELxpYJwSoCnDJbLq107z5KF0EaPqAXHi3sdFAvuCjcxq5n+ooNTkF9tBA9sW4KcQIDAQAB",
				RSAUtil.base64Decode(baseSign));

		String Str = new String(a);

		logger.info("解析响应数据:" + Str);
		String[] array = Str.split("\\&");
		logger.info("拆分数据:" + array);
		String[] list = array[0].split("\\=");
		if (list[0].equals("orderNum")) {
			logger.info("合作商订单号:" + list[1]);

			params.put("orderNum", list[1]);

		}
		String[] list3 = array[1].split("\\=");
		if (list3[0].equals("pl_orderNum")) {
			logger.info("合作商订单号:" + list3[1]);

			params.put("pl_orderNum", list3[1]);

		}
		String[] list1 = array[2].split("\\=");
		if (list1[0].equals("pl_payState")) {
			logger.info("交易状态:" + list1[1]);
			params.put("pl_payState", list1[1]);

		}
		String[] list2 = array[3].split("\\=");
		if (list2[0].equals("pl_payMessage")) {
			logger.info("交易描述:" + list2[1]);
			params.put("pl_payMessage", list2[1]);
		}
		// 流水表transOrderId
		String transOrderId = params.get("orderNum");

		OriginalOrderInfo orig = getOriginOrderInfo(transOrderId);

		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		logger.info("流水表信息" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		logger.info("订单表信息" + pmsAppTransInfo);
		// 查询结果成功
		if ("4".equals(params.get("pl_payState").toString())) {
			Calendar cal1 = Calendar.getInstance();
			TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));
			java.text.SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");

			if (sdf.parse(sdf.format(cal1.getTime())).getTime() > sdf.parse("03:00:00").getTime()
					&& sdf.parse(sdf.format(cal1.getTime())).getTime() < sdf.parse("22:30:00").getTime()) {
				logger.info("D0订单号:" + transOrderId);

				UpdatePmsMerchantInfo449(orig);
			}
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// log.info("修改余额");
				// 修改余额
				logger.info(pmsAppTransInfo);
				// updateMerchantBanlance(pmsAppTransInfo);
				// 更新流水表
				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("5".equals(params.get("pl_payState").toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("2".equals(params.get("pl_payState").toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(params.get("pl_payState").toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("20");
				pospTransInfo.setPospsn(result.getPl_orderNum());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
		return params;

	
	}
}

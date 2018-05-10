package xdt.service.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.innovatepay.merchsdk.DefaultChinaInPayClient;
import com.innovatepay.merchsdk.request.ChinaInPayQuickPayRequest;
import com.innovatepay.merchsdk.request.ChinaInPayRequest;
import com.kspay.AESUtil;
import com.kspay.MD5Util;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayBankInfoDao;
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
import xdt.dto.QuickpayCheckLocalCardRequestDTO;
import xdt.dto.QuickpayCheckLocalCardResponseDTO;
import xdt.dto.code.SmsBalanceRequest;
import xdt.dto.code.SmsBalanceResponse;
import xdt.dto.code.SmsSendRequest;
import xdt.dto.code.SmsSendResponse;
import xdt.dto.code.SmsVariableRequest;
import xdt.dto.code.SmsVariableResponse;
import xdt.dto.cx.CXUtil;
import xdt.dto.hlb.Disguiser;
import xdt.dto.hlb.HLBUtil;
import xdt.dto.hlb.HttpClientService;
import xdt.dto.hlb.MyBeanUtils;
import xdt.dto.hm.AesEncryption;
import xdt.dto.hm.HMUtil;
import xdt.dto.hm.HttpsUtil;
import xdt.dto.hm.SHA256Util;
import xdt.dto.hm.TimeUtil;
import xdt.dto.lhzf.LhzfUtil;
import xdt.dto.lhzf.MerchantApiUtil;
import xdt.dto.mb.DemoBase;
import xdt.dto.mb.MBUtil;
import xdt.dto.quickPay.entity.ConsumeRequestEntity;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.entity.MessageRequestEntity;
import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.dto.ys.HttpUtils;
import xdt.dto.ys.SwpHashUtil;
import xdt.dto.ys.YSUtil;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PayBankInfo;
import xdt.model.PayCmmtufit;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospTransInfo;
import xdt.model.QuickpayCardRecord;
import xdt.model.ResultInfo;
import xdt.model.SessionInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.hf.comm.SampleConstant;
import xdt.quickpay.hf.util.EffersonPayService;
import xdt.quickpay.hf.util.PlatBase64Utils;
import xdt.quickpay.hf.util.PlatKeyGenerator;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.quickpay.shyb.entity.ShybQuickCallbackEntity;
import xdt.quickpay.shyb.entity.ShybQuickPayRequestEntity;
import xdt.quickpay.shyb.entity.ShybQuickRequestEntity;
import xdt.quickpay.shyb.entity.ShybUpdateRateRequestEntity;
import xdt.quickpay.shyb.util.Digest;
import xdt.quickpay.shyb.util.FeeSetPartsBuilder;
import xdt.quickpay.shyb.util.ReceviePartsBuiler;
import xdt.quickpay.shyb.util.RegisterPartsBuilder;
import xdt.quickpay.syys.HttpClientUtil;
import xdt.quickpay.syys.PayCore;
import xdt.quickpay.yb.util.YeepayService;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IQuickPayService;
import xdt.service.IShybQuickPayService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.servlet.AppPospContext;
import xdt.util.ChuangLanSmsUtil;
import xdt.util.EncodeUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class ShybQuickpayServiceImpl extends BaseServiceImpl implements IShybQuickPayService {

	private Logger logger = Logger.getLogger(ShybQuickpayServiceImpl.class);

	@Resource
	private IQuickpayRecordDao iQuickpayRecordDao;
	@Resource
	private IPayCmmtufitDao iPayCmmtufitDao;

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	private IPmsGoodsDao pmsGoodsDao;

	@Resource
	private IMerchantMineDao merchantMineDao;
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
	private MobaoPayHandel mobaoPayHandel;
	@Resource
	private IQuickpayRecordDao quickpayRecordDao;
	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;
	@Resource
	private IMerchantCollectMoneyService merchantCollectMoneyService;
	@Resource
	private IPmsMessageService pmsMessageService;
	@Resource
	IPmsMessageDao pmsMessageDao;
	@Resource
	private IAppRateConfigDao appRateConfigDao;
	@Resource
	private IQuickpayPreRecordDao quickpayPreRecordDao;
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;// 代付
	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	@Resource
	private IPayBankInfoDao payBankInfoDao;
	@Resource
	public PmsWeixinMerchartInfoService weixinService;

	/**
	 * 处理上海易宝快捷进件
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> updateHandle(ShybQuickRequestEntity originalinfo) throws Exception {

		Map<String, String> retMap = new HashMap<String, String>();
		// 商户号
		String mercId = originalinfo.getCustomerNumber();
		// 商户订单号
		logger.info("******************根据商户号查询");

		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(mercId);

		String MainCustomerNumber = busInfo.getBusinessnum();

		logger.info("上海易宝上送的商户号:" + MainCustomerNumber);
		String key = busInfo.getKek();

		logger.info("上海易宝上送的密钥:" + key);
		PostMethod postMethod = new PostMethod("https://skb.yeepay.com/skb-app/register.action");
		HttpClient client = new HttpClient();

		// String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
		// 业务号（2位）+业务细分（1位）+时间戳（13位）
		// 总共16位

		// 根据商户号查询

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				logger.error("参数错误!");
				retMap.put("v_code", "04");
				retMap.put("v_msg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {

				StringBuffer signature = new StringBuffer();
				signature.append(MainCustomerNumber == null ? "" : MainCustomerNumber)
						.append(originalinfo.getRequestId() == null ? "" : originalinfo.getRequestId())
						.append(originalinfo.getCustomerType() == null ? "" : originalinfo.getCustomerType())
						.append(originalinfo.getBusinessLicence() == null ? "" : originalinfo.getBusinessLicence())
						.append(originalinfo.getBindMobile() == null ? "" : originalinfo.getBindMobile())
						.append(originalinfo.getSignedName() == null ? "" : originalinfo.getSignedName())
						.append(originalinfo.getLinkMan() == null ? "" : originalinfo.getLinkMan())
						.append(originalinfo.getIdCard() == null ? "" : originalinfo.getIdCard())
						.append(originalinfo.getLegalPerson() == null ? "" : originalinfo.getLegalPerson())
						.append(originalinfo.getMinSettleAmount() == null ? "" : originalinfo.getMinSettleAmount())
						.append(originalinfo.getRiskReserveDay() == null ? "" : originalinfo.getRiskReserveDay())
						.append(originalinfo.getBankAccountNumber() == null ? "" : originalinfo.getBankAccountNumber())
						.append(originalinfo.getBankName() == null ? "" : originalinfo.getBankName())
						.append(originalinfo.getAccountName() == null ? "" : originalinfo.getAccountName())
						.append(originalinfo.getManualSettle() == null ? "" : originalinfo.getManualSettle());

				logger.info("上海易宝生成签名前的数据：" + signature.toString());
				String hmac = Digest.hmacSign(signature.toString(), key);
				logger.info("上海易宝生成的签名：" + hmac);

				Part[] parts = new RegisterPartsBuilder().setHmac(hmac).setMainCustomerNumber(MainCustomerNumber)
						.setMailStr(originalinfo.getMailStr()).setRequestId(originalinfo.getRequestId())
						.setCustomerType(originalinfo.getCustomerType())
						.setBusinessLicence(originalinfo.getBusinessLicence())
						.setBindMobile(originalinfo.getBindMobile()).setSignedName(originalinfo.getSignedName())
						.setLinkMan(originalinfo.getLinkMan()).setIdCard(originalinfo.getIdCard())
						.setLegalPerson(originalinfo.getLegalPerson())
						.setMinSettleAmount(originalinfo.getMinSettleAmount())
						.setRiskReserveDay(originalinfo.getRiskReserveDay())
						.setBankAccountNumber(originalinfo.getBankAccountNumber())
						.setBankName(originalinfo.getBankName()).setBankaccounttype(originalinfo.getBankAccountType())
						.setAccountName(originalinfo.getAccountName()).setBankCardPhoto(originalinfo.getBankCardPhoto())
						.setIdCardBackPhoto(originalinfo.getIdCardBackPhoto())
						.setIdCardPhoto(originalinfo.getIdCardPhoto()).setPersonPhoto(originalinfo.getPersonPhoto())
						.setManualSettle(originalinfo.getManualSettle()).setAuditStatus(originalinfo.getAuditStatus())
						// .setElectronicAgreement(elecAgreement)
						.generateParams();

				postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));

				logger.info("上海易宝发送数据:" + postMethod.toString());

				int status = client.executeMethod(postMethod);
				logger.info("上海易宝返回的状态码:" + status);
				logger.info("上海易宝返回的响应数据:" + postMethod.getResponseBodyAsString());
				net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(postMethod.getResponseBodyAsString());
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					key = (String) it.next();
					if (key.equals("code")) {

						String value = ob.getString(key);
						retMap.put("code", value);
					}
					if (key.equals("customerNumber")) {

						String value = ob.getString(key);
						retMap.put("subContractId", value);
					}
					if (key.equals("message")) {

						String value = ob.getString(key);
						retMap.put("message", value);
					}
				}

			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("v_code", "16");
				retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
				return retMap;
			}
		} else {
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("v_code", "16");
			retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服\"");
			return retMap;
		}
		return retMap;
	}
	/**
	 * 处理上海易宝快捷修改费率
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> updateRate(ShybUpdateRateRequestEntity originalinfo) throws Exception {

		Map<String, String> retMap = new HashMap<String, String>();
		// 商户号
		String mercId = originalinfo.getCustomerNumber();
		// 商户订单号
		logger.info("******************根据商户号查询");

		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(mercId);

		String MainCustomerNumber = busInfo.getBusinessnum();

		logger.info("上海易宝上送的商户号:" + MainCustomerNumber);
		String key = busInfo.getKek();

		logger.info("上海易宝上送的密钥:" + key);
		PostMethod postMethod = new PostMethod("https://skb.yeepay.com/skb-app/feeSetApi.action");
		HttpClient client = new HttpClient();

		// String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
		// 业务号（2位）+业务细分（1位）+时间戳（13位）
		// 总共16位

		// 根据商户号查询

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				logger.error("参数错误!");
				retMap.put("v_code", "04");
				retMap.put("v_msg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {

				StringBuffer signature = new StringBuffer();
				signature.append(originalinfo.getSubContractId() == null ? "" : originalinfo.getSubContractId())
						.append(MainCustomerNumber == null ? "" : MainCustomerNumber)
						.append(originalinfo.getProductType() == null ? "" : originalinfo.getProductType())
						.append(originalinfo.getRate() == null ? "" : originalinfo.getRate());
				logger.info("上海易宝生成签名前的数据：" + signature.toString());
				String hmac = Digest.hmacSign(signature.toString(), key);
				logger.info("上海易宝生成的签名：" + hmac);

				Part[] parts = new FeeSetPartsBuilder().setCustomerNumber(originalinfo.getSubContractId())
						.setGroupCustomerNumber(MainCustomerNumber).setProductType(originalinfo.getProductType())
						.setHmac(hmac).setRate(originalinfo.getRate()).generateParams();

				postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));

				logger.info("上海易宝发送数据:" + postMethod.toString());

				int status = client.executeMethod(postMethod);
				logger.info("上海易宝返回的状态码:" + status);
				logger.info("上海易宝返回的响应数据:" + postMethod.getResponseBodyAsString());
				net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(postMethod.getResponseBodyAsString());
				Iterator it = ob.keys();
				Map<String, String> map = new HashMap<>();
				while (it.hasNext()) {
					key = (String) it.next();
					if (key.equals("code")) {

						String value = ob.getString(key);
						retMap.put("code", value);
					}
					if (key.equals("customerNumber")) {

						String value = ob.getString(key);
						retMap.put("customerNumber", value);
					}
					if (key.equals("message")) {

						String value = ob.getString(key);
						retMap.put("message", value);
					}
				}

			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("v_code", "16");
				retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
				return retMap;
			}
		} else {
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("v_code", "16");
			retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服\"");
			return retMap;
		}
		return retMap;
	}

	/**
	 * 处理上海易宝快捷支付
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> payHandle(ShybQuickPayRequestEntity originalinfo) throws Exception {
		Map<String, String> retMap = new HashMap<String, String>();
		// 商户号
		String merchId = originalinfo.getCustomerNumber();
		// 金额
		String acount = originalinfo.getAmount();
		// 商户订单号
		logger.info("******************根据商户号查询");

		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getRequestId());
		orig.setPid(originalinfo.getCustomerNumber());

		if (originalDao.selectByOriginal(orig) != null) {
			logger.info("下单重复");
			return setResp("03", "下单重复");
		}
		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(merchId);

		String MainCustomerNumber = busInfo.getBusinessnum();

		logger.info("上海易宝上送的商户号:" + MainCustomerNumber);
		String key = busInfo.getKek();

		logger.info("上海易宝上送的密钥:" + key);
		PostMethod postMethod = new PostMethod("https://skb.yeepay.com/skb-app/receiveApi.action");
		HttpClient client = new HttpClient();

		// String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
		// 业务号（2位）+业务细分（1位）+时间戳（13位）
		// 总共16位
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getRequestId());// 原始数据的订单编号
		original.setOrderId(originalinfo.getRequestId()); // 为主键
		original.setPid(originalinfo.getCustomerNumber());
		//original.setOrderTime(originalinfo.get);
		original.setOrderAmount(originalinfo.getAmount());
		original.setPageUrl(originalinfo.getWebCallBackUrl());
		original.setBgUrl(originalinfo.getCallBackUrl());
		original.setBankNo(originalinfo.getWithdrawCardNo());;
		originalDao.insert(original);

		// 根据商户号查询
		String mercId = originalinfo.getCustomerNumber();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				logger.error("参数错误!");
				retMap.put("v_code", "04");
				retMap.put("v_msg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {

				// 实际金额
				String factAmount = "" + new BigDecimal(originalinfo.getAmount()).multiply(new BigDecimal(100));
				// 查询商户路由
				PmsBusinessPos pmsBusinessPos = selectKey(originalinfo.getCustomerNumber());
				// 校验欧单金额限制
				ResultInfo payCheckResult = iPublicTradeVerifyService
						.amountVerifyOagent((int) Double.parseDouble(factAmount), TradeTypeEnum.onlinePay, oAgentNo);
				if (!payCheckResult.getErrCode().equals("0")) {
					// 交易不支持
					logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return setResp("05", "欧单金额限制，请重试或联系客服");
				}

				// 校验欧单模块是否开启
				ResultInfo resultInfoForOAgentNo = iPublicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.onlinePay,
						oAgentNo);
				if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
					// 交易不支持
					if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
						logger.error("交易关闭，请重试或联系客服");
						return setResp("06", "交易关闭，请重试或联系客服");
					} else {
						return setResp("07", "系统异常，请重试或联系客服");
					}

				}
				// 校验商户模块是否开启
				ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.onlinePay, mercId);
				if (!payCheckResult3.getErrCode().equals("0")) {
					// 交易不支持
					logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return setResp("08", "商户模块限制,请重试或联系客服");
				}
				// 校验商户金额限制
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
				paramMap.put("businesscode", TradeTypeEnum.onlinePay.getTypeCode());// 业务编号
				paramMap.put("oAgentNo", oAgentNo);
				// 商户 网购 业务信息
				Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

				if (resultMap == null || resultMap.size() == 0) {
					// 若查到的是空值，直接返回错误
					logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
					return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
				}

				String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
				String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
				String paymentAmount = factAmount;// 交易金额

				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
					// 金额超过最大金额
					logger.info("交易金额大于最打金额");
					return setResp("10", "金额超过最大交易金额");
				} else if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
					// 金额小于最小金额
					logger.info("交易金额小于最小金额");
					return setResp("11", "交易金额小于最小金额");

				}

				// 组装订单数据
				PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
				// 写入欧单编号
				pmsAppTransInfo.setoAgentNo(oAgentNo);
				pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
				pmsAppTransInfo.setTradetype(TradeTypeEnum.onlinePay.getTypeName());// 业务功能模块名称
																					// ：网购
				pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
				pmsAppTransInfo.setMercid(merchantinfo.getMercId());
				pmsAppTransInfo.setTradetypecode(TradeTypeEnum.onlinePay.getTypeCode());// 业务功能模块编号
																						// ：17
				pmsAppTransInfo.setOrderid(originalinfo.getRequestId());// 设置订单号
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay.getTypeCode());
				BigDecimal factBigDecimal = new BigDecimal(factAmount);
				BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

				pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
				pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
				pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
//				if ("0".equals(originalinfo.getV_type())) {
//					pmsAppTransInfo.setSettlementState("D0");
//				}
//				if ("1".equals(originalinfo.getV_type())) {
//
//					pmsAppTransInfo.setSettlementState("T1");
//				}

				// 插入订单信息
				Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
				if (insertAppTrans == 1) {

					// 查询订单信息
					pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

					String quickRateType = resultMap.get("QUICKRATETYPE").toString();// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

					if (appRateConfig == null) {
						// 若查到的是空值，直接返回错误
						// 若查到的是空值，直接返回错误
						logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
						return setResp("09", "没有查到相关费率配置,,请重试或联系客服");

					}

//					String isTop = appRateConfig.getIsTop();
//					String rate = appRateConfig.getRate();
//					String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
//					paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
//					String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
//					Double minPoundage = 0.0; // 附加费
//
//					if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
//							&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
//						if (StringUtils.isNotBlank(minPoundageStr)) {
//							minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
//						} else {
//							// 若查到的是空值，直接返回错误
//							logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
//							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
//						}
//					}
//
//					BigDecimal payAmount = null;
//					BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
//					// 费率
//					BigDecimal fee = new BigDecimal(0);
//					Double settleFee = null;
//					Double userfee = null;
//					String rateStr = "";
//					// Double payfee = null;
//					// 计算结算金额
//					if ("1".equals(isTop)) {
//
//						rateStr = rate + "-" + topPoundage;
//						// 是封顶费率类型
//						fee = new BigDecimal(rate).multiply(dfactAmount);
//
//						if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
//							// 手续费大于封顶金额，按封顶金额处理
//							payAmount = dfactAmount
//									.subtract(new BigDecimal(topPoundage).subtract(new BigDecimal(minPoundage)));
//							fee = new BigDecimal(topPoundage).add(new BigDecimal(minPoundage));
//						} else {
//							// 按当前费率处理
//							rateStr = rate;
//							fee.add(new BigDecimal(minPoundage));
//							payAmount = dfactAmount.subtract(fee);
//						}
//
//					} else {
//						// 按当前费率处理
//						double dfpag = Double.parseDouble(merchantinfo.getPoundage());
//						double daifu = Double.parseDouble(merchantinfo.getCounter());
////						if (!"".equals(originalinfo.getV_userFee()) && originalinfo.getV_userFee() != null) {
////							userfee = Double.parseDouble(originalinfo.getV_userFee());
////						}
////
////						if (!"".equals(originalinfo.getV_settleUserFee())
////								&& originalinfo.getV_settleUserFee() != null) {
////							settleFee = Double.parseDouble(originalinfo.getV_settleUserFee());
////						}
////						if (originalinfo.getV_type().equals("1")) {
////							// 按当前费率处理
////							rateStr = rate;
////							if (Double.parseDouble(rateStr) <= userfee) {
////								BigDecimal num = dfactAmount.multiply(new BigDecimal(userfee));
////								if (num.doubleValue() / 100 >= daifu) {
////									fee = num;
////								} else {
////									fee = new BigDecimal(daifu * 100);
////								}
////								rateStr = userfee.toString();
////								payAmount = dfactAmount.subtract(fee);
////								logger.info("清算金额:" + paymentAmount);
////								if (payAmount.doubleValue() < 0) {
////									payAmount = new BigDecimal(0.00);
////								}
////
////							} else {
////								logger.info("费率低于成本费率：" + merchantinfo.getMercId());
////								return setResp("12", "费率低于成本费率");
////							}
////						}
////						if (originalinfo.getV_type().equals("0")) {
////
////							// 按当前费率处理
////							rateStr = rate;
////							if (Double.parseDouble(rateStr) <= userfee) {
////								fee = new BigDecimal(userfee).multiply(dfactAmount).add(new BigDecimal(minPoundage));
////							} else {
////								logger.info("费率低于成本费率：" + merchantinfo.getMercId());
////								return setResp("12", "费率低于成本费率");
////
////							}
////							if (dfpag > settleFee) {
////								settleFee = dfpag;
////							}
////							switch (pmsBusinessPos.getBusinessnum()) {
////
////							case "1711030001":// 沈阳银盛
////								payAmount = dfactAmount.subtract(fee).subtract(new BigDecimal(100));
////								fee = fee.add(new BigDecimal(100));
////								break;
////							case "88882017092010001121":// 赢酷快捷
////								payAmount = dfactAmount.subtract(fee).subtract(new BigDecimal(20));
////								fee = fee.add(new BigDecimal(20));
////								break;
////							default:
////								break;
////
////							}
////							logger.info("清算金额:" + paymentAmount);
////							if (payAmount.doubleValue() < 0) {
////								payAmount = new BigDecimal(0.00);
////							}
////						}
//					}

					// 设置结算金额
//					pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
//					pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
//					pmsAppTransInfo.setPoundage(fee.toString());
					pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
					// 转换double为int
					Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

					// 验证支付方式是否开启
					payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay, oAgentNo, merchantinfo.getMercId());
					if (!payCheckResult.getErrCode().equals("0")) {
						// 交易不支持

						logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
								+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
						return setResp("13", "暂不支持该交易方式");
					}
					ViewKyChannelInfo channelInfo = AppPospContext.context.get(HENGFENGPAY + HENGFENGCHANNELNUM);

					// 设置通道信息
					pmsAppTransInfo.setBusinessNum(channelInfo.getBusinessnum());
					pmsAppTransInfo.setChannelNum(HENGFENGCHANNELNUM);

					// 查看当前交易是否已经生成了流水表
					PospTransInfo pospTransInfo = null;
					// 流水表是否需要更新的标记 0 insert，1：update
					int insertOrUpdateFlag = 0;
					// 生成上送流水号
					String transOrderId = generateTransOrderId(TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay);
					if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
						// 已经存在，修改流水号，设置pospsn为空
						logger.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
						pospTransInfo.setTransOrderId(originalinfo.getRequestId());
						pospTransInfo.setResponsecode("99");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
					} else {
						// 不存在流水，生成一个流水
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
						// 设置上送流水号
						pospTransInfo.setTransOrderId(originalinfo.getRequestId());
						insertOrUpdateFlag = 0;
					}
					// 插入流水表信息
					if (insertOrUpdateFlag == 0) {
						// 插入一条流水
						pospTransInfoDAO.insert(pospTransInfo);
					} else if (insertOrUpdateFlag == 1) {
						// 更新一条流水
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
					logger.info("修改订单信息");
					logger.info(pmsAppTransInfo);

					int num = pmsAppTransInfoDao.update(pmsAppTransInfo);
					if (num > 0) {
						
						StringBuffer signature = new StringBuffer();
						signature.append(originalinfo.getSource() == null ? "" : originalinfo.getSource())
								.append(MainCustomerNumber == null ? "" : MainCustomerNumber)
								.append(originalinfo.getSubContractId() == null ? "" : originalinfo.getSubContractId())
								.append(originalinfo.getAmount() == null ? "" : originalinfo.getAmount())
								.append(originalinfo.getMcc() == null ? "" : originalinfo.getMcc())
								.append(originalinfo.getRequestId() == null ? "" : originalinfo.getRequestId())
								.append(originalinfo.getMobileNumber() == null ? "" : originalinfo.getMobileNumber())
								.append(BaseUtil.url+"/shyb_app/notifyUrl.action")
								.append(BaseUtil.url+"/shyb_app/returnUrl.action?orderNo="+originalinfo.getRequestId())
								.append(originalinfo.getPayerBankAccountNo() == null ? "" : originalinfo.getPayerBankAccountNo());

						logger.info("上海易宝生成签名前的数据：" + signature.toString());
						String hmac = Digest.hmacSign(signature.toString(), key);
						logger.info("上海易宝生成的签名：" + hmac);

						   Part[] parts = new ReceviePartsBuiler()
				                    .setMainCustomerNumber(MainCustomerNumber)
				                    .setAmount(originalinfo.getAmount()).setCallBackUrl(BaseUtil.url+"/shyb_app/notifyUrl.action")
				                    .setCustomerNumber(originalinfo.getSubContractId()).setHamc(hmac)
				                    .setMcc(originalinfo.getMcc()).setMobileNumber(originalinfo.getMobileNumber() )
				                    .setRequestId(originalinfo.getRequestId())
				                    .setSource(originalinfo.getSource())
				                    .setPayerBankAccountNo(originalinfo.getPayerBankAccountNo())
				                    .setWebCallBackUrl(BaseUtil.url+"/shyb_app/returnUrl.action?orderNo="+originalinfo.getRequestId())		                    
//				                    .setAutoWithdraw(originalinfo.getAutoWithdraw())
//				                    .setWithdrawCardNo(originalinfo.getWithdrawCardNo())
//				                    .setWithdrawCallBackUrl(originalinfo.getWithdrawCallBackUrl())
				                    .generateParams();
				            postMethod.setRequestEntity(new MultipartRequestEntity(parts,
				                    postMethod.getParams()));
						logger.info("上海易宝发送数据:" + postMethod.toString());

						int status = client.executeMethod(postMethod);
						logger.info("上海易宝返回的状态码:" + status);
						logger.info("上海易宝返回的响应数据:" + postMethod.getResponseBodyAsString());
						net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(postMethod.getResponseBodyAsString());
						Iterator it = ob.keys();
						Map<String, String> map = new HashMap<>();
						String code="";
						String customerNumber="";
						String message="";
						String requestId="";
						String url="";
						while (it.hasNext()) {
							key = (String) it.next();
							if (key.equals("code")) {

								code = ob.getString(key);
								
							}
							if (key.equals("customerNumber")) {

								customerNumber = ob.getString(key);
								
							}
							if (key.equals("message")) {

								message = ob.getString(key);
								retMap.put("message", message);
							}
							if (key.equals("requestId")) {

								requestId = ob.getString(key);
								retMap.put("requestId", requestId);
							}
							if (key.equals("url")) {

								url = ob.getString(key);
								
							}							
						}
						if("0000".equals(code)&&url!=null)
						{
							 retMap.put("customerNumber", originalinfo.getCustomerNumber());
							 retMap.put("code","00");
							 xdt.quickpay.shyb.util.AESUtil aes=new xdt.quickpay.shyb.util.AESUtil();
							 String decrypt = aes.decrypt(url, busInfo.getKek());
							 logger.info("上游返回的信息："+decrypt);
							 retMap.put("url", decrypt);
						}
						

					}
				}
			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("v_code", "16");
				retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
				return retMap;
			}
		} else {
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("v_code", "16");
			retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服\"");
			return retMap;
		}
		return retMap;
	}
	public synchronized int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo) throws Exception {
		logger.info("代付实时填金:" + JSON.toJSON(originalInfo));
		DecimalFormat df = new DecimalFormat("#.00");
		// PmsMerchantInfo pmsMerchantInfo =new PmsMerchantInfo();
		PmsDaifuMerchantInfo pmsDaifuMerchantInfo = new PmsDaifuMerchantInfo();
		PmsMerchantInfo merchantInfo = pmsMerchantInfoDao.selectMercByMercId(originalInfo.getPid());
		logger.info("merchantInfo:" + JSON.toJSON(merchantInfo));
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(originalInfo.getOrderId());
		logger.info("pmsAppTransInfo:" + JSON.toJSON(pmsAppTransInfo));
		pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
		PmsDaifuMerchantInfo daifuMerchantInfo = pmsDaifuMerchantInfoDao
				.selectByDaifuMerchantInfo(pmsDaifuMerchantInfo);
		logger.info("daifuMerchantInfo:" + JSON.toJSON(daifuMerchantInfo));
		if (daifuMerchantInfo != null) {
			logger.info("11111111111111111111111");
			return 0;
		} else {
			if ("0".equals(merchantInfo.getOpenPay())) {
				// 手续费
				Double poundage = Double.parseDouble(pmsAppTransInfo.getPoundage());
				poundage = Double.parseDouble(df.format(poundage));
				String position = merchantInfo.getPosition();
				Double amount = Double.parseDouble(originalInfo.getOrderAmount());
				logger.info("订单金额：" + amount);
				BigDecimal positions = new BigDecimal(position);
				// Double ds =positions.doubleValue();
				Double dd = amount * 100 - poundage;
				// dd =(dd+ds);
				logger.info("来了1---------");
				Map<String, String> map = new HashMap<>();
				map.put("machId", originalInfo.getPid());
				map.put("payMoney", dd.toString());
				int i = pmsMerchantInfoDao.updataPay(map);
				if (i != 1) {
					logger.info("实时填金失败！");
					// 状态
					pmsDaifuMerchantInfo.setResponsecode("01");
				} else {
					// 状态
					logger.info("实时成功！");
					pmsDaifuMerchantInfo.setResponsecode("00");
				}
				logger.info("来到这里了11！");
				PmsMerchantInfo info = select(originalInfo.getPid());
				// pmsMerchantInfo.setMercId(originalInfo.getPid());
				// pmsMerchantInfo.setPosition(df.format(dd));
				// 商户号
				logger.info("来到这里了22！");
				pmsDaifuMerchantInfo.setMercId(originalInfo.getPid());
				// 订单号
				pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
				// 总金额
				pmsDaifuMerchantInfo.setAmount((Double.parseDouble(originalInfo.getOrderAmount())) + "");

				// 备注
				pmsDaifuMerchantInfo.setRemarks("D0");
				// 记录描述
				pmsDaifuMerchantInfo.setRecordDescription(
						"订单号:" + originalInfo.getOrderId() + "交易金额:" + originalInfo.getOrderAmount());
				// 交易类型
				pmsDaifuMerchantInfo.setTransactionType(pmsAppTransInfo.getPaymenttype());
				// 发生额
				pmsDaifuMerchantInfo.setPayamount((Double.parseDouble(originalInfo.getOrderAmount())) + "");
				// 账户余额
				pmsDaifuMerchantInfo.setPosition(info.getPosition());
				// 手续费
				pmsDaifuMerchantInfo.setPayCounter(poundage / 100 + "");
				pmsDaifuMerchantInfo.setOagentno("100333");
				logger.info("来了2---------");
				// 交易时间
				// pmsDaifuMerchantInfo.setCreationdate(new SimpleDateFormat("yyyy-MM-dd
				// HH:mm:ss").format(new Date()));
				int s = pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
				logger.info("---s:" + s);
				logger.info("来了3---------");
				// int i =pmsMerchantInfoDao.UpdatePmsMerchantInfo(pmsMerchantInfo);
				logger.info("---i:" + i);
				return i;
			} else {
				logger.info("此商户未开通代付！！");
			}
		}

		return 0;
	}

	public void otherInvoke(ShybQuickCallbackEntity result) throws Exception {
		// TODO Auto-generated method stub

		logger.info("上游返回的数据" + result);
		// 流水表transOrderId
		String transOrderId = result.getRequestId();
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		logger.info("流水表信息" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		logger.info("订单表信息" + pmsAppTransInfo);
		// 查询结果成功
		if ("SUCCESS".equals(result.getStatus().toString())) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getMessage().toString());
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
				pospTransInfo.setPospsn(result.getRequestId());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("FAIL".equals(result.getStatus().toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getMessage().toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getRequestId());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}

	}

	public Map<String, String> quickQuery(QueryRequestEntity query) {

		Map<String, String> result = new HashMap<>();
		OriginalOrderInfo origin = new OriginalOrderInfo();
		String orderid = query.getV_oid();
		logger.info("快捷查询订单号:" + orderid);
		origin = originalDao.getOriginalOrderInfoByOrderid(orderid);
		PmsAppTransInfo pmsAppTransInfo = null;
		try {
			if (origin != null) {
				pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(origin.getOrderId());
				if (pmsAppTransInfo != null) {
					logger.info("pmsAppTransInfo：" + JSON.toJSON(pmsAppTransInfo));

					result.put("v_mid", query.getV_mid());// 商户号
					result.put("v_oid", query.getV_oid());// 订单号
					result.put("v_txnAmt", origin.getOrderAmount());// 金额
					result.put("v_attach", origin.getAttach());// 支付类型
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					if ("0".equals(pmsAppTransInfo.getStatus())) {
						result.put("v_status_code", "0000");// 支付状态
						result.put("v_status_msg", "支付成功");
					} else if (("1".equals(pmsAppTransInfo.getStatus()))) {
						result.put("v_status_code", "1001");// 支付状态
						result.put("v_status_msg", "支付失败");
					} else {
						result.put("v_status_code", "200");// 支付状态
						result.put("v_status_msg", "初始化");
					}

				} else {
					result.put("v_code", "15");
					result.put("v_msg", "请求失败");
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 
	 * @Description 设置响应信息
	 * @author Administrator
	 * @param respCode
	 * @param respInfo
	 * @return
	 */
	private Map<String, String> setResp(String respCode, String respInfo) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("v_code", respCode);
		result.put("v_msg", respInfo);
		return result;
	}
	/**
	 * 查询商户密钥信息
	 * 
	 * @param merchantId
	 *            商户号
	 * @return
	 * @throws Exception
	 */
	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {

		logger.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}
	/**
	 * 查询原始信息
	 * 
	 * @param tranId
	 *            本地订单id
	 * @return 原始上送信息
	 * @throws Exception
	 */
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {

		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		logger.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}


}

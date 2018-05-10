package xdt.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.kspay.cert.LoadKeyFromPKCS12;
import com.unicompay.sign.Encrypt;
import com.uns.inf.api.model.Request;
import com.uns.inf.api.service.Service;

import net.sf.json.JSONObject;
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
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.quickpay.wzf.Constant;
import xdt.quickpay.wzf.DES;
import xdt.quickpay.wzf.DesUtil;
import xdt.quickpay.wzf.UniPaySignUtils;
import xdt.quickpay.wzf.WzfSignUtil;
import xdt.quickpay.ysb.model.YsbRequsetEntity;
import xdt.quickpay.ysb.util.SignUtil;
import xdt.quickpay.ysb.util.YsbSignUtil;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IWZFPayService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.servlet.AppPospContext;
import xdt.util.HttpURLConection;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

@Component
public class WzfPayServiceImpl extends BaseServiceImpl implements IWZFPayService {
	/**
	 * 记录日志
	 */
	private Logger log = Logger.getLogger(WzfPayServiceImpl.class);

	private Logger logger = Logger.getLogger(WzfPayServiceImpl.class);

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	IPmsGoodsDao pmsGoodsDao;

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

	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;

	/**
	 * 微信商户信息
	 */
	@Resource
	public PmsWeixinMerchartInfoService weixinService;

	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;

	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {

		log.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	public synchronized Map<String, String> customerRegister(YsbRequsetEntity originalinfo) throws Exception {
		// TODO Auto-generated method stub
		String message = "0:initialize";

		String jsonString = null;

		Map<String, String> retMap = new HashMap<String, String>();

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(originalinfo.getMerchantId());

		String merchantKey = keyinfo.getMerchantkey();
		SignUtil signUtil = new SignUtil();
		if (!signUtil.verify(WzfSignUtil.wzfdaifuSigiString(originalinfo), originalinfo.getSign(), merchantKey)) {
			log.error("签名错误!");
			retMap.put("respCode", "15");
			retMap.put("respMsg", "签名错误");
			return retMap;
		}
		// 根据商户号查询
		String mercId = originalinfo.getMerchantId();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();//

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				log.error("参数错误!");
				retMap.put("respCode", "16");
				retMap.put("respMsg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {

				PmsWeixinMerchartInfo wx = weixinService.selectByPrimaryKey(originalinfo.getPhoneNo());
				if (wx != null) {
					Map<String, String> result = setResp("100005", "账号已经注册！");
					result.put("merchartId", mercId);
					return result;
				}
				// 查询上游商户号
				PmsBusinessPos busInfo = selectKey(originalinfo.getMerchantId());

				Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();

				String interfaceVersion = "2.0.0.0";

				String payProducts = originalinfo.getPayProducts();

				String merNo = busInfo.getBusinessnum();

				String signDate = UtilDate.getDate();

				String signNo = UtilDate.getOrderNum();

				String accType = originalinfo.getAccType();

				String accCode = originalinfo.getAccCode();

				String bankCode = originalinfo.getBankCode();

				String signAccInfo = "";

				String signChnl = originalinfo.getSignChnl();

				String charSet = "UTF-8";

				String signType = "RSA_SHA256";

				String signMsg = "";

				String signAcc = originalinfo.getCardNo();

				String signName = originalinfo.getName();

				String identityNo = originalinfo.getIdCardNo();

				String CCV = "";

				String validity = "";

				if (originalinfo.getValidity() != null) {
					validity = originalinfo.getValidity();
				}
				if (originalinfo.getCCV() != null) {
					CCV = originalinfo.getCCV();
				}

				if ("A01".equals(originalinfo.getAccCode())) {

					String cert = "identityNo=1" + identityNo + "|" + "signAcc=" + signAcc + "|" + "signName="
							+ signName;

					logger.info("签约账户信息:" + cert);

					String key = busInfo.getKek();

					logger.info("沃支付商户密钥:" + key);

					String sign = DesUtil.encrypt(cert, key, "utf-8").replaceAll("\r\n","");

					logger.info("沃支付生成的签名:" + sign);

					signAccInfo = sign;

				} else if ("A02".equals(originalinfo.getAccCode())) {
					String cert = "CCV=" + CCV + "|" + "identityNo=1" + identityNo + "|" + "signAcc=" + signAcc + "|"
							+ "signName=" + signName + "|" + "validity=" + validity;

					logger.info("签约账户信息:" + cert);

					String key = busInfo.getKek();

					logger.info("沃支付商户密钥:" + key);

					String sign = DesUtil.encrypt(cert, key, "utf-8").replaceAll("\r\n","");

					logger.info("沃支付生成的签名:" + sign);

					signAccInfo = sign;
				}

				String str = "accCode=" + accCode + "|" + "accType=" + accType + "|" + "bankCode=" + bankCode + "|"
							+ "charSet=" + charSet + "|"  + "interfaceVersion="
							+ interfaceVersion + "|" + "merNo=" + merNo + "|" + "payProducts=" + payProducts + "|"
							 + "signAccInfo=" + signAccInfo + "|" + "signChnl=" + signChnl
							+ "|" + "signDate=" + signDate + "|" + "signNo=" + signNo
							+ "|" + "signType=" + signType + "|";

				logger.info("向上游待发数据:" + str);

				// 生成签名
				Map<String, String> params = WzfSignUtil.rep2Map(str);
				// 商户的签名
				String sign = UniPaySignUtils.merSign(params, "RSA_SHA256").replaceAll(" ", "");
				// HttpClientUtil client = new HttpClientUtil();
				logger.info("向上游发送的签名:" + sign);
				String cert = "accCode=" + accCode + "&" + "accType=" + accType + "&" + "bankCode=" + bankCode + "&"
						+ "charSet=" + charSet + "&" + "interfaceVersion=" + interfaceVersion + "&" + "merNo=" + merNo
						+ "&" + "payProducts=" + payProducts + "&" + "signAccInfo=" + signAccInfo + "&" + "signChnl="
						+ signChnl + "&" + "signDate=" + signDate + "&" + "signNo=" + signNo + "&" + "signType="
						+ signType + "&" + "signMsg=" + sign;

				String url = Constant.SIN_URL;

				logger.info("向上游发送的数据:" + cert);
				String result = HttpURLConection.httpURLConnectionPOST(url, cert);
				// String result = client.post(url, cert);

				logger.info("响应的数据:"+result);
			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("respCode", "17");
				retMap.put("respMsg", "商户没有进行实名认证");
				return retMap;
			}
		} else {
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("respCode", "17");
			retMap.put("respMsg", "商户没有进行实名认证");
			return retMap;
		}

		return retMap;
	}
	public synchronized Map<String, String> payHandle(YsbRequsetEntity originalinfo) throws Exception {

		String message = "0:initialize";

		String jsonString = null;

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		Map<String, String> retMap = new HashMap<String, String>();
		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(originalinfo.getMerchantId());

		String merchantKey = keyinfo.getMerchantkey();
		SignUtil signUtil = new SignUtil();
		if (!signUtil.verify(WzfSignUtil.wzfdaikouSigiString(originalinfo), originalinfo.getSign(), merchantKey)) {
			log.error("签名错误!");
			retMap.put("respCode", "15");
			retMap.put("respMsg", "签名错误");
			return retMap;
		}
		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getOrderId());
		orig.setPid(originalinfo.getMerchantId());

		if (originalDao.selectByOriginal(orig) != null) {

			log.error("下单重复!");
			retMap.put("respCode", "20");
			retMap.put("respMsg", "下单重复");
			return retMap;
		}

		String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
															// 业务号（2位）+业务细分（1位）+时间戳（13位）
															// 总共16位
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getOrderId());// 原始数据的订单编号
		original.setOrderId(orderNumber); // 为主键
		original.setPid(originalinfo.getMerchantId());
		// original.setOrderTime(originalinfo.get);
		original.setOrderAmount(originalinfo.getAmount());
		// original.setPageUrl(originalinfo.getFrontUrl());
		original.setBgUrl(originalinfo.getResponseUrl());
		//original.setBankNo(originalinfo.getCardNo());
		 original.setBankId(originalinfo.getSubContractId());
		 //original.getProcdutName(originalinfo.getGoodsName());
		// original.setPayType(originalinfo.getTranTp());
		originalDao.insert(original);

		// 根据商户号查询
		String mercId = originalinfo.getMerchantId();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();//

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				log.error("参数错误!");
				retMap.put("respCode", "16");
				retMap.put("respMsg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {
				// 实际金额
				String factAmount = originalinfo.getAmount();
				// 校验欧单金额限制
				ResultInfo payCheckResult = iPublicTradeVerifyService
						.amountVerifyOagent((int) Double.parseDouble(factAmount), TradeTypeEnum.onlinePay, oAgentNo);
				if (!payCheckResult.getErrCode().equals("0")) {
					// 交易不支持
					logger.info(
							"欧单金额限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.WithholdPay.getTypeCode());
					retMap.put("0004", "欧单金额限制代扣");
					return retMap;
				}
				// 校验商户模块是否开启
				ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.onlinePay, mercId);
				if (!payCheckResult3.getErrCode().equals("0")) {
					// 交易不支持
					logger.info(
							"商户模块限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.WithholdPay.getTypeCode());
					retMap.put("0005", "商户模块限制代扣");
					return retMap;
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
					retMap.put("0006", "没有查到相关费率配置：");
					return retMap;
				}

				String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
				String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
				String paymentAmount = factAmount;// 交易金额

				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
					// 金额超过最大金额
					logger.info("交易金额大于最打金额");
					retMap.put("0007", "金额超过最大交易金额");
					return retMap;
				} else if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
					// 金额小于最小金额
					logger.info("交易金额小于最小金额");
					retMap.put("0008", "交易金额小于最小金额");
					return retMap;

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
				pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.WithholdPay.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.WithholdPay.getTypeCode());
				pmsAppTransInfo.setPrepaidphonenumber(originalinfo.getPhoneNo());// 手机号码
				// pmsAppTransInfo.setToken(originalinfo.getToken()); //令牌
				BigDecimal factBigDecimal = new BigDecimal(factAmount);
				BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

				pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
				pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
				pmsAppTransInfo.setDrawMoneyType("1");// 普通提款

				// 插入订单信息
				Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
				if (insertAppTrans == 1) {

					// 查询订单信息
					pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

					paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
					// 设置结算金额
					pmsAppTransInfo.setPayamount(paymentAmount.toString());// 结算金额
					pmsAppTransInfo.setRate("0");// 0.50_35 || 0.50
					pmsAppTransInfo.setPoundage("0");
					pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
					// 转换double为int
					Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

					// 验证支付方式是否开启
					payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.onlinePay,
							PaymentCodeEnum.WithholdPay, oAgentNo, merchantinfo.getMercId());
					if (!payCheckResult.getErrCode().equals("0")) {
						// 交易不支持
						responseDTO.setRetCode(1);
						responseDTO.setRetMessage(payCheckResult.getMsg());
						try {
							jsonString = createJsonString(responseDTO);
						} catch (Exception em) {
							em.printStackTrace();
						}
						logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
								+ PaymentCodeEnum.WithholdPay.getTypeCode());
						retMap.put("0011", "不支持代扣");
						return retMap;
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
					String transOrderId = generateTransOrderId(TradeTypeEnum.onlinePay, PaymentCodeEnum.WithholdPay);
					if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
						// 已经存在，修改流水号，设置pospsn为空
						logger.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
						pospTransInfo.setTransOrderId(originalinfo.getOrderId());
						pospTransInfo.setResponsecode("99");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
					} else {
						// 不存在流水，生成一个流水
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
						// 设置上送流水号
						pospTransInfo.setTransOrderId(originalinfo.getOrderId());
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
					log.info("修改订单信息");
					log.info(pmsAppTransInfo);
					int number = pmsAppTransInfoDao.update(pmsAppTransInfo);
					if (number > 0) {
						Request dcRequest = new Request();
						Double amount = Double.parseDouble(originalinfo.getAmount()) / 100;
						log.info("代扣金额为:" + amount);
						dcRequest.put("accountId", "2120170904150304001");
						dcRequest.put("subContractId", originalinfo.getSubContractId());
						dcRequest.put("orderId", originalinfo.getOrderId());
						dcRequest.put("purpose", originalinfo.getPurpose());
						dcRequest.put("amount", amount.toString());
						dcRequest.put("phoneNo", originalinfo.getPhoneNo());
						dcRequest.put("responseUrl", "http://60.28.24.183:9101/app_posp/ysb/bgPayResult.action");
						dcRequest.put("key", "30eccdd59dbee2");
						try {
							String result = Service.sendPost(dcRequest,
									"http://114.80.54.73:8081/unspay-external/delegateCollect/collect");
							log.info("result:" + result);
							JSONObject jb = JSONObject.fromObject(result);
							String resultCode = (String) jb.get("result_code");
							String result_msg = (String) jb.get("result_msg");
							retMap.put("result_code", resultCode);
							retMap.put("result_msg", result_msg);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("respCode", "17");
				retMap.put("respMsg", "商户没有进行实名认证");
				return retMap;
			}
		} else {
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("respCode", "17");
			retMap.put("respMsg", "商户没有进行实名认证");
			return retMap;
		}
		return retMap;

	}

	/**
	 * 
	 * @Description 设置响应信息
	 * @author Administrator
	 * @param respCode
	 * @param respInfo
	 * @return
	 */
	public Map<String, String> setResp(String respCode, String respInfo) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("respCode", respCode);
		result.put("respInfo", respInfo);
		return result;
	}
	public static void main(String[] args) throws Exception {
		
		String cert="accCode=A02|accType=1|bankCode=SPDB|charSet=UTF-8|interfaceVersion=2.0.0.0|merNo=301101910008366|payProducts=YDK|signAccInfo=lHswGHRVaiXRvPs5+5PkHPcAYAVeDVTgbhQpKi0ZcR7VoBctGFEwxcvD3J5pPG8P4iHdLhCPWw4bkjtHLSyoaLYwuyAhrtgpqTSlDJ+E/xzMa4LlmJyWU4eICgaz8UXg|signChnl=www|signDate=20171116|signNo=20171116150756|signType=RSA_SHA256|";
		// 生成签名
		Map<String, String> params = WzfSignUtil.rep2Map(cert);
		// 商户的签名
		String sign = UniPaySignUtils.merSign(params, "RSA_SHA256");
		
		System.out.println(sign);
	}

}

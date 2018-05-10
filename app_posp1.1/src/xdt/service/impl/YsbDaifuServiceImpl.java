package xdt.service.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.capinfo.crypt.Md5;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.uns.inf.api.model.CallBack;
import com.uns.inf.api.model.Request;
import com.uns.inf.api.service.Service;

import net.sf.json.JSONObject;
import xdt.common.RetAppMessage;
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
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.hf.entity.PayResponseEntity;
import xdt.quickpay.hf.util.PreSignUtil;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.quickpay.ysb.Constants;
import xdt.quickpay.ysb.model.YsbRequsetEntity;
import xdt.quickpay.ysb.util.SignUtil;
import xdt.quickpay.ysb.util.YsbSignUtil;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IYsbDaifuService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.servlet.AppPospContext;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

@Component
public class YsbDaifuServiceImpl extends BaseServiceImpl implements IYsbDaifuService {

	/**
	 * 记录日志
	 */
	private Logger log = Logger.getLogger(YsbDaifuServiceImpl.class);

	private Logger logger = Logger.getLogger(YsbDaifuServiceImpl.class);

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
		if (!signUtil.verify(YsbSignUtil.ybsdaifuSigiString(originalinfo), originalinfo.getSign(), merchantKey)) {
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
				Request dcRequest = new Request();
				dcRequest.put("accountId", "2120170904150304001");
				dcRequest.put("contractId", "2120170904150304001");
				dcRequest.put("name", originalinfo.getName());
				dcRequest.put("phoneNo", originalinfo.getPhoneNo());
				dcRequest.put("cardNo", originalinfo.getCardNo());
				dcRequest.put("idCardNo", originalinfo.getIdCardNo());
				dcRequest.put("startDate", originalinfo.getStartDate());
				dcRequest.put("endDate", originalinfo.getEndDate());
				dcRequest.put("cycle", originalinfo.getCycle());
				dcRequest.put("triesLimit", originalinfo.getTriesLimit());
				dcRequest.put("key", "30eccdd59dbee2");
				try {
					String result = Service.sendPost(dcRequest,
							"http://114.80.54.73:8081/unspay-external/subcontract/signSimpleSubContract");
					log.info("result:" + result);
					JSONObject jb = JSONObject.fromObject(result);
					String resultCode = (String) jb.get("result_code");
					String result_msg = (String) jb.get("result_msg");
					String subContractId = (String) jb.get("subContractId");
					retMap.put("result_code", resultCode);
					retMap.put("result_msg", result_msg);
					retMap.put("subContractId", subContractId);
					if (resultCode.equals("0000")) {
						PmsWeixinMerchartInfo entity = new PmsWeixinMerchartInfo();
						entity.setMerchartId(mercId);
						entity.setAccount(originalinfo.getPhoneNo());
						entity.setCertNo(originalinfo.getIdCardNo());
						entity.setMobile(originalinfo.getPhoneNo());
						entity.setCardNo(originalinfo.getCardNo());
						entity.setRealName(originalinfo.getName());
						entity.setMerchartName(merchantinfo.getMercName());
						entity.setoAgentNo("100333");
						weixinService.updateRegister(entity);
					}
				} catch (Exception e) {
					e.printStackTrace();
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

	public synchronized Map<String, String> payHandle(YsbRequsetEntity originalinfo) throws Exception {

		String message = "0:initialize";

		String jsonString = null;

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		Map<String, String> retMap = new HashMap<String, String>();
		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(originalinfo.getMerchantId());

		String merchantKey = keyinfo.getMerchantkey();
		SignUtil signUtil = new SignUtil();
		if (!signUtil.verify(YsbSignUtil.ybsdaikouSigiString(originalinfo), originalinfo.getSign(), merchantKey)) {
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
		original.setBankNo(originalinfo.getCardNo());
		// original.setBankId(originalinfo.getCard());
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
						dcRequest.put("responseUrl", BaseUtil.url+"/ysb/bgPayResult.action");
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
	public synchronized Map<String, String> payHandle1(YsbRequsetEntity originalinfo) throws Exception {

		String message = "0:initialize";

		String jsonString = null;

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		Map<String, String> retMap = new HashMap<String, String>();
		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(originalinfo.getMerchantId());

		String merchantKey = keyinfo.getMerchantkey();
		SignUtil signUtil = new SignUtil();
		if (!signUtil.verify(YsbSignUtil.ybsdaikouSigiString(originalinfo), originalinfo.getSign(), merchantKey)) {
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
		original.setBankNo(originalinfo.getCardNo());
		// original.setBankId(originalinfo.getCard());
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
					BigDecimal payAmount = null;
					BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
					// 费率
					BigDecimal fee = new BigDecimal(0);
					String rateStr = "";
					// 计算结算金额
					Double daikou_min_poundage=Double.parseDouble(merchantinfo.getDaikouMinPoundage());//代扣最小金额
					Double daikou_max_poundage=Double.parseDouble(merchantinfo.getDaikouMaxPoundage());//代扣最小金额
					if(dfactAmount.doubleValue()/100>daikou_min_poundage.doubleValue()&&dfactAmount.doubleValue()/100<=5000)
					{
						fee=new BigDecimal(daikou_min_poundage).multiply(new BigDecimal(100));
					}else if(dfactAmount.doubleValue()/100>5000&&dfactAmount.doubleValue()/100<=20000)
					{
						fee=new BigDecimal(daikou_max_poundage).multiply(new BigDecimal(100));
					}else
					{
						logger.info("交易金额小于最小代扣金额");
						retMap.put("0009", "交易金额小于最小代扣金额");
						return retMap;
					}
					payAmount = dfactAmount.subtract(fee);	

					// 设置结算金额
					pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
					pmsAppTransInfo.setRate("0");// 0.50_35 || 0.50
					pmsAppTransInfo.setPoundage(fee.toString());
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
						dcRequest.put("responseUrl", BaseUtil.url+"/app_posp/ysb/bgPayResult.action");
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

	@Override
	public synchronized void otherInvoke(CallBack result) throws Exception {

		// 流水表transOrderId
		String transOrderId = result.getOrderId();

		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);

		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());

		// 查询结果成功
		if ("0000".equals(result.getResult_code())) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getResult_msg());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// log.info("修改余额");
				// 修改余额
				log.info(pmsAppTransInfo);
				// updateMerchantBanlance(pmsAppTransInfo);
				// 更新流水表
				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(result.getResult_msg());
				log.info("更新流水");
				log.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("代扣失败");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn("代扣失败");
				log.info("更新流水");
				log.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
	}

	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		log.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	public synchronized Map<String, String> pay(YsbRequsetEntity originalinfo) throws Exception {


		Map<String, String> result = new HashMap<String, String>();

		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();

		String merid = originalinfo.getMerchantId();

		log.info("下游上送的商户号:" + merid);

		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(merid);

		String merchantKey = keyinfo.getMerchantkey();
		if (!SignUtil.verify(YsbSignUtil.ybsdaifuSigiPay(originalinfo), originalinfo.getSign(), merchantKey)) {
			log.error("签名错误!");
			result.put("respCode", "15");
			result.put("respMsg", "签名错误");
			return result;
		}

		model.setMercId(originalinfo.getMerchantId());
		model.setAmount(originalinfo.getAmount());
		model.setBatchNo(originalinfo.getOrderId());
		model.setIdentity(originalinfo.getOrderId());
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {

			result.put("12", "代付重复");
			log.info("代付重复");
			return result;
		}
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(originalinfo.getMerchantId());
		// o单编号
		String oAgentNo = "";
		Double surAmount = null;
		BigDecimal b3 = new BigDecimal(0.00);
		BigDecimal volumn = new BigDecimal("0");

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();//

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				log.error("参数错误!");
				result.put("respCode", "16");
				result.put("respMsg", "参数错误,没有欧单编号");
				return result;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {

				// 判断此商户是否开启代付
				if ("0".equals(merchantinfo.getOpenPay())) {
					// 实际金额
					Double factAmount = Double.parseDouble(model.getAmount());
					BigDecimal b1 = new BigDecimal(factAmount.toString());
					BigDecimal b2 = new BigDecimal(merchantinfo.getPositionT1().toString());

					if(b1.doubleValue()>0&&b1.doubleValue()<=50000)
					{
						b3 = new BigDecimal(3);
					}else if(b1.doubleValue()>50000&&b1.doubleValue()<=200000)
					{
						b3 = new BigDecimal(12);
					}
					
					log.info("代付金额:" + b1.multiply(new BigDecimal(100)).doubleValue());
					log.info("可用额度:" + b2.doubleValue());
					log.info("每笔代付手续费:" + b3);
					volumn = new BigDecimal(1);
					log.info("总笔数:" + volumn.toString());
					double fee = volumn.multiply(b3).doubleValue() * 100;
					log.info("代付总手续费:" + fee);
					// 清算金额
					Double payAmount = factAmount * 100 + fee;
					BigDecimal b4 = new BigDecimal(payAmount.toString());
					log.info("清算金额:" + b4);
					if (payAmount <= b2.doubleValue()) {

						BigDecimal min = new BigDecimal(merchantinfo.getMinDaiFu());
						BigDecimal max = new BigDecimal(merchantinfo.getMaxDaiFu());
						if (min.compareTo(b1) == 1) {
							log.info("代付金额小于最小代付金额");
							result.put("005", "代付金额小于最小代付金额！");
							return result;
						} else if (max.compareTo(b1) == -1) {
							log.info("代付金额大于最大代付金额");
							result.put("005", "代付金额大于最大代付金额！");
							return result;
						}
						Request dprequest = new Request();
						dprequest.put("accountId", "2120170904150304001");
						dprequest.put("name", originalinfo.getName());
						dprequest.put("cardNo", originalinfo.getCardNo());
						dprequest.put("orderId", originalinfo.getOrderId());
						dprequest.put("purpose", originalinfo.getPurpose());
						dprequest.put("amount", originalinfo.getAmount());
						dprequest.put("responseUrl", BaseUtil.url+"/wechat/bgPayResult.action");
						dprequest.put("key", "30eccdd59dbee2");
						try {
							log.info("上送的数据:" + dprequest);
							String rescode = Service.sendPost(dprequest,
									"http://114.80.54.73:8081/unspay-external/delegatePay/pay");
							log.info("响应信息:" + rescode);
							JSONObject jb = JSONObject.fromObject(rescode);
							String resultCode = (String) jb.get("result_code");
							String result_msg = (String) jb.get("result_msg");
							result.put("result_code", resultCode);
							result.put("result_msg", result_msg);
							if ("0000".equals(resultCode)) {
								Request dp = new Request();
								dp.put("accountId", "2120170904150304001");
								dp.put("orderId", originalinfo.getOrderId());
								dp.put("key", "30eccdd59dbee2");
								String res = Service.sendPost(dprequest,
										"http://114.80.54.73:8081/unspay-external/delegatePay/queryOrderStatus");
								log.info("result:" + res);
								JSONObject jb1 = JSONObject.fromObject(res);
								String Code = (String) jb1.get("result_code");
								 result_msg = (String) jb1.get("result_msg");
								String status = (String) jb1.get("status");
								String desc = (String) jb1.get("desc");
								result.put("result_code", Code);
								result.put("result_msg", result_msg);
								result.put("status", status);
								result.put("desc", desc);
								if ("0000".equals(Code)) {
									if("00".equals(status))
									{
										 surAmount =Double.valueOf(b2.subtract(b4).doubleValue());
										 this.log.info("剩余可用额度:" + surAmount.toString());
										 merchantinfo.setPositionT1(surAmount.toString());
										 this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
		
									}else if ("10".equals(status)) {
										surAmount = Double.valueOf(b2.subtract(b4).doubleValue());
										this.log.info("剩余可用额度:" + surAmount.toString());
										merchantinfo.setPositionT1(surAmount.toString());
										this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
									}else if ("20".equals(status)) {
										this.log.info("每笔代付手续费:" + b3);
										surAmount = Double.parseDouble(merchantinfo.getPositionT1())
												- Double.parseDouble(merchantinfo.getPoundage()) * 100;
										this.log.info("剩余额度:" + surAmount);
										merchantinfo.setPositionT1(surAmount.toString());
									    this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
									}
									
				
				
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						log.info("代付金额小于实际额度，请充值后在支付！");
						result.put("004", "代付金额小于实际额度，请充值后在支付！");
						return result;
					}
				} else {
					// 请求参数为空
					logger.info("商户没有开启代付，" + merchantinfo.getMercId());
					result.put("01", "还没有开启代付，请先开启代付，或者等待客服审核！");
					return result;
				}

			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				result.put("01", "还没有进行实名认证，请先去进行实名认证，或者等待客服审核！");
				return result;
			}
			/**
			 * 插入代付数据信息
			 */
			model.setCount("1");
			model.setAmount(originalinfo.getAmount());
			model.setCardno(originalinfo.getCardNo());
			model.setRealname(originalinfo.getName());
			model.setPayamount("-" + originalinfo.getAmount());
			model.setOagentno("100333");
			if (surAmount != null) {
				model.setPosition(surAmount.toString());
			}
			if (b3 != null) {
				model.setPayCounter(b3.toString());// 手续费
			}
			model.setRecordDescription(merchantinfo.getPoundage());
			model.setRemarks("T1");
			model.setTransactionType("代付");
			model.setResponsecode("200");
			pmsDaifuMerchantInfoDao.insert(model);
		}

		return result;
	}
	public int UpdateDaifu(DaifuRequestEntity daifu) throws Exception {

		log.info("原始数据:" + daifu);

		PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

		log.info("上送的批次号:" + daifu.getV_batch_no());

		pdf.setBatchNo(daifu.getV_batch_no());
		pdf.setResponsecode(daifu.getResponsecode());
		return pmsDaifuMerchantInfoDao.update(pdf);
	}

}

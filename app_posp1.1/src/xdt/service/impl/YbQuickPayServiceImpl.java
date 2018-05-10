package xdt.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

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
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.quickpay.yb.entity.PayRequestEntity;
import xdt.quickpay.yb.util.YeepayService;
import xdt.service.HfQuickPayService;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IYbQuickPayService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.servlet.AppPospContext;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;

@Service
public class YbQuickPayServiceImpl extends BaseServiceImpl implements IYbQuickPayService {

	private Logger logger = Logger.getLogger(YbQuickPayServiceImpl.class);

	public static final String[] TRADEORDER = { "parentMerchantNo", "merchantNo", "orderId", "orderAmount",
			"timeoutExpress", "requestDate", "redirectUrl", "notifyUrl", "goodsParamExt", "paymentParamExt",
			"industryParamExt", "memo", "riskParamExt", "csUrl" };
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
	@Resource
	private HfQuickPayService payService;

	@Override
	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {
		// TODO Auto-generated method stub
		logger.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	@Override
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
		// TODO Auto-generated method stub
		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		logger.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	@Override
	public Map<String, String> updateHandle(PayRequestEntity register) throws Exception {
		// TODO Auto-generated method stub

		Map<String, String> retMap = new HashMap<String, String>();
		// 金额
		// String acount = pay.get;
		// 商户订单号
		logger.info("******************根据商户号查询");

		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(register.getOrderId());
		orig.setPid(register.getMerchantNo());

		if (originalDao.selectByOriginal(orig) != null) {
			logger.info("下单重复");
			return setResp("03", "下单重复");
		}

		// String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
		// 业务号（2位）+业务细分（1位）+时间戳（13位）
		// 总共16位
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(register.getOrderId());// 原始数据的订单编号
		original.setOrderId(register.getOrderId()); // 为主键
		original.setPid(register.getMerchantNo());
		original.setOrderTime(register.getRequestDate());
		original.setOrderAmount(register.getOrderAmount());
		original.setPageUrl(register.getRedirectUrl());
		original.setBgUrl(register.getNotityUrl());
		originalDao.insert(original);

		// 根据商户号查询
		String mercId = register.getMerchantNo();

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
				String factAmount = "" + new BigDecimal(register.getOrderAmount()).multiply(new BigDecimal(100));
				// 查询商户路由
				PmsBusinessPos pmsBusinessPos = selectKey(register.getMerchantNo());
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
				pmsAppTransInfo.setOrderid(register.getOrderId());// 设置订单号
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay.getTypeCode());
				BigDecimal factBigDecimal = new BigDecimal(factAmount);
				BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

				pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
				pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
				pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
				pmsAppTransInfo.setSettlementState("T1");

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

					String isTop = appRateConfig.getIsTop();
					String rate = appRateConfig.getRate();
					String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
					paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
					String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
					Double minPoundage = 0.0; // 附加费

					if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
							&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
						if (StringUtils.isNotBlank(minPoundageStr)) {
							minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
						} else {
							// 若查到的是空值，直接返回错误
							logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
						}
					}

					BigDecimal payAmount = new BigDecimal("0");
					BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
					// 费率
					BigDecimal fee = new BigDecimal(0);

					String rateStr = "";
					// 计算结算金额
					if ("1".equals(isTop)) {

						rateStr = rate + "-" + topPoundage;
						// 是封顶费率类型
						fee = new BigDecimal(rate).multiply(dfactAmount);

						if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
							// 手续费大于封顶金额，按封顶金额处理
							payAmount = dfactAmount
									.subtract(new BigDecimal(topPoundage).subtract(new BigDecimal(minPoundage)));
							fee = new BigDecimal(topPoundage).add(new BigDecimal(minPoundage));
						} else {
							// 按当前费率处理
							rateStr = rate;
							fee.add(new BigDecimal(minPoundage));
							payAmount = dfactAmount.subtract(fee);
						}

					} else {
						// 按当前费率处理
						double daifu = Double.parseDouble(merchantinfo.getCounter());
						// 按当前费率处理
						rateStr = rate;
						BigDecimal num = dfactAmount.multiply(new BigDecimal(rate));
						if (num.doubleValue() / 100 >= daifu) {
									fee = num;
							  } else {
									fee = new BigDecimal(daifu * 100);
								}
								payAmount = dfactAmount.subtract(fee);
								logger.info("清算金额:" + paymentAmount);
								if (payAmount.doubleValue() < 0) {
									payAmount = new BigDecimal(0.00);
								}			
						}
					// 设置结算金额
					pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
					pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
					pmsAppTransInfo.setPoundage(fee.toString());
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
						pospTransInfo.setTransOrderId(register.getOrderId());
						pospTransInfo.setResponsecode("99");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
					} else {
						// 不存在流水，生成一个流水
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
						// 设置上送流水号
						pospTransInfo.setTransOrderId(register.getOrderId());
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

						logger.info("上游通道商户号:" + pmsBusinessPos.getChannelnum());
						switch (pmsBusinessPos.getBusinessnum()) {
						case "10000466938":// 易宝快捷
							String orderId = register.getOrderId();
							String orderAmount = register.getOrderAmount();
							String timeoutExpress =register.getTimeoutExpress();
							String requestDate = register.getRequestDate();
							// String redirectUrl = "http://www.lssc888.com/shop/control/yibao_return.php";
							// String notifyUrl = "http://www.lssc888.com/shop/control/yibao_notify.php";
							String redirectUrl = "http://www.lssc888.com/shop/control/return/yibao_return_new.php";
							String notifyUrl = "http://www.lssc888.com/shop/control/yibao_pay2_notify_return.php";
							String goodsName = register.getGoodsName();
							String goodsDesc = register.getGoodsDesc();
							String paymentParamExt = "";
							String bizSource = "";
							String bizEntity = "";
							String memo = "";
							String riskParamExt = "";
							String csUrl = "";
							String goodsParamExt = "{\"goodsName\":\"" + goodsName + "\",\"goodsDesc\":\""
									+ goodsDesc + "\"}";
							String industryParamExt = "{\"bizSource\":\"" + bizSource + "\",\"bizEntity\":\""
									+ bizEntity + "\"}";

							logger.info("goodsParamExt:" + goodsParamExt);
							Map<String, String> params = new HashMap<>();
							params.put("orderId", orderId);
							params.put("orderAmount", orderAmount);
							params.put("timeoutExpress", "");
							params.put("requestDate", requestDate);
							params.put("redirectUrl", redirectUrl);
							params.put("notifyUrl", notifyUrl);
							params.put("goodsParamExt", goodsParamExt);
							params.put("paymentParamExt", paymentParamExt);
							params.put("industryParamExt", industryParamExt);
							params.put("memo", memo);
							params.put("riskParamExt", riskParamExt);
							params.put("csUrl", csUrl);

							logger.info("token上送的数据:" + params);
							String uri = "/rest/v1.0/std/trade/order";
							Map<String, String> result = YeepayService.requestYOP(params, uri, TRADEORDER);
							logger.info("上游返回的数据:" + result);
							if ("OPR00000".equals(result.get("code"))) {
								String token = result.get("token");
								logger.info("获取易宝返回的token：" + token);
								String parentMerchantNo = "10018465070";
								String merchantNo = "10018465070";
								// String token = request.getParameter("token");
								String timestamp = String.valueOf(Math.round(new Date().getTime() / 1000));
								String directPayType = "YJZF";
								String cardType =register.getCardType();
								String userNo = register.getUserNo();
								String userType = register.getUserType();
								String appId = "";
								String openId = "";
								String clientId = "";
								String ext = "";
								params = new HashMap<String, String>();
								params.put("parentMerchantNo", parentMerchantNo);
								params.put("merchantNo", merchantNo);
								params.put("token", token);
								params.put("timestamp", timestamp);
								params.put("directPayType", directPayType);
								params.put("cardType", cardType);
								params.put("userNo", userNo);
								params.put("userType", userType);
								params.put("ext", ext);
								String url = YeepayService.getUrl(params);
								logger.info("向上游发送的数据:" + url);
								retMap.put("path", url);
							}
							break;
						default:
							break;
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

	@Override
	public void otherInvoke(String orderId,String status) throws Exception {
		// TODO Auto-generated method stub
		logger.info("易宝返回的订单号" + orderId);
		logger.info("易宝返回的状态码" + status);
		// 流水表transOrderId
		//String transOrderId = result.getOrderId();
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(orderId);
		logger.info("流水表信息" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		logger.info("订单表信息" + pmsAppTransInfo);
		// 查询结果成功
		if ("SUCCESS".equals(status)) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(status);
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
				pospTransInfo.setPospsn(orderId);
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("FAIL".equals(status)) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(status);
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(orderId);
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
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
		result.put("code", respCode);
		result.put("message", respInfo);
		return result;
	}

}

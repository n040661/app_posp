package xdt.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import xdt.dao.IAmountLimitControlDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayTypeControlDao;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.pufa.PayRequestEntity;
import xdt.dto.pufa.QueryRequestEntity;
import xdt.dto.pufa.RefundRequestEntity;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.pufa.PuFaThread;
import xdt.pufa.PufaPayService;
import xdt.pufa.base.PufaFieldDefine;
import xdt.schedule.ThreadPool;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.PufaService;
import xdt.util.Constants;
import xdt.util.PaymentCodeEnum;
import xdt.util.RateTypeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

/**
 * 浦发业务实现
 * 
 * @Description
 * @author Shiwen .Li
 * @date 2016年9月10日 上午11:57:53
 * @version V1.3.1
 */
@Service
public class PufaServiceImpl extends BaseServiceImpl implements PufaService {

	public static final Logger logger = Logger.getLogger(PufaServiceImpl.class);

	// 浦发上游交易
	@Resource
	private PufaPayService pay;

	@Resource
	private IMerchantMineDao merchantMineDao;

	// 商户信息服务层
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;

	// 原始数据
	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	private IPmsAppAmountAndRateConfigDao pmsAppAmountAndRateConfigDao;// 商户费率配置

	@Resource
	private IAppRateConfigDao appRateConfigDao;// 费率

	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao; // 业务配置服务层

	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;// 流水

	@Resource
	private IPublicTradeVerifyService publicTradeVerifyService;

	@Resource
	private IPayTypeControlDao payTypeControlDao;// 开关

	@Resource
	private IAmountLimitControlDao amountLimitControlDao;// 最大值最小值总开关判断

	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;

	@Override
	public Map<String, Object> updatePay(PayRequestEntity reqeustInfo) {

		Map<String, Object> result = new HashMap<String, Object>();

		String orderid = "";// 订单号
		orderid = UtilMethod.getOrderid("171"); // 10业务号2业务细; 订单号 现根据规则生成订单号

		logger.info("处理二维码生成");

		logger.info("根据商户号查询");
		String mercId = reqeustInfo.getMerchantId();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
					.searchList(merchantinfo);

			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(reqeustInfo.getOrderId());
				oriInfo.setPid(reqeustInfo.getMerchantId());

				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					logger.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfo(reqeustInfo, orderid, mercId);

					// 校验商户金额限制
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
					paramMap.put("businesscode",
							TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
					paramMap.put("oAgentNo", oAgentNo);
					// 商户 网购 业务信息
					Map<String, String> resultMap = merchantMineDao
							.queryBusinessInfo(paramMap);

					String quickRateType = resultMap.get("QUICKRATETYPE");// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao
							.getByRateTypeAndoAgentNo(appRate);

					paramMap = new HashMap<String, String>();

					paramMap.put("mercid", mercId);
					paramMap.put("businesscode",
							TradeTypeEnum.merchantCollect.getTypeCode());

					if ("0".equals(reqeustInfo.getPayType())) {
						paramMap.put("paymentcode",
								PaymentCodeEnum.zhifubaoPay.getTypeCode());
					} else {
						paramMap.put("paymentcode",
								PaymentCodeEnum.weixinPay.getTypeCode());
					}

					// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
					AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
							.queryAmountAndStatus(paramMap);

					if (appRateTypeAndAmount != null) {

						String status = appRateTypeAndAmount.getStatus();// 此业务是否开通

						String payStatus = appRateTypeAndAmount.getPayStatus();// 此支付方式是否开通

						// 判断此业务O单是否开通（总）
						ResultInfo resultInfoForOAgentNo = publicTradeVerifyService
								.moduleVerifyOagent(
										TradeTypeEnum.merchantCollect, oAgentNo);

						if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
							if (StringUtils.isEmpty(resultInfoForOAgentNo
									.getMsg())) {
								logger.error("此功能暂时关闭!");
								result.put("respCode", "05");
								result.put("respMsg", "此功能暂时关闭");
							} else {
								logger.error(resultInfoForOAgentNo.getMsg());
								result.put("respCode", "05");
								result.put("respMsg",
										resultInfoForOAgentNo.getMsg());
							}
						} else {

							if ("1".equals(status)) {

								// 判断支付方式时候开通总开关
								ResultInfo payCheckResult = null;

								if ("0".equals(reqeustInfo.getPayType())) {
									payCheckResult = payTypeControlDao
											.checkLimit(oAgentNo,
													PaymentCodeEnum.zhifubaoPay
															.getTypeCode());
								} else {
									payCheckResult = payTypeControlDao
											.checkLimit(oAgentNo,
													PaymentCodeEnum.weixinPay
															.getTypeCode());
								}

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									logger.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(
											reqeustInfo.getTranAmt());// 收款金额
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao
											.checkLimit(
													oAgentNo,
													payAmt,
													TradeTypeEnum.merchantCollect
															.getTypeCode());
									// 返回不为0，一律按照交易失败处理
									if (!resultInfo.getErrCode().equals("0")) {
										result.put("respCode", "08");
										result.put("respMsg", "交易金额不在申请的范围之内");
										logger.info("交易金额不在申请的范围之内");

									} else {

										// 商户渠道支付方式
										// 商户渠道交易类型
										// 验证支付方式是否开启

										ResultInfo resultinfo = null;

										if ("0".equals(reqeustInfo.getPayType())) {
											payCheckResult = iPublicTradeVerifyService
													.payTypeVerifyMer(
															PaymentCodeEnum.zhifubaoPay,
															mercId);
										} else {
											payCheckResult = iPublicTradeVerifyService
													.payTypeVerifyMer(
															PaymentCodeEnum.weixinPay,
															mercId);
										}

										if ("0".equals(resultinfo.getErrCode())) {
											if ("0".equals(payStatus)) {
												// 有效
												// MIN_AMOUNT,MAX_AMOUNT ,RATE
												// ,STATUS
												String rateStr = appRateConfig
														.getRate(); // 商户费率 RATE

												BigDecimal min_amount = new BigDecimal(
														appRateTypeAndAmount
																.getMinAmount());// 最低收款金额
												// MIN_AMOUNT
												BigDecimal max_amount = new BigDecimal(
														appRateTypeAndAmount
																.getMaxAmount());// 最高收款金额
												// MAX_AMOUNT

												if (min_amount
														.compareTo(payAmt) != 1) {// 判断收款金额是否大于最低收款金额
													// 大于等于执行
													// 小于不执行
													if (payAmt
															.compareTo(max_amount) != 1) {
														// 组装报文
														String totalAmount = reqeustInfo
																.getTranAmt(); // 交易金额

														PmsAppTransInfo appTransInfo = this
																.insertOrder(
																		orderid,
																		totalAmount,
																		mercId,
																		rateStr,
																		oAgentNo);

														if (appTransInfo != null) {
															// 调用浦发
															this.payProcess(
																	reqeustInfo,
																	result,
																	appTransInfo);
														} else {
															// 交易金额小于收款最低金额
															result.put(
																	"respCode",
																	"11");
															result.put(
																	"respMsg",
																	"生成订单流水失败");
															logger.info("生成订单流水失败");
														}

													} else {

														// 交易金额小于收款最低金额
														result.put("respCode",
																"10");
														result.put("respMsg",
																"交易金额大于收款最高金额");
														logger.info("交易金额大于收款最高金额");
													}

												} else {
													// 交易金额小于收款最低金额
													result.put("respCode", "09");
													result.put("respMsg",
															"交易金额小于收款最低金额");
													logger.info("交易金额小于收款最低金额");
												}

											} else {
												// 交易金额小于收款最低金额
												result.put("respCode", "12");
												result.put("respMsg", "商户收款关闭");
												logger.info("商户交易关闭");
											}
										} else {
											// 交易金额小于收款最低金额
											result.put("respCode", "13");
											result.put("respMsg", "扫码支付关闭");
											logger.info("扫码支付关闭");
										}

									}
								}

							} else {
								logger.error("此功能暂未开通");
								result.put("respCode", "06");
								result.put("respMsg", "此功能暂未开通");

							}

						}

					} else {
						logger.error("没有找到商户费率");
						result.put("respCode", "04");
						result.put("respMsg", "没有找到商户费率");
					}

				} else {
					logger.error("不是正式商户!");
					result.put("respCode", "03");
					result.put("respMsg", "不是正式商户");
				}

			} else {
				logger.error("商户不存在!");
				result.put("respCode", "02");
				result.put("respMsg", "商户不存在");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("处理异常", e);
		}

		return result;
	}

	/**
	 * 
	 * @Description 插入原始订单表信息
	 * @author Administrator
	 * @param reqeustInfo
	 * @param orderid
	 * @param mercId
	 * @throws Exception
	 */
	private int saveOriginAlInfo(PayRequestEntity reqeustInfo, String orderid,
			String mercId) throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(reqeustInfo.getOrderId());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType(reqeustInfo.getPayType());
		Double amt = Double.parseDouble(reqeustInfo.getTranAmt());// 单位分

		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
	}

	/**
	 * 处理扫码支付
	 * 
	 * @Description
	 * @author Administrator
	 * @param reqeustInfo
	 *            下游交易参数
	 * @param result
	 *            返回结果
	 * @param pospTransInfo
	 *            流水数据
	 * @throws Exception
	 */
	private void payProcess(PayRequestEntity reqeustInfo,
			Map<String, Object> result, PmsAppTransInfo pmsAppTransInfo)
			throws Exception {
		pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo
				.getOrderid());

		logger.info("请求交易map");
		Map<String, Object> requestMap = new HashMap<String, Object>();

		// 商户号-我们商户号，将用来筛选路由
		requestMap.put(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD,
				pmsAppTransInfo.getMercid());

		requestMap.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID,
				pmsAppTransInfo.getOrderid());

		requestMap.put(PufaFieldDefine.PF_REQ_BODY_AUTH_CODE,
				reqeustInfo.getAuthCode());

		requestMap.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT,
				pmsAppTransInfo.getOrderamount());

		// 交易代码
		requestMap.put(PufaFieldDefine.PF_HEAD_TRAN_CD, "1131");// 1131
		// 业务代码
		// 组装上送参数
		switch (reqeustInfo.getPayType()) {
		case "0":
			// 0 支付宝
			requestMap.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000008");// -支付宝
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay
					.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay
					.getTypeCode());
			break;
		case "1":
			// 1微信
			requestMap.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000007");// -微信
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay
					.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay
					.getTypeCode());
			break;

		}

		// 产品代码
		requestMap.put(PufaFieldDefine.PF_HEAD_PROD_CD, "1151");//

		Map<String, Object> transResult = pay.createMerpay(requestMap);
		logger.info("上送返回结果");
		// 启线程查询订单状态
		logger.info(pmsAppTransInfo);
		logger.info(transResult);

		// 填充通道
		pmsAppTransInfo.setChannelNum((String) transResult
				.get(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD));

		pmsAppTransInfoDao.update(pmsAppTransInfo);

		// 启线程查询订单状态
		ThreadPool.executor(new PuFaThread(pmsAppTransInfo.getOrderid(), this,
				pmsAppTransInfoDao));

		logger.info("设置result map 返回值");
		result.put("merchantId", reqeustInfo.getMerchantId());
		result.put("orderId", reqeustInfo.getOrderId());
		result.put("transOrderId", pmsAppTransInfo.getOrderid());
		result.put("respCode",
				transResult.get(PufaFieldDefine.PF_REQ_BODY_RET_CD));
		result.put("respMsg",
				transResult.get(PufaFieldDefine.PF_REQ_BODY_RET_MSG));
		result.put("buyerUser",
				transResult.get(PufaFieldDefine.PF_REQ_BODY_BUYER_USER));
		result.put("payTime",
				transResult.get(PufaFieldDefine.PF_REQ_BODY_PAY_TIME));

		OriginalOrderInfo original = originalDao
				.getOriginalOrderInfoByOrderid(pmsAppTransInfo.getOrderid());
		if (original != null) {
			original.setByUser((String) result.get("buyerUser"));
			logger.info("修改原始订单" + original);
			originalDao.update(original);
		}

		sign(result);

	}

	@Override
	public Map<String, Object> updateFlushes() {

		Map<String, Object> result = new HashMap<String, Object>();

		// logger.info("冲正交易请求map");
		// Map<String, Object> requestMap = new HashMap<String, Object>();

		// Map<String,Object> transResult=pay..flushes(requestMap);

		return result;
	}

	@Override
	public Map<String, Object> query(QueryRequestEntity requestInfo)
			throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();

		// 1 查询商户

		// 2 查询原始交易

		// 3 查询订单 返回结果
		OriginalOrderInfo oriInfo = new OriginalOrderInfo();
		oriInfo.setMerchantOrderId(requestInfo.getOrderId());
		oriInfo.setPid(requestInfo.getMerchantId());

		oriInfo = originalDao.selectByOriginal(oriInfo);
		logger.info("原始订单信息" + oriInfo);
		if (oriInfo == null) {
			logger.error("订单不存在!");
			result.put("respCode", "14");
			result.put("respMsg", "订单不存在!");
		} else {
			// 查询返回结果
			PmsAppTransInfo orderinfo = pmsAppTransInfoDao
					.searchOrderInfo(oriInfo.getOrderId());
			logger.info("订单本地信息" + orderinfo);
			if ("0".equals(orderinfo.getStatus())) {
				logger.info("交易成功");
				result.put("transOrderId", oriInfo.getOrderId());
				result.put("orderId", oriInfo.getMerchantOrderId());
				result.put("merchantId", oriInfo.getPid());
				result.put("respCode", "0000");
				result.put("respMsg", "success");
				result.put("buyerUser", oriInfo.getByUser());
				result.put("payTime", orderinfo.getFinishtime());
			} else if ("1".equals(orderinfo.getStatus())) {
				logger.info("交易失败");
				result.put("transOrderId", oriInfo.getOrderId());
				result.put("orderId", oriInfo.getMerchantOrderId());
				result.put("merchantId", oriInfo.getPid());
				result.put("respCode", "0001");
				result.put("respMsg", "fail");
				result.put("buyerUser", "");
				result.put("payTime", orderinfo.getFinishtime());
			} else if ("2".equals(orderinfo.getStatus())) {
				logger.info("扫码创建订单 等待支付");
				result.put("transOrderId", oriInfo.getOrderId());
				result.put("orderId", oriInfo.getMerchantOrderId());
				result.put("merchantId", oriInfo.getPid());
				result.put("respCode", "0002");
				result.put("respMsg", "wait");
				result.put("buyerUser", "");
				result.put("payTime", orderinfo.getFinishtime());
			}
			logger.info("查询返回结果:" + result);
			// 签名
			sign(result);
		}
		return result;
	}

	@Override
	public Map<String, Object> updateTwoDimensionCode(
			PayRequestEntity reqeustInfo) {
		Map<String, Object> result = new HashMap<String, Object>();

		logger.info("处理二维码生成");

		logger.info("根据商户号查询");

		String orderid = "";// 订单号
		orderid = UtilMethod.getOrderid("171"); // 10业务号2业务细; 订单号 现根据规则生成订单号

		logger.info("根据商户号查询");
		String mercId = reqeustInfo.getMerchantId();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
					.searchList(merchantinfo);

			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(reqeustInfo.getOrderId());
				oriInfo.setPid(reqeustInfo.getMerchantId());

				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					logger.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfo(reqeustInfo, orderid, mercId);

					// 校验商户金额限制
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
					paramMap.put("businesscode",
							TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
					paramMap.put("oAgentNo", oAgentNo);
					// 商户 网购 业务信息
					Map<String, String> resultMap = merchantMineDao
							.queryBusinessInfo(paramMap);

					String quickRateType = resultMap.get("QUICKRATETYPE");// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao
							.getByRateTypeAndoAgentNo(appRate);

					paramMap.put("mercid", mercId);
					paramMap.put("businesscode",
							TradeTypeEnum.merchantCollect.getTypeCode());
					if ("0".equals(reqeustInfo.getPayType())) {
						paramMap.put("paymentcode",
								PaymentCodeEnum.zhifubaoPay.getTypeCode());
					} else {
						paramMap.put("paymentcode",
								PaymentCodeEnum.weixinPay.getTypeCode());
					}

					// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
					AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
							.queryAmountAndStatus(paramMap);

					if (appRateTypeAndAmount != null) {

						String status = appRateTypeAndAmount.getStatus();// 此业务是否开通

						// String statusMessage =
						// appRateTypeAndAmount.getMessage();//此业务是否开通的描述

						String payStatus = appRateTypeAndAmount.getPayStatus();// 此支付方式是否开通

						// 判断此业务O单是否开通（总）
						ResultInfo resultInfoForOAgentNo = publicTradeVerifyService
								.moduleVerifyOagent(
										TradeTypeEnum.merchantCollect, oAgentNo);

						if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
							if (StringUtils.isEmpty(resultInfoForOAgentNo
									.getMsg())) {
								logger.error("此功能暂时关闭!");
								result.put("respCode", "05");
								result.put("respMsg", "此功能暂时关闭");
							} else {
								logger.error(resultInfoForOAgentNo.getMsg());
								result.put("respCode", "05");
								result.put("respMsg",
										resultInfoForOAgentNo.getMsg());
							}
						} else {

							if ("1".equals(status)) {

								// 判断支付方式时候开通总开关
								ResultInfo payCheckResult = null;

								if ("0".equals(reqeustInfo.getPayType())) {
									payCheckResult = payTypeControlDao
											.checkLimit(oAgentNo,
													PaymentCodeEnum.zhifubaoPay
															.getTypeCode());
								} else {
									payCheckResult = payTypeControlDao
											.checkLimit(oAgentNo,
													PaymentCodeEnum.weixinPay
															.getTypeCode());
								}

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									logger.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(
											reqeustInfo.getTranAmt());// 收款金额
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao
											.checkLimit(
													oAgentNo,
													payAmt,
													TradeTypeEnum.merchantCollect
															.getTypeCode());
									// 返回不为0，一律按照交易失败处理
									if (!resultInfo.getErrCode().equals("0")) {
										result.put("respCode", "08");
										result.put("respMsg", "交易金额不在申请的范围之内");
										logger.info("交易金额不在申请的范围之内");

									} else {

										// 商户渠道支付方式
										// 商户渠道交易类型
										// 验证支付方式是否开启

										ResultInfo resultinfo = null;
										if ("0".equals(reqeustInfo.getPayType())) {
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(
															PaymentCodeEnum.zhifubaoPay,
															mercId);
										} else {
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(
															PaymentCodeEnum.weixinPay,
															mercId);
										}

										if ("0".equals(resultinfo.getErrCode())) {
											if ("0".equals(payStatus)) {
												// 有效
												// MIN_AMOUNT,MAX_AMOUNT ,RATE
												// ,STATUS
												String rateStr = appRateConfig
														.getRate(); // 商户费率 RATE

												BigDecimal min_amount = new BigDecimal(
														appRateTypeAndAmount
																.getMinAmount());// 最低收款金额
												// MIN_AMOUNT
												BigDecimal max_amount = new BigDecimal(
														appRateTypeAndAmount
																.getMaxAmount());// 最高收款金额
												// MAX_AMOUNT

												if (min_amount
														.compareTo(payAmt) != 1) {// 判断收款金额是否大于最低收款金额
													// 大于等于执行
													// 小于不执行
													if (payAmt
															.compareTo(max_amount) != 1) {
														// 组装报文
														String totalAmount = reqeustInfo
																.getTranAmt(); // 交易金额

														PmsAppTransInfo appTransInfo = this
																.insertOrder(
																		orderid,
																		totalAmount,
																		mercId,
																		rateStr,
																		oAgentNo);

														if (appTransInfo != null) {
															// 处理生成二维码
															this.twoDimensionCodeProcess(
																	reqeustInfo,
																	result,
																	appTransInfo);
														} else {
															// 交易金额小于收款最低金额
															result.put(
																	"respCode",
																	"11");
															result.put(
																	"respMsg",
																	"生成订单流水失败");
															logger.info("生成订单流水失败");
														}

													} else {

														// 交易金额小于收款最低金额
														result.put("respCode",
																"10");
														result.put("respMsg",
																"交易金额大于收款最高金额");
														logger.info("交易金额大于收款最高金额");
													}

												} else {
													// 交易金额小于收款最低金额
													result.put("respCode", "09");
													result.put("respMsg",
															"交易金额小于收款最低金额");
													logger.info("交易金额小于收款最低金额");
												}

											} else {
												// 交易金额小于收款最低金额
												result.put("respCode", "12");
												result.put("respMsg", "商户收款关闭");
												logger.info("商户交易关闭");
											}
										} else {
											// 交易金额小于收款最低金额
											result.put("respCode", "13");
											result.put("respMsg", "扫码支付关闭");
											logger.info("扫码支付关闭");
										}

									}
								}

							} else {
								logger.error("此功能暂未开通");
								result.put("respCode", "06");
								result.put("respMsg", "此功能暂未开通");

							}

						}

					} else {
						logger.error("没有找到商户费率");
						result.put("respCode", "04");
						result.put("respMsg", "没有找到商户费率");
					}

				} else {
					logger.error("不是正式商户!");
					result.put("respCode", "03");
					result.put("respMsg", "不是正式商户");
				}

			} else {
				logger.error("商户不存在!");
				result.put("respCode", "02");
				result.put("respMsg", "商户不存在");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("处理异常", e);
		}

		return result;

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
	private void twoDimensionCodeProcess(PayRequestEntity reqeustInfo,
			Map<String, Object> result, PmsAppTransInfo pmsAppTransInfo)
			throws Exception {

		pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo
				.getOrderid());
		logger.info("请求交易生成二维码map");
		Map<String, Object> requestMap = new HashMap<String, Object>();

		// 商户号-我们商户号，将用来筛选路由
		requestMap.put(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD,
				pmsAppTransInfo.getMercid());
		// 上送订单号
		requestMap.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID,
				pmsAppTransInfo.getOrderid());
		// 交易金额
		requestMap.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT,
				pmsAppTransInfo.getOrderamount());

		requestMap.put(PufaFieldDefine.PF_HEAD_TRAN_CD, "7131");
		// 业务代码

		// 组装上送参数
		switch (reqeustInfo.getPayType()) {
		case "0":
			// 0 支付宝
			requestMap.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000008");// -支付宝
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay
					.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay
					.getTypeCode());
			break;
		case "1":
			// 1微信
			requestMap.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000007");// -微信
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay
					.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay
					.getTypeCode());
			break;

		default:
			break;
		}

		// 产品代码
		requestMap.put(PufaFieldDefine.PF_HEAD_PROD_CD, "1151");//

		Map<String, Object> transResult = pay.createUserPayOrder(requestMap);
		logger.info("上送返回结果");

		// 启线程查询订单状态
		String status = (String) transResult
				.get(PufaFieldDefine.PF_REQ_BODY_RET_CD);
		logger.info(pmsAppTransInfo);
		logger.info(transResult);

		pmsAppTransInfo.setChannelNum((String) transResult
				.get(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD));

		pmsAppTransInfoDao.update(pmsAppTransInfo);

		if ("0000".equals(status)) {

			// 启线程查询订单状态
			ThreadPool.executor(new PuFaThread(pmsAppTransInfo.getOrderid(),
					this, pmsAppTransInfoDao));

		} else {
			logger.info("生成二维码失败");
		}

		logger.info("设置result map 返回值");
		result.put("merchantId", reqeustInfo.getMerchantId());
		result.put("orderId", reqeustInfo.getOrderId());
		result.put("transOrderId", pmsAppTransInfo.getOrderid());
		result.put("respCode",
				transResult.get(PufaFieldDefine.PF_REQ_BODY_RET_CD));
		result.put("respMsg",
				transResult.get(PufaFieldDefine.PF_REQ_BODY_RET_MSG));
		result.put("url",
				transResult.get(PufaFieldDefine.PF_REQ_BODY_PAY_ORDER_ID));
		OriginalOrderInfo original = originalDao
				.getOriginalOrderInfoByOrderid(pmsAppTransInfo.getOrderid());
		if (original != null) {
			original.setUrl((String) result.get("url"));
			logger.info("修改原始订单" + original);
			originalDao.update(original);
		}
		// 签名
		sign(result);

	}

	/**
	 * 订单入库
	 * 
	 * @Description
	 * @author Administrator
	 * @param orderid
	 * @param payamount
	 * @param mercId
	 * @param rateStr
	 * @param businessnum
	 * @param oAgentNo
	 * @return
	 * @throws Exception
	 */
	public PmsAppTransInfo insertOrder(String orderid, String payamount,
			String mercId, String rateStr, String oAgentNo) throws Exception {
		// 查询商户费率
		BigDecimal rate = new BigDecimal(rateStr);
		BigDecimal amount = new BigDecimal(payamount);

		// 成功后订到入库app后台
		PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();

		pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect
				.getTypeName());
		pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
		pmsAppTransInfo.setOrderid(orderid);// 上送的订单号

		pmsAppTransInfo.setReasonofpayment(TradeTypeEnum.merchantCollect
				.getTypeName());
		pmsAppTransInfo.setMercid(mercId);
		pmsAppTransInfo.setFactamount(payamount);// 实际金额 按分为最小单位 例如：1元=100分
													// 采用100
		pmsAppTransInfo.setTradetypecode("1");
		pmsAppTransInfo.setOrderamount(payamount);// 订单金额 按分为最小单位 例如：1元=100分
													// 采用100
		pmsAppTransInfo.setStatus(Constants.ORDERINITSTATUS);// 订单初始化状态
		pmsAppTransInfo.setoAgentNo(oAgentNo);// o单编号

		BigDecimal poundage = amount.multiply(rate);// 手续费
		BigDecimal b = new BigDecimal(0);
		BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
		int fee1 = poundage.setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);
		//结算金额
		BigDecimal payAmount = null;
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
				.searchList(merchantinfo);

		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			// 正式商户
			merchantinfo = merchantList.get(0);
			if (merchantinfo.getCounter() != null) {
				int num = Integer.parseInt(merchantinfo.getCounter());
				if (fee1 < num) {
					b = new BigDecimal(Integer.toString(num));
					payAmount = dfactAmount.subtract(b);
				} else {
					b=poundage;
					payAmount = dfactAmount.subtract(b);
				}
			}
		}
		pmsAppTransInfo.setRate(rateStr);// 费率

		// 结算金额 按分为最小单位 例如：1元=100分 采用100 商户收款时给商户记账时减去费率(实际金额-手续费)
		pmsAppTransInfo.setPayamount(payAmount.toString());

		pmsAppTransInfo.setPoundage(b.toString());// 手续费 按分为最小单位
															// 例如：1元=100分 采用100
		String sendString = createJsonString(pmsAppTransInfo);

		try {
			if (pmsAppTransInfoDao.insert(pmsAppTransInfo) != 1) {
				logger.info("订单入库失败， 订单号：" + orderid + "，结束时间："
						+ UtilDate.getDateFormatter() + "。订单详细信息：" + sendString);
				throw new RuntimeException("手动抛出");
			}
		} catch (Exception e) {
			logger.info(
					"订单入库失败， 订单号：" + orderid + "，结束时间："
							+ UtilDate.getDateFormatter() + "。订单详细信息："
							+ sendString, e);
			throw new RuntimeException("手动抛出");
		}
		return pmsAppTransInfo;

	}

	/**
	 * 
	 * Description
	 * 
	 * @param requestEntity下游上送退款信息
	 * @return
	 * @throws Exception
	 * @see xdt.service.PufaService#refund(xdt.dto.pufa.RefundRequestEntity)
	 */
	@Override
	public Map<String, Object> updateRefund(RefundRequestEntity requestEntity)
			throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();

		String mercId = requestEntity.getMerchantId();

		OriginalOrderInfo oriInfo = new OriginalOrderInfo();
		oriInfo.setMerchantOrderId(requestEntity.getOrderId());
		oriInfo.setPid(requestEntity.getMerchantId());

		oriInfo = originalDao.selectByOriginal(oriInfo);

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		String oAgentNo = "";

		try {
			// 查询当前商户信息
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
					.searchList(merchantinfo);
			// 正式商户
			merchantinfo = merchantList.get(0);
			oAgentNo = merchantinfo.getoAgentNo();//
		} catch (Exception e) {
			logger.error("商户不存在!");
			result.put("respCode", "02");
			result.put("respMsg", "商户不存在");
			throw new RuntimeException("商户不存在!");
		}

		if (oriInfo != null) {
			logger.error("下单重复");
			result.put("respCode", "16");
			result.put("respMsg", "下单重复");
		} else {
			oriInfo = new OriginalOrderInfo();
			oriInfo.setPid(requestEntity.getMerchantId());
			oriInfo.setMerchantOrderId(requestEntity.getOrigOrderId());
			oriInfo = originalDao.selectByOriginal(oriInfo);
			if (oriInfo == null) {
				logger.error("原始交易不存在");
				result.put("respCode", "17");
				result.put("respMsg", "原始交易不存在");
			} else {

				String origOrderId = oriInfo.getOrderId();// 本地原始交易订单id

				String oriAmt = oriInfo.getOrderAmount();// 原始交易金额

				Double amt = Double.parseDouble(requestEntity.getTranAmt());// 单位分

				amt /= 100;

				if (amt <= Double.parseDouble(oriAmt)) {
					if (amt < 0) {
						logger.error("退款金额非法");
						result.put("respCode", "17");
						result.put("respMsg", "退款金额非法");
					} else {
						String orderid = "";// 订单号
						orderid = UtilMethod.getOrderid("173"); // 10业务号2业务细;
																// 订单号
																// 现根据规则生成订单号
						// 根据下游商户订单查询本地订单
						// 插入原始信息
						OriginalOrderInfo info = new OriginalOrderInfo();
						info.setPid(requestEntity.getMerchantId());
						info.setMerchantOrderId(requestEntity.getOrderId());
						info.setOrderId(orderid);
						info.setOrderTime(requestEntity.getTransTime());
						info.setPayType(requestEntity.getPayType());
						amt = Double.parseDouble(requestEntity.getTranAmt());// 单位分

						amt /= 100;
						DecimalFormat df = new DecimalFormat("######0.00");

						info.setOrderAmount(df.format(amt));
						// 重新组装退款请求
						if (originalDao.insert(info) != 0) {
							// 下退款订单
							PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();

							pmsAppTransInfo.setOrderid(orderid);
							if ("0".equals(requestEntity.getPayType())) {
								pmsAppTransInfo
										.setPaymenttype(PaymentCodeEnum.zhifubaoPay
												.getTypeName());
								pmsAppTransInfo
										.setPaymentcode(PaymentCodeEnum.zhifubaoPay
												.getTypeCode());
							} else {
								pmsAppTransInfo
										.setPaymenttype(PaymentCodeEnum.weixinPay
												.getTypeName());
								pmsAppTransInfo
										.setPaymentcode(PaymentCodeEnum.weixinPay
												.getTypeCode());

							}

							pmsAppTransInfo.setTradetype(TradeTypeEnum.refund
									.getTypeName());
							pmsAppTransInfo.setTradetime(UtilDate
									.getDateFormatter());
							pmsAppTransInfo.setOrderid(orderid);// 上送的订单号

							pmsAppTransInfo.setMercid(mercId);
							pmsAppTransInfo.setFactamount(requestEntity
									.getTranAmt());// 实际金额 按分为最小单位 例如：1元=100分
							// 采用100
							pmsAppTransInfo
									.setTradetypecode(TradeTypeEnum.refund
											.getTypeCode());
							pmsAppTransInfo.setOrderamount(requestEntity
									.getTranAmt());// 订单金额 按分为最小单位 例如：1元=100分
							// 采用100
							pmsAppTransInfo
									.setStatus(Constants.ORDERINITSTATUS);// 订单初始化状态
							pmsAppTransInfo.setoAgentNo(oAgentNo);// o单编号

							if (pmsAppTransInfoDao.insert(pmsAppTransInfo) != 0) {
								refundProcess(result, requestEntity,
										pmsAppTransInfo, origOrderId);
								logger.info("退款处理中");
							} else {
								logger.error("下单失败");
								result.put("respCode", "18");
								result.put("respMsg", "下单失败");
							}

						} else {
							logger.error("下单失败");
							result.put("respCode", "18");
							result.put("respMsg", "下单失败");
						}
					}
				} else {
					logger.error("退款金额非法");
					result.put("respCode", "17");
					result.put("respMsg", "退款金额非法");
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @Description
	 * @author Administrator
	 * @param result
	 *            返回结果
	 * @param requestEntity
	 *            本地上送退款信息
	 * @throws Exception 
	 */
	public void refundProcess(Map<String, Object> result,
			RefundRequestEntity requestEntity, PmsAppTransInfo pmsAppTransInfo,
			String origOrderId) throws Exception {

		logger.info("退款请求map");
		Map<String, Object> requestMap = new HashMap<String, Object>();

		requestMap.put(PufaFieldDefine.PF_HEAD_TRAN_CD, "3131");// 授权码-扫码设备读取
		requestMap.put(PufaFieldDefine.PF_HEAD_PROD_CD, "1151");//
		// 组装上送参数
		switch (requestEntity.getPayType()) {
		case "0":
			// 0 支付宝
			requestMap.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000008");// -支付宝
			break;
		case "1":
			// 1微信
			requestMap.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000007");// -支付宝
			break;

		default:
			break;
		}
		requestMap.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID,
				pmsAppTransInfo.getOrderid());
		requestMap.put(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID, origOrderId);
		requestMap.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT,
				requestEntity.getTranAmt());
		try {
			requestMap.put(PufaFieldDefine.PF_REQ_BODY_REFUND_REASON,
					new String(requestEntity.getRefundReason().getBytes(),
							"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("退款请求参数:" + requestMap);
		Map<String, Object> transResult = pay.refund(requestMap);

		logger.info("上送返回结果");

		// 启线程查询订单状态
		String status = (String) transResult
				.get(PufaFieldDefine.PF_REQ_BODY_RET_CD);
		logger.info(transResult);

		if ("0000".equals(status)) {
			// 启线程查询订单状态
			ThreadPool.executor(new PuFaThread(pmsAppTransInfo.getOrderid(),
					this, pmsAppTransInfoDao));
		} else {
			logger.info("扫码退款失败");
		}
		result.put("orderId", requestEntity.getOrderId());
		result.put("respCode",
				transResult.get(PufaFieldDefine.PF_REQ_BODY_RET_CD));
		result.put("respMsg",
				transResult.get(PufaFieldDefine.PF_REQ_BODY_RET_MSG));

		pmsAppTransInfo.setChannelNum((String) transResult
				.get(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD));
		pmsAppTransInfoDao.updateChanNum(pmsAppTransInfo);

		// 签名
		sign(result);
	}

	/**
	 * 定时任务用
	 * 
	 * @Description
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @throws Exception
	 */
	@Override
	public void updateOrderStatusByOrder(PmsAppTransInfo pmsAppTransInfo)
			throws Exception {

		// 请求交易map
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID,
				pmsAppTransInfo.getOrderid());
		// 交易代码
		requestMap.put(PufaFieldDefine.PF_HEAD_TRAN_CD, "5131");//
		// 产品代码
		requestMap.put(PufaFieldDefine.PF_HEAD_PROD_CD, "1151");//
		// 业务代码
		if ("3".equals(pmsAppTransInfo.getPaymentcode())) {
			requestMap.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000007");//
		} else {
			requestMap.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000008");//
		}
		// 查询返回结果
		Map<String, Object> transResult = pay.query(requestMap);

		if (!transResult.isEmpty()) {

			PospTransInfo pospTranInfo = pospTransInfoDAO
					.searchByOrderId(pmsAppTransInfo.getOrderid());

			String respCode = (String) transResult
					.get(PufaFieldDefine.PF_REQ_BODY_RET_CD);
			switch (respCode) {
			case "0000":
				logger.info("交易成功");
				updateOrderSuccess(pmsAppTransInfo, transResult, pospTranInfo);
				break;
			case "PF25":
				logger.info("交易不存在");
			case "PF98":
				logger.info("交易查询到商户  等待付款");
				updateOrderWaitPay(pmsAppTransInfo, transResult);
				break;
			default:
				updateOrderFail(pmsAppTransInfo);
				break;
			}

		}

	}

	/**
	 * 
	 * @Description 等待支付
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @param transResult
	 * @param pospTranInfo
	 */
	private void updateOrderWaitPay(PmsAppTransInfo pmsAppTransInfo,
			Map<String, Object> transResult) {
		logger.info("交易查询到商户  等待付款");
		pmsAppTransInfo.setStatus("2");
		logger.info("查询原始订单表");
		OriginalOrderInfo originalInfo = originalDao
				.getOriginalOrderInfoByOrderid(pmsAppTransInfo.getOrderid());
		originalInfo.setByUser((String) transResult
				.get(PufaFieldDefine.PF_REQ_BODY_BUYER_USER));
		logger.info("修改原始订单");
		logger.info(originalInfo);
		try {
			originalDao.update(originalInfo);
			pmsAppTransInfoDao.update(pmsAppTransInfo);
		} catch (Exception e) {
			logger.error("修改订单为失败", e);
		}
	}

	/**
	 * 
	 * @Description 修改订单状态为失败
	 * @author Administrator
	 * @param pmsAppTransInfo
	 *            订单
	 */
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

	/**
	 * 修改订单为成功状态
	 * 
	 * @Description
	 * @author Administrator
	 * @param pmsAppTransInfo
	 *            订单
	 * @param transResult
	 *            上游查询返回数据
	 * @param pospTranInfo
	 *            流水记录
	 * @throws Exception
	 */
	private void updateOrderSuccess(PmsAppTransInfo pmsAppTransInfo,
			Map<String, Object> transResult, PospTransInfo pospTranInfo)
			throws Exception {

		String respCode = (String) transResult
				.get(PufaFieldDefine.PF_REQ_BODY_RET_CD);
		String buyerUser = (String) transResult
				.get(PufaFieldDefine.PF_REQ_BODY_BUYER_USER);

		if ("0000".equals(respCode) && (buyerUser != null && buyerUser != "")) {
			logger.info("修改订单为成功");
			pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
			pmsAppTransInfo.setStatus("0");
			logger.info("查询原始订单表");
			OriginalOrderInfo originalInfo = originalDao
					.getOriginalOrderInfoByOrderid(pmsAppTransInfo.getOrderid());
			originalInfo.setByUser(buyerUser);
			logger.info("修改原始订单");
			logger.info(originalInfo);
			pospTranInfo.setResponsecode(respCode);
			logger.info("修改流水状态响应码" + pospTranInfo);
			try {
				originalDao.update(originalInfo);
				pospTransInfoDAO.update(pospTranInfo);
				pmsAppTransInfoDao.update(pmsAppTransInfo);
			} catch (Exception e) {
				logger.error("修改订单为失败", e);
			}
		} else {
			updateOrderWaitPay(pmsAppTransInfo, transResult);

		}

	}

}

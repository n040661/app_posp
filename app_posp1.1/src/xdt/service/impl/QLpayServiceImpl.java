package xdt.service.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import xdt.dao.IAmountLimitControlDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayTypeControlDao;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.dao.IPmsAppMerchantPayChannelDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsBusinessInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.ITAccAccountDao;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.pufa.QueryRequestEntity;
import xdt.dto.qianlong.PayRequestEntity;
import xdt.dto.qianlong.QLThread;
import xdt.dto.qianlong.QueryRequestDto;
import xdt.model.AppRateConfig;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.pufa.PufaPayService;
import xdt.quickpay.qianlong.model.PayResponseEntity;
import xdt.quickpay.qianlong.util.HttpClientHelper;
import xdt.quickpay.qianlong.util.HttpResponse;
import xdt.quickpay.qianlong.util.MyRSAUtils;
import xdt.quickpay.qianlong.util.SdkUtil;
import xdt.quickpay.qianlong.util.SignatureUtil;
import xdt.schedule.ThreadPool;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.service.QLpayService;
import xdt.util.BeanToMapUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

@Component
public class QLpayServiceImpl extends BaseServiceImpl implements QLpayService {
	/**
	 * 记录日志
	 */
	private Logger logger = Logger.getLogger(QLpayServiceImpl.class);

	public static final String SUCCESS_CODE = "200";// 成功

	private static final String URL = SdkUtil.getStringValue("chroneQrpayUrl");
	// 钱龙上游交易
	@Resource
	private PufaPayService pay;

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private IMerchantMineDao merchantMineDao;

	@Resource
	private IPmsBusinessInfoDao pmsBusinessInfoDao;

	// 原始数据
	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	private IPmsAppAmountAndRateConfigDao pmsAppAmountAndRateConfigDao;// 商户费率配置

	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao; // 业务配置服务层

	@Resource
	private IAppRateConfigDao appRateConfigDao;// 费率

	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;// 流水

	@Resource
	private IPayTypeControlDao payTypeControlDao;// 开关

	@Resource
	private ITAccAccountDao accountDao;

	@Resource
	private IPmsAppMerchantPayChannelDao pmsIPmsAppMerchantPayChannelDao;

	@Resource
	private IAmountLimitControlDao amountLimitControlDao;// 最大值最小值总开关判断

	@Resource
	private IPublicTradeVerifyService publicTradeVerifyService;

	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;

	/**
	 * 微信商户信息
	 */
	@Resource
	public PmsWeixinMerchartInfoService weixinService;

	/**
	 * 
	 * @Description 完成注册功能
	 * @author Administrator
	 * @param reqeustInfo
	 * @param result
	 * @param pospTransInfo
	 * @throws Exception
	 */
	public Map<String, String> customerRegister(PmsWeixinMerchartInfo weixin) throws Exception {

		logger.info("******************注册地址：" + SdkUtil.getStringValue("chroneRegistUrl"));

		String merchId = weixin.getMerchartId();

		logger.info("******************根据商户号查询");

		PmsMerchantInfo merchantinfo = getMerchantInfo(merchId);

		Map<String, String> retMap = new HashMap<String, String>();

		if (merchantinfo != null) {

			Map<String, String> params = new HashMap<String, String>();

			// String userid = "";// 路由获取

			PospRouteInfo route = super.route(merchId);
			logger.info("******************路由:" + route);
			PmsBusinessInfo busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());

			PmsWeixinMerchartInfo wx = weixinService.selectByPrimaryKey(weixin.getAccount());

			if (wx != null) {
				Map<String, String> result = setResp("100005", "账号已经注册！");
				result.put("merchartId", merchId);
				return result;
			}
			logger.info("商户费率：" + merchantinfo.getPremiumrate());
			logger.info("下游上送的微信T1费率：" + weixin.getWxT1Fee());
			logger.info("下游上送的微信T0费率：" + weixin.getWxT0Fee());
			logger.info("下游上送的支付宝T1费率：" + weixin.getAlipayT1Fee());
			logger.info("下游上送的支付宝T0费率：" + weixin.getAlipayT0Fee());
			if (Double.parseDouble(weixin.getAlipayT0Fee()) < Double.parseDouble(merchantinfo.getPremiumrate())) {
				Map<String, String> result = setResp("100006", "支付宝T0费率小于商户费率！");
				result.put("merchartId", merchId);
				return result;
			} else if (Double.parseDouble(weixin.getAlipayT1Fee()) < Double
					.parseDouble(merchantinfo.getPremiumrate())) {
				Map<String, String> result = setResp("100007", "支付宝T1费率小于商户费率！");
				result.put("merchartId", merchId);
				return result;
			} else if (Double.parseDouble(weixin.getWxT0Fee()) < Double.parseDouble(merchantinfo.getPremiumrate())) {
				Map<String, String> result = setResp("100008", "微信T0费率小于商户费率！");
				result.put("merchartId", merchId);
				return result;
			} else if (Double.parseDouble(weixin.getWxT1Fee()) < Double.parseDouble(merchantinfo.getPremiumrate())) {
				Map<String, String> result = setResp("100009", "微信T1费率小于商户费率！");
				result.put("merchartId", merchId);
				return result;
			}else if(!(weixin.getWxT0Fee().equals(weixin.getAlipayT0Fee())))
			{
				Map<String, String> result = setResp("100010", "不符合有关费率的相关规定!");
				result.put("merchartId", merchId);
				return result;
			}
			
			params.put("cardType", weixin.getCardType());
			params.put("pmsBankNo", weixin.getPmsBankNo());
			params.put("certNo", weixin.getCertNo());
			params.put("mobile", weixin.getMobile());
			params.put("password", weixin.getPassword());
			params.put("cardNo", weixin.getCardNo());
			params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
			params.put("realName", weixin.getRealName().trim());
			params.put("certType", weixin.getCertType());
			params.put("account", weixin.getAccount());
			params.put("mchntName", weixin.getMerchartName());
			params.put("wxT1Fee", weixin.getWxT1Fee());
			params.put("wxT0Fee", weixin.getWxT0Fee());
			params.put("alipayT1Fee", weixin.getAlipayT1Fee());
			params.put("alipayT0Fee", weixin.getAlipayT0Fee());
			System.out.println("============" + JSON.toJSONString(params));
			String bigStr = SignatureUtil.hex(params);
			String signnature = MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr,
					MyRSAUtils.MD5_SIGN_ALGORITHM);
			params.put("signature", signnature);
			weixin.setSignnature(signnature);
			logger.info("******************生成报文:" + params);
			String postData = JSON.toJSONString(params);
			logger.info("******************发送报文:" + postData);
			// temp.setPostdata(bigStr);
			List<String[]> headers = new ArrayList<String[]>();
			headers.add(new String[] { "Content-Type", "application/json" });
			HttpResponse response1 = HttpClientHelper.doHttp(SdkUtil.getStringValue("chroneRegistUrl"),
					HttpClientHelper.POST, headers, "utf-8", postData, "60000");
			if (StringUtils.isNotEmpty(response1.getRspStr())) {
				logger.debug("chrone regist result:" + response1.getRspStr());
				retMap = JSON.parseObject(response1.getRspStr(), new TypeReference<Map<String, String>>() {
				});
				logger.info("******************响应报文:" + retMap);
				if (SUCCESS_CODE.equals(retMap.get("respCode"))) {
					retMap.put("respCode", "0000");
					PmsWeixinMerchartInfo entity = new PmsWeixinMerchartInfo();
					entity.setMerchartId(merchId);
					entity.setAccount(weixin.getAccount());
					entity.setCardType(weixin.getCardType());
					entity.setPassword(weixin.getPassword());
					entity.setCertCorrect(weixin.getCertCorrect());
					entity.setPmsBankNo(weixin.getPmsBankNo());
					entity.setCardOpposite(weixin.getCardOpposite());
					entity.setCertNo(weixin.getCertNo());
					entity.setMobile(weixin.getMobile());
					entity.setCertMeet(weixin.getCertMeet());
					entity.setCardNo(weixin.getCardNo());
					entity.setRealName(weixin.getRealName());
					entity.setCardCorrect(weixin.getCardCorrect());
					entity.setCertType(weixin.getCertType());
					entity.setCertOpposite(weixin.getCertOpposite());
					entity.setMerchartName(weixin.getMerchartName());
					entity.setWxT1Fee(weixin.getWxT1Fee());
					entity.setWxT0Fee(weixin.getWxT0Fee());
					entity.setAlipayT1Fee(weixin.getAlipayT1Fee());
					entity.setAlipayT0Fee(weixin.getAlipayT0Fee());
					entity.setoAgentNo("100333");
					weixinService.updateRegister(entity);
					logger.info("******************####################注册微信商户信息");
					logger.info(entity);
				} else {
					retMap.put("respCode", "0001");
				}
			}
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
	private Map<String, String> setResp(String respCode, String respInfo) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("respCode", respCode);
		result.put("respInfo", respInfo);
		return result;
	}

	/**
	 * 
	 * @Description 生成二维码
	 * @author Administrator
	 * @param reqeustInfo
	 * @param result
	 * @param pospTransInfo
	 * @throws Exception
	 */
	public Map<String, String> twoDimensionCode(PayRequestEntity reqeustInfo) throws Exception {

		logger.info("生成二维码：" + URL);

		// 商户号
		String merchId = reqeustInfo.getMerchartId();
		// 金额
		String acount = reqeustInfo.getAmount();
		double acount1 = Double.parseDouble(acount);
		// 商户订单号
		String merchOrderId = reqeustInfo.getOrgOrderNo();
		logger.info("根据商户号查询");

		PmsMerchantInfo merchantinfo = getMerchantInfo(merchId);

		if (merchantinfo != null) {

			OriginalOrderInfo orig = new OriginalOrderInfo();
			orig.setMerchantOrderId(merchOrderId);
			orig.setPid(merchId);

			if (originalDao.selectByOriginal(orig) != null) {
				logger.info("下单重复");
				return setResp("0004", "下单重复");
			} else {

				String orderNumber = UtilMethod.getOrderid("185");
				/**
				 * 插入原始数据信息
				 */
				OriginalOrderInfo original = new OriginalOrderInfo();
				original.setMerchantOrderId(merchOrderId);// 原始数据的订单编号
				original.setOrderId(orderNumber); // 为主键
				original.setPid(merchId);
				original.setOrderTime(UtilDate.getOrderNum());
				NumberFormat nbf = NumberFormat.getInstance();
				nbf.setMinimumFractionDigits(2);
				Double orderAmount = Double.valueOf(acount);
				String c = nbf.format(orderAmount / 100);
				original.setOrderAmount(c);// 单位分
				original.setByUser(reqeustInfo.getAccount());
				original.setBgUrl(reqeustInfo.getNotifyUrl());
				originalDao.insert(original);

				logger.info("o单编号");
				String oAgentNo = merchantinfo.getoAgentNo();

				logger.info("实际金额");
				String factAmount = acount;
				logger.info("校验欧单金额限制");
				ResultInfo payCheckResult = iPublicTradeVerifyService.amountVerifyOagent(
						(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);

				if (payCheckResult.getErrCode().equals("0")) {

					logger.info("校验欧单模块是否开启");
					ResultInfo payCheckResult1 = iPublicTradeVerifyService
							.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);

					if (payCheckResult1.getErrCode().equals("0")) {
						logger.info("校验商户模块是否开启");
						ResultInfo payCheckResult3 = iPublicTradeVerifyService
								.moduelVerifyMer(TradeTypeEnum.merchantCollect, merchId);
						if (payCheckResult3.getErrCode().equals("0")) {

							logger.info("校验商户金额限制");
							Map<String, String> paramMap = new HashMap<String, String>();
							paramMap.put("mercid", merchId);
							paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
							paramMap.put("oAgentNo", oAgentNo);
							//
							logger.info("商户 商城 业务信息 ");
							Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

							if (!(resultMap == null || resultMap.size() == 0)) {

								String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
								String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
								String paymentAmount = factAmount;// 交易金额

								if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
									// 金额超过最大金额
									logger.info("交易金额大于最打金额");
									return setResp("0004", "交易金额大于最打金额");
								} else if (new BigDecimal(paymentAmount)
										.compareTo(new BigDecimal(minTransMoney)) == -1) {
									logger.info("交易金额小于最小金额");
									return setResp("0004", "金额小于最小交易金额");
								} else {

									//
									logger.info("组装订单数据");
									PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
									logger.info("写入欧单编号");

									pmsAppTransInfo.setoAgentNo(oAgentNo);
									pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
									pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());// 业务功能模块名称
									logger.info("网购");
									pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
									pmsAppTransInfo.setMercid(merchantinfo.getMercId());
									pmsAppTransInfo.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());// 业务功能模块编号
									// ：17
									pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
									if ("0".equals(reqeustInfo.getSource())) {
										pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay.getTypeName());
										pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay.getTypeCode());
									} else if ("1".equals(reqeustInfo.getSource())) {
										pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
										pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
									}
									BigDecimal factBigDecimal = new BigDecimal(factAmount);
									BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

									pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
									pmsAppTransInfo
											.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
									pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
									// pmsAppTransInfo.setSettlementPeriod(reqeustInfo.getTranTp());
									if (reqeustInfo.getTranTp().equals("0")) {
										pmsAppTransInfo.setSettlementPeriod(reqeustInfo.getTranTp());// 结算周期
									 pmsAppTransInfo.setSettlementState(reqeustInfo.getTranTp());//结算状态
									} else if (reqeustInfo.getTranTp().equals("1")) {
										pmsAppTransInfo.setSettlementPeriod(reqeustInfo.getTranTp());// 结算周期
										// pmsAppTransInfo.setSettlementState("");//结算状态
									}
									logger.info("插入订单信息");
									Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);

									if (insertAppTrans == 1) {

										logger.info("查询订单信息");
										pmsAppTransInfo = pmsAppTransInfoDao
												.searchOrderInfo(pmsAppTransInfo.getOrderid());

										String quickRateType = resultMap.get("QUICKRATETYPE").toString();// 快捷支付费率类型
										// System.out.println("111111111=" +
										// quickRateType);
										logger.info("获取o单第三方支付的费率");
										AppRateConfig appRate = new AppRateConfig();
										appRate.setRateType(quickRateType);
										appRate.setoAgentNo(oAgentNo);
										AppRateConfig appRateConfig = appRateConfigDao
												.getByRateTypeAndoAgentNo(appRate);
										if (appRateConfig != null) {
											String isTop = appRateConfig.getIsTop();
											String rate = appRateConfig.getRate();
											String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
											paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
											String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
											System.out.println("最低费率:" + rate);
											Double rates = Double.parseDouble(rate);
											;
											Double minPoundage = 0.0; // 附加费
											if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
													&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
												if (StringUtils.isNotBlank(minPoundageStr)) {
													minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
												} else {
													// 若查到的是空值，直接返回错误
													logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
													return setResp("0004", "没有查到相关费率配置（附加费），请联系客服人员");
												}
											} else {
											
												BigDecimal payAmount = null;
												BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
												// 费率
												BigDecimal fee = new BigDecimal(0);
												PmsWeixinMerchartInfo weixin=weixinService.selectByPrimaryKey(reqeustInfo.getAccount());
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
														if ("0".equals(reqeustInfo.getSource())) {
															rateStr=weixin.getWxT0Fee();
														} else if ("1".equals(reqeustInfo.getSource())) {
															rateStr=weixin.getAlipayT0Fee();
														}
														fee.add(new BigDecimal(minPoundage));
														payAmount = dfactAmount.subtract(fee);
													}

												} else {
													// 按当前费率处理
													if ("0".equals(reqeustInfo.getSource())) {
														rateStr=weixin.getWxT0Fee();
													} else if ("1".equals(reqeustInfo.getSource())) {
														rateStr=weixin.getAlipayT0Fee();
													}
													fee = new BigDecimal(rateStr).multiply(dfactAmount).add(new BigDecimal(minPoundage));
													payAmount = dfactAmount.subtract(fee);
												}
												// 设置结算金额
												pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
												pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
												pmsAppTransInfo.setPoundage(fee.toString());
												pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
												// 转换double为int
												Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

												logger.info("验证支付方式是否开启");
												if ("1".equals(reqeustInfo.getSource())) {
													payCheckResult = iPublicTradeVerifyService.totalVerify(
															paymentAmountInt, TradeTypeEnum.merchantCollect,
															PaymentCodeEnum.zhifubaoPay, oAgentNo,
															merchantinfo.getMercId());

												} else if ("0".equals(reqeustInfo.getSource())) {
													payCheckResult = iPublicTradeVerifyService.totalVerify(
															paymentAmountInt, TradeTypeEnum.merchantCollect,
															PaymentCodeEnum.weixinPay, oAgentNo,
															merchantinfo.getMercId());
												}

												if (!payCheckResult.getErrCode().equals("0")) {
													if ("0".equals(reqeustInfo.getSource())) {
														logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
																+ PaymentCodeEnum.weixinPay.getTypeCode());
														return setResp("0004", "交易不支持");
													} else if ("1".equals(reqeustInfo.getSource())) {
														logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
																+ PaymentCodeEnum.zhifubaoPay.getTypeCode());
														return setResp("0004", "交易不支持");
													}

												} else {
													logger.info("查看当前交易是否已经生成了流水表");
													PospTransInfo pospTransInfo = null;
													logger.info("流水表是否需要更新的标记 0 insert，1：update");
													logger.info("生成上送流水号");
													String transOrderId = generateTransOrderId(
															TradeTypeEnum.merchantCollect, PaymentCodeEnum.weixinPay);

													logger.info("不存在流水，生成一个流水");
													pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
													logger.info("设置上送流水号");
													pospTransInfo.setPospsn(transOrderId);

													pospTransInfoDAO.insert(pospTransInfo);

													logger.info("流水表信息:" + pospTransInfo);

													String userId = "";// 路由获取

													Map<String, String> params = new HashMap<String, String>();

													params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
													params.put("account", reqeustInfo.getAccount() + "");
													params.put("amount", reqeustInfo.getAmount() + "");
													params.put("source", reqeustInfo.getSource() + "");
													params.put("subject", reqeustInfo.getSubject() + "");
													// int intFee = (int)
													// Math.floor(settlement1);
													params.put("settleAmt", "0");
													params.put("notifyUrl", SdkUtil.getStringValue("chroneNotifyurl"));
													params.put("tranTp", reqeustInfo.getTranTp() + "");
													params.put("orgOrderNo", reqeustInfo.getOrgOrderNo());
													String bigStr = SignatureUtil.hex(params);
													params.put("signature",
															MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"),
																	bigStr, MyRSAUtils.MD5_SIGN_ALGORITHM));
													logger.info("请求报文:" + params);
													String postData = JSON.toJSONString(params);
													logger.info("发送的报文{}" + postData);
													List<String[]> headers = new ArrayList<>();
													headers.add(new String[] { "Content-Type", "application/json" });
													HttpResponse response = HttpClientHelper.doHttp(
															SdkUtil.getStringValue("chroneQrpayUrl"),
															HttpClientHelper.POST, headers, "utf-8", postData, "60000");
													logger.info("响应报文:" + response);
													if (StringUtils.isNotEmpty(response.getRspStr())) {
														logger.debug("chrone regist result:" + response.getRspStr());
														Map<String, String> retMap = JSON.parseObject(
																response.getRspStr(),
																new TypeReference<Map<String, String>>() {
																});
														logger.info("******************响应报文:" + retMap);
														if (SUCCESS_CODE.equals(retMap.get("respCode"))) {
															retMap.put("respCode", "0000");
															QueryRequestDto query = new QueryRequestDto();
															query.setMerchId(merchId);
															query.setPhone(params.get("account").toLowerCase());
															query.setPos_platOrderId(
																	params.get("orgOrderNo").toLowerCase());
															pospTransInfo.setSysseqno(
																	params.get("orgOrderNo").toLowerCase());
															 ThreadPool.executor(new
															 QLThread(this,
															 query));
														} else {
															retMap.put("respCode", "0001");
														}
														logger.info("修改订单信息");
														logger.info(pmsAppTransInfo);
														pmsAppTransInfoDao.update(pmsAppTransInfo);
														pospTransInfoDAO.updateByOrderId(pospTransInfo);

														retMap.put("orderId", pmsAppTransInfo.getOrderid());
														logger.info("流水表信息:" + pospTransInfo);
														return retMap;
													}
												}
											}

										} else {
											// 若查到的是空值，直接返回错误
											logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
											return setResp("0004", "没有查到相关费率配置，请联系客服人员！！");

										}

									} else {
										return setResp("0004", "下单失败！！");
									}
								}

							} else {
								return setResp("0004", "没有查到相关费率配置，请联系客服人员");
							}

						} else {
							// 交易不支持
							logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
									+ PaymentCodeEnum.weixinPay.getTypeCode());
							return setResp("0004", payCheckResult3.getMsg());
						}

					} else {
						// 交易不支持
						logger.info(
								"欧单模块限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.weixinPay.getTypeCode());
						return setResp("0004", payCheckResult.getMsg());
					}

				} else {
					logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.weixinPay.getTypeCode());
					return setResp("0004", payCheckResult.getMsg());
				}
			}
		}
		return null;
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
		if (pmsAppTransInfo.getSettlementState() != null) {
			pmsAppTransInfo.setSettlementState("1");// 结算状态set
		}
		// pmsAppTransInfo.setSettlementState("1");
		try {
			if (!UtilDate.isInDefiMinit(pmsAppTransInfo.getTradetime(), 30)) {
				logger.info("订单在30分未完成支付  修改为失败");
				pmsAppTransInfoDao.update(pmsAppTransInfo);
			}
		} catch (Exception e) {
			logger.error("修改订单为失败", e);
		}
	}

	/**
	 * 
	 * @Description 修改订单成功状态
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @param pospTransInfo
	 */
	private void updateOrderSuccess(PmsAppTransInfo pmsAppTransInfo, PospTransInfo pospTransInfo) {
		pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
		pmsAppTransInfo.setStatus("0");
		if (pmsAppTransInfo.getSettlementState() != null) {
			pmsAppTransInfo.setSettlementState("0");// 结算状态set
		}
		pospTransInfo.setResponsecode("0000");
		try {
			pospTransInfoDAO.update(pospTransInfo);
			pmsAppTransInfoDao.update(pmsAppTransInfo);
		} catch (Exception e) {
			logger.error("修改订单为失败", e);
		}
	}

	/**
	 * 
	 * @Description 修改订单为等待支付 超过30分钟失败处理
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @param pospTransInfo
	 */
	private void updateOrderWaitPay(PmsAppTransInfo pmsAppTransInfo, PospTransInfo pospTransInfo) {
		logger.info("修改订单为失败");

		try {
			if (!UtilDate.isInDefiMinit(pmsAppTransInfo.getTradetime(), 5)) {
				logger.info("订单在30分未完成支付  修改为失败");
				pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
				pmsAppTransInfo.setStatus("1");
				if (pmsAppTransInfo.getSettlementState() != null) {
					pmsAppTransInfo.setSettlementState("1");// 结算状态set
				}
				// pmsAppTransInfo.setSettlementState("1");
			} else {
				pmsAppTransInfo.setStatus("2");
			}
			pmsAppTransInfoDao.update(pmsAppTransInfo);
		} catch (Exception e) {
			logger.error("修改订单为失败", e);
		}
	}

	@Override
	public Map<String, Object> query(QueryRequestEntity requestInfo) throws Exception {

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
			PmsAppTransInfo orderinfo = pmsAppTransInfoDao.searchOrderInfo(oriInfo.getOrderId());
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

	/**
	 * 
	 * @Description 获取商户信息
	 * @author Administrator
	 * @param merchId
	 * @return
	 * @throws Exception
	 */
	private PmsMerchantInfo getMerchantInfo(String merchId) throws Exception {
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(merchId);

		logger.info("查询当前商户信息");
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (merchantList.isEmpty() && merchantList.size() == 0) {
			return null;
		} else {
			return merchantList.get(0);
		}

	}

	/**
	 * 
	 * @Description 收款结果查询
	 * @author Administrator
	 * @param merchId
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> updateQuery(QueryRequestDto req) throws Exception {

		logger.info("*************线程处理订单状态");
		logger.info("确认订单状态：");
		String merchId = req.getMerchId();

		logger.info("根据商户号查询");

		PmsMerchantInfo merchantinfo = getMerchantInfo(merchId);

		if (merchantinfo != null) {

//			String userId = "";// 路由获取
//
//			PospRouteInfo route = super.route(merchId);
//
//			PmsBusinessInfo busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
//
//			userId = busInfo.getBusinessNum();

			Map<String, String> params = new HashMap<String, String>();
			params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
			params.put("orgOrderNo", req.getPos_platOrderId());
			String bigStr = SignatureUtil.hex(params);
			params.put("signature",
					MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr, MyRSAUtils.MD5_SIGN_ALGORITHM));
			logger.info("请求报文:" + params);
			String postData = JSON.toJSONString(params);
			List<String[]> headers = new ArrayList<String[]>();
			headers.add(new String[] { "Content-Type", "application/json" });
			HttpResponse response = HttpClientHelper.doHttp(SdkUtil.getStringValue("chroneSkpayUrl"),
					HttpClientHelper.POST, headers, "utf-8", postData, "60000");
			logger.info("响应报文:" + response);
			// Map result = JsonUtil.jsonToMap(resp);

			// String respCode =result.get("respCode").toString();
			if (StringUtils.isNotEmpty(response.getRspStr())) {
				logger.debug("chrone regist result:" + response.getRspStr());
				Map<String, String> retMap = JSON.parseObject(response.getRspStr(),
						new TypeReference<Map<String, String>>() {
						});
				if (SUCCESS_CODE.equals(retMap.get("respCode"))) {
					String sysseqno = req.getPos_platOrderId();
					logger.info("上游订单号:" + sysseqno + "响应码：" + retMap.get("respCode"));
					PospTransInfo pospTransInfo = pospTransInfoDAO.selectBySysseqno(sysseqno);
					if (pospTransInfo != null) {
						logger.info("查询流水:" + pospTransInfo);

						PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao
								.searchOrderInfo(pospTransInfo.getOrderId());
						if (pmsAppTransInfo != null) {
							logger.info("查询订单:" + pmsAppTransInfo);
							if ("2".equals(retMap.get("paySt"))) {
								retMap.put("respCode", "0000");
								// TODO 修改本地订单数据
								updateOrderSuccess(pmsAppTransInfo, pospTransInfo);

							} else {
								retMap.put("respCode", "0001");
								updateOrderWaitPay(pmsAppTransInfo, pospTransInfo);
							}
						}

						return retMap;
					}
				}
			}

		}
		return null;
	}

	/**
	 * 
	 * @Description 付款结果查询
	 * @author Administrator
	 * @param merchId
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> payQuery(PayResponseEntity temp) throws Exception {

		logger.info("*************线程处理订单状态");
		logger.info("确认订单状态：");
		// String merchId = req.getMerchId();

		logger.info("响应参数:{}" + temp);

		Map<String, String> params = new HashMap<String, String>();
		params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
		params.put("orderNo", temp.getOrderNo());
		String bigStr = SignatureUtil.hex(params);
		params.put("signature",
				MyRSAUtils.sign(SdkUtil.getStringValue("chronePrivateKey"), bigStr, MyRSAUtils.MD5_SIGN_ALGORITHM));
		logger.info("请求报文:" + params);
		String postData = JSON.toJSONString(params);
		List<String[]> headers = new ArrayList<String[]>();
		headers.add(new String[] { "Content-Type", "application/json" });
		HttpResponse response = HttpClientHelper.doHttp(SdkUtil.getStringValue("chroneFkpayUrl"), HttpClientHelper.POST,
				headers, "utf-8", postData, "60000");
		logger.info("响应报文:" + response);
		if (StringUtils.isNotEmpty(response.getRspStr())) {
			logger.debug("chrone regist result:" + response.getRspStr());
			Map<String, String> retMap = JSON.parseObject(response.getRspStr(),
					new TypeReference<Map<String, String>>() {
					});
			if (SUCCESS_CODE.equals(retMap.get("respCode"))) {

				String sysseqno = temp.getOrgOrderNo();

				logger.info("上游订单号:" + sysseqno + "响应码：" + retMap.get("respCode"));
				logger.info("流水dao:" + pospTransInfoDAO);
				PospTransInfo pospTransInfo = pospTransInfoDAO.selectBySysseqno(sysseqno);
				if (pospTransInfo != null) {
					logger.info("查询流水:" + pospTransInfo);

					PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
					if (pmsAppTransInfo != null) {
						logger.info("查询订单:" + pmsAppTransInfo);
						if ("2".equals(retMap.get("paySt"))) {
							retMap.put("respCode", "0000");
							// TODO 修改本地订单数据
							updateOrderSuccess(pmsAppTransInfo, pospTransInfo);

						} else {
							retMap.put("respCode", "0001");
							updateOrderWaitPay(pmsAppTransInfo, pospTransInfo);
						}
					}
					Map<String, String> map = BeanToMapUtil.convertBean(temp);

					map.put("respCode", "0000");

					return map;
				}
			}
		}
		//
		// }
		return null;
	}

	/**
	 * 
	 * @Description 生成固态二维码
	 * @author Administrator
	 * @param reqeustInfo
	 * @param result
	 * @param pospTransInfo
	 * @throws Exception
	 */
	public Map<String, String> solidtwodimensionCode(PayRequestEntity reqeustInfo) throws Exception {
		// TODO Auto-generated method stub

		logger.info("生成二维码：" + URL);

		// 商户号
		String merchId = reqeustInfo.getMerchartId();
		// 金额
		String acount = reqeustInfo.getAmount();
		double acount1 = Double.parseDouble(acount);
		// 商户订单号
		String merchOrderId = reqeustInfo.getOrgOrderNo();
		logger.info("根据商户号查询");

		PmsMerchantInfo merchantinfo = getMerchantInfo(merchId);

		if (merchantinfo != null) {

			OriginalOrderInfo orig = new OriginalOrderInfo();
			orig.setMerchantOrderId(merchOrderId);
			orig.setPid(merchId);

			if (originalDao.selectByOriginal(orig) != null) {
				logger.info("下单重复");
				return setResp("0004", "下单重复");
			} else {

				String orderNumber = UtilMethod.getOrderid("185");
				/**
				 * 插入原始数据信息
				 */
				OriginalOrderInfo original = new OriginalOrderInfo();
				original.setMerchantOrderId(merchOrderId);// 原始数据的订单编号
				original.setOrderId(orderNumber); // 为主键
				original.setPid(merchId);
				original.setOrderTime(UtilDate.getOrderNum());
				NumberFormat nbf = NumberFormat.getInstance();
				nbf.setMinimumFractionDigits(2);
				Double orderAmount = Double.valueOf(acount);
				String c = nbf.format(orderAmount / 100);
				original.setOrderAmount(c);// 单位分
				original.setByUser(reqeustInfo.getAccount());
				originalDao.insert(original);

				logger.info("o单编号");
				String oAgentNo = merchantinfo.getoAgentNo();

				logger.info("实际金额");
				String factAmount = acount;
				logger.info("校验欧单金额限制");
				ResultInfo payCheckResult = iPublicTradeVerifyService.amountVerifyOagent(
						(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);

				if (payCheckResult.getErrCode().equals("0")) {

					logger.info("校验欧单模块是否开启");
					ResultInfo payCheckResult1 = iPublicTradeVerifyService
							.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);

					if (payCheckResult1.getErrCode().equals("0")) {
						logger.info("校验商户模块是否开启");
						ResultInfo payCheckResult3 = iPublicTradeVerifyService
								.moduelVerifyMer(TradeTypeEnum.merchantCollect, merchId);
						if (payCheckResult3.getErrCode().equals("0")) {

							logger.info("校验商户金额限制");
							Map<String, String> paramMap = new HashMap<String, String>();
							paramMap.put("mercid", merchId);
							paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
							paramMap.put("oAgentNo", oAgentNo);
							//
							logger.info("商户 商城 业务信息 ");
							Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

							if (!(resultMap == null || resultMap.size() == 0)) {

								String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
								String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
								String paymentAmount = factAmount;// 交易金额

								if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
									// 金额超过最大金额
									logger.info("交易金额大于最打金额");
									return setResp("0004", "交易金额大于最打金额");
								} else if (new BigDecimal(paymentAmount)
										.compareTo(new BigDecimal(minTransMoney)) == -1) {
									logger.info("交易金额小于最小金额");
									return setResp("0004", "金额小于最小交易金额");
								} else {

									//
									logger.info("组装订单数据");
									PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
									logger.info("写入欧单编号");

									pmsAppTransInfo.setoAgentNo(oAgentNo);
									pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
									pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());// 业务功能模块名称
									logger.info("网购");
									pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
									pmsAppTransInfo.setMercid(merchantinfo.getMercId());
									pmsAppTransInfo.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());// 业务功能模块编号
									// ：17
									pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
									pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay.getTypeName());
									pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay.getTypeCode());
									BigDecimal factBigDecimal = new BigDecimal(factAmount);
									BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

									pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
									pmsAppTransInfo
											.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
									pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
									// pmsAppTransInfo.setSettlementPeriod(reqeustInfo.getTranTp());
									if (reqeustInfo.getTranTp().equals("0")) {
										pmsAppTransInfo.setSettlementPeriod(reqeustInfo.getTranTp());// 结算周期
										// pmsAppTransInfo.setSettlementState(reqeustInfo.getTranTp());//结算状态
									} else if (reqeustInfo.getTranTp().equals("1")) {
										pmsAppTransInfo.setSettlementPeriod(reqeustInfo.getTranTp());// 结算周期
										// pmsAppTransInfo.setSettlementState("");//结算状态
									}
									logger.info("插入订单信息");
									Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);

									if (insertAppTrans == 1) {

										logger.info("查询订单信息");
										pmsAppTransInfo = pmsAppTransInfoDao
												.searchOrderInfo(pmsAppTransInfo.getOrderid());

										String quickRateType = resultMap.get("QUICKRATETYPE").toString();// 快捷支付费率类型
										// System.out.println("111111111=" +
										// quickRateType);
										logger.info("获取o单第三方支付的费率");
										AppRateConfig appRate = new AppRateConfig();
										appRate.setRateType(quickRateType);
										appRate.setoAgentNo(oAgentNo);
										AppRateConfig appRateConfig = appRateConfigDao
												.getByRateTypeAndoAgentNo(appRate);
										if (appRateConfig != null) {
											String isTop = appRateConfig.getIsTop();
											String rate = appRateConfig.getRate();
											String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
											paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
											String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
											System.out.println("最低费率:" + rate);
											Double rates = Double.parseDouble(rate);
											;
											Double minPoundage = 0.0; // 附加费
											if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
													&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
												if (StringUtils.isNotBlank(minPoundageStr)) {
													minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
												} else {
													// 若查到的是空值，直接返回错误
													logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
													return setResp("0004", "没有查到相关费率配置（附加费），请联系客服人员");
												}
											} else {
												// 结算金额
												String settlement = reqeustInfo.getSettleAmt();
												double settlement1 = Double.parseDouble(settlement);
												System.out.println("结算金额" + settlement1);
												// 手续费
												double shouxufei = acount1 - settlement1;
												// 用户自定义费率
												double feilv = shouxufei / acount1;
												logger.info("用户自定义费率:" + feilv);
												if (rates <= feilv) {
													logger.info("设置结算金额");
													pmsAppTransInfo.setPayamount(settlement);// 结算金额
													pmsAppTransInfo.setRate(feilv + "");// 0.50_35
																						// ||
																						// 0.50
													pmsAppTransInfo.setPoundage(shouxufei + "");
													pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
												} else {
													logger.info("用户费率小于固定费率！");
													pmsAppTransInfo.setStatus("10");
												}
												Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

												logger.info("验证支付方式是否开启");
												payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt,
														TradeTypeEnum.merchantCollect, PaymentCodeEnum.weixinPay,
														oAgentNo, merchantinfo.getMercId());
												if (!payCheckResult.getErrCode().equals("0")) {
													logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
															+ PaymentCodeEnum.weixinPay.getTypeCode());
													return setResp("0004", "交易不支持");
												} else {
													logger.info("查看当前交易是否已经生成了流水表");
													PospTransInfo pospTransInfo = null;
													logger.info("流水表是否需要更新的标记 0 insert，1：update");
													logger.info("生成上送流水号");
													String transOrderId = generateTransOrderId(
															TradeTypeEnum.merchantCollect, PaymentCodeEnum.weixinPay);

													logger.info("不存在流水，生成一个流水");
													pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
													logger.info("设置上送流水号");
													pospTransInfo.setPospsn(transOrderId);

													pospTransInfoDAO.insert(pospTransInfo);

													logger.info("流水表信息:" + pospTransInfo);

													String userId = "";// 路由获取

													Map<String, String> params = new HashMap<String, String>();

													params.put("orgId", SdkUtil.getStringValue("chroneOrgId"));
													params.put("source", reqeustInfo.getSource() + "");
													params.put("settleAmt", reqeustInfo.getSettleAmt() + "");
													params.put("cardNo", reqeustInfo.getCardNo());
													params.put("realName", reqeustInfo.getRealName().trim());
													params.put("pmsBankNo", reqeustInfo.getPmsBankNo());
													params.put("portType", reqeustInfo.getPortType());
													// params.put("account",
													// temp.getAccount()+"");
													params.put("amount", reqeustInfo.getAmount() + "");
													params.put("notifyUrl", SdkUtil.getStringValue("chroneNotifyurl"));
													params.put("tranTp", reqeustInfo.getTranTp() + "");
													params.put("orgOrderNo", reqeustInfo.getOrgOrderNo());
													// params.put("mobile",
													// temp.getMobile());
													// params.put("certType",
													// temp.getCertType());
													// params.put("account",
													// temp.getAccount());
													// params.put("mchntName",
													// temp.getMchntName().trim());
													System.out.println("============" + JSON.toJSONString(params));
													String bigStr = SignatureUtil.hex(params);
													String signnature = MyRSAUtils.sign(
															SdkUtil.getStringValue("chronePrivateKey"), bigStr,
															MyRSAUtils.MD5_SIGN_ALGORITHM);
													params.put("signature", signnature);
													// temp.setSignature(signnature);
													String postData = JSON.toJSONString(params);
													// temp.setPostdata(bigStr);
													List<String[]> headers = new ArrayList<String[]>();
													headers.add(new String[] { "Content-Type", "application/json" });
													HttpResponse response = HttpClientHelper.doHttp(
															SdkUtil.getStringValue("chroneFqrpayUrl"),
															HttpClientHelper.POST, headers, "utf-8", postData, "60000");
													logger.info("响应报文:" + response);
													if (StringUtils.isNotEmpty(response.getRspStr())) {
														logger.debug("chrone regist result:" + response.getRspStr());
														Map<String, String> retMap = JSON.parseObject(
																response.getRspStr(),
																new TypeReference<Map<String, String>>() {
																});
														logger.info("******************响应报文:" + retMap);
														if (SUCCESS_CODE.equals(retMap.get("respCode"))) {
															retMap.put("respCode", "0000");
														} else {
															retMap.put("respCode", "0001");
														}
														logger.info("修改订单信息");
														logger.info(pmsAppTransInfo);
														pmsAppTransInfoDao.update(pmsAppTransInfo);
														pospTransInfoDAO.updateByOrderId(pospTransInfo);

														retMap.put("orderId", pmsAppTransInfo.getOrderid());
														logger.info("流水表信息:" + pospTransInfo);
														return retMap;
													}
												}
											}

										} else {
											// 若查到的是空值，直接返回错误
											logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
											return setResp("0004", "没有查到相关费率配置，请联系客服人员！！");

										}

									} else {
										return setResp("0004", "下单失败！！");
									}
								}

							} else {
								return setResp("0004", "没有查到相关费率配置，请联系客服人员");
							}

						} else {
							// 交易不支持
							logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
									+ PaymentCodeEnum.weixinPay.getTypeCode());
							return setResp("0004", payCheckResult3.getMsg());
						}

					} else {
						// 交易不支持
						logger.info(
								"欧单模块限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.weixinPay.getTypeCode());
						return setResp("0004", payCheckResult.getMsg());
					}

				} else {
					logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.weixinPay.getTypeCode());
					return setResp("0004", payCheckResult.getMsg());
				}
			}
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * xdt.service.impl.HfQuickPayService#getOriginOrderInfo(java.lang.String)
	 */
	@Override
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
		OriginalOrderInfo original = null;
		this.logger.info("根据上送订单号  查询商户上送原始信息");
		original = this.originalDao.getOriginalOrderInfoByMerchanOrderId(tranId);
		return original;
	}

	public synchronized void otherInvoke(PayResponseEntity result) throws Exception {
		this.logger.info("异步通知的数据:" + result);

		String tranId = result.getOrgOrderNo();
		this.logger.info("上游返回的机构订单号:" + tranId);

		OriginalOrderInfo originalInfo = this.originalDao.getOriginalOrderInfoByMerchanOrderId(tranId);

		String transOrderId = originalInfo.getOrderId();
		this.logger.info("流水订单号:" + transOrderId);

		PospTransInfo pospTransInfo = this.pospTransInfoDAO.searchBycjtOrderId(transOrderId);

		PmsAppTransInfo pmsAppTransInfo = this.pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());

		if ("2".equals(result.getPaySt())) {
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getPaySt());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());

			int updateAppTrans = this.pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				this.logger.info(pmsAppTransInfo);

				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(result.getDescription());
				this.logger.info("更新流水");
				this.logger.info(pospTransInfo);
				this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("3".equals(result.getPaySt())) {
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getPaySt());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());

			int updateAppTrans = this.pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getDescription());
				this.logger.info("更新流水");
				this.logger.info(pospTransInfo);
				this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
	}

}

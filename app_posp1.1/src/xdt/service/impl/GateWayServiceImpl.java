package xdt.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.dvcs.Data;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yufusoft.payplatform.security.cipher.YufuCipher;
import com.yufusoft.payplatform.security.vo.ParamPacket;

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
import xdt.dto.gateway.entity.GateWayQueryRequestEntity;
import xdt.dto.gateway.entity.GateWayQueryResponseEntity;
import xdt.dto.gateway.entity.GateWayRequestEntity;
import xdt.dto.gateway.entity.GateWayResponseEntity;
import xdt.dto.gateway.entity.GatrWayGefundEntity;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.dto.scanCode.entity.ScanCodeRequestEntity;
import xdt.dto.yf.DoYf;
import xdt.dto.yf.PostUtils;
import xdt.dto.yf.YFUtil;
import xdt.dto.yf.YufuCipherSupport;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.service.IGateWayService;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.servlet.AppPospContext;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

@Component
public class GateWayServiceImpl extends BaseServiceImpl implements IGateWayService{

	private Logger logger = Logger.getLogger(GateWayServiceImpl.class);

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
		
		logger.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
		
		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		logger.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}
	/**
	 * 代付查询订单信息
	 * @param tranId
	 * @return
	 * @throws Exception
	 */
	public OriginalOrderInfo getOriginOrderInfos(String tranId) throws Exception {
		
		OriginalOrderInfo original = null;
		// 查询流水信息
		logger.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(tranId);
		return original;
	}
	
	public Map<String, String> updateHandle(GateWayRequestEntity originalinfo) throws Exception {
		
		Map<String, String> retMap = new HashMap<String, String>();
				// 商户号
				String merchId = originalinfo.getV_mid();
				// 金额
				String acount = originalinfo.getV_txnAmt();
				// 商户订单号
				logger.info("******************根据商户号查询");
				
				// 查询上游商户号
				PmsBusinessPos busInfo = selectKey(merchId);

				// 验证当前是否已经下单
				OriginalOrderInfo orig = new OriginalOrderInfo();
				orig.setMerchantOrderId(originalinfo.getV_oid());
				orig.setPid(originalinfo.getV_mid());

				if (originalDao.selectByOriginal(orig) != null) {
					logger.info("下单重复");
					return setResp("03", "下单重复");
				}

				//String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
																	// 业务号（2位）+业务细分（1位）+时间戳（13位）
																	// 总共16位
				/**
				 * 插入原始数据信息
				 */
				OriginalOrderInfo original = new OriginalOrderInfo();
				original.setMerchantOrderId(originalinfo.getV_oid());// 原始数据的订单编号
				original.setOrderId(originalinfo.getV_oid()); // 为主键
				original.setPid(originalinfo.getV_mid());
				original.setOrderTime(originalinfo.getV_time());
				original.setOrderAmount(originalinfo.getV_txnAmt());
				original.setPayType(originalinfo.getV_type());
				original.setPageUrl(originalinfo.getV_url());
				original.setAttach(originalinfo.getV_attach());
				original.setBgUrl(originalinfo.getV_notify_url());
				original.setBankId(originalinfo.getV_bankAddr());
				originalDao.insert(original);
				// 根据商户号查询
				String mercId = originalinfo.getV_mid();

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
						return setResp("04", "参数错误,没有欧单编号");
					}
					// 判断是否为正式商户
					if ("60".equals(merchantinfo.getMercSts())) {
						
						// 实际金额
						String factAmount = "" + new BigDecimal(originalinfo.getV_txnAmt()).multiply(new BigDecimal(100));
						// 校验欧单金额限制
						ResultInfo payCheckResult = iPublicTradeVerifyService
								.amountVerifyOagent((int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);
						if (!payCheckResult.getErrCode().equals("0")) {
							// 交易不支持
							logger.info(
									"欧单金额限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.WithholdPay.getTypeCode());
							return setResp("05", "欧单金额限制，请重试或联系客服");
						}

						// 校验欧单模块是否开启
						ResultInfo resultInfoForOAgentNo= iPublicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.merchantCollect,
								oAgentNo);
						if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
							// 交易不支持
							if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
								if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
									logger.error("此功能暂时关闭!");
									return setResp("06", "交易关闭，请重试或联系客服");
								} else {
									logger.error(resultInfoForOAgentNo.getMsg());
									return setResp("07", "系统异常，请重试或联系客服");
								}
							}
							
						}
						// 校验商户模块是否开启
						ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.merchantCollect, mercId);
						if (!payCheckResult3.getErrCode().equals("0")) {
							// 交易不支持
							logger.info(
									"商户模块限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.WithholdPay.getTypeCode());
							return setResp("08", "商户模块限制,请重试或联系客服");
						}
						// 校验商户金额限制
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
						paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
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
							logger.info("交易金额大于最大金额");
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
						pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());// 业务功能模块名称
																							// ：网购
						pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
						pmsAppTransInfo.setMercid(merchantinfo.getMercId());
						pmsAppTransInfo.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());// 业务功能模块编号
																								// ：17
						pmsAppTransInfo.setOrderid(originalinfo.getV_oid());// 设置订单号
						pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.GatewayCodePay.getTypeName());
						pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.GatewayCodePay.getTypeCode());
						BigDecimal factBigDecimal = new BigDecimal(factAmount);
						BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
						pmsAppTransInfo.setBusinessNum(busInfo.getBusinessnum());
						pmsAppTransInfo.setChannelNum(busInfo.getChannelnum());
						pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
						pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
						pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
						if("0".equals(originalinfo.getV_channel())) {
							pmsAppTransInfo.setSettlementState("D0");
						}else if("1".equals(originalinfo.getV_channel())) {
							pmsAppTransInfo.setSettlementState("T1");
						}

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
									return setResp("12", "费率低于成本费率");
								}
							}

							BigDecimal payAmount = null;
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
								double dfpag = Double.parseDouble(merchantinfo.getCounter());
								logger.info("最低手续费:"+dfpag);
								rateStr = rate;
								fee = new BigDecimal(rate).multiply(dfactAmount).add(new BigDecimal(minPoundage));
								if(dfpag*100>fee.doubleValue())
								{
									fee=new BigDecimal(dfpag).multiply(new BigDecimal(100));
								}
								logger.info("清算金额:"+fee);
								payAmount = dfactAmount.subtract(fee);
								logger.info("清算金额:"+paymentAmount);
								if(payAmount.doubleValue()<0)
								{
									payAmount=new BigDecimal(0);
								}
							}
							// 设置结算金额
							pmsAppTransInfo.setPayamount(payAmount.doubleValue()+"");// 结算金额
							pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
							pmsAppTransInfo.setPoundage(fee.toString());
							pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
							// 转换double为int
							Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

							// 验证支付方式是否开启
							payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.merchantCollect,
									PaymentCodeEnum.GatewayCodePay, oAgentNo, merchantinfo.getMercId());
							if (!payCheckResult.getErrCode().equals("0")) {
								// 交易不支持
			
								logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
										+ PaymentCodeEnum.GatewayCodePay.getTypeCode());
								return setResp("13", "暂不支持该交易方式");
							}


							// 查看当前交易是否已经生成了流水表
							PospTransInfo pospTransInfo = null;
							// 流水表是否需要更新的标记 0 insert，1：update
							int insertOrUpdateFlag = 0;
							// 生成上送流水号
							String transOrderId = generateTransOrderId(TradeTypeEnum.merchantCollect, PaymentCodeEnum.GatewayCodePay);
							if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
								// 已经存在，修改流水号，设置pospsn为空
								logger.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
								pospTransInfo.setTransOrderId(originalinfo.getV_oid());
								pospTransInfo.setResponsecode("99");
								pospTransInfo.setPospsn("");
								insertOrUpdateFlag = 1;
							} else {
								// 不存在流水，生成一个流水
								pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
								// 设置上送流水号
								pospTransInfo.setTransOrderId(originalinfo.getV_oid());
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
							int num=pmsAppTransInfoDao.update(pmsAppTransInfo);
							if (num>0) {
								
								retMap.put("v_code", "00");
								retMap.put("v_msg", "成功");
								
							} else {
								// 交易金额小于收款最低金额
								retMap.put("v_code", "15");
								retMap.put("v_msg", "请求失败");
								logger.info("生成订单流水失败");
							}
						}
		        } else {
					// 请求参数为空
					logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
					return setResp("16", "商户没有进行实名认证,请重试或联系客服");
				}
			} else {
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				return setResp("16", "商户没有进行实名认证,请重试或联系客服");
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
		result.put("v_code", respCode);
		result.put("v_msg", respInfo);
		return result;
	}
	/**
	 * 
	 * @Description 网关修改订单信息
	 * @author Administrator
	 * @param respCode
	 * @param respInfo
	 * @return
	 */
	public void otherInvoke(GateWayQueryResponseEntity result) throws Exception {
		// TODO Auto-generated method stub

		logger.info("上游返回的数据" + result);
		// 流水表transOrderId
		String transOrderId = result.getV_oid();
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		logger.info("流水表信息" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		logger.info("订单表信息" + pmsAppTransInfo);
		// 查询结果成功
		if ("0000".equals(result.getV_status().toString())) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getV_msg().toString());
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
				pospTransInfo.setPospsn(result.getV_oid());
				pospTransInfo.setTransOrderId(result.getV_attach());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("1001".equals(result.getV_status().toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getV_msg().toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getV_oid());
				pospTransInfo.setTransOrderId(result.getV_attach());
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}else if ("1004".equals(result.getV_status().toString())) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.returnMoneySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getV_msg().toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if(updateAppTrans==1) {
				logger.info("退款修改成功");
			}
		}

	}
	
	public int updateBusinfo(String orderId,String bpSerialNum) throws Exception {
		PospTransInfo pospTransInfo=new PospTransInfo();
		pospTransInfo.setPospsn(orderId);
		pospTransInfo.setTransOrderId(bpSerialNum);
		pospTransInfo.setOrderId(orderId);
		return pospTransInfoDAO.updateByOrderId(pospTransInfo);
	}
	/**
	 * 
	 * @Description 网关查询
	 * @author Administrator
	 * @param respCode
	 * @param respInfo
	 * @return
	 */
	public Map<String, String> gateWayQuery(GateWayQueryRequestEntity query) {

		Map<String, String> result = new HashMap<>();
		OriginalOrderInfo origin = new OriginalOrderInfo();
		String orderid = query.getV_oid();
		logger.info("网关查询订单号:" + orderid);
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
						result.put("v_status", "0000");// 支付状态
						result.put("v_status_msg", "支付成功");
					} else if(("1".equals(pmsAppTransInfo.getStatus()))){
						result.put("v_status", "1001");// 支付状态
						result.put("v_status_msg", "支付失败");
					}else{
						result.put("v_status", "200");// 支付状态
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
	 * @Description 网关100%入金
	 * @author Administrator
	 * @param respCode
	 * @param respInfo
	 * @return
	 */
	public synchronized int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo)
			throws Exception {
		logger.info("代付实时填金:"+JSON.toJSON(originalInfo));
		DecimalFormat df =new DecimalFormat("#.00");
		PmsMerchantInfo pmsMerchantInfo =new PmsMerchantInfo();
		PmsDaifuMerchantInfo pmsDaifuMerchantInfo=new PmsDaifuMerchantInfo();
		PmsMerchantInfo merchantInfo =pmsMerchantInfoDao.selectMercByMercId(originalInfo.getPid());
		logger.info("merchantInfo:"+JSON.toJSON(merchantInfo));
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(originalInfo.getOrderId());
		logger.info("pmsAppTransInfo:"+JSON.toJSON(pmsAppTransInfo));
		pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
		PmsDaifuMerchantInfo daifuMerchantInfo =pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(pmsDaifuMerchantInfo);
		logger.info("daifuMerchantInfo:"+JSON.toJSON(daifuMerchantInfo));
		if(daifuMerchantInfo!=null){
			logger.info("11111111111111111111111");
			return 0;
		}else{
			if("0".equals(merchantInfo.getOpenPay())){
				//手续费
				Double poundage =Double.parseDouble(pmsAppTransInfo.getPoundage());
				poundage=Double.parseDouble(df.format(poundage));
				String position= merchantInfo.getPosition();
				Double amount=Double.parseDouble(originalInfo.getOrderAmount());
				logger.info("订单金额："+amount);
				BigDecimal  positions =new BigDecimal(position);
				Double ds =positions.doubleValue();
				Double dd=0.0;
				dd =amount*100-poundage;
				dd =(dd+ds);
				logger.info("来了1---------");
				pmsMerchantInfo.setMercId(originalInfo.getPid());
				pmsMerchantInfo.setPosition(df.format(dd));
				//商户号
				pmsDaifuMerchantInfo.setMercId(originalInfo.getPid());
				//订单号
				pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
				//总金额
				pmsDaifuMerchantInfo.setAmount((Double.parseDouble(originalInfo.getOrderAmount()))+"");
				//状态
				pmsDaifuMerchantInfo.setResponsecode("00");
				//备注
				pmsDaifuMerchantInfo.setRemarks("D0");
				//记录描述
				pmsDaifuMerchantInfo.setRecordDescription("订单号:"+originalInfo.getOrderId()+"交易金额:"+originalInfo.getOrderAmount());
				//交易类型
				pmsDaifuMerchantInfo.setTransactionType(pmsAppTransInfo.getPaymenttype());
				//发生额
				pmsDaifuMerchantInfo.setPayamount((Double.parseDouble(originalInfo.getOrderAmount()))+"");
				//账户余额
				pmsDaifuMerchantInfo.setPosition(df.format(dd));
				//手续费
				pmsDaifuMerchantInfo.setPayCounter(poundage/100+"");
				pmsDaifuMerchantInfo.setOagentno("100333");
				logger.info("来了2---------");
				//交易时间
				//pmsDaifuMerchantInfo.setCreationdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				int s=pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
				logger.info("---s:"+s);
				logger.info("来了3---------");
				int i =pmsMerchantInfoDao.UpdatePmsMerchantInfo(pmsMerchantInfo);
				logger.info("---i:"+i);
				return i;
			}else{
				logger.info("此商户未开通代付！！");
			}
		}
		
		return 0;
	}


	@Override
	public Map<String, String> gatYftk(GatrWayGefundEntity param ,Map<String, String> result) {
		try {
		
		PmsBusinessPos busInfo =selectKey(param.getV_mid());
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("version", "1.0.0");
		params1.put("merchantId", busInfo.getBusinessnum());
		params1.put("settleMerchantId", busInfo.getBusinessnum());
		params1.put("merchantOrderId", param.getV_oid());
		params1.put("merchantOrderTime", param.getV_time());
		Double dd = Double.parseDouble(param.getV_txnAmt()) * 100;
		Integer ii = dd.intValue();
		params1.put("merchantOrderAmt", ii.toString());
		params1.put("gwType", "01");
		params1.put("backUrl", YFUtil.tkNotifyUrl);// 商户异步通知接口
		params1.put("orgTransTime", param.getV_orgTransTime());//原始订单时间
		params1.put("orgBpSerialNum", param.getV_orgBpSerialNum());//原始订单

		System.out.println("网银-置单上送报文：" + params1.toString());
		final String merCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "/ky/" + params1.get("merchantId") + ".cer";
		final String pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "//ky//" + params1.get("merchantId") + ".pfx";
		final String pfxPwd = busInfo.getKek();
		YufuCipher cipher = null;
		YufuCipherSupport instance = null;
		cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
		//YufuCipher cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath,pfxPwd);
		ParamPacket bo = cipher.doPack(params1);
		logger.info("11!:" + JSON.toJSON(bo));
		TreeMap<String, String> map_param = new TreeMap<>();
		map_param.put("merchantId", busInfo.getBusinessnum());
		map_param.put("data", URLEncoder.encode(bo.getData(), "utf-8"));
		map_param.put("enc", URLEncoder.encode(bo.getEnc(), "utf-8"));
		map_param.put("sign", URLEncoder.encode(bo.getSign(), "utf-8"));
		String urls = "";
		if ("000001110100000812".equals(busInfo.getBusinessnum())) {
			urls = " http://malltest.yfpayment.com/payment/refund.do";
		} else {
			urls = " http://www.yfpayment.com/payment/refund.do";
		}
		String returnStr = PostUtils.doPost(urls, map_param);
		if (returnStr != null && !"".equals(returnStr)) {
			// 二、验签解密
			returnStr = URLDecoder.decode(returnStr, "utf-8");
			System.out.println("WEB-置单应答报文：" + returnStr.toString());
			TreeMap<String, String> boMap = JSON.parseObject(returnStr,
					new TypeReference<TreeMap<String, String>>() {
					});
			Map<String, String> payshowParams = cipher.unPack(new ParamPacket(
					boMap.get("data"), boMap.get("enc"), boMap.get("sign")));
			System.out.println("解密后的置单应答结果：" + payshowParams);
			if ("0000".equals(payshowParams.get("respCode"))) {
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
				if("01".equals(payshowParams.get("transStatus"))) {
					result.put("v_state", "00");
					result.put("v_state_msg", "退款成功");
				}else if("02".equals(payshowParams.get("transStatus"))) {
					result.put("v_state", "01");
					result.put("v_state_msg", "退款失败");
				}else if("03".equals(payshowParams.get("transStatus"))) {
					result.put("v_state", "200");
					result.put("v_state_msg", "退款中");
				}
				
			} else {
				result.put("v_code", "01");
				result.put("v_msg", payshowParams.get("respDesc"));
			}
		}else {
			System.out.println("置单返回报文为空！");
		}
		
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	
	@Override
	public Map<String, String> gatYfQuick(GatrWayGefundEntity param, Map<String, String> result) {
		try {
			
			/*PmsBusinessPos busInfo =selectKey(param.getV_mid());
			Map<String, String> params1 = new HashMap<String, String>();
			params1.put("version", "1.0.0");
			params1.put("merchantId", busInfo.getBusinessnum());
			params1.put("settleMerchantId", busInfo.getBusinessnum());
			params1.put("merchantOrderId", param.getV_oid());
			params1.put("merchantOrderTime", param.getV_time());
			Double dd = Double.parseDouble(param.getV_txnAmt()) * 100;
			Integer ii = dd.intValue();
			params1.put("merchantOrderAmt", ii.toString());
			params1.put("gwType", "01");
			params1.put("backUrl", YFUtil.tkNotifyUrl);// 商户异步通知接口
			params1.put("orgTransTime", param.getV_orgTransTime());//原始订单时间
			params1.put("orgBpSerialNum", param.getV_orgBpSerialNum());//原始订单

			System.out.println("网银-置单上送报文：" + params1.toString());
			final String merCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
					.getParentFile().getCanonicalPath() + "/ky/" + params1.get("merchantId") + ".cer";
			final String pfxPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
					.getParentFile().getCanonicalPath() + "//ky//" + params1.get("merchantId") + ".pfx";
			final String pfxPwd = busInfo.getKek();
			YufuCipher cipher = null;
			YufuCipherSupport instance = null;
			cipher = YufuCipherSupport.getCipherInstance( merCertPath, pfxPath, pfxPwd,cipher,instance);
			//YufuCipher cipher = YufuCipherSupport.getCipherInstance(merCertPath, pfxPath,pfxPwd);
			ParamPacket bo = cipher.doPack(params1);
			logger.info("11!:" + JSON.toJSON(bo));
			TreeMap<String, String> map_param = new TreeMap<>();
			map_param.put("merchantId", busInfo.getBusinessnum());
			map_param.put("data", URLEncoder.encode(bo.getData(), "utf-8"));
			map_param.put("enc", URLEncoder.encode(bo.getEnc(), "utf-8"));
			map_param.put("sign", URLEncoder.encode(bo.getSign(), "utf-8"));
			String urls = "";
			if ("000001110100000812".equals(busInfo.getBusinessnum())) {
				urls = " http://malltest.yfpayment.com/payment/refund.do";
			} else {
				urls = " http://www.yfpayment.com/payment/refund.do";
			}
			String returnStr = PostUtils.doPost(urls, map_param);
			if (returnStr != null && !"".equals(returnStr)) {
				// 二、验签解密
				returnStr = URLDecoder.decode(returnStr, "utf-8");
				System.out.println("WEB-置单应答报文：" + returnStr.toString());
				TreeMap<String, String> boMap = JSON.parseObject(returnStr,
						new TypeReference<TreeMap<String, String>>() {
						});
				Map<String, String> payshowParams = cipher.unPack(new ParamPacket(
						boMap.get("data"), boMap.get("enc"), boMap.get("sign")));
				System.out.println("解密后的置单应答结果：" + payshowParams);
				if ("0000".equals(payshowParams.get("respCode"))) {
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					if("01".equals(payshowParams.get("transStatus"))) {
						result.put("v_state", "00");
						result.put("v_state_msg", "退款成功");
					}else if("02".equals(payshowParams.get("transStatus"))) {
						result.put("v_state", "01");
						result.put("v_state_msg", "退款失败");
					}else if("03".equals(payshowParams.get("transStatus"))) {
						result.put("v_state", "200");
						result.put("v_state_msg", "退款中");
					}
					
				} else {
					result.put("v_code", "01");
					result.put("v_msg", payshowParams.get("respDesc"));
				}
			}else {
				System.out.println("置单返回报文为空！");
			}*/
			
			} catch (Exception e) {
				// TODO: handle exception
			}
			return result;
	}

}

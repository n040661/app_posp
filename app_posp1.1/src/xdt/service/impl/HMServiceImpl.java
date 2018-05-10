package xdt.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import xdt.dao.IAmountLimitControlDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayTypeControlDao;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.hfb.Desede;
import xdt.dto.hfb.HFBPayRequest;
import xdt.dto.hfb.HeepayClient2;
import xdt.dto.hfb.HfbRequest;
import xdt.dto.hfb.HfbResponse;
import xdt.dto.hfb.HfbUtil;
import xdt.dto.hm.AesEncryption;
import xdt.dto.hm.HMRequest;
import xdt.dto.hm.HMResponse;
import xdt.dto.hm.HMUtil;
import xdt.dto.hm.HttpsUtil;
import xdt.dto.hm.SHA256Util;
import xdt.dto.hm.TimeUtil;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.service.HfQuickPayService;
import xdt.service.IHMService;
import xdt.service.IPublicTradeVerifyService;
import xdt.util.Constants;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;

@Service
public class HMServiceImpl extends BaseServiceImpl implements IHMService {

	Logger log = Logger.getLogger(this.getClass());

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
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	@Resource
	private IAmountLimitControlDao amountLimitControlDao;// 最大值最小值总开关判断
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;// 代付
	@Resource
	private HfQuickPayService payService;
	@Override
	public Map<String, String> shortcutAlipay(HMRequest hmRequest,
			Map<String, String> result) {

		log.info("恒明参数：" + hmRequest);
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = hmRequest.getOrderNumber(); // 10业务号2业务细; 订单号
													// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = hmRequest.getMerchantId();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
					.searchList(merchantinfo);
			log.info("查询当前商户信息" + merchantList);
			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(hmRequest.getOrderNumber());// ---------------------------
				oriInfo.setPid(hmRequest.getMerchantId());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoWxPay(hmRequest, out_trade_no, mercId);
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
					// 微信支付
					paramMap.put("paymentcode",
							PaymentCodeEnum.moBaoQuickPay.getTypeCode());

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
								log.error("此功能暂时关闭!");
								result.put("respCode", "05");
								result.put("respMsg", "此功能暂时关闭");
							} else {
								log.error(resultInfoForOAgentNo.getMsg());
								result.put("respCode", "05");
								result.put("respMsg",
										resultInfoForOAgentNo.getMsg());
							}
						} else {

							if ("1".equals(status)) {

								// 判断支付方式时候开通总开关
								ResultInfo payCheckResult = null;

								payCheckResult = payTypeControlDao.checkLimit(
										oAgentNo,
										PaymentCodeEnum.moBaoQuickPay
												.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(
											hmRequest.getAmount());// 收款金额
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
										log.info("交易金额不在申请的范围之内");

									} else {
										ResultInfo resultinfo = null;
										// 商户渠道支付方式
										// 商户渠道交易类型
										// 验证支付方式是否开启
										resultinfo = iPublicTradeVerifyService
												.payTypeVerifyMer(
														PaymentCodeEnum.moBaoQuickPay,
														mercId);

										if ("0".equals(resultinfo.getErrCode())) {
											if ("0".equals(payStatus)) {
												// 有效
												// MIN_AMOUNT,MAX_AMOUNT ,RATE
												// ,STATUS
												String rateStr = appRateConfig
														.getRate(); // 商户费率
																	// RATE

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
														log.info("来了111111111111");
														String totalAmount = hmRequest
																.getAmount(); // 交易金额
														PmsAppTransInfo appTransInfo = this
																.insertOrder(
																		out_trade_no,
																		totalAmount,
																		mercId,
																		rateStr,
																		oAgentNo);
														log.info("来了22222222");
														log.info("appTransInfo1:"+appTransInfo);
														log.info("appTransInfo2:"+JSON.toJSONString(appTransInfo));
														if (appTransInfo != null) {

															result = otherInvokeCardPay(
																	hmRequest,
																	result,
																	appTransInfo);
															log.info("result:"+result);
															result.put("respCode","00");
															result.put("respMsg","请求成功");

														} else {
															// 交易金额小于收款最低金额
															result.put("respCode","11");
															result.put("respMsg","生成订单流水失败");
															log.info("生成订单流水失败");
														}

													} else {

														// 交易金额小于收款最低金额
														result.put("respCode",
																"10");
														result.put("respMsg",
																"交易金额大于收款最高金额");
														log.info("交易金额大于收款最高金额");
													}

												} else {
													// 交易金额小于收款最低金额
													result.put("respCode", "09");
													result.put("respMsg",
															"交易金额小于收款最低金额");
													log.info("交易金额小于收款最低金额");
												}

											} else {
												// 交易金额小于收款最低金额
												result.put("respCode", "12");
												result.put("respMsg", "商户收款关闭");
												log.info("商户交易关闭");
											}
										} else {
											// 交易金额小于收款最低金额
											result.put("respCode", "13");
											result.put("respMsg", "扫码支付关闭");
											log.info("扫码支付关闭");
										}

									}
								}

							} else {
								log.error("此功能暂未开通");
								result.put("respCode", "06");
								result.put("respMsg", "此功能暂未开通");

							}

						}

					} else {
						log.error("没有找到商户费率");
						result.put("respCode", "04");
						result.put("respMsg", "没有找到商户费率");
					}

				} else {
					log.error("不是正式商户!");
					result.put("respCode", "03");
					result.put("respMsg", "不是正式商户");
				}

			} else {
				log.error("商户不存在!");
				result.put("respCode", "02");
				result.put("respMsg", "商户不存在");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("处理异常", e);
		}

		return result;
	}

	public Map<String, String> otherInvokeCardPay(HMRequest hmRequest,
			Map<String, String> result, PmsAppTransInfo appTransInfo) {
			log.info("进来了！！！");
			try {
			PmsBusinessPos pmsBusinessPos =this.selectKey(hmRequest.getMerchantId());
			log.info("pmsBusinessPos:"+pmsBusinessPos);
			appTransInfo = pmsAppTransInfoDao.searchOrderInfo(appTransInfo
					.getOrderid());
			log.info("appTransInfo:"+appTransInfo);
			appTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
			pmsAppTransInfoDao.update(appTransInfo);
			log.info("进来了2313！！！");
			JSONObject requestObj = new JSONObject();
			requestObj.put("ordernumber",hmRequest.getOrderNumber());
			requestObj.put("merchantid", "M"+pmsBusinessPos.getBusinessnum());
			requestObj.put("username", hmRequest.getUserName());
			requestObj.put("userpid", hmRequest.getUserId());
			requestObj.put("usercardno", hmRequest.getUserCardNo());
			requestObj.put("usertel", hmRequest.getUserTel());
			requestObj.put("amount", hmRequest.getAmount());// 单位分 100=1元
			requestObj.put("backurl",HMUtil.backurl);
			requestObj.put("returnurl", "");
				String encryptdata = AesEncryption.Encrypt(
						requestObj.toJSONString(), HMUtil.aeskey, HMUtil.aeskey);
	
				String timestamp = TimeUtil.getTime();
				String signstr = SHA256Util.sha256(pmsBusinessPos.getKek()
						+ "M"+pmsBusinessPos.getBusinessnum() + encryptdata + timestamp
						+ pmsBusinessPos.getKek());
				System.out.println(signstr);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("merchantid", "M"+pmsBusinessPos.getBusinessnum());
				jsonObject.put("data", encryptdata);
				jsonObject.put("timestamp", timestamp);
				jsonObject.put("sign", signstr);
				String postdata = "merchantid=" + "M"+pmsBusinessPos.getBusinessnum() + "&data="
						+ encryptdata + "&timestamp=" + timestamp + "&sign="
						+ signstr;
				String openApiUrl="";
				if("0".equals(hmRequest.getType())){
					openApiUrl=HMUtil.openApiUrl;
				}else if("1".equals(hmRequest.getType())){
					openApiUrl=HMUtil.openApiUrl1;
				}
				String results = HttpsUtil.doSslPost(openApiUrl, postdata,
						"utf-8");
				log.info("恒明返回参数：" + results);
				
				JSONObject responseObj = JSONObject.parseObject(results);
				log.info("message:"+responseObj.get("message"));
				result.put("orderNumber",hmRequest.getOrderNumber());
				result.put("amount", hmRequest.getAmount());
				result.put("merchantId", hmRequest.getMerchantId());
				if("0".equals(responseObj.get("ret").toString())){
					String dedata = AesEncryption.Desencrypt(responseObj
							.get("data").toString(), HMUtil.aeskey, HMUtil.aeskey);
					log.info("恒明解析参数：" + dedata);
				JSONObject jsonObject2 = JSONObject.parseObject(dedata);
				
				// 查看当前交易是否已经生成了流水表
				PospTransInfo pospTransInfo = null;
				// 流水表是否需要更新的标记 0 insert，1：update
				int insertOrUpdateFlag = 0;
				log.info("***************进入payHandle5-14-3***************");
				// 生成上送流水号
				String transOrderId = jsonObject2.getString("payorderno");
				log.info("***************进入payHandle5-15***************");
				if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(jsonObject2.getString("ordernumber"))) != null) {
					// 已经存在，修改流水号，设置pospsn为空
					log.info("订单号：" + hmRequest.getOrderNumber() + ",生成上送通道的流水号："
							+ transOrderId);
					pospTransInfo.setTransOrderId(transOrderId);
					pospTransInfo.setResponsecode("20");
					pospTransInfo.setPospsn("");
					insertOrUpdateFlag = 1;
					log.info("***************进入payHandle5-16***************");
				} else {
					// 不存在流水，生成一个流水
					pospTransInfo = InsertJournal(appTransInfo);
					// 设置上送流水号
					// 通道订单号
					pospTransInfo.setTransOrderId(transOrderId);
					insertOrUpdateFlag = 0;
				}
				log.info("***************进入payHandle5-17***************");
				// 插入流水表信息
				if (insertOrUpdateFlag == 0) {
					// 插入一条流水
					pospTransInfoDAO.insert(pospTransInfo);
				} else if (insertOrUpdateFlag == 1) {
					// 更新一条流水
					pospTransInfoDAO.updateByOrderId(pospTransInfo);
				}
				PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
				if("0".equals(jsonObject2.get("orderstate"))){
					result.put("state","00");
					result.put("message", jsonObject2.getString("payinfo"));
					pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
					pmsAppTransInfo.setThirdPartResultCode("0");
					pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
					// 修改订单
					int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
					if (updateAppTrans == 1) {
						// log.info("修改余额");
						// 修改余额
						log.info("订单表信息：" + pmsAppTransInfo);
						// updateMerchantBanlance(pmsAppTransInfo);
						// 更新流水表
						pospTransInfo.setResponsecode("00");
						if(pmsAppTransInfo.getOrderid()!=null&& pmsAppTransInfo.getOrderid()!=""){
							pospTransInfo.setPospsn(pmsAppTransInfo.getOrderid());
						}
						
						log.info("更新流水");
						log.info("流水表信息：" + pospTransInfo);
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
					OriginalOrderInfo originalInfo = null;
					if (jsonObject2.get("payorderno") != null && jsonObject2.get("payorderno")!= "") {
						originalInfo = this.payService.getOriginOrderInfo(jsonObject2.get("payorderno").toString());
					}
					int ii =UpdatePmsMerchantInfo(originalInfo);
					if(ii==1){
						log.info("实时填金成功！！");
					}
					
				}else if("8".equals(jsonObject2.get("orderstate"))||"9".equals(jsonObject2.get("orderstate"))){
					result.put("state","05");
					result.put("message", "待支付");
				}else{
					result.put("state","01");
					result.put("message", jsonObject2.getString("payinfo"));
					pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
					pmsAppTransInfo.setThirdPartResultCode("1");
					pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
					// 修改订单
					int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
					if (updateAppTrans == 1) {
						// 更新流水表
						pospTransInfo.setResponsecode("02");
						if(jsonObject2.get("payorderno")!=null&& jsonObject2.get("payorderno")!=""){
							pospTransInfo.setPospsn(jsonObject2.get("payorderno").toString());
						}
						log.info("更新流水");
						log.info("流水表信息：" + pospTransInfo);
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
				}
				}else{
					result.put("state","01");
					result.put("message", responseObj.getString("message"));
				}
				
		} catch (Exception e) {
			e.printStackTrace();
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
	private int saveOriginAlInfoWxPay(HMRequest hmRequest, String orderid,
			String mercId) throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(orderid);
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		// 想要传服务器要改实体
		info.setBgUrl(hmRequest.getUrl());
		info.setPageUrl(hmRequest.getReUrl());
		Double amt = Double.parseDouble(hmRequest.getAmount());// 单位分
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
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

		System.out.println("12345613454354=" + orderid);
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
		double fee1 = poundage.doubleValue();
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);
		// 结算金额
		BigDecimal payAmount = null;
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
				.searchList(merchantinfo);

		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			// 正式商户
			merchantinfo = merchantList.get(0);
			if (merchantinfo.getCounter() != null) {
				Double ss = Double.parseDouble(merchantinfo.getCounter());
				double num = ss * 100;
				if (fee1 < num) {
					b = new BigDecimal(String.valueOf(num));
					payAmount = dfactAmount.subtract(b);
				} else {
					b = poundage;
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
				log.info("订单入库失败， 订单号：" + orderid + "，结束时间："
						+ UtilDate.getDateFormatter() + "。订单详细信息：" + sendString);
				throw new RuntimeException("手动抛出");
			}
			log.info("执行完成！！");
		} catch (Exception e) {
			log.info(
					"订单入库失败， 订单号：" + orderid + "，结束时间："
							+ UtilDate.getDateFormatter() + "。订单详细信息："
							+ sendString, e);
			throw new RuntimeException("手动抛出");
		}
		return pmsAppTransInfo;

	}


	/**
	 * 录入交易流水 并记算费率
	 * 
	 * @throws Exception
	 */
	public PospTransInfo InsertJournal(PmsAppTransInfo pmsAppTransInfo)
			throws Exception {
		log.info("----插入流水开始----");
		PospTransInfo pospTransInfo = new PospTransInfo();
		Integer id = pospTransInfoDAO.getNextTransid();
		if (id != null && id != 0) {
			pospTransInfo.setId(id);
		} else {
			log.info("根据订单生成流水失败，orderid：" + pmsAppTransInfo.getOrderid());
			return null;
		}
		// 获取通道的标准费率 END

		// 设置主机交易流水号
		pospTransInfo.setSysseqno(null);
		// 设置宣称费率
		pospTransInfo.setTransfee2(null);
		// 设置通道费率
		pospTransInfo.setTransfee4(null);
		// 设置实际佣金
		pospTransInfo.setTransfee1(null);
		// 设置消费冲正原因
		pospTransInfo.setReason(null);
		// 设置说明
		pospTransInfo.setRemark(pmsAppTransInfo.getTradetype() + "  金额："
				+ pmsAppTransInfo.getFactamount());
		// 设置SIM卡
		pospTransInfo.setSimId(null);
		// 设置 TAC
		pospTransInfo.setTac(null);
		// 设置银行编码
		pospTransInfo.setBnkCd(null);
		// 设置平台流水奥 这里默认设置第三方订单号
		pospTransInfo.setPospsn(pmsAppTransInfo.getPortorderid());
		// 设置卡有效期
		pospTransInfo.setCardvaliddate(null);
		// 设置通道pos终端号
		pospTransInfo.setBuspos(null);
		// 设置pos平台交易吗
		pospTransInfo.setPospservicecode(null);
		// 设置冲正流水
		pospTransInfo.setCancelflag(null);
		// 设置商户号
		pospTransInfo.setMerchantcode(pmsAppTransInfo.getMercid());
		// 设置补录时记录上传的终端机流水号
		pospTransInfo.setTerminalsn(null);
		// 设置交易上送帐期
		pospTransInfo.setSenddate(new Date());
		// 服务网点PIN码
		pospTransInfo.setCounterpin(null);
		// 设置渠道号 03：手机
		pospTransInfo.setChannelno("03");
		// 设置银行名称
		pospTransInfo.setBnkNm(null);
		// 设置posid
		pospTransInfo.setPosid(null);
		// 设置交易码 默认都为消费业务
		pospTransInfo.setTranscode("000000");
		// 设置交易安全控制信息
		pospTransInfo.setTranssecuritycontrol(null);
		// 设置卡类型
		pospTransInfo.setCrdTyp(null);
		// 设置卡号
		pospTransInfo.setCardno(null);
		// 设置真正的交易类型 交易码 +交易类型+支付方式
		pospTransInfo.setSearchTransCode("000000"
				+ pmsAppTransInfo.getTradetypecode()
				+ pmsAppTransInfo.getPaymentcode());
		// 设置pos交易日期
		pospTransInfo.setTransdate(UtilDate.getDate());
		// 设置pos交易时间
		pospTransInfo.setTranstime(UtilDate.getDateTime());
		// 设置批量结算结果标志
		pospTransInfo.setSettlementflag(null);
		// 设置最近批结算ID
		pospTransInfo.setSettlementid(null);
		// 设置授权码
		pospTransInfo.setAuthoritycode(null);
		// 设置是否自清 默认自清
		pospTransInfo.setIsClearSelf(null);
		// 设置交易响应标志 00-成功
		pospTransInfo.setResponsecode(null);
		/*
		 * if(pmsAppTransInfo.getStatus().equals("0")){
		 * pospTransInfo.setResponsecode("00"); }else{
		 * pospTransInfo.setResponsecode(null); }
		 */
		// 设置订单id
		pospTransInfo.setOrderId(pmsAppTransInfo.getOrderid());
		// 设置通道商户编码 商户编码不设置
		pospTransInfo.setBusinfo(null);
		// 设置附加费用
		pospTransInfo.setAddfee(null);
		// 设置刷卡费率 当前处理为调用第三方处理，刷卡费率不设置
		pospTransInfo.setPremiumrate(null);
		// 设置原始交易记录报文id
		pospTransInfo.setPfmtid(null);
		// 服务网点输入方式
		pospTransInfo.setInputtype(null);
		// 0-脱机POS上送流水，1-联机消费流水
		pospTransInfo.setTransstatus(null);
		// 设置基站信息
		pospTransInfo.setStationInfo(null);
		// 设置交易时间间隔 这里先不处理，没有发现需要用到的地方
		pospTransInfo.setInterVal(null);
		// 设置关联路由id
		pospTransInfo.setRouteid(null);
		// 设置交易消息类型 交易类型+支付方式
		pospTransInfo.setMsgtype(pmsAppTransInfo.getTradetypecode()
				+ pmsAppTransInfo.getPaymentcode());
		// 设置发生额
		pospTransInfo.setTransamt(new BigDecimal(pmsAppTransInfo
				.getFactamount()));
		// 设置终端号
		pospTransInfo.setPosterminalid(null);
		// 设置操作员id
		pospTransInfo.setOperid(null);
		// 设置POS服务平台代码
		pospTransInfo.setPospid(null);
		// 设置货币代码
		pospTransInfo.setCurrencycode(null);
		// 结算日期
		pospTransInfo.setBalancedate(null);
		// PSAM卡号
		pospTransInfo.setPsamno(null);
		// 个人标识码
		pospTransInfo.setPersonalid(null);
		// 卡号
		pospTransInfo.setCrdNm(null);
		// 设置冲正标志 0-正常交易，1-冲正交易，2-被冲正交易
		pospTransInfo.setCancelflag(0);
		// 设置冻结状态
		pospTransInfo.setFreezeState(null);
		// 设置终端序列号
		pospTransInfo.setPossn(null);
		// 设置服务网点条件码
		pospTransInfo.setConuterconditioncode(null);
		// 是否App交易
		pospTransInfo.setIsapp(1);
		// 设置支付方式
		pospTransInfo.setPaymentType(pmsAppTransInfo.getPaymentcode());
		// 设置批次号
		pospTransInfo.setBatno(null);
		// O单编号
		pospTransInfo.setoAgentNo(pmsAppTransInfo.getoAgentNo());

		return pospTransInfo;
	}
	@Override
	public synchronized int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo)
			throws Exception {
		log.info("代付实时填金:"+JSON.toJSON(originalInfo));
		DecimalFormat df =new DecimalFormat("#.00");
		//PmsMerchantInfo pmsMerchantInfo =new PmsMerchantInfo();
		PmsDaifuMerchantInfo pmsDaifuMerchantInfo=new PmsDaifuMerchantInfo();
		PmsMerchantInfo merchantInfo =pmsMerchantInfoDao.selectMercByMercId(originalInfo.getPid());
		log.info("merchantInfo:"+JSON.toJSON(merchantInfo));
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(originalInfo.getOrderId());
		log.info("pmsAppTransInfo:"+JSON.toJSON(pmsAppTransInfo));
		pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
		PmsDaifuMerchantInfo daifuMerchantInfo =pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(pmsDaifuMerchantInfo);
		log.info("daifuMerchantInfo:"+JSON.toJSON(daifuMerchantInfo));
		if(daifuMerchantInfo!=null){
			log.info("11111111111111111111111");
			return 0;
		}else{
			if("0".equals(merchantInfo.getOpenPay())){
				//手续费
				Double poundage =Double.parseDouble(pmsAppTransInfo.getPoundage());
				poundage=Double.parseDouble(df.format(poundage));
				String position= merchantInfo.getPosition();
				Double amount=Double.parseDouble(originalInfo.getOrderAmount());
				log.info("订单金额："+amount);
				BigDecimal  positions =new BigDecimal(position);
				//Double ds =positions.doubleValue();
				Double dd =amount*100-poundage;
				//dd =(dd+ds);
				log.info("来了1---------");
				Map<String, String> map =new HashMap<>();
				map.put("machId", originalInfo.getPid());
				map.put("payMoney", dd.toString());
				int i =pmsMerchantInfoDao.updataPay(map);
				if(i!=1) {
					log.info("实时填金失败！");
					//状态
					pmsDaifuMerchantInfo.setResponsecode("01");
				}else {
					//状态
					log.info("实时成功！");
					pmsDaifuMerchantInfo.setResponsecode("00");
				}
				log.info("来到这里了11！");
				PmsMerchantInfo info = select(originalInfo.getPid());
				//pmsMerchantInfo.setMercId(originalInfo.getPid());
				//pmsMerchantInfo.setPosition(df.format(dd));
				//商户号
				log.info("来到这里了22！");
				pmsDaifuMerchantInfo.setMercId(originalInfo.getPid());
				//订单号
				pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
				//总金额
				pmsDaifuMerchantInfo.setAmount((Double.parseDouble(originalInfo.getOrderAmount()))+"");
				
				//备注
				pmsDaifuMerchantInfo.setRemarks("D0");
				//记录描述
				pmsDaifuMerchantInfo.setRecordDescription("订单号:"+originalInfo.getOrderId()+"交易金额:"+originalInfo.getOrderAmount());
				//交易类型
				pmsDaifuMerchantInfo.setTransactionType(pmsAppTransInfo.getPaymenttype());
				//发生额
				pmsDaifuMerchantInfo.setPayamount((Double.parseDouble(originalInfo.getOrderAmount()))+"");
				//账户余额
				pmsDaifuMerchantInfo.setPosition(info.getPosition());
				//手续费
				pmsDaifuMerchantInfo.setPayCounter(poundage/100+"");
				pmsDaifuMerchantInfo.setOagentno("100333");
				log.info("来了2---------");
				//交易时间
				//pmsDaifuMerchantInfo.setCreationdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				int s=pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
				log.info("---s:"+s);
				log.info("来了3---------");
				//int i =pmsMerchantInfoDao.UpdatePmsMerchantInfo(pmsMerchantInfo);
				log.info("---i:"+i);
				return i;
			}else{
				log.info("此商户未开通代付！！");
			}
		}
		
		return 0;
	}

	@Override
	public synchronized Map<String, String> pay(HMRequest hmRequest,
			Map<String, String> result) {
		log.info("恒明----下游传送代付参数:"+JSON.toJSON(hmRequest));
		BigDecimal b1=new BigDecimal("0");// 下游上传的总金额
		BigDecimal b2=new BigDecimal("0");// 系统代付余额
		BigDecimal b3=new BigDecimal("0");// 单笔交易总手续费
		BigDecimal min=new BigDecimal("0");// 代付最小金额
		BigDecimal max=new BigDecimal("0");// 代付最大金额
		Double surplus;// 代付剩余金额
		log.info("恒明----查询当前代付订单是否存在");
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		Map<String, String> map =new HashMap<>();
		Map<String, String> maps=new HashMap<>();//填金
		model.setMercId(hmRequest.getMerchantId());
		model.setBatchNo(hmRequest.getOrderNumber());
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
			result.put("respCode", "0006");
			result.put("respMsg", "下单失败,订单存在");
			log.info("恒明----**********************代付 下单失败:{}");
			log.info("恒明----订单存在");
			return result;
		}
		try {
			label104: {

				log.info("********************恒明-------------根据商户号查询");
				String e = hmRequest.getMerchantId();
				PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
				merchantinfo.setMercId(e);
				List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
				if (merchantList.size() != 0 && !merchantList.isEmpty()) {
					merchantinfo = (PmsMerchantInfo) merchantList.get(0);
					if(merchantinfo.getOpenPay().equals("1")){
						result.put("respCode", "01");
						result.put("respMsg","未开通代付");
						return result;								
					}
					String oAgentNo = merchantinfo.getoAgentNo();
					log.info("***********恒明*************商户信息:" + merchantinfo);
					if (StringUtils.isBlank(oAgentNo)) {
						throw new RuntimeException("系统错误----------------o单编号为空");
					}

					if ("60".equals(merchantinfo.getMercSts())) {
						//插入异步数据
						saveOriginAlInfoWxPay1(hmRequest, hmRequest.getOrderNumber(), hmRequest.getMerchantId());
						// 判断交易类型
						log.info("***********恒明*************实际金额");
						// 分
						String payAmt= hmRequest.getAmount();
						b1 =new BigDecimal(payAmt);
						
						System.out.println("参数:"+b1.doubleValue());
						log.info("***********恒明*************校验欧单金额限制");
						log.info("恒明----下游上传代付总金额:" + b1.doubleValue());
						b3 = new BigDecimal(merchantinfo.getPoundage());
						log.info("恒明----系统商户代付单笔手续费:" + b3.doubleValue());
						min = new BigDecimal(merchantinfo.getMinDaiFu());
						log.info("恒明----系统代付最小金额:" + min.doubleValue());
						max = new BigDecimal(merchantinfo.getMaxDaiFu());
						log.info("恒明----系统代付最大金额:" + max.doubleValue());
						b2 = new BigDecimal(merchantinfo.getPosition());
						log.info("恒明----系统剩余可用额度:" + b2.doubleValue());
						
						
						
						if (b1.doubleValue() + b3.doubleValue()*100 > b2.doubleValue()) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额高于剩余额度");
							log.info("恒明**********************代付金额高于剩余额度");
							int i = add(hmRequest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("恒明----添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() < min.doubleValue() * 100) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额小于代付最小金额");
							log.info("恒明**********************代付金额小于代付最小金额");
							int i = add(hmRequest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("恒明--添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() > max.doubleValue() * 100) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额大于代付最大金额");
							log.info("恒明**********************代付金额大于代付最大金额");
							int i = add(hmRequest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("恒明--添加失败订单成功");
							}
							return result;
						}
							//surplus = b2.subtract(b1).doubleValue() - b3.doubleValue() * 100;
							//merchantinfo.setPosition(surplus.toString());
							//给代付扣款用
							Map<String, String> mapPay=new HashMap<>();
							mapPay.put("machId", hmRequest.getMerchantId());
							mapPay.put("payMoney", hmRequest.getAmount());
							int num =pmsMerchantInfoDao.updataD0(mapPay);
							
							if (num != 1) {
								log.info("恒明--扣款失败！！");
								result.put("respCode", "02");
								result.put("respMsg", "代付失败");
								return result;
							}
							log.info("恒明--扣款成功！！");
							int i =add(hmRequest, select(hmRequest.getMerchantId()), result, "200");
						PmsBusinessPos pmsBusinessPos =selectKey(hmRequest.getMerchantId());
						//int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
						if (i == 1) {
							log.info("恒明--代付订单添加成功");
							
							JSONObject requestObj = new JSONObject();
							requestObj.put("ordernumber",hmRequest.getOrderNumber());
							requestObj.put("username", hmRequest.getUserName());
							requestObj.put("usercardno", hmRequest.getUserCardNo());
							requestObj.put("accounttype", hmRequest.getAccountType());
							requestObj.put("amount", hmRequest.getAmount());// 单位分 100=1元
							requestObj.put("backurl",HMUtil.backurl);
							//requestObj.put("bank", hmRequest.getBank());
							//requestObj.put("userpidtype", hmRequest.getUserpidType());
							//requestObj.put("bankunion", hmRequest.getBankunion());
							requestObj.put("userpid", hmRequest.getUserId());
							requestObj.put("usertel", hmRequest.getUserTel());
							requestObj.put("returnurl", "http://www.baidu.com");
							requestObj.put("ordertype", "10");
							String encryptdata = AesEncryption.Encrypt(
									requestObj.toJSONString(), HMUtil.aeskey, HMUtil.aeskey);
				
							String timestamp = TimeUtil.getTime();
							String signstr = SHA256Util.sha256(pmsBusinessPos.getKek()
									+ "M"+pmsBusinessPos.getBusinessnum() + encryptdata + timestamp
									+ pmsBusinessPos.getKek());
							System.out.println(signstr);
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("merchantid", "M"+pmsBusinessPos.getBusinessnum());
							jsonObject.put("data", encryptdata);
							jsonObject.put("timestamp", timestamp);
							jsonObject.put("sign", signstr);
							String postdata = "merchantid=" + "M"+pmsBusinessPos.getBusinessnum() + "&data="
									+ encryptdata + "&timestamp=" + timestamp + "&sign="
									+ signstr;
							//判断是否T1和D0的代付
							/*String url="";
							if("1".equals(hmRequest.getType())) {
							url=HMUtil.payUrl;
							}else if("0".equals(hmRequest.getType())) {
								url=HMUtil.payUrlT1	;
							}*/
							String url =HMUtil.url+"/pay/unionpay/entrust/credit";
							String results = HttpsUtil.doSslPost(url, postdata,
									"utf-8");
							log.info("恒明返回参数：" + results);
							
							JSONObject responseObj = JSONObject.parseObject(results);
							log.info("message:"+responseObj.get("message"));
							if("0".equals(responseObj.getString("ret"))){
							String dedata = AesEncryption.Desencrypt(responseObj
										.get("data").toString(), HMUtil.aeskey, HMUtil.aeskey);
								log.info("恒明解析参数：" + dedata);
							JSONObject json = JSONObject.parseObject(dedata);
								if("0".equals(json.get("orderstate"))){
									//UpdateDaifu(hmRequest.getOrderNumber(), "00");
									result.put("respCode", "00");
						        	result.put("respMsg", "请求成功");
						        	result.put("merchantId", hmRequest.getMerchantId());
						        	result.put("orderNumber", hmRequest.getOrderNumber());
						        	result.put("amount", hmRequest.getAmount());
								}else if("9".equals(json.get("orderstate"))||"8".equals(json.get("orderstate"))){
									//UpdateDaifu(hmRequest.getOrderNumber(), "200");
									result.put("respCode", "200");
						        	result.put("respMsg", "处理中");
						        	result.put("merchantId", hmRequest.getMerchantId());
						        	result.put("orderNumber", hmRequest.getOrderNumber());
						        	result.put("amount", hmRequest.getAmount());
								}else{
									result.put("respCode", "01");
						        	result.put("respMsg", "请求失败！");
						        	result.put("merchantId", hmRequest.getMerchantId());
						        	result.put("orderNumber", hmRequest.getOrderNumber());
						        	result.put("amount", hmRequest.getAmount());
						        	UpdateDaifu(hmRequest.getOrderNumber(), "01");
						        	maps.put("payMoney",hmRequest.getAmount());
					     			maps.put("machId", hmRequest.getMerchantId());
									int nus = pmsMerchantInfoDao.updataPay(maps);
									if(nus==1){
										log.info("恒明***补款成功");
										//surplus = surplus+Double.parseDouble(hmRequest.getAmount());
										//根据商户号查询信息
										PmsMerchantInfo info= select(hmRequest.getMerchantId());
										merchantinfo.setPosition(info.getPosition());
										hmRequest.setOrderNumber(hmRequest.getOrderNumber()+"/A");
										int id =add(hmRequest, merchantinfo, result, "00");
										if(id==1){
											log.info("恒明代付补单成功");
										}
									}
								}
								
							}else{
								result.put("state","01");
								result.put("message", responseObj.getString("message"));
							}
						        
						}
					} else {
						throw new RuntimeException("恒明***系统错误----------------当前商户非正式商户");
					}

				} else {
					throw new RuntimeException("恒明***系统错误----------------商户不存在");
				}
				break label104;
			}

		} catch (Exception var43) {
			log.error("恒明*******************************代付错误", var43);
			try {
				throw var43;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	 log.info("***********恒明*********************代付------处理完成");
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
	private int saveOriginAlInfoWxPay1(HMRequest hmRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(orderid);
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("恒明代付");
		//想要传服务器要改实体
		info.setBgUrl(hmRequest.getUrl());
		Double amt = Double.parseDouble(hmRequest.getAmount());// 单位分
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
	}
	
	public synchronized int add(HMRequest hmRequest, PmsMerchantInfo merchantinfo, Map<String, String> result ,String state) throws Exception {
		log.info("进来添加代付订单了");
		BigDecimal b1=new BigDecimal("0");//总金额
		int iii=0;
		merchantinfo=select(hmRequest.getMerchantId());
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		b1 =new BigDecimal(hmRequest.getAmount());
				 model.setProvince(hmRequest.getProvince());
				 model.setCity(hmRequest.getCity());
				 model.setMercId(hmRequest.getMerchantId());
				 model.setCount("1");
				 model.setBatchNo(hmRequest.getOrderNumber());
				 model.setIdentity(hmRequest.getOrderNumber());
				 model.setAmount(b1.doubleValue()/100+"");
				 model.setCardno(hmRequest.getUserCardNo());
				 model.setRealname(hmRequest.getUserName());
				 if(hmRequest.getOrderNumber().indexOf("/A")!=-1){
					 model.setPayamount(b1.doubleValue()/100+"");
				 }else{
					 model.setPayamount("-" +b1.doubleValue()/100);
				 }
				 //联行号
				 model.setPmsbankno("");
				 if(hmRequest.getOrderNumber().indexOf("/A")!=-1){
					 model.setTransactionType("代付补款");
				 }else{
					 model.setTransactionType("代付");
				 }
				 model.setPosition(String.valueOf(merchantinfo.getPosition()));
				 model.setRemarks("D0");
				 model.setRecordDescription("批次号:" + hmRequest.getOrderNumber()+"订单号："+hmRequest.getOrderNumber()+ "错误原因:" + result.get("respMsg"));
				 model.setResponsecode(state);
				 model.setOagentno("100333");
				 model.setPayCounter(new BigDecimal(merchantinfo.getPoundage()).doubleValue() + "");
				 PmsDaifuMerchantInfo daifu = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model);
				 if (daifu == null) {
					iii = pmsDaifuMerchantInfoDao.insert(model);
					log.info("iii:" + iii);
				}
		
				 return iii;
			}
	public int UpdateDaifu(String batchNo, String responsecode) throws Exception {
		if(batchNo==null||batchNo==""){
			return 0;
		}
		log.info("原始数据:" + batchNo);

		PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

		log.info("上送的批次号:" + batchNo);

		pdf.setBatchNo(batchNo);
		pdf.setResponsecode(responsecode);
		return pmsDaifuMerchantInfoDao.update(pdf);
	}

	@Override
	public Map<String, String> select(HMRequest hmRequest,
			Map<String, String> result) {
		try {
			PmsBusinessPos pmsBusinessPos =selectKey(hmRequest.getMerchantId());
			JSONObject requestObj = new JSONObject();
			requestObj.put("ordernumber",hmRequest.getOrderNumber());
			String encryptdata = AesEncryption.Encrypt(requestObj.toJSONString(), HMUtil.aeskey, HMUtil.aeskey);

			String timestamp = TimeUtil.getTime();
			String signstr = SHA256Util.sha256(pmsBusinessPos.getKek()
					+ "M"+pmsBusinessPos.getBusinessnum() + encryptdata + timestamp
					+ pmsBusinessPos.getKek());
			System.out.println(signstr);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("merchantid", "M"+pmsBusinessPos.getBusinessnum());
			jsonObject.put("data", encryptdata);
			jsonObject.put("timestamp", timestamp);
			jsonObject.put("sign", signstr);
			String postdata = "merchantid=" + "M"+pmsBusinessPos.getBusinessnum() + "&data="
					+ encryptdata + "&timestamp=" + timestamp + "&sign="
					+ signstr;

			String results = HttpsUtil.doSslPost(HMUtil.selectUrl, postdata,
					"utf-8");
			log.info("恒明返回参数：" + results);
			
			JSONObject responseObj = JSONObject.parseObject(results);
			log.info("message:"+responseObj.get("message"));
			if("0".equals(responseObj.getString("ret"))){
			String dedata = AesEncryption.Desencrypt(responseObj
						.get("data").toString(), HMUtil.aeskey, HMUtil.aeskey);
				log.info("恒明解析参数：" + dedata);
			JSONObject json = JSONObject.parseObject(dedata);
			if("0".equals(json.getString("orderstate"))){
				result.put("respCode", "00");
				result.put("respMsg", "代付成功");
				result.put("merchantId", hmRequest.getMerchantId());
				result.put("orderNumber", hmRequest.getOrderNumber());
			}else if("9".equals(json.getString("orderstate"))){
				result.put("respCode", "200");
				result.put("respMsg", "代付中");
				result.put("merchantId", hmRequest.getMerchantId());
				result.put("orderNumber", hmRequest.getOrderNumber());
			}else{
				result.put("respCode", "01");
				result.put("respMsg", "代付失败");
				result.put("merchantId", hmRequest.getMerchantId());
				result.put("orderNumber", hmRequest.getOrderNumber());
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
}

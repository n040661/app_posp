/**
 * 
 */
package xdt.service.impl;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IPmsAddressDao;
import xdt.dao.IPmsAppTransInfoDao;
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
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.hengfeng.entity.PayQueryRequestEntity;
import xdt.quickpay.hengfeng.entity.PayQueryResponseEntity;
import xdt.quickpay.hengfeng.entity.PayRequestEntity;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.service.BeenQuickPayService;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.servlet.AppPospContext;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;
import cn.beecloud.BCEumeration.PAY_CHANNEL;
import cn.beecloud.BCPay;
import cn.beecloud.bean.BCOrder;

/**
 * @ClassName: HFQuickPayService
 * @Description: 第三方 恒丰快捷支付
 * @author LiShiwen
 * @date 2016年6月16日 上午10:51:28
 * 
 */
@Component
public class BeenCloudQuickPayServiceImpl extends BaseServiceImpl implements
		BeenQuickPayService {
	
	/**
	 * 记录日志
	 */
	private Logger log = Logger.getLogger(BeenCloudQuickPayServiceImpl.class);

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	IPmsGoodsDao pmsGoodsDao;
	private Logger logger = Logger.getLogger(ShopPayServiceImpl.class);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * xdt.service.impl.HfQuickPayService#payHandle(xdt.quickpay.hengfeng.entity
	 * .PayRequestEntity, javax.servlet.http.HttpSession)
	 */
	@Override
	public  void payHandle(PayRequestEntity originalinfo,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setContentType("text/html;charset=UTF-8");
		
		PrintWriter out=response.getWriter();
		
		String jsonString = null;

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(originalinfo.getPid());

		String merchantKey = keyinfo.getMerchantkey();

		HFSignUtil signUtil = new HFSignUtil();
		if (!signUtil.verify(PreSginUtil.paySigiString(originalinfo),
				originalinfo.getSignmsg(), merchantKey)) {
			responseDTO.setRetCode(11);
			responseDTO.setRetMessage("签名错误");
			jsonString = createJsonString(responseDTO);
			log.info("签名错误");
		}else{
			logger.info("验证当前是否已经下单");
			OriginalOrderInfo orig = new OriginalOrderInfo();
			orig.setMerchantOrderId(originalinfo.getTransactionid());
			orig.setPid(originalinfo.getPid());

			if (originalDao.selectByOriginal(orig) != null) {
				responseDTO.setRetCode(12);
				responseDTO.setRetMessage("下单重复");
				jsonString = createJsonString(responseDTO);
				log.info("下单重复");
			}else{
				String orderNumber = UtilMethod.getOrderid("182");
				/**
				 * 插入原始数据信息
				 */
				OriginalOrderInfo original = new OriginalOrderInfo();
				original.setMerchantOrderId(originalinfo.getTransactionid());// 原始数据的订单编号
				original.setOrderId(orderNumber); // 为主键
				original.setPid(originalinfo.getPid());
				original.setOrderTime(originalinfo.getOrdertime());
				NumberFormat nbf=NumberFormat.getInstance();   
				nbf.setMinimumFractionDigits(2);   
				Double orderAmount=Double.valueOf(originalinfo.getOrderamount());
				String c = nbf.format(orderAmount/100); 
				original.setOrderAmount(c);//单位分
				original.setPayType(originalinfo.getPaytype());
				original.setPageUrl(originalinfo.getPageurl());
				original.setBgUrl(originalinfo.getBgurl());
				original.setBankNo(originalinfo.getBankno());
				originalDao.insert(original);

				logger.info("根据商户号查询");
				String mercId = originalinfo.getPid();

				PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
				merchantinfo.setMercId(mercId);

				logger.info("o单编号");
				// 
				String oAgentNo = "";
				logger.info("查询当前商户信息");
				List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
				
				if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
					
					merchantinfo = merchantList.get(0);
					oAgentNo = merchantinfo.getoAgentNo();
					if (StringUtils.isBlank(oAgentNo)) {
						// 如果没有欧单编号，直接返回错误
						responseDTO.setRetCode(1);
						responseDTO.setRetMessage("参数错误");
						jsonString = createJsonString(responseDTO);
						log.info("参数错误,没有欧单编号");
					}else if ("60".equals(merchantinfo.getMercSts())) {
						logger.info("实际金额");
						String factAmount = originalinfo.getOrderamount();
						String cardNo = originalinfo.getBankno();
						logger.info("校验欧单金额限制");
						ResultInfo payCheckResult = iPublicTradeVerifyService.amountVerifyOagent((int) Double.parseDouble(factAmount),TradeTypeEnum.shop, oAgentNo);
						
						if (payCheckResult.getErrCode().equals("0")) {
							logger.info("校验欧单模块是否开启");
							ResultInfo payCheckResult1 = iPublicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.shop, oAgentNo);
							
							if (payCheckResult1.getErrCode().equals("0")) {
								logger.info("校验商户模块是否开启");
								ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.shop, mercId);
								if (payCheckResult3.getErrCode().equals("0")) {
									
									logger.info("校验商户金额限制");
									Map<String, String> paramMap = new HashMap<String, String>();
									paramMap.put("mercid", mercId);
									paramMap.put("businesscode",TradeTypeEnum.shop.getTypeCode());// 业务编号
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
											responseDTO.setRetCode(1);
											responseDTO.setRetMessage("金额超过最大交易金额");
											logger.info("交易金额大于最打金额");
											try {
												jsonString = createJsonString(responseDTO);
											} catch (Exception em) {
												em.printStackTrace();
											}
										} else if (new BigDecimal(paymentAmount)
												.compareTo(new BigDecimal(minTransMoney)) == -1) {
											// 金额小于最小金额
											responseDTO.setRetCode(1);
											responseDTO.setRetMessage("金额小于最小交易金额");
											try {
												jsonString = createJsonString(responseDTO);
											} catch (Exception em) {
												em.printStackTrace();
											}
											logger.info("交易金额小于最小金额");
										}else{
											
											// 
											logger.info("组装订单数据");
											PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
											logger.info("写入欧单编号");
											
											pmsAppTransInfo.setoAgentNo(oAgentNo);
											pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
											pmsAppTransInfo.setTradetype(TradeTypeEnum.shop.getTypeName());// 业务功能模块名称
											logger.info("网购");
											pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
											pmsAppTransInfo.setMercid(merchantinfo.getMercId());
											pmsAppTransInfo.setTradetypecode(TradeTypeEnum.shop.getTypeCode());// 业务功能模块编号
																	// ：17
											pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
											pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.BCQuickPay
													.getTypeName());
											pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.BCQuickPay.getTypeCode());
											BigDecimal factBigDecimal = new BigDecimal(factAmount);
											BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

											pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
											pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
											pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
											
											logger.info("插入订单信息");
											Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
											
											if (insertAppTrans == 1) {
												
												logger.info("查询订单信息");
												pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

												String quickRateType = resultMap.get("QUICKRATETYPE").toString();//快捷支付费率类型

												logger.info("获取o单第三方支付的费率");
												AppRateConfig appRate = new AppRateConfig();
												appRate.setRateType(quickRateType);
												appRate.setoAgentNo(oAgentNo);
												AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
													
												if (appRateConfig != null) {
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
															responseDTO.setRetCode(1);
															responseDTO
																	.setRetMessage("没有查到相关费率配置（附加费），请联系客服人员");
															logger.info("没有查到相关费率附加费（最低手续费）："
																	+ merchantinfo.getMobilephone());
															try {
																jsonString = createJsonString(responseDTO);
															} catch (Exception em) {
																em.printStackTrace();
															}
														}
													}else{
														
														BigDecimal payAmount = null;
														
														BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
														// 费率
														BigDecimal fee = new BigDecimal(0);
														BigDecimal b=new BigDecimal(0);
														String rateStr = "";
														// 计算结算金额
														if ("1".equals(isTop)) {

															rateStr = rate + "-" + topPoundage;
															logger.info("是封顶费率类型");
															fee = new BigDecimal(rate).multiply(dfactAmount);

															if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
																logger.info("手续费大于封顶金额，按封顶金额处理");
																payAmount = dfactAmount.subtract(new BigDecimal(
																		topPoundage).subtract(new BigDecimal(
																		minPoundage)));
																fee = new BigDecimal(topPoundage)
																		.add(new BigDecimal(minPoundage));
															} else {
																logger.info("按当前费率处理");
																rateStr = rate;
																fee.add(new BigDecimal(minPoundage));
																payAmount = dfactAmount.subtract(fee);
															}

														} else {
															logger.info("按当前费率处理"); 
															rateStr = rate;
															fee = new BigDecimal(rate).multiply(dfactAmount).add(
																	new BigDecimal(minPoundage));
															int fee1=fee.setScale(1, BigDecimal.ROUND_HALF_UP).intValue();	
															if(merchantinfo.getCounter()!=null)
															{
																int num=Integer.parseInt(merchantinfo.getCounter())*100;
																if(fee1<num)
																{
																	b=new BigDecimal(Integer.toString(num));
																	payAmount = dfactAmount.subtract(b);
																}else
																{
																	b=fee; 
																	payAmount = dfactAmount.subtract(fee);
																}
															}
														}
														
														logger.info("设置结算金额");
														pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
														pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
														pmsAppTransInfo.setPoundage(b.toString());
														pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
														
														Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);
														
														logger.info("验证支付方式是否开启");
														payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.shop,PaymentCodeEnum.BCQuickPay, oAgentNo,merchantinfo.getMercId());
														if (!payCheckResult.getErrCode().equals("0")) {
															logger.info("交易不支持");
															responseDTO.setRetCode(1);
															responseDTO.setRetMessage(payCheckResult.getMsg());
															try {
																jsonString = createJsonString(responseDTO);
															} catch (Exception em) {
																em.printStackTrace();
															}
															logger.info("不支持的支付方式，oAagentNo:"+ oAgentNo+ ",payType:"+ PaymentCodeEnum.BCQuickPay.getTypeCode());
														}else{
															
															
															ViewKyChannelInfo channelInfo = AppPospContext.context.get(BC_CHANNEL + BC_CHANNEL_TYPE);

															logger.info("设置通道信息");
															if(channelInfo!=null){
																
																pmsAppTransInfo.setBusinessNum(channelInfo.getBusinessnum());
																pmsAppTransInfo.setChannelNum(channelInfo.getChannelNum());
															}

															logger.info("查看当前交易是否已经生成了流水表");
															PospTransInfo pospTransInfo = null;
															logger.info("流水表是否需要更新的标记 0 insert，1：update");
																	logger.info("生成上送流水号");
															String transOrderId = generateTransOrderId(TradeTypeEnum.shop,PaymentCodeEnum.BCQuickPay);
															
															
															logger.info("不存在流水，生成一个流水");
															pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
															logger.info("设置上送流水号");
															pospTransInfo.setPospsn(transOrderId);
																
															pospTransInfoDAO.insert(pospTransInfo);
															
															
//															TODO
														    String title = "changjie";
														    
														    PAY_CHANNEL channel;
														    
														    channel = PAY_CHANNEL.valueOf("BC_EXPRESS");
														    
															//渠道  金额 交易号  标题
														    BCOrder bcOrder = new BCOrder(channel, 0, pospTransInfo.getPospsn(), title);
														    bcOrder.setBillTimeout(360);
														    
														    bcOrder.setTotalFee(Integer.parseInt(factAmount));
														    log.info("回调地址："+BC_PAY_CALLBAK);
											                bcOrder.setReturnUrl(BC_PAY_CALLBAK);
															bcOrder.setBcExpressCardNo(cardNo);
											                bcOrder = BCPay.startBCPay(bcOrder);
														    
											                //查询订单用
											                String objectId=bcOrder.getObjectId();
											                pospTransInfo.setSysseqno(objectId);
											                
											                response.sendRedirect(bcOrder.getUrl());
											                
											                pospTransInfoDAO.updateByOrderId(pospTransInfo);
											                
											                log.info("修改订单信息");
															log.info(pmsAppTransInfo);
															pmsAppTransInfoDao.update(pmsAppTransInfo);
														}
														
													}
													
												}else{
													// 若查到的是空值，直接返回错误
													responseDTO.setRetCode(1);
													responseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
													logger.info("没有查到相关费率配置："+ merchantinfo.getMobilephone());
													try {
														jsonString = createJsonString(responseDTO);
													} catch (Exception em) {
														em.printStackTrace();
													}
													
												}
												
											}else{
												responseDTO.setRetCode(1);
												responseDTO.setRetMessage("下单失败！！");
												jsonString = createJsonString(responseDTO);
											}
										}
										
									}else{
										// 若查到的是空值，直接返回错误
										responseDTO.setRetCode(1);
										responseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
										logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
										try {
											jsonString = createJsonString(responseDTO);
										} catch (Exception em) {
											em.printStackTrace();
										}
									}
									
									
								}else{
									// 交易不支持
									responseDTO.setRetCode(1);
									responseDTO.setRetMessage(payCheckResult3.getMsg());
									try {
										jsonString = createJsonString(responseDTO);
									} catch (Exception em) {
										em.printStackTrace();
									}
									logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
								}
								
							}else{
								// 交易不支持
								responseDTO.setRetCode(1);
								responseDTO.setRetMessage(payCheckResult1.getMsg());
								try {
									jsonString = createJsonString(responseDTO);
								} catch (Exception em) {
									em.printStackTrace();
								}
								logger.info("欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
							}
							
						}else{
							// 交易不支持
							responseDTO.setRetCode(1);
							responseDTO.setRetMessage(payCheckResult.getMsg());
							try {
								jsonString = createJsonString(responseDTO);
							} catch (Exception em) {
								em.printStackTrace();
							}
							logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
							
						}
						
					}else{
						// 请求参数为空
						logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
						responseDTO.setRetCode(1);
						responseDTO.setRetMessage("还没有进行实名认证，请先去进行实名认证，或者等待客服审核");
						jsonString = createJsonString(responseDTO);
					}
				}else{
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage("商户信息不存在");
					jsonString = createJsonString(responseDTO);
				}
				
			}
		}
		
				
		
		
		
		out.print(jsonString);
		out.flush();
		out.close();

	}

	/*

	/*
	 * (non-Javadoc)
	 * 
	 * @see xdt.service.impl.HfQuickPayService#updateMerchantBanlance(xdt.model.
	 * PmsAppTransInfo)
	 */
	@Override
	public synchronized int updateMerchantBanlance(
			PmsAppTransInfo pmsAppTransInfo) {
		int result = 0;
		try {
			result = merchantCollectMoneyService
					.updateMerchantBalance(pmsAppTransInfo);
		} catch (Exception e) {
			logger.info("修改余额的时候出错(" + pmsAppTransInfo.getOrderid() + ")："
					+ e.getMessage());
		}
		return result;
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
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		log.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xdt.service.impl.HfQuickPayService#getTransInfo(java.lang.String)
	 */
	@Override
	public PospTransInfo getTransInfo(String orderId) {
		PospTransInfo transinfo = pospTransInfoDAO.searchByOrderId(orderId);
		return transinfo;
	}

	/*

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * xdt.service.impl.HfQuickPayService#getoriginInfoByMerchantOrderId(java.
	 * lang.String)
	 */
	@Override
	public synchronized OriginalOrderInfo getoriginInfoByMerchantOrderId(
			String originalOrderId) {
		log.info("根据商户订单id  获取商户上送原始信息");
		return originalDao
				.getOriginalOrderInfoByMerchanOrderId(originalOrderId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xdt.service.HfQuickPayService#getChannelConfigKey(java.lang.String)
	 */
	@Override
	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId)
			throws Exception {
		log.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	@Override
	public ViewKyChannelInfo getChannelInfo() {
		ViewKyChannelInfo channelInfo = AppPospContext.context.get(BC_CHANNEL
				+ BC_CHANNEL_TYPE);
		return channelInfo;
	}

	@Override
	public OriginalOrderInfo selectByOriginal(OriginalOrderInfo queryWhere) {
		log.info("查询上送原始信息   下游订单id  商户号联合查询");
		return originalDao.selectByOriginal(queryWhere);
	}

	@Override
	public Map<String, String> ququeryOrderInfoByOrigin(
			PayQueryRequestEntity queryInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PayQueryResponseEntity queryLocalOrderStatus(
			String orderId) throws Exception {
		log.info("查询支付结果");
		PayQueryResponseEntity result=new PayQueryResponseEntity();

		PmsAppTransInfo traninfo=pmsAppTransInfoDao.searchOrderInfo(orderId);
		
		if(traninfo!=null){
			result.setOrderAmount(traninfo.getOrderamount());
			result.setResponseCode("0000");
			result.setMsg("success");
			// 0支付成功 1支付失败2
			if("0".equals(traninfo.getStatus())){
				//支付成功
				result.setTransStatus("1");
			}
			if("1".equals(traninfo.getStatus())){
				//支付成功
				result.setTransStatus("2");
			}
			if("2".equals(traninfo.getStatus())||"200".equals(traninfo.getStatus())){
				//支付成功
				result.setTransStatus("0");
			}
		}else{
			result.setResponseCode("0001");
			result.setMsg("fail");
		}
		
		return result;
	}

	@Override
	public void updateOrderStatusByOrder(PmsAppTransInfo pmsAppTransInfo)
			throws Exception {
		logger.info("订单任务处理 BeeCloud:"+pmsAppTransInfo);
		PospTransInfo pospTransInfo=pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid());
		logger.info("订单任务BC cloud:"+pospTransInfo);
		if(pospTransInfo!=null){
			
			 if(!StringUtils.isEmpty(pospTransInfo.getSysseqno())){
				 
				 BCOrder order = BCPay.startQueryBillById(pospTransInfo.getSysseqno());
				 
				 boolean result=order.isResult();
				 
				 if(result){
					 logger.info("支付成功");
					 updateOrderSuccess(pmsAppTransInfo,pospTransInfo);
				 }else{
					 logger.info("支付失败或还没有支付");
					 updateOrderWaitPay(pmsAppTransInfo,pospTransInfo);
				 }
			 }else{
				 updateOrderWaitPay(pmsAppTransInfo,pospTransInfo);
			 }
			 
			 
			 
			 
		}
		
	
	}
	/**
	 * 
	 * @Description 修改订单为等待支付  超过30分钟失败处理
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @param pospTransInfo
	 */
	private void updateOrderWaitPay(PmsAppTransInfo pmsAppTransInfo,
			PospTransInfo pospTransInfo) {
		logger.info("修改订单为失败");
		
		try {
			if(!UtilDate.isInDefiMinit(pmsAppTransInfo.getTradetime(), 30)){
				logger.info("订单在30分未完成支付  修改为失败");
				pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
				pmsAppTransInfo.setStatus("1");
			}else{
				pmsAppTransInfo.setStatus("2");
			}
			pmsAppTransInfoDao.update(pmsAppTransInfo);
		} catch (Exception e) {
			logger.error("修改订单为失败",e);
		}		
	}
	/**
	 * 
	 * @Description 修改订单成功状态 
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @param pospTransInfo
	 */
	private void updateOrderSuccess(PmsAppTransInfo pmsAppTransInfo,
			PospTransInfo pospTransInfo) {
		pmsAppTransInfo.setFinishtime(UtilDate.getOrderNum());
		pmsAppTransInfo.setStatus("0");
		pospTransInfo.setResponsecode("0000");
		try {
			pospTransInfoDAO.update(pospTransInfo);
			pmsAppTransInfoDao.update(pmsAppTransInfo);
		} catch (Exception e) {
			logger.error("修改订单为失败",e);
		}		
	}

	@Override
	public OriginalOrderInfo getOriginOrderInfoByPospsn(String orderId) {
		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchByPospsn(orderId);
		if(transInfo!=null){
			String oderId = transInfo.getOrderId();
			log.info("根据上送订单号  查询商户上送原始信息");
			original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		}
		return original;
	}

	@Override
	public void handleLocalOrderInfo(String orderId) throws Exception {
		logger.info("webhook 通知修改订单");
		if(!StringUtils.isEmpty(orderId)){
			 
			
			 PospTransInfo pospTransInfo=pospTransInfoDAO.selectBySysseqno(orderId);
			 
			 PmsAppTransInfo pmsAppTransInfo= pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
			
			 if(pospTransInfo!=null&&pmsAppTransInfo!=null){
				 
				 BCOrder order = BCPay.startQueryBillById(orderId);
				 
				 boolean result=order.isResult();
				 
				 if(result){
					 logger.info("支付成功");
					 updateOrderSuccess(pmsAppTransInfo,pospTransInfo);
				 }else{
					 logger.info("支付失败或还没有支付");
				 }
			 }
		 }
		
		
	}

}

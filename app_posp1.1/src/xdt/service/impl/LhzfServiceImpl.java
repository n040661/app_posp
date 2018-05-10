package xdt.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

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
import xdt.dto.hfb.HfbRequest;
import xdt.dto.hfb.HfbResponse;
import xdt.dto.lhzf.LhzfRequset;
import xdt.dto.lhzf.LhzfResponse;
import xdt.dto.lhzf.LhzfUtil;
import xdt.dto.lhzf.MerchantApiUtil;
import xdt.dto.tfb.CardPayApplyRequest;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.service.ILhzfService;
import xdt.service.IPublicTradeVerifyService;
import xdt.util.Constants;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
@Service
public class LhzfServiceImpl extends BaseServiceImpl implements ILhzfService {

	Logger log =Logger.getLogger(this.getClass());
	
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
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;//代付
	@Override
	public Map<String, String> quickAgentPayH5(LhzfRequset lhzfRequset,
			Map<String, String> result) {
		
		log.info("--------------蓝海网关进来了---------------");
		log.info("上传到server层参数:"+JSON.toJSON(lhzfRequset));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = lhzfRequset.getOrderNo(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = lhzfRequset.getMerNo();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);
		
		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			log.info("查询当前商户信息"+merchantList);
			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(lhzfRequset.getOrderNo());//---------------------------
				oriInfo.setPid(lhzfRequset.getMerNo());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoCardPay(lhzfRequset, out_trade_no, mercId);
					// 校验商户金额限制
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
					paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
					paramMap.put("oAgentNo", oAgentNo);
					// 商户 网购 业务信息
					Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

					String quickRateType = resultMap.get("QUICKRATETYPE");// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

					paramMap.put("mercid", mercId);
					paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
					// 微信支付
					paramMap.put("paymentcode", PaymentCodeEnum.moBaoQuickPay.getTypeCode());

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
								.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);

						if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
							if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
								log.error("此功能暂时关闭!");
								result.put("respCode", "05");
								result.put("respMsg", "此功能暂时关闭");
							} else {
								log.error(resultInfoForOAgentNo.getMsg());
								result.put("respCode", "05");
								result.put("respMsg", resultInfoForOAgentNo.getMsg());
							}
						} else {

							if ("1".equals(status)) {

								// 判断支付方式时候开通总开关
								ResultInfo payCheckResult = null;

								payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
										PaymentCodeEnum.moBaoQuickPay.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(lhzfRequset.getTransAmt());// 收款金额
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao.checkLimit(oAgentNo, payAmt,
											TradeTypeEnum.merchantCollect.getTypeCode());
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
													.payTypeVerifyMer(PaymentCodeEnum.moBaoQuickPay, mercId);
										
										if ("0".equals(resultinfo.getErrCode())) {
											if ("0".equals(payStatus)) {
												// 有效
												// MIN_AMOUNT,MAX_AMOUNT ,RATE
												// ,STATUS
												String rateStr = appRateConfig.getRate(); // 商户费率
																							// RATE

												BigDecimal min_amount = new BigDecimal(
														appRateTypeAndAmount.getMinAmount());// 最低收款金额
												// MIN_AMOUNT
												BigDecimal max_amount = new BigDecimal(
														appRateTypeAndAmount.getMaxAmount());// 最高收款金额
												// MAX_AMOUNT

												if (min_amount.compareTo(payAmt) != 1) {// 判断收款金额是否大于最低收款金额
													// 大于等于执行
													// 小于不执行
													if (payAmt.compareTo(max_amount) != 1) {
														// 组装报文
														String totalAmount = lhzfRequset.getTransAmt(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															Double d1=Double.parseDouble(lhzfRequset.getUserRate());//客户上传费率
															Double d2 =Double.parseDouble(rateStr)*100;//系统费率
															if(d1<d2){
																result.put("respCode", "01");
																result.put("respMsg", "费率低于签约费率");
																return result;
															}
															Double d3 =Double.parseDouble(lhzfRequset.getUserFee());//商户上传代付费用
															Double d4 =Double.parseDouble(merchantinfo.getPoundage())*100;//系统代付费用
															if(d3<d4){
																result.put("respCode", "01");
																result.put("respMsg", "代付手续费低于签约手续费");
																return result;
															}
															PmsBusinessPos pmsBusinessPos =selectKey(lhzfRequset.getMerNo());
															switch (pmsBusinessPos.getChannelnum()) {
															case "YK":
																result =otherInvokeCardPay(lhzfRequset, appTransInfo,rateStr,merchantinfo);
																result.put("respCode", "00");
																result.put("respMsg", "请求成功");
																break;
															case "YPL":
																//quickPay(lhzfRequset, appTransInfo, rateStr, merchantinfo);
																break;
															default:
																result.put("respCode", "01");
																result.put("respMsg", "请求失败，未找到路由！");
																break;
															}
															
														} else {
															// 交易金额小于收款最低金额
															result.put("respCode", "11");
															result.put("respMsg", "生成订单流水失败");
															log.info("生成订单流水失败");
														}

													} else {

														// 交易金额小于收款最低金额
														result.put("respCode", "10");
														result.put("respMsg", "交易金额大于收款最高金额");
														log.info("交易金额大于收款最高金额");
													}

												} else {
													// 交易金额小于收款最低金额
													result.put("respCode", "09");
													result.put("respMsg", "交易金额小于收款最低金额");
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
	
	
	public Map<String, String> otherInvokeCardPay(LhzfRequset lhzfRequset,PmsAppTransInfo appTransInfo,String rateStr,PmsMerchantInfo merchantinfo) throws Exception{
		
		
		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = lhzfRequset.getOrderNo();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(lhzfRequset.getOrderNo())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + lhzfRequset.getOrderNo()
					+ ",生成上送通道的流水号：" + transOrderId);
			pospTransInfo.setTransOrderId(transOrderId);
			pospTransInfo.setResponsecode("20");
			pospTransInfo.setPospsn("");
			insertOrUpdateFlag = 1;
			log.info("***************进入payHandle5-16***************");
		} else {
			// 不存在流水，生成一个流水
			pospTransInfo = InsertJournal(appTransInfo);
			// 设置上送流水号
			//通道订单号
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
		appTransInfo=pmsAppTransInfoDao.searchOrderInfo(appTransInfo.getOrderid());
		log.info("请求交易生成二维码map");
		//1微信
		appTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
		appTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
			
        pmsAppTransInfoDao.update(appTransInfo);
        PmsBusinessPos pmsBusinessPos =selectKey(lhzfRequset.getMerNo());
		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("merKey", LhzfUtil.merKey);

		paramMap.put("transId", "QUICK_AGENT_PAY_H5");

		paramMap.put("serialNo", lhzfRequset.getOrderNo());
		
		paramMap.put("transAmt", Double.parseDouble(lhzfRequset.getTransAmt())/100+"");
		//
        paramMap.put("currency","156");
	
		paramMap.put("orderNo", lhzfRequset.getOrderNo());

		Date orderDate = new Date();// 订单日期
		String orderDateStr = new SimpleDateFormat("yyyyMMdd").format(orderDate);// 订单日期
		paramMap.put("transDate", orderDateStr);

		Date orderTime = new Date();// 订单时间
		String orderTimeStr = new SimpleDateFormat("yyyyMMddHHmmss").format(orderTime);// 订单时间
		paramMap.put("transTime", orderTimeStr);

		paramMap.put("orderDesc", lhzfRequset.getOrderDesc());
		
		paramMap.put("returnUrl", LhzfUtil.returnUrl);
		
		paramMap.put("notifyUrl", LhzfUtil.notifyUrl);

		paramMap.put("remark", lhzfRequset.getRemark());

		paramMap.put("cardNo", lhzfRequset.getCardNo());
		paramMap.put("cardType", lhzfRequset.getCardType());
		paramMap.put("idName", lhzfRequset.getIdName());
		paramMap.put("idType", lhzfRequset.getIdType());
		paramMap.put("idNo", lhzfRequset.getIdNo());
		paramMap.put("mobileNo", lhzfRequset.getMobileNo());
		//payeeCardType-----payeeIdType
		paramMap.put("payeeIdType", lhzfRequset.getPayeeCardType());
		paramMap.put("payeeCardNo", lhzfRequset.getPayeeCardNo());
		//我们平台收取金额
		/*paramMap.put("userRate",Double.parseDouble(rateStr)*100+"");//
		paramMap.put("userFee", merchantinfo.getPoundage());//
*/		paramMap.put("userRate",Double.parseDouble(lhzfRequset.getUserRate())+"");//
		paramMap.put("userFee", Double.parseDouble(lhzfRequset.getUserFee())/100+"");//
		//-------------------
		paramMap.put("payeeCurrency", "156");
		paramMap.put("bankCode", lhzfRequset.getBankCode());
		paramMap.put("payeeBankCode", lhzfRequset.getPayeeBankCode());
		paramMap.put("payeeIdNo", lhzfRequset.getIdNo());
		
		
		  ////////////扩展字段,选填,原值返回///////////
        paramMap.put("transInfo",lhzfRequset.getTransInfo()==null?"transInfo":lhzfRequset.getTransInfo());
        paramMap.put("extraInfo",lhzfRequset.getExtraInfo()==null?"extraInfo":lhzfRequset.getExtraInfo());

        /*paramMap.put("merIp", "111.230.194.185");*/
        paramMap.put("payeeIdName", lhzfRequset.getIdName());
        paramMap.put("payeeMobileNo", lhzfRequset.getPayeeMobileNo()==null ?lhzfRequset.getMobileNo():lhzfRequset.getPayeeMobileNo());
        
    	log.info("瀛酷---签名前数据："+JSON.toJSONString(paramMap));
		///// 签名及生成请求API的方法///
        String paySecret=pmsBusinessPos.getKek();//LhzfUtil.paySecret;//
		String sign = MerchantApiUtil.getSign(paramMap, paySecret);
		paramMap.put("sign", sign);
		log.info("瀛酷---签名后数据："+JSON.toJSONString(paramMap));
		String requestUrl = LhzfUtil.commonRequestUrl1;
		paramMap.put("requestUrl", requestUrl);
		paramMap.put("respCode", "00");
		paramMap.put("respMsg", "请求成功");
		
		return paramMap;
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
	private int saveOriginAlInfoCardPay(LhzfRequset lhzfRequset, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(orderid);
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		//想要传服务器要改实体
		info.setBgUrl(lhzfRequset.getUrl());
		info.setPageUrl(lhzfRequset.getReUrl());
		Double amt = Double.parseDouble(lhzfRequset.getTransAmt());// 单位分
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
	public PmsAppTransInfo insertOrder(String orderid, String payamount, String mercId, String rateStr, String oAgentNo)
			throws Exception {

		System.out.println("12345613454354=" + orderid);
		// 查询商户费率
		BigDecimal rate = new BigDecimal(rateStr);
		BigDecimal amount = new BigDecimal(payamount);

		// 成功后订到入库app后台
		PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();

		pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());
		pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
		pmsAppTransInfo.setOrderid(orderid);// 上送的订单号

		pmsAppTransInfo.setReasonofpayment(TradeTypeEnum.merchantCollect.getTypeName());
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
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);

		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			// 正式商户
			merchantinfo = merchantList.get(0);
			if (merchantinfo.getCounter() != null) {
				Double ss =Double.parseDouble(merchantinfo.getCounter());
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
				log.info("订单入库失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。订单详细信息：" + sendString);
				throw new RuntimeException("手动抛出");
			}
		} catch (Exception e) {
			log.info("订单入库失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。订单详细信息：" + sendString, e);
			throw new RuntimeException("手动抛出");
		}
		return pmsAppTransInfo;

	}
	/**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) 
	{
		PmsBusinessPos businessPos = new PmsBusinessPos();
		try {
		PospRouteInfo route = route(merid);
		System.out.println(route);
		PmsBusinessInfo busInfo= new PmsBusinessInfo();
		System.out.println(route.getMerchantId().toString());
		busInfo = pmsBusinessInfoDao.searchById(route.getMerchantId().toString());
		businessPos.setBusinessnum(busInfo.getBusinessNum());
		businessPos =businessPosDao.searchById(businessPos.getBusinessnum()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	   return businessPos;
	}
	/**
	 * 录入交易流水 并记算费率
	 * 
	 * @throws Exception
	 */
	public PospTransInfo InsertJournal(PmsAppTransInfo pmsAppTransInfo) throws Exception {
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
		pospTransInfo.setRemark(pmsAppTransInfo.getTradetype() + "  金额：" + pmsAppTransInfo.getFactamount());
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
		pospTransInfo
				.setSearchTransCode("000000" + pmsAppTransInfo.getTradetypecode() + pmsAppTransInfo.getPaymentcode());
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
		pospTransInfo.setMsgtype(pmsAppTransInfo.getTradetypecode() + pmsAppTransInfo.getPaymentcode());
		// 设置发生额
		pospTransInfo.setTransamt(new BigDecimal(pmsAppTransInfo.getFactamount()));
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
	public void update(LhzfResponse hfbResponse) throws Exception {
		log.info("返回的参数："+hfbResponse);
		log.info("**************进入修改方法*************************");
		// 流水表transOrderId
		String transOrderId = "";
		if(hfbResponse.getOrderNo()!=""&& hfbResponse.getOrderNo()!=null){
			transOrderId=hfbResponse.getOrderNo();
		}
		log.info("异步通知回来的订单号:" + transOrderId);
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息：" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息：" + pmsAppTransInfo);
		// 查询结果成功
		if("SUCCESS".equals(hfbResponse.getStatus())) {
			// 支付成功
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
				if(hfbResponse.getOrderNo()!=null&& hfbResponse.getOrderNo()!=""){
					pospTransInfo.setPospsn(hfbResponse.getOrderNo());
				}
				
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if("FAILED".equals(hfbResponse.getStatus())){
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("1");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				if(hfbResponse.getOrderNo()!=null&& hfbResponse.getOrderNo()!=""){
					pospTransInfo.setPospsn(hfbResponse.getOrderNo());
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
			
		}else{
			// 支付中
			pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("1");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("200");
				if(hfbResponse.getOrderNo()!=null&& hfbResponse.getOrderNo()!=""){
					pospTransInfo.setPospsn(hfbResponse.getOrderNo());
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}	
		}

	}

	
	
	/*public Map<String, String> quickPay(LhzfRequset lhzfRequset,PmsAppTransInfo appTransInfo,String rateStr,PmsMerchantInfo merchantinfo) throws Exception{

		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = lhzfRequset.getOrderNo();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(lhzfRequset.getOrderNo())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + lhzfRequset.getOrderNo()
					+ ",生成上送通道的流水号：" + transOrderId);
			pospTransInfo.setTransOrderId(transOrderId);
			pospTransInfo.setResponsecode("20");
			pospTransInfo.setPospsn("");
			insertOrUpdateFlag = 1;
			log.info("***************进入payHandle5-16***************");
		} else {
			// 不存在流水，生成一个流水
			pospTransInfo = InsertJournal(appTransInfo);
			// 设置上送流水号
			//通道订单号
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
		appTransInfo=pmsAppTransInfoDao.searchOrderInfo(appTransInfo.getOrderid());
		log.info("请求交易生成二维码map");
		//1微信
		appTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
		appTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
			
        pmsAppTransInfoDao.update(appTransInfo);
        PmsBusinessPos pmsBusinessPos =selectKey(lhzfRequset.getMerNo());
        
        MerchantApiPayReq req = new MerchantApiPayReq();
		req.setTransactionId("ZF"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		req.setOrderAmount("110");
		req.setCur("CNY");
		req.setProductName("小米MIX");
		req.setOrderDesc("小米MIX");
		req.setBgUrl("https://www.baidu.com/");
		req.setBuyerIp("127.0.0.1");
		req.setPayType(ValueConstant.PAY_TYPE_1010);
		req.setPayerBankCode("GDB");
		req.setPayerAcc("5203821456451234");
		req.setPayerName("张三");
		req.setPayerPhoneNo("18545629502");
		req.setCardType("CC");
		req.setExpiryDate("2601");
		req.setCvv2("312");
		req.setPayerIdNum("440507199811041234");
		req.setPrivateFlag("B");
		req.setPayeeBankCode("GDB");
		req.setPayeeAcc("6216123456893");
		req.setPayeePhoneNo("18545629502");
		req.setFeeRate("0.41");
		req.setExtraFee("1.01");
		//req.setMaxFee("29");
		req.setBgUrl("http://127.0.0.1:8532/testCtrl/noticePro");
		
		String merchantPrivateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOTYIqkjyCrIHdIeOAvTwaggG6mAhXU6byrW5SIqAXE3znaiBeOeDVNWJzs/pQtXuTn6fB1LoU3Q93hPcLkh7kdoH3+BJDzoPWZ5tPyzgua2nad9xMNNphfRYDVTiEoAxOnFc3aNI22gse+wPS0Ll29/LGp+z3e/p+e1cRP/ibFJAgMBAAECgYEA3pVbISisiPAcEUNTQC23LtAMF9Hp/RvZBNIADDrPLFAbgUgWck5Ip8YkYnyFC4NHphz8m4H0Yrvd+CdMfMWD/BkPRf3eafhnJlHGKyGqsAXLmGh/mvJbleE3NH9LS1N/0+pPam58mAjvkujxoPQ0v5BxHyS7r14lBMkvxiXN9AECQQD8B2zTpvsXDWJFwjKYmKRkWCs3JOaOJmWX6MTY3qPSE6mFW/93blDAs1kEioB01ZsbKiE3fIubZVcFEzI90nCXAkEA6HMxd+GYWA7+UdeOklhz/XhBdtlsOeHZDG8glOFhsHJguURcnov2TG4G5L1t+qdnpZzTeNKVrSyT2ECE4gVJHwJAVwiZZF39x/AvR7fQkTHlU2G/SsPLert3ygXwNJRuLlXr7MngZvYJnQJSc2cBBVfewHrEDc1MyNUuP+ppJ0BM8QJBALdi6gwiNwaCDbKT1S8wCZJXZY5WSkQAIjTlF1dd2KxUEGsZu9h5o3747wdXS4UMvYCzEUOpH9zX5mwdurh2YxECQQDuPsVpoJlevwbIuRymGzvYvVZvDP2N+O4rN0lrJnlhTXkYdsRLSw92QcBX0jRqjwl/LwEMPt8EaK25xJ6rEc07";
		String aesKey = "xbDy7BIi4rgG+Bp0m4JpQA==";
		String submitUrl = "http://localhost:9060/rdPay/payProcess";
		String channelPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfRRqiTyiDRvgPwAnHm+odB6kEY1O51Zh5rlr3iSYEgDKfO00yD6ZCAh6MlKfYT0DD+WKN91lt6t9g/u0Cw2WJwGeUiOEWUDso/MiOGmdGYrfsarEzGCTSRmu1tIdwFKNi9HThcMTs7aU99lBtoGIYu2mxsXoWnLbdExZ9TaOBgwIDAQAB";
		String merId = "0000000000000005";
		
		//1.签名
		String jsonStr = JSON.toJSONString(req);
		String signData = MerchantUtil.sign(jsonStr, merchantPrivateKey, MerchantUtil.SIGNTYPE_RSA, "UTF-8");

		//2.加密
		String businessContext = MerchantUtil.encryptDataByAES(signData, aesKey, "UTF-8");
		log.info("密文："+businessContext);
		//3.组装报文
		MerchantBaseMsg baseReq = new MerchantBaseMsg();
		baseReq.setVersion("1.0");
		baseReq.setMerId(merId);
		baseReq.setTransCode(ValueConstant.TRANS_CODE_T01017);//预下单
		baseReq.setOrderTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		baseReq.setSignType("RSA");
		baseReq.setCharse("UTF-8");
		baseReq.setBusinessContext(businessContext);
		//4.发送报文
		@SuppressWarnings("unchecked")
		Map<String,String> requestParams = JSON.parseObject(JSON.toJSONString(baseReq), Map.class);
		String rspStr = HttpClientUtil.doPost(requestParams, submitUrl);
		MerchantBaseMsg baseMsg = JSON.parseObject(rspStr, MerchantBaseMsg.class);
		
		//5.解密
		businessContext = baseMsg.getBusinessContext();
		log.info("应答密文："+businessContext);
		String rspMsg = MerchantUtil.decryptDataByAES(businessContext, aesKey, "UTF-8");
		
		//6.验签
		boolean isTrue = MerchantUtil.verify(rspMsg, channelPublicKey, MerchantUtil.SIGNTYPE_RSA, "UTF-8");
		log.info("回应报文："+rspMsg);
		if(isTrue){
			log.info("验签成功");
			MerchantApiPayConfirmRsp rsp = JSON.parseObject(rspMsg, MerchantApiPayConfirmRsp.class);
			String retCode = rsp.getRetCode();
			if(ReturnCdConstant.RETURN_CD_0000.equals(retCode)){
				//processApiPayRsp(req);
			}
		}else{
			log.info("验签失败");
		}
        
		return null;
	}*/
//	public Map<String, String> quickPay(LhzfRequset lhzfRequset,PmsAppTransInfo appTransInfo,String rateStr,PmsMerchantInfo merchantinfo) throws Exception{
//
//		// 查看当前交易是否已经生成了流水表
//		PospTransInfo pospTransInfo = null;
//		// 流水表是否需要更新的标记 0 insert，1：update
//		int insertOrUpdateFlag = 0;
//		log.info("***************进入payHandle5-14-3***************");
//		// 生成上送流水号
//		String transOrderId = lhzfRequset.getOrderNo();
//		log.info("***************进入payHandle5-15***************");
//		if ((pospTransInfo = pospTransInfoDAO
//				.searchByOrderId(lhzfRequset.getOrderNo())) != null) {
//			// 已经存在，修改流水号，设置pospsn为空
//			log.info("订单号：" + lhzfRequset.getOrderNo()
//					+ ",生成上送通道的流水号：" + transOrderId);
//			pospTransInfo.setTransOrderId(transOrderId);
//			pospTransInfo.setResponsecode("20");
//			pospTransInfo.setPospsn("");
//			insertOrUpdateFlag = 1;
//			log.info("***************进入payHandle5-16***************");
//		} else {
//			// 不存在流水，生成一个流水
//			pospTransInfo = InsertJournal(appTransInfo);
//			// 设置上送流水号
//			//通道订单号
//			pospTransInfo.setTransOrderId(transOrderId);
//			insertOrUpdateFlag = 0;
//		}
//		log.info("***************进入payHandle5-17***************");
//		// 插入流水表信息
//		if (insertOrUpdateFlag == 0) {
//			// 插入一条流水
//			pospTransInfoDAO.insert(pospTransInfo);
//		} else if (insertOrUpdateFlag == 1) {
//			// 更新一条流水
//			pospTransInfoDAO.updateByOrderId(pospTransInfo);
//		}
//		appTransInfo=pmsAppTransInfoDao.searchOrderInfo(appTransInfo.getOrderid());
//		log.info("请求交易生成二维码map");
//		//1微信
//		appTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
//		appTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
//			
//        pmsAppTransInfoDao.update(appTransInfo);
//        PmsBusinessPos pmsBusinessPos =selectKey(lhzfRequset.getMerNo());
//        
//        MerchantApiPayReq req = new MerchantApiPayReq();
//		req.setTransactionId("ZF"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//		req.setOrderAmount("110");
//		req.setCur("CNY");
//		req.setProductName("小米MIX");
//		req.setOrderDesc("小米MIX");
//		req.setBgUrl("https://www.baidu.com/");
//		req.setBuyerIp("127.0.0.1");
//		req.setPayType(ValueConstant.PAY_TYPE_1010);
//		req.setPayerBankCode("GDB");
//		req.setPayerAcc("5203821456451234");
//		req.setPayerName("张三");
//		req.setPayerPhoneNo("18545629502");
//		req.setCardType("CC");
//		req.setExpiryDate("2601");
//		req.setCvv2("312");
//		req.setPayerIdNum("440507199811041234");
//		req.setPrivateFlag("B");
//		req.setPayeeBankCode("GDB");
//		req.setPayeeAcc("6216123456893");
//		req.setPayeePhoneNo("18545629502");
//		req.setFeeRate("0.41");
//		req.setExtraFee("1.01");
//		//req.setMaxFee("29");
//		req.setBgUrl("http://127.0.0.1:8532/testCtrl/noticePro");
//		
//		String merchantPrivateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOTYIqkjyCrIHdIeOAvTwaggG6mAhXU6byrW5SIqAXE3znaiBeOeDVNWJzs/pQtXuTn6fB1LoU3Q93hPcLkh7kdoH3+BJDzoPWZ5tPyzgua2nad9xMNNphfRYDVTiEoAxOnFc3aNI22gse+wPS0Ll29/LGp+z3e/p+e1cRP/ibFJAgMBAAECgYEA3pVbISisiPAcEUNTQC23LtAMF9Hp/RvZBNIADDrPLFAbgUgWck5Ip8YkYnyFC4NHphz8m4H0Yrvd+CdMfMWD/BkPRf3eafhnJlHGKyGqsAXLmGh/mvJbleE3NH9LS1N/0+pPam58mAjvkujxoPQ0v5BxHyS7r14lBMkvxiXN9AECQQD8B2zTpvsXDWJFwjKYmKRkWCs3JOaOJmWX6MTY3qPSE6mFW/93blDAs1kEioB01ZsbKiE3fIubZVcFEzI90nCXAkEA6HMxd+GYWA7+UdeOklhz/XhBdtlsOeHZDG8glOFhsHJguURcnov2TG4G5L1t+qdnpZzTeNKVrSyT2ECE4gVJHwJAVwiZZF39x/AvR7fQkTHlU2G/SsPLert3ygXwNJRuLlXr7MngZvYJnQJSc2cBBVfewHrEDc1MyNUuP+ppJ0BM8QJBALdi6gwiNwaCDbKT1S8wCZJXZY5WSkQAIjTlF1dd2KxUEGsZu9h5o3747wdXS4UMvYCzEUOpH9zX5mwdurh2YxECQQDuPsVpoJlevwbIuRymGzvYvVZvDP2N+O4rN0lrJnlhTXkYdsRLSw92QcBX0jRqjwl/LwEMPt8EaK25xJ6rEc07";
//		String aesKey = "xbDy7BIi4rgG+Bp0m4JpQA==";
//		String submitUrl = "http://localhost:9060/rdPay/payProcess";
//		String channelPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfRRqiTyiDRvgPwAnHm+odB6kEY1O51Zh5rlr3iSYEgDKfO00yD6ZCAh6MlKfYT0DD+WKN91lt6t9g/u0Cw2WJwGeUiOEWUDso/MiOGmdGYrfsarEzGCTSRmu1tIdwFKNi9HThcMTs7aU99lBtoGIYu2mxsXoWnLbdExZ9TaOBgwIDAQAB";
//		String merId = "0000000000000005";
//		
//		//1.签名
//		String jsonStr = JSON.toJSONString(req);
//		String signData = MerchantUtil.sign(jsonStr, merchantPrivateKey, MerchantUtil.SIGNTYPE_RSA, "UTF-8");
//
//		//2.加密
//		String businessContext = MerchantUtil.encryptDataByAES(signData, aesKey, "UTF-8");
//		log.info("密文："+businessContext);
//		//3.组装报文
//		MerchantBaseMsg baseReq = new MerchantBaseMsg();
//		baseReq.setVersion("1.0");
//		baseReq.setMerId(merId);
//		baseReq.setTransCode(ValueConstant.TRANS_CODE_T01017);//预下单
//		baseReq.setOrderTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//		baseReq.setSignType("RSA");
//		baseReq.setCharse("UTF-8");
//		baseReq.setBusinessContext(businessContext);
//		//4.发送报文
//		@SuppressWarnings("unchecked")
//		Map<String,String> requestParams = JSON.parseObject(JSON.toJSONString(baseReq), Map.class);
//		String rspStr = HttpClientUtil.doPost(requestParams, submitUrl);
//		MerchantBaseMsg baseMsg = JSON.parseObject(rspStr, MerchantBaseMsg.class);
//		
//		//5.解密
//		businessContext = baseMsg.getBusinessContext();
//		log.info("应答密文："+businessContext);
//		String rspMsg = MerchantUtil.decryptDataByAES(businessContext, aesKey, "UTF-8");
//		
//		//6.验签
//		boolean isTrue = MerchantUtil.verify(rspMsg, channelPublicKey, MerchantUtil.SIGNTYPE_RSA, "UTF-8");
//		log.info("回应报文："+rspMsg);
//		if(isTrue){
//			log.info("验签成功");
//			MerchantApiPayConfirmRsp rsp = JSON.parseObject(rspMsg, MerchantApiPayConfirmRsp.class);
//			String retCode = rsp.getRetCode();
//			if(ReturnCdConstant.RETURN_CD_0000.equals(retCode)){
//				//processApiPayRsp(req);
//			}
//		}else{
//			log.info("验签失败");
//		}
//        
//		return null;
//	}
}

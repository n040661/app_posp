package xdt.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kspay.AESUtil;
import com.kspay.DateUtil;
import com.kspay.MD5Util;

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
import xdt.dto.hj.HJResponse;
import xdt.dto.hm.AesEncryption;
import xdt.dto.hm.HMUtil;
import xdt.dto.hm.HttpsUtil;
import xdt.dto.hm.SHA256Util;
import xdt.dto.hm.TimeUtil;
import xdt.dto.mb.DemoBase;
import xdt.dto.mb.MBReqest;
import xdt.dto.mb.MBUtil;
import xdt.dto.quick.QuickPayRequest;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.service.HfQuickPayService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IQuickPayAllService;
import xdt.util.Constants;
import xdt.util.EncodeUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月28日 上午10:21:13 
* 类说明 
*/
@Service
public class QuickPayAllServiceImpl extends BaseServiceImpl implements IQuickPayAllService {

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
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;//代付
	@Resource
	private HfQuickPayService payService;
	
	@Override
	public Map<String, String> quickPay(QuickPayRequest quickPayRequest, Map<String, String> result) {

		log.info("快捷支付进来了！");
		log.info("快捷---service参数mbReqest："+JSON.toJSON(quickPayRequest));
		
		String out_trade_no = "";// 订单号
		out_trade_no = quickPayRequest.getOrderId(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = quickPayRequest.getMerchantId();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);
		
		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			log.info("查询当前商户信息******快捷"+merchantList);
			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(quickPayRequest.getOrderId());//---------------------------
				oriInfo.setPid(quickPayRequest.getMerchantId());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复******快捷");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoCardPay(quickPayRequest, out_trade_no, mercId);
					// 校验商户金额限制
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
					paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
					paramMap.put("oAgentNo", oAgentNo);
					// 商户 网购 业务信息
					Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

					String quickRateType = resultMap.get("QUICKRATETYPE");// 支付费率类型

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
								log.info("此功能暂时关闭******快捷!");
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
										PaymentCodeEnum.weixinPay.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭******快捷");
								} else {

									BigDecimal payAmt = new BigDecimal(quickPayRequest.getAmount());// 收款金额
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao.checkLimit(oAgentNo, payAmt,
											TradeTypeEnum.merchantCollect.getTypeCode());
									// 返回不为0，一律按照交易失败处理
									if (!resultInfo.getErrCode().equals("0")) {
										result.put("respCode", "08");
										result.put("respMsg", "交易金额不在申请的范围之内");
										log.info("交易金额不在申请的范围之内******快捷");

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
														String totalAmount = quickPayRequest.getAmount(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															log.info("来了！！！");
															PmsBusinessPos pmsBusinessPos =selectKey(quickPayRequest.getMerchantId());
															//----------------------------------------------------------------
															switch (pmsBusinessPos.getChannelnum()) {
															case "GZHM"://恒明快捷
																quick(quickPayRequest, result, appTransInfo);
																break;
															default:
																result.put("respCode", "01");
																result.put("respMsg", "未绑定路由，请联系运营人员");
																break;
															}
															/*if("cj001".equals(mbReqest.getType())) {
																result=otherInvokeCardPay(mbReqest, result, appTransInfo);
															}else if("cj005".equals(mbReqest.getType())) {
																result=quickPay(mbReqest, result, appTransInfo);
															}*/
															
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
	/**
	 * 
	 * @Description 插入原始订单表信息
	 * @author Administrator
	 * @param reqeustInfo
	 * @param orderid
	 * @param mercId
	 * @throws Exception
	 */
	private int saveOriginAlInfoCardPay(QuickPayRequest quickPayRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(quickPayRequest.getOrderId());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		//想要传服务器要改实体
		info.setBgUrl(quickPayRequest.getUrl());
		Double amt = Double.parseDouble(quickPayRequest.getAmount());// 单位分
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
	 * 快捷支付
	 * @param quickPayRequest
	 * @param result
	 * @param appTransInfo
	 * @return
	 */
	public Map<String, String> quick(QuickPayRequest quickPayRequest, Map<String, String> result,PmsAppTransInfo appTransInfo){
		
		try {
			// 查看当前交易是否已经生成了流水表
			PospTransInfo pospTransInfo = null;
			// 流水表是否需要更新的标记 0 insert，1：update
			int insertOrUpdateFlag = 0;
			log.info("***************进入payHandle5-14-3***************");
			// 生成上送流水号
			String transOrderId = quickPayRequest.getOrderId();
			log.info("***************进入payHandle5-15***************");
			if ((pospTransInfo = pospTransInfoDAO
					.searchByOrderId(quickPayRequest.getOrderId())) != null) {
				// 已经存在，修改流水号，设置pospsn为空
				log.info("订单号：" + quickPayRequest.getOrderId()
						+ ",生成上送通道的流水号：" + transOrderId);
				pospTransInfo.setTransOrderId(transOrderId);
				pospTransInfo.setResponsecode("20");
				pospTransInfo.setPospsn("");
				insertOrUpdateFlag = 1;
				log.info("***************进入payHandle5-16***************");
			} else {
				// 不存在流水，生成一个流水
				log.info("111111111111111111111111");
				pospTransInfo =InsertJournal(appTransInfo);
				log.info("2222222222222222222222222");
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
			// 组装上送参数
			//1微信
				appTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
				appTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
	         pmsAppTransInfoDao.update(appTransInfo);
	         //获取上游商户号和密钥
	         PmsBusinessPos pmsBusinessPos =selectKey(quickPayRequest.getMerchantId());
			//商户号码
			//String merId =pmsBusinessPos.getBusinessnum();//818310048160000
			//商户号私钥
			//String merKey=pmsBusinessPos.getKek();//1FDD2547FA4FB61F
			String month=quickPayRequest.getMonth()==null?"":quickPayRequest.getMonth();
			String year=quickPayRequest.getYear()==null?"":quickPayRequest.getYear();
			JSONObject requestObj = new JSONObject();
			requestObj.put("ordernumber",quickPayRequest.getOrderId());
			requestObj.put("merchantid", "M"+pmsBusinessPos.getBusinessnum());
			requestObj.put("username", quickPayRequest.getAcctName());
			requestObj.put("userpid", quickPayRequest.getLiceneceNo());
			requestObj.put("usercardno", quickPayRequest.getAcctNo());
			requestObj.put("usertel", quickPayRequest.getPhone());
			requestObj.put("amount", quickPayRequest.getAmount());// 单位分 100=1元
			requestObj.put("ordertype", "10");//10:D0,11:T1
			requestObj.put("cvn2", quickPayRequest.getCvv2()==null?"":quickPayRequest.getCvv2());
			requestObj.put("expdate", year+month);
			requestObj.put("usertel", quickPayRequest.getPhone());
			requestObj.put("backurl",HMUtil.quickUrl);
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
				if(!"".equals(quickPayRequest.getCvv2())&&!"".equals(quickPayRequest.getYear())&&!"".equals(quickPayRequest.getMonth())){
					openApiUrl=HMUtil.quickPayXinUrl;
				}else{
					/*result.put("respCode", "01");
					result.put("respMsg", "cvv2,year,month填写有误");
					return result;*/
					openApiUrl=HMUtil.quickPayJieUrl;
				}
				String results = HttpsUtil.doSslPost(openApiUrl, postdata,
						"utf-8");
				log.info("恒明返回参数：" + results);
				
				JSONObject responseObj = JSONObject.parseObject(results);
				log.info("message:"+responseObj.get("message"));
				result.put("orderId",quickPayRequest.getOrderId());
				result.put("amount", quickPayRequest.getAmount());
				result.put("merchantId", quickPayRequest.getMerchantId());
				if("0".equals(responseObj.get("ret").toString())){
					String dedata = AesEncryption.Desencrypt(responseObj
							.get("data").toString(), HMUtil.aeskey, HMUtil.aeskey);
					log.info("恒明解析参数：" + dedata);
				JSONObject jsonObject2 = JSONObject.parseObject(dedata);
				//PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
				if("0".equals(jsonObject2.get("orderstate"))){
					result.put("state","00");
					result.put("message", jsonObject2.getString("payinfo"));
					result.put("payorderno", jsonObject2.getString("payorderno"));
/*					OriginalOrderInfo originalInfo = null;
					if (jsonObject2.get("payorderno") != null && jsonObject2.get("payorderno")!= "") {
						originalInfo = this.payService.getOriginOrderInfo(jsonObject2.get("payorderno").toString());
					}
					int ii =UpdatePmsMerchantInfo(originalInfo);
					if(ii==1){
						log.info("实时填金成功！！");
					}
*/				}else{
					result.put("state","01");
					result.put("message", jsonObject2.getString("payinfo"));
					/*pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
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
					}*/
				}
				}else{
					result.put("state","01");
					result.put("message", responseObj.getString("message"));
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return result;
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
	public Map<String, String> verificationCode(QuickPayRequest quickPayRequest, Map<String, String> result) {
		try {
		 PmsBusinessPos pmsBusinessPos =selectKey(quickPayRequest.getMerchantId());
		JSONObject requestObj = new JSONObject();
		requestObj.put("payorderno",quickPayRequest.getPayorderno());
		requestObj.put("merchantid", "M"+pmsBusinessPos.getBusinessnum());
		requestObj.put("smscode", quickPayRequest.getSmsCode());
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
				openApiUrl=HMUtil.url+"/pay/unionpay/quick/sms";
			String results = HttpsUtil.doSslPost(openApiUrl, postdata,
					"utf-8");
			log.info("恒明返回参数：" + results);
			
			JSONObject responseObj = JSONObject.parseObject(results);
			log.info("message:"+responseObj.get("message"));
			result.put("orderId",quickPayRequest.getOrderId());
			result.put("amount", quickPayRequest.getAmount());
			result.put("merchantId", quickPayRequest.getMerchantId());
			if("0".equals(responseObj.get("ret").toString())){
				String dedata = AesEncryption.Desencrypt(responseObj
						.get("data").toString(), HMUtil.aeskey, HMUtil.aeskey);
				log.info("恒明解析参数：" + dedata);
			JSONObject jsonObject2 = JSONObject.parseObject(dedata);
			
			log.info("jsonObject2"+jsonObject2);
			if("0".equals(jsonObject2.getString("orderstate"))) {
				result.put("respCode", "00");
				result.put("respMsg", "请求成功");
			}else {
				result.put("respCode", "01");
				result.put("respMsg", "请求失败");
			}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	
	@Override
	public void update(String orderId,String respcode ,OriginalOrderInfo originalInfo) throws Exception {
		log.info("返回的订单号："+orderId);
		log.info("**************进入修改方法*************************");
		// 流水表transOrderId
		String transOrderId = "";
		if(orderId!=""&& orderId!=null){
			transOrderId=orderId;
		}
		log.info("异步通知回来的订单号:" + transOrderId);
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息：" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息：" + pmsAppTransInfo);
		// 查询结果成功
		if("0".equals(respcode)) {
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
				if(orderId!=null&& orderId!=""){
					pospTransInfo.setPospsn(orderId);
				}
				
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				int ii =pospTransInfoDAO.updateByOrderId(pospTransInfo);
				log.info("更新流水结果："+ii);
				
			}
		} else {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("1");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				if(orderId!=null&& orderId!=""){
					pospTransInfo.setPospsn(orderId);
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
			
		}

		
	}
}

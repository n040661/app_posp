package xdt.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.kspay.cert.CertVerify;
import com.kspay.cert.LoadKeyFromPKCS12;

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
import xdt.dto.mb.DemoBase;
import xdt.dto.mb.HttpDeal;
import xdt.dto.mb.HttpService;
import xdt.dto.mb.MBReqest;
import xdt.dto.mb.MBResponse;
import xdt.dto.mb.MBThread;
import xdt.dto.mb.MBUtil;
import xdt.dto.mb.MD5;
import xdt.dto.mb.MapKeyComparator;
import xdt.dto.sxf.PayRequsest;
import xdt.dto.tfb.WxPayApplyRequest;
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
import xdt.schedule.ThreadPool;
import xdt.service.IMBService;
import xdt.service.IPublicTradeVerifyService;
import xdt.util.Constants;
import xdt.util.EncodeUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.TransDemo;
import xdt.util.UtilDate;
@Service
public class MBServiceImpl extends BaseServiceImpl implements IMBService {

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
	
	/**
	 * 银联二维码 向上游传送数据
	 * @param applyRequest
	 * @param result
	 * @param appTransInfo
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> otherInvokeCardPay(MBReqest mbReqest,Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception {
		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = mbReqest.getOrderId();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(mbReqest.getOrderId())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + mbReqest.getOrderId()
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
			appTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay.getTypeCode());
         pmsAppTransInfoDao.update(appTransInfo);
         //获取上游商户号和密钥
         StringBuffer paramSrc=new StringBuffer();
         PmsBusinessPos pmsBusinessPos =selectKey(mbReqest.getMerId());
         log.info("pmsBusinessPos:"+JSON.toJSONString(pmsBusinessPos));
         Map<String, String> paramsMap = new HashMap<String, String>();
         paramsMap.put("merId",pmsBusinessPos.getBusinessnum());//pmsBusinessPos.getBusinessnum()"818310048160000" 
         paramsMap.put("orderId", mbReqest.getOrderId());
         paramsMap.put("transDate", new SimpleDateFormat("YYYYMMddHHmmss").format(new Date()));
         paramsMap.put("transAmount", Double.parseDouble(mbReqest.getTransAmount())/100+"");
         paramsMap.put("backNotifyUrl", MBUtil.notifyUrl);
         paramsMap.put("versionId", MBUtil.versionId);
         paramsMap.put("businessType", MBUtil.businessType1);
         paramsMap.put("transChanlName", MBUtil.transChanlName);
         if(StringUtils.isNotBlank(mbReqest.getDev())){
        	 paramsMap.put("dev", new String(mbReqest.getDev().getBytes(MBUtil.codeU),MBUtil.codeG));
         }  
         paramsMap.put("orderDesc", URLEncoder.encode(mbReqest.getOrderDesc()));
         
         MapKeyComparator mapkey= new MapKeyComparator();
         Map<String, String> map=   mapkey.sortMapByKey1(paramsMap);
         
         Iterator<Entry<String, String>> it = map.entrySet().iterator();
 		 while (it.hasNext()) {
 			Entry<String, String> elem = it.next();
 			if(elem.getValue()!=null &&elem.getValue()!=""){
 				paramSrc.append(elem.getKey() + "=" + elem.getValue() + "&");
 			}
 		}
         log.info("签名之前的数据***魔宝"+paramSrc);
         String signData = MD5.MD5(paramSrc.toString()+"key="+pmsBusinessPos.getKek());//1FDD2547FA4FB61F+pmsBusinessPos.getKek()
         paramsMap.put("signType", MBUtil.signType);
         paramsMap.put("signData", signData);
         String s =paramSrc+"signType=MD5&signData="+signData;
         log.info("发送之前数据***魔宝："+JSON.toJSON(paramsMap));
         
         HttpService  HT=new HttpService();
         //String  retuString =RequestUtils.doPost("http://115.182.202.23:8880/ks_smpay/netsm/pay.sm", s,"GBK");
         String  retuString=HT.POSTReturnString(MBUtil.payUrl, paramsMap,MBUtil.codeG);
         log.info("魔宝返回参数："+JSON.toJSON(retuString));
			ObjectMapper om = new ObjectMapper();
			Map<String, String> maps=new HashMap<>();
			maps = om.readValue(retuString, Map.class);
			result.put("respMsg",URLDecoder.decode(maps.get("refMsg"),"GBK"));
			result.put("orderId", mbReqest.getOrderId());
			result.put("merId", mbReqest.getMerId());
			result.put("codeImgUrl",maps.get("codeImgUrl") );
			result.put("codeUrl",maps.get("codeUrl") );
			result.put("status",maps.get("refCode") );
			result.put("respCode",maps.get("status") );
			result.put("transAmount",maps.get("transAmount") );
	    return result;
	}
	
	
	
	/**
	 * cj001银联二维码
	 */
	
	@Override
	public Map<String, String> unionPayScanCode(MBReqest mbReqest,
			Map<String, String> result) {
		log.info("魔宝银联扫码支付进来了！");
		log.info("魔宝---service参数mbReqest："+JSON.toJSON(mbReqest));
		
		String out_trade_no = "";// 订单号
		out_trade_no = mbReqest.getOrderId(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = mbReqest.getMerId();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);
		
		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			log.info("查询当前商户信息******魔宝"+merchantList);
			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(mbReqest.getOrderId());//---------------------------
				oriInfo.setPid(mbReqest.getMerId());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复******魔宝");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoCardPay(mbReqest, out_trade_no, mercId);
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
					paramMap.put("paymentcode", PaymentCodeEnum.hengFengQuickPay.getTypeCode());

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
								log.info("此功能暂时关闭******魔宝!");
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
									log.info("此支付方式暂时关闭******魔宝");
								} else {

									BigDecimal payAmt = new BigDecimal(mbReqest.getTransAmount());// 收款金额
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao.checkLimit(oAgentNo, payAmt,
											TradeTypeEnum.merchantCollect.getTypeCode());
									// 返回不为0，一律按照交易失败处理
									if (!resultInfo.getErrCode().equals("0")) {
										result.put("respCode", "08");
										result.put("respMsg", "交易金额不在申请的范围之内");
										log.info("交易金额不在申请的范围之内******魔宝");

									} else {
										ResultInfo resultinfo = null;
										// 商户渠道支付方式
										// 商户渠道交易类型
										// 验证支付方式是否开启
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.GatewayCodePay, mercId);
										
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
														String totalAmount = mbReqest.getTransAmount(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															log.info("来了！！！");
															if("cj001".equals(mbReqest.getType())) {
																result=otherInvokeCardPay(mbReqest, result, appTransInfo);
															}else if("cj005".equals(mbReqest.getType())) {
																result=quickPay(mbReqest, result, appTransInfo);
															}else if("cj007".equals(mbReqest.getType()))
															{
																result=updateHandle(mbReqest, result, appTransInfo);
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
	/**
	 * 
	 * @Description 插入原始订单表信息
	 * @author Administrator
	 * @param reqeustInfo
	 * @param orderid
	 * @param mercId
	 * @throws Exception
	 */
	private int saveOriginAlInfoWxPay(WxPayApplyRequest applyRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(applyRequest.getSp_billno());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		//想要传服务器要改实体
		info.setBgUrl(applyRequest.getUrl());
		info.setPageUrl(applyRequest.getReUrl());
		Double amt = Double.parseDouble(applyRequest.getTran_amt());// 单位分
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
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
	private int saveOriginAlInfoCardPay(MBReqest mbReqest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(mbReqest.getOrderId());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		//想要传服务器要改实体
		info.setBgUrl(mbReqest.getUrl());
		if(mbReqest.getPageNotifyUrl()!=null)
		{
			info.setPageUrl(mbReqest.getPageNotifyUrl());
		}
		if(mbReqest.getDev()!=null)
		{
			info.setAttach(mbReqest.getDev());
		}
		Double amt = Double.parseDouble(mbReqest.getTransAmount());// 单位分
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
	
	
	public void update(MBResponse mbResponse) throws Exception {
		log.info("返回的参数***魔宝："+mbResponse);
		log.info("**************进入修改方法*************************");
		// 流水表transOrderId
		String transOrderId = "";
		if(mbResponse.getOrderId()!=""&& mbResponse.getOrderId()!=null){
			transOrderId=mbResponse.getOrderId();
		}
		
		log.info("异步通知回来的订单号:" + transOrderId);
		// 流水信息
		
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息：" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息：" + pmsAppTransInfo);
		log.info("订单表信息：" + mbResponse.getRefcode());
		// 查询结果成功
		if("00".equals(mbResponse.getRefcode())) {
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
				if(mbResponse.getOrderId()!=null&& mbResponse.getOrderId()!=""){
					pospTransInfo.setPospsn(mbResponse.getOrderId());
				}
				
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if("01".equals(mbResponse.getRefcode())){
			// 支付中
			pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("2");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("20");
				if(mbResponse.getOrderId()!=null&&mbResponse.getOrderId()!=""){
					pospTransInfo.setPospsn(mbResponse.getOrderId());
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
			
		}else  if("02".equals(mbResponse.getRefcode())){
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("1");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				if(mbResponse.getOrderId()!=null&&mbResponse.getOrderId()!=""){
					pospTransInfo.setPospsn(mbResponse.getOrderId());
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} 
	}
	public void update1(MBResponse mbResponse) throws Exception {
		log.info("返回的参数***魔宝："+mbResponse);
		log.info("**************进入修改方法*************************");
		// 流水表transOrderId
		String transOrderId = "";
		if(mbResponse.getOrderId()!=""&& mbResponse.getOrderId()!=null){
			transOrderId=mbResponse.getOrderId();
		}
		
		log.info("异步通知回来的订单号:" + transOrderId);
		// 流水信息
		
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息：" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息：" + pmsAppTransInfo);
		log.info("订单表信息：" + mbResponse.getRefcode());
		// 查询结果成功
		if("00".equals(mbResponse.getRefcode())) {
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
				if(mbResponse.getOrderId()!=null&& mbResponse.getOrderId()!=""){
					pospTransInfo.setPospsn(mbResponse.getOrderId());
				}
				
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if("03".equals(mbResponse.getRefcode())){
			// 支付中
			pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("2");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("20");
				if(mbResponse.getOrderId()!=null&&mbResponse.getOrderId()!=""){
					pospTransInfo.setPospsn(mbResponse.getOrderId());
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
			
		}else  if("02".equals(mbResponse.getRefcode())){
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("1");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				if(mbResponse.getOrderId()!=null&&mbResponse.getOrderId()!=""){
					pospTransInfo.setPospsn(mbResponse.getOrderId());
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} 
	}
	//D0填金
	public synchronized int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo ) throws Exception{
		log.info("代付实时填金:"+JSON.toJSON(originalInfo));
		DecimalFormat df =new DecimalFormat("#.00");
		PmsMerchantInfo pmsMerchantInfo =new PmsMerchantInfo();
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
				Double ds =positions.doubleValue();
				Double dd =amount*100+ds;
				dd =dd-poundage;
				log.info("来了1---------");
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
				log.info("来了2---------");
				//交易时间
				//pmsDaifuMerchantInfo.setCreationdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				int s=pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
				log.info("---s:"+s);
				log.info("来了3---------");
				int i =pmsMerchantInfoDao.UpdatePmsMerchantInfo(pmsMerchantInfo);
				log.info("---i:"+i);
				return i;
			}else{
				log.info("此商户未开通代付！！");
			}
		}
		
		return 0;
	}
	public int UpdatePmsMerchantInfo1(OriginalOrderInfo originalInfo ) throws Exception{
		log.info("代付实时填金:"+JSON.toJSON(originalInfo));
		DecimalFormat df =new DecimalFormat("#.00");
		PmsMerchantInfo pmsMerchantInfo =new PmsMerchantInfo();
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
				Double ds =positions.doubleValue();
				Double dd =(amount*100-poundage)*0.5;
				dd =(dd+ds);
				log.info("来了1---------");
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
				log.info("来了2---------");
				//交易时间
				//pmsDaifuMerchantInfo.setCreationdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				int s=pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
				log.info("---s:"+s);
				log.info("来了3---------");
				int i =pmsMerchantInfoDao.UpdatePmsMerchantInfo(pmsMerchantInfo);
				log.info("---i:"+i);
				return i;
			}else{
				log.info("此商户未开通代付！！");
			}
		}
		
		return 0;
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
	public Map<String, String> paySelect(MBReqest mbReqest,
			Map<String, String> result) throws Exception {
		 log.info("下游查询参数***魔宝"+JSON.toJSON(mbReqest));

		 PmsBusinessPos pmsBusinessPos =selectKey(mbReqest.getMerId());
		 Map<String, String> map = new HashMap<String, String>();
	        map.put("versionId",MBUtil.versionId);
	        map.put("businessType", MBUtil.businessType2);
	        map.put("transChanlName", MBUtil.transChanlName);
	        map.put("merId",pmsBusinessPos.getBusinessnum());//"818310048160000" pmsBusinessPos.getBusinessnum()
	        map.put("orderId", mbReqest.getOrderId());
	        map.put("transDate", new SimpleDateFormat("YYYYMMddHHmmss").format(new Date()));
	        if(StringUtils.isNotBlank(mbReqest.getDev())){
	            map.put("dev", mbReqest.getDev());
	        }      
			StringBuffer str = new StringBuffer();
			MapKeyComparator mapkey= new MapKeyComparator();
			Map<String, String> map2=   mapkey.sortMapByKey1(map);
			Iterator<Entry<String, String>> it = map2.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> elem = it.next();				
				str.append(elem.getKey() + "=" + elem.getValue() + "&");
			}
	        String signData = MD5.MD5(str.toString()+"key="+pmsBusinessPos.getKek());//1FDD2547FA4FB61F+pmsBusinessPos.getKek()
	        map.put("signType", MBUtil.signType);
	        map.put("signData", signData);
	        HttpDeal deal = new HttpDeal();
	        String  retuString=  deal.post(MBUtil.payUrl, map);
	        log.info("魔宝返回查询数据："+JSON.toJSON(retuString));
	        ObjectMapper om = new ObjectMapper();
	        Map<String,Object> maps = om.readValue(retuString, Map.class);
	        System.out.println("112312"+URLDecoder.decode(maps.get("refMsg").toString()));
	        result.put("respCode", (String) maps.get("status"));
	        result.put("status", (String) maps.get("refCode"));
	        result.put("orderId", (String) maps.get("orderId"));
	        result.put("respMsg", (String) maps.get("refMsg"));
	        if("00".equals((String) maps.get("status"))){
	        	  result.put("orderDesc", (String) maps.get("orderDesc"));
	  	        result.put("dev", (String) maps.get("dev"));
	        }
	        result.put("merId", mbReqest.getMerId());
	        
		    return result;
	}



	@Override
	public Map<String, String> pay(MBReqest mbReqest, Map<String, String> result) {
		log.info("魔宝----下游传送代付参数:"+JSON.toJSON(mbReqest));
		Calendar cal1 = Calendar.getInstance();
		java.text.SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
		try {
			
			long s =sdf.parse(sdf.format(cal1.getTime())).getTime();
			if (sdf.parse(sdf.format(cal1.getTime())).getTime() > sdf.parse("22:30:00").getTime()||sdf.parse(sdf.format(cal1.getTime())).getTime() < sdf.parse("09:00:00").getTime()) {
				log.info("时间不在正常入金时间内!");
				result.put("respCode", "0005");
				result.put("respMsg", "不在代付时间内！");
				return result;
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		BigDecimal b1=new BigDecimal("0");// 下游上传的总金额
		BigDecimal b2=new BigDecimal("0");// 系统代付余额
		BigDecimal b3=new BigDecimal("0");// 单笔交易总手续费
		BigDecimal min=new BigDecimal("0");// 代付最小金额
		BigDecimal max=new BigDecimal("0");// 代付最大金额
		int ii;
		Double surplus;// 代付剩余金额
		log.info("魔宝----查询当前代付订单是否存在");
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		Map<String, String> map =new HashMap<>();
		model.setMercId(mbReqest.getMerId());
		model.setBatchNo(mbReqest.getOrderId());
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
			result.put("respCode", "0006");
			result.put("respMsg", "下单失败,订单存在");
			log.info("魔宝----**********************代付 下单失败:{}");
			log.info("魔宝----订单存在");
			return result;
		}
		try {
			label104: {

				log.info("********************魔宝-------------根据商户号查询");
				String e = mbReqest.getMerId();
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
					log.info("***********魔宝*************商户信息:" + merchantinfo);
					if (StringUtils.isBlank(oAgentNo)) {
						throw new RuntimeException("系统错误----------------o单编号为空");
					}

					if ("60".equals(merchantinfo.getMercSts())) {
						// 判断交易类型
						log.info("***********魔宝*************实际金额");
						// 分
						String payAmt= mbReqest.getTransAmount();
						b1 =new BigDecimal(payAmt);
						
						System.out.println("参数:"+b1.doubleValue());
						log.info("***********魔宝*************校验欧单金额限制");
						log.info("魔宝----下游上传代付总金额:" + b1.doubleValue());
						b3 = new BigDecimal(merchantinfo.getPoundage());
						log.info("魔宝----系统商户代付单笔手续费:" + b3.doubleValue());
						min = new BigDecimal(merchantinfo.getMinDaiFu());
						log.info("魔宝----系统代付最小金额:" + min.doubleValue());
						max = new BigDecimal(merchantinfo.getMaxDaiFu());
						log.info("魔宝----系统代付最大金额:" + max.doubleValue());
						b2 = new BigDecimal(merchantinfo.getPosition());
						log.info("魔宝----系统剩余可用额度:" + b2.doubleValue());
						
						
						
						if (b1.doubleValue() + b3.doubleValue()*100 > b2.doubleValue()) {
							result.put("respCode", "0006");
							result.put("respMsg", "下单失败,代付金额高于剩余额度");
							log.info("魔宝**********************代付金额高于剩余额度");
							int i = add(mbReqest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("魔宝----添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() < min.doubleValue() * 100) {
							result.put("respCode", "0006");
							result.put("respMsg", "下单失败,代付金额小于代付最小金额");
							log.info("魔宝**********************代付金额小于代付最小金额");
							int i = add(mbReqest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("魔宝--添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() > max.doubleValue() * 100) {
							result.put("respCode", "0006");
							result.put("respMsg", "下单失败,代付金额大于代付最大金额");
							log.info("魔宝**********************代付金额大于代付最大金额");
							int i = add(mbReqest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("魔宝--添加失败订单成功");
							}
							return result;
						}
							surplus = b2.subtract(b1).doubleValue() - b3.doubleValue() * 100;
							merchantinfo.setPosition(surplus.toString());
							
							
							
						PmsBusinessPos pmsBusinessPos =selectKey(mbReqest.getMerId());
						int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
						if (num == 1) {
							log.info("魔宝--扣款成功！！");
							int i =add(mbReqest, merchantinfo, result, "200");
							if (i == 1) {
								log.info("魔宝--代付订单添加成功");
								//--------------------------------------
								net.sf.json.JSONObject transData = new net.sf.json.JSONObject(); 
								net.sf.json.JSONObject obj = new net.sf.json.JSONObject();  
								StringBuffer str = new StringBuffer();
					            //交易
					            transData.put("accName", URLEncoder.encode(mbReqest.getAccName(),"GBK")); // 收款人姓名
					            transData.put("accNo", mbReqest.getAccNo()); // 收款人账号  
					            transData.put("orderId", mbReqest.getOrderId()); // 订单号  
					            transData.put("transAmount", Double.parseDouble(mbReqest.getTransAmount())/100+""); // 交易金额
					            transData.put("transDate", new SimpleDateFormat("YYYYMMddHHmmss").format(new Date())); // 交易日期

					            //私钥证书加密
								String pfxFileName = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".pfx";//"D:\\936775585060000.pfx";
								log.info("魔宝代付私钥"+pfxFileName);

								String pfxPassword = "111111";
								 //LoadKeyFromPKCS12.initPrivateKey(pfxFileName, pfxPassword);
								
								//String  transBody=LoadKeyFromPKCS12.PrivateSign(transData.toString());
								PrivateKey privateKey = null;
								privateKey =TransDemo.initPrivateKey(pfxFileName, pfxPassword);
								System.out.println("111:"+privateKey);
								String  transBody=TransDemo.PrivateSign(transData.toString(),privateKey);
								log.info("墨宝代付个人信息"+transBody);
								obj.put("transBody", transBody);
								obj.put("businessType", MBUtil.businessType3); // 业务类型
					            obj.put("merId",pmsBusinessPos.getBusinessnum()); // 商户号pmsBusinessPos.getBusinessnum()
					            obj.put("versionId", MBUtil.versionId); // 版本号 
					            if(!"".equals(mbReqest.getDev())&&mbReqest.getDev()!=null){
					            	obj.put("dev",URLEncoder.encode(mbReqest.getDev()));
					             str.append("businessType" + "=" + MBUtil.businessType3).append("&dev="+mbReqest.getDev()).append("&merId" + "=" + pmsBusinessPos.getBusinessnum())
							            .append("&transBody" + "=" + transBody).append("&versionId" + "=" + MBUtil.versionId);
					            }else{
					            	
					            	str.append("businessType" + "=" + MBUtil.businessType3).append("&merId" + "=" + pmsBusinessPos.getBusinessnum())
					            	.append("&transBody" + "=" + transBody).append("&versionId" + "=" + MBUtil.versionId);
					            	
					            }
					           
					            String signData = MD5Util.MD5Encode(str.toString()+"&key="+pmsBusinessPos.getKek());
					            System.out.println(signData);
					            obj.put("signData", signData); // 交易日期
					            obj.put("signType", "MD5"); // 版本号          
					            
					            log.info("魔宝****发送给上游参数："+obj);
					            URL url = new URL(MBUtil.payPUrl);  
					            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
					            connection.setDoOutput(true);  
					            connection.setDoInput(true);  
					            connection.setRequestMethod("POST");  
					            connection.setUseCaches(false);  
					            connection.setInstanceFollowRedirects(true);               
					            connection.setRequestProperty("Content-Type","application/json; charset=GBK");                     
					            connection.connect();  
					            //POST请求  
					            DataOutputStream out = new DataOutputStream(connection.getOutputStream());  
					            out.write(obj.toString().getBytes("GBK"));
					            out.flush();  
					            out.close();  
					            
					            
					          //读取响应  
					            BufferedReader reader = new BufferedReader(new InputStreamReader(  
					                    connection.getInputStream()));  
					            String lines;  
					            StringBuffer sb = new StringBuffer("");  
					            while ((lines = reader.readLine()) != null) {  
					                lines = new String(lines.getBytes(), "gbk");  
					                sb.append(lines);  
					            }  
					            reader.close();  
					            // 断开连接  
					            connection.disconnect(); 
					            System.out.println(sb); 
					            ObjectMapper om = new ObjectMapper();
					            Map<String,Object> map1 = om.readValue(sb.toString(), Map.class); 
					            log.info("魔宝代付返回状态参数："+map1);
					            
					            if("00".equals(map1.get("status"))){
					            	String data = map1.get("resBody").toString();
						    		//公钥证书解密
						    		String cerFileName = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".cer";
						    		byte[]signByte=LoadKeyFromPKCS12.encryptBASE64(data);
						    	     CertVerify.initPublicKey(cerFileName);
						    		byte[] str1=CertVerify.publicKeyDecrypt(signByte);
						    		String string = new  String(str1);
						    		JSONObject  jasonObject = JSONObject.parseObject(string);
						    		
						    		log.info("魔宝返回代付状态参数："+jasonObject);
						    		Map<String, String> maps = new HashMap<String, String>();
						    		maps = (Map)jasonObject;
						    		if("00".equals(maps.get("refCode"))){
						    			//UpdateDaifu(mbReqest.getOrderId(), "00");
						    			result.put("orderId", maps.get("orderId"));
							    		result.put("transAmount", mbReqest.getTransAmount());
							    		result.put("respCode",(String)map1.get("status") );
							    		result.put("respMsg", URLDecoder.decode(maps.get("refMsg"),"GBK"));
							    		result.put("status", maps.get("refCode"));
							    		result.put("merId", mbReqest.getMerId());
							            System.out.println(URLDecoder.decode(maps.get("refMsg"),"GBK"));
							            ThreadPool.executor(new MBThread(this, mbReqest, pmsMerchantInfoDao, merchantinfo));
						    		}else{
						    			log.info("代付失败1！！！");
						    			result.put("respCode","0001");
										result.put("respMsg",URLDecoder.decode(maps.get("refMsg"),"GBK"));
						            	UpdateDaifu(mbReqest.getOrderId(), "01");
						            	Double payMoney =Double.parseDouble(mbReqest.getTransAmount())+Double.parseDouble(merchantinfo.getPoundage())*100;
						            	map.put("machId", mbReqest.getMerId());
						            	map.put("payMoney",payMoney.toString());
						            	int nus = pmsMerchantInfoDao.updataPay(map);
						            	if(nus==1){
						            		log.info("魔宝***补款成功");
						            		mbReqest.setOrderId(mbReqest.getOrderId()+"/A");
						            		//surplus = surplus+Double.parseDouble(mbReqest.getTransAmount());
						            		//merchantinfo.setPosition(surplus.toString());
						            		ii =add(mbReqest, merchantinfo, result, "00");
						            		if(ii==1) {
						            			log.info("墨宝添加代付失败订单成功");
						            		}else {
						            			log.info("墨宝添加代付失败订单失败");
						            		}
						            	}
										return result;
						    		}
					            }else{
					            	log.info("代付失败2！！！");
					            	result.put("respCode","0001");
									result.put("respMsg","代付请求失败");
					            	UpdateDaifu(mbReqest.getOrderId(), "01");
					            	Double payMoney =Double.parseDouble(mbReqest.getTransAmount())+Double.parseDouble(merchantinfo.getPoundage())*100;
									map.put("machId", mbReqest.getMerId());
									map.put("payMoney",payMoney.toString());
									int nus = pmsMerchantInfoDao.updataPay(map);
									if(nus==1){
										log.info("随行付***补款成功");
										mbReqest.setOrderId(mbReqest.getOrderId()+"/A");
										//surplus = surplus+Double.parseDouble(mbReqest.getTransAmount());
										//merchantinfo.setPosition(surplus.toString());
										ii =add(mbReqest, merchantinfo, result, "00");
										if(ii==1){
											log.info("魔宝添加失败订单成功");
										}else {
											log.info("魔宝添加失败订单失败");
										}
									}
									log.info("随行付代付返回状态吗错误");
									
									return result;
					            	
					            }
							}
						}
						
					} else {
						throw new RuntimeException("魔宝***系统错误----------------当前商户非正式商户");
					}

				} else {
					throw new RuntimeException("魔宝***系统错误----------------商户不存在");
				}
				break label104;
			}

		} catch (Exception var43) {
			log.error("魔宝*******************************代付错误", var43);
			try {
				throw var43;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	log.info("***********魔宝*********************代付------处理完成");
	return result;

	}
	
	
	//添加代付订单
		public synchronized int add(MBReqest mbReqest, PmsMerchantInfo merchantinfo, Map<String, String> result ,String state) throws Exception {
			log.info("进来添加代付订单了");
			BigDecimal b1=new BigDecimal("0");//总金额
			int iii=0;
			merchantinfo=select(mbReqest.getMerId());
			PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
			b1 =new BigDecimal(mbReqest.getTransAmount());
					 model.setMercId(mbReqest.getMerId());
					 model.setCount("1");
					 model.setBatchNo(mbReqest.getOrderId());
					 model.setIdentity(mbReqest.getOrderId());
					 model.setAmount(b1.doubleValue()/100+"");
					 model.setCardno(mbReqest.getAccNo());
					 model.setRealname(mbReqest.getAccName());
					 if(mbReqest.getOrderId().indexOf("/A")>0){
						 model.setPayamount(b1.doubleValue()/100+"");
					 }else{
						 model.setPayamount("-" +b1.doubleValue()/100);
					 }
					 model.setPmsbankno(mbReqest.getPmsbankno());
					 if(mbReqest.getOrderId().indexOf("/A")>0){
						 model.setTransactionType("代付补款");
					 }else{
						 model.setTransactionType("代付");
					 }
					 model.setPosition(String.valueOf(merchantinfo.getPosition()));
					 model.setRemarks("D0");
					 model.setRecordDescription("批次号:" + mbReqest.getOrderId()+"订单号："+mbReqest.getOrderId()+ "错误原因:" + result.get("respMsg"));
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
		
		// 修改代付状态
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
		public Map<String, String> paysSelect(MBReqest mbReqest,
				Map<String, String> result) throws Exception {
			try{  
				PmsBusinessPos pmsBusinessPos =selectKey(mbReqest.getMerId());
	            //创建连接  
	            URL url = new URL(MBUtil.payPUrl);  
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();  
	            connection.setDoOutput(true);  
	            connection.setDoInput(true);  
	            connection.setRequestMethod("POST");  
	            connection.setUseCaches(false);  
	            connection.setInstanceFollowRedirects(true);               
	            connection.setRequestProperty("Content-Type","application/json; charset=GBK");                     
	            connection.connect();  
	  
	            //POST请求  
	            DataOutputStream out = new DataOutputStream(connection.getOutputStream());  
	            JSONObject obj = new JSONObject();           
	            
	            String merId = pmsBusinessPos.getBusinessnum();
	            String orderId = mbReqest.getOrderId();
	            String transDate = new SimpleDateFormat("YYYYMMddHHmmss").format(new Date());
	            String versionId = "001";
	            StringBuffer str = new StringBuffer(); 
	            
	            JSONObject transData = new JSONObject(); 
	            //查询
	            transData.put("orderId", orderId); // 订单号  
	            transData.put("transDate", transDate); // 交易日期

	            //私钥证书加密
				String pfxFileName = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+merId+".pfx";

				String pfxPassword = "111111";
				/*TransDemo.initPrivateKey(pfxFileName, pfxPassword);
				String  transBody=TransDemo.PrivateSign(transData.toString());*/
				PrivateKey privateKey = null;
				privateKey =TransDemo.initPrivateKey(pfxFileName, pfxPassword);
				System.out.println("111:"+privateKey);
				String  transBody=TransDemo.PrivateSign(transData.toString(),privateKey);
				obj.put("transBody", transBody);
				System.out.println(transBody);
				
	            obj.put("businessType", "460000"); // 业务类型
	            obj.put("merId", merId); // 商户号
	            obj.put("versionId", versionId); // 版本号             
	            str.append("businessType" + "=" + "460000").append("&merId" + "=" + merId)
	            .append("&transBody" + "=" + transBody).append("&versionId" + "=" + versionId);
	            
	            System.out.println(str);
	            String signData = MD5.MD5(str.toString()+"&key="+pmsBusinessPos.getKek());
	            System.out.println(signData);
	            obj.put("signData", signData); // 交易日期
	            obj.put("signType", "MD5"); // 版本号          

	            System.out.println(obj);
	            out.write(obj.toString().getBytes("GBK"));
	            out.flush();  
	            out.close();  
	              
	            //读取响应  
	            BufferedReader reader = new BufferedReader(new InputStreamReader(  
	                    connection.getInputStream()));  
	            String lines;  
	            StringBuffer sb = new StringBuffer("");  
	            while ((lines = reader.readLine()) != null) {  
	                lines = new String(lines.getBytes(), "gbk");  
	                sb.append(lines);  
	            }  
	            System.out.println(sb); 
	            ObjectMapper om = new ObjectMapper();
	            Map<String,Object> map1 = om.readValue(sb.toString(), Map.class); 
	            reader.close();  
	            // 断开连接  
	            connection.disconnect();  
	            if("00".equals(map1.get("status"))){
	            	String data = map1.get("resBody").toString();
		    		//公钥证书解密
		    		String cerFileName = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+merId+".cer";  		
		    		/*byte[]signByte=LoadKeyFromPKCS12.encryptBASE64(data);
		    		 CertVerify.initPublicKey(cerFileName);
		    		byte[] str1=CertVerify.publicKeyDecrypt(signByte);*/
		    		byte[]signByte=LoadKeyFromPKCS12.encryptBASE64(data);
		    	     CertVerify.initPublicKey(cerFileName);
		    		byte[] str1=CertVerify.publicKeyDecrypt(signByte);
		    		String string = new  String(str1);
		    		
		    		JSONObject  jasonObject = JSONObject.parseObject(string);
		    		Map<String, String> map = new HashMap<String, String>();
		    		map = (Map)jasonObject;
		    		log.info("查询成功返回的参数："+map);
		    		result.put("respCode", map1.get("status")+"");
		    		result.put("status", map.get("refCode"));
					result.put("orderId", map.get("orderId"));
					result.put("respMsg",URLDecoder.decode(map.get("refMsg"),"GBK"));
					result.put("merId",mbReqest.getMerId() );
					if(map1.get("dev")!=null&&map1.get("dev")!=""){
						result.put("dev",map1.get("dev")+"");
					}
		            System.out.println(URLDecoder.decode(map.get("refMsg"),"GBK"));
	            }else{
	            	log.info("查询失败！！！");
	            	result.put("respCode","0001");
					result.put("respMsg","查询失败");
					result.put("merId",mbReqest.getMerId());
					result.put("orderId", mbReqest.getOrderId());
	            }
	    		
	           
	        } catch (MalformedURLException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } catch (UnsupportedEncodingException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	  
			return result;
		}
		/**
		 * 快捷短信支付
		 */
		public Map<String, String> quickPay(MBReqest mbReqest, Map<String, String> result,PmsAppTransInfo appTransInfo) {
			
		try {
			// 查看当前交易是否已经生成了流水表
			PospTransInfo pospTransInfo = null;
			// 流水表是否需要更新的标记 0 insert，1：update
			int insertOrUpdateFlag = 0;
			log.info("***************进入payHandle5-14-3***************");
			// 生成上送流水号
			String transOrderId = mbReqest.getOrderId();
			log.info("***************进入payHandle5-15***************");
			if ((pospTransInfo = pospTransInfoDAO
					.searchByOrderId(mbReqest.getOrderId())) != null) {
				// 已经存在，修改流水号，设置pospsn为空
				log.info("订单号：" + mbReqest.getOrderId()
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
	         PmsBusinessPos pmsBusinessPos =selectKey(mbReqest.getMerId());
			//商户号码
			String merId =pmsBusinessPos.getBusinessnum();//818310048160000
			//商户号私钥
			String merKey=pmsBusinessPos.getKek();//1FDD2547FA4FB61F
//			
			Map<String,String>  transmap= new LinkedHashMap<String, String>();
			transmap.put("versionId","001");     //版本号  固定
			transmap.put("businessType","1401");  //预交易  1401  
//			transmap.put("insCode","");
			transmap.put("merId",merId);            //商户号
			transmap.put("orderId",mbReqest.getOrderId());   //订单号
			transmap.put("transDate",mbReqest.getTransDate()==null?DateUtil.getTimess1():mbReqest.getTransDate());  //时间    yymmddhhmmss
			transmap.put("transAmount",Double.parseDouble(mbReqest.getTransAmount())/100+"");   //金额 单位元，对于正式商户最低支付金额为10元
			transmap.put("cardByName",MD5Util.encode(mbReqest.getAccName().getBytes("UTF-8")));  //此处的MD5util为Base64加密
			transmap.put("cardByNo",mbReqest.getAccNo());  //卡号
			transmap.put("cardType",mbReqest.getAccType());  //卡类型01借记卡
			transmap.put("expireDate",mbReqest.getExpireDate()); // 有效期
			transmap.put("CVV",mbReqest.getCVV());    //CVN
			transmap.put("bankCode",mbReqest.getBankCode());   //可为空    银行代码
			transmap.put("openBankName",mbReqest.getOpenBankName());//可为空    银行代码
			transmap.put("cerType",mbReqest.getCerType());   //证件类型 01  身份证
			transmap.put("cerNumber",mbReqest.getCerNumber());//身份证
			transmap.put("mobile",mbReqest.getMobile());  //手机号
			transmap.put("isAcceptYzm","00"); //默认00
			transmap.put("backNotifyUrl", MBUtil.notifyUrl);
			transmap.put("instalTransFlag","01");  //分期标志
			//transmap.put("instalTransNums","12");   //分期期数
//			transmap.put("orderDesc","");
//			transmap.put("dev","");
//			transmap.put("fee","");
			//transmap.put("pageNotifyUrl", MBUtil.returnUrl);
			//需要加密的字符串
			String  signstr=EncodeUtil.getUrlStr(transmap);
			System.out.println("需要签名的明文"+signstr);
			String  signtrue=MD5Util.MD5Encode(signstr+merKey);
			transmap.put("signType","MD5");
			transmap.put("signData",signtrue);
			//AES加密
			String  transUrlStr=EncodeUtil.getUrlStr(transmap);
			//
			String  transData=AESUtil.encrypt(transUrlStr, merKey);
			//生产地址
			String   testUrl=MBUtil.quick;
			String str =DemoBase.requestBody(merId,transData, testUrl);
			//获取交易返回结果
			System.out.println(str);
			ObjectMapper om = new ObjectMapper();
			Map<String, String> maps=new HashMap<>();
			maps = om.readValue(str, Map.class);
			result.put("merId", mbReqest.getMerId());
			result.put("orderId", mbReqest.getOrderId());
			if("00".equals(maps.get("status"))) {
				result.put("respCode", "00");
				result.put("respMsg", "请求成功");
				if("01".equals(maps.get("refCode"))){
					
					result.put("status", "00");
					result.put("message", maps.get("refMsg"));
					result.put("payOrderId", maps.get("ksPayOrderId"));
					result.put("bankOrderId", maps.get("bankOrderId"));
					result.put("transTime", maps.get("transTime"));
				}else if("02".equals(maps.get("refCode"))){
					result.put("status", "01");
					result.put("message", maps.get("refMsg"));
				}else {
					result.put("status", "01");
					result.put("message", maps.get("refMsg"));
				}
			}else if("01".equals(maps.get("status"))){
				result.put("respCode", "01");
				result.put("respMsg", maps.get("refMsg"));
			}else if("02".equals(maps.get("status"))){
				result.put("respCode", "02");
				result.put("respMsg", "系统错误");
			}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return result;
		}
		/**
		 * 快捷收银台支付
		 */
		public Map<String, String> updateHandle(MBReqest mbReqest, Map<String, String> result,PmsAppTransInfo appTransInfo) {
			
		try {
			// 查看当前交易是否已经生成了流水表
			PospTransInfo pospTransInfo = null;
			// 流水表是否需要更新的标记 0 insert，1：update
			int insertOrUpdateFlag = 0;
			log.info("***************进入payHandle5-14-3***************");
			// 生成上送流水号
			String transOrderId = mbReqest.getOrderId();
			log.info("***************进入payHandle5-15***************");
			if ((pospTransInfo = pospTransInfoDAO
					.searchByOrderId(mbReqest.getOrderId())) != null) {
				// 已经存在，修改流水号，设置pospsn为空
				log.info("订单号：" + mbReqest.getOrderId()
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
	         PmsBusinessPos pmsBusinessPos =selectKey(mbReqest.getMerId());
			//商户号码
			String merId =pmsBusinessPos.getBusinessnum();//818310048160000
			//商户号私钥
			String merKey=pmsBusinessPos.getKek();//1FDD2547FA4FB61F
//			
			//Map<String,String>  transmap= new LinkedHashMap<String, String>();
			result.put("versionId","001");     //版本号  固定
			result.put("businessType","1100");  //预交易  1401  
//			transmap.put("insCode","");
			result.put("merId","936640995770000");            //商户号
			result.put("orderId",mbReqest.getOrderId());   //订单号
			result.put("transDate",mbReqest.getTransDate()==null?DateUtil.getTimess1():mbReqest.getTransDate());  //时间    yymmddhhmmss
			result.put("transAmount",Double.parseDouble(mbReqest.getTransAmount())/100+"");   //金额 单位元，对于正式商户最低支付金额为10元
			result.put("transCurrency","156");  //此处的MD5util为Base64加密
			if(mbReqest.getTransChanlName()!=null)
			{
				result.put("transChanlName",mbReqest.getTransChanlName());  //卡号
			}	
			result.put("pageNotifyUrl",MBUtil.returnUrl);  //卡类型01借记卡
			result.put("backNotifyUrl", MBUtil.unionPaynotifyUrl);
			String attch=new String(mbReqest.getDev().getBytes("ISO-8859-1"),"GBK");
			result.put("dev",attch);  //分期标志
			String  signstr = "";
			//需要加密的字符串
			if(mbReqest.getTransChanlName()!=null)
			{
				signstr = "versionId=001&businessType=1100&merId=936640995770000&orderId="+mbReqest.getOrderId()+"&transDate="+result.get("transDate")+"&transAmount="+result.get("transAmount")+"&transCurrency=156&transChanlName="+mbReqest.getTransChanlName()+"&pageNotifyUrl="+MBUtil.returnUrl+"&backNotifyUrl="+MBUtil.unionPaynotifyUrl+"&dev="+attch;
				
			}else {
				
				signstr = "versionId=001&businessType=1100&merId=936640995770000&orderId="+mbReqest.getOrderId()+"&transDate="+result.get("transDate")+"&transAmount="+result.get("transAmount")+"&transCurrency=156&pageNotifyUrl="+MBUtil.returnUrl+"&backNotifyUrl="+MBUtil.unionPaynotifyUrl+"&dev="+attch;
				
			}
			
			System.out.println("需要签名的明文"+signstr);
			String  signtrue=MD5Util.MD5Encode(signstr+"072C15B8D473BB29");
			result.put("signData",signtrue);
			} catch (Exception e) {
				// TODO: handle exception
			}
			return result;
		}
		/**
		 * 快捷验证支付
		 */

		@Override
		public Map<String, String> verification(MBReqest mbReqest, Map<String, String> result) {
			
			 PmsBusinessPos pmsBusinessPos =selectKey(mbReqest.getMerId());
			//商户号码
			String merId =pmsBusinessPos.getBusinessnum();
			//商户号私钥
			String merKey=pmsBusinessPos.getKek();
		
			Map<String,String>  transmap= new LinkedHashMap<String, String>();
			transmap.put("versionId","001");
			transmap.put("businessType","1411");
			transmap.put("insCode","");
			transmap.put("merId",merId);
			transmap.put("yzm",mbReqest.getYzm());         //从1401交易 获取的yzm 填入此项完成支付验证
			transmap.put("ksPayOrderId",mbReqest.getKsPayOrderId()); //从1401交易 获取的ksPayOrderId 填入此项 寻找原交易 完成支付
			//需要加密的字符串
			String  signstr=EncodeUtil.getUrlStr(transmap);
			System.out.println("需要签名的明文"+signstr);
			String  signtrue=MD5Util.MD5Encode(signstr+merKey);
			transmap.put("signType","MD5");
			transmap.put("signData",signtrue);
			//AES加密
			String  transUrlStr=EncodeUtil.getUrlStr(transmap);
			//
			String  transData=AESUtil.encrypt(transUrlStr, merKey);
			//获取交易返回结果
			String   testUrl=MBUtil.quick;
			String str =DemoBase.requestBody(merId,transData, testUrl);
			System.out.println(str);
			ObjectMapper om = new ObjectMapper();
			Map<String, String> maps=new HashMap<>();
			try {
				maps = om.readValue(str, Map.class);
				
				result.put("merId", mbReqest.getMerId());
				result.put("orderId", mbReqest.getOrderId());
				result.put("payOrderId", mbReqest.getKsPayOrderId());
				if("00".equals(maps.get("status"))) {
					result.put("respCode", "00");
					result.put("respMsg", "请求成功");
					if("00".equals(maps.get("refCode"))){
						result.put("status", "00");
						result.put("message", maps.get("refMsg"));
						result.put("transTime", maps.get("transTime"));
						result.put("bankOrderId", maps.get("bankOrderId"));
					}else if("02".equals(maps.get("refCode"))) {
						result.put("status", "01");
						result.put("message", maps.get("refMsg"));
					}else if("03".equals(maps.get("refCode"))) {
						result.put("status", "200");
						result.put("message", maps.get("refMsg"));
					}else {
						result.put("status", "01");
						result.put("message", maps.get("refMsg"));
					}
				}else if("01".equals(maps.get("status"))){
					result.put("respCode", "01");
					result.put("respMsg", "请求失败");
				}else if("02".equals(maps.get("status"))){
					result.put("respCode", "02");
					result.put("respMsg", "系统错误");
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}
		public static void main(String[] args) {
			try {
				System.out.println(URLDecoder.decode("%CF%B5%CD%B3%B4%ED%CE%F3%2C%C7%EB%C1%AA%CF%B5%C4%A6%B1%A6%BF%CD%BB%A7%B7%FE%CE%F1","GBK"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}

package xdt.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import xdt.dto.BaseUtil;
import xdt.dto.sxf.Base64Utils;
import xdt.dto.sxf.DESUtils;
import xdt.dto.sxf.DF1003Request;
import xdt.dto.sxf.HttpClientUtil;
import xdt.dto.sxf.JsonUtils;
import xdt.dto.sxf.PayRequsest;
import xdt.dto.sxf.PayResponse;
import xdt.dto.sxf.SXFRequest;
import xdt.dto.sxf.SXFResponse;
import xdt.dto.sxf.SXFUtil;
import xdt.dto.sxf.SxfThread;
import xdt.dto.sxf.SxfThreads;
import xdt.dto.tfb.CardPayApplyRequest;
import xdt.dto.tfb.PayRequest;
import xdt.dto.tfb.TFBConfig;
import xdt.dto.tfb.TFBThread;
import xdt.dto.tfb.WxPayApplyRequest;
import xdt.dto.tfb.WxPayApplyResponse;
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
import xdt.service.IPublicTradeVerifyService;
import xdt.service.ISxfService;
import xdt.util.Constants;
import xdt.util.HttpURLConection;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.PaymentUtils;
import xdt.util.utils.RSAUtils;
import xdt.util.utils.RequestUtils;
@Service
@Transactional
public class SxfServiceImpl extends BaseServiceImpl implements ISxfService {
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
	public Map<String, String> cardPay(SXFRequest sxfRequest,
			Map<String, String> result) {
		log.info("---------------网关支付进来了---------------");
		log.info("上传到server层参数:"+JSON.toJSON(sxfRequest));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = sxfRequest.getOrderNo(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = sxfRequest.getMercNo();

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
				oriInfo.setMerchantOrderId(sxfRequest.getOrderNo());//---------------------------
				oriInfo.setPid(sxfRequest.getMercNo());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoCardPay(sxfRequest, out_trade_no, mercId);
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
					paramMap.put("paymentcode", PaymentCodeEnum.weixinPay.getTypeCode());

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
										PaymentCodeEnum.weixinPay.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(sxfRequest.getTranAmt());// 收款金额
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
														String totalAmount = sxfRequest.getTranAmt(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															String str =otherInvokeCardPay(sxfRequest, resultMap, appTransInfo);
															JSONObject json =JSONObject.parseObject(str);
															result.put("sign", json.get("sign").toString());
															result.put("mercNo",SXFUtil.mercNo);
															result.put("tranCd", SXFUtil.tranCd);
															result.put("version", SXFUtil.version);
															result.put("reqData", json.get("reqData").toString());
															result.put("ip", SXFUtil.ip);
															result.put("type", SXFUtil.type);
															result.put("encodeType",SXFUtil.encodeType);
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
	private int saveOriginAlInfoCardPay(SXFRequest sxfRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(sxfRequest.getOrderNo());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		//想要传服务器要改实体
		info.setBgUrl(sxfRequest.getUrl());
		info.setPageUrl(sxfRequest.getReUrl());
		Double amt = Double.parseDouble(sxfRequest.getTranAmt());// 单位分
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
	 * 网关支付向上游传送数据
	 * @param applyRequest
	 * @param result
	 * @param appTransInfo
	 * @return
	 * @throws Exception
	 */
	public String otherInvokeCardPay(SXFRequest sxfRequest,Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception {
		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = sxfRequest.getOrderNo();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(sxfRequest.getOrderNo())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + sxfRequest.getOrderNo()
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
		// 组装上送参数
		//1微信
			appTransInfo.setPaymenttype(PaymentCodeEnum.GatewayCodePay.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.GatewayCodePay.getTypeCode());
         pmsAppTransInfoDao.update(appTransInfo);
         //获取上游商户号和密钥
         PmsBusinessPos pmsBusinessPos =selectKey(sxfRequest.getMercNo());
         TreeMap<String, String> paramsMap = new TreeMap<String, String>();
         net.sf.json.JSONObject json = new net.sf.json.JSONObject();
         	Double d =Double.parseDouble(sxfRequest.getTranAmt())/100;
            json.put("orderNo", sxfRequest.getOrderNo());
	     	json.put("tranAmt", d);
	     	json.put("ccy",SXFUtil.ccy);
	     	json.put("pname", sxfRequest.getPname());
	     	json.put("pnum",SXFUtil.pnum);
	     	json.put("pdesc", sxfRequest.getPdesc());
	     	json.put("retUrl", SXFUtil.returnUrl);
	     	json.put("notifyUrl",SXFUtil.notifyUrl);
	     	json.put("bankWay",sxfRequest.getBankWay());
	     	json.put("period",sxfRequest.getPeriod());
	     	json.put("desc",sxfRequest.getDesc());
	     	json.put("userId",sxfRequest.getUserId());
	     	json.put("payWay", sxfRequest.getPayWay());
	     	json.put("payChannel",sxfRequest.getPayChannel());
	     	json.put("bankCardNo",sxfRequest.getBankCardNo());
	     	json.put("cvv", sxfRequest.getCvv());
	     	json.put("valid",sxfRequest.getValid());
	     	json.put("accountName", sxfRequest.getAccountName());
	     	json.put("certificateNo", sxfRequest.getCertificateNo());
	     	json.put("mobilePhone", sxfRequest.getMobilePhone());
	     	log.info("json："+json);
	     	String key =pmsBusinessPos.getKek();//"12345";
	     	String data=PaymentUtils.encrypt(json.toString(),SXFUtil.publicKey );//
	     	
	     	net.sf.json.JSONObject main = new net.sf.json.JSONObject();
	     	String mercNo =pmsBusinessPos.getBusinessnum();
			log.info("随行付查询上游商户号："+mercNo);
	    	main.put("mercNo",mercNo);
	    	main.put("tranCd", SXFUtil.tranCd);
	    	main.put("version", SXFUtil.version);
	    	main.put("reqData", data);
	    	main.put("ip", SXFUtil.ip);
	    	log.info("main："+main.toString());
	    	// 加签名，注意参数顺序
	    	String sign = PaymentUtils.sign(main.toString(),key );//SXFUtil.mercPrivateKey
	    	log.info("sign："+sign);
	    	
	    	net.sf.json.JSONObject jsons = new net.sf.json.JSONObject();
	    	jsons.put("sign",sign );
	    	jsons.put("reqData", data);
	    	
	    	
	    	/*paramsMap.put("mercNo",SXFUtil.mercNo);
	    	paramsMap.put("tranCd", SXFUtil.tranCd);
	    	paramsMap.put("version", SXFUtil.version);
	    	paramsMap.put("reqData", data);
	    	paramsMap.put("ip", SXFUtil.ip);
	    	paramsMap.put("type", SXFUtil.type);
	    	paramsMap.put("encodeType",SXFUtil.encodeType);
	    	String paramSrc = RequestUtils.getParamSrc(paramsMap);
	    	paramSrc=paramSrc+"&sign="+sign;
	    	//paramSrc =URLEncoder.encode(paramSrc); 
	    	//paramSrc=paramSrc.replace("+", "2B%");
	    	log.info("上传上游前生成签名字符串:"+paramSrc);
	    	String cipherData=RequestUtils.doPost(SXFUtil.url, paramSrc,"UTF-8");
	    	log.info("随行付返回参数："+cipherData);
	    	log.info("随行付返回json参数："+JSON.toJSON(cipherData));*/
	    	return jsons.toString();
	}
	
	@Override
	public Map<String, String> paySelect(SXFRequest sxfRequest,
			Map<String, String> result) throws Exception {
		//net.sf.json.JSONObject json = new net.sf.json.JSONObject();
		//json.put("orderNo", sxfRequest.getOrderNo());
		//log.info("json："+json);
		//TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		//String data=PaymentUtils.encrypt(json.toString(), SXFUtil.publicKey);
		//net.sf.json.JSONObject main = new net.sf.json.JSONObject();
    	//main.put("mercNo",SXFUtil.mercNo);
    	//main.put("tranCd", SXFUtil.tranCd);
    	//main.put("version", SXFUtil.version);
    	//main.put("reqData", data);
    	//main.put("ip", SXFUtil.ip);
    	//log.info("main："+main.toString());
    	// 加签名，注意参数顺序
    	//String sign = PaymentUtils.sign(main.toString(), SXFUtil.mercPrivateKey);
    	//log.info("sign："+sign);
    	result.put("orderNo", sxfRequest.getOrderNo());
    	//result.put("sign", sign);
    	//result.put("reqData", data);
    	result.put("mercNo",SXFUtil.mercNo);
    	result.put("tranCd", SXFUtil.tranCd1);
    	result.put("version", SXFUtil.version);
    	result.put("ip", SXFUtil.ip);
    	result.put("encodeType",SXFUtil.encodeType);
    	/*net.sf.json.JSONObject jsons = new net.sf.json.JSONObject();
    	jsons.put("mercNo",SXFUtil.mercNo);
    	jsons.put("tranCd", SXFUtil.tranCd1);
    	jsons.put("version", SXFUtil.version);
    	jsons.put("reqData", data);
    	jsons.put("ip", SXFUtil.ip);
    	jsons.put("encodeType",SXFUtil.encodeType);
    	jsons.put("sign",sign);
    	log.info("上传上游前生成签名字符串:"+jsons.toString());
    	String cipherData=RequestUtils.doPost(SXFUtil.selectUrl, "_t="+jsons.toString(),"UTF-8");
    	log.info("随行付返回参数："+cipherData);
    	log.info("随行付返回json参数："+JSON.toJSON(cipherData));
    	result.put("str", cipherData);*/
		return result;
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
	
	
	public int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo ) throws Exception{
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
				dd =(dd-poundage)*0.95;
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
	
	@Override
	public void update(SXFResponse sxfResponse) throws Exception {
		log.info("返回的参数："+sxfResponse);
		log.info("**************进入修改方法*************************");
		// 流水表transOrderId
		String transOrderId = "";
		if(sxfResponse.getOrderNo()!=""&& sxfResponse.getOrderNo()!=null){
			transOrderId=sxfResponse.getOrderNo();
		}
		
		log.info("异步通知回来的订单号:" + transOrderId);
		// 流水信息
		
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息：" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息：" + pmsAppTransInfo);
		// 查询结果成功
		if("S".equals(sxfResponse.getTranSts())) {
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
				if(sxfResponse.getOrderNo()!=null&& sxfResponse.getOrderNo()!=""){
					pospTransInfo.setPospsn(sxfResponse.getOrderNo());
				}
				
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if("U".equals(sxfResponse.getTranSts())){
			// 支付中
			pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("2");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("20");
				if(sxfResponse.getOrderNo()!=null&&sxfResponse.getOrderNo()!=""){
					pospTransInfo.setPospsn(sxfResponse.getOrderNo());
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
			
		}else {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("1");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				if(sxfResponse.getOrderId()!=null&&sxfResponse.getOrderId()!=""){
					pospTransInfo.setPospsn(sxfResponse.getOrderId());
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} 

		
	}


	/**
	 * 代付
	 */
	@Override
	public synchronized Map<String, String> pay(PayRequsest payRequsest,
			Map<String, String> result) {
		log.info("随行付----下游传送代付参数:"+JSON.toJSON(payRequsest));
		BigDecimal b1=new BigDecimal("0");// 下游上传的总金额
		BigDecimal b2=new BigDecimal("0");// 系统代付余额
		BigDecimal b3=new BigDecimal("0");// 单笔交易总手续费
		BigDecimal min=new BigDecimal("0");// 代付最小金额
		BigDecimal max=new BigDecimal("0");// 代付最大金额
		int ii;
		Double surplus;// 代付剩余金额
		log.info("随行付----查询当前代付订单是否存在");
		DF1003Request df1003Request =new DF1003Request();
		DF1003Request.PayItems payItems =new DF1003Request.PayItems();
		PayRequsest requsest =new PayRequsest();
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		Map<String, String> map =new HashMap<>();
		model.setMercId(payRequsest.getClientId());
		model.setBatchNo(payRequsest.getReqId());
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
			result.put("respCode", "0006");
			result.put("respMsg", "下单失败,订单存在");
			log.info("随行付----**********************代付 下单失败:{}");
			log.info("随行付----订单存在");
			return result;
		}
		try {
			label104: {

				log.info("********************随行付-------------根据商户号查询");
				String e = payRequsest.getClientId();
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
					log.info("***********随行付*************商户信息:" + merchantinfo);
					if (StringUtils.isBlank(oAgentNo)) {
						throw new RuntimeException("系统错误----------------o单编号为空");
					}

					if ("60".equals(merchantinfo.getMercSts())) {
						// 判断交易类型
						log.info("***********随行付*************实际金额");
						// 分
						String payAmt= payRequsest.getPayAmt();
						b1 =new BigDecimal(payAmt);
						
						System.out.println("参数:"+b1.doubleValue());
						log.info("***********随行付*************校验欧单金额限制");
						log.info("随行付----下游上传代付总金额:" + b1.doubleValue());
						b3 = new BigDecimal(merchantinfo.getPoundage());
						log.info("随行付----系统商户代付单笔手续费:" + b3.doubleValue());
						min = new BigDecimal(merchantinfo.getMinDaiFu());
						log.info("随行付----系统代付最小金额:" + min.doubleValue());
						max = new BigDecimal(merchantinfo.getMaxDaiFu());
						log.info("随行付----系统代付最大金额:" + max.doubleValue());
						b2 = new BigDecimal(merchantinfo.getPositionT1());
						log.info("随行付----系统剩余可用额度T1:" + b2.doubleValue());
						
						
						
						if (b1.doubleValue() + b3.doubleValue()*100 > b2.doubleValue()) {
							result.put("respCode", "0006");
							result.put("respMsg", "下单失败,代付金额高于剩余额度");
							log.info("随行付**********************代付金额高于剩余额度");
							int i = add(payRequsest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("随行付----添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() < min.doubleValue() * 100) {
							result.put("respCode", "0006");
							result.put("respMsg", "下单失败,代付金额小于代付最小金额");
							log.info("随行付**********************代付金额小于代付最小金额");
							int i = add(payRequsest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("随行付--添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() > max.doubleValue() * 100) {
							result.put("respCode", "0006");
							result.put("respMsg", "下单失败,代付金额大于代付T1最大金额");
							log.info("随行付**********************代付金额大于代付T1最大金额");
							int i = add(payRequsest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("随行付--添加失败订单成功");
							}
							return result;
						}
							surplus = b2.subtract(b1).doubleValue() - b3.doubleValue() * 100;
							merchantinfo.setPositionT1(surplus.toString());
							
							
							int i =add(payRequsest, merchantinfo, result, "200");
							if (i == 1) {
								log.info("随行付--添加代付扣款订单成功！");
							}
						PmsBusinessPos pmsBusinessPos =selectKey(payRequsest.getClientId());
						int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
						if (num == 1) {
							log.info("随行付--扣款成功！！");
						}
						if (i == 1) {
							log.info("随行付--代付订单添加成功");
							List<DF1003Request.PayItems> list =new ArrayList<>();
							payItems.setPayItemId(payRequsest.getPayItemId());
							payItems.setSeqNo("1");
							payItems.setPayAmt(Double.parseDouble(payRequsest.getPayAmt())/100+"");
							payItems.setActNm(payRequsest.getActNm());
							payItems.setActNo(payRequsest.getActNo());
							payItems.setActTyp(payRequsest.getActTyp());
							payItems.setBnkCd(payRequsest.getBnkCd());
							payItems.setBnkNm(payRequsest.getBnkNm());
							payItems.setLbnkNo(payRequsest.getLbnkNo());
							payItems.setLbnkNm(payRequsest.getLbnkNm());
							payItems.setRmk(payRequsest.getRmk());
							payItems.setSmsFlg(payRequsest.getSmsFlg());
							payItems.setTel(payRequsest.getTel());
							payItems.setBankPayPurpose(payRequsest.getBankPayPurpose());
							log.info("随行付--给上游参数:"+JSON.toJSON(payItems));
							list.add(payItems);
							//----------------------------------
							df1003Request.setTotalPayCount("1");
							df1003Request.setPayTyp(payRequsest.getPayTyp());
							df1003Request.setTotalPayAmt(Double.parseDouble(payRequsest.getPayAmt())/100+"");
							df1003Request.setPayItems(list);
							log.info("随行付--给上游表头:"+JSON.toJSON(df1003Request));
							log.info(df1003Request);
							//-------------------------------
							String mercNo =pmsBusinessPos.getBusinessnum();
							log.info("随行付查询上游商户号："+mercNo);
							requsest.setClientId(mercNo);//"600000000001044" SXFUtil.mercNo
							requsest.setReqId(payRequsest.getReqId());
							requsest.setTranCd("DF1003");
							requsest.setVersion("0.0.0.1");
							log.info("随行付--上传上游加密之前参数:"+JsonUtils.toJson(df1003Request));
							try{
								byte[] bs = DESUtils.encrypt(JsonUtils.toJson(df1003Request).getBytes("UTF-8"), "12345678");
								//Base64编码
								String reqDataEncrypt = Base64Utils.encode(bs);
								requsest.setReqData(reqDataEncrypt);
								//String payPubKey =pmsBusinessPos.getKek();//"12345";
								String payPubKey =SXFUtil.PrivateKey; //SXFUtil.mercPrivateKey;payPubKey
								System.out.println(payPubKey);
//								//RSA签名
								requsest.setSign(xdt.dto.sxf.RSAUtils.sign(reqDataEncrypt, payPubKey));
							}catch(Exception e1){
								e1.printStackTrace();
								result.put("respCode","0002");
								result.put("respMsg","向上游加密参数出现异常");
								result.put("msg:0002","向上游加密参数出现异常");
								return result;
							}
							
							try{
								String reqStr=JsonUtils.toJson(requsest);
								log.info("随行付----发送上游之前数据："+JSON.toJSON(reqStr));
								//=====================代付地址
								String url =SXFUtil.payUrl;
								log.info("随行付***给上游发送地址:"+url);
								log.info("随行付***HttpClient ===开始");
								String body=HttpClientUtil.doPost(url, reqStr);
								log.info("随行付***上游返回原始参数:"+JSON.toJSON(body));
								log.info("随行付***HttpClient  ===结束");
								if(body==null || "".equals(body) || "exception".equals(body)){
									payRequsest.setReqId(payRequsest.getReqId()+"/A");
									surplus = surplus+Double.parseDouble(payRequsest.getPayAmt());
									merchantinfo.setPositionT1(surplus.toString());
									ii =add(payRequsest, merchantinfo, result, "00");
									 
									log.info("随行付修改余额1："+num);
									map.put("mercId", payRequsest.getClientId());
									map.put("payMoney",payRequsest.getPayAmt());
									int nus = pmsMerchantInfoDao.updataPayT1(map);
									if(nus==1){
										log.info("随行付***补款成功");
									}
									result.put("respCode","0002");
									result.put("respMsg", "请求上游出现异常："+body);
									return result;
								}
								PayResponse payResponse = JsonUtils.fromJson(body,PayResponse.class);
								log.info("随行付***上游返回解析实体类参数:"+JSON.toJSON(payResponse));
								if(!"000000".equals(payResponse.getResCode())){
									UpdateDaifu(payRequsest.getReqId(), "01");
									payRequsest.setReqId(payRequsest.getReqId()+"/A");
									surplus = surplus+Double.parseDouble(payRequsest.getPayAmt());
									merchantinfo.setPositionT1(surplus.toString());
									ii =add(payRequsest, merchantinfo, result, "00");
									log.info("随行付添加失败订单2："+ii);
									log.info("随行付修改余额2："+num);
									map.put("machId", payRequsest.getClientId());
									map.put("payMoney",payRequsest.getPayAmt());
									int nus = pmsMerchantInfoDao.updataPayT1(map);
									if(nus==1){
										log.info("随行付***补款成功");
									}
									log.info("随行付代付返回状态吗错误");
									result.put("respCode","0001");
									result.put("respMsg","系统错误，代付失败");
									return result;
								}
								String sign = payResponse.getSign();
								String resData = payResponse.getResData();
							
								String payPreKey = SXFUtil.publicPayKey;//mercPrivateKey,payPreKey
								boolean signFlag = sign !=null && StringUtils.isNotBlank(resData) && xdt.dto.sxf.RSAUtils.verify(resData, sign, payPreKey);
								log.info("随行付***结果"+signFlag);
								//if (!signFlag)
									//System.out.println("签名验证失败");//根据实际业务修改
								byte[] base64bs = Base64Utils.decode(resData);
								
								// DES解密
								byte[] debs = DESUtils.decrypt(base64bs, "12345678");
								
								String resDataDecrypt = new String(debs,"UTF-8");
								JSONObject json =JSONObject.parseObject(resDataDecrypt);
								log.info("随行付状态:"+json.getString("payResultList"));
								String s = json.getString("payResultList");
								s =s.replace("[","").replace("]", "");
								JSONObject jsons =JSONObject.parseObject(s);
								log.info(""+jsons.getString("resCd"));
								if(!"00".equals(jsons.getString("resCd"))){
									log.info("随行付代付错误："+jsons.getString("resMsg"));
									payRequsest.setReqId(payRequsest.getReqId()+"/A");
									surplus = surplus+Double.parseDouble(payRequsest.getPayAmt());
									merchantinfo.setPositionT1(surplus.toString());
									ii =add(payRequsest, merchantinfo, result, "00");
									log.info("添加失败订单3："+ii);
									map.put("mercId", payRequsest.getClientId());
									map.put("payMoney",payRequsest.getPayAmt());
									int nus = pmsMerchantInfoDao.updataPayT1(map);
									if(nus==1){
										log.info("随行付***补款成功");
									}
									result.put("respCode","0001");
									result.put("respMsg", jsons.getString("resMsg"));
							        return result;
								}
								UpdateDaifu(payRequsest.getReqId(), "200");
								result.put("respCode","0000");
								result.put("respMsg",jsons.getString("resMsg"));
								result.put("payItemId",payRequsest.getPayItemId());
								result.put("clientId",payRequsest.getClientId());
								result.put("reqId",payRequsest.getReqId());
								result.put("payAmt", payRequsest.getPayAmt());
								ThreadPool.executor(new SxfThread( result, this, pmsBusinessPos,surplus, pmsMerchantInfoDao));
								return result;
							}catch(Exception e1){
								e1.printStackTrace();
								result.put("respCode","0002");
								result.put("respMsg", "请求接口出现异常:"+e1.getMessage());
								return result;
							}
							
						}
					} else {
						throw new RuntimeException("随行付***系统错误----------------当前商户非正式商户");
					}

				} else {
					throw new RuntimeException("随行付***系统错误----------------商户不存在");
				}
				break label104;
			}

		} catch (Exception var43) {
			log.error("随行付*******************************代付错误", var43);
			try {
				throw var43;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	log.info("***********随行付*********************代付------处理完成");
	return result;

	}


	//添加代付订单
	public synchronized int add(PayRequsest payRequsest, PmsMerchantInfo merchantinfo, Map<String, String> result ,String state) throws Exception {
		log.info("进来添加失败余额了");
		String positions = "";
		BigDecimal b1=new BigDecimal("0");//总金额
		int iii=0;
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		b1 =new BigDecimal(payRequsest.getPayAmt());
		
				 model.setMercId(payRequsest.getClientId());
				 model.setCount("1");
				 model.setBatchNo(payRequsest.getReqId());
				 model.setIdentity(payRequsest.getPayItemId());
				 model.setAmount(b1.doubleValue()/100+"");
				 model.setCardno(payRequsest.getActNo());
				 model.setRealname(payRequsest.getActNm());
				 if(payRequsest.getReqId().indexOf("/A")!=-1){
					 model.setPayamount(b1.doubleValue()/100+"");
				 }else{
					 model.setPayamount("-" +b1.doubleValue()/100);
				 }
				 
				 model.setPmsbankno(payRequsest.getLbnkNo());
				 if(payRequsest.getReqId().indexOf("/A")!=-1){
					 model.setTransactionType("代付补款");
				 }else{
					 model.setTransactionType("代付");
				 }
				 model.setPosition(String.valueOf(merchantinfo.getPositionT1()));
				 model.setRemarks("T1");
				 model.setRecordDescription("批次号:" + payRequsest.getReqId()+"订单号："+payRequsest.getPayItemId()+ "错误原因:" + result.get("respMsg"));
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
			public Map<String, String> pSelect(PayRequsest payRequsest,
					Map<String, String> result) throws Exception {
				log.info(JSON.toJSON(payRequsest));
				return null;
			}



			@Override
			public Map<String, String> payCs(PayRequsest payRequsest,
					Map<String, String> result) {
				
				log.info("随行付----下游传送代付参数:"+JSON.toJSON(payRequsest));
				log.info("随行付----查询当前代付订单是否存在");
				PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
				Map<String, String> map =new HashMap<>();
				model.setMercId(payRequsest.getClientId());
				model.setBatchNo(payRequsest.getReqId());
				if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
					result.put("respCode", "0006");
					result.put("respMsg", "下单失败,订单存在");
					log.info("随行付----**********************代付 下单失败:{}");
					log.info("随行付----订单存在");
					return result;
				}
				try {
					label104: {

						log.info("********************随行付-------------根据商户号查询");
						String e = payRequsest.getClientId();
						PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
						merchantinfo.setMercId(e);
						List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
						if (merchantList.size() != 0 && !merchantList.isEmpty()) {
							merchantinfo = (PmsMerchantInfo) merchantList.get(0);
							
							int i =add(payRequsest, merchantinfo, result, "200");
							if (i == 1) {
								log.info("随行付--添加代付扣款订单成功！");
							}
						if (i == 1) {
							LinkedHashMap<String, String> paramsMap =new LinkedHashMap<>();
							paramsMap.put("actNm",payRequsest.getActNm());
							paramsMap.put("actNo",payRequsest.getActNo());
							paramsMap.put("actTyp",payRequsest.getActTyp());
							paramsMap.put("bankPayPurpose","代付");
							paramsMap.put("bnkCd",payRequsest.getBnkCd());
							paramsMap.put("bnkNm",payRequsest.getBnkNm());
							paramsMap.put("clientId","10035036642");
							paramsMap.put("LbnkNm",payRequsest.getLbnkNm());
							paramsMap.put("LbnkNo",payRequsest.getLbnkNo());
							paramsMap.put("payAmt",payRequsest.getPayAmt());
							paramsMap.put("payItemId",payRequsest.getPayItemId());
							paramsMap.put("payTyp",payRequsest.getPayTyp());
							paramsMap.put("reqId",payRequsest.getReqId());
							paramsMap.put("rmk","代付");
							paramsMap.put("smsFlg",payRequsest.getSmsFlg());
							paramsMap.put("tel",payRequsest.getTel());
							// 获取商户秘钥
							String key = "477c5d12a33e44c8b4749f2e22c06a52";
							String paramSrc ="actNm="+payRequsest.getActNm()+"&actNo="+payRequsest.getActNo()+"&actTyp="+payRequsest.getActTyp()+"&bankPayPurpose="+payRequsest.getBankPayPurpose()+"&bnkCd="+payRequsest.getBnkCd()+"&bnkNm="+payRequsest.getBnkNm()+"&clientId=10035036642&lbnkNm="+payRequsest.getLbnkNm()+"&lbnkNo="+payRequsest.getLbnkNo()+"&payAmt="+payRequsest.getPayAmt()+"&payItemId="+payRequsest.getPayItemId()+"&payTyp="+payRequsest.getPayTyp()+"&reqId="+payRequsest.getReqId()+"&rmk=代付&smsFlg="+payRequsest.getSmsFlg()+"&tel="+payRequsest.getTel();//RequestUtils.getParamSrcs(paramsMap);
							String md5 = MD5Utils.sign(paramSrc, key, "UTF-8");
							String content =paramSrc+"&sign="+md5;
							String str =HttpURLConection.httpURLConnectionPOST(BaseUtil.url+"/SXFController/pay.action", content);
							ObjectMapper om = new ObjectMapper();
							result = om.readValue(str, Map.class);
							if("0000".equals(result.get("respCode"))){
								ThreadPool.executor(new SxfThreads(this, payRequsest));
							}else{
								UpdateDaifu(payRequsest.getReqId(), "01");
							}
							} else {
								throw new RuntimeException("随行付***系统错误----------------当前商户非正式商户");
							}

						} else {
							throw new RuntimeException("随行付***系统错误----------------商户不存在");
						}
						break label104;
					}

				} catch (Exception var43) {
					log.error("随行付*******************************代付错误", var43);
					try {
						throw var43;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			log.info("***********随行付*********************代付------处理完成");
			return result;
			}
	
			
			
}

package xdt.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;








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
import xdt.dto.hlb.Disguiser;
import xdt.dto.hlb.HLBRequest;
import xdt.dto.hlb.HLBThread;
import xdt.dto.hlb.HLBUtil;
import xdt.dto.hlb.HttpClientService;
import xdt.dto.hlb.MyBeanUtils;
import xdt.dto.hlb.RSA;
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
import xdt.service.HfQuickPayService;
import xdt.service.IHLBService;
import xdt.service.IPublicTradeVerifyService;
import xdt.util.Constants;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
@Service
public class HLBServiceImpl extends BaseServiceImpl implements IHLBService {

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
	
	@Resource
	private HfQuickPayService payService;
	@Override
	public Map<String, String> cardPay(HLBRequest hlbRequest,
			Map<String, String> result) {
		
		log.info("上传到server层参数:"+JSON.toJSON(hlbRequest));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = hlbRequest.getOrderId(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = hlbRequest.getMerNo();

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
				oriInfo.setMerchantOrderId(hlbRequest.getOrderId());//---------------------------
				oriInfo.setPid( hlbRequest.getMerNo());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoCardPay(hlbRequest, out_trade_no, mercId);
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

									BigDecimal payAmt = new BigDecimal(hlbRequest.getOrderAmount());// 收款金额
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
														String totalAmount = hlbRequest.getOrderAmount(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															
															result =createOrder(hlbRequest, result, appTransInfo);
															
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
	 * 下单
	 * @param hlbReques
	 * @param result
	 * @return
	 */
	public Map<String, String> createOrder(HLBRequest hlbRequest,Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception{
		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = hlbRequest.getOrderId();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(hlbRequest.getOrderId())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + hlbRequest.getOrderId()
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
			appTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
         pmsAppTransInfoDao.update(appTransInfo);
         //获取上游商户号和密钥
         PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());

		System.out.println("下单来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		try {
		map.put("P1_bizType", "QuickPayBankCardPay");
		map.put("P2_customerNumber","C"+pmsBusinessPos.getBusinessnum());
		map.put("P3_userId", hlbRequest.getUserId());//170000000002
		map.put("P4_orderId", hlbRequest.getOrderId());
		map.put("P5_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("P6_payerName",hlbRequest.getPayerName());// URLEncoder.encode("安晓楠","UTF-8")
		map.put("P7_idCardType", "IDCARD");
		map.put("P8_idCardNo", hlbRequest.getIdCardNo());//URLEncoder.encode("130722198710107446","UTF-8")
		map.put("P9_cardNo", hlbRequest.getCardNo());//URLEncoder.encode("5268550479591851","UTF-8")
		map.put("P10_year", hlbRequest.getYear()==null?"":hlbRequest.getYear());//URLEncoder.encode("20","UTF-8")
		map.put("P11_month",hlbRequest.getMonth()==null?"":hlbRequest.getMonth());//URLEncoder.encode("07","UTF-8");
		map.put("P12_cvv2",hlbRequest.getCvv2()==null?"":hlbRequest.getCvv2());//URLEncoder.encode("862","UTF-8")
		map.put("P13_phone",hlbRequest.getPhone());//URLEncoder.encode("15652000669","UTF-8")
		map.put("P14_currency", "CNY");
		map.put("P15_orderAmount",Double.parseDouble(hlbRequest.getOrderAmount())/100+"");
		map.put("P16_goodsName", hlbRequest.getGoodsName());
		map.put("P17_goodsDesc", hlbRequest.getGoodsDesc());
		map.put("P18_terminalType", "IMEI");
		map.put("P19_terminalId", hlbRequest.getTerminalId());
		map.put("P20_orderIp", "127.0.0.1");
		map.put("P21_period", hlbRequest.getPeriod()==null?"1":hlbRequest.getPeriod());
		map.put("P22_periodUnit", hlbRequest.getPeriodUnit()==null?"Day":hlbRequest.getPeriodUnit());
		map.put("P23_serverCallbackUrl", HLBUtil.notifyUrl);
		log.info("签名之前的数据:"+map);
		String key=pmsBusinessPos.getKek();
		 String oriMessage = MyBeanUtils.getSigned(map, null,key);
        log.info("签名原文串：" + oriMessage);
		String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
		map.put("sign", sign);
		Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.url);
		String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		if("0000".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "00");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("userId", hlbRequest.getUserId());
			result.put("respMsg", json.getString("rt3_retMsg"));
			result.put("orderId", json.getString("rt5_orderId"));
			
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("userId", hlbRequest.getUserId());
			result.put("respMsg", json.getString("rt3_retMsg"));
			result.put("orderId", json.getString("rt5_orderId"));
		}
		log.info("下单返回参数:"+JSON.toJSONString(resultMap));
		
		} catch (Exception e) {
			log.info("下单"+e);
		}
		
		return result;
	}
	//获取短信验证码
	@Override
	public Map<String, String> sendValidateCode(HLBRequest hlbRequest,
			Map<String, String> result) {
		System.out.println("获取短信验证码来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		try {
		PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());
		map.put("P1_bizType", "QuickPaySendValidateCode");
		map.put("P2_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
		map.put("P3_orderId", hlbRequest.getOrderId());
		map.put("P4_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("P5_phone",hlbRequest.getPhone());
		log.info("签名之前的数据:"+map);
		String key=pmsBusinessPos.getKek();
		 String oriMessage = MyBeanUtils.getSigned(map, null,key);
        log.info("签名原文串：" + oriMessage);
		String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
		map.put("sign", sign);
		 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.url);
		log.info("获取短信验证码返回参数:"+JSON.toJSONString(resultMap));
		String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		if("0000".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "00");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("phone", json.getString("rt6_phone"));
			result.put("respMsg", json.getString("rt3_retMsg"));
			result.put("orderId", json.getString("rt5_orderId"));
			
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("phone", json.getString("rt6_phone"));
			result.put("respMsg", json.getString("rt3_retMsg"));
			result.put("orderId", json.getString("rt5_orderId"));
		}
		} catch (Exception e) {
			log.info("获取短信验证码"+e);
		}
		
		return result;
	}
	//确认支付
	@Override
	public Map<String, String> confirmPay(HLBRequest hlbRequest,
			Map<String, String> result) {
		System.out.println("确认支付来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		try {
			///////////////----------待定------------------------------
			PmsMerchantInfo merchantinfo  =new PmsMerchantInfo();
			PmsMerchantInfo merchantinfos  =new PmsMerchantInfo();
			merchantinfo.setMercId(hlbRequest.getMerNo());
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			merchantinfo=merchantList.get(0);
			//剩余余额
			Double positionT1 =Double.parseDouble(merchantinfo.getPositionT1());//分
			
			//鉴权手续费
			Double authentication=Double.parseDouble(merchantinfo.getAuthentication())*100;//分
			//扣除手续费剩余钱
			/*if(positionT1-authentication<0){
				result.put("respCode", "01");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("respMsg", "余额不足无法鉴权");
				result.put("orderId", hlbRequest.getOrderId());
				return result;
			}*/
			Double dou=positionT1-authentication;
			merchantinfos.setMercId(merchantinfo.getMercId());
			merchantinfos.setPositionT1(dou.toString());
			/*int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfos);
			if(num!=1){
				result.put("respCode", "01");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("respMsg", "系统异常");
				result.put("orderId", hlbRequest.getOrderId());
				return result;
			}*/
			log.info("合利宝--扣除鉴权手续费成功！！");
			PmsDaifuMerchantInfo model=new PmsDaifuMerchantInfo();
			model.setMercId(hlbRequest.getMerNo());
			model.setBatchNo(hlbRequest.getOrderId());
			model.setIdentity(hlbRequest.getUserId());
			model.setMercId(hlbRequest.getMerNo());
			model.setCardno(hlbRequest.getCardNo());
			model.setRealname(hlbRequest.getPayerName());
			model.setPmsbankno(hlbRequest.getBankUnionCode());
			model.setPayCounter(merchantinfo.getAuthentication());
			model.setPayamount("-"+merchantinfo.getAuthentication());
			model.setPosition(dou+"");
			model.setTransactionType("绑卡鉴权");
			model.setOagentno("100333");
			model.setResponsecode("00");
			/*int i= pmsDaifuMerchantInfoDao.insert(model);
			if(i==1){
				log.info("插入鉴权订单成功！！");
			}*/
		PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());
		map.put("P1_bizType", "QuickPayConfirmPay");
		map.put("P2_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
		map.put("P3_orderId", hlbRequest.getOrderId());
		map.put("P4_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("P5_validateCode", hlbRequest.getValidateCode());
		map.put("P6_orderIp", "127.0.0.1");
		log.info("签名之前的数据:"+map);
		String key=pmsBusinessPos.getKek();
		 String oriMessage = MyBeanUtils.getSigned(map, null,key);
        log.info("签名原文串：" + oriMessage);
		String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
		map.put("sign", sign);
		 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.url);
		log.info("确认支付返回参数:"+JSON.toJSONString(resultMap));
		String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		OriginalOrderInfo originalInfo = null;
		originalInfo = this.payService.getOriginOrderInfo(hlbRequest.getOrderId());
		if("0000".equals(json.getString("rt2_retCode"))){
			if("SUCCESS".equals(json.getString("rt9_orderStatus"))){
				result.put("respCode", "00");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("bindId",  json.getString("rt10_bindId"));
				result.put("userId",  json.getString("rt14_userId"));
				result.put("respMsg", json.getString("rt3_retMsg"));
				result.put("orderId", json.getString("rt5_orderId"));
				update(hlbRequest.getOrderId(), "0", "00",json.getString("rt10_bindId"));
				if("0".equals(hlbRequest.getDataType())){
					int ii =UpdatePmsMerchantInfo(originalInfo);
					if(ii==1){
						log.info("实时填金成功！！");
					}
				}
				
			}else if("DOING".equals(json.getString("rt9_orderStatus"))){
				result.put("respCode", "200");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("bindId",  json.getString("rt10_bindId"));
				result.put("userId",  json.getString("rt14_userId"));
				result.put("respMsg", json.getString("rt3_retMsg"));
				result.put("orderId", json.getString("rt5_orderId"));
				update(hlbRequest.getOrderId(), "200", "200",json.getString("rt10_bindId"));
			}else{
				result.put("respCode", "01");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("bindId",  json.getString("rt10_bindId"));
				result.put("userId",  json.getString("rt14_userId"));
				result.put("respMsg", json.getString("rt3_retMsg"));
				result.put("orderId", json.getString("rt5_orderId"));
				update(hlbRequest.getOrderId(), "1", "02",json.getString("rt10_bindId"));
			}
		}else if("0001".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "200");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("bindId",  json.getString("rt10_bindId"));
			result.put("userId",  json.getString("rt14_userId"));
			result.put("respMsg", json.getString("rt3_retMsg"));
			result.put("orderId", json.getString("rt5_orderId"));
			update(hlbRequest.getOrderId(), "200", "200",json.getString("rt10_bindId"));
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("bindId",  json.getString("rt10_bindId"));
			result.put("userId",  json.getString("rt14_userId"));
			result.put("respMsg", json.getString("rt3_retMsg"));
			result.put("orderId", json.getString("rt5_orderId"));
			update(hlbRequest.getOrderId(), "1", "02",json.getString("rt10_bindId"));
		}
		} catch (Exception e) {
			log.info("确认支付"+e);
		}
		
		return result;
	}
	//鉴权绑卡短信
	@Override
	public Map<String, String> authenticationCardPay(HLBRequest hlbRequest,
			Map<String, String> result) {
		System.out.println("鉴权绑卡短信来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		try {
		PmsMerchantInfo merchantinfo  =new PmsMerchantInfo();
		PmsMerchantInfo merchantinfos  =new PmsMerchantInfo();
		merchantinfo.setMercId(hlbRequest.getMerNo());
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		merchantinfo=merchantList.get(0);
		//剩余余额
		Double positionT1 =Double.parseDouble(merchantinfo.getPositionT1());//分
		
		//鉴权手续费
		Double authentication=Double.parseDouble(merchantinfo.getAuthentication())*100;//分
		//扣除手续费剩余钱
		if(positionT1-authentication<0){
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("respMsg", "余额不足无法鉴权");
			result.put("orderId", hlbRequest.getOrderId());
			return result;
		}
		Double dou=positionT1-authentication;
		merchantinfos.setMercId(merchantinfo.getMercId());
		merchantinfos.setPositionT1(dou.toString());
		int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfos);
		if(num!=1){
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("respMsg", "系统异常");
			result.put("orderId", hlbRequest.getOrderId());
			return result;
		}
		log.info("合利宝--扣除鉴权手续费成功！！");
		PmsDaifuMerchantInfo model=new PmsDaifuMerchantInfo();
		model.setMercId(hlbRequest.getMerNo());
		model.setBatchNo(hlbRequest.getOrderId());
		model.setIdentity(hlbRequest.getUserId());
		model.setMercId(hlbRequest.getMerNo());
		model.setCardno(hlbRequest.getCardNo());
		model.setRealname(hlbRequest.getPayerName());
		model.setPmsbankno(hlbRequest.getBankUnionCode());
		model.setPayCounter(authentication.toString());
		model.setPosition(dou+"");
		model.setTransactionType("绑卡鉴权");
		model.setOagentno("100333");
		model.setResponsecode("00");
		int i= pmsDaifuMerchantInfoDao.insert(model);
		if(i==1){
			log.info("插入鉴权订单成功！！");
		}
		PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());
		map.put("P1_bizType", "QuickPayBindCardValidateCode");
		map.put("P2_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
		map.put("P3_userId", hlbRequest.getUserId());
		map.put("P4_orderId", hlbRequest.getOrderId());
		map.put("P5_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("P6_cardNo", hlbRequest.getCardNo());
		map.put("P7_phone", hlbRequest.getPhone());
		log.info("签名之前的数据:"+map);
		String key=pmsBusinessPos.getKek();
		 String oriMessage = MyBeanUtils.getSigned(map, null,key);
        log.info("签名原文串：" + oriMessage);
		String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
		map.put("sign", sign);
		 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.url);
		log.info("鉴权绑卡短信返回参数:"+JSON.toJSONString(resultMap));
		String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		if("0000".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "00");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("userId", hlbRequest.getUserId());
			result.put("phone", json.getString("rt6_phone"));
			result.put("respMsg", json.getString("rt3_retMsg"));
			result.put("orderId", json.getString("rt5_orderId"));
			
		}else if("0001".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "200");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("userId", hlbRequest.getUserId());
			result.put("phone", json.getString("rt6_phone"));
			result.put("respMsg", json.getString("rt3_retMsg"));
			result.put("orderId", json.getString("rt5_orderId"));
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("userId", hlbRequest.getUserId());
			result.put("phone", json.getString("rt6_phone").toString());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt5_orderId").toString());
		}
		} catch (Exception e) {
			log.info("鉴权绑卡短信"+e);
		}
		return result;
	}
	//鉴权绑卡
	@Override
	public Map<String, String> authenticationCard(HLBRequest hlbRequest,
			Map<String, String> result) {
		System.out.println("鉴权绑卡来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		try {
		PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());
		map.put("P1_bizType", "QuickPayBindCard");
		map.put("P2_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
		map.put("P3_userId", hlbRequest.getUserId());
		map.put("P4_orderId",hlbRequest.getOrderId());
		map.put("P5_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("P6_payerName", hlbRequest.getPayerName());
		map.put("P7_idCardType", "IDCARD");
		map.put("P8_idCardNo", hlbRequest.getIdCardNo());
		map.put("P9_cardNo", hlbRequest.getCardNo());
		map.put("P10_year", hlbRequest.getYear());
		map.put("P11_month", hlbRequest.getMonth());
		map.put("P12_cvv2", hlbRequest.getCvv2());
		map.put("P13_phone",hlbRequest.getPhone());
		map.put("P14_validateCode", hlbRequest.getValidateCode());//短信验证码
		log.info("签名之前的数据:"+map);
		String key=pmsBusinessPos.getKek();
		 String oriMessage = MyBeanUtils.getSigned(map, null,key);
        log.info("签名原文串：" + oriMessage);
		String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
		map.put("sign", sign);
		 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.url);
		log.info("鉴权绑卡返回参数:"+JSON.toJSONString(resultMap));
		String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		if("0000".equals( json.getString("rt2_retCode"))){
			if("SUCCESS".equals( json.getString("rt7_bindStatus").toString())){
				result.put("respCode", "00");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("phone",   hlbRequest.getPhone());
				result.put("bindId",  json.getString("rt10_bindId").toString());
				result.put("userId",  json.getString("rt5_userId").toString());
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt6_orderId").toString());
			}else if("DOING".equals( json.getString("rt7_bindStatus").toString())){
				result.put("respCode", "200");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("phone",   hlbRequest.getPhone());
				result.put("bindId",  json.getString("rt10_bindId").toString());
				result.put("userId",  json.getString("rt5_userId").toString());
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt6_orderId").toString());
			}else{
				result.put("respCode", "01");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("phone",   hlbRequest.getPhone());
				result.put("bindId",  json.getString("rt10_bindId").toString());
				result.put("userId",  json.getString("rt5_userId").toString());
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt6_orderId").toString());
			}
		}else if("0001".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "200");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("phone",   hlbRequest.getPhone());
			result.put("bindId",  json.getString("rt10_bindId").toString());
			result.put("userId",  json.getString("rt5_userId").toString());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt6_orderId").toString());
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("phone",   hlbRequest.getPhone());
			result.put("bindId",  json.getString("rt10_bindId").toString());
			result.put("userId",  json.getString("rt5_userId").toString());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt6_orderId").toString());
		}
		
		} catch (Exception e) {
			log.info("鉴权绑卡"+e);
		}
		return result;
	}
	//绑卡支付短信    403bf3a22ba44a34a6c850751919e04a
	@Override
	public Map<String, String> paymentCardPay(HLBRequest hlbRequest,
			Map<String, String> result) {
		log.info("上传到server层参数:"+JSON.toJSON(hlbRequest));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = hlbRequest.getOrderId(); // 10业务号2业务细; 订单号
																
		log.info("根据商户号查询");
		String mercId = hlbRequest.getMerNo();

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
				oriInfo.setMerchantOrderId(hlbRequest.getOrderId());//---------------------------
				oriInfo.setPid( hlbRequest.getMerNo());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoCardPay(hlbRequest, out_trade_no, mercId);
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

									BigDecimal payAmt = new BigDecimal(hlbRequest.getOrderAmount());// 收款金额
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
														String totalAmount = hlbRequest.getOrderAmount(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															
															result =mentCard(hlbRequest, result, appTransInfo);
															
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
	//绑卡支付
	@Override
	public Map<String, String> paymentCard(HLBRequest hlbRequest,
			Map<String, String> result) {
		try {
			PmsMerchantInfo merchantinfo  =new PmsMerchantInfo();
			PmsMerchantInfo merchantinfos  =new PmsMerchantInfo();
			merchantinfo.setMercId(hlbRequest.getMerNo());
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			merchantinfo=merchantList.get(0);
			//剩余余额
			Double positionT1 =Double.parseDouble(merchantinfo.getPositionT1());//分
			
			//鉴权手续费
			Double authentication=Double.parseDouble(merchantinfo.getAuthentication())*100;//分
			//扣除手续费剩余钱
		/*	if(positionT1-authentication<0){
				result.put("respCode", "01");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("respMsg", "余额不足无法鉴权");
				result.put("orderId", hlbRequest.getOrderId());
				return result;
			}
			Double dou=positionT1-authentication;
			merchantinfos.setMercId(merchantinfo.getMercId());
			merchantinfos.setPositionT1(dou.toString());
			int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfos);
			if(num!=1){
				result.put("respCode", "01");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("respMsg", "系统异常");
				result.put("orderId", hlbRequest.getOrderId());
				return result;
			}
			log.info("合利宝--扣除鉴权手续费成功！！");
			PmsDaifuMerchantInfo model=new PmsDaifuMerchantInfo();
			model.setMercId(hlbRequest.getMerNo());
			model.setBatchNo(hlbRequest.getOrderId());
			model.setIdentity(hlbRequest.getUserId());
			model.setMercId(hlbRequest.getMerNo());
			model.setCardno(hlbRequest.getCardNo());
			model.setRealname(hlbRequest.getPayerName());
			model.setPmsbankno(hlbRequest.getBankUnionCode());
			model.setPayCounter(merchantinfo.getAuthentication());
			model.setPayamount(merchantinfo.getAuthentication());
			model.setPosition(dou+"");
			model.setTransactionType("绑卡鉴权");
			model.setOagentno("100333");
			model.setResponsecode("00");
			int i= pmsDaifuMerchantInfoDao.insert(model);
			if(i==1){
				log.info("插入鉴权订单成功！！");
			}*/
		System.out.println("绑卡支付来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());
		map.put("P1_bizType", "QuickPayBindPay");
		map.put("P2_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
		map.put("P3_bindId", hlbRequest.getBindId());//403bf3a22ba44a34a6c850751919e04a
		map.put("P4_userId", hlbRequest.getUserId());
		map.put("P5_orderId", hlbRequest.getOrderId());
		map.put("P6_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("P7_currency", "CNY");
		map.put("P8_orderAmount", Double.parseDouble(hlbRequest.getOrderAmount())/100+"");
		map.put("P9_goodsName", hlbRequest.getGoodsName());
		map.put("P10_goodsDesc", hlbRequest.getGoodsDesc());
		map.put("P11_terminalType", "IMEI");
		map.put("P12_terminalId", hlbRequest.getTerminalId());
		map.put("P13_orderIp", "127.0.0.1");
		map.put("P14_period", hlbRequest.getPeriod()==null?"1":hlbRequest.getPeriod());
		map.put("P15_periodUnit", hlbRequest.getPeriodUnit()==null?"hour":hlbRequest.getPeriodUnit());
		map.put("P16_serverCallbackUrl", HLBUtil.notifyUrl);
		/*map.put("P18_isIntegral", "");*/
		log.info("签名之前的数据:"+map);
		String key=pmsBusinessPos.getKek();
		 String oriMessage = MyBeanUtils.getSigned(map, null,key);
        log.info("签名原文串：" + oriMessage);
		String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
		map.put("P17_validateCode", hlbRequest.getValidateCode());//短信验证码
		map.put("sign", sign);
		 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.url);
		log.info("绑卡支付返回参数:"+JSON.toJSONString(resultMap));
		String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		OriginalOrderInfo originalInfo = null;
		originalInfo = this.payService.getOriginOrderInfo(hlbRequest.getOrderId());
		if("0000".equals(json.getString("rt2_retCode"))){
			if("SUCCESS".equals(json.getString("rt9_orderStatus").toString())){
				result.put("respCode", "00");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("userId", hlbRequest.getUserId());
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt5_orderId").toString());
				result.put("orderAmount", Double.parseDouble(json.getString("rt8_orderAmount"))*100+"");
				update(hlbRequest.getOrderId(), "0", "00",json.getString("rt10_bindId"));
				if("0".equals(hlbRequest.getDataType())){
					int ii =UpdatePmsMerchantInfo(originalInfo);
					if(ii==1){
						log.info("实时填金成功！！");
					}
				}
			}else if("DOING".equals(json.getString("rt9_orderStatus").toString())){
				result.put("respCode", "200");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("userId", hlbRequest.getUserId());
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt5_orderId").toString());
				result.put("orderAmount",  Double.parseDouble(json.getString("rt8_orderAmount"))*100+"");
				update(hlbRequest.getOrderId(), "200", "200",json.getString("rt10_bindId"));
			}else {
				result.put("respCode", "01");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("userId", hlbRequest.getUserId());
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt5_orderId").toString());
				result.put("orderAmount",  Double.parseDouble(json.getString("rt8_orderAmount"))*100+"");
				update(hlbRequest.getOrderId(), "1", "01",json.getString("rt10_bindId"));
			}
		}else if("0001".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "200");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("userId", hlbRequest.getUserId());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt5_orderId").toString());
			result.put("orderAmount",  Double.parseDouble(json.getString("rt8_orderAmount"))*100+"");
			update(hlbRequest.getOrderId(), "200", "200",json.getString("rt10_bindId"));
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("userId", hlbRequest.getUserId());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt5_orderId").toString());
			result.put("orderAmount",  Double.parseDouble(json.getString("rt8_orderAmount"))*100+"");
			update(hlbRequest.getOrderId(), "1", "01",json.getString("rt10_bindId"));
		}
		
		} catch (Exception e) {
			log.info("绑卡支付"+e);
		}
		
		return result;
	}
	public Map<String, String> mentCard(HLBRequest hlbRequest,
			Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception
	{
		// 查看当前交易是否已经生成了流水表
				PospTransInfo pospTransInfo = null;
				// 流水表是否需要更新的标记 0 insert，1：update
				int insertOrUpdateFlag = 0;
				log.info("***************进入payHandle5-14-3***************");
				// 生成上送流水号
				String transOrderId = hlbRequest.getOrderId();
				log.info("***************进入payHandle5-15***************");
				if ((pospTransInfo = pospTransInfoDAO
						.searchByOrderId(hlbRequest.getOrderId())) != null) {
					// 已经存在，修改流水号，设置pospsn为空
					log.info("订单号：" + hlbRequest.getOrderId()
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
					appTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
					appTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
		         pmsAppTransInfoDao.update(appTransInfo);
		         //获取上游商户号和密钥
		PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());

		
		System.out.println("绑卡支付短信来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		try {
		map.put("P1_bizType", "QuickPayBindPayValidateCode");
		map.put("P2_customerNumber","C"+pmsBusinessPos.getBusinessnum());//HLBUtil.merchantNo
		map.put("P3_bindId", hlbRequest.getBindId());//403bf3a22ba44a34a6c850751919e04a
		map.put("P4_userId", hlbRequest.getUserId());
		map.put("P5_orderId", hlbRequest.getOrderId());
		map.put("P6_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("P7_currency", "CNY");
		map.put("P8_orderAmount",Double.parseDouble( hlbRequest.getOrderAmount())/100+"");
		map.put("P9_phone", hlbRequest.getPhone());
		log.info("签名之前的数据:"+map);
		String key=HLBUtil.key;
		 String oriMessage = MyBeanUtils.getSigned(map, null,key);
        log.info("签名原文串：" + oriMessage);
		String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
		map.put("sign", sign);
		 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.url);
		log.info("绑卡支付短信返回参数:"+JSON.toJSONString(resultMap));
		String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		if("0000".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "00");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("userId", hlbRequest.getUserId());
			result.put("phone", json.getString("rt6_phone").toString());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt5_orderId").toString());
			
		}else if("0001".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "200");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("userId", hlbRequest.getUserId());
			result.put("phone", json.getString("rt6_phone").toString());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt5_orderId").toString());
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("userId", hlbRequest.getUserId());
			result.put("phone", json.getString("rt6_phone").toString());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt5_orderId").toString());
			update(hlbRequest.getOrderId(), "1", "02","");
		}
		} catch (Exception e) {
			log.info("绑卡支付短信"+e);
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
	private int saveOriginAlInfoCardPay(HLBRequest hlbRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(hlbRequest.getOrderId());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		info.setByUser(hlbRequest.getUserId());
		//想要传服务器要改实体
		info.setBgUrl(hlbRequest.getUrl());
		Double amt = Double.parseDouble(hlbRequest.getOrderAmount());// 单位分
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
	public void update(String orderId,String status,String responsecode ,String bindId) throws Exception {
		log.info("返回的参数："+orderId);
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
		pmsAppTransInfo.setStatus(status);
		pmsAppTransInfo.setThirdPartResultCode(status);
		pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
		// 修改订单
		int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
		if (updateAppTrans == 1) {
			// log.info("修改余额");
			// 修改余额
			log.info("订单表信息：" + pmsAppTransInfo);
			// 更新流水表
			pospTransInfo.setResponsecode(responsecode);
			if(orderId!=null&& orderId!=""){
				pospTransInfo.setPospsn(orderId);
			}
			pospTransInfo.setUniqueKey(bindId);
			log.info("更新流水");
			log.info("流水表信息：" + pospTransInfo);
			pospTransInfoDAO.updateByOrderId(pospTransInfo);

	}
	}
	@Override
	public synchronized Map<String, String> pay(HLBRequest hlbRequest,
			Map<String, String> result) {
		
		if("cj009".equals(hlbRequest.getType())){
			PmsDaifuMerchantInfo model1=new PmsDaifuMerchantInfo();
			model1.setIdentity(hlbRequest.getUserId()+"-A");
			PmsDaifuMerchantInfo models =pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model1);
			if(models==null||!"00".equals(models.getResponsecode())){
				result.put("respCode", "01");
				result.put("respMsg", "请先绑卡");
				result.put("merNo", hlbRequest.getMerNo());
				result.put("orderId", hlbRequest.getOrderId());
				result.put("type", hlbRequest.getType());
				return result;
			}
			hlbRequest.setCardNo(models.getCardno());
			hlbRequest.setPayerName(models.getRealname());
			hlbRequest.setBankUnionCode(models.getPmsbankno());
		}
		
		log.info("合利宝----下游传送代付参数:"+JSON.toJSON(hlbRequest));
		BigDecimal b1=new BigDecimal("0");// 下游上传的总金额
		BigDecimal b2=new BigDecimal("0");// 系统代付余额
		BigDecimal b3=new BigDecimal("0");// 单笔交易总手续费
		BigDecimal min=new BigDecimal("0");// 代付最小金额
		BigDecimal max=new BigDecimal("0");// 代付最大金额
		Double surplus;// 代付剩余金额
		log.info("合利宝----查询当前代付订单是否存在");
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		Map<String, String> map =new HashMap<>();
		Map<String, String> maps=new HashMap<>();//填金
		model.setMercId(hlbRequest.getMerNo());
		model.setBatchNo(hlbRequest.getOrderId());
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
			result.put("respCode", "0006");
			result.put("respMsg", "下单失败,订单存在");
			log.info("合利宝----**********************代付 下单失败:{}");
			log.info("合利宝----订单存在");
			return result;
		}
		try {
			label104: {

				log.info("********************合利宝-------------根据商户号查询");
				String e = hlbRequest.getMerNo();
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
					log.info("***********合利宝*************商户信息:" + merchantinfo);
					if (StringUtils.isBlank(oAgentNo)) {
						throw new RuntimeException("系统错误----------------o单编号为空");
					}

					if ("60".equals(merchantinfo.getMercSts())) {
						// 判断交易类型
						log.info("***********合利宝*************实际金额");
						// 分
						String payAmt= hlbRequest.getOrderAmount();
						b1 =new BigDecimal(payAmt);
						
						System.out.println("参数:"+b1.doubleValue());
						log.info("***********合利宝*************校验欧单金额限制");
						log.info("合利宝----下游上传代付总金额:" + b1.doubleValue());
						b3 = new BigDecimal(merchantinfo.getPoundage());
						log.info("合利宝----系统商户代付单笔手续费:" + b3.doubleValue());
						min = new BigDecimal(merchantinfo.getMinDaiFu());
						log.info("合利宝----系统代付最小金额:" + min.doubleValue());
						max = new BigDecimal(merchantinfo.getMaxDaiFu());
						log.info("合利宝----系统代付最大金额:" + max.doubleValue());
						String type="T1";
						if("0".equals(hlbRequest.getDataType())){
							b2 = new BigDecimal(merchantinfo.getPosition());
							log.info("合利宝----系统剩余可用额度:" + b2.doubleValue());
							type="D0";
						}else if("1".equals(hlbRequest.getDataType())){
							b2 = new BigDecimal(merchantinfo.getPositionT1());
							log.info("合利宝----系统剩余可用额度:" + b2.doubleValue());
							
						}
						
						if (b1.doubleValue() + b3.doubleValue()*100 > b2.doubleValue()) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额高于剩余额度");
							log.info("合利宝**********************代付金额高于剩余额度");
							int i = add(hlbRequest, merchantinfo, result,"01",type);
							if (i == 1) {
								log.info("合利宝----添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() < min.doubleValue() * 100) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额小于代付最小金额");
							log.info("合利宝**********************代付金额小于代付最小金额");
							int i = add(hlbRequest, merchantinfo, result,"01",type);
							if (i == 1) {
								log.info("合利宝--添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() > max.doubleValue() * 100) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额大于代付最大金额");
							log.info("合利宝**********************代付金额大于代付最大金额");
							int i = add(hlbRequest, merchantinfo, result,"01",type);
							if (i == 1) {
								log.info("合利宝--添加失败订单成功");
							}
							return result;
						}
							//surplus = b2.subtract(b1).doubleValue() - b3.doubleValue() * 100;
							//这里改过还没测试-------------
							Map<String, String> mapPay=new HashMap<>();
							mapPay.put("machId", hlbRequest.getMerNo());
							mapPay.put("payMoney", hlbRequest.getOrderAmount());
							int num=0;
							if("0".equals(hlbRequest.getDataType())){
								num =pmsMerchantInfoDao.updataD0(mapPay);
							}else if("1".equals(hlbRequest.getDataType())){
								num =pmsMerchantInfoDao.updataT1(mapPay);
							}
							//--------------------
							if (num != 1) {
								log.info("恒明--扣款失败！！");
								result.put("respCode", "02");
								result.put("respMsg", "代付失败");
								return result;
							}
							int i =add(hlbRequest, merchantinfo, result, "200",type);
							if (i == 1) {
								log.info("合利宝--添加代付扣款订单成功！");
							}
						
						if (i == 1) {
							log.info("合利宝--代付订单添加成功");
							if("cj009".equals(hlbRequest.getType())){
								result =settlementCardWithdraw(hlbRequest, result);
							}else{
								result =creditCardWithdraw(hlbRequest, result);
							}
							if("00".equals(result.get("respCode"))){
								ThreadPool.executor(new HLBThread(this, hlbRequest, pmsMerchantInfoDao, merchantinfo));
							}else{
								UpdateDaifu(hlbRequest.getOrderId(), "01");
								maps.put("payMoney",hlbRequest.getOrderAmount());
				     			maps.put("machId", hlbRequest.getMerNo());
				     			int nus =0;
				     			if("0".equals(hlbRequest.getDataType())){
				     				nus = pmsMerchantInfoDao.updataPay(maps);
								}else if("1".equals(hlbRequest.getDataType())){
									nus = pmsMerchantInfoDao.updataPayT1(maps);
								}
								if(nus==1){
									log.info("合利宝***补款成功");
									//这里改过还没测试-------------
									PmsMerchantInfo info= select(hlbRequest.getMerNo());
									//surplus = surplus+Double.parseDouble(hlbRequest.getOrderAmount());
									if("0".equals(hlbRequest.getDataType())){
										merchantinfo.setPosition(info.getPosition());
									}else if("1".equals(hlbRequest.getDataType())){
										merchantinfo.setPositionT1(info.getPositionT1());
									}
									//-----------------------
									hlbRequest.setOrderId(hlbRequest.getOrderId()+"/A");
									int id =add(hlbRequest, merchantinfo, result, "00",type);
									if(id==1){
										log.info("合利宝代付补单成功");
									}
								}
							}
							  
						}
					} else {
						throw new RuntimeException("合利宝***系统错误----------------当前商户非正式商户");
					}

				} else {
					throw new RuntimeException("合利宝***系统错误----------------商户不存在");
				}
				break label104;
			}

		} catch (Exception var43) {
			log.error("合利宝*******************************代付错误", var43);
			try {
				throw var43;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	log.info("***********合利宝*********************代付------处理完成");
	return result;
	}
	
	public synchronized Map<String, String> settlementCardBind(HLBRequest hlbRequest,Map<String, String> result){
	
		if(hlbRequest.getUserId()!=null){
			PmsDaifuMerchantInfo model=new PmsDaifuMerchantInfo();
			model.setIdentity(hlbRequest.getUserId()+"-A");
			PmsDaifuMerchantInfo models =pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model);
			if(models!=null&&"00".equals(models.getResponsecode())&&"代付绑卡".equals(models.getTransactionType())){
				result.put("respCode", "00");
				result.put("respMsg", "成功");
				result.put("merNo", models.getMercId());
				result.put("orderId", models.getBatchNo());
				result.put("type", hlbRequest.getType());
				return result;
			}else{
				
				PmsMerchantInfo merchantinfo  =new PmsMerchantInfo();
				PmsMerchantInfo merchantinfos  =new PmsMerchantInfo();
				merchantinfo.setMercId(hlbRequest.getMerNo());
				List<PmsMerchantInfo> merchantList;
				try {
					merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
				
				merchantinfo=merchantList.get(0);
				//剩余余额
				Double position=0.0;
				Double authentication=Double.parseDouble(merchantinfo.getAuthentication())*100;//分
				//鉴权手续费
				if("1".equals(hlbRequest.getDataType())){
				 position =Double.parseDouble(merchantinfo.getPositionT1());//分
				 
				}else if("0".equals(hlbRequest.getDataType())) {
				 position =Double.parseDouble(merchantinfo.getPosition());//分
				}
				//扣除手续费剩余钱
				if(position-authentication<0){
					result.put("respCode", "01");
					result.put("type", hlbRequest.getType());
					result.put("merNo", hlbRequest.getMerNo());
					result.put("respMsg", "余额不足无法鉴权");
					result.put("orderId", hlbRequest.getOrderId());
					return result;
				}
				Double dou=position-authentication;
				merchantinfos.setMercId(merchantinfo.getMercId());
				if("1".equals(hlbRequest.getDataType())){
					 merchantinfos.setPositionT1(dou.toString());
					}else if("0".equals(hlbRequest.getDataType())) {
						merchantinfos.setPosition(dou.toString());
					}
				int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfos);
				if(num!=1){
					result.put("respCode", "01");
					result.put("type", hlbRequest.getType());
					result.put("merNo", hlbRequest.getMerNo());
					result.put("respMsg", "系统异常");
					result.put("orderId", hlbRequest.getOrderId());
					return result;
				}
				log.info("合利宝--扣除鉴权手续费成功！！");
				PmsDaifuMerchantInfo modell=new PmsDaifuMerchantInfo();
				modell.setMercId(hlbRequest.getMerNo());
				modell.setBatchNo(hlbRequest.getOrderId());
				modell.setIdentity(hlbRequest.getUserId());
				modell.setMercId(hlbRequest.getMerNo());
				modell.setCardno(hlbRequest.getCardNo());
				modell.setRealname(hlbRequest.getPayerName());
				modell.setPmsbankno(hlbRequest.getBankUnionCode());
				modell.setPayCounter(merchantinfo.getAuthentication());
				modell.setPayamount(merchantinfo.getAuthentication());
				modell.setPosition(dou+"");
				modell.setTransactionType("绑卡鉴权");
				modell.setOagentno("100333");
				modell.setResponsecode("00");
				int i= pmsDaifuMerchantInfoDao.insert(modell);
				if(i==1){
					log.info("插入鉴权订单成功！！");
				}
				
				System.out.println("绑结算卡来了！");
				LinkedHashMap<String, String> map =new LinkedHashMap<>();
				PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());
					
				map.put("P1_bizType", "SettlementCardBind");
				map.put("P2_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
				map.put("P3_userId", hlbRequest.getUserId());
				map.put("P4_orderId", hlbRequest.getOrderId());
				map.put("P5_payerName", hlbRequest.getPayerName());
				map.put("P6_idCardType", "IDCARD");
				map.put("P7_idCardNo", hlbRequest.getIdCardNo());
				map.put("P8_cardNo", hlbRequest.getCardNo());
				map.put("P9_phone", hlbRequest.getPhone());
				map.put("P10_bankUnionCode", hlbRequest.getBankUnionCode());
				log.info("签名之前的数据:"+map);
				String key=pmsBusinessPos.getKek();
				 String oriMessage = MyBeanUtils.getSigned(map, null,key);
		        log.info("签名原文串：" + oriMessage);
				String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
				map.put("sign", sign);
				map.put("P11_operateType", "ADD");
				 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.url);
				log.info("绑结算卡返回参数:"+JSON.toJSONString(resultMap));
				String s =resultMap.get("response").toString();
				System.out.println(s);
				JSONObject json =JSONObject.parseObject(s);
				if("0000".equals(json.getString("rt2_retCode"))){
					if("SUCCESS".equals(json.getString("rt7_bindStatus"))){
						result.put("respCode", "00");
						result.put("type", hlbRequest.getType());
						result.put("merNo", hlbRequest.getMerNo());
						result.put("phone", hlbRequest.getPhone());
						result.put("respMsg", json.getString("rt3_retMsg").toString());
						result.put("orderId", json.getString("rt6_orderId").toString());
						model.setResponsecode("00");
					}else if("DOING".equals(json.getString("rt7_bindStatus"))){
						result.put("respCode", "200");
						result.put("type", hlbRequest.getType());
						result.put("merNo", hlbRequest.getMerNo());
						result.put("phone", hlbRequest.getPhone());
						result.put("respMsg", json.getString("rt3_retMsg").toString());
						result.put("orderId", json.getString("rt6_orderId").toString());
						model.setResponsecode("200");
					}else{
						result.put("respCode", "01");
						result.put("type", hlbRequest.getType());
						result.put("merNo", hlbRequest.getMerNo());
						result.put("phone", hlbRequest.getPhone());
						result.put("respMsg", json.getString("rt3_retMsg").toString());
						result.put("orderId", json.getString("rt5_orderId").toString());
						model.setResponsecode("01");
					}
					
				}else if("0001".equals(json.getString("rt2_retCode"))){
					result.put("respCode", "200");
					result.put("type", hlbRequest.getType());
					result.put("merNo", hlbRequest.getMerNo());
					result.put("phone", hlbRequest.getPhone());
					result.put("respMsg", json.getString("rt3_retMsg").toString());
					result.put("orderId", json.getString("rt6_orderId").toString());
					model.setResponsecode("200");
				}else{
					result.put("respCode", "01");
					result.put("type", hlbRequest.getType());
					result.put("merNo", hlbRequest.getMerNo());
					result.put("phone", hlbRequest.getPhone());
					result.put("respMsg", json.getString("rt3_retMsg").toString());
					result.put("orderId", json.getString("rt5_orderId").toString());
					update(hlbRequest.getOrderId(), "1", "02","");
					model.setResponsecode("01");
				}
				model.setBatchNo(hlbRequest.getOrderId()+"-B");
				model.setIdentity(hlbRequest.getUserId()+"-A");
				model.setMercId(hlbRequest.getMerNo());
				model.setCardno(hlbRequest.getCardNo());
				model.setRealname(hlbRequest.getPayerName());
				model.setPmsbankno(hlbRequest.getBankUnionCode());
				model.setTransactionType("代付绑卡");
				model.setOagentno("100333");
				pmsDaifuMerchantInfoDao.insert(model);
				} catch (Exception e) {
					log.info("绑结算卡短信"+e);
				}
			}
		}
		return result;
	}
	
	public Map<String, String> settlementCardWithdraw(HLBRequest hlbRequest,Map<String, String> result){
		System.out.println("结算(借记卡)卡提现来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());
		try {
		map.put("P1_bizType", "SettlementCardWithdraw");
		map.put("P2_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
		map.put("P3_userId", hlbRequest.getUserId());
		map.put("P4_orderId", hlbRequest.getOrderId());
		map.put("P5_amount", Double.parseDouble(hlbRequest.getOrderAmount())/100+"");
		map.put("P6_feeType", "PAYER");
		map.put("P7_summary", "代付");
		log.info("签名之前的数据:"+map);
		String key=HLBUtil.payKey;
        String oriMessage = MyBeanUtils.getSigned(map, null,"");
        oriMessage = oriMessage.substring(0, oriMessage.lastIndexOf("&"));
        log.info("签名原文串：" + oriMessage);
        String sign = RSA.sign(oriMessage, RSA.getPrivateKey(key));
		 //String oriMessage = MyBeanUtils.getSigned(map, null,key);
        
		//String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
		map.put("sign", sign);
		 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.payUrl);
		log.info("算卡提现返回参数:"+JSON.toJSONString(resultMap));
		String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		if("0000".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "00");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("userId", hlbRequest.getUserId());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt6_orderId").toString());
		}else if("0001".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "200");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("userId", hlbRequest.getUserId());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt6_orderId").toString());
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("userId", hlbRequest.getUserId());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt6_orderId").toString());
		}
		} catch (Exception e) {
			log.info("结算卡提现短信"+e);
		}
		return result;
	}
	public Map<String, String> creditCardWithdraw(HLBRequest hlbRequest,Map<String, String> result){
		System.out.println("结算(信用卡)卡提现来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());
		try {
		map.put("P1_bizType", "CreditCardRepayment");
		map.put("P2_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
		map.put("P3_userId", hlbRequest.getUserId());
		map.put("P4_bindId", hlbRequest.getBindId());
		map.put("P5_orderId", hlbRequest.getOrderId());
		map.put("P6_timestamp",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("P7_currency", "CNY");
		map.put("P8_orderAmount", Double.parseDouble(hlbRequest.getOrderAmount())/100+"");
		map.put("P9_feeType", "PAYER");
		map.put("P10_summary", "代付");
		log.info("签名之前的数据:"+map);
		String key=HLBUtil.payKey;
        String oriMessage = MyBeanUtils.getSigned(map, null,"");
        oriMessage = oriMessage.substring(0, oriMessage.lastIndexOf("&"));
        log.info("签名原文串：" + oriMessage);
        String sign = RSA.sign(oriMessage, RSA.getPrivateKey(key));
		 //String oriMessage = MyBeanUtils.getSigned(map, null,key);
        
		//String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
		map.put("sign", sign);
		 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.payUrl);
		log.info("算卡提现返回参数:"+JSON.toJSONString(resultMap));
		String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		if("0000".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "00");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("userId", hlbRequest.getUserId());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt6_orderId").toString());
		}else if("0001".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "200");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("userId", hlbRequest.getUserId());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt6_orderId").toString());
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("userId", hlbRequest.getUserId());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt6_orderId").toString());
		}
		} catch (Exception e) {
			log.info("结算卡提现短信"+e);
		}
		return result;
	}
	
	public Map<String, String> settlementCardQuery(HLBRequest hlbRequest,Map<String, String> result){
		System.out.println("算卡查询来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		try {
		PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());	
		map.put("P1_bizType", "SettlementCardQuery");
		map.put("P2_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
		map.put("P3_userId", hlbRequest.getUserId());
		map.put("P4_orderId", hlbRequest.getOrderId());
		map.put("P5_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		log.info("签名之前的数据:"+map);
		String key=pmsBusinessPos.getKek();
		 String oriMessage = MyBeanUtils.getSigned(map, null,key);
        log.info("签名原文串：" + oriMessage);
		String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
		map.put("sign", sign);
		 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.url);
		log.info("算卡查询返回参数:"+JSON.toJSONString(resultMap));
		/*String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		if("0000".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "00");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("phone", json.getString("rt6_phone").toString());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt5_orderId").toString());
			update(hlbRequest.getOrderId(), "0", "00");
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("orderAmount", hlbRequest.getOrderAmount());
			result.put("phone", json.getString("rt6_phone").toString());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt5_orderId").toString());
			update(hlbRequest.getOrderId(), "1", "02");
		}*/
		} catch (Exception e) {
			log.info("算卡查询"+e);
		}
		return result;
	}
	
	public Map<String, String> transferQuery(HLBRequest hlbRequest,Map<String, String> result){
		
		System.out.println("代付结果查询来了！");
		LinkedHashMap<String, String> map =new LinkedHashMap<>();
		try {
		PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());		
		map.put("P1_bizType", "TransferQuery");
		map.put("P2_orderId", hlbRequest.getOrderId());
		map.put("P3_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
		log.info("签名之前的数据:"+map);
		String key=HLBUtil.payKey;
		/* String oriMessage = MyBeanUtils.getSigned(map, null,key);
        log.info("签名原文串：" + oriMessage);
		String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");*/
		String oriMessage = MyBeanUtils.getSigned(map, null,"");
        oriMessage = oriMessage.substring(0, oriMessage.lastIndexOf("&"));
        log.info("签名原文串：" + oriMessage);
        String sign = RSA.sign(oriMessage, RSA.getPrivateKey(key));
		map.put("sign", sign);
		 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.payUrl);
		log.info("算卡查询返回参数:"+JSON.toJSONString(resultMap));
		String s =resultMap.get("response").toString();
		System.out.println(s);
		JSONObject json =JSONObject.parseObject(s);
		if("0000".equals(json.getString("rt2_retCode"))){
			if("SUCCESS".equals(json.getString("rt7_orderStatus"))){
				result.put("respCode", "00");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt5_orderId").toString());
				UpdateDaifu(json.getString("rt5_orderId").toString(), "00");
			}else if("DOING".equals(json.getString("rt7_orderStatus"))||"INIT".equals(json.getString("rt7_orderStatus"))){
				result.put("respCode", "200");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt5_orderId").toString());
			}
			else if("FAIL".equals(json.getString("rt7_orderStatus"))||"REFUND".equals(json.getString("rt7_orderStatus"))){
				result.put("respCode", "01");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("orderId", json.getString("rt5_orderId").toString());
				UpdateDaifu(json.getString("rt5_orderId").toString(), "01");
			}
		}else if("0001".equals(json.getString("rt2_retCode"))){
			result.put("respCode", "200");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt5_orderId").toString());
		}else{
			result.put("respCode", "01");
			result.put("type", hlbRequest.getType());
			result.put("merNo", hlbRequest.getMerNo());
			result.put("respMsg", json.getString("rt3_retMsg").toString());
			result.put("orderId", json.getString("rt5_orderId").toString());
			UpdateDaifu(json.getString("rt5_orderId").toString(), "01");
		}
		} catch (Exception e) {
			log.info("算卡查询"+e);
		}
		return result;
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
	
	public synchronized int add(HLBRequest hlbRequest, PmsMerchantInfo merchantinfo, Map<String, String> result ,String state,String type) throws Exception {
		log.info("进来添加代付订单了");
		BigDecimal b1=new BigDecimal("0");//总金额
		int iii=0;
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		b1 =new BigDecimal(hlbRequest.getOrderAmount());
				 model.setMercId(hlbRequest.getMerNo());
				 model.setCount("1");
				 model.setBatchNo(hlbRequest.getOrderId());
				 model.setIdentity(hlbRequest.getOrderId());
				 model.setAmount(b1.doubleValue()/100+"");
				 model.setCardno(hlbRequest.getCardNo());
				 model.setRealname(hlbRequest.getPayerName());
				 if(hlbRequest.getOrderId().indexOf("/A")!=-1){
					 model.setPayamount(b1.doubleValue()/100+"");
				 }else{
					 model.setPayamount("-" +b1.doubleValue()/100);
				 }
				 //联行号
				 model.setPmsbankno(hlbRequest.getBankUnionCode());
				 if(hlbRequest.getOrderId().indexOf("/A")!=-1){
					 model.setTransactionType("代付补款");
				 }else{
					 model.setTransactionType("代付");
				 }if("T1".equals(type)){
					 model.setPosition(String.valueOf(merchantinfo.getPositionT1()));
				 }else{
					 model.setPosition(String.valueOf(merchantinfo.getPosition()));
				 }
				 model.setRemarks(type);
				 model.setRecordDescription("批次号:" + hlbRequest.getOrderId()+"订单号："+hlbRequest.getOrderId()+ "错误原因:" + result.get("respMsg"));
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
	public int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo)
			throws Exception {
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
				Double dd =amount*100-poundage;
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
	@Override
	public Map<String, String> selectBalance(HLBRequest hlbRequest,
			Map<String, String> result) {
		log.info("查询余额进来了！");
		
		
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("mercid", hlbRequest.getMerNo());// 商户编号
		paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
		paramMap.put("oAgentNo", "100333");
		// 商户 网购 业务信息
		Map<String, String> resultMaps = merchantMineDao.queryBusinessInfo(paramMap);

		String quickRateType = resultMaps.get("QUICKRATETYPE");// 快捷支付费率类型

		// 获取o单第三方支付的费率
		AppRateConfig appRate = new AppRateConfig();
		appRate.setRateType(quickRateType);
		appRate.setoAgentNo("100333");
		AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

		paramMap.put("mercid", hlbRequest.getMerNo());
		paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
		// 微信支付
		paramMap.put("paymentcode", PaymentCodeEnum.moBaoQuickPay.getTypeCode());

		// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
		AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
				.queryAmountAndStatus(paramMap);
		if (appRateTypeAndAmount != null) {
			String rateStr = appRateConfig.getRate(); // 商户费率
			LinkedHashMap<String, String> map =new LinkedHashMap<>();
			try {
			PmsBusinessPos pmsBusinessPos =selectKey(hlbRequest.getMerNo());		
			map.put("P1_bizType", "AccountQuery");
			map.put("P2_customerNumber", "C"+pmsBusinessPos.getBusinessnum());
			map.put("P3_userId", hlbRequest.getUserId());
			map.put("P4_timestamp", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			log.info("签名之前的数据:"+map);
			String key=pmsBusinessPos.getKek();
			 String oriMessage = MyBeanUtils.getSigned(map, null,key);
	       log.info("签名原文串：" + oriMessage);
			String sign =Disguiser.disguiseMD5(oriMessage, "UTF-8");
			map.put("sign", sign);
			log.info("1231:"+map);
			 Map<String, Object> resultMap = HttpClientService.getHttpResp(map,HLBUtil.url);
			log.info("算卡查询返回参数:"+JSON.toJSONString(resultMap));
			String s =resultMap.get("response").toString();
			System.out.println(s);
			JSONObject json =JSONObject.parseObject(s);
			if("0000".equals(json.getString("rt2_retCode"))){
				result.put("respCode", "00");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("respMsg", json.getString("rt3_retMsg").toString());
				result.put("userId", json.getString("rt5_userId").toString());
				result.put("payerName", json.getString("rt6_accountName").toString());
				result.put("idCardNo", json.getString("rt7_idCardNo").toString());
				result.put("accountStatus", json.getString("rt8_accountStatus").toString());
				System.out.println(Double.parseDouble(json.getString("rt9_accountBalance").toString())*Double.parseDouble(rateStr));
				Double accountBalance =Double.parseDouble(json.getString("rt9_accountBalance").toString())*100-Double.parseDouble(json.getString("rt9_accountBalance").toString())*Double.parseDouble(rateStr)*100;
				accountBalance =(double) Math.round(accountBalance);
				result.put("accountBalance", accountBalance.toString());
				result.put("accountFrozenBalance", Double.parseDouble(json.getString("rt10_accountFrozenBalance").toString())*100+"");
				result.put("createDate", json.getString("rt12_createDate").toString());
			}else{
				result.put("respCode", "01");
				result.put("type", hlbRequest.getType());
				result.put("merNo", hlbRequest.getMerNo());
				result.put("respMsg","请求失败");
			}
			} catch (Exception e) {
				log.info("算卡查询"+e);
			}
		}
		return result;
	}
	@Override
	public Map<String, String> creditPay(HLBRequest hlbRequest,
			Map<String, String> result) {
		
		return null;
	}
}

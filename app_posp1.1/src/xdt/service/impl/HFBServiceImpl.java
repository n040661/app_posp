package xdt.service.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import xdt.dto.hfb.Desede;
import xdt.dto.hfb.HFBPayRequest;
import xdt.dto.hfb.HeepayClient2;
import xdt.dto.hfb.HfbRequest;
import xdt.dto.hfb.HfbResponse;
import xdt.dto.hfb.HfbUtil;
import xdt.dto.hfb.HttpsUtil;
import xdt.dto.hj.HJPayRequest;
import xdt.dto.hj.HJPayResponse;
import xdt.dto.hj.HJThread;
import xdt.dto.hj.HJUtil;
import xdt.dto.mb.HttpService;
import xdt.dto.mb.MBUtil;
import xdt.dto.sxf.JsonUtils;
import xdt.dto.sxf.SXFResponse;
import xdt.dto.tfb.CardPayApplyRequest;
import xdt.dto.tfb.TFBConfig;
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
import xdt.service.IHFBService;
import xdt.service.IPublicTradeVerifyService;
import xdt.util.Constants;
import xdt.util.JsonUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.utils.RequestUtils;
@Service
public class HFBServiceImpl extends BaseServiceImpl implements IHFBService {

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
	
	//获取银行信息
	@Override
	public String onlineBankList(HfbRequest hfbRequest,
			Map<String, String> result) {
		Map<String, String> map =new HashMap<>();
		String retStr="";
		//获取上游商户号和密钥
        try {
			//PmsBusinessPos pmsBusinessPos =selectKey(hfbRequest.getMerchantId());
			map.put("merchantId", "100213");//pmsBusinessPos.getBusinessnum()
			List<NameValuePair> params =HttpsUtil.createNVPairs(map);
			retStr = HttpsUtil.sendHttpsRequestWithParam(HfbUtil.bankUrl, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retStr;
	}
	
	/**
	 * 网关支付
	 */
	@Override
	public Map<String, String> cardPay(HfbRequest hfbRequest,
			Map<String, String> result) {
		
		
		log.info("---------------网关支付进来了---------------");
		log.info("上传到server层参数:"+JSON.toJSON(hfbRequest));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = hfbRequest.getMerchantOrderNo(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = hfbRequest.getMerchantId();

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
				oriInfo.setMerchantOrderId(hfbRequest.getMerchantOrderNo());//---------------------------
				oriInfo.setPid( hfbRequest.getMerchantId());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoWxPay(hfbRequest, out_trade_no, mercId);
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
					paramMap.put("paymentcode", PaymentCodeEnum.GatewayCodePay.getTypeCode());

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
										PaymentCodeEnum.GatewayCodePay.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(hfbRequest.getPayAmount());// 收款金额
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
														String totalAmount = hfbRequest.getPayAmount(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															
															result =otherInvokeCardPay(hfbRequest, resultMap, appTransInfo);
															result.put("respCode", "00");
															result.put("respMsg", "请求成功");
															//String str =otherInvokeWxPay(hfbRequest, resultMap, appTransInfo);
															//TreeMap<String, String> map = RequestUtils.Dom2Map(str);
															//log.info("上游返回数据:"+JSON.toJSON(map));
															
															
															//JSONObject json =JSONObject.parseObject(str);
															
															
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

	public Map<String, String> otherInvokeCardPay(HfbRequest hfbRequest,Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception{
		
				// 查看当前交易是否已经生成了流水表
				PospTransInfo pospTransInfo = null;
				// 流水表是否需要更新的标记 0 insert，1：update
				int insertOrUpdateFlag = 0;
				log.info("***************进入payHandle5-14-3***************");
				// 生成上送流水号
				String transOrderId = hfbRequest.getMerchantOrderNo();
				log.info("***************进入payHandle5-15***************");
				if ((pospTransInfo = pospTransInfoDAO
						.searchByOrderId(hfbRequest.getMerchantOrderNo())) != null) {
					// 已经存在，修改流水号，设置pospsn为空
					log.info("订单号：" + hfbRequest.getMerchantOrderNo()
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
		         PmsBusinessPos pmsBusinessPos =selectKey(hfbRequest.getMerchantId());
		
		
		         //PmsBusinessPos pmsBusinessPos =selectKey(hfbRequest.getMerchantId());//获取上游商户号和秘钥
				Map<String, String> map =new HashMap<>();
				map.put("merchantId", "100381");
				map.put("merchantOrderNo", hfbRequest.getMerchantOrderNo());
				map.put("merchantUserId", "100089282");
				map.put("notifyUrl", HfbUtil.notifyUrl);
				map.put("payAmount", Double.parseDouble(hfbRequest.getPayAmount())/100+"");
				map.put("productCode", HfbUtil.productCode);
				map.put("version", HfbUtil.version);
				String signString =HttpsUtil.createSign(map, pmsBusinessPos.getKek());
				map.put("requestTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				map.put("callBackUrl", hfbRequest.getCallBackUrl()==null?"":hfbRequest.getCallBackUrl());
				map.put("onlineType", HfbUtil.onlineType);
				map.put("signString", signString);
				return map;
	}
	
	
	@Override
	public Map<String, String> WZpay(HfbRequest hfbRequest, Map<String, String> result) {

		
		
		log.info("---------------微信qq钱包进来了---------------");
		log.info("上传到server层参数:"+JSON.toJSON(hfbRequest));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = hfbRequest.getMerchantOrderNo(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = hfbRequest.getMerchantId();

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
				oriInfo.setMerchantOrderId(hfbRequest.getMerchantOrderNo());//---------------------------
				oriInfo.setPid( hfbRequest.getMerchantId());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoWxPay(hfbRequest, out_trade_no, mercId);
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
					if("weixin_qr".equals(hfbRequest.getTradeType())||"weixin_pub".equals(hfbRequest.getTradeType())||"weixin_h5".equals(hfbRequest.getTradeType())){
						paramMap.put("paymentcode", PaymentCodeEnum.weixinPay.getTypeCode());
					}else if("alipay_qr".equals(hfbRequest.getTradeType())||"alipay_wap".equals(hfbRequest.getTradeType())){
						paramMap.put("paymentcode", PaymentCodeEnum.GatewayCodePay.getTypeCode());
					}else if("qq_qr".equals(hfbRequest.getTradeType())){
						paramMap.put("paymentcode", PaymentCodeEnum.QQCodePay.getTypeCode());
					}else if("jd_qr".equals(hfbRequest.getTradeType())){
						paramMap.put("paymentcode", PaymentCodeEnum.JingDong.getTypeCode());
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
										PaymentCodeEnum.GatewayCodePay.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(hfbRequest.getPayAmount());// 收款金额
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
										if("weixin_qr".equals(hfbRequest.getTradeType())||"weixin_pub".equals(hfbRequest.getTradeType())||"weixin_h5".equals(hfbRequest.getTradeType())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.weixinPay, mercId);
										}else if("alipay_qr".equals(hfbRequest.getTradeType())||"alipay_wap".equals(hfbRequest.getTradeType())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.zhifubaoPay, mercId);
										}else if("qq_qr".equals(hfbRequest.getTradeType())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.QQCodePay, mercId);
										}else if("jd_qr".equals(hfbRequest.getTradeType())||"alipay_wap".equals(hfbRequest.getTradeType())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.JingDong, mercId);
										}
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
														String totalAmount = hfbRequest.getPayAmount(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															
															String	results =otherInvokeWZPay(hfbRequest, resultMap, appTransInfo);
															JSONObject json =JSONObject.fromObject(results);
															result.put("merchantId", hfbRequest.getMerchantId());
															if("1000".equals(json.getString("retCode"))){
																result.put("respCode", "00");
																result.put("respMsg", "请求成功");
																result.put("tradeType", json.getString("tradeType"));
																result.put("payUrl", json.getString("payUrl"));
																result.put("merchantOrderNo", json.getString("merchantBillNo"));
															}else{
																result.put("respCode", json.getString("retCode"));
																result.put("respMsg", json.getString("retMsg"));
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
	
	
	public String otherInvokeWZPay(HfbRequest hfbRequest,Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception{
		
		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = hfbRequest.getMerchantOrderNo();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(hfbRequest.getMerchantOrderNo())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + hfbRequest.getMerchantOrderNo()
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
		if("weixin_qr".equals(hfbRequest.getTradeType())||"weixin_pub".equals(hfbRequest.getTradeType())||"weixin_h5".equals(hfbRequest.getTradeType())){
			appTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay.getTypeCode());
		}else if("alipay_qr".equals(hfbRequest.getTradeType())||"alipay_wap".equals(hfbRequest.getTradeType())){
			appTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
		}else if("qq_qr".equals(hfbRequest.getTradeType())){
			appTransInfo.setPaymenttype(PaymentCodeEnum.QQCodePay.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.QQCodePay.getTypeCode());
		}else if("jd_qr".equals(hfbRequest.getTradeType())){
			appTransInfo.setPaymenttype(PaymentCodeEnum.JingDong.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.JingDong.getTypeCode());
		}
			
         pmsAppTransInfoDao.update(appTransInfo);
         //获取上游商户号和密钥
        PmsBusinessPos pmsBusinessPos =selectKey(hfbRequest.getMerchantId());
		Map<String, String> map =new HashMap<>();
		map.put("merchantBillNo", hfbRequest.getMerchantOrderNo());
		map.put("merchantId", "100381");
		map.put("notifyUrl", HfbUtil.notifyUrl);
		map.put("payAmt", Double.parseDouble(hfbRequest.getPayAmount())/100+"");
		map.put("requestTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("tradeType",hfbRequest.getTradeType());
		map.put("userIp", hfbRequest.getUserIp()==""?"192.168.71.1":hfbRequest.getUserIp());
		map.put("version", HfbUtil.version);
	    String key=pmsBusinessPos.getKek();
		/*if("weixin_qr".equals(hfbRequest.getTradeType())){
			key="1ae4f3e957bbe4cf205e4337547aed6c";
		}else if("weixin_pub".equals(hfbRequest.getTradeType())){
			key="40f429121427c2ac658ca08145bf8ca7";
		}else if("weixin_h5".equals(hfbRequest.getTradeType())){
			key="36efccd63b7a3c386ea00310f6bec228";
		}else if("alipay_qr".equals(hfbRequest.getTradeType())){
			key="792dd569ce65909ca6bb06dbe56b7a5a";
		}else if("alipay_wap".equals(hfbRequest.getTradeType())){
			key="5c76937bb8722fe9b0eaf67b2c3f4194";
		}else if("qq_qr".equals(hfbRequest.getTradeType())){
			key="3cb7d81b02459ca07fcd9c3ba0025984";
		}else if("jd_qr".equals(hfbRequest.getTradeType())){
			key="";
		}*/
		String sign =HttpsUtil.createSign(map, key);
		map.put("goodsName",hfbRequest.getGoodsName());
		map.put("goodsName",hfbRequest.getGoodsName()==null?"":hfbRequest.getGoodsName());
		/*map.put("returnUrl", hfbRequest.getReturnUrl());*/
		map.put("remark", hfbRequest.getRemark()==null?"":hfbRequest.getRemark());
		System.out.println(hfbRequest.getGoodsNote()==null?"":hfbRequest.getGoodsNote());
		if("weixin_h5".equals(hfbRequest.getTradeType())){
			map.put("goodsDetail","{\"n\":\"汇元网\", \"id\":\"http://www.9186.com/index.aspx\"}");//{\"n\":\"汇元网\", \"id\":\"http://www.9186.com/index.aspx\"}
		}
		map.put("goodsNote", hfbRequest.getGoodsNote()==null?"":hfbRequest.getGoodsNote());
		map.put("qrCodeStatus", hfbRequest.getQrCodeStatus()==null?"":hfbRequest.getQrCodeStatus());
		map.put("sign", sign);
		String results = "";
		List<NameValuePair> params =HttpsUtil.createNVPairs(map);
		log.info("微信支付宝上传之前的参数:"+params);
		try {
		 results =HttpsUtil.sendHttpsRequestWithParam(HfbUtil.WZUrl, params);
		 log.info("汇付宝微信支付宝返回参数："+JSON.toJSONString(results));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	@Override
	public String WZSelect(HfbRequest hfbRequest,
			Map<String, String> result) {
		Map<String, String> map =new HashMap<>();
		map.put("merchantBillNo", hfbRequest.getMerchantOrderNo());
		map.put("merchantId", "100213");
		map.put("requestTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		map.put("version", HfbUtil.version);
		String key="1a6cbfcbc012476cdea47bbea224250a";
		String sign =HttpsUtil.createSign(map, key);
		map.put("sign", sign);
		String results = "";
		List<NameValuePair> params =HttpsUtil.createNVPairs(map);
		log.info("微信支付宝上传之前的参数:"+params);
		try {
		 results =HttpsUtil.sendHttpsRequestWithParam(HfbUtil.WZSelectUrl, params);
		 log.info("汇付宝微信支付宝查询返回参数："+JSON.toJSONString(results));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
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
	private int saveOriginAlInfoWxPay(HfbRequest hfbRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(orderid);
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		//想要传服务器要改实体
		info.setBgUrl(hfbRequest.getUrl());
		info.setPageUrl(hfbRequest.getReUrl());
		Double amt = Double.parseDouble(hfbRequest.getPayAmount());// 单位分
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
	private int saveOriginAlInfoCardPay(CardPayApplyRequest cardPayApplyＲequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(cardPayApplyＲequest.getSpbillno());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		//想要传服务器要改实体
		info.setBgUrl(cardPayApplyＲequest.getUrl());
		info.setPageUrl(cardPayApplyＲequest.getReUrl());
		Double amt = Double.parseDouble(cardPayApplyＲequest.getMoney());// 单位分
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
	public void update(HfbResponse hfbResponse) throws Exception {
		log.info("返回的参数："+hfbResponse);
		log.info("**************进入修改方法*************************");
		// 流水表transOrderId
		String transOrderId = "";
		if(hfbResponse.getMerchantOrderNo()!=""&& hfbResponse.getMerchantOrderNo()!=null){
			transOrderId=hfbResponse.getMerchantOrderNo();
		}
		log.info("异步通知回来的订单号:" + transOrderId);
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息：" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息：" + pmsAppTransInfo);
		// 查询结果成功
		if("1000".equals(hfbResponse.getResult())) {
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
				if(hfbResponse.getMerchantOrderNo()!=null&& hfbResponse.getMerchantOrderNo()!=""){
					pospTransInfo.setPospsn(hfbResponse.getMerchantOrderNo());
				}
				
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if("1002".equals(hfbResponse.getResult())){
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("1");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				if(hfbResponse.getMerchantOrderNo()!=null&&hfbResponse.getMerchantOrderNo()!=""){
					pospTransInfo.setPospsn(hfbResponse.getMerchantOrderNo());
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
			
		}

		
	}

	@Override
	public Map<String, String> pay(HFBPayRequest hfbRequest, Map<String, String> result) {
		
		log.info("汇付宝----下游传送代付参数:"+JSON.toJSON(hfbRequest));
		BigDecimal b1=new BigDecimal("0");// 下游上传的总金额
		BigDecimal b2=new BigDecimal("0");// 系统代付余额
		BigDecimal b3=new BigDecimal("0");// 单笔交易总手续费
		BigDecimal min=new BigDecimal("0");// 代付最小金额
		BigDecimal max=new BigDecimal("0");// 代付最大金额
		Double surplus;// 代付剩余金额
		log.info("汇付宝----查询当前代付订单是否存在");
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		Map<String, String> map =new HashMap<>();
		Map<String, String> maps=new HashMap<>();//填金
		model.setMercId(hfbRequest.getMerchantId());
		model.setBatchNo(hfbRequest.getMerchantBatchNo());
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
			result.put("respCode", "0006");
			result.put("respMsg", "下单失败,订单存在");
			log.info("汇付宝----**********************代付 下单失败:{}");
			log.info("汇付宝----订单存在");
			return result;
		}
		try {
			label104: {

				log.info("********************汇付宝-------------根据商户号查询");
				String e = hfbRequest.getMerchantId();
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
					log.info("***********汇付宝*************商户信息:" + merchantinfo);
					if (StringUtils.isBlank(oAgentNo)) {
						throw new RuntimeException("系统错误----------------o单编号为空");
					}

					if ("60".equals(merchantinfo.getMercSts())) {
						//插入异步数据
						saveOriginAlInfoWxPay1(hfbRequest, hfbRequest.getMerchantBatchNo(), hfbRequest.getMerchantId());
						// 判断交易类型
						log.info("***********汇付宝*************实际金额");
						// 分
						String payAmt= hfbRequest.getAmount();
						b1 =new BigDecimal(payAmt);
						
						System.out.println("参数:"+b1.doubleValue());
						log.info("***********汇付宝*************校验欧单金额限制");
						log.info("汇付宝----下游上传代付总金额:" + b1.doubleValue());
						b3 = new BigDecimal(merchantinfo.getPoundage());
						log.info("汇付宝----系统商户代付单笔手续费:" + b3.doubleValue());
						min = new BigDecimal(merchantinfo.getMinDaiFu());
						log.info("汇付宝----系统代付最小金额:" + min.doubleValue());
						max = new BigDecimal(merchantinfo.getMaxDaiFu());
						log.info("汇付宝----系统代付最大金额:" + max.doubleValue());
						b2 = new BigDecimal(merchantinfo.getPositionT1());
						log.info("汇付宝----系统剩余可用额度:" + b2.doubleValue());
						
						
						
						if (b1.doubleValue() + b3.doubleValue()*100 > b2.doubleValue()) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额高于剩余额度");
							log.info("汇付宝**********************代付金额高于剩余额度");
							int i = add(hfbRequest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("汇付宝----添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() < min.doubleValue() * 100) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额小于代付最小金额");
							log.info("汇付宝**********************代付金额小于代付最小金额");
							int i = add(hfbRequest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("汇付宝--添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() > max.doubleValue() * 100) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额大于代付最大金额");
							log.info("汇付宝**********************代付金额大于代付最大金额");
							int i = add(hfbRequest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("汇付宝--添加失败订单成功");
							}
							return result;
						}
							surplus = b2.subtract(b1).doubleValue() - b3.doubleValue() * 100;
							merchantinfo.setPositionT1(surplus.toString());
							
							
							int i =add(hfbRequest, merchantinfo, result, "200");
							if (i == 1) {
								log.info("汇付宝--添加代付扣款订单成功！");
							}
						PmsBusinessPos pmsBusinessPos =selectKey(hfbRequest.getMerchantId());
						int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
						if (num == 1) {
							log.info("汇付宝--扣款成功！！");
						}
						if (i == 1) {
							log.info("汇付宝--代付订单添加成功");
							HeepayClient2 transferClient;
								if(Double.parseDouble(hfbRequest.getAmount())/100<50000){
									 transferClient = new HeepayClient2("https://open.heepay.com/transferSmallApply.do", HfbUtil.transferKey);//pmsBusinessPos
								}else{
									 transferClient = new HeepayClient2("https://open.heepay.com/transferLargeApply.do", HfbUtil.transferKey);//pmsBusinessPos
								}
								map.put("merchantPayNo", hfbRequest.getMerchantPayNo());     //商户付款流水号
						        map.put("bankId", hfbRequest.getBankId());                   //收款方银行ID，参考对应接口或文档
						        map.put("publicFlag", hfbRequest.getPublicFlag());                 //对公对私，0=对私，1=对公
						        map.put("bankcardNo", hfbRequest.getBankcardNo());  //收款方银行卡号
						        map.put("ownerName",hfbRequest.getOwnerName());               //收款方持卡人姓名
						        map.put("amount", Double.parseDouble(hfbRequest.getAmount())/100+"");                //转账金额
						        map.put("reason", "上游厂商结算款");                  //转账理由或描述，参考对应接口或文档
						        map.put("province", hfbRequest.getProvince());               //收款方开户省，参考对应接口或文档
						        map.put("city", hfbRequest.getCity());               //收款方开户市，参考对应接口或文档
						        map.put("bankName", hfbRequest.getBankName());   //收款方开户支行名称
						        List<Map> transferDetails = new ArrayList<>();
						        transferDetails.add(map);
						        System.out.println("来了:"+JSONArray.fromObject(transferDetails).toString());
						        String timeStamp = String.valueOf(new Date().getTime());

						        String cipher;
								cipher = Desede.encodeECB(JSON.toJSONString(transferDetails), HfbUtil.transferKey.substring(0, 24));

						        Map<String, String> req = new HashMap<>();
						        req.put("merchantId", pmsBusinessPos.getBusinessnum().substring(0, 6));//pmsBusinessPos //商户IDHfbUtil.merchantId
						        req.put("merchantBatchNo", hfbRequest.getMerchantBatchNo());    //商户转账批次号
						        req.put("batchAmount", Double.parseDouble(hfbRequest.getAmount())/100+"");           //商户转账总金额
						        req.put("batchNum", "1");                   //商户转账总笔数
						        req.put("intoAccountDay", hfbRequest.getIntoAccountDay()==null?"0":hfbRequest.getIntoAccountDay());             //到账日期 0=当日，1=次日
						        req.put("transferDetails", cipher);  //转账详情
						        req.put("requestTime", timeStamp);          //请求时间
						        req.put("version", "2.0");                  //请求版本
						        req.put("notifyUrl", HfbUtil.notifyUrls); //通知地址，不需要通知则传空字符串
						        log.info("上传前的参数："+req);
						        String retStr = transferClient.execute(req);
						        System.out.println("转账申请，返回"+retStr);
						        com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(retStr);
						        
						        if("1".equals(json.getString("retCode"))){
						        	result.put("respCode", "00");
						        	result.put("respMsg", "请求成功，状态查看异步！");
						        	result.put("merchantId", hfbRequest.getMerchantId());
						        	result.put("merchantBatchNo", hfbRequest.getMerchantBatchNo());
						        	result.put("amount", hfbRequest.getAmount());
						        }else{
						        	result.put("respCode", "01");
						        	result.put("respMsg", "请求失败！");
						        	result.put("merchantId", hfbRequest.getMerchantId());
						        	result.put("merchantBatchNo", hfbRequest.getMerchantBatchNo());
						        	result.put("amount", hfbRequest.getAmount());
						        	UpdateDaifu(hfbRequest.getMerchantBatchNo(), "01");
						        	maps.put("payMoney",hfbRequest.getAmount());
					     			maps.put("machId", hfbRequest.getMerchantId());
									int nus = pmsMerchantInfoDao.updataPayT1(maps);
									if(nus==1){
										log.info("汇付宝***补款成功");
										surplus = surplus+Double.parseDouble(hfbRequest.getAmount());
										merchantinfo.setPositionT1(surplus.toString());
										hfbRequest.setMerchantBatchNo(hfbRequest.getMerchantBatchNo()+"/A");
										int id =add(hfbRequest, merchantinfo, result, "00");
										if(id==1){
											log.info("汇付宝代付补单成功");
										}
									}
						        }
						}
					} else {
						throw new RuntimeException("汇付宝***系统错误----------------当前商户非正式商户");
					}

				} else {
					throw new RuntimeException("汇付宝***系统错误----------------商户不存在");
				}
				break label104;
			}

		} catch (Exception var43) {
			log.error("汇付宝*******************************代付错误", var43);
			try {
				throw var43;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	log.info("***********汇付宝*********************代付------处理完成");
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
		
		public synchronized int add(HFBPayRequest hfbPayRequest, PmsMerchantInfo merchantinfo, Map<String, String> result ,String state) throws Exception {
			log.info("进来添加代付订单了");
			BigDecimal b1=new BigDecimal("0");//总金额
			int iii=0;
			PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
			b1 =new BigDecimal(hfbPayRequest.getAmount());
					 model.setProvince(hfbPayRequest.getProvince());
					 model.setCity(hfbPayRequest.getCity());
					 model.setMercId(hfbPayRequest.getMerchantId());
					 model.setCount("1");
					 model.setBatchNo(hfbPayRequest.getMerchantBatchNo());
					 model.setIdentity(hfbPayRequest.getMerchantPayNo());
					 model.setAmount(b1.doubleValue()/100+"");
					 model.setCardno(hfbPayRequest.getBankcardNo());
					 model.setRealname(hfbPayRequest.getOwnerName());
					 if(hfbPayRequest.getMerchantBatchNo().indexOf("/A")!=-1){
						 model.setPayamount(b1.doubleValue()/100+"");
					 }else{
						 model.setPayamount("-" +b1.doubleValue()/100);
					 }
					 //联行号
					 model.setPmsbankno("");
					 if(hfbPayRequest.getMerchantBatchNo().indexOf("/A")!=-1){
						 model.setTransactionType("代付补款");
					 }else{
						 model.setTransactionType("代付");
					 }
					 model.setPosition(String.valueOf(merchantinfo.getPositionT1()));
					 model.setRemarks("T1");
					 model.setRecordDescription("批次号:" + hfbPayRequest.getMerchantBatchNo()+"订单号："+hfbPayRequest.getMerchantPayNo()+ "错误原因:" + result.get("respMsg"));
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
		
		/**
		 * 
		 * @Description 插入原始订单表信息
		 * @author Administrator
		 * @param reqeustInfo
		 * @param orderid
		 * @param mercId
		 * @throws Exception
		 */
		private int saveOriginAlInfoWxPay1(HFBPayRequest hfbRequest, String orderid, String mercId)
				throws Exception {
			// 插入原始信息
			OriginalOrderInfo info = new OriginalOrderInfo();
			info.setPid(mercId);
			info.setMerchantOrderId(orderid);
			info.setOrderId(orderid);
			info.setOrderTime(UtilDate.getOrderNum());
			info.setPayType("汇付宝代付");
			//想要传服务器要改实体
			info.setBgUrl(hfbRequest.getUrl());
			Double amt = Double.parseDouble(hfbRequest.getAmount());// 单位分
			amt /= 100;
			DecimalFormat df = new DecimalFormat("######0.00");

			info.setOrderAmount(df.format(amt));

			return originalDao.insert(info);
		}

		@Override
		public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
			OriginalOrderInfo original = null;
			log.info("根据上送订单号  查询商户上送原始信息");
			original = originalDao.getOriginalOrderInfoByOrderid(tranId);
			return original;
		}
		
		
}

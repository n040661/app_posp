package xdt.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
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
import xdt.dto.hj.HJPayRequest;
import xdt.dto.hj.HJResponse;
import xdt.dto.lhzf.LhzfRequset;
import xdt.dto.pay.BaseResMessage;
import xdt.dto.pay.Constants;
import xdt.dto.pay.ConsumeSMSVo;
import xdt.dto.pay.EncryptUtil;
import xdt.dto.pay.MerchantVo;
import xdt.dto.pay.PayRequest;
import xdt.dto.pay.PayUtil;
import xdt.dto.pay.SignUtil;
import xdt.dto.ys.DateUtil;
import xdt.dto.ys.HttpUtils;
import xdt.dto.ys.SwpHashUtil;
import xdt.dto.ys.YSUtil;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.service.HfQuickPayService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IYSService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年2月2日 上午10:05:33 
* 类说明 
*/
@Service
public class YSServiceImpl extends BaseServiceImpl implements IYSService {

	private Logger log = Logger.getLogger(this.getClass());
	
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

	@Resource
	public PmsWeixinMerchartInfoService weixinService;
	@Override
	public Map<String, String> quickPay(PayRequest payRequest, Map<String, String> result) {
		try {
		Map<String, String> map =new HashMap<>();
		log.info("支付参数进来了："+JSON.toJSONString(payRequest));
		String mercId=payRequest.getMerchantId();
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
				.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			// 正式商户
			merchantinfo = merchantList.get(0);

		 String	oAgentNo = merchantinfo.getoAgentNo();//
		 
		 if("60".equals(merchantinfo.getMercSts())) {

				// 判断是否为正式商户
			  	saveOriginAlInfoCardPay(payRequest, mercId);
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
										payRequest.getAmount());// 收款金额
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

											if (min_amount.compareTo(payAmt) != 1) {// 判断收款金额是否大于最低收款金额
												// 大于等于执行
												// 小于不执行
												if (payAmt.compareTo(max_amount) != 1) {
													// 组装报文
													//---------------------
													log.info("来了111111111111");
													PmsAppTransInfo appTransInfo = this.insertOrder(payRequest.getOrderId(),
															payRequest.getAmount(), mercId, rateStr, oAgentNo);
													PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
													//B2
													if("000000003".equals(pmsBusinessPos.getBusinessnum())) {
														otherInvokeCardPay(payRequest, appTransInfo, rateStr, merchantinfo, result);
													//C2	
													}else if("000000001".equals(pmsBusinessPos.getBusinessnum())) {
														cardPay(payRequest, appTransInfo, rateStr, merchantinfo, result);
													}
													//---------------------
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

			
		 }else {
				log.error("不是正式商户!");
				result.put("respCode", "03");
				result.put("respMsg", "不是正式商户");
			}
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	@Override
	public Map<String, String> register(PayRequest payRequest,
			Map<String, String> result) {
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mercid", payRequest.getMerchantId());// 商户编号
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

			paramMap.put("mercid", payRequest.getMerchantId());
			paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
			// 微信支付
			paramMap.put("paymentcode", PaymentCodeEnum.moBaoQuickPay.getTypeCode());

			// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
			AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
					.queryAmountAndStatus(paramMap);
			if (appRateTypeAndAmount == null) {
				result.put("respCode", "01");
				result.put("respMsg", "费率为null");
				return result;
			}
			String rateStr = appRateConfig.getRate(); // 商户费率
			if(Double.parseDouble(rateStr)>Double.parseDouble(payRequest.getDebitRate())){
				result.put("respCode", "01");
				result.put("respMsg", "费率不能低于系统费率");
				return result;
			}
			
		result.put("merchantId", payRequest.getMerchantId());
		PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
		Map<String, String> map =new HashMap<>();
		log.info("注册参数进来了："+JSON.toJSONString(payRequest));
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(payRequest.getMerchantId());
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if(Double.parseDouble(merchantList.get(0).getPoundage())>Double.parseDouble(payRequest.getWithdrawDepositSingleFee())/100){
			result.put("respCode", "01");
			result.put("respMsg", "代付费用不能低于系统费用");
			return result;
		}
		
		String channelKey = YSUtil.channelKey;
		String channel_sign_method = "SHA256";
		Map<String, Object> reqMap = new TreeMap<String, Object>();
		reqMap.put("sp_id", YSUtil.sp_id);//服务商号
		reqMap.put("mch_id", YSUtil.merId4);//商户号
		reqMap.put("out_trade_no",payRequest.getOrderId());
		reqMap.put("id_type", "01");
		reqMap.put("acc_name", payRequest.getAcctName());//持卡人姓名
		if("0".equals(payRequest.getBusinessType())) {
			reqMap.put("acc_type", "PERSONNEL");//PERSONNEL：对私 CORPORATE：对公
		}else if("1".equals(payRequest.getBusinessType())) {
			reqMap.put("acc_type", "CORPORATE");//PERSONNEL：对私 CORPORATE：对公
		}
		reqMap.put("bank_code", payRequest.getPmsbankNo());//联行号
		reqMap.put("acc_no", payRequest.getAcctNo());//卡号
		reqMap.put("acc_province", payRequest.getProvince());//省
		reqMap.put("acc_city", payRequest.getCity());//市
		reqMap.put("mobile", payRequest.getPhone());//手机号
		reqMap.put("id_no", payRequest.getLiceneceNo());//证件号
		reqMap.put("settle_rate", payRequest.getDebitRate());//结算费率
		reqMap.put("extra_rate", payRequest.getWithdrawDepositSingleFee());//T0费率
		Date t = new Date();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(t);
		long sys_timestamp = cal.getTimeInMillis();
		reqMap.put("timestamp", sys_timestamp);//时间戳
		
		StringBuilder sb = new StringBuilder();
        Set<String> keySet = reqMap.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
        	String key = iter.next();
            sb.append(key);
            sb.append("=");
            sb.append(reqMap.get(key));
            sb.append("&");
        }
        String sign = SwpHashUtil.getSign(sb.toString()+"key="+channelKey, channelKey, channel_sign_method);
        reqMap.put("sign", sign);
        
        sb.append("sign");
        sb.append("=");
        sb.append(sign);
        System.out.println(sb.toString());
        String url=YSUtil.url+"/swp/ybbh/b2_register.do";
			HttpResponse httpResponse =HttpUtils.doPost(url, "", sb.toString(), "application/x-www-form-urlencoded; charset=UTF-8");
			String resp =EntityUtils.toString(httpResponse.getEntity());
			System.out.println("接受请求:"+resp);
			JSONObject json =JSONObject.parseObject(resp);

			if("SUCCESS".equals(json.getString("status"))) {
				result.put("respCode", "00");
				result.put("respMsg", "请求成功");
				if("SUCCESS".equals(json.getString("trade_state"))) {
					PmsWeixinMerchartInfo merchartInfo =new PmsWeixinMerchartInfo();
					merchartInfo.setAccount(json.getString("swpaccid"));//账号
					merchartInfo.setMerchartId(payRequest.getMerchantId());
					merchartInfo.setMerchartName(merchantList.get(0).getMercName());
					merchartInfo.setMerchartNameSort(merchantList.get(0).getShortname());
					merchartInfo.setCertNo(payRequest.getLiceneceNo());//证件号
					merchartInfo.setCardNo(payRequest.getAcctNo());//卡号
					merchartInfo.setRealName(payRequest.getAcctName());//姓名
					merchartInfo.setMobile(payRequest.getPhone());//手机号
					merchartInfo.setAccountType(payRequest.getBusinessType());//账户类型
					merchartInfo.setBankName(payRequest.getBankName());//开户行
					merchartInfo.setPmsBankNo(payRequest.getPmsbankNo());//联行号
					merchartInfo.setProvince(payRequest.getProvince());//省份
					merchartInfo.setCity(payRequest.getCity());//城市
					merchartInfo.setDebitRate(payRequest.getDebitRate());//借记卡费率
					merchartInfo.setWithdrawDepositSingleFee(payRequest.getWithdrawDepositSingleFee());//提现单笔手续费
					merchartInfo.setoAgentNo("100333");
					merchartInfo.setRateCode(payRequest.getMerchantId());
					int i =weixinService.updateRegister(merchartInfo);
					log.info("ii:"+i);
					result.put("code", "00");
					result.put("message", json.getString("trade_state_desc"));
					result.put("merchantCode",json.getString("swpaccid"));
				}else {
					result.put("code", "01");
					result.put("respMsg", json.getString("trade_state_desc"));
				}
			}else {
				result.put("respCode", "01");
				result.put("respMsg", json.getString("message"));
				System.out.println(json.getString("message"));
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 修改用户信息
	 */
	@Override
	public Map<String, String> update(PayRequest payRequest, Map<String, String> result) {
		log.info("修改参数进来了:"+JSON.toJSONString(payRequest));
		
		try {
			
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("mercid", payRequest.getMerchantId());// 商户编号
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

		paramMap.put("mercid", payRequest.getMerchantId());
		paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
		// 微信支付
		paramMap.put("paymentcode", PaymentCodeEnum.moBaoQuickPay.getTypeCode());

		// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
		AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
				.queryAmountAndStatus(paramMap);
		if (appRateTypeAndAmount == null) {
			result.put("respCode", "01");
			result.put("respMsg", "费率为null");
			return result;
		}
		String rateStr = appRateConfig.getRate(); // 商户费率
		if(Double.parseDouble(rateStr)>Double.parseDouble(payRequest.getDebitRate())){
			result.put("respCode", "01");
			result.put("respMsg", "费率不能低于系统费率");
			return result;
		}
		
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(payRequest.getMerchantId());
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if(Double.parseDouble(merchantList.get(0).getPoundage())>Double.parseDouble(payRequest.getWithdrawDepositSingleFee())/100){
			result.put("respCode", "01");
			result.put("respMsg", "代付费用不能低于系统费用");
			return result;
		}
		
		PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
		log.info("pmsBusinessPos:"+JSON.toJSONString(pmsBusinessPos));
		PmsWeixinMerchartInfo model =new PmsWeixinMerchartInfo();
		model.setMerchartId(payRequest.getMerchantId());
		model.setAccount(payRequest.getMerchantCode());
		log.info("0000");
		List<PmsWeixinMerchartInfo>  merchartInfo =weixinService.selectlist(model);
		PmsWeixinMerchartInfo merchartInfo2=null;
		String resp="";
		if(merchartInfo.size() >0){
			merchartInfo2=merchartInfo.get(0);
			if("1".equals(payRequest.getChangeType())) {
				merchartInfo2.setDebitRate(payRequest.getDebitRate());
				merchartInfo2.setWithdrawDepositSingleFee(payRequest.getWithdrawDepositSingleFee());
				String channelKey = YSUtil.channelKey;
				String channel_sign_method = "SHA256";
				Map<String, Object> reqMap = new TreeMap<String, Object>();
				reqMap.put("sp_id", YSUtil.sp_id);
				reqMap.put("mch_id", YSUtil.merId4);
				reqMap.put("out_trade_no", payRequest.getOrderId());
				reqMap.put("swpaccid", payRequest.getMerchantCode());
				reqMap.put("settle_rate", payRequest.getDebitRate());
				reqMap.put("extra_rate", payRequest.getWithdrawDepositSingleFee());
				Date t = new Date();
				java.util.Calendar cal = java.util.Calendar.getInstance();
				cal.setTime(t);
				long sys_timestamp = cal.getTimeInMillis();
				reqMap.put("timestamp", sys_timestamp);
				
				StringBuilder sb = new StringBuilder();
		        Set<String> keySet = reqMap.keySet();
		        Iterator<String> iter = keySet.iterator();
		        while (iter.hasNext()) {
		        	String key = iter.next();
		            sb.append(key);
		            sb.append("=");
		            sb.append(reqMap.get(key));
		            sb.append("&");
		        }
		        String sign = SwpHashUtil.getSign(sb.toString()+"key="+channelKey, channelKey, channel_sign_method);
		        reqMap.put("sign", sign);
		        
		        sb.append("sign");
		        sb.append("=");
		        sb.append(sign);
		        String url =YSUtil.url+"/swp/ybbh/b2_settleFee.do";
		        System.out.println(sb.toString());
				HttpResponse httpResponse =HttpUtils.doPost(url, "", sb.toString(), "application/x-www-form-urlencoded; charset=UTF-8");
				resp =EntityUtils.toString(httpResponse.getEntity());
				
				
			}else if("2".equals(payRequest.getChangeType())) {
				merchartInfo2.setAccountType(payRequest.getBusinessType());
				merchartInfo2.setPmsBankNo(payRequest.getPmsbankNo());//联行号
				merchartInfo2.setCardNo(payRequest.getAcctNo());//卡号
				merchartInfo2.setMobile(payRequest.getPhone());//手机号
				merchartInfo2.setProvince(payRequest.getProvince());//省份
				merchartInfo2.setCity(payRequest.getCity());//城市
				
				String channelKey = YSUtil.channelKey;
				String channel_sign_method = "SHA256";
				Map<String, Object> reqMap = new TreeMap<String, Object>();
				reqMap.put("sp_id", YSUtil.sp_id);
				reqMap.put("mch_id", YSUtil.merId4);
				reqMap.put("out_trade_no", payRequest.getOrderId());
				reqMap.put("swpaccid", payRequest.getMerchantCode());
				reqMap.put("acc_no", payRequest.getAcctNo());//卡号
				reqMap.put("bank_code", payRequest.getPmsbankNo());
				reqMap.put("mobile", payRequest.getPhone());
				if("0".equals(payRequest.getBusinessType())) {
					reqMap.put("acc_type", "PERSONNEL");//PERSONNEL：对私 CORPORATE：对公
				}else if("1".equals(payRequest.getBusinessType())) {
					reqMap.put("acc_type", "CORPORATE");//PERSONNEL：对私 CORPORATE：对公
				}
				reqMap.put("acc_city", payRequest.getCity());
				reqMap.put("acc_province", payRequest.getProvince());
				Date t = new Date();
				java.util.Calendar cal = java.util.Calendar.getInstance();
				cal.setTime(t);
				long sys_timestamp = cal.getTimeInMillis();
				reqMap.put("timestamp", sys_timestamp);
				
				StringBuilder sb = new StringBuilder();
		        Set<String> keySet = reqMap.keySet();
		        Iterator<String> iter = keySet.iterator();
		        while (iter.hasNext()) {
		        	String key = iter.next();
		            sb.append(key);
		            sb.append("=");
		            sb.append(reqMap.get(key));
		            sb.append("&");
		        }
		        String sign = SwpHashUtil.getSign(sb.toString()+"key="+channelKey, channelKey, channel_sign_method);
		        reqMap.put("sign", sign);
		        
		        sb.append("sign");
		        sb.append("=");
		        sb.append(sign);
		        String url =YSUtil.url+"/swp/ybbh/b2_settleCard.do";
		        System.out.println(sb.toString());
				HttpResponse httpResponse =HttpUtils.doPost(url, "", sb.toString(), "application/x-www-form-urlencoded; charset=UTF-8");
				resp =EntityUtils.toString(httpResponse.getEntity());
			}
		}
		
		System.out.println("接受请求:"+resp);
		JSONObject json =JSONObject.parseObject(resp);
		if("SUCCESS".equals(json.getString("status"))) {
			result.put("respCode", "00");
			result.put("respMsg", "请求成功");
			if("SUCCESS".equals(json.getString("trade_state"))) {
				
				int i =weixinService.updateByPrimaryKeySelective(merchartInfo2);
				if(i==1){
					log.info("修改成功");
				}else {
					throw new SQLException("修改失败:" + json.getString("trade_state_desc"));
				}
				result.put("code", "00");
				result.put("message", json.getString("trade_state_desc"));
			}else {
				result.put("code", "01");
				result.put("respMsg", json.getString("trade_state_desc"));
			}
		}else {
			result.put("respCode", "01");
			result.put("respMsg", json.getString("message"));
			System.out.println(json.getString("message"));
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	
	public Map<String, String> otherInvokeCardPay(PayRequest payRequest,PmsAppTransInfo appTransInfo,String rateStr,PmsMerchantInfo merchantinfo,Map<String, String> result) throws Exception{
		
		// 查看当前交易是否已经生成了流水表
				PospTransInfo pospTransInfo = null;
				// 流水表是否需要更新的标记 0 insert，1：update
				int insertOrUpdateFlag = 0;
				log.info("***************进入payHandle5-14-3***************");
				// 生成上送流水号
				String transOrderId = payRequest.getOrderId();
				log.info("***************进入payHandle5-15***************");
				if ((pospTransInfo = pospTransInfoDAO
						.searchByOrderId(payRequest.getOrderId())) != null) {
					// 已经存在，修改流水号，设置pospsn为空
					log.info("订单号：" + payRequest.getOrderId()
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
		        PmsBusinessPos pmsBusinessPos =selectKey(payRequest.getMerchantId());
		        String channelKey = YSUtil.channelKey;
				String channel_sign_method = "SHA256";
				Map<String, Object> reqMap = new TreeMap<String, Object>();
				reqMap.put("sp_id", YSUtil.sp_id);
				reqMap.put("mch_id", "BJ"+pmsBusinessPos.getBusinessnum());//商户号YSUtil.merId3
				reqMap.put("out_trade_no",payRequest.getOrderId());
				reqMap.put("swpaccid", payRequest.getMerchantCode());
				reqMap.put("total_fee", payRequest.getAmount());
				reqMap.put("body", payRequest.getProductName());
				reqMap.put("acc_type", "CREDIT");
				reqMap.put("acc_name", payRequest.getAcctName());
				reqMap.put("acc_no", payRequest.getAcctNo());
				reqMap.put("mobile", payRequest.getPhone());
				reqMap.put("bank_code", payRequest.getPmsbankNo());
				reqMap.put("id_type", "01");
				reqMap.put("id_no", payRequest.getLiceneceNo());
				reqMap.put("front_notify_url", YSUtil.returnUrl);
				reqMap.put("back_notify_url", YSUtil.notifyUrl);
				Date t = new Date();
				java.util.Calendar cal = java.util.Calendar.getInstance();
				cal.setTime(t);
				long sys_timestamp = cal.getTimeInMillis();
				reqMap.put("timestamp", sys_timestamp);
				
				StringBuilder sb = new StringBuilder();
		        Set<String> keySet = reqMap.keySet();
		        Iterator<String> iter = keySet.iterator();
		        while (iter.hasNext()) {
		        	String key = iter.next();
		            sb.append(key);
		            sb.append("=");
		            sb.append(reqMap.get(key));
		            sb.append("&");
		        }
		        String sign = SwpHashUtil.getSign(sb.toString()+"key="+channelKey, channelKey, channel_sign_method);
		        reqMap.put("sign", sign);
		        
		        sb.append("sign");
		        sb.append("=");
		        sb.append(sign);
		        System.out.println(sb.toString());
		        String url =YSUtil.url+"/swp/ybbh/b2_preorder.do";
				HttpResponse httpResponse =HttpUtils.doPost(url, "", sb.toString(), "application/x-www-form-urlencoded; charset=UTF-8");
				String resp =EntityUtils.toString(httpResponse.getEntity());
				System.out.println("接受请求:"+resp);
				JSONObject json =JSONObject.parseObject(resp);

				if("SUCCESS".equals(json.getString("status"))) {
					result.put("respCode", "00");
					result.put("respMsg", "请求成功");
					if("SUCCESS".equals(json.getString("trade_state"))) {
						
						result.put("code", "00");
						result.put("message", json.getString("trade_state_desc"));
						result.put("html",json.getString("page_content"));
						result.put("orderId",json.getString("sys_trade_no"));
					}else {
						result.put("code", "01");
						result.put("respMsg", json.getString("trade_state_desc"));
					}
				}else {
					result.put("respCode", "01");
					result.put("respMsg", json.getString("message"));
					System.out.println(json.getString("message"));
				}
				
				
			return result;
		        
	}
	/**
	 * C2快捷短信
	 * @param payRequest
	 * @param appTransInfo
	 * @param rateStr
	 * @param merchantinfo
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> cardPay(PayRequest payRequest,PmsAppTransInfo appTransInfo,String rateStr,PmsMerchantInfo merchantinfo,Map<String, String> result) throws Exception{
		
		// 查看当前交易是否已经生成了流水表
				PospTransInfo pospTransInfo = null;
				// 流水表是否需要更新的标记 0 insert，1：update
				int insertOrUpdateFlag = 0;
				log.info("***************进入payHandle5-14-3***************");
				// 生成上送流水号
				String transOrderId = payRequest.getOrderId();
				log.info("***************进入payHandle5-15***************");
				if ((pospTransInfo = pospTransInfoDAO
						.searchByOrderId(payRequest.getOrderId())) != null) {
					// 已经存在，修改流水号，设置pospsn为空
					log.info("订单号：" + payRequest.getOrderId()
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
		        PmsBusinessPos pmsBusinessPos =selectKey(payRequest.getMerchantId());
		        String channelKey = YSUtil.channelKey;
				String channel_sign_method = "SHA256";
				Map<String, Object> reqMap = new TreeMap<String, Object>();
				reqMap.put("sp_id", YSUtil.sp_id);
				reqMap.put("mch_id", pmsBusinessPos.getBusinessnum());//商户号YSUtil.merId1
				reqMap.put("out_trade_no",payRequest.getOrderId());
				reqMap.put("swpaccid", payRequest.getMerchantCode());
				reqMap.put("total_fee", payRequest.getAmount());
				Date t = new Date();
				java.util.Calendar cal = java.util.Calendar.getInstance();
				cal.setTime(t);
				long sys_timestamp = cal.getTimeInMillis();
				reqMap.put("timestamp", sys_timestamp);
				
				StringBuilder sb = new StringBuilder();
		        Set<String> keySet = reqMap.keySet();
		        Iterator<String> iter = keySet.iterator();
		        while (iter.hasNext()) {
		        	String key = iter.next();
		            sb.append(key);
		            sb.append("=");
		            sb.append(reqMap.get(key));
		            sb.append("&");
		        }
		        String sign = SwpHashUtil.getSign(sb.toString()+"key="+channelKey, channelKey, channel_sign_method);
		        reqMap.put("sign", sign);
		        
		        sb.append("sign");
		        sb.append("=");
		        sb.append(sign);
		        System.out.println(sb.toString());
		        String url =YSUtil.url+"/swp/up/sms.do";
				HttpResponse httpResponse =HttpUtils.doPost(url, "", sb.toString(), "application/x-www-form-urlencoded; charset=UTF-8");
				String resp =EntityUtils.toString(httpResponse.getEntity());
				System.out.println("接受请求:"+resp);
				JSONObject json =JSONObject.parseObject(resp);

				if("SUCCESS".equals(json.getString("status"))) {
					result.put("respCode", "00");
					result.put("respMsg", "请求成功");
					if("SUCCESS".equals(json.getString("trade_state"))) {
						result.put("code", "00");
						result.put("message", json.getString("trade_state_desc"));
						result.put("orderId",payRequest.getOrderId());
						result.put("identity", json.getString("sys_trade_no"));//上游商户号
					}else {
						result.put("code", "01");
						result.put("respMsg", json.getString("trade_state_desc"));
					}
				}else {
					result.put("respCode", "01");
					result.put("respMsg", json.getString("message"));
					System.out.println(json.getString("message"));
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
	private int saveOriginAlInfoCardPay(PayRequest payRequest, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(payRequest.getOrderId());
		info.setOrderId(payRequest.getOrderId());
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		//想要传服务器要改实体
		info.setBgUrl(payRequest.getUrl());
		info.setPageUrl(payRequest.getReUrl());
		Double amt = Double.parseDouble(payRequest.getAmount());// 单位分
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
		pmsAppTransInfo.setStatus(xdt.util.Constants.ORDERINITSTATUS);// 订单初始化状态
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
	
	public void updateOrdeId(String state,OriginalOrderInfo originalInfo) throws Exception {
		log.info("返回的状态参数："+state);
		log.info("**************进入修改方法*************************");
		// 流水表transOrderId
		String transOrderId = originalInfo.getOrderId();
		log.info("异步通知回来的订单号:" + transOrderId);
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息：" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息：" + pmsAppTransInfo);
		// 查询结果成功
		if("SUCCESS".equals(state)) {
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
				pospTransInfo.setPospsn(transOrderId);
				
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				int ii =pospTransInfoDAO.updateByOrderId(pospTransInfo);
				log.info("更新流水结果："+ii);
				
			}
		} else if("PAYERROR".equals(state)||"CLOSED".equals(state)){
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("1");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(transOrderId);
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
	}
	
	public synchronized int add(PayRequest payRequest, Map<String, String> result ,String state) throws Exception {
		log.info("进来添加代付订单了");
		BigDecimal b1=new BigDecimal("0");//总金额
		int iii=0;
		PmsMerchantInfo merchantinfo=select(payRequest.getMerchantId());
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		b1 =new BigDecimal(payRequest.getAmount());
		
				 model.setMercId(payRequest.getMerchantId());
				 model.setCount("1");
				 model.setBatchNo(payRequest.getOrderId());
				 model.setIdentity(payRequest.getIdentity());
				 model.setAmount(b1.doubleValue()/100+"");
				 model.setCardno(payRequest.getAcctNo());
				 model.setRealname(payRequest.getAcctName());
				 if(payRequest.getOrderId().indexOf("/A")!=-1){
					 model.setPayamount(b1.doubleValue()/100+"");
				 }else{
					 model.setPayamount("-" +b1.doubleValue()/100);
				 }
				 
				 model.setPmsbankno(payRequest.getSummary());
				 if(payRequest.getOrderId().indexOf("/A")!=-1){
					 model.setTransactionType("代付补款");
				 }else{
					 model.setTransactionType("代付");
				 }
				 model.setPosition(String.valueOf(merchantinfo.getPosition()));
				 model.setRemarks("D0");
				 model.setRecordDescription("批次号:" + payRequest.getOrderId()+"订单号："+payRequest.getIdentity()+ "错误原因:" + result.get("respMsg"));
				 model.setResponsecode(state);
				 model.setOagentno("100333");
				 //手续费
				 BigDecimal PayFree =new BigDecimal("0");
				 Double d ;
				 if(!"".equals(merchantinfo.getPoundageFree())&&merchantinfo.getPoundageFree()!=null) {
						PayFree=new BigDecimal(merchantinfo.getPoundageFree()).divide(new BigDecimal("100"));
						d=b1.multiply(PayFree).doubleValue();//.setScale(1)
					}else {
						d=0.0;
					}
				 String poundage =new BigDecimal(d).add(new BigDecimal(merchantinfo.getPoundage()).multiply(new BigDecimal("100"))).doubleValue()/100+"";
				 model.setPayCounter(poundage);
				 PmsDaifuMerchantInfo daifu = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model);
				 if (daifu == null) {
					iii = pmsDaifuMerchantInfoDao.insert(model);
					log.info("iii:" + iii);
				}
		
				 return iii;
			}
	@Override
	public Map<String, String> selectB2(String orderId,String merchantId, Map<String, String> result) {
		PmsBusinessPos pmsBusinessPos =selectKey(merchantId);
		String channelKey = YSUtil.channelKey;
		String channel_sign_method = "SHA256";
		Map<String, Object> reqMap = new TreeMap<String, Object>();
		reqMap.put("sp_id", YSUtil.sp_id);
		reqMap.put("mch_id", pmsBusinessPos.getBusinessnum());//YSUtil.merId3
		reqMap.put("out_trade_no", orderId);
		Date t = new Date();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(t);
		long sys_timestamp = cal.getTimeInMillis();
		reqMap.put("timestamp", sys_timestamp);
		
		StringBuilder sb = new StringBuilder();
        Set<String> keySet = reqMap.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
        	String key = iter.next();
            sb.append(key);
            sb.append("=");
            sb.append(reqMap.get(key));
            sb.append("&");
        }
        String sign = SwpHashUtil.getSign(sb.toString()+"key="+channelKey, channelKey, channel_sign_method);
        reqMap.put("sign", sign);
        
        sb.append("sign");
        sb.append("=");
        sb.append(sign);
        System.out.println(sb.toString());
        String url =YSUtil.url+"/swp/ybbh/b2_qry.do";
		try {
			HttpResponse httpResponse =HttpUtils.doPost(url, "", sb.toString(), "application/x-www-form-urlencoded; charset=UTF-8");
			String resp =EntityUtils.toString(httpResponse.getEntity());
			System.out.println("接受请求:"+resp);
			JSONObject json =JSONObject.parseObject(resp);

			if("SUCCESS".equals(json.getString("status"))) {
				result.put("respCode", "00");
				result.put("respMsg", "请求成功");
				if("SUCCESS".equals(json.getString("daifu_state"))) {
					result.put("code", "00");
					result.put("message", "代付成功");
				}else if("NOTDF".equals(json.getString("daifu_state"))){
					result.put("code", "200");
					result.put("message", "未代付");
				}else if("PROCESSING".equals(json.getString("daifu_state"))){
					result.put("code", "200");
					result.put("message", "代付中");
				}else if("FAIL".equals(json.getString("daifu_state"))){
					result.put("code", "01");
					result.put("message", "代付失败");
				}
			}else {
				result.put("respCode", "01");
				result.put("respMsg", json.getString("message"));
				System.out.println(json.getString("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
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
		/**
		 * c2开通商户
		 */
		@Override
		public Map<String, String> openC2(PayRequest payRequest, Map<String, String> result) {

			try {
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("mercid", payRequest.getMerchantId());// 商户编号
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

				paramMap.put("mercid", payRequest.getMerchantId());
				paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
				// 微信支付
				paramMap.put("paymentcode", PaymentCodeEnum.moBaoQuickPay.getTypeCode());

				// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
				AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
						.queryAmountAndStatus(paramMap);
				if (appRateTypeAndAmount == null) {
					result.put("respCode", "01");
					result.put("respMsg", "费率为null");
					return result;
				}
				String rateStr = appRateConfig.getRate(); // 商户费率
				if(Double.parseDouble(rateStr)>Double.parseDouble(payRequest.getDebitRate())){
					result.put("respCode", "01");
					result.put("respMsg", "费率不能低于系统费率");
					return result;
				}
				
			result.put("merchantId", payRequest.getMerchantId());
			PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
			Map<String, String> map =new HashMap<>();
			log.info("注册参数进来了："+JSON.toJSONString(payRequest));
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(payRequest.getMerchantId());
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			if(Double.parseDouble(merchantList.get(0).getPoundage())>Double.parseDouble(payRequest.getWithdrawDepositSingleFee())/100){
				result.put("respCode", "01");
				result.put("respMsg", "代付费用不能低于系统费用");
				return result;
			}
			
			String channelKey = YSUtil.channelKey;
			String channel_sign_method = "SHA256";
			Map<String, Object> reqMap = new TreeMap<String, Object>();
			reqMap.put("sp_id", YSUtil.sp_id);//服务商号
			reqMap.put("mch_id", YSUtil.merId2);//商户号
			reqMap.put("out_trade_no",payRequest.getOrderId());
			reqMap.put("id_type", "01");
			reqMap.put("acc_name", payRequest.getAcctName());//持卡人姓名
			if("0".equals(payRequest.getBusinessType())) {
				reqMap.put("acc_type", "PERSONNEL");//PERSONNEL：对私 CORPORATE：对公
			}else if("1".equals(payRequest.getBusinessType())) {
				reqMap.put("acc_type", "CORPORATE");//PERSONNEL：对私 CORPORATE：对公
			}
			reqMap.put("in_acc_no", payRequest.getAcctNo());//卡号
			reqMap.put("mobile", payRequest.getPhone());//手机号
			reqMap.put("id_no", payRequest.getLiceneceNo());//证件号
			reqMap.put("settle_rate", payRequest.getDebitRate());//结算费率
			reqMap.put("extra_rate", payRequest.getWithdrawDepositSingleFee());//T0费率
			Date t = new Date();
			java.util.Calendar cal = java.util.Calendar.getInstance();
			cal.setTime(t);
			long sys_timestamp = cal.getTimeInMillis();
			reqMap.put("timestamp", sys_timestamp);//时间戳
			
			StringBuilder sb = new StringBuilder();
	        Set<String> keySet = reqMap.keySet();
	        Iterator<String> iter = keySet.iterator();
	        while (iter.hasNext()) {
	        	String key = iter.next();
	            sb.append(key);
	            sb.append("=");
	            sb.append(reqMap.get(key));
	            sb.append("&");
	        }
	        String sign = SwpHashUtil.getSign(sb.toString()+"key="+channelKey, channelKey, channel_sign_method);
	        reqMap.put("sign", sign);
	        
	        sb.append("sign");
	        sb.append("=");
	        sb.append(sign);
	        System.out.println(sb.toString());
	        String url=YSUtil.url+"/swp/up/settlecheck.do";
				HttpResponse httpResponse =HttpUtils.doPost(url, "", sb.toString(), "application/x-www-form-urlencoded; charset=UTF-8");
				String resp =EntityUtils.toString(httpResponse.getEntity());
				System.out.println("接受请求:"+resp);
				JSONObject json =JSONObject.parseObject(resp);

				if("SUCCESS".equals(json.getString("status"))) {
					result.put("respCode", "00");
					result.put("respMsg", "请求成功");
					if("SUCCESS".equals(json.getString("trade_state"))) {
						PmsWeixinMerchartInfo merchartInfo =new PmsWeixinMerchartInfo();
						merchartInfo.setAccount(json.getString("sub_mch_id"));//账号
						merchartInfo.setMerchartId(payRequest.getMerchantId());
						merchartInfo.setMerchartName(merchantList.get(0).getMercName());
						merchartInfo.setMerchartNameSort(merchantList.get(0).getShortname());
						merchartInfo.setCertNo(payRequest.getLiceneceNo());//证件号
						merchartInfo.setCardNo(payRequest.getAcctNo());//卡号
						merchartInfo.setRealName(payRequest.getAcctName());//姓名
						merchartInfo.setMobile(payRequest.getPhone());//手机号
						merchartInfo.setAccountType(payRequest.getBusinessType());//账户类型
						merchartInfo.setBankName(payRequest.getBankName());//开户行
						merchartInfo.setPmsBankNo(payRequest.getPmsbankNo());//联行号
						merchartInfo.setProvince(payRequest.getProvince());//省份
						merchartInfo.setCity(payRequest.getCity());//城市
						merchartInfo.setDebitRate(payRequest.getDebitRate());//借记卡费率
						merchartInfo.setWithdrawDepositSingleFee(payRequest.getWithdrawDepositSingleFee());//提现单笔手续费
						merchartInfo.setoAgentNo("100333");
						merchartInfo.setRateCode(payRequest.getMerchantId());
						int i =weixinService.updateRegister(merchartInfo);
						log.info("ii:"+i);
						result.put("code", "00");
						result.put("message", json.getString("trade_state_desc"));
						result.put("merchantCode",json.getString("sub_mch_id"));
					}else {
						result.put("code", "01");
						result.put("respMsg", json.getString("trade_state_desc"));
					}
				}else {
					result.put("respCode", "01");
					result.put("respMsg", json.getString("message"));
					System.out.println(json.getString("message"));
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		
		}
		@Override
		public Map<String, String> updateC2(PayRequest payRequest, Map<String, String> result) {

			log.info("修改参数进来了:"+JSON.toJSONString(payRequest));
			
			try {
				
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mercid", payRequest.getMerchantId());// 商户编号
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

			paramMap.put("mercid", payRequest.getMerchantId());
			paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
			// 微信支付
			paramMap.put("paymentcode", PaymentCodeEnum.moBaoQuickPay.getTypeCode());

			// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
			AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
					.queryAmountAndStatus(paramMap);
			if (appRateTypeAndAmount == null) {
				result.put("respCode", "01");
				result.put("respMsg", "费率为null");
				return result;
			}
			String rateStr = appRateConfig.getRate(); // 商户费率
			if(Double.parseDouble(rateStr)>Double.parseDouble(payRequest.getDebitRate())){
				result.put("respCode", "01");
				result.put("respMsg", "费率不能低于系统费率");
				return result;
			}
			
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(payRequest.getMerchantId());
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			if(Double.parseDouble(merchantList.get(0).getPoundage())>Double.parseDouble(payRequest.getWithdrawDepositSingleFee())/100){
				result.put("respCode", "01");
				result.put("respMsg", "代付费用不能低于系统费用");
				return result;
			}
			
			PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
			log.info("pmsBusinessPos:"+JSON.toJSONString(pmsBusinessPos));
			PmsWeixinMerchartInfo model =new PmsWeixinMerchartInfo();
			model.setMerchartId(payRequest.getMerchantId());
			model.setAccount(payRequest.getMerchantCode());
			log.info("0000");
			List<PmsWeixinMerchartInfo>  merchartInfo =weixinService.selectlist(model);
			PmsWeixinMerchartInfo merchartInfo2=null;
			String resp="";
			if(merchartInfo.size() >0){
				merchartInfo2=merchartInfo.get(0);
				merchartInfo2.setDebitRate(payRequest.getDebitRate());
				merchartInfo2.setWithdrawDepositSingleFee(payRequest.getWithdrawDepositSingleFee());
				merchartInfo2.setAccountType(payRequest.getBusinessType());
				merchartInfo2.setCardNo(payRequest.getAcctNo());//卡号
				merchartInfo2.setMobile(payRequest.getPhone());//手机号
				String channelKey = YSUtil.channelKey;
				String channel_sign_method = "SHA256";
				Map<String, Object> reqMap = new TreeMap<String, Object>();
				reqMap.put("sp_id", YSUtil.sp_id);
				reqMap.put("mch_id", YSUtil.merId2);
				reqMap.put("out_trade_no", payRequest.getOrderId());
				reqMap.put("acc_name", payRequest.getAcctName());
				reqMap.put("in_acc_no", payRequest.getAcctNo());//卡号
				reqMap.put("mobile", payRequest.getPhone());//手机号
				reqMap.put("sub_mch_id", payRequest.getMerchantCode());//证件号
				reqMap.put("settle_rate", payRequest.getDebitRate());//结算费率
				reqMap.put("extra_rate", payRequest.getWithdrawDepositSingleFee());//T0费率
				Date t = new Date();
				java.util.Calendar cal = java.util.Calendar.getInstance();
				cal.setTime(t);
				long sys_timestamp = cal.getTimeInMillis();
				reqMap.put("timestamp", sys_timestamp);
				
				StringBuilder sb = new StringBuilder();
		        Set<String> keySet = reqMap.keySet();
		        Iterator<String> iter = keySet.iterator();
		        while (iter.hasNext()) {
		        	String key = iter.next();
		            sb.append(key);
		            sb.append("=");
		            sb.append(reqMap.get(key));
		            sb.append("&");
		        }
		        String sign = SwpHashUtil.getSign(sb.toString()+"key="+channelKey, channelKey, channel_sign_method);
		        reqMap.put("sign", sign);
		        
		        sb.append("sign");
		        sb.append("=");
		        sb.append(sign);
		        String url =YSUtil.url+"/swp/up/modifySettle.do";
		        System.out.println(sb.toString());
				HttpResponse httpResponse =HttpUtils.doPost(url, "", sb.toString(), "application/x-www-form-urlencoded; charset=UTF-8");
				resp =EntityUtils.toString(httpResponse.getEntity());
			}
			
			System.out.println("接受请求:"+resp);
			JSONObject json =JSONObject.parseObject(resp);
			if("SUCCESS".equals(json.getString("status"))) {
				result.put("respCode", "00");
				result.put("respMsg", "请求成功");
				if("SUCCESS".equals(json.getString("trade_state"))) {
					
					int i =weixinService.updateByPrimaryKeySelective(merchartInfo2);
					if(i==1){
						log.info("修改成功");
					}else {
						throw new SQLException("修改失败:" + json.getString("trade_state_desc"));
					}
					result.put("code", "00");
					result.put("message", json.getString("trade_state_desc"));
				}else {
					result.put("code", "01");
					result.put("respMsg", json.getString("trade_state_desc"));
				}
			}else {
				result.put("respCode", "01");
				result.put("respMsg", json.getString("message"));
				System.out.println(json.getString("message"));
			}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return result;
		
		}
		/**
		 * 快捷绑卡
		 */
		@Override
		public Map<String, String> bindingCard(PayRequest payRequest, Map<String, String> result) {
			
			log.info("快捷绑卡进来了:"+JSON.toJSONString(payRequest));
			try {
			
			String channelKey = YSUtil.channelKey;
			String channel_sign_method = "SHA256";
			Map<String, Object> reqMap = new TreeMap<String, Object>();
			reqMap.put("sp_id", YSUtil.sp_id);//服务商号
			reqMap.put("mch_id", YSUtil.merId2);//商户号
			reqMap.put("out_trade_no",payRequest.getOrderId());
			reqMap.put("id_type", "01");
			reqMap.put("sub_mch_id", payRequest.getMerchantCode());
			reqMap.put("acc_name", payRequest.getAcctName());//持卡人姓名
			reqMap.put("cvn2", payRequest.getCvv2());//
			reqMap.put("expired", payRequest.getMonth()+payRequest.getYear());
			reqMap.put("acc_no", payRequest.getAcctNo());//卡号
			reqMap.put("bankcode", payRequest.getBankAbbr());//银行代码
			reqMap.put("mobile", payRequest.getPhone());//手机号
			reqMap.put("id_no", payRequest.getLiceneceNo());//证件号
			Date t = new Date();
			java.util.Calendar cal = java.util.Calendar.getInstance();
			cal.setTime(t);
			long sys_timestamp = cal.getTimeInMillis();
			reqMap.put("timestamp", sys_timestamp);//时间戳
			
			StringBuilder sb = new StringBuilder();
	        Set<String> keySet = reqMap.keySet();
	        Iterator<String> iter = keySet.iterator();
	        while (iter.hasNext()) {
	        	String key = iter.next();
	            sb.append(key);
	            sb.append("=");
	            sb.append(reqMap.get(key));
	            sb.append("&");
	        }
	        String sign = SwpHashUtil.getSign(sb.toString()+"key="+channelKey, channelKey, channel_sign_method);
	        reqMap.put("sign", sign);
	        
	        sb.append("sign");
	        sb.append("=");
	        sb.append(sign);
	        System.out.println(sb.toString());
	        String url=YSUtil.url+"/swp/up/bindCardBack.do";
			HttpResponse httpResponse =HttpUtils.doPost(url, "", sb.toString(), "application/x-www-form-urlencoded; charset=UTF-8");
			String resp =EntityUtils.toString(httpResponse.getEntity());
			System.out.println("接受请求:"+resp);
			JSONObject json =JSONObject.parseObject(resp);

			if("SUCCESS".equals(json.getString("status"))) {
				result.put("respCode", "00");
				result.put("respMsg", "请求成功");
				if("SUCCESS".equals(json.getString("trade_state"))) {
					result.put("code", "00");
					result.put("message", "绑卡成功");
					result.put("merchantCode", json.getString("swpaccid"));
					PmsDaifuMerchantInfo model=new PmsDaifuMerchantInfo();
					model.setMercId(payRequest.getMerchantId());
					model.setBatchNo(payRequest.getOrderId());
					model.setIdentity(json.getString("swpaccid"));
					model.setCardno(payRequest.getAcctNo());
					model.setRealname(payRequest.getAcctName());
					model.setPmsbankno(json.getString("trade_state_desc"));
					model.setTransactionType("快捷绑卡");
					model.setOagentno("100333");
					model.setResponsecode("00");
					int i= pmsDaifuMerchantInfoDao.insert(model);
					if(i==1){
						log.info("插入鉴权订单成功！！");
					}
				}else {
					result.put("code", "01");
					result.put("respMsg", json.getString("trade_state_desc"));
				}
			}else {
				result.put("respCode", "01");
				result.put("respMsg", json.getString("message"));
				System.out.println(json.getString("message"));
			}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return result;
		}
		/**
		 * 快捷确认支付
		 */
		
		@Override
		public Map<String, String> confirmPay(PayRequest payRequest, Map<String, String> result) {
			log.info("快捷确认支付进来了:"+JSON.toJSONString(payRequest));
			try {
			
			String channelKey = YSUtil.channelKey;
			String channel_sign_method = "SHA256";
			Map<String, Object> reqMap = new TreeMap<String, Object>();
			reqMap.put("sp_id", YSUtil.sp_id);//服务商号
			reqMap.put("mch_id", YSUtil.merId1);//商户号
			reqMap.put("sys_trade_no",payRequest.getIdentity());
			reqMap.put("password", payRequest.getSmsCode());
			reqMap.put("notifyurl", YSUtil.notifyUrl);
			Date t = new Date();
			java.util.Calendar cal = java.util.Calendar.getInstance();
			cal.setTime(t);
			long sys_timestamp = cal.getTimeInMillis();
			reqMap.put("timestamp", sys_timestamp);//时间戳
			
			StringBuilder sb = new StringBuilder();
	        Set<String> keySet = reqMap.keySet();
	        Iterator<String> iter = keySet.iterator();
	        while (iter.hasNext()) {
	        	String key = iter.next();
	            sb.append(key);
	            sb.append("=");
	            sb.append(reqMap.get(key));
	            sb.append("&");
	        }
	        String sign = SwpHashUtil.getSign(sb.toString()+"key="+channelKey, channelKey, channel_sign_method);
	        reqMap.put("sign", sign);
	        
	        sb.append("sign");
	        sb.append("=");
	        sb.append(sign);
	        System.out.println(sb.toString());
	        String url=YSUtil.url+"/swp/up/submit.do";
			HttpResponse httpResponse =HttpUtils.doPost(url, "", sb.toString(), "application/x-www-form-urlencoded; charset=UTF-8");
			String resp =EntityUtils.toString(httpResponse.getEntity());
			System.out.println("接受请求:"+resp);
			JSONObject json =JSONObject.parseObject(resp);

			if("SUCCESS".equals(json.getString("status"))) {
				result.put("respCode", "00");
				result.put("respMsg", "请求成功");
				result.put("orderId", payRequest.getOrderId());
				if("SUCCESS".equals(json.getString("trade_state"))) {
					result.put("code", "00");
					result.put("message", json.getString("trade_state_desc"));
				}else if("PROCESSING".equals(json.getString("trade_state"))){
					result.put("code", "200");
					result.put("message", json.getString("trade_state_desc"));
				}else {
					result.put("code", "01");
					result.put("message", json.getString("trade_state_desc"));
				}
			}else {
				result.put("respCode", "01");
				result.put("respMsg", json.getString("message"));
				System.out.println(json.getString("message"));
			}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return result;
		}
 
		
		
}

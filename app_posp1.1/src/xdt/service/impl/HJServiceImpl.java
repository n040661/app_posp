package xdt.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
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
import xdt.dto.hj.HJPayRequest;
import xdt.dto.hj.HJPayResponse;
import xdt.dto.hj.HJRequest;
import xdt.dto.hj.HJResponse;
import xdt.dto.hj.HJThread;
import xdt.dto.hj.HJUtil;
import xdt.dto.mb.HttpService;
import xdt.dto.mb.MBUtil;
import xdt.dto.sxf.JsonUtils;
import xdt.dto.sxf.PayRequsest;
import xdt.dto.sxf.PayResponse;
import xdt.dto.sxf.SxfThread;
import xdt.dto.tfb.CardPayApplyRequest;
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
import xdt.service.IHJService;
import xdt.service.IPublicTradeVerifyService;
import xdt.util.Constants;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.utils.RequestUtils;
@Service
public class HJServiceImpl extends BaseServiceImpl implements IHJService{

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
	public Map<String, String> cardPay(HJRequest hjRequest,
			Map<String, String> result) {
		log.info("汇聚网关参数来了："+JSON.toJSONString(hjRequest));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = hjRequest.getOrderNo(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = hjRequest.getMerchantNo();

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
				oriInfo.setMerchantOrderId(hjRequest.getOrderNo());//---------------------------
				oriInfo.setPid(hjRequest.getMerchantNo());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoWxPay(hjRequest, out_trade_no, mercId);
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
					// 网关支付
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

									BigDecimal payAmt = new BigDecimal(hjRequest.getAmount());// 收款金额
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
														String totalAmount = hjRequest.getAmount(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															
															result =otherInvokeCardPay(hjRequest, result, appTransInfo);
															
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
	public Map<String, String> otherInvokeCardPay(HJRequest hjRequest,Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception{
		
		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = hjRequest.getOrderNo();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(hjRequest.getOrderNo())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + hjRequest.getOrderNo()
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
         PmsBusinessPos pmsBusinessPos =selectKey(hjRequest.getMerchantNo());//获取上游商户号和秘钥
         try {
     		StringBuilder str = new StringBuilder();
     		str.append(pmsBusinessPos.getBusinessnum());//HJUtil.merchantNo
     		str.append(hjRequest.getOrderNo());
     		str.append(Double.parseDouble(hjRequest.getAmount())/100+"");
     		str.append("1");
     		str.append(hjRequest.getProductName());
     		str.append(hjRequest.getMp()==null?"":hjRequest.getMp());
     		str.append(HJUtil.returnUrl);
     		str.append(HJUtil.notifyUrl);
     		str.append(hjRequest.getFrpCode()==null?"":hjRequest.getFrpCode());
     		str.append(hjRequest.getOrderPeriod()==null?"0":hjRequest.getOrderPeriod());
     		log.info("汇聚待签名数据:"+str.toString());
     		//String hmac =MD5Utils.sign(str.toString(), HJUtil.privateKey, "UTF-8");//RSAUtils.sign(str.toString().getBytes("UTF-8"), HJUtil.privateKey);
     		String hmac =DigestUtils.md5Hex(str.toString() +pmsBusinessPos.getKek() );//HJUtil.privateKey
     		result.put("p1_MerchantNo", pmsBusinessPos.getBusinessnum());
     		result.put("p2_OrderNo", hjRequest.getOrderNo());
     		result.put("p3_Amount", Double.parseDouble(hjRequest.getAmount())/100+"");
     		result.put("p4_Cur", "1");
     		result.put("p5_ProductName",URLEncoder.encode(hjRequest.getProductName(), "utf-8"));
     		if(hjRequest.getMp()!=null){
     			result.put("p6_Mp",URLEncoder.encode( hjRequest.getMp(), "utf-8"));
     		}
     		result.put("p7_ReturnUrl",URLEncoder.encode( HJUtil.returnUrl, "utf-8") );
     		result.put("p8_NotifyUrl", URLEncoder.encode( HJUtil.notifyUrl, "utf-8"));
     		if(hjRequest.getFrpCode()!=null){
     			result.put("p9_FrpCode", hjRequest.getFrpCode());
     		}
     		result.put("pa_OrderPeriod",hjRequest.getOrderPeriod()==null?"0":hjRequest.getOrderPeriod());
     		result.put("hmac", hmac);
     		result.put("cardPayUrl", HJUtil.cardPay);
     		result.put("respCode", "00");
     		result.put("respMsg", "成功");
     		} catch (Exception e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}
		return result;
}
	@Override
	public Map<String, String> scanCode(HJRequest hjRequest,
			Map<String, String> result) {
		log.info("汇聚网关参数来了："+JSON.toJSONString(hjRequest));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = hjRequest.getOrderNo(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = hjRequest.getMerchantNo();

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
				oriInfo.setMerchantOrderId(hjRequest.getOrderNo());//---------------------------
				oriInfo.setPid(hjRequest.getMerchantNo());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoWxPay(hjRequest, out_trade_no, mercId);
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
					//--------------------------------------------------------------------
					// 支付宝支付
					if("ALIPAY_NATIVE".equals(hjRequest.getFrpCode())||"ALIPAY_CARD".equals(hjRequest.getFrpCode())||"ALIPAY_APP".equals(hjRequest.getFrpCode())||"ALIPAY_H5".equals(hjRequest.getFrpCode())||"ALIPAY_FWC".equals(hjRequest.getFrpCode())||"ALIPAY_SYT".equals(hjRequest.getFrpCode())){
						paramMap.put("paymentcode", PaymentCodeEnum.zhifubaoPay.getTypeCode());
					//微信
					}else if("WEIXIN_NATIVE".equals(hjRequest.getFrpCode())||"WEIXIN_CARD".equals(hjRequest.getFrpCode())||"WEIXIN_APP".equals(hjRequest.getFrpCode())||"WEIXIN_H5".equals(hjRequest.getFrpCode())||"WEIXIN_GZH".equals(hjRequest.getFrpCode())||"WEIXIN_XCX".equals(hjRequest.getFrpCode())){
						paramMap.put("paymentcode", PaymentCodeEnum.weixinPay.getTypeCode());
					//京东
					}else if("JD_NATIVE".equals(hjRequest.getFrpCode())||"JD_CARD".equals(hjRequest.getFrpCode())||"JD_APP".equals(hjRequest.getFrpCode())||"JD_H5".equals(hjRequest.getFrpCode())){
						paramMap.put("paymentcode", PaymentCodeEnum.JingDong.getTypeCode());
						//qq
					}else if("QQ_NATIVE".equals(hjRequest.getFrpCode())||"QQ_CARD".equals(hjRequest.getFrpCode())||"QQ_APP".equals(hjRequest.getFrpCode())||"QQ_H5".equals(hjRequest.getFrpCode())){
						paramMap.put("paymentcode", PaymentCodeEnum.QQCodePay.getTypeCode());
						//银联扫码
					}else if("UNIONPAY_NATIVE".equals(hjRequest.getFrpCode())||"UNIONPAY_CARD".equals(hjRequest.getFrpCode())||"UNIONPAY_APP".equals(hjRequest.getFrpCode())||"UNIONPAY_H5".equals(hjRequest.getFrpCode())){
						paramMap.put("paymentcode", PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					}else if("BAIDU_NATIVE".equals(hjRequest.getFrpCode())){
						paramMap.put("paymentcode", PaymentCodeEnum.baiduPay.getTypeCode());
					}else if("SUNING_NATIVE".equals(hjRequest.getFrpCode())){
						paramMap.put("paymentcode", PaymentCodeEnum.suningPay.getTypeCode());
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
								if("ALIPAY_NATIVE".equals(hjRequest.getFrpCode())||"ALIPAY_CARD".equals(hjRequest.getFrpCode())||"ALIPAY_APP".equals(hjRequest.getFrpCode())||"ALIPAY_H5".equals(hjRequest.getFrpCode())||"ALIPAY_FWC".equals(hjRequest.getFrpCode())||"ALIPAY_SYT".equals(hjRequest.getFrpCode())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.GatewayCodePay.getTypeCode());
								//微信
								}else if("WEIXIN_NATIVE".equals(hjRequest.getFrpCode())||"WEIXIN_CARD".equals(hjRequest.getFrpCode())||"WEIXIN_APP".equals(hjRequest.getFrpCode())||"WEIXIN_H5".equals(hjRequest.getFrpCode())||"WEIXIN_GZH".equals(hjRequest.getFrpCode())||"WEIXIN_XCX".equals(hjRequest.getFrpCode())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.weixinPay.getTypeCode());
								//京东
								}else if("JD_NATIVE".equals(hjRequest.getFrpCode())||"JD_CARD".equals(hjRequest.getFrpCode())||"JD_APP".equals(hjRequest.getFrpCode())||"JD_H5".equals(hjRequest.getFrpCode())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.JingDong.getTypeCode());
									//qq
								}else if("QQ_NATIVE".equals(hjRequest.getFrpCode())||"QQ_CARD".equals(hjRequest.getFrpCode())||"QQ_APP".equals(hjRequest.getFrpCode())||"QQ_H5".equals(hjRequest.getFrpCode())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.QQCodePay.getTypeCode());
									//银联扫码
								}else if("UNIONPAY_NATIVE".equals(hjRequest.getFrpCode())||"UNIONPAY_CARD".equals(hjRequest.getFrpCode())||"UNIONPAY_APP".equals(hjRequest.getFrpCode())||"UNIONPAY_H5".equals(hjRequest.getFrpCode())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.hengFengQuickPay.getTypeCode());
								}else if("BAIDU_NATIVE".equals(hjRequest.getFrpCode())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.baiduPay.getTypeCode());
								}else if("SUNING_NATIVE".equals(hjRequest.getFrpCode())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.suningPay.getTypeCode());
								}
								

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(hjRequest.getAmount());// 收款金额
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
										if("ALIPAY_NATIVE".equals(hjRequest.getFrpCode())||"ALIPAY_CARD".equals(hjRequest.getFrpCode())||"ALIPAY_APP".equals(hjRequest.getFrpCode())||"ALIPAY_H5".equals(hjRequest.getFrpCode())||"ALIPAY_FWC".equals(hjRequest.getFrpCode())||"ALIPAY_SYT".equals(hjRequest.getFrpCode())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.GatewayCodePay, mercId);
										//微信
										}else if("WEIXIN_NATIVE".equals(hjRequest.getFrpCode())||"WEIXIN_CARD".equals(hjRequest.getFrpCode())||"WEIXIN_APP".equals(hjRequest.getFrpCode())||"WEIXIN_H5".equals(hjRequest.getFrpCode())||"WEIXIN_GZH".equals(hjRequest.getFrpCode())||"WEIXIN_XCX".equals(hjRequest.getFrpCode())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.weixinPay, mercId);
										//京东
										}else if("JD_NATIVE".equals(hjRequest.getFrpCode())||"JD_CARD".equals(hjRequest.getFrpCode())||"JD_APP".equals(hjRequest.getFrpCode())||"JD_H5".equals(hjRequest.getFrpCode())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.JingDong, mercId);
											//qq
										}else if("QQ_NATIVE".equals(hjRequest.getFrpCode())||"QQ_CARD".equals(hjRequest.getFrpCode())||"QQ_APP".equals(hjRequest.getFrpCode())||"QQ_H5".equals(hjRequest.getFrpCode())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.QQCodePay, mercId);
											//银联扫码
										}else if("UNIONPAY_NATIVE".equals(hjRequest.getFrpCode())||"UNIONPAY_CARD".equals(hjRequest.getFrpCode())||"UNIONPAY_APP".equals(hjRequest.getFrpCode())||"UNIONPAY_H5".equals(hjRequest.getFrpCode())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.hengFengQuickPay, mercId);
										}else if("BAIDU_NATIVE".equals(hjRequest.getFrpCode())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.baiduPay, mercId);
										}else if("SUNING_NATIVE".equals(hjRequest.getFrpCode())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.suningPay, mercId);
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
														String totalAmount = hjRequest.getAmount(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															log.info("1111"+result);
															result =otherInvokescanCodePay(hjRequest, result, appTransInfo);
															
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

	

	public Map<String, String> otherInvokescanCodePay(HJRequest hjRequest,Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception{
		
		Map<String, String> results=new HashMap<>();
		// 查看当前交易是否已经生成了流水表
				PospTransInfo pospTransInfo = null;
				// 流水表是否需要更新的标记 0 insert，1：update
				int insertOrUpdateFlag = 0;
				log.info("***************进入payHandle5-14-3***************");
				// 生成上送流水号
				String transOrderId = hjRequest.getOrderNo();
				log.info("***************进入payHandle5-15***************");
				if ((pospTransInfo = pospTransInfoDAO
						.searchByOrderId(hjRequest.getOrderNo())) != null) {
					// 已经存在，修改流水号，设置pospsn为空
					log.info("订单号：" + hjRequest.getOrderNo()
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
				if("ALIPAY_NATIVE".equals(hjRequest.getFrpCode())||"ALIPAY_CARD".equals(hjRequest.getFrpCode())||"ALIPAY_APP".equals(hjRequest.getFrpCode())||"ALIPAY_H5".equals(hjRequest.getFrpCode())||"ALIPAY_FWC".equals(hjRequest.getFrpCode())||"ALIPAY_SYT".equals(hjRequest.getFrpCode())){
					appTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
					appTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
				//微信
				}else if("WEIXIN_NATIVE".equals(hjRequest.getFrpCode())||"WEIXIN_CARD".equals(hjRequest.getFrpCode())||"WEIXIN_APP".equals(hjRequest.getFrpCode())||"WEIXIN_H5".equals(hjRequest.getFrpCode())||"WEIXIN_GZH".equals(hjRequest.getFrpCode())||"WEIXIN_XCX".equals(hjRequest.getFrpCode())){
					appTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay.getTypeName());
					appTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay.getTypeCode());
				//京东
				}else if("JD_NATIVE".equals(hjRequest.getFrpCode())||"JD_CARD".equals(hjRequest.getFrpCode())||"JD_APP".equals(hjRequest.getFrpCode())||"JD_H5".equals(hjRequest.getFrpCode())){
					appTransInfo.setPaymenttype(PaymentCodeEnum.JingDong.getTypeName());
					appTransInfo.setPaymentcode(PaymentCodeEnum.JingDong.getTypeCode());
					//qq
				}else if("QQ_NATIVE".equals(hjRequest.getFrpCode())||"QQ_CARD".equals(hjRequest.getFrpCode())||"QQ_APP".equals(hjRequest.getFrpCode())||"QQ_H5".equals(hjRequest.getFrpCode())){
					appTransInfo.setPaymenttype(PaymentCodeEnum.QQCodePay.getTypeName());
					appTransInfo.setPaymentcode(PaymentCodeEnum.QQCodePay.getTypeCode());
					//银联扫码
				}else if("UNIONPAY_NATIVE".equals(hjRequest.getFrpCode())||"UNIONPAY_CARD".equals(hjRequest.getFrpCode())||"UNIONPAY_APP".equals(hjRequest.getFrpCode())||"UNIONPAY_H5".equals(hjRequest.getFrpCode())){
					appTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay.getTypeName());
					appTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay.getTypeCode());
				}else if("BAIDU_NATIVE".equals(hjRequest.getFrpCode())){
					appTransInfo.setPaymenttype(PaymentCodeEnum.baiduPay.getTypeName());
					appTransInfo.setPaymentcode(PaymentCodeEnum.baiduPay.getTypeCode());
				}else if("SUNING_NATIVE".equals(hjRequest.getFrpCode())){
					appTransInfo.setPaymenttype(PaymentCodeEnum.suningPay.getTypeName());
					appTransInfo.setPaymentcode(PaymentCodeEnum.suningPay.getTypeCode());
				}
		         pmsAppTransInfoDao.update(appTransInfo);
		    PmsBusinessPos pmsBusinessPos =selectKey(hjRequest.getMerchantNo());//获取上游商户号和秘钥
     		StringBuilder str = new StringBuilder();
     		str.append(HJUtil.Version);
     		str.append(pmsBusinessPos.getBusinessnum());//pmsBusinessPos.getBusinessnum()
     		str.append(hjRequest.getOrderNo());
     		str.append(Double.parseDouble(hjRequest.getAmount())/100+"");
     		str.append("1");
     		str.append(hjRequest.getProductName());
     		str.append(hjRequest.getProductDesc()==null?"":hjRequest.getProductDesc());
     		str.append(hjRequest.getMp()==null?"":hjRequest.getMp());
     		str.append(HJUtil.returnUrl);
     		str.append(HJUtil.notifyUrl);
     		str.append(hjRequest.getFrpCode());
     		str.append(hjRequest.getMerchantBankCode()==null?"":hjRequest.getMerchantBankCode());
     		str.append(hjRequest.getSubMerchantNo()==null?"":hjRequest.getSubMerchantNo());
     		str.append(hjRequest.getIsShowPic()==null?"":hjRequest.getIsShowPic());
     		str.append(hjRequest.getOpenId()==null?"":hjRequest.getOpenId());
     		str.append(hjRequest.getAuthCode()==null?"":hjRequest.getAuthCode());
     		str.append(hjRequest.getAppId()==null?"":hjRequest.getAppId());
     		str.append(hjRequest.getTransactionModel()==null?"":hjRequest.getTransactionModel());
     		log.info("汇聚待签名数据:"+str.toString());
     		//String hmac =MD5Utils.sign(str.toString(), HJUtil.privateKey, "UTF-8");//RSAUtils.sign(str.toString().getBytes("UTF-8"), HJUtil.privateKey);
     		String hmac =DigestUtils.md5Hex(str.toString() + pmsBusinessPos.getKek());//pmsBusinessPos.getKek()
     		results.put("p0_Version",HJUtil.Version);
     		results.put("p1_MerchantNo", pmsBusinessPos.getBusinessnum());
     		results.put("p2_OrderNo", hjRequest.getOrderNo());
     		results.put("p3_Amount", Double.parseDouble(hjRequest.getAmount())/100+"");
     		results.put("p4_Cur", "1");
     		results.put("p5_ProductName",URLEncoder.encode(hjRequest.getProductName(), "utf-8"));
     		if(hjRequest.getProductDesc()!=null){
     			results.put("p6_ProductDesc",URLEncoder.encode(hjRequest.getProductDesc(), "utf-8"));
     		}
     		if(hjRequest.getMp()!=null){
     			results.put("p7_Mp",URLEncoder.encode( hjRequest.getMp(), "utf-8"));
     		}
     		results.put("p8_ReturnUrl",URLEncoder.encode( HJUtil.returnUrl, "utf-8") );
     		results.put("p9_NotifyUrl", URLEncoder.encode( HJUtil.notifyUrl, "utf-8"));
     		results.put("q1_FrpCode", hjRequest.getFrpCode());
     		if(hjRequest.getMerchantBankCode()!=null){
     			results.put("q2_MerchantBankCode", hjRequest.getMerchantBankCode());
     		}
     		if(hjRequest.getSubMerchantNo()!=null){
     			results.put("q3_SubMerchantNo",hjRequest.getSubMerchantNo());
     		}
     		if(hjRequest.getIsShowPic()!=null){
     			results.put("q4_IsShowPic", hjRequest.getIsShowPic());
     		}
     		if(hjRequest.getOpenId()!=null){
     			results.put("q5_OpenId", hjRequest.getOpenId());
     		}
     		if(hjRequest.getAuthCode()!=null){
     			results.put("q6_AuthCode", hjRequest.getAuthCode());	
     		}
     		if(hjRequest.getAppId()!=null){
     			results.put("q7_AppId", hjRequest.getAppId());
     		}
     		if(hjRequest.getTerminalNo()!=null){
     			results.put("q8_TerminalNo", hjRequest.getTerminalNo());
     		}
     		if(hjRequest.getTransactionModel()!=null){
     			results.put("q9_TransactionModel", hjRequest.getTransactionModel());
     		}
     		results.put("hmac",URLEncoder.encode(hmac, "utf-8") );
     		TreeMap<String, String> paramsMap =new TreeMap<>();
     		paramsMap.putAll(results);
     		String paramSrc = RequestUtils.getParamSrc(paramsMap);
     		log.info("汇聚扫码支付给上游发送的数据:"+paramSrc);
     		//String html = RequestUtils.sendPost(HJUtil.scanCodePay, paramSrc);
     		HttpService  HT=new HttpService();
     		String  retuString=HT.POSTReturnString(HJUtil.scanCodePay, paramsMap,MBUtil.codeG);
     		log.info("汇聚返回字符串参数："+retuString);
     		JSONObject json =JSONObject.fromObject(retuString);
     		result.put("merchantNo", hjRequest.getMerchantNo());
     		if("100".equals(json.getString("ra_Code"))){
     			result.put("orderNo", json.getString("r2_OrderNo"));
     			result.put("amount", json.getString("r3_Amount"));
     			result.put("frpCode", json.getString("r6_FrpCode"));
     			result.put("result", json.getString("rc_Result"));
     			result.put("respCode", "00");
     			result.put("respMsg", "请求成功");
     			if(!"".equals(json.getString("rd_Pic"))){
      				result.put("pic", json.getString("rd_Pic"));
      			}
      			if(!"".equals(json.getString("r5_Mp"))){
      				result.put("mp", json.getString("r5_Mp"));
      			}
     		}else{
     			result.put("orderNo", json.getString("r2_OrderNo"));
     			result.put("respCode", "01");
     			result.put("respMsg", json.getString("rb_CodeMsg"));
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
	private int saveOriginAlInfoWxPay(HJRequest hjRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(hjRequest.getOrderNo());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		//想要传服务器要改实体
		info.setBgUrl(hjRequest.getUrl());
		info.setPageUrl(hjRequest.getReUrl());
		Double amt = Double.parseDouble(hjRequest.getAmount());// 单位分
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
	public void update(HJResponse hjResponse,OriginalOrderInfo originalInfo) throws Exception {
		log.info("返回的参数："+hjResponse);
		log.info("**************进入修改方法*************************");
		// 流水表transOrderId
		String transOrderId = "";
		if(hjResponse.getR2_OrderNo()!=""&& hjResponse.getR2_OrderNo()!=null){
			transOrderId=hjResponse.getR2_OrderNo();
		}
		log.info("异步通知回来的订单号:" + transOrderId);
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息：" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息：" + pmsAppTransInfo);
		// 查询结果成功
		if("100".equals(hjResponse.getR6_Status())) {
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
				if(hjResponse.getR2_OrderNo()!=null&& hjResponse.getR2_OrderNo()!=""){
					pospTransInfo.setPospsn(hjResponse.getR2_OrderNo());
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
				if(hjResponse.getR2_OrderNo()!=null&& hjResponse.getR2_OrderNo()!=""){
					pospTransInfo.setPospsn(hjResponse.getR2_OrderNo());
				}
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
			
		}

		
	}
	@Override
	public Map<String, String> select(HJRequest hjRequest,
			Map<String, String> result) {
		try {
		Map<String, String> results =new HashMap<>();
		 PmsBusinessPos pmsBusinessPos =selectKey(hjRequest.getMerchantNo());//获取上游商户号和秘钥
  		StringBuilder str = new StringBuilder();
  		str.append(pmsBusinessPos.getBusinessnum());
  		str.append(hjRequest.getOrderNo());
  		log.info("汇聚待签名数据:"+str.toString());
  		//String hmac =MD5Utils.sign(str.toString(), HJUtil.privateKey, "UTF-8");//RSAUtils.sign(str.toString().getBytes("UTF-8"), HJUtil.privateKey);
  		String hmac =DigestUtils.md5Hex(str.toString() + pmsBusinessPos.getKek());
  		results.put("p1_MerchantNo", pmsBusinessPos.getBusinessnum());
  		results.put("p2_OrderNo", hjRequest.getOrderNo());
  		results.put("hmac",URLEncoder.encode(hmac, "utf-8") );
  		TreeMap<String, String> paramsMap =new TreeMap<>();
  		paramsMap.putAll(results);
  		String paramSrc = RequestUtils.getParamSrc(paramsMap);
  		log.info("汇聚扫码支付给上游发送的数据:"+paramSrc);
  		//String html = RequestUtils.sendPost(HJUtil.scanCodePay, paramSrc);
  		HttpService  HT=new HttpService();
  		String  retuString=HT.POSTReturnString(HJUtil.scanCodePay, paramsMap,MBUtil.codeG);
  		log.info("汇聚返回字符串参数："+retuString);
  		JSONObject json =JSONObject.fromObject(retuString);
  		result.put("merchantNo", hjRequest.getMerchantNo());
  		if("100".equals(json.getString("ra_Code"))){
  			result.put("orderNo", json.getString("r2_OrderNo"));
  			result.put("amount", json.getString("r3_Amount"));
  			result.put("frpCode", json.getString("r6_FrpCode"));
  			result.put("result", json.getString("rc_Result"));
  			result.put("respCode", "00");
  			result.put("respMsg", "请求成功");
  			if(json.getString("rd_Pic")!=null&&json.getString("rd_Pic")!=""){
  				result.put("pic", json.getString("rd_Pic"));
  			}
  			if( json.getString("r5_Mp")!=null&&json.getString("r5_Mp")!=""){
  				result.put("mp", json.getString("r5_Mp"));
  			}
  		}else{
  			result.put("orderNo", json.getString("r2_OrderNo"));
  			result.put("respCode", "01");
  			result.put("respMsg", json.getString("rb_CodeMsg"));
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
				Double dd=0.0;
				if("10036043434".equals(originalInfo.getPid())) {
					dd =(amount*100-poundage);
				}else {
					dd =(amount*100-poundage)*0.8;
				}
				//dd =(amount*100-poundage)*0.8;
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
	public Map<String, String> pay(HJPayRequest hjPayRequest,
			Map<String, String> result) {
		log.info("汇聚----下游传送代付参数:"+JSON.toJSON(hjPayRequest));
		BigDecimal b1=new BigDecimal("0");// 下游上传的总金额
		BigDecimal b2=new BigDecimal("0");// 系统代付余额
		BigDecimal b3=new BigDecimal("0");// 单笔交易总手续费
		BigDecimal PayFree=new BigDecimal("0");//代付手续费率
		BigDecimal min=new BigDecimal("0");// 代付最小金额
		BigDecimal max=new BigDecimal("0");// 代付最大金额
		Double surplus;// 代付剩余金额
		log.info("汇聚----查询当前代付订单是否存在");
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		Map<String, String> map =new HashMap<>();
		Map<String, String> maps=new HashMap<>();//填金
		model.setMercId(hjPayRequest.getMerchantNo());
		model.setBatchNo(hjPayRequest.getBatchNo());
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
			result.put("respCode", "0006");
			result.put("respMsg", "下单失败,订单存在");
			log.info("汇聚----**********************代付 下单失败:{}");
			log.info("汇聚----订单存在");
			return result;
		}
		try {
			label104: {

				log.info("********************汇聚-------------根据商户号查询");
				String e = hjPayRequest.getMerchantNo();
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
					log.info("***********汇聚*************商户信息:" + JSON.toJSONString(merchantinfo));
					if (StringUtils.isBlank(oAgentNo)) {
						throw new RuntimeException("系统错误----------------o单编号为空");
					}

					if ("60".equals(merchantinfo.getMercSts())) {
						// 判断交易类型
						log.info("***********汇聚*************实际金额");
						// 分
						String payAmt= hjPayRequest.getAmount();
						b1 =new BigDecimal(payAmt);
						
						System.out.println("参数:"+b1.doubleValue());
						log.info("***********汇聚*************校验欧单金额限制");
						log.info("汇聚----下游上传代付总金额:" + b1.doubleValue());
						b3 = new BigDecimal(merchantinfo.getPoundage());
						log.info("汇聚----系统商户代付单笔手续费:" + b3.doubleValue());
						Double d ;
						if(!"".equals(merchantinfo.getPoundageFree())&&merchantinfo.getPoundageFree()!=null) {
							log.info("laile");
							PayFree=new BigDecimal(merchantinfo.getPoundageFree()).divide(new BigDecimal("100"));
							d=b1.multiply(PayFree).doubleValue();
						}else {
							log.info("lailenull");
							d=0.0;
						}
						log.info("汇聚----系统商户代付单笔手续费率:" + PayFree.doubleValue());
						min = new BigDecimal(merchantinfo.getMinDaiFu());
						log.info("汇聚----系统代付最小金额:" + min.doubleValue());
						max = new BigDecimal(merchantinfo.getMaxDaiFu());
						log.info("汇聚----系统代付最大金额:" + max.doubleValue());
						b2 = new BigDecimal(merchantinfo.getPosition());
						log.info("汇聚----系统剩余可用额度:" + b2.doubleValue());
						
						
						
						if (b1.doubleValue()+d+ b3.doubleValue()*100 > b2.doubleValue()) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额高于剩余额度");
							log.info("汇聚**********************代付金额高于剩余额度");
							int i = add(hjPayRequest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("汇聚----添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() < min.doubleValue() * 100) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额小于代付最小金额");
							log.info("汇聚**********************代付金额小于代付最小金额");
							int i = add(hjPayRequest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("汇聚--添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() > max.doubleValue() * 100) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额大于代付最大金额");
							log.info("汇聚**********************代付金额大于代付最大金额");
							int i = add(hjPayRequest, merchantinfo, result,"01");
							if (i == 1) {
								log.info("汇聚--添加失败订单成功");
							}
							return result;
						}
						Map<String, String> mapPay=new HashMap<>();
						mapPay.put("machId", hjPayRequest.getMerchantNo());
						mapPay.put("payMoney", b1.doubleValue()+d+"");
						int num =pmsMerchantInfoDao.updataD0(mapPay);
						if (num == 1) {
							log.info("汇聚--扣款成功！！");
						}
							//surplus = b2.subtract(b1).doubleValue() - b3.doubleValue() * 100-b1.multiply(PayFree).doubleValue();
							//merchantinfo.setPosition(surplus.toString());
							
							
							int i =add(hjPayRequest, merchantinfo, result, "200");
							if (i == 1) {
								log.info("汇聚--添加代付扣款订单成功！");
							}
						PmsBusinessPos pmsBusinessPos =selectKey(hjPayRequest.getMerchantNo());
						//int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
						
						if (i == 1) {
							log.info("汇聚--代付订单添加成功");
							//----------------------------------	
							StringBuilder str = new StringBuilder();
							String s =hjPayRequest.getIdentity()+"|"+hjPayRequest.getAccountName()+"|"+hjPayRequest.getBankCard()+"|"+Double.parseDouble(hjPayRequest.getAmount())/100+"|";
							if(hjPayRequest.getRemarks()!=null){
								s+=hjPayRequest.getRemarks()+"|";
							}
							if("10034015555".equals(hjPayRequest.getMerchantNo())||"10034015282".equals(hjPayRequest.getMerchantNo())) {
								s+=hjPayRequest.getCity()+"|1|"+hjPayRequest.getType()+"|"+hjPayRequest.getPmsbankno();
							}else {
								s+=hjPayRequest.getCity()+"|0|"+hjPayRequest.getType()+"|"+hjPayRequest.getPmsbankno();
							}
				     		str.append(pmsBusinessPos.getBusinessnum());
				     		str.append(hjPayRequest.getBatchNo());
				     		str.append(s);
				     		str.append("3");
				     		log.info("汇聚待签名数据:"+str.toString());
				     		String hmac =DigestUtils.md5Hex(str.toString() + pmsBusinessPos.getKek());
				     		map.put("p1_MerchantNo", pmsBusinessPos.getBusinessnum());
				     		map.put("p2_BatchNo", hjPayRequest.getBatchNo());
				     		map.put("p3_Details",URLEncoder.encode(s, "utf-8") );
				     		map.put("p4_ProductType", "3");
				     		map.put("hmac",URLEncoder.encode(hmac, "utf-8") );
				     		TreeMap<String, String> paramsMap =new TreeMap<>();
				     		paramsMap.putAll(map);
				     		log.info("汇聚代付上传参数前数据:"+JSON.toJSONString(paramsMap));
				     		HttpService  HT=new HttpService();
				     		String retuString=HT.POSTReturnString(HJUtil.pay, paramsMap,MBUtil.codeG);
				     		log.info("汇聚返回字符串参数："+retuString);
				     		HJPayResponse payResponse = JsonUtils.fromJson(retuString,HJPayResponse.class);
				     		log.info("汇聚代付返回参数:"+JSON.toJSONString(payResponse));
				     		Double shouxufei =d+b3.doubleValue()*100;//总的代付手续费
				     		if("100".equals(payResponse.getRb_Code())||"102".equals(payResponse.getRb_Code())){
				     			result.put("respCode", "00");
								result.put("respMsg", "代付请求成功！");
								result.put("merchantNo", hjPayRequest.getMerchantNo());
				     			result.put("batchNo", hjPayRequest.getBatchNo());
				     			result.put("amount", hjPayRequest.getAmount());
				     			ThreadPool.executor(new HJThread(pmsMerchantInfoDao, this, hjPayRequest, pmsBusinessPos, merchantinfo, shouxufei));
				     		}else{
				     			result.put("merchantNo", hjPayRequest.getMerchantNo());
				     			result.put("batchNo", hjPayRequest.getBatchNo());
				     			result.put("amount", hjPayRequest.getAmount());
				     			result.put("respCode", "06");
								result.put("respMsg", "代付请求失败！");
				     			UpdateDaifu(hjPayRequest.getBatchNo(), "02");
				     			maps.put("payMoney",(Double.parseDouble(hjPayRequest.getAmount())+shouxufei)+"");
				     			maps.put("machId", hjPayRequest.getMerchantNo());
								int nus = pmsMerchantInfoDao.updataPay(maps);
								if(nus==1){
									log.info("汇聚***补款成功");
									//surplus = surplus+Double.parseDouble(hjPayRequest.getAmount());
									//merchantinfo.setPosition(surplus.toString());
									hjPayRequest.setBatchNo(hjPayRequest.getBatchNo()+"/A");
									int id =add(hjPayRequest, merchantinfo, result, "00");
									if(id==1){
										log.info("汇聚代付补单成功");
									}
								}
				     		}
				     		
				     		
						}
					} else {
						throw new RuntimeException("汇聚***系统错误----------------当前商户非正式商户");
					}

				} else {
					throw new RuntimeException("汇聚***系统错误----------------商户不存在");
				}
				break label104;
			}

		} catch (Exception var43) {
			log.error("汇聚*******************************代付错误", var43);
			try {
				throw var43;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	log.info("***********汇聚*********************代付------处理完成");
	return result;

	}
	public synchronized int add(HJPayRequest hjPayRequest, PmsMerchantInfo merchantinfo, Map<String, String> result ,String state) throws Exception {
		log.info("进来添加代付订单了");
		String positions = "";
		BigDecimal b1=new BigDecimal("0");//总金额
		int iii=0;
		merchantinfo=select(hjPayRequest.getMerchantNo());
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		b1 =new BigDecimal(hjPayRequest.getAmount());
		
				 model.setMercId(hjPayRequest.getMerchantNo());
				 model.setCount("1");
				 model.setBatchNo(hjPayRequest.getBatchNo());
				 model.setIdentity(hjPayRequest.getIdentity());
				 model.setAmount(b1.doubleValue()/100+"");
				 model.setCardno(hjPayRequest.getBankCard());
				 model.setRealname(hjPayRequest.getAccountName());
				 if(hjPayRequest.getBatchNo().indexOf("/A")!=-1){
					 model.setPayamount(b1.doubleValue()/100+"");
				 }else{
					 model.setPayamount("-" +b1.doubleValue()/100);
				 }
				 
				 model.setPmsbankno(hjPayRequest.getPmsbankno());
				 if(hjPayRequest.getBatchNo().indexOf("/A")!=-1){
					 model.setTransactionType("代付补款");
				 }else{
					 model.setTransactionType("代付");
				 }
				 model.setPosition(String.valueOf(merchantinfo.getPosition()));
				 model.setRemarks("D0");
				 model.setRecordDescription("批次号:" + hjPayRequest.getBatchNo()+"订单号："+hjPayRequest.getIdentity()+ "错误原因:" + result.get("respMsg"));
				 model.setResponsecode(state);
				 model.setOagentno("100333");
				 model.setIsDisplay("0");
				 model.setIsDelete("1");
				 model.setIsExamine("1");
				 model.setIsAdd("0");
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


			public HJPayResponse paySelect(HJPayRequest hjPayRequest,PmsBusinessPos pmsBusinessPos){
				
				HJPayResponse payResponse =new HJPayResponse();
				StringBuilder str = new StringBuilder();
				try {
				Map<String, String> map =new HashMap<>();
				str.append(pmsBusinessPos.getBusinessnum());
	     		str.append(hjPayRequest.getBatchNo());
	     		log.info("汇聚待签名数据:"+str.toString());
	     		String hmac =DigestUtils.md5Hex(str.toString() + pmsBusinessPos.getKek());
	     		map.put("p1_MerchantNo", pmsBusinessPos.getBusinessnum());
	     		map.put("p2_BatchNo", hjPayRequest.getBatchNo());
	     		map.put("hmac",URLEncoder.encode(hmac, "utf-8") );
	     		TreeMap<String, String> paramsMap =new TreeMap<>();
	     		paramsMap.putAll(map);
	     		log.info("汇聚代付上传参数前数据:"+JSON.toJSONString(paramsMap));
	     		HttpService  HT=new HttpService();
	     		String retuString=HT.POSTReturnString(HJUtil.paySelect, paramsMap,MBUtil.codeG);
	     		log.info("汇聚返回字符串参数："+retuString);
	     		payResponse = JsonUtils.fromJson(retuString,HJPayResponse.class);
	     		log.info("汇聚代付返回参数:"+JSON.toJSONString(payResponse));
				} catch (Exception e) {
					// TODO: handle exception
				}
				return payResponse;
			}
			public static void main(String[] args) {
				String ss ="1%7C%E4%B8%87%E9%93%A0%E7%91%9E%7C6230520050006248275%7C45000.0%7C6%7C%E6%AD%A6%E6%B1%89%7C0%7C2%7C2";
				System.out.println(new BigDecimal("1"));
			}
}

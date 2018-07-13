package xdt.service.impl;


import static com.jiupai.paysdk.entity.enums.Service.QRCODESPDBPREORDER;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.SelectAllMethodGenerator;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ielpm.mer.sdk.secret.CertUtil;
import com.ielpm.mer.sdk.secret.Secret;
import com.ielpm.mer.sdk.secret.SecretConfig;
import com.jiupai.paysdk.entity.requestDTO.QrcodeSpdbPreOrderDTO;
import com.yeepay.shade.com.yeepay.g3.utils.common.CommonUtils;

import net.sf.json.JSONObject;
import xdt.dao.ChannleMerchantConfigKeyDao;
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
import xdt.dto.gateway.entity.GateWayQueryRequestEntity;
import xdt.dto.hj.HJUtil;
import xdt.dto.jp.JpUtil;
import xdt.dto.jsds.JsdsResponseDto;
import xdt.dto.jsds.JsdsUtils;
import xdt.dto.mb.HttpService;
import xdt.dto.mb.MBUtil;
import xdt.dto.scanCode.entity.ScanCodeRequestEntity;
import xdt.dto.scanCode.entity.ScanCodeResponseEntity;
import xdt.dto.scanCode.util.RequestUtil;
import xdt.dto.scanCode.util.ResponseUtil;
import xdt.dto.scanCode.util.ScanCodeUtil;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IScanCodeService;
import xdt.util.Constants;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.RSAUtil;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年4月20日 上午11:05:58 
* 类说明 
*/
@Service
public class ScanCodeServiceImpl extends BaseServiceImpl implements IScanCodeService {

	Logger log =Logger.getLogger(this.getClass());
	@Resource
	private IMerchantMineDao merchantMineDao;

	// 商户信息服务层
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
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
	public Map<String, String> scanCode(ScanCodeRequestEntity entity, Map<String, String> result) {

		log.info("汇聚扫码参数来了："+JSON.toJSONString(entity));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = entity.getV_oid(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = entity.getV_mid();

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
				oriInfo.setMerchantOrderId(out_trade_no);//---------------------------
				oriInfo.setPid(mercId);
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("v_code", "16");
					result.put("v_msg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户
					PmsBusinessPos pmsBusinessPos =selectKey(entity.getV_mid());//获取上游商户号和秘钥
					if(pmsBusinessPos==null){
						result.put("v_code", "18");
						result.put("v_msg", "未找到路由，请联系业务开通！");
						return result;
					}
					if("1".equals(pmsBusinessPos.getOutPay())) {
						result.put("v_code", "19");
						result.put("v_msg", "入金未开通,请联系业务经理!");
						return result;
					}
					saveOrderInfo(entity);
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
					if("ALIPAY_NATIVE".equals(entity.getV_cardType())||"ALIPAY_CARD".equals(entity.getV_cardType())||"ALIPAY_APP".equals(entity.getV_cardType())||"ALIPAY_H5".equals(entity.getV_cardType())||"ALIPAY_FWC".equals(entity.getV_cardType())||"ALIPAY_SYT".equals(entity.getV_cardType())){
						paramMap.put("paymentcode", PaymentCodeEnum.zhifubaoPay.getTypeCode());
					//微信
					}else if("WEIXIN_NATIVE".equals(entity.getV_cardType())||"WEIXIN_CARD".equals(entity.getV_cardType())||"WEIXIN_APP".equals(entity.getV_cardType())||"WEIXIN_H5".equals(entity.getV_cardType())||"WEIXIN_GZH".equals(entity.getV_cardType())||"WEIXIN_XCX".equals(entity.getV_cardType())){
						paramMap.put("paymentcode", PaymentCodeEnum.weixinPay.getTypeCode());
					//京东
					}else if("JD_NATIVE".equals(entity.getV_cardType())||"JD_CARD".equals(entity.getV_cardType())||"JD_APP".equals(entity.getV_cardType())||"JD_H5".equals(entity.getV_cardType())){
						paramMap.put("paymentcode", PaymentCodeEnum.JingDong.getTypeCode());
						//qq
					}else if("QQ_NATIVE".equals(entity.getV_cardType())||"QQ_CARD".equals(entity.getV_cardType())||"QQ_APP".equals(entity.getV_cardType())||"QQ_H5".equals(entity.getV_cardType())){
						paramMap.put("paymentcode", PaymentCodeEnum.QQCodePay.getTypeCode());
						//银联扫码
					}else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())||"UNIONPAY_CARD".equals(entity.getV_cardType())||"UNIONPAY_APP".equals(entity.getV_cardType())||"UNIONPAY_H5".equals(entity.getV_cardType())){
						paramMap.put("paymentcode", PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					//百度
					}else if("BAIDU_NATIVE".equals(entity.getV_cardType())){
						paramMap.put("paymentcode", PaymentCodeEnum.baiduPay.getTypeCode());
					//苏宁
					}else if("SUNING_NATIVE".equals(entity.getV_cardType())){
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
								result.put("v_code", "05");
								result.put("v_msg", "此功能暂时关闭");
							} else {
								log.error(resultInfoForOAgentNo.getMsg());
								result.put("v_code", "05");
								result.put("v_msg", resultInfoForOAgentNo.getMsg());
							}
						} else {

							if ("1".equals(status)) {

								// 判断支付方式时候开通总开关
								ResultInfo payCheckResult = null;
								if("ALIPAY_NATIVE".equals(entity.getV_cardType())||"ALIPAY_CARD".equals(entity.getV_cardType())||"ALIPAY_APP".equals(entity.getV_cardType())||"ALIPAY_H5".equals(entity.getV_cardType())||"ALIPAY_FWC".equals(entity.getV_cardType())||"ALIPAY_SYT".equals(entity.getV_cardType())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.GatewayCodePay.getTypeCode());
								//微信
								}else if("WEIXIN_NATIVE".equals(entity.getV_cardType())||"WEIXIN_CARD".equals(entity.getV_cardType())||"WEIXIN_APP".equals(entity.getV_cardType())||"WEIXIN_H5".equals(entity.getV_cardType())||"WEIXIN_GZH".equals(entity.getV_cardType())||"WEIXIN_XCX".equals(entity.getV_cardType())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.weixinPay.getTypeCode());
								//京东
								}else if("JD_NATIVE".equals(entity.getV_cardType())||"JD_CARD".equals(entity.getV_cardType())||"JD_APP".equals(entity.getV_cardType())||"JD_H5".equals(entity.getV_cardType())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.JingDong.getTypeCode());
									//qq
								}else if("QQ_NATIVE".equals(entity.getV_cardType())||"QQ_CARD".equals(entity.getV_cardType())||"QQ_APP".equals(entity.getV_cardType())||"QQ_H5".equals(entity.getV_cardType())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.QQCodePay.getTypeCode());
								//银联扫码
								}else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())||"UNIONPAY_CARD".equals(entity.getV_cardType())||"UNIONPAY_APP".equals(entity.getV_cardType())||"UNIONPAY_H5".equals(entity.getV_cardType())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.hengFengQuickPay.getTypeCode());
								//百度
								}else if("BAIDU_NATIVE".equals(entity.getV_cardType())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.baiduPay.getTypeCode());
								//苏宁
								}else if("SUNING_NATIVE".equals(entity.getV_cardType())){
									payCheckResult = payTypeControlDao.checkLimit(oAgentNo,
											PaymentCodeEnum.suningPay.getTypeCode());
								}
								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("v_code", "07");
									result.put("v_msg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {
									Double txnAmt=Double.parseDouble(entity.getV_txnAmt())*100;
									BigDecimal payAmt=new BigDecimal(txnAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao.checkLimit(oAgentNo, payAmt,
											TradeTypeEnum.merchantCollect.getTypeCode());
									// 返回不为0，一律按照交易失败处理
									if (!resultInfo.getErrCode().equals("0")) {
										result.put("v_code", "08");
										result.put("v_msg", "交易金额不在申请的范围之内");
										log.info("交易金额不在申请的范围之内");

									} else {
										ResultInfo resultinfo = null;
										// 商户渠道支付方式
										// 商户渠道交易类型
										// 验证支付方式是否开启
										if("ALIPAY_NATIVE".equals(entity.getV_cardType())||"ALIPAY_CARD".equals(entity.getV_cardType())||"ALIPAY_APP".equals(entity.getV_cardType())||"ALIPAY_H5".equals(entity.getV_cardType())||"ALIPAY_FWC".equals(entity.getV_cardType())||"ALIPAY_SYT".equals(entity.getV_cardType())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.GatewayCodePay, mercId);
										//微信
										}else if("WEIXIN_NATIVE".equals(entity.getV_cardType())||"WEIXIN_CARD".equals(entity.getV_cardType())||"WEIXIN_APP".equals(entity.getV_cardType())||"WEIXIN_H5".equals(entity.getV_cardType())||"WEIXIN_GZH".equals(entity.getV_cardType())||"WEIXIN_XCX".equals(entity.getV_cardType())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.weixinPay, mercId);
										//京东
										}else if("JD_NATIVE".equals(entity.getV_cardType())||"JD_CARD".equals(entity.getV_cardType())||"JD_APP".equals(entity.getV_cardType())||"JD_H5".equals(entity.getV_cardType())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.JingDong, mercId);
											//qq
										}else if("QQ_NATIVE".equals(entity.getV_cardType())||"QQ_CARD".equals(entity.getV_cardType())||"QQ_APP".equals(entity.getV_cardType())||"QQ_H5".equals(entity.getV_cardType())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.QQCodePay, mercId);
											//银联扫码
										}else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())||"UNIONPAY_CARD".equals(entity.getV_cardType())||"UNIONPAY_APP".equals(entity.getV_cardType())||"UNIONPAY_H5".equals(entity.getV_cardType())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.hengFengQuickPay, mercId);
										}else if("BAIDU_NATIVE".equals(entity.getV_cardType())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.baiduPay, mercId);
										}else if("SUNING_NATIVE".equals(entity.getV_cardType())){
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
														BigDecimal totalAmount=new BigDecimal(txnAmt).setScale(0, BigDecimal.ROUND_HALF_UP);

														PmsAppTransInfo appTransInfo = this.insertOrder(totalAmount.toString(),rateStr, oAgentNo,pmsBusinessPos, entity);

														if (appTransInfo != null) {
															
															//----------------------------------
															PospTransInfo pospTransInfo = null;
															// 流水表是否需要更新的标记 0 insert，1：update
															int insertOrUpdateFlag = 0;
															log.info("***************进入payHandle5-14-3***************");
															// 生成上送流水号
															String transOrderId = entity.getV_oid();
															log.info("***************进入payHandle5-15***************");
															if ((pospTransInfo = pospTransInfoDAO
																	.searchByOrderId(transOrderId)) != null) {
																// 已经存在，修改流水号，设置pospsn为空
																log.info("订单号：" + transOrderId
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
															if("ALIPAY_NATIVE".equals(entity.getV_cardType())||"ALIPAY_CARD".equals(entity.getV_cardType())||"ALIPAY_APP".equals(entity.getV_cardType())||"ALIPAY_H5".equals(entity.getV_cardType())||"ALIPAY_FWC".equals(entity.getV_cardType())||"ALIPAY_SYT".equals(entity.getV_cardType())){
																appTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
																appTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
															//微信
															}else if("WEIXIN_NATIVE".equals(entity.getV_cardType())||"WEIXIN_CARD".equals(entity.getV_cardType())||"WEIXIN_APP".equals(entity.getV_cardType())||"WEIXIN_H5".equals(entity.getV_cardType())||"WEIXIN_GZH".equals(entity.getV_cardType())||"WEIXIN_XCX".equals(entity.getV_cardType())){
																appTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay.getTypeName());
																appTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay.getTypeCode());
															//京东
															}else if("JD_NATIVE".equals(entity.getV_cardType())||"JD_CARD".equals(entity.getV_cardType())||"JD_APP".equals(entity.getV_cardType())||"JD_H5".equals(entity.getV_cardType())){
																appTransInfo.setPaymenttype(PaymentCodeEnum.JingDong.getTypeName());
																appTransInfo.setPaymentcode(PaymentCodeEnum.JingDong.getTypeCode());
																//qq
															}else if("QQ_NATIVE".equals(entity.getV_cardType())||"QQ_CARD".equals(entity.getV_cardType())||"QQ_APP".equals(entity.getV_cardType())||"QQ_H5".equals(entity.getV_cardType())){
																appTransInfo.setPaymenttype(PaymentCodeEnum.QQCodePay.getTypeName());
																appTransInfo.setPaymentcode(PaymentCodeEnum.QQCodePay.getTypeCode());
																//银联扫码
															}else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())||"UNIONPAY_CARD".equals(entity.getV_cardType())||"UNIONPAY_APP".equals(entity.getV_cardType())||"UNIONPAY_H5".equals(entity.getV_cardType())){
																appTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay.getTypeName());
																appTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay.getTypeCode());
															}else if("BAIDU_NATIVE".equals(entity.getV_cardType())){
																appTransInfo.setPaymenttype(PaymentCodeEnum.baiduPay.getTypeName());
																appTransInfo.setPaymentcode(PaymentCodeEnum.baiduPay.getTypeCode());
															}else if("SUNING_NATIVE".equals(entity.getV_cardType())){
																appTransInfo.setPaymenttype(PaymentCodeEnum.suningPay.getTypeName());
																appTransInfo.setPaymentcode(PaymentCodeEnum.suningPay.getTypeCode());
															}
													        pmsAppTransInfoDao.update(appTransInfo);
													        insertProfit(entity.getV_oid(), entity.getV_txnAmt(), merchantinfo, appTransInfo.getPaymenttype(), entity.getV_channel());
															switch ("YSZF") {//pmsBusinessPos.getChannelnum()
															case "HJZF":
																result =hjScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "JS100669":
																result =jsScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "JP":
																result =jpScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "SHYL":
																result =ylScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "YSB":
																result =ysbScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "CS":
																result =ysbScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "JHJ":
																result =jhjScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "JMZFB":
																result =jmScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "XMMZ":
																result =xmmzScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "WFB":
																result =wfbScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "SJJ":
																result =sjjScanCodePay(entity, result,pmsBusinessPos);
																break;
															case "YSZF":
																result =yszfScanCodePay(entity, result,pmsBusinessPos);
																break;
															default:
																result.put("v_code", "11");
																result.put("v_msg", "未找到路由，请联系业务开通！");
																break;
															}
															
														} else {
															// 交易金额小于收款最低金额
															result.put("v_code", "11");
															result.put("v_msg", "生成订单流水失败");
															log.info("生成订单流水失败");
														}

													} else {

														// 交易金额小于收款最低金额
														result.put("v_code", "10");
														result.put("v_msg", "交易金额大于收款最高金额");
														log.info("交易金额大于收款最高金额");
													}

												} else {
													// 交易金额小于收款最低金额
													result.put("v_code", "09");
													result.put("v_msg", "交易金额小于收款最低金额");
													log.info("交易金额小于收款最低金额");
												}

											} else {
												// 交易金额小于收款最低金额
												result.put("v_code", "12");
												result.put("v_msg", "商户收款关闭");
												log.info("商户交易关闭");
											}
										} else {
											// 交易金额小于收款最低金额
											result.put("v_code", "13");
											result.put("v_msg", "扫码支付关闭");
											log.info("扫码支付关闭");
										}

									}
								}

							} else {
								log.error("此功能暂未开通");
								result.put("v_code", "06");
								result.put("v_msg", "此功能暂未开通");

							}

						}

					} else {
						log.error("没有找到商户费率");
						result.put("v_code", "04");
						result.put("v_msg", "没有找到商户费率");
					}

				} else {
					log.error("不是正式商户!");
					result.put("v_code", "03");
					result.put("v_msg", "不是正式商户");
				}

			} else {
				log.error("商户不存在!");
				result.put("v_code", "02");
				result.put("v_msg", "商户不存在");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("处理异常", e);
		}
		return result;
	
	}

	
	
	public int saveOrderInfo (ScanCodeRequestEntity entity) {
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(entity.getV_oid());// 原始数据的订单编号
		original.setOrderId(entity.getV_oid()); // 为主键
		original.setPid(entity.getV_mid());
		original.setOrderTime(entity.getV_time());
		original.setOrderAmount(entity.getV_txnAmt());
		original.setProcdutName(entity.getV_productDesc());
		original.setProcdutDesc(entity.getV_productDesc());
		original.setPayType(entity.getV_cardType());
		original.setBgUrl(entity.getV_notify_url());
		original.setUrl(entity.getV_return_url());
		original.setCertNo(entity.getV_subMerchantNo());
		original.setBankId(entity.getV_merchantBankCode());
		original.setSumCode(entity.getV_clientIP());
		original.setAttach(entity.getV_attach());
		int ii=0;
		try {
			ii = originalDao.insert(original);
		} catch (Exception e) {
			log.info("插入订单原始表出错~");
			e.printStackTrace();
		}
		
		return ii;
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
	public PmsAppTransInfo insertOrder(String payamount, String rateStr, String oAgentNo,PmsBusinessPos pmsBusinessPos,ScanCodeRequestEntity entity)
			throws Exception {

		System.out.println("12345613454354=" + entity.getV_oid());
		// 查询商户费率
		BigDecimal rate = new BigDecimal(rateStr);
		BigDecimal amount = new BigDecimal(payamount);
		// 成功后订到入库app后台
		PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();

		pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());
		pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
		pmsAppTransInfo.setOrderid(entity.getV_oid());// 上送的订单号

		pmsAppTransInfo.setReasonofpayment(TradeTypeEnum.merchantCollect.getTypeName());
		pmsAppTransInfo.setMercid(entity.getV_mid());
		pmsAppTransInfo.setFactamount(payamount);// 实际金额 按分为最小单位 例如：1元=100分
													// 采用100
		pmsAppTransInfo.setTradetypecode("1");
		pmsAppTransInfo.setOrderamount(payamount);// 订单金额 按分为最小单位 例如：1元=100分
													// 采用100
		pmsAppTransInfo.setStatus(Constants.ORDERINITSTATUS);// 订单初始化状态
		pmsAppTransInfo.setoAgentNo(oAgentNo);// o单编号
		pmsAppTransInfo.setBusinessNum(pmsBusinessPos.getBusinessnum());
		if("0".equals(entity.getV_channel())) {
			pmsAppTransInfo.setSettlementState("D0");
		}else if("1".equals(entity.getV_channel())) {
			pmsAppTransInfo.setSettlementState("T1");
		}
		BigDecimal poundage = amount.multiply(rate);// 手续费
		BigDecimal b = new BigDecimal(0);

		BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
		double fee1 = poundage.doubleValue();
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(entity.getV_mid());
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
				log.info("订单入库失败， 订单号：" + entity.getV_oid() + "，结束时间：" + UtilDate.getDateFormatter() + "。订单详细信息：" + sendString);
				throw new RuntimeException("手动抛出");
			}
		} catch (Exception e) {
			log.info("订单入库失败， 订单号：" + entity.getV_oid() + "，结束时间：" + UtilDate.getDateFormatter() + "。订单详细信息：" + sendString, e);
			throw new RuntimeException("手动抛出");
		}
		return pmsAppTransInfo;

	}
	
	/**
	 * 汇聚给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> hjScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
		Map<String, String> results=new HashMap<>();
		StringBuilder str = new StringBuilder();
 		str.append(HJUtil.Version);
 		str.append(pmsBusinessPos.getBusinessnum());//pmsBusinessPos.getBusinessnum()
 		str.append(entity.getV_oid());
 		str.append(entity.getV_txnAmt());
 		str.append("1");
 		str.append(entity.getV_productName());
 		str.append(entity.getV_productDesc()==null?"":entity.getV_productDesc());
 		str.append(entity.getV_attach()==null?"":entity.getV_attach());
 		str.append(ScanCodeUtil.hjReturnUrl);
 		str.append(ScanCodeUtil.hjNotifyUrl);
 		str.append(entity.getV_cardType());
 		str.append(entity.getV_merchantBankCode()==null?"":entity.getV_merchantBankCode());
 		str.append(entity.getV_subMerchantNo()==null?"":entity.getV_subMerchantNo());
 		str.append("1");//是否展示图片的
 		str.append(entity.getV_openId()==null?"":entity.getV_openId());
 		str.append(entity.getV_authCode()==null?"":entity.getV_authCode());
 		str.append(entity.getV_appId()==null?"":entity.getV_appId());
 		log.info("汇聚待签名数据:"+str.toString());
 		String hmac =DigestUtils.md5Hex(str.toString() + pmsBusinessPos.getKek());//pmsBusinessPos.getKek()
 		results.put("p0_Version",HJUtil.Version);
 		results.put("p1_MerchantNo", pmsBusinessPos.getBusinessnum());
 		results.put("p2_OrderNo", entity.getV_oid());
 		results.put("p3_Amount", entity.getV_txnAmt());
 		results.put("p4_Cur", "1");
 		results.put("p5_ProductName",URLEncoder.encode(entity.getV_productName(), "utf-8"));
 		if(entity.getV_productDesc()!=null){
 			results.put("p6_ProductDesc",URLEncoder.encode(entity.getV_productDesc(), "utf-8"));
 		}
 		if(entity.getV_attach()!=null){
 			results.put("p7_Mp",URLEncoder.encode(entity.getV_attach(), "utf-8"));
 		}
 		results.put("p8_ReturnUrl",URLEncoder.encode( ScanCodeUtil.hjReturnUrl, "utf-8") );
 		results.put("p9_NotifyUrl", URLEncoder.encode( ScanCodeUtil.hjNotifyUrl, "utf-8"));
 		results.put("q1_FrpCode", entity.getV_cardType());
 		if(entity.getV_merchantBankCode()!=null){
 			results.put("q2_MerchantBankCode", entity.getV_merchantBankCode());
 		}
 		if(entity.getV_subMerchantNo()!=null){
 			results.put("q3_SubMerchantNo",entity.getV_subMerchantNo());
 		}
 		results.put("q4_IsShowPic","1");
 		if(entity.getV_openId()!=null){
 			results.put("q5_OpenId", entity.getV_openId());
 		}
 		if(entity.getV_authCode()!=null){
 			results.put("q6_AuthCode", entity.getV_authCode());	
 		}
 		if(entity.getV_appId()!=null){
 			results.put("q7_AppId",entity.getV_appId());
 		}
 		results.put("hmac",URLEncoder.encode(hmac, "utf-8") );
 		TreeMap<String, String> paramsMap =new TreeMap<>();
 		paramsMap.putAll(results);
 		String paramSrc = RequestUtils.getParamSrc(paramsMap);
 		log.info("汇聚扫码支付给上游发送的数据:"+paramSrc);
 		HttpService  HT=new HttpService();
 		String  retuString=HT.POSTReturnString(HJUtil.scanCodePay, paramsMap,MBUtil.codeG);
 		log.info("汇聚返回字符串参数："+retuString);
 		JSONObject json =JSONObject.fromObject(retuString);
 		result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_cardType());
 		if("100".equals(json.getString("ra_Code"))){
 			result.put("v_result", json.getString("rc_Result"));
 			result.put("v_code", "00");
 			result.put("v_msg", "请求成功");
 		}else{
 			result.put("v_code", "01");
 			result.put("v_msg", json.getString("rb_CodeMsg"));
 		}
     		return result;
	}
	
	/**
	 * 江苏电商给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> jsScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
		log.info("下游上送的参数:"+entity);
		log.info("上游通道信息:"+pmsBusinessPos);
		Map<String, String> params = new HashMap<String, String>();
		String transOrderId = entity.getV_oid();
		log.info("下游上送的订单号:"+transOrderId);
		params.put("merchantCode", pmsBusinessPos.getBusinessnum());//JsdsUtils.merchantCode
		params.put("terminalCode", JsdsUtils.terminalCode);
		params.put("orderNum", transOrderId);
		Double txnAmt=Double.parseDouble(entity.getV_txnAmt())*100;
		BigDecimal payAmt=new BigDecimal(txnAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
		params.put("transMoney", payAmt.toString());
		params.put("notifyUrl",ScanCodeUtil.jsdsNotifyUrl);
		params.put("returnUrl",ScanCodeUtil.jsdsReturnUrl);
		String[] stry = { "300fa33635394cf1bb9fcbfebac32259",
				"625f2f03d82b43528cef7d5d9a89a59e",
				"855ea3ecafff457a826fc83520f460d6",
				"6513cad27fd3497ea4a4a0cd643dbb1a",
				"88d30464873847d297b89532486aa38b",
				"93145a745fa24cc2a7e453c5f4dea00f",
				"881850857dac4e07a50b8443e3664123",
				"bf20c371f4c6410d8a0010b70d38d9f1",
				"0215445411e24e9a9da749d151ab6a1d",
				"36656655db6340fdae04be245c179845",
				"4d19ccd88b8e486593e3907b5a82d1cc",
				"7468186ed26144a9876a3d31514f9c41",
				"c4488bfe30f44e9ea051d657012077f4",
				"b2e7b8d93ba944e38da92ad0046ad57f",
				"18dd081ce2ae4a5185fb8ad6eefe6cad",
				"a314e3afd0c240cb8eabb443a4dedb00",
				"d102780837da423782566af102635c3b",
				"b73cbedb3de346c0960eac12b04d7123",
				"7d8ada9f4b9f4c50958186099d4108eb",
				"bb39f8052bc6482591909fdf18852dbc" };
		String[] str = { "20100024000751", "20110124011752",
				"20120224022753", "20130324033754", "20140424044755",
				"20150524055756", "20160624066757", "20170724077758",
				"20180824088759", "20190924099750", "20011024012749",
				"20021124023748", "20031224034747", "20041324045746",
				"20051424056745", "20061524067744", "20071624078743",
				"20081724090742", "20091824090741", "20201924001740" };
		String terminalNum = "";
		String merchantNum = "";
		Random random = new Random();
		int num = random.nextInt(20);
		for (int i = 0; i < 20; i++) {
			if (num == i) {
				terminalNum = stry[i];
				merchantNum = str[i];
				break;
			}
		}
		log.info(
				"终端号:" + terminalNum + "\t" + "门店编号:" + merchantNum);
		params.put("merchantName", entity.getV_mid() == null
				? "支付" : entity.getV_mid());
		params.put("merchantNum", merchantNum);
		params.put("terminalNum", terminalNum);
		String apply = HttpUtil.parseParams(params);
		log.info("生成签名前的数据:" + apply);
		byte[] sign = RSAUtil.encrypt(pmsBusinessPos.getKek(),
				apply.getBytes());
		log.info("上送的签名:" + sign);
		Map<String, String> map = new HashMap<String, String>();
		map.put("groupId",JsdsUtils.groupId);
		switch (entity.getV_cardType()) {
		case "ALIPAY_NATIVE":
			log.info("************************江苏电商----支付宝二维码----处理 开始");
			map.put("service", "SMZF005");
			break;
		case "WEIXIN_NATIVE":
			log.info("************************江苏电商----微信二维码----处理 开始");
			map.put("service", "SMZF004");
			break;
		case "QQ_NATIVE":
			log.info("************************江苏电商----QQ钱包----处理 开始");
			map.put("service", "SMZF016");
			break;
		case "JD_NATIVE":
			log.info("************************江苏电商----京东----处理 开始");
			map.put("service", "SMZF021");
			break;
		case "JD_H5":
			log.info("************************江苏电商----京东H5----处理 开始");
			map.put("service", "SMZF025");
			break;
		default:
			break;
		}
		
		map.put("signType", "RSA");
		map.put("sign", RSAUtil.base64Encode(sign));
		map.put("datetime", UtilDate.getOrderNum());
		log.info("map:"+map);
		String jsonmap = HttpUtil.parseParams(map);
		log.info("上送数据:" + jsonmap);
		String respJson = HttpURLConection.httpURLConnectionPOST(
				JsdsUtils.url,//http://121.41.121.164:8044/TransInterface/TransRequest
				jsonmap);
		log.info("**********江苏电商响应报文:{}" + respJson);
		if (respJson != null) {
			result.put("v_mid", entity.getV_mid());
			result.put("v_oid", entity.getV_oid());
			
			result.put("v_txnAmt", entity.getV_txnAmt());
			result.put("v_cardType", entity.getV_cardType());
			JSONObject ob = JSONObject.fromObject(respJson);
			log.info("封装之后的数据:{}" + ob);
			Iterator it = ob.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (key.equals("pl_code")) {
					String value = ob.getString(key);
					log.info("提交状态:" + "\t" + value);
					result.put("v_code", value);
				}
				if (key.equals("pl_sign")) {
					String value = ob.getString(key);
					log.info("签名:" + "\t" + value);
					result.put("sign", value);
				}
				if (key.equals("pl_datetime")) {
					String value = ob.getString(key);
					log.info("交易时间:" + "\t" + value);
				}
				if (key.equals("pl_message")) {
					String value = ob.getString(key);
					log.info("交易描述:" + "\t" + value);
					result.put("v_msg", value);
				}
			}
			if (result.get("v_code").equals("0000")) {
				result.put("v_code", "00");
				result.put("v_mid", entity.getV_mid());
		 		result.put("v_attach", entity.getV_attach());
		 		result.put("v_txnAmt", entity.getV_txnAmt());
		 		result.put("v_oid", entity.getV_oid());
		 		result.put("v_cardType", entity.getV_cardType());
				if("QQ_NATIVE".equals(entity.getV_cardType()))
				{
					String sign1 = result.get("sign");
					String baseSign = URLDecoder.decode(sign1, "UTF-8");

					baseSign = baseSign.replace(" ", "+");

					byte[] a = RSAUtil.verify(pmsBusinessPos.getKek(),
							RSAUtil.base64Decode(baseSign));

					String Str = new String(a);

					log.info("解析之后的数据:" + Str);

					String[] array = Str.split("\\&");

					log.info("拆分数据:" + array);
					String[] list = array[0].split("\\=");
					if (list[0].equals("orderNum")) {
						log.info("合作商订单号:" + list[1]);

						result.put("v_oid", list[1]);

					}
					String[] list1 = array[1].split("\\=");
					if (list1[0].equals("pl_orderNum")) {
						log.info("平台订单号:" + list1[1]);
						//result.put("v_oid", list1[1]);

					}
					String list2 = array[2].replaceAll("pl_url=", "");
					log.info("上游返回的url:"+list2);
					log.info("URL:" + URLDecoder.decode(list2, "UTF-8") );
					result.put("v_result", URLDecoder.decode(list2, "UTF-8"));
				}else
				{
					String sign1 = result.get("sign");
					String baseSign = URLDecoder.decode(sign1, "UTF-8");

					baseSign = baseSign.replace(" ", "+");

					byte[] a = RSAUtil.verify(pmsBusinessPos.getKek(),
							RSAUtil.base64Decode(baseSign));

					String Str = new String(a);

					log.info("解析之后的数据:" + Str);

					String[] array = Str.split("\\&");

					log.info("拆分数据:" + array);
					String[] list = array[0].split("\\=");
					if (list[0].equals("orderNum")) {
						log.info("合作商订单号:" + list[1]);
						result.put("v_oid", list[1]);
					}
					/*String[] list1 = array[1].split("\\=");
					if (list1[0].equals("pl_orderNum")) {
						log.info("平台订单号:" + list1[1]);
						 result.put("v_oid",
						 list1[1]);

					}*/
					String list2 = array[2].replaceAll("pl_url=", "");
					log.info("URL:" + list2);
					result.put("v_result", URLDecoder.decode(list2, "UTF-8"));
				}
				
			} else {

				result.put("v_code", "00");
				result.put("v_mid", entity.getV_mid());
		 		result.put("v_attach", entity.getV_attach());
		 		result.put("v_txnAmt", entity.getV_txnAmt());
		 		result.put("v_oid", entity.getV_oid());
		 		result.put("v_cardType", entity.getV_cardType());
			}
			//if (result.get("respCode").equals("0000")) {
				// 启线程查询订单状态
				//ThreadPool.executor(new JsPayThread(this,pmsAppTransInfoDao,pospTransInfoDAO,reqData,cmckeyDao));
			//}else{
			//	log.info("生成二维码失败");
			//}
		}	
			result.remove("sign");
     		return result;
	}
	
	/**
	 * 九派给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> jpScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
		xdt.dto.jp.BaseJiupayServiceImpl baseJiupayService = new xdt.dto.jp.BaseJiupayServiceImpl();
		QrcodeSpdbPreOrderDTO dto = new QrcodeSpdbPreOrderDTO();
		dto.setCharset("02");
		dto.setVersion("1.0");
		dto.setRequestTime(entity.getV_time());
		dto.setRequestId(entity.getV_oid());
		dto.setService("qrcodeSpdbPreOrder");
		dto.setSignType("RSA256");
        dto.setMerchantId(pmsBusinessPos.getBusinessnum());
        dto.setClientIP(entity.getV_clientIP());
        if("WEIXIN_NATIVE".equals(entity.getV_cardType())) {
        	dto.setPayChannel("WXP");//微信支付
        }else if("ALIPAY_NATIVE".equals(entity.getV_cardType())) {
        	dto.setPayChannel("ALP");//支付宝
        }else if("".equals(entity.getV_cardType())) {
        	dto.setPayChannel("JDB");//借贷宝
        }else if("QQ_NATIVE".equals(entity.getV_cardType())) {
        	dto.setPayChannel("QQP");//QQ钱包
        }else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())) {
        	dto.setPayChannel("UPOP");//银联活码
        }
        dto.setOrderId(entity.getV_oid());
        Double txnAmt=Double.parseDouble(entity.getV_txnAmt())*100;
		BigDecimal payAmt=new BigDecimal(txnAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
        dto.setAmount(payAmt.toString());
        dto.setTerminalId("MC"+entity.getV_oid());
        dto.setCorpOrg(dto.getPayChannel());
        dto.setGoodsName(entity.getV_productName());
        dto.setGoodsDesc(entity.getV_productDesc());
        dto.setOfflineNotifyUrl(ScanCodeUtil.jpNotifyUrl);
        dto.setQrMerchantId("");
        String merchantCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".p12";
        String rootcerPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//rootca.cer";
        String merchantCertPass =pmsBusinessPos.getKek();//"nknEuX"; //秘钥
        String url="";
   	    if("800001400010085".equals(pmsBusinessPos.getBusinessnum())) {
   	    	url =JpUtil.scanCodeTest;
   	    }else {
   	    	url =JpUtil.scanCode;
   	    }
   	    log.info("dto:"+JSON.toJSONString(dto));
   	    log.info("merchantCertPath:"+merchantCertPath);
   	    log.info("rootcerPath:"+rootcerPath);
   	    log.info("url:"+url);
	   	 log.info(QRCODESPDBPREORDER.getService());
	   	log.info(QRCODESPDBPREORDER.getUrl());
	   	log.info(System.getProperty("user.dir"));
   	 
   	    String str= baseJiupayService.doSend(QRCODESPDBPREORDER,dto,merchantCertPath,merchantCertPass,rootcerPath,url);
        log.info("str:"+JSON.toJSONString(str));
        com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(str);
        
        result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_cardType());
		if("IPS00000".equals(json.getString("rspCode"))) {
			result.put("v_result", json.getString("bankUrl"));
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
		}else {
			result.put("v_code", "01");
			result.put("v_msg", json.getString("rspMessage"));
		}
     	return result;
	}
	
	/**
	 * 漪雷给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> ylScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
        TreeMap<String, String> map =new TreeMap<>();
        
        map.put("p3_uno",pmsBusinessPos.getBusinessnum());
        map.put("p3_orderno",entity.getV_oid());
        Double txnAmt=Double.parseDouble(entity.getV_txnAmt())*100;
		BigDecimal payAmt=new BigDecimal(txnAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
        map.put("p3_money",payAmt.toString());
        if("WEIXIN_NATIVE".equals(entity.getV_cardType())) {
        	map.put("p3_type","W0");//微信支付
        }else if("ALIPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("p3_type","A0");//支付宝
        }else if("WEIXIN_GZH".equals(entity.getV_cardType())) {
        	map.put("p3_type","W1");//微信公众号
        }else if("QQ_NATIVE".equals(entity.getV_cardType())) {
        	map.put("p3_type","Q0");//QQ钱包
        }else if("ALIPAY_H5".equals(entity.getV_cardType())) {
        	map.put("p3_type","A1");//支付宝WAP
        }else if("QQ_APP".equals(entity.getV_cardType())) {
        	map.put("p3_type","Q1");
        }
        map.put("p3_ip",entity.getV_clientIP());
        map.put("p3_nurl",ScanCodeUtil.ylNotifyUrl);
        map.put("p3_burl",ScanCodeUtil.hjReturnUrl);
        map.put("p3_body",entity.getV_productName());
        map.put("p3_note",entity.getV_productDesc());
        
        String paramSrc = RequestUtils.getParamSrc(map);
		log.info("签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, pmsBusinessPos.getKek(), "UTF-8").toUpperCase();
		System.out.println(md5);
		paramSrc=paramSrc+"&sign="+md5;
		String url ="http://www.0aai.cn/p3/order";
		String str=RequestUtil.doGetStr(url+"?"+paramSrc);
		System.out.println(str);
		result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_cardType());
		if("iu92aade75eb1222da".equals(pmsBusinessPos.getBusinessnum())) {
			if("ALIPAY_H5".equals(entity.getV_cardType())) {
				result.put("v_result", str);
				result.put("v_code", "0000");
				result.put("v_msg", "请求成功");
				return result;
			}
		}
		com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(str);
		if("0".equals(json.getString("code"))) {
			result.put("v_result", json.getString("p3_purl"));
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
		}else {
			result.put("v_code", "01");
			result.put("v_msg", json.getString("info"));
		}
		
     	return result;
	}
	
	/**
	 * 银生宝给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> ysbScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
		com.uns.inf.api.model.Request map = new com.uns.inf.api.model.Request();
        
        map.put("accountId","1120070626131527001");//pmsBusinessPos.getBusinessnum()
        if("WEIXIN_NATIVE".equals(entity.getV_cardType())) {
        	map.put("payType","2");//微信支付
        }else if("ALIPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("payType","1");//支付宝
        }else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("payType","3");//银联二维码
        }
        map.put("orderId",entity.getV_oid());
        map.put("commodity",entity.getV_productName());
        map.put("amount",entity.getV_txnAmt());
        map.put("responseUrl",ScanCodeUtil.ysbNotifyUrl);
        //map.put("ext",entity.getV_attach());
        
        String paramSrc = RequestUtils.getParamSrcs(map);
		log.info("签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, "123456", "UTF-8").toUpperCase();//pmsBusinessPos.getKek()
		System.out.println(md5);
		map.put("mac", md5);
		log.info(JSON.toJSONString(map));
		//paramSrc=paramSrc+"&mac="+md5;
		String url ="http://180.166.114.156:18082/scancode-pay-front/scanCodePay/applyScanCode";
		String str =RequestUtils.sendPost(url, JSON.toJSONString(map),"UTF-8");
		System.out.println(str);
		result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_cardType());
		
		com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(str);
		if("0000".equals(json.getString("result_code"))) {
			result.put("v_result", json.getString("qrcode"));
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
		}else {
			result.put("v_code", "01");
			result.put("v_msg", json.getString("result_msg"));
		}
		
     	return result;
	}
	
	/**
	 * 收银通给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> jhjScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
		com.uns.inf.api.model.Request map = new com.uns.inf.api.model.Request();
		DecimalFormat df= new DecimalFormat("######0.00");   
		map.put("amount",df.format(Double.parseDouble(entity.getV_txnAmt())));
		map.put("body",entity.getV_productName());
		if("WEIXIN_NATIVE".equals(entity.getV_cardType())) {
			map.put("channel","wxPubQR");//微信支付
		}else if("ALIPAY_NATIVE".equals(entity.getV_cardType())) {
			map.put("channel","alipayQR");//支付宝
		}else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())) {
			map.put("channel","unionpayQR");//银联二维码
		}else if("QQ_NATIVE".equals(entity.getV_cardType())) {
			map.put("channel","qqQr");//qq扫码
		}else if("JD_NATIVE".equals(entity.getV_cardType())) {
			map.put("channel","jdQR");//京东扫码
		}else if("SUNING_NATIVE".equals(entity.getV_cardType())) {
			map.put("channel","suningQr");//苏宁扫码
		}
		map.put("mchId", "000010010");
		map.put("notifyUrl",ScanCodeUtil.jhjNotifyUrl);
		map.put("outTradeNo",entity.getV_oid());
		map.put("settleCycle", "0");
		map.put("subMchId", pmsBusinessPos.getBusinessnum());
		map.put("tradeType","cs.pay.submit");//
		map.put("version", "1.5");
       
        String paramSrc = RequestUtils.getParamSrcs(map);
		log.info("签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, pmsBusinessPos.getKek(), "UTF-8").toUpperCase();//pmsBusinessPos.getKek()
		System.out.println(md5);
		map.put("sign", md5);
		log.info(JSON.toJSONString(map));
		//paramSrc=paramSrc+"&mac="+md5;
		String url ="http://mch.fintech2syx.com/cloud/cloudplatform/api/trade.html";
		String str =RequestUtils.sendPost(url, JSON.toJSONString(map),"UTF-8");
		System.out.println(str);
		result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_cardType());
		
		com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(str);
		if("0".equals(json.getString("returnCode"))) {
			if("0".equals(json.getString("resultCode"))) {
				result.put("v_result", json.getString("codeUrl"));
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
			}else {
				result.put("v_code", "01");
				result.put("v_msg", json.getString("errCodeDes"));
			}
		}else {
			result.put("v_code", "01");
			result.put("v_msg", json.getString("returnMsg"));
		}
		
     	return result;
	}
	/**
	 * 漪雷乘势给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> ylcsScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
        TreeMap<String, String> map =new TreeMap<>();
        
        map.put("tradeType","cs.pay.submit");
        map.put("version", "1.5");
        map.put("mchId", "");
        map.put("subMchId", pmsBusinessPos.getBusinessnum());
        if("WEIXIN_NATIVE".equals(entity.getV_cardType())) {
        	map.put("channel","2");//微信支付
        }else if("ALIPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("channel","1");//支付宝
        }else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("channel","3");//银联二维码
        }
        map.put("outTradeNo",entity.getV_oid());
        map.put("body",entity.getV_productName());
        map.put("amount",entity.getV_txnAmt());
        map.put("notifyUrl",ScanCodeUtil.ylcsNotifyUrl);
        map.put("settleCycle", entity.getV_cardType());
        String paramSrc = RequestUtils.getParamSrc(map);
		log.info("签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, "123456", "UTF-8").toUpperCase();//pmsBusinessPos.getKek()
		System.out.println(md5);
		paramSrc=paramSrc+"&sign="+md5;
		String url ="http://mch.fintech2syx.com/cloud/cloudplatform/api/trade.html";
		String str =RequestUtils.doPost(url, paramSrc,"UTF-8");
		System.out.println(str);
		result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_channel());
		
		com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(str);
		if("0".equals(json.getString("code"))) {
			result.put("v_result", json.getString("p3_purl"));
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
		}else {
			result.put("v_code", "01");
			result.put("v_msg", json.getString("info"));
		}
		
     	return result;
	}
	
	/**
	 * 主付宝金米给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> jmScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
		TreeMap<String, String> map = new TreeMap<>();
		Double txnAmt=Double.parseDouble(entity.getV_txnAmt())*100;
		BigDecimal payAmt=new BigDecimal(txnAmt).setScale(0, BigDecimal.ROUND_HALF_UP);
        map.put("merchant_no",pmsBusinessPos.getBusinessnum());//pmsBusinessPos.getBusinessnum()
        if("WEIXIN_NATIVE".equals(entity.getV_cardType())) {
        	map.put("biz_code","");//支付宝
        }else if("ALIPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("biz_code","0");//微信支付
        }else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("biz_code","");//银联二维码
        }
        map.put("merchant_req_no",entity.getV_oid());
        map.put("subject",entity.getV_productName());
        map.put("order_amt",payAmt.toString());
        map.put("bg_url",ScanCodeUtil.jmNotifyUrl);
        //map.put("ext",entity.getV_attach());
        
        String paramSrc = RequestUtils.getParamSrc(map);
		log.info("签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.md5(paramSrc + "&" + pmsBusinessPos.getKek(),"UTF-8").toUpperCase();
		//String md5 = MD5Utils.signs(paramSrc, "lD0Y4D9X3k90", "UTF-8").toUpperCase();//pmsBusinessPos.getKek()
		System.out.println(md5);
		map.put("sign", md5);
		log.info(JSON.toJSONString(map));
		//paramSrc=paramSrc+"&"+md5;
		String url ="http://api.jinmpay.com/api/alipayCode/create";
		String str =RequestUtils.sendPost(url, JSON.toJSONString(map),"UTF-8");
		System.out.println(str);
		result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_cardType());
		
		com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(str);
		if("00".equals(json.getString("rsp_code"))) {
			result.put("v_result", json.getString("ali_pay_url"));
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
		}else {
			result.put("v_code", "01");
			result.put("v_msg", json.getString("rsp_msg"));
		}
		
     	return result;
	}
	/**
	 * 厦门美智给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> xmmzScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
		TreeMap<String, String> map = new TreeMap<>();
        map.put("merchantId",pmsBusinessPos.getBusinessnum());//pmsBusinessPos.getBusinessnum()
        if("WEIXIN_NATIVE".equals(entity.getV_cardType())) {
        	map.put("service","0002");//weixin
        	map.put("payChannel", "WXP");
        	map.put("corpOrg", "WXP");
        	
        }else if("ALIPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("service","0010");//支付宝支付
        	map.put("payChannel", "ALP");
        	map.put("corpOrg", "ALP");
        }else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("service","010800");//银联二维码
        	map.put("payChannel", "Other");
        	map.put("corpOrg", "ALP");
        }else if("QQ_NATIVE".equals(entity.getV_cardType())) {
        	map.put("service","0015");//qq钱包
        	map.put("payChannel", "QQ");
        	map.put("corpOrg", "ALP");
        }else if("JD_NATIVE".equals(entity.getV_cardType())) {
        	map.put("service","010700");//京东
        	map.put("payChannel", "Other");
        	map.put("corpOrg", "ALP");
        }
        map.put("orderId",entity.getV_oid());
        map.put("transCode", "001");
        map.put("reqDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));
        map.put("reqTime", new SimpleDateFormat("hhMMss").format(new Date()));
        map.put("requestIp", entity.getV_clientIP());
        map.put("dateTime", entity.getV_time());
        map.put("payChannel", "ALP");
        map.put("goodsDesc", entity.getV_productDesc());
        map.put("goodsName",entity.getV_productName());
        map.put("amount",entity.getV_txnAmt());
        map.put("offlineNotifyUrl",ScanCodeUtil.jmNotifyUrl);
        map.put("terminalId", new SimpleDateFormat("ddHHMMss").format(new Date()));
        String paramSrc = RequestUtils.getParamSrc(map);
		log.info("签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, pmsBusinessPos.getKek(), "UTF-8").toUpperCase();
		//String md5 = MD5Utils.md5(paramSrc + "&" + pmsBusinessPos.getKek(),"UTF-8").toUpperCase();
		//String md5 = MD5Utils.signs(paramSrc, "lD0Y4D9X3k90", "UTF-8").toUpperCase();//pmsBusinessPos.getKek()
		System.out.println(md5);
		map.put("sign", md5);
		log.info(JSON.toJSONString(map));
		//paramSrc=paramSrc+"&"+md5;
		String url ="http://paypaul.385mall.top/onlinepay/scanPayApi"; 
		String str =RequestUtils.sendPost(url, JSON.toJSONString(map),"UTF-8");
		System.out.println(str);
		result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_cardType());
		
		com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(str);
		if("00".equals(json.getString("rsp_code"))) {
			result.put("v_result", json.getString("ali_pay_url"));
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
		}else {
			result.put("v_code", "01");
			result.put("v_msg", json.getString("rsp_msg"));
		}
		
     	return result;
	}
	
	
	/**
	 * 微宝付给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> wfbScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
		TreeMap<String, String> map = new TreeMap<>();
        map.put("payKey",pmsBusinessPos.getBusinessnum());//pmsBusinessPos.getBusinessnum()
        if("WEIXIN_H5".equals(entity.getV_cardType())) {
        	map.put("productType","10000203");//weixinH5
        }else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("productType","60000103");//银联二维码
        }else if("QQ_NATIVE".equals(entity.getV_cardType())) {
        	map.put("productType","70000103");//qq钱包
        }else if("JD_NATIVE".equals(entity.getV_cardType())) {
        	map.put("productType","80000103");//京东
        }else if("ALIPAY_H5".equals(entity.getV_cardType())) {
        	map.put("productType","20000203");//支付宝H5
        }
        map.put("outTradeNo",entity.getV_oid());//订单编号
        map.put("orderPrice",entity.getV_txnAmt());//金额
        map.put("orderTime", new SimpleDateFormat("yyyyMMDDHHMMSS").format(new Date()));
        map.put("productName",entity.getV_productName());
        map.put("orderIp", entity.getV_clientIP());
        map.put("notifyUrl",ScanCodeUtil.wbfNotifyUrl);
        map.put("returnUrl",ScanCodeUtil.wbfReturnUrl);
        String paramSrc = RequestUtils.getParamSrc(map);
		log.info("签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.md5(paramSrc+"&paySecret="+pmsBusinessPos.getKek(), "UTF-8").toUpperCase();//pmsBusinessPos.getKek()
		System.out.println(md5);
		map.put("sign", md5);
		log.info(JSON.toJSONString(map));
		String url ="http://192.144.172.91:8080/gateway/cnpPay/initPay"; 
		String str = xdt.dto.scanCode.util.SimpleHttpUtils.httpPost(url, map);
		System.out.println(str);
		result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_cardType());
		
		com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(str);
		if("0000".equals(json.getString("resultCode"))) {
			result.put("v_result", json.getString("payMessage"));
			result.put("v_code", "00");
			result.put("v_msg", "请求成功");
		}else {
			result.put("v_code", "01");
			result.put("v_msg", json.getString("errMsg"));
		}
		
     	return result;
	}
	
	/**
	 * 三境界给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> sjjScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
		if(!"ALIPAY_H5".equals(entity.getV_cardType())){
			result.put("v_mid", entity.getV_mid());
	 		result.put("v_attach", entity.getV_attach());
	 		result.put("v_txnAmt", entity.getV_txnAmt());
	 		result.put("v_oid", entity.getV_oid());
	 		result.put("v_cardType", entity.getV_cardType());
			result.put("v_code", "01");
			result.put("v_msg", "支付类型有误");
			return result;
		}
		TreeMap<String, String> map = new TreeMap<>();
        map.put("mcht_no",pmsBusinessPos.getBusinessnum());//pmsBusinessPos.getBusinessnum()
        map.put("trade_no",entity.getV_oid());//订单编号
        map.put("notify_url",ScanCodeUtil.sjjReturnUrl);
        map.put("totalAmount",new BigDecimal(entity.getV_txnAmt()).multiply(new BigDecimal("100"))+"");//金额
        map.put("subject",entity.getV_productName());
        String paramSrc = RequestUtils.getParamSrc(map);
		log.info("签名前数据**********支付:" + paramSrc);
		String md5 = MD5Utils.sign(paramSrc, pmsBusinessPos.getKek(), "UTF-8").toUpperCase();//pmsBusinessPos.getKek()
		System.out.println(md5);
		map.put("sign", md5);
		log.info(JSON.toJSONString(map));
		String url ="http://47.105.32.53:8080/gyprovider/alipay/alipayScavenging"; 
		String str =RequestUtils.sendPost(url, JSON.toJSONString(map),"UTF-8");
		System.out.println(str);
		result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_cardType());
		
		com.alibaba.fastjson.JSONObject json =com.alibaba.fastjson.JSONObject.parseObject(str);
		if("0".equals(json.getString("status"))) {
			if("000000".equals(json.getString("resultCode"))) {
				result.put("v_result", json.getString("qrcode"));
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
			}else {
				result.put("v_code", "01");
				result.put("v_msg", json.getString("errMsg"));
			}
			
		}else {
			result.put("v_code", "01");
			result.put("v_msg", json.getString("errMsg"));
		}
		
     	return result;
	}
	
	/**
	 * 易势支付给上游发送参数
	 * @param entity
	 * @param result
	 * @param pmsBusinessPos
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> yszfScanCodePay(ScanCodeRequestEntity entity,Map<String, String> result,PmsBusinessPos pmsBusinessPos) throws Exception{
		Map<String, String> resultMap = null;
		TreeMap<String, String> map = new TreeMap<>();
		DecimalFormat df =new DecimalFormat("#");
        map.put("merchantNo",pmsBusinessPos.getBusinessnum());//pmsBusinessPos.getBusinessnum()
        map.put("version","v1");//订单编号
        map.put("channelNo", "05");
        map.put("tranCode", "YS1003");
        map.put("tranFlow", entity.getV_oid());
        map.put("tranDate", entity.getV_time().substring(0,8));
        map.put("tranTime", entity.getV_time().substring(8,14));
        map.put("amount",df.format(new BigDecimal(entity.getV_txnAmt()).multiply(new BigDecimal("100")).doubleValue()));//金额
        if("WEIXIN_NATIVE".equals(entity.getV_cardType())) {
        	map.put("payType", "1"); //微信
        }else if("ALIPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("payType", "2"); //支付宝
        }else if("WEIXIN_GZH".equals(entity.getV_cardType())) {
        	map.put("payType", "3"); //微信公众号
        }else if("ALIPAY_FWC".equals(entity.getV_cardType())) {
        	map.put("payType", "4"); //支付宝服务窗支付 
        }else if("WEIXIN_CARD".equals(entity.getV_cardType())) {
        	map.put("payType", "5"); //微信条码支付
        }else if("ALIPAY_CARD".equals(entity.getV_cardType())) {
        	map.put("payType", "6"); //支付宝条码支付 
        }else if("QQ_NATIVE".equals(entity.getV_cardType())) {
        	map.put("payType", "7"); //QQ 钱包支付 
        }else if("WEIXIN_H5".equals(entity.getV_cardType())) {
        	map.put("payType", "8"); //微信 WAP 支付
        }else if("ALIPAY_H5".equals(entity.getV_cardType())) {
        	map.put("payType", "9"); //支付宝 WAP 支付 
        }else if("WEIXIN_APP".equals(entity.getV_cardType())) {
        	map.put("payType", "10"); //微信 APP 支付 
        }else if("QQ_CARD".equals(entity.getV_cardType())) {
        	map.put("payType", "11"); //QQ 钱包条码支付 
        }else if("ALIPAY_APP".equals(entity.getV_cardType())) {
        	map.put("payType", "12"); //支付宝 APP 支付 
        }else if("UNIONPAY_NATIVE".equals(entity.getV_cardType())) {
        	map.put("payType", "13"); //银联钱包扫码支付 
        }else if("UNIONPAY_CARD".equals(entity.getV_cardType())) {
        	map.put("payType", "14"); //银联钱包条码支付
        }else if("JD_NATIVE".equals(entity.getV_cardType())) {
        	map.put("payType", "17"); //京东钱包支付  
        }
        //此接口特殊的地方
        map.put("bindId",pmsBusinessPos.getDepartmentnum());//"YS2016092315422110117263111723"
        map.put("notifyUrl",ScanCodeUtil.yszfReturnUrl);
        map.put("bizType","09");
        map.put("goodsName", entity.getV_productName());
        String cerPath=new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "/ky/"+pmsBusinessPos.getBusinessnum()+".cer";
        String keyStorePath=new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "/ky/"+pmsBusinessPos.getBusinessnum()+".pfx";
        
        String keyPass=pmsBusinessPos.getKek();
        
        SecretConfig e = new SecretConfig(cerPath, keyStorePath, keyPass);
        Secret secret = new Secret(e);
        // 敏感信息加密
        map.put("buyerName", secret.encrypt("张三"));
        map.put("buyerId", "123456");
        map.put("remark", entity.getV_attach()==null?"付款":entity.getV_attach());
        String paramSrc = RequestUtils.getParamSrc(map);
		log.info("易势支付签名前数据**********支付:" + paramSrc);
		String sign = secret.sign(paramSrc);
		System.out.println(sign);
		map.put("sign", sign);
		log.info(JSON.toJSONString(map));
		String url ="https://paydemo.ielpm.com/paygate/v1/smpay"; 
		String str = xdt.dto.scanCode.util.SimpleHttpUtils.httpPost(url, map);
		System.out.println(str);
		resultMap = ResponseUtil.parseResponse(str, secret);
		result.put("v_mid", entity.getV_mid());
 		result.put("v_attach", entity.getV_attach());
 		result.put("v_txnAmt", entity.getV_txnAmt());
 		result.put("v_oid", entity.getV_oid());
 		result.put("v_cardType", entity.getV_cardType());
		
			if("0000".equals(resultMap.get("rtnCode"))) {
				result.put("v_result", resultMap.get("qrCodeURL"));
				result.put("v_code", "00");
				result.put("v_msg", "请求成功");
			}else {
				result.put("v_code", "01");
				result.put("v_msg", resultMap.get("rtnMsg"));
			}
     	return result;
	}
	public void otherInvoke(ScanCodeResponseEntity result) throws Exception {
		// TODO Auto-generated method stub

		log.info("上游返回的数据" + result);
		// 流水表transOrderId
		String transOrderId = result.getV_oid();
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息" + pmsAppTransInfo);
		// 查询结果成功
		if ("0000".equals(result.getV_status().toString())) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getV_msg().toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// log.info("修改余额");
				// 修改余额
				log.info(pmsAppTransInfo);
				// updateMerchantBanlance(pmsAppTransInfo);
				// 更新流水表
				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(result.getV_oid());
				log.info("更新流水");
				log.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("1001".equals(result.getV_status().toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getV_msg().toString());
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(result.getV_oid());
				log.info("更新流水");
				log.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}

	}
	/**
	 * 80%入金个别户个别处理
	 * @param originalInfo
	 * @return
	 * @throws Exception
	 */
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
	/**
	 * 100%入金个别户个别处理
	 * @param originalInfo
	 * @return
	 * @throws Exception
	 */
	public synchronized int UpdatePmsMerchantInfo1(OriginalOrderInfo originalInfo)
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
				dd =(amount*100-poundage);
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
	 * 扫码查询
	 */
	public Map<String, String> getScanCodeQuick(GateWayQueryRequestEntity query) {
		Map<String, String> result = new HashMap<>();
		OriginalOrderInfo origin = new OriginalOrderInfo();
		String orderid = query.getV_oid();
		log.info("网关查询订单号:" + orderid);
		origin = originalDao.getOriginalOrderInfoByOrderid(orderid);
		PmsAppTransInfo pmsAppTransInfo = null;
		try {
			if (origin != null) {
				pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(origin.getOrderId());
				if (pmsAppTransInfo != null) {
					log.info("pmsAppTransInfo：" + JSON.toJSON(pmsAppTransInfo));

					result.put("v_mid", query.getV_mid());// 商户号
					result.put("v_oid", query.getV_oid());// 订单号
					result.put("v_txnAmt", origin.getOrderAmount());// 金额
					result.put("v_attach", origin.getAttach());// 支付类型
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					if ("0".equals(pmsAppTransInfo.getStatus())) {
						result.put("v_status", "0000");// 支付状态
						result.put("v_status_msg", "支付成功");
					} else if(("1".equals(pmsAppTransInfo.getStatus()))){
						result.put("v_status", "1001");// 支付状态
						result.put("v_status_msg", "支付失败");
					}else{
						result.put("v_status", "200");// 支付状态
						result.put("v_status_msg", "初始化");
					}

				} else {
					result.put("v_code", "15");
					result.put("v_msg", "订单不存在");
				}
			}else {
				result.put("v_code", "15");
				result.put("v_msg", "订单不存在");
			}

		} catch (Exception e) {
			log.info("查询订单异常："+e);
			e.printStackTrace();
		}
		return result;
	}
	public synchronized Map<String, String> handleNofity(JsdsResponseDto result) throws Exception {
		log.info("上游返回的数据" + result);

		Map<String, String> params = new HashMap<String, String>();
		String sign1 = result.getPl_sign();
		String baseSign = URLDecoder.decode(sign1, "UTF-8");

		baseSign = baseSign.replace(" ", "+");

		byte[] a = RSAUtil.verify(
				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSUnSUG5I3Xh2ANLpC5xLe96WCVQG+A5iPBKPqRKBcF2OCdCtwNs8X40nyqYnVWqhkZwGiItT4+wFc04boL1Az01UJiZBLqmOumU0mxyyKCqGwFZakl3LWI4u2IBDuwyde3muXZDWtSDBH1k2BKzOHju3eeSicZu5D7SQ1Hol7AwIDAQAB",
				RSAUtil.base64Decode(baseSign));
		
		String Str = new String(a);

		log.info("解析响应数据:" + Str);
		String[] array = Str.split("\\&");
		log.info("拆分数据:" + array);
		String[] list = array[0].split("\\=");
		if (list[0].equals("orderNum")) {
			log.info("合作商订单号:" + list[1]);

			params.put("orderNum", list[1]);

		}
		String[] list3 = array[1].split("\\=");
		if (list3[0].equals("pl_orderNum")) {
			log.info("合作商订单号:" + list3[1]);

			params.put("pl_orderNum", list3[1]);

		}
		String[] list1 = array[2].split("\\=");
		if (list1[0].equals("pl_payState")) {
			log.info("交易状态:" + list1[1]);
			params.put("pl_payState", list1[1]);

		}
		String[] list2 = array[3].split("\\=");
		if (list2[0].equals("pl_payMessage")) {
			log.info("交易描述:" + list2[1]);
			params.put("pl_payMessage", list2[1]);
		}
		
		return params;

	}
	
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {

		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		if(transInfo!=null) {
			String oderId = transInfo.getOrderId();
			log.info("根据上送订单号  查询商户上送原始信息");
			original = originalDao.getOriginalOrderInfoByOrderid(oderId);
			if(original!=null) {
				return original;
			}
		}
		return original;
	}
	/**
	 * 易势扫码支付查询
	 */
	public Map<String, String> quickYs(String orderId,String merId){
		PmsBusinessPos pmsBusinessPos = selectKey(merId);
		TreeMap<String, String> req = new TreeMap<>();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> resultMap = null;
    	req.put("merchantNo",pmsBusinessPos.getBusinessnum());//
    	req.put("version", "v1");
    	req.put("channelNo", "05");
    	req.put("tranCode", "YS2002");
    	req.put("tranSerialNumY", orderId);
    	String cerPath;
		try {
			cerPath = new File(this.getClass().getResource("/").getPath()).getParentFile()
					.getParentFile().getCanonicalPath() + "/ky/"+pmsBusinessPos.getBusinessnum()+".cer";
		
        String keyStorePath=new File(this.getClass().getResource("/").getPath()).getParentFile()
				.getParentFile().getCanonicalPath() + "/ky/"+pmsBusinessPos.getBusinessnum()+".pfx";
        
        String keyPass=pmsBusinessPos.getKek();
        
        SecretConfig e = new SecretConfig(cerPath, keyStorePath, keyPass);
        Secret secret = new Secret(e);
        String paramSrc = RequestUtils.getParamSrc(req);
		log.info("签名前数据**********支付:" + paramSrc);
		String md5 = secret.sign(paramSrc);
		System.out.println(md5);
		req.put("sign", md5);
		log.info(JSON.toJSONString(req));
		String url ="https://paydemo.ielpm.com/paygate/v1/smpay"; 
		String str = xdt.dto.scanCode.util.SimpleHttpUtils.httpPost(url, req);
		System.out.println(str);
		if (!"".equals(str)) {
	    	resultMap = ResponseUtil.parseResponse(str, secret);
	        map.put("v_code", "00");
	        map.put("v_msg", "请求成功");
	        if ("0000".equals(resultMap.get("rtnCode"))) {
	        	if("0000".equals(resultMap.get("rtnCodeY"))&&"1".equals(resultMap.get("status"))) {
	        		map.put("v_status", "0000");
	        		map.put("v_status_msg", "支付成功");
	        	}else if(!"0000".equals(resultMap.get("rtnCodeY"))&&"2".equals(resultMap.get("status"))) {
	        		map.put("v_status", "1001");
			          map.put("v_status_msg", "交易失败");
	        	}else if(!"0000".equals(resultMap.get("rtnCodeY"))&&"0".equals(resultMap.get("status"))){
	        		
	        	}
	        }else if("2000".equals(resultMap.get("rtnCode"))) {
	        	map.put("v_status", "1001");
		        map.put("v_status_msg", "交易不存在");
	        }
	      } else {
	        map.put("v_code", "01");
	        map.put("v_msg", "请求失败");
	      }
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return resultMap;
	}
	public static void main(String[] args) {
		/*DecimalFormat df1 = new DecimalFormat("######0"); 
		Double txnAmt=Double.parseDouble("5.02");
		System.out.println(txnAmt*100.0);
		System.out.println(df1.format(txnAmt));
		BigDecimal payAmt=new BigDecimal(txnAmt).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
		System.out.println(payAmt.toString());
		System.out.println(df1.format(payAmt));*/
		//String ss="{v_code=00, v_msg=请求成功, v_sign=171B89F93E49562DE30BB8F70B93918D}"; 
		//com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(ss);
		//System.out.println(json);
	}
}

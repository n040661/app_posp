package xdt.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
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
import xdt.dto.jsds.JsdsRequestDto;
import xdt.dto.nbs.alipay.AlipayParamRequest;
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
import xdt.quickpay.nbs.common.util.DateUtil;
import xdt.quickpay.nbs.common.util.RandomUtil;
import xdt.schedule.ThreadPool;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.ITFBService;
import xdt.tools.Client;
import xdt.util.Constants;
import xdt.util.JsdsUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.RSAUtil;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RSAUtils;
import xdt.util.utils.RequestUtils;
@Service
public class TFBServiceImpl extends BaseServiceImpl implements ITFBService {

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
	public Map<String, String> wxPayApply(WxPayApplyRequest applyRequest,Map<String, String> result) {
		log.info("---------------微信qq钱包进来了---------------");
		log.info("上传到server层参数:"+JSON.toJSON(applyRequest));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = applyRequest.getSp_billno(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = applyRequest.getSpid();

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
				oriInfo.setMerchantOrderId(applyRequest.getSp_billno());//---------------------------
				oriInfo.setPid(applyRequest.getSpid());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoWxPay(applyRequest, out_trade_no, mercId);
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

									BigDecimal payAmt = new BigDecimal(applyRequest.getTran_amt());// 收款金额
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
										if("wxpay".equals(applyRequest.getOut_channel())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.weixinPay, mercId);
										}else if("qqpay".equals(applyRequest.getOut_channel())){
											resultinfo = iPublicTradeVerifyService
													.payTypeVerifyMer(PaymentCodeEnum.QQCodePay, mercId);
										}else{
											result.put("respCode", "01");
											result.put("respMsg", "没有添加外接支付方式");
											log.info("没有添加外接支付方式");
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
														String totalAmount = applyRequest.getTran_amt(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															String str =otherInvokeWxPay(applyRequest, resultMap, appTransInfo);
															TreeMap<String, String> map = RequestUtils.Dom2Map(str);
															log.info("上游返回数据:"+JSON.toJSON(map));
															if("00".equals(map.get("retcode"))){
																result.put("respCode", "00");
																result.put("respMsg", map.get("retmsg"));
																result.put("spid", applyRequest.getSpid());
																result.put("sp_billno", map.get("sp_billno"));
																result.put("pay_type", map.get("pay_type"));
																result.put("qrcode", map.get("qrcode"));
																result.put("pay_info", map.get("pay_info"));
																result.put("tran_amt", map.get("tran_amt"));
																
															}else if("205235".equals(map.get("retcode"))){
																result.put("respCode", "205235");
																result.put("respMsg", map.get("retmsg"));
															}else{
																result.put("respCode", map.get("retcode"));
																result.put("respMsg", map.get("retmsg"));
															}
															
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
	 * 微信和qq钱包支付向上游传送数据
	 * @param applyRequest
	 * @param result
	 * @param appTransInfo
	 * @return
	 * @throws Exception
	 */
	public String otherInvokeWxPay(WxPayApplyRequest applyRequest,Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception {
		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = applyRequest.getSp_billno();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(applyRequest.getSp_billno())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + applyRequest.getSp_billno()
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
		if("wxpay".equals(applyRequest.getOut_channel())){
			appTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay.getTypeCode());
		}else if("qqpay".equals(applyRequest.getOut_channel())){
			appTransInfo.setPaymenttype(PaymentCodeEnum.QQCodePay.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.QQCodePay.getTypeCode());
		}
         pmsAppTransInfoDao.update(appTransInfo);
         //获取上游商户号和密钥
         PmsBusinessPos pmsBusinessPos =selectKey(applyRequest.getSpid());
         TreeMap<String, String> paramsMap = new TreeMap<String, String>();
         paramsMap.put("spid",pmsBusinessPos.getBusinessnum());//商户号 
         paramsMap.put("sp_billno", applyRequest.getSp_billno());//商户订单号
         paramsMap.put("spbill_create_ip", applyRequest.getSpbill_create_ip());//商户ip
         paramsMap.put("pay_type", applyRequest.getPay_type());//支付类型
         paramsMap.put("tran_amt", applyRequest.getTran_amt());//交易金额
         paramsMap.put("cur_type", "CNY");//币种类型
         paramsMap.put("notify_url", TFBConfig.notifyUrl);//通知回调URL
         paramsMap.put("pay_show_url", TFBConfig.returnUrl);//成功跳转URL
         paramsMap.put("auth_code", applyRequest.getAuth_code());//二维码
         paramsMap.put("pay_limit",applyRequest.getPay_limit());//支付限制
         paramsMap.put("item_name", applyRequest.getItem_name());//商品描述
         paramsMap.put("item_attach",applyRequest.getItem_attach());//商品附加数据
         paramsMap.put("sp_udid", applyRequest.getSp_udid());//终端设备id
         paramsMap.put("tran_time", new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()));//发起交易时间
         paramsMap.put("bank_mch_name", applyRequest.getBank_mch_name());//三级商户名称
         paramsMap.put("bank_mch_id", applyRequest.getBank_mch_id());//三级商户ID
         paramsMap.put("out_channel", applyRequest.getOut_channel());//外接支付方式
         String paramSrc = RequestUtils.getParamSrc(paramsMap);
         log.info("上传上游前生成签名字符串:"+paramSrc);
         
         String key =pmsBusinessPos.getKek();//"12345";
         log.info("此商户对应上游秘钥:"+key);
         String sign = MD5Utils.sign(paramSrc,key , TFBConfig.serverEncodeType);
         log.info("此商户生成签名:"+sign);
         paramSrc=paramSrc+"&sign="+sign+"&input_charset=UTF-8";
         log.info("加上签名之后的数据:"+paramSrc);
         
         String applyResponse=RequestUtils.doPost(TFBConfig.payApplyApi, paramSrc, "GBK");
        // applyResponse=new String(applyResponse.getBytes("GBK"),"UTF-8");
         log.info("上游返回数据:"+JSON.toJSON(applyResponse));
         //------------------------------------签名错误-----------------------------------
		return applyResponse;
	}
	/**
	 * 网关支付向上游传送数据
	 * @param applyRequest
	 * @param result
	 * @param appTransInfo
	 * @return
	 * @throws Exception
	 */
	public String otherInvokeCardPay(CardPayApplyRequest cardPayApplyＲequest,Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception {
		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = cardPayApplyＲequest.getSpbillno();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(cardPayApplyＲequest.getSpbillno())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + cardPayApplyＲequest.getSpbillno()
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
         PmsBusinessPos pmsBusinessPos =selectKey(cardPayApplyＲequest.getSpid());
         TreeMap<String, String> paramsMap = new TreeMap<String, String>();
         paramsMap.put("spid",pmsBusinessPos.getBusinessnum());//商户号 "1800071515"
         paramsMap.put("sp_userid", "1800689259");//用户号
         paramsMap.put("spbillno", cardPayApplyＲequest.getSpbillno());//商户订单号
         paramsMap.put("money",cardPayApplyＲequest.getMoney() );//交易金额
         paramsMap.put("cur_type", "1");//币种类型
         paramsMap.put("notify_url", TFBConfig.notifyUrl);//通知回调URL
         paramsMap.put("return_url", TFBConfig.returnUrl);//成功跳转URL
         paramsMap.put("errpage_url", cardPayApplyＲequest.getErrpage_url());//错误页面
         paramsMap.put("memo", cardPayApplyＲequest.getMemo());//商品名称
         paramsMap.put("card_type",cardPayApplyＲequest.getCard_type());//银行卡类型
         paramsMap.put("bank_segment",cardPayApplyＲequest.getBank_segment());//银行代号
         paramsMap.put("user_type", cardPayApplyＲequest.getUser_type());//用户类型
         paramsMap.put("expire_time", cardPayApplyＲequest.getExpire_time());//订单有效时长
         paramsMap.put("channel", cardPayApplyＲequest.getChannel());//渠道类型
         paramsMap.put("encode_type", "MD5");
         paramsMap.put("risk_ctrl",cardPayApplyＲequest.getRisk_ctrl());
         String paramSrc = RequestUtils.getParamSrc(paramsMap);
         log.info("上传上游前生成签名字符串:"+paramSrc);
         
         String key =pmsBusinessPos.getKek();//"12345";
         log.info("此商户对应上游秘钥:"+key);
         String sign = MD5Utils.sign(paramSrc,key , "GBK");
         log.info("此商户生成签名:"+sign);
         paramSrc=paramSrc+"&sign="+sign;//+"&input_charset=UTF-8"
         log.info("加上签名之后的数据:"+paramSrc);
         String url =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.GC_PUBLIC_KEY_PATH;
         String publickey = RSAUtils.loadPublicKey(url);
         log.info("url:"+url);
         String cipherData = RSAUtils.encrypt(paramSrc.toString(),"GBK" , publickey);
 		 System.out.println("加密结果:" + cipherData);

         
		return cipherData;
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
		/**
		 * 网关支付
		 */
		@Override
		public Map<String, String> cardPayApply(CardPayApplyRequest cardPayApplyＲequest,
				Map<String, String> result) {
			log.info("---------------网关支付进来了---------------");
			log.info("上传到server层参数:"+JSON.toJSON(cardPayApplyＲequest));
			log.info("根据商户号查询");
			String out_trade_no = "";// 订单号
			out_trade_no = cardPayApplyＲequest.getSpbillno(); // 10业务号2业务细; 订单号
																	// 现根据规则生成订单号
			log.info("根据商户号查询");
			String mercId = cardPayApplyＲequest.getSpid();

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
					oriInfo.setMerchantOrderId(cardPayApplyＲequest.getSpbillno());//---------------------------
					oriInfo.setPid(cardPayApplyＲequest.getSpid());
					oriInfo = originalDao.selectByOriginal(oriInfo);

					if (oriInfo != null) {
						log.error("下单重复");
						result.put("respCode", "16");
						result.put("respMsg", "下单重复");
					} else if ("60".equals(merchantinfo.getMercSts())) {
						// 判断是否为正式商户

						saveOriginAlInfoCardPay(cardPayApplyＲequest, out_trade_no, mercId);
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

										BigDecimal payAmt = new BigDecimal(cardPayApplyＲequest.getMoney());// 收款金额
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
															String totalAmount = cardPayApplyＲequest.getMoney(); // 交易金额

															PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																	totalAmount, mercId, rateStr, oAgentNo);

															if (appTransInfo != null) {
																String str =otherInvokeCardPay(cardPayApplyＲequest, resultMap, appTransInfo);
																result.put("str", str);
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
		 * 单笔代付
		 */
		@Override
		public Map<String, String> payApply(PayRequest payRequest,
				Map<String, String> result) {
				log.info("下游传送代付参数:"+JSON.toJSON(payRequest));
				BigDecimal b1;// 下游上传的金额
				BigDecimal b2;// 系统代付余额
				BigDecimal b3;// 单笔交易手续费
				BigDecimal min;// 代付最小金额
				BigDecimal max;// 代付最大金额
				Double surplus;// 代付剩余金额
				log.info("查询当前代付订单是否存在");
				Map<String, String> map =new HashMap<>();
				Map<String, String> maps =new HashMap<>();
				PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
				model.setMercId(payRequest.getSpid());
				model.setBatchNo(payRequest.getSp_serialno());
				if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
					result.put("respCode", "0006");
					result.put("respMsg", "下单失败,订单存在");
					log.info("**********************代付 下单失败:{}");
					log.info("订单存在");
					return result;
				}
				try {
					label104: {

						log.info("********************天下付-------------根据商户号查询");
						String e = payRequest.getSpid();
						PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
						PmsMerchantInfo merchantinfo1 = new PmsMerchantInfo();
						merchantinfo.setMercId(e);
						merchantinfo1.setMercId(e);
						List merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
						if (merchantList.size() != 0 && !merchantList.isEmpty()) {
							merchantinfo = (PmsMerchantInfo) merchantList.get(0);
							if(merchantinfo.getOpenPay().equals("1")){
								result.put("respCode", "01");
								result.put("respMsg","未开通代付");
								return result;								
							}
							String oAgentNo = merchantinfo.getoAgentNo();
							log.info("***********天下付*************商户信息:" + merchantinfo);
							if (StringUtils.isBlank(oAgentNo)) {
								throw new RuntimeException("系统错误----------------o单编号为空");
							}

							if ("60".equals(merchantinfo.getMercSts())) {
								// 判断交易类型
								log.info("***********天下付*************实际金额");
								// 分
								String factAmount = payRequest.getTran_amt();
								log.info("***********天下付*************校验欧单金额限制");
								b1 = new BigDecimal(factAmount);
								if("1".equals(payRequest.getPay_type())){

									
									log.info("下游上传代付金额:" + b1.doubleValue());
									b2 = new BigDecimal(merchantinfo.getPositionT1());
									log.info("系统剩余可用额度D0:" + b2.doubleValue());
									b3 = new BigDecimal(merchantinfo.getPoundage());
									log.info("系统商户代付手续费:" + b3.doubleValue());
									min = new BigDecimal(merchantinfo.getMinDaiFu());
									log.info("系统代付最小金额:" + min.doubleValue());
									max = new BigDecimal(merchantinfo.getMaxDaiFu());
									log.info("系统代付最大金额:" + max.doubleValue());
									if (b1.doubleValue() + b3.doubleValue()*100 > b2.doubleValue()) {
										result.put("respCode", "0006");
										result.put("respMsg", "下单失败,代付金额高于剩余额度");
										log.info("**********************代付金额高于剩余额度");
										int i = add(payRequest, merchantinfo, result);
										if (i == 1) {
											log.info("添加失败订单成功");
										}
										return result;
									}
									if (b1.doubleValue() < min.doubleValue() * 100) {
										result.put("respCode", "0006");
										result.put("respMsg", "下单失败,代付金额小于代付最小金额");
										log.info("**********************代付金额小于代付最小金额");
										int i = add(payRequest, merchantinfo, result);
										if (i == 1) {
											log.info("添加失败订单成功");
										}
										return result;
									}
									if (b1.doubleValue() > max.doubleValue() * 100) {
										result.put("respCode", "0006");
										result.put("respMsg", "下单失败,代付金额大于代付T1最大金额");
										log.info("**********************代付金额大于代付T1最大金额");
										int i = add(payRequest, merchantinfo, result);
										if (i == 1) {
											log.info("添加失败订单成功");
										}
										return result;
									}
									surplus = b2.subtract(b1).doubleValue() - b3.doubleValue() * 100;
									
									merchantinfo.setPositionT1(surplus.toString());
									int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
									if (num == 1) {
										log.info("扣款成功！！");
									}
									// 代付插入数据
									model.setCount("1");
									if(payRequest.getSp_batch_no()!=""&&payRequest.getSp_batch_no()!=null){
										model.setBatchNo(payRequest.getSp_batch_no());
										model.setIdentity(payRequest.getSp_batch_no());
									}else{
										model.setBatchNo(payRequest.getSp_serialno());
										model.setIdentity(payRequest.getSp_serialno());
									}
									model.setAmount(Double.parseDouble(factAmount) / 100 + "");
									model.setCardno(payRequest.getAcct_id());
									model.setRealname(payRequest.getAcct_name());
									model.setPayamount("-" + Double.parseDouble(factAmount) / 100);
									model.setPmsbankno(payRequest.getBank_settle_no());
									model.setTransactionType("代付");
									model.setPosition(String.valueOf(surplus));
									model.setRemarks("D0");
									model.setRecordDescription("批次号:" + payRequest.getSp_batch_no()+"订单号:"+payRequest.getSp_serialno());
									model.setResponsecode("200");
									model.setOagentno("100333");
									model.setPayCounter(b3.doubleValue() + "");
									int iii = pmsDaifuMerchantInfoDao.insert(model);
									PmsBusinessPos pmsBusinessPos =selectKey(payRequest.getSpid());
									if (iii == 1) {
										log.info("代付订单添加成功");
										TreeMap<String, String> paramsMap = new TreeMap<String, String>();
										paramsMap.put("version","1.0");    //固定填1.0
										paramsMap.put("spid",pmsBusinessPos.getBusinessnum());   //填写国采分配的商户号"1800046681"
										Date date=new Date();
										SimpleDateFormat  format=new SimpleDateFormat("yyyyMMddHHmmss");
										String time=format.format(date);
										paramsMap.put("sp_serialno",payRequest.getSp_serialno()); //商户交易单号，商户保证其在本系统唯一,每次交易入库需要修改订单号
										paramsMap.put("sp_reqtime", time);   //系统发送时间，14位固定长度
										paramsMap.put("tran_amt",payRequest.getTran_amt());    //交易金额，单位为分，不带小数点
										paramsMap.put("cur_type", "1");      //
										paramsMap.put("pay_type", payRequest.getPay_type());      //普通余额支付填 1；垫资代付填3
										paramsMap.put("acct_name",payRequest.getAcct_name());  //收款人姓名
										paramsMap.put("acct_id", payRequest.getAcct_id());   //收款人账号
										paramsMap.put("acct_type", payRequest.getAcct_type());   //0 借记卡， 1 贷记卡， 2 对公账户
										paramsMap.put("mobile", payRequest.getMobile());
										paramsMap.put("bank_name", payRequest.getBank_name());
										paramsMap.put("bank_settle_no", payRequest.getBank_settle_no());  //对私可不值，对公必传
										paramsMap.put("bank_branch_name",payRequest.getBank_branch_name());
										paramsMap.put("business_type",payRequest.getBusiness_type());
										paramsMap.put("memo", new String(payRequest.getMemo().getBytes(), "UTF-8"));
										String paramSrc = RequestUtils.getParamSrc(paramsMap);
								        log.info("上传上游前生成签名字符串:"+paramSrc);
								         
								         String key =pmsBusinessPos.getKek();//"12345";
								         log.info("此商户对应上游秘钥:"+key);
								         String sign = MD5Utils.sign(paramSrc,key , TFBConfig.serverEncodeType);
								         log.info("此商户生成签名:"+sign);
								         paramSrc=paramSrc+"&sign="+sign;
								         log.info("加上签名之后的数据:"+paramSrc);
								         //------------------------

								         String url =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.GC_PUBLIC_KEY_PATH;
								         log.info("url:"+url);
								         String cipherData = RequestUtils.encrypt(paramSrc.toString(), url);
								 		System.out.println("加密结果:" + cipherData);

								 		System.out.println("发起请求--------------------------------------------");
								         String applyResponse=RequestUtils.doPost(TFBConfig.payUrl,"cipher_data=" + URLEncoder.encode(cipherData) , "UTF-8");
								         log.info("代付返回数据1:"+JSON.toJSON(applyResponse));
								       //-----------------------------------------------
								 		String cipherResponseData =RequestUtils.parseXml(applyResponse);
								 		if(!"失败".equals(cipherResponseData)){
								 			log.info("代付返回数据2:"+JSON.toJSON(cipherResponseData));
									 		//自己的私钥------------------到时候要改
									 		String privateKey =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.PRIVATE_KEY_PATH;
									 		
									 		String responseData = RequestUtils.decryptResponseData(cipherResponseData, privateKey);
									 		log.info("responseData："+responseData);
											String sign1 = responseData.substring(responseData.indexOf("sign=") + 5, responseData.length());
											String source = responseData.substring(0, responseData.lastIndexOf("&sign"));
											
											log.info("source:" + source);
											System.out.println("sign:" + sign1);
											//rsa验签
											if (MD5Utils.verify(source, sign1, key, "UTF-8")) {
												System.out.println("验签结果：通过");
												log.info("成功的source:" + JSON.toJSON(source));
												 map = strToMap(source);
												log.info("JSONObject解析数据"+JSON.toJSON(map));
												if("1".equals(map.get("serialno_state"))){
													result.put("respCode", "00");
													result.put("respMsg", map.get("serialno_desc"));
													result.put("sp_serialno", map.get("sp_serialno"));
													result.put("spid", payRequest.getSpid());
													result.put("state", "00");
													UpdateDaifu(payRequest.getSp_serialno(), "00");
												}else if("2".equals(map.get("serialno_state"))){
													result.put("respCode", "00");
													result.put("respMsg", map.get("serialno_desc"));
													result.put("sp_serialno", map.get("sp_serialno"));
													result.put("spid", payRequest.getSpid());
													result.put("state", "200");
													ThreadPool.executor(new TFBThread(this, payRequest, pmsDaifuMerchantInfoDao,pmsMerchantInfoDao,payRequest.getPay_type()));
												}else{
													model.setTransactionType("代付补款");	
													model.setResponsecode("00");
													model.setPosition(surplus+b1.doubleValue()+"");
													model.setPayamount(Double.parseDouble(factAmount) / 100+"");
													if(payRequest.getSp_batch_no()!=""&&payRequest.getSp_batch_no()!=null){
														model.setBatchNo(payRequest.getSp_batch_no()+"/A");
													}else{
														model.setBatchNo(payRequest.getSp_serialno()+"/A");
													}
													int ii = pmsDaifuMerchantInfoDao.insert(model);
													if(ii==1){
														log.info("补款成功！");
													}
													maps.put("mercId", payRequest.getSpid());
													maps.put("payMoney", b1.doubleValue() + "");
													int nus = pmsMerchantInfoDao.updataPayT1(maps);
													//merchantinfo.setPosition(surplus+b1.doubleValue()+"");
													//int p=pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
													if(nus==1){
														log.info("补款成功");
													}
													result.put("respCode", "00");
													result.put("respMsg", map.get("serialno_desc"));
													result.put("sp_serialno", map.get("sp_serialno"));
													result.put("spid", payRequest.getSpid());
													result.put("state", "01");
													UpdateDaifu(payRequest.getSp_serialno(), "01");
												}

											} else {
												log.info("验签结果：失败");
											}
								 		}else{
								 			model.setTransactionType("代付补款");	
											model.setResponsecode("00");
											model.setPosition(surplus+b1.doubleValue()+"");
											model.setPayamount(Double.parseDouble(factAmount) / 100+"");
											if(payRequest.getSp_batch_no()!=""&&payRequest.getSp_batch_no()!=null){
												model.setBatchNo(payRequest.getSp_batch_no()+"/A");
											}else {
												model.setBatchNo(payRequest.getSp_serialno()+"/A");
											}
											int ii = pmsDaifuMerchantInfoDao.insert(model);
											if(ii==1){
												log.info("补款成功！");
											}
											maps.put("mercId", payRequest.getSpid());
											maps.put("payMoney", b1.doubleValue() + "");
											int nus = pmsMerchantInfoDao.updataPayT1(maps);
											//merchantinfo.setPosition(surplus+b1.doubleValue()+"");
											//int p=pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
											if(nus==1){
												log.info("补款成功");
											}
											result.put("respCode", "00");
											result.put("respMsg", map.get("serialno_desc"));
											result.put("sp_serialno", map.get("sp_serialno"));
											result.put("spid", payRequest.getSpid());
											result.put("state", "01");
											UpdateDaifu(payRequest.getSp_serialno(), "01");
								 		}
								 		

								} 
								
								}else if("3".equals(payRequest.getPay_type())){
									
									log.info("下游上传代付金额:" + b1.doubleValue());
									b2 = new BigDecimal(merchantinfo.getPosition());
									log.info("系统剩余可用额度D0:" + b2.doubleValue());
									b3 = new BigDecimal(merchantinfo.getPoundage());
									log.info("系统商户代付手续费:" + b3.doubleValue());
									min = new BigDecimal(merchantinfo.getMinDaiFu());
									log.info("系统代付最小金额:" + min.doubleValue());
									max = new BigDecimal(merchantinfo.getMaxDaiFu());
									log.info("系统代付最大金额:" + max.doubleValue());
									if (b1.doubleValue() + b3.doubleValue() > b2.doubleValue()) {
										result.put("respCode", "0006");
										result.put("respMsg", "下单失败,代付金额高于剩余额度");
										log.info("**********************代付金额高于剩余额度");
										int i = add(payRequest, merchantinfo, result);
										if (i == 1) {
											log.info("添加失败订单成功");
										}
										return result;
									}
									if (b1.doubleValue() < min.doubleValue() * 100) {
										result.put("respCode", "0006");
										result.put("respMsg", "下单失败,代付金额小于代付最小金额");
										log.info("**********************代付金额小于代付最小金额");
										int i = add(payRequest, merchantinfo, result);
										if (i == 1) {
											log.info("添加失败订单成功");
										}
										return result;
									}
									if (b1.doubleValue() > max.doubleValue() * 100) {
										result.put("respCode", "0006");
										result.put("respMsg", "下单失败,代付金额大于代付D0最大金额");
										log.info("**********************代付金额大于代付D0最大金额");
										int i = add(payRequest, merchantinfo, result);
										if (i == 1) {
											log.info("添加失败订单成功");
										}
										return result;
									}
									surplus = b2.subtract(b1).doubleValue() - b3.doubleValue() * 100;
									
									merchantinfo.setPosition(surplus.toString());
									int num = pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
									if (num == 1) {
										log.info("扣款成功！！");
									}
									// 代付插入数据
									model.setCount("1");
									if(payRequest.getSp_batch_no()!=""&&payRequest.getSp_batch_no()!=null){
										model.setBatchNo(payRequest.getSp_batch_no());
										model.setIdentity(payRequest.getSp_batch_no());
									}else{
										model.setBatchNo(payRequest.getSp_serialno());
										model.setIdentity(payRequest.getSp_serialno());
									}
									model.setAmount(Double.parseDouble(factAmount) / 100 + "");
									model.setCardno(payRequest.getAcct_id());
									model.setRealname(payRequest.getAcct_name());
									model.setPayamount("-" + Double.parseDouble(factAmount) / 100);
									model.setPmsbankno(payRequest.getBank_settle_no());
									model.setTransactionType("代付");
									model.setPosition(String.valueOf(surplus));
									model.setRemarks("D0");
									model.setRecordDescription("批次号:" + payRequest.getSp_batch_no()+"订单号:"+payRequest.getSp_serialno());
									model.setResponsecode("200");
									model.setOagentno("100333");
									model.setPayCounter(b3.doubleValue() + "");
									int iii = pmsDaifuMerchantInfoDao.insert(model);
									PmsBusinessPos pmsBusinessPos =selectKey(payRequest.getSpid());
									if (iii == 1) {
										log.info("代付订单添加成功");
										TreeMap<String, String> paramsMap = new TreeMap<String, String>();
										paramsMap.put("version","1.0");    //固定填1.0
										paramsMap.put("spid",pmsBusinessPos.getBusinessnum());   //填写国采分配的商户号"1800046681"
										Date date=new Date();
										SimpleDateFormat  format=new SimpleDateFormat("yyyyMMddHHmmss");
										String time=format.format(date);
										paramsMap.put("sp_serialno",payRequest.getSp_serialno()); //商户交易单号，商户保证其在本系统唯一,每次交易入库需要修改订单号
										paramsMap.put("sp_reqtime", time);   //系统发送时间，14位固定长度
										paramsMap.put("tran_amt",payRequest.getTran_amt());    //交易金额，单位为分，不带小数点
										paramsMap.put("cur_type", "1");      //
										paramsMap.put("pay_type", "3");      //普通余额支付填 1；垫资代付填3
										paramsMap.put("acct_name",payRequest.getAcct_name());  //收款人姓名
										paramsMap.put("acct_id", payRequest.getAcct_id());   //收款人账号
										paramsMap.put("acct_type", payRequest.getAcct_type());   //0 借记卡， 1 贷记卡， 2 对公账户
										paramsMap.put("mobile", payRequest.getMobile());
										paramsMap.put("bank_name", payRequest.getBank_name());
										paramsMap.put("bank_settle_no", payRequest.getBank_settle_no());  //对私可不值，对公必传
										paramsMap.put("bank_branch_name",payRequest.getBank_branch_name());
										paramsMap.put("business_type",payRequest.getBusiness_type());
										paramsMap.put("memo", payRequest.getMemo());
										String paramSrc = RequestUtils.getParamSrc(paramsMap);
								        log.info("上传上游前生成签名字符串:"+paramSrc);
								         
								         String key =pmsBusinessPos.getKek();//"12345";
								         log.info("此商户对应上游秘钥:"+key);
								         String sign = MD5Utils.sign(paramSrc,key , TFBConfig.serverEncodeType);
								         log.info("此商户生成签名:"+sign);
								         paramSrc=paramSrc+"&sign="+sign;
								         log.info("加上签名之后的数据:"+paramSrc);
								         //------------------------

								         String url =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.GC_PUBLIC_KEY_PATH;
								         log.info("url:"+url);
								         String cipherData = RequestUtils.encrypt(paramSrc.toString(), url);
								 		System.out.println("加密结果:" + cipherData);

								 		System.out.println("发起请求--------------------------------------------");
								         String applyResponse=RequestUtils.doPost(TFBConfig.payUrl,"cipher_data=" + URLEncoder.encode(cipherData) , "UTF-8");
								         log.info("代付返回数据1:"+JSON.toJSON(applyResponse));
								       //-----------------------------------------------
								 		String cipherResponseData =RequestUtils.parseXml(applyResponse);
								 		log.info("代付解析返回的数据:"+JSON.toJSON(applyResponse));
								 		if(!"失败".equals(cipherResponseData)){
								 			log.info("代付返回数据2:"+JSON.toJSON(cipherResponseData));
									 		//自己的私钥------------------到时候要改
									 		String privateKey =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.PRIVATE_KEY_PATH;
									 		
									 		String responseData = RequestUtils.decryptResponseData(cipherResponseData, privateKey);
									 		log.info("responseData："+responseData);
											String sign1 = responseData.substring(responseData.indexOf("sign=") + 5, responseData.length());
											String source = responseData.substring(0, responseData.lastIndexOf("&sign"));
											
											log.info("source:" + source);
											System.out.println("sign:" + sign1);
											//rsa验签
											if (MD5Utils.verify(source, sign1, key, "UTF-8")) {
												System.out.println("验签结果：通过");
												log.info("成功的source:" + JSON.toJSON(source));
												 map = strToMap(source);
												log.info("JSONObject解析数据"+JSON.toJSON(map));
												if("1".equals(map.get("serialno_state"))){
													result.put("respCode", "00");
													result.put("respMsg", map.get("serialno_desc"));
													result.put("sp_serialno", map.get("sp_serialno"));
													result.put("spid", payRequest.getSpid());
													result.put("state", "00");
													UpdateDaifu(payRequest.getSp_serialno(), "00");
												}else if("2".equals(map.get("serialno_state"))){
													result.put("respCode", "00");
													result.put("respMsg", map.get("serialno_desc"));
													result.put("sp_serialno", map.get("sp_serialno"));
													result.put("spid", payRequest.getSpid());
													result.put("state", "200");
													ThreadPool.executor(new TFBThread(this, payRequest, pmsDaifuMerchantInfoDao,pmsMerchantInfoDao,payRequest.getPay_type()));
												}else{
													model.setTransactionType("代付补款");	
													model.setResponsecode("00");
													model.setPosition(surplus+b1.doubleValue()+"");
													model.setPayamount(Double.parseDouble(factAmount) / 100+"");
													if(payRequest.getSp_batch_no()!=""&&payRequest.getSp_batch_no()!=null){
														model.setBatchNo(payRequest.getSp_batch_no()+"/A");
													}else{
														model.setBatchNo(payRequest.getSp_serialno()+"/A");
													}
													int ii = pmsDaifuMerchantInfoDao.insert(model);
													if(ii==1){
														log.info("补款成功！");
													}
													maps.put("mercId", payRequest.getSpid());
													maps.put("payMoney", b1.doubleValue() + "");
													int nus = pmsMerchantInfoDao.updataPay(maps);
													//merchantinfo.setPosition(surplus+b1.doubleValue()+"");
													//int p=pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
													if(nus==1){
														log.info("补款成功");
													}
													result.put("respCode", "00");
													result.put("respMsg", map.get("serialno_desc"));
													result.put("sp_serialno", map.get("sp_serialno"));
													result.put("spid", payRequest.getSpid());
													result.put("state", "01");
													UpdateDaifu(payRequest.getSp_serialno(), "01");
												}

											} else {
												log.info("验签结果：失败");
											}
								 		}else{
								 			model.setTransactionType("代付补款");	
											model.setResponsecode("00");
											model.setPosition(surplus+b1.doubleValue()+"");
											model.setPayamount(Double.parseDouble(factAmount) / 100+"");
											if(payRequest.getSp_batch_no()!=""&&payRequest.getSp_batch_no()!=null){
												model.setBatchNo(payRequest.getSp_batch_no()+"/A");
											}else {
												model.setBatchNo(payRequest.getSp_serialno()+"/A");
											}
											int ii = pmsDaifuMerchantInfoDao.insert(model);
											if(ii==1){
												log.info("补款成功！");
											}
											maps.put("mercId", payRequest.getSpid());
											maps.put("payMoney", b1.doubleValue() + "");
											int nus = pmsMerchantInfoDao.updataPay(maps);
											//merchantinfo.setPosition(surplus+b1.doubleValue()+"");
											//int p=pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
											if(nus==1){
												log.info("补款成功");
											}
											result.put("respCode", "00");
											result.put("respMsg", map.get("serialno_desc"));
											result.put("sp_serialno", map.get("sp_serialno"));
											result.put("spid", payRequest.getSpid());
											result.put("state", "01");
											UpdateDaifu(payRequest.getSp_serialno(), "01");
								 		}
								 		

								} 
								}
								

							} else {
								throw new RuntimeException("系统错误----------------当前商户非正式商户");
							}

						} else {
							throw new RuntimeException("系统错误----------------商户不存在");
						}
						break label104;
					}

				} catch (Exception var43) {
					log.error("****************************代付错误", var43);
					try {
						throw var43;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			log.info("***********天下付*********************代付------处理完成");
			return result;

		}
		
		
	/**
	 * 支付单笔查询
	 */
	public Map<String, String> WxPaySelect(WxPayApplyRequest applyRequest,Map<String, String> result){
		//PmsBusinessPos pmsBusinessPos =selectKey(applyRequest.getSpid());
		log.info("下游上传参数:"+JSON.toJSON(applyRequest));
		try {
		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		PmsBusinessPos pmsBusinessPos =selectKey(applyRequest.getSpid());
		paramsMap.put("spid", pmsBusinessPos.getBusinessnum());//"1800776625"
		paramsMap.put("sp_billno", applyRequest.getSp_billno());
		String paramSrc = RequestUtils.getParamSrc(paramsMap);
        log.info("上传上游前生成签名字符串:"+paramSrc);
        
        String key =pmsBusinessPos.getKek();//"12345";
        log.info("此商户对应上游秘钥:"+key);
        String sign = MD5Utils.sign(paramSrc,key , TFBConfig.serverEncodeType);
        log.info("此商户生成签名:"+sign);
        paramSrc=paramSrc+"&sign="+sign+"&input_charset=UTF-8";
        log.info("加上签名之后的数据:"+paramSrc);
        
        String applyResponse=RequestUtils.doPost(TFBConfig.payBatchQueryApi, paramSrc, "GBK");
        log.info("上游返回查询数据:"+JSON.toJSON(applyResponse));
        String data = applyResponse.substring(applyResponse.indexOf("<data>") + 6,
        		applyResponse.indexOf("</data>"));
       
			result = RequestUtils.Dom2Map(applyResponse);
			result.put("data", data);
			result.put("spid", applyRequest.getSpid());
			result.put("retcode", "00");
        	result.put("retmsg", "请求成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 代付查询
	 */
	
	@Override
	public Map<String, String> PaySelect(PayRequest payRequest,Map<String, String> result){
		//PmsBusinessPos pmsBusinessPos =selectKey(applyRequest.getSpid());
		log.info("下游上传参数:"+JSON.toJSON(payRequest));
		try {
		PmsBusinessPos pmsBusinessPos =selectKey(payRequest.getSpid());
		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		paramsMap.put("spid", pmsBusinessPos.getBusinessnum());//"1800046681"
		paramsMap.put("sp_serialno", payRequest.getSp_serialno());
		paramsMap.put("version", "1.0");
		paramsMap.put("sp_reqtime", new SimpleDateFormat("YYYYMMDDhhmmss").format(new Date()));
		String paramSrc = RequestUtils.getParamSrc(paramsMap);
        log.info("上传上游前生成签名字符串:"+paramSrc);
        
        String key =pmsBusinessPos.getKek();//"12345";
        log.info("此商户对应上游秘钥:"+key);
        String sign = MD5Utils.sign(paramSrc,key , "GBK"/*TFBConfig.serverEncodeType*/);
        log.info("此商户生成签名:"+sign);
        paramSrc=paramSrc+"&sign="+sign;//+"&input_charset=UTF-8"
        log.info("加上签名之后的数据:"+paramSrc);
        String url;
		
			url = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.GC_PUBLIC_KEY_PATH;
        log.info("url:"+url);
        String cipherData = RequestUtils.encrypt(paramSrc.toString(), url);
		System.out.println("加密结果:" + cipherData);

		System.out.println("发起请求--------------------------------------------");
        String applyResponse=RequestUtils.doPost(TFBConfig.payQueryApi,"cipher_data=" + URLEncoder.encode(cipherData) , "UTF-8");
        log.info("代付返回数据1:"+JSON.toJSON(applyResponse));
       /* String applyResponse=RequestUtils.doPost(TFBConfig.payQueryApi, paramSrc, "UTF-8");
        log.info("上游返回查询数据:"+JSON.toJSON(applyResponse));*/
        String privateKey =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.PRIVATE_KEY_PATH;
        TreeMap<String, String> map = RequestUtils.Dom2Map(applyResponse);
        if("00".equals(map.get("retcode"))){
        	String responseData = RequestUtils.decryptResponseData(map.get("cipher_data").toString(), privateKey);
     		log.info("responseData："+responseData);
    		String sign1 = responseData.substring(responseData.indexOf("sign=") + 5, responseData.length());
    		String source = responseData.substring(0, responseData.lastIndexOf("&sign"));
            if (MD5Utils.verify(source, sign1, key, "UTF-8")) {
    			System.out.println("验签结果：通过");
    			log.info("成功的source:" + JSON.toJSON(source));
    			result = strToMap(source);
    			result.remove("tfb_rsptime");
    			result.remove("tfb_serialno");
    			result.put("retcode", "00");
	        	result.put("retmsg", "请求成功");
    			log.info("JSONObject解析数据"+JSON.toJSON(result));
    		} else {
    			log.info("验签结果：失败");
    		}
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
		//添加代付订单
		public synchronized int add(PayRequest payRequest, PmsMerchantInfo merchantinfo, Map<String, String> result) throws Exception {
			log.info("进来添加失败余额了");
			String type = "";
			String positions = "";
			int iii=0;
			

			PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
			String factAmount = payRequest.getTran_amt();
			model.setMercId(payRequest.getSpid());
			model.setCount("1");
			
			if(payRequest.getSp_batch_no()!=""&&payRequest.getSp_batch_no()!=null){
				model.setBatchNo(payRequest.getSp_batch_no());
				model.setIdentity(payRequest.getSp_batch_no());
			}else{
				model.setBatchNo(payRequest.getSp_serialno());
				model.setIdentity(payRequest.getSp_serialno());
			}
			model.setAmount(Double.parseDouble(factAmount) / 100 + "");
			model.setCardno(payRequest.getAcct_id());
			model.setRealname(payRequest.getAcct_name());
			model.setPayamount("-" + Double.parseDouble(factAmount) / 100);
			model.setPmsbankno(payRequest.getBank_settle_no());
			model.setTransactionType("代付");
			model.setPosition(String.valueOf(positions));
			model.setRemarks(type);
			model.setRecordDescription("批次号:" + payRequest.getSp_batch_no()+"订单号："+payRequest.getSp_serialno()+ "错误原因:" + result.get("respMsg"));
			model.setResponsecode("01");
			model.setOagentno("100333");
			model.setPayCounter(new BigDecimal(merchantinfo.getPoundage()).doubleValue() + "");
			PmsDaifuMerchantInfo daifu = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model);
			if (daifu == null) {
			iii = pmsDaifuMerchantInfoDao.insert(model);
			log.info("iii:" + iii);
			}
			return iii;
		}
		
		
		@Override
		public void update(WxPayApplyResponse wxPayApplyResponse) throws Exception {
			log.info("返回的参数："+JSON.toJSON(wxPayApplyResponse));
			log.info("**************进入修改方法*************************");
			// 流水表transOrderId
			String transOrderId = "";
			if(wxPayApplyResponse.getSp_billno()!=""&&wxPayApplyResponse.getSp_billno()!=null){
				transOrderId=wxPayApplyResponse.getSp_billno();
			}else if(wxPayApplyResponse.getSpbillno()!=""&&wxPayApplyResponse.getSpbillno()!=null){
				transOrderId=wxPayApplyResponse.getSpbillno();
			}
			
			
			log.info("异步通知回来的订单号:" + transOrderId);
			// 流水信息
			PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
			log.info("流水表信息：" + pospTransInfo);
			// 订单信息
			PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
			log.info("订单表信息：" + pmsAppTransInfo);
			wxPayApplyResponse.setSpid(pmsAppTransInfo.getMercid());
			// 查询结果成功
			if ("00".equals(wxPayApplyResponse.getRetcode())) {
				if("1".equals(wxPayApplyResponse.getResult())){
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
						if(wxPayApplyResponse.getSp_billno()!=null&&wxPayApplyResponse.getSp_billno()!=""){
							pospTransInfo.setPospsn(wxPayApplyResponse.getSp_billno());
						}else if(wxPayApplyResponse.getSpbillno()!=null&&wxPayApplyResponse.getSpbillno()!=""){
							pospTransInfo.setPospsn(wxPayApplyResponse.getSpbillno());
						}
						
						log.info("更新流水");
						log.info("流水表信息：" + pospTransInfo);
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
				}else{
					log.info("天下支付未知情况！");
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
					if(wxPayApplyResponse.getSp_billno()!=null&&wxPayApplyResponse.getSp_billno()!=""){
						pospTransInfo.setPospsn(wxPayApplyResponse.getSp_billno());
					}else if(wxPayApplyResponse.getSpbillno()!=null&&wxPayApplyResponse.getSpbillno()!=""){
						pospTransInfo.setPospsn(wxPayApplyResponse.getSpbillno());
					}
					log.info("更新流水");
					log.info("流水表信息：" + pospTransInfo);
					pospTransInfoDAO.updateByOrderId(pospTransInfo);
				}
			} 

			
		}
		
	public Map<String, String> strToMap(String str){
		Map<String, String> map =new HashMap<>();
		String s[] =str.split("&");
		for (int i = 0; i < s.length; i++) {
			map.put(s[i].split("=")[0], s[i].split("=")[1]);
		}
		
		return map;
	}

	//查询
	public Map<String, Object> paySelect(WxPayApplyRequest payApplyRequest,Map<String, Object> result){
		System.out.println("111");
		if("0".equals(payApplyRequest.getType())){
			OriginalOrderInfo origin = new OriginalOrderInfo();
			origin.setMerchantOrderId(payApplyRequest.getSp_billno());
			origin.setPid(payApplyRequest.getSpid());
			origin = originalDao.selectByOriginal(origin);
			PmsAppTransInfo pmsAppTransInfo = null;
			
			try {
				if(origin!=null){
					pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(origin.getOrderId());
					if(pmsAppTransInfo!=null){
						log.info("pmsAppTransInfo："+JSON.toJSON(pmsAppTransInfo));
						result.put("tran_amt",pmsAppTransInfo.getFactamount() );//金额
						result.put("spid", pmsAppTransInfo.getMercid());//商户号
						result.put("sp_billno", pmsAppTransInfo.getOrderid());//订单号
						result.put("pay_type",pmsAppTransInfo.getPaymenttype() );//支付类型
						result.put("status", pmsAppTransInfo.getStatus());//支付状态
						result.put("type", "0");
						result.put("retcode", "00");
						result.put("retmsg", "请求成功");
					}else{
						result.put("retcode", "00");
						result.put("retmsg", "请求成功,此订单号不存在！");
					}
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//result =RequestUtils.beanToMap(pmsAppTransInfo);
		}else if("1".equals(payApplyRequest.getType())){
			PmsDaifuMerchantInfo daifuMerchantInfo = new PmsDaifuMerchantInfo();
			daifuMerchantInfo.setBatchNo(payApplyRequest.getSp_billno());
			daifuMerchantInfo.setMercId(payApplyRequest.getSpid());
			daifuMerchantInfo = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(daifuMerchantInfo);
			if(daifuMerchantInfo!=null){
				result.put("tran_amt",daifuMerchantInfo.getAmount() );
				result.put("spid", daifuMerchantInfo.getMercId());
				result.put("sp_billno", daifuMerchantInfo.getBatchNo());
				result.put("paymenttype","代付");
				result.put("status", daifuMerchantInfo.getResponsecode());
				result.put("message", daifuMerchantInfo.getRecordDescription()==null?"":daifuMerchantInfo.getRecordDescription());
				result.put("type","1");
				result.put("retcode", "00");
				result.put("retmsg", "请求成功");
			}else{
				result.put("retcode", "00");
				result.put("retmsg", "请求成功,此订单号不存在！");
			}
			
		}
		return result;
	}
	
	//网关查询---------------------------------------------------
	@Override
	public Map<String, String> cardSelect(
			CardPayApplyRequest cardPayApplyＲequest, Map<String, String> result) {
			try {
				log.info("下游上传参数:"+JSON.toJSON(cardPayApplyＲequest));
				PmsBusinessPos pmsBusinessPos =selectKey(cardPayApplyＲequest.getSpid());
				TreeMap<String, String> paramsMap = new TreeMap<String, String>();
				paramsMap.put("spid", pmsBusinessPos.getBusinessnum());//"1800071515"
				paramsMap.put("spbillno", cardPayApplyＲequest.getSpbillno());
				paramsMap.put("listid", "");
				paramsMap.put("channel", "1");
				paramsMap.put("encode_type", "MD5");
				paramsMap.put("risk_ctrl", cardPayApplyＲequest.getRisk_ctrl());
				String paramSrc = RequestUtils.getParamSrc(paramsMap);
		        log.info("上传上游前生成签名字符串:"+paramSrc);
		        
		        String key =pmsBusinessPos.getKek();//"12345";
		        log.info("此商户对应上游秘钥:"+key);
		        String sign = MD5Utils.sign(paramSrc,key , "GBK"/*TFBConfig.serverEncodeType*/);
		        log.info("此商户生成签名:"+sign);
		        paramSrc=paramSrc+"&sign="+sign;//+"&input_charset=UTF-8"
		        log.info("加上签名之后的数据:"+paramSrc);
		        String url;
				
					url = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.GC_PUBLIC_KEY_PATH;
		        log.info("url:"+url);
		        String cipherData = RequestUtils.encrypt(paramSrc.toString(), url);
				System.out.println("加密结果:" + cipherData);

				System.out.println("发起请求--------------------------------------------");
		        String applyResponse=RequestUtils.doPost(TFBConfig.cardPayQueryApi,"cipher_data=" + URLEncoder.encode(cipherData) , "GBK");
		        log.info("代付返回数据1:"+JSON.toJSON(applyResponse));
		       /* String applyResponse=RequestUtils.doPost(TFBConfig.payQueryApi, paramSrc, "UTF-8");
		        log.info("上游返回查询数据:"+JSON.toJSON(applyResponse));*/
		        String privateKey =new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"/ky"+TFBConfig.PRIVATE_KEY_PATH;//TFBConfig.PRIVATE_KEY_PATH1;
		        TreeMap<String, String> map = RequestUtils.Dom2Map(applyResponse);
		        log.info("map:"+JSON.toJSON(map));
		        if("00".equals(map.get("retcode"))){
		        	String responseData = RequestUtils.decryptResponseData(map.get("cipher_data").toString(), privateKey);
			 		log.info("responseData："+responseData);
					String sign1 = responseData.substring(responseData.indexOf("sign=") + 5, responseData.length());
					String source = responseData.substring(0, responseData.lastIndexOf("&sign"));
					log.info("成功的source:" + JSON.toJSON(source));
					result = RequestUtils.parseString(source);
					result.put("retcode", "00");
		        	result.put("retmsg", "请求成功");
		        	result.remove("memo");
			       /* if (MD5Utils.verify(source, sign1, key, "UTF-8")) {
						System.out.println("验签结果：通过");
						log.info("成功的source:" + JSON.toJSON(source));
						result = strToMap(source);
						result.remove("tfb_rsptime");
						result.remove("tfb_serialno");
						log.info("JSONObject解析数据"+JSON.toJSON(result));
					} else {
						log.info("验签结果：失败");
					}*/
		        }else{
		        	result.put("retcode", "01");
		        	result.put("retmsg", "订单不存在");
		        }
		 		
				} catch (Exception e) {
					
					e.printStackTrace();
				}
		return result;
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
				Double dd =(amount*100-poundage)*0.95;
				dd =dd+ds;
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
	
	
}

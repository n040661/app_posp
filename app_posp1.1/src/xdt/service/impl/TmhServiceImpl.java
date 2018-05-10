package xdt.service.impl;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;

import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAmountLimitControlDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayTypeControlDao;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.BaseUtil;
import xdt.dto.nbs.alipay.AlipayParamRequest;
import xdt.dto.nbs.alipay.AlipayParamResponse;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.nbs.common.constant.Constant;
import xdt.quickpay.taomihui.entity.TaoPayRequestEntity;
import xdt.quickpay.taomihui.entity.TaoPayResponseEntity;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.ITmhService;
import xdt.util.BeanToMapUtil;
import xdt.util.Constants;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;

@Service
public class TmhServiceImpl extends BaseServiceImpl implements ITmhService{
	
	private Logger log = LoggerFactory.getLogger(TmhServiceImpl.class);

	private static Gson gson = new Gson();
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
	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	
	/**
	 * 分发请求
	 * 
	 * @param reqData
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> updateHandle(TaoPayRequestEntity alipayParam) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String, Object>();

		log.info("************************淘米慧支付----二维码----处理转发 开始");

		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(alipayParam.getMerchantId());
		try {

			switch (alipayParam.getPayType() == null ? alipayParam.getPayType() : alipayParam.getPayType()) {
			case "1":
				log.info("************************淘米慧----支付宝扫码----处理 开始");
				result = (Map<String, Object>) this.alipayParam(alipayParam, result, busInfo);
				break;
//			case "2":
//				log.info("***********************淘米慧----微信扫码----处理 开始");
//				result = this.alipayRefund(alipayParam, result, busInfo);
//				break;
//			case "ORDERQUERY":
//				log.info("************************淘米慧----查询接口----处理 开始");
//				result = this.alipayReverseorder(alipayParam, result, busInfo);
//				break;
			default:
				break;
				
			}

		} catch (Exception e) {
			log.info("************************江苏电商----二维码----处理转发 失败", e);
			throw new RuntimeException("系统错误");
		}

		log.info("************************江苏电商----二维码----处理转发 结果:{}" + result);

		return result;
	}

	
	/**
	 * 支付宝接口
	 * 
	 * @param alipayScanParamRequest
	 * @return
	 */
	public Map<String, Object> alipayParam(TaoPayRequestEntity alipayScanParamRequest, Map<String, Object> result, PmsBusinessPos busInfo) {
		//Map<String, Object> result = new HashMap<String, Object>();
		TaoPayResponseEntity AlipayParamResponse = null;
		log.info("生成支付宝二维码");

		log.info("根据商户号查询");

		String out_trade_no = "";// 订单号
		out_trade_no = alipayScanParamRequest.getOutTradeNo(); // 10业务号2业务细;
																	// 订单号
																	// 现根据规则生成订单号
		log.info("根据商户号查询");
		// 下游商户号
		String mercId = alipayScanParamRequest.getMerchantId();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);

			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
 				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(alipayScanParamRequest.getOutTradeNo());
				oriInfo.setPid(alipayScanParamRequest.getMerchantId());

				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoAlipay(alipayScanParamRequest, out_trade_no, mercId);
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
					// 支付宝支付
					paramMap.put("paymentcode", PaymentCodeEnum.zhifubaoPay.getTypeCode());

					// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
					AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
							.queryAmountAndStatus(paramMap);
					if (appRateTypeAndAmount != null) {

						String status = appRateTypeAndAmount.getStatus();// 此业务是否开通

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
										PaymentCodeEnum.zhifubaoPay.getTypeCode());
								String ss = payCheckResult.getErrCode();
								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(alipayScanParamRequest.getAmount());// 收款金额
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao.checkLimit(oAgentNo, payAmt,
											TradeTypeEnum.merchantCollect.getTypeCode());
									// 返回不为0，一律按照交易失败处理
									if (!resultInfo.getErrCode().equals("0")) {
										result.put("respCode", "08");
										result.put("respMsg", "交易金额不在申请的范围之内");
										log.info("交易金额不在申请的范围之内");

									} else {

										// 商户渠道支付方式
										// 商户渠道交易类型
										// 验证支付方式是否开启

										ResultInfo resultinfo = null;
										resultinfo = iPublicTradeVerifyService
												.payTypeVerifyMer(PaymentCodeEnum.zhifubaoPay, mercId);
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
														String totalAmount = alipayScanParamRequest.getAmount()+""; // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo);

														if (appTransInfo != null) {
															// 处理生成二维码
															// ------------------------------------------
															AlipayParamResponse = this.twoDimensionCodeAlipayScanParam(alipayScanParamRequest, result, appTransInfo,busInfo);
															result=BeanToMapUtil.convertBean(AlipayParamResponse);
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
	 * @Description 支付宝处理生成二维码
	 * @author Administrator
	 * @param wechatScannedRequest
	 * @param result
	 * @param pospTransInfo
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private TaoPayResponseEntity twoDimensionCodeAlipayScanParam(TaoPayRequestEntity alipayScanParamRequest,
			Map<String, Object> result, PmsAppTransInfo pmsAppTransInfo, PmsBusinessPos busInfo) throws Exception {

		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = alipayScanParamRequest.getOutTradeNo();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
			pospTransInfo.setTransOrderId(transOrderId);
			pospTransInfo.setResponsecode("20");
			pospTransInfo.setPospsn("");
			insertOrUpdateFlag = 1;
			log.info("***************进入payHandle5-16***************");
		} else {
			// 不存在流水，生成一个流水
			pospTransInfo = InsertJournal(pmsAppTransInfo);
			// 设置上送流水号
			// 通道订单号
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
		pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());
		log.info("请求交易生成二维码map");
		// 业务代码
		// 组装上送参数
		String service_type = "";
		switch (alipayScanParamRequest.getPayType()) {
		case "1":
			// 1 支付宝扫码
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.zhifubaoPay.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.zhifubaoPay.getTypeCode());
			break;
		case "2":
			// 2微信扫码
			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.weixinPay.getTypeName());
			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.weixinPay.getTypeCode());
			break;
		default:
			break;

		}
		String bgurl="";
		if(alipayScanParamRequest.getBgUrl()!=null)
		{
			bgurl=alipayScanParamRequest.getBgUrl();
		}else{
			bgurl=BaseUtil.url+"/tmh/bgPayResult.action";
		}
		log.info("订单表信息:" + pmsAppTransInfo);
		pmsAppTransInfoDao.update(pmsAppTransInfo);
		
		String agent=busInfo.getBusinessnum();
		
		String key1=busInfo.getKek();
		
		TaoPayRequestEntity alipay=new TaoPayRequestEntity();
		
		alipay.setAgent(agent);
		alipay.setAmount(alipayScanParamRequest.getAmount());
		alipay.setOutTradeNo(alipayScanParamRequest.getOutTradeNo());
		alipay.setPayType(alipayScanParamRequest.getPayType());
		alipay.setBgUrl(bgurl);
		alipay.setChannel_id("6");
		alipay.setCity(alipayScanParamRequest.getCity());
		alipay.setMerchantName(alipayScanParamRequest.getMerchantName());
		alipay.setMerchantShortName(alipayScanParamRequest.getMerchantShortName());
		alipay.setProvince(alipayScanParamRequest.getProvince());
		alipay.setDistrictCode(alipayScanParamRequest.getDistrictCode());
		alipay.setAddress(alipayScanParamRequest.getAddress());
		alipay.setSettlePeriod("1");
		alipay.setWeixinRate(pmsAppTransInfo.getRate());
		alipay.setAliRate(pmsAppTransInfo.getRate());
		alipay.setBankNo(alipayScanParamRequest.getBankNo());
		alipay.setRealName(alipayScanParamRequest.getRealName());
		alipay.setCardNo(alipayScanParamRequest.getCardNo());
		alipay.setTel(alipayScanParamRequest.getTel());
		alipay.setBankName(alipayScanParamRequest.getBankName());
		alipay.setBankBranchId(alipayScanParamRequest.getBankBranchId());
		alipay.setBankBranchName(alipayScanParamRequest.getBankBranchName());
		
		log.info("生成签名的数据:"+alipay);

		HashMap<String, String> signMap = JsdsUtil.beanToMap(alipay);
		signMap.remove("sign");
		signMap.remove("merchantId");
		Set<String> keys = new TreeSet<String>();
		// 剔除值为空的
		for (String key : signMap.keySet()) {
			if ("".equals(signMap.get(key)) || signMap.get(key) == null) {
				keys.add(key);
			}
		}
		for (String key : keys) {
			signMap.remove(key);
		}
		String sign=JsdsUtil.sign(signMap, key1);
		alipay.setSign(sign);
		log.info("生成的签名:"+sign);
		HashMap<String, String> map = JsdsUtil.beanToMap(alipay);
		map.remove("merchantId");
		Set<String> key2 = new TreeSet<String>();
		// 剔除值为空的
		for (String key :  map.keySet()) {
			if ("".equals( map.get(key)) ||  map.get(key) == null) {
				keys.add(key);
			}
		}
		for (String key : key2) {
			 map.remove(key);
		}
		//Bean2QueryStrUtil bean2Util = new Bean2QueryStrUtil();

		//log.info("拼接之后的数据:" + bean2Util.bean2QueryStr(alipay));
		
		String json=HttpUtil.toJson3(map);
		
		log.info("拼接之后的数据:" + json);
		//log.info("拼接之后的数据:" + bean2Util.bean2QueryStr(alipay));
		
		String resultcode = HttpClientUtil.post("http://dpt.51qmf.cn/ledgerApi.php?m=QRCode&a=pay", json);
		
		log.info("响应的数据:"+resultcode);
		TaoPayResponseEntity response=new TaoPayResponseEntity();
	
		return response;

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
				double num = Double.parseDouble(merchantinfo.getCounter()) * 100;
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
	 * 
	 * @Description 支付宝插入原始订单表信息 公众号
	 * @author Administrator
	 * @param reqeustInfo
	 * @param orderid
	 * @param mercId
	 * @throws Exception
	 */
	private int saveOriginAlInfoAlipay(TaoPayRequestEntity alipayScanParamRequest, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(alipayScanParamRequest.getOutTradeNo());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("公众号扫码");
		info.setBgUrl(alipayScanParamRequest.getBgUrl());
		Double amt = Double.parseDouble(alipayScanParamRequest.getAmount()+"");// 单位元
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
	}
	
	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {
		log.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}


	@Override
	public Map<String, Object> alipayScanSelect(TaoPayRequestEntity alipayParamRequest, Map<String, Object> result,
			PmsBusinessPos busInfo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

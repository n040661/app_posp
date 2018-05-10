package xdt.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import xdt.common.RetAppMessage;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IPmsAddressDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsBusinessInfoDao;
import xdt.dao.IPmsGoodsDao;
import xdt.dao.IPmsGoodsOrderDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPmsMessageDao;
import xdt.dao.IPmsOrderHelpDao;
import xdt.dao.IPospRouteInfoDAO;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.IQuickpayPreRecordDao;
import xdt.dao.IQuickpayRecordDao;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.CjtQuickPaymentInfo;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.cjt.entity.CjtRequestEntity;
import xdt.quickpay.hengfeng.entity.PayQueryRequestEntity;
import xdt.quickpay.hengfeng.entity.PayQueryResponseEntity;
import xdt.quickpay.hengfeng.entity.PayRequestEntity;
import xdt.quickpay.hengfeng.entity.PayResponseEntity;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.service.CjtQuickPaymentService;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.servlet.AppPospContext;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

/**
 * @ClassName: CjtQuickPaymentService
 * @Description: 第三方 畅捷快捷支付
 * @author 尚延超
 * @date 2016年10月25日 
 * 
 */

@Component
public class CjtQuickPaymentServiceImpl extends BaseServiceImpl implements CjtQuickPaymentService{

	/**
	 * 记录日志
	 */
	private Logger log = Logger.getLogger(CjtQuickPaymentServiceImpl.class);

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	IPmsGoodsDao pmsGoodsDao;
	private Logger logger = Logger.getLogger(ShopPayServiceImpl.class);
	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	private IPmsGoodsOrderDao pmsGoodsOrderDao;
	@Resource
	private IPmsAddressDao pmsAddressDao;
	@Resource
	private IPmsOrderHelpDao pmsOrderHelpDao;
	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	@Resource
	IPayCmmtufitDao iPayCmmtufitDao;
	@Resource
	MobaoPayHandel mobaoPayHandel;
	@Resource
	IQuickpayRecordDao quickpayRecordDao;
	@Resource
	IPospTransInfoDAO pospTransInfoDAO;
	@Resource
	IMerchantCollectMoneyService merchantCollectMoneyService;
	@Resource
	IPmsMessageService pmsMessageService;
	@Resource
	IPmsMessageDao pmsMessageDao;
	@Resource
	private IQuickpayPreRecordDao quickpayPreRecordDao;
	@Resource
	private IAppRateConfigDao appRateConfigDao;

	@Resource
	private IPospRouteInfoDAO pospRouteInfoDao;
	@Resource
	private IPmsBusinessInfoDao pmsBusinessInfoDao;
	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	
	public synchronized String payHandle(CjtRequestEntity originalinfo)
			throws Exception {
		log.info("***************进入payHandle***************");
		
		String message = "0:initialize";

		String jsonString = null;

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		// 验证签名
		ChannleMerchantConfigKey keyinfo = this
				.getChannelConfigKey(originalinfo.getPid());

		String merchantKey = keyinfo.getMerchantkey();

		HFSignUtil signUtil = new HFSignUtil();
		if (!signUtil.verify(PreSginUtil.paySigiString(originalinfo),
				originalinfo.getSignmsg(), merchantKey)) {
			responseDTO.setRetCode(11);
			responseDTO.setRetMessage("签名错误");
			jsonString = createJsonString(responseDTO);
			log.info("签名错误");
			return jsonString;
		}
		log.info("***************进入payHandle1***************");
		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getTransactionid());
		orig.setPid(originalinfo.getPid());

		if (originalDao.selectByOriginal(orig) != null) {
			responseDTO.setRetCode(12);
			responseDTO.setRetMessage("下单重复");
			jsonString = createJsonString(responseDTO);
			log.info("下单重复");
			return jsonString;
		}
		log.info("***************进入payHandle2***************");
		String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
															// 业务号（2位）+业务细分（1位）+时间戳（13位）
															// 总共16位
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getTransactionid());// 原始数据的订单编号
		original.setOrderId(orderNumber); // 为主键
		original.setPid(originalinfo.getPid());
		original.setOrderTime(originalinfo.getOrdertime());
		original.setOrderAmount(originalinfo.getOrderamount());
		original.setPayType(originalinfo.getPay_type());
		original.setBankId(originalinfo.getBank_code());
		original.setProcdutName(originalinfo.getProductname());
		original.setPageUrl(originalinfo.getPageurl());
		original.setBgUrl(originalinfo.getBgurl());
		original.setBankNo(originalinfo.getBankno());
		originalDao.insert(original);
		log.info("***************进入payHandle3***************");
		// 根据商户号查询
		String mercId = originalinfo.getPid();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";
		log.info("***************进入payHandle4***************");
		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
				.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();//

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				responseDTO.setRetCode(1);
				responseDTO.setRetMessage("参数错误");
				jsonString = createJsonString(responseDTO);
				log.info("参数错误,没有欧单编号");
				return jsonString;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {
				// 实际金额
				String factAmount = ""
						+ new BigDecimal(originalinfo.getOrderamount())
								.multiply(new BigDecimal(100));
				// 校验欧单金额限制
				ResultInfo payCheckResult = iPublicTradeVerifyService
						.amountVerifyOagent(
								(int) Double.parseDouble(factAmount),
								TradeTypeEnum.onlinePay, oAgentNo);
				if (!payCheckResult.getErrCode().equals("0")) {
					// 交易不支持
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage(payCheckResult.getMsg());
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return jsonString;
				}

				// 校验欧单模块是否开启
				ResultInfo payCheckResult1 = iPublicTradeVerifyService
						.moduleVerifyOagent(TradeTypeEnum.onlinePay, oAgentNo);
				if (!payCheckResult1.getErrCode().equals("0")) {
					// 交易不支持
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage(payCheckResult1.getMsg());
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					logger.info("欧单模块限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return jsonString;
				}
				// 校验商户模块是否开启
				ResultInfo payCheckResult3 = iPublicTradeVerifyService
						.moduelVerifyMer(TradeTypeEnum.onlinePay, mercId);
				if (!payCheckResult3.getErrCode().equals("0")) {
					// 交易不支持
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage(payCheckResult3.getMsg());
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return jsonString;
				}
				// 校验商户金额限制
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
				paramMap.put("businesscode",
						TradeTypeEnum.onlinePay.getTypeCode());// 业务编号
				paramMap.put("oAgentNo", oAgentNo);
				//商户 网购 业务信息 
				Map<String, String> resultMap = merchantMineDao
						.queryBusinessInfo(paramMap);

				if (resultMap == null || resultMap.size() == 0) {
					// 若查到的是空值，直接返回错误
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
					logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					return jsonString;
				}

				String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
				String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
				String paymentAmount = factAmount;// 交易金额

				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(
						maxTransMoney)) == 1) {
					// 金额超过最大金额
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage("金额超过最大交易金额");
					logger.info("交易金额大于最打金额");
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					return jsonString;
				} else if (new BigDecimal(paymentAmount)
						.compareTo(new BigDecimal(minTransMoney)) == -1) {
					// 金额小于最小金额
					responseDTO.setRetCode(1);
					responseDTO.setRetMessage("金额小于最小交易金额");
					try {
						jsonString = createJsonString(responseDTO);
					} catch (Exception em) {
						em.printStackTrace();
					}
					logger.info("交易金额小于最小金额");
					return jsonString;
				}
				log.info("***************进入payHandle5***************");
				// 组装订单数据
				PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
				// 写入欧单编号
				pmsAppTransInfo.setoAgentNo(oAgentNo);
				pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
				pmsAppTransInfo.setTradetype(TradeTypeEnum.onlinePay
						.getTypeName());// 业务功能模块名称
										// ：网购
				pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
				pmsAppTransInfo.setMercid(merchantinfo.getMercId());
				pmsAppTransInfo.setTradetypecode(TradeTypeEnum.onlinePay
						.getTypeCode());// 业务功能模块编号
										// ：17
				pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay
						.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay
						.getTypeCode());
				BigDecimal factBigDecimal = new BigDecimal(factAmount);
				BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

				pmsAppTransInfo.setFactamount(factBigDecimal
						.stripTrailingZeros().toPlainString());// 实际金额
				pmsAppTransInfo.setOrderamount(orderAmountBigDecimal
						.stripTrailingZeros().toPlainString());// 订单金额
				pmsAppTransInfo.setDrawMoneyType("1");// 普通提款

				// 插入订单信息
				Integer insertAppTrans = pmsAppTransInfoDao
						.insert(pmsAppTransInfo);
				log.info("***************进入payHandle5-1***************");
				if (insertAppTrans == 1) {
					log.info("***************进入payHandle5-2***************");
					//查询订单信息
					pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

					String quickRateType = resultMap.get("QUICKRATETYPE").toString();//快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
					log.info("***************进入payHandle5-3***************");
					if (appRateConfig == null) {
						// 若查到的是空值，直接返回错误
						responseDTO.setRetCode(1);
						responseDTO.setRetMessage("没有查到相关费率配置，请联系客服人员");
						logger.info("没有查到相关费率配置："
								+ merchantinfo.getMobilephone());
						try {
							jsonString = createJsonString(responseDTO);
						} catch (Exception em) {
							em.printStackTrace();
						}
						return jsonString;
					}
					log.info("***************进入payHandle5-4***************");
					String isTop = appRateConfig.getIsTop();
					String rate = appRateConfig.getRate();
					String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
					paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
					String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
					Double minPoundage = 0.0; // 附加费
					log.info("***************进入payHandle5-5***************");
					if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
							&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
						if (StringUtils.isNotBlank(minPoundageStr)) {
							minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
						} else {
							// 若查到的是空值，直接返回错误
							responseDTO.setRetCode(1);
							responseDTO
									.setRetMessage("没有查到相关费率配置（附加费），请联系客服人员");
							logger.info("没有查到相关费率附加费（最低手续费）："
									+ merchantinfo.getMobilephone());
							try {
								jsonString = createJsonString(responseDTO);
							} catch (Exception em) {
								em.printStackTrace();
							}
							return jsonString;
						}
					}
					log.info("***************进入payHandle5-6***************");
					BigDecimal payAmount = null;
					BigDecimal dfactAmount = new BigDecimal(
							pmsAppTransInfo.getFactamount());
					// 费率
					BigDecimal fee = new BigDecimal(0);
					BigDecimal b=new BigDecimal(0);
					String rateStr = "";
					log.info("***************进入payHandle5-7***************");
					// 计算结算金额
					if ("1".equals(isTop)) {
						log.info("***************进入payHandle5-8***************");
						rateStr = rate + "-" + topPoundage;
						// 是封顶费率类型
						fee = new BigDecimal(rate).multiply(dfactAmount);

						if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
							// 手续费大于封顶金额，按封顶金额处理
							payAmount = dfactAmount.subtract(new BigDecimal(
									topPoundage).subtract(new BigDecimal(
									minPoundage)));
							fee = new BigDecimal(topPoundage)
									.add(new BigDecimal(minPoundage));
						} else {
							// 按当前费率处理
							rateStr = rate;
							fee.add(new BigDecimal(minPoundage));
							payAmount = dfactAmount.subtract(fee);
						}
						log.info("***************进入payHandle5-9***************");
					} else {
						// 按当前费率处理
						rateStr = rate;
						fee = new BigDecimal(rate).multiply(dfactAmount).add(
								new BigDecimal(minPoundage));
						int fee1=fee.setScale(1, BigDecimal.ROUND_HALF_UP).intValue();	
						if(merchantinfo.getCounter()!=null)
						{
							int num=Integer.parseInt(merchantinfo.getCounter())*100;
							if(fee1<num)
							{
								b=new BigDecimal(Integer.toString(num));
								payAmount = dfactAmount.subtract(b);
							}else
							{
								b=fee;
								payAmount = dfactAmount.subtract(b);
							}
						}
					}
					log.info("***************进入payHandle5-10***************");
					// 设置结算金额
					pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
					pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
					pmsAppTransInfo.setPoundage(b.toString());
					pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
					// 转换double为int
					Integer paymentAmountInt = (int) Double
							.parseDouble(paymentAmount);
					log.info("***************进入payHandle5-11***************");
					// 验证支付方式是否开启
					payCheckResult = iPublicTradeVerifyService.totalVerify(
							paymentAmountInt, TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay, oAgentNo,
							merchantinfo.getMercId());
					log.info("***************进入payHandle5-12***************");
					if (!payCheckResult.getErrCode().equals("0")) {
						log.info("***************进入payHandle5-13***************");
						// 交易不支持
						responseDTO.setRetCode(1);
						responseDTO.setRetMessage(payCheckResult.getMsg());
						try {
							jsonString = createJsonString(responseDTO);
						} catch (Exception em) {
							em.printStackTrace();
						}
						logger.info("不支持的支付方式，oAagentNo:"
								+ oAgentNo
								+ ",payType:"
								+ PaymentCodeEnum.hengFengQuickPay
										.getTypeCode());
						return jsonString;
					}
					log.info("***************进入payHandle5-14***************");
					ViewKyChannelInfo channelInfo = AppPospContext.context
							.get("HF0001" + "HF0001");
					log.info("***************进入payHandle5-14-1***************");
					log.info("输出可疑值HENGFENGCHANNELNUM："+HENGFENGCHANNELNUM);
					log.info("输出可疑值Businessnum"+channelInfo.getBusinessnum());
					// 设置通道信息
					pmsAppTransInfo
							.setBusinessNum("HF0001");
					log.info("******************进入payHandle5-14-1-1***********");
					log.info("输出可疑值："+HENGFENGCHANNELNUM);
					
					pmsAppTransInfo.setChannelNum("HF0001");
					log.info("***************进入payHandle5-14-2***************") ;
					// 查看当前交易是否已经生成了流水表
					PospTransInfo pospTransInfo = null;
					// 流水表是否需要更新的标记 0 insert，1：update
					int insertOrUpdateFlag = 0;
					log.info("***************进入payHandle5-14-3***************");
					// 生成上送流水号
					String transOrderId = generateTransOrderId(
							TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay);
					log.info("***************进入payHandle5-15***************");
					if ((pospTransInfo = pospTransInfoDAO
							.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
						// 已经存在，修改流水号，设置pospsn为空
						logger.info("订单号：" + pmsAppTransInfo.getOrderid()
								+ ",生成上送通道的流水号：" + transOrderId);
						pospTransInfo.setTransOrderId(transOrderId);
						pospTransInfo.setResponsecode("20");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
						log.info("***************进入payHandle5-16***************");
					} else {
						// 不存在流水，生成一个流水
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
						// 设置上送流水号
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
					log.info("修改订单信息");
					log.info(pmsAppTransInfo);
					pmsAppTransInfoDao.update(pmsAppTransInfo);
				}
			} else {
				log.info("***************进入payHandle5-18***************");
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				responseDTO.setRetCode(1);
				responseDTO.setRetMessage("还没有进行实名认证，请先去进行实名认证，或者等待客服审核");
				jsonString = createJsonString(responseDTO);
				return jsonString;
			}
		} else {
			message = RetAppMessage.MERCHANTDOESNOTEXIST;
		}
		log.info("***************进入payHandle7***************");
		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("merchantDoesNotExist")) {
			retMessage = "商户信息不存在";
		}
		log.info("***************结束payHandle***************");
		responseDTO.setRetCode(retCode);
		responseDTO.setRetMessage(retMessage);
		jsonString = createJsonString(responseDTO);
		return jsonString;

	}
	public void otherInvoke(CjtRequestEntity result) throws Exception {
		// 流水信息
		         OriginalOrderInfo orig = new OriginalOrderInfo();
		         orig.setMerchantOrderId(result.getTransactionid());
		         orig.setPid(result.getPid());
		         OriginalOrderInfo o=originalDao.selectByCjtOriginal(orig);
		         if (o!= null) {
		        	// 流水表transOrderId
		        	 System.out.println(o.getOrderId());
		        	 String transOrderId =o.getOrderId();
						// 流水信息
					   PospTransInfo pospTransInfo = pospTransInfoDAO.searchBycjtOrderId(transOrderId);
                       System.out.println(pospTransInfo.getOrderId());
						// 订单信息
						PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
						// 查询结果成功
						if ("0".equals(result.getPayresult())) {
							// 支付成功
							pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
							pmsAppTransInfo.setThirdPartResultCode(result.getPayresult());
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
								//pospTransInfo.setPospsn(result.getDealid());
								log.info("更新流水");
								log.info(pospTransInfo);
								pospTransInfoDAO.updateByOrderId(pospTransInfo);
							}
						} else if ("1".equals(result.getPayresult())) {
							// 交易正在处理中
							pmsAppTransInfo.setStatus(OrderStatusEnum.waitingClientPay.getStatus());
							pmsAppTransInfo.setThirdPartResultCode(result.getPayresult());
							pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
							// 修改订单
							int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
							if (updateAppTrans == 1) {
								// 更新流水表
								pospTransInfo.setResponsecode("01");
								//pospTransInfo.setPospsn(result.getDealid());
								log.info("更新流水");
								log.info(pospTransInfo);
								pospTransInfoDAO.updateByOrderId(pospTransInfo);
							}
						}else if ("2".equals(result.getPayresult())) {
							// 支付失败
							pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
							pmsAppTransInfo.setThirdPartResultCode(result.getPayresult());
							pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
							// 修改订单
							int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
							if (updateAppTrans == 1) {
								// 更新流水表
								pospTransInfo.setResponsecode("02");
								//pospTransInfo.setPospsn(result.getDealid());
								log.info("更新流水");
								log.info(pospTransInfo);
								pospTransInfoDAO.updateByOrderId(pospTransInfo);
							}
						}
		 		}
	}
	
	public void queryPayResultHandle(PayQueryRequestEntity queryRequest)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	public int updateMerchantBanlance(PmsAppTransInfo pmsAppTransInfo) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
		// TODO Auto-generated method stub
		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		log.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	
	public PospTransInfo getTransInfo(String orderId) {
		PospTransInfo transinfo = pospTransInfoDAO.searchByOrderId(orderId);
		return transinfo;
	}

	
	public PayQueryResponseEntity queryPayResult(
			PayQueryRequestEntity queryRequest) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public OriginalOrderInfo getoriginInfoByMerchantOrderId(
			String originalOrderId) {
		// TODO Auto-generated method stub
		return null;
	}

	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId)
			throws Exception {
		// TODO Auto-generated method stub
		log.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	public ViewKyChannelInfo getChannelInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public OriginalOrderInfo selectByOriginal(OriginalOrderInfo queryWhere) {
		log.info("查询上送原始信息   下游订单id  商户号联合查询");
		return originalDao.selectByOriginal(queryWhere);
	}
	public PmsMerchantInfo selectByPmsMerchantInfo(String mercId) throws Exception {
		
		return pmsMerchantInfoDao.selectMercByMercId(mercId);
	}
	
	public PospRouteInfo selectByPospRouteInfo(int mercid) {
		
		return pospRouteInfoDao.selectByPospRouteInfo(mercid);
	}
	
	public PmsBusinessInfo selectByPmsBusinessInfo(String id) {
		
		return pmsBusinessInfoDao.selectBusinessInfo(id);
	}

}

package xdt.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Service;
import xdt.baidu.BDUtil;
import xdt.common.RetAppMessage;
import xdt.dao.*;
import xdt.dto.*;
import xdt.model.*;
import xdt.offi.OffiPay;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.ISinopecCardService;
import xdt.servlet.AppPospContext;
import xdt.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("SinopecCardService")
public class SinopecCardServiceImpl extends BaseServiceImpl implements
		ISinopecCardService {

	private Logger logger = Logger.getLogger(SinopecCardServiceImpl.class);
	@Resource
	private IViewKyChannelInfoDao channelInfoDao; // 通道信息层
	@Resource
	private IPmsAppTransInfoDao pmsAppTransInfoDao;// 订单处理
	@Resource
	private IAppOrderDetailDao appOrderDetailDao; // 详细信息层
	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
	private OffiPay offiPay; // 欧飞
	@Resource
	private IPayCmmtufitDao payCmmtufitDao; // 银行卡信息层
	@Resource
	private IAppRateConfigDao appRateConfigDao;
	@Resource
	private IPublicTradeVerifyService publicTradeVerifyService;// 校验业务,金额,支付方式的限制

	/**
	 * 中石化加油卡卡号信息查询接口
	 * 
	 * @param queryCardInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String queryCardInfo(HttpSession session, String queryCardInfo)
			throws Exception {
		logger.info("中石化加油卡卡号信息查询");
		String message = INITIALIZEMESSAGE;
		String gameUserid = null;
		String username = null;
		String chargeType = null;
		
		SinopecCardResponseDTO responseData = new SinopecCardResponseDTO();
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		
		String oAgentNo = sessionInfo.getoAgentNo();
		String mercId = sessionInfo.getMercId();
        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
            responseData.setRetCode(1);
            responseData.setRetMessage("参数错误");
            String jsonString = createJsonString(responseData);
            return jsonString;
        }
		
		//校验欧单的模块
		ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.sinopecPay,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
        	responseData.setRetCode(1);
        	responseData.setRetMessage(resultInfo.getMsg());
        	String jsonString = createJsonString(resultInfo);
        	
        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.sinopecPay.getTypeName()+",msg:"+resultInfo.getMsg());
            return jsonString;
        }else{
        	//校验商户的模块
        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.sinopecPay,mercId);
            if(!resultInfo.getErrCode().equals("0")){
            	responseData.setRetCode(1);
            	responseData.setRetMessage(resultInfo.getMsg());
            	String jsonString = createJsonString(resultInfo);
            	
            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.sinopecPay.getTypeName()+",msg:"+resultInfo.getMsg());
                return jsonString;
            }
        }
		
		// 解析商户登录信息
		Object obj = parseJsonString(queryCardInfo, SinopecCardRequestDTO.class);

		if (!obj.equals(DATAPARSINGMESSAGE)) {
			oAgentNo = sessionInfo.getoAgentNo();
            if(StringUtils.isBlank(oAgentNo)){
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                String jsonString = createJsonString(responseData);
                return jsonString;
            }
            
			SinopecCardRequestDTO requestDTO = (SinopecCardRequestDTO) obj;
			ViewKyChannelInfo channelInfo = AppPospContext.context.get(OFFICHANNEL+QUERYCARDINFO);
			if (null != channelInfo) {
				// 请求第三方省份查询接口
				String path = channelInfo.getUrl();
				String channelNO = channelInfo.getChannelNO();
				String channelPwd = channelInfo.getChannelPwd();
				String version = channelInfo.getVersion();
				String cardId = requestDTO.getCardId();

				if (cardId.length() >= 16 && cardId.length() <= 19) {

					if (cardId.length() == 19
							&& "100011".equals(cardId.substring(0, 6))) {
						chargeType = "1";
					} else if (cardId.length() == 16
							&& "9".equals(cardId.substring(0, 1))) {
						chargeType = "2";
					}

					String md5_str = channelNO + channelPwd
							+ requestDTO.getCardId()
							+ BaseServiceImpl.OFFIKEYSTR;

					if (StringUtils.isNotBlank(path)) {
						path += "?userid=" + channelNO + "&userpws="
								+ channelPwd + "&version=" + version
								+ "&game_userid=" + requestDTO.getCardId()
								+ "&md5_str="
								+ UtilMethod.getMd5Str(md5_str).toUpperCase();
						String httpresult = HttpURLConection
								.httpURLConectionGET(path, "gb2312");
						if (StringUtils.isNotBlank(httpresult)) {
							// 解析返回结果
							Document doc = DocumentHelper.parseText(httpresult);
							Map<String, Object> map = XMLUtil.Dom2Map(doc);
							int retcode = Integer.parseInt(map.get("retcode")
									.toString());
							String err_msg = map.get("err_msg").toString();

							if (retcode == 1) {
								gameUserid = map.get("game_userid").toString();
								username = map.get("username").toString();

								session.setAttribute("accountName", username);
								message = SUCCESSMESSAGE;
							} else {
								message = retcode + ":" + err_msg;
							}
						}
					} else {
						logger.info("调用第三方省份查询接口失败");
					}
				} else {
					message = 1 + ":输入卡信息错误";
				}
			} else {
				insertAppLogs("", "", "2001");
				message = DATAPARSINGMESSAGE;
			}
		}

		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("success")) {
			retMessage = "查询成功";
		}

		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		responseData.setClientId(gameUserid);
		responseData.setClientName(username);
		responseData.setChargeType(chargeType);
		String jsonString = createJsonString(responseData);
		logger.info("[app_rsp]" + createJson(responseData));
		return jsonString;
	}

	/**
	 * 中石化加油卡卡号信息查询接口异常
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String queryCardInfoException(String queryCardInfo) throws Exception {
		SinopecCardResponseDTO responseData = new SinopecCardResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

	/**
	 * 中石化加油卡生成订单
	 * 
	 * @param sinopecCardInfo
	 * @return
	 * @throws Exception
	 */
	@Override
	public String producedOrder(String sinopecCardInfo, HttpSession session)
			throws Exception {
		logger.info("加油卡生成订单");
		String message = INITIALIZEMESSAGE;
		String jsonString = null;
		Object obj = parseJsonString(sinopecCardInfo,
				GeneralSinopecCardOrderRequestDTO.class);
		GeneralSinopecCardOrderResponseDTO responseData = new GeneralSinopecCardOrderResponseDTO();
		String inprice = "";
		String paymentAmount = "";
		
		SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
		
		String oAgentNo = sessionInfo.getoAgentNo();
		String mercId = sessionInfo.getMercId();
        if(StringUtils.isBlank(oAgentNo) || StringUtils.isBlank(mercId)){
            responseData.setRetCode(1);
            responseData.setRetMessage("参数错误");
            jsonString = createJsonString(responseData);
            return jsonString;
        }
		
		//校验欧单的模块
		ResultInfo resultInfo = publicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.sinopecPay,oAgentNo);
        if(!resultInfo.getErrCode().equals("0")){
        	responseData.setRetCode(1);
        	responseData.setRetMessage(resultInfo.getMsg());
        	jsonString = createJsonString(resultInfo);
        	
        	logger.info("O单业务受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.sinopecPay.getTypeName()+",msg:"+resultInfo.getMsg());
            return jsonString;
        }else{
        	//校验商户的模块
        	resultInfo = publicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.sinopecPay,mercId);
            if(!resultInfo.getErrCode().equals("0")){
            	responseData.setRetCode(1);
            	responseData.setRetMessage(resultInfo.getMsg());
            	jsonString = createJsonString(resultInfo);
            	
            	logger.info("商户业务受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.sinopecPay.getTypeName()+",msg:"+resultInfo.getMsg());
                return jsonString;
            }
        }

		if (null != sessionInfo) {
			oAgentNo = sessionInfo.getoAgentNo();

            if(StringUtils.isBlank(oAgentNo)){
                //如果没有欧单编号，直接返回错误
                responseData.setRetCode(1);
                responseData.setRetMessage("参数错误");
                jsonString = createJsonString(responseData);
            }
            
			if (!obj.equals(DATAPARSINGMESSAGE)) {

				GeneralSinopecCardOrderRequestDTO generalSinopecCardOrderRequestDTO = (GeneralSinopecCardOrderRequestDTO) obj;
				// 分发请求
				if (generalSinopecCardOrderRequestDTO.getPayType().equals("1")) {
					//校验欧单的支付方式限制
					resultInfo = publicTradeVerifyService.paytypeVerifyOagent(PaymentCodeEnum.shuakaPay,oAgentNo);
			        if(!resultInfo.getErrCode().equals("0")){
			        	responseData.setRetCode(1);
			        	responseData.setRetMessage(resultInfo.getMsg());
			        	jsonString = createJsonString(resultInfo);
			        	
			        	logger.info("O单支付方式受限，oAagentNo:"+oAgentNo+",payment:"+PaymentCodeEnum.shuakaPay.getTypeName()+",msg:"+resultInfo.getMsg());
			            return jsonString;
			        }else{
			        	//校验商户的支付方式限制
			        	resultInfo = publicTradeVerifyService.payTypeVerifyMer(PaymentCodeEnum.shuakaPay,mercId);
			            if(!resultInfo.getErrCode().equals("0")){
			            	responseData.setRetCode(1);
			            	responseData.setRetMessage(resultInfo.getMsg());
			            	jsonString = createJsonString(resultInfo);
			            	
			            	logger.info("商户支付方式受限，mercId:"+mercId+",payment:"+PaymentCodeEnum.shuakaPay.getTypeName()+",msg:"+resultInfo.getMsg());
			                return jsonString;
			            }
			        }
				}else if (generalSinopecCardOrderRequestDTO
						.getPayType().equals("2")) {
					// 第三方支付
					if (StringUtils
							.isNotBlank(generalSinopecCardOrderRequestDTO
									.getPayChannel())) {
						if (generalSinopecCardOrderRequestDTO
								.getPayChannel().equals("1")) {
							// 支付宝SDK
							// 设置费率，手续费
						} else if (generalSinopecCardOrderRequestDTO
								.getPayChannel().equals("2")) {
							// 微信SDK
							// 设置费率，手续费
						} else if (generalSinopecCardOrderRequestDTO
								.getPayChannel().equals("3")) {
							// 百度SDK
							//校验欧单的支付方式限制
							resultInfo = publicTradeVerifyService.paytypeVerifyOagent(PaymentCodeEnum.bdSDKPay,oAgentNo);
					        if(!resultInfo.getErrCode().equals("0")){
					        	responseData.setRetCode(1);
					        	responseData.setRetMessage(resultInfo.getMsg());
					        	jsonString = createJsonString(resultInfo);
					        	
					        	logger.info("O单支付方式受限，oAagentNo:"+oAgentNo+",payment:"+PaymentCodeEnum.bdSDKPay.getTypeName()+",msg:"+resultInfo.getMsg());
					            return jsonString;
					        }else{
					        	//校验商户的支付方式限制
					        	resultInfo = publicTradeVerifyService.payTypeVerifyMer(PaymentCodeEnum.bdSDKPay,mercId);
					            if(!resultInfo.getErrCode().equals("0")){
					            	responseData.setRetCode(1);
					            	responseData.setRetMessage(resultInfo.getMsg());
					            	jsonString = createJsonString(resultInfo);
					            	
					            	logger.info("商户支付方式受限，mercId:"+mercId+",payment:"+PaymentCodeEnum.bdSDKPay.getTypeName()+",msg:"+resultInfo.getMsg());
					                return jsonString;
					            }
					        }
						}
					}
				}
				
				paymentAmount = generalSinopecCardOrderRequestDTO.getRechargeAmt().toString();
				
				//校验欧单的模块金额限制
				resultInfo = publicTradeVerifyService.amountVerifyOagent(Integer.parseInt(paymentAmount),TradeTypeEnum.sinopecPay,oAgentNo);
		        if(!resultInfo.getErrCode().equals("0")){
		        	responseData.setRetCode(1);
		        	responseData.setRetMessage(resultInfo.getMsg());
		        	jsonString = createJsonString(resultInfo);
		        	
		        	logger.info("O单业务金额受限，oAagentNo:"+oAgentNo+",tradeType:"+TradeTypeEnum.sinopecPay.getTypeName()+",amount:"+paymentAmount+",msg:"+resultInfo.getMsg());
		            return jsonString;
		        }else{
		        	//校验商户的模块金额限制
		        	resultInfo = publicTradeVerifyService.amountVerifyMer(Integer.parseInt(paymentAmount),TradeTypeEnum.sinopecPay,oAgentNo);
		            if(!resultInfo.getErrCode().equals("0")){
		            	responseData.setRetCode(1);
		            	responseData.setRetMessage(resultInfo.getMsg());
		            	jsonString = createJsonString(resultInfo);
		            	
		            	logger.info("商户业务金额受限，mercId:"+mercId+",tradeType:"+TradeTypeEnum.sinopecPay.getTypeName()+",amount:"+paymentAmount+",msg:"+resultInfo.getMsg());
		                return jsonString;
		            }
		        }

				String cardId = generalSinopecCardOrderRequestDTO.getCardId();
				String orderId ="";
				if (cardId.length() >= 16 && cardId.length() <= 19) {

					if (cardId.length() == 19
							&& "100011".equals(cardId.substring(0, 6))) {
						cardId = "64127500";
						orderId = UtilMethod.getOrderid("150");
					} else if (cardId.length() == 16
							&& "9".equals(cardId.substring(0, 1))) {
						cardId = "64349102";
						orderId = UtilMethod.getOrderid("151");
					}

					// 分发请求
					if (StringUtils
							.isNotBlank(generalSinopecCardOrderRequestDTO
									.getPayType())) {

						PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
						pmsAppTransInfo.setTradetype("加油卡");// 加油卡
						pmsAppTransInfo.setTradetypecode("6");// 加油卡
						pmsAppTransInfo.setTradetime(UtilDate
								.getDateFormatter());
						pmsAppTransInfo.setOrderid(orderId);// 加油卡业务编码

						paymentAmount = generalSinopecCardOrderRequestDTO
								.getRechargeAmt().toString();

						pmsAppTransInfo.setPayamount(paymentAmount);// 设置交易金额

						pmsAppTransInfo.setMercid(sessionInfo.getMercId()); // 设置商户id
						pmsAppTransInfo.setFactamount(paymentAmount); // 设置实际金额
						pmsAppTransInfo.setOrderamount(paymentAmount);// 设置订单金额
						pmsAppTransInfo.setoAgentNo(oAgentNo);
						int appTransInsert = pmsAppTransInfoDao
								.insert(pmsAppTransInfo);

						if (appTransInsert == 1) {
							// 插入成功，下面的操作

							AppOrderDetail appOrderDetail = new AppOrderDetail();
							appOrderDetail.setOrderId(orderId);
							appOrderDetail.setCardId(cardId);
							appOrderDetail
									.setCardnum(generalSinopecCardOrderRequestDTO
											.getRechargeAmt().toString());
							appOrderDetail
									.setGasCardTel(generalSinopecCardOrderRequestDTO
											.getGasCardTel());
							appOrderDetail
									.setAccount(generalSinopecCardOrderRequestDTO
											.getCardId());
							appOrderDetail.setAccountName(session.getAttribute(
									"accountName").toString());

							int count = appOrderDetailDao
									.insert(appOrderDetail);
							if (count == 1) {
								message = SUCCESSMESSAGE;

								if (generalSinopecCardOrderRequestDTO
										.getPayType().equals("1")) {
									// 刷卡支付 约定这里只操作订单表，三方前置负责流水表处理
									BrushCalorieOfConsumptionRequestDTO dto = generalSinopecCardOrderRequestDTO
											.getDto();
									// 获取通道的费率
									Map<String, String> paramMap = new HashMap<String, String>();
                                    paramMap.put("mercid",sessionInfo.getMercId());//商户编号
									paramMap.put("businesscode",
											TradeTypeEnum.transeMoney
													.getTypeCode());// 业务编号
									Map<String, String> resultMap = merchantMineDao
											.queryBusinessInfo(paramMap);

									String isTop = resultMap.get("IS_TOP");
									String rate = resultMap.get("RATE");
									String topPoundage = resultMap
											.get("TOP_POUNDAGE");// 封顶费率
									String maxTransMoney = resultMap
											.get("MAX_AMOUNT"); // 每笔最大交易金额
									String minTransMoney = resultMap
											.get("MIN_AMOUNT"); // 每笔最小交易金额

									if (Double.parseDouble(paymentAmount) > Double
											.parseDouble(maxTransMoney)) {
										// 金额超过最大金额
										responseData.setRetCode(1);
										responseData.setRetMessage("金额超过最大金额");
										logger.info("交易金额大于最打金额");
										try {
											jsonString = createJsonString(responseData);
										} catch (Exception em) {
											em.printStackTrace();
										}
										return jsonString;
									} else if (Double
											.parseDouble(paymentAmount) < Double
											.parseDouble(minTransMoney)) {
										// 金额小于最小金额
										responseData.setRetCode(1);
										responseData.setRetMessage("金额小于最小金额");
										try {
											jsonString = createJsonString(responseData);
										} catch (Exception em) {
											em.printStackTrace();
										}
										logger.info("交易金额小于最小金额");
										return jsonString;
									}

									Double factAmount = 0.0;
									// 费率
									Double fee = 0.0;
									String rateStr = "";
									// 计算实际金额
									if ("1".equals(isTop)) {

										rateStr = rate + "-" + topPoundage;
										// 是封顶费率类型
										fee = Double.parseDouble(rate)
												* Double
														.parseDouble(paymentAmount);

										if (fee > Double
												.parseDouble(topPoundage)) {
											// 费率大于最大手续费，按最大手续费处理
											factAmount = Double
													.parseDouble(topPoundage)
													+ Double
															.parseDouble(paymentAmount);
											fee = Double
													.parseDouble(topPoundage);
										} else {
											// 按当前费率处理
											rateStr = rate;
											factAmount = Double
													.parseDouble(paymentAmount)
													+ fee;
										}

									} else {
										// 按当前费率处理
										rateStr = rate;
										fee = Double.parseDouble(rate)
												* Double
														.parseDouble(paymentAmount);
										factAmount = Double
												.parseDouble(paymentAmount)
												+ fee;
									}
									dto.setPayAmount(String.valueOf((int) Math
											.ceil(factAmount)));
									String sendStr8583 = "param="
											+ createBrushCalorieOfConsumptionDTORequest(
													sessionInfo,
													dto,
													pmsAppTransInfo
															.getOrderid(),
													CREDITTWOCARDPAYMENTCONSUMPTIONBUSINESSNUM,
													rateStr, dto.getSn());
									if ("param=fail".equals(sendStr8583)) {
										// 上送参数错误
										logger.info("上送参数错误， 订单号：" + orderId
												+ "，结束时间："
												+ UtilDate.getDateFormatter());
										// 金额小于最小金额
										responseData.setRetCode(1);
										responseData.setRetMessage("上送参数错误");
										try {
											jsonString = createJsonString(responseData);
										} catch (Exception em) {
											em.printStackTrace();
										}
										return jsonString;
									} else if ("param=meros"
											.equals(sendStr8583)) {
										// 上送参数错误
										logger.info("pos机信息读取失败， 订单号："
												+ orderId + "，结束时间："
												+ UtilDate.getDateFormatter());
										// 金额小于最小金额
										responseData.setRetCode(1);
										responseData
												.setRetMessage("pos机信息读取失败，不支持的卡类型");
										try {
											jsonString = createJsonString(responseData);
										} catch (Exception em) {
											em.printStackTrace();
										}
										return jsonString;
									} else {

										logger.info("调用三方前置刷卡接口请求参数："
												+ sendStr8583 + "，结束时间："
												+ UtilDate.getDateFormatter());
										ViewKyChannelInfo channelInfo = AppPospContext.context
												.get(SHUAKA + REMITPAYMENT);

										String successFlag = HttpURLConection
												.httpURLConnectionPOST(
														channelInfo.getUrl(),
														sendStr8583);

										logger.info("调用三方前置刷卡接口返回参数："
												+ successFlag + "，结束时间："
												+ UtilDate.getDateFormatter());

										BrushCalorieOfConsumptionResponseDTO response = (BrushCalorieOfConsumptionResponseDTO) parseJsonString(
												successFlag,
												BrushCalorieOfConsumptionResponseDTO.class);

										if ("0000"
												.equals(response.getRetCode())) {// 判断调用接口处理是否成功
											// 0000表示刷卡成功
											// 修改订单状态 加入相关信息
											PmsAppTransInfo pmsAppTrans = pmsAppTransInfoDao
													.searchOrderInfo(orderId);
											pmsAppTrans
													.setStatus(OrderStatusEnum.waitingPlantPay
															.getStatus());
											pmsAppTrans
													.setFactamount(factAmount
															.toString());// 设置实际金额
											pmsAppTrans.setBankno(dto
													.getCardNo());// 设置卡号
											pmsAppTrans.setPoundage(fee
													.toString()); // 设置费率
											pmsAppTrans.setPaymentcode("5");// 刷卡支付
											pmsAppTrans
													.setBrushType(generalSinopecCardOrderRequestDTO
															.getBrushType());// 设置刷卡类型
											pmsAppTrans.setSnNO(dto.getSn());// 设置sn
											pmsAppTrans.setRate(rateStr);// 设置费率
											pmsAppTrans.setPaymenttype("刷卡支付");
											List<PayCmmtufit> cardList = payCmmtufitDao
													.searchCardInfoByBeforeSix(dto
															.getCardNo()
															.substring(0, 6)
															+ "%");
											if (cardList != null
													&& cardList.size() > 0) {
												pmsAppTrans
														.setBankname(cardList
																.get(0)
																.getBnkName());
											}

											pmsAppTrans
													.setBusinessNum(SINOPECORDER);
											pmsAppTrans
													.setChannelNum(SELFCHANEL);
											Integer appTransUpdate = pmsAppTransInfoDao
													.update(pmsAppTrans);
											if (appTransUpdate == 1) {
												// 调用欧飞接口充值话费
												Integer resultOffi = offiPay
														.sinopecOrder(pmsAppTrans);
												if (resultOffi == 1) {// 支付成功
													// 支付成功，修改订单状态
													pmsAppTrans
															.setThirdPartResultCode(resultOffi
																	.toString());
													pmsAppTrans
															.setFinishtime(UtilDate
																	.getDateFormatter());
													pmsAppTrans
															.setThirdPartResultCode("1");// 设置第三方返回码
													pmsAppTrans
															.setStatus(OrderStatusEnum.paySuccess
																	.getStatus());
													pmsAppTransInfoDao
															.update(pmsAppTrans);
												} else if (resultOffi == 2) { // 正在支付，将状态改为正在支付
													pmsAppTrans
															.setThirdPartResultCode(resultOffi
																	.toString());
													pmsAppTrans
															.setStatus(OrderStatusEnum.plantPayingNow
																	.getStatus());
													pmsAppTransInfoDao
															.update(pmsAppTrans);
												}

												message = SUCCESSMESSAGE;
											} else {
												// 刷卡支付错误
												logger
														.info("更新订单出错， 订单号："
																+ orderId
																+ "，结束时间："
																+ UtilDate
																		.getDateFormatter());
												responseData.setRetCode(1);
												responseData
														.setRetMessage("更新订单出错，请查询订单");
												try {
													jsonString = createJsonString(responseData);
												} catch (Exception em) {
													em.printStackTrace();
												}
												return jsonString;
											}
										} else {
											responseData.setRetCode(1);
											responseData.setRetMessage("错误码："
													+ response.getRetCode()
													+ "\n错误信息："
													+ response.getRetMessage());
											logger
													.info("订单生成失败， 订单号："
															+ orderId
															+ "，结束时间："
															+ UtilDate
																	.getDateFormatter());
											try {
												jsonString = createJsonString(responseData);
											} catch (Exception em) {
												em.printStackTrace();
											}
											return jsonString;
										}
									}
								} else if (generalSinopecCardOrderRequestDTO
										.getPayType().equals("2")) {
									// 第三方支付
									if (StringUtils
											.isNotBlank(generalSinopecCardOrderRequestDTO
													.getPayChannel())) {
										if (generalSinopecCardOrderRequestDTO
												.getPayChannel().equals("1")) {
											// 支付宝SDK
											// 设置费率，手续费
										} else if (generalSinopecCardOrderRequestDTO
												.getPayChannel().equals("2")) {
											// 微信SDK
											// 设置费率，手续费
										} else if (generalSinopecCardOrderRequestDTO
												.getPayChannel().equals("3")) {
											// 百度SDK

											// 查询当前订单
											// 计算费率
											String rateStr = "0.006";
											AppRateConfig appC = new AppRateConfig();
											appC.setRateType("3");
											appC.setoAgentNo(oAgentNo);
											AppRateConfig appRateConfig = appRateConfigDao
													.getByRateTypeAndoAgentNo(appC);
											if (appRateConfig != null
													&& StringUtils
															.isNotBlank(appRateConfig
																	.getRate())) {
												rateStr = appRateConfig
														.getRate();
											}

											Double fee = Double
													.parseDouble(rateStr)
													* Double
															.parseDouble(paymentAmount);
											Double factAmount = Double
													.parseDouble(paymentAmount)
													+ fee;
											PmsAppTransInfo pmsAppTrans = pmsAppTransInfoDao
													.searchOrderInfo(orderId);
											pmsAppTrans.setFactamount(String
													.valueOf(Math
															.ceil(factAmount)));
											pmsAppTrans.setPoundage(fee
													.toString());
											pmsAppTrans.setPaymenttype("百度支付");
											pmsAppTrans.setPaymentcode("2");
											pmsAppTrans
													.setStatus(OrderStatusEnum.waitingClientPay
															.getStatus());
											pmsAppTrans.setRate(rateStr);
											pmsAppTrans
													.setBusinessNum(BAIDUCALLBACKURL);
											// 设置费率，手续费(百度没有)
											responseData.setOrderNumber(orderId);
											responseData
													.setPageUrl(BDUtil
															.generalBDSDKCallStr(pmsAppTrans));
											if (pmsAppTransInfoDao
													.update(pmsAppTrans) == 1) {
												message = SUCCESSMESSAGE;
											}
										}
									}
								}
							} else {
								// 插入数据错误
								logger.info("插入数据错误， 订单号：" + orderId + "，结束时间："
										+ UtilDate.getDateFormatter());
								// 金额小于最小金额
								responseData.setRetCode(1);
								responseData.setRetMessage("系统错误，请重新下单");
								try {
									jsonString = createJsonString(responseData);
								} catch (Exception em) {
									em.printStackTrace();
								}
								return jsonString;
							}
						}
					}
				} else {
					message = 1 + ":输入卡信息错误";
				}
			}
		} else {
			message = RetAppMessage.SESSIONINVALIDATION;
		}
		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("dataParsing")) {
			retMessage = "数据解析错误";
		} else if (retMessage.equals("success")) {
			retMessage = "充值成功";
		} else if (retMessage.equals("fail")) {
			retMessage = "查询失败";
		} else if (retMessage.equals("sessionInvalidation")) {
			retMessage = "会话失效，请重新登录";
		}
		responseData.setRetCode(retCode);
		responseData.setRetMessage(retMessage);
		jsonString = createJsonString(responseData);
		return jsonString;
	}

	/**
	 * 中石化加油卡生成订单异常
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public String producedOrderException(HttpSession session) throws Exception {
		GeneralSinopecCardOrderResponseDTO responseData = new GeneralSinopecCardOrderResponseDTO();
		responseData.setRetCode(100);
		responseData.setRetMessage("系统异常");

		logger.info("[app_rsp]" + createJson(responseData));

		return createJsonString(responseData);
	}

}

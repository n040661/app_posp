package xdt.service.impl;

import com.alibaba.fastjson.JSON;
import com.kspay.MD5Util;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.BaseUtil;
import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.conformityQucikPay.entity.ConformityQucikPayRequestEntity;
import xdt.quickpay.conformityQucikPay.entity.ConformityQuickPayQueryRequestEntity;
import xdt.quickpay.conformityQucikPay.util.OrderStatusEnum;
import xdt.quickpay.conformityQucikPay.util.UtilDate;
import xdt.quickpay.yb.util.YeepayService;
import xdt.service.IConformityQucikPayService;
import xdt.service.IPublicTradeVerifyService;
import xdt.servlet.AppPospContext;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.PaymentCodeEnum;
import xdt.util.RSAUtil;
import xdt.util.TradeTypeEnum;

@Service
public class ConformityQucikPayServiceImpl extends BaseServiceImpl implements IConformityQucikPayService {

	private Logger logger = Logger.getLogger(ConformityQucikPayServiceImpl.class);
	public static final String[] TRADEORDER = { "parentMerchantNo", "merchantNo", "orderId", "orderAmount",
			"timeoutExpress", "requestDate", "redirectUrl", "notifyUrl", "goodsParamExt", "paymentParamExt",
			"industryParamExt", "memo", "riskParamExt", "csUrl" };
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	@Resource
	private OriginalOrderInfoDao originalDao;
	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao;
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;
	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;
	@Resource
	private IMerchantMineDao merchantMineDao;
	@Resource
	private IAppRateConfigDao appRateConfigDao;

	public Map<String, String> payHandle(ConformityQucikPayRequestEntity originalinfo) throws Exception {
		this.logger.info("快捷(WAP)支付请求参数：" + JSON.toJSONString(originalinfo));
		Map<String, String> retMap = new HashMap();

		String merchId = originalinfo.getV_mid();

		String acount = originalinfo.getV_txnAmt();

		this.logger.info("******************根据商户号查询################");

		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getV_oid());
		orig.setPid(originalinfo.getV_mid());
		if (this.originalDao.selectByOriginal(orig) != null) {
			this.logger.info("下单重复");
			return setResp("03", "下单重复");
		}
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getV_oid());
		original.setOrderId(originalinfo.getV_oid());
		original.setPid(originalinfo.getV_mid());
		original.setOrderTime(originalinfo.getV_time());
		original.setOrderAmount(originalinfo.getV_txnAmt());
		original.setProcdutName(originalinfo.getV_productDesc());
		original.setProcdutDesc(originalinfo.getV_productDesc());
		original.setPayType(originalinfo.getV_type());
		original.setPageUrl(originalinfo.getV_url());
		original.setBgUrl(originalinfo.getV_notify_url());
		original.setAttach(originalinfo.getV_attach());
		original.setBankType(originalinfo.getV_cardType());
		original.setByUser(originalinfo.getV_userId());
		this.originalDao.insert(original);

		String mercId = originalinfo.getV_mid();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		String oAgentNo = "";

		List<PmsMerchantInfo> merchantList = this.pmsMerchantInfoDao.searchList(merchantinfo);
		if ((merchantList.size() != 0) && (!merchantList.isEmpty())) {
			merchantinfo = (PmsMerchantInfo) merchantList.get(0);

			oAgentNo = merchantinfo.getoAgentNo();
			if (StringUtils.isBlank(oAgentNo)) {
				this.logger.error("参数错误!");
				retMap.put("v_code", "04");
				retMap.put("v_msg", "参数错误,没有欧单编号");
				return retMap;
			}
			if ("60".equals(merchantinfo.getMercSts())) {
				this.logger.info("是正式商户");

				String factAmount = "" + new BigDecimal(originalinfo.getV_txnAmt()).multiply(new BigDecimal(100));

				PmsBusinessPos pmsBusinessPos = selectKey(originalinfo.getV_mid());

				ResultInfo payCheckResult = this.iPublicTradeVerifyService
						.amountVerifyOagent((int) Double.parseDouble(factAmount), TradeTypeEnum.onlinePay, oAgentNo);
				if (!payCheckResult.getErrCode().equals("0")) {
					this.logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return setResp("05", "欧单金额限制，请重试或联系客服");
				}
				ResultInfo resultInfoForOAgentNo = this.iPublicTradeVerifyService
						.moduleVerifyOagent(TradeTypeEnum.onlinePay, oAgentNo);
				if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
					if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
						this.logger.error("交易关闭，请重试或联系客服");
						return setResp("06", "交易关闭，请重试或联系客服");
					}
					return setResp("07", "系统异常，请重试或联系客服");
				}
				ResultInfo payCheckResult3 = this.iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.onlinePay,
						mercId);
				if (!payCheckResult3.getErrCode().equals("0")) {
					this.logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
							+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					return setResp("08", "商户模块限制,请重试或联系客服");
				}
				Map<String, String> paramMap = new HashMap();
				paramMap.put("mercid", merchantinfo.getMercId());
				paramMap.put("businesscode", TradeTypeEnum.onlinePay.getTypeCode());
				paramMap.put("oAgentNo", oAgentNo);

				Map<String, String> resultMap = this.merchantMineDao.queryBusinessInfo(paramMap);
				if ((resultMap == null) || (resultMap.size() == 0)) {
					this.logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
					return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
				}
				String maxTransMoney = (String) resultMap.get("MAX_AMOUNT");
				String minTransMoney = (String) resultMap.get("MIN_AMOUNT");
				String paymentAmount = factAmount;
				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
					this.logger.info("交易金额大于最打金额");
					return setResp("10", "金额超过最大交易金额");
				}
				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
					this.logger.info("交易金额小于最小金额");
					return setResp("11", "交易金额小于最小金额");
				}
				PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();

				pmsAppTransInfo.setoAgentNo(oAgentNo);
				pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
				pmsAppTransInfo.setTradetype(TradeTypeEnum.onlinePay.getTypeName());

				pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
				pmsAppTransInfo.setMercid(merchantinfo.getMercId());
				pmsAppTransInfo.setTradetypecode(TradeTypeEnum.onlinePay.getTypeCode());

				pmsAppTransInfo.setOrderid(originalinfo.getV_oid());
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay.getTypeCode());
				BigDecimal factBigDecimal = new BigDecimal(factAmount);
				BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

				pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());
				pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());
				pmsAppTransInfo.setDrawMoneyType("1");
				if ("0".equals(originalinfo.getV_type())) {
					pmsAppTransInfo.setSettlementState("D0");
				}
				if ("1".equals(originalinfo.getV_type())) {
					pmsAppTransInfo.setSettlementState("T1");
				}
				Integer insertAppTrans = Integer.valueOf(this.pmsAppTransInfoDao.insert(pmsAppTransInfo));
				if (insertAppTrans.intValue() == 1) {
					pmsAppTransInfo = this.pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

					String quickRateType = ((String) resultMap.get("QUICKRATETYPE")).toString();

					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = this.appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
					if (appRateConfig == null) {
						this.logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
						return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
					}
					String isTop = appRateConfig.getIsTop();
					String rate = appRateConfig.getRate();
					String topPoundage = appRateConfig.getTopPoundage();
					paymentAmount = pmsAppTransInfo.getFactamount();
					String minPoundageStr = appRateConfig.getBottomPoundage();
					Double minPoundage = Double.valueOf(0.0D);
					if ((StringUtils.isNotBlank(appRateConfig.getIsBottom()))
							&& (appRateConfig.getIsBottom().equals("1"))) {
						if (StringUtils.isNotBlank(minPoundageStr)) {
							minPoundage = Double.valueOf(Double.parseDouble(minPoundageStr));
						} else {
							this.logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
						}
					}
					BigDecimal payAmount = new BigDecimal("0");
					BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());

					BigDecimal fee = new BigDecimal(0);
					String rateStr = "";
					if ("1".equals(isTop)) {
						rateStr = rate + "-" + topPoundage;

						fee = new BigDecimal(rate).multiply(dfactAmount);
						if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
							payAmount = dfactAmount.subtract(
									new BigDecimal(topPoundage).subtract(new BigDecimal(minPoundage.doubleValue())));
							fee = new BigDecimal(topPoundage).add(new BigDecimal(minPoundage.doubleValue()));
						} else {
							rateStr = rate;
							fee.add(new BigDecimal(minPoundage.doubleValue()));
							payAmount = dfactAmount.subtract(fee);
						}
					} else {
						double payfee = Double.parseDouble(merchantinfo.getCounter());

						rateStr = rate;
						BigDecimal num = dfactAmount.multiply(new BigDecimal(rateStr));
						if (num.doubleValue() / 100.0D >= payfee) {
							fee = num;
						} else {
							fee = new BigDecimal(payfee * 100.0D);
						}
						payAmount = dfactAmount.subtract(fee);
						this.logger.info("清算金额:" + paymentAmount);
						if (payAmount.doubleValue() < 0.0D) {
							payAmount = new BigDecimal(0.0D);
						}
					}
					pmsAppTransInfo.setPayamount(payAmount.toString());
					pmsAppTransInfo.setRate(rateStr);
					pmsAppTransInfo.setPoundage(fee.toString());
					pmsAppTransInfo.setDrawMoneyType("1");

					Integer paymentAmountInt = Integer.valueOf((int) Double.parseDouble(paymentAmount));

					payCheckResult = this.iPublicTradeVerifyService.totalVerify(paymentAmountInt.intValue(),
							TradeTypeEnum.onlinePay, PaymentCodeEnum.hengFengQuickPay, oAgentNo,
							merchantinfo.getMercId());
					if (!payCheckResult.getErrCode().equals("0")) {
						this.logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
								+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
						return setResp("13", "暂不支持该交易方式");
					}
					ViewKyChannelInfo channelInfo = (ViewKyChannelInfo) AppPospContext.context
							.get(HENGFENGPAY + HENGFENGCHANNELNUM);

					pmsAppTransInfo.setBusinessNum(channelInfo.getBusinessnum());
					pmsAppTransInfo.setChannelNum(HENGFENGCHANNELNUM);

					PospTransInfo pospTransInfo = null;

					int insertOrUpdateFlag = 0;

					String transOrderId = generateTransOrderId(TradeTypeEnum.onlinePay,
							PaymentCodeEnum.hengFengQuickPay);
					if ((pospTransInfo = this.pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
						this.logger.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
						pospTransInfo.setTransOrderId(originalinfo.getV_oid());
						pospTransInfo.setResponsecode("99");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
					} else {
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);

						System.out.println("流水表生成的时间:" + pospTransInfo.getSenddate());

						pospTransInfo.setTransOrderId(originalinfo.getV_oid());
						insertOrUpdateFlag = 0;
					}
					if (insertOrUpdateFlag == 0) {
						this.pospTransInfoDAO.insert(pospTransInfo);
					} else if (insertOrUpdateFlag == 1) {
						this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
					this.logger.info("修改订单信息");
					this.logger.info(pmsAppTransInfo);

					int num = this.pmsAppTransInfoDao.update(pmsAppTransInfo);
					if (num > 0) {
						this.logger.info("上游通道商户号:" + pmsBusinessPos.getChannelnum());

						String merId = pmsBusinessPos.getChannelnum();

						String merKey = pmsBusinessPos.getKek();
						String str1;
						switch (merId) {
						case "MBXHF":
							this.logger.info("################摩宝(WAP)支付开始处理#################");
							retMap.put("versionId", "001");
							retMap.put("businessType", "1100");
							retMap.put("merId", "936327552190000");
							retMap.put("orderId", originalinfo.getV_oid());
							retMap.put("transDate", originalinfo.getV_time());
							retMap.put("transAmount", originalinfo.getV_txnAmt());
							retMap.put("transCurrency", "156");
							retMap.put("transChanlName", "UNIONPAY");

							retMap.put("pageNotifyUrl", BaseUtil.url + "/conformity/mbReturnUrl.action");
							retMap.put("backNotifyUrl", BaseUtil.url + "/conformity/mbNotifyUrl.action");
							String attch = new String(originalinfo.getV_attach().getBytes("ISO-8859-1"), "GBK");
							retMap.put("dev", attch);

							String signstr = "versionId=001&businessType=1100&merId=936327552190000&orderId="
									+ originalinfo.getV_oid() + "&transDate=" + originalinfo.getV_time()
									+ "&transAmount=" + originalinfo.getV_txnAmt()
									+ "&transCurrency=156&transChanlName=UNIONPAY&pageNotifyUrl="
									+ ((String) retMap.get("pageNotifyUrl")).toString() + "&backNotifyUrl="
									+ ((String) retMap.get("backNotifyUrl")).toString() + "&dev=" + attch;
							this.logger.info("需要签名的明文" + signstr);
							String signtrue = MD5Util.MD5Encode(signstr + "10ED8DE4DFE53D48");

							retMap.put("signData", signtrue);
							retMap.put("v_code", "00");
							break;
						case "YBLS":
							this.logger.info("################易宝(WAP)支付开始处理#################");
							String orderId = originalinfo.getV_oid();
							String orderAmount = originalinfo.getV_txnAmt();
							String timeoutExpress = "";
							String requestDate = UtilDate.getDateFormatter();
							String redirectUrl = "http://www.lssc888.com/shop/control/yibao_return_vt.php";
							String notifyUrl = "http://www.lssc888.com/shop/control/yibao_notify_vt.php";
							String goodsName = originalinfo.getV_productDesc();
							String goodsDesc = originalinfo.getV_productDesc();
							String paymentParamExt = "";
							String bizSource = "";
							String bizEntity = "";
							String memo = "";
							String riskParamExt = "";
							String csUrl = "";

							String goodsParamExt = "{\"goodsName\":\"" + goodsName + "\",\"goodsDesc\":\"" + goodsDesc
									+ "\"}";
							String industryParamExt = "{\"bizSource\":\"" + bizSource + "\",\"bizEntity\":\""
									+ bizEntity + "\"}";

							this.logger.info("goodsParamExt:" + goodsParamExt);
							Map<String, String> params = new HashMap();
							params.put("orderId", orderId);
							params.put("orderAmount", orderAmount);
							params.put("timeoutExpress", "");
							params.put("requestDate", requestDate);
							params.put("redirectUrl", redirectUrl);
							params.put("notifyUrl", notifyUrl);
							params.put("goodsParamExt", goodsParamExt);
							params.put("paymentParamExt", paymentParamExt);
							params.put("industryParamExt", industryParamExt);
							params.put("memo", memo);
							params.put("riskParamExt", riskParamExt);
							params.put("csUrl", csUrl);

							this.logger.info("token上送的数据:" + params);
							String uri = "/rest/v1.0/std/trade/order";
							Map<String, String> result = YeepayService.requestYOP(params, uri, TRADEORDER);
							this.logger.info("上游返回的数据:" + result);
							if ("OPR00000".equals(result.get("code"))) {
								String token = (String) result.get("token");
								this.logger.info("获取易宝返回的token：" + token);
								String parentMerchantNo = "10018465070";
								String merchantNo = "10018465070";

								String timestamp = String.valueOf(Math.round((float) (new Date().getTime() / 1000L)));
								String directPayType = "YJZF";
								String cardType = "";
								if ("1".equals(originalinfo.getV_cardType())) {
									cardType = "DEBIT";
								}
								if ("2".equals(originalinfo.getV_cardType())) {
									cardType = "CREDIT";
								}
								String userNo = UtilDate.getOrderNum();
								String userType = "MAC";
								String appId = "";
								String openId = "";
								String clientId = "";

								String ext = "";

								params = new HashMap();
								params.put("parentMerchantNo", parentMerchantNo);
								params.put("merchantNo", merchantNo);
								params.put("token", token);
								params.put("timestamp", timestamp);
								params.put("directPayType", directPayType);
								params.put("cardType", cardType);
								params.put("userNo", userNo);
								params.put("userType", userType);
								params.put("ext", ext);
								String url = YeepayService.getUrl(params);
								this.logger.info("向上游发送的数据:" + url);
								retMap.put("path", url);
								retMap.put("v_code", "00");
								retMap.put("v_msg", "请求成功");
							} else {
								retMap.put("v_code", "15");
								retMap.put("v_msg", "请求失败");
								return retMap;
							}
							break;
						case "JS100669":
	                             logger.info("###########江苏电商(WAP)快捷支付##########");
								Map<String, String> resultss = new HashMap<String, String>();
								OriginalOrderInfo originas = new OriginalOrderInfo();
								originas.setMerchantOrderId(originalinfo.getV_oid());
								originas.setPid(originalinfo.getV_mid());
								OriginalOrderInfo originals = originalDao.selectByOriginal(originas);
								logger.info("原始订单信息:" + originals.getOrderAmount());
								logger.info("上游通道信息:" + pmsBusinessPos);
								Map<String, String> paramss = new HashMap<String, String>();
								transOrderId = originalinfo.getV_oid();
								logger.info("下游上送的订单号:" + transOrderId);
								paramss.put("merchantCode", pmsBusinessPos.getBusinessnum());// JsdsUtils.merchantCode
								paramss.put("terminalCode", "02228985");
								paramss.put("orderNum", transOrderId);
								Double amountss = (Double.parseDouble(originals.getOrderAmount()) * 100);
								Integer aa = amountss.intValue();
								paramss.put("transMoney", aa.toString());
								paramss.put("notifyUrl",
										BaseUtil.url + "/conformity/jsNotifyUrl.action");
								paramss.put("returnUrl",
										BaseUtil.url + "/conformity/jsReturnUrl.action");
								paramss.put("commodityName", originals.getProcdutName());
								String apply = HttpUtil.parseParams(paramss);
								logger.info("江苏电商快捷生成签名前的数据:" + apply);
								byte[] signss = xdt.util.RSAUtil.encrypt(pmsBusinessPos.getKek(), apply.getBytes());
								logger.info("江苏电商快捷上送的签名:" + signss);
								Map<String, String> mapss = new HashMap<String, String>();
								mapss.put("groupId", "108269");
								mapss.put("service", "WGZF006");
								mapss.put("signType", "RSA");
								mapss.put("sign", xdt.util.RSAUtil.base64Encode(signss));
								mapss.put("datetime", UtilDate.getOrderNum());
								String jsonmap = HttpUtil.parseParams(mapss);
								logger.info("江苏电商快捷上送数据:" + jsonmap);
								String respJson = HttpURLConection.httpURLConnectionPOST(
										"http://180.96.28.8:8044/TransInterface/TransRequest", jsonmap);
								logger.info("**********江苏电商快捷响应报文:{}" + respJson);
								if (respJson != null) {
									net.sf.json.JSONObject ob = net.sf.json.JSONObject.fromObject(respJson);
									logger.info("封装之后的数据:{}" + ob);
									Iterator it = ob.keys();
									while (it.hasNext()) {
										String key = (String) it.next();
										if (key.equals("pl_code")) {
											String value = ob.getString(key);
											logger.info("提交状态:" + "\t" + value);
											resultss.put("respCode", value);
										}
										if (key.equals("pl_sign")) {
											String value = ob.getString(key);
											logger.info("签名:" + "\t" + value);
											resultss.put("sign", value);
										}
										if (key.equals("pl_datetime")) {
											String value = ob.getString(key);
											logger.info("交易时间:" + "\t" + value);
											resultss.put("pl_datetime", value);
										}
										if (key.equals("pl_message")) {
											String value = ob.getString(key);
											logger.info("交易描述:" + "\t" + value);
											resultss.put("pl_message", value);
										}

									}
									if (resultss.get("respCode").equals("0000")) {

										String sign1 = resultss.get("sign");
										String baseSign = URLDecoder.decode(sign1, "UTF-8");

										baseSign = baseSign.replace(" ", "+");

										byte[] a = xdt.util.RSAUtil.verify(pmsBusinessPos.getKek(),
												xdt.util.RSAUtil.base64Decode(baseSign));

										String Str = new String(a);

										logger.info("解析之后的数据:" + Str);

										String[] array = Str.split("\\&");

										logger.info("拆分数据:" + array);
										String[] list = array[0].split("\\=");
										if (list[0].equals("orderNum")) {
											logger.info("合作商订单号:" + list[1]);

											resultss.put("orderNum", list[1]);

										}
										String[] list1 = array[1].split("\\=");
										if (list1[0].equals("pl_orderNum")) {
											logger.info("平台订单号:" + list1[1]);
											resultss.put("pl_orderNum", list1[1]);

										}
										String list2 = array[2].replaceAll("pl_url=", "");
										logger.info("URL:" + list2);
										retMap.put("pl_url", list2);
										retMap.put("v_code", "00");
										retMap.put("v_msg", "请求成功");
									} else {

										retMap.put("v_code", "15");
										retMap.put("v_msg", "请求失败");
									}
								}
								break;
						case "YSB":
							this.logger.info("################银生宝(WAP)支付开始处理#################");
							Map<String, Object> infoMaps = new HashMap();
							String accountId = pmsBusinessPos.getBusinessnum();
							String customerId = originalinfo.getV_userId();
							String orderNo = originalinfo.getV_oid();
							String commodityName = originalinfo.getV_productDesc();
							String amounts = originalinfo.getV_txnAmt();
							String responseUrl = BaseUtil.url + "/conformity/ysbNotifyUrl.action";
							String pageResponseUrl = BaseUtil.url + "/conformity/ysbReturnUrl.action";
							infoMaps.put("accountId", accountId);
							infoMaps.put("customerId", customerId);
							infoMaps.put("orderNo", orderNo);
							infoMaps.put("commodityName", commodityName);
							infoMaps.put("amount", amounts);
							infoMaps.put("responseUrl", responseUrl);
							infoMaps.put("pageResponseUrl", pageResponseUrl);
							String keys = pmsBusinessPos.getKek();

							String strss = "accountId=" + accountId + "&customerId=" + customerId + "&orderNo="
									+ orderNo + "&commodityName=" + commodityName + "&amount=" + amounts
									+ "&responseUrl=" + responseUrl + "&pageResponseUrl=" + pageResponseUrl + "&key="
									+ keys;
							this.logger.info("银生宝生成签名前的数据:" + strss);
							String signs = MD5Util.MD5Encode(strss).toUpperCase();
							this.logger.info("银生宝生成的签名:" + signs);
							retMap.put("accountId", accountId);
							retMap.put("customerId", customerId);
							retMap.put("orderNo", orderNo);
							retMap.put("commodityName", commodityName);
							retMap.put("amount", amounts);
							retMap.put("responseUrl", responseUrl);
							retMap.put("pageResponseUrl", pageResponseUrl);
							retMap.put("mac", signs);
							retMap.put("v_code", "00");
							break;
						default:							
							break;
						}
					}
				}
			} else {
				this.logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("v_code", "16");
				retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
				return retMap;
			}
		} else {
			this.logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("v_code", "16");
			retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服\"");
			return retMap;
		}
		return retMap;
	}

	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {
		this.logger.info("获取商户密钥信息");
		return this.cmckeyDao.get(merchantId);
	}

	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
		OriginalOrderInfo original = null;

		PospTransInfo transInfo = this.pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		this.logger.info("根据上送订单号  查询商户上送原始信息");
		original = this.originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	public void otherInvoke(String OrderId, String status) throws Exception {
		this.logger.info("上游返回的订单号" + OrderId);
		this.logger.info("上游返回的状态码" + status);

		String transOrderId = OrderId;

		PospTransInfo pospTransInfo = this.pospTransInfoDAO.searchBytransOrderId(transOrderId);
		this.logger.info("流水表信息" + pospTransInfo);

		PmsAppTransInfo pmsAppTransInfo = this.pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		this.logger.info("订单表信息" + pmsAppTransInfo);
		if ("0000".equals(status)) {
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(status);
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());

			int updateAppTrans = this.pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				this.logger.info(pmsAppTransInfo);

				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(OrderId);
				this.logger.info("更新流水");
				this.logger.info(pospTransInfo);
				this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("1001".equals(status)) {
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(status);
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());

			int updateAppTrans = this.pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(OrderId);
				this.logger.info("更新流水");
				this.logger.info(pospTransInfo);
				this.pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
	}

	public int updatePmsMerchantInfo(OriginalOrderInfo originalInfo) throws Exception {
		this.logger.info("代付实时填金:" + JSON.toJSON(originalInfo));
		DecimalFormat df = new DecimalFormat("#.00");

		PmsDaifuMerchantInfo pmsDaifuMerchantInfo = new PmsDaifuMerchantInfo();
		PmsMerchantInfo merchantInfo = this.pmsMerchantInfoDao.selectMercByMercId(originalInfo.getPid());
		this.logger.info("merchantInfo:" + JSON.toJSON(merchantInfo));
		PmsAppTransInfo pmsAppTransInfo = this.pmsAppTransInfoDao.searchOrderInfo(originalInfo.getOrderId());
		this.logger.info("pmsAppTransInfo:" + JSON.toJSON(pmsAppTransInfo));
		pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
		PmsDaifuMerchantInfo daifuMerchantInfo = this.pmsDaifuMerchantInfoDao
				.selectByDaifuMerchantInfo(pmsDaifuMerchantInfo);
		this.logger.info("daifuMerchantInfo:" + JSON.toJSON(daifuMerchantInfo));
		if (daifuMerchantInfo != null) {
			this.logger.info("11111111111111111111111");
			return 0;
		}
		if ("0".equals(merchantInfo.getOpenPay())) {
			Double poundage = Double.valueOf(Double.parseDouble(pmsAppTransInfo.getPoundage()));
			poundage = Double.valueOf(Double.parseDouble(df.format(poundage)));
			String position = merchantInfo.getPosition();
			Double amount = Double.valueOf(Double.parseDouble(originalInfo.getOrderAmount()));
			this.logger.info("订单金额：" + amount);
			BigDecimal positions = new BigDecimal(position);

			Double dd = Double.valueOf(amount.doubleValue() * 100.0D - poundage.doubleValue());

			this.logger.info("来了1---------");
			Map<String, String> map = new HashMap();
			map.put("machId", originalInfo.getPid());
			map.put("payMoney", dd.toString());
			int i = this.pmsMerchantInfoDao.updataPay(map);
			if (i != 1) {
				this.logger.info("实时填金失败！");

				pmsDaifuMerchantInfo.setResponsecode("01");
			} else {
				this.logger.info("实时成功！");
				pmsDaifuMerchantInfo.setResponsecode("00");
			}
			this.logger.info("来到这里了11！");
			PmsMerchantInfo info = select(originalInfo.getPid());

			this.logger.info("来到这里了22！");
			pmsDaifuMerchantInfo.setMercId(originalInfo.getPid());

			pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());

			pmsDaifuMerchantInfo.setAmount(Double.parseDouble(originalInfo.getOrderAmount()) + "");

			pmsDaifuMerchantInfo.setRemarks("D0");

			pmsDaifuMerchantInfo
					.setRecordDescription("订单号:" + originalInfo.getOrderId() + "交易金额:" + originalInfo.getOrderAmount());

			pmsDaifuMerchantInfo.setTransactionType(pmsAppTransInfo.getPaymenttype());

			pmsDaifuMerchantInfo.setPayamount(Double.parseDouble(originalInfo.getOrderAmount()) + "");

			pmsDaifuMerchantInfo.setPosition(info.getPosition());

			pmsDaifuMerchantInfo.setPayCounter(poundage.doubleValue() / 100 + "");
			pmsDaifuMerchantInfo.setOagentno("100333");
			this.logger.info("来了2---------");

			int s = this.pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
			this.logger.info("---s:" + s);
			this.logger.info("来了3---------");

			this.logger.info("---i:" + i);
			return i;
		}
		this.logger.info("此商户未开通代付！！");

		return 0;
	}

	public int updatePmsMerchantInfo80(OriginalOrderInfo originalInfo) throws Exception {

		logger.info("代付实时填金:" + JSON.toJSON(originalInfo));
		DecimalFormat df = new DecimalFormat("#.00");

		PmsDaifuMerchantInfo pmsDaifuMerchantInfo = new PmsDaifuMerchantInfo();
		PmsMerchantInfo merchantInfo = pmsMerchantInfoDao.selectMercByMercId(originalInfo.getPid());
		logger.info("merchantInfo:" + JSON.toJSON(merchantInfo));
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(originalInfo.getOrderId());
		logger.info("pmsAppTransInfo:" + JSON.toJSON(pmsAppTransInfo));
		pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
		PmsDaifuMerchantInfo daifuMerchantInfo = pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(pmsDaifuMerchantInfo);
		logger.info("daifuMerchantInfo:" + JSON.toJSON(daifuMerchantInfo));
		if (daifuMerchantInfo != null) {
			logger.info("11111111111111111111111");
			return 0;
		}
		if ("0".equals(merchantInfo.getOpenPay())) {
			Double poundage = Double.valueOf(Double.parseDouble(pmsAppTransInfo.getPoundage()));
			poundage = Double.valueOf(Double.parseDouble(df.format(poundage)));
			String position = merchantInfo.getPosition();
			Double amount = Double.valueOf(Double.parseDouble(originalInfo.getOrderAmount()));
			this.logger.info("订单金额：" + amount);
			BigDecimal positions = new BigDecimal(position);
			
			BigDecimal sum_amount=new BigDecimal(0);
			

			Double dd = Double.valueOf(amount.doubleValue() * 100.0D - poundage.doubleValue());
			
			sum_amount=new BigDecimal(dd).multiply(new BigDecimal(0.8));
			Map<String, String> map = new HashMap();
			map.put("machId", originalInfo.getPid());
			map.put("payMoney", sum_amount.toString());
			int i = this.pmsMerchantInfoDao.updataPay(map);
			if (i != 1) {
				this.logger.info("实时填金失败！");

				pmsDaifuMerchantInfo.setResponsecode("01");
			} else {
				this.logger.info("实时成功！");
				pmsDaifuMerchantInfo.setResponsecode("00");
			}
			PmsMerchantInfo info = select(originalInfo.getPid());
			pmsDaifuMerchantInfo.setMercId(originalInfo.getPid());

			pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());

			pmsDaifuMerchantInfo.setAmount(Double.parseDouble(originalInfo.getOrderAmount()) + "");

			pmsDaifuMerchantInfo.setRemarks("D0");

			pmsDaifuMerchantInfo
					.setRecordDescription("订单号:" + originalInfo.getOrderId() + "交易金额:" + originalInfo.getOrderAmount());

			pmsDaifuMerchantInfo.setTransactionType(pmsAppTransInfo.getPaymenttype());

			pmsDaifuMerchantInfo.setPayamount(Double.parseDouble(originalInfo.getOrderAmount()) + "");

			pmsDaifuMerchantInfo.setPosition(info.getPosition());

			pmsDaifuMerchantInfo.setPayCounter(poundage.doubleValue() / 100 + "");
			pmsDaifuMerchantInfo.setOagentno("100333");
			int s = this.pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
			return i;
		}
		this.logger.info("此商户未开通代付！！");

		return 0;
	}

	private Map<String, String> setResp(String respCode, String respInfo) {
		Map<String, String> result = new HashMap();
		result.put("v_code", respCode);
		result.put("v_msg", respInfo);
		return result;
	}

	public Map<String, String> quickQuery(ConformityQuickPayQueryRequestEntity paramQueryRequestEntity) {
		// TODO Auto-generated method stub
		Map<String, String> result = new HashMap<>();
		OriginalOrderInfo origin = new OriginalOrderInfo();
		String orderid = paramQueryRequestEntity.getV_oid();
		logger.info("快捷查询订单号:" + orderid);
		origin = originalDao.getOriginalOrderInfoByOrderid(orderid);
		// 查询商户路由
		PmsBusinessPos pmsBusinessPos = selectKey(paramQueryRequestEntity.getV_mid());
		PmsAppTransInfo pmsAppTransInfo = null;
		try {
			if (origin != null) {
				pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(origin.getOrderId());
				if (pmsAppTransInfo != null) {
					logger.info("pmsAppTransInfo：" + JSON.toJSON(pmsAppTransInfo));

					result.put("v_mid", paramQueryRequestEntity.getV_mid());// 商户号
					result.put("v_oid", paramQueryRequestEntity.getV_oid());// 订单号
					result.put("v_userId", origin.getByUser());
					result.put("v_txnAmt", origin.getOrderAmount());// 金额
					result.put("v_attach", origin.getAttach());// 支付类型
					result.put("v_time", origin.getOrderTime());
					result.put("v_code", "00");
					result.put("v_msg", "请求成功");
					if ("0".equals(pmsAppTransInfo.getStatus())) {
						result.put("v_payStatus", "0000");// 支付状态
						result.put("v_status_msg", "支付成功");
					} else if (("1".equals(pmsAppTransInfo.getStatus()))) {
						result.put("v_payStatus", "1001");// 支付状态
						result.put("v_paymsg", "支付失败");
					} else {
						result.put("v_payStatus", "200");// 支付状态
						result.put("v_paymsg", "初始化");
					}

				} else {
					result.put("v_code", "15");
					result.put("v_msg", "请求失败");
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}

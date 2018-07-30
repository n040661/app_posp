package xdt.service.impl;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayBankInfoDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.BaseUtil;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PayBankInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.clearQuickPay.entity.ClearPayRequestEntity;
import xdt.quickpay.clearQuickPay.util.HttpClient;
import xdt.quickpay.clearQuickPay.util.HttpClientUtil;
import xdt.quickpay.clearQuickPay.util.MD5Util;
import xdt.quickpay.clearQuickPay.util.SignatureUtil;
import xdt.quickpay.clearQuickPay.util.UtilDate;
import xdt.service.IClearPayQuickService;
import xdt.service.IPublicTradeVerifyService;
import xdt.servlet.AppPospContext;
import xdt.util.HttpURLConection;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;

@Service("IClearPayQuickService")
public class ClearPayQuickServiceImpl extends BaseServiceImpl implements IClearPayQuickService {

	private Logger logger = Logger.getLogger(ClearPayQuickServiceImpl.class);
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
	@Resource
	private IPayBankInfoDao payBankInfoDao;

	@Override
	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {

		logger.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	@Override
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {

		OriginalOrderInfo original = null;

		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		logger.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	public Map<String, String> payHandle(ClearPayRequestEntity originalinfo) throws Exception {

		logger.info("快捷(直清)支付请求参数：" + JSON.toJSONString(originalinfo));
		Map<String, String> retMap = new HashMap();

		String merchId = originalinfo.getV_mid();

		String acount = originalinfo.getV_txnAmt();

		logger.info("******************根据商户号查询################");

		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getV_oid());
		orig.setPid(originalinfo.getV_mid());
		if (originalDao.selectByOriginal(orig) != null) {
			logger.info("下单重复");
			return setResp("03", "下单重复");
		}
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getV_oid());
		original.setOrderId(originalinfo.getV_oid());
		original.setPid(originalinfo.getV_mid());
		original.setOrderTime(originalinfo.getV_time());
		original.setOrderAmount(originalinfo.getV_txnAmt());
		original.setPageUrl(originalinfo.getV_url());
		original.setBgUrl(originalinfo.getV_notify_url());
		original.setAttach(originalinfo.getV_attach());
		if (originalinfo.getV_userId() != null) {
			original.setByUser(originalinfo.getV_userId());
		}

		originalDao.insert(original);

		String mercId = originalinfo.getV_mid();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		String oAgentNo = "";

		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if ((merchantList.size() != 0) && (!merchantList.isEmpty())) {
			merchantinfo = (PmsMerchantInfo) merchantList.get(0);

			oAgentNo = merchantinfo.getoAgentNo();
			if (StringUtils.isBlank(oAgentNo)) {
				logger.error("参数错误!");
				retMap.put("v_code", "04");
				retMap.put("v_msg", "参数错误,没有欧单编号");
				return retMap;
			}
			if ("60".equals(merchantinfo.getMercSts())) {
				logger.info("是正式商户");

				String factAmount = "" + new BigDecimal(originalinfo.getV_txnAmt()).multiply(new BigDecimal(100));

				PmsBusinessPos pmsBusinessPos = selectKey(originalinfo.getV_mid());

				ResultInfo payCheckResult = iPublicTradeVerifyService.amountVerifyOagent(
						(int) Double.parseDouble(factAmount), TradeTypeEnum.merchantCollect, oAgentNo);
				if (!payCheckResult.getErrCode().equals("0")) {
					logger.info(
							"欧单金额限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.moBaoQuickPay.getTypeCode());
					return setResp("05", "欧单金额限制，请重试或联系客服");
				}
				ResultInfo resultInfoForOAgentNo = iPublicTradeVerifyService
						.moduleVerifyOagent(TradeTypeEnum.merchantCollect, oAgentNo);
				if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
					if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
						logger.error("交易关闭，请重试或联系客服");
						return setResp("06", "交易关闭，请重试或联系客服");
					}
					return setResp("07", "系统异常，请重试或联系客服");
				}
				ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.merchantCollect,
						mercId);
				if (!payCheckResult3.getErrCode().equals("0")) {
					logger.info(
							"商户模块限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.moBaoQuickPay.getTypeCode());
					return setResp("08", "商户模块限制,请重试或联系客服");
				}
				Map<String, String> paramMap = new HashMap();
				paramMap.put("mercid", merchantinfo.getMercId());
				paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
				paramMap.put("oAgentNo", oAgentNo);

				Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);
				if ((resultMap == null) || (resultMap.size() == 0)) {
					logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
					return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
				}
				String maxTransMoney = (String) resultMap.get("MAX_AMOUNT");
				String minTransMoney = (String) resultMap.get("MIN_AMOUNT");
				String paymentAmount = factAmount;
				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
					logger.info("交易金额大于最打金额");
					return setResp("10", "金额超过最大交易金额");
				}
				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
					logger.info("交易金额小于最小金额");
					return setResp("11", "交易金额小于最小金额");
				}
				PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();

				pmsAppTransInfo.setoAgentNo(oAgentNo);
				pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());
				pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect.getTypeName());

				pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
				pmsAppTransInfo.setMercid(merchantinfo.getMercId());
				pmsAppTransInfo.setTradetypecode(TradeTypeEnum.merchantCollect.getTypeCode());

				pmsAppTransInfo.setOrderid(originalinfo.getV_oid());
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
				BigDecimal factBigDecimal = new BigDecimal(factAmount);
				BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);
				pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());
				pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());
				pmsAppTransInfo.setDrawMoneyType("1");
				pmsAppTransInfo.setSettlementState("D0");
				Integer insertAppTrans = Integer.valueOf(pmsAppTransInfoDao.insert(pmsAppTransInfo));
				if (insertAppTrans.intValue() == 1) {
					pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

					String quickRateType = ((String) resultMap.get("QUICKRATETYPE")).toString();

					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
					if (appRateConfig == null) {
						logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
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
							logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
						}
					}
					BigDecimal payAmount = new BigDecimal("0");
					BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());

					BigDecimal fee = new BigDecimal(0);
					String rateStr = "";
					Integer amount = null;
					Double settleFee=null;
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
						Double payfee = Double.parseDouble(merchantinfo.getCounter());

						Double userfee = Double.parseDouble(originalinfo.getV_userFee());

					    settleFee = Double.parseDouble(originalinfo.getV_settleUserFee()) * 100;
						amount = settleFee.intValue();

						Double dfpag = Double.parseDouble(merchantinfo.getPoundage());

						rateStr = rate;
						if (Double.parseDouble(rateStr) <= userfee) {
							//fee = new BigDecimal(userfee).multiply(dfactAmount).add(new BigDecimal(minPoundage));
							fee=new BigDecimal(dfactAmount.doubleValue()*userfee);
							if (!this.isNumeric(fee.toString())) {
								fee = new BigDecimal(fee.setScale(0, fee.ROUND_UP).intValue());
							}
						} else {
							logger.info("费率低于成本费率：" + merchantinfo.getMercId());
							return setResp("12", "费率低于成本费率");

						}
						if (dfpag != null) {
							if (dfpag > Double.parseDouble(originalinfo.getV_settleUserFee())) {
								logger.info("手续费低于最小手续费：" + merchantinfo.getMercId());
								return setResp("20", "手续费低于最小手续费");
							} else {
								fee.add(new BigDecimal(amount.toString()));
							}
						}
						payAmount = dfactAmount.subtract(fee).subtract(new BigDecimal(amount.toString()));
						logger.info("清算金额:" + paymentAmount);
						if (payAmount.doubleValue() < 0.0D) {
							payAmount = new BigDecimal(0.0D);
						}
					}
					pmsAppTransInfo.setPayamount(payAmount.toString());
					pmsAppTransInfo.setRate(rateStr);
					pmsAppTransInfo.setPoundage(fee.add(new BigDecimal(amount.toString())).toString());
					pmsAppTransInfo.setDrawMoneyType("1");

					Integer paymentAmountInt = Integer.valueOf((int) Double.parseDouble(paymentAmount));

					payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt.intValue(),
							TradeTypeEnum.merchantCollect, PaymentCodeEnum.moBaoQuickPay, oAgentNo,
							merchantinfo.getMercId());
					if (!payCheckResult.getErrCode().equals("0")) {
						logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
								+ PaymentCodeEnum.moBaoQuickPay.getTypeCode());
						return setResp("13", "暂不支持该交易方式");
					}
					// 查看当前交易是否已经生成了流水表
					PospTransInfo pospTransInfo = null;
					// 流水表是否需要更新的标记 0 insert，1：update
					int insertOrUpdateFlag = 0;
					// 生成上送流水号
					if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
						// 已经存在，修改流水号，设置pospsn为空
						logger.info("订单号：" + pmsAppTransInfo.getOrderid());
						pospTransInfo.setTransOrderId(originalinfo.getV_oid());
						pospTransInfo.setResponsecode("99");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
					} else {
						// 不存在流水，生成一个流水
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);

						System.out.println("流水表生成的时间:" + pospTransInfo.getSenddate());
						// 设置上送流水号
						pospTransInfo.setTransOrderId(originalinfo.getV_oid());
						insertOrUpdateFlag = 0;
					}
					// 插入流水表信息
					if (insertOrUpdateFlag == 0) {
						// 插入一条流水
						pospTransInfoDAO.insert(pospTransInfo);
					} else if (insertOrUpdateFlag == 1) {
						// 更新一条流水
						pospTransInfoDAO.updateByOrderId(pospTransInfo);
					}
					logger.info("修改订单信息");
					logger.info(pmsAppTransInfo);

					int num = pmsAppTransInfoDao.update(pmsAppTransInfo);
					if (num > 0) {
						logger.info("上游通道商户号:" + pmsBusinessPos.getChannelnum());
						PayBankInfo bank = new PayBankInfo();
						bank.setBank_pmsbankNo(originalinfo.getV_settlePmsBankNo());
						bank = payBankInfoDao.selectByBankInfo(bank);
						logger.info("查询结算商户银行信息:" + bank.getBank_name());
						switch (pmsBusinessPos.getChannelnum()) {
						case "YT":
							logger.info("################易通快捷(直清)支付开始处理#################");
							Map<String, String> map = new HashMap<String, String>();
							map.put("version", "1.0.0");
							map.put("transCode", "8888");
							map.put("merchantId", "888201711310120");
							map.put("merOrderNum", originalinfo.getV_oid());
							map.put("bussId", "ONL0017");
							amount = (int) (Double.parseDouble(originalinfo.getV_txnAmt()) * 100);
							map.put("tranAmt", amount.toString());
							map.put("sysTraceNum", originalinfo.getV_oid());
							map.put("tranDateTime", originalinfo.getV_time());
							map.put("currencyType", "156");
							map.put("merURL", BaseUtil.url + "/clearPay/clearPayNotifyUrl.action");
							map.put("backURL", BaseUtil.url + "/clearPay/clearPayNotifyUrl.action");
							map.put("orderInfo", "");
							map.put("userId", "");
							map.put("userNameHF", MD5Util.bytes2HexStr(originalinfo.getV_realName().getBytes("UTF-8")));
							map.put("quickPayCertNo", originalinfo.getV_cert_no());
							map.put("arrviedAcctNo", originalinfo.getV_settleCardNo());
							map.put("arrviedPhone", originalinfo.getV_settlePhone());
							map.put("arrviedBankName", bank.getBank_name());
							map.put("userPhoneHF", originalinfo.getV_phone());
							map.put("userAcctNo", originalinfo.getV_cardNo());
							map.put("cardCvn2", originalinfo.getV_cvn2());
							map.put("cardExpire", originalinfo.getV_expired());
							map.put("userIp", "");
							map.put("bankId", "888880170122900");
							map.put("stlmId", "");
							map.put("entryType", "1");
							map.put("attach", originalinfo.getV_attach());

							Integer fees = fee.add(new BigDecimal(settleFee.intValue())).intValue();

							map.put("reserver1", fees.toString());
							map.put("reserver2", "");
							map.put("reserver3", "");
							map.put("reserver4", "7");
							String datakey = "TLM3O9zGu69lP411";
							String txnString = map.get("version") + "|" + map.get("transCode") + "|"
									+ map.get("merchantId") + "|" + map.get("merOrderNum") + "|" + map.get("bussId")
									+ "|" + map.get("tranAmt") + "|" + map.get("sysTraceNum") + "|"
									+ map.get("tranDateTime") + "|" + map.get("currencyType") + "|" + map.get("merURL")
									+ "|" + map.get("backURL") + "|" + map.get("orderInfo") + "|" + map.get("userId");
							String signVal = xdt.quickpay.clearQuickPay.util.MD5.getInstance()
									.getMD5ofStr(txnString + datakey);
							logger.info("生成的签名" + signVal);
							map.put("signValue", signVal);
							logger.info("上送的数据:" + map);
							String encode = "utf-8";
							String url = "https://cashier.etonepay.com/NetPay/SynonymNamePay.action";
							// HttpClient client = new HttpClient(url, 20000, 20000);
							// String result=client.send(map,encode);
							String result = HttpClientUtil.post(url, map);
							logger.info("响应信息:" + result);
							Map<String, String> m = HttpClientUtil.transStringToMap(result, "&", "=");
							logger.info("响应信息:" + m);
							if ("0000".equals(m.get("respCode"))) {
								retMap.put("v_mid", originalinfo.getV_mid());
								retMap.put("v_txnAmt", originalinfo.getV_txnAmt());
								retMap.put("v_time", originalinfo.getV_time());
								retMap.put("v_oid", originalinfo.getV_oid());
								retMap.put("v_code", "00");
								retMap.put("v_msg", "请求成功");
							} else {
								retMap.put("v_code", "15");
								retMap.put("v_msg",
										"请求失败:" + new String(MD5Util.hexStr2Bytes(m.get("reserver3")), "UTF-8"));
								return retMap;
							}
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

	private Map<String, String> setResp(String respCode, String respInfo) {
		Map<String, String> result = new HashMap();
		result.put("v_code", respCode);
		result.put("v_msg", respInfo);
		return result;
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

}

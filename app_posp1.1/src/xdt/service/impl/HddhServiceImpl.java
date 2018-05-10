package xdt.service.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.etonepay.b2c.utils.MD5;
import com.kspay.AESUtil;
import com.kspay.MD5Util;
import com.yufusoft.payplatform.security.cipher.YufuCipher;
import com.yufusoft.payplatform.security.vo.ParamPacket;

import net.sf.json.JSONArray;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPayBankInfoDao;
import xdt.dao.IPayCmmtufitDao;
import xdt.dao.IPmsAddressDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsGoodsDao;
import xdt.dao.IPmsGoodsOrderDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPmsMessageDao;
import xdt.dao.IPmsOrderHelpDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.IQuickpayPreRecordDao;
import xdt.dao.IQuickpayRecordDao;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.BaseUtil;
import xdt.dto.code.SmsBalanceRequest;
import xdt.dto.code.SmsBalanceResponse;
import xdt.dto.code.SmsSendRequest;
import xdt.dto.code.SmsSendResponse;
import xdt.dto.code.SmsVariableRequest;
import xdt.dto.code.SmsVariableResponse;
import xdt.dto.hj.HJUtil;
import xdt.dto.hlb.Disguiser;
import xdt.dto.hlb.HLBUtil;
import xdt.dto.hlb.HttpClientService;
import xdt.dto.hlb.MyBeanUtils;
import xdt.dto.hm.AesEncryption;
import xdt.dto.hm.HMUtil;
import xdt.dto.hm.HttpsUtil;
import xdt.dto.hm.SHA256Util;
import xdt.dto.hm.TimeUtil;
import xdt.dto.mb.DemoBase;
import xdt.dto.mb.HttpService;
import xdt.dto.mb.MBUtil;
import xdt.dto.nbs.register.RegisterResponse;
import xdt.dto.yf.GsonUtil;
import xdt.dto.yf.PostUtils;
import xdt.dto.yf.QuickReq;
import xdt.dto.yf.YFUtil;
import xdt.dto.yf.YufuCipherSupport;
import xdt.dto.ys.HttpUtils;
import xdt.dto.ys.SwpHashUtil;
import xdt.dto.ys.YSUtil;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PayBankInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.gyy.util.ApiUtil;
import xdt.quickpay.hddh.entity.RegisterRequestEntity;
import xdt.quickpay.hddh.entity.ReplacePayRequestEntity;
import xdt.quickpay.hddh.util.Base64;
import xdt.quickpay.jbb.util.RSAEncrypt;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.quickpay.syys.HttpClientUtil;
import xdt.quickpay.syys.PayCore;
import xdt.service.HfQuickPayService;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IhddhService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.servlet.AppPospContext;
import xdt.util.ChuangLanSmsUtil;
import xdt.util.EncodeUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.utils.RequestUtils;

@Service
public class HddhServiceImpl extends BaseServiceImpl implements IhddhService {

	private Logger logger = Logger.getLogger(QuickpayServiceImpl.class);
	@Resource
	private IQuickpayRecordDao iQuickpayRecordDao;
	@Resource
	private IPayCmmtufitDao iPayCmmtufitDao;

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	private IPmsGoodsDao pmsGoodsDao;

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
	private MobaoPayHandel mobaoPayHandel;
	@Resource
	private IQuickpayRecordDao quickpayRecordDao;
	@Resource
	private IPospTransInfoDAO pospTransInfoDAO;
	@Resource
	private IMerchantCollectMoneyService merchantCollectMoneyService;
	@Resource
	private IPmsMessageService pmsMessageService;
	@Resource
	IPmsMessageDao pmsMessageDao;
	@Resource
	private IAppRateConfigDao appRateConfigDao;
	@Resource
	private IQuickpayPreRecordDao quickpayPreRecordDao;
	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;// 代付
	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;
	@Resource
	private IPayBankInfoDao payBankInfoDao;
	@Resource
	public PmsWeixinMerchartInfoService weixinService;
	@Resource
	private HfQuickPayService payService;

	@Override
	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {
		// TODO Auto-generated method stub
		logger.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	@Override
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
		// TODO Auto-generated method stub
		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		logger.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	@Override
	public Map<String, String> registerHandle(RegisterRequestEntity originalinfo) throws Exception {
		// TODO Auto-generated method stub
		logger.info("上海漪雷签约上送的参数" + originalinfo);
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getCooperatorUserId());// 原始数据的订单编号
		original.setOrderId(originalinfo.getCooperatorUserId()); // 为主键
		original.setPid(originalinfo.getMerid());
		original.setPageUrl(originalinfo.getCallBackUrl());
		originalDao.insert(original);
		Map<String, String> retMap = new HashMap<String, String>();
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(originalinfo.getMerid());
		// o单编号
		String oAgentNo = "";
		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();//

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				// 如果没有欧单编号，直接返回错误
				logger.error("参数错误!");
				retMap.put("16", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {
				String cooperatorUserId = originalinfo.getCooperatorUserId();
				String callBackUrl = BaseUtil.url+"/hddh/hddhNotifyUrl.action";
				String cooperator_order_id = originalinfo.getCooperatorOrderId();
				String cooperatorId = "b3b4f7f52060ab7fcc81d9f60382ee1e";
				Map<String, String> map = new HashMap<String, String>();
				map.put("cooperatorUserId", cooperatorUserId);
				map.put("joinType", "H5");
				map.put("callBackUrl", callBackUrl);
				map.put("cooperatorOrderId", cooperator_order_id);
				map.put("cooperatorId", cooperatorId);
				net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(map);
				logger.info("海德绑卡签名json数据:" + j.toString());

				xdt.quickpay.hddh.util.MD5 md = new xdt.quickpay.hddh.util.MD5();
				byte[] raw;
				try {
					raw = j.toString().getBytes("utf-8");
					String data = Base64.encode(raw, 0, raw.length);
					String sign = "data=" + data + "&key=bb946c036823d4372617c366e7939efd";
					logger.info("海德绑卡生成的签名前的数据:" + sign);
					String signMsg = md.md5(sign);
					logger.info("海德绑卡生成的签名:" + signMsg);

					String url = "http://api.kuaikuaifu.net/ypapi/repay/createBindCardOrder.do";

					String params = "sign=" + signMsg + "&cooperatorId=" + cooperatorId + "&data=" + data;

					String path = url + "?" + params;

					HttpURLConection http = new HttpURLConection();
					String results = http.httpURLConectionGET(path, "UTF-8");
					logger.info("海德绑卡响应结果" + results);
					Map<String, String> maps = new HashMap<String, String>();
					maps = ApiUtil.toMap(results);
					logger.info("海德解析map结果" + maps);
					String datas = new String(xdt.quickpay.jbb.util.Base64.decode(maps.get("data")),
							Charset.forName("UTF-8"));
					maps = ApiUtil.toMap(datas);
					retMap.put("html", maps.get("jumpUrl"));

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("17", "商户没有进行实名认证");
				return retMap;
			}
		} else {
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("17", "商户没有进行实名认证");
			return retMap;
		}
		return retMap;
	}

	public Map<String, String> replaceHandle(ReplacePayRequestEntity originalinfo) throws Exception {
		// TODO Auto-generated method stub

		Map<String, String> retMap = new HashMap<String, String>();
		// 商户号
		String merchId = originalinfo.getMerid();
		// 金额
		// String acount = originalinfo.ge;
		// 商户订单号
		logger.info("******************根据商户号查询");

		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getCooperator_repay_order_id());
		orig.setPid(originalinfo.getMerid());

		if (originalDao.selectByOriginal(orig) != null) {
			logger.info("下单重复");
			return setResp("03", "下单重复");
		}

		// String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
		// 业务号（2位）+业务细分（1位）+时间戳（13位）
		// 总共16位
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getCooperator_user_id());// 原始数据的订单编号
		original.setOrderId(originalinfo.getCooperator_user_id()); // 为主键
		original.setPid(originalinfo.getMerid());
		original.setPageUrl(originalinfo.getUnion_callback_url());
		originalDao.insert(original);
		// 根据商户号查询
		String mercId = originalinfo.getMerid();
		// 查询商户路由
		PmsBusinessPos pmsBusinessPos = selectKey(originalinfo.getMerid());
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

		// o单编号
		String oAgentNo = "";

		// 查询当前商户信息
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {
			merchantinfo = merchantList.get(0);
			// merchantinfo.setCustomertype("3");

			oAgentNo = merchantinfo.getoAgentNo();

			if (StringUtils.isBlank(oAgentNo)) {
				// 如果没有欧单编号，直接返回错误
				logger.error("参数错误!");
				retMap.put("v_code", "04");
				retMap.put("v_msg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {
				String repayItemList = originalinfo.getRepayItemList();
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				String[] array = repayItemList.split("\\$");

				for (int i = 0; i < array.length; i++) {
					Map<String, String> map = new LinkedHashMap<String, String>();
					String bb = array[i];
					String[] bb1 = bb.split("\\|");
					String[] name = { "trade_time", "transfer_time", "trade_amount", "transfer_amount", "fee",
							"cooperator_item_id" };
					for (int j = 0; j < bb1.length; j++) {

						map.put(name[j], bb1[j]);
					}
					// 实际金额
					String factAmount =new BigDecimal(map.get("trade_amount").toString())+"";

					// 校验欧单金额限制
					ResultInfo payCheckResult = iPublicTradeVerifyService.amountVerifyOagent(
							(int) Double.parseDouble(factAmount), TradeTypeEnum.onlinePay, oAgentNo);
					if (!payCheckResult.getErrCode().equals("0")) {
						// 交易不支持
						logger.info("欧单金额限制，oAagentNo:" + oAgentNo + ",payType:"
								+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
						return setResp("05", "欧单金额限制，请重试或联系客服");
					}

					// 校验欧单模块是否开启
					ResultInfo resultInfoForOAgentNo = iPublicTradeVerifyService
							.moduleVerifyOagent(TradeTypeEnum.onlinePay, oAgentNo);
					if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
						// 交易不支持
						if (StringUtils.isEmpty(resultInfoForOAgentNo.getMsg())) {
							logger.error("交易关闭，请重试或联系客服");
							return setResp("06", "交易关闭，请重试或联系客服");
						} else {
							return setResp("07", "系统异常，请重试或联系客服");
						}

					}
					// 校验商户模块是否开启
					ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.onlinePay,
							mercId);
					if (!payCheckResult3.getErrCode().equals("0")) {
						// 交易不支持
						logger.info("商户模块限制，oAagentNo:" + oAgentNo + ",payType:"
								+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
						return setResp("08", "商户模块限制,请重试或联系客服");
					}
					// 校验商户金额限制
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
					paramMap.put("businesscode", TradeTypeEnum.onlinePay.getTypeCode());// 业务编号
					paramMap.put("oAgentNo", oAgentNo);
					// 商户 网购 业务信息
					Map<String, String> resultMap = merchantMineDao.queryBusinessInfo(paramMap);

					if (resultMap == null || resultMap.size() == 0) {
						// 若查到的是空值，直接返回错误
						logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
						return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
					}

					String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
					String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
					String paymentAmount = factAmount;// 交易金额

					if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
						// 金额超过最大金额
						logger.info("交易金额大于最打金额");
						return setResp("10", "金额超过最大交易金额");
					} else if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
						// 金额小于最小金额
						logger.info("交易金额小于最小金额");
						return setResp("11", "交易金额小于最小金额");

					}

					// 组装订单数据
					PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
					// 写入欧单编号
					pmsAppTransInfo.setoAgentNo(oAgentNo);
					pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
					pmsAppTransInfo.setTradetype(TradeTypeEnum.onlinePay.getTypeName());// 业务功能模块名称
																						// ：网购
					pmsAppTransInfo.setTradetime(map.get("trade_time").toString()); // 设置时间
					pmsAppTransInfo.setMercid(merchantinfo.getMercId());
					pmsAppTransInfo.setTradetypecode(TradeTypeEnum.onlinePay.getTypeCode());// 业务功能模块编号
																							// ：17
					pmsAppTransInfo.setOrderid(map.get("cooperator_item_id").toString());// 设置订单号
					pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.hengFengQuickPay.getTypeName());
					pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.hengFengQuickPay.getTypeCode());
					BigDecimal factBigDecimal = new BigDecimal(factAmount);
					BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

					pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
					pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
					pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
					pmsAppTransInfo.setSettlementState("D0");
					// 插入订单信息
					Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
					if (insertAppTrans == 1) {

						logger.info("订单表的订单号:"+pmsAppTransInfo.getOrderid());
						// 查询订单信息
						pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());

						String quickRateType = resultMap.get("QUICKRATETYPE").toString();// 快捷支付费率类型

						// 获取o单第三方支付的费率
						AppRateConfig appRate = new AppRateConfig();
						appRate.setRateType(quickRateType);
						appRate.setoAgentNo(oAgentNo);
						AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

						if (appRateConfig == null) {
							// 若查到的是空值，直接返回错误
							// 若查到的是空值，直接返回错误
							logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
							return setResp("09", "没有查到相关费率配置,,请重试或联系客服");

						}

						String isTop = appRateConfig.getIsTop();
						String rate = appRateConfig.getRate();
						String topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
						paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
						String minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费
						Double minPoundage = 0.0; // 附加费

						if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
								&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
							if (StringUtils.isNotBlank(minPoundageStr)) {
								minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
							} else {
								// 若查到的是空值，直接返回错误
								logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
								return setResp("09", "没有查到相关费率配置,,请重试或联系客服");
							}
						}

						BigDecimal payAmount = new BigDecimal("0");
						BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
						// 费率
						BigDecimal fee = new BigDecimal(0);
						Double settleFee = 0.0;
						Double userfee = 0.0;
						String rateStr = "";
						// Double payfee = null;
						// 计算结算金额
						if ("1".equals(isTop)) {

							rateStr = rate + "-" + topPoundage;
							// 是封顶费率类型
							fee = new BigDecimal(rate).multiply(dfactAmount);

							if (fee.compareTo(new BigDecimal(topPoundage)) == 1) {
								// 手续费大于封顶金额，按封顶金额处理
								payAmount = dfactAmount
										.subtract(new BigDecimal(topPoundage).subtract(new BigDecimal(minPoundage)));
								fee = new BigDecimal(topPoundage).add(new BigDecimal(minPoundage));
							} else {
								// 按当前费率处理
								rateStr = rate;
								fee.add(new BigDecimal(minPoundage));
								payAmount = dfactAmount.subtract(fee);
							}

						} else {
							// 按当前费率处理
							double dfpag = Double.parseDouble(merchantinfo.getPoundage());
							double daifu = Double.parseDouble(merchantinfo.getCounter());
							if (!"".equals(originalinfo.getRate()) && originalinfo.getRate() != null) {
								userfee = Double.parseDouble(originalinfo.getRate());
							}

							// 按当前费率处理
							rateStr = rate;
							if (Double.parseDouble(rateStr) <= userfee / 100) {
								BigDecimal num = dfactAmount.multiply(new BigDecimal(userfee));
								if (num.doubleValue() / 100 >= daifu) {
									if (num.doubleValue() > Double.parseDouble(map.get("fee"))) {
										fee = num;
									} else {
										fee = new BigDecimal(map.get("fee"));
									}

								} else {
									logger.info("手续费低于成本手续费：" + merchantinfo.getMercId());
									return setResp("12", "手续费低于成本手续费");
								}
								rateStr = userfee.toString();
								payAmount = dfactAmount;
								fee=fee.divide(new BigDecimal(100));
								logger.info("清算金额:" + paymentAmount);
								if (payAmount.doubleValue() < 0) {
									payAmount = new BigDecimal(0.00);
								}

							} else {
								logger.info("费率低于成本费率：" + merchantinfo.getMercId());
								return setResp("12", "费率低于成本费率");
							}
						}

						// 设置结算金额
						pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
						pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
						pmsAppTransInfo.setPoundage(fee.toString());
						pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
						// 转换double为int
						Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

						// 验证支付方式是否开启
						payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt,
								TradeTypeEnum.onlinePay, PaymentCodeEnum.hengFengQuickPay, oAgentNo,
								merchantinfo.getMercId());
						if (!payCheckResult.getErrCode().equals("0")) {
							// 交易不支持

							logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
									+ PaymentCodeEnum.hengFengQuickPay.getTypeCode());
							return setResp("13", "暂不支持该交易方式");
						}
						ViewKyChannelInfo channelInfo = AppPospContext.context.get(HENGFENGPAY + HENGFENGCHANNELNUM);

						// 设置通道信息
						pmsAppTransInfo.setBusinessNum(channelInfo.getBusinessnum());
						pmsAppTransInfo.setChannelNum(HENGFENGCHANNELNUM);

						// 查看当前交易是否已经生成了流水表
						PospTransInfo pospTransInfo = null;
						// 流水表是否需要更新的标记 0 insert，1：update
						int insertOrUpdateFlag = 0;
						// 生成上送流水号
						String transOrderId = generateTransOrderId(TradeTypeEnum.onlinePay,
								PaymentCodeEnum.hengFengQuickPay);
						if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
							// 已经存在，修改流水号，设置pospsn为空
							logger.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
							pospTransInfo.setTransOrderId(map.get("cooperator_item_id").toString());
							pospTransInfo.setResponsecode("99");
							pospTransInfo.setPospsn("");
							insertOrUpdateFlag = 1;
						} else {
							// 不存在流水，生成一个流水
							pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
							// 设置上送流水号
							pospTransInfo.setTransOrderId(map.get("cooperator_item_id").toString());
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
							PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
							model.setMercId(originalinfo.getMerid());
							model.setAmount(map.get("transfer_amount").toString());
							model.setBatchNo(map.get("cooperator_item_id").toString());
							model.setIdentity(map.get("cooperator_item_id").toString());
							if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {

								retMap.put("12", "代付重复");
								logger.info("代付重复");
								return retMap;
							}
							PmsDaifuMerchantInfo models = new PmsDaifuMerchantInfo();
							models.setMercId(originalinfo.getMerid());
							models.setCount("1");
							models.setAmount(Double.parseDouble(map.get("transfer_amount").toString())/100+"");
							models.setBatchNo(map.get("cooperator_item_id").toString());
							models.setCardno(originalinfo.getBank_card_id());
							models.setProvince(originalinfo.getProvince_name());
							models.setCity(originalinfo.getCity_name());
							models.setPayamount("-" + Double.parseDouble(map.get("transfer_amount").toString())/100);
							models.setOagentno("100333");
							models.setPayCounter("0");// 手续费
							models.setIdentity(map.get("cooperator_item_id").toString());
							models.setRecordDescription(merchantinfo.getPoundage());
							models.setRemarks("T1");
							models.setTransactionType("代还");
							models.setResponsecode("200");
							models.setCreationdate(map.get("transfer_time").toString());
							int number=pmsDaifuMerchantInfoDao.insertSelectives(models);
							if(number>0){
								list.add(map);
							}			
						}
					}

				}
				logger.info("上游通道商户号:" + pmsBusinessPos.getChannelnum());
				// 商户号码
				String merId = pmsBusinessPos.getBusinessnum();// 818310048160000
				// 商户号私钥
				String merKey = pmsBusinessPos.getKek();
				switch (pmsBusinessPos.getBusinessnum()) {

				case "12345678":// 上海漪雷代还
					logger.info("#################上海漪雷代还######################");
					String cooperator_repay_order_id = originalinfo.getCooperator_repay_order_id();
					String bank_card_id = originalinfo.getBank_card_id();
					String cooperator_user_id = originalinfo.getCooperator_user_id();
					String longitude = originalinfo.getLatitude();
					String latitude = originalinfo.getLatitude();
					String rate = originalinfo.getRate();
					String cost = "0";
					String channel_type = "1";
					String province_name = originalinfo.getProvince_name();
					String city_name = originalinfo.getCity_name();
					String device_id = originalinfo.getDevice_id();
					String union_callback_url = BaseUtil.url+"/hddh/hddhReturnUrl.action?cooperator_user_id="
							+ cooperator_user_id;

					JSONArray jsons = JSONArray.fromObject(list);

					repayItemList = jsons.toString().replaceAll("\\[", "").replaceAll("\\]", "").toString();
					logger.info("上海漪雷代还计划明细列表:" + repayItemList);
					String cooperatorId = "b3b4f7f52060ab7fcc81d9f60382ee1e";

					Map<String, String> map = new HashMap<String, String>();
					map.put("cooperator_repay_order_id", cooperator_repay_order_id);
					map.put("bank_card_id", bank_card_id);
					map.put("cooperator_user_id", cooperator_user_id);
					map.put("longitude", longitude);
					map.put("latitude", latitude);
					map.put("rate", rate);
					map.put("cost", cost);
					map.put("channel_type", channel_type);
					map.put("province_name", province_name);
					map.put("city_name", city_name);
					map.put("device_id", device_id);
					map.put("union_callback_url", union_callback_url);
					map.put("repayItemList", jsons.toString());
					map.put("cooperatorId", cooperatorId);

					String str = "{\"bank_card_id\":\"" + bank_card_id + "\",\"cost\":\"" + cost + "\",\"device_id\":\""
							+ device_id + "\",\"latitude\":\"" + latitude + "\",\"repayItemList\"" + ":" + ""
							+ jsons.toString() + ",\"province_name\":\"" + province_name + "\",\"city_name\":\""
							+ city_name + "\",\"cooperatorId\":\"" + cooperatorId + "\",\"cooperator_user_id\":\""
							+ cooperator_user_id + "\",\"rate\":\"" + rate + "\",\"union_callback_url\":\""
							+ union_callback_url + "\",\"cooperator_repay_order_id\":\"" + cooperator_repay_order_id
							+ "\",\"channel_type\":\"" + channel_type + "\",\"longitude\":\"" + longitude + "\"}";
					net.sf.json.JSONObject j = net.sf.json.JSONObject.fromObject(map);
					logger.info("海德绑卡签名json数据:" + j.toString());
					logger.info("海德绑卡签名:" + str);
					xdt.quickpay.hddh.util.MD5 md = new xdt.quickpay.hddh.util.MD5();
					byte[] raw;
					raw = str.getBytes("utf-8");
					String data = Base64.encode(raw, 0, raw.length);
					String sign = "data=" + data + "&key=bb946c036823d4372617c366e7939efd";
					logger.info("海德绑卡生成的签名前的数据:" + sign);
					String signMsg = md.md5(sign);
					logger.info("海德绑卡生成的签名:" + signMsg);

					String url = "http://api.kuaikuaifu.net/ypapi/repay/createRepayPlan.do";

					String params = "sign=" + signMsg + "&cooperatorId=" + cooperatorId + "&data=" + data;

					String path = url + "?" + params;

					HttpURLConection http = new HttpURLConection();
					String results = http.httpURLConectionGET(path, "UTF-8");
					logger.info("海德绑卡响应结果" + results);
					retMap = ApiUtil.toMap(results);
					logger.info("海德解析map结果" + retMap);
					String datas = new String(xdt.quickpay.jbb.util.Base64.decode(retMap.get("data")),
							Charset.forName("UTF-8"));
					retMap = ApiUtil.toMap(datas);
					logger.info("海德解析data结果" + retMap);
					break;
				default:
					break;

				}
			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("v_code", "16");
				retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服");
				return retMap;
			}
		} else

		{
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("v_code", "16");
			retMap.put("v_msg", "商户没有进行实名认证,请重试或联系客服\"");
			return retMap;
		}
		return retMap;

	}
	@Override
	public void otherInvoke(String orderId,String status) throws Exception {
		// TODO Auto-generated method stub
		logger.info("海德代还返回的订单号" + orderId);
		logger.info("海德代还返回的状态码" + status);
		// 流水表transOrderId
		//String transOrderId = result.getOrderId();
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(orderId);
		logger.info("流水表信息" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		logger.info("订单表信息" + pmsAppTransInfo);
		// 查询结果成功
		if ("SUCCESS".equals(status)) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(status);
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// log.info("修改余额");
				// 修改余额
				logger.info(pmsAppTransInfo);
				// updateMerchantBanlance(pmsAppTransInfo);
				// 更新流水表
				pospTransInfo.setResponsecode("00");
				pospTransInfo.setPospsn(orderId);
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else if ("FAIL".equals(status)) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(status);
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(orderId);
				logger.info("更新流水");
				logger.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
	}
	@Override
	public int UpdateDaifu(String batchNo, String responsecode) throws Exception {
		if (batchNo == null || batchNo == "") {
			return 0;
		}
		logger.info("原始数据:" + batchNo);

		PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

		logger.info("上送的批次号:" + batchNo);

		pdf.setBatchNo(batchNo);
		pdf.setResponsecode(responsecode);
		return pmsDaifuMerchantInfoDao.update(pdf);
	}

	/**
	 * 
	 * @Description 设置响应信息
	 * @author Administrator
	 * @param respCode
	 * @param respInfo
	 * @return
	 */
	private Map<String, String> setResp(String respCode, String respInfo) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("v_code", respCode);
		result.put("v_msg", respInfo);
		return result;
	}

}

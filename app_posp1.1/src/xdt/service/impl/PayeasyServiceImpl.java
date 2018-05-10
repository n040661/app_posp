package xdt.service.impl;
/**
 * @ClassName: PayeasyServiceImpl
 * @Description: 首信易快捷支付
 * @author YanChao.Shang
 * @date 2017年4月1日 上午10:51:28
 * 
 */

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.capinfo.crypt.Md5;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import xdt.common.RetAppMessage;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.dao.OriginalOrderInfoDao;
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.dto.payeasy.PayEasyQueryRequestEntity;
import xdt.dto.payeasy.PayEasyQueryResponseEntity;
import xdt.dto.payeasy.PayEasyRequestEntity;
import xdt.dto.payeasy.PayEasyResponseEntity;
import xdt.dto.payeasy.PayEasyResponseEntitys;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.service.IPayeasyService;
import xdt.service.IPublicTradeVerifyService;
import xdt.servlet.AppPospContext;
import xdt.util.HttpUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

@Component
public class PayeasyServiceImpl extends BaseServiceImpl implements IPayeasyService {
	/**
	 * 记录日志
	 */
	private Logger log = Logger.getLogger(PayeasyServiceImpl.class);

	private Logger logger = Logger.getLogger(PayeasyServiceImpl.class);

	// 代付接口
	private static final String daifu_URL = "http://pay.yizhifubj.com/merchant/virement/mer_payment_submit_utf8.jsp";

	// 代付接口
	private static final String orderquery_URL = "http://pay.yizhifubj.com/merchant/virement/mer_payment_status_utf8.jsp";
	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;

	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private IMerchantMineDao merchantMineDao;

	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;

	@Resource
	IPospTransInfoDAO pospTransInfoDAO;

	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;

	@Resource
	private IAppRateConfigDao appRateConfigDao;

	@Resource
	private IPublicTradeVerifyService iPublicTradeVerifyService;

	public String payHandle(PayEasyRequestEntity originalinfo) throws Exception {

		String message = "0:initialize";

		String jsonString = null;

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(originalinfo.getMerchantId());

		String merchantKey = keyinfo.getMerchantkey();

		HFSignUtil signUtil = new HFSignUtil();
		if (!signUtil.verify(PreSginUtil.payResultString(originalinfo), originalinfo.getV_md5info(), merchantKey)) {
			responseDTO.setRetCode(11);
			responseDTO.setRetMessage("签名错误");
			jsonString = createJsonString(responseDTO);
			log.info("签名错误");
			return jsonString;
		}
		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getV_oid());
		orig.setPid(originalinfo.getMerchantId());

		if (originalDao.selectByOriginal(orig) != null) {
			responseDTO.setRetCode(12);
			responseDTO.setRetMessage("下单重复");
			jsonString = createJsonString(responseDTO);
			log.info("下单重复");
			return jsonString;
		}

		String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
															// 业务号（2位）+业务细分（1位）+时间戳（13位）
															// 总共16位
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getV_oid());// 原始数据的订单编号
		original.setOrderId(originalinfo.getV_oid()); // 为主键
		original.setPid(originalinfo.getMerchantId());
		original.setOrderTime(originalinfo.getV_ymd());
		original.setOrderAmount(originalinfo.getV_amount());
		original.setPayType(originalinfo.getV_moneytype());
		original.setPageUrl(originalinfo.getV_url());
		original.setBgUrl(originalinfo.getV_bgurl());
		originalDao.insert(original);

		// 根据商户号查询
		String mercId = originalinfo.getMerchantId();

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);

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
				responseDTO.setRetCode(1);
				responseDTO.setRetMessage("参数错误");
				jsonString = createJsonString(responseDTO);
				log.info("参数错误,没有欧单编号");
				return jsonString;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {
				// 实际金额
				String factAmount = "" + new BigDecimal(originalinfo.getV_amount()).multiply(new BigDecimal(100));
				// 校验欧单金额限制
				ResultInfo payCheckResult = iPublicTradeVerifyService
						.amountVerifyOagent((int) Double.parseDouble(factAmount), TradeTypeEnum.onlinePay, oAgentNo);
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
							+ PaymentCodeEnum.GatewayCodePay.getTypeCode());
					return jsonString;
				}

				// 校验欧单模块是否开启
				ResultInfo payCheckResult1 = iPublicTradeVerifyService.moduleVerifyOagent(TradeTypeEnum.onlinePay,
						oAgentNo);
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
							+ PaymentCodeEnum.GatewayCodePay.getTypeCode());
					return jsonString;
				}
				// 校验商户模块是否开启
				ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.onlinePay, mercId);
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
							+ PaymentCodeEnum.GatewayCodePay.getTypeCode());
					return jsonString;
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

				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
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
				} else if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
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

				// 组装订单数据
				PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();
				// 写入欧单编号
				pmsAppTransInfo.setoAgentNo(oAgentNo);
				pmsAppTransInfo.setStatus(OrderStatusEnum.initlize.getStatus());// 订单初始化状态
				pmsAppTransInfo.setTradetype(TradeTypeEnum.onlinePay.getTypeName());// 业务功能模块名称
																					// ：网购
				pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter()); // 设置时间
				pmsAppTransInfo.setMercid(merchantinfo.getMercId());
				pmsAppTransInfo.setTradetypecode(TradeTypeEnum.onlinePay.getTypeCode());// 业务功能模块编号
																						// ：17
				pmsAppTransInfo.setOrderid(orderNumber);// 设置订单号
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.GatewayCodePay.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.GatewayCodePay.getTypeCode());
				BigDecimal factBigDecimal = new BigDecimal(factAmount);
				BigDecimal orderAmountBigDecimal = new BigDecimal(factAmount);

				pmsAppTransInfo.setFactamount(factBigDecimal.stripTrailingZeros().toPlainString());// 实际金额
				pmsAppTransInfo.setOrderamount(orderAmountBigDecimal.stripTrailingZeros().toPlainString());// 订单金额
				pmsAppTransInfo.setDrawMoneyType("1");// 普通提款

				// 插入订单信息
				Integer insertAppTrans = pmsAppTransInfoDao.insert(pmsAppTransInfo);
				if (insertAppTrans == 1) {

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
							responseDTO.setRetCode(1);
							responseDTO.setRetMessage("没有查到相关费率配置（附加费），请联系客服人员");
							logger.info("没有查到相关费率附加费（最低手续费）：" + merchantinfo.getMobilephone());
							try {
								jsonString = createJsonString(responseDTO);
							} catch (Exception em) {
								em.printStackTrace();
							}
							return jsonString;
						}
					}

					BigDecimal payAmount = null;
					BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
					// 费率
					BigDecimal fee = new BigDecimal(0);
					String rateStr = "";
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
						rateStr = rate;
						fee = new BigDecimal(rate).multiply(dfactAmount).add(new BigDecimal(minPoundage));
						payAmount = dfactAmount.subtract(fee);
					}
					// 设置结算金额
					pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
					pmsAppTransInfo.setRate(rateStr);// 0.50_35 || 0.50
					pmsAppTransInfo.setPoundage(fee.toString());
					pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
					// 转换double为int
					Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

					// 验证支付方式是否开启
					payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.onlinePay,
							PaymentCodeEnum.GatewayCodePay, oAgentNo, merchantinfo.getMercId());
					if (!payCheckResult.getErrCode().equals("0")) {
						// 交易不支持
						responseDTO.setRetCode(1);
						responseDTO.setRetMessage(payCheckResult.getMsg());
						try {
							jsonString = createJsonString(responseDTO);
						} catch (Exception em) {
							em.printStackTrace();
						}
						logger.info("不支持的支付方式，oAagentNo:" + oAgentNo + ",payType:"
								+ PaymentCodeEnum.GatewayCodePay.getTypeCode());
						return jsonString;
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
					String transOrderId = generateTransOrderId(TradeTypeEnum.onlinePay, PaymentCodeEnum.GatewayCodePay);
					if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
						// 已经存在，修改流水号，设置pospsn为空
						logger.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
						pospTransInfo.setTransOrderId(originalinfo.getV_oid());
						pospTransInfo.setResponsecode("99");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
					} else {
						// 不存在流水，生成一个流水
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
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
					log.info("修改订单信息");
					log.info(pmsAppTransInfo);
					pmsAppTransInfoDao.update(pmsAppTransInfo);
				}
			} else {
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

		// 解析要返回的信息
		int retCode = Integer.parseInt(message.split(":")[0]);
		String retMessage = message.split(":")[1];
		if (retMessage.equals("initialize")) {
			retMessage = "系统初始化";
		} else if (retMessage.equals("merchantDoesNotExist")) {
			retMessage = "商户信息不存在";
		}
		responseDTO.setRetCode(retCode);
		responseDTO.setRetMessage(retMessage);
		jsonString = createJsonString(responseDTO);
		return jsonString;

	}

	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {

		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		log.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
	}

	public OriginalOrderInfo selectByOriginal(OriginalOrderInfo queryWhere) {

		log.info("查询上送原始信息   下游订单id  商户号联合查询");
		return originalDao.selectByOriginal(queryWhere);
	}

	public PospTransInfo getTransInfo(String orderId) {

		PospTransInfo transinfo = pospTransInfoDAO.searchByOrderId(orderId);
		return transinfo;
	}

	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {

		log.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	public synchronized PayEasyQueryResponseEntity queryPayResult(PayEasyQueryRequestEntity queryRequest)
			throws Exception {

		log.info("查询支付结果");

		log.info("查询信息:{}" + queryRequest);

		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(queryRequest.getMerchantId());
		String v_mid = busInfo.getBusinessnum();
		String[] array = queryRequest.getV_oid().split("-");

		StringBuffer a = new StringBuffer();

		a.append(array[0]);
		a.append("-");
		a.append(array[1].replace(array[1], v_mid));
		a.append("-");
		a.append(array[2]);

		log.info("上送的订单号:" + a.toString());
		String v_oid = a.toString();

		// 查询字符串
		String queryString = "";

		// 请求地址
		String url = "http://api.yizhifubj.com/merchant/order/order_ack_oid_list.jsp";

		Md5 md5 = new Md5("");
		String str = queryRequest.getV_mid() + v_oid;
		log.info("拼接后的字符串:" + str);
		md5.hmac_Md5(str, busInfo.getKek());
		byte b[] = md5.getDigest();
		String digestString = md5.stringify(b);
		log.info("加密后的字符串:" + digestString);

		// 设置上送参数
		queryString = "v_mid=" + queryRequest.getV_mid() + "&v_oid=" + v_oid + "&v_mac=" + digestString;
		// 设置转发页面
		String path = url + "?" + queryString;
		log.info("重定向 第三方：" + path);
		// response.sendRedirect(path.replace(" ", ""));
		HttpUtil hf = new HttpUtil();
		String xml = hf.sendPosts(path);
		log.info("上游返回的xml数据" + xml);
		PayEasyQueryResponseEntity response = new PayEasyQueryResponseEntity();
		// 解析上游返回的xml文件
		StringReader read = new StringReader(xml);
		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
		InputSource source = new InputSource(read);
		// 创建一个新的SAXBuilder
		SAXBuilder sb = new SAXBuilder();
		// 通过输入源构造一个Document
		Document doc = sb.build(source);
		// 取的根元素
		Element root = doc.getRootElement();
		log.info("根元素名称:" + root.getName());
		// 得到根元素所有子元素的集合
		List jiedian = root.getChildren();
		log.info("根元素下的子元素:" + jiedian);
		// 获得XML中的命名空间（XML中未定义可不写）
		Namespace ns = root.getNamespace();
		Element et = null; // 定义messagehead下的内容
		Element et1 = null;// 定义messagebody下的内容
		// 获取messagehead下的内容
		et = (Element) jiedian.get(0);
		log.info("状态" + et.getChild("status").getText());
		response.setV_status(et.getChild("status").getText());
		log.info("状态描述" + et.getChild("statusdesc").getText());
		response.setV_desc(et.getChild("statusdesc").getText());
		log.info("商户编号" + et.getChild("mid").getText());
		response.setV_mid(et.getChild("mid").getText());
		log.info("订单号" + et.getChild("oid").getText());
		response.setV_oid(et.getChild("oid").getText());
		// 获取messagebody下的内容
		et1 = (Element) jiedian.get(1);
		log.info("body内容:" + et1);
		List zjiedian = et1.getChildren();
		log.info("list集合:" + zjiedian);
		Namespace nss = et1.getNamespace();
		for (int j = 0; j < zjiedian.size(); j++) {
			Element xet = (Element) zjiedian.get(j);
			log.info("支付方式" + xet.getChild("pmode").getText());
			response.setV_pmode(xet.getChild("pmode", nss).getText());
			log.info("支付状态" + xet.getChild("pstatus").getText());
			response.setV_pstatus(xet.getChild("pstatus", nss).getText());
			log.info("支付结果" + xet.getChild("pstring").getText());
			response.setV_pstring(xet.getChild("pstring", nss).getText());
			log.info("金额" + xet.getChild("amount").getText());
			response.setV_amount(xet.getChild("amount", nss).getText());
			log.info("币种" + xet.getChild("moneytype").getText());
			response.setV_moneytype(xet.getChild("moneytype", nss).getText());
			log.info("是否已转账" + xet.getChild("isvirement").getText());
			response.setV_isvirement(xet.getChild("isvirement", nss).getText());
			log.info("签名" + xet.getChild("sign", nss).getText());
			response.setV_sign(xet.getChild("sign", nss).getText());

		}
		log.info("数据出来了！！！");
		// 解析后的数据
		log.info("解析xml后的数据:{}" + response);

		return response;

	}

	public synchronized void otherInvoke(PayEasyResponseEntitys result) throws Exception {

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
		if ("1".equals(result.getV_pstatus().toString())) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getV_pstatus().toString());
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
		} else if ("3".equals(result.getV_pstatus().toString())) {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getV_pstatus().toString());
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

	public synchronized Map<String, String> InsertDaifu(List<DaifuRequestEntity> array) throws Exception {

		String message = "0:initialize";

		String jsonString = null;

		Map<String, String> result = new HashMap<String, String>();

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		DaifuRequestEntity df = new DaifuRequestEntity();
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();

		for (DaifuRequestEntity daifu : array) {

			df.setMerchantId(daifu.getMerchantId());
			df.setV_mac(daifu.getV_mac());
			df.setV_data(daifu.getV_data());
			break;
		}
		String merid = df.getMerchantId();

		log.info("下游上送的商户号:" + merid);

		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(merid);

		String merchantKey = keyinfo.getMerchantkey();

		HFSignUtil signUtil = new HFSignUtil();
		if (!signUtil.verify(PreSginUtil.paydaifuResultString(df), df.getV_mac(), merchantKey)) {

			log.info("签名错误");
			result.put("11", "签名错误");

			return result;

		}
		for (DaifuRequestEntity dfr : array) {

			model.setMercId(dfr.getMerchantId());
			model.setAmount(dfr.getV_sum_amount());
			model.setBatchNo(dfr.getV_batch_no());
			model.setIdentity(dfr.getV_identity());
			if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {

				result.put("12", "代付重复");
				log.info("代付重复");
				return result;
			}
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(dfr.getMerchantId());
			// o单编号
			String oAgentNo = "";
			Double surAmount = null;
			BigDecimal b3 = new BigDecimal(0.00);
			BigDecimal volumn = new BigDecimal("0");

			// 查询当前商户信息
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				merchantinfo = merchantList.get(0);
				// merchantinfo.setCustomertype("3");

				oAgentNo = merchantinfo.getoAgentNo();//

				if (StringUtils.isBlank(oAgentNo)) {
					// 如果没有欧单编号，直接返回错误
					result.put("1", "参数错误");
					log.info("参数错误,没有欧单编号");
					return result;
				}
				// 判断是否为正式商户
				if ("60".equals(merchantinfo.getMercSts())) {

					// 判断此商户是否开启代付
					if ("0".equals(merchantinfo.getOpenPay())) {
						// 实际金额
						Double factAmount = Double.parseDouble(model.getAmount());
						BigDecimal b1 = new BigDecimal(factAmount.toString());
						BigDecimal b2 = new BigDecimal(merchantinfo.getPositionT1().toString());
						b3 = new BigDecimal(merchantinfo.getPoundage());
						log.info("代付金额:" + b1.multiply(new BigDecimal(100)).doubleValue());
						log.info("可用额度:" + b2.doubleValue());
						log.info("每笔代付手续费:" + b3);
						volumn = new BigDecimal(array.size());
						log.info("总笔数:" + volumn.toString());
						double fee = volumn.multiply(b3).doubleValue() * 100;
						log.info("代付总手续费:" + fee);
						// 清算金额
						Double payAmount = factAmount * 100 + fee;
						BigDecimal b4 = new BigDecimal(payAmount.toString());
						log.info("清算金额:" + b4);
						if (payAmount <= b2.doubleValue()) {

							BigDecimal min = new BigDecimal(merchantinfo.getMinDaiFu());
							BigDecimal max = new BigDecimal(merchantinfo.getMaxDaiFu());
							if (min.compareTo(b1) == 1) {
								log.info("代付金额小于最小代付金额");
								result.put("005", "代付金额小于最小代付金额！");
								return result;
							} else if (max.compareTo(b1) == -1) {
								log.info("代付金额大于最大代付金额");
								result.put("005", "代付金额大于最大代付金额！");
								return result;
							}
							log.info("上送的数据:" + df.getV_data());
							// 查询上游商户号
							PmsBusinessPos busInfo = selectKey(df.getMerchantId());
							String v_mid = busInfo.getBusinessnum();
							logger.info("上游商户号:" + v_mid);
							// String v_mid = "13240";
							String v_data = df.getV_data();
							String v_data1 = URLEncoder.encode(v_data, "UTF-8");
							String v_version = "1.0";
							Md5 md5 = new Md5("");
							System.out.println("转码之后的数据:" + URLEncoder.encode(v_data, "utf-8"));
							logger.info("上游秘钥:" + busInfo.getKek());
							md5.hmac_Md5(v_mid + v_data1, busInfo.getKek());
							byte[] b = md5.getDigest();
							String v_mac = Md5.stringify(b);
							String queryString = "v_mid=" + v_mid + "&v_data=" + v_data + "&v_mac=" + v_mac
									+ "&v_version=" + v_version;
							this.log.info("上送的参数:" + queryString);
							String path = daifu_URL + "?" + queryString;
							this.log.info("重定向 第三方：" + path);
							Client cc = Client.create();
							WebResource rr = cc.resource(daifu_URL);
							MultivaluedMap queryParams = new MultivaluedMapImpl();
							queryParams.add("v_mid", v_mid); // 商户编号
							queryParams.add("v_data", java.net.URLEncoder.encode(v_data, "GBK"));
							queryParams.add("v_version", v_version);
							queryParams.add("v_mac", v_mac);
							log.info("向上游发送的数据:" + queryParams);
							String xml = rr.queryParams(queryParams).get(String.class);
							// String xml = HttpUtil.sendPost(path);
							// String
							// xml=HttpUtil.sendPost(daifu_URL,result);
							this.log.info("返回的xml数据:" + xml);

							StringReader read = new StringReader(xml);

							InputSource source = new InputSource(read);

							SAXBuilder sb = new SAXBuilder();

							Document doc = sb.build(source);

							Element root = doc.getRootElement();
							this.log.info("根元素名称:" + root.getName());
							this.log.info("状态值" + root.getChild("status").getText());
							String status = root.getChild("status").getText();
							log.info("代付状态:" + status);
							if ("0".equals(status)) {
								String v_mid1 = busInfo.getBusinessnum();
								String v_data0 = dfr.getV_identity();
								Md5 md51 = new Md5("");
								log.info("转码之后的数据:" + URLEncoder.encode(v_data0, "utf-8"));
								md51.hmac_Md5(v_mid + URLEncoder.encode(v_data0, "utf-8"), busInfo.getKek());
								byte[] b0 = md51.getDigest();
								String v_mac1 = Md5.stringify(b0);
								String queryString1 = "v_mid=" + v_mid1 + "&v_data=" + v_data0 + "&v_mac=" + v_mac1;
								this.log.info("上送的参数:" + queryString1);
								String path1 = orderquery_URL + "?" + queryString1;
								this.log.info("重定向 第三方：" + path1);
								Client cc1 = Client.create();
								WebResource rr1 = cc1.resource(orderquery_URL);
								MultivaluedMap queryParams1 = new MultivaluedMapImpl();
								queryParams1.add("v_mid", v_mid1); // 商户编号
								queryParams1.add("v_data", java.net.URLEncoder.encode(v_data0, "GBK"));
								queryParams1.add("v_mac", v_mac1);
								log.info("向上游发送的数据:" + queryParams1);
								String xml1 = rr1.queryParams(queryParams1).get(String.class);

								this.log.info("返回的xml数据:" + xml1);

								StringReader read1 = new StringReader(xml1);

								InputSource source1 = new InputSource(read1);

								SAXBuilder sb1 = new SAXBuilder();

								Document doc1 = sb1.build(source1);

								Element root1 = doc1.getRootElement();
								this.log.info("根元素名称:" + root1.getName());

								this.log.info("状态值" + root1.getChild("status").getText());
								String status1 = root1.getChild("status").getText();
								log.info("查询代付状态:" + status1);
								if ("1".equals(status1)) {
									surAmount = Double.valueOf(b2.subtract(b4).doubleValue());
									this.log.info("剩余可用额度:" + surAmount.toString());
									merchantinfo.setPositionT1(surAmount.toString());
									int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
									if (num > 0) {
										this.log.info("状态描述" + root1.getChild("statusdesc").getText());
										String statusdesc = root1.getChild("statusdesc").getText();
										result.put("status", status1);
										result.put("statusdesc", "已成功");
									}
								} else if ("0".equals(status1)) {
									surAmount = Double.valueOf(b2.subtract(b4).doubleValue());
									this.log.info("剩余可用额度:" + surAmount.toString());
									merchantinfo.setPositionT1(surAmount.toString());
									int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
									if (num > 0) {
										this.log.info("状态描述" + root1.getChild("statusdesc").getText());
										String statusdesc = root1.getChild("statusdesc").getText();
										result.put("status", status1);
										result.put("statusdesc", "未处理");
									}
								} else if ("2".equals(status1)) {
									surAmount = Double.valueOf(b2.subtract(b4).doubleValue());
									this.log.info("剩余可用额度:" + surAmount.toString());
									merchantinfo.setPositionT1(surAmount.toString());
									int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
									if (num > 0) {
										this.log.info("状态描述" + root1.getChild("statusdesc").getText());
										String statusdesc = root1.getChild("statusdesc").getText();
										result.put("status", status1);
										result.put("statusdesc", "处理中");
									}
								} else if ("4".equals(status1)) {
									surAmount = Double.valueOf(b2.subtract(b4).doubleValue());
									this.log.info("剩余可用额度:" + surAmount.toString());
									merchantinfo.setPositionT1(surAmount.toString());
									int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
									if (num > 0) {
										this.log.info("状态描述" + root1.getChild("statusdesc").getText());
										String statusdesc = root1.getChild("statusdesc").getText();
										result.put("status", status1);
										result.put("statusdesc", "待处理");
									}
								} else {

									b3 = new BigDecimal(merchantinfo.getPoundage());
									this.log.info("每笔代付手续费:" + b3);
									surAmount = Double.parseDouble(merchantinfo.getPositionT1())
											- Double.parseDouble(merchantinfo.getPoundage()) * 100;
									this.log.info("剩余额度:" + surAmount);
									merchantinfo.setPositionT1(surAmount.toString());
									int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
									if (num > 0) {
										this.logger.info("代付查询后失败");
										result.put("status", "3");
										result.put("statusdesc", "代付失败");
									}

								}
							} else {
								b3 = new BigDecimal(merchantinfo.getPoundage());
								this.log.info("每笔代付手续费:" + b3);
								surAmount = Double.parseDouble(merchantinfo.getPositionT1())
										- Double.parseDouble(merchantinfo.getPoundage()) * 100;
								this.log.info("剩余额度:" + surAmount);
								merchantinfo.setPositionT1(surAmount.toString());
								int num = this.pmsMerchantInfoDao.UpdatePmsMerchantInfo(merchantinfo);
								if (num > 0) {
									this.logger.info("代付后失败");
									result.put("status", "3");
									result.put("statusdesc", "代付失败");
								}
							}

						} else {
							log.info("代付金额小于实际额度，请充值后在支付！");
							result.put("004", "代付金额小于实际额度，请充值后在支付！");
							return result;
						}
					} else {
						// 请求参数为空
						logger.info("商户没有开启代付，" + merchantinfo.getMercId());
						result.put("01", "还没有开启代付，请先开启代付，或者等待客服审核！");
						return result;
					}

				} else {
					// 请求参数为空
					logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
					result.put("01", "还没有进行实名认证，请先去进行实名认证，或者等待客服审核！");
					return result;
				}
				/**
				 * 插入代付数据信息
				 */
				model.setCount(dfr.getV_count());
				model.setAmount(dfr.getV_sum_amount());
				model.setCardno(dfr.getV_cardNo());
				model.setRealname(dfr.getV_realName());
				model.setProvince(dfr.getV_province());
				model.setCity(dfr.getV_city());
				model.setPayamount("-" + dfr.getV_amount());
				model.setPmsbankno(dfr.getV_pmsBankNo());
				model.setOagentno("100333");
				if (surAmount != null) {
					model.setPosition(surAmount.toString());
				}
				if (b3 != null) {
					model.setPayCounter(b3.toString());// 手续费
				}
				model.setRecordDescription(merchantinfo.getPoundage());
				model.setRemarks("T1");
				model.setTransactionType("代付");
				model.setResponsecode("01");
				pmsDaifuMerchantInfoDao.insert(model);
			}
		}

		return result;

	}

	public int UpdateDaifu(DaifuRequestEntity daifu) throws Exception {

		log.info("原始数据:" + daifu);

		PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

		log.info("上送的批次号:" + daifu.getV_batch_no());

		pdf.setBatchNo(daifu.getV_batch_no());
		pdf.setResponsecode(daifu.getResponsecode());
		return pmsDaifuMerchantInfoDao.update(pdf);
	}

}

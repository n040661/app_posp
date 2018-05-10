package xdt.service.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.uns.inf.api.model.CallBack;
import com.uns.inf.api.model.Request;
import com.uns.inf.api.service.Service;

import net.sf.json.JSONObject;
import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.dao.IAppRateConfigDao;
import xdt.dao.IMerchantMineDao;
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
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.model.AppRateConfig;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.daikou.model.DaiKouResponseEntity;
import xdt.quickpay.daikou.model.DaikouRequsetEntity;
import xdt.quickpay.daikou.util.HttpUtils;
import xdt.quickpay.daikou.util.SignUtil;
import xdt.quickpay.daikou.util.SignUtilEntity;
import xdt.quickpay.hengfeng.util.Bean2QueryStrUtil;
import xdt.quickpay.hengfeng.util.HttpClientUtil;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.quickpay.nbs.common.util.MD5Util;
import xdt.quickpay.wzf.Constant;
import xdt.quickpay.wzf.DesUtil;
import xdt.quickpay.wzf.UniPaySignUtils;
import xdt.quickpay.wzf.WzfSignUtil;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.IDaiKouService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.servlet.AppPospContext;
import xdt.util.BeanToMapUtil;
import xdt.util.HttpURLConection;
import xdt.util.HttpUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.UtilMethod;

@Component
public class DaikouServiceImpl extends BaseServiceImpl implements IDaiKouService {

	/**
	 * 记录日志
	 */
	private Logger log = Logger.getLogger(DaikouServiceImpl.class);

	private Logger logger = Logger.getLogger(DaikouServiceImpl.class);

	@Resource
	private IPmsMerchantInfoDao pmsMerchantInfoDao; // 商户信息服务层

	@Resource
	private OriginalOrderInfoDao originalDao;

	@Resource
	IPmsGoodsDao pmsGoodsDao;

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
	private IAppRateConfigDao appRateConfigDao;
	@Resource
	private IQuickpayPreRecordDao quickpayPreRecordDao;

	/**
	 * 渠道商户密钥信息
	 */
	@Resource
	private ChannleMerchantConfigKeyDao cmckeyDao;

	/**
	 * 微信商户信息
	 */
	@Resource
	public PmsWeixinMerchartInfoService weixinService;

	@Resource
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;

	public ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception {

		log.info("获取商户密钥信息");
		return cmckeyDao.get(merchantId);
	}

	public synchronized Map<String, String> customerRegister(DaikouRequsetEntity originalinfo) throws Exception {
		// TODO Auto-generated method stub
		String message = "0:initialize";

		String jsonString = null;

		Map<String, String> retMap = new HashMap<String, String>();

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(originalinfo.getMerchantId());

		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(originalinfo.getMerchantId());

		String merchantKey = keyinfo.getMerchantkey();
		SignUtil signUtil = new SignUtil();
		switch (busInfo.getBusinessnum()) {
		case "2120170904150304003":
		case "2120170904150304002":
		case "2120170904150304001":
			logger.info("************************银生宝----代扣录入----处理 开始");
			if (!signUtil.verify(SignUtilEntity.ybsdaifuSigiString(originalinfo), originalinfo.getSign(),
					merchantKey)) {
				log.error("签名错误!");
				retMap.put("respCode", "15");
				retMap.put("respMsg", "签名错误");
				return retMap;
			}
			break;
		case "301101910008364":
		case "301101910008365":
		case "301101910008366":
			logger.info("************************沃支付----代扣录入----处理 开始");
			if (!signUtil.verify(SignUtilEntity.wzfdaifuSigiString(originalinfo), originalinfo.getSign(),
					merchantKey)) {
				log.error("签名错误!");
				retMap.put("respCode", "15");
				retMap.put("respMsg", "签名错误");
				return retMap;
			}
			break;
		default:
			break;
		}

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
				log.error("参数错误!");
				retMap.put("respCode", "16");
				retMap.put("respMsg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {

				PmsWeixinMerchartInfo weixin = new PmsWeixinMerchartInfo();
				weixin.setMerchartId(mercId);
				weixin.setAccount(originalinfo.getPhoneNo());
				weixin.setCardNo(originalinfo.getCardNo());
				PmsWeixinMerchartInfo wx = weixinService.selectByEntity(weixin);
				if (wx != null) {
					Map<String, String> result = setResp("100005", "账号已经注册！");
					result.put("merchartId", mercId);
					return result;
				}
				switch (busInfo.getBusinessnum()) {
				case "2120170904150304003":
				case "2120170904150304002":
				case "2120170904150304001":
					logger.info("************************银生宝----代扣录入----处理 开始");

					// TreeMap<String, String> map = new TreeMap<String, String>();
					String accountId = "2120180110100540001";
					String contractId = "2120180110100540001";
					String cardNo = originalinfo.getCardNo();
					String name = originalinfo.getName();
					String idCardNo = originalinfo.getIdCardNo();
					String phoneNo = originalinfo.getPhoneNo();
					String startDate = "20180110";
					String endDate = "20190108";
					String cycle = originalinfo.getCycle();
					String triesLimit = originalinfo.getTriesLimit();
					String key1 = "cjzfysb123abc";
					JSONObject jsobj1 = new JSONObject();
					jsobj1.put("cardNo", cardNo);
					jsobj1.put("name", name);
					jsobj1.put("idCardNo", idCardNo);
					jsobj1.put("phoneNo", phoneNo);
					jsobj1.put("startDate", startDate);
					jsobj1.put("endDate", endDate);
					jsobj1.put("cycle", cycle);
					jsobj1.put("triesLimit", triesLimit);
					jsobj1.put("accountId", "2120180110100540001");
					jsobj1.put("contractId", "2120180110100540001");
					// map.put("cardNo", cardNo);
					// map.put("name", name);
					// map.put("idCardNo", idCardNo);
					// map.put("phoneNo", phoneNo);
					// map.put("startDate", startDate);
					// map.put("endDate", endDate);
					// map.put("cycle", cycle);
					// map.put("triesLimit", triesLimit);
					// map.put("accountId", "1120180104165923001");
					// map.put("contractId", "1120180104165923001");
					String paramSrc = "accountId=" + accountId + "&contractId=" + contractId + "&name=" + name
							+ "&phoneNo=" + phoneNo + "&cardNo=" + cardNo + "&idCardNo=" + idCardNo + "&startDate="
							+ startDate + "&endDate=" + endDate + "&cycle=" + cycle + "&triesLimit=" + triesLimit
							+ "&key=" + key1;
					logger.info("签名前数据**********支付:" + paramSrc);

					String mac = MD5Util.MD5Encode(paramSrc).toUpperCase();
					logger.info("支付生成的签名：" + mac);
					jsobj1.put("mac", mac);
					// String json=HttpUtil.toJson3(map);

					// logger.info("银生宝上送的数据"+json);

					// String
					// result=HttpUtil.sendPost("http://180.166.114.155:58082/delegate-collect-front/subcontract/signSimpleSubContractJson",
					// map);

					try {
						String result = HttpClientUtil.post(
								"http://114.80.54.68/delegate-collect-front/subcontract/signSimpleSubContractJson",
								jsobj1);
						logger.info("银生宝响应的数据" + result);
						JSONObject jb = JSONObject.fromObject(result);
						String resultCode = (String) jb.get("result_code");
						String result_msg = (String) jb.get("result_msg");
						String subContractId = (String) jb.get("subContractId");
						retMap.put("result_code", resultCode);
						retMap.put("result_msg", result_msg);
						retMap.put("subContractId", subContractId);
						if (resultCode.equals("0000")) {
							PmsWeixinMerchartInfo entity = new PmsWeixinMerchartInfo();
							entity.setMerchartId(mercId);
							entity.setAccount(originalinfo.getPhoneNo());
							entity.setCertNo(originalinfo.getIdCardNo());
							entity.setMobile(originalinfo.getPhoneNo());
							entity.setCardNo(originalinfo.getCardNo());
							entity.setRealName(originalinfo.getName());
							entity.setMerchartName(merchantinfo.getMercName());
							entity.setoAgentNo("100333");
							weixinService.updateRegister(entity);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case "301101910008364":
				case "301101910008365":
				case "301101910008366":
					logger.info("************************沃支付----代扣录入----处理 开始");
					String interfaceVersion = "2.0.0.0";

					String payProducts = originalinfo.getPayProducts();

					String merNo = "301101910008366";

					String signDate = UtilDate.getDate();

					String signNo = UtilDate.getOrderNum();

					String accType = originalinfo.getAccType();

					String accCode = originalinfo.getAccCode();

					String bankCode = originalinfo.getBankCode();

					String signAccInfo = "";

					String signChnl = originalinfo.getSignChnl();

					String charSet = "UTF-8";

					String signType = "RSA_SHA256";

					String signMsg = "";

					String signAcc = originalinfo.getCardNo();

					String signName = originalinfo.getName();

					String identityNo = originalinfo.getIdCardNo();

					String CCV = "";

					String validity = "";

					if (originalinfo.getValidity() != null) {
						validity = originalinfo.getValidity();
					}
					if (originalinfo.getCCV() != null) {
						CCV = originalinfo.getCCV();
					}

					if ("A01".equals(originalinfo.getAccCode())) {

						String cert = "signAcc=" + signAcc + "|" + "signName=" + signName + "|" + "identityNo=1"
								+ identityNo;

						logger.info("签约账户信息:" + cert);

						String key = "Q310LQ04O8Q64FOCUNB0GDHN4IJUQRQB";

						logger.info("沃支付商户密钥:" + key);

						String sign = DesUtil.encrypt(cert, key, "utf-8").replaceAll("\r\n", "");
						// sign=URLEncoder.encode(sign,"UTF-8");
						logger.info("沃支付生成的签名:" + sign);

						signAccInfo = sign;

					} else if ("A02".equals(originalinfo.getAccCode())) {
						String cert = "signAcc=" + signAcc + "|" + "signName=" + signName + "|" + "identityNo=1"
								+ identityNo + "|" + "validity=" + validity + "|" + "CCV=" + CCV;

						logger.info("签约账户信息:" + cert);

						String key = busInfo.getKek();

						logger.info("沃支付商户密钥:" + key);

						String sign = DesUtil.encrypt(cert, key, "utf-8");
						// sign=URLEncoder.encode(sign,"utf-8");

						logger.info("沃支付生成的签名:" + sign);

						signAccInfo = sign;
					}

					String str = "accCode=" + accCode + "|" + "accType=" + accType + "|" + "bankCode=" + bankCode + "|"
							+ "charSet=" + charSet + "|" + "interfaceVersion=" + interfaceVersion + "|" + "merNo="
							+ merNo + "|" + "payProducts=" + payProducts + "|" + "signAccInfo=" + signAccInfo + "|"
							+ "signChnl=" + signChnl + "|" + "signDate=" + signDate + "|" + "signNo=" + signNo + "|"
							+ "signType=" + signType + "|";

					logger.info("向上游待发数据:" + str);

					// 生成签名
					Map<String, String> params = WzfSignUtil.rep2Map(str);
					// 商户的签名
					String sign = UniPaySignUtils.merSign(params, "RSA_SHA256");
					// sign=URLEncoder.encode(sign,"UTF-8");
					params.put("signMsg", sign);
					// HttpClientUtil client = new HttpClientUtil();
					logger.info("向上游发送的签名:" + sign);
					String cert = "accCode=" + accCode + "&" + "accType=" + accType + "&" + "bankCode=" + bankCode + "&"
							+ "charSet=" + charSet + "&" + "interfaceVersion=" + interfaceVersion + "&" + "merNo="
							+ merNo + "&" + "payProducts=" + payProducts + "&" + "signAccInfo=" + signAccInfo + "&"
							+ "signChnl=" + signChnl + "&" + "signDate=" + signDate + "&" + "signNo=" + signNo + "&"
							+ "signType=" + signType + "&" + "signMsg=" + sign;

					String url = Constant.SIN_URL;

					logger.info("向上游发送的数据:" + cert);
					// String result = HttpURLConection.httpURLConnectionPOST(url, cert);
					List list = HttpUtils.URLPost(url, params);

					// String result = client.post(url, cert);

					logger.info("响应的数据:" + list.get(0).toString());
					String[] array = list.get(0).toString().split("\\&");

					DaiKouResponseEntity dkr = new DaiKouResponseEntity();

					if (array[2] != null) {
						String[] bankCode1 = array[2].split("\\=");
						dkr.setBankCode(bankCode1[1]);
					}
					if (array[7] != null) {
						String[] merWhProtno = array[7].split("\\=");
						dkr.setMerWhProtno(merWhProtno[1]);
					}
					if (array[9] != null) {
						String[] payProducts1 = array[9].split("\\=");
						dkr.setPayProducts(payProducts1[1]);

					}
					if (array[12] != null) {
						String[] signChnl1 = array[12].split("\\=");
						dkr.setSignChnl(signChnl1[1]);
					}
					if (array[16] != null) {
						String[] signNo1 = array[16].split("\\=");
						dkr.setSignNo(signNo1[1]);
					}
					if (array[17] != null) {
						String[] signRst1 = array[17].split("\\=");
						dkr.setSignRst(signRst1[1]);
					}

					if (array[20] != null) {
						String[] transCode1 = array[20].split("\\=");
						dkr.setTransCode(transCode1[1]);
					}
					if (array[21] != null) {
						String[] transDesc1 = array[21].split("\\=");
						dkr.setTransDesc(transDesc1[1]);
					}
					if (dkr.getSignRst().equals("1")) {
						PmsWeixinMerchartInfo entity = new PmsWeixinMerchartInfo();
						entity.setMerchartId(mercId);
						entity.setAccount(originalinfo.getPhoneNo());
						entity.setCertNo(originalinfo.getIdCardNo());
						entity.setMobile(originalinfo.getPhoneNo());
						entity.setCardNo(originalinfo.getCardNo());
						entity.setRealName(originalinfo.getName());
						entity.setSubContractId(dkr.getMerWhProtno());
						;
						entity.setBankName(originalinfo.getBankCode());
						entity.setCardType(originalinfo.getAccCode());
						entity.setoAgentNo("100333");
						weixinService.updateRegister(entity);
					}
					dkr.setMerchantId(originalinfo.getMerchantId());

					retMap = BeanToMapUtil.convertBean(dkr);

					break;
				default:
					break;
				}

			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("respCode", "17");
				retMap.put("respMsg", "商户没有进行实名认证");
				return retMap;
			}
		} else {
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("respCode", "17");
			retMap.put("respMsg", "商户没有进行实名认证");
			return retMap;
		}

		return retMap;
	}

	/**
	 * 
	 * @Description 设置响应信息
	 * @author Administrator
	 * @param respCode
	 * @param respInfo
	 * @return
	 */
	public Map<String, String> setResp(String respCode, String respInfo) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("respCode", respCode);
		result.put("respInfo", respInfo);
		return result;
	}

	public synchronized Map<String, String> payHandle(DaikouRequsetEntity originalinfo) throws Exception {

		String message = "0:initialize";

		String jsonString = null;

		SubmitOrderNoCardPayResponseDTO responseDTO = new SubmitOrderNoCardPayResponseDTO();

		Map<String, String> retMap = new HashMap<String, String>();
		// 查询上游商户号
		PmsBusinessPos busInfo = selectKey(originalinfo.getMerchantId());
		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(originalinfo.getMerchantId());

		String merchantKey = keyinfo.getMerchantkey();
		SignUtil signUtil = new SignUtil();
		switch (busInfo.getBusinessnum()) {
		case "2120170904150304003":
		case "2120170904150304002":
		case "2120170904150304001":
			logger.info("************************银生宝----代扣录入----处理 开始");
			if (!signUtil.verify(SignUtilEntity.ybsdaikouSigiString(originalinfo), originalinfo.getSign(), merchantKey)) {
				log.error("签名错误!");
				retMap.put("respCode", "15");
				retMap.put("respMsg", "签名错误");
				return retMap;
			}
			break;
		case "301101910008364":
		case "301101910008365":
		case "301101910008366":
			logger.info("************************沃支付----代扣录入----处理 开始");
			if (!signUtil.verify(SignUtilEntity.wzfdaikouSigiString(originalinfo), originalinfo.getSign(), merchantKey)) {
				log.error("签名错误!");
				retMap.put("respCode", "15");
				retMap.put("respMsg", "签名错误");
				return retMap;
			}
			break;
		default:
			break;
		}
		// 验证当前是否已经下单
		OriginalOrderInfo orig = new OriginalOrderInfo();
		orig.setMerchantOrderId(originalinfo.getOrderId());
		orig.setPid(originalinfo.getMerchantId());

		if (originalDao.selectByOriginal(orig) != null) {

			log.error("下单重复!");
			retMap.put("respCode", "20");
			retMap.put("respMsg", "下单重复");
			return retMap;
		}

		String orderNumber = UtilMethod.getOrderid("180");// 1、订单号由
															// 业务号（2位）+业务细分（1位）+时间戳（13位）
															// 总共16位
		/**
		 * 插入原始数据信息
		 */
		OriginalOrderInfo original = new OriginalOrderInfo();
		original.setMerchantOrderId(originalinfo.getOrderId());// 原始数据的订单编号
		original.setOrderId(orderNumber); // 为主键
		original.setPid(originalinfo.getMerchantId());
		original.setOrderAmount(originalinfo.getAmount());
		original.setBgUrl(originalinfo.getResponseUrl());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		original.setOrderTime(df.format(new Date()).toString());
		if(originalinfo.getSubContractId()!=null)
		{
			original.setBankId(originalinfo.getSubContractId());
		}
		if(originalinfo.getGoodsName()!=null)
		{
			original.setProcdutName(originalinfo.getGoodsName());
		}		
		if(originalinfo.getCardNo()!=null)
		{
			original.setBankNo(originalinfo.getCardNo());
		}
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
				log.error("参数错误!");
				retMap.put("respCode", "16");
				retMap.put("respMsg", "参数错误,没有欧单编号");
				return retMap;
			}
			// 判断是否为正式商户
			if ("60".equals(merchantinfo.getMercSts())) {
				// 实际金额
				String factAmount = originalinfo.getAmount();
				// 校验欧单金额限制
				ResultInfo payCheckResult = iPublicTradeVerifyService
						.amountVerifyOagent((int) Double.parseDouble(factAmount), TradeTypeEnum.onlinePay, oAgentNo);
				if (!payCheckResult.getErrCode().equals("0")) {
					// 交易不支持
					logger.info(
							"欧单金额限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.WithholdPay.getTypeCode());
					retMap.put("0004", "欧单金额限制代扣");
					return retMap;
				}
				// 校验商户模块是否开启
				ResultInfo payCheckResult3 = iPublicTradeVerifyService.moduelVerifyMer(TradeTypeEnum.onlinePay, mercId);
				if (!payCheckResult3.getErrCode().equals("0")) {
					// 交易不支持
					logger.info(
							"商户模块限制，oAagentNo:" + oAgentNo + ",payType:" + PaymentCodeEnum.WithholdPay.getTypeCode());
					retMap.put("0005", "商户模块限制代扣");
					return retMap;
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
					retMap.put("0006", "没有查到相关费率配置：");
					return retMap;
				}

				String maxTransMoney = resultMap.get("MAX_AMOUNT"); // 每笔最大交易金额
				String minTransMoney = resultMap.get("MIN_AMOUNT"); // 每笔最小交易金额
				String paymentAmount = factAmount;// 交易金额

				if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(maxTransMoney)) == 1) {
					// 金额超过最大金额
					logger.info("交易金额大于最打金额");
					retMap.put("0007", "金额超过最大交易金额");
					return retMap;
				} else if (new BigDecimal(paymentAmount).compareTo(new BigDecimal(minTransMoney)) == -1) {
					// 金额小于最小金额
					logger.info("交易金额小于最小金额");
					retMap.put("0008", "交易金额小于最小金额");
					return retMap;

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
				pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.WithholdPay.getTypeName());
				pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.WithholdPay.getTypeCode());
				pmsAppTransInfo.setPrepaidphonenumber(originalinfo.getPhoneNo());// 手机号码
				// pmsAppTransInfo.setToken(originalinfo.getToken()); //令牌
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

					paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
					BigDecimal payAmount = null;
					BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
					// 费率
					BigDecimal fee = new BigDecimal(0);
					String rateStr = "";
					Double daikou_min_poundage=null;
					Double daikou_max_poundage=null;
					
					String quickRateType="";
					
					AppRateConfig appRate = new AppRateConfig();
					String isTop = "";
					String rate = "";
					String topPoundage = "";// 封顶手续费
					String minPoundageStr = "";// 最低手续费
					Double minPoundage = 0.0; // 附加费
					AppRateConfig appRateConfig=new AppRateConfig();
					Double payfee = null;
					switch (busInfo.getBusinessnum()) {
					case "2120170904150304003":
					case "301101910008364":
						logger.info("************************----代扣交易按费率收取----处理 开始");
						quickRateType = resultMap.get("QUICKRATETYPE").toString();// 快捷支付费率类型

						// 获取o单第三方支付的费率
						appRate.setRateType(quickRateType);
						appRate.setoAgentNo(oAgentNo);
					    appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

						if (appRateConfig == null) {
							// 若查到的是空值，直接返回错误
							logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
							retMap.put("0010", "没有查到相关费率配置，请联系客服人员");
							return retMap;
						}

						isTop = appRateConfig.getIsTop();
						 rate = appRateConfig.getRate();
						topPoundage = appRateConfig.getTopPoundage();// 封顶手续费
						paymentAmount = pmsAppTransInfo.getFactamount();// 支付金额
					    minPoundageStr = appRateConfig.getBottomPoundage();// 最低手续费

						if (StringUtils.isNotBlank(appRateConfig.getIsBottom())
								&& appRateConfig.getIsBottom().equals("1")) {// 是否有清算费用，"1":有，“0”无
							if (StringUtils.isNotBlank(minPoundageStr)) {
								minPoundage = Double.parseDouble(minPoundageStr); // 清算手续费
							} else {
								// 若查到的是空值，直接返回错误
								logger.info("没有查到相关费率配置：" + merchantinfo.getMobilephone());
								retMap.put("0010", "没有查到相关费率配置，请联系客服人员");
								return retMap;
							}
						}
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
						break;
					case "2120170904150304002":
					case "301101910008365":
						logger.info("************************----代扣交易按笔收取----处理 开始");
			
						// 计算结算金额
						daikou_min_poundage = Double.parseDouble(merchantinfo.getDaikouMinPoundage());// 代扣最小金额
					    daikou_max_poundage = Double.parseDouble(merchantinfo.getDaikouMaxPoundage());// 代扣最小金额
						if (dfactAmount.doubleValue() / 100 > daikou_min_poundage.doubleValue()
								&& dfactAmount.doubleValue() / 100 <= 5000) {
							fee = new BigDecimal(daikou_min_poundage).multiply(new BigDecimal(100));
						} else if (dfactAmount.doubleValue() / 100 > 5000 && dfactAmount.doubleValue() / 100 <= 20000) {
							fee = new BigDecimal(daikou_max_poundage).multiply(new BigDecimal(100));
						} else {
							logger.info("交易金额小于最小代扣金额");
							retMap.put("0009", "交易金额小于最小代扣金额");
							return retMap;
						}
						payAmount = dfactAmount.subtract(fee);

						// 设置结算金额
						pmsAppTransInfo.setPayamount(payAmount.toString());// 结算金额
						pmsAppTransInfo.setRate("0");// 0.50_35 || 0.50
						pmsAppTransInfo.setPoundage(fee.toString());
						break;
					case "2120170904150304001":
					case "301101910008366":
						logger.info("************************----代扣交易按年费收取----处理 开始");
						// 设置结算金额
						pmsAppTransInfo.setPayamount(paymentAmount.toString());// 结算金额
						pmsAppTransInfo.setRate("0");// 0.50_35 || 0.50
						pmsAppTransInfo.setPoundage("0");
						break;
					default:
						break;
					}
					pmsAppTransInfo.setDrawMoneyType("1");// 普通提款
					// 转换double为int
					Integer paymentAmountInt = (int) Double.parseDouble(paymentAmount);

					// 验证支付方式是否开启
					payCheckResult = iPublicTradeVerifyService.totalVerify(paymentAmountInt, TradeTypeEnum.onlinePay,
							PaymentCodeEnum.WithholdPay, oAgentNo, merchantinfo.getMercId());
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
								+ PaymentCodeEnum.WithholdPay.getTypeCode());
						retMap.put("0011", "不支持代扣");
						return retMap;
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
					String transOrderId = generateTransOrderId(TradeTypeEnum.onlinePay, PaymentCodeEnum.WithholdPay);
					if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
						// 已经存在，修改流水号，设置pospsn为空
						logger.info("订单号：" + pmsAppTransInfo.getOrderid() + ",生成上送通道的流水号：" + transOrderId);
						pospTransInfo.setTransOrderId(originalinfo.getOrderId());
						pospTransInfo.setResponsecode("99");
						pospTransInfo.setPospsn("");
						insertOrUpdateFlag = 1;
					} else {
						// 不存在流水，生成一个流水
						pospTransInfo = generateTransFromAppTrans(pmsAppTransInfo);
						// 设置上送流水号
						pospTransInfo.setTransOrderId(originalinfo.getOrderId());
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
					int number = pmsAppTransInfoDao.update(pmsAppTransInfo);
					if (number > 0) {
						switch (busInfo.getBusinessnum()) {
						case "2120170904150304003":
						case "2120170904150304002":
						case "2120170904150304001":
							logger.info("************************银生宝----代扣交易上送----处理 开始");
							Request dcRequest = new Request();
							Double amount = Double.parseDouble(originalinfo.getAmount()) / 100;
							log.info("代扣金额为:" + amount);
							String accountId = "2120180110100540001";
							String subContractId = originalinfo.getSubContractId();
							String orderId = originalinfo.getOrderId();
							String purpose = originalinfo.getPurpose();
							//String amount = amount.toString();
							String phoneNo = originalinfo.getPhoneNo();
							String responseUrl=BaseUtil.url+"/dk/ysbbgPayResult.action";
							String key1 = "cjzfysb123abc";
							 String paramSrc = "accountId=" + accountId + "&subContractId=" + subContractId +"&orderId=" + orderId + "&purpose="+ purpose + "&amount=" + amount + "&phoneNo=" + phoneNo + "&responseUrl=" +responseUrl +"&key=" + key1;
							logger.info("签名前数据**********支付:" + paramSrc);

							String  mac= MD5Util.MD5Encode(paramSrc).toUpperCase();
							logger.info("支付生成的签名："+mac);
							JSONObject jsobj1 = new JSONObject();
							jsobj1.put("subContractId", subContractId);
							jsobj1.put("orderId", orderId);
							jsobj1.put("purpose", purpose);
							jsobj1.put("phoneNo", phoneNo);
							jsobj1.put("amount", amount.toString());
							jsobj1.put("accountId", "2120180110100540001");
							jsobj1.put("responseUrl", responseUrl);
							jsobj1.put("mac", mac);				
							try {
								String result=HttpClientUtil.post("http://114.80.54.68/delegate-collect-front/delegateCollect/collectJson",jsobj1);
								logger.info("银生宝响应的数据"+result);
								JSONObject jb = JSONObject.fromObject(result);
								String resultCode = (String) jb.get("result_code");
								String result_msg = (String) jb.get("result_msg");
								retMap.put("result_code", resultCode);
								retMap.put("result_msg", result_msg);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						case "301101910008364":
						case "301101910008365":
						case "301101910008366":
							logger.info("************************沃支付----代扣交易----处理 开始");
							String interfaceVersion = "2.0.0.0";

							String tradeMode = originalinfo.getTradeMode();
							
							String payProducts = originalinfo.getPayProducts();

							String merNo = "301101910008366";

							String goodsName =originalinfo.getGoodsName();
							
							String merWhProtno=originalinfo.getSubContractId();

							String reqTime = UtilDate.getOrderNum();

							String orderDate = UtilDate.getDate();

							String orderNo = originalinfo.getOrderId();

							String Payamount = originalinfo.getAmount();

							String merWhDetails = payProducts+"|"+merWhProtno+"|";

							String bizCode = originalinfo.getBizCode();
							
							String callbackUrl=BaseUtil.url+"/dk/wzfbgPayResult.action";

							String charSet = "UTF-8";

							String signType = "RSA_SHA256";

							String signMsg = "";


							String str ="amount="+ Payamount+"|"+"bizCode="+bizCode+"|"+"callbackUrl=" + callbackUrl  + "|"+"goodsName="+goodsName+"|"
										+ "charSet=" + charSet + "|"  + "interfaceVersion="
										+ interfaceVersion + "|" + "merNo=" + merNo + "|" + "merWhDetails=" + merWhDetails + "|"
										 + "orderDate=" + orderDate + "|" + "orderNo=" + orderNo+"|"+"reqTime="+reqTime
										+ "|" + "signType=" + signType+"|"+"tradeMode="+tradeMode+"|" ;

							logger.info("向上游待发数据:" + str);

							// 生成签名
							Map<String, String> params =new  HashMap<String, String>();
							params.put("amount", Payamount);
							params.put("bizCode", bizCode);
							params.put("callbackUrl", callbackUrl);
							params.put("charSet", charSet);
							params.put("goodsName",goodsName);
							params.put("interfaceVersion", interfaceVersion);
							params.put("merNo", merNo);
							params.put("merWhDetails", merWhDetails);
							params.put("orderDate", orderDate);
							params.put("orderNo", orderNo);
							params.put("reqTime", reqTime);
							params.put("signType", signType);
							params.put("tradeMode", tradeMode);
							// 商户的签名
							String sign = UniPaySignUtils.merSign(params, "RSA_SHA256");
							//sign=URLEncoder.encode(sign,"UTF-8");
							params.put("signMsg", sign);
							// HttpClientUtil client = new HttpClientUtil();
							logger.info("向上游发送的签名:" + sign);
//							String cert = "accCode=" + accCode + "&" + "accType=" + accType + "&" + "bankCode=" + bankCode + "&"
//									+ "charSet=" + charSet + "&" + "interfaceVersion=" + interfaceVersion + "&" + "merNo=" + merNo
//									+ "&" + "payProducts=" + payProducts + "&" + "signAccInfo=" + signAccInfo + "&" + "signChnl="
//									+ signChnl + "&" + "signDate=" + signDate + "&" + "signNo=" + signNo + "&" + "signType="
//									+ signType + "&" + "signMsg=" + sign;

							String url = Constant.WITHH_URL;

							logger.info("向上游发送的数据:" + params);
							//String result = HttpURLConection.httpURLConnectionPOST(url, cert);
							List list = HttpUtils.URLPost("http://mertest.unicompayment.com/WithhGw_XT/servlet/SingleWithhServlet.htm", params);
							
							// String result = client.post(url, cert);

							logger.info("响应的数据:"+list.get(0).toString());
							String[] array=list.get(0).toString().split("\\&");
			
							if (array[0] != null) {
								String[] acountDate = array[0].split("\\=");
								if(acountDate.length==2)
								{
									retMap.put("acountDate", acountDate[1]);
								}
								
							}
							if (array[1] != null) {
								String[] amount2 = array[1].split("\\=");
								retMap.put("amount", amount2[1]);
							}
							if (array[2] != null) {
								String[] bizCode2 = array[2].split("\\=");
								retMap.put("bizCode", bizCode2[1]);
							}
							if (array[3] != null) {
								String[] callbackUrl2 = array[3].split("\\=");
								retMap.put("callbackUrl", callbackUrl2[1]);
							}
							if (array[4] != null) {
								String[] charSet2 = array[4].split("\\=");
								retMap.put("charSet", charSet2[1]);
							}

							if (array[16] != null) {
								String[] merWhDetails2 = array[16].split("\\=");
								retMap.put("merWhDetails", merWhDetails2[1]);
							}
							if (array[18] != null) {
								String[] orderDate2 = array[18].split("\\=");
								retMap.put("orderDate", orderDate2[1]);
							}
							if (array[19] != null) {
								String[] orderNo2 = array[19].split("\\=");
								retMap.put("orderNo", orderNo2[1]);
							}
							if (array[20] != null) {
								String[] payJnlno2 = array[20].split("\\=");
								if(payJnlno2.length==2)
								{
								  retMap.put("payJnlno", payJnlno2[1]);
								}
								
							}
							if (array[25] != null) {
								String[] reqTime2 = array[25].split("\\=");
								retMap.put("reqTime", reqTime2[1]);
							}
							if (array[28] != null) {
								String[] tradeMode2 = array[28].split("\\=");
								retMap.put("tradeMode", tradeMode2[1]);
							}
							if (array[29] != null) {
								String[] transCode2 = array[29].split("\\=");
								retMap.put("transCode", transCode2[1]);
							}
							if (array[30] != null) {
								String[] transDesc2 = array[30].split("\\=");
								retMap.put("transDesc", transDesc2[1]);
							}

							if (array[31] != null) {
								String[] transDis2 = array[31].split("\\=");
								retMap.put("transDis", transDis2[1]);
							}
							if (array[32] != null) {
								String[] transRst2 = array[32].split("\\=");
								retMap.put("transRst", transRst2[1]);
							}
							break;
						default:
							break;
						}
					}
				}
			} else {
				// 请求参数为空
				logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
				retMap.put("respCode", "17");
				retMap.put("respMsg", "商户没有进行实名认证");
				return retMap;
			}
		} else {
			logger.info("商户没有进行实名认证，" + merchantinfo.getMercId());
			retMap.put("respCode", "17");
			retMap.put("respMsg", "商户没有进行实名认证");
			return retMap;
		}
		return retMap;

	}

	@Override
	public synchronized void otherInvoke(CallBack result) throws Exception {

		// 流水表transOrderId
		String transOrderId = result.getOrderId();

		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);

		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());

		// 查询结果成功
		if ("00".equals(result.getResult_code())) {
			// 支付成功
			pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
			pmsAppTransInfo.setThirdPartResultCode(result.getResult_msg());
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
				pospTransInfo.setPospsn(result.getResult_msg());
				log.info("更新流水");
				log.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("代扣失败");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn("代扣失败");
				log.info("更新流水");
				log.info(pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		}
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

}

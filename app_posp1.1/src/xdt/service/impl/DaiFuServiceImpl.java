package xdt.service.impl;

import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import com.capinfo.crypt.Md5;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import ccit.security.bssp.util.Constants;
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
import xdt.dto.SubmitOrderNoCardPayResponseDTO;
import xdt.dto.jsds.JsdsRequestDto;
import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.quickpay.daifu.DaiFuRequestEntity;
import xdt.quickpay.daikou.util.HttpUtils;
import xdt.quickpay.hengfeng.util.HFSignUtil;
import xdt.quickpay.hengfeng.util.PreSginUtil;
import xdt.quickpay.mobao.MobaoPayHandel;
import xdt.quickpay.wzf.Constant;
import xdt.quickpay.wzf.UniPaySignUtils;
import xdt.service.IDaiFuService;
import xdt.service.IMerchantCollectMoneyService;
import xdt.service.IPmsMessageService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.util.JsdsUtil;
import xdt.util.UtilDate;

@Component
public class DaiFuServiceImpl extends BaseServiceImpl implements IDaiFuService {
	/**
	 * 记录日志
	 */
	private Logger log = Logger.getLogger( DaiFuServiceImpl.class);

	private Logger logger = Logger.getLogger(DaiFuServiceImpl.class);

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
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception {
		OriginalOrderInfo original = null;
		// 查询流水信息
		PospTransInfo transInfo = pospTransInfoDAO.searchBytransOrderId(tranId);
		String oderId = transInfo.getOrderId();
		log.info("根据上送订单号  查询商户上送原始信息");
		original = originalDao.getOriginalOrderInfoByOrderid(oderId);
		return original;
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
	@Override
	public Map<String, String> Payroll(DaiFuRequestEntity array) throws Exception {

		Map<String, String> result = new HashMap<String, String>();

		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		
		model.setMercId(array.getMerchantId());
		model.setAmount(array.getAmount());
		model.setBatchNo(array.getOrderId());

		String merid = array.getMerchantId();

		log.info("下游上送的商户号:" + merid);

		// 验证签名
		ChannleMerchantConfigKey keyinfo = this.getChannelConfigKey(merid);

		String merchantKey = keyinfo.getMerchantkey();
		if (this.verify(array, result)) {
			logger.info("****************************代付------签名错误");
			return result;
		} else {
			if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {

				result.put("12", "代付重复");
				log.info("代付重复");
				return result;
			}
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(array.getMerchantId());
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
						volumn = new BigDecimal(1);
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
							Map<String,String> params=new HashMap<String,String>();
 							String interfaceVersion="1.0.0.1";  					
							String tranType=array.getTranType();
							String merNo="301100710007122";
							String orderDate=UtilDate.getDate();
							String reqTime=UtilDate.getOrderNum();
							String orderNo=array.getOrderId();
							String amount=array.getAmount();
							String bizCode=array.getBizCode();
							String payeeAcc="";
							params.put("interfaceVersion", interfaceVersion);
 							params.put("tranType", tranType);
 							params.put("merNo", merNo);
 							params.put("orderDate", orderDate);
 							params.put("reqTime", reqTime);
 							params.put("orderNo", orderNo);
 							params.put("amount",amount);
 							params.put("bizCode", bizCode);
							if(array.getPayeeAcc()!=null)
							{
								payeeAcc=array.getPayeeAcc();
								params.put("payeeAcc", payeeAcc);
							}
							String woType=array.getWoType();
							params.put("woType", woType);
							String payeeBankCode="";
							if(array.getPayeeBankCode()!=null)
							{
								payeeBankCode=array.getPayeeBankCode();
								params.put("payeeBankCode", payeeBankCode);
							}
							String payeeName="";
							if(array.getPayeeName()!=null)
							{
								payeeName=array.getPayeeName();
								params.put("payeeName", payeeName);
							}
							String payeeBankBranch="";
							if(array.getPayeeBankBranch()!=null)
							{
								payeeBankBranch=array.getPayeeBankBranch();
								params.put("payeeBankBranch", payeeBankBranch);
							}
							String payeeUnionBan="";
							if(array.getPayeeUnionBankNo()!=null)
							{
								payeeUnionBan=array.getPayeeUnionBankNo();
								params.put("payeeUnionBan", payeeUnionBan);
							}
							String payeeAttribution="";
							if(array.getPayeeAttribution()!=null)
							{
								payeeAttribution=array.getPayeeAttribution();
								params.put("payeeAttribution", payeeAttribution);
							}
							String identityInfo="";
							if(array.getIdentityInfo()!=null)
							{
								identityInfo=array.getIdentityInfo();
								params.put("identityInfo", identityInfo);
							}
							String callbackUrl=array.getCallbackUrl();
							params.put("callbackUrl", callbackUrl);
							String merExtend="";
							if(array.getMerExtend()!=null)
							{
								merExtend=array.getMerExtend();
								params.put("merExtend", merExtend);
							}
							String signType="RSA_SHA256";
							params.put("signType", signType);
							String signMsg="";
							// 商户的签名
							String sign = UniPaySignUtils.merSign(params, "RSA_SHA256");
							//sign=URLEncoder.encode(sign,"UTF-8");
							params.put("signMsg", sign);
							// HttpClientUtil client = new HttpClientUtil();
							logger.info("向上游发送的签名:" + sign);
							String url = Constant.SINGLEPAY_URL;

							logger.info("向上游发送的数据:" + params);
							List list = HttpUtils.URLPost("http://mertest.unicompayment.com/issuegw/servlet/SingleIssueServlet.htm", params);
							

							logger.info("响应的数据:"+list.get(0).toString());
							
							

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
//				/**
//				 * 插入代付数据信息
//				 */
////				//model.setCount(dfr.getV_count());
////				//model.setAmount(dfr.getV_sum_amount());
////				model.setCardno(dfr.getV_cardNo());
////				model.setRealname(dfr.getV_realName());
////				model.setProvince(dfr.getV_province());
////				model.setCity(dfr.getV_city());
////				model.setPayamount("-" + dfr.getV_amount());
////				model.setPmsbankno(dfr.getV_pmsBankNo());
//				model.setOagentno("100333");
//				if (surAmount != null) {
//					model.setPosition(surAmount.toString());
//				}
//				if (b3 != null) {
//					model.setPayCounter(b3.toString());// 手续费
//				}
//				model.setRecordDescription(merchantinfo.getPoundage());
//				model.setRemarks("T1");
//				model.setTransactionType("代付");
//				model.setResponsecode("01");
//				pmsDaifuMerchantInfoDao.insert(model);
				
				
		}
		}
		return result;
		
	}
	private boolean verify(DaiFuRequestEntity reqData, Map<String, String> result) {
		boolean signResult = false;
		logger.info("****************************开始签名处理");

		try {
			String e = reqData.getSign();
			HashMap<String, String> signMap = JsdsUtil.beanToMap(reqData);
			signMap.remove("sign");
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
			String merchNo = reqData.getMerchantId();
			logger.info("********************江苏电商-----------------当前商户号:" + merchNo);
			ChannleMerchantConfigKey channerKey = this.cmckeyDao.get(merchNo);
			logger.info("********************江苏电商-----------------商户密钥:" + channerKey);
			String key = channerKey.getMerchantkey();
			System.out.println("生成签名的数据:" + signMap);
			System.out.println("秘钥:" + key);
			if (!e.equals(JsdsUtil.sign(signMap, key))) {
				result.put("respCode", "0008");
				result.put("respMsg", "签名错误");
				signResult = true;
			}
		} catch (Exception var9) {
			result.put("respCode", "0008");
			result.put("respMsg", "签名错误");
			signResult = true;
		}

		return signResult;
	}

}

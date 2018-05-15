package xdt.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

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
import xdt.dto.cj.BaseConstant;
import xdt.dto.cj.ChanPayUtil;
import xdt.dto.hj.HJRequest;
import xdt.dto.hj.HJUtil;
import xdt.dto.hm.AesEncryption;
import xdt.dto.hm.HMRequest;
import xdt.dto.hm.HMUtil;
import xdt.dto.hm.HttpsUtil;
import xdt.dto.hm.SHA256Util;
import xdt.dto.hm.TimeUtil;
import xdt.dto.jp.JpUtil;
import xdt.dto.jp.MerchantUtil;
import xdt.dto.jp.RSASignUtil;
import xdt.dto.pay.Balance;
import xdt.dto.pay.BaseResMessage;
import xdt.dto.pay.Bill;
import xdt.dto.pay.Constants;
import xdt.dto.pay.ConsumeSMSVo;
import xdt.dto.pay.ConsumeVo;
import xdt.dto.pay.EncryptUtil;
import xdt.dto.pay.MerchantVo;
import xdt.dto.pay.PageOpenCardVo;
import xdt.dto.pay.PayRequest;
import xdt.dto.pay.PayThread;
import xdt.dto.pay.PayUtil;
import xdt.dto.pay.QueryPayThread;
import xdt.dto.pay.QueryWithdrawDepositResult;
import xdt.dto.pay.SignUtil;
import xdt.dto.pay.TokenRes;
import xdt.mapper.PmsWeixinMerchartInfoMapper;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.schedule.ThreadPool;
import xdt.service.HfQuickPayService;
import xdt.service.IPayService;
import xdt.service.IPublicTradeVerifyService;
import xdt.service.PmsWeixinMerchartInfoService;
import xdt.util.JsonUtil;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;

@Service
public class PayserviceImpl extends BaseServiceImpl implements IPayService {

	Logger log = Logger.getLogger(this.getClass());

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
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;// 代付
	@Resource
	private HfQuickPayService payService;

	@Resource
	public PmsWeixinMerchartInfoService weixinService;
	
	@Override
	public Map<String, String> pay(PayRequest payRequest,
			Map<String, String> result) {
		log.info("畅捷----下游传送代付参数:" + JSON.toJSON(payRequest));
		BigDecimal b1 = new BigDecimal("0");// 下游上传的总金额
		BigDecimal b2 = new BigDecimal("0");// 系统代付余额
		BigDecimal b3 = new BigDecimal("0");// 单笔交易总手续费
		BigDecimal min = new BigDecimal("0");// 代付最小金额
		BigDecimal max = new BigDecimal("0");// 代付最大金额
		Double surplus;// 代付剩余金额
		payRequest.setSummary("T1");
		log.info("畅捷----查询当前代付订单是否存在");
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		Map<String, String> map = new HashMap<>();
		Map<String, String> maps = new HashMap<>();// 填金
		model.setMercId(payRequest.getMerchantId());
		model.setBatchNo(payRequest.getOrderId());
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
			result.put("respCode", "0006");
			result.put("respMsg", "下单失败,订单存在");
			log.info("畅捷----**********************代付 下单失败:{}");
			log.info("畅捷----订单存在");
			return result;
		}
		try {
			label104: {

				log.info("********************畅捷-------------根据商户号查询");
				String e = payRequest.getMerchantId();
				PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
				merchantinfo.setMercId(e);
				List merchantList = this.pmsMerchantInfoDao
						.searchList(merchantinfo);
				if (merchantList.size() != 0 && !merchantList.isEmpty()) {
					merchantinfo = (PmsMerchantInfo) merchantList.get(0);
					if (merchantinfo.getOpenPay().equals("1")) {
						result.put("respCode", "01");
						result.put("respMsg", "未开通代付");
						return result;
					}
					String oAgentNo = merchantinfo.getoAgentNo();
					log.info("***********畅捷*************商户信息:" + merchantinfo);
					if (StringUtils.isBlank(oAgentNo)) {
						throw new RuntimeException("系统错误----------------o单编号为空");
					}

					if ("60".equals(merchantinfo.getMercSts())) {
						// 插入异步数据
						saveOriginAlInfoWxPay1(payRequest,
								payRequest.getOrderId(),
								payRequest.getMerchantId());
						// 判断交易类型
						log.info("***********畅捷*************实际金额");
						// 分
						String payAmt = payRequest.getAmount();
						b1 = new BigDecimal(payAmt);

						System.out.println("参数:" + b1.doubleValue());
						log.info("***********畅捷*************校验欧单金额限制");
						log.info("畅捷----下游上传代付总金额:" + b1.doubleValue());
						b3 = new BigDecimal(merchantinfo.getPoundage());
						log.info("畅捷----系统商户代付单笔手续费:" + b3.doubleValue());
						min = new BigDecimal(merchantinfo.getMinDaiFu());
						log.info("畅捷----系统代付最小金额:" + min.doubleValue());
						max = new BigDecimal(merchantinfo.getMaxDaiFu());
						log.info("畅捷----系统代付最大金额:" + max.doubleValue());
						b2 = new BigDecimal(merchantinfo.getPositionT1());
						log.info("畅捷----系统剩余可用额度:" + b2.doubleValue());

						if (b1.doubleValue() + b3.doubleValue() * 100 > b2
								.doubleValue()) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额高于剩余额度");
							log.info("畅捷**********************代付金额高于剩余额度");
							int i = add(payRequest, merchantinfo, result, "01");
							if (i == 1) {
								log.info("畅捷----添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() < min.doubleValue() * 100) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额小于代付最小金额");
							log.info("畅捷**********************代付金额小于代付最小金额");
							int i = add(payRequest, merchantinfo, result, "01");
							if (i == 1) {
								log.info("畅捷--添加失败订单成功");
							}
							return result;
						}
						if (b1.doubleValue() > max.doubleValue() * 100) {
							result.put("respCode", "06");
							result.put("respMsg", "下单失败,代付金额大于代付最大金额");
							log.info("畅捷**********************代付金额大于代付最大金额");
							int i = add(payRequest, merchantinfo, result, "01");
							if (i == 1) {
								log.info("畅捷--添加失败订单成功");
							}
							return result;
						}
						//
						Map<String, String> mapPay=new HashMap<>();
						mapPay.put("machId", payRequest.getMerchantId());
						mapPay.put("payMoney", payRequest.getAmount());
						int num =pmsMerchantInfoDao.updataT1(mapPay);
						//surplus = b2.subtract(b1).doubleValue()- b3.doubleValue() * 100;
						//merchantinfo.setPositionT1(surplus.toString());
						if (num != 1) {
							log.info("畅捷-九派-扣款失败！！");
							result.put("respCode", "02");
							result.put("respMsg", "代付失败");
							return result;
						}
						//-------------------------
						
						//merchantinfo.setPosition(select(payRequest.getMerchantId()).getPositionT1());
						int i = add(payRequest, select(payRequest.getMerchantId()), result, "200");
						if (i == 1) {
							log.info("畅捷-九派-添加代付扣款订单成功！");
						}
						PmsBusinessPos pmsBusinessPos = selectKey(payRequest
								.getMerchantId());
						if (num == 1) {
							log.info("畅捷-九派-扣款成功！！");
						}
						if (i == 1) {
							log.info("畅捷-九派--代付订单添加成功");
							if("jpPay".equals(payRequest.getType())){
								jpPay(payRequest, result,  merchantinfo, pmsBusinessPos);
							}else if("cjPay".equals(payRequest.getType())){
								sendPay(payRequest, result, merchantinfo,pmsBusinessPos);
							}
						}
					} else {
						throw new RuntimeException(
								"畅捷***系统错误----------------当前商户非正式商户");
					}

				} else {
					throw new RuntimeException("畅捷***系统错误----------------商户不存在");
				}
				break label104;
			}

		} catch (Exception var43) {
			log.error("畅捷*******************************代付错误", var43);
			try {
				throw var43;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		log.info("***********畅捷*********************代付------处理完成");
		return result;
	}

	public void sendPay(PayRequest payRequest,Map<String, String> result,PmsMerchantInfo merchantinfo,PmsBusinessPos pmsBusinessPos){
		
		Map<String, String> map=new HashMap<>();
		
		map.put(BaseConstant.SERVICE, "cjt_dsf");// 鉴权绑卡确认的接口名
		map.put(BaseConstant.VERSION, "1.0");
		//origMap.put(BaseConstant.PARTNER_ID, "200000920146"); //生产
		map.put(BaseConstant.PARTNER_ID, pmsBusinessPos.getBusinessnum()); //生产环境测试商户号
		map.put(BaseConstant.TRADE_DATE, BaseConstant.DATE);
		map.put(BaseConstant.TRADE_TIME, BaseConstant.TIME);
		map.put(BaseConstant.INPUT_CHARSET, BaseConstant.CHARSET);// 字符集
		map.put(BaseConstant.MEMO, "");// 备注
		map.put("TransCode", "T10000"); // 交易码
		map.put("OutTradeNo", payRequest.getOrderId()); // 商户网站唯一订单号
		//map.put("CorpAcctNo", "62170000000000000"); // Y环境企业账号
		//map.put("CorpAcctNo", "62233333333333"); // 企业账号 （T环境）  
		//map.put("CorpAcctNo", "1223332343");  //59  的  1223332343
		map.put("BusinessType", payRequest.getBusinessType()); // 业务类型
		map.put("BankCommonName", payRequest.getBranchBankName()); // 通用银行名称
		map.put("AccountType", "00"); // 账户类型
		map.put("AcctNo", ChanPayUtil.encrypt(payRequest.getAcctNo(), BaseConstant.MERCHANT_PUBLIC_KEY, BaseConstant.CHARSET)); // 对手人账号(此处需要用真实的账号信息)
		map.put("AcctName", ChanPayUtil.encrypt(payRequest.getAcctName(), BaseConstant.MERCHANT_PUBLIC_KEY, BaseConstant.CHARSET)); // 对手人账户名称
		map.put("TransAmt", Double.parseDouble(payRequest.getAmount())/100+"");
		
		//************** 以下信息可空  *******************
		map.put("Province", payRequest.getProvince()==null?"":payRequest.getProvince()); // 省份信息
		map.put("City", payRequest.getCity()==null?"":payRequest.getCity()); // 城市信息
//		map.put("BranchBankName", "中国建设银行股份有限公司兰州新港城支行"); // 对手行行名
//		map.put("BranchBankCode", "105821005604");
//		map.put("DrctBankCode", "105821005604");
//		map.put("Currency", "CNY");
//		map.put("LiceneceType", "01");
		if(payRequest.getLiceneceNo()!=null&&payRequest.getLiceneceNo()!=""){
			map.put("LiceneceNo", ChanPayUtil.encrypt(payRequest.getLiceneceNo(), BaseConstant.MERCHANT_PUBLIC_KEY, BaseConstant.CHARSET));
		}
		if(payRequest.getPhone()!=null&&payRequest.getPhone()!=""){
			map.put("Phone", ChanPayUtil.encrypt(payRequest.getPhone(), BaseConstant.MERCHANT_PUBLIC_KEY, BaseConstant.CHARSET));
		}
		if(payRequest.getMonth()!=null&&payRequest.getYear()!=null){
			map.put("AcctExp", payRequest.getMonth()+"/"+payRequest.getYear());
		}
		if(payRequest.getCvv2()!=null&& payRequest.getCvv2()!=""){
			map.put("AcctCvv2", ChanPayUtil.encrypt(payRequest.getCvv2()==null?"":payRequest.getCvv2(), BaseConstant.MERCHANT_PUBLIC_KEY, BaseConstant.CHARSET));
		}
//		map.put("CorpCheckNo", "201703061413");
//		map.put("Summary", "");
		
		map.put("CorpPushUrl", "");		
		map.put("PostScript", payRequest.getPurpose()==null?"":payRequest.getPurpose());
		
		String data= ChanPayUtil.sendPost(map, BaseConstant.CHARSET,
				pmsBusinessPos.getKek());
		if(data.indexOf("[")==1&&data.indexOf("]")==1){
			int a =data.indexOf("[")-1;
			int b = data.indexOf("]")+1;
			data= data.substring(0, a+"[".length())+data.substring(b, data.length());//利用substring进行字符串截取
		}
         
       System.out.println(data);
		log.info("畅捷返回参数："+JSON.toJSONString(data));
		Map<String, String> maps =JsonUtil.jsonToMap(data);
		
		Map<String, String> m=new HashMap<>();
		try {
		if("S".equals(maps.get("AcceptStatus"))&&"0000".equals(maps.get("PlatformRetCode"))){
			if("000000".equals(maps.get("OriginalRetCode"))){
				UpdateDaifu(payRequest.getOrderId(), "00");
				result.put("respCode", "00");
				result.put("respMsg", "交易受理成功");
				result.put("state", "00");
				result.put("message", maps.get("AppRetMsg")+","+maps.get("OriginalErrorMessage"));
				result.put("amount", payRequest.getAmount());
				result.put("orderId", payRequest.getOrderId());
				result.put("merchantId", payRequest.getMerchantId());
			}else if("111111".equals(maps.get("OriginalRetCode"))){
				UpdateDaifu(payRequest.getOrderId(), "01");
				result.put("respCode", "00");
				result.put("respMsg", "交易受理成功");
				result.put("state", "01");
				result.put("message", maps.get("AppRetMsg")+","+maps.get("OriginalErrorMessage"));
				result.put("amount", payRequest.getAmount());
				result.put("orderId", payRequest.getOrderId());
				result.put("merchantId", payRequest.getMerchantId());
				m.put("payMoney",payRequest.getAmount());
     			m.put("machId", payRequest.getMerchantId());
				int nus = pmsMerchantInfoDao.updataPayT1(m);
				if(nus==1){
					log.info("畅捷***补款成功");
					//surplus = surplus+Double.parseDouble(payRequest.getAmount());
					//merchantinfo.setPosition(surplus.toString());
					PmsMerchantInfo info= select(payRequest.getMerchantId());
					merchantinfo.setPositionT1(info.getPositionT1());
					payRequest.setOrderId(payRequest.getOrderId()+"/A");
					int id =add(payRequest, info, result, "00");
					if(id==1){
						log.info("畅捷代付补单成功");
					}
				}
			}else{
				result.put("respCode", "00");
				result.put("respMsg", "交易受理成功");
				result.put("state", "200");
				result.put("message", maps.get("AppRetMsg")+","+maps.get("OriginalErrorMessage"));
				result.put("amount", payRequest.getAmount());
				result.put("orderId", payRequest.getOrderId());
				result.put("merchantId", payRequest.getMerchantId());
			}
		}else{
			UpdateDaifu(payRequest.getOrderId(), "01");
			result.put("respCode", "00");
			result.put("respMsg", "交易受理成功");
			result.put("state", "01");
			result.put("message", maps.get("RetMsg")+",交易失败");
			result.put("amount", payRequest.getAmount());
			result.put("orderId", payRequest.getOrderId());
			result.put("merchantId", payRequest.getMerchantId());
			m.put("payMoney",payRequest.getAmount());
 			m.put("machId", payRequest.getMerchantId());
			int nus = pmsMerchantInfoDao.updataPayT1(m);
			if(nus==1){
				log.info("畅捷***补款成功");
				//surplus = surplus+Double.parseDouble(payRequest.getAmount());
				//merchantinfo.setPosition(surplus.toString());
				PmsMerchantInfo info= select(payRequest.getMerchantId());
				merchantinfo.setPositionT1(info.getPositionT1());
				payRequest.setOrderId(payRequest.getOrderId()+"/A");
				int id =add(payRequest, info, result, "00");
				if(id==1){
					log.info("畅捷代付补单成功");
				}
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	private int saveOriginAlInfoWxPay1(PayRequest payRequest, String orderid,
			String mercId) throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(orderid);
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("畅捷代付");
		// 想要传服务器要改实体
		info.setBgUrl(payRequest.getUrl());
		info.setUserId(payRequest.getMerchantUuid());
		info.setVerifyId(payRequest.getMerchantCode());
		Double amt = Double.parseDouble(payRequest.getAmount());// 单位分
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
	}
	/**
	 * 添加代付订单
	 * @param payRequest
	 * @param merchantinfo
	 * @param result
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public synchronized int add(PayRequest payRequest,
			PmsMerchantInfo merchantinfo, Map<String, String> result,
			String state) throws Exception {
		log.info("进来添加代付订单了");
		merchantinfo=select(payRequest.getMerchantId());
		BigDecimal b1 = new BigDecimal("0");// 总金额
		int iii = 0;
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		b1 = new BigDecimal(payRequest.getAmount());
		model.setProvince(payRequest.getProvince());
		model.setCity(payRequest.getCity());
		model.setMercId(payRequest.getMerchantId());
		model.setCount("1");
		model.setBatchNo(payRequest.getOrderId());
		model.setIdentity(payRequest.getIdentity() == null ? payRequest
				.getOrderId() : payRequest.getIdentity()+"-"+payRequest.getMerchantUuid());
		model.setAmount(b1.doubleValue() / 100 + "");
		model.setCardno(payRequest.getAcctNo());
		model.setRealname(payRequest.getAcctName());
		if (payRequest.getOrderId().indexOf("/A") != -1) {
			model.setPayamount(b1.doubleValue() / 100 + "");
		} else {
			model.setPayamount("-" + b1.doubleValue() / 100);
		}
		// 联行号
		model.setPmsbankno("");
		if (payRequest.getOrderId().indexOf("/A") != -1) {
			model.setTransactionType("代付补款");
		} else {
			model.setTransactionType("代付");
		}
		if("1".equals(payRequest.getSummary())) {
			model.setPosition(String.valueOf(merchantinfo.getPositionT1()));
			model.setRemarks("T1");
		}else {
			model.setPosition(String.valueOf(merchantinfo.getPosition()));
			model.setRemarks("D0");
		}
		model.setRecordDescription("批次号:" + payRequest.getOrderId() + "订单号："
				+ payRequest.getOrderId() + "错误原因:" + result.get("respMsg"));
		model.setResponsecode(state);
		model.setOagentno("100333");
		if("25".equals(payRequest.getType())) {
			model.setPayCounter("0");
		}else {
			model.setPayCounter(new BigDecimal(merchantinfo.getPoundage())
					.doubleValue() + "");
		}
		PmsDaifuMerchantInfo daifu = pmsDaifuMerchantInfoDao
				.selectByDaifuMerchantInfo(model);
		if (daifu == null) {
			iii = pmsDaifuMerchantInfoDao.insert(model);
			log.info("iii:" + iii);
		}

		return iii;
	}
	@Override
	public int UpdateDaifu(String batchNo, String responsecode)
			throws Exception {
		if (batchNo == null || batchNo == "") {
			return 0;
		}
		log.info("原始数据:" + batchNo);

		PmsDaifuMerchantInfo pdf = new PmsDaifuMerchantInfo();

		log.info("上送的批次号:" + batchNo);

		pdf.setBatchNo(batchNo);
		pdf.setResponsecode(responsecode);
		return pmsDaifuMerchantInfoDao.update(pdf);
	}
	@Override
	public Map<String, String> token(PayRequest payRequest,
			Map<String, String> result) {
		PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
		String url  ="";
		if("10000171".equals(pmsBusinessPos.getBusinessnum())||"10000125".equals(pmsBusinessPos.getBusinessnum())) {
			 url=PayUtil.urlTest;
		}else if("10000173".equals(pmsBusinessPos.getBusinessnum())||"10000160".equals(pmsBusinessPos.getBusinessnum())) {
			 url=PayUtil.url;
		}
		TreeMap<String, Object> signParams = new TreeMap<String, Object>();
		signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
		signParams.put("tokenType", payRequest.getType());
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
		jsonObj.put("tokenType", payRequest.getType());
		String md5 =SignUtil.signByMap(pmsBusinessPos.getKek(), signParams);
		jsonObj.put("sign", md5);
		String tokenJsonReq = jsonObj.toJSONString();
		System.out.println("tokenJsonReq: " + tokenJsonReq);

		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(PayUtil.JSON, tokenJsonReq);
		String payUrl=url+"/gateway/api/getPayToken";
		Request request = new Request.Builder().url(payUrl).post(body).build();
		Response response;
		try {
			response = client.newCall(request).execute();
			String tokenJsonRsp = response.body().string();
			System.out.println("tokenJsonRsp: " + tokenJsonRsp);
			BaseResMessage<TokenRes> res = null;
			if (response.isSuccessful()) {
				res = JSONObject.parseObject(tokenJsonRsp, new TypeReference<BaseResMessage<TokenRes>>() {
				});

				System.out.println("\n接口响应内容：" + res.getData());
				if("000000".equals(res.getCode())) {
					result.put("url", url);
					result.put("code", res.getCode());
					result.put("message", res.getMessage());
					result.put("token", res.getData().getToken());
				}
			} else {
				System.out.println("响应码: " + response.code());
				throw new IOException("Unexpected code " + response.message());
			}
			
			System.out.println("111:"+result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	
	@Override
	public Map<String, String> register(PayRequest payRequest,
			Map<String, String> result) {
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mercid", payRequest.getMerchantId());// 商户编号
			paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
			paramMap.put("oAgentNo", "100333");
			// 商户 网购 业务信息
			Map<String, String> resultMaps = merchantMineDao.queryBusinessInfo(paramMap);

			String quickRateType = resultMaps.get("QUICKRATETYPE");// 快捷支付费率类型

			// 获取o单第三方支付的费率
			AppRateConfig appRate = new AppRateConfig();
			appRate.setRateType(quickRateType);
			appRate.setoAgentNo("100333");
			AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

			paramMap.put("mercid", payRequest.getMerchantId());
			paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
			// 微信支付
			paramMap.put("paymentcode", PaymentCodeEnum.moBaoQuickPay.getTypeCode());

			// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
			AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
					.queryAmountAndStatus(paramMap);
			if (appRateTypeAndAmount == null) {
				result.put("respCode", "01");
				result.put("respMsg", "费率为null");
				return result;
			}
			String rateStr = appRateConfig.getRate(); // 商户费率
			if(Double.parseDouble(rateStr)>Double.parseDouble(payRequest.getDebitRate())||Double.parseDouble(rateStr)>Double.parseDouble(payRequest.getCreditRate())){
				result.put("respCode", "01");
				result.put("respMsg", "费率不能低于系统费率");
				return result;
			}
			
		result.put("merchantId", payRequest.getMerchantId());
		PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
		Map<String, String> map =new HashMap<>();
		log.info("注册参数进来了："+JSON.toJSONString(payRequest));
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(payRequest.getMerchantId());
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
		token(payRequest, map);
		System.out.println("11231::"+map);
		if(Double.parseDouble(merchantList.get(0).getPoundage())>Double.parseDouble(payRequest.getWithdrawDepositSingleFee())/100){
			result.put("respCode", "01");
			result.put("respMsg", "代付费用不能低于系统费用");
			return result;
		}
		String rateCode = payRequest.getRateCode(); // 服务商费率编号
		String merName = merchantList.get(0).getMercName();// 商户名称merchantList.get(0).getMercName()
		String merAbbr = merchantList.get(0).getShortname();// 商户简称merchantList.get(0).getShortname()
		String idCardNoText =payRequest.getLiceneceNo();// 身份证号，3DES加密payRequest.getLiceneceNo()
		String bankAccountNoText = payRequest.getAcctNo();// 银行结算卡卡号，3DES加密payRequest.getAcctNo()
		String phonenoText = payRequest.getPhone(); // 银行卡预留手机号，3DES加密payRequest.getPhone()
		String bankAccountName = payRequest.getAcctName(); // 银行卡户名payRequest.getAcctName()
		Integer bankAccountType = Integer.parseInt(payRequest.getBusinessType()); // 银行卡账户类型 2 对私Integer.parseInt(payRequest.getBusinessType())
		String bankName = payRequest.getBankName(); // 银行名称payRequest.getBankName()
		String bankSubName = payRequest.getBranchBankName(); // 银行支行名称payRequest.getBranchBankName()
		String bankCode = payRequest.getBankCode(); // 银行代码，请见银行代码、简称对照表 payRequest.getBankCode()
		String bankAbbr = payRequest.getBankAbbr(); // 银行代号，请见银行代码、简称对照表payRequest.getBankAbbr()
		String bankChannelNo = payRequest.getPmsbankNo(); // 银行联行号payRequest.getPmsbankNo()
		String bankProvince = payRequest.getProvince(); // 银行所属省payRequest.getProvince()
		String bankCity =payRequest.getCity(); // 银行所属市payRequest.getCity()
		String debitRate = payRequest.getDebitRate(); // 借记卡费率payRequest.getDebitRate()
		String debitCapAmount = payRequest.getDebitCapAmount(); // 借记卡封顶payRequest.getDebitCapAmount()
		String creditRate = payRequest.getCreditRate(); // 信用卡费率payRequest.getCreditRate()
		String creditCapAmount =payRequest.getCreditCapAmount(); // 信用卡封顶payRequest.getCreditCapAmount()
		String withdrawDepositRate =payRequest.getWithdrawDepositRate() ; // 提现费率payRequest.getWithdrawDepositRate()
		String withdrawDepositSingleFee = payRequest.getWithdrawDepositSingleFee(); // 单笔提现手续费payRequest.getWithdrawDepositSingleFee()
		Integer isOrgMerchant =0;
		if("10000173".equals(pmsBusinessPos.getBusinessnum())||"10000171".equals(pmsBusinessPos.getBusinessnum())){
			 isOrgMerchant=1;
		}else if("10000160".equals(pmsBusinessPos.getBusinessnum())||"10000158".equals(pmsBusinessPos.getBusinessnum())){
			 isOrgMerchant=0;
		}else{
			result.put("respCode", "01");
			result.put("respMsg", "请联系运行绑路由");
			return result;
		}
		// 敏感数据3DES加密
		String bankAccountNo = null;
		String phoneno = null;
		String idCardNo = null;
		
			bankAccountNo = EncryptUtil.desEncrypt(bankAccountNoText, pmsBusinessPos.getKek());
			phoneno = EncryptUtil.desEncrypt(phonenoText, pmsBusinessPos.getKek());
			idCardNo = EncryptUtil.desEncrypt(idCardNoText, pmsBusinessPos.getKek());
		if("".equals(map.get("token"))||map.get("token")==null) {
			result.put("respCode", "01");
			result.put("respMsg", "参数有误");
			log.info("未获取到token");
			return result;
		}
		String token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
		TreeMap<String, Object> signParams = new TreeMap<String, Object>();
		signParams.put("token", token);
		signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
		signParams.put("rateCode",rateCode );
		signParams.put("merName", merName);
		signParams.put("merAbbr", merAbbr);
		signParams.put("idCardNo", idCardNoText);
		signParams.put("bankAccountNo", bankAccountNoText);
		signParams.put("phoneno", phonenoText);
		signParams.put("bankAccountName", bankAccountName);
		signParams.put("bankAccountType", bankAccountType);
		signParams.put("bankName", bankName);
		signParams.put("bankSubName", bankSubName);
		signParams.put("bankCode", bankCode);
		signParams.put("bankAbbr", bankAbbr);
		signParams.put("bankChannelNo", bankChannelNo);
		signParams.put("bankProvince", bankProvince);
		signParams.put("bankCity", bankCity);
		signParams.put("debitRate", debitRate);
		signParams.put("debitCapAmount", debitCapAmount);
		signParams.put("creditRate", creditRate);
		signParams.put("creditCapAmount", creditCapAmount);
		signParams.put("withdrawDepositRate", withdrawDepositRate);
		signParams.put("withdrawDepositSingleFee", withdrawDepositSingleFee);
		signParams.put("isOrgMerchant", isOrgMerchant);
		// 构建请求参数
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("token", token);
		jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
		jsonObj.put("rateCode", rateCode);
		jsonObj.put("merName", merName);
		jsonObj.put("merAbbr", merAbbr);
		jsonObj.put("idCardNo", idCardNo);
		jsonObj.put("bankAccountNo", bankAccountNo);
		jsonObj.put("phoneno", phoneno);
		jsonObj.put("bankAccountName", bankAccountName);
		jsonObj.put("bankAccountType", bankAccountType);
		jsonObj.put("bankName", bankName);
		jsonObj.put("bankSubName", bankSubName);
		jsonObj.put("bankCode", bankCode);
		jsonObj.put("bankAbbr", bankAbbr);
		jsonObj.put("bankChannelNo", bankChannelNo);
		jsonObj.put("bankProvince", bankProvince);
		jsonObj.put("bankCity", bankCity);
		jsonObj.put("debitRate", debitRate);
		jsonObj.put("debitCapAmount", debitCapAmount);
		jsonObj.put("creditRate", creditRate);
		jsonObj.put("creditCapAmount", creditCapAmount);
		jsonObj.put("withdrawDepositRate", withdrawDepositRate);
		jsonObj.put("withdrawDepositSingleFee", withdrawDepositSingleFee);
		jsonObj.put("isOrgMerchant", isOrgMerchant);
		jsonObj.put("sign", SignUtil.signByMap(pmsBusinessPos.getKek(), signParams));

		// 接口访问
		String jsonReq = jsonObj.toJSONString();
		System.out.println("jsonReq: " + jsonReq);

		OkHttpClient client = new OkHttpClient();
		client.newBuilder().connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS).writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
				.readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS);
		RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
		String url =map.get("url")+"/v1/merchant/merchantReg";
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();
		
		String jsonRsp = response.body().string();
		System.out.println("jsonRsp: " + jsonRsp);
		
		BaseResMessage<MerchantVo> res = null;
		if (response.isSuccessful()) {
			res = JSONObject.parseObject(jsonRsp, new TypeReference<BaseResMessage<MerchantVo>>() {
			});
			result.put("respCode", "00");
			result.put("respMsg", "请求成功");
			System.out.println("\n接口响应内容：" + res.getData());
			System.out.println(res.getCode());
			if("00".equals(res.getCode())){
				PmsWeixinMerchartInfo merchartInfo =new PmsWeixinMerchartInfo();
				merchartInfo.setAccount(res.getData().getMerchantCode());//账号
				merchartInfo.setMerchartId(payRequest.getMerchantId());
				merchartInfo.setRateCode(rateCode);
				merchartInfo.setMerchartName(merName);
				merchartInfo.setMerchartNameSort(merAbbr);
				merchartInfo.setCertNo(idCardNoText);//证件号
				merchartInfo.setCardNo(bankAccountNoText);//卡号
				merchartInfo.setRealName(bankAccountName);//姓名
				merchartInfo.setMobile(phonenoText);//手机号
				merchartInfo.setAccountType(bankAccountType.toString());//账户类型
				merchartInfo.setBankName(bankSubName);//开户行
				merchartInfo.setPmsBankNo(bankChannelNo);//联行号
				merchartInfo.setProvince(bankProvince);//省份
				merchartInfo.setCity(bankCity);//城市
				merchartInfo.setDebitRate(debitRate);//借记卡费率
				merchartInfo.setDebitCapAmount(debitCapAmount);//借记卡封顶值
				merchartInfo.setCreditRate(creditRate);//信用卡费率
				merchartInfo.setCreditCapAmount(creditCapAmount);//信用卡封顶值
				merchartInfo.setRateCode(rateCode);//费率编号
				merchartInfo.setWithdrawDepositRate(withdrawDepositRate);//提现费率
				merchartInfo.setWithdrawDepositSingleFee(withdrawDepositSingleFee);//提现单笔手续费
				merchartInfo.setBankCode(bankCode);//银行代码
				merchartInfo.setBankName(bankName);//银行代号
				merchartInfo.setPassword(res.getData().getMerchantUuid());
				merchartInfo.setWalletD0("0");
				merchartInfo.setWalletT1("1");
				merchartInfo.setoAgentNo("100333");
				int i =weixinService.updateRegister(merchartInfo);
				log.info("ii:"+i);
				result.put("code", "00");
				result.put("message", res.getMessage());
				result.put("merchantCode", res.getData().getMerchantCode());
				result.put("merchantUuid", res.getData().getMerchantUuid());
			}else{
				result.put("code", "01");
				result.put("message", res.getMessage());
			}
			
		} else {
			System.out.println("响应码: " + response.code());
			result.put("code", "01");
			result.put("message", response.message());
			throw new IOException("Unexpected code " + response.message());
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Map<String, String> openMessages(PayRequest payRequest,
			Map<String, String> result) {
		Map<String, String> map =new HashMap<>();
		log.info("注册参数进来了："+JSON.toJSONString(payRequest));
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(payRequest.getMerchantId());
		token(payRequest, map);
		result.put("merchantId", payRequest.getMerchantId());
		try {
			PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			String token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
			String requestId = UUID.randomUUID().toString().replace("-", ""); // 请求流水号，每次请求保持唯一
			String orderId = payRequest.getOrderId(); // 商户订单号，商户系统保证唯一
			String merchantCode = payRequest.getMerchantCode(); // 合作商户编号，合作商户的唯一标识
			String rateCode = payRequest.getRateCode();// 合作商户费率编号
			String accountName = payRequest.getAcctName(); // 银行卡姓名
			String cardNoText = payRequest.getAcctNo(); // 银行卡卡号，3DES加密
			Integer cardType = Integer.parseInt(payRequest.getAccountType()); // 银行卡类型，1-借记卡 2-信用卡
			String certType = "01"; // 银行预留证件类型 01、身份证
			String certNoText = payRequest.getLiceneceNo(); // 银行预留证件号码，3DES加密
			String phonenoText = payRequest.getPhone(); // 银行预留手机号，3DES加密
			String bankCode = payRequest.getBankCode(); // 银行代码，请见银行代码、简称对照表
			String bankAbbr = payRequest.getBankAbbr(); // 银行代号，请见银行代码、简称对照表
			// 敏感数据3DES加密
			String cardNo = null;
			String phoneno = null;
			String certNo = null;
			try {
				cardNo = EncryptUtil.desEncrypt(cardNoText, pmsBusinessPos.getKek());
				phoneno = EncryptUtil.desEncrypt(phonenoText, pmsBusinessPos.getKek());
				certNo = EncryptUtil.desEncrypt(certNoText, pmsBusinessPos.getKek());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 构建签名参数
			TreeMap<String, Object> signParams = new TreeMap<String, Object>();
			signParams.put("token", token);
			signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
			signParams.put("requestId", requestId);
			signParams.put("orderId", orderId);
			signParams.put("merchantCode", merchantCode);
			signParams.put("rateCode", rateCode);
			signParams.put("accountName", accountName);
			signParams.put("cardNo", cardNoText);
			signParams.put("cardType", cardType);
			signParams.put("certType", certType);
			signParams.put("certNo", certNoText);
			signParams.put("phoneno", phonenoText);
			signParams.put("bankCode", bankCode);
			signParams.put("bankAbbr", bankAbbr);

			// 构建请求参数
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("token", token);
			jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
			jsonObj.put("requestId", requestId);
			jsonObj.put("orderId", orderId);
			jsonObj.put("merchantCode", merchantCode);
			jsonObj.put("rateCode", rateCode);
			jsonObj.put("accountName", accountName);
			jsonObj.put("cardNo", cardNo);
			jsonObj.put("cardType", cardType);
			jsonObj.put("certType", certType);
			jsonObj.put("certNo", certNo);
			jsonObj.put("phoneno", phoneno);
			jsonObj.put("bankCode", bankCode);
			jsonObj.put("bankAbbr", bankAbbr);
			jsonObj.put("sign", SignUtil.signByMap(pmsBusinessPos.getKek(), signParams));

			// 接口访问
			String jsonReq = jsonObj.toJSONString();
			System.out.println("jsonReq: " + jsonReq);
			String url =map.get("url")+"/gateway/api/openCardSMS";
			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
			Request request = new Request.Builder().url(url).post(body).build();
			Response response = client.newCall(request).execute();

			String jsonRsp = response.body().string();
			System.out.println("jsonRsp: " + jsonRsp);
			PmsDaifuMerchantInfo daifuMerchantInfo =new PmsDaifuMerchantInfo();
			daifuMerchantInfo.setMercId(payRequest.getMerchantId());
			BaseResMessage<PageOpenCardVo> res = null;
			result.put("orderId", orderId);
			result.put("merchantCode",merchantCode);
			result.put("requestId", requestId);
			if (response.isSuccessful()) {
				res = JSONObject.parseObject(jsonRsp, new TypeReference<BaseResMessage<PageOpenCardVo>>() {
				});
				result.put("respCode", "00");
				System.out.println("\n接口响应内容：" + res.getData());
				if("000000".equals(res.getCode())){
					result.put("respMsg", "请求成功");
					if("2".equals(res.getData().getActivateStatus().toString())){
						daifuMerchantInfo.setResponsecode("2");
						result.put("code", "00");
						result.put("message", "开通成功");
					}else if("0".equals(res.getData().getActivateStatus().toString())){
						daifuMerchantInfo.setResponsecode("0");
						result.put("code", "02");
						result.put("message", "未开通");
					}else if("1".equals(res.getData().getActivateStatus().toString())){
						daifuMerchantInfo.setResponsecode("1");
						result.put("code", "03");
						result.put("message", "等待开通");
					}else if("4".equals(res.getData().getActivateStatus().toString())){
						daifuMerchantInfo.setResponsecode("4");
						result.put("code", "04");
						result.put("message", "绑卡状态失效");
					}else if("3".equals(res.getData().getActivateStatus().toString())){
						daifuMerchantInfo.setResponsecode("3");
						result.put("code", "01");
						result.put("message", "开通失败");
					}
				}else{
					daifuMerchantInfo.setResponsecode("3");
					result.put("code", "01");
					result.put("message",res.getMessage());
					result.put("respMsg", "请求成功");
				}
				
			} else {
				daifuMerchantInfo.setResponsecode("3");
				result.put("respCode", "01");
				result.put("respMsg", "请求失败");
				System.out.println("响应码: " + response.code());
				throw new IOException("Unexpected code " + response.message());
			}
			daifuMerchantInfo.setBatchNo(orderId);
			daifuMerchantInfo.setCardno(cardNoText);
			daifuMerchantInfo.setProvince(certNoText);
			daifuMerchantInfo.setIdentity(requestId);
			daifuMerchantInfo.setCity(phonenoText);
			daifuMerchantInfo.setRealname(accountName);
			daifuMerchantInfo.setTransactionType("商户侧开通短信");
			daifuMerchantInfo.setRemarks("D0");
			daifuMerchantInfo.setOagentno("100333");
			daifuMerchantInfo.setRecordDescription("合作商编号:"+merchantCode+",费率编号:"+rateCode);
			int i=pmsDaifuMerchantInfoDao.insert(daifuMerchantInfo);
			if(i==1){
				log.info("商户侧开通短信定订单添加成功");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	@Override
	public Map<String, String> OpenCard(PayRequest payRequest,
			Map<String, String> result) {
		Map<String, String> map =new HashMap<>();
		log.info("开通进来了："+JSON.toJSONString(payRequest));
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(payRequest.getMerchantId());
		token(payRequest, map);
		PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
		String key =pmsBusinessPos.getKek();
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			String token = EncryptUtil.desDecrypt(map.get("token"), key);
			
			// 请求参数
			String requestId = UUID.randomUUID().toString().replace("-", ""); // 请求流水号，每次请求保持唯一
			String orderId = payRequest.getOrderId(); // 开通短信订单号，商户系统保证唯一
			String merchantCode = payRequest.getMerchantCode(); // 合作商户编号，合作商户的唯一标识
			String rateCode = payRequest.getRateCode();// 合作商户费率编号
			String accountName = payRequest.getAcctName(); // 银行卡姓名
			String cardNoText = payRequest.getAcctNo(); // 银行卡卡号，3DES加密
			Integer cardType = Integer.parseInt(payRequest.getAccountType()); // 银行卡类型，1-借记卡 2-信用卡
			String certType = "01"; // 银行预留证件类型 01、身份证
			String certNoText = payRequest.getLiceneceNo(); // 银行预留证件号码，3DES加密
			String phonenoText = payRequest.getPhone(); // 银行预留手机号，3DES加密
			String cvn2Text = payRequest.getCvv2(); // 银行卡背面的cvn2三位数字，3DES加密（2-信用卡必填）
			String expired = payRequest.getYear()+payRequest.getMonth(); // 银行卡有效期，年在前、月在后（2-信用卡必填）
			String bankCode = payRequest.getBankCode(); // 银行代码，请见银行代码、简称对照表
			String bankAbbr =payRequest.getBankAbbr(); // 银行代号，请见银行代码、简称对照表
			String smsCode = payRequest.getSmsCode(); // 短信验证码，6位短信验证码

			// 敏感数据3DES加密
			String cardNo = null;
			String phoneno = null;
			String certNo = null;
			String cvn2 = null;
			try {
				cardNo = EncryptUtil.desEncrypt(cardNoText, key);
				phoneno = EncryptUtil.desEncrypt(phonenoText, key);
				certNo = EncryptUtil.desEncrypt(certNoText, key);
				if (StringUtils.isNotEmpty(cvn2Text))
					cvn2 = EncryptUtil.desEncrypt(cvn2Text, key);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 构建签名参数
			TreeMap<String, Object> signParams = new TreeMap<String, Object>();
			signParams.put("token", token);
			signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
			signParams.put("requestId", requestId);
			signParams.put("orderId", orderId);
			signParams.put("merchantCode", merchantCode);
			signParams.put("rateCode", rateCode);
			signParams.put("accountName", accountName);
			signParams.put("cardNo", cardNoText);
			signParams.put("cardType", cardType);
			signParams.put("certType", certType);
			signParams.put("certNo", certNoText);
			signParams.put("phoneno", phonenoText);
			signParams.put("cvn2", cvn2Text);
			signParams.put("expired", expired);
			signParams.put("bankCode", bankCode);
			signParams.put("bankAbbr", bankAbbr);
			signParams.put("smsCode", smsCode);

			// 构建请求参数
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("token", token);
			jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
			jsonObj.put("requestId", requestId);
			jsonObj.put("orderId", orderId);
			jsonObj.put("merchantCode", merchantCode);
			jsonObj.put("rateCode", rateCode);
			jsonObj.put("accountName", accountName);
			jsonObj.put("cardNo", cardNo);
			jsonObj.put("cardType", cardType);
			jsonObj.put("certType", certType);
			jsonObj.put("certNo", certNo);
			jsonObj.put("phoneno", phoneno);
			jsonObj.put("cvn2", cvn2);
			jsonObj.put("expired", expired);
			jsonObj.put("bankCode", bankCode);
			jsonObj.put("bankAbbr", bankAbbr);
			jsonObj.put("smsCode", smsCode);
			jsonObj.put("sign", SignUtil.signByMap(key, signParams));

			// 接口访问
			String jsonReq = jsonObj.toJSONString();
			System.out.println("jsonReq: " + jsonReq);
			String url =map.get("url")+"/gateway/api/backOpenCard";
			OkHttpClient client = new OkHttpClient();
			RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
			Request request = new Request.Builder().url(url).post(body).build();
			Response response = client.newCall(request).execute();

			String jsonRsp = response.body().string();
			System.out.println("jsonRsp: " + jsonRsp);
			result.put("orderId", orderId);
			result.put("merchantCode",merchantCode);
			result.put("requestId", requestId);
			BaseResMessage<PageOpenCardVo> res = null;
			if (response.isSuccessful()) {
				res = JSONObject.parseObject(jsonRsp, new TypeReference<BaseResMessage<PageOpenCardVo>>() {
				});
				result.put("respCode", "00");
				if("000000".equals(res.getCode())){
					result.put("respMsg", "请求成功");
					int i = 0 ;
					if("2".equals(res.getData().getActivateStatus().toString())){
						i =UpdateDaifu(orderId, "2");
						result.put("code", "00");
						result.put("message", "开通成功");
					}else if("0".equals(res.getData().getActivateStatus().toString())){
						i=UpdateDaifu(orderId, "0");
						result.put("code", "02");
						result.put("message", "未开通");
					}else if("1".equals(res.getData().getActivateStatus().toString())){
						i=UpdateDaifu(orderId, "1");
						result.put("code", "03");
						result.put("message", "等待开通");
					}else if("4".equals(res.getData().getActivateStatus().toString())){
						i=UpdateDaifu(orderId, "4");
						result.put("code", "04");
						result.put("message", "绑卡状态失效");
					}else if("3".equals(res.getData().getActivateStatus().toString())){
						i=UpdateDaifu(orderId, "03");
						result.put("code", "01");
						result.put("message", "开通失败");
					}
					if(i==1){
						log.info("商户侧绑卡开通成功");
					}
				}else{
					result.put("code", "01");
					result.put("message", res.getMessage());
					result.put("respMsg", "请求成功");
				}
				
				
				System.out.println("\n接口响应内容：" + res.getData());
			} else {
				System.out.println("响应码: " + response.code());
				result.put("respCode", "01");
				result.put("respMsg", "请求失败");
				throw new IOException("Unexpected code " + response.message());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Map<String, String> update(PayRequest payRequest,
			Map<String, String> result) {
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("mercid", payRequest.getMerchantId());// 商户编号
			paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
			paramMap.put("oAgentNo", "100333");
			// 商户 网购 业务信息
			Map<String, String> resultMaps = merchantMineDao.queryBusinessInfo(paramMap);
	
			String quickRateType = resultMaps.get("QUICKRATETYPE");// 快捷支付费率类型
	
			// 获取o单第三方支付的费率
			AppRateConfig appRate = new AppRateConfig();
			appRate.setRateType(quickRateType);
			appRate.setoAgentNo("100333");
			AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);
	
			paramMap.put("mercid", payRequest.getMerchantId());
			paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
			// 微信支付
			paramMap.put("paymentcode", PaymentCodeEnum.moBaoQuickPay.getTypeCode());
	
			// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
			AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
					.queryAmountAndStatus(paramMap);
			if (appRateTypeAndAmount == null) {
				result.put("respCode", "01");
				result.put("respMsg", "费率为null");
				return result;
			}
			String rateStr = appRateConfig.getRate(); // 商户费率
			result.put("merchantId", payRequest.getMerchantId());
			Map<String, String> map =new HashMap<>();
			log.info("修改参数进来了："+JSON.toJSONString(payRequest));
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(payRequest.getMerchantId());
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			token(payRequest, map);
			if("1".equals(payRequest.getChangeType())) {
				if(Double.parseDouble(rateStr)>Double.parseDouble(payRequest.getDebitRate())||Double.parseDouble(rateStr)>Double.parseDouble(payRequest.getCreditRate())){
					result.put("respCode", "01");
					result.put("respMsg", "费率不能低于系统费率");
					return result;
				}
			}else if("1".equals(payRequest.getChangeType())){
				if(Double.parseDouble(merchantList.get(0).getPoundage())>Double.parseDouble(payRequest.getWithdrawDepositSingleFee())/100){
					result.put("respCode", "01");
					result.put("respMsg", "代付费用不能低于系统费用");
					return result;
				}
			}
			log.info("laile");
			PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
			log.info("pmsBusinessPos:"+JSON.toJSONString(pmsBusinessPos));
			String token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
			log.info("token:"+token);
			// 请求参数
			String merchantCode = payRequest.getMerchantCode(); // 合作商户编号，合作商户的唯一标识
			Integer changeType = Integer.parseInt(payRequest.getChangeType()); // 变更类型 1 交易费率变更 2 银行卡信息变更 3 交易费率新增 4 提现费率变更
			// 2 银行卡信息变更 填写-------------------------------------------start
			String bankAccountNoText = ""; // 银行结算卡卡号，3DES加密
			String phonenoText = ""; // 银行卡预留手机号，3DES加密
			String bankName = ""; // 银行名称
			String bankSubName = ""; // 银行支行名称
			String bankCode = ""; // 银行代码，请见银行代码、简称对照表
			String bankAbbr = ""; // 银行代号，请见银行代码、简称对照表
			String bankChannelNo = ""; // 银行联行号
			String bankProvince = ""; // 银行所属省
			String bankCity = ""; // 银行所属市
			// ---------------------------------------------------------------end

			// 1 交易费率变更、3 交易费率新增 填写-------------------------------start
			String rateCode = ""; // 商户费率编号
			String debitRate = ""; // 借记卡费率
			String debitCapAmount = ""; // 借记卡封顶
			String creditRate = ""; // 信用卡费率
			String creditCapAmount = ""; // 信用卡封顶
			// ---------------------------------------------------------------end

			// 4 提现费率变更 填写----------------------------------------------start
			String withdrawDepositRate = ""; // 提现费率
			String withdrawDepositSingleFee = ""; // 单笔提现手续费
			// ---------------------------------------------------------------end

			
			PmsWeixinMerchartInfo model =new PmsWeixinMerchartInfo();
			model.setMerchartId(payRequest.getMerchantId());
			model.setAccount(payRequest.getMerchantCode());
			log.info("0000");
			List<PmsWeixinMerchartInfo>  merchartInfo =weixinService.selectlist(model);
			log.info("1111");
			if(merchartInfo.size() >0){
				log.info("2222");
				PmsWeixinMerchartInfo merchartInfo2=merchartInfo.get(0);
				if("3".equals(payRequest.getChangeType())){
					log.info("3333");
					model.setRateCode(payRequest.getRateCode());
					PmsWeixinMerchartInfo merchartInfos =weixinService.selectByEntity(model);
					log.info("4444");
					if(merchartInfos !=null){
						result.put("respCode", "01");
						result.put("respMsg", "费率已有");
						return result;
					}else{
						 rateCode = payRequest.getRateCode(); // 商户费率编号
						 debitRate = payRequest.getDebitRate(); // 借记卡费率
						 debitCapAmount = payRequest.getDebitCapAmount(); // 借记卡封顶
						 creditRate = payRequest.getCreditRate(); // 信用卡费率
						 creditCapAmount = payRequest.getCreditCapAmount(); // 信用卡封顶
						 merchartInfo2.setRateCode(payRequest.getRateCode());
						 merchartInfo2.setDebitRate(payRequest.getDebitRate());
						 merchartInfo2.setDebitCapAmount(payRequest.getDebitCapAmount());
						 merchartInfo2.setCreditRate(payRequest.getCreditRate());
						 merchartInfo2.setCreditCapAmount(payRequest.getCreditCapAmount());
						 
					}
				}else if("1".equals(payRequest.getChangeType())){
					model.setRateCode(payRequest.getRateCode());
					log.info("5555");
					PmsWeixinMerchartInfo merchartInfos =weixinService.selectByEntity(model);
					log.info("6666");
					if(merchartInfos !=null){
					 rateCode = payRequest.getRateCode(); // 商户费率编号
					 debitRate = payRequest.getDebitRate(); // 借记卡费率
					 debitCapAmount = payRequest.getDebitCapAmount(); // 借记卡封顶
					 creditRate = payRequest.getCreditRate(); // 信用卡费率
					 creditCapAmount = payRequest.getCreditCapAmount(); // 信用卡封顶
					 
					 merchartInfo2.setRateCode(payRequest.getRateCode());
					 merchartInfo2.setDebitRate(payRequest.getDebitRate());
					 merchartInfo2.setDebitCapAmount(payRequest.getDebitCapAmount());
					 merchartInfo2.setCreditRate(payRequest.getCreditRate());
					 merchartInfo2.setCreditCapAmount(payRequest.getCreditCapAmount());
					 
					}else{
						result.put("respCode", "01");
						result.put("respMsg", "费率没有请添加");
						return result;
					}
				}else if("2".equals(payRequest.getChangeType())|"4".equals(payRequest.getChangeType())){
						 bankAccountNoText = payRequest.getAcctNo(); // 银行结算卡卡号，3DES加密
						 phonenoText = payRequest.getPhone(); // 银行卡预留手机号，3DES加密
						 bankName = payRequest.getBankName(); // 银行名称
						 bankSubName = payRequest.getBranchBankName(); // 银行支行名称
						 bankCode = payRequest.getBankCode(); // 银行代码，请见银行代码、简称对照表
						 bankAbbr = payRequest.getBankAbbr(); // 银行代号，请见银行代码、简称对照表
						 bankChannelNo = payRequest.getPmsbankNo(); // 银行联行号
						 bankProvince = payRequest.getProvince(); // 银行所属省
						 bankCity = payRequest.getCity(); // 银行所属市
						 withdrawDepositRate = payRequest.getWithdrawDepositRate(); // 提现费率
						 withdrawDepositSingleFee = payRequest.getWithdrawDepositSingleFee(); // 单笔提现手续费
						 merchartInfo2.setCardNo(payRequest.getAcctNo());
						 merchartInfo2.setMobile(payRequest.getPhone());
						 merchartInfo2.setBankName(payRequest.getBankName()+","+payRequest.getBranchBankName());
						 merchartInfo2.setBankCode(payRequest.getBankCode());//银行代码
						 merchartInfo2.setBankName(payRequest.getBankAbbr());//银行代号
						 merchartInfo2.setPmsBankNo(payRequest.getPmsbankNo());
						 merchartInfo2.setProvince(payRequest.getProvince());
						 merchartInfo2.setCity(payRequest.getCity());
						 merchartInfo2.setWithdrawDepositRate(withdrawDepositRate);
						 merchartInfo2.setWithdrawDepositSingleFee(withdrawDepositSingleFee);
				}
			
			// 敏感数据3DES加密
			String bankAccountNo = null;
			String phoneno = null;
			try {
				bankAccountNo = EncryptUtil.desEncrypt(bankAccountNoText, pmsBusinessPos.getKek());
				phoneno = EncryptUtil.desEncrypt(phonenoText, pmsBusinessPos.getKek());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 构建签名参数
			TreeMap<String, Object> signParams = new TreeMap<String, Object>();
			signParams.put("token", token);
			signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
			signParams.put("merchantCode", merchantCode);
			signParams.put("changeType", changeType);
			signParams.put("bankAccountNo", bankAccountNoText);
			signParams.put("phoneno", phonenoText);
			signParams.put("bankName", bankName);
			signParams.put("bankSubName", bankSubName);
			signParams.put("bankCode", bankCode);
			signParams.put("bankAbbr", bankAbbr);
			signParams.put("bankChannelNo", bankChannelNo);
			signParams.put("bankProvince", bankProvince);
			signParams.put("bankCity", bankCity);
			signParams.put("rateCode", rateCode);
			signParams.put("debitRate", debitRate);
			signParams.put("debitCapAmount", debitCapAmount);
			signParams.put("creditRate", creditRate);
			signParams.put("creditCapAmount", creditCapAmount);
			signParams.put("withdrawDepositRate", withdrawDepositRate);
			signParams.put("withdrawDepositSingleFee", withdrawDepositSingleFee);

			// 构建请求参数
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("token", token);
			jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
			jsonObj.put("merchantCode", merchantCode);
			jsonObj.put("changeType", changeType);
			jsonObj.put("bankAccountNo", bankAccountNo);
			jsonObj.put("phoneno", phoneno);
			jsonObj.put("bankName", bankName);
			jsonObj.put("bankSubName", bankSubName);
			jsonObj.put("bankCode", bankCode);
			jsonObj.put("bankAbbr", bankAbbr);
			jsonObj.put("bankChannelNo", bankChannelNo);
			jsonObj.put("bankProvince", bankProvince);
			jsonObj.put("bankCity", bankCity);
			jsonObj.put("rateCode", rateCode);
			jsonObj.put("debitRate", debitRate);
			jsonObj.put("debitCapAmount", debitCapAmount);
			jsonObj.put("creditRate", creditRate);
			jsonObj.put("creditCapAmount", creditCapAmount);
			jsonObj.put("withdrawDepositRate", withdrawDepositRate);
			jsonObj.put("withdrawDepositSingleFee", withdrawDepositSingleFee);
			jsonObj.put("sign", SignUtil.signByMap(pmsBusinessPos.getKek(), signParams));

			// 接口访问
			String jsonReq = jsonObj.toJSONString();
			System.out.println("jsonReq: " + jsonReq);
			String url =map.get("url")+"/v1/merchant/merchantChange";
			OkHttpClient client = new OkHttpClient();
			client.newBuilder().connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS).writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
					.readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS);
			RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
			Request request = new Request.Builder().url(url).post(body).build();
			Response response = client.newCall(request).execute();

			String jsonRsp = response.body().string();
			System.out.println("jsonRsp: " + jsonRsp);

			if (response.isSuccessful()) {
				System.out.println("\n接口响应内容：" + jsonRsp);
				JSONObject json =JSONObject.parseObject(jsonRsp);
				result.put("merchantId",payRequest.getMerchantId());
				result.put("merchantCode",payRequest.getMerchantCode());
				if("00".equals(json.getString("code"))){
					result.put("respCode","00");
					result.put("respMsg", json.getString("message"));
					if("3".equals(payRequest.getChangeType())){
						int i =weixinService.updateRegister(merchartInfo2);
						if(i==1){
							log.info("添加费率成功");
						}else {
							throw new SQLException("添加费率失败" + response.message());
						}
					}else{
						int i =weixinService.updateByPrimaryKeySelective(merchartInfo2);
						if(i==1){
							log.info("修改成功");
						}else {
							throw new SQLException("修改失败" + response.message());
						}
					}
				}else{
					result.put("respCode","01");
					result.put("respMsg", json.getString("message"));
				}
			} else {
				System.out.println("响应码: " + response.code());
				throw new IOException("Unexpected code " + response.message());
			}
			}else{
				result.put("respCode", "01");
				result.put("respMsg", "此商户未注册");
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * 支付短信
	 */
	@Override
	public Map<String, String> payCode(PayRequest payRequest, Map<String, String> result) {
		try {
		Map<String, String> map =new HashMap<>();
		log.info("注册参数进来了："+JSON.toJSONString(payRequest));
		String mercId=payRequest.getMerchantId();
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
				.searchList(merchantinfo);
		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			// 正式商户
			merchantinfo = merchantList.get(0);

		 String	oAgentNo = merchantinfo.getoAgentNo();//
		 
		 if("60".equals(merchantinfo.getMercSts())) {

				// 判断是否为正式商户

				// 校验商户金额限制
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
				paramMap.put("businesscode",
						TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
				paramMap.put("oAgentNo", oAgentNo);
				// 商户 网购 业务信息
				Map<String, String> resultMap = merchantMineDao
						.queryBusinessInfo(paramMap);

				String quickRateType = resultMap.get("QUICKRATETYPE");// 快捷支付费率类型

				// 获取o单第三方支付的费率
				AppRateConfig appRate = new AppRateConfig();
				appRate.setRateType(quickRateType);
				appRate.setoAgentNo(oAgentNo);
				AppRateConfig appRateConfig = appRateConfigDao
						.getByRateTypeAndoAgentNo(appRate);

				paramMap.put("mercid", mercId);
				paramMap.put("businesscode",
						TradeTypeEnum.merchantCollect.getTypeCode());
				// 微信支付
				paramMap.put("paymentcode",
						PaymentCodeEnum.moBaoQuickPay.getTypeCode());

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
							.moduleVerifyOagent(
									TradeTypeEnum.merchantCollect, oAgentNo);

					if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
						if (StringUtils.isEmpty(resultInfoForOAgentNo
								.getMsg())) {
							log.error("此功能暂时关闭!");
							result.put("respCode", "05");
							result.put("respMsg", "此功能暂时关闭");
						} else {
							log.error(resultInfoForOAgentNo.getMsg());
							result.put("respCode", "05");
							result.put("respMsg",
									resultInfoForOAgentNo.getMsg());
						}
					} else {

						if ("1".equals(status)) {

							// 判断支付方式时候开通总开关
							ResultInfo payCheckResult = null;

							payCheckResult = payTypeControlDao.checkLimit(
									oAgentNo,
									PaymentCodeEnum.moBaoQuickPay
											.getTypeCode());

							if (!payCheckResult.getErrCode().equals("0")) {
								// 支付方式时候开通总开关 禁用
								result.put("respCode", "07");
								result.put("respMsg", "此支付方式暂时关闭");
								log.info("此支付方式暂时关闭");
							} else {

								BigDecimal payAmt = new BigDecimal(
										payRequest.getAmount());// 收款金额
								// 判读 交易金额是不是在欧单区间控制之内
								ResultInfo resultInfo = amountLimitControlDao
										.checkLimit(
												oAgentNo,
												payAmt,
												TradeTypeEnum.merchantCollect
														.getTypeCode());
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
											.payTypeVerifyMer(
													PaymentCodeEnum.moBaoQuickPay,
													mercId);

									if ("0".equals(resultinfo.getErrCode())) {
										if ("0".equals(payStatus)) {
											// 有效
											// MIN_AMOUNT,MAX_AMOUNT ,RATE
											// ,STATUS
											String rateStr = appRateConfig
													.getRate(); // 商户费率
																// RATE

											BigDecimal min_amount = new BigDecimal(
													appRateTypeAndAmount
															.getMinAmount());// 最低收款金额
											// MIN_AMOUNT
											BigDecimal max_amount = new BigDecimal(
													appRateTypeAndAmount
															.getMaxAmount());// 最高收款金额
											// MAX_AMOUNT

											if (min_amount
													.compareTo(payAmt) != 1) {// 判断收款金额是否大于最低收款金额
												// 大于等于执行
												// 小于不执行
												if (payAmt
														.compareTo(max_amount) != 1) {
													// 组装报文
													log.info("来了111111111111");

													token(payRequest, map);
													PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
													String token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
														// 请求参数
														String requestId = UUID.randomUUID().toString().replace("-", ""); // 请求流水号，每次请求保持唯一
														String orderId = payRequest.getOrderId(); // 商户订单号，商户系统保证唯一
														String merchantCode = payRequest.getMerchantCode(); // 合作商户编号，合作商户的唯一标识
														String rateCode = payRequest.getRateCode();// 合作商户费率编号
														String orderAmountText = payRequest.getAmount(); // 订单金额，订单金额以分为单位，3DES加密
														String cardNoText = payRequest.getAcctNo(); // 银行卡卡号，3DES加密
														String accountName = payRequest.getAcctName(); // 银行卡姓名
														Integer cardType = Integer.parseInt(payRequest.getAccountType()); // 银行卡类型，1-借记卡 2-信用卡
														String phonenoText = payRequest.getPhone(); // 银行预留手机号，3DES加密
														String certType = "01"; // 银行预留证件类型 01、身份证
														String certNoText = payRequest.getLiceneceNo(); // 银行预留证件号码，3DES加密
														String bankCode = payRequest.getBankCode(); // 银行代码，请见银行代码、简称对照表
														String bankAbbr = payRequest.getBankAbbr(); // 银行代号，请见银行代码、简称对照表
														String key =pmsBusinessPos.getKek();
														// 敏感数据3DES加密
														String orderAmount = null;
														String cardNo = null;
														String phoneno = null;
														String certNo = null;
														orderAmount = EncryptUtil.desEncrypt(orderAmountText, key);
														cardNo = EncryptUtil.desEncrypt(cardNoText, key);
														phoneno = EncryptUtil.desEncrypt(phonenoText, key);
														certNo = EncryptUtil.desEncrypt(certNoText, key);

														// 构建签名参数
														TreeMap<String, Object> signParams = new TreeMap<String, Object>();
														signParams.put("token", token);
														signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
														signParams.put("requestId", requestId);
														signParams.put("orderId", orderId);
														signParams.put("merchantCode", merchantCode);
														signParams.put("rateCode", rateCode);
														signParams.put("orderAmount", orderAmountText);
														signParams.put("cardNo", cardNoText);
														signParams.put("accountName", accountName);
														signParams.put("cardType", cardType);
														signParams.put("phoneno", phonenoText);
														signParams.put("certType", certType);
														signParams.put("certNo", certNoText);
														signParams.put("bankCode", bankCode);
														signParams.put("bankAbbr", bankAbbr);

														// 构建请求参数
														JSONObject jsonObj = new JSONObject();
														jsonObj.put("token", token);
														jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
														jsonObj.put("requestId", requestId);
														jsonObj.put("orderId", orderId);
														jsonObj.put("merchantCode", merchantCode);
														jsonObj.put("rateCode", rateCode);
														jsonObj.put("orderAmount", orderAmount);
														jsonObj.put("cardNo", cardNo);
														jsonObj.put("accountName", accountName);
														jsonObj.put("cardType", cardType);
														jsonObj.put("phoneno", phoneno);
														jsonObj.put("certType", certType);
														jsonObj.put("certNo", certNo);
														jsonObj.put("bankCode", bankCode);
														jsonObj.put("bankAbbr", bankAbbr);
														jsonObj.put("sign", SignUtil.signByMap(key, signParams));

														// 接口访问
														String jsonReq = jsonObj.toJSONString();
														System.out.println("jsonReq: " + jsonReq);
														String url =map.get("url")+"/gateway/api/consumeSMS";
														OkHttpClient client = new OkHttpClient();
														RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
														Request request = new Request.Builder().url(url).post(body).build();
														Response response = client.newCall(request).execute();

														String jsonRsp = response.body().string();
														System.out.println("jsonRsp: " + jsonRsp);

														BaseResMessage<ConsumeSMSVo> res = null;
														if (response.isSuccessful()) {
															res = JSONObject.parseObject(jsonRsp, new TypeReference<BaseResMessage<ConsumeSMSVo>>() {
															});
															
															if("000000".equals(res.getCode())) {
																result.put("respCode", "00");
																result.put("respMsg", "请求成功");
																result.put("orderId",payRequest.getOrderId());
																result.put("merchantId", payRequest.getMerchantId());
																if("000000".equals(res.getData().getRespCode())) {
																	result.put("code", "00");
																	result.put("message", "成功");
																	result.put("merchantCode", res.getData().getMerchantCode());
																	result.put("payNo", res.getData().getPayNo());
																}else{
																	result.put("code", "01");
																	result.put("message",res.getData().getRespMsg());
																}
															}else {
																result.put("respCode", "01");
																result.put("respMsg", "请求失败");
															}
															
															System.out.println("\n接口响应内容：" + res.getData());
														} else {
															System.out.println("响应码: " + response.code());
															throw new IOException("Unexpected code " + response.message());
														}
													
													
												} else {

													// 交易金额小于收款最低金额
													result.put("respCode",
															"10");
													result.put("respMsg",
															"交易金额大于收款最高金额");
													log.info("交易金额大于收款最高金额");
												}

											} else {
												// 交易金额小于收款最低金额
												result.put("respCode", "09");
												result.put("respMsg",
														"交易金额小于收款最低金额");
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

			
		 }else {
				log.error("不是正式商户!");
				result.put("respCode", "03");
				result.put("respMsg", "不是正式商户");
			}

		 
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * 消费支付
	 * @see xdt.service.IPayService#consumerPayment(xdt.dto.pay.PayRequest, java.util.Map)
	 */
	@Override
	public Map<String, String> consumerPayment(PayRequest payRequest,
			Map<String, String> result) {
	
		String out_trade_no = "";// 订单号
		out_trade_no = payRequest.getOrderId(); // 10业务号2业务细; 订单号
													// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = payRequest.getMerchantId();

		// o单编号
		String oAgentNo = "";

		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);
		PmsWeixinMerchartInfo model =new PmsWeixinMerchartInfo();
		model.setAccount(payRequest.getMerchantCode());
		model =weixinService.selectByEntity(model);
		
		// 查询当前商户信息
		try {
			List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
					.searchList(merchantinfo);
			log.info("查询当前商户信息" + merchantList);
			if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

				// 正式商户
				merchantinfo = merchantList.get(0);

				oAgentNo = merchantinfo.getoAgentNo();//

				OriginalOrderInfo oriInfo = new OriginalOrderInfo();
				oriInfo.setMerchantOrderId(payRequest.getOrderId());// ---------------------------
				oriInfo.setPid(payRequest.getMerchantId());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoWxPay(payRequest, out_trade_no, mercId);
					// 校验商户金额限制
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mercid", merchantinfo.getMercId());// 商户编号
					paramMap.put("businesscode",
							TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
					paramMap.put("oAgentNo", oAgentNo);
					// 商户 网购 业务信息
					Map<String, String> resultMap = merchantMineDao
							.queryBusinessInfo(paramMap);

					String quickRateType = resultMap.get("QUICKRATETYPE");// 快捷支付费率类型

					// 获取o单第三方支付的费率
					AppRateConfig appRate = new AppRateConfig();
					appRate.setRateType(quickRateType);
					appRate.setoAgentNo(oAgentNo);
					AppRateConfig appRateConfig = appRateConfigDao
							.getByRateTypeAndoAgentNo(appRate);

					paramMap.put("mercid", mercId);
					paramMap.put("businesscode",
							TradeTypeEnum.merchantCollect.getTypeCode());
					// 微信支付
					paramMap.put("paymentcode",
							PaymentCodeEnum.moBaoQuickPay.getTypeCode());

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
								.moduleVerifyOagent(
										TradeTypeEnum.merchantCollect, oAgentNo);

						if (!resultInfoForOAgentNo.getErrCode().equals("0")) {
							if (StringUtils.isEmpty(resultInfoForOAgentNo
									.getMsg())) {
								log.error("此功能暂时关闭!");
								result.put("respCode", "05");
								result.put("respMsg", "此功能暂时关闭");
							} else {
								log.error(resultInfoForOAgentNo.getMsg());
								result.put("respCode", "05");
								result.put("respMsg",
										resultInfoForOAgentNo.getMsg());
							}
						} else {

							if ("1".equals(status)) {

								// 判断支付方式时候开通总开关
								ResultInfo payCheckResult = null;

								payCheckResult = payTypeControlDao.checkLimit(
										oAgentNo,
										PaymentCodeEnum.moBaoQuickPay
												.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(
											payRequest.getAmount());// 收款金额
									// 判读 交易金额是不是在欧单区间控制之内
									ResultInfo resultInfo = amountLimitControlDao
											.checkLimit(
													oAgentNo,
													payAmt,
													TradeTypeEnum.merchantCollect
															.getTypeCode());
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
												.payTypeVerifyMer(
														PaymentCodeEnum.moBaoQuickPay,
														mercId);

										if ("0".equals(resultinfo.getErrCode())) {
											if ("0".equals(payStatus)) {
												// 有效
												// MIN_AMOUNT,MAX_AMOUNT ,RATE
												// ,STATUS
												String rateStr = appRateConfig
														.getRate(); // 商户费率
																	// RATE

												BigDecimal min_amount = new BigDecimal(
														appRateTypeAndAmount
																.getMinAmount());// 最低收款金额
												// MIN_AMOUNT
												BigDecimal max_amount = new BigDecimal(
														appRateTypeAndAmount
																.getMaxAmount());// 最高收款金额
												// MAX_AMOUNT

												if (min_amount
														.compareTo(payAmt) != 1) {// 判断收款金额是否大于最低收款金额
													// 大于等于执行
													// 小于不执行
													if (payAmt
															.compareTo(max_amount) != 1) {
														// 组装报文
														log.info("来了111111111111");
														String totalAmount = payRequest
																.getAmount(); // 交易金额
														PmsAppTransInfo appTransInfo = this
																.insertOrder(
																		out_trade_no,
																		totalAmount,
																		mercId,
																		rateStr,
																		oAgentNo,payRequest.getMerchantCode());
														log.info("来了22222222");
														log.info("appTransInfo1:"+appTransInfo);
														log.info("appTransInfo2:"+JSON.toJSONString(appTransInfo));
														if (appTransInfo != null) {

															result = otherInvokeCardPay(
																	payRequest,
																	result,
																	appTransInfo);
															log.info("result:"+result);

														} else {
															// 交易金额小于收款最低金额
															result.put("respCode","11");
															result.put("respMsg","生成订单流水失败");
															log.info("生成订单流水失败");
														}

													} else {

														// 交易金额小于收款最低金额
														result.put("respCode",
																"10");
														result.put("respMsg",
																"交易金额大于收款最高金额");
														log.info("交易金额大于收款最高金额");
													}

												} else {
													// 交易金额小于收款最低金额
													result.put("respCode", "09");
													result.put("respMsg",
															"交易金额小于收款最低金额");
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

	public Map<String, String> otherInvokeCardPay(PayRequest payRequest,
			Map<String, String> result, PmsAppTransInfo appTransInfo) {
			log.info("进来了！！！");
			try {
			PmsBusinessPos pmsBusinessPos =this.selectKey(payRequest.getMerchantId());
			log.info("pmsBusinessPos:"+pmsBusinessPos);
			appTransInfo = pmsAppTransInfoDao.searchOrderInfo(appTransInfo
					.getOrderid());
			log.info("appTransInfo:"+appTransInfo);
			appTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
			appTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
			pmsAppTransInfoDao.update(appTransInfo);
			log.info("进来了2313！！！");
			PospTransInfo pospTransInfo = null;
			// 流水表是否需要更新的标记 0 insert，1：update
			int insertOrUpdateFlag = 0;
			log.info("***************进入payHandle5-14-3***************");
			// 生成上送流水号
			String transOrderId = payRequest.getOrderId();
			log.info("***************进入payHandle5-15***************");
			if ((pospTransInfo = pospTransInfoDAO.searchByOrderId(payRequest.getOrderId())) != null) {
				// 已经存在，修改流水号，设置pospsn为空
				log.info("订单号：" + payRequest.getOrderId() + ",生成上送通道的流水号："
						+ transOrderId);
				pospTransInfo.setTransOrderId(transOrderId);
				pospTransInfo.setResponsecode("20");
				pospTransInfo.setPospsn("");
				insertOrUpdateFlag = 1;
				log.info("***************进入payHandle5-16***************");
			} else {
				// 不存在流水，生成一个流水
				pospTransInfo = InsertJournal(appTransInfo);
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
			Map<String, String> map =new HashMap<>();
			log.info("参数进来了："+JSON.toJSONString(payRequest));
			PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
			merchantinfo.setMercId(payRequest.getMerchantId());
			token(payRequest, map);
				//List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
				log.info("给上游组装数据");
				String token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
				// 请求参数
				log.info("tiken:"+token);
				String requestId = "CJZF"+payRequest.getOrderId(); // 请求流水号，每次请求保持唯一
				String orderId = payRequest.getOrderId(); // 支付短信订单号
				String merchantCode = payRequest.getMerchantCode(); // 合作商户编号，合作商户的唯一标识
				String rateCode = payRequest.getRateCode();// 合作商户费率编号
				String payNo = payRequest.getPayNo();// 支付短信流水号
				String payAmountText = payRequest.getAmount(); // 支付金额，支付金额以分为单位，3DES加密
				String cardNoText = payRequest.getAcctNo(); // 银行卡卡号，3DES加密
				String accountName = payRequest.getAcctName(); // 银行卡姓名
				Integer cardType = Integer.parseInt(payRequest.getAccountType()); // 银行卡类型，1-借记卡 2-信用卡
				String phonenoText = payRequest.getPhone(); // 银行预留手机号，银行卡对应手机号，3DES加密
				String certType = "01"; // 银行预留证件类型 01、身份证
				String certNoText = payRequest.getLiceneceNo(); // 银行预留证件号码，3DES加密
				String bankCode = payRequest.getBankCode(); // 银行代码，请见银行代码、简称对照表
				String bankAbbr = payRequest.getBankAbbr(); // 银行代号，请见银行代码、简称对照表
				String smsCode = payRequest.getSmsCode(); // 短信验证码，6位短信验证码
				String productName = payRequest.getProductName(); // 商品名称，购买商品名称
				String productDesc = payRequest.getProductDesc(); // 商品描述，购买商品描述
				String cvn2Text=payRequest.getCvv2();
				String expiredText=payRequest.getYear()+payRequest.getMonth();
				String offlineNotifyUrlText = PayUtil.notifyUrl; // 后台通知url，开通结果通过后台通讯通知到这个url（以此为准），3DES加密最外层做base64编码
				// 敏感数据3DES加密
				String payAmount = null;
				String cardNo = null;
				String phoneno = null;
				String certNo = null;
				String offlineNotifyUrl = null;
				String cvn2="";
				//String expired="";
				try {
					payAmount = EncryptUtil.desEncrypt(payAmountText, pmsBusinessPos.getKek());
					cardNo = EncryptUtil.desEncrypt(cardNoText, pmsBusinessPos.getKek());
					phoneno = EncryptUtil.desEncrypt(phonenoText, pmsBusinessPos.getKek());
					certNo = EncryptUtil.desEncrypt(certNoText, pmsBusinessPos.getKek());
					cvn2 =EncryptUtil.desEncrypt(cvn2Text, pmsBusinessPos.getKek());
					//expired =EncryptUtil.desEncrypt(expiredText, pmsBusinessPos.getKek());
					log.info("到这里了");
					offlineNotifyUrl =Base64.getEncoder().encodeToString(EncryptUtil.desEncrypt(offlineNotifyUrlText, pmsBusinessPos.getKek()).getBytes());
					log.info("能否到这里呢");
				} catch (Exception e) {
					e.printStackTrace();
					log.info(e);
				}
				// 构建签名参数
				TreeMap<String, Object> signParams = new TreeMap<String, Object>();
				signParams.put("token", token);
				signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
				signParams.put("requestId", requestId);
				signParams.put("orderId", orderId);
				signParams.put("merchantCode", merchantCode);
				signParams.put("rateCode", rateCode);
				signParams.put("payNo", payNo);
				signParams.put("payAmount", payAmountText);
				signParams.put("cardNo", cardNoText);
				signParams.put("accountName", accountName);
				signParams.put("cardType", cardType);
				signParams.put("phoneno", phonenoText);
				signParams.put("certType", certType);
				signParams.put("certNo", certNoText);
				signParams.put("cvn2", cvn2Text);
				signParams.put("expired", expiredText);
				signParams.put("bankCode", bankCode);
				signParams.put("bankAbbr", bankAbbr);
				signParams.put("smsCode", smsCode);
				signParams.put("productName", productName);
				signParams.put("productDesc", productDesc);
				signParams.put("offlineNotifyUrl", offlineNotifyUrlText);

				// 构建请求参数
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("token", token);
				jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
				jsonObj.put("requestId", requestId);
				jsonObj.put("orderId", orderId);
				jsonObj.put("merchantCode", merchantCode);
				jsonObj.put("rateCode", rateCode);
				jsonObj.put("payNo", payNo);
				jsonObj.put("payAmount", payAmount);
				jsonObj.put("cardNo", cardNo);
				jsonObj.put("accountName", accountName);
				jsonObj.put("cardType", cardType);
				jsonObj.put("phoneno", phoneno);
				jsonObj.put("certType", certType);
				jsonObj.put("certNo", certNo);
				jsonObj.put("cvn2", cvn2);
				jsonObj.put("expired", expiredText);
				jsonObj.put("bankCode", bankCode);
				jsonObj.put("bankAbbr", bankAbbr);
				jsonObj.put("smsCode", smsCode);
				jsonObj.put("productName", productName);
				jsonObj.put("productDesc", productDesc);
				jsonObj.put("offlineNotifyUrl", offlineNotifyUrl);
				jsonObj.put("sign", SignUtil.signByMap(pmsBusinessPos.getKek(), signParams));

				// 接口访问
				String jsonReq = jsonObj.toJSONString();
				log.info("发送前数据："+jsonReq);
				String url =map.get("url")+"/gateway/api/consume";
				OkHttpClient client = new OkHttpClient();
				RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
				Request request = new Request.Builder().url(url).post(body).build();
				client.newBuilder()
	               .connectTimeout(30, TimeUnit.SECONDS)
	               .readTimeout(60, TimeUnit.SECONDS).build();
				Response response = client.newCall(request).execute();

				String jsonRsp = response.body().string();
				System.out.println("jsonRsp: " + jsonRsp);
				log.info("发送后返回数据："+jsonRsp);
				BaseResMessage<ConsumeVo> res = null;
				if (response.isSuccessful()) {
					res = JSONObject.parseObject(jsonRsp, new TypeReference<BaseResMessage<ConsumeVo>>() {
					});

					System.out.println("\n接口响应内容：" + res.getData());
					if("000000".equals(res.getCode())){
						PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
						result.put("merchantId", payRequest.getMerchantId());
						result.put("merchantCode", payRequest.getMerchantCode());
						if("000000".equals(res.getData().getRespCode())){
							
							result.put("respCode", "00");
							result.put("respMsg", "请求成功,请看异步！");
							result.put("orderId",res.getData().getOrderId());
							result.put("amount", EncryptUtil.desDecrypt(res.getData().getPayAmount(), pmsBusinessPos.getKek()));
							result.put("requestId",res.getData().getRequestId());
							/*pmsAppTransInfo.setStatus(OrderStatusEnum.paySuccess.getStatus());
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
								if(pmsAppTransInfo.getOrderid()!=null&& pmsAppTransInfo.getOrderid()!=""){
									pospTransInfo.setPospsn(pmsAppTransInfo.getOrderid());
								}
								
								log.info("更新流水");
								log.info("流水表信息：" + pospTransInfo);
								pospTransInfoDAO.updateByOrderId(pospTransInfo);
								OriginalOrderInfo originalInfo = null;
								if (res.getData().getOrderId() != null && res.getData().getOrderId() != "") {
									originalInfo = this.payService.getOriginOrderInfo(res.getData().getOrderId());
								}
								int ii =UpdatePmsMerchantInfo(originalInfo);
								if(ii==1){
									log.info("实时填金成功！！");
								}
							}*/
						}else{
							result.put("respCode", "01");
							result.put("respMsg", res.getData().getRespMsg());
							result.put("orderId",EncryptUtil.desDecrypt(res.getData().getOrderId(), pmsBusinessPos.getKek()));
							updateByOrderId(transOrderId, "3", result);
						}
					}else{
						result.put("respCode", "01");
						result.put("respMsg", "请求失败");
					}
					
				} else {
					System.out.println("响应码: " + response.code());
					throw new IOException("Unexpected code " + response.message());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return result;
	}
	
	
	/**
	 * 录入交易流水 并记算费率
	 * 
	 * @throws Exception
	 */
	public PospTransInfo InsertJournal(PmsAppTransInfo pmsAppTransInfo)
			throws Exception {
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
		pospTransInfo.setRemark(pmsAppTransInfo.getTradetype() + "金额："
				+ pmsAppTransInfo.getFactamount());
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
		pospTransInfo.setSearchTransCode("000000"
				+ pmsAppTransInfo.getTradetypecode()
				+ pmsAppTransInfo.getPaymentcode());
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
		pospTransInfo.setMsgtype(pmsAppTransInfo.getTradetypecode()
				+ pmsAppTransInfo.getPaymentcode());
		// 设置发生额
		pospTransInfo.setTransamt(new BigDecimal(pmsAppTransInfo
				.getFactamount()));
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
	public PmsAppTransInfo insertOrder(String orderid, String payamount,
			String mercId, String rateStr, String oAgentNo,String merchantCode) throws Exception {

		System.out.println("12345613454354=" + orderid);
		// 查询商户费率
		BigDecimal rate = new BigDecimal(rateStr);
		BigDecimal amount = new BigDecimal(payamount);

		// 成功后订到入库app后台
		PmsAppTransInfo pmsAppTransInfo = new PmsAppTransInfo();

		pmsAppTransInfo.setTradetype(TradeTypeEnum.merchantCollect
				.getTypeName());
		pmsAppTransInfo.setTradetime(UtilDate.getDateFormatter());
		pmsAppTransInfo.setOrderid(orderid);// 上送的订单号

		pmsAppTransInfo.setReasonofpayment(TradeTypeEnum.merchantCollect
				.getTypeName());
		pmsAppTransInfo.setMercid(mercId);
		pmsAppTransInfo.setFactamount(payamount);// 实际金额 按分为最小单位 例如：1元=100分
													// 采用100
		pmsAppTransInfo.setTradetypecode("1");
		pmsAppTransInfo.setOrderamount(payamount);// 订单金额 按分为最小单位 例如：1元=100分
													// 采用100
		pmsAppTransInfo.setStatus(xdt.util.Constants.ORDERINITSTATUS);// 订单初始化状态
		pmsAppTransInfo.setoAgentNo(oAgentNo);// o单编号

		pmsAppTransInfo.setSearchNum(merchantCode);
		BigDecimal poundage = amount.multiply(rate);// 手续费
		BigDecimal b = new BigDecimal(0);

		BigDecimal dfactAmount = new BigDecimal(pmsAppTransInfo.getFactamount());
		double fee1 = poundage.doubleValue();
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(mercId);
		// 结算金额
		BigDecimal payAmount = null;
		List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao
				.searchList(merchantinfo);

		if (!(merchantList.size() == 0 || merchantList.isEmpty())) {

			// 正式商户
			merchantinfo = merchantList.get(0);
			if (merchantinfo.getCounter() != null) {
				Double ss = Double.parseDouble(merchantinfo.getCounter());
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
				log.info("订单入库失败， 订单号：" + orderid + "，结束时间："
						+ UtilDate.getDateFormatter() + "。订单详细信息：" + sendString);
				throw new RuntimeException("手动抛出");
			}
			log.info("执行完成！！");
		} catch (Exception e) {
			log.info(
					"订单入库失败， 订单号：" + orderid + "，结束时间："
							+ UtilDate.getDateFormatter() + "。订单详细信息："
							+ sendString, e);
			throw new RuntimeException("手动抛出");
		}
		return pmsAppTransInfo;

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
	private int saveOriginAlInfoWxPay(PayRequest payRequest, String orderid,
			String mercId) throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setMerchantOrderId(orderid);
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType("标准快捷");
		// 想要传服务器要改实体
		info.setBgUrl(payRequest.getUrl());
		info.setPageUrl(payRequest.getReUrl());
		info.setUserId(payRequest.getMerchantUuid());
		info.setVerifyId(payRequest.getMerchantCode());
		Double amt = Double.parseDouble(payRequest.getAmount());// 单位分
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
	}
//---------------------给小商户入金
	@Override
	public synchronized int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo,Map<String, String> result)
			throws Exception {
		log.info("代付实时填金:"+JSON.toJSON(originalInfo));
		DecimalFormat df =new DecimalFormat("#.00");
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
				String position= "";
				if("0".equals(result.get("type"))) {
					position= merchantInfo.getPosition();
				}else if("1".equals(result.get("type"))) {
					position= merchantInfo.getPositionT1();
				}else {
					log.info("未选择账户类型");
					return 0;
				}
				Double amount=Double.parseDouble(originalInfo.getOrderAmount());
				log.info("订单金额："+amount);
				Double dd =amount*100-poundage;
				log.info("来了1---------");
				Map<String, String> map =new HashMap<>();
				map.put("machId", originalInfo.getPid());
				map.put("payMoney", dd.toString());
				map.put("account", result.get("merchantCode"));
				int i =0;
				int y =0;
				if("0".equals(result.get("type"))) {
					i =pmsMerchantInfoDao.updataPay(map);
					y =weixinService.updataPay(map);
				}else if("1".equals(result.get("type"))) {
					i =pmsMerchantInfoDao.updataPayT1(map);
					y =weixinService.updataPayT1(map);
				}else {
					log.info("未选择账户类型");
					return 0;
				}
				if(i!=1) {
					log.info("实时填金失败！");
					//状态
					pmsDaifuMerchantInfo.setResponsecode("01");
				}else {
					//状态
					log.info("实时填金成功！");
					pmsDaifuMerchantInfo.setResponsecode("00");
				}
				if(y!=1) {
					log.info("小钱包实时填金失败！");
				}else {
					log.info("小钱包实时填金成功！");
				}
				
				//商户号
				pmsDaifuMerchantInfo.setMercId(originalInfo.getPid());
				//订单号
				pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
				//总金额
				pmsDaifuMerchantInfo.setAmount((Double.parseDouble(originalInfo.getOrderAmount()))+"");
				//状态
				//pmsDaifuMerchantInfo.setResponsecode("00");
				//备注
				if("0".equals(result.get("type"))) {
					pmsDaifuMerchantInfo.setRemarks("D0");
					pmsDaifuMerchantInfo.setPosition(select(originalInfo.getPid()).getPosition());//账户余额
				}else if("1".equals(result.get("type"))) {
					pmsDaifuMerchantInfo.setRemarks("T1");
					pmsDaifuMerchantInfo.setPosition(select(originalInfo.getPid()).getPositionT1());//账户余额
				}
				//记录描述
				pmsDaifuMerchantInfo.setRecordDescription("订单号:"+originalInfo.getOrderId()+"交易金额:"+originalInfo.getOrderAmount());
				//交易类型
				pmsDaifuMerchantInfo.setTransactionType(pmsAppTransInfo.getPaymenttype());
				//发生额
				pmsDaifuMerchantInfo.setPayamount((Double.parseDouble(originalInfo.getOrderAmount()))+"");
				
				//手续费
				pmsDaifuMerchantInfo.setPayCounter(poundage/100+"");
				pmsDaifuMerchantInfo.setOagentno("100333");
				log.info("来了2---------");
				//交易时间
				//pmsDaifuMerchantInfo.setCreationdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				int s=pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
				log.info("---s:"+s);
				log.info("来了3---------");
				return i;
			}else{
				log.info("此商户未开通代付！！");
			}
		}
		return 0;
	}
	
	
	@Override
	public Map<String, String> updateByOrderId(String orderId,String orderStatus,Map<String, String> result) {
		log.info("**************进入修改方法*************************"+orderStatus);
		
		try {
			// 流水表transOrderId
			String transOrderId = "";
			if(orderId!=""&& orderId!=null){
				transOrderId=orderId;
			}
			log.info("异步通知回来的订单号:" + transOrderId);
			// 流水信息
			PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
			log.info("流水表信息：" + JSON.toJSONString(pospTransInfo));
			// 订单信息
			PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
			log.info("订单表信息：" + JSON.toJSONString(pmsAppTransInfo));
			// 查询结果成功
			if("2".equals(orderStatus)) {
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
					if(orderId!=null&& orderId!=""){
						pospTransInfo.setPospsn(orderId);
					}
					
					log.info("更新流水");
					log.info("流水表信息：" + pospTransInfo);
					pospTransInfoDAO.updateByOrderId(pospTransInfo);
				}
			} else if("5".equals(orderStatus)||"3".equals(orderStatus)||"4".equals(orderStatus)){
				// 支付失败
				pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
				pmsAppTransInfo.setThirdPartResultCode("1");
				pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
				// 修改订单
				int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
				if (updateAppTrans == 1) {
					// 更新流水表
					pospTransInfo.setResponsecode("02");
					if(orderId!=null&&orderId!=""){
						pospTransInfo.setPospsn(orderId);
					}
					log.info("更新流水");
					log.info("流水表信息：" + pospTransInfo);
					pospTransInfoDAO.updateByOrderId(pospTransInfo);
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;

	}

	@Override
	public Map<String, String> select(PayRequest payRequest,
			Map<String, String> result) {
		Map<String, String> map =new HashMap<>();
		log.info("查询参数进来了："+JSON.toJSONString(payRequest));
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(payRequest.getMerchantId());
		token(payRequest, map);
			//List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			log.info("给上游组装数据");
			String token;
			try {
				PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
				token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
				log.info("tiken:"+token);
				
				String requestId = UUID.randomUUID().toString().replace("-", ""); // 请求流水号，每次请求保持唯一
				String merchantCode = payRequest.getMerchantCode(); // 合作商户编号，合作商户的唯一标识
				String orderId = payRequest.getOrderId(); // 商户订单号，商户系统保证唯一
				String payNo = ""; //支付流水号

				// 构建签名参数
				TreeMap<String, Object> signParams = new TreeMap<String, Object>();
				signParams.put("token", token);
				signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
				signParams.put("merchantCode", merchantCode);
				signParams.put("requestId", requestId);
				signParams.put("orderId", orderId);
				signParams.put("payNo", payNo);

				// 构建请求参数
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("token", token);
				jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
				jsonObj.put("merchantCode", merchantCode);
				jsonObj.put("requestId", requestId);
				jsonObj.put("orderId", orderId);
				jsonObj.put("payNo", payNo);
				jsonObj.put("sign", SignUtil.signByMap(pmsBusinessPos.getKek(), signParams));

				// 接口访问
				String jsonReq = jsonObj.toJSONString();
				System.out.println("jsonReq: " + jsonReq);
				String url =map.get("url")+"/gateway/api/payOrderQuery";
				OkHttpClient client = new OkHttpClient();
				RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
				Request request = new Request.Builder().url(url).post(body).build();
				Response response = client.newCall(request).execute();

				String jsonRsp = response.body().string();
				System.out.println("jsonRsp: " + jsonRsp);

				BaseResMessage<ConsumeVo> res = null;
				if (response.isSuccessful()) {
					res = JSONObject.parseObject(jsonRsp, new TypeReference<BaseResMessage<ConsumeVo>>() {
					});
					
					System.out.println("\n接口响应内容：" + res.getData());
					String orderStatus=res.getData().getOrderStatus().toString();
					if("000000".equals(res.getCode())){
						result.put("payDate", res.getData().getPayDate());
						result.put("merchantCode", res.getData().getMerchantCode());
						result.put("payNo", res.getData().getPayNo());
						result.put("payAmount", res.getData().getPayAmount());
						result.put("orderId", res.getData().getOrderId());
						result.put("requestId", res.getData().getRequestId());
						if("2".equals(orderStatus)){
							result.put("respCode", "00");
							result.put("respMsg", "支付成功");
						}else if("1".equals(orderStatus)||"0".equals(orderStatus)){
							result.put("respCode", "200");
							result.put("respMsg", "处理中");
						}else if("3".equals(orderStatus)||"4".equals(orderStatus)||"5".equals(orderStatus)){
							result.put("respCode", "01");
							result.put("respMsg", "失败");
						}
					}else{
						result.put("respCode", "02");
						result.put("respMsg", "失败");
					}
				} else {
					result.put("respCode", "02");
					result.put("respMsg", "失败");
					System.out.println("响应码: " + response.code());
					throw new IOException("Unexpected code " + response.message());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 请求参数
			
		return result;
	}
	/**
	 * 畅捷快捷代付
	 */
	@Override
	public Map<String, String> withdrawals(PayRequest payRequest,
			Map<String, String> result) {
		log.info("畅捷----下游传送代付参数:" + JSON.toJSON(payRequest));
		BigDecimal b1 = new BigDecimal("0");// 下游上传的总金额
		BigDecimal b2 = new BigDecimal("0");// 系统代付余额D0金额
		BigDecimal b21 = new BigDecimal("0");// 系统代付余额T0金额
		BigDecimal b3 = new BigDecimal("0");// 单笔交易总手续费
		BigDecimal min = new BigDecimal("0");// 代付最小金额
		BigDecimal max = new BigDecimal("0");// 代付最大金额
		Double surplus;// 代付剩余金额
		log.info("畅捷----查询当前代付订单是否存在");
		PmsDaifuMerchantInfo model = new PmsDaifuMerchantInfo();
		Map<String, String> map = new HashMap<>();
		Map<String, String> maps = new HashMap<>();// 填金
		model.setMercId(payRequest.getMerchantId());
		model.setBatchNo(payRequest.getOrderId());
		if (pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(model) != null) {
			result.put("respCode", "0006");
			result.put("respMsg", "下单失败,订单存在");
			log.info("畅捷----**********************代付 下单失败:{}");
			log.info("畅捷----订单存在");
			return result;
		}
		try {
			label104: {

				log.info("********************畅捷-------------根据商户号查询");
				String e = payRequest.getMerchantId();
				PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
				merchantinfo.setMercId(e);
				List merchantList = this.pmsMerchantInfoDao
						.searchList(merchantinfo);
				if (merchantList.size() != 0 && !merchantList.isEmpty()) {
					merchantinfo = (PmsMerchantInfo) merchantList.get(0);
					if (merchantinfo.getOpenPay().equals("1")) {
						result.put("respCode", "01");
						result.put("respMsg", "未开通代付");
						return result;
					}
					String oAgentNo = merchantinfo.getoAgentNo();
					log.info("***********畅捷*************商户信息:" + merchantinfo);
					if (StringUtils.isBlank(oAgentNo)) {
						throw new RuntimeException("系统错误----------------o单编号为空");
					}

					if ("60".equals(merchantinfo.getMercSts())) {
						// 插入异步数据
						saveOriginAlInfoWxPay1(payRequest,
								payRequest.getOrderId(),
								payRequest.getMerchantId());
						// 判断交易类型
						log.info("***********畅捷*************实际金额");
						// 分
						String payAmt = payRequest.getAmount();
						b1 = new BigDecimal(payAmt);

						System.out.println("参数:" + b1.doubleValue());
						log.info("***********畅捷*************校验欧单金额限制");
						log.info("畅捷----下游上传代付总金额:" + b1.doubleValue());
						b3 = new BigDecimal(merchantinfo.getPoundage());
						log.info("畅捷----系统商户代付单笔手续费:" + b3.doubleValue());
						min = new BigDecimal(merchantinfo.getMinDaiFu());
						log.info("畅捷----系统代付最小金额:" + min.doubleValue());
						max = new BigDecimal(merchantinfo.getMaxDaiFu());
						log.info("畅捷----系统代付最大金额:" + max.doubleValue());
						b2 = new BigDecimal(merchantinfo.getPosition());//剩余D0金额
						log.info("畅捷----系统剩余可用额度:" + b2.doubleValue());
						b21=new BigDecimal(merchantinfo.getPositionT1());//剩余T1额度
						
						
						int i =0;
						PmsWeixinMerchartInfo merchartInfos =new PmsWeixinMerchartInfo();
						merchartInfos.setPassword(payRequest.getMerchantUuid());
						merchartInfos =weixinService.selectByEntity(merchartInfos);
						if ("1".equals(payRequest.getSummary())) {//b21.doubleValue()>b1.doubleValue() + b3.doubleValue() * 100
							//走T1余额
							/*if (b1.doubleValue() + b3.doubleValue()*100 > b21.doubleValue()) {
								result.put("respCode", "06");
								result.put("respMsg", "下单失败,代付金额高于剩余额度");
								log.info("畅捷**********************代付金额高于剩余额度");
								int g = add(payRequest, merchantinfo, result,"01");
								if (g == 1) {
									log.info("畅捷----添加失败订单成功");
								}
								return result;
							}*/
							if (b1.doubleValue() < min.doubleValue() * 100) {
								result.put("respCode", "06");
								result.put("respMsg", "下单失败,代付金额小于代付最小金额");
								log.info("畅捷**********************代付金额小于代付最小金额");
								 i = add(payRequest, merchantinfo, result, "01");
								if (i == 1) {
									log.info("畅捷--添加失败订单成功");
								}
								return result;
							}
							if (b1.doubleValue() > max.doubleValue() * 100) {
								result.put("respCode", "06");
								result.put("respMsg", "下单失败,代付金额大于代付最大金额");
								log.info("畅捷**********************代付金额大于代付最大金额");
								 i = add(payRequest, merchantinfo, result, "01");
								if (i == 1) {
									log.info("畅捷--添加失败订单成功");
								}
								return result;
							}
							/*Map<String, String> mapPay=new HashMap<>();
							mapPay.put("machId", payRequest.getMerchantId());
							mapPay.put("payMoney", (Double.parseDouble(payRequest.getAmount())-b3.doubleValue()*100)+"");
							mapPay.put("account", merchartInfos.getAccount());
							int num =pmsMerchantInfoDao.updataT1(mapPay);
							int numy =weixinService.updataT1(mapPay);
							if (num != 1&& numy !=1) {
								log.info("畅捷--扣款失败！！");
								result.put("respCode", "02");
								result.put("respMsg", "代付失败");
								return result;
							}*/
							log.info("畅捷--添加代付扣款订单成功！");

							i = add(payRequest, select(payRequest.getMerchantId()), result, "200");
							PmsBusinessPos pmsBusinessPos = selectKey(payRequest
									.getMerchantId());
							if (i == 1) {
								log.info("畅捷--代付订单添加成功");
								cjPay(payRequest, result, merchantinfo, pmsBusinessPos,merchartInfos);
								 
							}
						}else {
							if ("0".equals(payRequest.getSummary())) {//b2.doubleValue()>b1.doubleValue() + b3.doubleValue() * 100  
								
								/*if (b1.doubleValue() + b3.doubleValue()*100 > b2.doubleValue()) {
									result.put("respCode", "06");
									result.put("respMsg", "下单失败,代付金额高于剩余额度");
									log.info("畅捷**********************代付金额高于剩余额度");
									int g = add(payRequest, merchantinfo, result,"01");
									if (g == 1) {
										log.info("畅捷----添加失败订单成功");
									}
									return result;
								}*/
								//走D0余额
								if (b1.doubleValue() < min.doubleValue() * 100) {
									result.put("respCode", "06");
									result.put("respMsg", "下单失败,代付金额小于代付最小金额");
									log.info("畅捷**********************代付金额小于代付最小金额");
									 i = add(payRequest, merchantinfo, result, "01");
									if (i == 1) {
										log.info("畅捷--添加失败订单成功");
									}
									return result;
								}
								if (b1.doubleValue() > max.doubleValue() * 100) {
									result.put("respCode", "06");
									result.put("respMsg", "下单失败,代付金额大于代付最大金额");
									log.info("畅捷**********************代付金额大于代付最大金额");
									 i = add(payRequest, merchantinfo, result, "01");
									if (i == 1) {
										log.info("畅捷--添加失败订单成功");
									}
									return result;
								}
								/*Map<String, String> mapPay=new HashMap<>();
								mapPay.put("machId", payRequest.getMerchantId());
								mapPay.put("payMoney",(Double.parseDouble(payRequest.getAmount())-b3.doubleValue()*100)+"");
								mapPay.put("account", merchartInfos.getAccount());
								int num =pmsMerchantInfoDao.updataD0(mapPay);
								int numy =weixinService.updataD0(mapPay);
								
								if (num != 1&&numy!=1) {
									log.info("畅捷--扣款失败！！");
									result.put("respCode", "02");
									result.put("respMsg", "代付失败");
									return result;
								}*/
								log.info("畅捷--添加代付扣款订单成功！");

							 i = add(payRequest, select(payRequest.getMerchantId()), result, "200");
							 PmsBusinessPos pmsBusinessPos = selectKey(payRequest
										.getMerchantId());
							if (i == 1) {
								log.info("畅捷--代付订单添加成功");

								cjPay(payRequest, result, merchantinfo, pmsBusinessPos,merchartInfos);
								 
							}
							}else {
								result.put("respCode", "06");
								result.put("respMsg", "未选择账户类型");
								log.info("畅捷**********************未选择账户类型");
								 i = add(payRequest, merchantinfo, result, "01");
								if (i == 1) {
									log.info("畅捷----添加失败订单成功");
								}
								return result;
							}
						}
						
						
					} else {
						throw new RuntimeException(
								"畅捷***系统错误----------------当前商户非正式商户");
					}

				} else {
					throw new RuntimeException("畅捷***系统错误----------------商户不存在");
				}
				break label104;
			}

		} catch (Exception var43) {
			log.error("畅捷*******************************代付错误", var43);
			try {
				throw var43;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		log.info("***********畅捷*********************代付------处理完成");
		
		
		return result;
	}
	
	public void cjPay(PayRequest payRequest,
			Map<String, String> result,PmsMerchantInfo merchantinfo,PmsBusinessPos pmsBusinessPos,PmsWeixinMerchartInfo merchartInfos){
		Map<String, String> map =new HashMap<>();
		log.info("代付进来了："+JSON.toJSONString(payRequest));
		token(payRequest, map);
		log.info("给上游组装数据");
		String token;
		try {
			token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
			log.info("tiken:"+token);
		// 请求参数
		String merchantUuidText = payRequest.getMerchantUuid();// 商户UUID，3DES加密
		String reqFlowNo = payRequest.getOrderId();// 请求流水号
		Integer walletType =0;
		if("0".equals(payRequest.getSummary())) {
			 walletType = 400;
		}else if("1".equals(payRequest.getSummary())) {
			 walletType = 402;
		}
		String amountText = payRequest.getAmount();// 提现金额，支付金额以分为单位，3DES加密
		String bankAccountNoText = payRequest.getAcctNo();// 银行卡卡号，3DES加密
		String bankAccountNameText = payRequest.getAcctName(); // 银行卡户名，3DES加密
		Integer bankAccountType = Integer.parseInt(payRequest.getBusinessType()); // 银行卡账户类型 2 对私
		String bankName =payRequest.getBankName(); // 银行名称
		String bankSubName = payRequest.getBranchBankName(); // 银行支行名称
		String bankChannelNo = payRequest.getPmsbankNo(); // 银行联行号
		String bankCode = payRequest.getBankCode(); // 银行代码，请见银行代码、简称对照表
		String bankAbbr = payRequest.getBankAbbr(); // 银行代号，请见银行代码、简称对照表
		String bankProvince = payRequest.getProvince(); // 银行所属省
		String bankCity = payRequest.getCity(); // 银行所属市
		String bankArea = payRequest.getCity(); // 银行所属区域
		String key =pmsBusinessPos.getKek();
		// 敏感数据3DES加密
		String merchantUuid = null;
		String amount = null;
		String bankAccountNo = null;
		String bankAccountName = null;
		log.info("laile11111!!!");
			merchantUuid = EncryptUtil.desEncrypt(merchantUuidText, key);
			amount = EncryptUtil.desEncrypt(amountText, key);
			bankAccountNo = EncryptUtil.desEncrypt(bankAccountNoText, key);
			bankAccountName = EncryptUtil.desEncrypt(bankAccountNameText, key);
			System.out.println("1111111111");
		// 构建签名参数
			log.info("laile222222!!!");
		TreeMap<String, Object> signParams = new TreeMap<String, Object>();
		signParams.put("token", token);
		signParams.put("clientNo", "");
		signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
		signParams.put("merchantUuid", merchantUuidText);
		signParams.put("reqFlowNo", reqFlowNo);
		signParams.put("walletType", walletType);
		signParams.put("amount", amountText);
		signParams.put("bankAccountNo", bankAccountNoText);
		signParams.put("bankAccountName", bankAccountNameText);
		signParams.put("bankAccountType", bankAccountType);
		signParams.put("bankName", bankName);
		signParams.put("bankSubName", bankSubName);
		signParams.put("bankChannelNo", bankChannelNo);
		signParams.put("bankCode", bankCode);
		signParams.put("bankAbbr", bankAbbr);
		signParams.put("bankProvince", bankProvince);
		signParams.put("bankCity", bankCity);
		signParams.put("bankArea", bankArea);

		// 构建请求参数
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("token", token);
		jsonObj.put("clientNo", "");
		jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
		jsonObj.put("merchantUuid", merchantUuid);
		jsonObj.put("reqFlowNo", reqFlowNo);
		jsonObj.put("walletType", walletType);
		jsonObj.put("amount", amount);
		jsonObj.put("bankAccountNo", bankAccountNo);
		jsonObj.put("bankAccountName", bankAccountName);
		jsonObj.put("bankAccountType", bankAccountType);
		jsonObj.put("bankName", bankName);
		jsonObj.put("bankSubName", bankSubName);
		jsonObj.put("bankChannelNo", bankChannelNo);
		jsonObj.put("bankCode", bankCode);
		jsonObj.put("bankAbbr", bankAbbr);
		jsonObj.put("bankProvince", bankProvince);
		jsonObj.put("bankCity", bankCity);
		jsonObj.put("bankArea", bankArea);
		jsonObj.put("sign", SignUtil.signByMap(key, signParams));
		
		// 接口访问
		String jsonReq = jsonObj.toJSONString();
		System.out.println("jsonReq: " + jsonReq);
		String url =map.get("url")+"/gateway/api/withdrawDeposit";
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();

		String jsonRsp = response.body().string();
		System.out.println("jsonRsp: " + jsonRsp);
		/*Map<String, String> m =new HashMap<>();
		m.put("machId", payRequest.getMerchantId());
		m.put("payMoney",payRequest.getAmount());
		m.put("account",merchartInfos.getAccount());*/
		//int nus = 0;
		///int nusy=0;
		BaseResMessage<ConsumeVo> res = null;
		if (response.isSuccessful()) {
			res = JSONObject.parseObject(jsonRsp, new TypeReference<BaseResMessage<ConsumeVo>>() {
			});
			result.put("merchantId", payRequest.getMerchantId());
			if("000000".equals(res.getCode())){
				result.put("code", "200");
				result.put("message", "受理成功,代付中");
				result.put("respCode", "00");
				result.put("respMsg", "请求成功");
				
				ThreadPool.executor(new QueryPayThread(this, payRequest, pmsMerchantInfoDao,weixinService));
			}else{
				UpdateDaifu(payRequest.getOrderId(), "01");
				result.put("code", "01");
				result.put("message", "受理失败");
				result.put("respCode", "00");
				result.put("respMsg", "请求成功");
				/*if("1".equals(payRequest.getSummary())) {
					nus = pmsMerchantInfoDao.updataPayT1(m);
					nusy = weixinService.updataPayT1(m);
				}else {
					nus = pmsMerchantInfoDao.updataPay(m);
					nusy = weixinService.updataPay(m);
				}*/
			}
			System.out.println("\n接口响应内容：" + res.getData());
		} else {
			UpdateDaifu(payRequest.getOrderId(), "01");
			result.put("code", "01");
			result.put("message", "受理失败");
			result.put("respCode", "01");
			result.put("respMsg", "请求失败");
			/*if("1".equals(payRequest.getSummary())) {
				nus = pmsMerchantInfoDao.updataPayT1(m);
				nusy = weixinService.updataPayT1(m);
			}else {
				nus = pmsMerchantInfoDao.updataPay(m);
				nusy = weixinService.updataPay(m);
			}*/
			System.out.println("响应码: " + response.code());
			throw new IOException("Unexpected code " + response.message());
		}
		/*log.info("大商户补款状态L:"+nus+",小商户补款状态："+nusy);
		if(nus==1&&nusy==1){
			log.info("畅捷***补款成功");
			//surplus = surplus+Double.parseDouble(payRequest.getAmount());
			//merchantinfo.setPosition(surplus.toString());
			PmsMerchantInfo info= select(payRequest.getMerchantId());
			payRequest.setOrderId(payRequest.getOrderId()+"/A");
			int id =add(payRequest, info, result, "00");
			if(id==1){
				log.info("畅捷代付补单成功");
			}
		}*/
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	
	
	/**
	 * 九派网关
	 */
	@Override
	public Map<String, String> cardPay(PayRequest payRequest, Map<String, String> result) {
		
		log.info("九派网关参数来了："+JSON.toJSONString(payRequest));
		log.info("根据商户号查询");
		String out_trade_no = "";// 订单号
		out_trade_no = payRequest.getOrderId(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = payRequest.getMerchantId();

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
				oriInfo.setMerchantOrderId(payRequest.getOrderId());//---------------------------
				oriInfo.setPid(payRequest.getMerchantId());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfoWxPay(payRequest, out_trade_no, mercId);
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
					// 网关支付
						paramMap.put("paymentcode", PaymentCodeEnum.GatewayCodePay.getTypeCode());

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
										PaymentCodeEnum.GatewayCodePay.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(payRequest.getAmount());// 收款金额
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
														String totalAmount = payRequest.getAmount(); // 交易金额

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo,"");

														if (appTransInfo != null) {
															
															result =otherInvokeCardPay1(payRequest, result, appTransInfo);
															
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
	
	public Map<String, String> otherInvokeCardPay1(PayRequest payRequest,Map<String, String> result,PmsAppTransInfo appTransInfo) throws Exception{
		
		// 查看当前交易是否已经生成了流水表
		PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId = payRequest.getOrderId();
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(payRequest.getOrderId())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + payRequest.getOrderId()
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
         PmsBusinessPos pmsBusinessPos =selectKey(payRequest.getMerchantId());//获取上游商户号和秘钥
         try {
        	 Map<String, String> dataMap = new LinkedHashMap<String, String>();
        	 dataMap.put("charset", "02");//字符集02：utf-8
             dataMap.put("version", "1.0");//版本号
             dataMap.put("service", JpUtil.service);
             dataMap.put("signType", "RSA256");
             dataMap.put("merchantId", pmsBusinessPos.getBusinessnum());//"800001400010085"
             dataMap.put("requestTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
             dataMap.put("requestId", String.valueOf(System.currentTimeMillis()));

             dataMap.put("pageReturnUrl", JpUtil.returnUrl);
             dataMap.put("notifyUrl", JpUtil.notifyUrl);
             dataMap.put("merchantName", "大饼鸡蛋");
             //dataMap.put("subMerchantId", "");
             dataMap.put("memberId", String.valueOf(System.currentTimeMillis()));
             dataMap.put("orderTime", payRequest.getStartDate()==""?new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()):payRequest.getStartDate());
             dataMap.put("orderId", payRequest.getOrderId());
             dataMap.put("totalAmount", payRequest.getAmount());
             dataMap.put("currency", "CNY");
             dataMap.put("bankAbbr", payRequest.getBankAbbr());//"CMB"
             dataMap.put("cardType", "0");
             dataMap.put("payType", payRequest.getBusinessType()==null?"B2C":payRequest.getBusinessType());//B2B个人/B2C公司

             dataMap.put("validUnit", "01");//00分钟，01小时，02日，03月
             dataMap.put("validNum", "1");//有效期数量
             //dataMap.put("showUrl", "");
             dataMap.put("goodsName", payRequest.getProductName());//商品名称
             //dataMap.put("goodsId", "");
             //dataMap.put("goodsDesc", "");
             
             String merchantCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".p12";
             String merchantCertPass =pmsBusinessPos.getKek();//"nknEuX"; //秘钥
             result.putAll(dataMap);
             Set set = dataMap.keySet();
             Iterator iterator = set.iterator();
             while (iterator.hasNext()) {
                 String key = (String) iterator.next();
                 if ((dataMap.get(key) == null) || dataMap.get(key).toString().trim().length() == 0) {
                	 result.remove(key);
                 }
             }
             RSASignUtil util = new RSASignUtil(merchantCertPath, merchantCertPass);

             String reqData = util.coverMap2String(result);
             util.setService(JpUtil.service);
             String merchantSign = util.sign(reqData, "UTF-8");
             String merchantCert = util.getCertInfo();
             //请求报文
             String buf = reqData + "&merchantSign=" + merchantSign + "&merchantCert=" + merchantCert;
        	 log.info("给九派网关请求参数："+buf);
             result.put("merchantSign", merchantSign);
             result.put("merchantCert", merchantCert);
             result.put("respCode", "00");
             String url="";
        	    if("800001400010085".equals(pmsBusinessPos.getBusinessnum())) {
        	    	url =JpUtil.cardUrlTest;
        	    }else if("800000200020011".equals(pmsBusinessPos.getBusinessnum())) {
        	    	url =JpUtil.cardUrl;
        	    }
        	 result.put("cardUrl", url);
     		} catch (Exception e) {
     			// TODO Auto-generated catch block
     			e.printStackTrace();
     		}
		return result;
}

	public void jpPay(PayRequest payRequest,Map<String, String> result,PmsMerchantInfo merchantinfo,PmsBusinessPos pmsBusinessPos){
        try {
       	 Map<String, String> dataMap = new LinkedHashMap<String, String>();
       	 	
       	 Map<String, String> map =new HashMap<>();
       	 	dataMap.put("charset", "02");//字符集02：utf-8
            dataMap.put("version", "1.0");//版本号
            dataMap.put("service", JpUtil.pay);
            dataMap.put("signType", "RSA256");
            dataMap.put("merchantId", pmsBusinessPos.getBusinessnum());//"800001400010085"
            dataMap.put("requestTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            dataMap.put("requestId", String.valueOf(System.currentTimeMillis()));

            dataMap.put("callBackUrl", JpUtil.responseUrl);
			dataMap.put("mcSequenceNo","CJZF"+payRequest.getOrderId());
			dataMap.put("mcTransDateTime", payRequest.getStartDate()==""?new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()):payRequest.getStartDate());//payRequest.getStartDate()
			dataMap.put("orderNo",payRequest.getOrderId());
			dataMap.put("amount",payRequest.getAmount());
			dataMap.put("cardNo",payRequest.getAcctNo());
			dataMap.put("accName",payRequest.getAcctName());
			//dataMap.put("idInfo",idInfo);
			//dataMap.put("idType",idType);
			dataMap.put("accType",payRequest.getBusinessType());
			dataMap.put("lBnkNo",payRequest.getPmsbankNo());
			dataMap.put("lBnkNam",payRequest.getBankName());
			dataMap.put("validPeriod",payRequest.getYear()+payRequest.getMonth());
			dataMap.put("cvv2",payRequest.getCvv2());
			dataMap.put("cellPhone",payRequest.getPhone());
			//dataMap.put("remark",payRequest.get);
			//dataMap.put("bnkRsv",bnkRsv);
			//dataMap.put("capUse",capUse);
			dataMap.put("crdType","00");
			//dataMap.put("remark1",remark1);
			//dataMap.put("remark2",remark2);
			//dataMap.put("remark3",remark3);
			 String merchantCertPath = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath()+"//ky//"+pmsBusinessPos.getBusinessnum()+".p12";
            String merchantCertPass =pmsBusinessPos.getKek();//"nknEuX"; //秘钥
            map.putAll(dataMap);
            Set set = dataMap.keySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if ((dataMap.get(key) == null) || dataMap.get(key).toString().trim().length() == 0) {
                	map.remove(key);
                }
            }
            RSASignUtil util = new RSASignUtil(merchantCertPath, merchantCertPass);

            String reqData = util.coverMap2String(map);
            util.setService(JpUtil.service);
            String merchantSign = util.sign(reqData, "UTF-8");
            String merchantCert = util.getCertInfo();
            //请求报文
            String buf = reqData + "&merchantSign=" + merchantSign + "&merchantCert=" + merchantCert;
       	    log.info("给九派网关请求参数："+buf);
       	    
       	    String url="";
       	    if("800001400010085".equals(pmsBusinessPos.getBusinessnum())) {
       	    	url =JpUtil.payUrlTest;
       	    }else if("800000200020011".equals(pmsBusinessPos.getBusinessnum())) {
       	    	url =JpUtil.payUrl;
       	    }
       	    
            String res = MerchantUtil.sendAndRecv(url, buf, "UTF-8");
            log.info("代付返回参数："+res);
            Map<String,String> retMap = new LinkedHashMap<String,String>();
	   	    retMap.put("charset",(String)util.getValue(res,"charset"));
			retMap.put("version",(String)util.getValue(res,"version"));
			retMap.put("service",(String)util.getValue(res,"service"));
			retMap.put("requestId",(String)util.getValue(res,"requestId"));
			retMap.put("responseId",(String)util.getValue(res,"responseId"));
			retMap.put("responseTime",(String)util.getValue(res,"responseTime"));
			retMap.put("signType",(String)util.getValue(res,"signType"));
			retMap.put("merchantId",(String)util.getValue(res,"merchantId"));
			retMap.put("rspCode",(String)util.getValue(res,"rspCode"));
			retMap.put("rspMessage",(String)util.getValue(res,"rspMessage"));
			retMap.put("mcTransDateTime",(String)util.getValue(res,"mcTransDateTime"));
			retMap.put("orderNo",(String)util.getValue(res,"orderNo"));
			retMap.put("bfbSequenceNo",(String)util.getValue(res,"bfbSequenceNo"));
			retMap.put("mcSequenceNo",(String)util.getValue(res,"mcSequenceNo"));
			retMap.put("mcTransDateTime",(String)util.getValue(res,"mcTransDateTime"));
			retMap.put("cardNo",(String)util.getValue(res,"cardNo"));
			retMap.put("amount",(String)util.getValue(res,"amount"));
			retMap.put("remark1",(String)util.getValue(res,"remark1"));
			retMap.put("remark2",(String)util.getValue(res,"remark2"));
			retMap.put("remark3",(String)util.getValue(res,"remark3"));
			retMap.put("transDate",(String)util.getValue(res,"transDate"));
			retMap.put("transTime",(String)util.getValue(res,"transTime"));
			retMap.put("respMsg",(String)util.getValue(res,"rspMessage"));
			retMap.put("orderSts",(String)util.getValue(res,"orderSts"));
			Map<String, String> m=new HashMap<>();
			if("IPS00000".equals(retMap.get("rspCode"))){
				result.put("respCode", "00");
	            result.put("respMsg", "请求成功，请看异步！");
	            result.put("orderId", payRequest.getOrderId());
	            result.put("amount", payRequest.getAmount());
				if("S".equals(retMap.get("orderSts"))) {
					UpdateDaifu(payRequest.getOrderId(), "00");
					result.put("code", "00");
		            result.put("message", "代付成功");
				}else if("F".equals(retMap.get("orderSts"))||"R".equals(retMap.get("orderSts"))) {
					result.put("code", "01");
		            result.put("message", "代付失败");
					UpdateDaifu(payRequest.getOrderId(), "02");
					m.put("payMoney",payRequest.getAmount());
	     			m.put("machId", payRequest.getMerchantId());
					int nus = pmsMerchantInfoDao.updataPayT1(m);
					if(nus==1){
						log.info("九派***补款成功");
						PmsMerchantInfo info= select(payRequest.getMerchantId());
						merchantinfo.setPosition(info.getPositionT1());
						//surplus = surplus+Double.parseDouble(payRequest.getAmount());
						//merchantinfo.setPosition(surplus.toString());
						payRequest.setOrderId(payRequest.getOrderId()+"/A");
						int id =add(payRequest, merchantinfo, result, "00");
						if(id==1){
							log.info("九派代付补单成功");
						}
					}
				}else if("P".equals(retMap.get("orderSts"))||"U".equals(retMap.get("orderSts"))) {
					result.put("code", "200");
		            result.put("message", "代付中");
				}else if("N".equals(retMap.get("orderSts"))) {
					result.put("code", "200");
		            result.put("message", "等待人工处理");
				}
			}else {
				result.put("respCode", "01");
	            result.put("respMsg", retMap.get("respMsg"));
	            result.put("orderId", payRequest.getOrderId());
	            result.put("amount", payRequest.getAmount());
	            UpdateDaifu(payRequest.getOrderId(), "02");
				m.put("payMoney",payRequest.getAmount());
     			m.put("machId", payRequest.getMerchantId());
				int nus = pmsMerchantInfoDao.updataPayT1(m);
				if(nus==1){
					log.info("九派***补款成功");
					//surplus = surplus+Double.parseDouble(payRequest.getAmount());
					//merchantinfo.setPosition(surplus.toString());
					PmsMerchantInfo info= select(payRequest.getMerchantId());
					merchantinfo.setPosition(info.getPositionT1());
					payRequest.setOrderId(payRequest.getOrderId()+"/A");
					int id =add(payRequest, merchantinfo, result, "00");
					if(id==1){
						log.info("畅捷代付补单成功");
					}
				}
			}
			/*Map responseMap = new HashMap();
			responseMap.putAll(retMap);
		    Set set1 = retMap.keySet();
		    Iterator iterator1 = set1.iterator();
		    while (iterator1.hasNext()) {
		      String key0 = (String)iterator1.next();
		      String tmp = retMap.get(key0);
		      if (StringUtils.equals(tmp, "null")||StringUtils.isBlank(tmp)) {
		    	  responseMap.remove(key0);
		      }

		    }
		    String sf=	util.coverMap2String(responseMap);			
		    // -- 验证签名
			boolean flag = false;
			RSASignUtil rsautil = new RSASignUtil(merchantCertPath);
			Map testMap = rsautil.coverString2Map(res);
			String serverSign = (String) testMap.get("serverSign");
	
			String serverCert = (String) testMap.get("serverCert");
	
			flag = rsautil.verify(sf,(String) util.getValue(res,"serverSign"),(String) util.getValue(res,"serverCert"),"UTF-8");*/
				//if(!flag){
					/*result.put("respCode", "01");
	                result.put("respMsg", "请求失败！");
	                result.put("orderId", payRequest.getOrderId());
		            result.put("amount", payRequest.getAmount());*/
				//}else {
				
			//}
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
                result.put("respCode", "01");
                result.put("respMsg", "请求失败！");
                result.put("orderId", payRequest.getOrderId());
	            result.put("amount", payRequest.getAmount());
	            e.printStackTrace();
    		}
	}

	/**
	 * 畅捷贷还代付查询
	 * 
	 */
	@Override
	public Map<String, String> paySelect(PayRequest payRequest, Map<String, String> result) {
		Map<String, String> map =new HashMap<>();
		log.info("注册参数进来了："+JSON.toJSONString(payRequest));
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(payRequest.getMerchantId());
		payRequest.setType("28");
		token(payRequest, map);
			//List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			log.info("给上游组装数据");
			String token;
			try {
				PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
				token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
				log.info("tiken:"+token);
				

				// 构建签名参数
				TreeMap<String, Object> signParams = new TreeMap<String, Object>();
				signParams.put("token", token);
				signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
				signParams.put("merchantUuid", payRequest.getMerchantUuid());
				signParams.put("queryType",2);
				signParams.put("reqFlowNo", payRequest.getOrderId());
				String merchantUuid = EncryptUtil.desEncrypt(payRequest.getMerchantUuid(), pmsBusinessPos.getKek());
				// 构建请求参数
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("token", token);
				jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
				jsonObj.put("merchantUuid", merchantUuid);
				jsonObj.put("queryType", 2);
				jsonObj.put("reqFlowNo", payRequest.getOrderId());
				jsonObj.put("sign", SignUtil.signByMap(pmsBusinessPos.getKek(), signParams));
				
				// 接口访问
				String jsonReq = jsonObj.toJSONString();
				System.out.println("jsonReq: " + jsonReq);
				String url =map.get("url")+"/gateway/api/queryWithdrawDepositResult";
				OkHttpClient client = new OkHttpClient();
				RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
				Request request = new Request.Builder().url(url).post(body).build();
				Response response = client.newCall(request).execute();

				String jsonRsp = response.body().string();
				System.out.println("jsonRsp: " + jsonRsp);

				BaseResMessage<QueryWithdrawDepositResult> res = null;
				if (response.isSuccessful()) {
					res = JSONObject.parseObject(jsonRsp, new TypeReference<BaseResMessage<QueryWithdrawDepositResult>>() {
					});
					
					System.out.println("\n接口响应内容：" + res.getData());
					
					if("000000".equals(res.getCode())){
						PmsWeixinMerchartInfo model =new PmsWeixinMerchartInfo();
						model.setPassword(res.getData().getMerchantUuid());
						model =weixinService.selectByEntity(model);
						result.put("respCode", "00");
						result.put("code", res.getData().getRemitStatus());
						result.put("walletType", res.getData().getWalletType());//400D0钱包，402是T1钱包
						result.put("payOrderNo", res.getData().getPayOrderNo());
						result.put("payTraceNo", res.getData().getPayTraceNo());
						result.put("reqFlowNo", res.getData().getReqFlowNo());
						result.put("merchantUuid", res.getData().getMerchantUuid());
						result.put("account", model.getAccount());
					}else{
						result.put("respCode", "01");
						result.put("respMsg", "失败");
					}
				} else {
					result.put("respCode", "01");
					result.put("respMsg", "失败");
					System.out.println("响应码: " + response.code());
					throw new IOException("Unexpected code " + response.message());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 请求参数
			
		return result;
	}

	@Override
	public Map<String, String> bill(PayRequest payRequest, Map<String, String> result) {
		Map<String, String> map =new HashMap<>();
		log.info("注册参数进来了："+JSON.toJSONString(payRequest));
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(payRequest.getMerchantId());
		token(payRequest, map);
			//List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			log.info("给上游组装数据");
			String token;
			try {
				PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
				token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
				log.info("tiken:"+token);
				

				// 构建签名参数
				TreeMap<String, Object> signParams = new TreeMap<String, Object>();
				signParams.put("token", token);
				signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
				signParams.put("flowReportTime", payRequest.getFlowReportTime());

				// 构建请求参数
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("token", token);
				jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
				jsonObj.put("flowReportTime", payRequest.getFlowReportTime());
				jsonObj.put("sign", SignUtil.signByMap(pmsBusinessPos.getKek(), signParams));

				// 接口访问
				String jsonReq = jsonObj.toJSONString();
				System.out.println("jsonReq: " + jsonReq);
				String url =map.get("url")+"/gateway/api/queryWalletFlowReport";
				OkHttpClient client = new OkHttpClient();
				RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
				Request request = new Request.Builder().url(url).post(body).build();
				Response response = client.newCall(request).execute();

				String jsonRsp = response.body().string();
				System.out.println("jsonRsp: " + jsonRsp);

				BaseResMessage<Bill> res = null;
				if (response.isSuccessful()) {
					res = JSONObject.parseObject(jsonRsp, new TypeReference<BaseResMessage<Bill>>() {
					});
					
					System.out.println("\n接口响应内容：" + res.getData());
					 BASE64Decoder decoder = new BASE64Decoder();
					String ExcelText = new String(decoder.decodeBuffer(res.getData().getExcelText()), "UTF-8");
					System.out.println();
					if("000000".equals(res.getCode())){
							result.put("excelText", ExcelText);
							result.put("totalNumber", res.getData().getTotalNumber());
					}else{
						result.put("respCode", "01");
						result.put("respMsg", "失败");
					}
				} else {
					result.put("respCode", "01");
					result.put("respMsg", "失败");
					System.out.println("响应码: " + response.code());
					throw new IOException("Unexpected code " + response.message());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 请求参数
			
		return result;
	}

	@Override
	public Map<String, String> balance(PayRequest payRequest, Map<String, String> result) {
		Map<String, String> map =new HashMap<>();
		log.info("参数进来了："+JSON.toJSONString(payRequest));
		PmsMerchantInfo merchantinfo = new PmsMerchantInfo();
		merchantinfo.setMercId(payRequest.getMerchantId());
		
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("mercid", payRequest.getMerchantId());// 商户编号
		paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());// 业务编号
		paramMap.put("oAgentNo", "100333");
		// 商户 网购 业务信息
		Map<String, String> resultMaps = merchantMineDao.queryBusinessInfo(paramMap);

		String quickRateType = resultMaps.get("QUICKRATETYPE");// 快捷支付费率类型

		// 获取o单第三方支付的费率
		AppRateConfig appRate = new AppRateConfig();
		appRate.setRateType(quickRateType);
		appRate.setoAgentNo("100333");
		AppRateConfig appRateConfig = appRateConfigDao.getByRateTypeAndoAgentNo(appRate);

		paramMap.put("mercid", payRequest.getMerchantId());
		paramMap.put("businesscode", TradeTypeEnum.merchantCollect.getTypeCode());
		// 微信支付
		paramMap.put("paymentcode", PaymentCodeEnum.moBaoQuickPay.getTypeCode());

		// 查询商户费率 和 最 低收款金额 支付方式是否开通 业务是否开通 等参数
		AppRateTypeAndAmount appRateTypeAndAmount = pmsAppAmountAndRateConfigDao
				.queryAmountAndStatus(paramMap);
		if (appRateTypeAndAmount != null) {
			String rateStr = appRateConfig.getRate(); // 商户费率
			token(payRequest, map);
			//List<PmsMerchantInfo> merchantList = pmsMerchantInfoDao.searchList(merchantinfo);
			log.info("给上游组装数据");
			String token;
			try {
				PmsBusinessPos pmsBusinessPos =	this.selectKey(payRequest.getMerchantId());
				token = EncryptUtil.desDecrypt(map.get("token"), pmsBusinessPos.getKek());
				log.info("tiken:"+token);
				String merchantUuidText = payRequest.getMerchantUuid();// 商户UUID，3DES加密
				String key =pmsBusinessPos.getKek();
				// 构建签名参数
				TreeMap<String, Object> signParams = new TreeMap<String, Object>();
				signParams.put("token", token);
				signParams.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
				signParams.put("merchantUuid",merchantUuidText);
				String merchantUuid = EncryptUtil.desEncrypt(merchantUuidText, key);
				// 构建请求参数
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("token", token);
				jsonObj.put("serverProviderCode", pmsBusinessPos.getBusinessnum());
				jsonObj.put("merchantUuid",merchantUuid);
				jsonObj.put("sign", SignUtil.signByMap(pmsBusinessPos.getKek(), signParams));

				// 接口访问
				String jsonReq = jsonObj.toJSONString();
				System.out.println("jsonReq: " + jsonReq);
				String url =map.get("url")+"/gateway/api/queryMerchantWallet";
				OkHttpClient client = new OkHttpClient();
				RequestBody body = RequestBody.create(PayUtil.JSON, jsonReq);
				Request request = new Request.Builder().url(url).post(body).build();
				Response response = client.newCall(request).execute();

				String jsonRsp = response.body().string();
				System.out.println("jsonRsp: " + jsonRsp);

				BaseResMessage<Balance> res = null;
				if (response.isSuccessful()) {
					res = JSONObject.parseObject(jsonRsp, new TypeReference<BaseResMessage<Balance>>() {
					});
					
					System.out.println("\n接口响应内容：" + res.getData());
					// BASE64Decoder decoder = new BASE64Decoder();
					//String ExcelText = new String(decoder.decodeBuffer(res.getData().getExcelText()), "UTF-8");
					System.out.println();
					result.put("merchantId", payRequest.getMerchantId());
					if("000000".equals(res.getCode())){
							result.put("respCode", "00");
							result.put("respMsg", "成功");
							result.put("merchantUuid",EncryptUtil.desDecrypt(res.getData().getMerchantUuid(), key) );
							log.info("quickPayWalletBalance:"+EncryptUtil.desDecrypt(res.getData().getQuickPayWalletBalance(), key));
							log.info("quickPayD0WalletWithdrawBalance:"+EncryptUtil.desDecrypt(res.getData().getQuickPayD0WalletWithdrawBalance(), key));
							log.info("quickPayT1WalletWithdrawBalance:"+EncryptUtil.desDecrypt(res.getData().getQuickPayT1WalletWithdrawBalance(), key));
							result.put("quickPayWalletBalance",EncryptUtil.desDecrypt(res.getData().getQuickPayWalletBalance(), key));
							result.put("quickPayD0WalletWithdrawBalance", EncryptUtil.desDecrypt(res.getData().getQuickPayD0WalletWithdrawBalance(), key));
							result.put("quickPayT1WalletWithdrawBalance",EncryptUtil.desDecrypt(res.getData().getQuickPayT1WalletWithdrawBalance(), key));
					}else{
						result.put("respCode", "01");
						result.put("respMsg", "失败");
					}
				} else {
					result.put("respCode", "01");
					result.put("respMsg", "失败");
					System.out.println("响应码: " + response.code());
					throw new IOException("Unexpected code " + response.message());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		return result;
	}
	
}

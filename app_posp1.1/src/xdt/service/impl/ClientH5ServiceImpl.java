package xdt.service.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

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
import xdt.dto.BaseUtil;
import xdt.model.AppRateConfig;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;
import xdt.model.ResultInfo;
import xdt.service.IClientH5Service;
import xdt.service.IPublicTradeVerifyService;
import xdt.tools.Base64;
import xdt.tools.Client;
import xdt.tools.ConstantsClient;
import xdt.tools.Tools;
import xdt.tools.Xml;
import xdt.tools.http.HttpClient;
import xdt.tools.rsa.Signatory;
import xdt.util.Constants;
import xdt.util.OrderStatusEnum;
import xdt.util.PaymentCodeEnum;
import xdt.util.TradeTypeEnum;
import xdt.util.UtilDate;
@Service

public class ClientH5ServiceImpl extends BaseServiceImpl implements IClientH5Service{
private static Logger log = LoggerFactory.getLogger(ClientH5ServiceImpl.class);
	
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
	private IPmsDaifuMerchantInfoDao pmsDaifuMerchantInfoDao;//代付
	
	/**
     * 【H5页面标准版】的商户订单下单接口，本接口比SDK的下单接口增加了【returnUrl】和【clientIp】参数
     * @param merchantId        商户代码
     * @param merchOrderId      商户订单号
     * @param amount            商户订单金额，单位为元，格式： nnnnnn.nn
     * @param orderDesc         商户订单描述    字符最大128，中文最多40个；参与签名：采用UTF-8编码
     * @param tradeTime         商户订单提交时间，格式：yyyyMMddHHmmss，超过订单超时时间未支付，订单作废；不提交该参数，采用系统的默认时间（从接收订单后超时时间为30分钟）
     * @param expTime           交易超时时间，格式：yyyyMMddHHmmss， 超过订单超时时间未支付，订单作废；不提交该参数，采用系统的默认时间（从接收订单后超时时间为30分钟）
     * @param notifyUrl         异步通知URL
     * @param returnUrl         同步通知URL
     * @param extData           商户保留信息； 通知结果时，原样返回给商户；字符最大128，中文最多40个；参与签名：采用UTF-8编码
     * @param miscData          订单扩展信息   根据不同的行业，传送的信息不一样；参与签名：采用UTF-8编码
     * @param notifyFlag        订单通知标志    0：成功才通知，1：全部通知（成功或失败）  不填默认为“1：全部通知”
     * @param clientIp          针对配置了防钓鱼的商户需要提交，商户服务器通过获取访问ip得到该参数
     * @param mercPriKey        商户签名的私钥
     * @param payecoPubKey      易联签名验证公钥
     * @param payecoUrl         易联服务器URL地址，只需要填写域名部分
     * @param retXml            通讯返回数据；当不是通讯错误时，该对象返回数据
     * @return  处理状态码： 0000 : 处理成功， 其他： 处理失败
     * @throws Exception        E101:通讯失败； E102：签名验证失败；  E103：签名失败；
     */
    public  String MerchantOrderH5(Client client, Xml retXml, PmsAppTransInfo pmsAppTransInfo,String orderid) throws Exception {
    	
    	PospTransInfo pospTransInfo = null;
		// 流水表是否需要更新的标记 0 insert，1：update
		int insertOrUpdateFlag = 0;
		log.info("***************进入payHandle5-14-3***************");
		// 生成上送流水号
		String transOrderId =orderid;//========================
		log.info("***************进入payHandle5-15***************");
		if ((pospTransInfo = pospTransInfoDAO
				.searchByOrderId(pmsAppTransInfo.getOrderid())) != null) {
			// 已经存在，修改流水号，设置pospsn为空
			log.info("订单号：" + pmsAppTransInfo.getOrderid()
					+ ",生成上送通道的流水号：" + transOrderId);
			pospTransInfo.setTransOrderId(transOrderId);
			pospTransInfo.setResponsecode("20");
			pospTransInfo.setPospsn("");
			insertOrUpdateFlag = 1;
			log.info("***************进入payHandle5-16***************");
		} else {
			// 不存在流水，生成一个流水
			pospTransInfo = InsertJournal(pmsAppTransInfo);
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
		}//-----------------------------------------------
		
    	
    	
    	pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());
    	//1微信
    			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
    			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
		//交易参数
    	pmsAppTransInfoDao.update(pmsAppTransInfo);
		String tradeCode = "PayOrder";
		String version = ConstantsClient.COMM_INTF_VERSION;
		//String signData="";
		/*if(client.getMiscData()!=null&&!client.getMiscData().equals("")){
			signData = "Version="+version+"&MerchantId=" + client.getMerchantId() + "&MerchOrderId=" + client.getMerchOrderId() 
					+ "&Amount=" + client.getAmount() + "&OrderDesc=" + client.getOrderDesc() + "&TradeTime=" + client.getTradeTime() + "&ExpTime="
					+ client.getExpTime() + "&NotifyUrl=" + client.getNotifyUrl() + "&ReturnUrl=" + client.getReturnUrl() + "&ExtData=" + client.getExtData()
					+ "&MiscData=" + client.getMiscData() + "&NotifyFlag=" + client.getNotifyFlag() + "&ClientIp=" + client.getClientIp();
		}else{
			signData = "Version="+version+"&MerchantId=" + client.getMerchantId() + "&MerchOrderId=" + client.getMerchOrderId() 
					+ "&Amount=" + client.getAmount() + "&OrderDesc=" + client.getOrderDesc() + "&TradeTime=" + client.getTradeTime() + "&ExpTime="
					+ client.getExpTime() + "&NotifyUrl=" + client.getNotifyUrl() + "&ReturnUrl=" + client.getReturnUrl() + "&ExtData=" + client.getExtData()
					+ "&NotifyFlag=" + client.getNotifyFlag() + "&ClientIp=" + client.getClientIp();
		}*/
		//进行数据签名  
		String signData = "Version="+version+"&MerchantId=" + client.getMerchantId() + "&MerchOrderId=" + client.getMerchOrderId() 
				+ "&Amount=" + client.getAmount() + "&OrderDesc=" + client.getOrderDesc() + "&TradeTime=" + client.getTradeTime() + "&ExpTime="
				+ client.getExpTime() + "&NotifyUrl=" + client.getNotifyUrl() + "&ReturnUrl=" + client.getReturnUrl() + "&ExtData=" + client.getExtData()
				+ "&MiscData=" + client.getMiscData() + "&NotifyFlag=" + client.getNotifyFlag() + "&ClientIp=" + client.getClientIp();
		
		// 私钥签名
		log.info("PrivateKey = " + client.getPriKey());
		log.info("SignData = " + signData);
		String sign = Signatory.sign(client.getPriKey(), signData, ConstantsClient.PAYECO_DATA_ENCODE);
		if(Tools.isStrEmpty(sign)){
			throw new Exception("E103");
		}
		log.info("sign=" + sign);

		//提交参数包含中文的需要做base64转码
		String orderDesc64 = Base64.encodeBytes(client.getOrderDesc().getBytes(ConstantsClient.PAYECO_DATA_ENCODE));
		String miscData64="";
		
		String extData64 = Base64.encodeBytes(client.getExtData().getBytes(ConstantsClient.PAYECO_DATA_ENCODE));
		//通知地址做URLEncoder处理
		String notifyUrlEn = URLEncoder.encode(client.getNotifyUrl(), ConstantsClient.PAYECO_DATA_ENCODE);
		String returnUrlEn = URLEncoder.encode(client.getReturnUrl(), ConstantsClient.PAYECO_DATA_ENCODE);
		if(client.getMiscData()!=null&&!client.getMiscData().equals("")){
			 miscData64 = Base64.encodeBytes(client.getMiscData().getBytes(ConstantsClient.PAYECO_DATA_ENCODE));
		}
		String data64 = "Version="+version+"&MerchantId=" + client.getMerchantId() + "&MerchOrderId=" + client.getMerchOrderId() 
                 + "&Amount=" + client.getAmount() + "&OrderDesc=" + orderDesc64 + "&TradeTime=" + client.getTradeTime() + "&ExpTime="
                 + client.getExpTime() + "&NotifyUrl=" + notifyUrlEn + "&ReturnUrl=" + returnUrlEn + "&ExtData=" + extData64
                 + "&MiscData=" + miscData64 + "&NotifyFlag=" + client.getNotifyFlag() + "&ClientIp=" + client.getClientIp();
		//通讯报文
		String url= client.getPayecoUrl() + "/ppi/merchant/itf.do"; //下订单URL
		data64 = "TradeCode=" + tradeCode + "&" + data64 + "&Sign=" + sign;
		HttpClient httpClient = new HttpClient();
		log.info("url = " + url + "?" + data64);
		String retStr = httpClient.send(url, data64, ConstantsClient.PAYECO_DATA_ENCODE, ConstantsClient.PAYECO_DATA_ENCODE,
				ConstantsClient.CONNECT_TIME_OUT, ConstantsClient.RESPONSE_TIME_OUT);
		log.info("retStr="+retStr);
		if(Tools.isStrEmpty(retStr)){
			throw new Exception("E101");
		}

		//返回数据的返回码判断
		retXml.setXmlData(retStr);
		String retCode = Tools.getXMLValue(retStr, "retCode");
		retXml.setRetCode(retCode);
		retXml.setRetMsg(Tools.getXMLValue(retStr, "retMsg"));
		if(!"0000".equals(retCode)){
			return retCode;
		}
		//获取返回数据
		String retVer = Tools.getXMLValue(retStr, "Version");
		String retMerchantId = Tools.getXMLValue(retStr, "MerchantId");
		String retMerchOrderId = Tools.getXMLValue(retStr, "MerchOrderId");
		String retAmount = Tools.getXMLValue(retStr, "Amount");
		String retTradeTime = Tools.getXMLValue(retStr, "TradeTime");
		String retOrderId = Tools.getXMLValue(retStr, "OrderId");
		String retVerifyTime = Tools.getXMLValue(retStr, "VerifyTime");
		String retSign = Tools.getXMLValue(retStr, "Sign");
		
		//设置返回数据
		retXml.setTradeCode(tradeCode);
		retXml.setVersion(retVer);
		retXml.setMerchantId(retMerchantId);
		retXml.setMerchOrderId(retMerchOrderId);
		retXml.setAmount(retAmount);
		retXml.setTradeTime(client.getTradeTime());
		retXml.setOrderId(retOrderId);
		retXml.setVerifyTime(retVerifyTime);
		retXml.setSign(retSign);
		
		//验证签名的字符串
		String backSign = "Version="+retVer+"&MerchantId=" + retMerchantId + "&MerchOrderId=" + retMerchOrderId 
				+ "&Amount=" + retAmount + "&TradeTime=" + retTradeTime + "&OrderId=" + retOrderId + "&VerifyTime=" + retVerifyTime;
		//验证签名
		retSign = retSign.replaceAll(" ", "+");
		boolean b = Signatory.verify(client.getPubKey(), backSign, retSign, ConstantsClient.PAYECO_DATA_ENCODE);
		log.info("PublicKey=" + client.getPubKey());
		log.info("data=" + backSign);
		log.info("Sign=" + retSign);
		log.info("验证结果=" + b);
		if(!b){
			throw new Exception("E102");
		}
		
		
		return retCode;
	}
    public Map<String, Object> MerchantOrderApi(Client client, Xml retXml, PmsAppTransInfo pmsAppTransInfo,String orderid) throws Exception {
    	// 查看当前交易是否已经生成了流水表
    		Map<String, Object> result =new HashMap<>();
    	
    			PospTransInfo pospTransInfo = null;
    			// 流水表是否需要更新的标记 0 insert，1：update
    			int insertOrUpdateFlag = 0;
    			log.info("***************进入payHandle5-14-3***************");
    			// 生成上送流水号
    			String transOrderId = client.getMerchOrderId();
    			log.info("***************进入payHandle5-15***************");
    			if ((pospTransInfo = pospTransInfoDAO
    					.searchByOrderId(client.getMerchOrderId())) != null) {
    				// 已经存在，修改流水号，设置pospsn为空
    				log.info("订单号：" + client.getMerchOrderId()
    						+ ",生成上送通道的流水号：" + transOrderId);
    				pospTransInfo.setTransOrderId(transOrderId);
    				pospTransInfo.setResponsecode("20");
    				pospTransInfo.setPospsn("");
    				insertOrUpdateFlag = 1;
    				log.info("***************进入payHandle5-16***************");
    			} else {
    				// 不存在流水，生成一个流水
    				pospTransInfo = InsertJournal(pmsAppTransInfo);
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
    			pmsAppTransInfo=pmsAppTransInfoDao.searchOrderInfo(pmsAppTransInfo.getOrderid());
    			log.info("请求交易生成二维码map");
    			// 组装上送参数
    			//1微信
    			pmsAppTransInfo.setPaymenttype(PaymentCodeEnum.moBaoQuickPay.getTypeName());
    			pmsAppTransInfo.setPaymentcode(PaymentCodeEnum.moBaoQuickPay.getTypeCode());
    	        pmsAppTransInfoDao.update(pmsAppTransInfo);
    	//---------------------------------
    	      //交易参数
    	        PmsBusinessPos pmsBusinessPos =selectKey(client.getMerchantId());
    			String tradeCode = "SendSmCode";
    			String version = ConstantsClient.COMM_INTF_VERSION;
    			String smId=client.getSmId();
    			String urls ="https://mobile.payeco.com";//https://testmobile.payeco.com
    			String tradeTime=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    			String verifyTradeCode="PayByAcc";
    			String industryNo=client.getIndustryNo()==null?"":client.getIndustryNo();
    			String smParam=industryNo+"|"+client.getName()+"|"+client.getCardNo()+"|"+client.getBankNo()+"|"+client.getAmount();
    		    //进行数据签名
    		    String signData = "Version="+version+"&MerchantId="+pmsBusinessPos.getBusinessnum() + "&SmId=" + smId 
    		    		+ "&MerchOrderId=" + client.getMerchOrderId() + "&TradeTime=" + tradeTime + "&MobileNo=" 
    		    		+ client.getMobileNo() + "&VerifyTradeCode=" + verifyTradeCode + "&SmParam=" + smParam;
    		   
    		    // 私钥签名
    			log.info("data=" + signData);
    		    String sign = Signatory.sign(client.getPriKey(), signData, ConstantsClient.PAYECO_DATA_ENCODE);
    			if(Tools.isStrEmpty(sign)){
    				throw new Exception("E103");
    			}
    			log.info("sign=" + sign);

    			//提交参数包含中文的需要做base64转码
    			String smParam64 = Base64.encodeBytes(smParam.getBytes(ConstantsClient.PAYECO_DATA_ENCODE));
    		    String data64 = "Version="+version+"&MerchantId=" + pmsBusinessPos.getBusinessnum() + "&SmId=" + smId 
    		    		+ "&MerchOrderId=" + client.getMerchOrderId() + "&TradeTime=" + tradeTime + "&MobileNo=" 
    		    		+ client.getMobileNo() + "&VerifyTradeCode=" + verifyTradeCode + "&SmParam=" + smParam64;

    			//通讯报文
    		    //-----------------------------------------client.getPayecoUrl()
    			String url= urls + "/ppi/merchant/itf.do"; //请求URL
    			data64 = "TradeCode=" + tradeCode + "&" + data64 + "&Sign=" + sign;
    			HttpClient httpClient = new HttpClient();
    			log.info("url = " + url + "?" + data64);
    			String retStr = httpClient.send(url, data64, ConstantsClient.PAYECO_DATA_ENCODE, ConstantsClient.PAYECO_DATA_ENCODE,
    					ConstantsClient.CONNECT_TIME_OUT, ConstantsClient.RESPONSE_TIME_OUT);
    			log.info("retStr="+retStr);
    			if(Tools.isStrEmpty(retStr)){
    				throw new Exception("E101");
    			}

    			//返回数据的返回码判断
    			retXml.setXmlData(retStr);
    			String retCode = Tools.getXMLValue(retStr, "retCode");
    			retXml.setRetCode(retCode);
    			retXml.setRetMsg(Tools.getXMLValue(retStr, "retMsg"));
    			if(!"0000".equals(retCode)){
    				result.put("respCode", retCode);
					result.put("respMsg",Tools.getXMLValue(retStr, "retMsg"));
    				return result;
    			}
    			//获取返回数据
    			String retVer = Tools.getXMLValue(retStr, "Version");
    			String retMerchantId = Tools.getXMLValue(retStr, "MerchantId");
    			String retSmId = Tools.getXMLValue(retStr, "SmId");
    			String retMerchOrderId = Tools.getXMLValue(retStr, "MerchOrderId");
    			String retTradeTime = Tools.getXMLValue(retStr, "TradeTime");
    			String retComplated = Tools.getXMLValue(retStr, "Complated");
    			String retRemain = Tools.getXMLValue(retStr, "Remain");
    			String retExpTime = Tools.getXMLValue(retStr, "ExpTime");
    			String retSign = Tools.getXMLValue(retStr, "Sign");  //该凭证号还可以再次发送的短信次数。无值为不限制
    			
    			//设置返回数据
    			retXml.setTradeCode(tradeCode);
    			retXml.setVersion(retVer);
    			retXml.setMerchantId(retMerchantId);
    			retXml.setSmId(retSmId);
    			retXml.setMerchOrderId(retMerchOrderId);
    			retXml.setTradeTime(retTradeTime);
    			retXml.setComplated(retComplated);
    			retXml.setRemain(retRemain);
    			retXml.setExpTime(retExpTime);
    			retXml.setSign(retSign);
    			result.put("merchantId",client.getMerchantId());
    			result.put("smId",retSmId );
    			result.put("merchOrderId",client.getMerchOrderId() );
    			result.put("tradeTime",retTradeTime );
    			result.put("complated", retComplated);
    			result.put("remain",retRemain );
    			result.put("expTime",retExpTime );
    			result.put("cardNo", client.getCardNo());
    			result.put("name", client.getName());
    			result.put("mobileNo", client.getMobileNo());
    			result.put("bankNo", client.getBankNo());
    			result.put("amount", client.getAmount());
    			result.put("tradeTime", tradeTime);
    			result.put("industryNo", client.getIndustryNo());
    			//验证签名的字符串
    			String backSign = "Version="+retVer+"&MerchantId=" + retMerchantId +"&SmId=" + retSmId 
    					+ "&MerchOrderId=" + retMerchOrderId + "&TradeTime="+retTradeTime 
    					+ "&Complated=" + retComplated + "&Remain=" + retRemain + "&ExpTime=" + retExpTime;

    			//验证签名
    			retSign = retSign.replaceAll(" ", "+");
    			boolean b = Signatory.verify(xdt.tools.Constants.PAYECO_RSA_PUBLIC_KEY, backSign, retSign, ConstantsClient.PAYECO_DATA_ENCODE);
    			log.info("PublicKey=" + client.getPriKey());
    			log.info("data=" + backSign);
    			log.info("Sign=" + retSign);
    			log.info("验证结果=" + b);
    			if(!b){
    				throw new Exception("E102");
    			}
    			return result;
    }
	
    public Map<String, Object> ReceiveInformationApiPay(Client client) throws Exception{
    	Map<String, Object> result=new HashMap<>();
    	OriginalOrderInfo info = new OriginalOrderInfo();
    	info.setBgUrl(client.getNotifyUrl());
    	info.setOrderId(client.getMerchOrderId());
    	originalDao.update(info);
    	
    	// 设置参数
    			String amount = client.getAmount();
    			String orderDesc = client.getOrderDesc();
    			String extData = client.getExtData(); //
    			//以下扩展参数是按互联网金融行业填写的；其他行业请参考接口文件说明进行填写
    			//String miscData =client.getMobileNo()+"|0||"+client.getName()+"|"+client.getCardNo()+"|"+client.getBankNo()+"|"+client.getCity()+"|||||||";
    			String miscData = client.getMobileNo()+"|0||"+client.getName()+"|"+client.getCardNo()+"|"+client.getBankNo()+"|"+client.getCity()+"||||";  //互联网金融
    			String merchOrderId = client.getMerchOrderId(); // 订单号
    			String notifyUrl = BaseUtil.url+"/clientH5Controller/Notify.action"; // 封装的API会自动做URLEncode
    			String tradeTime =client.getTradeTime();
    			String expTime = ""; // 采用系统默认的订单有效时间
    			String notifyFlag = "1";
    			String industryId =client.getIndustryNo()==null?"":client.getIndustryNo();
    			String smId = client.getSmId();
    			String smCode = client.getSmCode();
    			//交易参数
    			String tradeCode = "PayByAcc";
    			String version = ConstantsClient.COMM_INTF_VERSION;
    			Double d =Double.parseDouble(amount);
    			d =d/100;
				log.info("转换的金额："+amount);
				client.setAmount(amount+"");
    			PmsBusinessPos pmsBusinessPos =selectKey(client.getMerchantId());
    			//进行数据签名  
    			String signData = "Version="+version+"&MerchantId=" + pmsBusinessPos.getBusinessnum() + "&IndustryId=" + industryId 
    					+ "&MerchOrderId=" + merchOrderId + "&Amount=" + d.toString() + "&OrderDesc=" + orderDesc 
    					+ "&TradeTime=" + tradeTime + "&ExpTime=" + expTime + "&NotifyUrl=" + notifyUrl 
    					+ "&ExtData=" + extData + "&MiscData=" + miscData + "&NotifyFlag=" + notifyFlag
    					+ "&SmId=" + smId + "&SmCode=" + smCode;
    			
    			// 私钥签名
    			log.info("PrivateKey = " + pmsBusinessPos.getKek());
    			log.info("SignData = " + signData);
    			String sign = Signatory.sign(pmsBusinessPos.getKek(), signData, ConstantsClient.PAYECO_DATA_ENCODE);
    			if(Tools.isStrEmpty(sign)){
    				throw new Exception("E103");
    			}
    			log.info("sign=" + sign);

    			//提交参数包含中文的需要做base64转码
    			String orderDesc64 = Base64.encodeBytes(orderDesc.getBytes(ConstantsClient.PAYECO_DATA_ENCODE));
    			String extData64 = Base64.encodeBytes(extData.getBytes(ConstantsClient.PAYECO_DATA_ENCODE));
    			String miscData64 = Base64.encodeBytes(miscData.getBytes(ConstantsClient.PAYECO_DATA_ENCODE));
    			//通知地址做URLEncoder处理
    			String notifyUrlEn = URLEncoder.encode(notifyUrl, ConstantsClient.PAYECO_DATA_ENCODE);
    			
    			String data64 = "Version="+version+"&MerchantId=" + pmsBusinessPos.getBusinessnum() + "&IndustryId=" + industryId 
    					    + "&MerchOrderId=" + merchOrderId + "&Amount=" + d.toString() + "&OrderDesc=" + orderDesc64 
    					    + "&TradeTime=" + tradeTime + "&ExpTime=" + expTime + "&NotifyUrl=" + notifyUrlEn 
    					    + "&ExtData=" + extData64 + "&MiscData=" + miscData64 + "&NotifyFlag=" + notifyFlag
    	   				 	+ "&SmId=" + smId + "&SmCode=" + smCode;

    			//通讯报文
    			String url= "https://mobile.payeco.com/ppi/merchant/itf.do"; //请求URL
    			data64 = "TradeCode=" + tradeCode + "&" + data64 + "&Sign=" + sign;
    			HttpClient httpClient = new HttpClient();
    			log.info("url = " + url + "?" + data64);
    			String retStr = httpClient.send(url, data64, ConstantsClient.PAYECO_DATA_ENCODE, ConstantsClient.PAYECO_DATA_ENCODE,
    					ConstantsClient.CONNECT_TIME_OUT, ConstantsClient.RESPONSE_TIME_OUT);
    			log.info("retStr="+retStr);
    			if(Tools.isStrEmpty(retStr)){
    				throw new Exception("E101");
    			}

    			//返回数据的返回码判断
    			String retCode = Tools.getXMLValue(retStr, "retCode");
    			String retMsg = Tools.getXMLValue(retStr, "retMsg");
    			if(!"0000".equals(retCode)){
    				result.put("respCode", retCode);
					result.put("respMsg",retMsg);
    				return result;
    			}
    			//获取返回数据
    			String retVer = Tools.getXMLValue(retStr, "Version");
    			String retMerchantId = Tools.getXMLValue(retStr, "MerchantId");
    			String retMerchOrderId = Tools.getXMLValue(retStr, "MerchOrderId");
    			String retAmount = Tools.getXMLValue(retStr, "Amount");
    			String retExtData = Tools.getXMLValue(retStr, "ExtData");
    			if (retExtData != null){
    				retExtData = retExtData.replaceAll(" ", "+");
    				retExtData = new String (Base64.decode(retExtData), ConstantsClient.PAYECO_DATA_ENCODE);
    			}
    			String retOrderId = Tools.getXMLValue(retStr, "OrderId");
    			String retStatus = Tools.getXMLValue(retStr, "Status");
    			String retPayTime = Tools.getXMLValue(retStr, "PayTime");
    			String retSettleDate = Tools.getXMLValue(retStr, "SettleDate");
    			String retSign = Tools.getXMLValue(retStr, "Sign");
    			//设置返回数据
    			  
    			//验证签名的字符串
    			String backSign = "Version="+retVer+"&MerchantId=" + retMerchantId + "&MerchOrderId=" + retMerchOrderId 
    			  + "&Amount=" + retAmount + "&ExtData=" + retExtData + "&OrderId=" + retOrderId
    			  + "&Status=" + retStatus + "&PayTime=" + retPayTime + "&SettleDate=" + retSettleDate;

    			//验证签名
    			retSign = retSign.replaceAll(" ", "+");
    			boolean b = Signatory.verify(xdt.tools.Constants.PAYECO_RSA_PUBLIC_KEY, backSign, retSign, ConstantsClient.PAYECO_DATA_ENCODE);
    			log.info("PublicKey=" + xdt.tools.Constants.PAYECO_RSA_PUBLIC_KEY);
    			log.info("data=" + backSign);
    			log.info("Sign=" + retSign);
    			log.info("验证结果=" + b);
    			if(!b){
    				throw new Exception("E102");
    			}
    			
    			result.put("respCode", "00");
				result.put("respMsg","请求成功");
				result.put("merchantId", client.getMerchantId());
				result.put("merchOrderId", retMerchOrderId);
				result.put("amount", retAmount);
				result.put("extData", retExtData);
				result.put("status", retStatus);
    	  return result;
    }
	
    /**
     * 【H5页面标准版】的生成订单支付重定向地址
	 * @param payecoUrl		：	易联服务器URL地址，只需要填写域名部分 
     * @param retXml 下单成功后的通讯返回数据
     * @return
     */
    public String getPayInitRedirectUrl(Client client, Xml retXml) {
        String tradeId = "h5Init";
        String version = ConstantsClient.COMM_INTF_VERSION;
        String merchantId = retXml.getMerchantId();         //商户代码
        String merchOrderId = retXml.getMerchOrderId();     //商户订单号
        String amount = retXml.getAmount();                 //商户订单金额，单位为元，格式： nnnnnn.nn
        String tradeTime = retXml.getTradeTime();           //商户订单提交时间
        String orderId = retXml.getOrderId();               //易联订单号
        String verifyTime = retXml.getVerifyTime();         //验证时间戳
        String sign = retXml.getSign();                     //签名,下单时返回的签名
        
        String datas = "Version=" + version + "&MerchantId=" + merchantId
                + "&MerchOrderId=" + merchOrderId + "&Amount=" + amount
                + "&TradeTime=" + tradeTime + "&OrderId=" + orderId
                + "&VerifyTime=" + verifyTime + "&Sign=" + sign;
       
        String redirectUrl = client.getPayecoUrl() + "/ppi/h5/plugin/itf.do?tradeId=" + tradeId + "&" + datas;
        
        log.info("redirectUrl=" + redirectUrl);
        
        return redirectUrl;
    }	
	
	/**
	 * 录入交易流水 并记算费率
	 * @throws Exception 
	 */
	public PospTransInfo InsertJournal(PmsAppTransInfo pmsAppTransInfo) throws Exception {
		log.info("----插入流水开始----");
		PospTransInfo pospTransInfo = new PospTransInfo();
		Integer id = pospTransInfoDAO.getNextTransid();
        if(id != null && id != 0 ){
            pospTransInfo.setId(id);
        }else{
            log.info("根据订单生成流水失败，orderid："+pmsAppTransInfo.getOrderid());
            return null;
        }
		//获取通道的标准费率 END

        //设置主机交易流水号
        pospTransInfo.setSysseqno(null);
        //设置宣称费率
        pospTransInfo.setTransfee2(null);
        //设置通道费率
        pospTransInfo.setTransfee4(null);
        //设置实际佣金
        pospTransInfo.setTransfee1(null);
        //设置消费冲正原因
        pospTransInfo.setReason(null);
        //设置说明
        pospTransInfo.setRemark(pmsAppTransInfo.getTradetype() + "  金额：" + pmsAppTransInfo.getFactamount());
        //设置SIM卡
        pospTransInfo.setSimId(null);
        //设置 TAC
        pospTransInfo.setTac(null);
        //设置银行编码
        pospTransInfo.setBnkCd(null);
        //设置平台流水奥  这里默认设置第三方订单号
        pospTransInfo.setPospsn(pmsAppTransInfo.getPortorderid());
        //设置卡有效期
        pospTransInfo.setCardvaliddate(null);
        //设置通道pos终端号
        pospTransInfo.setBuspos(null);
        //设置pos平台交易吗
        pospTransInfo.setPospservicecode(null);
        //设置冲正流水
        pospTransInfo.setCancelflag(null);
        //设置商户号
        pospTransInfo.setMerchantcode(pmsAppTransInfo.getMercid());
        //设置补录时记录上传的终端机流水号
        pospTransInfo.setTerminalsn(null);
        //设置交易上送帐期
        pospTransInfo.setSenddate(new Date());
        //服务网点PIN码
        pospTransInfo.setCounterpin(null);
        //设置渠道号  03：手机
        pospTransInfo.setChannelno("03");
        //设置银行名称
        pospTransInfo.setBnkNm(null);
        //设置posid
        pospTransInfo.setPosid(null);
        //设置交易码  默认都为消费业务
        pospTransInfo.setTranscode("000000");
        //设置交易安全控制信息
        pospTransInfo.setTranssecuritycontrol(null);
        //设置卡类型
        pospTransInfo.setCrdTyp(null);
        //设置卡号
        pospTransInfo.setCardno(null);
        //设置真正的交易类型     交易码 +交易类型+支付方式
        pospTransInfo.setSearchTransCode("000000" + pmsAppTransInfo.getTradetypecode()+pmsAppTransInfo.getPaymentcode());
        //设置pos交易日期
        pospTransInfo.setTransdate(UtilDate.getDate());
        //设置pos交易时间
        pospTransInfo.setTranstime(UtilDate.getDateTime());
        //设置批量结算结果标志
        pospTransInfo.setSettlementflag(null);
        //设置最近批结算ID
        pospTransInfo.setSettlementid(null);
        //设置授权码
        pospTransInfo.setAuthoritycode(null);
        //设置是否自清 默认自清
        pospTransInfo.setIsClearSelf(null);
        //设置交易响应标志 00-成功
        pospTransInfo.setResponsecode(null);
        /*if(pmsAppTransInfo.getStatus().equals("0")){
            pospTransInfo.setResponsecode("00");
        }else{
            pospTransInfo.setResponsecode(null);
        }*/
        //设置订单id
        pospTransInfo.setOrderId(pmsAppTransInfo.getOrderid());
        //设置通道商户编码  商户编码不设置
        pospTransInfo.setBusinfo(null);
        //设置附加费用
        pospTransInfo.setAddfee(null);
        //设置刷卡费率  当前处理为调用第三方处理，刷卡费率不设置
        pospTransInfo.setPremiumrate(null);
        //设置原始交易记录报文id
        pospTransInfo.setPfmtid(null);
       //服务网点输入方式
        pospTransInfo.setInputtype(null);
        // 0-脱机POS上送流水，1-联机消费流水
        pospTransInfo.setTransstatus(null);
        //设置基站信息
        pospTransInfo.setStationInfo(null);
        //设置交易时间间隔   这里先不处理，没有发现需要用到的地方
        pospTransInfo.setInterVal(null);
        //设置关联路由id
        pospTransInfo.setRouteid(null);
        //设置交易消息类型    交易类型+支付方式
        pospTransInfo.setMsgtype( pmsAppTransInfo.getTradetypecode() + pmsAppTransInfo.getPaymentcode() );
        //设置发生额
        pospTransInfo.setTransamt(new BigDecimal(pmsAppTransInfo.getFactamount()));
        //设置终端号
        pospTransInfo.setPosterminalid(null);
        //设置操作员id
        pospTransInfo.setOperid(null);
        //设置POS服务平台代码
        pospTransInfo.setPospid(null);
        //设置货币代码
        pospTransInfo.setCurrencycode(null);
        //结算日期
        pospTransInfo.setBalancedate(null);
        // PSAM卡号
        pospTransInfo.setPsamno(null);
        //个人标识码
        pospTransInfo.setPersonalid(null);
        //卡号
        pospTransInfo.setCrdNm(null);
        //设置冲正标志   0-正常交易，1-冲正交易，2-被冲正交易
        pospTransInfo.setCancelflag(0);
        //设置冻结状态
        pospTransInfo.setFreezeState(null);
        //设置终端序列号
        pospTransInfo.setPossn(null);
        //设置服务网点条件码
        pospTransInfo.setConuterconditioncode(null);
        //是否App交易
        pospTransInfo.setIsapp(1);
        //设置支付方式
        pospTransInfo.setPaymentType(pmsAppTransInfo.getPaymentcode());
        //设置批次号
        pospTransInfo.setBatno(null);
        //O单编号
        pospTransInfo.setoAgentNo(pmsAppTransInfo.getoAgentNo());
		
		
		return pospTransInfo;
	}

	/**
	 * 易联跳转点击生成订单跳入方法
	 */
	@Override
	public String ReceiveInformationH5(Client client, Xml retXml) throws Exception {
		String retCode="";
		Map<String, Object> result = new HashMap<String, Object>();
		log.info("处理二维码生成");

		log.info("根据商户号查询");

		String out_trade_no = "";// 订单号
		out_trade_no = client.getMerchOrderId(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = client.getMerchantId1();

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
				oriInfo.setMerchantOrderId(client.getMerchOrderId());//---------------------------
				oriInfo.setPid(client.getMerchantId1());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfo(client, out_trade_no, mercId);
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
					// 微信支付
					paramMap.put("paymentcode", PaymentCodeEnum.weixinPay.getTypeCode());

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
										PaymentCodeEnum.weixinPay.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(client.getAmount());// 收款金额
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
												.payTypeVerifyMer(PaymentCodeEnum.weixinPay, mercId);
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
														String totalAmount = client.getAmount(); // 交易金额
														String name=client.getName();

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo,name);

														if (appTransInfo != null) {
															// 处理生成二维码
															log.info("提交的金额是："+totalAmount);
															double amount =Double.parseDouble(totalAmount);
															amount =amount/100;
															log.info("转换的金额："+amount);
															client.setAmount(amount+"");
															 retCode = MerchantOrderH5(client, retXml,appTransInfo,out_trade_no);
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

		return retCode;
		
		
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
	private int saveOriginAlInfo(Client client, String orderid, String mercId)
			throws Exception {
		// 插入原始信息
		OriginalOrderInfo info = new OriginalOrderInfo();
		info.setPid(mercId);
		info.setByUser(client.getRealName());
		info.setBankNo(client.getCardNo());
		info.setMerchantOrderId(client.getMerchOrderId());
		info.setOrderId(orderid);
		info.setOrderTime(UtilDate.getOrderNum());
		info.setPayType(client.getProduct());
		//想要传服务器要改实体
		info.setBgUrl(client.getUrl());
		info.setPageUrl(client.getReUrl());
		Double amt = Double.parseDouble(client.getAmount());// 单位分
		amt /= 100;
		DecimalFormat df = new DecimalFormat("######0.00");

		info.setOrderAmount(df.format(amt));

		return originalDao.insert(info);
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
	public PmsAppTransInfo insertOrder(String orderid, String payamount, String mercId, String rateStr, String oAgentNo,String name)
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
		pmsAppTransInfo.setBankname(name);
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
				Double ss =Double.parseDouble(merchantinfo.getCounter());
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
				log.info("订单入库失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。订单详细信息：" + sendString);
				throw new RuntimeException("手动抛出");
			}
		} catch (Exception e) {
			log.info("订单入库失败， 订单号：" + orderid + "，结束时间：" + UtilDate.getDateFormatter() + "。订单详细信息：" + sendString, e);
			throw new RuntimeException("手动抛出");
		}
		return pmsAppTransInfo;

	}
	

	@Override
	public void otherInvokeH5(Client client) throws Exception {
		log.info("返回的参数："+client);
		log.info("**************进入修改方法*************************");
		// 流水表transOrderId
		String transOrderId = client.getMerchOrderId();
		log.info("异步通知回来的订单号:" + transOrderId);
		// 流水信息
		PospTransInfo pospTransInfo = pospTransInfoDAO.searchBytransOrderId(transOrderId);
		log.info("流水表信息：" + pospTransInfo);
		// 订单信息
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(pospTransInfo.getOrderId());
		log.info("订单表信息：" + pmsAppTransInfo);
		// 查询结果成功
		if ("02".equals(client.getStatus())) {
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
				pospTransInfo.setPospsn(client.getOrderId());
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} else {
			// 支付失败
			pmsAppTransInfo.setStatus(OrderStatusEnum.payFail.getStatus());
			pmsAppTransInfo.setThirdPartResultCode("1");
			pmsAppTransInfo.setFinishtime(UtilDate.getDateFormatter());
			// 修改订单
			int updateAppTrans = pmsAppTransInfoDao.update(pmsAppTransInfo);
			if (updateAppTrans == 1) {
				// 更新流水表
				pospTransInfo.setResponsecode("02");
				pospTransInfo.setPospsn(client.getMerchOrderId());
				log.info("更新流水");
				log.info("流水表信息：" + pospTransInfo);
				pospTransInfoDAO.updateByOrderId(pospTransInfo);
			}
		} 

		
	}

	@Override
	@Transactional
	public int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo ) throws Exception{
		log.info("代付实时填金:"+JSON.toJSON(originalInfo));
		DecimalFormat df =new DecimalFormat("#.00");
		PmsMerchantInfo pmsMerchantInfo =new PmsMerchantInfo();
		PmsDaifuMerchantInfo pmsDaifuMerchantInfo=new PmsDaifuMerchantInfo();
		PmsMerchantInfo merchantInfo =pmsMerchantInfoDao.selectMercByMercId(originalInfo.getPid());
		PmsAppTransInfo pmsAppTransInfo = pmsAppTransInfoDao.searchOrderInfo(originalInfo.getOrderId());
		pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
		PmsDaifuMerchantInfo daifuMerchantInfo =pmsDaifuMerchantInfoDao.selectByDaifuMerchantInfo(pmsDaifuMerchantInfo);
		if(daifuMerchantInfo!=null){
			return 0;
		}
		if("0".equals(merchantInfo.getOpenPay())){
			//手续费
			Double poundage =Double.parseDouble(pmsAppTransInfo.getPoundage());
			poundage=Double.parseDouble(df.format(poundage));
			String position= merchantInfo.getPosition();
			Double amount=Double.parseDouble(originalInfo.getOrderAmount());
			BigDecimal  positions =new BigDecimal(position);
			Double ds =positions.doubleValue();
			Double dd =amount*100+ds;
			dd =dd-poundage;
			log.info("来了1---------");
			pmsMerchantInfo.setMercId(originalInfo.getPid());
			pmsMerchantInfo.setPosition(df.format(dd));
			//商户号
			pmsDaifuMerchantInfo.setMercId(originalInfo.getPid());
			//订单号
			pmsDaifuMerchantInfo.setBatchNo(originalInfo.getOrderId());
			//总金额
			pmsDaifuMerchantInfo.setAmount((Double.parseDouble(originalInfo.getOrderAmount()))+"");
			//状态
			pmsDaifuMerchantInfo.setResponsecode("00");
			//备注
			pmsDaifuMerchantInfo.setRemarks("D0");
			//记录描述
			pmsDaifuMerchantInfo.setRecordDescription("订单号:"+originalInfo.getOrderId()+"交易金额:"+originalInfo.getOrderAmount());
			//交易类型
			pmsDaifuMerchantInfo.setTransactionType(pmsAppTransInfo.getPaymenttype());
			//发生额
			pmsDaifuMerchantInfo.setPayamount((Double.parseDouble(originalInfo.getOrderAmount()))+"");
			//账户余额
			pmsDaifuMerchantInfo.setPosition(df.format(dd));
			//手续费
			pmsDaifuMerchantInfo.setPayCounter(poundage/100+"");
			pmsDaifuMerchantInfo.setOagentno("100333");
			log.info("来了2---------");
			//交易时间
			//pmsDaifuMerchantInfo.setCreationdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			int s=pmsDaifuMerchantInfoDao.insert(pmsDaifuMerchantInfo);
			log.info("---s:"+s);
			log.info("来了3---------");
			int i =pmsMerchantInfoDao.UpdatePmsMerchantInfo(pmsMerchantInfo);
			log.info("---i:"+i);
			return i;
		}else{
			log.info("此商户未开通代付！！");
		}
		return 0;
	}
	@Override
	public Map<String, Object> ReceiveInformationApi(Client client, Xml retXml)
			throws Exception {
		String retCode="";
		Map<String, Object> result = new HashMap<String, Object>();
		log.info("处理二维码生成");

		log.info("根据商户号查询");

		String out_trade_no = "";// 订单号
		out_trade_no = client.getMerchOrderId(); // 10业务号2业务细; 订单号
																// 现根据规则生成订单号
		log.info("根据商户号查询");
		String mercId = client.getMerchantId();

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
				oriInfo.setMerchantOrderId(client.getMerchOrderId());//---------------------------
				oriInfo.setPid(client.getMerchantId());
				oriInfo = originalDao.selectByOriginal(oriInfo);

				if (oriInfo != null) {
					log.error("下单重复");
					result.put("respCode", "16");
					result.put("respMsg", "下单重复");
				} else if ("60".equals(merchantinfo.getMercSts())) {
					// 判断是否为正式商户

					saveOriginAlInfo(client, out_trade_no, mercId);
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
					// 微信支付
					paramMap.put("paymentcode", PaymentCodeEnum.weixinPay.getTypeCode());

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
										PaymentCodeEnum.weixinPay.getTypeCode());

								if (!payCheckResult.getErrCode().equals("0")) {
									// 支付方式时候开通总开关 禁用
									result.put("respCode", "07");
									result.put("respMsg", "此支付方式暂时关闭");
									log.info("此支付方式暂时关闭");
								} else {

									BigDecimal payAmt = new BigDecimal(client.getAmount());// 收款金额
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
												.payTypeVerifyMer(PaymentCodeEnum.moBaoQuickPay, mercId);
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
														String totalAmount = client.getAmount(); // 交易金额
														String name=client.getName();

														PmsAppTransInfo appTransInfo = this.insertOrder(out_trade_no,
																totalAmount, mercId, rateStr, oAgentNo,name);

														if (appTransInfo != null) {
															// 处理生成二维码
															log.info("提交的金额是："+totalAmount);
															double amount =Double.parseDouble(totalAmount);
															amount =amount/100;
															log.info("转换的金额："+amount);
															client.setAmount(amount+"");
															result = MerchantOrderApi(client, retXml,appTransInfo,out_trade_no);
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


}

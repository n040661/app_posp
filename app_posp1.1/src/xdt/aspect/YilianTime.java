package xdt.aspect;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

import xdt.dao.IPmsAppTransInfoDao;
import xdt.dao.IPmsDaifuMerchantInfoDao;
import xdt.dao.IPospTransInfoDAO;
import xdt.model.MsgBean;
import xdt.model.MsgBody;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.service.IClientCollectionPayService;
import xdt.service.IClientH5Service;
import xdt.service.IPmsAppTransInfoService;
import xdt.service.IPmsDaifuMerchantInfoService;
import xdt.service.ISxfService;
import xdt.service.JsdsQrCodeService;
import xdt.tools.Base64;
import xdt.tools.Constants;
import xdt.tools.ConstantsClient;
import xdt.tools.Tools;
import xdt.tools.Xml;
import xdt.tools.http.HttpClient;
import xdt.tools.rsa.Signatory;
@Component
public class YilianTime {
	
	Logger log = Logger.getLogger(JsTimeTask.class);
	@Resource
	private IPmsDaifuMerchantInfoService daifuMerchantInfoService;
	@Resource
	private IPmsAppTransInfoService pmsAppTransInfoService;
	@Resource
	private JsdsQrCodeService jsdsQrCodeService;
	@Resource
	public IPmsAppTransInfoDao pmsAppTransInfoDao;
	@Resource
	public IPospTransInfoDAO pospTransInfoDAO;
	@Resource 
	private  IClientH5Service ClientH5ServiceImpl;
	@Resource
	private IClientCollectionPayService clientCollectionPayService;
	@Resource
	private ISxfService iSxfService;
	@Transactional
	public void yilianSelect() throws Exception{
		List<PmsDaifuMerchantInfo> list = daifuMerchantInfoService.selectYLDaifu();
		
		for (PmsDaifuMerchantInfo pmsDaifuMerchantInfo : list) {
			MsgBean req_bean = new MsgBean();
			req_bean.setVERSION("2.1");
			req_bean.setMSG_TYPE("100002");
			req_bean.setBATCH_NO(pmsDaifuMerchantInfo.getBatchNo());//同代付交易请求批次号
			req_bean.setUSER_NAME(Constants.user_name);//系统后台登录名
			String res = clientCollectionPayService.sendAndRead(clientCollectionPayService.signANDencrypt(req_bean));
			
			MsgBean res_bean = clientCollectionPayService.decryptANDverify(res);
			if("0000".equals(res_bean.getTRANS_STATE())){
				List<MsgBody> BODYS=res_bean.getBODYS();
				MsgBody body =BODYS.get(0);
				int i;
				if("0000".equals(body.getPAY_STATE())){
					i= iSxfService.UpdateDaifu(pmsDaifuMerchantInfo.getBatchNo(), "00");
				}else if("T452".equals(body.getPAY_STATE())||"T206".equals(body.getPAY_STATE())||"00A4".equals(body.getPAY_STATE())||"0094".equals(body.getPAY_STATE())||"0068".equals(body.getPAY_STATE())){
					i=iSxfService.UpdateDaifu(pmsDaifuMerchantInfo.getBatchNo(), "200");
				}else{
					i=iSxfService.UpdateDaifu(pmsDaifuMerchantInfo.getBatchNo(), "01");
				}
				System.out.println(i);
			}
			
			System.out.println(JSON.toJSON(res_bean));
		}
		
		
	}
	public  String OrderQuery(String merchantId, String merchOrderId, String tradeTime, 
			String priKey, String pubKey, String payecoUrl, Xml retXml) 
			throws Exception{
		//交易参数
		String tradeCode = "QueryOrder";
		String version = ConstantsClient.COMM_INTF_VERSION;
		
	    //进行数据签名
	    String signData = "Version="+version+"&MerchantId=" + merchantId + "&MerchOrderId=" + merchOrderId 
	             + "&TradeTime=" + tradeTime;
	    
	    // 私钥签名
		log.info("PrivateKey=" + priKey);
		log.info("data=" + signData);
	    String sign = Signatory.sign(priKey, signData, ConstantsClient.PAYECO_DATA_ENCODE);
		if(Tools.isStrEmpty(sign)){
			throw new Exception("E103");
		}
		log.info("sign=" + sign);

		//通讯报文
	    String url= payecoUrl + "/ppi/merchant/itf.do?TradeCode="+tradeCode; //请求URL
	    signData = signData + "&Sign=" + sign;
	    HttpClient httpClient = new HttpClient();
	    log.info("url="+url+"&"+signData);
		String retStr = httpClient.send(url, signData, ConstantsClient.PAYECO_DATA_ENCODE, ConstantsClient.PAYECO_DATA_ENCODE,
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
		retXml.setTradeCode(tradeCode);
		retXml.setVersion(retVer);
		retXml.setMerchantId(retMerchantId);
		retXml.setMerchOrderId(retMerchOrderId);
		retXml.setAmount(retAmount);
		retXml.setExtData(retExtData);
		retXml.setOrderId(retOrderId);
		retXml.setStatus(retStatus);
		retXml.setPayTime(retPayTime);
		retXml.setSettleDate(retSettleDate);
		retXml.setSign(retSign);
		  
		//验证签名的字符串
		String backSign = "Version="+retVer+"&MerchantId=" + retMerchantId + "&MerchOrderId=" + retMerchOrderId 
		  + "&Amount=" + retAmount + "&ExtData=" + retExtData + "&OrderId=" + retOrderId
		  + "&Status=" + retStatus + "&PayTime=" + retPayTime + "&SettleDate=" + retSettleDate;

		//验证签名
		retSign = retSign.replaceAll(" ", "+");
		boolean b = Signatory.verify(pubKey, backSign, retSign, ConstantsClient.PAYECO_DATA_ENCODE);
		log.info("PublicKey=" + pubKey);
		log.info("data=" + backSign);
		log.info("Sign=" + retSign);
		log.info("验证结果=" + b);
		if(!b){
			throw new Exception("E102");
		}
		return retStr;
	}
	
	
}

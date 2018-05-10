package xdt.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import xdt.model.PmsBusinessPos;
import xdt.service.IClientH5Service;
import xdt.tools.Base64;
import xdt.tools.Client;
import xdt.tools.Constants;
import xdt.tools.ConstantsClient;
import xdt.tools.Tools;
import xdt.tools.Xml;
import xdt.tools.http.HttpClient;
import xdt.tools.rsa.Signatory;


@Controller
@RequestMapping("/clientOrderQueryController")
public class ClientOrderQueryController extends BaseAction{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Resource 
	private  IClientH5Service ClientH5ServiceImpl;
	/**
	 * 订单查询接口测试
	 * @throws Exception 
	 */
	@RequestMapping(value="orderQueryTest")
	public void OrderQueryTest(Client client,HttpServletResponse response) throws Exception {
		// 设置参数
		PmsBusinessPos busInfo=ClientH5ServiceImpl.selectKey(client.getMerchantId1());
		String merchantId = busInfo.getBusinessnum();
		String merchOrderId = client.getMerchOrderId();  
		String tradeTime = Tools.getSysTime();

		// 调用查询接口
		System.out.println("-------订单查询接口测试-------------------------");
		try {
			Xml retXml = new Xml();
			//接口参数请参考TransactionClient的参数说明
			String ret = OrderQuery(merchantId, merchOrderId, tradeTime,
					busInfo.getKek(), Constants.PAYECO_RSA_PUBLIC_KEY, Constants.PAYECO_URL, retXml);
			log.info("返回参数："+ret);
			if(!"0000".equals(Tools.getXMLValue(ret, "retCode"))){
				System.out.println("订单查询接口测试失败！：retCode="+ret+"; msg="+retXml.getRetMsg());
				return;
			}
			
			outString(response,retXml.getStatus());
		} catch (Exception e) {
			System.out.println("订单查询接口测试失败！：");
			e.printStackTrace();
			return;
		}
		System.out.println("订单查询接口测试----ok");
		System.out.println("------------------------------------------------");
	}
	
	/**
	 * 商户订单查询接口
	 * @param merchantId:		商户代码
	 * @param merchOrderId	:	商户订单号
	 * @param tradeTime		:	商户订单提交时间
	 * @param priKey		:	商户签名的私钥
	 * @param pubKey        :   易联签名验证公钥
	 * @param payecoUrl		：	易联服务器URL地址，只需要填写域名部分
	 * @param retXml        :   通讯返回数据；当不是通讯错误时，该对象返回数据
	 * @return 				: 处理状态码： 0000 : 处理成功， 其他： 处理失败
	 * @throws Exception    :  E101:通讯失败； E102：签名验证失败；  E103：签名失败；
	 */
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

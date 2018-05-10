package xdt.service;

import java.util.Map;

import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.tools.Client;
import xdt.tools.Xml;

public interface IClientH5Service {
	String ReceiveInformationH5(Client client, Xml retXml) throws Exception;
	public Map<String, Object> ReceiveInformationApi(Client client, Xml retXml) throws Exception;
	public Map<String, Object> ReceiveInformationApiPay(Client client) throws Exception;
	
	/**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	PmsBusinessPos selectKey(String merchantId1) throws Exception;

	void otherInvokeH5(Client client)throws Exception;

	String getPayInitRedirectUrl(Client client, Xml retXml);
	
    int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo) throws Exception;

}

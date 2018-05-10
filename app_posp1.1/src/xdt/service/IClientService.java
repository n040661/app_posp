package xdt.service;

import java.util.List;
import java.util.Map;

import xdt.model.PmsBusinessPos;
import xdt.tools.Client;
import xdt.tools.Xml;

public interface IClientService {
	

	String ReceiveInformation(Client client, Xml retXml) throws Exception;

	PmsBusinessPos selectKey(String merchantId1) throws Exception;

	void otherInvoke(Client client)throws Exception;
}

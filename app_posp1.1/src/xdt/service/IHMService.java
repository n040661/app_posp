package xdt.service;

import java.util.Map;

import xdt.dto.hfb.HFBPayRequest;
import xdt.dto.hfb.HfbResponse;
import xdt.dto.hm.HMRequest;
import xdt.dto.hm.HMResponse;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsMerchantInfo;

public interface IHMService {

	public Map<String, String> shortcutAlipay(HMRequest hmRequest,Map<String, String> result);
	public int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo)
			throws Exception ;
	public int UpdateDaifu(String batchNo, String responsecode) throws Exception;
	public Map<String, String> pay(HMRequest hmRequest,Map<String, String> result);
	public Map<String, String> select(HMRequest hmRequest,Map<String, String> result);
	
	public int add(HMRequest hmRequest, PmsMerchantInfo merchantinfo, Map<String, String> result ,String state) throws Exception;
}

package xdt.service;

import java.util.Map;

import xdt.dto.hj.HJPayRequest;
import xdt.dto.hj.HJPayResponse;
import xdt.dto.hj.HJRequest;
import xdt.dto.hj.HJResponse;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsMerchantInfo;

public interface IHJService {

	
	Map<String, String> cardPay(HJRequest hjRequest,Map<String, String> result);
	
	
	Map<String, String> scanCode(HJRequest hjRequest,Map<String, String> result);
	
	public void update(HJResponse hjResponse,OriginalOrderInfo originalInfo) throws Exception;
	
	Map<String, String> select(HJRequest hjRequest,Map<String, String> result);
	
	int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo ) throws Exception;
	
	Map<String, String> pay(HJPayRequest hjPayRequest,Map<String, String> result);

	public int UpdateDaifu(String batchNo, String responsecode) throws Exception ;
	HJPayResponse paySelect(HJPayRequest hjPayRequest,
			PmsBusinessPos pmsBusinessPos);


	int add(HJPayRequest hjPayRequest, PmsMerchantInfo merchantinfo,
			Map<String, String> result, String string) throws Exception;
}

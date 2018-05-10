package xdt.service;

import java.util.Map;

import xdt.dto.mb.MBReqest;
import xdt.dto.mb.MBResponse;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsMerchantInfo;

public interface IMBService {

	Map<String, String> unionPayScanCode(MBReqest mbReqest,Map<String, String> result);
	
	void update(MBResponse mbResponse) throws Exception;
	void update1(MBResponse mbResponse) throws Exception;
	int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo ) throws Exception;
	int UpdatePmsMerchantInfo1(OriginalOrderInfo originalInfo ) throws Exception;
	
	Map<String, String> paySelect(MBReqest mbReqest,Map<String, String> result) throws Exception;
	Map<String, String> paysSelect(MBReqest mbReqest,Map<String, String> result) throws Exception;
	
	Map<String, String> pay(MBReqest mbReqest,Map<String, String> result);
	Map<String, String> verification(MBReqest mbReqest,Map<String, String> result);

	int UpdateDaifu(String string, String string2) throws Exception;

	int add(MBReqest mbReqest, PmsMerchantInfo merchantinfo, Map<String, String> result, String string) throws Exception;
	
}

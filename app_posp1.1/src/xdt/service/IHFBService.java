package xdt.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import xdt.dto.hfb.HFBPayRequest;
import xdt.dto.hfb.HfbRequest;
import xdt.dto.hfb.HfbResponse;
import xdt.model.OriginalOrderInfo;

public interface IHFBService {

	public String onlineBankList(HfbRequest hfbRequest,Map<String, String> result);
	
	Map<String, String> cardPay(HfbRequest hfbRequest,Map<String, String> result);
	public void update(HfbResponse hfbResponse) throws Exception ;
	
	public Map<String, String> WZpay(HfbRequest hfbRequest,Map<String, String> result);
	
	public String WZSelect(HfbRequest hfbRequest,Map<String, String> result);
	public Map<String, String> pay(HFBPayRequest hfbRequest,Map<String, String> result);
	
	int UpdateDaifu(String batchNo, String responsecode) throws Exception;
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception;
}

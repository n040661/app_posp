package xdt.service;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import xdt.dto.tfb.CardPayApplyRequest;
import xdt.dto.tfb.PayRequest;
import xdt.dto.tfb.WxPayApplyRequest;
import xdt.dto.tfb.WxPayApplyResponse;
import xdt.model.OriginalOrderInfo;
import xdt.tools.Client;


public interface ITFBService {
	//微信、qq钱包支付
	
	Map<String, String> wxPayApply(WxPayApplyRequest applyRequest,Map<String, String> result);
	
	//网关支付
	Map<String, String> cardPayApply(CardPayApplyRequest cardPayApplyＲequest,Map<String, String> result);
	//单笔代付
	Map<String, String> payApply(PayRequest payRequest,Map<String, String> result);

	void update(WxPayApplyResponse wxPayApplyResponse) throws Exception;
	//支付查询
	Map<String, String> WxPaySelect(WxPayApplyRequest applyRequest,Map<String, String> result);


	Map<String, String> PaySelect(PayRequest payRequest,
			Map<String, String> result);

	Map<String, Object> paySelect(WxPayApplyRequest payApplyRequest,
			Map<String, Object> results);
	public int UpdateDaifu(String batchNo, String responsecode) throws Exception ;
	
	Map<String, String> cardSelect(CardPayApplyRequest cardPayApplyＲequest,Map<String, String> result);

	int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo) throws Exception;
}

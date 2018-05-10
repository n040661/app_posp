package xdt.service;

import java.util.Map;

import xdt.dto.hfb.HfbResponse;
import xdt.dto.hlb.HLBRequest;
import xdt.dto.hlb.HLBResponse;
import xdt.model.PmsMerchantInfo;

public interface IHLBService {
	//下单接口
	Map<String, String> cardPay(HLBRequest hlbRequest,Map<String, String> result);
	//获取短信
	Map<String, String> sendValidateCode(HLBRequest hlbRequest,Map<String, String> result);
	//确认支付
	Map<String, String> confirmPay(HLBRequest hlbRequest,Map<String, String> result);
	//鉴权绑卡短信
	Map<String, String> authenticationCardPay(HLBRequest hlbRequest,Map<String, String> result);
	//鉴权绑卡
	Map<String, String> authenticationCard(HLBRequest hlbRequest,Map<String, String> result);
	//绑卡支付短信
	Map<String, String> paymentCardPay(HLBRequest hlbRequest,Map<String, String> result);
	//绑卡支付
	Map<String, String> paymentCard(HLBRequest hlbRequest,Map<String, String> result);
	//查询余额
	Map<String, String> selectBalance(HLBRequest hlbRequest,Map<String, String> result);
	//信用卡代付
	Map<String, String> creditPay(HLBRequest hlbRequest,Map<String, String> result);
	//借记卡代付
	Map<String, String> pay(HLBRequest hlbRequest,Map<String, String> result);
	
	public Map<String, String> settlementCardBind(HLBRequest hlbRequest,Map<String, String> result);
	
	public Map<String, String> settlementCardWithdraw(HLBRequest hlbRequest,Map<String, String> result);
	
	public Map<String, String> settlementCardQuery(HLBRequest hlbRequest,Map<String, String> result);
	
	public Map<String, String> transferQuery(HLBRequest hlbRequest,Map<String, String> result);
	
	public void update(String orderId,String status,String responsecode,String bindId) throws Exception ;
	
	
	public int UpdateDaifu(String batchNo, String responsecode) throws Exception;
	
	public  int add(HLBRequest hlbRequest, PmsMerchantInfo merchantinfo, Map<String, String> result ,String state,String type) throws Exception;
	
	 
}

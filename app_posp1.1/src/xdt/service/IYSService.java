package xdt.service;
/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年2月2日 上午10:04:19 
* 类说明 
*/

import java.util.Map;

import xdt.dto.hj.HJResponse;
import xdt.dto.pay.PayRequest;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsMerchantInfo;

public interface IYSService {

	Map<String, String> quickPay(PayRequest payRequest,Map<String, String> result);
	
	public Map<String, String> register(PayRequest payRequest,Map<String, String> result);
	
	public Map<String, String> openC2(PayRequest payRequest,Map<String, String> result);
	
	public Map<String, String> update(PayRequest payRequest,
			Map<String, String> result);
	
	public Map<String, String> updateC2(PayRequest payRequest,
			Map<String, String> result);
	
	public Map<String, String> bindingCard(PayRequest payRequest,
			Map<String, String> result);

	public Map<String, String> confirmPay(PayRequest payRequest,
			Map<String, String> result);
	
	public void updateOrdeId(String state,OriginalOrderInfo originalInfo) throws Exception;
	
	public int add(PayRequest payRequest, Map<String, String> result ,String state) throws Exception;
	
	Map<String, String> selectB2(String orderId,String merchantId, Map<String, String> result);
	
	public int UpdateDaifu(String batchNo, String responsecode) throws Exception;
}

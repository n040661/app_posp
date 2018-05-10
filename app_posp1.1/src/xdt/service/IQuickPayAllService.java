package xdt.service;
/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月28日 上午10:20:46 
* 类说明 
*/

import java.util.Map;

import xdt.dto.quick.QuickPayRequest;
import xdt.model.OriginalOrderInfo;

public interface IQuickPayAllService {

	
	public Map<String, String> quickPay(QuickPayRequest quickPayRequest,Map<String, String> result);
	public int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo)
			throws Exception ;
	public int UpdateDaifu(String batchNo, String responsecode) throws Exception;
	
	public Map<String, String> verificationCode(QuickPayRequest quickPayRequest,Map<String, String> result);
	public void update(String orderId,String ordernumber,OriginalOrderInfo originalInfo) throws Exception;
}

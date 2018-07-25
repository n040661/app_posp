package xdt.service;

import java.util.Map;

import xdt.dto.gateway.entity.GateWayQueryRequestEntity;
import xdt.dto.jsds.JsdsResponseDto;
import xdt.dto.scanCode.entity.ScanCodeRequestEntity;
import xdt.dto.scanCode.entity.ScanCodeResponseEntity;
import xdt.model.OriginalOrderInfo;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年4月20日 上午11:03:16 
* 类说明 
*/
public interface IScanCodeService {

	/**
	 * 扫码支付
	 * @param hjRequest
	 * @param result
	 * @return
	 */
	public Map<String, String> scanCode(ScanCodeRequestEntity entity,Map<String, String> result) ;
	//修改支付状态
	public void otherInvoke(ScanCodeResponseEntity result) throws Exception;
	public int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo)
			throws Exception;//异步实时入金80%
	public int UpdatePmsMerchantInfo1(OriginalOrderInfo originalInfo,Double dou)
			throws Exception;//异步实时入金100%
	public Map<String, String> getScanCodeQuick(GateWayQueryRequestEntity query);//查询交易状态
	
	public Map<String, String> handleNofity(JsdsResponseDto result) throws Exception ;
	public Map<String, String> quickYs(String orderId,String merId);
	public Map<String, String> zhjhQuick(String orderId,String merId);
}


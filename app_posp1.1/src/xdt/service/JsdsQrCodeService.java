package xdt.service;

import java.util.Map;

import xdt.dto.jsds.CustomerRegister;
import xdt.dto.jsds.JsdsRequestDto;
import xdt.dto.jsds.JsdsResponseDto;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsBusinessPos;

public interface JsdsQrCodeService {

	/**
	 * 处理江苏电商
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateHandle(JsdsRequestDto reqData) throws Exception;
	/**
	 * 处理江苏电商异步结果
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> otherInvoke(JsdsResponseDto result) throws Exception;
	
	public Map<String, String> handleNofity(JsdsResponseDto result) throws Exception;
	public Map<String, String> gatewayNofity(JsdsResponseDto result) throws Exception;
	public Map<String, String> gatewayNofity1(JsdsResponseDto result) throws Exception;
	
	public OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception;
	
	/**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) throws Exception;
	
	/**
	 * 时时更新D0代付额度
	 * 
	 * @param obj
	 * @return
	 */
	
	int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo) throws Exception;
	
	/**
	 * 时时更新T1代付额度
	 * 
	 * @param obj
	 * @return
	 */
	
	int UpdatePmsMerchantInfo449(OriginalOrderInfo originalInfo) throws Exception;
	
	/**
	 * 商户注册接口
	 * 
	 * @param obj
	 * @return
	 */
	
	Map<String, String> Register(CustomerRegister cr) throws Exception;
	
	/**
	 * 查询商户链接
	 * 
	 * @param obj
	 * @return
	 */
	public OriginalOrderInfo selectKeyUrl(CustomerRegister reqeustInfo) throws Exception;
	int UpdateDaifu(String string, String string2)throws Exception;
	/**
	 * 查询订单状态
	 * 
	 * @param reqData
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> handleQuery(JsdsRequestDto reqData, Map<String, String> result, PmsBusinessPos busInfo) throws Exception;
	
	/**
	 * 处理上游主动 通知的订单
	 * 
	 * @param orderinfo
	 * @throws Exception
	 */
	public Map<String, String> handleOrder(PmsAppTransInfo orderinfo) throws Exception;

}

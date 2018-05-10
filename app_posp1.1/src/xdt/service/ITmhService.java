package xdt.service;

import java.util.Map;

import xdt.dto.nbs.alipay.AlipayParamRequest;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.taomihui.entity.TaoPayRequestEntity;

public interface ITmhService {
	
	/**
	 * 分发请求
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, Object> updateHandle(TaoPayRequestEntity alipayParamRequest) throws Exception;
	
	/**
	 * 查询商户密钥信息
	 * 
	 * @param merchantId
	 *            商户号
	 * @return
	 * @throws Exception
	 */
	ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception;
	
	/**
	 * 支付宝支付接口
	 * 
	 * @param obj
	 * @return
	 */
	public  Map<String, Object> alipayParam(TaoPayRequestEntity alipayParamRequest,Map<String, Object> result, PmsBusinessPos busInfo);
	
	/**
	 * 支付宝查询接口
	 * 
	 * @param obj
	 * @return
	 */
	public  Map<String, Object> alipayScanSelect(TaoPayRequestEntity alipayParamRequest,Map<String, Object> result, PmsBusinessPos busInfo) throws Exception;
	
	/**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) throws Exception;

}

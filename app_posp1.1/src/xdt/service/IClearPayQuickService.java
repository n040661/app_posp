package xdt.service;

import java.util.Map;

import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.clearQuickPay.entity.ClearPayRequestEntity;
import xdt.quickpay.conformityQucikPay.entity.ConformityQucikPayRequestEntity;

public interface IClearPayQuickService {
	
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
	 * 查询原始信息
	 * 
	 * @param tranId
	 *            本地订单id
	 * @return 原始上送信息
	 * @throws Exception
	 */
	OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception;

	/**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) throws Exception;
	/**
	 * 处理快捷(直清)支付
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> payHandle(ClearPayRequestEntity Entity) throws Exception;
	/**
	 * 银联主动 请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	public void otherInvoke(String orderid, String status) throws Exception;

}

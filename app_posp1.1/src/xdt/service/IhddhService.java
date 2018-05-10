package xdt.service;

import java.util.Map;

import xdt.dto.quickPay.entity.MessageRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.hddh.entity.RegisterRequestEntity;
import xdt.quickpay.hddh.entity.ReplacePayRequestEntity;

public interface IhddhService {
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
	 * 处理上海漪雷代还签约
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> registerHandle(RegisterRequestEntity gateWayRequestEntity) throws Exception;
	/**
	 * 处理上海漪雷代还
	 * @param reqData
	 * @returnreplaceHandle
	 * @throws Exception 
	 */
	Map<String, String> replaceHandle(ReplacePayRequestEntity gateWayRequestEntity) throws Exception;
	/**
	 * 银联主动 请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	void otherInvoke(String orderId,String status) throws Exception;
	
	public int UpdateDaifu(String batchNo, String responsecode)throws Exception;

}

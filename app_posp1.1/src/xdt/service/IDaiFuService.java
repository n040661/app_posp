package xdt.service;

import java.util.List;
import java.util.Map;

import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.daifu.DaiFuRequestEntity;

public interface IDaiFuService {
	
	
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

	Map<String, String> setResp(String respCode, String respInfo);
	/**
	 * 沃支付代付功能
	 * 
	 * @param merchantId
	 *            商户号
	 * @return
	 * @throws Exception
	 */
	Map<String, String> Payroll(DaiFuRequestEntity array) throws Exception;

}

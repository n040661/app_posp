package xdt.service;

import java.util.Map;

import xdt.model.ChannleMerchantConfigKey;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.ysb.model.YsbRequsetEntity;

public interface IWZFPayService {

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
	 * 沃支付子协议录入接口
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
	Map<String, String> customerRegister(YsbRequsetEntity originalinfo) throws Exception;

	Map<String, String> setResp(String respCode, String respInfo);

	/**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) throws Exception;
	
	/**
	 * 委托代扣接口
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
    public Map<String, String> payHandle(YsbRequsetEntity originalinfo) throws Exception;

}

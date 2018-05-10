package xdt.service;

import java.util.Map;

import xdt.dto.balance.BalanceRequestEntity;
import xdt.model.ChannleMerchantConfigKey;

public interface IWechatScanCodeService {
	
	/**
	 * 余额查询功能
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
	Map<String, String> payHandle(BalanceRequestEntity originalinfo) throws Exception;
	
	/**
	 * 查询商户密钥信息
	 * 
	 * @param merchantId
	 *            商户号
	 * @return
	 * @throws Exception
	 */
	ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception;

}

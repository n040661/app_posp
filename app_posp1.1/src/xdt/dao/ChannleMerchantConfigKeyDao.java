/**
 * 
 */
package xdt.dao;

import xdt.model.ChannleMerchantConfigKey;

/**
 * @ClassName: ChannleMerchantConfigKeyDao
 * @Description: 渠道配置密钥信息
 * @author LiShiwen
 * @date 2016年6月21日 下午5:23:50
 *
 */
public interface ChannleMerchantConfigKeyDao extends IBaseDao<ChannleMerchantConfigKey> {
	
	public  ChannleMerchantConfigKey get(String mercid);

	int saveKey(ChannleMerchantConfigKey key);

}

/**
 * 
 */
package xdt.dao.impl;

import org.springframework.stereotype.Component;

import xdt.dao.ChannleMerchantConfigKeyDao;
import xdt.model.ChannleMerchantConfigKey;

/**
 * @ClassName: ChannleMerchantConfigKeyDaoImpl
 * @Description: 渠道商户密钥信息
 * @author LiShiwen
 * @date 2016年6月21日 下午5:25:15
 *
 */
@Component
public class ChannleMerchantConfigKeyDaoImpl extends BaseDaoImpl<ChannleMerchantConfigKey>
		implements ChannleMerchantConfigKeyDao {

	@Override
	public ChannleMerchantConfigKey get(String mercid) {
		return this.sqlSession.selectOne("xdt.mapping.ChannleMerchantConfigKeyMapper.selectByPrimaryKey",mercid);
	}
	@Override
	public int saveKey(ChannleMerchantConfigKey key) {
		return this.sqlSession.insert(
				"xdt.mapping.ChannleMerchantConfigKeyMapper.save", key);
	}

}

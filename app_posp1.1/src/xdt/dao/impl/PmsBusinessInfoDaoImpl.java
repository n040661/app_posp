package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IPmsBusinessInfoDao;
import xdt.model.PmsBusinessInfo;

/**
 *
 * User: Jeff
 * Date: 15-8-29
 * Time: 下午5:11
 * 通道商户
 */
@Repository
public class PmsBusinessInfoDaoImpl extends BaseDaoImpl<PmsBusinessInfo> implements IPmsBusinessInfoDao {
	
	public PmsBusinessInfo selectBusinessInfo(String id) {
		String sql = this.getStatementId("selectByPrimaryKey"); 
		return sqlSession.selectOne(sql, id);
	}
	@Override
	public PmsBusinessInfo selectBusinessInfoBusinessNum(String businessNum) {
		String sql = this.getStatementId("selectByPrimaryKeyBusinessNum"); 
		return sqlSession.selectOne(sql, businessNum);
	}
}

package xdt.dao.impl;


import org.springframework.stereotype.Repository;

import xdt.dao.IPmsAddressDao;
import xdt.model.PmsAddress;

@Repository
public class PmsAddressDaoImpl extends BaseDaoImpl<PmsAddress> implements IPmsAddressDao {

	private static final String UPDATE = "updateByPrimaryKey";   
	
	@Override
	public int updateById(PmsAddress pmsAddress) throws Exception {
		String sql = this.getStatementId(UPDATE); 
		return sqlSession.update(sql,pmsAddress);
	}
}

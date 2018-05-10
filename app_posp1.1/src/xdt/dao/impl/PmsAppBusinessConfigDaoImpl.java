package xdt.dao.impl;

import xdt.dao.IPmsAppBusinessConfigDao;
import xdt.dao.impl.BaseDaoImpl;
import xdt.model.PmsAppBusinessConfig;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PmsAppBusinessConfigDaoImpl extends BaseDaoImpl<PmsAppBusinessConfig> implements IPmsAppBusinessConfigDao {
		
	private static final String SELECT = "selectBusinessInfo"; 
	
	private static final String SELECT1 = "selectBusinessInfo1"; 
	
	/**
	 * 检索商户的业务列表信息
	 * @param mercId
	 * @return
	 * @throws Exception
	 */
	public List<PmsAppBusinessConfig> searchBusinessInfo(String mercId) throws Exception{
		String sql = getStatementId(SELECT);
		return sqlSession.selectList(sql, mercId);
	}

	@Override
	public List<PmsAppBusinessConfig> searchBusinessInfo1(String mercId) throws Exception {
		String sql = getStatementId(SELECT1);
		return sqlSession.selectList(sql, mercId);
	}
	
}

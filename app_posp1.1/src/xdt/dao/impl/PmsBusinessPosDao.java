package xdt.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import xdt.dao.IPmsBusinessPosDao;
import xdt.model.PmsBusinessPos;
@Repository
public class PmsBusinessPosDao extends BaseDaoImpl<PmsBusinessPos> implements IPmsBusinessPosDao {

	@Override
	public PmsBusinessPos selectBusinessInfo(String id) {
		String sql = this.getStatementId("selectByPrimaryKey"); 
		return sqlSession.selectOne(sql, id);
	}
	@Override
	public PmsBusinessPos selectBusinessposBusinessNum(String id) {
		String sql = this.getStatementId("selectByPrimaryKey"); 
		return sqlSession.selectOne(sql, id);
	}

	
}

package xdt.dao.impl;

import xdt.dao.ITAccRateDao;
import xdt.dao.impl.BaseDaoImpl;
import xdt.model.TAccRate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class TAccRateDaoImpl extends BaseDaoImpl<TAccRate> implements ITAccRateDao {
	//获取查询费率映射的名字
	private static final String SELECT = "selectversion";
	
	private static final String SELECTONE = "selectRateByTypeGrade";
	
	/**
	 * 查询费率
	 * @return
	 * @throws Exception
	 */
	public List<TAccRate> selectAccRate()throws Exception{
		String sql=this.getStatementId(SELECT);
		return  sqlSession.selectList(sql);
	}
	
	/**
	 * 根据交易类型与等级查询费率
	 */
	@Override
	public TAccRate selectRateByTypeGrade(HashMap<String, String> map)throws Exception {
		String sql = this.getStatementId(SELECTONE);
		return sqlSession.selectOne(sql, map);
	}
}

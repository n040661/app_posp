package xdt.dao.impl;

import xdt.dao.IPmsTransHistoryRecordDao;
import xdt.dao.impl.BaseDaoImpl;
import xdt.model.PmsTransHistoryRecord;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class PmsTransHistoryRecordDaoImpl extends BaseDaoImpl<PmsTransHistoryRecord> implements IPmsTransHistoryRecordDao {

	private static final String SELECTONE = "selectCardInfo";
	
	private static final String SELECTLIST = "selectCardListByMercId";
	
	/**
	 * 检索交易历史记录信息
	 */

	public PmsTransHistoryRecord selectCardInfo(HashMap<String, String> hashMap)throws Exception {
		String sql = getStatementId(SELECTONE);	
		return sqlSession.selectOne(sql,hashMap);
	}

	/**
	 * 检索交易历史记录列表
	 */
	@Override
	public List<PmsTransHistoryRecord> searchHistoryRecord(HashMap<String, String> hashMap) throws Exception {
		String sql = getStatementId(SELECTLIST);	
		return sqlSession.selectList(sql, hashMap);
	}
}

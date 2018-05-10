package xdt.dto;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import xdt.dao.IPmsSupportBankInfoDao;
import xdt.dao.impl.BaseDaoImpl;
import xdt.model.PmsSupportBankInfo;

@Repository
public class PmsSupportBankInfoDaoImpl extends BaseDaoImpl<PmsSupportBankInfo> implements IPmsSupportBankInfoDao {

	private final static String SELECTLIST = "selectBankList";
	
	/**
	 * 检索银行列表
	 */

	public List<PmsSupportBankInfo> selectBankList(HashMap<String, String> map)throws Exception {
		String sql = getStatementId(SELECTLIST);	
		return sqlSession.selectList(sql, map);
	}
}

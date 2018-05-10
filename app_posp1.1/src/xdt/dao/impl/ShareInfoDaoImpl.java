package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IShareInfoDao;
import xdt.model.ShareInfo;

@Repository
public class ShareInfoDaoImpl extends BaseDaoImpl<ShareInfo> implements IShareInfoDao {

	private final static String SELECTBYOAGENTNO = "selectByOagentNo";

	/**
	 * 根据前6位数字检索银行卡信息
	 */

	public ShareInfo selectByOagentNo(String oAgentNo) {
		String sql = getStatementId(SELECTBYOAGENTNO);
		return sqlSession.selectOne(sql, oAgentNo);
	}
}

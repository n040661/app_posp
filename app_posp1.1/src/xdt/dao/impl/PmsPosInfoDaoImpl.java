package xdt.dao.impl;


import org.springframework.stereotype.Repository;

import xdt.dao.IPmsPosInfoDao;
import xdt.model.PmsPosInfo;

@Repository
public class PmsPosInfoDaoImpl extends BaseDaoImpl<PmsPosInfo> implements IPmsPosInfoDao {
	//通过sn进行查询用户是否存在
	private static final String SELECTSN="selectSero";
	
	//通过posId进行查询设备类型
	private static final String SELECTPODID="selectPosId";
	/**
	 * 通过sn进行查询用户是否存在
	 */
	public PmsPosInfo selectBusinessPos(String sero) throws Exception {
		return sqlSession.selectOne(getStatementId(SELECTSN), sero);
		}
	
	@Override
	public PmsPosInfo selectPosId(String posId) throws Exception {
		return sqlSession.selectOne(getStatementId(SELECTPODID), posId);
	}
}

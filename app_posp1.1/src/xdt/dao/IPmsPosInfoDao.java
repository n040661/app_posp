package xdt.dao;

import xdt.model.PmsPosInfo;

public interface IPmsPosInfoDao extends IBaseDao<PmsPosInfo> {
	/**
	 * 通过sn号进行查询
	 */
	public PmsPosInfo selectBusinessPos(String sn) throws Exception;

	/**
	 * 通过posId进行查询
	 */
	public PmsPosInfo selectPosId(String posId) throws Exception;

}

package xdt.dao;

import java.util.HashMap;
import java.util.List;

import xdt.model.PmsSupportBankInfo;


public interface IPmsSupportBankInfoDao extends IBaseDao<PmsSupportBankInfo> {
	
	/**
	 * 检索银行列表
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public List<PmsSupportBankInfo> selectBankList (HashMap<String,String> map) throws Exception;
}

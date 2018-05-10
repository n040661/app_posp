package xdt.dao;

import xdt.dao.IBaseDao;
import xdt.model.TAccRate;

import java.util.HashMap;
import java.util.List;

public interface ITAccRateDao extends IBaseDao<TAccRate> {
	/**
	 * 查询费率
	 * @return
	 * @throws Exception
	 */
	public List<TAccRate> selectAccRate()throws Exception;
	
	/**
	 * 根据交易类型与等级查询费率
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public TAccRate selectRateByTypeGrade(HashMap<String, String> map)throws Exception;
}

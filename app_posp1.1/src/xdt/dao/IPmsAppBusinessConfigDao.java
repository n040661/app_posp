package xdt.dao;

import xdt.dao.IBaseDao;
import xdt.model.PmsAppBusinessConfig;

import java.util.List;

public interface IPmsAppBusinessConfigDao extends IBaseDao<PmsAppBusinessConfig> {
	
	/**
	 * 检索商户的业务列表信息
	 * @param mercId
	 * @return
	 * @throws Exception
	 */
	public List<PmsAppBusinessConfig> searchBusinessInfo(String mercId) throws Exception;

	public List<PmsAppBusinessConfig> searchBusinessInfo1(String mercId) throws Exception;

}

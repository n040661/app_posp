package xdt.dao;

import xdt.model.PmsAddress;

public interface IPmsAddressDao extends IBaseDao<PmsAddress> {

	public int updateById(PmsAddress pmsAddress) throws Exception;

}

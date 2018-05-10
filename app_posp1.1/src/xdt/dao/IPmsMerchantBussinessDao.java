package xdt.dao;

import xdt.dao.IBaseDao;
import xdt.model.PmsMerchantBussiness;

import java.util.List;

public interface IPmsMerchantBussinessDao extends IBaseDao<PmsMerchantBussiness> {
	
	/**
	 * 批量保存->给注册成功的商户分配的基本业务
	 * @param list
	 * @return
	 */
	public int saveMerchantBussinessInfo(List<PmsMerchantBussiness> list) throws Exception;
}

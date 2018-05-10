package xdt.dao;


import java.math.BigDecimal;
import java.util.Map;

import xdt.dao.IBaseDao;
import xdt.model.TAccAccount;

public interface ITAccAccountDao extends IBaseDao<TAccAccount> {
	
	/**
	 * 检索账户信息
	 */
	public TAccAccount searchAccountInfo(String accNum)throws Exception;
	/**
     * 更新商户账户余额
     * @author wumeng   20150522
     * @param balance   余额
     * @param accnum   账户
     */
	public int updateMerchantBalance(TAccAccount tAccAccount)throws Exception;
	/**
	 * 查询商户账户余额  根据 mercid
	 * @author wumeng   20150522
     * @param mercid   商户ID
	 */
	public BigDecimal selectMerchantBalance(String mercid)throws Exception;
	/**
	 * 更新商户账户余额记录
	 * @author wumeng   20150522
     * @param param
	 */
	public int insertAccountHistory(Map<String, String> param)throws Exception;
}

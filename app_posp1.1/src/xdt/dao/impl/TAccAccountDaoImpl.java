package xdt.dao.impl;


import java.math.BigDecimal;
import java.util.Map;

import xdt.dao.ITAccAccountDao;
import xdt.dao.impl.BaseDaoImpl;
import xdt.model.TAccAccount;

import org.springframework.stereotype.Repository;
@Repository
public class TAccAccountDaoImpl extends BaseDaoImpl<TAccAccount> implements ITAccAccountDao {
	//检索账户信息
	private static final String SELECT = "selectByTAccNum";
	//查询商户账户余额  根据 mercid
	private static final String SELECTMERCHANTBALANCE = "selectMerchantBalance";
	
	//更新商户账户余额
	private static final String UPDATEMERCHANTBALANCE = "updateMerchantBalance";
	// 更新商户账户余额记录
	private static final String INSERTACCOUNTHISTORY = "insertAccountHistory";
	
	/**
	 * 检索账户信息
	 */
	public TAccAccount searchAccountInfo(String accNum)throws Exception {
		return sqlSession.selectOne(getStatementId(SELECT),accNum);
	}
	
	/**
     * 更新商户账户余额
     * @author wumeng   20150522
     * @param balance   余额
     * @param accnum   账户
     */
	public int updateMerchantBalance(TAccAccount tAccAccount){
		return sqlSession.update(getStatementId(UPDATEMERCHANTBALANCE), tAccAccount);
	}
	
    /**
	 * 查询商户账户余额  根据 mercid
	 * @author wumeng   20150522
     * @param mercid   商户ID
	 */
	public BigDecimal selectMerchantBalance(String mercid)throws Exception {
		return sqlSession.selectOne(getStatementId(SELECTMERCHANTBALANCE),mercid);
	}
	/**
	 * 更新商户账户余额记录
	 * @author wumeng   20150522
     * @param param
	 */
	@Override
	public int insertAccountHistory(Map<String, String> param)
			throws Exception {
		return sqlSession.insert(getStatementId(INSERTACCOUNTHISTORY),param);
		
	}
	
}

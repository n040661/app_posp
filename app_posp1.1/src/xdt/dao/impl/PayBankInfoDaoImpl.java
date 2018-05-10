package xdt.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import xdt.dao.IPayBankInfoDao;
import xdt.model.PayBankInfo;

@Repository
public class PayBankInfoDaoImpl extends BaseDaoImpl<PayBankInfo> implements IPayBankInfoDao {

	private static final String SELECTDES = "selectBankInfo";
	
	private static final String SELECTCODE = "selectBankCodes";
	
	public PayBankInfo selectByBankInfo(PayBankInfo pay) throws Exception {
		
		 String sql = getStatementId(SELECTDES);
	     return sqlSession.selectOne(sql, pay);
	}


	public List<PayBankInfo> selectBankCodes(PayBankInfo pay) throws Exception {
		String sql = getStatementId(SELECTCODE);
		return sqlSession.selectList(sql, pay);
//	    return sqlSession.selectOne(sql, pay);
	}

	

}

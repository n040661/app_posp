package xdt.dao.impl;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;

import xdt.dao.IPmsMerchantBindingcardInfoDao;
import xdt.model.PmsMerchantBindingcardInfo;

@Repository
public class PmsMerchantBindingcardInfoDaoImpl extends BaseDaoImpl<PmsMerchantBindingcardInfo> implements IPmsMerchantBindingcardInfoDao {

	private final static String SELECTList = "selectCardListByMercId";
	private final static String SELECTONE = "selectCardInfo";
	
	/**
	 * 检索银行卡列表
	 */
	
	public List<PmsMerchantBindingcardInfo> selectCardListByMercId(HashMap<String,String> map)throws Exception {
		String sql = getStatementId(SELECTList);	
		return sqlSession.selectList(sql,map);
	}

	/**
	 * 检索银行卡信息
	 */

	public PmsMerchantBindingcardInfo searchBankCardInfo(HashMap<String, String> map) throws Exception {
		String sql = getStatementId(SELECTONE);
		return sqlSession.selectOne(sql, map);
	}
}

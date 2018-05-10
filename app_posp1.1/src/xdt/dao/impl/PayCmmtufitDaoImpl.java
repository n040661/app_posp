package xdt.dao.impl;
import org.springframework.stereotype.Repository;
import xdt.dao.IPayCmmtufitDao;
import xdt.model.PayCmmtufit;

import java.util.List;

@Repository
public class PayCmmtufitDaoImpl extends BaseDaoImpl<PayCmmtufit> implements IPayCmmtufitDao {
	
	private final static String SELECT = "selectCardInfoByBeforeSix";
	private final static String SELECTBYCARDNUM = "selectByBankNum";

	/**
	 * 根据前6位数字检索银行卡信息
	 */
	
	public List<PayCmmtufit> searchCardInfoByBeforeSix(String beforeSixCardNumber) throws Exception {
		String sql = getStatementId(SELECT);	
		return sqlSession.selectList(sql,beforeSixCardNumber);
	}
	/**
	 * 根据银行卡号检索银行卡卡宾信息
	 */

	public PayCmmtufit selectByCardNum(String cardNum) throws Exception {
		String sql = getStatementId(SELECTBYCARDNUM);
		return sqlSession.selectOne(sql,cardNum);
	}
}

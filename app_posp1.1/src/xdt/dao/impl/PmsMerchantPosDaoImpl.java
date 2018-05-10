package xdt.dao.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import xdt.dao.IPmsMerchantPosDao;
import xdt.model.PmsMerchantPos;
/**
 * pos表注册
 * @author p
 *
 */
@Repository
public class PmsMerchantPosDaoImpl extends BaseDaoImpl<PmsMerchantPos> implements IPmsMerchantPosDao {
	//根据posid查询商户Pos 信息
private static final String UPDATE = "selectPosid";
private final static String SELECTBYSN = "selectPosInfoBySN";

	//通过商户id进行查询merchantid
	private static final String SELECTM= "selectMerchantid";
	//通过POSBUSINESSNO（sn）号进行查询
	private static final String  SELECTSN= "selectSn";
	//通过posId解绑
	private static final String  UPDATEBYPOSID= "updateByPosId";
	
	/**
	 * 通过posid进行查询
	 */
	public  PmsMerchantPos selectMerchantPos(String posid)throws Exception{
		return sqlSession.selectOne(this.getStatementId(UPDATE), posid);
	}
	
	
	
	/**
	 * 通过商户id进行查询
	 * 
	 */
	public PmsMerchantPos selectMerchantidAndSn(Map<String,String> paramMap)throws Exception{
		return sqlSession.selectOne(this.getStatementId(SELECTM), paramMap);
	}
	/**
	 * 通过posbusinessno进行查询
	 */
	public PmsMerchantPos selectSn(int posbusinessno) throws Exception {
		return sqlSession.selectOne(this.getStatementId(SELECTSN), posbusinessno);
	}

	/**
	 * 通过sn号查询pos信息
	 */

	public PmsMerchantPos searchMerchantPosBySN(String serialno)throws Exception {
		String sql = getStatementId(SELECTBYSN);	
		return sqlSession.selectOne(sql, serialno);
	}

	/**
	 * 通过posId解绑设备
	 */
	@Override
	public int updateByPosId(PmsMerchantPos pmsMerchantPos) throws Exception {
		String sql = this.getStatementId(UPDATEBYPOSID); 
		return sqlSession.update(sql,pmsMerchantPos);
	}

}

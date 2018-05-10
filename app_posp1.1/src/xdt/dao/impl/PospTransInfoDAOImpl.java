package xdt.dao.impl;

import org.springframework.stereotype.Repository;

import xdt.dao.IPospTransInfoDAO;
import xdt.model.PospTransInfo;

import java.util.List;

/**
 * 支付流水
 * User: Jeff
 * Date: 15-5-22
 * Time: 下午2:24
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class PospTransInfoDAOImpl extends BaseDaoImpl<PospTransInfo> implements IPospTransInfoDAO {

    private static final String SELECTDES = "selectOneDes";
    private static final String SELECTBYORDERID = "selectByOrderId";
    private static final String GETNEXTTRANSID = "getNextTransid";

    /**
     * 修改流水表状态
     */
    private static final String UPDETEPOSPTRANSINFO = "updetePospTransInfo";
    /**
     * 根据订单号修改流水
     */
    private static final String UPDATEBYORDERID = "updateByOrderId";
    
    /**
     * 根据唯一修改流水
     */
    private static final String UPDATEBYUNIQUEKEY = "updateByUniqueKey";
    /**
     * 拿到最近的一条记录
     * @return
     */
    @Override
    public PospTransInfo searchLatest(PospTransInfo pospTransInfo) {
        String sql = getStatementId(SELECTDES);
        return sqlSession.selectOne(sql, pospTransInfo);
    }

    @Override
    public PospTransInfo searchByOrderId(String orderId) {
        String sql = getStatementId(SELECTBYORDERID);
        return sqlSession.selectOne(sql, orderId);
    }
    /**
     * 修改流水表状态
     */
	@Override
	public int updetePospTransInfo(String orderid) throws Exception {
		return sqlSession.update(getStatementId(UPDETEPOSPTRANSINFO),orderid);
	}

    /**
     * 查询交易ID序列
     * @return
     * @throws Exception
     */
    @Override
    public int getNextTransid() throws Exception {
        Integer result = 0;
        List s =null;
        String sql = getStatementId(GETNEXTTRANSID);
        if( (s= sqlSession.selectList(sql,null)) != null){
            result = Integer.parseInt(s.get(0).toString());
        }
        return   result;
    }
    
    public int getJourno()   {
        Integer result = 0;
        List s =null;
        String sql = getStatementId("getJourno");
        if( (s= sqlSession.selectList(sql,null)) != null){
            result = Integer.parseInt(s.get(0).toString());
        }
        return   result;
    }
    

    /**
     * 根据订单号修改流水
     * @param pospTransInfo
     * @return
     * @throws Exception
     */
    @Override
    public int updateByOrderId(PospTransInfo pospTransInfo) throws Exception {
        return  sqlSession.update(getStatementId(UPDATEBYORDERID),pospTransInfo);
    }
    
    /**
     * 根据唯一修改流水
     * @param pospTransInfo
     * @return
     * @throws Exception
     */
    @Override
    public int updateByUniqueKey(PospTransInfo pospTransInfo) throws Exception {
        return  sqlSession.update(getStatementId(UPDATEBYUNIQUEKEY),pospTransInfo);
    }

	@Override
	public PospTransInfo searchBytransOrderId(String transOrderId) {
		String sql = getStatementId("searchBytransOrderId");
		return sqlSession.selectOne(sql, transOrderId);
	}

	@Override
	public PospTransInfo selectSrc(PospTransInfo para) {
		String sql = getStatementId("selectSrc");
		return sqlSession.selectOne(sql, para);
	}
	
	
	
	public PospTransInfo selectJourByUniqueKey(String uniqueKey) {
		String sql = getStatementId("selectByUniqueKey");
		return sqlSession.selectOne(sql, uniqueKey);
	}
	
	public PospTransInfo selectSrcJour(PospTransInfo para) {
		String sql = getStatementId("selectSrcJour");
		return sqlSession.selectOne(sql, para);
	}

	@Override
	public PospTransInfo searchByPospsn(String pospsn) {
		return sqlSession.selectOne("searchByPospsn", pospsn);
	}

	@Override
	public PospTransInfo selectBySysseqno(String sysseqno) {
		return sqlSession.selectOne("selectBySysseqno", sysseqno);
	}
	@Override
	public PospTransInfo searchBycjtOrderId(String OrderId) {
		
		String sql = getStatementId("searchBycjtsOrderId");
		return sqlSession.selectOne(sql,OrderId);
	}
	public List<PospTransInfo> selectPay() {
		String sql = this.getStatementId("selectPay");
		return sqlSession.selectList(sql);
	}
}

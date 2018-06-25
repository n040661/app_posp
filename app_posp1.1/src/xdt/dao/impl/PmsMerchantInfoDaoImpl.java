package xdt.dao.impl;

import java.util.Map;

import xdt.dao.IPmsMerchantInfoDao;
import xdt.dao.impl.BaseDaoImpl;
import xdt.model.PmsMerchantInfo;

import org.springframework.stereotype.Repository;

@Repository
public class PmsMerchantInfoDaoImpl extends BaseDaoImpl<PmsMerchantInfo> implements IPmsMerchantInfoDao {
	
	//根据商户实体更新商户 信息
	private static final String UPDATE = "updateByPmsMerchantInfo";
    //根据商户id查询商户信息
    private static final String SELECTBYMERCID="selectByMercId";
	
	//根据手机号查询商户信息
	private static final String QUERYMERCURYINFO = "queryMercuryInfo";
	
	//根据商户编号    查询商户是否正式商户  60 正式商户
	private static final String QUERYMERCURYSTATUS = "queryMercuryStatus";
	
	//查询支付宝和微信是不是绑定了路由
	private static final String GETCHANNELCOUNT = "getChannelCount";
	
	//根据商户编号查询商户信息     wm 2016-02-22
	private static final String  SELECTMERCHANTINFOBYMERCID ="selectMerchantInfoByMercid";
	//查询商户清分费率     wm 2016-05-11
	private static final String  QUERYMERSETTLERATETYPE ="queryMersettleRateType";
	
	private static final String UPDATET ="updateByPrimaryKeySelective";	
	
	//查询商户当前所属代理，刷卡费率和清算费率     wm 2016-05-18
	private static final String  QUERYAGENTSHUAKARATEANDSETTLERATE ="queryAgentShuaKaRateAndSettleRate";
	
	
	
	/**
	 * 根据商户实体更新商户信息
	 */
	public int merchantUpdateByPmsMerchantInfo(PmsMerchantInfo pmsMerchantInfo)throws Exception {
		String sql = this.getStatementId(UPDATE); 
		return sqlSession.update(sql,pmsMerchantInfo);
	}
	

    @Override
    public PmsMerchantInfo selectMercByMercId(String mercId) throws Exception {
        String sql = this.getStatementId(SELECTBYMERCID);
        return sqlSession.selectOne(sql,mercId);
    }


	/**
	 * 根据商户用户名（手机号）查询商户信息
	 * wumeng  20150525
	 */
	public Map<String, String> queryMercuryInfo(String  mobilePhone)throws Exception{
		
		String sql = this.getStatementId(QUERYMERCURYINFO); 
		return sqlSession.selectOne(sql,mobilePhone);
		
	}
	

	/**
	 * 根据商户编号   查询商户是否正式商户  60 正式商户
	 * wumeng  20150525
	 */
	public String queryMercuryStatus(String  mercid)throws Exception{
		
		String sql = this.getStatementId(QUERYMERCURYSTATUS); 
		return sqlSession.selectOne(sql,mercid);
		
	}
	/**
	 * 查询支付宝和微信是不是绑定了路由
	 * wumeng  20150730
	 */
	public int getChannelCount(Map<String,String> map)throws Exception{
		
		String sql = this.getStatementId(GETCHANNELCOUNT); 
		return sqlSession.selectOne(sql,map);
		
		
	}	

	/**
	 * 根据商户编号查询商户信息
	 *    wm 2016-02-22
	 */
	public PmsMerchantInfo selectMerchantInfoByMercid(String  mercid)throws Exception{
		
		String sql = this.getStatementId(SELECTMERCHANTINFOBYMERCID); 
		return sqlSession.selectOne(sql,mercid);
		
	}
	/**
	 * 查询商户清分费率
	 *    wm 2016-05-11
	 */
	public Map<String,String> queryMersettleRateType(String mercid)throws Exception{
		
		String sql = this.getStatementId(QUERYMERSETTLERATETYPE); 
		return sqlSession.selectOne(sql,mercid);
		
	}
	
	/**
	 * 查询商户当前所属代理，刷卡费率和清算费率
	 *    wm 2016-05-18
	 */
	public Map<String, String> queryAgentShuaKaRateAndSettleRate(String mercid)throws Exception{
		String sql = this.getStatementId(QUERYAGENTSHUAKARATEANDSETTLERATE); 
		return sqlSession.selectOne(sql,mercid);
	}
	/**
	 * 查询商户是否存在
	 *    wm 2016-12-02
	 */
	public PmsMerchantInfo selectMerchantInfoByPhone(String account)
			throws Exception {

		String sql = this.getStatementId("selectphone"); 
		return sqlSession.selectOne(sql,account);
	}
	public int merchantUpdateByPmsMerchantInfos(PmsMerchantInfo pmsMerchantInfo)throws Exception {
		String sql = this.getStatementId(UPDATET); 
		return sqlSession.update(sql,pmsMerchantInfo);
	}
	//修改可用额度
	public int UpdatePmsMerchantInfo(PmsMerchantInfo pmsMerchantInfo) throws Exception {
	
		String sql = this.getStatementId("updatePmsMerchantInfo"); 
		return sqlSession.update(sql,pmsMerchantInfo);
	}


	@Override
	public synchronized int updataPay(Map<String, String> map) {
		String sql = this.getStatementId("updataPay"); 
		return sqlSession.update(sql, map);
	}


	@Override
	public synchronized int updataPayT1(Map<String, String> map) {
		String sql = this.getStatementId("updataPayT1");
		return sqlSession.update(sql, map);
	}


	@Override
	public synchronized int updataD0(Map<String, String> map) {
		String sql = this.getStatementId("updataD0");
		return sqlSession.update(sql, map);
	}


	@Override
	public synchronized int updataT1(Map<String, String> map) {
		String sql = this.getStatementId("updateT1");
		return sqlSession.update(sql, map);
	}
}

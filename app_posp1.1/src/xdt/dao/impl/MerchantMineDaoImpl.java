package xdt.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import xdt.dao.IMerchantMineDao;
import xdt.model.MerchantMinel;
import xdt.model.WeChatPublicNo;


@Repository
public class MerchantMineDaoImpl extends BaseDaoImpl<MerchantMinel> implements IMerchantMineDao {
	
	//商户在客户端查看绑定的卡信息   提现页面显示使用
	private static final String QUERYDRAWMONEYACC = "queryDrawMoneyAcc";
	// 获取业务信息   最大值、最小值、费率
	private static final String  QUERYBUSINESSINFO ="queryBusinessInfo";
	
	//查询节假日
	private static final String QUERYFESTIVAL ="queryFestival";
	private static final String INSERT ="insertDrawMoneyAcc";
	// 微信公众号信息获取
	private static final String QUERYWECHATPUBLICNO ="queryWechatPublicNo";
	// 查询提现时间段    
	private static final String QUERYTIXIANTIME ="queryTiXianTime";
	// 查询刷卡交易时间段
	private static final String QUERYSHUAKAAGENT0TIME ="queryShuaKaAgent0Time";
	
	
	
	/**
	 * 商户在客户端查看绑定的卡信息  提现页面显示使用
	 * @param paramMap
	 * @return
	 */
	@Override
	public Map<String, String> queryDrawMoneyAcc(
			Map<String, String> paramMap) {
		String sql = this.getStatementId(QUERYDRAWMONEYACC); 
		return sqlSession.selectOne(sql,paramMap);
	}

	
	/**
	 *  获取业务信息   最大值、最小值、费率
	 * @param paramMap
	 * @return
	 */
	@Override
	public Map<String, String> queryBusinessInfo(Map<String, String> paramMap){
		String sql = this.getStatementId(QUERYBUSINESSINFO); 
		return sqlSession.selectOne(sql,paramMap);
	}
	
	
	/**
	 *提款记录存入
	 * @param paramMap
	 * @return
	 */
	@Override
	public Integer saveDrawMoneyAcc(MerchantMinel pmsMerchantCollectManager) {
		String sql = this.getStatementId(INSERT); 
		return sqlSession.insert(sql,pmsMerchantCollectManager);
	}
	
  /**
   * 查询交易日期是否是节假日 （是否是可提款转账日期
   * @param date
   * @return
   */
	@Override
	public int queryFestival(String  date) {
		String sql = this.getStatementId(QUERYFESTIVAL); 
		return sqlSession.selectOne(sql,date);
	}
	/**
	 * 微信公众号信息获取
	 * wumeng  20150831
	 * @param getoAgentNo
	 */
	public WeChatPublicNo queryWechatPublicNo(String oAgentNo){
		String sql = this.getStatementId(QUERYWECHATPUBLICNO); 
		return sqlSession.selectOne(sql,oAgentNo);
	}
	/**
	 * 查询提现时间段  
	 * wumeng  2015-10-25
	 */
	public Map<String, String> queryTiXianTime(){
			String sql = this.getStatementId(QUERYTIXIANTIME); 
			return sqlSession.selectOne(sql);
	}
	
	
	/**
	 * 查询刷卡交易时间段判断 
	 *   wm 2016-03-04  
	 */
	public Map<String, String> queryShuaKaAgent0Time(){

		String sql = this.getStatementId(QUERYSHUAKAAGENT0TIME); 
		return sqlSession.selectOne(sql);

	}
	
	
}

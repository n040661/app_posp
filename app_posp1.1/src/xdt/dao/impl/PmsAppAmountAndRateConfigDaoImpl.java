package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IPmsAppAmountAndRateConfigDao;
import xdt.model.AppRateTypeAndAmount;
import xdt.model.PmsAppAmountAndRateConfig;

import java.util.Map;

/**
 * 商户费率配置DAO
 * User: Jeff
 * Date: 15-5-26
 * Time: 下午5:40
 */
@Repository
public class PmsAppAmountAndRateConfigDaoImpl  extends BaseDaoImpl<PmsAppAmountAndRateConfig> implements IPmsAppAmountAndRateConfigDao {
	
	
	//查询商户刷卡费率 和  最低收款金额    费率    是否是封顶费率标记  封顶金额
	private static final String QUERYAMOUNTANDRATEINFOFORSHUAKA = "queryAmountAndRateInfoForShuaka";
	//查询  最低、最高收款金额   ，支付方式是否开通 ， 业务是否开通
	private static final String QUERYAMOUNTANDSTATUS = "queryAmountAndStatus";
	//根据商户编号和业务类型（交易类型）  查询  商户的费率和金额限制信息
	private static final String UPDATEBYMERCID = "updateByMercId";
	
	/**
     * 查询商户刷卡费率 和  最低收款金额    费率    是否是封顶费率标记  封顶金额   
     * @author wumeng   20150522
     */
	@Override
	public AppRateTypeAndAmount queryAmountAndRateInfoForShuaka(Map<String, String> param) {
		String sql = this.getStatementId(QUERYAMOUNTANDRATEINFOFORSHUAKA); 
		return sqlSession.selectOne(sql,param);
	}
	/**
	 * 查询  最低、最高收款金额   ，支付方式是否开通 ， 业务是否开通
	 * @author wumeng 20150522
	 * @param param
	 */
	public AppRateTypeAndAmount queryAmountAndStatus(Map<String, String> param){
		String sql = this.getStatementId(QUERYAMOUNTANDSTATUS); 
		return sqlSession.selectOne(sql,param);
	}
	/**
	 * 修改商户费率
	 * @author lev12
     * @param p 商户id 业务id
	 */
	@Override
	public int updateByMercId(PmsAppAmountAndRateConfig p) {
		String sql = this.getStatementId(UPDATEBYMERCID); 
		return sqlSession.update(sql,p);
	}

}

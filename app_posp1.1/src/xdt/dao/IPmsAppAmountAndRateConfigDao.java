package xdt.dao;

import xdt.model.AppRateTypeAndAmount;
import xdt.model.PmsAppAmountAndRateConfig;

import java.util.Map;

public interface IPmsAppAmountAndRateConfigDao extends
		IBaseDao<PmsAppAmountAndRateConfig> {

	/**
	 * 查询商户刷卡费率 和  最低收款金额    费率    是否是封顶费率标记  封顶金额   
	 * @author wumeng 20150522
	 * @param param
	 */
	public AppRateTypeAndAmount queryAmountAndRateInfoForShuaka(Map<String, String> param);
	/**
	 * 查询  最低、最高收款金额   ，支付方式是否开通 ， 业务是否开通 
	 * @author wumeng 20150522
	 * @param param
	 */
	public AppRateTypeAndAmount queryAmountAndStatus(Map<String, String> param);
	
	/**
	 * 修改商户费率
	 * 
	 * @param p
	 * @return
	 */
	public int updateByMercId(PmsAppAmountAndRateConfig p);

}

/**
 * 
 */
package xdt.dao;

import xdt.model.OriginalOrderInfo;

/**
 * @ClassName: OriginalOrderInfoDao
 * @Description: 恒丰快捷支付 下游上送数据 接口
 * @author LiShiwen
 * @date 2016年6月20日 下午4:14:21
 *
 */
public interface OriginalOrderInfoDao extends IBaseDao<OriginalOrderInfo> {
	/**
	 * @param orderId
	 *            本地订单id
	 * @return
	 */
	public OriginalOrderInfo getOriginalOrderInfoByOrderid(String orderId);

	/**
	 * 查询原始记录信息
	 * 
	 * @param merchantOrderId
	 *            下游商户订单ids
	 * @return
	 */
	public OriginalOrderInfo getOriginalOrderInfoByMerchanOrderId(String merchantOrderId);
	
	
	 
	/** 
	 * @Description 
	 * @author Administrator
	 * @param model
	 * @return  
	 */
	  	
	public OriginalOrderInfo  selectByOriginal(OriginalOrderInfo model);
	
	/** 
	 * @Description 
	 * @author Administrator
	 * @param model
	 * @return  
	 */  	
	public OriginalOrderInfo  selectByCjtOriginal(OriginalOrderInfo model);
}

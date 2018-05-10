/**
 * 
 */
package xdt.service;

import xdt.model.OriginalOrderInfo;

/**
 * @ClassName: OriginalOrderInfo
 * @Description:恒丰快捷支付 下游 原始数据 业务
 * @author LiShiwen
 * @date 2016年6月20日 下午4:10:46
 *
 */
public interface OriginalOrderInfoService {

	/**
	 * 添加恒丰快捷支付原始记录信息
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	int save(OriginalOrderInfo entity) throws Exception;

	/**
	 * 修改恒丰快捷支付原始记录信息
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	int update(OriginalOrderInfo entity) throws Exception;

	/**
	 * 查询唯一记录
	 * 
	 * @param orderId
	 *            非下游订单id
	 * @return
	 * @throws Exception 
	 */
	OriginalOrderInfo get(String orderId) throws Exception;

}

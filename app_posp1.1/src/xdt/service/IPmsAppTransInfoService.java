package xdt.service;

import java.util.List;

import xdt.model.PmsAppTransInfo;

/**
 * 订单信息  service
 * wumeng 20150525
 */
public interface IPmsAppTransInfoService {
	
	
	/**
	 * 修改订单是否已经修改过余额标记
	 * wumeng 20150525
	 */
	public int updateOrderAccountingFlag(String orderid) throws Exception;
	/**
	 * 查询一个小时内未处理的订单
	 * 
	 * @param obj
	 * @return
	 */
	public List<PmsAppTransInfo> selectOrderPmsAppTransInfo() throws Exception;
	
	public PmsAppTransInfo searchOrderInfo(String orderId) throws Exception;
	
	
	
}

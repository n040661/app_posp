package xdt.service;

import java.util.Map;

import xdt.dto.pufa.PayRequestEntity;
import xdt.dto.pufa.QueryRequestEntity;
import xdt.dto.pufa.RefundRequestEntity;
import xdt.model.PmsAppTransInfo;

public interface PufaService {
	/**
	 * 支付交易
	 * @Description 
	 * @author Administrator
	 * @param reqeustInfo 
	 * @return
	 */
	public Map<String, Object> updatePay(PayRequestEntity reqeustInfo);

	/**
	 * 退款交易
	 * @Description 
	 * @author Administrator
	 * @param requestEntity 
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> updateRefund(RefundRequestEntity requestEntity) throws Exception;

	/**
	 * 冲正交易
	 * @Description 
	 * @author Administrator
	 * @return
	 */
	public Map<String, Object> updateFlushes();

	/**
	 * 查询交易
	 * @Description 
	 * @author Administrator
	 * @param requestInfo
	 * @return
	 * @throws Exception 
	 */
	public Map<String, Object> query(QueryRequestEntity requestInfo) throws Exception;
	/**
	 * 生成二维码
	 * @Description 
	 * @author Administrator
	 * @param reqeustInfo
	 * @return
	 */
	public Map<String, Object> updateTwoDimensionCode(PayRequestEntity reqeustInfo);

	/**
	 * 定时任务查询上游订单状态  修改状态
	 * @Description 
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @throws Exception 
	 */
	void updateOrderStatusByOrder(PmsAppTransInfo pmsAppTransInfo) throws Exception;

}

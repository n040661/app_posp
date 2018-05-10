package xdt.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PospTransInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.hengfeng.entity.PayQueryRequestEntity;
import xdt.quickpay.hengfeng.entity.PayQueryResponseEntity;
import xdt.quickpay.hengfeng.entity.PayRequestEntity;

public interface BeenQuickPayService {

	/**
	 * 快捷支付处理
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @param response 
	 * @param request 
	 * @return
	 * @throws Exception
	 */
	void payHandle(PayRequestEntity originalinfo, HttpServletRequest request, HttpServletResponse response) throws Exception;


	/**
	 * 更新账单信息
	 * 
	 * @param pmsAppTransInfo
	 * @return
	 */
	int updateMerchantBanlance(PmsAppTransInfo pmsAppTransInfo);

	/**
	 * 查询原始信息
	 * 
	 * @param tranId
	 *            本地订单id
	 * @return 原始上送信息
	 * @throws Exception
	 */
	OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception;

	/**
	 * 根据本地订单id 获取流水数据信息
	 * 
	 * @param orderId
	 * @return
	 */
	PospTransInfo getTransInfo(String orderId);

	/**
	 * 
	 * @param queryRequest
	 *            查询请求信息
	 * @throws Exception
	 */

	/**
	 * 查询原始记录信息
	 * 
	 * @param originalOrderId
	 *            原始订单id
	 * @return
	 */
	OriginalOrderInfo getoriginInfoByMerchantOrderId(String originalOrderId);

	/**
	 * 查询商户密钥信息
	 * 
	 * @param merchantId
	 *            商户号
	 * @return
	 * @throws Exception
	 */
	ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception;

	/**
	 * 查询通知信息
	 * 
	 * @return
	 */
	ViewKyChannelInfo getChannelInfo();

	OriginalOrderInfo selectByOriginal(OriginalOrderInfo queryWhere);
	/**
	 * 
	 * @Description 查询本地订单信息 
	 * @author Administrator
	 * @param queryInfo
	 * @return
	 */
	Map<String, String> ququeryOrderInfoByOrigin(PayQueryRequestEntity queryInfo);
	/**
	 * 
	 * @Description 查询本地订单状态 
	 * @author Administrator
	 * @param string
	 * @return
	 * @throws Exception 
	 */
	PayQueryResponseEntity queryLocalOrderStatus(String string) throws Exception;

	/**
	 * 定时任务查询上游订单状态  修改状态
	 * @Description 
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @throws Exception 
	 */
	void updateOrderStatusByOrder(PmsAppTransInfo pmsAppTransInfo) throws Exception;

	OriginalOrderInfo getOriginOrderInfoByPospsn(String orderId);
	/**
	 * 
	 * @Description webhook回调处理本地订单状态 
	 * @author Administrator
	 * @param respCode
	 * @throws Exception 
	 */
	void handleLocalOrderInfo(String respCode) throws Exception;

}
package xdt.service;

import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PospTransInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.hf.entity.PayResponseEntity;

public interface HfQPayService {

//	/**
//	 * 快捷获取token处理
//	 * 
//	 * @param originalinfo
//	 *            下游请求原始数据
//	 * @return
//	 * @throws Exception
//	 */
//	String payHandle(PayRequestEntity originalinfo) throws Exception;
//	/**
//	 * 快捷获取token处理
//	 * 
//	 * @param originalinfo
//	 *            下游请求原始数据
//	 * @return
//	 * @throws Exception
//	 */
//	String payHandle1(PayRequestEntity originalinfo) throws Exception;
	/**
	 * 快捷支付处理
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
	String consumepayHandle(PayRequestEntity originalinfo) throws Exception;
//	/**
//	 * 快捷获取验证码处理
//	 * 
//	 * @param originalinfo
//	 *            下游请求原始数据
//	 * @return
//	 * @throws Exception
//	 */
//	String pay(PayRequestEntity originalinfo) throws Exception;

//	/**
//	 * 查询支付结果并处理 订单 流水
//	 * 
//	 * @param queryRequest
//	 *            查询请求信息
//	 * @throws Exception
//	 */
//	void queryPayResultHandle(PayQueryRequestEntity queryRequest) throws Exception;

	/**
	 * 银联主动 请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	void otherInvoke(PayResponseEntity result) throws Exception;

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

//	/**
//	 * 查询支付结果
//	 * 
//	 * @param queryRequest
//	 *            查询请求信息
//	 * @throws Exception
//	 */
//	PayQueryResponseEntity queryPayResult(PayQueryRequestEntity queryRequest) throws Exception;

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
	 * 查询手续费
	 * 
	 * @return
	 */
	public PmsAppTransInfo getFeeInfo(String orderId) throws Exception;
	
	/**
	 * 查询原始记录信息
	 * 
	 * @param merchantOrderId
	 *            下游商户订单ids
	 * @return
	 */
	public OriginalOrderInfo getOriginalOrderInfoByMerchanOrderId(String merchantOrderId);


}
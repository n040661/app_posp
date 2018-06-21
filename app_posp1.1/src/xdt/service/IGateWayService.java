package xdt.service;

import java.util.Map;

import xdt.dto.gateway.entity.GateWayQueryRequestEntity;
import xdt.dto.gateway.entity.GateWayQueryResponseEntity;
import xdt.dto.gateway.entity.GateWayRequestEntity;
import xdt.dto.gateway.entity.GateWayResponseEntity;
import xdt.dto.gateway.entity.GatrWayGefundEntity;
import xdt.dto.nbs.alipay.AlipayParamRequest;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;

public interface IGateWayService {
	
	
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
	 * 查询原始信息
	 * 
	 * @param tranId
	 *            本地订单id
	 * @return 原始上送信息
	 * @throws Exception
	 */
	OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception;
	/**
	 * 代付查询订单信息
	 * @param tranId
	 * @return
	 * @throws Exception
	 */
	public OriginalOrderInfo getOriginOrderInfos(String tranId) throws Exception;
	/**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) throws Exception;
	
	/**
	 * 处理网关支付
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateHandle(GateWayRequestEntity gateWayRequestEntity) throws Exception;
	/**
	 * 网关主动 请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	void otherInvoke(GateWayQueryResponseEntity result) throws Exception;
	/**
	 * 网关查询功能
	 * 
	 * @param DaifuRequestEntity
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> gateWayQuery(GateWayQueryRequestEntity query);
	/**
	 * 网关入金功能
	 * 
	 * @param DaifuRequestEntity
	 * @return
	 * @throws Exception
	 */
	int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo ) throws Exception;

	
	/**
	 * 修改通道订单号
	 * @param orderId
	 * @param bpSerialNum
	 * @return
	 * @throws Exception
	 */
	public int updateBusinfo(String orderId,String bpSerialNum) throws Exception;
	/**
	 * 退款
	 * @param query
	 * @return
	 */
	public Map<String, String> gatYftk(GatrWayGefundEntity param,Map<String, String> result);
	
	/**
	 * 裕福查询接口
	 * @param param
	 * @param result
	 * @return
	 */
	public Map<String, String> gatYfQuick(GatrWayGefundEntity param ,Map<String, String> result);
}

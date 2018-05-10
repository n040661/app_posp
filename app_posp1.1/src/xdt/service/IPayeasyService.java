package xdt.service;

import java.util.List;
import java.util.Map;

import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.dto.payeasy.PayEasyQueryRequestEntity;
import xdt.dto.payeasy.PayEasyQueryResponseEntity;
import xdt.dto.payeasy.PayEasyRequestEntity;
import xdt.dto.payeasy.PayEasyResponseEntitys;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.PmsDaifuMerchantInfo;
import xdt.model.PospTransInfo;

public interface IPayeasyService {
	
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
	 * 快捷支付处理
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
	String payHandle(PayEasyRequestEntity originalinfo) throws Exception;
	
	/**
	 * 查询原始信息
	 * 
	 * @param tranId
	 *            本地订单id
	 * @return 原始上送信息
	 * @throws Exception
	 */
	OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception;
	
	OriginalOrderInfo selectByOriginal(OriginalOrderInfo queryWhere);
	
	/**
	 * 根据本地订单id 获取流水数据信息
	 * 
	 * @param orderId
	 * @return
	 */
	PospTransInfo getTransInfo(String orderId);
	
	/**
	 * 查询支付结果
	 * 
	 * @param queryInfo
	 *            查询请求信息
	 * @throws Exception
	 */
	PayEasyQueryResponseEntity queryPayResult(PayEasyQueryRequestEntity queryInfo) throws Exception;
	/**
	 * 银联主动 请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	void otherInvoke(PayEasyResponseEntitys result) throws Exception;
	
	/**
	 * 代付支付及查询功能
	 * 
	 * @param merchantId
	 *            商户号
	 * @return
	 * @throws Exception
	 */
	Map<String, String> InsertDaifu(List<DaifuRequestEntity> array) throws Exception;
	/**
	 * 根据批次号修改代付状态功能
	 * 
	 * @param no
	 *            商户号
	 * @return
	 * @throws Exception
	 */
	int UpdateDaifu(DaifuRequestEntity daifu) throws Exception;
	
	/**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) throws Exception;

}

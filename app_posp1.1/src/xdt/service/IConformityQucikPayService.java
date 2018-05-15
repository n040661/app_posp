package xdt.service;

import java.util.Map;

import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.conformityQucikPay.entity.ConformityQucikPayRequestEntity;
import xdt.quickpay.conformityQucikPay.entity.ConformityQuickPayQueryRequestEntity;

public interface IConformityQucikPayService {
	/**
	 * 查询商户密钥信息
	 * 
	 * @param merchantId
	 *            商户号
	 * @return
	 * @throws Exception
	 */
	public ChannleMerchantConfigKey getChannelConfigKey(String paramString) throws Exception;
	/**
	 * 查询原始信息
	 * 
	 * @param tranId
	 *            本地订单id
	 * @return 原始上送信息
	 * @throws Exception
	 */
	public OriginalOrderInfo getOriginOrderInfo(String paramString) throws Exception;
	/**
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String paramString) throws Exception;
	/**
	 * 处理快捷(WAP)支付
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> payHandle(ConformityQucikPayRequestEntity ConformityQucikPayRequestEntity) throws Exception;
	/**
	 * 银联主动 请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	public void otherInvoke(String paramString1, String paramString2) throws Exception;
	/**
	 * 快捷(WAP版)查询功能
	 * 
	 * @param ConformityQuickPayQueryRequestEntity
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> quickQuery(ConformityQuickPayQueryRequestEntity paramQueryRequestEntity);
	/**
	 * 快捷(WAP版)100%入金功能
	 * 
	 * @param OriginalOrderInfo
	 * @return
	 * @throws Exception
	 */
	public int updatePmsMerchantInfo(OriginalOrderInfo paramOriginalOrderInfo) throws Exception;
	/**
	 * 快捷(WAP版)80%入金功能
	 * 
	 * @param OriginalOrderInfo
	 * @return
	 * @throws Exception
	 */
	public int updatePmsMerchantInfo80(OriginalOrderInfo paramOriginalOrderInfo) throws Exception;

}

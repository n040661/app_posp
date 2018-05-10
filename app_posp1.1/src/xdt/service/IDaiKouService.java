package xdt.service;

import java.util.Map;

import com.uns.inf.api.model.CallBack;

import xdt.dto.payeasy.DaifuRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.quickpay.daikou.model.DaikouRequsetEntity;
import xdt.quickpay.hf.entity.PayRequestEntity;
import xdt.quickpay.hf.entity.PayResponseEntity;
import xdt.quickpay.ysb.model.YsbRequsetEntity;

public interface IDaiKouService {

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
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) throws Exception;

	Map<String, String> setResp(String respCode, String respInfo);

	/**
	 * 子协议录入接口
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
	Map<String, String> customerRegister(DaikouRequsetEntity originalinfo) throws Exception;

	/**
	 * 委托代扣接口
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> payHandle(DaikouRequsetEntity originalinfo) throws Exception;

	/**
	 * 银生宝主动 请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	void otherInvoke(CallBack result) throws Exception;

}

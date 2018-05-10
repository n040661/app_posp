package xdt.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import xdt.dto.gateway.entity.GateWayRequestEntity;
import xdt.dto.payeasy.PayEasyResponseEntitys;
import xdt.dto.quickPay.entity.ConsumeRequestEntity;
import xdt.dto.quickPay.entity.ConsumeResponseEntity;
import xdt.dto.quickPay.entity.MessageRequestEntity;
import xdt.dto.quickPay.entity.QueryRequestEntity;
import xdt.dto.quickPay.entity.QueryResponseEntity;
import xdt.dto.transfer_accounts.entity.DaifuQueryRequestEntity;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;
import xdt.model.QuickPayMessage;
import xdt.quickpay.shyb.entity.ShybQuickCallbackEntity;
import xdt.quickpay.shyb.entity.ShybQuickPayRequestEntity;
import xdt.quickpay.shyb.entity.ShybQuickRequestEntity;
import xdt.quickpay.shyb.entity.ShybUpdateRateQueryRequestEntity;
import xdt.quickpay.shyb.entity.ShybUpdateRateRequestEntity;

/**
 * 上海易宝快捷支付服务层
 * User: YanChao.Shang
 * Date: 17-3-9
 * Time: 下午2:29
 * To change this template use File | Settings | File Templates.
 */
public interface IShybQuickPayService {
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
	
	/**
	 * 处理上海易宝快捷进件
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateHandle(ShybQuickRequestEntity registerRequestEntity) throws Exception;
	/**
	 * 处理上海易宝快捷修改费率
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateRate(ShybUpdateRateRequestEntity  updateRateRequestEntity) throws Exception;
	
	/**
	 * 处理上海易宝快捷支付
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> payHandle(ShybQuickPayRequestEntity quickPayRequestEntity) throws Exception;
	
	
	
	/**
	 * 银联主动 请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	void otherInvoke(ShybQuickCallbackEntity result) throws Exception;
	
	/**
	 * 快捷查询功能
	 * 
	 * @param DaifuRequestEntity
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> quickQuery(QueryRequestEntity query);


}

package xdt.service;

import java.util.Map;

import org.slf4j.Logger;

import xdt.dto.nbs.alipay.AlipayParamRequest;
import xdt.dto.nbs.alipay.AlipayParamResponse;
import xdt.dto.nbs.micro.WechatMicroRequest;
import xdt.dto.nbs.micro.WechatMicroResponse;
import xdt.dto.nbs.orderquery.WechatOrderQueryRequest;
import xdt.dto.nbs.orderquery.WechatOrderQueryResponse;
import xdt.dto.nbs.scan.WechatScannedRequest;
import xdt.dto.nbs.scan.WechatScannedResponse;
import xdt.dto.nbs.settle.SettleQueryWebPayRequest;
import xdt.dto.nbs.settle.SettleQueryWebPayResponse;
import xdt.dto.nbs.settle.SettleWebPayRequest;
import xdt.dto.nbs.settle.SettleWebPayResponse;
import xdt.dto.nbs.webpay.WechatWebPay;
import xdt.dto.nbs.webpay.WechatWebPayResponse;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsBusinessPos;


/**
 * WechatService Interface
 *
 * @author zhang.hui@pufubao.net
 * @version v1.0
 * @date 2016/11/1 15:38
 */
public interface IWechatService {


	/**
	 * 处理北农商
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, Object> updateHandle(AlipayParamRequest alipayParamRequest) throws Exception;
    /**
     * 执行扫码请求
     *
     * @param scannedRequest
     * @param log
     * @return
     */
    WechatScannedResponse doScanned(WechatScannedRequest scannedRequest, Logger log);
    /**
     * 清算结果请求
     *
     * @param scannedRequest
     * @param log
     * @return
     */
    SettleWebPayResponse doScanned(SettleWebPayRequest settle, Logger log);
    /**
     * 清算结果查询请求
     *
     * @param scannedRequest
     * @param log
     * @return
     */
    SettleQueryWebPayResponse doScanned(SettleQueryWebPayRequest settle, Logger log);

    /**
     * 执行刷卡(小额)请求
     *
     * @param microRequest
     * @param log
     * @return
     */
    WechatMicroResponse doMicro(WechatMicroRequest microRequest, Logger log);

    /**
     * 执行订单查询请求
     *
     * @param orderQueryRequest
     * @param log
     * @return
     */
    WechatOrderQueryResponse doOrderQuery(WechatOrderQueryRequest orderQueryRequest, Logger log);
    /**
     * 进行公共号支付
     *
     * @param pay
     * @return
     */
    Map<String, String> pay(WechatWebPay wechat, Logger log) throws Exception;
    /**
     * 处理生成二维码
     * @param wechatScannedRequest
     * @return
     */
    
    public WechatScannedResponse updateTwoDimensionCode(WechatScannedRequest wechatScannedRequest);
	/**
	 * 北农商主动 请求返回处理订单状态
	 * 
	 * @param wechat
	 *            支付响应信息
	 * @throws Exception
	 */
	void otherInvoke(WechatWebPayResponse wechat) throws Exception;
	/**
	 * 查询原始信息
	 * 
	 * @param tranId
	 *            本地订单id
	 * @return 原始上送信息
	 * @throws Exception
	 */
	OriginalOrderInfo getOriginOrderInfo(String tranId) throws Exception;
	
	WechatMicroResponse payByCard(WechatMicroRequest wechatMicroRequest);
	
//	AlipayScanParamResponse alipayScan(AlipayParamRequest alipayScanParamRequest, Logger log);
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
	 * 查询上游商户号和密钥
	 * 
	 * @param obj
	 * @return
	 */
	public PmsBusinessPos selectKey(String merid) throws Exception;
	/**
	 * 支付宝支付接口
	 * 
	 * @param obj
	 * @return
	 */
	public  Map<String, Object> alipayParam(AlipayParamRequest alipayParamRequest,Map<String, Object> result, PmsBusinessPos busInfo);

	/**
	 * 支付宝查询接口
	 * 
	 * @param obj
	 * @return
	 */
	public  Map<String, Object> alipayScanSelect(AlipayParamRequest alipayParamRequest,Map<String, Object> result, PmsBusinessPos busInfo) throws Exception;
	
	/**
	 * 支付宝退款接口
	 * 
	 * @param obj
	 * @return
	 */
	public Map<String,Object> alipayRefund(AlipayParamRequest alipayParamRequest,Map<String, Object> result, PmsBusinessPos busInfo) throws Exception;
	/**
	 * 支付宝撤销接口
	 * 
	 * @param obj
	 * @return
	 */
	public Map<String,Object> alipayReverseorder(AlipayParamRequest alipayParamRequest,Map<String, Object> result, PmsBusinessPos busInfo) throws Exception;
	/**
	 * 微信关单接口
	 * 
	 * @param obj
	 * @return
	 */
	public Map<String,Object> wechatCloseorder(AlipayParamRequest alipayParamRequest,Map<String, Object> result, PmsBusinessPos busInfo) throws Exception;
	/**
	 * 微信退款接口
	 * 
	 * @param obj
	 * @return
	 */
	public Map<String,Object> wechatRefund(AlipayParamRequest alipayParamRequest,Map<String, Object> result, PmsBusinessPos busInfo) throws Exception;
	/**
	 * 微信撤销接口
	 * 
	 * @param obj
	 * @return
	 */
	public Map<String,Object> wechatReverseorder(AlipayParamRequest alipayParamRequest,Map<String, Object> result, PmsBusinessPos busInfo) throws Exception;
}

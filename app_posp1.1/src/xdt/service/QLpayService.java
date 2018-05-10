package xdt.service;

import java.util.Map;

import xdt.dto.pufa.QueryRequestEntity;
import xdt.dto.qianlong.PayRequestEntity;
import xdt.dto.qianlong.QueryRequestDto;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsAppTransInfo;
import xdt.model.PmsMerchantInfo;
import xdt.model.PmsWeixinMerchartInfo;
import xdt.quickpay.qianlong.model.Merchant;
import xdt.quickpay.qianlong.model.Order;
import xdt.quickpay.qianlong.model.PayResponseEntity;

public interface QLpayService {
	
 	/**
	 * 钱龙支付注册信息处理
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
	Map<String, String> customerRegister(PmsWeixinMerchartInfo weixin) throws Exception;
	/**
	 * 生成二维码
	 * @Description 
	 * @author Administrator
	 * @param reqeustInfo
	 * @return
	 */
	public Map<String, String> twoDimensionCode(PayRequestEntity reqeustInfo)  throws Exception;
	/**
	 * 付款结果查询
	 * @Description 
	 * @author Administrator
	 * @param pmsAppTransInfo
	 * @throws Exception 
	 */
	public Map<String, String>  payQuery(PayResponseEntity temp) throws Exception;
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
	 * 生成固态二维码
	 * @Description 
	 * @author Administrator
	 * @param requestInfo
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> solidtwodimensionCode(PayRequestEntity pay) throws Exception;
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
	 * 请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	void otherInvoke(PayResponseEntity result) throws Exception;


}

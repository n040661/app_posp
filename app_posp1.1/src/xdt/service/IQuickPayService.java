package xdt.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import com.yufusoft.payplatform.security.cipher.YufuCipher;

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

/**
 * 快捷支付服务层
 * User: YanChao.Shang
 * Date: 17-3-9
 * Time: 下午2:29
 * To change this template use File | Settings | File Templates.
 */
public interface IQuickPayService {
    /**
     * 校验快捷支付的卡号在本地是否有记录
     * @param request
     * @return '0':没有记录  ‘1’有记录
     */
    String  checkLocalCardRecord(HttpSession session,String request);
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
	 * 处理快捷短信
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> updateHandle(MessageRequestEntity gateWayRequestEntity) throws Exception;
	
	/**
	 * 处理快捷支付
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> payHandle(ConsumeRequestEntity gateWayRequestEntity) throws Exception;
	
	/**
	 * 处理贷还预下单接口
	 * @param reqData
	 * @return
	 * @throws Exception 
	 */
	Map<String, String> loanStillPay(MessageRequestEntity originalinfo) throws Exception;
	
	
	/**
	 * 银联主动 请求返回处理订单状态
	 * 
	 * @param result
	 *            支付响应信息
	 * @throws Exception
	 */
	void otherInvoke(ConsumeResponseEntity result) throws Exception;
	
	/**
	 * 快捷查询功能
	 * 
	 * @param DaifuRequestEntity
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> quickQuery(QueryRequestEntity query);
	/**
	 * 裕福查询银行卡信息
	 * @param originalinfo
	 * @param pmsBusinessPos
	 * @param cipher
	 * @return
	 */
	public Map<String, String> selectCard(MessageRequestEntity entity);
}

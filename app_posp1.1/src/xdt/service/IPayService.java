package xdt.service;

import java.util.Map;

import xdt.dto.pay.PayRequest;
import xdt.model.OriginalOrderInfo;
import xdt.model.PmsMerchantInfo;

public interface IPayService {

	Map<String, String> pay(PayRequest payRequest,Map<String, String> result);
	Map<String, String> token(PayRequest payRequest,Map<String, String> result);
	Map<String, String> register(PayRequest payRequest,Map<String, String> result);//注册
	Map<String, String> openMessages(PayRequest payRequest,Map<String, String> result);//商户侧开通短信
	Map<String, String> OpenCard(PayRequest payRequest,Map<String, String> result);//商户侧绑卡开通(无通知)
	Map<String, String> update(PayRequest payRequest,Map<String, String> result);//商户费率、结算银行卡信息变更
	Map<String, String> consumerPayment(PayRequest payRequest,Map<String, String> result);//消费支付(后台通知)
	Map<String, String> bill(PayRequest payRequest,Map<String, String> result);//对账单获取
	Map<String, String> balance(PayRequest payRequest,Map<String, String> result);//余额查询
	public int UpdatePmsMerchantInfo(OriginalOrderInfo originalInfo,Map<String, String> map) throws Exception;
	Map<String, String> updateByOrderId(String orderId,String orderStatus,Map<String, String> result);
	Map<String, String> select(PayRequest payRequest,Map<String, String> result);
	Map<String, String> paySelect(PayRequest payRequest,Map<String, String> result);
	Map<String, String> payCode(PayRequest payRequest,Map<String, String> result);//支付短信
	Map<String, String> withdrawals(PayRequest payRequest,Map<String, String> result);//无卡提现
	Map<String, String> cardPay(PayRequest payRequest,Map<String, String> result);//九派网关
	public int UpdateDaifu(String batchNo, String responsecode)throws Exception;
	int add(PayRequest payRequest, PmsMerchantInfo info, Map<String, String> result, String string) throws Exception;
	
	
	
}

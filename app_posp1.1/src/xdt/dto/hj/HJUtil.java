package xdt.dto.hj;

import xdt.dto.BaseUtil;

public class HJUtil {

	public static final String merchantNo="888100000004071";
	public static final String privateKey="d387e58e8fc04334b685bfb2cc232cf8";
	public static final String notifyUrl=BaseUtil.url+"/HJController/notifyUrl.action";
	public static final String returnUrl=BaseUtil.url+"/HJController/returnUrl.action";
	
	public static final String cardPay="https://www.joinpay.com/gateway/gateway_init.action";
	public static final String scanCodePay="https://www.joinpay.com/trade/uniPayApi.action";
	public static final String quickPay="https://www.joinpay.com/trade/agreementSmsApi.action";
	
	public static final String agreementPay="https://www.joinpay.com/trade/agreementSignApi.action";
	
	public static final String pay="https://www.joinpay.com/trade/batchProxyPayNew.action";
	public static final String xinPay="https://www.joinpay.com/payment/pay/singlePay";
	public static final String paySelect="https://www.joinpay.com/trade/queryBatchProxyPay.action";
	public static final String Version="1.0";
	public static final String Version2="2.0";

	
}

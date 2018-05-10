package xdt.test;

import java.math.BigDecimal;

import xdt.util.HttpURLConection;

public class hengfengUtilTest {
	
	public static void main(String[] args) {
		
		String url="http://api.zhongnanpay.com:3022/hmpay/online/qlqkNotify.do";
		
		String params="version=2.0.1&encoding=&certId=&signature=&signMethod=&txnType=&txnSubType=&bizType=&accessType=&merId=诚付商户&orderId=ZN15402017122216202676624&txnTime=20171222162026&txnAmt=50000&currencyCode=&reqReserved=&reserved=&queryId=&respCode=00&respMsg=成功[0000000]&settleAmt=&settleCurrencyCode=&settleDate=&tradeNo=&tradeTime=&exchangeDate=&exchangeRate=&accNo=&payCardType=&payType=20&payCardNo=&payCardIssueName=&accSplitData=";
		
		String result = HttpURLConection.httpURLConnectionPOST(url,
				params);
		
		System.out.println(result);
		
		BigDecimal dfactAmount = new BigDecimal(1001);
		// 费率
		BigDecimal fee = new BigDecimal(3.001);
		double settleFee=1;
		
		BigDecimal payAmount = dfactAmount.subtract(fee).subtract(new BigDecimal(settleFee));
		
		System.out.println(payAmount.toString());
	}

}

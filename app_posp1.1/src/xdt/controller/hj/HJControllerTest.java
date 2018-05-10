package xdt.controller.hj;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import xdt.dto.pay.PayRequest;
import xdt.service.IPayService;
import xdt.util.HttpURLConection;
import xdt.util.JsdsUtil;
import xdt.util.utils.RequestUtils;
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring.xml","classpath:spring-mvc.xml","classpath:spring-mybatis.xml"})
public class HJControllerTest {

	@Resource
	private IPayService iPayService;

	@Test
	public void testNotifyUrl() {
		
		Map<String, String> result=new HashMap<>();
		PayRequest payRequest =new PayRequest();
		payRequest.setType("28");///cj012
		payRequest.setOrderId("152083943135");
		payRequest.setMerchantCode("11362705");
		payRequest.setMerchantId("10032061473");
		payRequest.setLiceneceNo("120105197510055420");
		payRequest.setAcctNo("4512893456196113");
		payRequest.setPhone("13323358548");
		payRequest.setAcctName("李娟");
		payRequest.setBusinessType("2");
		payRequest.setBankName("中国兴业银行");
		payRequest.setBranchBankName("中国兴业银行");
		payRequest.setBankCode("309");
		payRequest.setBankAbbr("CIB");
		payRequest.setPmsbankNo("103110025032");
		payRequest.setProvince("天津市");
		payRequest.setCity("天津市");
		payRequest.setDebitRate("0.004");
		payRequest.setDebitCapAmount("99999900");
		payRequest.setCreditRate("0.004");
		payRequest.setCreditCapAmount("99999900");
		payRequest.setWithdrawDepositRate("0");
		payRequest.setWithdrawDepositSingleFee("20");
		payRequest.setAccountType("2");
		payRequest.setCvv2("795");
		payRequest.setYear("20");
		payRequest.setMonth("12");
		payRequest.setAmount("9900");
		payRequest.setProductName("大饼鸡蛋");
		payRequest.setProductDesc("大饼夹一切");
		payRequest.setMerchantUuid("48a30b58-7624-4520-b9dc-f0dd7d21b304");
		payRequest.setSmsCode("000000");
		payRequest.setChangeType("4");
		payRequest.setRateCode("1003007");
		payRequest.setPayNo("5899b58689b64141a357dbd8078dbb64");
		payRequest.setFlowReportTime("2018-01-22");
		payRequest.setSummary("0");
		//TreeMap<String, String> results = new TreeMap<String, String>();
		//results.putAll(JsdsUtil.beanToMap(payRequest));
		//String paramSrc = RequestUtils.getParamSrc(results);
		//String ss ="merchantCode=10202133&merchantFee=350&orderId=1513827261318&orderStatus=2&payAmount=100000&payDate=1513827381000&payNo=a88005a39df1485187274b2a526e80d4&receiveAmount=99450&requestId=932b3e8ab132494daf93e34d0e1becd1&respCode=000000&respMsg=SUCCESS&settleDate=20171222&sign=dedd23217d89ca26737248e53b6fdd35&withdrawDepositFee=200";

		//String s =HttpURLConection.httpURLConnectionPOST("http://60.28.24.164:8102/app_posp/PayController/paySign.action", paramSrc);
		//String s1 =HttpURLConection.httpURLConnectionPOST("http://60.28.24.164:8102/app_posp/PayController/quickPay.action", paramSrc+"&sign="+s);
		
		//String s1 =HttpURLConection.httpURLConnectionPOST("http://60.28.24.164:8102/app_posp/PayController/notifyUrl.action", ss);
		//result =iPayService.register(payRequest, result);
		//result =iPayService.openMessages(payRequest, result);
		//result =iPayService.OpenCard(payRequest, result);
		//result =iPayService.payCode(payRequest, result);
		//result =iPayService.consumerPayment(payRequest, result);
		//result =iPayService.update(payRequest, result);
		//result =iPayService.withdrawals(payRequest, result);
		//result =iPayService.bill(payRequest, result);
		//result =iPayService.balance(payRequest, result);
		result =iPayService.paySelect(payRequest, result);
		
		System.out.println("213:"+result);
	}

}

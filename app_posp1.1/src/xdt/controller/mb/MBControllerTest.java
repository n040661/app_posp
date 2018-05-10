package xdt.controller.mb;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import xdt.dto.mb.MBReqest;
import xdt.dto.pay.PayRequest;
import xdt.service.IMBService;
import xdt.service.IPayService;
import xdt.util.HttpURLConection;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月8日 上午11:09:14 
* 类说明 
*/
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring.xml","classpath:spring-mvc.xml","classpath:spring-mybatis.xml"})
public class MBControllerTest {

	@Resource
	private IMBService service;
	
	@Resource
	private IPayService payService;
	
	
	
	
	
	
	
	@Test
	public void test() {
		Map<String, String> result =new HashMap<>();
		/*MBReqest mbReqest =new MBReqest();
		mbReqest.setMerId("10032061473");
		mbReqest.setOrderId("123asd2131231235423");
		mbReqest.setTransAmount("1000");
		mbReqest.setAccType("01");
		mbReqest.setAccNo("6222080302013903148");
		mbReqest.setAccName("李娟");
		mbReqest.setCerType("01");
		mbReqest.setCerNumber("120105197510055420");
		mbReqest.setMobile("13323358548");
		mbReqest.setType("cj005");
		result =service.unionPayScanCode(mbReqest, result);
		
		System.out.println("12312:"+result);*/
		
		/*PayRequest payRequest =new PayRequest();
		payRequest.setMerchantId("10032061473");
		payRequest.setAmount("1000");
		payRequest.setOrderId("wesad23213123");
		result =payService.cardPay(payRequest, result);
		System.out.println(result);*/
		String paramSrc="businessType=1100&merId=936775585060000&orderId=CJZF1513112312321312&refCode=00&refMsg=%BD%BB%D2%D7%B3%C9%B9%A6&signData=A8C8665917B90F0B137C2C363309011F&transAmount=10.0&transDate=20171213&versionId=001";
		
		String s1 =HttpURLConection.httpURLConnectionPOST("http://60.28.24.164:8102/app_posp/MBController/notifyUrl.action", paramSrc);
		
		System.out.println(s1);
		
		
	}

}

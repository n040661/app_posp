package xdt.controller.lhzf;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;

import xdt.dto.lhzf.LhzfRequset;
import xdt.dto.pay.PayRequest;
import xdt.dto.ys.DateUtil;
import xdt.service.ILhzfService;
import xdt.service.IYSService;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年1月8日 下午5:08:46 
* 类说明 
*/
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring.xml","classpath:spring-mvc.xml","classpath:spring-mybatis.xml"})
public class LhzfControllerTest {

	@Resource 
	private IYSService service;
	@Test
	public void test() {
		PayRequest payRequest=new PayRequest();
		payRequest.setOrderId(DateUtil.getSimpleDateFormat(DateUtil.DATE_FORMAT_10).format(new Date()));
		payRequest.setMerchantId("10032061473");
		payRequest.setDebitRate("0.0047");
		
		payRequest.setAcctNo("62533990097935869");
		payRequest.setWithdrawDepositSingleFee("350");
		payRequest.setAcctName("高立明");
		payRequest.setPmsbankNo("103100000026");
		payRequest.setLiceneceNo("120224199303303413");
		payRequest.setPhone("18322276803");
		payRequest.setProvince("天津市");
		payRequest.setCity("天津市");
		payRequest.setMerchantCode("3c7fcf73fc5d4e248fef2c85fc64f057");
		payRequest.setBusinessType("0");
		payRequest.setChangeType("2");
		payRequest.setAmount("50000");
		Map<String, String> result =new HashMap<>();
		//result =service.register(payRequest, result);
		//result =service.update(payRequest, result);
		result =service.quickPay(payRequest, result);
		
		System.out.println(JSON.toJSON(result));
	}

}

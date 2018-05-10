package xdt.quickpay.hengfeng.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import xdt.quickpay.hengfeng.entity.PayQueryRequestEntity;
import xdt.service.HfQuickPayService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml", "classpath:spring-quartz.xml",
		"classpath:spring-mybatis.xml" })
public class HfQuickPayServiceTest {

	@Resource
	private HfQuickPayService payService;

	@Test
	public void test() throws Exception {
		System.out.println("查询商户密钥信息");
		System.out.println("密钥:" + payService.getChannelConfigKey("100130765104103"));
	}

	@Test
	public void testPayQuery() {
		PayQueryRequestEntity queryRequest = new PayQueryRequestEntity();
		queryRequest.setTransactionId("1801466577325760");
		try {
			payService.queryPayResultHandle(queryRequest);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}

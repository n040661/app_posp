package xdt.service;

import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import xdt.model.PmsWeixinMerchartInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml","classpath:spring-mybatis.xml"})
public class PmsWeixinMerchartInfoServiceTest {

	@Resource
	public PmsWeixinMerchartInfoService weixinService;

	@Test
	public void testSelectByPrimaryKey() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegister() {
		PmsWeixinMerchartInfo model = new PmsWeixinMerchartInfo();
		model.setAccount("12206027069");
		model.setPassword("lsw123");
		weixinService.updateRegister(model);
	}

	@Test
	public void testInsert() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertSelective() {
		PmsWeixinMerchartInfo model = new PmsWeixinMerchartInfo();

		weixinService.insertSelective(model);
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateByPrimaryKeySelective() {
		PmsWeixinMerchartInfo model = new PmsWeixinMerchartInfo();
		model=weixinService.selectByPrimaryKey("12206027068");
		model.setRealName("张三");
		model.setRate("0.0034");
		weixinService.updateByPrimaryKeySelective(model);
	}

	@Test
	public void testUpdateByPrimaryKey() {
		fail("Not yet implemented");
	}

}

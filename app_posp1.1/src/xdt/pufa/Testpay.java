package xdt.pufa;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import xdt.pufa.base.PufaFieldDefine;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring.xml",
		"classpath:spring-mybatis.xml", "classpath:spring-quartz.xml" })
public class Testpay   {

	@Autowired
	PufaPayService pay ;
	
	@Test
	public void testPya() {
		
		Map<String, Object> req = new HashMap<String, Object>();
//		req.put(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD,"63200000");//机构号--上线不用传的
		
		req.put(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD,"100510112345708");//商户号-我们商户号，将用来筛选路由
		req.put(PufaFieldDefine.PF_REQ_BODY_AUTH_CODE,"288658739413121757");//授权码-扫码设备读取
		req.put(PufaFieldDefine.PF_HEAD_TRAN_CD,"1131");//授权码-扫码设备读取
		req.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT,"1");//交易金额-分为单位
		req.put(PufaFieldDefine.PF_HEAD_VERSION, "1.1");
		req.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, "10000062");//商户号-我们订单号
//		req.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, "10000010");//商户号-我们订单号
		
		
		//产品代码
		req.put(PufaFieldDefine.PF_HEAD_PROD_CD, "1151");//
		//业务代码
		req.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000008");//-支付宝
		
		
		Map<String, Object> createMerpay = pay.createMerpay(req);
		System.out.println(createMerpay);
		
		
		
	}
	
	@Test
	public void testQry() {
		
		
		Map<String, Object> req = new HashMap<String, Object>();
		req.put(PufaFieldDefine.PF_HEAD_TRAN_CD,"5131");//授权码-扫码设备读取
		req.put(PufaFieldDefine.PF_HEAD_PROD_CD, "1151");//
		req.put(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID,"10000064");//订单号
		req.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000008");//-支付宝
		
		Map<String, Object> query = pay.query(req);
		System.out.println(query);
		
	}
	
	
	@Test
	public void testFlushes(){
		Map<String, Object> req = new HashMap<String, Object>();
		req.put(PufaFieldDefine.PF_HEAD_TRAN_CD,"4131");//授权码-扫码设备读取
		req.put(PufaFieldDefine.PF_HEAD_PROD_CD, "1151");//
		req.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000008");//-支付宝
		req.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, "10000061");//商户号-我们本次前端订单号
		req.put(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID,"10000061");//原前端订单号
		pay.flushes(req);
	}
	
	@Test
	public void testRefund(){
		Map<String, Object> req = new HashMap<String, Object>();
		req.put(PufaFieldDefine.PF_HEAD_TRAN_CD,"3131");//授权码-扫码设备读取
		req.put(PufaFieldDefine.PF_HEAD_PROD_CD, "1151");//
		req.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000008");//-支付宝
		req.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, "10000062");//商户号-我们本次前端订单号
		req.put(PufaFieldDefine.PF_REQ_BODY_ORIG_ORDER_ID,"10000064");//原前端订单号
		req.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT,"1");//交易金额-分为单位
		req.put(PufaFieldDefine.PF_REQ_BODY_REFUND_REASON,"1");//交易金额-分为单位
		pay.refund(req);
	}
	
	
	@Test
	public void testCreateUserOrder() {
		
		Map<String, Object> req = new HashMap<String, Object>();
//		req.put(PufaFieldDefine.PF_REQ_BODY_INS_ID_CD,"63200000");//机构号--上线不用传的
		
		req.put(PufaFieldDefine.PF_REQ_BODY_MCHNT_CD,"100510112345708");//商户号-我们商户号，将用来筛选路由
		req.put(PufaFieldDefine.PF_HEAD_TRAN_CD,"7131");//授权码-扫码设备读取
		req.put(PufaFieldDefine.PF_REQ_BODY_TRAN_AMT,"1");//交易金额-分为单位
		req.put(PufaFieldDefine.PF_HEAD_VERSION, "1.1");
		req.put(PufaFieldDefine.PF_REQ_BODY_ORDER_ID, "10000065");//商户号-我们订单号
		
		
		//产品代码
		req.put(PufaFieldDefine.PF_HEAD_PROD_CD, "1151");//
		//业务代码
		req.put(PufaFieldDefine.PF_HEAD_BIZ_CD, "0000008");//-支付宝
		
		
		Map<String, Object> createMerpay = pay.createUserPayOrder(req);
		System.out.println(createMerpay);
		
		
		
	}
	
}

package xdt.controller.hfb;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import xdt.dto.hfb.HFBPayRequest;
import xdt.dto.hfb.HttpsUtil;
import xdt.model.OriginalOrderInfo;
import xdt.service.IHFBService;
import xdt.service.IHMService;
import xdt.service.impl.HFBServiceImpl;
import xdt.util.HttpURLConection;
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring.xml","classpath:spring-mvc.xml","classpath:spring-mybatis.xml"})
public class HFBServiceImplTest {

	@Resource
	private IHMService ihmService;
	@Test
	public void testPay() {
//		HFBPayRequest hfbRequest=new HFBPayRequest();
		//Map<String, String> result=new HashMap<>();
//		Map<String, String> s =ihfbService.pay(hfbRequest, result);
//		System.out.println(JSON.toJSON(s));
//	
		//result.put("merchantBatchNo", "1507532004343");
		//result.put("merchantId", "100381");
		//result.put("successNum", "1");
		//result.put("transferDetails", "[{\"merchantPayNo\":\"CJZF1507532004343\",\"amount\":\"1.00\",\"status\":\"1000\"}]");
		
		//String ss ="{\"AcceptStatus\":\"F\",\"InputCharset\":\"UTF-8\",\"PartnerId\":\"200001160096\",\"RetCode\":\"REQUIRED_FIELD_NOT_EXIST\",\"RetMsg\":\"[\"BankCommonName\",\"BankCommonName\"]必填字段未填写\",\"Sign\":\"YBOiDr7eLcgkEM5ScC/FnGezxc/Har5pEzy84W07wbFuTZaJoeCCmKVFcdZqZ9j1bwfwNsyQXm2b8q8fUKBouJB86ylqBk/uz39yFPN90OMXWcr2woWgqkpZlo9xEyzztFgZbYQk72ameG6LNMcj7F0R+FKPUPdzA5TH1Gkd79A=\",\"SignType\":\"RSA\",\"TradeDate\":\"20171117\",\"TradeTime\":\"162146\"}";
		//String ss ="{\"merchantBatchNo\":\"1507532004343\",\"merchantId\":\"100381\",\"successNum\":\"1\",\"transferDetails\":\"[{\"merchantPayNo\":\"CJZF1507532004343\",\"amount\":\"1.00\",\"status\":\"1000\"}]\",\"hyBatchNo\":\"201710091019\",\"sign\":\"0d01029d1fc40b4c389f6598799661ef\",\"successAmount\":\"1.00\"}";
		//String s =HttpURLConection.httpURLConnectionPOST("http://60.28.24.164:8102/app_posp/HMController/notifyUrl.action", ss);
		//JSONObject o =(JSONObject) JSONObject.parse(ss);
		//System.out.println(o.get("RetMsg"));
		OriginalOrderInfo originalInfo =new OriginalOrderInfo();
		originalInfo.setPid("10032061473");
		originalInfo.setOrderAmount("1");
		originalInfo.setOrderId("1513927361711");
		try {
			ihmService.UpdatePmsMerchantInfo(originalInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}

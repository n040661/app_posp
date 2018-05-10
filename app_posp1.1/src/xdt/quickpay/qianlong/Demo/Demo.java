package xdt.quickpay.qianlong.Demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.druid.util.HttpClientUtils;
import com.alibaba.fastjson.JSON;

import xdt.dto.BaseUtil;
import xdt.quickpay.qianlong.api.ChroneApi;
import xdt.quickpay.qianlong.model.Order;
import xdt.quickpay.qianlong.util.HttpClientHelper;
import xdt.quickpay.qianlong.util.HttpResponse;
import xdt.quickpay.qianlong.util.SdkUtil;

/**
 * 注意：本demo的所有参数均为测试环境,上线之后请替换为正式环境参数
 * @author Jerry
 */
public class Demo {
	
	public static void main(String[] args) {
		//商户注册
//		Merchant merchant = new Merchant();
//		merchant.setAccount("18201868341");
//		merchant.setCardType("1");
//		merchant.setCardNo("6268228092275687");
//		merchant.setMchntName("测试商户");
//		merchant.setRealName("张三");
//		merchant.setPmsBankNo("308584000013");
//		merchant.setCertType("00");
//		merchant.setCertNo("110723198809242346");
//		merchant.setPassword("123456");
//		merchant.setMobile("18201868338");
//		boolean flag = ChroneApi.regist(merchant);
//		System.out.println("注册结果："+flag);
//		Merchant merchant = new Merchant();
//		merchant.setAccount("15222871910");
//		merchant.setCardType("1");
//		merchant.setCardNo("6222020302064298250");
//		merchant.setMchntName("测试商户");
//		merchant.setRealName("徐雷");
//		merchant.setPmsBankNo("102110001181");
//		merchant.setCertType("00");
//		merchant.setCertNo("341602199112223176");
//		merchant.setPassword("123456");
//		merchant.setMobile("15222871910");
//		boolean flag = ChroneApi.regist(merchant);
//		System.out.println("注册结果："+flag);
		
		//扫码支付
//		Order order = new Order();
//		order.setSource(0);
//		order.setAmount(1000);
//		order.setAccount("18201868341");
//		order.setSettleAmt(990);
//		order.setTranTp(0);
//		order.setOrderNo(System.currentTimeMillis()+"");
//		String qrcode = ChroneApi.qrpay(order);
//		System.out.println(qrcode);
		
		//真实数据
//		Order order = new Order();
//		order.setSource(0);
//		order.setAmount(100);
//		order.setSubject("aaaa");
//		order.setAccount("18902195994");
//		order.setSettleAmt(1);
//		order.setTranTp(1);
//		order.setOrderNo(System.currentTimeMillis()+"");
//		String qrcode = ChroneApi.qrpay(order);
//		System.out.println(qrcode);
		
		
		
		//被扫扫码支付
//		Order order = new Order();
//		order.setSource(0);
//		order.setAmount(1000);
//		order.setAccount("18201868341");
//		order.setSettleAmt(990);
//		order.setTranTp(0);
//		order.setOrderNo(System.currentTimeMillis()+"");
//		Map<String, String> qrcode = ChroneApi.fqrpay(order);
//		System.out.println(qrcode);
		String url=BaseUtil.url+"/ql/paySign.action";
		Map<String, String> maps=new HashMap<String,String>();
		maps.put("source","0");
		maps.put("subject", "aaaaaaaa");
		maps.put("account", "13312197275");
		maps.put("amount", "100");
		maps.put("orgOrderNo","QL20161213093722003");
		maps.put("tranTp","1");
		List<String[]> headers = new ArrayList<String[]>();
		headers.add(new String[]{"Content-Type","application/json"});
		String postData = JSON.toJSONString(maps);	
		HttpResponse response=HttpClientHelper.doHttp(url, HttpClientHelper.POST, headers, "utf-8", postData, "60000");
		if(StringUtils.isNotEmpty(response.getRspStr()))
		{
			System.out.println("aaaaaaaaa");
		}
	}
}

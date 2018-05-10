package xdt.test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import xdt.dto.hfb.HttpsUtil;
import xdt.quickpay.nbs.common.util.MD5Util;
import xdt.util.HttpUtil;
import xdt.util.JsdsUtil;
import xdt.util.utils.RequestUtils;

public class YSBJUtilTest {

	public static void main(String[] args) {

		TreeMap<String, String> map = new TreeMap<String, String>();
		String accountId = "1120180104165923001";
		String contractId = "1120180104165923001";
		String cardNo = "6225881226093945";
		String name = "于海涛";
		String idCardNo = "231121198112245414";
		String phoneNo = "18622883268";
		String startDate = "20180108";
		String endDate = "20180108";
		String cycle = "3";
		String triesLimit = "1";
		String key = "123456abc";
		map.put("cardNo", "6225881226093945");
		map.put("name", "于海涛");
		map.put("idCardNo", "231121198112245414");
		map.put("phoneNo", "18622883268");
		map.put("startDate", "20180108");
		map.put("endDate", "20180108");
		map.put("cycle", "3");
		map.put("triesLimit", "1");
		map.put("accountId", "1120180104165923001");
		map.put("contractId", "1120180104165923001");
		// String paramSrc = "accountId=" + accountId + "&contractId=" + contractId +
		// "&name=" + name + "&phoneNo="
		// + phoneNo + "&cardNo=" + cardNo + "&idCardNo=" + idCardNo + "&startDate=" +
		// startDate + "&endDate="
		// + endDate + "&cycle=" + cycle + "&triesLimit=" + triesLimit + "&key=" + key;
		String paramSrc = "123456";
		System.out.println("签名前数据**********支付:" + paramSrc);

		String sign = MD5Util.MD5Encode(paramSrc).toUpperCase();
		map.put("mac", sign);
		
		String json=HttpUtil.toJson3(map);
		
		System.out.println(json);

		System.out.println(sign);

	}

}

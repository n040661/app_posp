package xdt.quickpay.hengfeng.util;

import xdt.quickpay.hengfeng.entity.PayQueryRequestEntity;
import xdt.service.impl.HFQuickPayServiceImpl;

public class TestQueryResult {

	public static void main(String[] args) throws Exception {
		
		String order="1801470133936374";//订单号
		
		PayQueryRequestEntity quetinfo=new PayQueryRequestEntity();
		
		quetinfo.setTransactionId(order);
		quetinfo.setMerId("105290054110501");
		
		HFQuickPayServiceImpl service=new HFQuickPayServiceImpl();
		service.queryPayResult(quetinfo);
		
		
	}
}

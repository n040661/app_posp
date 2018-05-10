package xdt.dto.hfb;

import xdt.dto.BaseUtil;

public class HfbUtil {
	
	//获取银行列表路径
	public static final String bankUrl="https://c.heepay.com/online/onlineBankList.do";
	//网关请求地址
	public static final String caedUrl="https://c.heepay.com/quick/pc/index.do";//https://c.heepay.com/monitor.jsp?m=test
	//微信支付宝请求路径
	public static final String WZUrl="https://open.heepay.com/aggrPay.do";
	//微信支付宝查询请求路径
	public static final String WZSelectUrl="https://open.heepay.com/aggrPayQuery.do";
	//网关key
	public static final String cardKey="e653ee8e6315122b5265255e7050748b";
	//产品交易码
	public static final String productCode="HY_B2CEBANKPC";
	//选择银行方式
	public static final String onlineType="hard";
	//版本号
	public static final String version="1.0";
	
	public static final String transferKey = "71fada5657a43a37e918055741a747cd";
	public static final String merchantId="100381";
	
	//异步通知地址
	public static final String notifyUrl=BaseUtil.url+"/HFBController/notifyUrl.action";
	//同步通知地址
	public static final String returnUrl=BaseUtil.url+"/HFBController/returnUrl.action";
	
	//代付异步通知
	public static final String notifyUrls=BaseUtil.url+"/HFBController/notifyUrls.action";
}

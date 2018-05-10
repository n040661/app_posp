package xdt.quickpay.cjt.comm;

import xdt.util.Global;

/**
 * @ClassName: Constant
 * @Description:畅捷快捷支付常量
 * @author 尚延超
 * @date 2016年10月25日
 *
 */
public class Constant {

	/**
	 * 畅捷普通快捷商户号
	 */
	public static final String MERCHANT_NO = Global.getConfig("cjmerchantno");
	/**
	 * 畅捷大额认证借记卡快捷商户号
	 */
	public static final String MERCHANT_NO1 = Global.getConfig("cjmerchantno1");
	/**
	 * 
	 *畅捷支付平台公钥
	 */
	public static final String MERCHANT_PUBLIC_KEY = Global.getConfig("cjmerchantpublickey");
	/**
	 * 畅捷商户普通快捷私钥
	 */
	public static final String MERCHANT_PRIVATE_KEY = Global.getConfig("cjmerchantkey");
	/**
	 * 畅捷商户大额认证快捷私钥
	 */
	public static final String MERCHANT_PRIVATE_KEY1 = Global.getConfig("cjmerchankey1");
	/**
	 * 交易地址
	 */
	public static final String SUBMIT_URL = Global.getConfig("cjsubmiturl");
	
	/**
	 * 查询交易结果地址
	 */
	public static final String QUERY_URL = Global.getConfig("cjqueryurl");
	
	/**
	 * 平台的交易商户 url
	 */
	public static final String XDT_PAGE_URL = Global.getConfig("xdtPageUrl");
	
	/**
	 * 平台的交易服务器url
	 */
	public static final String XDT_BG_URL = Global.getConfig("xdtBgUrl");

}

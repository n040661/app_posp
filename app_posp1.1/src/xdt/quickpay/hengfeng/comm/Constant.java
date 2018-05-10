package xdt.quickpay.hengfeng.comm;

import xdt.util.Global;

/**
 * @ClassName: Constant
 * @Description:恒丰快捷支付常量
 * @author LiShiwen
 * @date 2016年6月14日 上午9:58:28
 *
 */
public class Constant {

	/**
	 * 恒丰商户号
	 */
	public static final String MERCHANT_NO = Global.getConfig("hfmerchantno");

	/**
	 * 恒丰商户密钥
	 */
	public static final String MERCHANT_KEY = Global.getConfig("hfmerchantkey");

	
	/**
	 * 交易地址
	 */
	public static final String SUBMIT_URL = Global.getConfig("hfsubmiturl");
	
	/**
	 * 查询交易结果地址
	 */
	public static final String QUERY_URL = Global.getConfig("hfqueryurl");
	
	/**
	 * 平台的交易商户 url
	 */
	public static final String XDT_PAGE_URL = Global.getConfig("xdtPageUrl");
	
	/**
	 * 平台的交易服务器url
	 */
	public static final String XDT_BG_URL = Global.getConfig("xdtBgUrl");

}

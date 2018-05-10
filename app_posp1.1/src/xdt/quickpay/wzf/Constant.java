package xdt.quickpay.wzf;

import java.util.ArrayList;
import java.util.List;

import xdt.util.Global;

/**
 * @ClassName: Constant
 * @Description:沃支付常量
 * @author Shangyanchao
 * @date 2017年11月15日 上午9:58:28
 *
 */
public class Constant {

	/**
	 * 代扣签约
	 */
	public static final String SIN_URL = Global.getConfig("agreeSin_url");

	/**
	 * 单笔代扣申请
	 */
	public static final String WITHH_URL = Global.getConfig("SingleWithh_url");

	
	/**
	 * 代扣订单查询
	 */
	public static final String QUERY_URL = Global.getConfig("SingleWithhQuery_url");
	
	/**
	 * 代扣解约
	 */
	public static final String TERM_URL = Global.getConfig("agreenTerm_url");
	
	/**
	 * 退款
	 */
	public static final String REFUND_URL = Global.getConfig("SingleWithhRefund_url");
	
	/**
	 * 代扣签约一体化
	 */
	public static final String SINGLEPAY_URL = Global.getConfig("signAndSinglePay_url");
	
	/** 签名方式及字典集合 */
	public static final String SIGNTYPE_RSA_SHA256 ="RSA_SHA256";
	public static final String SIGNTYPE_SM2_SM3 ="SM2_SM3";
	
	public final static List<String> SIGN_TYPE_DICT = new ArrayList<String>(){
		private static final long serialVersionUID = -8399940394002018355L;	{
		add(SIGNTYPE_RSA_SHA256);add(SIGNTYPE_SM2_SM3);
	}};
	/** 字段集编码及字典集合 */
	public static final String CHARSET_UTF8 = "UTF-8";
	
	/** 一些签名验签过程中可能会过滤的字符串 */
	public static final String EXCEPT_SIGNMSG = "signMsg";
	public static final String EXCEPT_HMAC = "hmac";
	public static final String EXCEPT_CERT = "cert";

}

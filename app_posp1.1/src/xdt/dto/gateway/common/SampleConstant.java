package xdt.dto.gateway.common;

import xdt.dto.BaseUtil;

public abstract class SampleConstant {

	/**
	 * 首信易网关接口参数
	 */
	//标准支付接口
	public static final String STANDARD_URL = "https://pay.yizhifubj.com/prs/user_payment.checkit";
	
	// 直连支付接口
	public static final String DIRECT_URL = "https://pay.yizhifubj.com/customer/gb/pay_bank.jsp";
	
	// 会员支付接口
	public static final String MEMBER_URL = "https://pay.yizhifubj.com/customer/gb/pay_member.jsp";

	// 前台通知页面
	public static final String page_URL = BaseUtil.url+"/payeasy/pagePayResult.action";
	/**
	 * 江苏电商网关接口参数
	 */
	public static final String BACK_URLT0 = BaseUtil.url+"/hfquick/hfbgPayResultT0.action";
	public static final String BACK_URLT1 = BaseUtil.url+"/hfquick/hfbgPayResultT1.action";
	public static final String REMOTE_PATH = "http://unionpay.rytpay.com.cn/";
	public static final String COMMON_DOMAIN = "unionpay.rytpay.com.cn";
	public static final String GATEWAY_REMOTE_URL = "http://" + COMMON_DOMAIN + "/rytpay-business/v2/quick/pay.html";
	public static final String PUB_KEY1 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJ8pDjB1lJQY+9v6OIcSdTN5sxtwi9mQ1mnrEZ3HQAMo39Pdb/TeoSsPejpQrdhQkVBBZpM9U3fMkwBUDMV0toZ/9Nmq++vOlw21tsjhUPdQZODEnUyfVdSrZ7kInprmftj9bcJiM+Z8bcJnDXiTyXsDwzTJmE8JWLJ7eag53d8QIDAQAB";
	public static final String APP_ID1 = "4028e48c5c5c2988015c5c3491c40006";
	public static final String APP_CODE1 = "4028e48c5c5c2988015c5c3491c40007";
	
	public static final String PUB_KEY0="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMIyjwS9erQMFjYYjbAbYNse9OGTWN+inENSRB0h5TwtTAAciS2MEDadwWG4IOeGDv2Z/qRW5k/rktpIZoIGvZZ0kEjdSS50AEGI9Zv57qOB2amd5U31moLy3+pLMn8ylvDXcCK652No63nc/4qGb+S/+rxa+IOdbaIYaU+JuoSQIDAQAB";
	public static final String APP_ID0="4028e48c5c5c2988015c5c36bf040008";
	public static final String APP_CODE0="4028e48c5c5c2988015c5c36bf040009";
    
	public static final String UTF8 = "UTF-8";
	public static final String SUCCESS_CODE = "00";
	public static final String SUCCESS_MSG = "Success!";
    
    
    
    
}

package xdt.dto.tfb;

import xdt.dto.BaseUtil;

public class TFBConfig {

    // 接口域名，测试访问域名（正式环境改为：upay.tfb8.com）
    public static String hostName = "http://upay.tfb8.com";//"http://upay.tfb8.com/test";
    
    public static String hostNamePay="http://upay.tfb8.com";//"http://apitest.tfb8.com";
    //正是环境
    public static String hostUrl="http://api.tfb8.com";
    // MD5密钥，安全检验码 6c3641ad6013f3fe0c2195f74690a326
    public static String key = "12345";

    // 服务器数据编码类型
    public static String serverEncodeType = "UTF-8";

    // 商户/平台在国采注册的账号。国采维度唯一，固定长度10位 1800559974
    public static String spid = "1800776625";

    // 订单金额的类型。1 – 人民币(单位: 分)
    public static String cur_type = "CNY";

    // 商户发起支付请求的IP
    public static String spbill_create_ip = "192.168.11.96";

    // 设备id
    public static String sp_udid = "";

    // WX支付申请接口名
    public static String payApplyApi = hostNamePay + "/cgi-bin/v2.0/api_wx_pay_apply.cgi";
    
    // 网关支付申请接口名
    public static String cardPayApplyApi =hostUrl+"/cgi-bin/v2.0/api_cardpay_apply.cgi";
    // 公众号支付申请接口
    public static String subpayApplyApi = hostName + "/cgi-bin/v2.0/api_wx_subpay_apply.cgi";
    
    // 订单关闭接口名
    public static String payCancelApi = hostName + "/cgi-bin/v2.0/api_wx_pay_cancel.cgi";

    // 单笔微信支付单查询接口名
    public static String paySingleQueryApi = hostName + "/cgi-bin/v2.0/api_wx_pay_single_qry.cgi";

    // 批量支付单查询接口名
    public static String payBatchQueryApi = hostName + "/cgi-bin/v2.0/api_wx_pay_batch_qry.cgi";

    
    //单笔代付查询地址
    public static String payQueryApi=hostUrl + "/cgi-bin/v2.0/api_pay_single_query.cgi";
    //单笔网关查询
    public static String cardPayQueryApi=hostUrl + "/cgi-bin/v2.0/api_single_qry_order.cgi";
    
    // 退款接口名
    public static String refundApi = hostName + "/cgi-bin/v2.0/api_wx_refund.cgi";

    // 单笔退款单查询接口名
    public static String refundSingleQueryApi = hostName + "/cgi-bin/v2.0/api_wx_refund_single_qry.cgi";

    // 批量退款单查询接口名
    public static String refundBatchQueryApi = hostName + "/cgi-bin/v2.0/api_wx_refund_batch_qry.cgi";

    // 下载账单接口名
    public static String downloadBillApi = hostName + "/cgi-bin/v2.0/api_wx_pay_downloadbill.cgi";
    
    //异步返回接口
    public static String notifyUrl = BaseUtil.url+"/TFBController/notifyUrl.action";
    //同步返回接口
    public static String returnUrl = BaseUtil.url+"/TFBController/returnUrl.action";
    //代付
    public static String payUrl= hostUrl+ "/cgi-bin/v2.0/api_pay_single.cgi";
    
    
    public static final String GC_PUBLIC_KEY_PATH ="/gczf_rsa_public.pem";     //国采提供的公钥key
    

	//public static final String PUBLIC_KEY_PATH = "F:/RSAKEY/rsa_public_key_68.pem";

	public static final String PRIVATE_KEY_PATH = "/pkcs8_rsa_private_key.pem";//"/dbli_pkcs8_rsa_private_key.pem";    //这里要换成自已的私钥key
	public static final String PRIVATE_KEY_PATH1 = "/dbli_pkcs8_rsa_private_key1.pem";    //这里要换成自已的私钥key
	
}

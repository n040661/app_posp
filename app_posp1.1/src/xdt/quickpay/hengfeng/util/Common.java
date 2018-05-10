/**
 * 
 */
package xdt.quickpay.hengfeng.util;

/**
 * @author zj
 * 
 * @date:2016年4月8日上午9:50:36
 */
public class Common
{

    public static final String CHARSET = "UTF-8";

    //测试URL
//    public static final String URL = "http://116.228.235.114:8087/External_Connect/tradition/WeChatpayment_mobile.action";
    //线上URL
    public static final String URL = "http://116.228.235.114:8083/External_Connect/tradition/WeChatpayment_mobile.action";
    
    // 商户标识(根据分配的用户标识进行修改)
    public static final String userid = "2355";
    
    public static final String key = "74b87337454200d4d33f80c4663dc5e5";
    
    /**交易类型---------BEGIN*/
    public static final String regesitor = "tb_Regesitor";//注册
    
    public static final String downLoadKeys = "tb_DownLoadKey";//下载
    
    public static final String verify = "tb_verifyInfo";//微信卡认证
    
    public static final String weiXinPay = "tb_WeixinPay";//微信支付
    
    public static final String orderConfirm = "tb_OrderConfirm";//订单状态查询
    
    public static final String changeRate = "xy_ChangeRate";//同步商户签约费率
    
    public static final String alipayVerify = "tb_alipayVerify";//支付宝卡认证
    
    public static final String aliPay = "tb_alipay";//支付宝支付
    /** 交易类型---------END*/
    
    
}

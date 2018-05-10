package xdt.util;

/**
 * 常量类 
 */
public class Constants {
	
	/**
	 * 订单初始化状态
	 */
	public static final String ORDERINITSTATUS = "200";
	
	
	//-------------讯联常量start----------------
	
	/**
	 * 讯联商户号
	 */
	public static final String  XL_SP_NO= "";
	/**
	 * 讯联调用接口http 商户收款使用
	 */
	public static final String  XL_PAY_HTTP= "http://localhost:9090/Ky_System/xLWechatAndAlipayAction/wechatAndAlipay.action";
	
	
	
	//-------------讯联常量end----------------
	
	
	
	//-------------移动和包常量start----------------
	
	
	/**
	 *  编码格式   00--GBK;01--GB2312;02--UTF-8   可以为空默认00--GBK
	 */
	public static final String  CHARACTERSET= "00";
	/**
	 *  只能是MD5，RSA
	 */
	public static final String SIGNTYPE = "MD5";					 
	/**
	 *  条码支付版本号2.0.0
	 */
	public static final String VERSION = "2.0.0";
	/**
	 *  移动和包订单查询版本号2.0.1
	 */
	public static final String QUERYVERSION = "2.0.1";
	/**
	 *    00 CNY-现金     ;  01  CMY-充值卡默认为：00
	 */
	public static final String CURRENCY="00";           
	
	/**
	 * 有效期数量         数字（云支付订单有效期不能大于30分钟）
	 */
	public static final String PERIOD="20";             
	/**
	 * 有效期单位  只能取以下枚举值   00-分
	 */
	public static final  String PERIODUNIT="00";		  
	
	//-------------移动和包常量end----------------
	
	
	
	
	
	
	
	
	//-------------百度钱包常量start----------------
	
	
	/**
	 * 百度接口调用  后通知http
	 */
	//public static final String BD_RETURN_HTTP = "http://xdt20150506.xicp.net:54623";
	
	/**
	 * 百度接口调用  create 生成二维码   http
	 */
	//public static final String BD_CREATE_HTTP = "https://www.baifubao.com/o2o/0/code/0/create/0";

	/**
	 * 百度接口调用   pay   付款   http
	 */
	//public static final String BD_PAY_HTTP = "https://www.baifubao.com/o2o/0/b2c/0/api/0/pay/0";
	
	/**
	 * 百度接口调用  订单查询  query   http
	 */
	//public static final String BD_QUERY_HTTP = "https://www.baifubao.com/api/0/query/0/pay_result_by_order_no";
	
	/**
	 *  码类型(不参与签名)   整数，目前必须为0
	 */
	public static final String  BD_CODE_TYPE = "0";

	/**
	 *  输出格式(不参与签名)   0：image；1：json；默认值：0
	 */
	public static final String   BD_OUTPUT_TYPE = "1";

	/**
	 * 	订单查询 服务编号	整数，目前必须为11
	 */
	public static final String   BD_SERVICE_QUERY_CODE = "11";
	
	/**
	 * 	服务编号	整数，目前必须为1   表示即时到帐支付
	 */
	public static final String   BD_SERVICE_CODE = "1";

	/**
	 * 	百付宝商户号	10位数字组成的字符串
	 */
	//public static final String   BD_SP_NO = "1000046133";//"9000100005";//

	/**
	 * 百付宝合作密钥
	 */
	//public static final String  BD_WORK_KEY  = "7Yn7KwUqpnzCDxZKvEG3TvsZEesBBb3H";//"pSAw3bzfMKYAXML53dgQ3R4LsKp758Ss";//

	/**
	 * 	币种，1	支付的币种是人民币
	 */
	public static final String  BD_CURRENCY  = "1";
	
	/**
	 * 	请求参数的字符编码	1	GBK
	 */
	public static final String  BD_INPUT_CHARSET  = "1";
	
	/**
	 * 	接口的版本号	必须为2
	 */
	public static final String  BD_VERSION  = "2";
	
	/**
	 * 	签名方法	    1	签名算法为MD5    2	SHA-1
	 */
	public static final String  BD_SIGN_METHOD  = "1";
	
	/**
	 *  支付结果      1	等待支付2	支付成功3	交易成功
	 */
	public static final String  BD_RESULT_FLAG_1  = "1";
	
	/**
	 *  支付结果      1	等待支付2	支付成功3	交易成功
	 */
	public static final String  BD_RESULT_FLAG_2  = "2";
	
	/**
	 *  支付结果      1	等待支付2	支付成功3	交易成功
	 */
	public static final String  BD_RESULT_FLAG_3  = "3";
	/**
	 *  默认支付方式 
	 *  1	余额支付（必须登录百度钱包）
	 *  2	网银支付（在百度钱包页面上选择银行，可以不登录百度钱包）
	 *  3	银行网关支付（直接跳到银行的支付页面，无需登录百度钱包）
	 */
	public static final String  BD_PAY_TYPE  = "2";
	
	/**
	 * 响应数据的格式，默认XML   1
	 */
	public static final String  BD_ORDER_OUTPUT_TYPE  = "1";

  //  public static final String  BD_CALLBACK_URL ="http://220.170.79.210:12530/appposp/baiDuAction/merchantCollectMoneyAction.action";

	//-------------百度钱包常量end----------------

	
	
	
	
	
	
	//-------------车行易start----------------
	/**
	 *  第三方标识
	 *  车行易为合作方分配的标识码
	 */
	public static final String  USER_FROM  = "user_from";
	
	/**
	 *  第三方用户ID
	 */
	public static final String  USER_ID  = "user_id";
	
	/**
	 *  加密字段
	 */
	public static final String  TOKEN  = "token";
	
	//-------------车行易end----------------

	//-------------表名start----------------
	/**
	 * 主键表
	 */
	public static final String  PMS_ADDRESS  = "PMS_ADDRESS";
	
	//-------------表名end----------------

}

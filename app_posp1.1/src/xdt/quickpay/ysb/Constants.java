package xdt.quickpay.ysb;

import java.util.ResourceBundle;

public class Constants {
	private static ResourceBundle MESSAGES_PROPS = ResourceBundle.getBundle("messages");
	//1.1实时代付请求地址
	public static final String DC_API_PAY_URL = MESSAGES_PROPS.getString("dc_api_pay_url");
	//1.1响应地址
	public static final String RESPONSE_URL=MESSAGES_PROPS.getString("response_Url");
	//1.2 订单状态查询接口
	public static final String QUERY_ORDER_STATUS=MESSAGES_PROPS.getString("query_Order_Status");
	//1.4商户账户余额及保证金余额查询接口
	public static final String QUERY_BLANCE=MESSAGES_PROPS.getString("query_Blance");
	//1.1 子协议录入请求地址
	public static final String SIGNSIMPLESUBCONTRACT_URL = MESSAGES_PROPS.getString("signsimplesubcontract_url");
	//1.2委托代扣接口请求地址
	public static final String COLLECT_URL=MESSAGES_PROPS.getString("collect_url");
	//1.2委托代扣接口响应地址
	public static final String COLLECT_RESPONSE_URL=MESSAGES_PROPS.getString("collect_response_url");
	//1.3订单状态查询接口URL
	public static final String QUERYORDERSTATUS_URL=MESSAGES_PROPS.getString("queryorderstatus_url");
	//1.4子协议号查询接口
	public static final String QUERYSUBCONTRACTID_URL=MESSAGES_PROPS.getString("querysubcontractid_url");
	//1.5子协议延期接口
	public static final String SUBCONSTRACTEXTENSION_URL=MESSAGES_PROPS.getString("subconstractextension_url");
}

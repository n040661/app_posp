package xdt.dto.nbs.alipay;

import xdt.dto.nbs.base.WechatResponseBase;

public class AlipayParamResponse extends WechatResponseBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String return_code;//返回状态码
	private String return_msg;//返回信息
	private String result_code;//业务结果
	private String mch_id;//商户号
	private String device_info;//设备号
	private String err_code;//错误代码
	private String err_code_des;//错误代码描述
	private String nonce_str;//随机字符串
	private String code_url;//二维码链接
	private String sign;//签名
	private String merchantId;//下游商户号
	private String trade_state;//订单状态
	private String out_trade_no;//商户订单号
	private String total_fee;//订单金额
	private String alipay_transaction_id;//支付宝订单号
	private String transaction_id;//农商行订单号
	private String buyer_logon_id;//买家支付宝账号
	private String store_name;//商户门店名称
	private String time_end;//支付完成时间
	private String brcbGatewayUrl;//上游地址
	private String querystring;
	
	
	
	public String getTrade_state() {
		return trade_state;
	}
	public void setTrade_state(String trade_state) {
		this.trade_state = trade_state;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getAlipay_transaction_id() {
		return alipay_transaction_id;
	}
	public void setAlipay_transaction_id(String alipay_transaction_id) {
		this.alipay_transaction_id = alipay_transaction_id;
	}
	public String getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}
	public String getBuyer_logon_id() {
		return buyer_logon_id;
	}
	public void setBuyer_logon_id(String buyer_logon_id) {
		this.buyer_logon_id = buyer_logon_id;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public String getTime_end() {
		return time_end;
	}
	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
	public String getReturn_msg() {
		return return_msg;
	}
	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}
	public String getResult_code() {
		return result_code;
	}
	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	public String getDevice_info() {
		return device_info;
	}
	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}
	public String getErr_code() {
		return err_code;
	}
	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}
	public String getErr_code_des() {
		return err_code_des;
	}
	public void setErr_code_des(String err_code_des) {
		this.err_code_des = err_code_des;
	}
	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	public String getCode_url() {
		return code_url;
	}
	public void setCode_url(String code_url) {
		this.code_url = code_url;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getBrcbGatewayUrl() {
		return brcbGatewayUrl;
	}
	public void setBrcbGatewayUrl(String brcbGatewayUrl) {
		this.brcbGatewayUrl = brcbGatewayUrl;
	}
	public String getQuerystring() {
		return querystring;
	}
	public void setQuerystring(String querystring) {
		this.querystring = querystring;
	}

	
}

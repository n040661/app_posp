package xdt.dto.hm;

public class HMResponse {

	private String ordernumber;//商户订单号
	private String payorderno;////支付订单号
	private String resptime;//响应时间
	private String orderstate;//订单状态
	private String payinfo;//返回前台跳转经过加密的 URL 
	private String ret;//请求状态码
	private String merchantid;//商户号
	private String merchantId;//商户号
	private String timestamp;//
	private String data;//
	private String message;//状态吗
	private String respCode;//
	private String respMsg;//
	private String amount;
	private String sign;//签名
	
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getRet() {
		return ret;
	}
	public void setRet(String ret) {
		this.ret = ret;
	}
	public String getMerchantid() {
		return merchantid;
	}
	public void setMerchantid(String merchantid) {
		this.merchantid = merchantid;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getOrdernumber() {
		return ordernumber;
	}
	public void setOrdernumber(String ordernumber) {
		this.ordernumber = ordernumber;
	}
	public String getPayorderno() {
		return payorderno;
	}
	public void setPayorderno(String payorderno) {
		this.payorderno = payorderno;
	}
	public String getResptime() {
		return resptime;
	}
	public void setResptime(String resptime) {
		this.resptime = resptime;
	}
	public String getOrderstate() {
		return orderstate;
	}
	public void setOrderstate(String orderstate) {
		this.orderstate = orderstate;
	}
	public String getPayinfo() {
		return payinfo;
	}
	public void setPayinfo(String payinfo) {
		this.payinfo = payinfo;
	}
	@Override
	public String toString() {
		return "HMResponse [ordernumber=" + ordernumber + ", payorderno="
				+ payorderno + ", resptime=" + resptime + ", orderstate="
				+ orderstate + ", payinfo=" + payinfo + "]";
	}
	
	
}

package xdt.dto.kkx;

public class KKXRequest {

	private String order_id;//订单号
	private String sign;//
	private String account;//商户号
	private String nonce_str;//随即字符串
	private String amount;//金额0.00
	private String body;//商品描述
	private String pay_method;//01-微信，02-支付宝，03-QQ钱包，04-网关支付
	private String return_url;//支付成功后跳转地址（04时必填，其他不填）
	private String bankCode;//银行编码（04时必填，其他不填）
	private String notifyurl;//回调地址
	private String  url;
	private String  reUrl;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getReUrl() {
		return reUrl;
	}
	public void setReUrl(String reUrl) {
		this.reUrl = reUrl;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getPay_method() {
		return pay_method;
	}
	public void setPay_method(String pay_method) {
		this.pay_method = pay_method;
	}
	public String getReturn_url() {
		return return_url;
	}
	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getNotifyurl() {
		return notifyurl;
	}
	public void setNotifyurl(String notifyurl) {
		this.notifyurl = notifyurl;
	}
	@Override
	public String toString() {
		return "KKXRequest [order_id=" + order_id + ", sign=" + sign
				+ ", account=" + account + ", nonce_str=" + nonce_str
				+ ", amount=" + amount + ", body=" + body + ", pay_method="
				+ pay_method + ", return_url=" + return_url + ", bankCode="
				+ bankCode + ", notifyurl=" + notifyurl + ", url=" + url
				+ ", reUrl=" + reUrl + "]";
	}
	
}

package xdt.dto.kkx;

public class KKXResponse {

	private String code;//code
	private String msg;//msg
	private String nonce_str;//返回说明
	private String order_id;//随机字符串
	private String amount;//金额
	private String status;//1：支付成功，0：未支付
	private String time;//支付时间
	private String sign;//
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "KKXResponse [code=" + code + ", msg=" + msg + ", nonce_str="
				+ nonce_str + ", order_id=" + order_id + ", amount=" + amount
				+ ", status=" + status + ", time=" + time + ", sign=" + sign
				+ "]";
	}
	
}

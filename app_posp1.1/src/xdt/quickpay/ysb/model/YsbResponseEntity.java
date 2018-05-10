package xdt.quickpay.ysb.model;

public class YsbResponseEntity {

	private String accountId;
	private String result_code;
	private String result_msg;
	private String amount;
	private String orderId;
	private String key;
	private String mac;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getResult_msg() {
		return result_msg;
	}

	public void setResult_msg(String result_msg) {
		this.result_msg = result_msg;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String toString() {
		return "YsbResponseEntity [accountId=" + accountId + ", result_code=" + result_code + ", result_msg="
				+ result_msg + ", amount=" + amount + ", orderId=" + orderId + ", key=" + key + ", mac=" + mac + "]";
	}

}

package xdt.quickpay.conformityQucikPay.entity;

public class ConformityQuickPayQueryResponseEntity {
	
	private String v_mid; //商户号
	
	private String v_userId; //用户ID
	
	private String v_oid; //订单号
	
	private String v_txnAmt; //交易金额
	
	private String v_time; //交易时间
	
	private String v_code;//响应码
	
	private String v_msg; //响应信息
	
	private String v_payStatus; //交易码
	
	private String v_paymsg; //交易描述
	
	private String v_attach;//数据包
	
	private String v_sign; //签名

	public String getV_mid() {
		return v_mid;
	}

	public void setV_mid(String v_mid) {
		this.v_mid = v_mid;
	}

	public String getV_userId() {
		return v_userId;
	}

	public void setV_userId(String v_userId) {
		this.v_userId = v_userId;
	}

	public String getV_oid() {
		return v_oid;
	}

	public void setV_oid(String v_oid) {
		this.v_oid = v_oid;
	}

	public String getV_txnAmt() {
		return v_txnAmt;
	}

	public void setV_txnAmt(String v_txnAmt) {
		this.v_txnAmt = v_txnAmt;
	}

	public String getV_time() {
		return v_time;
	}

	public void setV_time(String v_time) {
		this.v_time = v_time;
	}

	public String getV_code() {
		return v_code;
	}

	public void setV_code(String v_code) {
		this.v_code = v_code;
	}

	public String getV_msg() {
		return v_msg;
	}

	public void setV_msg(String v_msg) {
		this.v_msg = v_msg;
	}

	public String getV_payStatus() {
		return v_payStatus;
	}

	public void setV_payStatus(String v_payStatus) {
		this.v_payStatus = v_payStatus;
	}

	public String getV_paymsg() {
		return v_paymsg;
	}

	public void setV_paymsg(String v_paymsg) {
		this.v_paymsg = v_paymsg;
	}

	public String getV_attach() {
		return v_attach;
	}

	public void setV_attach(String v_attach) {
		this.v_attach = v_attach;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}	

}

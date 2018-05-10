package xdt.dto.gateway.entity;

public class GatrWayGefundEntity {

private String v_version; //版本号
	
	private String v_mid; //商户号
	
	private String v_oid; //订单号
	
	private String v_txnAmt;//退款金额
	
	private String v_time;//商户提交时间 
	
	private String v_notify_url; //回调地址
	
	private String v_merchantId; //商户代码
	
	private String v_orgBpSerialNum; //原订单号
	
	private String v_orgTransTime;//原订单时间
	
	private String v_sign; //签名

	public String getV_version() {
		return v_version;
	}

	public void setV_version(String v_version) {
		this.v_version = v_version;
	}

	public String getV_mid() {
		return v_mid;
	}

	public void setV_mid(String v_mid) {
		this.v_mid = v_mid;
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

	public String getV_notify_url() {
		return v_notify_url;
	}

	public void setV_notify_url(String v_notify_url) {
		this.v_notify_url = v_notify_url;
	}

	public String getV_merchantId() {
		return v_merchantId;
	}

	public void setV_merchantId(String v_merchantId) {
		this.v_merchantId = v_merchantId;
	}

	public String getV_orgBpSerialNum() {
		return v_orgBpSerialNum;
	}

	public void setV_orgBpSerialNum(String v_orgBpSerialNum) {
		this.v_orgBpSerialNum = v_orgBpSerialNum;
	}

	public String getV_orgTransTime() {
		return v_orgTransTime;
	}

	public void setV_orgTransTime(String v_orgTransTime) {
		this.v_orgTransTime = v_orgTransTime;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}

	@Override
	public String toString() {
		return "GatrWayGefundEntity [v_version=" + v_version + ", v_mid=" + v_mid + ", v_oid=" + v_oid + ", v_txnAmt="
				+ v_txnAmt + ", v_time=" + v_time + ", v_notify_url=" + v_notify_url + ", v_merchantId=" + v_merchantId
				+ ", v_orgBpSerialNum=" + v_orgBpSerialNum + ", v_orgTransTime=" + v_orgTransTime + ", v_sign=" + v_sign
				+ "]";
	}

	
	
}

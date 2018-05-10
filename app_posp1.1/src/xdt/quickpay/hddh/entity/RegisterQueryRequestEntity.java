package xdt.quickpay.hddh.entity;

public class RegisterQueryRequestEntity {
	
	private String merid;
	
	private String cooperatorUserId;
	
	private String callBackUrl;
	
	private String cooperatorOrderId;
	
	private String v_sign;

	public String getMerid() {
		return merid;
	}

	public void setMerid(String merid) {
		this.merid = merid;
	}

	public String getCooperatorUserId() {
		return cooperatorUserId;
	}

	public void setCooperatorUserId(String cooperatorUserId) {
		this.cooperatorUserId = cooperatorUserId;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getCooperatorOrderId() {
		return cooperatorOrderId;
	}

	public void setCooperatorOrderId(String cooperatorOrderId) {
		this.cooperatorOrderId = cooperatorOrderId;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}
	
	

}

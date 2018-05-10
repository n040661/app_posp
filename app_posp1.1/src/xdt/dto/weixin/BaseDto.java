package xdt.dto.weixin;

import java.io.Serializable;

public class BaseDto implements Serializable {
	
	/** @Fields serialVersionUID: */
	  	
	private static final long serialVersionUID = 1L;
	
	private String merchId;//商户号
	
	private String orderCode;//支付宝
	
	private String account;
	
	private String password;
	
	

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getMerchId() {
		return merchId;
	}

	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}

	@Override
	public String toString() {
		return "BaseDto [merchId=" + merchId + "]";
	}
	
}

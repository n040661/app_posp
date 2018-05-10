package xdt.dto;

/**
 * 商户解绑已绑定的设备请求
 * 
 * @author lev12
 * 
 */
public class MerchantsUnBindingPosRequestDTO {
	private String mobilePhone;// 手机号

	private String posId;// 设备序列

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

}

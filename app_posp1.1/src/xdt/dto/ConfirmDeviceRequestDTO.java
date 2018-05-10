package xdt.dto;

/**
 * 确认设备认证请求
 * 
 * @author lev12
 */
public class ConfirmDeviceRequestDTO {

	private String mobilePhone;// 手机号

	private String sn;// sn号

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

}
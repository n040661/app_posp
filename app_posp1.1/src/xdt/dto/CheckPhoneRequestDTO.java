package xdt.dto;

/**
 * 验证手机号请求
 * 
 * @author lev12
 * 
 */
public class CheckPhoneRequestDTO {

	private String mobilePhone;// 手机号
	private String validCode;// 验证码
	private String flag;// 验证码标识

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getValidCode() {
		return validCode;
	}

	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}

package xdt.dto;

/**
 * 修改密码验证确认接口请求
 * 
 * @author lev12
 * 
 */
public class ChangePasswordValidationConfirmRequestDTO {

	private String mobilePhone; // 手机号

	private String oldPassword; // 旧密码

	private String newPassword; // 新密码

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}

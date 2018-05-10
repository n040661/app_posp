package xdt.dto;

/**
 * 找回密码验证确认接口请求
 * 
 * @author lev12
 * 
 */
public class RetrievePasswordValidationConfirmRequestDTO {

	private String validCode; // 短信验证码

	private String mobilePhone; // 手机号

	private String identityCard; // 身份证号

	private String oAgentNo; // o单编号

	public String getValidCode() {
		return validCode;
	}

	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getIdentityCard() {
		return identityCard;
	}

	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

}

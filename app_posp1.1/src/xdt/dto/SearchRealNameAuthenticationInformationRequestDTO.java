package xdt.dto;
/**
 * 实名认证信息查询请求接口
 * @author lev12
 *
 */
public class SearchRealNameAuthenticationInformationRequestDTO {
	private String mobilePhone; //手机号

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

}

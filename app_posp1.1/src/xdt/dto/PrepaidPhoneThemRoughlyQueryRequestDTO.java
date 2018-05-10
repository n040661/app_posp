package xdt.dto;
/**
 * 手机充值号段查询请求接口
 * @author xiaomei
 *
 */
public class PrepaidPhoneThemRoughlyQueryRequestDTO {
	
	public String mobilePhone; //充值手机号

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
}

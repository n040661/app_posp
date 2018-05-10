package xdt.dto;
/**
 * 手机获取验证码接口请求
 * @author xiaomei
 *
 */
public class CaptchaValidationRequestDTO {
	
	private String mobilePhone; //手机号码
	
	private Integer mark; //0 注册  1 找回密码 2 登录后的操作

    private String oAgentNo;//欧单编号
	
	public Integer getMark() {
		return mark;
	}
	public void setMark(Integer mark) {
		this.mark = mark;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }
}

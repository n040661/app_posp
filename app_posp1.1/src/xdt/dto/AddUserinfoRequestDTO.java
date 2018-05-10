package xdt.dto;

/**
 * 账号请求
 * 
 * @author lev12
 * 
 */
public class AddUserinfoRequestDTO {

	private String loginName;// 账号
	private String loginPwd;// 密码
	private String trueName;// 姓名

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

}

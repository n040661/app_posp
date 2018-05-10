package xdt.dto;

/**
 * 商户登录接口请求
 * 
 * @author lev12
 */
public class MerchantLoginRequestDTO {

	private String userName; // 登录名

	private String passWord; // 登录密码
	
	private String oAgentNo;// o单编号

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

}

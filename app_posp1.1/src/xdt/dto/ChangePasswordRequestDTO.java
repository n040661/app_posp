package xdt.dto;

/**
 * 修改密码接口请求
 * 
 * @author lev12
 * 
 */
public class ChangePasswordRequestDTO {

	private String mobilePhone; // 手机号码

	private String newPassWord; // 新密码

    private String oAgentNo; // o单编号

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getNewPassWord() {
		return newPassWord;
	}

	public void setNewPassWord(String newPassWord) {
		this.newPassWord = newPassWord;
	}

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }
}

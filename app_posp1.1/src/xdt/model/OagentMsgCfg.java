package xdt.model;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-10-21
 * Time: 下午4:56
 * To change this template use File | Settings | File Templates.
 */
public class OagentMsgCfg {
    String oAgentNo;//欧单编号
    String account;//账号
    String pswd;//密码
    String describe;//描述

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
    
}

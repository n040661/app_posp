package xdt.dto;

/**
 * 验证手机号响应
 * 
 * @author lev12
 * 
 */
public class CheckPhoneResponseDTO {

	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

	private String loginName;// 账号

	public Integer getRetCode() {
		return retCode;
	}

	public void setRetCode(Integer retCode) {
		this.retCode = retCode;
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

}

package xdt.dto;

/**
 * 账号响应
 * 
 * @author lev12
 * 
 */
public class AddUserinfoResponseDTO {

	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

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

}
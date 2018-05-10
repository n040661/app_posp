package xdt.dto;

/**
 * 确认设备认证响应
 * 
 * @author lev12
 */
public class ConfirmDeviceResponseDTO {

	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

	private String status;// 认证状态 1为成功 2为失败

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}

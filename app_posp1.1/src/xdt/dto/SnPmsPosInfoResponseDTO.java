package xdt.dto;

/**
 * 商户绑定pos返回
 * 
 */
public class SnPmsPosInfoResponseDTO {
	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

	private String deviceStatus;// 设备状态

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

	public String getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(String deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

}

package xdt.dto;

/**
 * 商户解绑已绑定的设备响应
 * 
 * @author lev12
 * 
 */
public class MerchantsUnBindingPosResponseDTO {
	private Integer retCode;// 是否成功

	private String retMessage;// 信息描述

	private String bindStatus;// 绑定状态,0为未绑定；1为绑定成功；2为已解绑

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

	public String getBindStatus() {
		return bindStatus;
	}

	public void setBindStatus(String bindStatus) {
		this.bindStatus = bindStatus;
	}

}

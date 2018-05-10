package xdt.dto;

import java.util.List;

/**
 * 商户查询绑定的所有的pos
 * 
 * @author lev12
 * 
 */
public class MerchantsTheBindingPosResponseDTO {
	private Integer retCode;// 是否成功

	private String retMessage;// 信息描述

	private List deviceList;// 设备列表

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

	public List getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(List deviceList) {
		this.deviceList = deviceList;
	}

}

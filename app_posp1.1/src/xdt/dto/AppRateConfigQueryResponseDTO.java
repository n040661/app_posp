package xdt.dto;

import java.util.List;

import xdt.model.AppRateConfig;

/**
 * 查询费率响应
 * 
 * @author lev12
 * 
 */
public class AppRateConfigQueryResponseDTO {

	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

	private List<AppRateConfig> list;// 费率列表

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

	public List<AppRateConfig> getList() {
		return list;
	}

	public void setList(List<AppRateConfig> list) {
		this.list = list;
	}

}

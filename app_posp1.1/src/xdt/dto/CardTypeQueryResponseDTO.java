package xdt.dto;

import java.util.List;

import xdt.model.PmsDictionary;

/**
 * 查询证件类型响应
 * 
 * @author lev12
 * 
 */
public class CardTypeQueryResponseDTO {

	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

	private List<PmsDictionary> list;// 证件类型列表

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

	public List<PmsDictionary> getList() {
		return list;
	}

	public void setList(List<PmsDictionary> list) {
		this.list = list;
	}

}

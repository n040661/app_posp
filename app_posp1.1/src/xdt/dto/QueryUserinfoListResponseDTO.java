package xdt.dto;

import java.util.List;

import xdt.model.Userinfo;

/**
 * 我的收银员响应
 * 
 * @author lev12
 */
public class QueryUserinfoListResponseDTO {

	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

	private List<Userinfo> userinfoList;// 收银员列表

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

	public List<Userinfo> getUserinfoList() {
		return userinfoList;
	}

	public void setUserinfoList(List<Userinfo> userinfoList) {
		this.userinfoList = userinfoList;
	}

}

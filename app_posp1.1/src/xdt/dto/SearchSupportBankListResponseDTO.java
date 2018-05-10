package xdt.dto;

import java.util.List;

import xdt.model.PmsSupportBankInfo;



/**
 * 银行列表响应接口
 * @author lev12
 *
 */
public class SearchSupportBankListResponseDTO {
	
	private List<PmsSupportBankInfo> list; //银行列表
	
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

	public List<PmsSupportBankInfo> getList() {
		return list;
	}

	public void setList(List<PmsSupportBankInfo> list) {
		this.list = list;
	}

}

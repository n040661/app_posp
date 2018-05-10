package xdt.dto;

import xdt.model.PmsTransHistoryRecord;

import java.util.List;

/**
 * 银行卡历史记录列表响应接口
 * @author xiaomei
 */
public class BankCardHistoryRecordListResponseDTO {
	
	private Integer retCode;// 信息编号
		
	private String retMessage;// 信息描述
		
	private List<PmsTransHistoryRecord> list; //银行卡列表信息

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

	public List<PmsTransHistoryRecord> getList() {
		return list;
	}

	public void setList(List<PmsTransHistoryRecord> list) {
		this.list = list;
	}

}

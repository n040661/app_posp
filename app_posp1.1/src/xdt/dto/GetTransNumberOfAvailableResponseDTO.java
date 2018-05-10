package xdt.dto;
/**
 *  获取交易可用次数响应接口
 * @author Jeff
 */
public class GetTransNumberOfAvailableResponseDTO {
	
	private String count; //可用次数
	
	private String supperTransMoney; //交易金额（超级）
	
	private String commonTransMoney; //交易金额（普通）
	
	private Integer retCode;// 信息编号
	
	private String retMessage;// 信息描述

	public String getSupperTransMoney() {
		return supperTransMoney;
	}

	public void setSupperTransMoney(String supperTransMoney) {
		this.supperTransMoney = supperTransMoney;
	}

	public String getCommonTransMoney() {
		return commonTransMoney;
	}

	public void setCommonTransMoney(String commonTransMoney) {
		this.commonTransMoney = commonTransMoney;
	}

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

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
}

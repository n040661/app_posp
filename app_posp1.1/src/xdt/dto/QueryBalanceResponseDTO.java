package xdt.dto;

import xdt.model.Balance;

/**
 * 水电煤账户欠费查询接口响应
 * 
 * @author lev12
 * 
 */
public class QueryBalanceResponseDTO {

	private Integer retCode;// 操作返回代码，1成功,err_msg为空，其它数字具体错误在err_msg返回

	private String retMessage;// 错误描述，如请求得到正确返回，此处将为空

	private Balance balance;// 账户欠费信息

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

	public Balance getBalance() {
		return balance;
	}

	public void setBalance(Balance balance) {
		this.balance = balance;
	}

}

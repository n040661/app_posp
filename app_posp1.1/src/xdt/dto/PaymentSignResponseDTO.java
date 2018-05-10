package xdt.dto;
/**
 * 支付签到 状态修改 响应   
 * @author p
 *
 */
public class PaymentSignResponseDTO {

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
	
}

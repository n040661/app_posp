package xdt.dto;
/**
 * 获取交易可用次数请求接口
 * @author Jeff
 */
public class GetTransNumberOfAvailableRequestDTO {
	
	private String bankCardNumber; //银行卡号
	
	private Integer businessCode; //业务码  1:信用卡 5:提款
	
	public Integer getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(Integer businessCode) {
		this.businessCode = businessCode;
	}

	public String getBankCardNumber() {
		return bankCardNumber;
	}

	public void setBankCardNumber(String bankCardNumber) {
		this.bankCardNumber = bankCardNumber;
	}
}

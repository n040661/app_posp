package xdt.dto;
/**
 * 添加信用卡请求接口
 * @author xiaomei
 *
 */
public class AddCreditCardResponseDTO {
    
    private Integer retCode;// 信息编号
	
	private String retMessage;// 信息描述
	
	private String cardHolderName; //持卡人姓名
	
	private String bankName;  //银行名称
	
	private String bankCardNumber; //银行卡号
	
	private String shortBankCardNumber; //银行卡简称
	
	private Integer count; //可用次数
	
	public String getBankCardNumber() {
		return bankCardNumber;
	}

	public void setBankCardNumber(String bankCardNumber) {
		this.bankCardNumber = bankCardNumber;
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

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getShortBankCardNumber() {
		return shortBankCardNumber;
	}

	public void setShortBankCardNumber(String shortBankCardNumber) {
		this.shortBankCardNumber = shortBankCardNumber;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}

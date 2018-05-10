package xdt.dto;
/**
 * 添加信用卡请求接口
 * @author xiaomei
 *
 */
public class AddCreditCardRequestDTO {
      
	private String bankCardNumber;//信用卡卡号

    private String cardHolderName; //持卡人姓名

    private String bankId;//银行编码

    private String bankName;  //银行名称

	public String getBankCardNumber() {
		return bankCardNumber;
	}

	public void setBankCardNumber(String bankCardNumber) {
		this.bankCardNumber = bankCardNumber;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

}

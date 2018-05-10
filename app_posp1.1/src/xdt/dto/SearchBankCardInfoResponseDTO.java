package xdt.dto;
/**
 * 检索银行卡信息响应接口
 * @author lev12
 *
 */
public class SearchBankCardInfoResponseDTO {
	
	private String bankName; //银行名称
	
	private String cardName; //银行卡名称
	
    private Integer retCode;//是否成功
    
    private String retMessage;//信息描述

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

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
}

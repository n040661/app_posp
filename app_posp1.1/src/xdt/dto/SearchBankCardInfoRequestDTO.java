package xdt.dto;
/**
 * 检索银行卡信息请求接口
 * @author lev12
 *
 */
public class SearchBankCardInfoRequestDTO {
	
	private String bankCardBeforeSix; //银行卡前6位

	public String getBankCardBeforeSix() {
		return bankCardBeforeSix;
	}

	public void setBankCardBeforeSix(String bankCardBeforeSix) {
		this.bankCardBeforeSix = bankCardBeforeSix;
	}
}

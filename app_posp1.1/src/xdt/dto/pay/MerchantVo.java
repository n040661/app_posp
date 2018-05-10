package xdt.dto.pay;

import java.io.Serializable;

/**
 * 商户
 * 
 * @author tinn
 *
 */
public class MerchantVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String merchantUuid; // 商户UUID
	private String merchantCode; // 商户编号
	private String serverProviderCode; // 服务商编号
	private String debitRate; // 借记卡费率
	private String debitCapAmount; // 借记卡封顶
	private String creditRate; // 信用卡费率
	private String creditCapAmount; // 信用卡封顶

	public String getMerchantUuid() {
		return merchantUuid;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public String getServerProviderCode() {
		return serverProviderCode;
	}

	public String getDebitRate() {
		return debitRate;
	}

	public String getDebitCapAmount() {
		return debitCapAmount;
	}

	public String getCreditRate() {
		return creditRate;
	}

	public String getCreditCapAmount() {
		return creditCapAmount;
	}

	public void setMerchantUuid(String merchantUuid) {
		this.merchantUuid = merchantUuid;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public void setServerProviderCode(String serverProviderCode) {
		this.serverProviderCode = serverProviderCode;
	}

	public void setDebitRate(String debitRate) {
		this.debitRate = debitRate;
	}

	public void setDebitCapAmount(String debitCapAmount) {
		this.debitCapAmount = debitCapAmount;
	}

	public void setCreditRate(String creditRate) {
		this.creditRate = creditRate;
	}

	public void setCreditCapAmount(String creditCapAmount) {
		this.creditCapAmount = creditCapAmount;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("MerchantVo [merchantUuid=").append(merchantUuid).append(", merchantCode=").append(merchantCode)
				.append(", serverProviderCode=").append(serverProviderCode).append(", debitRate=").append(debitRate).append(", debitCapAmount=")
				.append(debitCapAmount).append(", creditRate=").append(creditRate).append(", creditCapAmount=").append(creditCapAmount).append("]").toString();
	}

}

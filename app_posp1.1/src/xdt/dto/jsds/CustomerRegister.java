package xdt.dto.jsds;

public class CustomerRegister {
	
	private String merchantCode;
	
	private String payType;
	
	private String product;
	
	private String tranTp;
	
	private String fee;
	
	private String paymentfee;
	
	private String realName;
	
	private String certNo;
	
	private String cardNo;
	
	private String mobile;
	
	private String bankCardName;
	
	private String pmsBankNo;
	
	private String identidy;
	
	private String url;
	
	private String sign;

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getTranTp() {
		return tranTp;
	}

	public void setTranTp(String tranTp) {
		this.tranTp = tranTp;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getPaymentfee() {
		return paymentfee;
	}

	public void setPaymentfee(String paymentfee) {
		this.paymentfee = paymentfee;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBankCardName() {
		return bankCardName;
	}

	public void setBankCardName(String bankCardName) {
		this.bankCardName = bankCardName;
	}

	public String getPmsBankNo() {
		return pmsBankNo;
	}

	public void setPmsBankNo(String pmsBankNo) {
		this.pmsBankNo = pmsBankNo;
	}
 
	public String getIdentidy() {
		return identidy;
	}

	public void setIdentidy(String identidy) {
		this.identidy = identidy;
	}
 
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "CustomerRegister [merchantCode=" + merchantCode + ", payType=" + payType + ", product=" + product
				+ ", tranTp=" + tranTp + ", fee=" + fee + ", paymentfee=" + paymentfee + ", realName=" + realName
				+ ", certNo=" + certNo + ", cardNo=" + cardNo + ", mobile=" + mobile + ", bankCardName=" + bankCardName
				+ ", pmsBankNo=" + pmsBankNo + ", identidy=" + identidy + ", url=" + url + ", sign=" + sign + "]";
	}
   

   
	
}

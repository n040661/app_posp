package xdt.dto;

/**
 * 实名认证信息查询响应接口
 * 
 * @author lev12
 * 
 */
public class SearchRealNameAuthenticationInformationResponseDTO {

	private Integer retCode;// 信息编号

	private String retMessage;// 信息描述

	private String merchantName;// 商户名称

	private String name;// 商户姓名

	private String identityCard;// 身份证号

	private String cardType;// 证件类型

	private String email; // 邮箱

	private String accNo; // 银行卡号
	
	private String bankName;//开户行

	private String address;//商户地址
	
	private String backReason;//打回原因
	
	private String brushRate;//刷卡费率
	
	private String thirdPartRate;//第三方费率
	
	private String mercId;//商户编号
	
	private String mobilephone;//手机号
	
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

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentityCard() {
		return identityCard;
	}

	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBackReason() {
		return backReason;
	}

	public void setBackReason(String backReason) {
		this.backReason = backReason;
	}

	public String getBrushRate() {
		return brushRate;
	}

	public void setBrushRate(String brushRate) {
		this.brushRate = brushRate;
	}

	public String getThirdPartRate() {
		return thirdPartRate;
	}

	public void setThirdPartRate(String thirdPartRate) {
		this.thirdPartRate = thirdPartRate;
	}

	public String getMercId() {
		return mercId;
	}

	public void setMercId(String mercId) {
		this.mercId = mercId;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}
	
	
	
	

}

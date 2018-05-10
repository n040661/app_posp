package xdt.dto;

/**
 * 实名认证上传文件请求
 * 
 * @author lev12
 * 
 */
public class RealNameAuthenticationUploadFilesRequestDTO {

	private String name;// 商户姓名

	private String identityCard;// 身份证号

	private String cardType;// 证件类型

	private String email;// 邮箱

	private String accNO; // 银行卡号

	private String headBankName; // 总行名称

	private String provinceId; // 省id

	private String cityId; // 市id

	private String bankCode; // 银行id

	private String merchantName;// 商户名称

	private String address; // 商户地址
	
	private String merchantAddress; // 商户地址

	private String rate;// 商户费率
	
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

	public String getAccNO() {
		return accNO;
	}

	public void setAccNO(String accNO) {
		this.accNO = accNO;
	}

	public String getHeadBankName() {
		return headBankName;
	}

	public void setHeadBankName(String headBankName) {
		this.headBankName = headBankName;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getMerchantAddress() {
		return merchantAddress;
	}

	public void setMerchantAddress(String merchantAddress) {
		this.merchantAddress = merchantAddress;
	}

}

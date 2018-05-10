package xdt.dto.weixin;

public class VerifyInfoDto extends BaseDto {

	
	/** @Fields serialVersionUID: */
	  	
	private static final long serialVersionUID = 1L;
	
	private String account;// 账号

	private String password;// 密码

	private String realName;// 真实姓名

	private String merchartName;// 商户名称

	private String merchartNameSort;// 商户简称

	private String phone;// 联系手机

	private String cardType;// 卡类型

	private String cardNo;// 卡号

	private String certType;// 证件类型

	private String certNo;// 证件号

	private String mobile;// 商户手机

	private String location;// 地址

	private Integer status;// 状态

	private String privateKey;//私钥

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getMerchartName() {
		return merchartName;
	}

	public void setMerchartName(String merchartName) {
		this.merchartName = merchartName;
	}

	public String getMerchartNameSort() {
		return merchartNameSort;
	}

	public void setMerchartNameSort(String merchartNameSort) {
		this.merchartNameSort = merchartNameSort;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "VerifyInfoDto [account=" + account + ", password=" + password
				+ ", realName=" + realName + ", merchartName=" + merchartName
				+ ", merchartNameSort=" + merchartNameSort + ", phone=" + phone
				+ ", cardType=" + cardType + ", cardNo=" + cardNo
				+ ", certType=" + certType + ", certNo=" + certNo + ", mobile="
				+ mobile + ", location=" + location + ", status=" + status
				+ ", privateKey=" + privateKey + "]";
	}
	

}

package xdt.model;

import java.util.Date;

public class PmsWeixinMerchartInfo {
	private String account;// 账号

	private String merchartId;// 平台商户号

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

	private Date createDate;//创建日期

	private Date updateDate;//修改日期
	
	private String rate;//费率
	
	private String wxRate;
	
	private String alipayRate;
	
	private String signnature;//签名
	
	private String appId;//关注公众号
	
	private String customerType;//商户类型
	
	private String contact;//联系人
	
	private String contactEmail;//联系邮箱
	
	private String servicePhone;//客服电话
	
	private String businessType;//经营行业
	
	private String provinceName;//经营省
	
	private String cityName;//经营市
	
	private String distirctName;//经营区
	
	private String licenseNo;//营业执照
	
	private String payChannel;//支付通道
	
	private String t0Status;//是否开通T+0
	
	private String settleRate;//+0 费率
	
	private String fixedFee;//T+0 单笔加收费用
	
	private String isCapped;//是否封顶（Y,N）
	
	private String settleMode;//结算(T0_INSTANT实时，T0_BATCH批量,T0_HANDING手动,T1_AUTO自动) 
	
	private String upperFee;//封顶值
	
	private String accountType;//账户类型（PERSONAL-个体户 ，COMPANY-公户）
	
	private String accountName;//开户名
	
	private String bankName;//开户行名称
	
	private String province;//开户行省份
	
	private String city;//开户行城市
	
	private String bankAddress;//开户行地址
	
	private String serviceType;//业务类型
	
	private String accountLicence;//开户许可证(如XXX.jpg 或 dir/XXX.jpg,不支持中文命名)
	
	
	private String certCorrect;//身份证正面图片
	
	private String pmsBankNo;//联行号
	
	private String cardOpposite;//银行卡背面图片
	
	private String certMeet;// 手持身份证图片
	
	private String cardCorrect;//银行卡正面图片
	
	private String certOpposite;//身份证背面图片
	
	private String wxT1Fee; //微信t1
	private String wxT0Fee; //微信t0
	private String alipayT1Fee; //支付宝t1
	private String alipayT0Fee; //支付宝t0
	
	private String oAgentNo; //欧单编号
	
    private String licenseImage;//营业执照(如XXX.jpg 或 dir/XXX.jpg,不支持中文命名)   
    private String subContractId;//子协议编号	
    
    
	private String blendFee;//混合快捷费率
    private String standard;//标准快捷费率
    private String withdrawDepositRate;//提现费率
    private String withdrawDepositSingleFee;//单笔提现手续费
    private String bankCode;//银行代码
    private String bankAbbr;//银行代号
    private String debitRate;//借记卡费率
    private String debitCapAmount;//借记卡封顶值
    private String creditRate;//信用卡费率
    private String creditCapAmount;//信用卡封顶值
    private String rateCode;//费率编号
    private String walletD0;//个人账户钱包D0
    private String walletT1;//个人账户钱包T1

	public String getWithdrawDepositRate() {
		return withdrawDepositRate;
	}

	public void setWithdrawDepositRate(String withdrawDepositRate) {
		this.withdrawDepositRate = withdrawDepositRate;
	}

	public String getWithdrawDepositSingleFee() {
		return withdrawDepositSingleFee;
	}

	public void setWithdrawDepositSingleFee(String withdrawDepositSingleFee) {
		this.withdrawDepositSingleFee = withdrawDepositSingleFee;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankAbbr() {
		return bankAbbr;
	}

	public void setBankAbbr(String bankAbbr) {
		this.bankAbbr = bankAbbr;
	}

	public String getDebitRate() {
		return debitRate;
	}

	public void setDebitRate(String debitRate) {
		this.debitRate = debitRate;
	}

	public String getDebitCapAmount() {
		return debitCapAmount;
	}

	public void setDebitCapAmount(String debitCapAmount) {
		this.debitCapAmount = debitCapAmount;
	}

	public String getCreditRate() {
		return creditRate;
	}

	public void setCreditRate(String creditRate) {
		this.creditRate = creditRate;
	}

	public String getCreditCapAmount() {
		return creditCapAmount;
	}

	public void setCreditCapAmount(String creditCapAmount) {
		this.creditCapAmount = creditCapAmount;
	}

	public String getRateCode() {
		return rateCode;
	}

	public void setRateCode(String rateCode) {
		this.rateCode = rateCode;
	}


	public String getWalletD0() {
		return walletD0;
	}

	public void setWalletD0(String walletD0) {
		this.walletD0 = walletD0;
	}

	public String getWalletT1() {
		return walletT1;
	}

	public void setWalletT1(String walletT1) {
		this.walletT1 = walletT1;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getServicePhone() {
		return servicePhone;
	}

	public void setServicePhone(String servicePhone) {
		this.servicePhone = servicePhone;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getDistirctName() {
		return distirctName;
	}

	public void setDistirctName(String distirctName) {
		this.distirctName = distirctName;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getT0Status() {
		return t0Status;
	}

	public void setT0Status(String t0Status) {
		this.t0Status = t0Status;
	}

	public String getSettleRate() {
		return settleRate;
	}

	public void setSettleRate(String settleRate) {
		this.settleRate = settleRate;
	}

	public String getFixedFee() {
		return fixedFee;
	}

	public void setFixedFee(String fixedFee) {
		this.fixedFee = fixedFee;
	}

	public String getIsCapped() {
		return isCapped;
	}

	public void setIsCapped(String isCapped) {
		this.isCapped = isCapped;
	}

	public String getSettleMode() {
		return settleMode;
	}

	public void setSettleMode(String settleMode) {
		this.settleMode = settleMode;
	}

	public String getUpperFee() {
		return upperFee;
	}

	public void setUpperFee(String upperFee) {
		this.upperFee = upperFee;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBankAddress() {
		return bankAddress;
	}

	public void setBankAddress(String bankAddress) {
		this.bankAddress = bankAddress;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getAccountLicence() {
		return accountLicence;
	}

	public void setAccountLicence(String accountLicence) {
		this.accountLicence = accountLicence;
	}

	public String getLicenseImage() {
		return licenseImage;
	}

	public void setLicenseImage(String licenseImage) {
		this.licenseImage = licenseImage;
	}

	public String getDoorHeadImage() {
		return doorHeadImage;
	}

	public void setDoorHeadImage(String doorHeadImage) {
		this.doorHeadImage = doorHeadImage;
	}

	public String getMerchantNumber() {
		return merchantNumber;
	}

	public void setMerchantNumber(String merchantNumber) {
		this.merchantNumber = merchantNumber;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	private String doorHeadImage;//门面照(如XXX.jpg 或 dir/XXX.jpg,不支持中文命名)
	
	private String merchantNumber;//机构商商户编号
	
	private String classId;//与正式商户id关联

	
	private String customerNum;//上又返回的商户号
	
	private String apiKey;//上游返回的秘钥
	
	private String sign;
	
	
	

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account == null ? null : account.trim();
	}

	public String getMerchartId() {
		return merchartId;
	}

	public void setMerchartId(String merchartId) {
		this.merchartId = merchartId == null ? null : merchartId.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName == null ? null : realName.trim();
	}

	public String getMerchartName() {
		return merchartName;
	}

	public void setMerchartName(String merchartName) {
		this.merchartName = merchartName == null ? null : merchartName.trim();
	}

	public String getMerchartNameSort() {
		return merchartNameSort;
	}

	public void setMerchartNameSort(String merchartNameSort) {
		this.merchartNameSort = merchartNameSort == null ? null
				: merchartNameSort.trim();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone == null ? null : phone.trim();
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType == null ? null : cardType.trim();
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo == null ? null : cardNo.trim();
	}

	public String getCertType() {
		return certType;
	}

	public void setCertType(String certType) {
		this.certType = certType == null ? null : certType.trim();
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo == null ? null : certNo.trim();
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? null : mobile.trim();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location == null ? null : location.trim();
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
		this.privateKey = privateKey == null ? null : privateKey.trim();
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getWxRate() {
		return wxRate;
	}

	public void setWxRate(String wxRate) {
		this.wxRate = wxRate;
	}

	public String getAlipayRate() {
		return alipayRate;
	}

	public void setAlipayRate(String alipayRate) {
		this.alipayRate = alipayRate;
	}
	public String getCertCorrect() {
		return certCorrect;
	}

	public void setCertCorrect(String certCorrect) {
		this.certCorrect = certCorrect;
	}

	public String getPmsBankNo() {
		return pmsBankNo;
	}

	public void setPmsBankNo(String pmsBankNo) {
		this.pmsBankNo = pmsBankNo;
	}

	public String getCardOpposite() {
		return cardOpposite;
	}

	public void setCardOpposite(String cardOpposite) {
		this.cardOpposite = cardOpposite;
	}

	public String getCertMeet() {
		return certMeet;
	}

	public void setCertMeet(String certMeet) {
		this.certMeet = certMeet;
	}

	public String getCardCorrect() {
		return cardCorrect;
	}

	public void setCardCorrect(String cardCorrect) {
		this.cardCorrect = cardCorrect;
	}

	public String getCertOpposite() {
		return certOpposite;
	}

	public void setCertOpposite(String certOpposite) {
		this.certOpposite = certOpposite;
	}

	public String getSignnature() {
		return signnature;
	}

	public void setSignnature(String signnature) {
		this.signnature = signnature;
	}

	public String getWxT1Fee() {
		return wxT1Fee;
	}

	public void setWxT1Fee(String wxT1Fee) {
		this.wxT1Fee = wxT1Fee;
	}

	public String getWxT0Fee() {
		return wxT0Fee;
	}

	public void setWxT0Fee(String wxT0Fee) {
		this.wxT0Fee = wxT0Fee;
	}

	public String getAlipayT1Fee() {
		return alipayT1Fee;
	}

	public void setAlipayT1Fee(String alipayT1Fee) {
		this.alipayT1Fee = alipayT1Fee;
	}

	public String getAlipayT0Fee() {
		return alipayT0Fee;
	}

	public void setAlipayT0Fee(String alipayT0Fee) {
		this.alipayT0Fee = alipayT0Fee;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

	public String getSubContractId() {
		return subContractId;
	}

	public void setSubContractId(String subContractId) {
		this.subContractId = subContractId;
	}

	public String getBlendFee() {
		return blendFee;
	}

	public void setBlendFee(String blendFee) {
		this.blendFee = blendFee;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}
    
	
	
}
package xdt.dto.quick;
/** 
* @author 作者 E-mail: 
* @version 创建时间：2017年12月28日 上午9:57:37 
* 类说明 
*/
public class QuickPayRequest {

	private String merchantId;//商户号
	private String orderId;//商户订单号
	private String payorderno;//平台订单号
	private String businessType;//业务类型0对私，1对公
	private String bankName;//银行名称
	private String acctNo;//银行卡号
	private String acctName;//账户名
	private String amount;//金额（分）
	private String accountType;//账户类型00借记卡
	private String returnUrl;//同步地址
	private String notifyUrl;//异步通知URL
	private String province;//省份
	private String city;//城市
	private String area;//区域
	private String branchBankName;//网点名称
	private String pmsbankNo;//联行号
	private String userIp;//用户ip
	private String liceneceType;//证件类型
	private String liceneceNo;//证件号
	private String phone;//手机号
	private String month;//信用卡有效期月
	private String year;//信用卡有效期年
	private String cvv2;//信用卡卡后三位
	private String purpose;//用途
	private String summary;//备注
	private String identity;//客户标识
	private String startDate;//发起代付时间
	private String url;//保存异步地址用
	private String reUrl;//保存同步地址用
	private String type;//
	private String bankCode;//银行编码
	private String bankAbbr;//银行代号
	private String smsCode;//短信验证码
	private String changeType;//变更类型
	private String merchantCode;//商户服务号
	private String payNo;//短信流水号
	private String productName;//商品名称
	private String productDesc;//商品描述
	private String respCode;
	private String respMsg;//
	private String oraderType;//交易周期 0:D0,1:T1
	private String sign;//
	
	public String getPayorderno() {
		return payorderno;
	}
	public void setPayorderno(String payorderno) {
		this.payorderno = payorderno;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAcctNo() {
		return acctNo;
	}
	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}
	public String getAcctName() {
		return acctName;
	}
	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
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
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getBranchBankName() {
		return branchBankName;
	}
	public void setBranchBankName(String branchBankName) {
		this.branchBankName = branchBankName;
	}
	public String getPmsbankNo() {
		return pmsbankNo;
	}
	public void setPmsbankNo(String pmsbankNo) {
		this.pmsbankNo = pmsbankNo;
	}
	public String getUserIp() {
		return userIp;
	}
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	public String getLiceneceType() {
		return liceneceType;
	}
	public void setLiceneceType(String liceneceType) {
		this.liceneceType = liceneceType;
	}
	public String getLiceneceNo() {
		return liceneceNo;
	}
	public void setLiceneceNo(String liceneceNo) {
		this.liceneceNo = liceneceNo;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getCvv2() {
		return cvv2;
	}
	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getReUrl() {
		return reUrl;
	}
	public void setReUrl(String reUrl) {
		this.reUrl = reUrl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getSmsCode() {
		return smsCode;
	}
	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}
	public String getChangeType() {
		return changeType;
	}
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	public String getMerchantCode() {
		return merchantCode;
	}
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
	public String getPayNo() {
		return payNo;
	}
	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public String getOraderType() {
		return oraderType;
	}
	public void setOraderType(String oraderType) {
		this.oraderType = oraderType;
	}
	
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "QuickPayRequest [merchantId=" + merchantId + ", orderId=" + orderId + ", businessType=" + businessType
				+ ", bankName=" + bankName + ", acctNo=" + acctNo + ", acctName=" + acctName + ", amount=" + amount
				+ ", accountType=" + accountType + ", returnUrl=" + returnUrl + ", notifyUrl=" + notifyUrl
				+ ", province=" + province + ", city=" + city + ", area=" + area + ", branchBankName=" + branchBankName
				+ ", pmsbankNo=" + pmsbankNo + ", userIp=" + userIp + ", liceneceType=" + liceneceType + ", liceneceNo="
				+ liceneceNo + ", phone=" + phone + ", month=" + month + ", year=" + year + ", cvv2=" + cvv2
				+ ", purpose=" + purpose + ", summary=" + summary + ", identity=" + identity + ", startDate="
				+ startDate + ", url=" + url + ", reUrl=" + reUrl + ", type=" + type + ", bankCode=" + bankCode
				+ ", bankAbbr=" + bankAbbr + ", smsCode=" + smsCode + ", changeType=" + changeType + ", merchantCode="
				+ merchantCode + ", payNo=" + payNo + ", productName=" + productName + ", productDesc=" + productDesc
				+ ", respCode=" + respCode + ", respMsg=" + respMsg + ", oraderType=" + oraderType + ", sign=" + sign
				+ "]";
	}
	
	
}

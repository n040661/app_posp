package xdt.dto.hfb;

public class HFBPayRequest {

	private String merchantId;//商户号
	private String merchantBatchNo;//商户交易号
	private String batchAmount;//付款总金额
	private String batchNum;//付款总笔数
	private String intoAccountDay;//到账日期0=当日，1=次日
	private String transferDetails;//付款详情
	private String version;//版本号
	private String requestTime;//请求时间
	private String notifyUrl;//通知URL
	//==================
	private String merchantPayNo;//商户单笔支付流水号
	private String bankId;//银行代码
	private String publicFlag;//对公对私
	private String bankcardNo;//银行卡号
	private String ownerName;//持卡人姓名
	private String amount;//转账金额
	private String reason;//转账理由
	private String province;//省
	private String city;//市
	private String bankName;//开户支行名称
	private String signString;//给上游的签名
	private String sign;//下游签名
	private String url;//下游异步地址
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantBatchNo() {
		return merchantBatchNo;
	}
	public void setMerchantBatchNo(String merchantBatchNo) {
		this.merchantBatchNo = merchantBatchNo;
	}
	public String getBatchAmount() {
		return batchAmount;
	}
	public void setBatchAmount(String batchAmount) {
		this.batchAmount = batchAmount;
	}
	public String getBatchNum() {
		return batchNum;
	}
	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
	}
	public String getIntoAccountDay() {
		return intoAccountDay;
	}
	public void setIntoAccountDay(String intoAccountDay) {
		this.intoAccountDay = intoAccountDay;
	}
	public String getTransferDetails() {
		return transferDetails;
	}
	public void setTransferDetails(String transferDetails) {
		this.transferDetails = transferDetails;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getMerchantPayNo() {
		return merchantPayNo;
	}
	public void setMerchantPayNo(String merchantPayNo) {
		this.merchantPayNo = merchantPayNo;
	}
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getPublicFlag() {
		return publicFlag;
	}
	public void setPublicFlag(String publicFlag) {
		this.publicFlag = publicFlag;
	}
	public String getBankcardNo() {
		return bankcardNo;
	}
	public void setBankcardNo(String bankcardNo) {
		this.bankcardNo = bankcardNo;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
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
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getSignString() {
		return signString;
	}
	public void setSignString(String signString) {
		this.signString = signString;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "HFBPayRequest [merchantId=" + merchantId + ", merchantBatchNo="
				+ merchantBatchNo + ", batchAmount=" + batchAmount
				+ ", batchNum=" + batchNum + ", intoAccountDay="
				+ intoAccountDay + ", transferDetails=" + transferDetails
				+ ", version=" + version + ", requestTime=" + requestTime
				+ ", notifyUrl=" + notifyUrl + ", merchantPayNo="
				+ merchantPayNo + ", bankId=" + bankId + ", publicFlag="
				+ publicFlag + ", bankcardNo=" + bankcardNo + ", ownerName="
				+ ownerName + ", amount=" + amount + ", reason=" + reason
				+ ", province=" + province + ", city=" + city + ", bankName="
				+ bankName + ", signString=" + signString + ", sign=" + sign
				+ ", url=" + url + "]";
	}
	
	
	
}

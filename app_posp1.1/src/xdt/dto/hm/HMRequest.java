package xdt.dto.hm;

public class HMRequest {

	private String merchantId;//商户号
	private String timeStamp;//时间戳yyyyMMddHHmmss
	private String orderNumber;//商户订单号
	private String orderId;//
	private String userName;//持卡人姓名
	private String userId;//持卡人证件编号
	private String userCardNo;//银行卡号
	private String userTel;//手机号
	private String amount;//金额分
	private String accountType;//0对公账户1对私账户
	private String userpidType;//证件类型01身份证02军官证03其他证件
	private String bank;//开户支行名称
	private String bankunion;//联行号
	private String notifyUrl;//后台通知地址
	private String returnUrl;//前台通知地址
	private String province;//省份
	private String city;//城市
	private String url;//异步
	private String reUrl;//同步
	private String respCode;//
	private String respMsg;//
	private String type;//0借记卡1信用卡
	private String sign;//签名
	
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserCardNo() {
		return userCardNo;
	}
	public void setUserCardNo(String userCardNo) {
		this.userCardNo = userCardNo;
	}
	public String getUserTel() {
		return userTel;
	}
	public void setUserTel(String userTel) {
		this.userTel = userTel;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
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
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getUserpidType() {
		return userpidType;
	}
	public void setUserpidType(String userpidType) {
		this.userpidType = userpidType;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getBankunion() {
		return bankunion;
	}
	public void setBankunion(String bankunion) {
		this.bankunion = bankunion;
	}
	@Override
	public String toString() {
		return "HMRequest [merchantId=" + merchantId + ", timeStamp="
				+ timeStamp + ", orderNumber=" + orderNumber + ", userName="
				+ userName + ", userId=" + userId + ", userCardNo="
				+ userCardNo + ", userTel=" + userTel + ", amount=" + amount
				+ ", accountType=" + accountType + ", userpidType="
				+ userpidType + ", bank=" + bank + ", bankunion=" + bankunion
				+ ", notifyUrl=" + notifyUrl + ", returnUrl=" + returnUrl
				+ ", url=" + url + ", reUrl=" + reUrl + ", sign=" + sign + "]";
	}
	
	
	
}

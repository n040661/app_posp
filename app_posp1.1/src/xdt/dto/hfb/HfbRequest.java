package xdt.dto.hfb;

public class HfbRequest {

	private String merchantId;//商户号
	private String merchantOrderNo;//商户交易号
	private String merchantUserId;//用户号
	private String productCode;//productCode
	private String payAmount;//交易金额
	private String requestTime;//请求时间
	private String version;//版本号
	private String notifyUrl;//通知URL
	private String callBackUrl;//通知URL
	private String description;//商品信息
	private String clientIp;//用户ip
	private String reqHyTime;//防钓鱼时间
	private String signString;//签名字符串
	private String onlineType;//选择银行方式
	private String bankId;//银行id
	private String bankName;//银行名称
	private String bankCardType;//银行卡类型
	//-------------------------------
	private String merchantBillNo;//商户订单号
	private String tradeType;//交易类型
	private String payAmt;//支付金额
	private String returnUrl;//同步通知地址
	private String userIp;//终端ip
	private String remark;//附加数据
	private String goodsName;//商品名称
	private String goodsNote;//支付说明
	private String sign;//签名字符串(详见3.3签名机制)
	private String url;
	private String reUrl;
	private String type;//0：D0,1:T1
	private String qrCodeStatus;
	
	
	
	public String getQrCodeStatus() {
		return qrCodeStatus;
	}
	public void setQrCodeStatus(String qrCodeStatus) {
		this.qrCodeStatus = qrCodeStatus;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}
	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
	}
	public String getMerchantUserId() {
		return merchantUserId;
	}
	public void setMerchantUserId(String merchantUserId) {
		this.merchantUserId = merchantUserId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}
	public String getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getCallBackUrl() {
		return callBackUrl;
	}
	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getReqHyTime() {
		return reqHyTime;
	}
	public void setReqHyTime(String reqHyTime) {
		this.reqHyTime = reqHyTime;
	}
	public String getSignString() {
		return signString;
	}
	public void setSignString(String signString) {
		this.signString = signString;
	}
	public String getOnlineType() {
		return onlineType;
	}
	public void setOnlineType(String onlineType) {
		this.onlineType = onlineType;
	}
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankCardType() {
		return bankCardType;
	}
	public void setBankCardType(String bankCardType) {
		this.bankCardType = bankCardType;
	}
	public String getMerchantBillNo() {
		return merchantBillNo;
	}
	public void setMerchantBillNo(String merchantBillNo) {
		this.merchantBillNo = merchantBillNo;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getPayAmt() {
		return payAmt;
	}
	public void setPayAmt(String payAmt) {
		this.payAmt = payAmt;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getUserIp() {
		return userIp;
	}
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsNote() {
		return goodsNote;
	}
	public void setGoodsNote(String goodsNote) {
		this.goodsNote = goodsNote;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "HfbRequest [merchantId=" + merchantId + ", merchantOrderNo="
				+ merchantOrderNo + ", merchantUserId=" + merchantUserId
				+ ", productCode=" + productCode + ", payAmount=" + payAmount
				+ ", requestTime=" + requestTime + ", version=" + version
				+ ", notifyUrl=" + notifyUrl + ", callBackUrl=" + callBackUrl
				+ ", description=" + description + ", clientIp=" + clientIp
				+ ", reqHyTime=" + reqHyTime + ", signString=" + signString
				+ ", onlineType=" + onlineType + ", bankId=" + bankId
				+ ", bankName=" + bankName + ", bankCardType=" + bankCardType
				+ ", merchantBillNo=" + merchantBillNo + ", tradeType="
				+ tradeType + ", payAmt=" + payAmt + ", returnUrl=" + returnUrl
				+ ", userIp=" + userIp + ", remark=" + remark + ", goodsName="
				+ goodsName + ", goodsNote=" + goodsNote + ", sign=" + sign
				+ "]";
	}
	
	
}

package xdt.tools;

public class Client {
	//交易金额
	private String amount;
	//商户订单描述
	private String orderDesc;
	//商户保留信息
	private String extData;
	//订单扩展信息
	private String miscData;
	//商户订单号
	private String merchOrderId;
	//商户代码
	private String merchantId;
	//异步通知URL
	private String notifyUrl;
	//商户订单提交时间
	private String tradeTime;
	//交易超时时间
	private String expTime;
	//订单通知标志
	private String notifyFlag;
	//下游商户号
	private String priKey;
	
	private String pubKey;
	//易联的url
	private String payecoUrl;
	//通讯协议版本号
	private String version;
	//返回码
	private String status;
	
	private String payTime;
	private String orderId;
	private String settleDate;
	//下有商户id
	private String merchantId1;
	//返回通知URL
	private String returnUrl;
	//用户请求IP
	private String clientIp;
	
	private String url;
	private String reUrl;
	private String sign;
	
	private String tranTp;
	
	private String product;
	
	private String realName;
	
	private String cardNo;//证件号
	
	private String name;//姓名
	
	private String smId;//短信凭证
	
	private String mobileNo;//手机号
	
	private String bankNo;//银行卡号
	
	private String industryNo;//行业编码
	
	private String city;//省份
	
	private String smCode;//短信验证码
	
	private String respCode;
	private String respDesc;
	
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public String getSmCode() {
		return smCode;
	}
	public void setSmCode(String smCode) {
		this.smCode = smCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getSmId() {
		return smId;
	}
	public void setSmId(String smId) {
		this.smId = smId;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getIndustryNo() {
		return industryNo;
	}
	public void setIndustryNo(String industryNo) {
		this.industryNo = industryNo;
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getPriKey() {
		return priKey;
	}
	public void setPriKey(String priKey) {
		this.priKey = priKey;
	}
	public String getPubKey() {
		return pubKey;
	}
	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}
	public String getPayecoUrl() {
		return payecoUrl;
	}
	public void setPayecoUrl(String payecoUrl) {
		this.payecoUrl = payecoUrl;
	}
	public String getMerchantId1() {
		return merchantId1;
	}
	public void setMerchantId1(String merchantId1) {
		this.merchantId1 = merchantId1;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getOrderDesc() {
		return orderDesc;
	}
	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}
	public String getExtData() {
		return extData;
	}
	public void setExtData(String extData) {
		this.extData = extData;
	}
	public String getMiscData() {
		return miscData;
	}
	public void setMiscData(String miscData) {
		this.miscData = miscData;
	}
	public String getMerchOrderId() {
		return merchOrderId;
	}
	public void setMerchOrderId(String merchOrderId) {
		this.merchOrderId = merchOrderId;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public String getExpTime() {
		return expTime;
	}
	public void setExpTime(String expTime) {
		this.expTime = expTime;
	}
	public String getNotifyFlag() {
		return notifyFlag;
	}
	public void setNotifyFlag(String notifyFlag) {
		this.notifyFlag = notifyFlag;
	}
	public String getTranTp() {
		return tranTp;
	}
	public void setTranTp(String tranTp) {
		this.tranTp = tranTp;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}

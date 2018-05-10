package xdt.dto.hj;

public class HJRequest {

	private String version;//版本号
	private String merchantNo;//商户编号 
	private String orderNo;//商户订单号 
	private String amount;//订单金额 
	private String cur;//交易币种 
	private String productName;//商品名称
	private String mp;//公用回传参数
	private String returnUrl;//商户页面通知地址 
	private String notifyUrl;//服务器异步通知地址 
	private String frpCode;//银行编码 
	private String orderPeriod;//订单有效期 
	private String productDesc;//商品描述 
	private String merchantBankCode;//银行商户编码 
	private String subMerchantNo;//子商户号 
	private String isShowPic;//是否展示图片 
	private String openId;//微信 Openid 
	private String authCode;//付款码数字 
	private String appId ;//APPID 
	private String terminalNo ;//终端号 
	private String transactionModel ;//微信 H5 模式 
	private String url;
	private String reUrl;
	private String hmac;//签名数据 
	private String sign;
	
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMerchantNo() {
		return merchantNo;
	}
	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCur() {
		return cur;
	}
	public void setCur(String cur) {
		this.cur = cur;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getMp() {
		return mp;
	}
	public void setMp(String mp) {
		this.mp = mp;
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
	public String getFrpCode() {
		return frpCode;
	}
	public void setFrpCode(String frpCode) {
		this.frpCode = frpCode;
	}
	public String getOrderPeriod() {
		return orderPeriod;
	}
	public void setOrderPeriod(String orderPeriod) {
		this.orderPeriod = orderPeriod;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public String getMerchantBankCode() {
		return merchantBankCode;
	}
	public void setMerchantBankCode(String merchantBankCode) {
		this.merchantBankCode = merchantBankCode;
	}
	public String getSubMerchantNo() {
		return subMerchantNo;
	}
	public void setSubMerchantNo(String subMerchantNo) {
		this.subMerchantNo = subMerchantNo;
	}
	public String getIsShowPic() {
		return isShowPic;
	}
	public void setIsShowPic(String isShowPic) {
		this.isShowPic = isShowPic;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTerminalNo() {
		return terminalNo;
	}
	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}
	public String getTransactionModel() {
		return transactionModel;
	}
	public void setTransactionModel(String transactionModel) {
		this.transactionModel = transactionModel;
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
	public String getHmac() {
		return hmac;
	}
	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
	@Override
	public String toString() {
		return "HJRequest [version=" + version + ", merchantNo=" + merchantNo
				+ ", orderNo=" + orderNo + ", amount=" + amount + ", cur="
				+ cur + ", productName=" + productName + ", mp=" + mp
				+ ", returnUrl=" + returnUrl + ", notifyUrl=" + notifyUrl
				+ ", frpCode=" + frpCode + ", orderPeriod=" + orderPeriod
				+ ", productDesc=" + productDesc + ", merchantBankCode="
				+ merchantBankCode + ", subMerchantNo=" + subMerchantNo
				+ ", isShowPic=" + isShowPic + ", openId=" + openId
				+ ", authCode=" + authCode + ", appId=" + appId
				+ ", terminalNo=" + terminalNo + ", transactionModel="
				+ transactionModel + ", url=" + url + ", reUrl=" + reUrl
				+ ", hmac=" + hmac + "]";
	}
	
}

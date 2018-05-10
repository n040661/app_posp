package xdt.dto.jsds;

public class JsdsRequestDto extends CommonDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//微信支付
	private String merchantCode;//平台商户编号
	private String terminalCode;//平台商户终端编号
	private String orderNum;//合作商订单号，全局唯一
    private String transMoney; //支付宝交易金额，单位分
	@Deprecated
	private String productName;
	private String notifyUrl;//支付结果异步通知地址
	private String merchantName;//收款商户名称
	@Deprecated
	private String commodityName;//商品名称（如不填则使用收款商户名称）
	private String merchantNum;//商户门店编号
	private String terminalNum;//商户机具终端编号
	@Deprecated
	private String limitPay;//是否可以使用信用卡支付，填写no_credit表示不能使用信用卡，不填表示可以使用信用卡
	
	private String accountName;//收款人账户名
	private String bankCard;//收款人账户号
	private String bankName;//收款人账户开户行名称
	private String bankLinked;//收款人账户开户行联行号
	private String type;//代付类型T1或D0
	private String transDate;//交易日期（YYYYMMDD）
	private String transTime;//交易时间（HH24mmss）
	
	private String returnUrl; //前台通知地址
	
	private String bankCode; //银行编码
	
	
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getBankCard() {
		return bankCard;
	}
	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankLinked() {
		return bankLinked;
	}
	public void setBankLinked(String bankLinked) {
		this.bankLinked = bankLinked;
	}
	public String getMerchantCode() {
		return merchantCode;
	}
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
	public String getTerminalCode() {
		return terminalCode;
	}
	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	
	public String getTransMoney() {
		return transMoney;
	}
	public void setTransMoney(String transMoney) {
		this.transMoney = transMoney;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getCommodityName() {
		return commodityName;
	}
	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}
	public String getMerchantNum() {
		return merchantNum;
	}
	public void setMerchantNum(String merchantNum) {
		this.merchantNum = merchantNum;
	}
	public String getTerminalNum() {
		return terminalNum;
	}
	public void setTerminalNum(String terminalNum) {
		this.terminalNum = terminalNum;
	}
	public String getLimitPay() {
		return limitPay;
	}
	public void setLimitPay(String limitPay) {
		this.limitPay = limitPay;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	@Override
	public String toString() {
		return "JsdsRequestDto [merchantCode=" + merchantCode + ", terminalCode=" + terminalCode + ", orderNum="
				+ orderNum + ", transMoney=" + transMoney + ", productName=" + productName + ", notifyUrl=" + notifyUrl
				+ ", merchantName=" + merchantName + ", commodityName=" + commodityName + ", merchantNum=" + merchantNum
				+ ", terminalNum=" + terminalNum + ", limitPay=" + limitPay + ", accountName=" + accountName
				+ ", bankCard=" + bankCard + ", bankName=" + bankName + ", bankLinked=" + bankLinked + ", type=" + type
				+ ", transDate=" + transDate + ", transTime=" + transTime + ", returnUrl=" + returnUrl + ", bankCode="
				+ bankCode + "]";
	}
	
	
}

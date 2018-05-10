package xdt.dto.hlb;

public class HLBRequest {

	private String service;//类型
	private String merNo;//商户号
	private String orderId;//订单号
	private String userId;//用户id
	private String payerName;//出金姓名
	private String idCardType;//证件类型
	private String idCardNo;//证件号码
	private String cardNo;//银行卡号
	private String year;//信用卡有效期年份
	private String month;//信用卡有效期月份
	private String cvv2;//
	private String phone;//手机号
	private String currency;//币种
	private String orderAmount;//交易金额
	private String goodsName;//商品名称
	private String goodsDesc;//商品描述
	private String terminalType;//终端类型
	private String terminalId;//终端标识
	private String orderIp;//终端ip
	private String period;//订单有效时间
	private String periodUnit;//订单有效时间单位
	private String notifyUrl;//异步回调地址
	private String isIntegral;//TRUE：有积分通道FALSE:无积分通道
	private String validateCode;//短信验证码
	private String bindId;//绑卡id
	private String url;
	private String type;//类型
	private String bankUnionCode;
	private String dataType;
	private String sign;//签名
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getBankUnionCode() {
		return bankUnionCode;
	}
	public void setBankUnionCode(String bankUnionCode) {
		this.bankUnionCode = bankUnionCode;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getMerNo() {
		return merNo;
	}
	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPayerName() {
		return payerName;
	}
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	public String getIdCardType() {
		return idCardType;
	}
	public void setIdCardType(String idCardType) {
		this.idCardType = idCardType;
	}
	public String getIdCardNo() {
		return idCardNo;
	}
	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getCvv2() {
		return cvv2;
	}
	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsDesc() {
		return goodsDesc;
	}
	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	public String getTerminalType() {
		return terminalType;
	}
	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getOrderIp() {
		return orderIp;
	}
	public void setOrderIp(String orderIp) {
		this.orderIp = orderIp;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getPeriodUnit() {
		return periodUnit;
	}
	public void setPeriodUnit(String periodUnit) {
		this.periodUnit = periodUnit;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getIsIntegral() {
		return isIntegral;
	}
	public void setIsIntegral(String isIntegral) {
		this.isIntegral = isIntegral;
	}
	public String getValidateCode() {
		return validateCode;
	}
	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}
	public String getBindId() {
		return bindId;
	}
	public void setBindId(String bindId) {
		this.bindId = bindId;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "HLBRequest [service=" + service + ", merNo=" + merNo
				+ ", orderId=" + orderId + ", userId=" + userId
				+ ", payerName=" + payerName + ", idCardType=" + idCardType
				+ ", idCardNo=" + idCardNo + ", cardNo=" + cardNo + ", year="
				+ year + ", month=" + month + ", cvv2=" + cvv2 + ", phone="
				+ phone + ", currency=" + currency + ", orderAmount="
				+ orderAmount + ", goodsName=" + goodsName + ", goodsDesc="
				+ goodsDesc + ", terminalType=" + terminalType
				+ ", terminalId=" + terminalId + ", orderIp=" + orderIp
				+ ", period=" + period + ", periodUnit=" + periodUnit
				+ ", notifyUrl=" + notifyUrl + ", isIntegral=" + isIntegral
				+ ", validateCode=" + validateCode + ", bindId=" + bindId
				+ ", url=" + url + ", type=" + type + ", bankUnionCode="
				+ bankUnionCode + ", sign=" + sign + "]";
	}
	
	
}

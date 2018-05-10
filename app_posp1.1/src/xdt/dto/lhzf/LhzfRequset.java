package xdt.dto.lhzf;

public class LhzfRequset {

	private String transId;//交易接口编号
	private String serialNo;//交易流水号
	private String merNo;//商户号
	private String merKey;//商户交易KEY
	private String merIp;//商户请求IP
	private String orderNo;//商户订单号
	private String transAmt;//交易金额
	private String orderDesc;//订单内容描述
	private String transDate;//交易日期
	private String transTime;//交易时间
	private String overTime;//订单有效期
	private String returnUrl;//同步返回地址
	private String notifyUrl;//同步返回地址
	private String remark;//交易备注
	private String currency;//币种
	private String bankCode;//银行代码
	private String cardNo;//银行卡号
	private String cardType;//银行卡类型
	private String idNo;//开户证件号
	private String idType;//开户证件类型
	private String idName;//证件名称
	private String mobileNo;//开户手机号码
	private String userRate;//用户收款费率
	private String userFee;//用户付款费用
	private String payeeCurrency;//收款币种
	private String payeeCardNo;//收款银行卡号
	private String payeeIdNo;//收款证件号
	private String payeeBankCode;//收款银行代码
	private String payeeCardType;//收款证件类型
	private String payeeIdName;//收款证件名称
	private String payeeMobileNo;//收款手机号码
	private String payeePmsbankno;//收款银行卡号
	private String extraInfo;//交易扩展信息
	private String transInfo;//交易附带信息
	private String sign;//报文签名信息
	private String url;
	private String reUrl;
	
	
	public String getPayeePmsbankno() {
		return payeePmsbankno;
	}
	public void setPayeePmsbankno(String payeePmsbankno) {
		this.payeePmsbankno = payeePmsbankno;
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
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getMerNo() {
		return merNo;
	}
	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}
	public String getMerKey() {
		return merKey;
	}
	public void setMerKey(String merKey) {
		this.merKey = merKey;
	}
	public String getMerIp() {
		return merIp;
	}
	public void setMerIp(String merIp) {
		this.merIp = merIp;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getTransAmt() {
		return transAmt;
	}
	public void setTransAmt(String transAmt) {
		this.transAmt = transAmt;
	}
	public String getOrderDesc() {
		return orderDesc;
	}
	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}
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
	public String getOverTime() {
		return overTime;
	}
	public void setOverTime(String overTime) {
		this.overTime = overTime;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdName() {
		return idName;
	}
	public void setIdName(String idName) {
		this.idName = idName;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getUserRate() {
		return userRate;
	}
	public void setUserRate(String userRate) {
		this.userRate = userRate;
	}
	public String getUserFee() {
		return userFee;
	}
	public void setUserFee(String userFee) {
		this.userFee = userFee;
	}
	public String getPayeeCurrency() {
		return payeeCurrency;
	}
	public void setPayeeCurrency(String payeeCurrency) {
		this.payeeCurrency = payeeCurrency;
	}
	public String getPayeeCardNo() {
		return payeeCardNo;
	}
	public void setPayeeCardNo(String payeeCardNo) {
		this.payeeCardNo = payeeCardNo;
	}
	public String getPayeeIdNo() {
		return payeeIdNo;
	}
	public void setPayeeIdNo(String payeeIdNo) {
		this.payeeIdNo = payeeIdNo;
	}
	public String getPayeeBankCode() {
		return payeeBankCode;
	}
	public void setPayeeBankCode(String payeeBankCode) {
		this.payeeBankCode = payeeBankCode;
	}
	
	public String getPayeeCardType() {
		return payeeCardType;
	}
	public void setPayeeCardType(String payeeCardType) {
		this.payeeCardType = payeeCardType;
	}
	public String getPayeeIdName() {
		return payeeIdName;
	}
	public void setPayeeIdName(String payeeIdName) {
		this.payeeIdName = payeeIdName;
	}
	public String getPayeeMobileNo() {
		return payeeMobileNo;
	}
	public void setPayeeMobileNo(String payeeMobileNo) {
		this.payeeMobileNo = payeeMobileNo;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	public String getTransInfo() {
		return transInfo;
	}
	public void setTransInfo(String transInfo) {
		this.transInfo = transInfo;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "LhzfRequset [transId=" + transId + ", serialNo=" + serialNo
				+ ", merNo=" + merNo + ", merKey=" + merKey + ", merIp="
				+ merIp + ", orderNo=" + orderNo + ", transAmt=" + transAmt
				+ ", orderDesc=" + orderDesc + ", transDate=" + transDate
				+ ", transTime=" + transTime + ", overTime=" + overTime
				+ ", returnUrl=" + returnUrl + ", notifyUrl=" + notifyUrl
				+ ", remark=" + remark + ", currency=" + currency
				+ ", bankCode=" + bankCode + ", cardNo=" + cardNo
				+ ", cardType=" + cardType + ", idNo=" + idNo + ", idType="
				+ idType + ", idName=" + idName + ", mobileNo=" + mobileNo
				+ ", userRate=" + userRate + ", userFee=" + userFee
				+ ", payeeCurrency=" + payeeCurrency + ", payeeCardNo="
				+ payeeCardNo + ", payeeIdNo=" + payeeIdNo + ", payeeBankCode="
				+ payeeBankCode + ", payeeCardType=" + payeeCardType
				+ ", payeeIdName=" + payeeIdName + ", payeeMobileNo="
				+ payeeMobileNo + ", extraInfo=" + extraInfo + ", transInfo="
				+ transInfo + ", sign=" + sign + ", url=" + url + ", reUrl="
				+ reUrl + "]";
	}
	
	
	
}

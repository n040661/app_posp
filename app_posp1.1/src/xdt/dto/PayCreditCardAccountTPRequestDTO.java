package xdt.dto;
/**
 * 信用卡账户支付第三方请求接口
 * @author xiaomei
 *
 */
public class PayCreditCardAccountTPRequestDTO {
	
	private String version; //接口版本号
	
	private String reqTime; //请求时间  格式：yyyyMM24ddhhmmss
	
	private String mxId; //商户ID
	
	private String mxLoginName; //钱包名
	
	private String mxOrderId; //商户订单号
	
	private String amount; //代付金额(不包含)
	
	private String cardHolder; //开户人姓名
	
	private String bankCardId; //卡号
	
	private String accountType; //账户类型
	
	private String cardType; //卡类型
	
	private String bankCode; //银行编码
	
	private String subBankName; //支行名称
	
	private String provinceId; //省份ID
	
	private String cityId; //城市ID
	
	private String alliedBankCode; //联行号
	
	private String payType; //付款类型
	
	private String tradeDesc; //交易描述
	
	private String notifyUrl; //后台通知地址
	
	private String currency; //货币类型
	
	private String payerId; //付款方Id
	
	private String tradeType; //交易类型
	
	private String mxReserved; //商户回传参数
	
	private String commonExtend; //公共扩展参数
	
	private String persistHandling; //执着处理
	
	private String persistTimeOut; //执着超时时间
	
	private String securityInfo; //安全相关信息
	
	private String remark; //备注信息
	
	private String hmac; //签名
	
	private String extend1; //扩展字段一
	
	private String extend2; //扩展字段二
	
	private String extend3; //扩展字段三
	
	private String dealType;//交易类型

    private String dealGrade;//等级
    
    private String mercId; //数据库商户ID
    
    private String channelNo; //通道号
    
    private String AppFee; //手续费
    
    private String businessNumber; //业务号
    
    private String paymentType; //支付类型  0账户 1刷卡
    
    private String accNum; //账户编号
    
    private String withdrawalWay; //提款方式  0: T+0  1: T+1

	public String getWithdrawalWay() {
		return withdrawalWay;
	}

	public void setWithdrawalWay(String withdrawalWay) {
		this.withdrawalWay = withdrawalWay;
	}

	public String getAccNum() {
		return accNum;
	}

	public void setAccNum(String accNum) {
		this.accNum = accNum;
	}

	public String getBusinessNumber() {
		return businessNumber;
	}

	public void setBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getChannelNo() {
		return channelNo;
	}

	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}

	public String getAppFee() {
		return AppFee;
	}

	public void setAppFee(String appFee) {
		AppFee = appFee;
	}

	public String getMercId() {
		return mercId;
	}

	public void setMercId(String mercId) {
		this.mercId = mercId;
	}

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public String getDealGrade() {
		return dealGrade;
	}

	public void setDealGrade(String dealGrade) {
		this.dealGrade = dealGrade;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getReqTime() {
		return reqTime;
	}

	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}

	public String getMxId() {
		return mxId;
	}

	public void setMxId(String mxId) {
		this.mxId = mxId;
	}

	public String getMxLoginName() {
		return mxLoginName;
	}

	public void setMxLoginName(String mxLoginName) {
		this.mxLoginName = mxLoginName;
	}

	public String getMxOrderId() {
		return mxOrderId;
	}

	public void setMxOrderId(String mxOrderId) {
		this.mxOrderId = mxOrderId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCardHolder() {
		return cardHolder;
	}

	public void setCardHolder(String cardHolder) {
		this.cardHolder = cardHolder;
	}

	public String getBankCardId() {
		return bankCardId;
	}

	public void setBankCardId(String bankCardId) {
		this.bankCardId = bankCardId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getSubBankName() {
		return subBankName;
	}

	public void setSubBankName(String subBankName) {
		this.subBankName = subBankName;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getAlliedBankCode() {
		return alliedBankCode;
	}

	public void setAlliedBankCode(String alliedBankCode) {
		this.alliedBankCode = alliedBankCode;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getTradeDesc() {
		return tradeDesc;
	}

	public void setTradeDesc(String tradeDesc) {
		this.tradeDesc = tradeDesc;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getMxReserved() {
		return mxReserved;
	}

	public void setMxReserved(String mxReserved) {
		this.mxReserved = mxReserved;
	}

	public String getCommonExtend() {
		return commonExtend;
	}

	public void setCommonExtend(String commonExtend) {
		this.commonExtend = commonExtend;
	}

	public String getPersistHandling() {
		return persistHandling;
	}

	public void setPersistHandling(String persistHandling) {
		this.persistHandling = persistHandling;
	}

	public String getPersistTimeOut() {
		return persistTimeOut;
	}

	public void setPersistTimeOut(String persistTimeOut) {
		this.persistTimeOut = persistTimeOut;
	}

	public String getSecurityInfo() {
		return securityInfo;
	}

	public void setSecurityInfo(String securityInfo) {
		this.securityInfo = securityInfo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getHmac() {
		return hmac;
	}

	public void setHmac(String hmac) {
		this.hmac = hmac;
	}

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}

	public String getExtend2() {
		return extend2;
	}

	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}

	public String getExtend3() {
		return extend3;
	}

	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}
}

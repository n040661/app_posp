package xdt.dto.mb;

public class MBReqest {

	private String versionId;//版本号
	private String businessType;//交易业务类型
	private String transChanlName;//扫码支付类型
	private String merId;//商户号
	private String orderId;//商户订单号
	private String transDate;//交易日期
	private String transAmount;//交易金额
	private String backNotifyUrl;//后台通知地址
	private String pageNotifyUrl;//前台地址
	private String orderDesc;//订单或者商品详细描述
	private String dev;//商户自定义域
	private String signType;//签名类型
	private String sign;//签名数据
	
	private String url;//
	private String type;//类型
	private String transBody;
	private String accNo;//银行卡号
	private String accName;//持卡人姓名
	private String accType;//卡类型
	private String pmsbankno;//联行号
	
	private String insCode;//机构号
	private String expireDate;//expireDate  年月 2008
	private String CVV;//卡后三位数
	private String bankCode;//银行编码
	private String openBankName;//开户行名称
	private String cerType;//证件类型 01身份证
	private String cerNumber;//身份证号
	private String mobile;//手机号
	private String isAcceptYzm;//00发送
	private String instalTransFlag;//01 不分期00 分期 当交易类型为借记卡时，为01
	private String instalTransNums;//分期次数  当为instalTransFlag为00的时候，此项必填
	private String fee;//空余字段
	private String tiredFlag;//是否使用优惠券0 否  1是
	private String tiredCard;//优惠券码
	private String yzm;//短信
	private String ksPayOrderId;//魔宝订单号
	
	
	
	
	public String getAccType() {
		return accType;
	}
	public void setAccType(String accType) {
		this.accType = accType;
	}
	public String getPageNotifyUrl() {
		return pageNotifyUrl;
	}
	public void setPageNotifyUrl(String pageNotifyUrl) {
		this.pageNotifyUrl = pageNotifyUrl;
	}
	public String getInsCode() {
		return insCode;
	}
	public void setInsCode(String insCode) {
		this.insCode = insCode;
	}
	public String getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	public String getCVV() {
		return CVV;
	}
	public void setCVV(String cVV) {
		CVV = cVV;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getOpenBankName() {
		return openBankName;
	}
	public void setOpenBankName(String openBankName) {
		this.openBankName = openBankName;
	}
	public String getCerType() {
		return cerType;
	}
	public void setCerType(String cerType) {
		this.cerType = cerType;
	}
	public String getCerNumber() {
		return cerNumber;
	}
	public void setCerNumber(String cerNumber) {
		this.cerNumber = cerNumber;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIsAcceptYzm() {
		return isAcceptYzm;
	}
	public void setIsAcceptYzm(String isAcceptYzm) {
		this.isAcceptYzm = isAcceptYzm;
	}
	public String getInstalTransFlag() {
		return instalTransFlag;
	}
	public void setInstalTransFlag(String instalTransFlag) {
		this.instalTransFlag = instalTransFlag;
	}
	public String getInstalTransNums() {
		return instalTransNums;
	}
	public void setInstalTransNums(String instalTransNums) {
		this.instalTransNums = instalTransNums;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getTiredFlag() {
		return tiredFlag;
	}
	public void setTiredFlag(String tiredFlag) {
		this.tiredFlag = tiredFlag;
	}
	public String getTiredCard() {
		return tiredCard;
	}
	public void setTiredCard(String tiredCard) {
		this.tiredCard = tiredCard;
	}
	public String getYzm() {
		return yzm;
	}
	public void setYzm(String yzm) {
		this.yzm = yzm;
	}
	public String getKsPayOrderId() {
		return ksPayOrderId;
	}
	public void setKsPayOrderId(String ksPayOrderId) {
		this.ksPayOrderId = ksPayOrderId;
	}
	public String getPmsbankno() {
		return pmsbankno;
	}
	public void setPmsbankno(String pmsbankno) {
		this.pmsbankno = pmsbankno;
	}
	public String getTransBody() {
		return transBody;
	}
	public void setTransBody(String transBody) {
		this.transBody = transBody;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getAccName() {
		return accName;
	}
	public void setAccName(String accName) {
		this.accName = accName;
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
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getTransChanlName() {
		return transChanlName;
	}
	public void setTransChanlName(String transChanlName) {
		this.transChanlName = transChanlName;
	}
	public String getMerId() {
		return merId;
	}
	public void setMerId(String merId) {
		this.merId = merId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public String getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(String transAmount) {
		this.transAmount = transAmount;
	}
	public String getBackNotifyUrl() {
		return backNotifyUrl;
	}
	public void setBackNotifyUrl(String backNotifyUrl) {
		this.backNotifyUrl = backNotifyUrl;
	}
	public String getOrderDesc() {
		return orderDesc;
	}
	public void setOrderDesc(String orderDesc) {
		this.orderDesc = orderDesc;
	}
	public String getDev() {
		return dev;
	}
	public void setDev(String dev) {
		this.dev = dev;
	}
	public String getSignType() {
		return signType;
	}
	public void setSignType(String signType) {
		this.signType = signType;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}

package xdt.dto.mb;

public class MBResponse {

	private String status;//报文状态
	private String merId;//商户号
	private String businessType;//交易业务类型
	private String transChanlName;//扫码支付类型
	private String ksPayOrderId;//摩宝平台订单号
	private String codeUrl;//二维码源（用于自生成二维码）
	private String codeImgUrl;//二维码图片地址
	private String signData;//签名数据
	private String refCode;//交易返回码
	private String refMsg;//交易返回信息说明
	private String versionId;//版本号
	private String orderId;//订单号
	private String transDate;//交易日期
	private String transAmount;//交易金额
	private String refcode;//订单状态描述
	private String orderDesc;//订单或者商品详细描述
	private String chanlRefCode;//渠道响应码
	private String bankOrderId;//渠道订单号
	private String yzm;//验证码
	private String dev;//签名类型
	private String signType;//签名类型
	
	private String transCurrency; //交易币种
	
	private String openBankName; //开户银行
	
	private String payStatus; //交易返回码
	
	private String payMsg;  //交易返回说明
	
	public String getChanlRefCode() {
		return chanlRefCode;
	}
	public void setChanlRefCode(String chanlRefCode) {
		this.chanlRefCode = chanlRefCode;
	}
	public String getBankOrderId() {
		return bankOrderId;
	}
	public void setBankOrderId(String bankOrderId) {
		this.bankOrderId = bankOrderId;
	}
	public String getYzm() {
		return yzm;
	}
	public void setYzm(String yzm) {
		this.yzm = yzm;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMerId() {
		return merId;
	}
	public void setMerId(String merId) {
		this.merId = merId;
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
	public String getKsPayOrderId() {
		return ksPayOrderId;
	}
	public void setKsPayOrderId(String ksPayOrderId) {
		this.ksPayOrderId = ksPayOrderId;
	}
	public String getCodeUrl() {
		return codeUrl;
	}
	public void setCodeUrl(String codeUrl) {
		this.codeUrl = codeUrl;
	}
	public String getCodeImgUrl() {
		return codeImgUrl;
	}
	public void setCodeImgUrl(String codeImgUrl) {
		this.codeImgUrl = codeImgUrl;
	}
	public String getSignData() {
		return signData;
	}
	public void setSignData(String signData) {
		this.signData = signData;
	}
	public String getRefCode() {
		return refCode;
	}
	public void setRefCode(String refCode) {
		this.refCode = refCode;
	}
	public String getRefMsg() {
		return refMsg;
	}
	public void setRefMsg(String refMsg) {
		this.refMsg = refMsg;
	}
	public String getVersionId() {
		return versionId;
	}
	public void setVersionId(String versionId) {
		this.versionId = versionId;
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
	public String getRefcode() {
		return refcode;
	}
	public void setRefcode(String refcode) {
		this.refcode = refcode;
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
	public String getTransCurrency() {
		return transCurrency;
	}
	public void setTransCurrency(String transCurrency) {
		this.transCurrency = transCurrency;
	}
	public String getOpenBankName() {
		return openBankName;
	}
	public void setOpenBankName(String openBankName) {
		this.openBankName = openBankName;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public String getPayMsg() {
		return payMsg;
	}
	public void setPayMsg(String payMsg) {
		this.payMsg = payMsg;
	}
	
	
}

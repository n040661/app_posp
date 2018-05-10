package xdt.quickpay.daikou.model;

public class DaikouRequsetEntity {

	private String merchantId;// 下游商户号

	private String name;// 姓名

	private String phoneNo;// 手机号
	private String cardNo;// 卡号

	private String idCardNo;// 身份证号
	private String startDate;// 开始时间
	private String endDate;// 结束时间
	private String cycle; // 扣款频率
	private String triesLimit; // 扣款次数限制
	private String orderId;// 订单号
	private String purpose; // 扣款目的
	private String amount; // 金额
	private String responseUrl;// 回调地址
	private String subContractId;// 子协议编号
	private String bankCode; // 银行编码
	private String signAccInfo; // 账户信息
	private String signChnl; // 渠道来源
	private String payProducts; // 工具类型
	private String accType;// 账户类型
	private String accCode;// 账户编码
	private String validity; // 有效期
	private String CCV;// 卡背面末三位数
	private String goodsName;//商品名称
	private String tradeMode;
	private String bizCode;
	private String sign;

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getSubContractId() {
		return subContractId;
	}

	public void setSubContractId(String subContractId) {
		this.subContractId = subContractId;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	public String getTriesLimit() {
		return triesLimit;
	}

	public void setTriesLimit(String triesLimit) {
		this.triesLimit = triesLimit;
	}

	public String getResponseUrl() {
		return responseUrl;
	}

	public void setResponseUrl(String responseUrl) {
		this.responseUrl = responseUrl;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getSignAccInfo() {
		return signAccInfo;
	}

	public void setSignAccInfo(String signAccInfo) {
		this.signAccInfo = signAccInfo;
	}

	public String getSignChnl() {
		return signChnl;
	}

	public void setSignChnl(String signChnl) {
		this.signChnl = signChnl;
	}

	public String getPayProducts() {
		return payProducts;
	}

	public void setPayProducts(String payProducts) {
		this.payProducts = payProducts;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getAccCode() {
		return accCode;
	}

	public void setAccCode(String accCode) {
		this.accCode = accCode;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getCCV() {
		return CCV;
	}

	public void setCCV(String cCV) {
		CCV = cCV;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getTradeMode() {
		return tradeMode;
	}

	public void setTradeMode(String tradeMode) {
		this.tradeMode = tradeMode;
	}

	public String getBizCode() {
		return bizCode;
	}

	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

}

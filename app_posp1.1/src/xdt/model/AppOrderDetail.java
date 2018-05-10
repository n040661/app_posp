package xdt.model;

import java.math.BigDecimal;

public class AppOrderDetail {

	private BigDecimal id;// id

	private String provId;// 省份ID

	private String provName;// 省份名称

	private String cityId;// 城市ID

	private String cityName;// 城市名称

	private String type;// 类型

	private String payProjectName;// 缴费类型名称

	private String chargeCompanyCode;// 缴费单位编码

	private String payUnitName;// 缴费单位名称

	private String cardId;// 水电煤的商品编号

	private String productName;// 商品名称

	private String inprice;// 单价

	private String cardnum;// 充值数量 ，充值数量始终为1

	private String userCode;// 用户编号

	private String account;// 充值账户

	private String accountName;// 用户姓名

	private String contractNo;// 合同号 （查到必传，查不到就不传）

	private String payMentDay;// 账期 (通过queryBalance.do查询到就需要传，查不到就不需要传)

	private String gasCardTel;// 持卡人手机号码

	private String gasCardName;// 持卡人姓名

	private String channelCode;// 渠道编码

	private String channelRemark;// 渠道信息说明

	private String portorderId;// 第三方订单号

	private String orderId;// 订单号

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getProvId() {
		return provId;
	}

	public void setProvId(String provId) {
		this.provId = provId;
	}

	public String getProvName() {
		return provName;
	}

	public void setProvName(String provName) {
		this.provName = provName;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPayProjectName() {
		return payProjectName;
	}

	public void setPayProjectName(String payProjectName) {
		this.payProjectName = payProjectName;
	}

	public String getChargeCompanyCode() {
		return chargeCompanyCode;
	}

	public void setChargeCompanyCode(String chargeCompanyCode) {
		this.chargeCompanyCode = chargeCompanyCode;
	}

	public String getPayUnitName() {
		return payUnitName;
	}

	public void setPayUnitName(String payUnitName) {
		this.payUnitName = payUnitName;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getInprice() {
		return inprice;
	}

	public void setInprice(String inprice) {
		this.inprice = inprice;
	}

	public String getCardnum() {
		return cardnum;
	}

	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getPayMentDay() {
		return payMentDay;
	}

	public void setPayMentDay(String payMentDay) {
		this.payMentDay = payMentDay;
	}

	public String getGasCardTel() {
		return gasCardTel;
	}

	public void setGasCardTel(String gasCardTel) {
		this.gasCardTel = gasCardTel;
	}

	public String getGasCardName() {
		return gasCardName;
	}

	public void setGasCardName(String gasCardName) {
		this.gasCardName = gasCardName;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getChannelRemark() {
		return channelRemark;
	}

	public void setChannelRemark(String channelRemark) {
		this.channelRemark = channelRemark;
	}

	public String getPortorderId() {
		return portorderId;
	}

	public void setPortorderId(String portorderId) {
		this.portorderId = portorderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}

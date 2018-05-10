package xdt.dto;

/**
 * 支付宝和微信公共请求DTO
 * wumeng 20150512
 */
public class WechatAndAlipayRequestDTO {

	private String oAgentNo;//o单编号
	private String totalAmount;//交易金额
	private String orderNo;//订单号6位
	private String paymenttype;//支付类型    支付宝 :012二维码(c2b)、011 付码 (b2c)    微信:  022二维码(c2b)  021付码（b2c）
	private String transType;//交易类型   31：付款码支付（终端主拍）32：用户扫二维码支付（终端被拍）
	private String merInfo;//商户编号
	
	
	
	private String serialNo;//讯联批次号    查询使用
	private String tradeTime;//讯联订单交易时间  查询使用
	private String searchNum;//讯联检索参考号
	 
	 
	
	private String  payCode;//扫码付款使用     用户扫商户
	 
	 
	
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getPaymenttype() {
		return paymenttype;
	}
	public void setPaymenttype(String paymenttype) {
		this.paymenttype = paymenttype;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getMerInfo() {
		return merInfo;
	}
	public void setMerInfo(String merInfo) {
		this.merInfo = merInfo;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public String getSearchNum() {
		return searchNum;
	}
	public void setSearchNum(String searchNum) {
		this.searchNum = searchNum;
	}
	public String getPayCode() {
		return payCode;
	}
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	public String getoAgentNo() {
		return oAgentNo;
	}
	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}
	
}
package xdt.dto;


/**
 * 生成二维码    和确认支付成功   DTO app 提供
 * wumeng 20150506
 */
public class ProducedTwoDimensionDTO {

	//生成二维码使用
	private String  payAmt;          //支付金额
	private String payChannel;       //支付渠道 1微信2支付宝3百度
	private String twoDimensionWay;  //二维护生成方式，1为服务端返回二维码图片，2为服务端返回二维码内容，客户端生成二维护图片
	
	//确认支付成功  使用
	private String orderNumber;//订单号   
	
	//扫码付款使用     用户扫商户
	private String  payCode;
	
	
	
	private String batchNo;//微信  和  支付宝（查询订单使用）
	
	
	
	public String getPayAmt() {
		return payAmt;
	}
	public void setPayAmt(String payAmt) {
		this.payAmt = payAmt;
	}
	public String getPayChannel() {
		return payChannel;
	}
	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}
	public String getTwoDimensionWay() {
		return twoDimensionWay;
	}
	public void setTwoDimensionWay(String twoDimensionWay) {
		this.twoDimensionWay = twoDimensionWay;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getPayCode() {
		return payCode;
	}
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	
}

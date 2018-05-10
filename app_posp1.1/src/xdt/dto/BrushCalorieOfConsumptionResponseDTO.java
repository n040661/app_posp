package xdt.dto;


/**
 * 刷卡消费响应
 * @author xiaomei
 */
public class BrushCalorieOfConsumptionResponseDTO {
   
	private String merchatName; //商户名称
	
	private String payAmount; //支付金额
	
	private String orderNumber; //订单号
	
    private String tradingHours; //交易时间
    
    private String retCode;// 信息编号
	
	private String retMessage;// 信息描述

	public String getMerchatName() {
		return merchatName;
	}

	public void setMerchatName(String merchatName) {
		this.merchatName = merchatName;
	}

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getTradingHours() {
		return tradingHours;
	}

	public void setTradingHours(String tradingHours) {
		this.tradingHours = tradingHours;
	}


	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}

	
    
}
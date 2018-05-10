package xdt.dto;

import xdt.model.PmsAppTransInfo;


/**
 * 商户收款 刷卡收款   服务器返回app
 * wumeng 2015-5-19
 *  
 */
public class PayCardResponseDTO {
	
	//生成订单 //提交订单支付  采用的是相同字段
	private String retCode ;         //返回码 0 生成订单成功  1 生成订单失败 100 系统异常
	private String retMessage;       //返回码信息  0 生成订单成功  1 生成订单失败 100 系统异常
	private String orderNumber;      //	String	订单号
	
	
	
	private PmsAppTransInfo pmsAppTransInfo; //订单类  在第一步条第二步使用
	
	
	
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
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public PmsAppTransInfo getPmsAppTransInfo() {
		return pmsAppTransInfo;
	}
	public void setPmsAppTransInfo(PmsAppTransInfo pmsAppTransInfo) {
		this.pmsAppTransInfo = pmsAppTransInfo;
	}
	
	
	
}

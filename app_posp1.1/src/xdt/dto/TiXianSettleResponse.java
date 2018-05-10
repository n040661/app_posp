package xdt.dto;

/**
 * 提现调用  清算系统返回
 * @author wm
 * 2016-01-27
 */
public class TiXianSettleResponse {
	
	private  String 	retCode      ;         // 信息编号 返回码  0000成功  0097失败  0009正在处理  0083 找不到原交易  
	private  String     retMessage    ;        // 信息描述 返回信息
	private  String     orderNumber   ;        //付呗订单 
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
	
	
}

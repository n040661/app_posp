package xdt.dto.qianlong;

import java.io.Serializable;

/**
 * 
 * @Description 扫码响应信息 
 * @author Shiwen .Li
 * @date 2016年9月11日 下午12:28:08 
 * @version V1.3.1
 */
public class PayResponseEntity implements Serializable {
	
	
	/** @Fields serialVersionUID: */
	  	
	private static final long serialVersionUID = 1L;

	private String transOrderId;//local 流水id
	
	private String orderId;//下游id
	
	private String merchantId;//商户id
	
	private String  respCode;//返回码 00 表示成功 
	
	private String respMsg;//返回消息
	
	private String buyerUser;//（微信 支付宝）用户登陆名称
	
	private String payTime;//支付完成时间


	public String getTransOrderId() {
		return transOrderId;
	}

	public void setTransOrderId(String transOrderId) {
		this.transOrderId = transOrderId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}


	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public String getBuyerUser() {
		return buyerUser;
	}

	public void setBuyerUser(String buyerUser) {
		this.buyerUser = buyerUser;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	
	
	
	

}

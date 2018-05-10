package xdt.dto.pay;

import java.io.Serializable;

/**
 * 快捷支付消费响应
 * 
 * @author tinn
 *
 */
public class ConsumeVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String respCode; // 应答码
	private String respMsg; // 应答信息

	private String requestId; // 请求流水号，合作商户请求的流水号，每次请求保持唯一
	private String orderId; // 商户订单号，商户系统保证唯一
	private String merchantCode; // 商户编号
	private String payNo; // 支付流水号，系统处理响应的流水号
	private String payAmount; // 支付金额，交易金额以分为单位
	private String payDate; // 支付时间
	private Integer orderStatus;
	
	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getRespCode() {
		return respCode;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public String getRequestId() {
		return requestId;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public String getPayNo() {
		return payNo;
	}

	public String getPayAmount() {
		return payAmount;
	}

	public String getPayDate() {
		return payDate;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	@Override
	public String toString() {
		return "ConsumeVo [respCode=" + respCode + ", respMsg=" + respMsg
				+ ", requestId=" + requestId + ", orderId=" + orderId
				+ ", merchantCode=" + merchantCode + ", payNo=" + payNo
				+ ", payAmount=" + payAmount + ", payDate=" + payDate
				+ ", orderStatus=" + orderStatus + "]";
	}

	

}

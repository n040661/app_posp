package xdt.dto.pufa;

import java.io.Serializable;

/**
 * 
 * @Description 退款请求实体
 * @author Shiwen .Li
 * @date 2016年9月18日 下午10:47:34
 * @version V1.3.1
 */
public class RefundRequestEntity implements Serializable {

	/** @Fields serialVersionUID: */

	private static final long serialVersionUID = 1L;

	private String orderId;// 订单id
	
	private String origOrderId;// 原始订单id

	private String merchantId;// 商户号

	private String tranAmt;// 退款金额

	private String refundReason;// 退款原因
	
	private String payType;//支付类型
	
	private String transTime;//交易时间
	
	private String sign;//签名字符串

	

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}



	public String getOrigOrderId() {
		return origOrderId;
	}

	public void setOrigOrderId(String origOrderId) {
		this.origOrderId = origOrderId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}


	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	@Override
	public String toString() {
		return "RefundRequestEntity [ origOrderId="
				+ origOrderId + ", merchantId=" + merchantId + ", trantAmt="
				+ tranAmt + ", refundReason=" + refundReason + "]";
	}

	public String getTranAmt() {
		return tranAmt;
	}

	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}


}

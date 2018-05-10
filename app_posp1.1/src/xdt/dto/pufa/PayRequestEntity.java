package xdt.dto.pufa;

import java.io.Serializable;

/**
 * 
 * @Description 扫码请求实体 
 * @author Shiwen .Li
 * @date 2016年9月11日 下午12:08:55 
 * @version V1.3.1
 */
public class PayRequestEntity implements Serializable{

	
	/** @Fields serialVersionUID: */
	  	
	private static final long serialVersionUID = 1L;
	
	
	private String orderId;//订单id
	
	private String merchantId;//商户号\
	
	private String authCode;//扫码设备读取
	
	private String tranAmt;//交易金额
	
	private String transTime;//交易时间
	
	private String payType;//0 支付宝 1微信
	
	private String sign;
	
	
	

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
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

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}


	public String getTranAmt() {
		return tranAmt;
	}

	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "PayRequestEntity [orderId=" + orderId + ", merchantId="
				+ merchantId + ", authCode=" + authCode + ", tranAmt="
				+ tranAmt + ", transTime=" + transTime + "]";
	}
	
}

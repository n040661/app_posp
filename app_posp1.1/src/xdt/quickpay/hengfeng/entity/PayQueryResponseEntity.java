package xdt.quickpay.hengfeng.entity;

/**
 * @ClassName: PayQueryResponseEntity
 * @Description: 恒丰查询响应信息
 * @author LiShiwen
 * @date 2016年6月16日 上午10:13:56
 *
 */
public class PayQueryResponseEntity {

	private String responseCode;//应答码0000查询成功其余失败
	
	private String msg;//应答描述
	
	private String merId;//供货商商户号
	
	private String transactionId;//商户订单号
	
	private String orderTime;//提交订单时间(yyyyMMddHHmmss)
	
	private String orderAmount;//付款金额（单位分）
	
	private String transStatus;//订单状态0未支付1成功2失败
	
	private String signData;//签名串

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getTransStatus() {
		return transStatus;
	}

	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}

	public String getSignData() {
		return signData;
	}

	public void setSignData(String signData) {
		this.signData = signData;
	}

	@Override
	public String toString() {
		return "PayQueryResponseEntity [responseCode=" + responseCode + ", msg=" + msg + ", merId=" + merId
				+ ", transactionId=" + transactionId + ", orderTime=" + orderTime + ", orderAmount=" + orderAmount
				+ ", transStatus=" + transStatus + ", signData=" + signData + "]";
	}
	
	
	
}

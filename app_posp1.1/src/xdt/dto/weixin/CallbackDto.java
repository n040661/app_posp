package xdt.dto.weixin;

public class CallbackDto extends BaseDto {

	
	/** @Fields serialVersionUID: */
	  	
	private static final long serialVersionUID = 1L;

	private String orderId;//订单
	
	private String respCode;//响应码
	
	private String respInfo;//响应信息
	
	private String WXOrderNo;//微信订单号

	private String amount;//订单金额
	

	public String getWXOrderNo() {
		return WXOrderNo;
	}

	public void setWXOrderNo(String wXOrderNo) {
		WXOrderNo = wXOrderNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespInfo() {
		return respInfo;
	}

	public void setRespInfo(String respInfo) {
		this.respInfo = respInfo;
	}

	@Override
	public String toString() {
		return "CallbackDto [orderId=" + orderId + ", respCode=" + respCode
				+ ", respInfo=" + respInfo + ", WXOrderNo=" + WXOrderNo
				+ ", amount=" + amount + "]";
	}
	
	
}

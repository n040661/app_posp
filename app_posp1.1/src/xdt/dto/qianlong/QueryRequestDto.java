package xdt.dto.qianlong;
public class QueryRequestDto extends BaseDto {
	
	/** @Fields serialVersionUID: */
	  	
	private static final long serialVersionUID = 1L;
	// 账号
	private String phone ;
	// 恒丰返回订单号
	private String pos_platOrderId ;
	
	private String orderNo;// 商户订单号
	
	
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPos_platOrderId() {
		return pos_platOrderId;
	}
	public void setPos_platOrderId(String pos_platOrderId) {
		this.pos_platOrderId = pos_platOrderId;
	}
	@Override
	public String toString() {
		return "QueryRequestDto [phone=" + phone + ", pos_platOrderId="
				+ pos_platOrderId + "]";
	}
	
	
}

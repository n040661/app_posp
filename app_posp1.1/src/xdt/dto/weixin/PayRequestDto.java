package xdt.dto.weixin;


public class PayRequestDto extends BaseDto{

	/** @Fields serialVersionUID: */

	private static final long serialVersionUID = 1L;

	private String orderNo;// 商户订单号
	private String account;// 手机号
	private String totalFee;// 金额 单位分
	private String notify_url;//下游回调地址


	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	@Override
	public String toString() {
		return "PayRequestDto [orderNo=" + orderNo + ", account=" + account + ", totalFee=" + totalFee + ", notify_url="
				+ notify_url + "]";
	}

	

}

package xdt.dto.weixin;

public class ChangeRateDto extends BaseDto {
	
	/** @Fields serialVersionUID: */
	  	
	private static final long serialVersionUID = 1L;
	// 账号
	private String phone;
	// 费率
	private String rate;
	
	private String wxRate;
	
	private String alipayRate;
	

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getWxRate() {
		return wxRate;
	}

	public void setWxRate(String wxRate) {
		this.wxRate = wxRate;
	}

	public String getAlipayRate() {
		return alipayRate;
	}

	public void setAlipayRate(String alipayRate) {
		this.alipayRate = alipayRate;
	}

	@Override
	public String toString() {
		return "ChangeRateDto [phone=" + phone + ", rate=" + rate + "]";
	}

}

package xdt.dto;

/**
 * 中石化加油卡卡号信息查询接口请求
 * 
 * @author lev12
 * 
 */
public class SinopecCardRequestDTO {

	private String mobilePhone;// 手机号

	private String cardId;// 加油卡卡号

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

}

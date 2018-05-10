package xdt.dto;

/**
 * 水电煤账户欠费查询接口请求
 * 
 * @author lev12
 * 
 */
public class QueryBalanceRequestDTO {

	private String provName;// 省份名称

	private String cityName;// 城市名称

	private String type;// 类型

	private String chargeCompanyCode;// 缴费单位编码

	private String chargeCompanyName;// 缴费单位名称

	private String account;// 水电煤充值账户

	private String cardId;// 水电煤的商品编号

	public String getProvName() {
		return provName;
	}

	public void setProvName(String provName) {
		this.provName = provName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChargeCompanyCode() {
		return chargeCompanyCode;
	}

	public void setChargeCompanyCode(String chargeCompanyCode) {
		this.chargeCompanyCode = chargeCompanyCode;
	}

	public String getChargeCompanyName() {
		return chargeCompanyName;
	}

	public void setChargeCompanyName(String chargeCompanyName) {
		this.chargeCompanyName = chargeCompanyName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

}

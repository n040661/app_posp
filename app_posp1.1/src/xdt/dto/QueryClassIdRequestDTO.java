package xdt.dto;

/**
 * 水煤电商品信息查询接口请求
 * 
 * @author lev12
 * 
 */
public class QueryClassIdRequestDTO {

	private String provId;// 省份ID

	private String cityId;// 城市ID

	private String type;// 缴费类型编号

	private String chargeCompanyCode;// 缴费单位编码

	public String getProvId() {
		return provId;
	}

	public void setProvId(String provId) {
		this.provId = provId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
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

}

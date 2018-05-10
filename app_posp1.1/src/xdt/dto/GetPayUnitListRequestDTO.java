package xdt.dto;

/**
 * 水煤电缴费单位查询接口请求
 * 
 * @author lev12
 * 
 */
public class GetPayUnitListRequestDTO {

	private String provinceId;// 省份ID

	private String cityId;// 城市ID

	private String payProjectId;// 缴费类型编号

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getPayProjectId() {
		return payProjectId;
	}

	public void setPayProjectId(String payProjectId) {
		this.payProjectId = payProjectId;
	}

}

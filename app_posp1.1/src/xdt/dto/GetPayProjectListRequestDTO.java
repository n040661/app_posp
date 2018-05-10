package xdt.dto;

/**
 * 水煤电充值类型查询接口请求
 * 
 * @author lev12
 * 
 */
public class GetPayProjectListRequestDTO {

	private String provinceId;// 省份ID

	private String cityId;// 城市ID

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

}

package xdt.dto;

/**
 * 城市查询接口请求
 * 
 * @author lev12
 * 
 */
public class GetCityListRequestDTO {

	private String provinceId;// 省份ID

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

}

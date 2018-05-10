package xdt.dto;

/**
 * 缴费详情查看接口请求
 * 
 * @author lev12
 * 
 */
public class QueryPayDetailRequestDTO {

	private String provinceId;// 省份编号

	private String provinceName;// 省份名称

	private String cityId;// 城市编号

	private String cityName;// 城市名称

	private String payProjectId;// 缴费项目编号

	private String payUnitId;// 缴费单位编号

	private String payUnitName;// 缴费单位名称

	private String clientId;// 客户编号

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getPayProjectId() {
		return payProjectId;
	}

	public void setPayProjectId(String payProjectId) {
		this.payProjectId = payProjectId;
	}

	public String getPayUnitId() {
		return payUnitId;
	}

	public void setPayUnitId(String payUnitId) {
		this.payUnitId = payUnitId;
	}

	public String getPayUnitName() {
		return payUnitName;
	}

	public void setPayUnitName(String payUnitName) {
		this.payUnitName = payUnitName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}

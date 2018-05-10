package xdt.model;

public class PayUnit {

	private String payUnitId;// 缴费单位编码

	private String payUnitName;// 缴费单位名称

	private String provinceId;// 省份编号

	private String cityId;// 城市编号

	private String payProjectId;// 缴费类型编号

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

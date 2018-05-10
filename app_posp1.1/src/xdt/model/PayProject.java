package xdt.model;

public class PayProject {
	private String payProjectId;// 缴费类型编号
	private String payProjectName;// 缴费类型名称
	private String provinceId;// 省份编号
	private String cityId;// 城市编号

	public String getPayProjectId() {
		return payProjectId;
	}

	public void setPayProjectId(String payProjectId) {
		this.payProjectId = payProjectId;
	}

	public String getPayProjectName() {
		return payProjectName;
	}

	public void setPayProjectName(String payProjectName) {
		this.payProjectName = payProjectName;
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

}

package xdt.dto;

import java.util.List;

import xdt.model.City;

/**
 * 城市查询接口响应
 * 
 * @author lev12
 * 
 */
public class GetCityListResponseDTO {

	private Integer retCode;// 操作返回代码，1成功,err_msg为空，其它数字具体错误在err_msg返回

	private String retMessage;// 错误描述，如请求得到正确返回，此处将为空

	private List<City> cityList;// 城市列表

	public Integer getRetCode() {
		return retCode;
	}

	public void setRetCode(Integer retCode) {
		this.retCode = retCode;
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}

	public List<City> getCityList() {
		return cityList;
	}

	public void setCityList(List<City> cityList) {
		this.cityList = cityList;
	}

}

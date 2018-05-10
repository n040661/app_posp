package xdt.dto;

import java.util.List;

import xdt.model.PmsAddress;

/**
 * 收货地址列表查看接口响应
 * 
 * @author lev12
 * 
 */
public class QueryAddressListResponseDTO {

	private Integer retCode;// 操作返回代码

	private String retMessage;// 返回码信息 0成功1 失败100 系统异常

	private List<PmsAddress> pmsAddressList;// 收货地址列表

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

	public List<PmsAddress> getPmsAddressList() {
		return pmsAddressList;
	}

	public void setPmsAddressList(List<PmsAddress> pmsAddressList) {
		this.pmsAddressList = pmsAddressList;
	}

}

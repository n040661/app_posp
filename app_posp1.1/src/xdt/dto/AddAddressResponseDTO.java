package xdt.dto;

/**
 * 增加收货地址接口响应
 * 
 * @author lev12
 * 
 */
public class AddAddressResponseDTO {

	private Integer retCode;// 操作返回代码

	private String retMessage;// 返回码信息 0成功1 失败100 系统异常

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

}

package xdt.dto;

/**
 * 缴费详情查看接口响应
 * 
 * @author lev12
 * 
 */
public class QueryPayDetailResponseDTO {

	private Integer retCode;// 返回码

	private String retMessage;// 返回码信息 0成功 1 失败 100 系统异常

	private String clientId;// 客户编号

	private String clientName;// 客户名称

	private String mustPayAmt;// 应缴费金额

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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getMustPayAmt() {
		return mustPayAmt;
	}

	public void setMustPayAmt(String mustPayAmt) {
		this.mustPayAmt = mustPayAmt;
	}

}

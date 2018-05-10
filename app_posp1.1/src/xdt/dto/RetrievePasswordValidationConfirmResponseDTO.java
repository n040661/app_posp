package xdt.dto;
/**
 * 找回密码验证确认接口响应
 * @author lev12
 *
 */
public class RetrievePasswordValidationConfirmResponseDTO {
	
    private Integer retCode;// 信息编号
	
	private String retMessage;// 信息描述
	
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

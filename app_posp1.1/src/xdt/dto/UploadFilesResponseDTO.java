package xdt.dto;
/**
 * 公共方法
 * @author p
 *
 */
public class UploadFilesResponseDTO {
	
    private Integer retCode;// 信息编号
	
	private String retMessage;// 信息描述
	
	private String authPath;// 文件路径
	
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
	
	public String getAuthPath() {
		return authPath;
	}

	public void setAuthPath(String authPath) {
		this.authPath = authPath;
	}
	
}

package xdt.dto.pay;

import java.io.Serializable;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年1月26日 下午4:15:45 
* 类说明 
*/
public class Bill implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private String respCode; // 应答码
	private String respMsg; // 应答信息
	private String serverProviderCode; 
	private String totalNumber; 
	private String totalAmount; 
	private String excelText;
	
	
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public String getServerProviderCode() {
		return serverProviderCode;
	}
	public void setServerProviderCode(String serverProviderCode) {
		this.serverProviderCode = serverProviderCode;
	}
	public String getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(String totalNumber) {
		this.totalNumber = totalNumber;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getExcelText() {
		return excelText;
	}
	public void setExcelText(String excelText) {
		this.excelText = excelText;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	} 
	
}

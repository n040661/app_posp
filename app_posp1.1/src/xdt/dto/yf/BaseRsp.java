package xdt.dto.yf;

import java.io.Serializable;

public class BaseRsp implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2272824568886958898L;
	
	private String respCode;
	private String respDesc;
	
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	
}

package xdt.dto;

public class MerchantsFeedbackRequestAndResponseDTO {

	private String retCode;//是否成功
    private String retMessage;//信息描述
    
    private String opinon;//意见

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}

	public String getOpinon() {
		return opinon;
	}

	public void setOpinon(String opinon) {
		this.opinon = opinon;
	}
	
}

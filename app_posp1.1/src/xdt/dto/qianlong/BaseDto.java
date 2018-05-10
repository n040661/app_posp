package xdt.dto.qianlong;

import java.io.Serializable;

public class BaseDto implements Serializable {
	
	/** @Fields serialVersionUID: */
	  	
	private static final long serialVersionUID = 1L;
	
	private String merchId;//商户号

	public String getMerchId() {
		return merchId;
	}

	public void setMerchId(String merchId) {
		this.merchId = merchId;
	}

	@Override
	public String toString() {
		return "BaseDto [merchId=" + merchId + "]";
	}
	
}

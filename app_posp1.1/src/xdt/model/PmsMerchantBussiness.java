package xdt.model;

import java.math.BigDecimal;

public class PmsMerchantBussiness {
    private BigDecimal id;

    private String businessCode;

    private String mercId;
    
    private String status;
    
    private String oAgentNo;

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	public String getMercId() {
		return mercId;
	}

	public void setMercId(String mercId) {
		this.mercId = mercId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

	public PmsMerchantBussiness(String businessCode,
			String mercId, String status, String oAgentNo) {
		super();
		this.businessCode = businessCode;
		this.mercId = mercId;
		this.status = status;
		this.oAgentNo = oAgentNo;
	}
	
}
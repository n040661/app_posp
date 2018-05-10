package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

public class Y2e0020ResDetail {	
	@Y2eField(path = "batch-no")
	private String batchNo;
	
	@Y2eField(path = "buss-name")
	private String bussName;
	
	@Y2eField(path = "batch-name")
	private String batchName;
	
	@Y2eField(path = "procedure-type")
	private String procedureType;
	
	@Y2eField(path = "pay-tm")
	private String payTm;
	
	@Y2eField(path = "merinsid ")
	private String merinsid ;
	
	@Y2eField(path ="pay-type")
	private String payType;
	
	@Y2eField(path ="bank-no")
	private String bankNo;

	@Y2eField(path ="real-name")
	private String realName;
	
	@Y2eField(path ="bank-name")
	private String bankName; 
	
	@Y2eField(path ="inter-bankno")
	private String interBankNo;
	
	@Y2eField(path ="pay-fee")
	private String payFee;
	
	@Y2eField(path ="status")
	private String status;
	
	@Y2eField(path ="fur-info")
	private String furInfo;
	
	
	public String getBussName() {
		return bussName;
	}

	public void setBussName(String bussName) {
		this.bussName = bussName;
	}
	
	public String getPayTm() {
		return payTm;
	}

	public void setPayTm(String payTm) {
		this.payTm = payTm;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(String procedureType) {
		this.procedureType = procedureType;
	}


	public String getMerinsid() {
		return merinsid;
	}

	public void setMerinsid(String merinsid) {
		this.merinsid = merinsid;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getInterBankNo() {
		return interBankNo;
	}

	public void setInterBankNo(String interBankNo) {
		this.interBankNo = interBankNo;
	}

	public String getPayFee() {
		return payFee;
	}

	public void setPayFee(String payFee) {
		this.payFee = payFee;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFurInfo() {
		return furInfo;
	}

	public void setFurInfo(String furInfo) {
		this.furInfo = furInfo;
	}
}

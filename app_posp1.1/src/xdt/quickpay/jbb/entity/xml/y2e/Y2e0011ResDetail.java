package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

public class Y2e0011ResDetail {	
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

}



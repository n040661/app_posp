package xdt.model;

import java.math.BigDecimal;

public class PmsAgentInfo {
    private BigDecimal agentId;

    private String agentNumber;

    private String agentName;

    private String agentCreatePerson;

    private String agentCreateDate;

    private String agentType;

    private String agentLevel;

    private BigDecimal agentStatus;

    private String agentAddress;

    private String registAddress;

    private String registType;

    private String registMoney;

    private String organCode;

    private String licenseIssueDate;

    private String licenseMaturityDate;

    private String businessLicenseNumber;

    private String accountBank;

    private String accountName;

    private String bankAccountNumber;

    private String transactionSystemNumber;

    private String corporationName;

    private String corporationPhone;

    private String corporationLandline;

    private String corporationIdenNumber;

    private String customArea;

    private BigDecimal removetag;

    private String userNo;

    private String kek1;

    private String kek2;

    private String kek3;

    private String limitIp;

    private String kyaccount;

    private BigDecimal parentid;

    private String copyAgentNumber;
    
    private String oAgentNo;// O单编号

    private String clearType;//清算类型 ：0:T+0;  1:T+1;  2:T+N

    private String lowestSettleRate;//最低清分费率
    
    private String lowestRate;//最低费率
    
	public String getLowestRate() {
		return lowestRate;
	}

	public void setLowestRate(String lowestRate) {
		this.lowestRate = lowestRate;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

    public BigDecimal getAgentId() {
        return agentId;
    }

    public void setAgentId(BigDecimal agentId) {
        this.agentId = agentId;
    }

    public String getAgentNumber() {
        return agentNumber;
    }

    public void setAgentNumber(String agentNumber) {
        this.agentNumber = agentNumber == null ? null : agentNumber.trim();
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName == null ? null : agentName.trim();
    }

    public String getAgentCreatePerson() {
        return agentCreatePerson;
    }

    public void setAgentCreatePerson(String agentCreatePerson) {
        this.agentCreatePerson = agentCreatePerson == null ? null : agentCreatePerson.trim();
    }

    public String getAgentCreateDate() {
        return agentCreateDate;
    }

    public void setAgentCreateDate(String agentCreateDate) {
        this.agentCreateDate = agentCreateDate == null ? null : agentCreateDate.trim();
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType == null ? null : agentType.trim();
    }

    public String getAgentLevel() {
        return agentLevel;
    }

    public void setAgentLevel(String agentLevel) {
        this.agentLevel = agentLevel == null ? null : agentLevel.trim();
    }

    public BigDecimal getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(BigDecimal agentStatus) {
        this.agentStatus = agentStatus;
    }

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress == null ? null : agentAddress.trim();
    }

    public String getRegistAddress() {
        return registAddress;
    }

    public void setRegistAddress(String registAddress) {
        this.registAddress = registAddress == null ? null : registAddress.trim();
    }

    public String getRegistType() {
        return registType;
    }

    public void setRegistType(String registType) {
        this.registType = registType == null ? null : registType.trim();
    }

    public String getRegistMoney() {
        return registMoney;
    }

    public void setRegistMoney(String registMoney) {
        this.registMoney = registMoney == null ? null : registMoney.trim();
    }

    public String getOrganCode() {
        return organCode;
    }

    public void setOrganCode(String organCode) {
        this.organCode = organCode == null ? null : organCode.trim();
    }

    public String getLicenseIssueDate() {
        return licenseIssueDate;
    }

    public void setLicenseIssueDate(String licenseIssueDate) {
        this.licenseIssueDate = licenseIssueDate == null ? null : licenseIssueDate.trim();
    }

    public String getLicenseMaturityDate() {
        return licenseMaturityDate;
    }

    public void setLicenseMaturityDate(String licenseMaturityDate) {
        this.licenseMaturityDate = licenseMaturityDate == null ? null : licenseMaturityDate.trim();
    }

    public String getBusinessLicenseNumber() {
        return businessLicenseNumber;
    }

    public void setBusinessLicenseNumber(String businessLicenseNumber) {
        this.businessLicenseNumber = businessLicenseNumber == null ? null : businessLicenseNumber.trim();
    }

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank == null ? null : accountBank.trim();
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName == null ? null : accountName.trim();
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber == null ? null : bankAccountNumber.trim();
    }

    public String getTransactionSystemNumber() {
        return transactionSystemNumber;
    }

    public void setTransactionSystemNumber(String transactionSystemNumber) {
        this.transactionSystemNumber = transactionSystemNumber == null ? null : transactionSystemNumber.trim();
    }

    public String getCorporationName() {
        return corporationName;
    }

    public void setCorporationName(String corporationName) {
        this.corporationName = corporationName == null ? null : corporationName.trim();
    }

    public String getCorporationPhone() {
        return corporationPhone;
    }

    public void setCorporationPhone(String corporationPhone) {
        this.corporationPhone = corporationPhone == null ? null : corporationPhone.trim();
    }

    public String getCorporationLandline() {
        return corporationLandline;
    }

    public void setCorporationLandline(String corporationLandline) {
        this.corporationLandline = corporationLandline == null ? null : corporationLandline.trim();
    }

    public String getCorporationIdenNumber() {
        return corporationIdenNumber;
    }

    public void setCorporationIdenNumber(String corporationIdenNumber) {
        this.corporationIdenNumber = corporationIdenNumber == null ? null : corporationIdenNumber.trim();
    }

    public String getCustomArea() {
        return customArea;
    }

    public void setCustomArea(String customArea) {
        this.customArea = customArea == null ? null : customArea.trim();
    }

    public BigDecimal getRemovetag() {
        return removetag;
    }

    public void setRemovetag(BigDecimal removetag) {
        this.removetag = removetag;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo == null ? null : userNo.trim();
    }

    public String getKek1() {
        return kek1;
    }

    public void setKek1(String kek1) {
        this.kek1 = kek1 == null ? null : kek1.trim();
    }

    public String getKek2() {
        return kek2;
    }

    public void setKek2(String kek2) {
        this.kek2 = kek2 == null ? null : kek2.trim();
    }

    public String getKek3() {
        return kek3;
    }

    public void setKek3(String kek3) {
        this.kek3 = kek3 == null ? null : kek3.trim();
    }

    public String getLimitIp() {
        return limitIp;
    }

    public void setLimitIp(String limitIp) {
        this.limitIp = limitIp == null ? null : limitIp.trim();
    }

    public String getKyaccount() {
        return kyaccount;
    }

    public void setKyaccount(String kyaccount) {
        this.kyaccount = kyaccount == null ? null : kyaccount.trim();
    }

    public BigDecimal getParentid() {
        return parentid;
    }

    public void setParentid(BigDecimal parentid) {
        this.parentid = parentid;
    }

    public String getCopyAgentNumber() {
        return copyAgentNumber;
    }

    public void setCopyAgentNumber(String copyAgentNumber) {
        this.copyAgentNumber = copyAgentNumber == null ? null : copyAgentNumber.trim();
    }

    public String getClearType() {
        return clearType;
    }

    public void setClearType(String clearType) {
        this.clearType = clearType;
    }

    public String getLowestSettleRate() {
        return lowestSettleRate;
    }

    public void setLowestSettleRate(String lowestSettleRate) {
        this.lowestSettleRate = lowestSettleRate;
    }

}
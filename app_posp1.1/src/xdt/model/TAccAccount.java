package xdt.model;

import java.math.BigDecimal;
import java.util.Date;

public class TAccAccount {
    private BigDecimal id;//id

    private String accNum;//帐户  对应 ky_account

    private BigDecimal balance;//余额

    private BigDecimal lastBalance;//上次余额

    private Date accountTime;//开户时间

    private String status;//0=初始，1=启用，2=冻结，3=停用

    private BigDecimal isCredit;

    private BigDecimal creditLimit;//信用额度

    private BigDecimal freezeBalance;//冻结余额

    private BigDecimal lastFreezeBalance;//上次冻结余额

    private BigDecimal accLevel; //帐户等级  1 是系统级别  2是系统级别  3是普通级别

    private Date modifiedTime;//修改时间

    private String remark;//备注

    private String name;//持卡人

    private String accType;//1.商户账户 2.代理商账户 3.销售员账户

    private String clearResult;

    private String errMsg;//结算错误信息

    private String oAgentNo;//欧单编号
    
    public BigDecimal getIsCredit() {
		return isCredit;
	}

	public void setIsCredit(BigDecimal isCredit) {
		this.isCredit = isCredit;
	}

	public BigDecimal getAccLevel() {
		return accLevel;
	}

	public void setAccLevel(BigDecimal accLevel) {
		this.accLevel = accLevel;
	}

	public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getAccNum() {
        return accNum;
    }

    public void setAccNum(String accNum) {
        this.accNum = accNum == null ? null : accNum.trim();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getLastBalance() {
        return lastBalance;
    }

    public void setLastBalance(BigDecimal lastBalance) {
        this.lastBalance = lastBalance;
    }

    public Date getAccountTime() {
        return accountTime;
    }

    public void setAccountTime(Date accountTime) {
        this.accountTime = accountTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }


    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getFreezeBalance() {
        return freezeBalance;
    }

    public void setFreezeBalance(BigDecimal freezeBalance) {
        this.freezeBalance = freezeBalance;
    }

    public BigDecimal getLastFreezeBalance() {
        return lastFreezeBalance;
    }

    public void setLastFreezeBalance(BigDecimal lastFreezeBalance) {
        this.lastFreezeBalance = lastFreezeBalance;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType == null ? null : accType.trim();
    }

    public String getClearResult() {
        return clearResult;
    }

    public void setClearResult(String clearResult) {
        this.clearResult = clearResult == null ? null : clearResult.trim();
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg == null ? null : errMsg.trim();
    }

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }
}
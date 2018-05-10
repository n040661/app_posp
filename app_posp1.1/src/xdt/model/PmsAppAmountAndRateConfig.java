package xdt.model;

public class PmsAppAmountAndRateConfig {
	private String mercId;     //商户编号

    private String businesscode;  //业务编码    （ 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、7 提款（提现））
    
    private String minAmount;  // 每笔最小金额 （分）

    private String maxAmount;    // 每笔最大金额（分）

    private String status;     // 状态 0 有效 1 无效

    private String accountTime; //  到账时间

    private String accountType;   //  0 t+0 1 t+1

    private String numberoftimes;  // 操作次数

    private String description;    // 描述

    private String ratetype;    // 费率类型  关联 app_rate_config的ratetype字段
    
    private String createTime;//创建时间   格式yyyy-MM-dd HH:mm:ss
    
    private String modifyTime;//修改时间   格式yyyy-MM-dd HH:mm:ss
    
    private String modifyUser;//修改人

    private String oAgentNo;//欧单编号

    private String message;

	private String quickRateType;//快捷费率类型

    public String getQuickRateType() {
		return quickRateType;
	}

	public void setQuickRateType(String quickRateType) {
		this.quickRateType = quickRateType;
	}
    
    public String getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(String minAmount) {
        this.minAmount = minAmount == null ? null : minAmount.trim();
    }

    public String getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(String maxAmount) {
        this.maxAmount = maxAmount == null ? null : maxAmount.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getAccountTime() {
        return accountTime;
    }

    public void setAccountTime(String accountTime) {
        this.accountTime = accountTime == null ? null : accountTime.trim();
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType == null ? null : accountType.trim();
    }

    public String getNumberoftimes() {
        return numberoftimes;
    }

    public void setNumberoftimes(String numberoftimes) {
        this.numberoftimes = numberoftimes == null ? null : numberoftimes.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getRatetype() {
        return ratetype;
    }

    public void setRatetype(String ratetype) {
        this.ratetype = ratetype == null ? null : ratetype.trim();
    }

	public String getMercId() {
		return mercId;
	}

	public void setMercId(String mercId) {
		this.mercId = mercId;
	}

	public String getBusinesscode() {
		return businesscode;
	}

	public void setBusinesscode(String businesscode) {
		this.businesscode = businesscode;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
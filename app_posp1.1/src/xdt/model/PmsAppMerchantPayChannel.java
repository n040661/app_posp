package xdt.model;

/**
 * 商户支付通道表
 */
public class PmsAppMerchantPayChannel {
    
	private String mercId;//商户编号

    private String businesscode;//业务编号    1 商户收款、2 转账汇款、3 信用卡还款

    private String paymentcode;//支付方式 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付、5刷卡支付、6移动和包支付、0其它
	
    private String status;//状态 0 有效 1 无效 

    private String createtime;//创建时间  格式yyyy-MM-dd HH:mm:ss

    private String modifytime;//修改时间  格式yyyy-MM-dd HH:mm:ss 

    private String modifyuser;//修改人
    
    private String describe;//描述    1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付、5刷卡支付、6移动和包支付、0其它

    private String oAgentNo;//欧单编号

    private String reason;//关闭原因
    
    private String openPay;//是否开通代付 0开通1未开通
	
	private String poundage;//代付手续费 元
	
	private String poundageFree;//代付手续费率%
	
	private String quickRateTypeT1;//费率类型T1
	
	private String quickRateTypeD0;//费率类型D0
	
	private String counter;//最低手续费
	
	private String clearType;//清算类型0 D0 1 T1
	
	
	
    public String getOpenPay() {
		return openPay;
	}

	public void setOpenPay(String openPay) {
		this.openPay = openPay;
	}

	public String getPoundage() {
		return poundage;
	}

	public void setPoundage(String poundage) {
		this.poundage = poundage;
	}

	public String getPoundageFree() {
		return poundageFree;
	}

	public void setPoundageFree(String poundageFree) {
		this.poundageFree = poundageFree;
	}

	public String getQuickRateTypeT1() {
		return quickRateTypeT1;
	}

	public void setQuickRateTypeT1(String quickRateTypeT1) {
		this.quickRateTypeT1 = quickRateTypeT1;
	}

	public String getQuickRateTypeD0() {
		return quickRateTypeD0;
	}

	public void setQuickRateTypeD0(String quickRateTypeD0) {
		this.quickRateTypeD0 = quickRateTypeD0;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	public String getClearType() {
		return clearType;
	}

	public void setClearType(String clearType) {
		this.clearType = clearType;
	}

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime == null ? null : createtime.trim();
    }

    public String getModifytime() {
        return modifytime;
    }

    public void setModifytime(String modifytime) {
        this.modifytime = modifytime == null ? null : modifytime.trim();
    }

    public String getModifyuser() {
        return modifyuser;
    }

    public void setModifyuser(String modifyuser) {
        this.modifyuser = modifyuser == null ? null : modifyuser.trim();
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

	public String getPaymentcode() {
		return paymentcode;
	}

	public void setPaymentcode(String paymentcode) {
		this.paymentcode = paymentcode;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
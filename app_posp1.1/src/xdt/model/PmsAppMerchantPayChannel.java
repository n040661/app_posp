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
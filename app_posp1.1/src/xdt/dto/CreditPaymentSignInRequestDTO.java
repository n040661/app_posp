package xdt.dto;
/**
 * 刷卡支付 签到请求(第三方)
 * @author p
 *
 */
public class CreditPaymentSignInRequestDTO {
    private String dealType;//消息类型
    
    private String merPos;//41域 通道POS的信息 8位  //这个41里 a.posbusinessno 试
    
    private String merInfo;//42域 通道商户的信息  15位 //42也是 merc_id 244193370717935 
    
    private String terminalSN;//40域 sn号
    
    private String phone;//手机号

    private String oAgentNo;//欧单编号

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTerminalSN() {
		return terminalSN;
	}

	public void setTerminalSN(String terminalSN) {
		this.terminalSN = terminalSN;
	}

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public String getMerPos() {
		return merPos;
	}

	public void setMerPos(String merPos) {
		this.merPos = merPos;
	}

	public String getMerInfo() {
		return merInfo;
	}

	public void setMerInfo(String merInfo) {
		this.merInfo = merInfo;
	}

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }
}

package xdt.dto.qianlong;

public class ValidateSign {
	
	private String amount;//订单金额
	
	private String orderDt;//订单日期
	
	private String orderNo;//平台订单号
	
	private String orgOrderNo;//机构订单号
	
	private String orgId;//所属机构
	
	private String paySt;//支付状态
	
	private String fee;//手续费
	
	private String signature;//签名
	
	private String subject;//订单标题
	
	private String respMsg;//应答码描述
	
	private String account;//所属商户账户
	
	private String respCode;//应答码
	
	private String description;
	
	private String body;
	
	private String extra;

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrderDt() {
		return orderDt;
	}

	public void setOrderDt(String orderDt) {
		this.orderDt = orderDt;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrgOrderNo() {
		return orgOrderNo;
	}

	public void setOrgOrderNo(String orgOrderNo) {
		this.orgOrderNo = orgOrderNo;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getPaySt() {
		return paySt;
	}

	public void setPaySt(String paySt) {
		this.paySt = paySt;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
	public String toString() {
		return "ValidateSign [amount=" + amount + ", orderDt=" + orderDt
				+ ", orderNo=" + orderNo + ", orgOrderNo=" + orgOrderNo
				+ ", orgId=" + orgId + ", paySt=" + paySt + ", fee=" + fee
				+ ", signature=" + signature + ", subject=" + subject
				+ ", respMsg=" + respMsg + ", account=" + account
				+ ", respCode=" + respCode + ", description=" + description
				+ ", body=" + body + ", extra=" + extra + "]";
	} 
}

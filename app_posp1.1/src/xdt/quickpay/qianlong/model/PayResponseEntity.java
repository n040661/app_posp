package xdt.quickpay.qianlong.model;

public class PayResponseEntity {
	
	private String amount; //订单总金额
	
	private String extra;  //订单额外参数
	
	private String orderDt; //订单日期
	
	private String orderNo;//平台订单号
	
	private String orgOrderNo; //机构订单号
	
	private String body; //订单描述
	
	private String orgId; //所属机构
	
	private String paySt; //支付状态
	
	private String fee; //手续费
	
	private String signature; //签名
	
	private String subject; //订单标题
	
	private String respMsg; //应答码描述
	
	private String description; //订单附件描述信息
	
	private String account; //商户账号
	
	private String respCode; //应答码
	
	private String merchartId;//商户号


	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
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

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getMerchartId() {
		return merchartId;
	}

	public void setMerchartId(String merchartId) {
		this.merchartId = merchartId;
	}
	
	

}

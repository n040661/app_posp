package xdt.quickpay.shyb.entity;

/**
 * 
 * @Description 上海易宝支付请求信息 
 * @author YanChao.Shang
 * @date 2017年12月25日 下午12:28:08 
 * @version V1.3.1
 */
public class ShybQuickPayQueryRequestEntity {
	
	private String customerNumber;
	
	private String subContractId;
	
	private String requestId;
	
	private String source;
	
	private String amount;
	
	private String mcc;
	
	private String callBackUrl;
	
	private String webCallBackUrl;
	
	private String payerBankAccountNo;
	
	private String autoWithdraw;
	
	private String withdrawCardNo;
	
	private String withdrawCallBackUrl;
	
	private String v_sign;

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	
	public String getSubContractId() {
		return subContractId;
	}

	public void setSubContractId(String subContractId) {
		this.subContractId = subContractId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getWebCallBackUrl() {
		return webCallBackUrl;
	}

	public void setWebCallBackUrl(String webCallBackUrl) {
		this.webCallBackUrl = webCallBackUrl;
	}

	public String getPayerBankAccountNo() {
		return payerBankAccountNo;
	}

	public void setPayerBankAccountNo(String payerBankAccountNo) {
		this.payerBankAccountNo = payerBankAccountNo;
	}

	public String getAutoWithdraw() {
		return autoWithdraw;
	}

	public void setAutoWithdraw(String autoWithdraw) {
		this.autoWithdraw = autoWithdraw;
	}

	public String getWithdrawCardNo() {
		return withdrawCardNo;
	}

	public void setWithdrawCardNo(String withdrawCardNo) {
		this.withdrawCardNo = withdrawCardNo;
	}

	public String getWithdrawCallBackUrl() {
		return withdrawCallBackUrl;
	}

	public void setWithdrawCallBackUrl(String withdrawCallBackUrl) {
		this.withdrawCallBackUrl = withdrawCallBackUrl;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}
	
	

}

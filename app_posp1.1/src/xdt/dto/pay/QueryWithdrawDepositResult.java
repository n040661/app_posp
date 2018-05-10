package xdt.dto.pay;

import java.io.Serializable;

public class QueryWithdrawDepositResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5017701953758898285L;

	private String serverProviderCode;//
	private String merchantUuid ;//
	private String payOrderNo ;//
	private String payTraceNo ;//
	private String reqFlowNo ;//
	private String walletType ;//
	private String amount ;//
	private String withdrawDepositFee ;//
	private String receiveAmount ;//
	private String remitStatus ;//
	private String reqTime;//
	private String remitTime;//
	public String getServerProviderCode() {
		return serverProviderCode;
	}
	public void setServerProviderCode(String serverProviderCode) {
		this.serverProviderCode = serverProviderCode;
	}
	public String getMerchantUuid() {
		return merchantUuid;
	}
	public void setMerchantUuid(String merchantUuid) {
		this.merchantUuid = merchantUuid;
	}
	public String getPayOrderNo() {
		return payOrderNo;
	}
	public void setPayOrderNo(String payOrderNo) {
		this.payOrderNo = payOrderNo;
	}
	public String getPayTraceNo() {
		return payTraceNo;
	}
	public void setPayTraceNo(String payTraceNo) {
		this.payTraceNo = payTraceNo;
	}
	public String getReqFlowNo() {
		return reqFlowNo;
	}
	public void setReqFlowNo(String reqFlowNo) {
		this.reqFlowNo = reqFlowNo;
	}
	public String getWalletType() {
		return walletType;
	}
	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getWithdrawDepositFee() {
		return withdrawDepositFee;
	}
	public void setWithdrawDepositFee(String withdrawDepositFee) {
		this.withdrawDepositFee = withdrawDepositFee;
	}
	public String getReceiveAmount() {
		return receiveAmount;
	}
	public void setReceiveAmount(String receiveAmount) {
		this.receiveAmount = receiveAmount;
	}
	public String getRemitStatus() {
		return remitStatus;
	}
	public void setRemitStatus(String remitStatus) {
		this.remitStatus = remitStatus;
	}
	public String getReqTime() {
		return reqTime;
	}
	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}
	public String getRemitTime() {
		return remitTime;
	}
	public void setRemitTime(String remitTime) {
		this.remitTime = remitTime;
	}
	 
}

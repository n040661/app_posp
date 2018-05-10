package xdt.dto.sxf;

public class PayResponse {

	private String serverId;
	private String clientId;
	private String reqId;
	private String tranCd;
	private String resCode;
	private String resMsg;
	private String resData;
	private String sign;
	private String successNum;
	private String failureNum;
	private String payResultList;
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getReqId() {
		return reqId;
	}
	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	public String getTranCd() {
		return tranCd;
	}
	public void setTranCd(String tranCd) {
		this.tranCd = tranCd;
	}
	public String getResCode() {
		return resCode;
	}
	public void setResCode(String resCode) {
		this.resCode = resCode;
	}
	public String getResMsg() {
		return resMsg;
	}
	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}
	public String getResData() {
		return resData;
	}
	public void setResData(String resData) {
		this.resData = resData;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSuccessNum() {
		return successNum;
	}
	public void setSuccessNum(String successNum) {
		this.successNum = successNum;
	}
	public String getFailureNum() {
		return failureNum;
	}
	public void setFailureNum(String failureNum) {
		this.failureNum = failureNum;
	}
	public String getPayResultList() {
		return payResultList;
	}
	public void setPayResultList(String payResultList) {
		this.payResultList = payResultList;
	}
	@Override
	public String toString() {
		return "PayResponse [serverId=" + serverId + ", clientId=" + clientId
				+ ", reqId=" + reqId + ", tranCd=" + tranCd + ", resCode="
				+ resCode + ", resMsg=" + resMsg + ", resData=" + resData
				+ ", sign=" + sign + ", successNum=" + successNum
				+ ", failureNum=" + failureNum + ", payResultList="
				+ payResultList + "]";
	}
	
}

package xdt.dto.lhzf;

public class LhzfResponse {

	private String transId;//交易接口编号
	private String serialNo;//交易流水号
	private String merNo;//交易流水号
	private String merKey;//商户交易KEY
	private String merIp;//商户请求IP
	private String orderNo;//商户订单号
	private String transDate;//交易日期
	private String transTime;//交易时间
	private String clearDate;//交易结算日期
	private String status;//交易状态
	private String respCode;//错误代码
	private String respMsg;
	private String respDesc;//错误描述
	private String trxNo;//平台交易编号
	private String transInfo;//交易附带信息
	private String sign;//报文签名信息
	
	
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getMerNo() {
		return merNo;
	}
	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}
	public String getMerKey() {
		return merKey;
	}
	public void setMerKey(String merKey) {
		this.merKey = merKey;
	}
	public String getMerIp() {
		return merIp;
	}
	public void setMerIp(String merIp) {
		this.merIp = merIp;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public String getClearDate() {
		return clearDate;
	}
	public void setClearDate(String clearDate) {
		this.clearDate = clearDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public String getTrxNo() {
		return trxNo;
	}
	public void setTrxNo(String trxNo) {
		this.trxNo = trxNo;
	}
	public String getTransInfo() {
		return transInfo;
	}
	public void setTransInfo(String transInfo) {
		this.transInfo = transInfo;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "LqzfResponse [transId=" + transId + ", serialNo=" + serialNo
				+ ", merNo=" + merNo + ", merKey=" + merKey + ", merIp="
				+ merIp + ", orderNo=" + orderNo + ", transDate=" + transDate
				+ ", transTime=" + transTime + ", clearDate=" + clearDate
				+ ", status=" + status + ", respCode=" + respCode
				+ ", respDesc=" + respDesc + ", trxNo=" + trxNo
				+ ", transInfo=" + transInfo + ", sign=" + sign + "]";
	}
	
	
}

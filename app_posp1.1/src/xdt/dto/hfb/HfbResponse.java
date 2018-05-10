package xdt.dto.hfb;

public class HfbResponse {

	private String successAmount;//支付金额
	private String payAmount;//交易金额
	private String transNo;//汇付宝订单号
	private String result;//支付结果 1000 成功 1002 失败
	private String merchantId;//商户在汇付宝的id
	private String merchantOrderNo;//商户的交易号
	private String version;//接口版本  1.0
	private String sign;//签名串，规则见说明
	private String retCode;//支付结果支付成功：1000
	private String payUrl;//支付URL
	private String charset;//编码
	private String hyBillNo;//汇付宝订单号
	private String requestTime;//请求时间
	private String merchantBillNo;//商户订单号
	private String remark;//附加数据
	private String tradeType;//tradeType
	private String payAmt;//支付金额
	private String retMsg;//支付结果信息
	private String respCode;
	private String respMsg;
	
	
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public String getSuccessAmount() {
		return successAmount;
	}
	public void setSuccessAmount(String successAmount) {
		this.successAmount = successAmount;
	}
	public String getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}
	public String getTransNo() {
		return transNo;
	}
	public void setTransNo(String transNo) {
		this.transNo = transNo;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}
	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getPayUrl() {
		return payUrl;
	}
	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getHyBillNo() {
		return hyBillNo;
	}
	public void setHyBillNo(String hyBillNo) {
		this.hyBillNo = hyBillNo;
	}
	public String getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}
	public String getMerchantBillNo() {
		return merchantBillNo;
	}
	public void setMerchantBillNo(String merchantBillNo) {
		this.merchantBillNo = merchantBillNo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getPayAmt() {
		return payAmt;
	}
	public void setPayAmt(String payAmt) {
		this.payAmt = payAmt;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	@Override
	public String toString() {
		return "HfbResponse [successAmount=" + successAmount + ", payAmount="
				+ payAmount + ", transNo=" + transNo + ", result=" + result
				+ ", merchantId=" + merchantId + ", merchantOrderNo="
				+ merchantOrderNo + ", version=" + version + ", sign=" + sign
				+ ", retCode=" + retCode + ", payUrl=" + payUrl + ", charset="
				+ charset + ", hyBillNo=" + hyBillNo + ", requestTime="
				+ requestTime + ", merchantBillNo=" + merchantBillNo
				+ ", remark=" + remark + ", tradeType=" + tradeType
				+ ", payAmt=" + payAmt + ", retMsg=" + retMsg + "]";
	}
	
}

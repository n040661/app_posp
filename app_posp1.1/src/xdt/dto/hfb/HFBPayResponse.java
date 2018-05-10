package xdt.dto.hfb;

public class HFBPayResponse {

	private String merchantId;//商户ID
	private String merchantBatchNo;//商户批次号
	private String hyBatchNo;//汇付宝批次号
	private String successAmount;//总成功金额
	private String successNum;//总成功笔数
	private String transferDetails;//付款详情
	private String signString;//签名结果
	private String merchantPayNo;//商户单笔支付流水号
	private String bankId;//银行代码
	private String publicFlag;//对公对私
	private String bankcardNo;//银行卡号
	private String ownerName;//持卡人姓名
	private String amount;//转账金额
	private String reason;//转账理由
	private String province;//省
	private String city;//市
	private String bankName;//开户支行名称
	private String status;//状态
	private String respCode;
	private String respMsg;
	private String sign;//下游签名
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantBatchNo() {
		return merchantBatchNo;
	}
	public void setMerchantBatchNo(String merchantBatchNo) {
		this.merchantBatchNo = merchantBatchNo;
	}
	public String getHyBatchNo() {
		return hyBatchNo;
	}
	public void setHyBatchNo(String hyBatchNo) {
		this.hyBatchNo = hyBatchNo;
	}
	public String getSuccessAmount() {
		return successAmount;
	}
	public void setSuccessAmount(String successAmount) {
		this.successAmount = successAmount;
	}
	public String getSuccessNum() {
		return successNum;
	}
	public void setSuccessNum(String successNum) {
		this.successNum = successNum;
	}
	public String getTransferDetails() {
		return transferDetails;
	}
	public void setTransferDetails(String transferDetails) {
		this.transferDetails = transferDetails;
	}
	public String getSignString() {
		return signString;
	}
	public void setSignString(String signString) {
		this.signString = signString;
	}
	public String getMerchantPayNo() {
		return merchantPayNo;
	}
	public void setMerchantPayNo(String merchantPayNo) {
		this.merchantPayNo = merchantPayNo;
	}
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getPublicFlag() {
		return publicFlag;
	}
	public void setPublicFlag(String publicFlag) {
		this.publicFlag = publicFlag;
	}
	public String getBankcardNo() {
		return bankcardNo;
	}
	public void setBankcardNo(String bankcardNo) {
		this.bankcardNo = bankcardNo;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
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
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "HFBPayResponse [merchantId=" + merchantId
				+ ", merchantBatchNo=" + merchantBatchNo + ", hyBatchNo="
				+ hyBatchNo + ", successAmount=" + successAmount
				+ ", successNum=" + successNum + ", transferDetails="
				+ transferDetails + ", signString=" + signString
				+ ", merchantPayNo=" + merchantPayNo + ", bankId=" + bankId
				+ ", publicFlag=" + publicFlag + ", bankcardNo=" + bankcardNo
				+ ", ownerName=" + ownerName + ", amount=" + amount
				+ ", reason=" + reason + ", province=" + province + ", city="
				+ city + ", bankName=" + bankName + ", status=" + status
				+ ", respCode=" + respCode + ", respMsg=" + respMsg + ", sign="
				+ sign + "]";
	}
	
	
}

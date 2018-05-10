package xdt.dto.hj;

public class HJPayRequest {

	private String merchantNo;//商户号
	private String batchNo;//订单号 
	private String identity;//代付明细序号
	private String amount;//金额
	private String accountName;//收款姓名
	private String bankCard;//收款账号
	private String remarks;//说明
	private String city;//收款账户地址
	private String examine;//是否审核
	private String type;//对公对私
	private String pmsbankno;//联行号
	private String productType;//代付类型1：普通代付 2：朝夕付 3：任意付
	private String sign;//签名
	public String getMerchantNo() {
		return merchantNo;
	}
	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getBankCard() {
		return bankCard;
	}
	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getExamine() {
		return examine;
	}
	public void setExamine(String examine) {
		this.examine = examine;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPmsbankno() {
		return pmsbankno;
	}
	public void setPmsbankno(String pmsbankno) {
		this.pmsbankno = pmsbankno;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "HJPayRequest [merchantNo=" + merchantNo + ", batchNo="
				+ batchNo + ", identity=" + identity + ", amount=" + amount
				+ ", accountName=" + accountName + ", bankCard=" + bankCard
				+ ", remarks=" + remarks + ", city=" + city + ", examine="
				+ examine + ", type=" + type + ", pmsbankno=" + pmsbankno
				+ ", productType=" + productType + ", sign=" + sign + "]";
	}
	
	
}

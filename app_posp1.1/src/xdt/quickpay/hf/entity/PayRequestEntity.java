package xdt.quickpay.hf.entity;

/**
 * @ClassName: PayRequestEntity
 * @Description:恒丰支付请求信息
 * @author YanChao.Shang
 * @date 2017年5月3日 上午10:23:25
 *
 */
public class PayRequestEntity {
	
	private String merchantId;//下游商户号
	
	private String payType;//0：T0，1：T1
	
	private String txnAmt;//交易金额
	
	private String orderId;// 商户交易号
	
	private String txnTime;// 订单时间 例如：20071117020101

	private String backUrl;// 服务器接受支付结果的后台地址

	private String accNo;// 交易卡号

	private String userfee; //用户手续费
	
	private String bankName; //结算银行
	
	private String toBankNo;//结算卡号
	
	private String name; //真实姓名
	
	private String certNo; //身份证号码
	
	private String frontUrl;// 支付完成后，跳转到商户方的页面
	
	private String phoneNo;//结算预留手机号
	
	private String setPhoneNo;//结算预留手机号
	
	private String pounage;//代付手续费
	
	private String signmsg;// 签名

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getTxnAmt() {
		return txnAmt;
	}

	public void setTxnAmt(String txnAmt) {
		this.txnAmt = txnAmt;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTxnTime() {
		return txnTime;
	}

	public void setTxnTime(String txnTime) {
		this.txnTime = txnTime;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public String getAccNo() {
		return accNo;
	}

	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getUserfee() {
		return userfee;
	}

	public void setUserfee(String userfee) {
		this.userfee = userfee;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getToBankNo() {
		return toBankNo;
	}

	public void setToBankNo(String toBankNo) {
		this.toBankNo = toBankNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getFrontUrl() {
		return frontUrl;
	}

	public void setFrontUrl(String frontUrl) {
		this.frontUrl = frontUrl;
	}

	public String getSignmsg() {
		return signmsg;
	}

	public void setSignmsg(String signmsg) {
		this.signmsg = signmsg;
	}

	public String getSetPhoneNo() {
		return setPhoneNo;
	}

	public void setSetPhoneNo(String setPhoneNo) {
		this.setPhoneNo = setPhoneNo;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getPounage() {
		return pounage;
	}

	public void setPounage(String pounage) {
		this.pounage = pounage;
	}

	@Override
	public String toString() {
		return "PayRequestEntity [merchantId=" + merchantId + ", payType=" + payType + ", txnAmt=" + txnAmt
				+ ", orderId=" + orderId + ", txnTime=" + txnTime + ", backUrl=" + backUrl + ", accNo=" + accNo
				+ ", userfee=" + userfee + ", bankName=" + bankName + ", toBankNo=" + toBankNo + ", name=" + name
				+ ", certNo=" + certNo + ", frontUrl=" + frontUrl + ", setPhoneNo=" + setPhoneNo + ", signmsg="
				+ signmsg + "]";
	}

	



}

package xdt.dto.sxf;

public class SXFRequest extends BaseRequest{

	private String orderNo ;//订单编号 
	private String tranAmt ;//支付金额 
	private String ccy ;//交易币种 
	private String pname;//商品名称 
	private String pnum;//商品数量
	private String pdesc ;//商品描述 
	private String retUrl ;//支付完成跳 转地址 
	private String notifyUrl;//后台通知的 地址
	private String bankWay;//银行简称 
	private String period ;//订单有效期 
	private String desc;//订单描述 
	private String userId;//商户用户 id 
	private String payWay ;//支付方式 
	private String payChannel;//商户用户 id 
	private String bankCardNo ;//银行卡号
	private String cvv ;//信用卡 cvv （卡背后 3 位数） 
	private String valid ;//信用卡有效 期  
	private String accountName ;//姓名 
	private String certificateNo ;//身份证号 
	private String mobilePhone  ;//手机号 
	private String url;//异步的
	private String reUrl;//同步的
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getReUrl() {
		return reUrl;
	}
	public void setReUrl(String reUrl) {
		this.reUrl = reUrl;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getTranAmt() {
		return tranAmt;
	}
	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}
	public String getCcy() {
		return ccy;
	}
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getPnum() {
		return pnum;
	}
	public void setPnum(String pnum) {
		this.pnum = pnum;
	}
	public String getPdesc() {
		return pdesc;
	}
	public void setPdesc(String pdesc) {
		this.pdesc = pdesc;
	}
	public String getRetUrl() {
		return retUrl;
	}
	public void setRetUrl(String retUrl) {
		this.retUrl = retUrl;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getBankWay() {
		return bankWay;
	}
	public void setBankWay(String bankWay) {
		this.bankWay = bankWay;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPayWay() {
		return payWay;
	}
	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}
	public String getPayChannel() {
		return payChannel;
	}
	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}
	public String getBankCardNo() {
		return bankCardNo;
	}
	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}
	public String getCvv() {
		return cvv;
	}
	public void setCvv(String cvv) {
		this.cvv = cvv;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getCertificateNo() {
		return certificateNo;
	}
	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	@Override
	public String toString() {
		return "SXFRequest [orderNo=" + orderNo + ", tranAmt=" + tranAmt
				+ ", ccy=" + ccy + ", pname=" + pname + ", pnum=" + pnum
				+ ", pdesc=" + pdesc + ", retUrl=" + retUrl + ", notifyUrl="
				+ notifyUrl + ", bankWay=" + bankWay + ", period=" + period
				+ ", desc=" + desc + ", userId=" + userId + ", payWay="
				+ payWay + ", payChannel=" + payChannel + ", bankCardNo="
				+ bankCardNo + ", cvv=" + cvv + ", valid=" + valid
				+ ", accountName=" + accountName + ", certificateNo="
				+ certificateNo + ", mobilePhone=" + mobilePhone + ", url="
				+ url + ", reUrl=" + reUrl + "]";
	}
	
	
	
}

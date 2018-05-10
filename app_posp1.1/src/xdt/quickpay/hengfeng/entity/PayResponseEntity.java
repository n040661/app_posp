package xdt.quickpay.hengfeng.entity;


/**
 * @ClassName: PayResponseEntity
 * @Description: 恒丰支付响应信息
 * @author LiShiwen
 * @date 2016年6月14日 上午10:23:47
 *
 */
public class PayResponseEntity {

	private String paytype;// 支付方式

	private String bankid;// 银行代码

	private String pid;// 合作伙伴用户编号

	private String transactionid;// 商户交易号

	private String ordertime;// 商户订单提交时间

	private String orderamount;// orderAmount 商户订单金额

	private String dealid;// 支付平台交易号

	private String dealtime;// 支付平台交易时间

	private String payamount;// 订单实际支 付金额

	private String ext1;// 扩展字段1

	private String ext2;// 扩展字段2

	private String payresult;// 处理结果

	private String errcode;// 错误代码

	private String signmsg;// 签名

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}

	public String getBankid() {
		return bankid;
	}

	public void setBankid(String bankid) {
		this.bankid = bankid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public String getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}

	public String getOrderamount() {
		return orderamount;
	}

	public void setOrderamount(String orderamount) {
		this.orderamount = orderamount;
	}

	public String getDealid() {
		return dealid;
	}

	public void setDealid(String dealid) {
		this.dealid = dealid;
	}

	public String getDealtime() {
		return dealtime;
	}

	public void setDealtime(String dealtime) {
		this.dealtime = dealtime;
	}

	public String getPayamount() {
		return payamount;
	}

	public void setPayamount(String payamount) {
		this.payamount = payamount;
	}

	public String getExt1() {
		return ext1;
	}

	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}

	public String getExt2() {
		return ext2;
	}

	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}

	public String getPayresult() {
		return payresult;
	}

	public void setPayresult(String payresult) {
		this.payresult = payresult;
	}

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getSignmsg() {
		return signmsg;
	}

	public void setSignmsg(String signmsg) {
		this.signmsg = signmsg;
	}

	@Override
	public String toString() {
		return "PayResponseEntity [paytype=" + paytype + ", bankid=" + bankid + ", pid=" + pid + ", transactionid="
				+ transactionid + ", ordertime=" + ordertime + ", orderamount=" + orderamount + ", dealid=" + dealid
				+ ", dealtime=" + dealtime + ", payamount=" + payamount + ", ext1=" + ext1 + ", ext2=" + ext2
				+ ", payresult=" + payresult + ", errcode=" + errcode + ", signmsg=" + signmsg + "]";
	}

}

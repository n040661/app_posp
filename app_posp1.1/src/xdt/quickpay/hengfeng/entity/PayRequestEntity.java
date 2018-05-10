package xdt.quickpay.hengfeng.entity;

/**
 * @ClassName: PayRequestEntity
 * @Description:恒丰支付请求信息
 * @author LiShiwen
 * @date 2016年6月14日 上午10:23:25
 *
 */
public class PayRequestEntity {

//	private String inputcharset = "1";// 字符集
//	
//	private String version = "v2.0";// 网关版本
//	
//	private String language = "1";// language
//	
//	private String signtype = "1";// signtype

	private String pageurl;// 支付完成后，跳转到商户方的页面

	private String bgurl;// 服务器接受支付结果的后台地址

	private String transactionid;// 商户交易号

	private String orderamount;// 订单金额

	private String ordertime;// 订单时间 例如：20071117020101

	private String productname;// 产品名称

	private String productnum;// 产品数量

	private String productdesc;// 产品说明

	private String ext1;// 英文或中文字符串 支付完成后，按照原样返回给商户

	private String ext2;// 英文或中文字符串 支付完成后，按照原样返回给商户

	private String paytype;// 支付方式

	private String bankid;// 银行代码

	private String bankno;//银行卡号
	
	private String pid;// 合作伙伴用户编号
	
	private String signmsg;// 签名
	
	

	public String getBankno() {
		return bankno;
	}

	public void setBankno(String bankno) {
		this.bankno = bankno;
	}

	public String getSignmsg() {
		return signmsg;
	}

	public void setSignmsg(String signmsg) {
		this.signmsg = signmsg;
	}

	public String getPageurl() {
		return pageurl;
	}

	public void setPageurl(String pageurl) {
		this.pageurl = pageurl;
	}

	public String getBgurl() {
		return bgurl;
	}

	public void setBgurl(String bgurl) {
		this.bgurl = bgurl;
	}

	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public String getOrderamount() {
		return orderamount;
	}

	public void setOrderamount(String orderamount) {
		this.orderamount = orderamount;
	}

	public String getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getProductnum() {
		return productnum;
	}

	public void setProductnum(String productnum) {
		this.productnum = productnum;
	}

	public String getProductdesc() {
		return productdesc;
	}

	public void setProductdesc(String productdesc) {
		this.productdesc = productdesc;
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

	@Override
	public String toString() {
		return "PayRequestEntity [pageurl=" + pageurl + ", bgurl=" + bgurl + ", transactionid=" + transactionid
				+ ", orderamount=" + orderamount + ", ordertime=" + ordertime + ", productname=" + productname
				+ ", productnum=" + productnum + ", productdesc=" + productdesc + ", ext1=" + ext1 + ", ext2=" + ext2
				+ ", paytype=" + paytype + ", bankid=" + bankid + ", pid=" + pid + ", signmsg=" + signmsg + "]";
	}


}

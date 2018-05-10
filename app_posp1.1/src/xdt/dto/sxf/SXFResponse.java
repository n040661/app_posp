package xdt.dto.sxf;

public class SXFResponse extends BaseResponse {

	private String orderNo ;//订单编号 
	private Double tranAmt ;//支付金额 
	private String ccy ;//交易币种 
	private String pname ;//商品名称 
	private String pnum ;//商品数量 
	private String pdesc ;//商品描述 
	private String tranSts ;//交易状态 U（未支付或者支付中）/ S （支付成功）/F 支付失败 
	private String orderId ;//SXF 流水 ID 
	private String bankId ;//银行返回的订单号 
	private String endTime ;//完成时间 
	private String payChannel ;//支付渠道 1 个人网银/2 企业网银/3 账户 支付/4 快捷支付/5 支付宝/6 微信 
	private String _t;
	
	public String get_t() {
		return _t;
	}
	public void set_t(String _t) {
		this._t = _t;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Double getTranAmt() {
		return tranAmt;
	}
	public void setTranAmt(Double tranAmt) {
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
	public String getTranSts() {
		return tranSts;
	}
	public void setTranSts(String tranSts) {
		this.tranSts = tranSts;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getBankId() {
		return bankId;
	}
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getPayChannel() {
		return payChannel;
	}
	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}
	@Override
	public String toString() {
		return "SXFResponse [orderNo=" + orderNo + ", tranAmt=" + tranAmt
				+ ", ccy=" + ccy + ", pname=" + pname + ", pnum=" + pnum
				+ ", pdesc=" + pdesc + ", tranSts=" + tranSts + ", orderId="
				+ orderId + ", bankId=" + bankId + ", endTime=" + endTime
				+ ", payChannel=" + payChannel + "]";
	}
}

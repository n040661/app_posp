package xdt.model;

/**
* ********************************************************
* @ClassName: PmsMerchantCollectManager
* @Description: PMS_MERCHANT_COLLECT_MANAGER商户收账管理表（提现  转账汇款  信用卡还款   表）
* @author 用wzl写的自动生成
* @date 2015-05-26 下午 03:14:29 
*******************************************************
*/
public class MerchantMinel{
	
	private String orderid;   //订单号
	private String amount;		//交易金额（商户实际提款金额         订单金额 = 交易金额+手续费）   以分为单位   1元=100分
	private String banksysnumber;		//开户行支付系统行号（联行号）
	private String bankname;		//开户行名称
	private String status;		//是否成功    0 成功   1失败 2等待处理
	private String mercId;		//商户编号
	private String businesscode;		//业务编号（ 1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、7 提款（提现））
	private String finishTime;		//完成时间（提款  汇款  还款  完成时间）  格式YYYYMMDDHHmmssSSS  20150526105900000  17位
	private String clrMerc;		//结算账号（卡号）
	private String createTime;		//创建时间（提款  汇款  还款  请求时间）  格式YYYYMMDDHHmmssSSS   20150526105900000   17位
	private String settlementname;		//结算账户名
	private String orderamount;   //订单金额        订单金额 = 交易金额+手续费
	private String rate;		//费率
	private String poundage;     //手续费        订单金额 = 交易金额+手续费
	private String reserve2;		//预留字段2
	private String reserve1;		//预留字段1
	private String oAgentNo; //欧单编号

	public String getAmount() {
		return this.amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBanksysnumber() {
		return this.banksysnumber;
	}

	public void setBanksysnumber(String banksysnumber) {
		this.banksysnumber = banksysnumber;
	}

	public String getBankname() {
		return this.bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMercId() {
		return this.mercId;
	}

	public void setMercId(String mercId) {
		this.mercId = mercId;
	}

	public String getBusinesscode() {
		return this.businesscode;
	}

	public void setBusinesscode(String businesscode) {
		this.businesscode = businesscode;
	}

	public String getFinishTime() {
		return this.finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public String getClrMerc() {
		return this.clrMerc;
	}

	public void setClrMerc(String clrMerc) {
		this.clrMerc = clrMerc;
	}

	public String getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getSettlementname() {
		return this.settlementname;
	}

	public void setSettlementname(String settlementname) {
		this.settlementname = settlementname;
	}

	public String getReserve2() {
		return this.reserve2;
	}

	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve1() {
		return this.reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getOrderamount() {
		return orderamount;
	}

	public void setOrderamount(String orderamount) {
		this.orderamount = orderamount;
	}

	public String getPoundage() {
		return poundage;
	}

	public void setPoundage(String poundage) {
		this.poundage = poundage;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

}


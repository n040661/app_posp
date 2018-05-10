package xdt.model;

public class TTransSettleAgentT0 {
    private String orderid;// 订单号

    private String mercId;//商户编号

    private String oagentno; //O单编号

    private String factamount;//实际金额    按分为最小单位  例如：1元=100分   采用100  用户消费了多少钱（商户收款收了多钱）

    private String orderamount; //订单金额 按分为最小单位  例如：1元=100分   采用100

    private String poundage;//手续费    按分为最小单位  例如：1元=100分   采用100

    private String settlepoundage;//清算手续费   按分为最小单位  例如：1元=100分   采用100  

    private String totalpoundage;//总手续费   按分为最小单位  例如：1元=100分   采用100  

    private String factpoundage;//实际手续费     按分为最小单位  例如：1元=100分   采用100 

    private String payamount;//结算金额   按分为最小单位  例如：1元=100分   采用100  给商户的钱     商户收款时给商户记账时的金额  ，提现时表示给商户打款的钱（订单金额-手续费）

    private String factsettleamount;//实际结算金额     按分为最小单位  例如：1元=100分   采用100

    
    private String banksysnumber;		//开户行支付系统行号（联行号）
	private String bankname;		//开户行名称
	private String clrMerc;		//结算账号（卡号）
	private String settlementname;		//结算账户名
    
	private String settlepoundageflag;//是否收清算手续费    0不收    1收
	
    private String status;//清算结果标记位    是否成功    0 成功   1失败

    private String settleflag;//清算方式    0手动清算    1  APP调用清算自动清算

    private String createTime;//创建时间（提款  汇款  还款  请求时间）  格式YYYYMMDDHHmmssSSS   20150526105900000   17位

    private String finishTime;//完成时间（提款  汇款  还款  完成时间）  格式YYYYMMDDHHmmssSSS  20150526105900000  17位

    private String paystatust;// 代付状态    0代付未发送   1代付已发送   2 代付成功   3代付失败   4代付暂不支持此卡

    private String paymsg;// 代付信息    0代付未发送   1代付已发送   2 代付成功   3代付失败   4代付暂不支持此卡

    private String requestsn;//代付请求序列号（代付查询使用）

    private String recordsn;// 代付请求记录号     （批量中的记录号）

    private String payrequesttime;// 代付请求时间     格式YYYYMMDDHHmmss   20150526105900  17位

    private String payfinishtime;//代付完成时间     格式YYYYMMDDHHmmss   20150526105900  17位

    private String payreturncode;//  代付系统返回的code

    private String payreturnmsg;//代付系统返回的信息

    private String channelpaytype  ;//   代付通道类型   1 高汇通代付    2 弘付数据代付

    private String channelpaymsg   ;//  代付通道类型   1 高汇通代付    2 弘付数据代付
	
    private String payorderid   ;//   代付订单号   G开头高汇通代付    H开头弘付数据代付
    
    
    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid == null ? null : orderid.trim();
    }

    public String getMercId() {
        return mercId;
    }

    public void setMercId(String mercId) {
        this.mercId = mercId == null ? null : mercId.trim();
    }

    public String getOagentno() {
        return oagentno;
    }

    public void setOagentno(String oagentno) {
        this.oagentno = oagentno == null ? null : oagentno.trim();
    }

    public String getFactamount() {
        return factamount;
    }

    public void setFactamount(String factamount) {
        this.factamount = factamount == null ? null : factamount.trim();
    }

    public String getOrderamount() {
        return orderamount;
    }

    public void setOrderamount(String orderamount) {
        this.orderamount = orderamount == null ? null : orderamount.trim();
    }

    public String getPoundage() {
        return poundage;
    }

    public void setPoundage(String poundage) {
        this.poundage = poundage == null ? null : poundage.trim();
    }

    public String getSettlepoundage() {
        return settlepoundage;
    }

    public void setSettlepoundage(String settlepoundage) {
        this.settlepoundage = settlepoundage == null ? null : settlepoundage.trim();
    }

    public String getTotalpoundage() {
        return totalpoundage;
    }

    public void setTotalpoundage(String totalpoundage) {
        this.totalpoundage = totalpoundage == null ? null : totalpoundage.trim();
    }

    public String getFactpoundage() {
        return factpoundage;
    }

    public void setFactpoundage(String factpoundage) {
        this.factpoundage = factpoundage == null ? null : factpoundage.trim();
    }

    public String getPayamount() {
        return payamount;
    }

    public void setPayamount(String payamount) {
        this.payamount = payamount == null ? null : payamount.trim();
    }

    public String getFactsettleamount() {
        return factsettleamount;
    }

    public void setFactsettleamount(String factsettleamount) {
        this.factsettleamount = factsettleamount == null ? null : factsettleamount.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getSettleflag() {
        return settleflag;
    }

    public void setSettleflag(String settleflag) {
        this.settleflag = settleflag == null ? null : settleflag.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime == null ? null : finishTime.trim();
    }

    public String getPaystatust() {
        return paystatust;
    }

    public void setPaystatust(String paystatust) {
        this.paystatust = paystatust == null ? null : paystatust.trim();
    }

    public String getPaymsg() {
        return paymsg;
    }

    public void setPaymsg(String paymsg) {
        this.paymsg = paymsg == null ? null : paymsg.trim();
    }

    public String getRequestsn() {
        return requestsn;
    }

    public void setRequestsn(String requestsn) {
        this.requestsn = requestsn == null ? null : requestsn.trim();
    }

    public String getRecordsn() {
        return recordsn;
    }

    public void setRecordsn(String recordsn) {
        this.recordsn = recordsn == null ? null : recordsn.trim();
    }

    public String getPayrequesttime() {
        return payrequesttime;
    }

    public void setPayrequesttime(String payrequesttime) {
        this.payrequesttime = payrequesttime == null ? null : payrequesttime.trim();
    }

    public String getPayfinishtime() {
        return payfinishtime;
    }

    public void setPayfinishtime(String payfinishtime) {
        this.payfinishtime = payfinishtime == null ? null : payfinishtime.trim();
    }

    public String getPayreturncode() {
        return payreturncode;
    }

    public void setPayreturncode(String payreturncode) {
        this.payreturncode = payreturncode == null ? null : payreturncode.trim();
    }

    public String getPayreturnmsg() {
        return payreturnmsg;
    }

    public void setPayreturnmsg(String payreturnmsg) {
        this.payreturnmsg = payreturnmsg == null ? null : payreturnmsg.trim();
    }

	public String getBanksysnumber() {
		return banksysnumber;
	}

	public void setBanksysnumber(String banksysnumber) {
		this.banksysnumber = banksysnumber;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getClrMerc() {
		return clrMerc;
	}

	public void setClrMerc(String clrMerc) {
		this.clrMerc = clrMerc;
	}

	public String getSettlementname() {
		return settlementname;
	}

	public void setSettlementname(String settlementname) {
		this.settlementname = settlementname;
	}

	public String getSettlepoundageflag() {
		return settlepoundageflag;
	}

	public void setSettlepoundageflag(String settlepoundageflag) {
		this.settlepoundageflag = settlepoundageflag;
	}

	public String getChannelpaytype() {
		return channelpaytype;
	}

	public void setChannelpaytype(String channelpaytype) {
		this.channelpaytype = channelpaytype;
	}

	public String getChannelpaymsg() {
		return channelpaymsg;
	}

	public void setChannelpaymsg(String channelpaymsg) {
		this.channelpaymsg = channelpaymsg;
	}

	public String getPayorderid() {
		return payorderid;
	}

	public void setPayorderid(String payorderid) {
		this.payorderid = payorderid;
	}
    
    
}
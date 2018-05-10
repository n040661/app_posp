
package xdt.model;
import java.math.BigDecimal;
import java.util.Date;

/**
* ********************************************************
* @ClassName: PospTransInfo
* @Description: 交易流水表
* @author 用wzl写的自动生成
* @date 2015-05-22 上午 09:38:22 
*******************************************************
*/
public class PospTransInfo {

	private Double transfee4;		//通道标准费率
	private String sysseqno;		//主机交易流水号
	private String transtime;		//pos交易时间
	private Double transfee2;		//宣称佣金 (通道费率减去协定费率)
	private Double transfee3;		//通道费率
	private Double transfee1;		//实际佣金 (商户费率减去通道费率)
	private String reason;		//消费冲正交易原因
	private String remark;		//null
	private String simId;		//sim卡ID
	private String tac;		//null
	private String bnkCd;		//银行编码
	private String pospsn;		//平台流水号
	private String cardvaliddate;		//卡有效日期
	private String buspos;		//通道POS终端号
	private String pospservicecode;		//设置POS服务平台交易码
	private Integer cancelid;		//冲正目标或原流水 该字段与冲正交易标志相关，当冲正交易标志为：0 时，该字段无意义 1 时，该字段为冲正目标流水ID 2 时，该字段为冲正源流水ID; 撤销交易时CANCELFLAG为0，CANCELID为原消费交易的ID
	private String merchantcode;		//商户号。
	private String terminalsn;		//此字段用于补录时记录上传的终端机流水号
	private Date senddate;		//交易上送帐期
	private Integer counterpin;		//服务网点PIN码
	private String channelno;		//渠道号 00：POS  01：CC ；02：WEB；03：手机；04 普通网点:05管理系统;06:日终处理
	private String bnkNm;		//银行名称
	private Integer posid;		//null
	private String transcode;		//01-充值（400000）； 02-脱机消费；12-撤销（200000）；13-退货；20-退订消费。
	private Integer transsecuritycontrol;		//设置交易安全控制信息
	private String crdTyp;		//卡类型
	private String cardno;		//卡号
	private String searchTransCode;		//真正的交易类型
	private String transdate;		//pos交易日期
	private String settlementflag;		//00-未结算 01-已成功结算 02-结算不成功 （都是指示与POS机结算）
	private Integer settlementid;		//对应POSP_SETTLEMENT_LOG表主键ID
	private String authoritycode;		//授权码
	private Integer isClearSelf;		//是否自清 1 自清，0 他清 为空默认规则走
	private String responsecode;		//00-成功 其他失败
	private String orderId;		//订单id（对应pms_app_trans_info订单表的orderid）
	private String businfo;		//通道商户编号
	private Double addfee;		//附加费用
	private String premiumrate;		//刷卡费率
	private Integer pfmtid;		//原始交易报文记录ID
	private Integer inputtype;		// 服务网点输入方式 1 表示IC卡 0 表示磁条卡
	private String transstatus;		//0-脱机POS上送流水，1-联机消费流水
	private Integer id;		//响应流水
	private String uniqueKey;//唯一值
	
	private Integer stationInfo;		//基站信息
	private String interVal;		//交易时间间隔
	private Integer routeid;		//关联路由ID POSP_ROUTE_INFO
	private String msgtype;		// 交易消息类型  0200-充值  0320-未确认交易上送        撤销冲正- 0400 撤销-0200 退货－0220 充值冲正－0400
	private BigDecimal transamt;		//发生额
	private String posterminalid;		//终端号
	private String operid;		//操作员Id
	private Integer pospid;		//POS服务平台代码
	private String currencycode;		//币别代码
	private String balancedate;		//结算日期
	private String psamno;		//PSAM卡号
	private String personalid;		//null
	private String crdNm;		//卡号
	private Integer cancelflag;		//0-正常交易，1-冲正交易，2-被冲正交易
	private String freezeState;		//冻结状态 
	private String possn;		//终端序列号
	private Integer conuterconditioncode;		//null
	private Integer isapp;		//是否APP交易
	private String paymentType;		//支付方式 例如： 1 账号（余额）支付、2 百度支付、3 微信支付、4 支付宝支付 （对应订单表中的payment_Type）
	private String batno;		//null
	private String oAgentNo; //O单编号
    private String transOrderId;//上送通道的订单号
    private Integer payTimes;//支付的次数  用于摩宝支付

	public Double getTransfee4() {
		return this.transfee4;
	}

	public void setTransfee4(Double transfee4) {
		this.transfee4 = transfee4;
	}

	public String getSysseqno() {
		return this.sysseqno;
	}

	public void setSysseqno(String sysseqno) {
		this.sysseqno = sysseqno;
	}

	public String getTranstime() {
		return this.transtime;
	}

	public void setTranstime(String transtime) {
		this.transtime = transtime;
	}

	public Double getTransfee2() {
		return this.transfee2;
	}

	public void setTransfee2(Double transfee2) {
		this.transfee2 = transfee2;
	}

	public Double getTransfee3() {
		return this.transfee3;
	}

	public void setTransfee3(Double transfee3) {
		this.transfee3 = transfee3;
	}

	public Double getTransfee1() {
		return this.transfee1;
	}

	public void setTransfee1(Double transfee1) {
		this.transfee1 = transfee1;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSimId() {
		return this.simId;
	}

	public void setSimId(String simId) {
		this.simId = simId;
	}

	public String getTac() {
		return this.tac;
	}

	public void setTac(String tac) {
		this.tac = tac;
	}

	public String getBnkCd() {
		return this.bnkCd;
	}

	public void setBnkCd(String bnkCd) {
		this.bnkCd = bnkCd;
	}

	public String getPospsn() {
		return this.pospsn;
	}

	public void setPospsn(String pospsn) {
		this.pospsn = pospsn;
	}

	public String getCardvaliddate() {
		return this.cardvaliddate;
	}

	public void setCardvaliddate(String cardvaliddate) {
		this.cardvaliddate = cardvaliddate;
	}

	public String getBuspos() {
		return this.buspos;
	}

	public void setBuspos(String buspos) {
		this.buspos = buspos;
	}

	public String getPospservicecode() {
		return this.pospservicecode;
	}

	public void setPospservicecode(String pospservicecode) {
		this.pospservicecode = pospservicecode;
	}

	public Integer getCancelid() {
		return this.cancelid;
	}

	public void setCancelid(Integer cancelid) {
		this.cancelid = cancelid;
	}

	public String getMerchantcode() {
		return this.merchantcode;
	}

	public void setMerchantcode(String merchantcode) {
		this.merchantcode = merchantcode;
	}

	public String getTerminalsn() {
		return this.terminalsn;
	}

	public void setTerminalsn(String terminalsn) {
		this.terminalsn = terminalsn;
	}

	public Date getSenddate() {
		return this.senddate;
	}

	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}

	public Integer getCounterpin() {
		return this.counterpin;
	}

	public void setCounterpin(Integer counterpin) {
		this.counterpin = counterpin;
	}

	public String getChannelno() {
		return this.channelno;
	}

	public void setChannelno(String channelno) {
		this.channelno = channelno;
	}

	public String getBnkNm() {
		return this.bnkNm;
	}

	public void setBnkNm(String bnkNm) {
		this.bnkNm = bnkNm;
	}

	public Integer getPosid() {
		return this.posid;
	}

	public void setPosid(Integer posid) {
		this.posid = posid;
	}

	public String getTranscode() {
		return this.transcode;
	}

	public void setTranscode(String transcode) {
		this.transcode = transcode;
	}

	public Integer getTranssecuritycontrol() {
		return this.transsecuritycontrol;
	}

	public void setTranssecuritycontrol(Integer transsecuritycontrol) {
		this.transsecuritycontrol = transsecuritycontrol;
	}

	public String getCrdTyp() {
		return this.crdTyp;
	}

	public void setCrdTyp(String crdTyp) {
		this.crdTyp = crdTyp;
	}

	public String getCardno() {
		return this.cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public String getSearchTransCode() {
		return this.searchTransCode;
	}

	public void setSearchTransCode(String searchTransCode) {
		this.searchTransCode = searchTransCode;
	}

	public String getTransdate() {
		return this.transdate;
	}

	public void setTransdate(String transdate) {
		this.transdate = transdate;
	}

	public String getSettlementflag() {
		return this.settlementflag;
	}

	public void setSettlementflag(String settlementflag) {
		this.settlementflag = settlementflag;
	}

	public Integer getSettlementid() {
		return this.settlementid;
	}

	public void setSettlementid(Integer settlementid) {
		this.settlementid = settlementid;
	}

	public String getAuthoritycode() {
		return this.authoritycode;
	}

	public void setAuthoritycode(String authoritycode) {
		this.authoritycode = authoritycode;
	}

	public Integer getIsClearSelf() {
		return this.isClearSelf;
	}

	public void setIsClearSelf(Integer isClearSelf) {
		this.isClearSelf = isClearSelf;
	}

	public String getResponsecode() {
		return this.responsecode;
	}

	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getBusinfo() {
		return this.businfo;
	}

	public void setBusinfo(String businfo) {
		this.businfo = businfo;
	}

	public Double getAddfee() {
		return this.addfee;
	}

	public void setAddfee(Double addfee) {
		this.addfee = addfee;
	}

	public String getPremiumrate() {
		return this.premiumrate;
	}

	public void setPremiumrate(String premiumrate) {
		this.premiumrate = premiumrate;
	}

	public Integer getPfmtid() {
		return this.pfmtid;
	}

	public void setPfmtid(Integer pfmtid) {
		this.pfmtid = pfmtid;
	}

	public Integer getInputtype() {
		return this.inputtype;
	}

	public void setInputtype(Integer inputtype) {
		this.inputtype = inputtype;
	}

	public String getTransstatus() {
		return this.transstatus;
	}

	public void setTransstatus(String transstatus) {
		this.transstatus = transstatus;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStationInfo() {
		return this.stationInfo;
	}

	public void setStationInfo(Integer stationInfo) {
		this.stationInfo = stationInfo;
	}

	public String getInterVal() {
		return this.interVal;
	}

	public void setInterVal(String interVal) {
		this.interVal = interVal;
	}

	public Integer getRouteid() {
		return this.routeid;
	}

	public void setRouteid(Integer routeid) {
		this.routeid = routeid;
	}

	public String getMsgtype() {
		return this.msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public BigDecimal getTransamt() {
		return this.transamt;
	}

	public void setTransamt(BigDecimal transamt) {
		this.transamt = transamt;
	}

	public String getPosterminalid() {
		return this.posterminalid;
	}

	public void setPosterminalid(String posterminalid) {
		this.posterminalid = posterminalid;
	}

	public String getOperid() {
		return this.operid;
	}

	public void setOperid(String operid) {
		this.operid = operid;
	}

	public Integer getPospid() {
		return this.pospid;
	}

	public void setPospid(Integer pospid) {
		this.pospid = pospid;
	}

	public String getCurrencycode() {
		return this.currencycode;
	}

	public void setCurrencycode(String currencycode) {
		this.currencycode = currencycode;
	}

	public String getBalancedate() {
		return this.balancedate;
	}

	public void setBalancedate(String balancedate) {
		this.balancedate = balancedate;
	}

	public String getPsamno() {
		return this.psamno;
	}

	public void setPsamno(String psamno) {
		this.psamno = psamno;
	}

	public String getPersonalid() {
		return this.personalid;
	}

	public void setPersonalid(String personalid) {
		this.personalid = personalid;
	}

	public String getCrdNm() {
		return this.crdNm;
	}

	public void setCrdNm(String crdNm) {
		this.crdNm = crdNm;
	}

	public Integer getCancelflag() {
		return this.cancelflag;
	}

	public void setCancelflag(Integer cancelflag) {
		this.cancelflag = cancelflag;
	}

	public String getFreezeState() {
		return this.freezeState;
	}

	public void setFreezeState(String freezeState) {
		this.freezeState = freezeState;
	}

	public String getPossn() {
		return this.possn;
	}

	public void setPossn(String possn) {
		this.possn = possn;
	}

	public Integer getConuterconditioncode() {
		return this.conuterconditioncode;
	}

	public void setConuterconditioncode(Integer conuterconditioncode) {
		this.conuterconditioncode = conuterconditioncode;
	}

	public Integer getIsapp() {
		return this.isapp;
	}

	public void setIsapp(Integer isapp) {
		this.isapp = isapp;
	}

	public String getPaymentType() {
		return this.paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getBatno() {
		return this.batno;
	}

	public void setBatno(String batno) {
		this.batno = batno;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

    public String getTransOrderId() {
        return transOrderId;
    }

    public void setTransOrderId(String transOrderId) {
        this.transOrderId = transOrderId;
    }

    public Integer getPayTimes() {
        return payTimes;
    }

    public void setPayTimes(Integer payTimes) {
        this.payTimes = payTimes;
    }
	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
}


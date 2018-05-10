package xdt.dto;

import java.math.BigDecimal;

/**
 * POSP-通道消费交易查询
 * 版权所有：2016-xdt
 * 项目名称：Posp2   
 *
 * 类描述：
 * 类名称：com.servlet.model.PospTransInfoOrgRspDTO     
 * 创建人：qt 
 * 创建时间：2016-3-2 下午04:39:13   
 * 修改人：
 * 修改时间：2016-3-2 下午04:39:13   
 * 修改备注：   
 * @version   V1.0
 */
public class PospTransInfoOrgRspDTO {
	
	private String retCode;
	private String retMessage;

	/**
	 * 交易卡号
	 */
	private String cardno;
	
	/**
	 * 交易处理码
	 */
	private String transcode;
	
	/**
	 * 终端流水号
	 */
	private String terminalsn;
	
	/**
	 * pos交易时间
	 */
	private String transtime;
	
	/**
	 * POS 交易日期
	 */
	private String transdate;
	
	/**
	 * 终端序列号=终端流水号
	 */
	private String possn;
	
	/**
	 * 业务终端号
	 */
	private String posterminalid;
	
	/**
	 * 卡有效期
	 */
	private String cardvaliddate;
	
	/**
	 * 实际交易发送时间
	 */
	private String senddate;
	
	/**
	 * 结算日期
	 */
	private String balancedate;
	
	/**
	 * 中心 交易检索号 37域
	 */
	private String sysseqno;
	
	/**
	 * 通道商户号
	 */
	private String merchantcode;
	
	/**
	 * 通道商户名称
	 */
	private String merchantName;
	/**
	 * 授权码 38域
	 */
	private String authoritycode;
	
	/**
	 * 交易货币代码 49域 （默认人民币-156）
	 */
	private String currencycode;
	
	/**
	 * 交易金额
	 */
	private BigDecimal transamt;
	/**
	 * 应答码 00成功（数据库会左补00）
	 */
	private String responsecode;
	/**
	 * 批次号
	 */
	private String batno;
	
	/**
	 * 消息类型
	 */
	private String msgtype;
	
	/**
	 * 操作员 （默认01）
	 */
	private String operid;
	
	/**
	 * POSP 交易检索号
	 */
	private String pospsn;
	
	/**
	 * 7域
	 */
	private String reqTranstime;
	
	/**
	 * 发卡行
	 */
	private String crdBnkName;

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public String getTranscode() {
		return transcode;
	}

	public void setTranscode(String transcode) {
		this.transcode = transcode;
	}

	public String getTerminalsn() {
		return terminalsn;
	}

	public void setTerminalsn(String terminalsn) {
		this.terminalsn = terminalsn;
	}

	public String getTranstime() {
		return transtime;
	}

	public void setTranstime(String transtime) {
		this.transtime = transtime;
	}

	public String getTransdate() {
		return transdate;
	}

	public void setTransdate(String transdate) {
		this.transdate = transdate;
	}

	public String getPossn() {
		return possn;
	}

	public void setPossn(String possn) {
		this.possn = possn;
	}

	public String getPosterminalid() {
		return posterminalid;
	}

	public void setPosterminalid(String posterminalid) {
		this.posterminalid = posterminalid;
	}

	public String getCardvaliddate() {
		return cardvaliddate;
	}

	public void setCardvaliddate(String cardvaliddate) {
		this.cardvaliddate = cardvaliddate;
	}

    public String getSenddate() {
        return senddate;
    }

    public void setSenddate(String senddate) {
        this.senddate = senddate;
    }

    public String getBalancedate() {
		return balancedate;
	}

	public void setBalancedate(String balancedate) {
		this.balancedate = balancedate;
	}

	public String getSysseqno() {
		return sysseqno;
	}

	public void setSysseqno(String sysseqno) {
		this.sysseqno = sysseqno;
	}

	public String getMerchantcode() {
		return merchantcode;
	}

	public void setMerchantcode(String merchantcode) {
		this.merchantcode = merchantcode;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getAuthoritycode() {
		return authoritycode;
	}

	public void setAuthoritycode(String authoritycode) {
		this.authoritycode = authoritycode;
	}

	public String getCurrencycode() {
		return currencycode;
	}

	public void setCurrencycode(String currencycode) {
		this.currencycode = currencycode;
	}

	public BigDecimal getTransamt() {
		return transamt;
	}

	public void setTransamt(BigDecimal transamt) {
		this.transamt = transamt;
	}

	public String getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}

	public String getBatno() {
		return batno;
	}

	public void setBatno(String batno) {
		this.batno = batno;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getOperid() {
		return operid;
	}

	public void setOperid(String operid) {
		this.operid = operid;
	}

	public String getPospsn() {
		return pospsn;
	}

	public void setPospsn(String pospsn) {
		this.pospsn = pospsn;
	}

	public String getReqTranstime() {
		return reqTranstime;
	}

	public void setReqTranstime(String reqTranstime) {
		this.reqTranstime = reqTranstime;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMessage() {
		return retMessage;
	}

	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}

	public String getCrdBnkName() {
		return crdBnkName;
	}

	public void setCrdBnkName(String crdBnkName) {
		this.crdBnkName = crdBnkName;
	}
}

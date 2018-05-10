package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

/**
 * 虚拟账户余额查询响应
 *
 */
public class Y2e0012Res extends Y2eXmlEntity{
	@Y2eField(path = "yt2e.trans.trn-y2e0012-res.status.rspcod")
	private String rspcod;//报文处理状�?
	
	@Y2eField(path = "yt2e.trans.trn-y2e0012-res.status.rspmsg")
	private String rspmsg;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0012-res.trn-info.mer-no")
	private String merNo;
	
	
	@Y2eField(path = "yt2e.trans.trn-y2e0012-res.trn-info.avail-bal")
	private String availBal;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0012-res.trn-info.to-cash-bal")
	private String toCashBal;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0012-res.trn-info.frozen-amt")
	private String frozenAmt;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0012-res.trn-info.acct-status")
	private String acctStatus;
	

	
	public String getRspcod() {
		return rspcod;
	}

	public void setRspcod(String rspcod) {
		this.rspcod = rspcod;
	}

	public String getRspmsg() {
		return rspmsg;
	}

	public void setRspmsg(String rspmsg) {
		this.rspmsg = rspmsg;
	}

	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}


	public String getAvailBal() {
		return availBal;
	}

	public void setAvailBal(String availBal) {
		this.availBal = availBal;
	}

	public String getToCashBal() {
		return toCashBal;
	}

	public void setToCashBal(String toCashBal) {
		this.toCashBal = toCashBal;
	}

	public String getFrozenAmt() {
		return frozenAmt;
	}

	public void setFrozenAmt(String frozenAmt) {
		this.frozenAmt = frozenAmt;
	}

	public String getAcctStatus() {
		return acctStatus;
	}

	public void setAcctStatus(String acctStatus) {
		this.acctStatus = acctStatus;
	}

	
	public static void main(String[] args){
		String xmlStr="<yt2e><trans><trn-y2e0012-res>"
		+"<status>"
		+"<rspcod>21</rspcod>"
		+"<rspmsg>212</rspmsg>"
		+"</status>"	
		+"<batchid>00000001</batchid>"
		+"<datakey>111111</datakey>"
		+"<certinfo>certInfo</certinfo>"
		+"<serverdt>20150319000000</serverdt>"
		+"<sign>signValue</sign>"
		+"</trn-y2e0012-res></trans></yt2e>";
		
		Y2e0012Res res=new Y2e0012Res();
		res.parseXml(xmlStr);
		
		System.out.println(res.getRspcod()+"-------------------");
		
	}
}

package xdt.quickpay.jbb.entity.xml.y2e;



import java.util.ArrayList;
import java.util.List;

import xdt.quickpay.jbb.util.Y2eField;

/**
 * 代付查询响应
 * @author Administrator
 */
public class Y2e0011Res extends Y2eXmlEntity{

	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.status.rspcod")
	private String rspcod;//报文处理状态
	
	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.status.rspmsg")
	private String rspmsg;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.trn-info.mer-no")
	private String merNo;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.trn-info.pay-tm")
	private String payTm;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.trn-info.batch-name")
	private String batchName;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.trn-info.batch-no")
	private String batchNo;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.trn-info.buss-name")
	private String bussName;

	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.trn-info.procedure-type")
	private String procedureType;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.trn-info.tot-cnt")
	private String totCnt;	

	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.trn-info.tot-amt")
	private String totAmt;	
	
	
	
	
	
	public String getBussName() {
		return bussName;
	}

	public void setBussName(String bussName) {
		this.bussName = bussName;
	}
	
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

	public String getPayTm() {
		return payTm;
	}

	public void setPayTm(String payTm) {
		this.payTm = payTm;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(String procedureType) {
		this.procedureType = procedureType;
	}

	public String getTotCnt() {
		return totCnt;
	}

	public void setTotCnt(String totCnt) {
		this.totCnt = totCnt;
	}

	public String getTotAmt() {
		return totAmt;
	}

	public void setTotAmt(String totAmt) {
		this.totAmt = totAmt;
	}


	@Y2eField(path = "yt2e.trans.trn-y2e0011-res.trn-details.pay-detail", type = "list")
	private List<Y2e0011ResDetail> detail;


	public List<Y2e0011ResDetail> getDetail() {
		return detail;
	}
	
	public void setDetail(List<Y2e0011ResDetail> detail) {
		this.detail = detail;
	}
	
	public void addDetail(Y2e0011ResDetail reqDetail) {
		if (this.detail == null) {
			this.detail = new ArrayList<Y2e0011ResDetail>();
		}
		this.detail.add(reqDetail);
	}

	public void clearDetail() {
		if (this.detail != null) {
			this.detail.clear();
		}
	}

	public static void main(String[] args){
		String xmlStr="<bocb2e><trans><trn-b2e0009-rs>"
		+"<status>"
		+"<rspcod>21</rspcod>"
		+"<rspmsg>212</rspmsg>"
		+"</status>"
		+"<b2e0009-rs>"
		+"<status>"
		+"<rspcod>ok</rspcod>"
		+"<rspmsg>133</rspmsg>"
		+"</status>"
		+"<insid/>"
		+"<obssid/>"
		
		+"</b2e0009-rs>"
		+"</trn-b2e0009-rs></trans></bocb2e>";
		
		Y2e0011Res res=new Y2e0011Res();
		res.parseXml(xmlStr);
		
		System.out.println(res.getRspcod()+"-------------------");
		
	}
}

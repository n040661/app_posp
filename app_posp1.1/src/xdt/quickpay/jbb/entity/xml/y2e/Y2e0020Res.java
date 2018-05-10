package xdt.quickpay.jbb.entity.xml.y2e;

import java.util.ArrayList;
import java.util.List;

import xdt.quickpay.jbb.util.Y2eField;


/**
 *�?��响应
 *
 */
public class Y2e0020Res extends Y2eXmlEntity{

	@Y2eField(path = "yt2e.trans.trn-y2e0020-res.status.rspcod")
	private String rspcod;//报文处理状�?
	
	@Y2eField(path = "yt2e.trans.trn-y2e0020-res.status.rspmsg")
	private String rspmsg;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0020-res.trn-info.mer-no")
	private String merNo;
	

	@Y2eField(path = "yt2e.trans.trn-y2e0020-res.trn-info.tot-cnt")
	private String totCnt;	

	@Y2eField(path = "yt2e.trans.trn-y2e0020-res.trn-info.tot-amt")
	private String totAmt;	
	

	

	
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



	@Y2eField(path = "yt2e.trans.trn-y2e0020-res.trn-details.trn-detail", type = "list")
	private List<Y2e0020ResDetail> detail;


	public List<Y2e0020ResDetail> getDetail() {
		return detail;
	}
	
	public void setDetail(List<Y2e0020ResDetail> detail) {
		this.detail = detail;
	}
	
	public void addDetail(Y2e0020ResDetail reqDetail) {
		if (this.detail == null) {
			this.detail = new ArrayList<Y2e0020ResDetail>();
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
		
		Y2e0020Res res=new Y2e0020Res();
		res.parseXml(xmlStr);
		
		System.out.println(res.getRspcod()+"-------------------");
		
	}
}

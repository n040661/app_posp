package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

/**
 * 代付响应
 */
public class Y2e0010Res extends Y2eXmlEntity{

	@Y2eField(path = "yt2e.trans.trn-y2e0010-res.status.rspcod")
	private String rspcod;//报文处理状�?
	
	@Y2eField(path = "yt2e.trans.trn-y2e0010-res.status.rspmsg")
	private String rspmsg;

	@Y2eField(path = "yt2e.trans.trn-y2e0010-res.mer-no")
	private String merNo;

	@Y2eField(path = "yt2e.trans.trn-y2e0010-res.batch-no")
	private String batchNo;


	
	@Y2eField(path = "yt2e.trans.trn-y2e0010-res.server-tm")
	private String serverTm;
	

	
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

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}



	public String getServerTm() {
		return serverTm;
	}

	public void setServerTm(String serverTm) {
		this.serverTm = serverTm;
	}


	

	public static void main(String[] args){
		String xmlStr="<yt2e><trans><trn-y2e0010-res>";//...

		Y2e0010Res res=new Y2e0010Res();
		res.parseXml(xmlStr);
		
		System.out.println(res.getRspcod()+"-------------------");		
	}
}

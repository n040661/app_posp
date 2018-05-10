package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

/**
 * 签到响应
 *
 */
public class Y2e0001Res extends Y2eXmlEntity{
	@Y2eField(path = "yt2e.trans.trn-y2e0001-res.status.rspcod")
	private String rspcod;//报文处理状�?
	@Y2eField(path = "yt2e.trans.trn-y2e0001-res.status.rspmsg")
	private String rspmsg;

	@Y2eField(path = "yt2e.trans.trn-y2e0001-res.batchid")
	private String batchid;

	@Y2eField(path = "yt2e.trans.trn-y2e0001-res.datakey")
	private String datakey;

	@Y2eField(path = "yt2e.trans.trn-y2e0001-res.serverdt")
	private String serverdt;
	

	
	
	
	
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
	/**
	 * @函数说明:
	 * @创建�?zxb
	 * @创建日期:2012-11-12
	 * @param args String[]
	 */
	public static void main(String[] args){
		String xmlStr="<yt2e><trans><trn-y2e0001-res>"
		+"<status>"
		+"<rspcod>21</rspcod>"
		+"<rspmsg>212</rspmsg>"
		+"</status>"	
		+"<batchid>00000001</batchid>"
		+"<datakey>111111</datakey>"
		+"<certinfo>certInfo</certinfo>"
		+"<serverdt>20150319000000</serverdt>"
		+"<sign>signValue</sign>"
		+"</trn-y2e0001-res></trans></yt2e>";
		
		Y2e0001Res res=new Y2e0001Res();
		res.parseXml(xmlStr);
		
		System.out.println(res.getRspcod()+"-------------------");
		
	}

	public String getBatchid() {
		return batchid;
	}

	public void setBatchid(String batchid) {
		this.batchid = batchid;
	}

	public String getDatakey() {
		return datakey;
	}

	public void setDatakey(String datakey) {
		this.datakey = datakey;
	}



	public String getServerdt() {
		return serverdt;
	}

	public void setServerdt(String serverdt) {
		this.serverdt = serverdt;
	}
}

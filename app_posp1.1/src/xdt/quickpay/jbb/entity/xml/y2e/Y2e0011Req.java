package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

/**
 * 代付查询
 *
 */
public class Y2e0011Req extends Y2eXmlEntity{
	@Y2eField(path = "yt2e.trans.trn-y2e0011-req.mer-no")
	private String merNo;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0011-req.batch-no")
	private String batchNo;

	
	@Y2eField(path = "yt2e.trans.trn-y2e0011-req.sign")
	private String sign;
	
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


	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
	}


	public static void main(final String[] args){		
		Y2e0011Req req =new Y2e0011Req();
		req.setMerNo("988088888888888");
		req.setBatchNo("00000001");

		String xmlStr=req.createXml();
		System.out.println("------xmlStr------"+xmlStr);
		
	}
}

package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

/**
 * �?��查询
 *
 */
public class Y2e0020Req extends Y2eXmlEntity{
	@Y2eField(path = "yt2e.trans.trn-y2e0020-req.mer-no")
	private String merNo;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0020-req.refund-date")
	private String refundDate;

	@Y2eField(path = "yt2e.trans.trn-y2e0020-req.sign")
	private String sign;
	
	public String getMerNo() {
		return merNo;
	}


	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}



	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
	}
	

	public String getRefundDate() {
		return refundDate;
	}


	public void setRefundDate(String refundDate) {
		this.refundDate = refundDate;
	}


	public static void main(final String[] args){		
		Y2e0020Req req =new Y2e0020Req();
		req.setMerNo("988088888888888");

		String xmlStr=req.createXml();
		System.out.println("------xmlStr------"+xmlStr);
		
	}
}

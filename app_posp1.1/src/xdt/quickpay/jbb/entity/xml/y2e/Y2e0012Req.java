package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

/**
 * 虚拟账户余额查询
 *
 */
public class Y2e0012Req extends Y2eXmlEntity{
	@Y2eField(path = "yt2e.trans.trn-y2e0012-req.mer-no")
	private String merNo;
	

	
	@Y2eField(path = "yt2e.trans.trn-y2e0012-req.qry-txnno")
	private String qryTxnno;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0012-req.sign")
	private String sign;
	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}


	public String getQryTxnno() {
		return qryTxnno;
	}

	public void setQryTxnno(String qryTxnno) {
		this.qryTxnno = qryTxnno;
	}

	public static void main(final String[] args){
		
		Y2e0012Req req =new Y2e0012Req();

		String xmlStr=req.createXml();
		System.out.println("------xmlStr------"+xmlStr);	
		
	}
	
}

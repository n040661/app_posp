package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

public class Y2e0030Req extends Y2eXmlEntity{
	@Y2eField(path = "yt2e.trans.trn-y2e0030-req.mer-no")
	private String merNo;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0030-req.pay-date")
	private String payDate;

	@Y2eField(path = "yt2e.trans.trn-y2e0030-req.sign")
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

	public String getPayDate() {
		return payDate;
	}


	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
	
	public static void main(final String[] args){		
	
	}
}

package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

/**
 * 代付签到请求
 * @author xqc
 *
 */
public class Y2e0001Req extends Y2eXmlEntity{
	@Y2eField(path = "yt2e.trans.trn-y2e0001-req.y2e0001.merNo")
	private String merNo;

	@Y2eField(path = "yt2e.trans.trn-y2e0001-req.y2e0001.loginName")
	private String loginName;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0001-req.y2e0001.pwd")
	private String pwd;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0001-req.y2e0001.custdt")
	private String custdt;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0001-req.y2e0001.batchid")
	private String batchid;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0001-req.sign")
	private String sign;

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public static void main(final String[] args){
		
		Y2e0001Req req =new Y2e0001Req();
		req.setMerNo("988088888888888");
		req.setLoginName("mer1");
		req.setPwd("xxxxxxxxPwd");
		req.setCustdt("20150319000009");
		req.setBatchid("00000001");
		req.setSign("sigggggggggggggnnn");
		String xmlStr=req.createXml();
		System.out.println("------xmlStr------"+xmlStr);
		
		
	}
	


	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getCustdt() {
		return custdt;
	}

	public void setCustdt(String custdt) {
		this.custdt = custdt;
	}

	public String getBatchid() {
		return batchid;
	}

	public void setBatchid(String batchid) {
		this.batchid = batchid;
	}

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
}

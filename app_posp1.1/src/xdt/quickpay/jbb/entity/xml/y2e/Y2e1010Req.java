package xdt.quickpay.jbb.entity.xml.y2e;



import java.util.ArrayList;
import java.util.List;

import xdt.quickpay.jbb.entity.xml.util.DateUtil;
import xdt.quickpay.jbb.util.Y2eField;
/**
 * 代付请求
 */
public class Y2e1010Req  extends Y2eXmlEntity{
	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.trn-info.mer-no")
	private String merNo;
	
	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.trn-info.pay-tm")
	private String payTm;	
	
	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.trn-info.batch-name")
	private String batchName;	
	
	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.trn-info.batch-no")
	private String batchNo;
	
	
	
	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.trn-info.buss-no")
	private String bussNo;
	
	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.trn-info.procedure-type")
	private String procedureType;	
	
	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.trn-info.tot-cnt")
	private String totCnt;	
	
	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.trn-info.notify-url")
	private String backUrl;	
	

	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.trn-info.mer-insid")
	private String merinsid ;
	
	@Y2eField(path ="yt2e.trans.trn-y2e1010-req.trn-info.pay-type")
	private String payType;
	
	@Y2eField(path ="yt2e.trans.trn-y2e1010-req.trn-info.bank-no")
	private String bankNo;

	@Y2eField(path ="yt2e.trans.trn-y2e1010-req.trn-info.real-name")
	private String realName;
	
	@Y2eField(path ="yt2e.trans.trn-y2e1010-req.trn-info.bank-name")
	private String bankName; 
	

	
	@Y2eField(path ="yt2e.trans.trn-y2e1010-req.trn-info.pay-fee")
	private String payFee;
	
	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.sign")
	private String sign;	

	@Y2eField(path = "yt2e.trans.trn-y2e1010-req.trn-info.tot-amt")
	private String totAmt;	
	
	

	

	
	
	
	

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

	public String getBussNo() {
		return bussNo;
	}

	public void setBussNo(String bussNo) {
		this.bussNo = bussNo;
	}

	public String getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(String procedureType) {
		this.procedureType = procedureType;
	}

	public String getTotAmt() {
		return totAmt;
	}

	public void setTotAmt(String totAmt) {
		this.totAmt = totAmt;
	}
	
	
	public String getTotCnt() {
		return totCnt;
	}

	public void setTotCnt(String totCnt) {
		this.totCnt = totCnt;
	}
	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	

	public String getMerinsid() {
		return merinsid;
	}

	public void setMerinsid(String merinsid) {
		this.merinsid = merinsid;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getPayFee() {
		return payFee;
	}

	public void setPayFee(String payFee) {
		this.payFee = payFee;
	}
	
	

	
	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	/**
	 * @param args Sting[]
	 * @函数说明:
	 * @创建�?zxb
	 * @创建日期:2012-11-12
	 */
	public static void main(final String[] args){		
		Y2e1010Req req =new Y2e1010Req();
		req.setMerNo("988088888888888");
		req.setPayTm(DateUtil.getLongDate());
		req.setBatchName("批量代付-2015济南代付01-(可提现-测试");
		req.setBatchNo("00000001");
		req.setBussNo("123456");
		req.setProcedureType("01");
		req.setTotCnt("2");
		req.setTotAmt("100000");

		
		
		
		String xmlStr=req.createXml();
		System.out.println("------xmlStr------"+xmlStr);
	}
}

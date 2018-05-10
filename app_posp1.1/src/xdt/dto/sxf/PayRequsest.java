package xdt.dto.sxf;

import java.util.List;


public class PayRequsest {

	private String reqData;
	private String reqId;
	private String clientId;
	private String payTyp;
	private String sign;
	private String tranCd;
	private String version;
	private String payItemId;
	
	private String seqNo;
	
	private String payAmt;
	
	private String actNm;
	
	private String actNo;
	
	private String actTyp;
	
	private String bnkCd;
	
	private String bnkNm;
	
	private String lbnkNo;
	
	private String lbnkNm;
	
	private String rmk;
	
	private String smsFlg;
	
	private String tel;
	
	private String bankPayPurpose;

	
	
	public String getTranCd() {
		return tranCd;
	}

	public void setTranCd(String tranCd) {
		this.tranCd = tranCd;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPayItemId() {
		return payItemId;
	}

	public void setPayItemId(String payItemId) {
		this.payItemId = payItemId;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(String payAmt) {
		this.payAmt = payAmt;
	}

	public String getActNm() {
		return actNm;
	}

	public void setActNm(String actNm) {
		this.actNm = actNm;
	}

	public String getActNo() {
		return actNo;
	}

	public void setActNo(String actNo) {
		this.actNo = actNo;
	}

	public String getActTyp() {
		return actTyp;
	}

	public void setActTyp(String actTyp) {
		this.actTyp = actTyp;
	}

	public String getBnkCd() {
		return bnkCd;
	}

	public void setBnkCd(String bnkCd) {
		this.bnkCd = bnkCd;
	}

	public String getBnkNm() {
		return bnkNm;
	}

	public void setBnkNm(String bnkNm) {
		this.bnkNm = bnkNm;
	}

	public String getLbnkNo() {
		return lbnkNo;
	}

	public void setLbnkNo(String lbnkNo) {
		this.lbnkNo = lbnkNo;
	}

	public String getLbnkNm() {
		return lbnkNm;
	}

	public void setLbnkNm(String lbnkNm) {
		this.lbnkNm = lbnkNm;
	}

	public String getRmk() {
		return rmk;
	}

	public void setRmk(String rmk) {
		this.rmk = rmk;
	}

	public String getSmsFlg() {
		return smsFlg;
	}

	public void setSmsFlg(String smsFlg) {
		this.smsFlg = smsFlg;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getBankPayPurpose() {
		return bankPayPurpose;
	}

	public void setBankPayPurpose(String bankPayPurpose) {
		this.bankPayPurpose = bankPayPurpose;
	}

	public String getReqData() {
		return reqData;
	}
	public void setReqData(String reqData) {
		this.reqData = reqData;
	}
	public String getReqId() {
		return reqId;
	}
	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getPayTyp() {
		return payTyp;
	}
	public void setPayTyp(String payTyp) {
		this.payTyp = payTyp;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "PayRequsest [reqData=" + reqData + ", reqId=" + reqId
				+ ", clientId=" + clientId + ", payTyp=" + payTyp + ", sign="
				+ sign + "]";
	}
	
}

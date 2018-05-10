package xdt.dto.sxf;

import java.util.List;

public class DF1003Request implements java.io.Serializable{
	
	private String payTyp;
	
	private String totalPayCount;
	
	private String totalPayAmt;
	
	private List<PayItems> payItems;
	
	public static class PayItems{
		
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
		
	}
	
	public String getPayTyp() {
		return payTyp;
	}
	public void setPayTyp(String payTyp) {
		this.payTyp = payTyp;
	}
	public String getTotalPayCount() {
		return totalPayCount;
	}
	public void setTotalPayCount(String totalPayCount) {
		this.totalPayCount = totalPayCount;
	}
	public String getTotalPayAmt() {
		return totalPayAmt;
	}
	public void setTotalPayAmt(String totalPayAmt) {
		this.totalPayAmt = totalPayAmt;
	}
	public List<PayItems> getPayItems() {
		return payItems;
	}
	public void setPayItems(List<PayItems> payItems) {
		this.payItems = payItems;
	}

}

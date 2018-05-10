package xdt.pufa.base;

public class Head {

	private String tran_cd;
	private String version;
	private String prod_cd;
	private String biz_cd;
	private String tran_dt_tm;
	private String signed_str;

	public String getTran_cd() {
		return tran_cd;
	}

	public void setTran_cd(String tran_cd) {
		this.tran_cd = tran_cd;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProd_cd() {
		return prod_cd;
	}

	public void setProd_cd(String prod_cd) {
		this.prod_cd = prod_cd;
	}

	public String getBiz_cd() {
		return biz_cd;
	}

	public void setBiz_cd(String biz_cd) {
		this.biz_cd = biz_cd;
	}

	public String getTran_dt_tm() {
		return tran_dt_tm;
	}

	public void setTran_dt_tm(String tran_dt_tm) {
		this.tran_dt_tm = tran_dt_tm;
	}

	public String getSigned_str() {
		return signed_str;
	}

	public void setSigned_str(String signed_str) {
		this.signed_str = signed_str;
	}
}

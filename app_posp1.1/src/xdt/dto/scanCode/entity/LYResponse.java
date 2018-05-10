package xdt.dto.scanCode.entity;

public class LYResponse {

	private String code;
	private String info;
	private String p3_uno;
	private String p3_orderno;
	private String p3_money;
	private String p3_type;
	private String p3_note;
	private String p3_sysno;
	private String sign;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getP3_uno() {
		return p3_uno;
	}
	public void setP3_uno(String p3_uno) {
		this.p3_uno = p3_uno;
	}
	public String getP3_orderno() {
		return p3_orderno;
	}
	public void setP3_orderno(String p3_orderno) {
		this.p3_orderno = p3_orderno;
	}
	public String getP3_money() {
		return p3_money;
	}
	public void setP3_money(String p3_money) {
		this.p3_money = p3_money;
	}
	public String getP3_type() {
		return p3_type;
	}
	public void setP3_type(String p3_type) {
		this.p3_type = p3_type;
	}
	public String getP3_note() {
		return p3_note;
	}
	public void setP3_note(String p3_note) {
		this.p3_note = p3_note;
	}
	public String getP3_sysno() {
		return p3_sysno;
	}
	public void setP3_sysno(String p3_sysno) {
		this.p3_sysno = p3_sysno;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "LYResponse [code=" + code + ", info=" + info + ", p3_uno=" + p3_uno + ", p3_orderno=" + p3_orderno
				+ ", p3_money=" + p3_money + ", p3_type=" + p3_type + ", p3_note=" + p3_note + ", p3_sysno=" + p3_sysno
				+ ", sign=" + sign + "]";
	}
	
	
}

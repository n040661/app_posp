package xdt.dto.tfb;

public class CardPayApplyResponse {

	private String retcode;//结果
	private String retmsg;//结果描述
	private String spid;//商户号
	private String spbillno;//支付订单号
	private String listid;//国采支付单号
	private String money;//订单交易金额
	private String cur_type;//金额类型
	private String result;//交易结果
	private String pay_type;//交易类型
	private String user_type;//用户类型
	private String attach;//附加数据
	private String sign;//签名
	private String encode_type;//签名类型
	private String cipher_data;//异步发送RSA签名字符串
	
	public String getCipher_data() {
		return cipher_data;
	}
	public void setCipher_data(String cipher_data) {
		this.cipher_data = cipher_data;
	}
	public String getRetcode() {
		return retcode;
	}
	public void setRetcode(String retcode) {
		this.retcode = retcode;
	}
	public String getRetmsg() {
		return retmsg;
	}
	public void setRetmsg(String retmsg) {
		this.retmsg = retmsg;
	}
	public String getSpid() {
		return spid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public String getSpbillno() {
		return spbillno;
	}
	public void setSpbillno(String spbillno) {
		this.spbillno = spbillno;
	}
	public String getListid() {
		return listid;
	}
	public void setListid(String listid) {
		this.listid = listid;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getCur_type() {
		return cur_type;
	}
	public void setCur_type(String cur_type) {
		this.cur_type = cur_type;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getEncode_type() {
		return encode_type;
	}
	public void setEncode_type(String encode_type) {
		this.encode_type = encode_type;
	}
	@Override
	public String toString() {
		return "CardPayApplyＲesponse [retcode=" + retcode + ", retmsg="
				+ retmsg + ", spid=" + spid + ", spbillno=" + spbillno
				+ ", listid=" + listid + ", money=" + money + ", cur_type="
				+ cur_type + ", result=" + result + ", pay_type=" + pay_type
				+ ", user_type=" + user_type + ", attach=" + attach + ", sign="
				+ sign + ", encode_type=" + encode_type + ", cipher_data="
				+ cipher_data + ", getCipher_data()=" + getCipher_data()
				+ ", getRetcode()=" + getRetcode() + ", getRetmsg()="
				+ getRetmsg() + ", getSpid()=" + getSpid() + ", getSpbillno()="
				+ getSpbillno() + ", getListid()=" + getListid()
				+ ", getMoney()=" + getMoney() + ", getCur_type()="
				+ getCur_type() + ", getResult()=" + getResult()
				+ ", getPay_type()=" + getPay_type() + ", getUser_type()="
				+ getUser_type() + ", getAttach()=" + getAttach()
				+ ", getSign()=" + getSign() + ", getEncode_type()="
				+ getEncode_type() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
	
}

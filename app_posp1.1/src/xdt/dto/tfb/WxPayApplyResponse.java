package xdt.dto.tfb;

public class WxPayApplyResponse {

	private String sign_type;//签名方式
	private String ver;//接口版本
	private String input_charset;//字符集
	private String sign;//签名
	private String sign_key_index;//密钥序号
	private String retcode;//返回状态码，返回状态码，0表示成功，其它参照错误码描述 注：刷卡支付返回205235表示处理中，需反查。
	private String retmsg;//返回信息
	private String spid;//商户号
	private String listid;//天付宝单号
	private String sp_billno;//商户订单号
	private String pay_type;//支付类型
	private String qrcode;//二维码信息
	private String pay_info;//支付地址
	private String merch_listid;//平台商户单号
	private String tran_amt;//交易金额
	private String cur_type;//币种类型
	private String sysd_time;//系统交易时间
	private String tran_state;//交易状态
	private String item_name;
	private String notify_type;
	private String tran_time; 
	private String spbillno;//网关支付订单
	private String money;//网关支付金额
	private String result;//支付结果
	private String user_type;//用户类型
	private String cipher_data;//签名参数
	
	
	public String getCipher_data() {
		return cipher_data;
	}
	public void setCipher_data(String cipher_data) {
		this.cipher_data = cipher_data;
	}
	public String getSpbillno() {
		return spbillno;
	}
	public void setSpbillno(String spbillno) {
		this.spbillno = spbillno;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getNotify_type() {
		return notify_type;
	}
	public void setNotify_type(String notify_type) {
		this.notify_type = notify_type;
	}
	public String getTran_time() {
		return tran_time;
	}
	public void setTran_time(String tran_time) {
		this.tran_time = tran_time;
	}
	public String getTran_state() {
		return tran_state;
	}
	public void setTran_state(String tran_state) {
		this.tran_state = tran_state;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getInput_charset() {
		return input_charset;
	}
	public void setInput_charset(String input_charset) {
		this.input_charset = input_charset;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSign_key_index() {
		return sign_key_index;
	}
	public void setSign_key_index(String sign_key_index) {
		this.sign_key_index = sign_key_index;
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
	public String getListid() {
		return listid;
	}
	public void setListid(String listid) {
		this.listid = listid;
	}
	public String getSp_billno() {
		return sp_billno;
	}
	public void setSp_billno(String sp_billno) {
		this.sp_billno = sp_billno;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public String getPay_info() {
		return pay_info;
	}
	public void setPay_info(String pay_info) {
		this.pay_info = pay_info;
	}
	public String getMerch_listid() {
		return merch_listid;
	}
	public void setMerch_listid(String merch_listid) {
		this.merch_listid = merch_listid;
	}
	public String getTran_amt() {
		return tran_amt;
	}
	public void setTran_amt(String tran_amt) {
		this.tran_amt = tran_amt;
	}
	public String getCur_type() {
		return cur_type;
	}
	public void setCur_type(String cur_type) {
		this.cur_type = cur_type;
	}
	public String getSysd_time() {
		return sysd_time;
	}
	public void setSysd_time(String sysd_time) {
		this.sysd_time = sysd_time;
	}
	@Override
	public String toString() {
		return "WxPayApplyResponse [sign_type=" + sign_type + ", ver=" + ver
				+ ", input_charset=" + input_charset + ", sign=" + sign
				+ ", sign_key_index=" + sign_key_index + ", retcode=" + retcode
				+ ", retmsg=" + retmsg + ", spid=" + spid + ", listid="
				+ listid + ", sp_billno=" + sp_billno + ", pay_type="
				+ pay_type + ", qrcode=" + qrcode + ", pay_info=" + pay_info
				+ ", merch_listid=" + merch_listid + ", tran_amt=" + tran_amt
				+ ", cur_type=" + cur_type + ", sysd_time=" + sysd_time + "]";
	}
	
	
	
}

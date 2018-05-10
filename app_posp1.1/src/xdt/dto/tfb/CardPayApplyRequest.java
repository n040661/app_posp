package xdt.dto.tfb;

public class CardPayApplyRequest{
	
	private String spid;//商户号
	private String sp_userid;//用户名
	private String spbillno;//支付订单号
	private String money;//订单交易金额
	private String cur_type;//金额类型
	private String return_url;//页面回调地址
	private String notify_url;//后台回调地址
	private String errpage_url;//错误页面回调地址
	private String memo;//订单备注
	private String expire_time;//订单有效时长
	private String attach;//附加数据
	private String card_type;//银行卡类型
	private String bank_segment;//银行代号
	private String user_type;//用户类型
	private String channel;//渠道类型
	private String sign;//签名
	private String encode_type;//签名类型
	private String risk_ctrl;//风险控制数据
	private String url;
	private String reUrl;
	
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getReUrl() {
		return reUrl;
	}
	public void setReUrl(String reUrl) {
		this.reUrl = reUrl;
	}
	public String getSpid() {
		return spid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public String getSp_userid() {
		return sp_userid;
	}
	public void setSp_userid(String sp_userid) {
		this.sp_userid = sp_userid;
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
	public String getCur_type() {
		return cur_type;
	}
	public void setCur_type(String cur_type) {
		this.cur_type = cur_type;
	}
	public String getReturn_url() {
		return return_url;
	}
	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getErrpage_url() {
		return errpage_url;
	}
	public void setErrpage_url(String errpage_url) {
		this.errpage_url = errpage_url;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getExpire_time() {
		return expire_time;
	}
	public void setExpire_time(String expire_time) {
		this.expire_time = expire_time;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public String getCard_type() {
		return card_type;
	}
	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}
	public String getBank_segment() {
		return bank_segment;
	}
	public void setBank_segment(String bank_segment) {
		this.bank_segment = bank_segment;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
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
	public String getRisk_ctrl() {
		return risk_ctrl;
	}
	public void setRisk_ctrl(String risk_ctrl) {
		this.risk_ctrl = risk_ctrl;
	}
	@Override
	public String toString() {
		return "CardPayApplyRequest [spid=" + spid + ", sp_userid=" + sp_userid
				+ ", spbillno=" + spbillno + ", money=" + money + ", cur_type="
				+ cur_type + ", return_url=" + return_url + ", notify_url="
				+ notify_url + ", errpage_url=" + errpage_url + ", memo="
				+ memo + ", expire_time=" + expire_time + ", attach=" + attach
				+ ", card_type=" + card_type + ", bank_segment=" + bank_segment
				+ ", user_type=" + user_type + ", channel=" + channel
				+ ", sign=" + sign + ", encode_type=" + encode_type
				+ ", risk_ctrl=" + risk_ctrl + ", url=" + url + ", reUrl="
				+ reUrl + "]";
	}
	
	









	
	
	
	
}

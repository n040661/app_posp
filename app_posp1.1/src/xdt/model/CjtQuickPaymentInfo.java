package xdt.model;

import java.math.BigDecimal;

public class CjtQuickPaymentInfo {
	
    private String outer_trade_no;    //订单号
	
	private String trade_amount;  //交易金额
	
	private BigDecimal user_poundage;  //用户手续费
	
	private BigDecimal mer_poundage; //商户手续费
	
	private String product_name; //商品名称
	
	private String action_desc; //交易描述
	
	private  String sell_id;   //卖家ID
	
	private String sell_id_type; //卖家ID类型
	
	private String sell_moble;   //卖家手机号
	
	private String notify_url;   //异步通知地址
	
	private String expred_time;  //支付过期时间
	
	private String order_time;   //订单提交时间
	
	private String buyer_id;     //买家ID
	
	private String buyer_id_type; //买家ID类型
	
	private String buyer_moble; //买家手机号
	
	private String buyer_ip;  //用户下单时的IP地址
	
	private String card_type; // 卡类型       借记：DC；贷记：CC
	
	private String pay_type;  //对公对私     对公：B；对私：C
	
	private String bank_code;  //银行编码
	
	private String payer_name;   //付款方名称
	  
	private String payer_card_no; //付款方银行卡号
	
	private String id_number;    //身份证号
	
	private String phone_number; //手机号
	
	private String expiry_date; //贷记卡有效期
	
	private String cvv2;    //CVV2码
	
	private String user_sign; //商户网站唯一标示
	
	private String royalty_parameters; //交易金额分润账户集
	
	private String ext; //扩展字段

	public String getOuter_trade_no() {
		return outer_trade_no;
	}

	public void setOuter_trade_no(String outer_trade_no) {
		this.outer_trade_no = outer_trade_no;
	}

	public String getTrade_amount() {
		return trade_amount;
	}

	public void setTrade_amount(String trade_amount) {
		this.trade_amount = trade_amount;
	}

	public BigDecimal getUser_poundage() {
		return user_poundage;
	}

	public void setUser_poundage(BigDecimal user_poundage) {
		this.user_poundage = user_poundage;
	}

	public BigDecimal getMer_poundage() {
		return mer_poundage;
	}

	public void setMer_poundage(BigDecimal mer_poundage) {
		this.mer_poundage = mer_poundage;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getAction_desc() {
		return action_desc;
	}

	public void setAction_desc(String action_desc) {
		this.action_desc = action_desc;
	}

	public String getSell_id() {
		return sell_id;
	}

	public void setSell_id(String sell_id) {
		this.sell_id = sell_id;
	}

	public String getSell_id_type() {
		return sell_id_type;
	}

	public void setSell_id_type(String sell_id_type) {
		this.sell_id_type = sell_id_type;
	}

	public String getSell_moble() {
		return sell_moble;
	}

	public void setSell_moble(String sell_moble) {
		this.sell_moble = sell_moble;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getExpred_time() {
		return expred_time;
	}

	public void setExpred_time(String expred_time) {
		this.expred_time = expred_time;
	}

	public String getOrder_time() {
		return order_time;
	}

	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}

	public String getBuyer_id() {
		return buyer_id;
	}

	public void setBuyer_id(String buyer_id) {
		this.buyer_id = buyer_id;
	}

	public String getBuyer_id_type() {
		return buyer_id_type;
	}

	public void setBuyer_id_type(String buyer_id_type) {
		this.buyer_id_type = buyer_id_type;
	}

	public String getBuyer_moble() {
		return buyer_moble;
	}

	public void setBuyer_moble(String buyer_moble) {
		this.buyer_moble = buyer_moble;
	}

	public String getBuyer_ip() {
		return buyer_ip;
	}

	public void setBuyer_ip(String buyer_ip) {
		this.buyer_ip = buyer_ip;
	}

	public String getCard_type() {
		return card_type;
	}

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public String getPayer_name() {
		return payer_name;
	}

	public void setPayer_name(String payer_name) {
		this.payer_name = payer_name;
	}

	public String getPayer_card_no() {
		return payer_card_no;
	}

	public void setPayer_card_no(String payer_card_no) {
		this.payer_card_no = payer_card_no;
	}

	public String getId_number() {
		return id_number;
	}

	public void setId_number(String id_number) {
		this.id_number = id_number;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(String expiry_date) {
		this.expiry_date = expiry_date;
	}

	public String getCvv2() {
		return cvv2;
	}

	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}

	public String getUser_sign() {
		return user_sign;
	}

	public void setUser_sign(String user_sign) {
		this.user_sign = user_sign;
	}

	public String getRoyalty_parameters() {
		return royalty_parameters;
	}

	public void setRoyalty_parameters(String royalty_parameters) {
		this.royalty_parameters = royalty_parameters;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}
	public String toString() {
		return "ChangjiePayInfo [outer_trade_no=" + outer_trade_no
				+ ", trade_amount=" + trade_amount + ", user_poundage="
				+ user_poundage + ", mer_poundage=" + mer_poundage
				+ ", product_name=" + product_name + ", action_desc="
				+ action_desc + ", sell_id=" + sell_id + ", sell_id_type="
				+ sell_id_type + ", sell_moble=" + sell_moble + ", notify_url="
				+ notify_url + ", expred_time=" + expred_time + ", order_time="
				+ order_time + ", buyer_id=" + buyer_id + ", buyer_id_type="
				+ buyer_id_type + ", buyer_moble=" + buyer_moble
				+ ", buyer_ip=" + buyer_ip + ", card_type=" + card_type
				+ ", pay_type=" + pay_type + ", bank_code=" + bank_code
				+ ", payer_name=" + payer_name + ", payer_card_no="
				+ payer_card_no + ", id_number=" + id_number
				+ ", phone_number=" + phone_number + ", expiry_date="
				+ expiry_date + ", cvv2=" + cvv2 + ", user_sign=" + user_sign
				+ ", royalty_parameters=" + royalty_parameters + ", ext=" + ext
				+ "]";
	}

}

package xdt.quickpay.cjt.entity;

import java.math.BigDecimal;

/**
 * @ClassName: CjtRequestEntity
 * @Description: 畅捷支付请求信息
 * @author 尚延超
 * @date 2016年10月25日 
 *
 */
public class CjtRequestEntity {
	
	private String pageurl;// 支付完成后，跳转到商户方的页面
	
    private String transactionid;    //订单号
	
	private String orderamount;  //交易金额
	
	private String user_poundage;  //用户手续费
	
	private String mer_poundage; //商户手续费
	
	private String productname; //商品名称
	
	private String action_desc; //交易描述
	
	private String sell_id;   //卖家ID
	
	private String sell_id_type; //卖家ID类型
	
	private String sell_moble;   //卖家手机号
	
	private String bgurl;   //服务器接受支付结果的后台地址
	
	private String expred_time;  //支付过期时间
	
	private String ordertime;   //订单提交时间
	
	private String buyer_id;     //买家ID
	
	private String buyer_id_type; //买家ID类型
	
	private String buyer_moble; //买家手机号
	
	private String buyer_ip;  //用户下单时的IP地址
	
	private String card_type; // 卡类型       借记：DC；贷记：CC
	
	private String pay_type;  //对公对私     对公：B；对私：C
	
	private String bank_code;  //银行编码
	
	private String payer_name;   //付款方名称
	  
	private String bankno; //付款方银行卡号
	
	private String id_number;    //身份证号
	
	private String phone_number; //手机号
	
	private String expiry_date; //贷记卡有效期
	
	private String cvv2;    //CVV2码
	
	private String user_sign; //商户网站唯一标示
	
	private String royalty_parameters; //交易金额分润账户集
	
	private String ext; //扩展字段
	
	private String pid;// 合作伙伴用户编号
	
	private String signmsg;// 签名
	
	private String payresult;// 处理结果
	
	private String result;
	
	private String error; //错误原因

	public String getPageurl() {
		return pageurl;
	}

	public void setPageurl(String pageurl) {
		this.pageurl = pageurl;
	}

	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public String getOrderamount() {
		return orderamount;
	}

	public void setOrderamount(String orderamount) {
		this.orderamount = orderamount;
	}

	public String getUser_poundage() {
		return user_poundage;
	}

	public void setUser_poundage(String user_poundage) {
		this.user_poundage = user_poundage;
	}

	public String getMer_poundage() {
		return mer_poundage;
	}

	public void setMer_poundage(String mer_poundage) {
		this.mer_poundage = mer_poundage;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
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

	public String getBgurl() {
		return bgurl;
	}

	public void setBgurl(String bgurl) {
		this.bgurl = bgurl;
	}

	public String getExpred_time() {
		return expred_time;
	}

	public void setExpred_time(String expred_time) {
		this.expred_time = expred_time;
	}

	public String getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
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

	public String getBankno() {
		return bankno;
	}

	public void setBankno(String bankno) {
		this.bankno = bankno;
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

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getSignmsg() {
		return signmsg;
	}

	public void setSignmsg(String signmsg) {
		this.signmsg = signmsg;
	}

	public String getPayresult() {
		return payresult;
	}

	public void setPayresult(String payresult) {
		this.payresult = payresult;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
    
    
}

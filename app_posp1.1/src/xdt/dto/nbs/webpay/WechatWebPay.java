package xdt.dto.nbs.webpay;

public class WechatWebPay {
	
	/**
     * 商户号ID
     */
    
    private String merchantId;
    /**
     * 商品描述
     */
    private String body;
    /**
     * 商户订单号
     */
    private String out_trade_no;
    /**
     * 订单金额
     */
    private String total_fee;
    /**
     * 通知地址
     */
    private String notify_url;
    /**
     * 自定义支付完成跳转页面
     */
    private String callback_url;
    /**
     * 签名
     */
    private String sign;
    
    private String attach;//附加信息
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public String getCallback_url() {
		return callback_url;
	}
	public void setCallback_url(String callback_url) {
		this.callback_url = callback_url;
	}
	@Override
	public String toString() {
		return "WechatWebPay [merchantId=" + merchantId + ", body=" + body + ", out_trade_no=" + out_trade_no
				+ ", total_fee=" + total_fee + ", notify_url=" + notify_url + ", callback_url=" + callback_url
				+ ", sign=" + sign + ", attach=" + attach + "]";
	}
	
    

}

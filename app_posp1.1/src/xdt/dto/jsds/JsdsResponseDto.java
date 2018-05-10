package xdt.dto.jsds;

/**
 * 
 * @Description 江苏电商请求响应 
 * @author Shiwen .Li
 * @date 2017年3月5日 下午2:55:46 
 * @version V1.3.1
 */
public class JsdsResponseDto extends CommonDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//微信支付
	private String orderNum;//合作商订单号
	private String pl_orderNum;	//平台订单号
	private String pl_payState;//微交易状态
	private String pl_payMessage;//交易说明
	private String pl_bankCardType; //银行卡类型,如CERDIT，DEBIT，CFT，UNKNOWN（目前仅微信支付类支持)		
	private String pl_amount; //交易金额	
	private String sign;// 数据的签名字符串
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getPl_orderNum() {
		return pl_orderNum;
	}
	public void setPl_orderNum(String pl_orderNum) {
		this.pl_orderNum = pl_orderNum;
	}
	public String getPl_payState() {
		return pl_payState;
	}
	public void setPl_payState(String pl_payState) {
		this.pl_payState = pl_payState;
	}
	public String getPl_payMessage() {
		return pl_payMessage;
	}
	public void setPl_payMessage(String pl_payMessage) {
		this.pl_payMessage = pl_payMessage;
	}
	public String getPl_bankCardType() {
		return pl_bankCardType;
	}
	public void setPl_bankCardType(String pl_bankCardType) {
		this.pl_bankCardType = pl_bankCardType;
	}
	public String getPl_amount() {
		return pl_amount;
	}
	public void setPl_amount(String pl_amount) {
		this.pl_amount = pl_amount;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
}

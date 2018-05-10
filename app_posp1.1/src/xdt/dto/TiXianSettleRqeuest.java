package xdt.dto;

/**
 * 提现调用  清算系统请求
 * @author wm
 * 2016-01-27
 */
public class TiXianSettleRqeuest {
	private String orderNumber; // 付呗订单
	private String input_charset; // 请求中文参数的字符编码 参数值的编码为GBK
	private String sign; // 签名结果
	private String sign_method; // 签名方法 签名算法为MD5
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getInput_charset() {
		return input_charset;
	}
	public void setInput_charset(String inputCharset) {
		input_charset = inputCharset;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSign_method() {
		return sign_method;
	}
	public void setSign_method(String signMethod) {
		sign_method = signMethod;
	}
	
	
}

package xdt.quickpay.cjt.entity;

/**
 * @ClassName: CjtQueryRequestEntity
 * @Description: 畅捷确认支付请求信息
 * @author 尚延超
 * @date 2016年10月25日 
 *
 */
public class CjtQueryRequestEntity {
	
	private String out_trade_no;  //订单号
	
	private String verification_code; //短信验证码

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getVerification_code() {
		return verification_code;
	}

	public void setVerification_code(String verification_code) {
		this.verification_code = verification_code;
	}

	@Override
	public String toString() {
		return "CjtQuickPaymentConfirmInfo [out_trade_no=" + out_trade_no
				+ ", verification_code=" + verification_code + "]";
	}

}

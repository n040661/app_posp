package xdt.quickpay.cjt.entity;

/**
 * @ClassName: CjtResponseEntity
 * @Description: 畅捷支付响应信息
 * @author 尚延超
 * @date 2016年10月25日 
 *
 */
public class CjtResponseEntity {
	
	private String out_trade_no;   //订单号
	
	private String authenticate_status; //鉴权是否成功 0成功1失败
	
	private String err_msg; //鉴权失败原因

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getAuthenticate_status() {
		return authenticate_status;
	}

	public void setAuthenticate_status(String authenticate_status) {
		this.authenticate_status = authenticate_status;
	}

	public String getErr_msg() {
		return err_msg;
	}

	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}
	public String toString() {
		return "CjtResponseEntity [out_trade_no=" + out_trade_no
				+ ", authenticate_status=" + authenticate_status + ", err_msg="
				+ err_msg + "]";
	}
}

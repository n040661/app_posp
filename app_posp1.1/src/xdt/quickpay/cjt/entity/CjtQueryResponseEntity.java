package xdt.quickpay.cjt.entity;

/**
 * @ClassName: CjtQueryResponseEntity
 * @Description: 畅捷确认支付响应信息
 * @author 尚延超
 * @date 2016年10月25日 
 *
 */
public class CjtQueryResponseEntity {

	private String outer_trade_no; //订单号
	
	private String trade_status; //支付状态 0支付成功 1支付处理中   2支付失败
	
	private String err_msg; //交易失败原因

	public String getOuter_trade_no() {
		return outer_trade_no;
	}
	public void setOuter_trade_no(String outer_trade_no) {
		this.outer_trade_no = outer_trade_no;
	}

	public String getTrade_status() {
		return trade_status;
	}

	public void setTrade_status(String trade_status) {
		this.trade_status = trade_status;
	}

	public String getErr_msg() {
		return err_msg;
	}

	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}
	public String toString() {
		return "CjtQueryResponseEntity [outer_trade_no=" + outer_trade_no
				+ ", trade_status=" + trade_status + ", err_msg=" + err_msg
				+ "]";
	}
	
}

package xdt.dto.nbs.settle;

import xdt.dto.nbs.AbstractBase;
import xdt.dto.nbs.base.WechatResponseBase;


/**
 * 下游商户T0结算查询返回信息
 *
 * @author YanChao.Shang
 * @version v1.0
 * @date 2016年10月14日 下午4:06:15
 */
public class SettleQueryWebPayResponse extends WechatResponseBase {

    /**
     * 返回码
     */
    private String return_code;
    /**
     * 返回信息
     */
    private String return_msg;
    /**
     * 商户订单号
     */
    private String out_trade_no;
    /**
     * 结算流水号
     */
    private String settle_num;
    /**
     * 结算状态
     */
    private String settle_status;
    /**
     * 交易金额
     */
    private String trade_amount;
    /**
     * 实结金额
     */
    private String actual_amount;
    /**
     * 结算手续费
     */
    private String settle_fee;
    /**
     * 受理时间
     */
    private String accept_time;
    /**
     * 完成时间
     */
    private String complete_time;
    /**
     * 签名
     */
    private String sign;
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
	public String getReturn_msg() {
		return return_msg;
	}
	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getSettle_num() {
		return settle_num;
	}
	public void setSettle_num(String settle_num) {
		this.settle_num = settle_num;
	}
	public String getSettle_status() {
		return settle_status;
	}
	public void setSettle_status(String settle_status) {
		this.settle_status = settle_status;
	}
	public String getTrade_amount() {
		return trade_amount;
	}
	public void setTrade_amount(String trade_amount) {
		this.trade_amount = trade_amount;
	}
	public String getActual_amount() {
		return actual_amount;
	}
	public void setActual_amount(String actual_amount) {
		this.actual_amount = actual_amount;
	}
	public String getSettle_fee() {
		return settle_fee;
	}
	public void setSettle_fee(String settle_fee) {
		this.settle_fee = settle_fee;
	}
	public String getAccept_time() {
		return accept_time;
	}
	public void setAccept_time(String accept_time) {
		this.accept_time = accept_time;
	}
	public String getComplete_time() {
		return complete_time;
	}
	public void setComplete_time(String complete_time) {
		this.complete_time = complete_time;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
    
}

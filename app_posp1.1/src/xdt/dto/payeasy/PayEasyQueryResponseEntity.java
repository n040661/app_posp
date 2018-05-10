package xdt.dto.payeasy;

/**
 * @ClassName: PayQueryResponseEntity
 * @Description: 首信易查询响应信息
 * @author YanChao.Shang
 * @date 2017年4月5日 上午10:13:56
 *
 */
public class PayEasyQueryResponseEntity {

	private String v_status; // 响应状态

	private String v_desc; // 响应状态描述

	private String v_mid; // 商户编号

	private String v_oid; // 订单编号

	private String v_pmode; // 支付方式

	private String v_pstatus; // 支付状态

	private String v_pstring; // 支付结果说明

	private String v_amount; // 订单金额

	private String v_moneytype;// 订单币种

	private String v_isvirement; // 订单是否已转账

	private String v_sign; // 数字签名

	public String getV_status() {
		return v_status;
	}

	public void setV_status(String v_status) {
		this.v_status = v_status;
	}

	public String getV_desc() {
		return v_desc;
	}

	public void setV_desc(String v_desc) {
		this.v_desc = v_desc;
	}

	public String getV_mid() {
		return v_mid;
	}

	public void setV_mid(String v_mid) {
		this.v_mid = v_mid;
	}

	public String getV_oid() {
		return v_oid;
	}

	public void setV_oid(String v_oid) {
		this.v_oid = v_oid;
	}

	public String getV_pmode() {
		return v_pmode;
	}

	public void setV_pmode(String v_pmode) {
		this.v_pmode = v_pmode;
	}

	public String getV_pstatus() {
		return v_pstatus;
	}

	public void setV_pstatus(String v_pstatus) {
		this.v_pstatus = v_pstatus;
	}

	public String getV_pstring() {
		return v_pstring;
	}

	public void setV_pstring(String v_pstring) {
		this.v_pstring = v_pstring;
	}

	public String getV_amount() {
		return v_amount;
	}

	public void setV_amount(String v_amount) {
		this.v_amount = v_amount;
	}

	public String getV_moneytype() {
		return v_moneytype;
	}

	public void setV_moneytype(String v_moneytype) {
		this.v_moneytype = v_moneytype;
	}

	public String getV_isvirement() {
		return v_isvirement;
	}

	public void setV_isvirement(String v_isvirement) {
		this.v_isvirement = v_isvirement;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}

	@Override
	public String toString() {
		return "PayEasyQueryResponseEntity [v_status=" + v_status + ", v_desc=" + v_desc + ", v_mid=" + v_mid
				+ ", v_oid=" + v_oid + ", v_pmode=" + v_pmode + ", v_pstatus=" + v_pstatus + ", v_pstring=" + v_pstring
				+ ", v_amount=" + v_amount + ", v_moneytype=" + v_moneytype + ", v_isvirement=" + v_isvirement
				+ ", v_sign=" + v_sign + "]";
	}

}

package xdt.dto.payeasy;

import java.math.BigDecimal;

/**
 * 
 * @Description 首信易支付响应实体 
 * @author YanChao.Shang
 * @date 2017年4月1日 下午12:08:55 
 * @version V1.3.1
 */
public class PayEasyResponseEntitys {
	
	private String v_count; //订单个数
	
	private String v_oid; //订单编号组

	private String v_pmode; //订单支付方式组
	
	private Integer v_pstatus; //支付状态组
	
	private String v_pstring; //支付结果信息组
	
	private BigDecimal v_amount;//订单实际支付金额
	
	private Integer v_moneytype;//订单实际支付币种
	
	private String v_mac;//数字签名
	
	private String v_md5money;//商城数据签名
	
	private String v_sign;//商城数据签名

	public String getV_count() {
		return v_count;
	}

	public void setV_count(String v_count) {
		this.v_count = v_count;
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

	public Integer getV_pstatus() {
		return v_pstatus;
	}

	public void setV_pstatus(Integer v_pstatus) {
		this.v_pstatus = v_pstatus;
	}

	public String getV_pstring() {
		return v_pstring;
	}

	public void setV_pstring(String v_pstring) {
		this.v_pstring = v_pstring;
	}

	public BigDecimal getV_amount() {
		return v_amount;
	}

	public void setV_amount(BigDecimal v_amount) {
		this.v_amount = v_amount;
	}

	public Integer getV_moneytype() {
		return v_moneytype;
	}

	public void setV_moneytype(Integer v_moneytype) {
		this.v_moneytype = v_moneytype;
	}

	public String getV_mac() {
		return v_mac;
	}

	public void setV_mac(String v_mac) {
		this.v_mac = v_mac;
	}

	public String getV_md5money() {
		return v_md5money;
	}

	public void setV_md5money(String v_md5money) {
		this.v_md5money = v_md5money;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}
	
	

}

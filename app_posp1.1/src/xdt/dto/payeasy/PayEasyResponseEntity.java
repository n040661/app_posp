package xdt.dto.payeasy;

import java.math.BigDecimal;

/**
 * 
 * @Description 首信易支付响应实体 
 * @author YanChao.Shang
 * @date 2017年4月1日 下午12:08:55 
 * @version V1.3.1
 */
public class PayEasyResponseEntity {
	
	private String v_url; //返回商户页面地址
	
	private String v_oid; //订单编号

	private String v_pmode; //订单支付方式
	
	private Integer v_pstatus; //支付状态
	
	private String v_pstring; //支付结果
	
	private String v_md5info; //数字签名
	
	private BigDecimal v_amount;//支付金额
	
	private Integer v_moneytype;//支付币种
	
	private String v_md5money;//数字签名
	
	private String v_sign;//商城数据签名
	
	
	public String getV_url() {
		return v_url;
	}

	public void setV_url(String v_url) {
		this.v_url = v_url;
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

	public String getV_md5info() {
		return v_md5info;
	}

	public void setV_md5info(String v_md5info) {
		this.v_md5info = v_md5info;
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

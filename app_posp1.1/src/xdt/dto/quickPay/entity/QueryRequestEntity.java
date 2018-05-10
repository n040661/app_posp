package xdt.dto.quickPay.entity;

/**
 * 
 * @Description 快捷查询请求信息
 * @author YanChao.Shang
 * @date 2018年1月05日 下午12:28:08
 * @version V1.3.1
 */

public class QueryRequestEntity {

	private String v_version; // 版本号

	private String v_mid; // 商户号

	private String v_oid; // 订单号

	private String v_type; // 交易类型

	private String v_sign; // 签名

	public String getV_version() {
		return v_version;
	}

	public void setV_version(String v_version) {
		this.v_version = v_version;
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

	public String getV_type() {
		return v_type;
	}

	public void setV_type(String v_type) {
		this.v_type = v_type;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}
	

}

package xdt.dto.transfer_accounts.entity;

/**
 * 
 * @Description 商户余额响应实体
 * @author YanChao.Shang
 * @date 2017年02月14日
 * @version V1.3.1
 */
public class BalanceResponseEntity {
	
	private String v_mid; // 商户号
	
	private String v_position; //余额
	
	private String v_code;// 请求响应码

	private String v_msg; // 请求响应信息描述
	
	private String v_sign; // 签名

	public String getV_mid() {
		return v_mid;
	}

	public void setV_mid(String v_mid) {
		this.v_mid = v_mid;
	}

	public String getV_position() {
		return v_position;
	}

	public void setV_position(String v_position) {
		this.v_position = v_position;
	}

	public String getV_code() {
		return v_code;
	}

	public void setV_code(String v_code) {
		this.v_code = v_code;
	}

	public String getV_msg() {
		return v_msg;
	}

	public void setV_msg(String v_msg) {
		this.v_msg = v_msg;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}
	
	

}

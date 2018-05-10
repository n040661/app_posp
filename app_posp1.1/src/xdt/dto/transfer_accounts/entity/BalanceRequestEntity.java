package xdt.dto.transfer_accounts.entity;
/**
 * 
 * @Description 商户余额查询实体
 * @author YanChao.Shang
 * @date 2017年02月14日
 * @version V1.3.1
 */
public class BalanceRequestEntity {
	
   private String v_version; //版本号
	
	private String v_mid; //商户编号
	
	private String v_type; //代付类型
	
	private String v_time; //代付时间
	
	private String v_sign;//签名

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

	public String getV_type() {
		return v_type;
	}

	public void setV_type(String v_type) {
		this.v_type = v_type;
	}

	public String getV_time() {
		return v_time;
	}

	public void setV_time(String v_time) {
		this.v_time = v_time;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}
	
	

	

	
	

}

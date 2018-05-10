package xdt.dto.transfer_accounts.entity;

/**
 * 
 * @Description 代付查询请求实体 
 * @author YanChao.Shang
 * @date 2017年12月28日 下午12:08:55 
 * @version V1.3.1
 */
public class DaifuQueryRequestEntity {
	
	private String v_version; //版本号
	
	private String v_mid; //商户号
	
	private String v_batch_no; //批次号
	
	private String v_identity; //客户标示
	
	private String v_type; //代付类型
	
	private String v_sign; //签名

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

	public String getV_batch_no() {
		return v_batch_no;
	}

	public void setV_batch_no(String v_batch_no) {
		this.v_batch_no = v_batch_no;
	}

	public String getV_identity() {
		return v_identity;
	}

	public void setV_identity(String v_identity) {
		this.v_identity = v_identity;
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

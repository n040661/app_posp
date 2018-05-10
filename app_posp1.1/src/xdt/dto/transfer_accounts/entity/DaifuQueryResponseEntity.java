package xdt.dto.transfer_accounts.entity;

/**
 * 
 * @Description 代付查询响应实体 
 * @author YanChao.Shang
 * @date 2017年12月28日 下午12:08:55 
 * @version V1.3.1
 */
public class DaifuQueryResponseEntity {
	
   
	private String v_mid; //商户号
	
	private String v_batch_no; //批次号
	
	private String v_sum_amount; //交易总金额
	
	private String v_amount;  //单笔交易金额
	
	private String v_code; //请求响应码
	
	private String v_msg; //请求响应信息
	
	private String v_status; //代付响应码
	
	private String v_status_msg; //代付响应信息
	
	private String v_type; //代付类型
	
	private String v_identity; //客户标示
	
	private String v_time; //代付时间
	
	private String v_sign; //签名

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

	public String getV_sum_amount() {
		return v_sum_amount;
	}

	public void setV_sum_amount(String v_sum_amount) {
		this.v_sum_amount = v_sum_amount;
	}

	public String getV_amount() {
		return v_amount;
	}

	public void setV_amount(String v_amount) {
		this.v_amount = v_amount;
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

	public String getV_status() {
		return v_status;
	}

	public void setV_status(String v_status) {
		this.v_status = v_status;
	}

	public String getV_status_msg() {
		return v_status_msg;
	}

	public void setV_status_msg(String v_status_msg) {
		this.v_status_msg = v_status_msg;
	}

	public String getV_type() {
		return v_type;
	}

	public void setV_type(String v_type) {
		this.v_type = v_type;
	}

	public String getV_identity() {
		return v_identity;
	}

	public void setV_identity(String v_identity) {
		this.v_identity = v_identity;
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

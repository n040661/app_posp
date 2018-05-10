package xdt.dto.gateway.entity;

/**
 * 
 * @Description 网关查询响应信息 
 * @author YanChao.Shang
 * @date 2017年12月25日 下午12:28:08 
 * @version V1.3.1
 */
public class  GateWayQueryResponseEntity {
	

	private String v_mid; //商户号
	
	private String v_oid; //订单号
	
	private String v_txnAmt;//交易金额
	
	private String v_code; //请求码
	
	private String v_msg; //请求返回信息
	
	private String v_status; //支付码
	
	private String v_status_msg;//支付返回信息
	
	private String v_attach; //附加数据
	
	private String v_sign; //签名

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

	public String getV_txnAmt() {
		return v_txnAmt;
	}

	public void setV_txnAmt(String v_txnAmt) {
		this.v_txnAmt = v_txnAmt;
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

	public String getV_attach() {
		return v_attach;
	}

	public void setV_attach(String v_attach) {
		this.v_attach = v_attach;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}
	
	

}

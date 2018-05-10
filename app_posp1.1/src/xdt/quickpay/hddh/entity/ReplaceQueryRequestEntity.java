package xdt.quickpay.hddh.entity;

public class ReplaceQueryRequestEntity {
	
	private String merid;
	
	private String cooperator_repay_order_id;
	
	private String v_sign;

	public String getMerid() {
		return merid;
	}

	public void setMerid(String merid) {
		this.merid = merid;
	}
	
	public String getCooperator_repay_order_id() {
		return cooperator_repay_order_id;
	}

	public void setCooperator_repay_order_id(String cooperator_repay_order_id) {
		this.cooperator_repay_order_id = cooperator_repay_order_id;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}

}

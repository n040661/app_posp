package xdt.quickpay.hddh.entity;

public class ReplacePayQueryRequestEntity {
	
	private String merid;
	
	private String cooperator_item_id;
	
	private String repayPlanId;
	
	private String v_sign;

	public String getMerid() {
		return merid;
	}

	public void setMerid(String merid) {
		this.merid = merid;
	}

	public String getCooperator_item_id() {
		return cooperator_item_id;
	}

	public void setCooperator_item_id(String cooperator_item_id) {
		this.cooperator_item_id = cooperator_item_id;
	}

	public String getRepayPlanId() {
		return repayPlanId;
	}

	public void setRepayPlanId(String repayPlanId) {
		this.repayPlanId = repayPlanId;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}

}

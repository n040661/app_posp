package xdt.quickpay.shyb.entity;

/**
 * 
 * @Description 上海易宝支付请求信息 
 * @author YanChao.Shang
 * @date 2017年12月25日 下午12:28:08 
 * @version V1.3.1
 */
public class ShybTransferRequestEntity {
	
	private String customerNumber;
	
	private String subContractId;
	
	private String externalNo;
	
	private String transferWay;
	
	private String amount;
	
	private String callBackUrl;
	
	private String v_sign;

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getSubContractId() {
		return subContractId;
	}

	public void setSubContractId(String subContractId) {
		this.subContractId = subContractId;
	}

	public String getExternalNo() {
		return externalNo;
	}

	public void setExternalNo(String externalNo) {
		this.externalNo = externalNo;
	}

	public String getTransferWay() {
		return transferWay;
	}

	public void setTransferWay(String transferWay) {
		this.transferWay = transferWay;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}

}

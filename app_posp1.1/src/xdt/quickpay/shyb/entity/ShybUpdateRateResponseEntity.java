package xdt.quickpay.shyb.entity;

/**
 * 
 * @Description 上海易宝设置费率响应信息 
 * @author YanChao.Shang
 * @date 2017年12月25日 下午12:28:08 
 * @version V1.3.1
 */
public class ShybUpdateRateResponseEntity {
	
	private String code;
	
	private String message;
	
	private String rate;
	
	private String subContractId;
	
	private String productType;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getSubContractId() {
		return subContractId;
	}

	public void setSubContractId(String subContractId) {
		this.subContractId = subContractId;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	

}

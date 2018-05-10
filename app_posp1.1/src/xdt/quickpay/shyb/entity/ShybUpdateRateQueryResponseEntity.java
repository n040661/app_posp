package xdt.quickpay.shyb.entity;

/**
 * 
 * @Description 上海易宝设置费率请求信息 
 * @author YanChao.Shang
 * @date 2017年12月25日 下午12:28:08 
 * @version V1.3.1
 */
public class ShybUpdateRateQueryResponseEntity {
	
	private String subContractId;
	
	private String productType;
	
	private String rate;
	
	private String v_sign;



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

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}
	
	

}

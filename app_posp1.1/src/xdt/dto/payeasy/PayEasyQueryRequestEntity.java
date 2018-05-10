package xdt.dto.payeasy;
/**
 * @ClassName: PayQueryResponseEntity
 * @Description: 首信易查询请求信息
 * @author YanChao.Shang
 * @date 2017年4月5日 上午10:13:56
 *
 */
public class PayEasyQueryRequestEntity {
	
	private String merchantId;//下游商户号
	
	private String v_mid; //商户编号
	
	private String v_oid; //订单编号
	
	private String v_mac; //数字指纹

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

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

	public String getV_mac() {
		return v_mac;
	}

	public void setV_mac(String v_mac) {
		this.v_mac = v_mac;
	}

	public String toString() {
		return "PayQueryRequestEntity [merchantId=" + merchantId + ", v_mid=" + v_mid + ", v_oid=" + v_oid + ", v_mac="
				+ v_mac + ", getMerchantId()=" + getMerchantId() + ", getV_mid()=" + getV_mid() + ", getV_oid()="
				+ getV_oid() + ", getV_mac()=" + getV_mac() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}
	

}

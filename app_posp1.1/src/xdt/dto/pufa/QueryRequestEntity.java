package xdt.dto.pufa;

import java.io.Serializable;

/**
 * 
 * @Description 查询请求信息
 * @author Shiwen .Li
 * @date 2016年9月11日 下午12:43:13
 * @version V1.3.1
 */
public class QueryRequestEntity implements Serializable {

	/** @Fields serialVersionUID: */

	private static final long serialVersionUID = 1L;

	private String merchantId;// 商户号

	private String orderId;// 订单id

	private String sign;

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return "QueryRequestEntity [merchantId=" + merchantId + ", orderId="
				+ orderId + "]";
	}

}

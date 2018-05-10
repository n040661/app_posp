package xdt.dto.pay;

import java.io.Serializable;

/**
 * 商户侧绑卡开通快捷支付响应
 * 
 * @author tinn
 *
 */
public class PageOpenCardVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String respCode; // 应答码
	private String respMsg; // 应答信息

	private String requestId; // 请求流水号
	private String orderId; // 商户订单号，商户系统保证唯一
	private String merchantCode; // 商户编号
	private String html; // Html页面，Html绑卡页面，成功时返回
	private Integer activateStatus;
	
	public Integer getActivateStatus() {
		return activateStatus;
	}

	public void setActivateStatus(Integer activateStatus) {
		this.activateStatus = activateStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRespCode() {
		return respCode;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public String getRequestId() {
		return requestId;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public String getHtml() {
		return html;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	@Override
	public String toString() {
		return "PageOpenCardVo [respCode=" + respCode + ", respMsg=" + respMsg
				+ ", requestId=" + requestId + ", orderId=" + orderId
				+ ", merchantCode=" + merchantCode + ", html=" + html
				+ ", activateStatus=" + activateStatus + "]";
	}


}

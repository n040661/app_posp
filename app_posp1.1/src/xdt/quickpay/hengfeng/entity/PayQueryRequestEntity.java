package xdt.quickpay.hengfeng.entity;

/**
 * @ClassName: PayQueryResponseEntity
 * @Description: 恒丰查询请求信息
 * @author LiShiwen
 * @date 2016年6月16日 上午10:13:56
 *
 */
public class PayQueryRequestEntity {
	
	private String merId;//商家ID
	
	private String transactionId;//商户交易号
	
	private String signData;//签名
	
	public String getMerId() {
		return merId;
	}
	public void setMerId(String merId) {
		this.merId = merId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getSignData() {
		return signData;
	}
	public void setSignData(String signData) {
		this.signData = signData;
	}
	@Override
	public String toString() {
		return "PayQueryRequestEntity [merId=" + merId + ", transactionId=" + transactionId + ", signData=" + signData
				+ "]";
	}
	
	
}

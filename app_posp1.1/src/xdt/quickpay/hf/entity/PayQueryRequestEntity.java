package xdt.quickpay.hf.entity;
/**
 * @ClassName: PayQueryResponseEntity
 * @Description: 恒丰查询请求信息
 * @author YanChao.Shang
 * @date 2017年5月3日 上午10:13:56
 *
 */
public class PayQueryRequestEntity {
	
	private String merchantId;//下游商户号
	
	private String orderId;// 商户交易号
	
	private String txnTime;// 订单时间 例如：20071117020101
	
	private String tranTp;//0：T0，1：T1
	
	private String signmsg;// 签名

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

	public String getTxnTime() {
		return txnTime;
	}

	public void setTxnTime(String txnTime) {
		this.txnTime = txnTime;
	}
	
	public String getTranTp() {
		return tranTp;
	}

	public void setTranTp(String tranTp) {
		this.tranTp = tranTp;
	}

	public String getSignmsg() {
		return signmsg;
	}

	public void setSignmsg(String signmsg) {
		this.signmsg = signmsg;
	}

	

	
}

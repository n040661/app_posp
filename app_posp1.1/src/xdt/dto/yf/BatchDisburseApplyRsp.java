package xdt.dto.yf;

/**
 * @author rg.zhao
 * 接口版本号			version					String	固定值：1.0.0
	商户代码			merchantId			String	商户代码必须为整数,且长度须在1-24之间
	商户批次订单号	merchantOrderId	String	同上送
	交易时间			transTime				String	裕福平台记录的交易时间
	交易状态			transStatus			String	01 受理失败 02 受理成功 03 受理中
	业务流水号		bpSerialNum			String	裕福支付平台为本次交易生成的唯一订单流水标识
	商户保留域		msgExt					String	同上送
	响应代码			respCode				String	参照本文档7.1节响应码表
	响应描述			respDesc				String	
 */
public class BatchDisburseApplyRsp {
	private static final long serialVersionUID = 754133729905076181L;
	
	private String version;
	private String merchantId;
	private String merchantOrderId;
	private String transTime;
	private String transStatus;
	
	private String bpSerialNum;
	private String msgExt;
	private String respCode;
	private String respDesc;
	
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantOrderId() {
		return merchantOrderId;
	}
	public void setMerchantOrderId(String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}
	public String getTransTime() {
		return transTime;
	}
	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	public String getTransStatus() {
		return transStatus;
	}
	public void setTransStatus(String transStatus) {
		this.transStatus = transStatus;
	}
	public String getBpSerialNum() {
		return bpSerialNum;
	}
	public void setBpSerialNum(String bpSerialNum) {
		this.bpSerialNum = bpSerialNum;
	}
	public String getMsgExt() {
		return msgExt;
	}
	public void setMsgExt(String msgExt) {
		this.msgExt = msgExt;
	}
	@Override
	public String toString() {
		return "BatchDisburseApplyRsp [version=" + version + ", merchantId=" + merchantId + ", merchantOrderId=" + merchantOrderId + ", transTime=" + transTime
				+ ", transStatus=" + transStatus + ", bpSerialNum=" + bpSerialNum + ", msgExt=" + msgExt + ", getRespCode()=" + getRespCode()
				+ ", getRespDesc()=" + getRespDesc() + "]";
	}

}

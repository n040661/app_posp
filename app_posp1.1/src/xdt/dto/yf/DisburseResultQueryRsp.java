package xdt.dto.yf;

/**
 * 代付结果查询
 * @author r.g
 *  接口版本号	version									String	固定值：1.0.0
	商户代码	merchantId								String	商户代码必须为整数,且长度须在1-24之间
	商户批次订单号	merchantOrderId				String	同上送
	商户批次订单时间	merchantOrderTime		String	同上送
	付款文件名称	batchPayFileName				String	同上送
	
	成功付款笔数	succPaidNum						String	成功付款笔数
	失败付款笔数	failPaidNum							String	失败付款笔数
	成功付款总金额	succPaidTotalAmt				String	成功付款总金额,单位分
	失败付款总金额	failPaidTotalAmt				String	失败付款总金额,单位分
	交易时间	transTime									String	裕福平台记录的交易时间
	
	交易状态	transStatus								String	01 受理失败 02 受理成功 03 受理中 04 交易进行中 05 交易完成
	业务流水号	bpSerialNum							String	裕福支付平台为本次交易生成的唯一订单流水标识
	商户保留域	msgExt									String	同上送
	响应码	respCode										String	
	响应描述	respDesc									String	
 */
public class DisburseResultQueryRsp {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2152992376670151293L;
	private String version;
	private String merchantId;
	private String merchantOrderId;
	private String merchantOrderTime;
	private String batchPayFileName;
	
	private String succPaidNum;
	private String failPaidNum;
	private String succPaidTotalAmt;
	private String failPaidTotalAmt;
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
	public String getMerchantOrderTime() {
		return merchantOrderTime;
	}
	public void setMerchantOrderTime(String merchantOrderTime) {
		this.merchantOrderTime = merchantOrderTime;
	}
	public String getBatchPayFileName() {
		return batchPayFileName;
	}
	public void setBatchPayFileName(String batchPayFileName) {
		this.batchPayFileName = batchPayFileName;
	}
	public String getSuccPaidNum() {
		return succPaidNum;
	}
	public void setSuccPaidNum(String succPaidNum) {
		this.succPaidNum = succPaidNum;
	}
	public String getFailPaidNum() {
		return failPaidNum;
	}
	public void setFailPaidNum(String failPaidNum) {
		this.failPaidNum = failPaidNum;
	}
	public String getSuccPaidTotalAmt() {
		return succPaidTotalAmt;
	}
	public void setSuccPaidTotalAmt(String succPaidTotalAmt) {
		this.succPaidTotalAmt = succPaidTotalAmt;
	}
	public String getFailPaidTotalAmt() {
		return failPaidTotalAmt;
	}
	public void setFailPaidTotalAmt(String failPaidTotalAmt) {
		this.failPaidTotalAmt = failPaidTotalAmt;
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
		return "DisburseResultQueryRsp [version=" + version + ", merchantId=" + merchantId + ", merchantOrderId=" + merchantOrderId + ", merchantOrderTime="
				+ merchantOrderTime + ", batchPayFileName=" + batchPayFileName + ", succPaidNum=" + succPaidNum + ", failPaidNum=" + failPaidNum
				+ ", succPaidTotalAmt=" + succPaidTotalAmt + ", failPaidTotalAmt=" + failPaidTotalAmt + ", transTime=" + transTime + ", transStatus="
				+ transStatus + ", bpSerialNum=" + bpSerialNum + ", msgExt=" + msgExt + ", getRespCode()=" + getRespCode() + ", getRespDesc()=" + getRespDesc()
				+ "]";
	}
	
}

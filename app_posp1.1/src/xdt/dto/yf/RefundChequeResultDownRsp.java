package xdt.dto.yf;

/**
 * 退票结果下载
 * @author r.g
 * 接口版本号		    version						String	固定值：1.0.0
	商户代码			merchantId				String	商户代码必须为整数,且长度须在1-24之间
	商户批次订单号	merchantOrderId		String	同上送
	业务流水号		bpSerialNum				String	裕福支付平台为本次交易生成的唯一订单流水标识
	退票文件名称		resultFileName			String	退票文件名称
	
	退票文件摘要值	resultFileDigest			String	退票文件摘要值
	商户订单时间		merchantOrderTime	String	同上送
	商户保留域		msgExt						String	同上送
	响应码				respCode					String	
	响应描述			respDesc					String	
 */
public class RefundChequeResultDownRsp extends BaseRsp{
	/**
	 * 
	 */
	private static final long serialVersionUID = -759645820687710969L;
	private String version;
	private String merchantId;
	private String merchantOrderId;
	private String bpSerialNum;
	private String resultFileName;
	
	private String resultFileDigest;
	private String merchantOrderTime;
	private String msgExt;
	
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
	public String getBpSerialNum() {
		return bpSerialNum;
	}
	public void setBpSerialNum(String bpSerialNum) {
		this.bpSerialNum = bpSerialNum;
	}
	public String getResultFileName() {
		return resultFileName;
	}
	public void setResultFileName(String resultFileName) {
		this.resultFileName = resultFileName;
	}
	public String getResultFileDigest() {
		return resultFileDigest;
	}
	public void setResultFileDigest(String resultFileDigest) {
		this.resultFileDigest = resultFileDigest;
	}
	public String getMerchantOrderTime() {
		return merchantOrderTime;
	}
	public void setMerchantOrderTime(String merchantOrderTime) {
		this.merchantOrderTime = merchantOrderTime;
	}
	public String getMsgExt() {
		return msgExt;
	}
	public void setMsgExt(String msgExt) {
		this.msgExt = msgExt;
	}
	@Override
	public String toString() {
		return "RefundChequeResultDownRsp [version=" + version + ", merchantId=" + merchantId + ", merchantOrderId=" + merchantOrderId + ", bpSerialNum="
				+ bpSerialNum + ", resultFileName=" + resultFileName + ", resultFileDigest=" + resultFileDigest + ", merchantOrderTime=" + merchantOrderTime
				+ ", msgExt=" + msgExt + ", getRespCode()=" + getRespCode() + ", getRespDesc()=" + getRespDesc() + "]";
	}
	
}

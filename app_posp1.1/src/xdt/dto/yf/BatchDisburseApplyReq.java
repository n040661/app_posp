package xdt.dto.yf;

/**
 * 
 * @author rg.zhao
 *	接口版本号			version						String	是	固定值：1.0.0
	商户代码				merchantId				String	是	商户代码必须为整数,且长度须在1-24之间
	商户批次订单号		merchantOrderId		String	是	商户批次订单号长度必须在8-32之间
	商户批次订单时间	merchantOrderTime	String	是	商户订单时间格式为：yyyyMMddHHmmss
	付款文件名称			batchPayFileName	String	是	代付文件名称，英文和数字组成。同商户号下文件名唯一。
	付款文件摘要			batchPayFileDigest	String	是	代付文件摘要值
	后台通知URL			backUrl						String	是	平台后台通知商户平台的地址
	商户保留域			msgExt						String	否	
	自定义保留域			misc							String	否	

 */
public class BatchDisburseApplyReq{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1117789932423592711L;
	private String version;
	private String merchantId;
	private String merchantOrderId;
	private String merchantOrderTime;
	private String batchPayFileName;
	
	private String batchPayFileDigest;
	private String backUrl;
	private String msgExt;
	private String misc;
	private String filePath;

	private String sharepayFileName;
	private String sharepayFileDigest;
	private String sharepayPhotoName;
	private String oriMerOrderId;
	private String oriMerOrderIdDesc;

	public String getSharepayPhotoName() {
		return sharepayPhotoName;
	}

	public void setSharepayPhotoName(String sharepayPhotoName) {
		this.sharepayPhotoName = sharepayPhotoName;
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
	public String getBatchPayFileDigest() {
		return batchPayFileDigest;
	}
	public void setBatchPayFileDigest(String batchPayFileDigest) {
		this.batchPayFileDigest = batchPayFileDigest;
	}
	public String getBackUrl() {
		return backUrl;
	}
	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}
	public String getMsgExt() {
		return msgExt;
	}
	public void setMsgExt(String msgExt) {
		this.msgExt = msgExt;
	}
	public String getMisc() {
		return misc;
	}
	public void setMisc(String misc) {
		this.misc = misc;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSharepayFileName() {
		return sharepayFileName;
	}

	public void setSharepayFileName(String sharepayFileName) {
		this.sharepayFileName = sharepayFileName;
	}

	public String getSharepayFileDigest() {
		return sharepayFileDigest;
	}

	public void setSharepayFileDigest(String sharepayFileDigest) {
		this.sharepayFileDigest = sharepayFileDigest;
	}
	
	

	public String getOriMerOrderId() {
		return oriMerOrderId;
	}

	public void setOriMerOrderId(String oriMerOrderId) {
		this.oriMerOrderId = oriMerOrderId;
	}

	public String getOriMerOrderIdDesc() {
		return oriMerOrderIdDesc;
	}

	public void setOriMerOrderIdDesc(String oriMerOrderIdDesc) {
		this.oriMerOrderIdDesc = oriMerOrderIdDesc;
	}

	@Override
	public String toString() {
		return "BatchDisburseApplyReq [version=" + version + ", merchantId=" + merchantId + ", merchantOrderId=" + merchantOrderId + ", merchantOrderTime="
				+ merchantOrderTime + ", batchPayFileName=" + batchPayFileName + ", batchPayFileDigest=" + batchPayFileDigest + ", backUrl=" + backUrl
				+ ", msgExt=" + msgExt + ", misc=" + misc + ", filePath=" + filePath + "]";
	}
}

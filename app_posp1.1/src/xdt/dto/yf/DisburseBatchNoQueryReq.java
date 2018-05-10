package xdt.dto.yf;

/**
 * 	代付批次查询
 * 	@author r.g
 *	接口版本号	version	                String	是	固定值：1.0.0
	商户代码	    merchantId	        String	是	商户代码必须为整数,且长度须在1-24之间
	批次申请日期	batchApplyDate	String	是	申请代付的日期，格式YYYYMMDD
	商户保留域	msgExt	                String	否	
	自定义保留域	misc	                    String	否	    
 */
public class DisburseBatchNoQueryReq extends BaseReq{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2427152515483814349L;
	private String version = "1.0.0";
	private String merchantId;
	private String batchApplyDate;
	private String msgExt;
	private String misc;
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
	public String getBatchApplyDate() {
		return batchApplyDate;
	}
	public void setBatchApplyDate(String batchApplyDate) {
		this.batchApplyDate = batchApplyDate;
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
	@Override
	public String toString() {
		return "DisburseBatchNoQueryReq [version=" + version + ", merchantId=" + merchantId + ", batchApplyDate=" + batchApplyDate + ", msgExt=" + msgExt
				+ ", misc=" + misc + "]";
	}
	
}

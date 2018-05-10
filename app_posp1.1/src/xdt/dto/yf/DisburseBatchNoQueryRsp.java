package xdt.dto.yf;

/**
 * 代付批次查询
 * @author r.g
 * 接口版本号	version					String	固定值：1.0.0
	商户代码		merchantId			String	商户代码必须为整数,且长度须在1-24之间
	批次申请日期	batchApplyDate	String	同上送
	批次数			batchApplyNum	String	当日收到代付申请批次的总数
	批次信息		batchInfoList			String	批次处理结果列表，JSON格式，详见下方注释
	商户保留域	msgExt					String	同上送
	响应码			respCode				String	表示查询请求结果，不代表订单结果。
	响应描述		respDesc				String	
 */
public class DisburseBatchNoQueryRsp extends BaseRsp{
	/**
	 	
	 */
	private String version;
	private String merchantId;
	private String batchApplyDate;
	private String batchApplyNum;
	private String batchInfoList;
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
	public String getBatchApplyDate() {
		return batchApplyDate;
	}
	public void setBatchApplyDate(String batchApplyDate) {
		this.batchApplyDate = batchApplyDate;
	}
	public String getBatchApplyNum() {
		return batchApplyNum;
	}
	public void setBatchApplyNum(String batchApplyNum) {
		this.batchApplyNum = batchApplyNum;
	}
	public String getBatchInfoList() {
		return batchInfoList;
	}
	public void setBatchInfoList(String batchInfoList) {
		this.batchInfoList = batchInfoList;
	}
	public String getMsgExt() {
		return msgExt;
	}
	public void setMsgExt(String msgExt) {
		this.msgExt = msgExt;
	}
	@Override
	public String toString() {
		return "DisburseBatchNoQueryRsp [version=" + version + ", merchantId=" + merchantId + ", batchApplyDate=" + batchApplyDate + ", batchApplyNum="
				+ batchApplyNum + ", batchInfoList=" + batchInfoList + ", msgExt=" + msgExt + ", getRespCode()=" + getRespCode() + ", getRespDesc()="
				+ getRespDesc() + "]";
	}
	
}

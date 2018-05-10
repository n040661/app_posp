package xdt.dto.yf;

import java.io.Serializable;

/**
 * 代付结果查询
 * @author r.g
 *  接口版本号							version								String	是	固定值：1.0.0
	商户代码								merchantId						String	是	商户代码必须为整数,且长度须在1-24之间
	原批次订单商户批次订单号		oriMerchantOrderId			String	否	商户批次订单号长度必须在8-32之间  （商户批次订单和业务流水号号选填其一）
	原批次订单商户批次订单时间	oriMerchantOrderTime		String	是	商户订单时间格式为：yyyyMMddHHmmss
	原订单业务流水号					oriBpSerialNum 				String	否	业务流水号 （商户订单和业务流水号号选填其一）
	商户保留域							msgExt								String	否	
	自定义保留域							misc									String	否	
 */
public class DisburseResultQueryReq {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2362291256495802885L;
	private String version = "1.0.0";
	private String merchantId;
	private String oriMerchantOrderId;
	private String oriMerchantOrderTime;
	private String oriBpSerialNum;
	private String msgExt;
	private String misc;
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
	public String getOriMerchantOrderId() {
		return oriMerchantOrderId;
	}
	public void setOriMerchantOrderId(String oriMerchantOrderId) {
		this.oriMerchantOrderId = oriMerchantOrderId;
	}
	public String getOriMerchantOrderTime() {
		return oriMerchantOrderTime;
	}
	public void setOriMerchantOrderTime(String oriMerchantOrderTime) {
		this.oriMerchantOrderTime = oriMerchantOrderTime;
	}
	public String getOriBpSerialNum() {
		return oriBpSerialNum;
	}
	public void setOriBpSerialNum(String oriBpSerialNum) {
		this.oriBpSerialNum = oriBpSerialNum;
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
		return "DisburseResultQueryReq [version=" + version + ", merchantId=" + merchantId + ", oriMerchantOrderId=" + oriMerchantOrderId
				+ ", oriMerchantOrderTime=" + oriMerchantOrderTime + ", oriBpSerialNum=" + oriBpSerialNum + ", msgExt=" + msgExt + ", misc=" + misc + "]";
	}

}

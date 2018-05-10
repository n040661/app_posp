package xdt.dto.yf;
/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年4月25日 下午5:29:33 
* 类说明 
*/
public class OnePayRequest {

	private String version;
	private String merchantId;
	private String merchantOrderId;
	private String merchantOrderTime;
	private String payInfo;
	private String backUrl;
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
	public String getPayInfo() {
		return payInfo;
	}
	public void setPayInfo(String payInfo) {
		this.payInfo = payInfo;
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
	@Override
	public String toString() {
		return "OnePayRequest [version=" + version + ", merchantId=" + merchantId + ", merchantOrderId="
				+ merchantOrderId + ", merchantOrderTime=" + merchantOrderTime + ", payInfo=" + payInfo + ", backUrl="
				+ backUrl + ", msgExt=" + msgExt + ", misc=" + misc + "]";
	}
	
}

package xdt.dto.yf;
/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年4月8日 上午10:32:16 
* 类说明 
*/
public class PayReq {

	private String version;//
	private String merchantId ;//
	private String token ;//
	private String msgExt ;//
	private String misc ;//
	private String smsCode ;//
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
	public String getSmsCode() {
		return smsCode;
	}
	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}
	@Override
	public String toString() {
		return "PayReq [version=" + version + ", merchantId=" + merchantId + ", token=" + token + ", msgExt=" + msgExt
				+ ", misc=" + misc + ", smsCode=" + smsCode + "]";
	}
	
	
}

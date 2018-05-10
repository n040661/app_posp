package xdt.dto.yf;

import java.util.List;
import java.util.Map;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年4月4日 上午10:30:12 
* 类说明 
*/
public class QuickReq {

	
	private static final long serialVersionUID = 1117789932423592711L;
	private String version;
	private String merchantId;
	private String merchantOrderId;
	private String merchantOrderTime;
	private String merchantOrderAmt;
	
	private String merchantDisctAmt;
	private String merchantOrderCurrency;
	private String gwType;
	private String backUrl;
	private String userType;

	private String merchantUserId;
	private String merchantOrderDesc;
	private String merchantSettleInfo;
	private String transTimeout;
	private String rcExt;
	private String msgExt;
	private String misc;
	private String payCardList;
	private String verifyId;
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
	public String getMerchantOrderAmt() {
		return merchantOrderAmt;
	}
	public void setMerchantOrderAmt(String merchantOrderAmt) {
		this.merchantOrderAmt = merchantOrderAmt;
	}
	public String getMerchantDisctAmt() {
		return merchantDisctAmt;
	}
	public void setMerchantDisctAmt(String merchantDisctAmt) {
		this.merchantDisctAmt = merchantDisctAmt;
	}
	public String getMerchantOrderCurrency() {
		return merchantOrderCurrency;
	}
	public void setMerchantOrderCurrency(String merchantOrderCurrency) {
		this.merchantOrderCurrency = merchantOrderCurrency;
	}
	public String getGwType() {
		return gwType;
	}
	public void setGwType(String gwType) {
		this.gwType = gwType;
	}
	public String getBackUrl() {
		return backUrl;
	}
	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getMerchantUserId() {
		return merchantUserId;
	}
	public void setMerchantUserId(String merchantUserId) {
		this.merchantUserId = merchantUserId;
	}
	public String getMerchantOrderDesc() {
		return merchantOrderDesc;
	}
	public void setMerchantOrderDesc(String merchantOrderDesc) {
		this.merchantOrderDesc = merchantOrderDesc;
	}
	public String getMerchantSettleInfo() {
		return merchantSettleInfo;
	}
	public void setMerchantSettleInfo(String merchantSettleInfo) {
		this.merchantSettleInfo = merchantSettleInfo;
	}
	public String getTransTimeout() {
		return transTimeout;
	}
	public void setTransTimeout(String transTimeout) {
		this.transTimeout = transTimeout;
	}
	public String getRcExt() {
		return rcExt;
	}
	public void setRcExt(String rcExt) {
		this.rcExt = rcExt;
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

	
	public String getPayCardList() {
		return payCardList;
	}
	public void setPayCardList(String payCardList) {
		this.payCardList = payCardList;
	}
	public String getVerifyId() {
		return verifyId;
	}
	public void setVerifyId(String verifyId) {
		this.verifyId = verifyId;
	}
	@Override
	public String toString() {
		return "QuickReq [version=" + version + ", merchantId=" + merchantId + ", merchantOrderId=" + merchantOrderId
				+ ", merchantOrderTime=" + merchantOrderTime + ", merchantOrderAmt=" + merchantOrderAmt
				+ ", merchantDisctAmt=" + merchantDisctAmt + ", merchantOrderCurrency=" + merchantOrderCurrency
				+ ", gwType=" + gwType + ", backUrl=" + backUrl + ", userType=" + userType + ", merchantUserId="
				+ merchantUserId + ", merchantOrderDesc=" + merchantOrderDesc + ", merchantSettleInfo="
				+ merchantSettleInfo + ", transTimeout=" + transTimeout + ", rcExt=" + rcExt + ", msgExt=" + msgExt
				+ ", misc=" + misc + ", payCardList=" + payCardList + ", verifyId=" + verifyId + "]";
	}
	
	
}

package xdt.dto.scanCode.entity;
/** 
* @author 作者 E-mail: LiMing
* @version 创建时间：2018年4月20日 上午9:30:35 
* 类说明 
*/
public class ScanCodeRequestEntity {

	private String v_version; //版本号
	
	private String v_mid; //商户编号
	
	private String v_oid; //订单号
	
	private String v_txnAmt;//交易金额
	
	private String v_notify_url; //回调地址
	
	private String v_return_url;//同步返回地址
	
	private String v_productName;//商品名称
	
	private String v_productDesc; //商品描述

	private String v_cardType;//支付类型 例 QQ扫码，微信扫码
	
	private String v_subMerchantNo;//子商户号
	
	private String v_clientIP; //客户端IP

	private String v_merchantBankCode; //银行商户编码
	
	private String v_openId; //
	
	private String v_authCode; //付款码
	
	private String v_appId; //
	
	private String v_time;//交易时间
	
	private String v_currency;//支付币种 例子:人民币 156
	
	private String v_channel;//渠道类型例子 D0，T1
	
	private String v_attach; //回传参数
	
	private String v_sign; //签名

	
	public String getV_return_url() {
		return v_return_url;
	}

	public void setV_return_url(String v_return_url) {
		this.v_return_url = v_return_url;
	}

	public String getV_version() {
		return v_version;
	}

	public void setV_version(String v_version) {
		this.v_version = v_version;
	}

	public String getV_mid() {
		return v_mid;
	}

	public void setV_mid(String v_mid) {
		this.v_mid = v_mid;
	}

	public String getV_oid() {
		return v_oid;
	}

	public void setV_oid(String v_oid) {
		this.v_oid = v_oid;
	}

	public String getV_txnAmt() {
		return v_txnAmt;
	}

	public void setV_txnAmt(String v_txnAmt) {
		this.v_txnAmt = v_txnAmt;
	}

	public String getV_notify_url() {
		return v_notify_url;
	}

	public void setV_notify_url(String v_notify_url) {
		this.v_notify_url = v_notify_url;
	}

	public String getV_productName() {
		return v_productName;
	}

	public void setV_productName(String v_productName) {
		this.v_productName = v_productName;
	}

	public String getV_productDesc() {
		return v_productDesc;
	}

	public void setV_productDesc(String v_productDesc) {
		this.v_productDesc = v_productDesc;
	}

	public String getV_cardType() {
		return v_cardType;
	}

	public void setV_cardType(String v_cardType) {
		this.v_cardType = v_cardType;
	}

	public String getV_subMerchantNo() {
		return v_subMerchantNo;
	}

	public void setV_subMerchantNo(String v_subMerchantNo) {
		this.v_subMerchantNo = v_subMerchantNo;
	}

	public String getV_clientIP() {
		return v_clientIP;
	}

	public void setV_clientIP(String v_clientIP) {
		this.v_clientIP = v_clientIP;
	}

	public String getV_merchantBankCode() {
		return v_merchantBankCode;
	}

	public void setV_merchantBankCode(String v_merchantBankCode) {
		this.v_merchantBankCode = v_merchantBankCode;
	}

	public String getV_openId() {
		return v_openId;
	}

	public void setV_openId(String v_openId) {
		this.v_openId = v_openId;
	}

	public String getV_authCode() {
		return v_authCode;
	}

	public void setV_authCode(String v_authCode) {
		this.v_authCode = v_authCode;
	}

	public String getV_appId() {
		return v_appId;
	}

	public void setV_appId(String v_appId) {
		this.v_appId = v_appId;
	}

	public String getV_time() {
		return v_time;
	}

	public void setV_time(String v_time) {
		this.v_time = v_time;
	}

	public String getV_currency() {
		return v_currency;
	}

	public void setV_currency(String v_currency) {
		this.v_currency = v_currency;
	}

	public String getV_channel() {
		return v_channel;
	}

	public void setV_channel(String v_channel) {
		this.v_channel = v_channel;
	}

	public String getV_attach() {
		return v_attach;
	}

	public void setV_attach(String v_attach) {
		this.v_attach = v_attach;
	}

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}

	@Override
	public String toString() {
		return "ScanCodeRequestEntity [v_version=" + v_version + ", v_mid=" + v_mid + ", v_oid=" + v_oid + ", v_txnAmt="
				+ v_txnAmt + ", v_notify_url=" + v_notify_url + ", v_productName=" + v_productName + ", v_productDesc="
				+ v_productDesc + ", v_cardType=" + v_cardType + ", v_subMerchantNo=" + v_subMerchantNo
				+ ", v_clientIP=" + v_clientIP + ", v_merchantBankCode=" + v_merchantBankCode + ", v_openId=" + v_openId
				+ ", v_authCode=" + v_authCode + ", v_appId=" + v_appId + ", v_time=" + v_time + ", v_currency="
				+ v_currency + ", v_channel=" + v_channel + ", v_attach=" + v_attach + ", v_sign=" + v_sign + "]";
	}
	
	
}

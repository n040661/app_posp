package xdt.dto.qianlong;

import java.io.Serializable;

/**
 * 
 * @Description 扫码请求实体 
 * @author shangyanchao
 * @date 2016年12月6日  
 * @version V1.1.1
 */
public class PayRequestEntity implements Serializable{

	
	/** @Fields serialVersionUID: */
	  	
	private static final long serialVersionUID = 1L;
	
	
	private String merchartId; //商户号
	
	private String account;//账户
	
	private String subject;//商品名称
	
	private String orgOrderNo;//机构订单号
	
	private String amount;//订单金额
	
	private String source;//付款方式 0:微信,1:支付宝
	
	private String tranTp;//0：T0，1：T1
	
	private String settleAmt;//结算金额
	
	private String notifyUrl;//回调通知地址
	
	private String sign;
	
	private String callbackUrl;//浏览器回调地址
	
	private String cardNo;
	
	private String realName;
	
	private String pmsBankNo;
	
	private String portType;
	
	private String orderNo;

	public String getMerchartId() {
		return merchartId;
	}

	public void setMerchartId(String merchartId) {
		this.merchartId = merchartId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getOrgOrderNo() {
		return orgOrderNo;
	}

	public void setOrgOrderNo(String orgOrderNo) {
		this.orgOrderNo = orgOrderNo;
	}
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTranTp() {
		return tranTp;
	}

	public void setTranTp(String tranTp) {
		this.tranTp = tranTp;
	}
    
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSettleAmt() {
		return settleAmt;
	}

	public void setSettleAmt(String settleAmt) {
		this.settleAmt = settleAmt;
	}
	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPmsBankNo() {
		return pmsBankNo;
	}

	public void setPmsBankNo(String pmsBankNo) {
		this.pmsBankNo = pmsBankNo;
	}

	public String getPortType() {
		return portType;
	}

	public void setPortType(String portType) {
		this.portType = portType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	@Override
	public String toString() {
		return "PayRequestEntity [merchartId=" + merchartId + ", account=" + account + ", subject=" + subject
				+ ", orgOrderNo=" + orgOrderNo + ", amount=" + amount + ", source=" + source + ", tranTp=" + tranTp
				+ ", settleAmt=" + settleAmt + ", notifyUrl=" + notifyUrl + ", sign=" + sign + ", callbackUrl="
				+ callbackUrl + ", cardNo=" + cardNo + ", realName=" + realName + ", pmsBankNo=" + pmsBankNo
				+ ", portType=" + portType + ", orderNo=" + orderNo + "]";
	}

}

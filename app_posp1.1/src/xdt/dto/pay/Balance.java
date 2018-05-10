package xdt.dto.pay;

import java.io.Serializable;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年1月29日 上午9:54:12 
* 类说明 
*/
public class Balance implements Serializable{

	private static final long serialVersionUID = 1L;
	private String respCode; // 应答码
	private String respMsg; // 应答信息
	private String merchantUuid;//商户UUID，3DES加密
	private String quickPayWalletBalance;//快捷支付商户钱包余额，3DES加密
	private String quickPayD0WalletWithdrawBalance;//快捷支付商户D0钱包可提现金额，3DES加密
	private String quickPayT1WalletWithdrawBalance;//快捷支付商户T1钱包可提现金额，3DES加密
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public String getMerchantUuid() {
		return merchantUuid;
	}
	public void setMerchantUuid(String merchantUuid) {
		this.merchantUuid = merchantUuid;
	}
	public String getQuickPayWalletBalance() {
		return quickPayWalletBalance;
	}
	public void setQuickPayWalletBalance(String quickPayWalletBalance) {
		this.quickPayWalletBalance = quickPayWalletBalance;
	}
	public String getQuickPayD0WalletWithdrawBalance() {
		return quickPayD0WalletWithdrawBalance;
	}
	public void setQuickPayD0WalletWithdrawBalance(String quickPayD0WalletWithdrawBalance) {
		this.quickPayD0WalletWithdrawBalance = quickPayD0WalletWithdrawBalance;
	}
	public String getQuickPayT1WalletWithdrawBalance() {
		return quickPayT1WalletWithdrawBalance;
	}
	public void setQuickPayT1WalletWithdrawBalance(String quickPayT1WalletWithdrawBalance) {
		this.quickPayT1WalletWithdrawBalance = quickPayT1WalletWithdrawBalance;
	}
	
	
}

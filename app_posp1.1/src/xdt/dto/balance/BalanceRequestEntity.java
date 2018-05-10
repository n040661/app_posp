package xdt.dto.balance;
/**
 * 
 * @Description 商户余额查询实体
 * @author YanChao.Shang
 * @date 2017年02月14日
 * @version V1.3.1
 */
public class BalanceRequestEntity {
	
	private String merchantId;
	
	private String tranTp;
	
	private String signmsg;

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getTranTp() {
		return tranTp;
	}

	public void setTranTp(String tranTp) {
		this.tranTp = tranTp;
	}

	public String getSignmsg() {
		return signmsg;
	}

	public void setSignmsg(String signmsg) {
		this.signmsg = signmsg;
	}
	
	

}

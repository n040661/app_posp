package xdt.dto.yf;
/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年4月26日 上午11:35:38 
* 类说明 
*/
public class PayRequest {

	private String accountName;//
	private String accountNo;//
	private String bankName;//
	private String province;//
	private String city;//
	private String amt;//
	private String pblFlag;//
	private String remark;//
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAmt() {
		return amt;
	}
	public void setAmt(String amt) {
		this.amt = amt;
	}
	public String getPblFlag() {
		return pblFlag;
	}
	public void setPblFlag(String pblFlag) {
		this.pblFlag = pblFlag;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "PayRequest [accountName=" + accountName + ", accountNo=" + accountNo + ", bankName=" + bankName
				+ ", province=" + province + ", city=" + city + ", amt=" + amt + ", pblFlag=" + pblFlag + ", remark="
				+ remark + "]";
	}
	
}

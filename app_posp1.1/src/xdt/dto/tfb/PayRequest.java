package xdt.dto.tfb;

public class PayRequest {

	private String version;//版本号
	private String spid;//商户号
	private String sp_serialno;//商户代付单号
	private String sp_batch_no;//每次代付批次号
	private String sp_reqtime;//请求时间
	private String tran_amt;//交易金额
	private String cur_type;//金额类型
	private String pay_type;//付款方式
	private String acct_name;//收款人姓名
	private String acct_id;//收款人账号
	private String acct_type;//账号类型
	private String mobile;//收款人手机号码
	private String bank_name;//开户行名称
	private String bank_settle_no;//开户行支行联行号
	private String bank_branch_name;//支行名称
	private String business_type;//业务类型
	private String business_no;//业务号码
	private String memo;//摘要
	private String sign;//关键参数签名
	private String type;//代付类型
	private String details;//批次明细
	private String batch_postscript;//批次说明
	private String total_money;//总金额
	
	
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getBatch_postscript() {
		return batch_postscript;
	}
	public void setBatch_postscript(String batch_postscript) {
		this.batch_postscript = batch_postscript;
	}
	public String getTotal_money() {
		return total_money;
	}
	public void setTotal_money(String total_money) {
		this.total_money = total_money;
	}
	public String getSp_batch_no() {
		return sp_batch_no;
	}
	public void setSp_batch_no(String sp_batch_no) {
		this.sp_batch_no = sp_batch_no;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSpid() {
		return spid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public String getSp_serialno() {
		return sp_serialno;
	}
	public void setSp_serialno(String sp_serialno) {
		this.sp_serialno = sp_serialno;
	}
	public String getSp_reqtime() {
		return sp_reqtime;
	}
	public void setSp_reqtime(String sp_reqtime) {
		this.sp_reqtime = sp_reqtime;
	}
	public String getTran_amt() {
		return tran_amt;
	}
	public void setTran_amt(String tran_amt) {
		this.tran_amt = tran_amt;
	}
	public String getCur_type() {
		return cur_type;
	}
	public void setCur_type(String cur_type) {
		this.cur_type = cur_type;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getAcct_name() {
		return acct_name;
	}
	public void setAcct_name(String acct_name) {
		this.acct_name = acct_name;
	}
	public String getAcct_id() {
		return acct_id;
	}
	public void setAcct_id(String acct_id) {
		this.acct_id = acct_id;
	}
	public String getAcct_type() {
		return acct_type;
	}
	public void setAcct_type(String acct_type) {
		this.acct_type = acct_type;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getBank_settle_no() {
		return bank_settle_no;
	}
	public void setBank_settle_no(String bank_settle_no) {
		this.bank_settle_no = bank_settle_no;
	}
	public String getBank_branch_name() {
		return bank_branch_name;
	}
	public void setBank_branch_name(String bank_branch_name) {
		this.bank_branch_name = bank_branch_name;
	}
	public String getBusiness_type() {
		return business_type;
	}
	public void setBusiness_type(String business_type) {
		this.business_type = business_type;
	}
	public String getBusiness_no() {
		return business_no;
	}
	public void setBusiness_no(String business_no) {
		this.business_no = business_no;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "PayRequest [version=" + version + ", spid=" + spid
				+ ", sp_serialno=" + sp_serialno + ", sp_reqtime=" + sp_reqtime
				+ ", tran_amt=" + tran_amt + ", cur_type=" + cur_type
				+ ", pay_type=" + pay_type + ", acct_name=" + acct_name
				+ ", acct_id=" + acct_id + ", acct_type=" + acct_type
				+ ", mobile=" + mobile + ", bank_name=" + bank_name
				+ ", bank_settle_no=" + bank_settle_no + ", bank_branch_name="
				+ bank_branch_name + ", business_type=" + business_type
				+ ", business_no=" + business_no + ", memo=" + memo + ", sign="
				+ sign + "]";
	}
	
	
}

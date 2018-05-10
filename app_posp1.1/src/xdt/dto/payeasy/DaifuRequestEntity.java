package xdt.dto.payeasy;
/**
 * 
 * @Description 代付请求实体 
 * @author YanChao.Shang
 * @date 2017年4月1日 下午12:08:55 
 * @version V1.3.1
 */
public class DaifuRequestEntity {
	
	private String merchantId; //商户编号
	
	private String v_data;  //批量代付数据
	
	private String v_mac;  //数字指纹
	
	private String v_count;//分账信息总行数
	
	private String v_sum_amount;//分账总金额
	
	private String v_batch_no;//批次号
	
	private String v_cardNo;//收方账号
	
	private String v_realName;//收方账户名
	
	private String v_bankname;//收方开户行
	
	private String v_province;//收方省份
	
	private String v_city; //收方城市
	
	private String v_amount;//付款金额
	
	private String v_identity;//客户标示
	
	private String v_pmsBankNo;//联行号
	
	private String responsecode;//代付状态

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getV_data() {
		return v_data;
	}

	public void setV_data(String v_data) {
		this.v_data = v_data;
	}

	public String getV_mac() {
		return v_mac;
	}

	public void setV_mac(String v_mac) {
		this.v_mac = v_mac;
	}

	public String getV_count() {
		return v_count;
	}

	public void setV_count(String v_count) {
		this.v_count = v_count;
	}

	public String getV_sum_amount() {
		return v_sum_amount;
	}

	public void setV_sum_amount(String v_sum_amount) {
		this.v_sum_amount = v_sum_amount;
	}

	public String getV_batch_no() {
		return v_batch_no;
	}

	public void setV_batch_no(String v_batch_no) {
		this.v_batch_no = v_batch_no;
	}

	public String getV_cardNo() {
		return v_cardNo;
	}

	public void setV_cardNo(String v_cardNo) {
		this.v_cardNo = v_cardNo;
	}

	public String getV_realName() {
		return v_realName;
	}

	public void setV_realName(String v_realName) {
		this.v_realName = v_realName;
	}

	public String getV_bankname() {
		return v_bankname;
	}

	public void setV_bankname(String v_bankname) {
		this.v_bankname = v_bankname;
	}

	public String getV_province() {
		return v_province;
	}

	public void setV_province(String v_province) {
		this.v_province = v_province;
	}

	public String getV_city() {
		return v_city;
	}

	public void setV_city(String v_city) {
		this.v_city = v_city;
	}

	public String getV_amount() {
		return v_amount;
	}

	public void setV_amount(String v_amount) {
		this.v_amount = v_amount;
	}

	public String getV_identity() {
		return v_identity;
	}

	public void setV_identity(String v_identity) {
		this.v_identity = v_identity;
	}

	public String getV_pmsBankNo() {
		return v_pmsBankNo;
	}

	public void setV_pmsBankNo(String v_pmsBankNo) {
		this.v_pmsBankNo = v_pmsBankNo;
	}

	public String getResponsecode() {
		return responsecode;
	}

	public void setResponsecode(String responsecode) {
		this.responsecode = responsecode;
	}
	
}

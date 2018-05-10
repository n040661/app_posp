package xdt.dto.transfer_accounts.entity;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;

/**
 * 
 * @Description 代付请求实体 
 * @author YanChao.Shang
 * @date 2017年12月28日 下午12:08:55 
 * @version V1.3.1
 */
public class DaifuRequestEntity {
	
	private String v_version; //版本号
	
	private String v_mid; //商户编号
	
	private String v_count;//分账信息总行数
	
	private String v_sum_amount;//分账总金额
	
	private String v_batch_no;//批次号
	
	private String v_cardNo;//收方账号
	
	private String v_realName;//收方账户名
	
	private String v_bankname;//收方开户行
	
	private String v_province;//收方省份
	
	private String v_city; //收方城市
	
	private String v_bankCode;//银行编码//新填ABC
	
	private String v_bankNumber;//银行编号103
	
	private String v_amount;//付款金额
	
	private String v_identity;//客户标示
	
	private String v_pmsBankNo;//联行号
	
	private String v_sign;//签名
	
	private String v_type; //代付类型
	
	private String v_time; //代付时间
	
	private String v_currency; //代付币种
	
	private String v_accountType;//账号类型 1：借记卡，2：贷记卡
	
	private String v_phone;//手机号
	
	private String v_cert_no;//证件号

	private String v_channel;//判断1：pc，2：手机
	
	private String v_ifName;//是否同名 1是，2非
	
	private String v_cardType;//卡类型 1：对私，2对公
	
	private MultipartFile  v_fileName;
	
	private String v_notify_url;//异步地址
	
	
	public String getV_notify_url() {
		return v_notify_url;
	}

	public void setV_notify_url(String v_notify_url) {
		this.v_notify_url = v_notify_url;
	}

	public MultipartFile getV_fileName() {
		return v_fileName;
	}

	public void setV_fileName(MultipartFile v_fileName) {
		this.v_fileName = v_fileName;
	}

	public String getV_cardType() {
		return v_cardType;
	}

	public void setV_cardType(String v_cardType) {
		this.v_cardType = v_cardType;
	}

	public String getV_ifName() {
		return v_ifName;
	}

	public void setV_ifName(String v_ifName) {
		this.v_ifName = v_ifName;
	}

	public String getV_channel() {
		return v_channel;
	}

	public void setV_channel(String v_channel) {
		this.v_channel = v_channel;
	}

	public String getV_bankNumber() {
		return v_bankNumber;
	}

	public void setV_bankNumber(String v_bankNumber) {
		this.v_bankNumber = v_bankNumber;
	}

	public String getV_bankCode() {
		return v_bankCode;
	}

	public void setV_bankCode(String v_bankCode) {
		this.v_bankCode = v_bankCode;
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

	public String getV_sign() {
		return v_sign;
	}

	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}

	public String getV_type() {
		return v_type;
	}

	public void setV_type(String v_type) {
		this.v_type = v_type;
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

	public String getV_accountType() {
		return v_accountType;
	}

	public void setV_accountType(String v_accountType) {
		this.v_accountType = v_accountType;
	}

	public String getV_phone() {
		return v_phone;
	}

	public void setV_phone(String v_phone) {
		this.v_phone = v_phone;
	}

	public String getV_cert_no() {
		return v_cert_no;
	}

	public void setV_cert_no(String v_cert_no) {
		this.v_cert_no = v_cert_no;
	}
	
	

}

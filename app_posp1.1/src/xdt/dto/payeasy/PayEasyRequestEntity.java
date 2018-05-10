package xdt.dto.payeasy;
/**
 * 
 * @Description 首信易支付请求实体 
 * @author YanChao.Shang
 * @date 2017年4月1日 下午12:08:55 
 * @version V1.3.1
 */
public class PayEasyRequestEntity {
	
	private String merchantId;//下游商户号
	
	private String v_mid; //商户编号
	
	private String v_oid; //订单编号
	
	private String v_rcvname; //收货人姓名
	
	private String v_rcvaddr; //收货人地址
	
	private String v_rcvtel; //收货人电话
	
	private String v_rcvpost; //收货人邮政编码
	
	private String v_amount; //订单总金额
	
	private String v_ymd; //订单生产日期
	
	private String v_orderstatus; //配货状态
	
	private String v_ordername; //订货人姓名
	
	private String v_moneytype; //支付币种
	
	private String v_url; //返回商户页面
	
	private String v_md5info; //订单数字指纹
	
	private String v_type; //支付类型
	
	private String v_pmode;//支付方式编码
	
	private String v_bgurl;//后天通知地址
	

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

	public String getV_rcvname() {
		return v_rcvname;
	}

	public void setV_rcvname(String v_rcvname) {
		this.v_rcvname = v_rcvname;
	}

	public String getV_rcvaddr() {
		return v_rcvaddr;
	}

	public void setV_rcvaddr(String v_rcvaddr) {
		this.v_rcvaddr = v_rcvaddr;
	}

	public String getV_rcvtel() {
		return v_rcvtel;
	}

	public void setV_rcvtel(String v_rcvtel) {
		this.v_rcvtel = v_rcvtel;
	}

	public String getV_rcvpost() {
		return v_rcvpost;
	}

	public void setV_rcvpost(String v_rcvpost) {
		this.v_rcvpost = v_rcvpost;
	}

	public String getV_amount() {
		return v_amount;
	}

	public void setV_amount(String v_amount) {
		this.v_amount = v_amount;
	}

	public String getV_ymd() {
		return v_ymd;
	}

	public void setV_ymd(String v_ymd) {
		this.v_ymd = v_ymd;
	}

	public String getV_orderstatus() {
		return v_orderstatus;
	}

	public void setV_orderstatus(String v_orderstatus) {
		this.v_orderstatus = v_orderstatus;
	}

	public String getV_ordername() {
		return v_ordername;
	}

	public void setV_ordername(String v_ordername) {
		this.v_ordername = v_ordername;
	}

	public String getV_moneytype() {
		return v_moneytype;
	}

	public void setV_moneytype(String v_moneytype) {
		this.v_moneytype = v_moneytype;
	}

	public String getV_url() {
		return v_url;
	}

	public void setV_url(String v_url) {
		this.v_url = v_url;
	}

	public String getV_md5info() {
		return v_md5info;
	}

	public void setV_md5info(String v_md5info) {
		this.v_md5info = v_md5info;
	}
	
	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getV_type() {
		return v_type;
	}

	public void setV_type(String v_type) {
		this.v_type = v_type;
	}

	public String getV_pmode() {
		return v_pmode;
	}

	public void setV_pmode(String v_pmode) {
		this.v_pmode = v_pmode;
	}

	public String getV_bgurl() {
		return v_bgurl;
	}

	public void setV_bgurl(String v_bgurl) {
		this.v_bgurl = v_bgurl;
	}
   
}

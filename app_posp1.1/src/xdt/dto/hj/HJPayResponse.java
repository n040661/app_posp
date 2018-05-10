package xdt.dto.hj;

public class HJPayResponse {

	private String r1_MerchantNo;//商户号
	private String r2_BatchNo;//订单号 
	private String r3_Details;
	private String rb_Code;//状态
	private String rc_CodeMsg;//状态描述
	private String hmac;//签名
	public String getR1_MerchantNo() {
		return r1_MerchantNo;
	}
	public void setR1_MerchantNo(String r1_MerchantNo) {
		this.r1_MerchantNo = r1_MerchantNo;
	}
	public String getR2_BatchNo() {
		return r2_BatchNo;
	}
	public void setR2_BatchNo(String r2_BatchNo) {
		this.r2_BatchNo = r2_BatchNo;
	}
	
	public String getR3_Details() {
		return r3_Details;
	}
	public void setR3_Details(String r3_Details) {
		this.r3_Details = r3_Details;
	}
	public String getRb_Code() {
		return rb_Code;
	}
	public void setRb_Code(String rb_Code) {
		this.rb_Code = rb_Code;
	}
	public String getRc_CodeMsg() {
		return rc_CodeMsg;
	}
	public void setRc_CodeMsg(String rc_CodeMsg) {
		this.rc_CodeMsg = rc_CodeMsg;
	}
	public String getHmac() {
		return hmac;
	}
	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
	@Override
	public String toString() {
		return "HJPayResponse [r1_MerchantNo=" + r1_MerchantNo
				+ ", r2_BatchNo=" + r2_BatchNo + ", r3_Details=" + r3_Details
				+ ", rb_Code=" + rb_Code + ", rc_CodeMsg=" + rc_CodeMsg
				+ ", hmac=" + hmac + "]";
	}
	
	
	
}

package xdt.dto.hj;

public class HJResponse {

	private String r0_Version  ;//
	private String r1_MerchantNo ;//
	private String r2_OrderNo  ;//
	private String r3_Amount  ;//
	private String r4_Cur  ;//
	private String r5_Mp  ;//
	private String r6_FrpCode  ;//
	private String r7_TrxNo  ;//
	private String r8_MerchantBankCode  ;//
	private String ra_Code  ;//
	private String rb_CodeMsg  ;//
	private String rc_Result  ;//
	private String rd_Pic  ;//
	private String r3_RefundOrderNo ;//
	private String ra_Status ;//
	private String rb_Code   ;//
	private String rc_CodeMsg ;//
	private String r4_RefundAmount_str ;//
	private String hmac ;//
	private String r6_Status ;//
	
	private String merchantNo;
	private String orderNo;
	private String amount;
	private String sign;
	private String respCode;
	private String respMsg;
	private String mp;
	
	
	public String getMp() {
		return mp;
	}
	public void setMp(String mp) {
		this.mp = mp;
	}
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
	public String getMerchantNo() {
		return merchantNo;
	}
	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getR6_Status() {
		return r6_Status;
	}
	public void setR6_Status(String r6_Status) {
		this.r6_Status = r6_Status;
	}
	public String getR0_Version() {
		return r0_Version;
	}
	public void setR0_Version(String r0_Version) {
		this.r0_Version = r0_Version;
	}
	public String getR1_MerchantNo() {
		return r1_MerchantNo;
	}
	public void setR1_MerchantNo(String r1_MerchantNo) {
		this.r1_MerchantNo = r1_MerchantNo;
	}
	public String getR2_OrderNo() {
		return r2_OrderNo;
	}
	public void setR2_OrderNo(String r2_OrderNo) {
		this.r2_OrderNo = r2_OrderNo;
	}
	public String getR3_Amount() {
		return r3_Amount;
	}
	public void setR3_Amount(String r3_Amount) {
		this.r3_Amount = r3_Amount;
	}
	public String getR4_Cur() {
		return r4_Cur;
	}
	public void setR4_Cur(String r4_Cur) {
		this.r4_Cur = r4_Cur;
	}
	public String getR5_Mp() {
		return r5_Mp;
	}
	public void setR5_Mp(String r5_Mp) {
		this.r5_Mp = r5_Mp;
	}
	public String getR6_FrpCode() {
		return r6_FrpCode;
	}
	public void setR6_FrpCode(String r6_FrpCode) {
		this.r6_FrpCode = r6_FrpCode;
	}
	public String getR7_TrxNo() {
		return r7_TrxNo;
	}
	public void setR7_TrxNo(String r7_TrxNo) {
		this.r7_TrxNo = r7_TrxNo;
	}
	public String getR8_MerchantBankCode() {
		return r8_MerchantBankCode;
	}
	public void setR8_MerchantBankCode(String r8_MerchantBankCode) {
		this.r8_MerchantBankCode = r8_MerchantBankCode;
	}
	public String getRa_Code() {
		return ra_Code;
	}
	public void setRa_Code(String ra_Code) {
		this.ra_Code = ra_Code;
	}
	public String getRb_CodeMsg() {
		return rb_CodeMsg;
	}
	public void setRb_CodeMsg(String rb_CodeMsg) {
		this.rb_CodeMsg = rb_CodeMsg;
	}
	public String getRc_Result() {
		return rc_Result;
	}
	public void setRc_Result(String rc_Result) {
		this.rc_Result = rc_Result;
	}
	public String getRd_Pic() {
		return rd_Pic;
	}
	public void setRd_Pic(String rd_Pic) {
		this.rd_Pic = rd_Pic;
	}
	public String getR3_RefundOrderNo() {
		return r3_RefundOrderNo;
	}
	public void setR3_RefundOrderNo(String r3_RefundOrderNo) {
		this.r3_RefundOrderNo = r3_RefundOrderNo;
	}
	public String getRa_Status() {
		return ra_Status;
	}
	public void setRa_Status(String ra_Status) {
		this.ra_Status = ra_Status;
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
	public String getR4_RefundAmount_str() {
		return r4_RefundAmount_str;
	}
	public void setR4_RefundAmount_str(String r4_RefundAmount_str) {
		this.r4_RefundAmount_str = r4_RefundAmount_str;
	}
	public String getHmac() {
		return hmac;
	}
	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
	@Override
	public String toString() {
		return "HJResponse [r0_Version=" + r0_Version + ", r1_MerchantNo="
				+ r1_MerchantNo + ", r2_OrderNo=" + r2_OrderNo + ", r3_Amount="
				+ r3_Amount + ", r4_Cur=" + r4_Cur + ", r5_Mp=" + r5_Mp
				+ ", r6_FrpCode=" + r6_FrpCode + ", r7_TrxNo=" + r7_TrxNo
				+ ", r8_MerchantBankCode=" + r8_MerchantBankCode + ", ra_Code="
				+ ra_Code + ", rb_CodeMsg=" + rb_CodeMsg + ", rc_Result="
				+ rc_Result + ", rd_Pic=" + rd_Pic + ", r3_RefundOrderNo="
				+ r3_RefundOrderNo + ", ra_Status=" + ra_Status + ", rb_Code="
				+ rb_Code + ", rc_CodeMsg=" + rc_CodeMsg
				+ ", r4_RefundAmount_str=" + r4_RefundAmount_str + ", hmac="
				+ hmac + ", r6_Status=" + r6_Status + ", merchantNo="
				+ merchantNo + ", orderNo=" + orderNo + ", amount=" + amount
				+ ", sign=" + sign + ", respCode=" + respCode + ", respMsg="
				+ respMsg + ", mp=" + mp + "]";
	}
	
	
}

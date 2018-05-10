package xdt.dto.sxf;

public class BaseResponse {

	private String mercNo ;//商户编号
	private String orderNo ;//订单编号 
	private String tranCd ;//交易码 
	private String resCode ;//回执代码 
	private String resMsg;//回执信息 
	private String resData;//业务数据 
	private String encodeType ;//加密方式
	private String sign ;//签名结果 
	public String getMercNo() {
		return mercNo;
	}
	public void setMercNo(String mercNo) {
		this.mercNo = mercNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getTranCd() {
		return tranCd;
	}
	public void setTranCd(String tranCd) {
		this.tranCd = tranCd;
	}
	public String getResCode() {
		return resCode;
	}
	public void setResCode(String resCode) {
		this.resCode = resCode;
	}
	public String getResMsg() {
		return resMsg;
	}
	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}
	public String getResData() {
		return resData;
	}
	public void setResData(String resData) {
		this.resData = resData;
	}
	public String getEncodeType() {
		return encodeType;
	}
	public void setEncodeType(String encodeType) {
		this.encodeType = encodeType;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "BaseResponse [mercNo=" + mercNo + ", orderNo=" + orderNo
				+ ", tranCd=" + tranCd + ", resCode=" + resCode + ", resMsg="
				+ resMsg + ", resData=" + resData + ", encodeType="
				+ encodeType + ", sign=" + sign + "]";
	}
	
}

package xdt.dto.sxf;

public class BaseRequest {

	private String mercNo ;//商户编号
	private String tranCd ;//交易码 
	private String version ;//版本号 
	private String reqData ;//业务数据
	private String ip;//客户端 ip 
	private String encodeType;//加密及签名 方式
	private String sign ;//签名结果 
	private String type ;//连接方式
	public String getMercNo() {
		return mercNo;
	}
	public void setMercNo(String mercNo) {
		this.mercNo = mercNo;
	}
	public String getTranCd() {
		return tranCd;
	}
	public void setTranCd(String tranCd) {
		this.tranCd = tranCd;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getReqData() {
		return reqData;
	}
	public void setReqData(String reqData) {
		this.reqData = reqData;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "BaseEntity [mercNo=" + mercNo + ", tranCd=" + tranCd
				+ ", version=" + version + ", reqData=" + reqData + ", ip="
				+ ip + ", encodeType=" + encodeType + ", sign=" + sign
				+ ", type=" + type + "]";
	}
	
	
}

package xdt.model;

public class Jq {

	private String Spid;//商户号
	private String CardNo;//银行卡号
	private String IDCardType;//证件类型（01身份证，02军官证，03护照，04回乡证，05台胞证，06警官证，07士兵证，99其它证）
	private String IDCardNo;//证件号码
	private String UserName;//用户姓名
	private String TelephoneNo;//银行预留电话
	private String Sign;//签名
	private String orderId; //流水号
	private String type;
	
	public String getSign() {
		return Sign;
	}
	public void setSign(String sign) {
		Sign = sign;
	}
	public String getSpid() {
		return Spid;
	}
	public void setSpid(String spid) {
		Spid = spid;
	}
	public String getCardNo() {
		return CardNo;
	}
	public void setCardNo(String cardNo) {
		CardNo = cardNo;
	}
	public String getIDCardType() {
		return IDCardType;
	}
	public void setIDCardType(String iDCardType) {
		IDCardType = iDCardType;
	}
	public String getIDCardNo() {
		return IDCardNo;
	}
	public void setIDCardNo(String iDCardNo) {
		IDCardNo = iDCardNo;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getTelephoneNo() {
		return TelephoneNo;
	}
	public void setTelephoneNo(String telephoneNo) {
		TelephoneNo = telephoneNo;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Jq [Spid=" + Spid + ", CardNo=" + CardNo + ", IDCardType="
				+ IDCardType + ", IDCardNo=" + IDCardNo + ", UserName="
				+ UserName + ", TelephoneNo=" + TelephoneNo + ", Sign=" + Sign
				+ "]";
	}
	
	
	
	
}

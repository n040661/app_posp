package xdt.dto;
/**
 * 刷卡消费请求接口
 * @author xiaomei
 */
public class BrushCalorieOfConsumptionRequestDTO {
	
	private String sn;//sn号

	private String cardNo; //银行卡号
	
	private String payAmount; //支付金额
	
	private String password; //银行卡密码
	
    private String twoTrack;//磁道数据 37位   
    
    private String threeTrack;//磁道数据 104位
    
    private String cardValid;//14域 卡有效期
     
    private String icRecord;//IC卡数据域	255位  *-IC卡信息 小传
    
    private String reservedPrivate;//解密工作密钥mac pin
    
    private String cardSeq;//卡片序列号
    
    private String safetyControl;//安全控制信息 16位
    
    private String serialno; //序列号
    
    private String af064;//64域 前面所有域的位图+响应消息中39域为“00”时必选  就是个bitmap位图
    
    private String authPath;//认证图片路径
    
	public String getAf064() {
		return af064;
	}

	public void setAf064(String af064) {
		this.af064 = af064;
	}

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

	public String getSafetyControl() {
		return safetyControl;
	}

	public void setSafetyControl(String safetyControl) {
		this.safetyControl = safetyControl;
	}
	
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getCardSeq() {
		return cardSeq;
	}

	public void setCardSeq(String cardSeq) {
		this.cardSeq = cardSeq;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIcRecord() {
		return icRecord;
	}

	public void setIcRecord(String icRecord) {
		this.icRecord = icRecord;
	}

	public String getReservedPrivate() {
		return reservedPrivate;
	}

	public void setReservedPrivate(String reservedPrivate) {
		this.reservedPrivate = reservedPrivate;
	}

	public String getCardValid() {
		return cardValid;
	}

	public void setCardValid(String cardValid) {
		this.cardValid = cardValid;
	}

	public String getTwoTrack() {
		return twoTrack;
	}

	public void setTwoTrack(String twoTrack) {
		this.twoTrack = twoTrack;
	}

	public String getThreeTrack() {
		return threeTrack;
	}

	public void setThreeTrack(String threeTrack) {
		this.threeTrack = threeTrack;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public String getAuthPath() {
		return authPath;
	}

	public void setAuthPath(String authPath) {
		this.authPath = authPath;
	}
	
	
    
}
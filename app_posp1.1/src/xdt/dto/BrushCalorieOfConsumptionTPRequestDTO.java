package xdt.dto;

/**
 * 刷卡消费第三方请求接口
 * @author xiaomei
 */
public class BrushCalorieOfConsumptionTPRequestDTO {

	private String batno;//   *  批次号  （第三方管理）

	private String dealType;//0域 消息类型 A001—签到 	A002—消费

	private String bitmap;//*  1域 位图 b64 8字节16位  * （第三方管理）

	private String cardNo;//2域 主账号 卡号

	private String typeLine;// *  3域 交易处理码    （第三方管理）

	private String money;//4域 交易金额	12位

	private String serialNo;// *  11域 流水号  6位  * （第三方管理）

	private String transData;//12域 所在地时间 HHMMSS 6位 

	private String transTime;//13域 所在地日期 MMDD  4位

	private String cardValid;//14域 卡有效期 

	private String cleraDate;//* 15域 清算日期 MMDD 4位   * （第三方管理）

	private String serviceCode;//22域 服务点输入方式码 3位 --输入密码方式

	private String cardSeq;//23域 卡片序列号 3位  

	private String serviceCondition;//25域 服务点条件码 2位 （第三方管理）

	private String servicePin;//26域 服务点PIN获取码 2位 --密码位数0

	private String idCode;// *   32域 收单机构标识码 11位 * （第三方管理）

	private String twoTrack;//35域 2磁道数据 37位   

	private String threeTrack;//36域 3磁道数据 104位

	private String reference;// *   37域 检索参考 12位 * （第三方管理）

	private String authorizationCode;//*  38域 授权码 6位   * （第三方管理）

	private String errCode;//39域 我把这个定义为返回的错误信息吧。2位  --错误码  00成功

	private String terminalSN;//40域  sn号

	private String merPos;//41域 通道POS的信息 8位  //这个41里 a.posbusinessno 试

	private String merInfo;//42域 通道商户的信息  15位 //42也是 merc_id 244193370717935 

	private String errCode2;//*   44域 附加响应数据  25位  * （第三方管理）

	private String transCurrency;//49域 交易货币代码 3位  156 RMB

	private String personalCode;//52域 个人标识码数据 B64  16位 -*有输入密码时必须上送

	private String safetyControl;//53域 安全控制信息 16位

	private String icRecord;//55域 IC卡数据域	255位  *-IC卡信息 小传

	private String posVersion;//*   59域 POS终端信息  600位  * （第三方管理）

	private String reseved60;// *   60域 交易类型码+批次号+网络管理信息码+终端读取能力+基于PBOC借/贷记标准的IC卡条件代码……  一般13位 * （第三方管理）

	private String reseved61;// *  61域 原始信息域 29位 （第三方管理）

	private String reservedPrivate;//62域 解密工作密钥mac pin 自定义域  22位   --签到 

	private String af063;//63域 操作员国际信用卡公司代码+自定义域2  一般3位 加后面的自定义域120

	private String af064;//64域 前面所有域的位图+响应消息中39域为“00”时必选  就是个bitmap位图
	
    private PayCreditCardAccountTPRequestDTO  paycred;//账户支参数
    
    private String transFee;//费率
    
    private String phone;//手机号

    private String oAgentNo;//欧单编号
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTransFee() {
		return transFee;
	}

	public void setTransFee(String transFee) {
		this.transFee = transFee;
	}

	public PayCreditCardAccountTPRequestDTO getPaycred() {
		return paycred;
	}

	public void setPaycred(PayCreditCardAccountTPRequestDTO paycred) {
		this.paycred = paycred;
	}

	public String getBatno() {
		return batno;
	}

	public void setBatno(String batno) {
		this.batno = batno;
	}

	public String getMerPos() {
		return merPos;
	}

	public void setMerPos(String merPos) {
		this.merPos = merPos;
	}

	public String getMerInfo() {
		return merInfo;
	}

	public void setMerInfo(String merInfo) {
		this.merInfo = merInfo;
	}
	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public String getBitmap() {
		return bitmap;
	}

	public void setBitmap(String bitmap) {
		this.bitmap = bitmap;
	}
	public String getTerminalSN() {
		return terminalSN;
	}

	public void setTerminalSN(String terminalSN) {
		this.terminalSN = terminalSN;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getTypeLine() {
		return typeLine;
	}

	public void setTypeLine(String typeLine) {
		this.typeLine = typeLine;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getTransData() {
		return transData;
	}

	public void setTransData(String transData) {
		this.transData = transData;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getCardValid() {
		return cardValid;
	}

	public void setCardValid(String cardValid) {
		this.cardValid = cardValid;
	}

	public String getCleraDate() {
		return cleraDate;
	}

	public void setCleraDate(String cleraDate) {
		this.cleraDate = cleraDate;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getCardSeq() {
		return cardSeq;
	}

	public void setCardSeq(String cardSeq) {
		this.cardSeq = cardSeq;
	}

	public String getServiceCondition() {
		return serviceCondition;
	}

	public void setServiceCondition(String serviceCondition) {
		this.serviceCondition = serviceCondition;
	}

	public String getServicePin() {
		return servicePin;
	}

	public void setServicePin(String servicePin) {
		this.servicePin = servicePin;
	}

	public String getIdCode() {
		return idCode;
	}

	public void setIdCode(String idCode) {
		this.idCode = idCode;
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

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrCode2() {
		return errCode2;
	}

	public void setErrCode2(String errCode2) {
		this.errCode2 = errCode2;
	}

	public String getTransCurrency() {
		return transCurrency;
	}

	public void setTransCurrency(String transCurrency) {
		this.transCurrency = transCurrency;
	}

	public String getPersonalCode() {
		return personalCode;
	}

	public void setPersonalCode(String personalCode) {
		this.personalCode = personalCode;
	}

	public String getSafetyControl() {
		return safetyControl;
	}

	public void setSafetyControl(String safetyControl) {
		this.safetyControl = safetyControl;
	}

	public String getIcRecord() {
		return icRecord;
	}

	public void setIcRecord(String icRecord) {
		this.icRecord = icRecord;
	}

	public String getPosVersion() {
		return posVersion;
	}

	public void setPosVersion(String posVersion) {
		this.posVersion = posVersion;
	}

	public String getReseved60() {
		return reseved60;
	}

	public void setReseved60(String reseved60) {
		this.reseved60 = reseved60;
	}

	public String getReseved61() {
		return reseved61;
	}

	public void setReseved61(String reseved61) {
		this.reseved61 = reseved61;
	}

	public String getReservedPrivate() {
		return reservedPrivate;
	}

	public void setReservedPrivate(String reservedPrivate) {
		this.reservedPrivate = reservedPrivate;
	}

	public String getAf063() {
		return af063;
	}

	public void setAf063(String af063) {
		this.af063 = af063;
	}

	public String getAf064() {
		return af064;
	}

	public void setAf064(String af064) {
		this.af064 = af064;
	}

    public String getoAgentNo() {
        return oAgentNo;
    }

    public void setoAgentNo(String oAgentNo) {
        this.oAgentNo = oAgentNo;
    }
}
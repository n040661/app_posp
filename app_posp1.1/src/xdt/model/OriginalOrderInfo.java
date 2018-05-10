package xdt.model;

/**
 * @ClassName: OriginalOrderInfo
 * @Description: 恒丰快捷支付原始信息 
 * @author LiShiwen
 * @date 2016年6月20日 下午3:07:03
 *
 */
public class OriginalOrderInfo {
	
    private String merchantOrderId;//下游订单id

    private String orderId;//订单id

    private String pid;//商户id

    private String orderTime;//订单时间

    private String orderAmount;//订单金额

    private String payType;//支付方式 

    private String bankId;//银行代码

    private String procdutName;//商品名称

    private String procdutNum; //商品数量

    private String procdutDesc;//商品说明 

    private String pageUrl;//前台url

    private String bgUrl;//后台url
    
    private String bankNo;//银行卡号
    private String byUser;//支付宝或微信登陆名
    private String url;//支付url,给用户扫码用
    
    private String realName; //真实姓名
    
    private String bankType; //银行类型
    
    private String phone;   //手机号
    
    private String cvn2;    //卡背后三位有效数字
    
    private String expired; //有效期
    
    private String attach;  //附加信息
    
    private String certNo;  //证件号
    
    private String sumCode; //验证码
    
    private String settleCardNo; //结算卡号
    
    private String settleUserName; //结算用户名
    
    private String settlePmsBankNo; //结算卡联行号
    
    private String userFee; //交易手续费率
    
    private String settleUserFee; //结算手续费

    private String userId;//用户唯一标志
    
    private String verifyId;//卡信息唯一值
    
    
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVerifyId() {
		return verifyId;
	}

	public void setVerifyId(String verifyId) {
		this.verifyId = verifyId;
	}

	public String getMerchantOrderId() {
		return merchantOrderId;
	}

	public void setMerchantOrderId(String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getProcdutName() {
		return procdutName;
	}

	public void setProcdutName(String procdutName) {
		this.procdutName = procdutName;
	}

	public String getProcdutNum() {
		return procdutNum;
	}

	public void setProcdutNum(String procdutNum) {
		this.procdutNum = procdutNum;
	}

	public String getProcdutDesc() {
		return procdutDesc;
	}

	public void setProcdutDesc(String procdutDesc) {
		this.procdutDesc = procdutDesc;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getBgUrl() {
		return bgUrl;
	}

	public void setBgUrl(String bgUrl) {
		this.bgUrl = bgUrl;
	}

	public String getBankNo() {
		return bankNo;
	}

	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	public String getByUser() {
		return byUser;
	}

	public void setByUser(String byUser) {
		this.byUser = byUser;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCvn2() {
		return cvn2;
	}

	public void setCvn2(String cvn2) {
		this.cvn2 = cvn2;
	}

	public String getExpired() {
		return expired;
	}

	public void setExpired(String expired) {
		this.expired = expired;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getCertNo() {
		return certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getSumCode() {
		return sumCode;
	}

	public void setSumCode(String sumCode) {
		this.sumCode = sumCode;
	}

	public String getSettleCardNo() {
		return settleCardNo;
	}

	public void setSettleCardNo(String settleCardNo) {
		this.settleCardNo = settleCardNo;
	}

	public String getSettleUserName() {
		return settleUserName;
	}

	public void setSettleUserName(String settleUserName) {
		this.settleUserName = settleUserName;
	}

	public String getSettlePmsBankNo() {
		return settlePmsBankNo;
	}

	public void setSettlePmsBankNo(String settlePmsBankNo) {
		this.settlePmsBankNo = settlePmsBankNo;
	}

	public String getUserFee() {
		return userFee;
	}

	public void setUserFee(String userFee) {
		this.userFee = userFee;
	}

	public String getSettleUserFee() {
		return settleUserFee;
	}

	public void setSettleUserFee(String settleUserFee) {
		this.settleUserFee = settleUserFee;
	}
    

	
    
}
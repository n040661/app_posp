package xdt.dto;

import xdt.model.PmsAppTransInfo;


/**
 * 提现 提款   DTO
 * wumeng 20150525
 */
public class DrawMoneyAccRequestAndResponseDTO {
	
	//请求参数
	private  String  accNo  ;//卡号
	private  String  mobilePhone ;// 手机号
	
	//确认订单提交请求参数
		//手机号
	private  String  drawAmt ;//提款金额
	
	private String validCode;//验证码
	
	
	//返回参数
	private  String  retCode  ;//返回码
	private  String  retMessage ;//返回码信息      0查询成功      1 查询失败       100 系统异常
	private  String  bankName ;// 开卡行名
	private  String  accNo_Show;//卡号(遮盖),显示用
	
	
	//卡号(遮盖),显示用        与请求参数共用  accNo  卡号遮盖索引值，传递用
	
	
	
	private  String  accName_Show ;//持卡人姓名(遮盖),显示用
	private  String  accName;//持卡人遮盖索引值，传递用
	private  String  accBalance ;//账户可用余额
	private  String  drawMoneyTimes ;//本日可提款次数
	private  String  remainDrawMoneyTimes ;//剩余提款次数
	private  String  chargeRate ;//手续费费率
	private  String  tranMinAmt;//每笔最少金额
	private  String  tranMaxAmt;//每笔最大金额
	private  String  alerts;//到账时间提示信息，如：下个工作日24点到账
	private String isTop;//是否是封顶费率  1封顶      0不封顶
	private String topPoundage;//封顶手续费
	private String bottomPoundage;//最低手续费
	private String isBottom;//是否最低  1最低   0不是最低
	
	//确认订单返回参数
	
	//返回码   //返回码信息      0查询成功      1 查询失败       100 系统异常   公用
	private  String  orderNumber;//订单号
	
	
	
	
	private PmsAppTransInfo  pmsAppTransInfo;  //第一步跳第二步使用
	
	
	
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getRetMessage() {
		return retMessage;
	}
	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAccNo_Show() {
		return accNo_Show;
	}
	public void setAccNo_Show(String accNoShow) {
		accNo_Show = accNoShow;
	}
	public String getAccName_Show() {
		return accName_Show;
	}
	public void setAccName_Show(String accNameShow) {
		accName_Show = accNameShow;
	}
	public String getAccName() {
		return accName;
	}
	public void setAccName(String accName) {
		this.accName = accName;
	}
	public String getAccBalance() {
		return accBalance;
	}
	public void setAccBalance(String accBalance) {
		this.accBalance = accBalance;
	}
	public String getDrawMoneyTimes() {
		return drawMoneyTimes;
	}
	public void setDrawMoneyTimes(String drawMoneyTimes) {
		this.drawMoneyTimes = drawMoneyTimes;
	}
	public String getRemainDrawMoneyTimes() {
		return remainDrawMoneyTimes;
	}
	public void setRemainDrawMoneyTimes(String remainDrawMoneyTimes) {
		this.remainDrawMoneyTimes = remainDrawMoneyTimes;
	}
	public String getChargeRate() {
		return chargeRate;
	}
	public void setChargeRate(String chargeRate) {
		this.chargeRate = chargeRate;
	}
	public String getTranMinAmt() {
		return tranMinAmt;
	}
	public void setTranMinAmt(String tranMinAmt) {
		this.tranMinAmt = tranMinAmt;
	}
	public String getTranMaxAmt() {
		return tranMaxAmt;
	}
	public void setTranMaxAmt(String tranMaxAmt) {
		this.tranMaxAmt = tranMaxAmt;
	}
	public String getAlerts() {
		return alerts;
	}
	public void setAlerts(String alerts) {
		this.alerts = alerts;
	}
	public String getDrawAmt() {
		return drawAmt;
	}
	public void setDrawAmt(String drawAmt) {
		this.drawAmt = drawAmt;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public PmsAppTransInfo getPmsAppTransInfo() {
		return pmsAppTransInfo;
	}
	public void setPmsAppTransInfo(PmsAppTransInfo pmsAppTransInfo) {
		this.pmsAppTransInfo = pmsAppTransInfo;
	}
	public String getValidCode() {
		return validCode;
	}
	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}
	public String getIsTop() {
		return isTop;
	}
	public void setIsTop(String isTop) {
		this.isTop = isTop;
	}
	public String getTopPoundage() {
		return topPoundage;
	}
	public void setTopPoundage(String topPoundage) {
		this.topPoundage = topPoundage;
	}
	public String getBottomPoundage() {
		return bottomPoundage;
	}
	public void setBottomPoundage(String bottomPoundage) {
		this.bottomPoundage = bottomPoundage;
	}
	public String getIsBottom() {
		return isBottom;
	}
	public void setIsBottom(String isBottom) {
		this.isBottom = isBottom;
	}

	
}

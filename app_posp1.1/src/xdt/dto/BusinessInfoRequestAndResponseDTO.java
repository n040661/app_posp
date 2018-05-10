package xdt.dto;

import java.util.List;

import xdt.model.AppRateConfig;

/**
 * 业务信息   最大值、最小值、费率
 */
public class BusinessInfoRequestAndResponseDTO {
	
	//请求
	private String mobilePhone; // 手机号
	private String businessCode; // 业务编号（1 商户收款、2 转账汇款、3 信用卡还款、4手机充值、5 水煤电、 6 加油卡充值、7 提款（提现））
	
	//返回
	private String  retCode;//返回码
	private String retMessage;//返回信息
	private String chargeRate;   // 手续费费率
	private String tranMinAmt;   // 每笔最少金额
	private String tranMaxAmt;   // 每笔最大金额
	private String isTop;//是否是封顶费率  1封顶      0不封顶
	private String topPoundage;//封顶手续费
	private String bottomPoundage;//最低手续费
	private String isBottom;//是否最低  1最低   0不是最低
	private String baiduRate;//百度第三方费率
	private String weixnRate;//微信第三方费率
	private String zhifubaoRate;//支付宝第三方
	private String ydhbRate;//移动和包第三方费率
	private String minSettleAmount;//T+1清算最低金额
	private List<AppRateConfig> list;// 费率列表
	
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getBusinessCode() {
		return businessCode;
	}
	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
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
	public String getBaiduRate() {
		return baiduRate;
	}
	public void setBaiduRate(String baiduRate) {
		this.baiduRate = baiduRate;
	}
	public String getWeixnRate() {
		return weixnRate;
	}
	public void setWeixnRate(String weixnRate) {
		this.weixnRate = weixnRate;
	}
	public String getZhifubaoRate() {
		return zhifubaoRate;
	}
	public void setZhifubaoRate(String zhifubaoRate) {
		this.zhifubaoRate = zhifubaoRate;
	}
	public String getYdhbRate() {
		return ydhbRate;
	}
	public void setYdhbRate(String ydhbRate) {
		this.ydhbRate = ydhbRate;
	}
	public String getMinSettleAmount() {
		return minSettleAmount;
	}
	public void setMinSettleAmount(String minSettleAmount) {
		this.minSettleAmount = minSettleAmount;
	}
	public List<AppRateConfig> getList() {
		return list;
	}
	public void setList(List<AppRateConfig> list) {
		this.list = list;
	}
	
}

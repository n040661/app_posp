package xdt.dto;

import java.math.BigDecimal;

/**
 * 水煤电生成订单接口请求
 * 
 * @author lev12
 * 
 */
public class GeneralUtilityOrderRequestDTO {

	private String provinceId;// 省份编号
	private String cityId;// 城市编号
	private String payProjectId;// 缴费项目编号
	private String payUnitId;// 缴费单位编号
	private String clientId;// 客户编号
	private BigDecimal rechargeAmt;// 充值金额
	private String payType;// 1.刷卡支付，2.第三方支付
	private String payChannel;// 1.支付宝，2.微信，3百度等 注：当payType为2时，必填
	private String brushType;// 刷卡类型：1音频刷卡，2蓝牙刷卡 注：当payType为1时，必填
	private BrushCalorieOfConsumptionRequestDTO dto;// 刷卡支付请求体，当为刷卡支付的时候必填

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getPayProjectId() {
		return payProjectId;
	}

	public void setPayProjectId(String payProjectId) {
		this.payProjectId = payProjectId;
	}

	public String getPayUnitId() {
		return payUnitId;
	}

	public void setPayUnitId(String payUnitId) {
		this.payUnitId = payUnitId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public BigDecimal getRechargeAmt() {
		return rechargeAmt;
	}

	public void setRechargeAmt(BigDecimal rechargeAmt) {
		this.rechargeAmt = rechargeAmt;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}

	public String getBrushType() {
		return brushType;
	}

	public void setBrushType(String brushType) {
		this.brushType = brushType;
	}

	public BrushCalorieOfConsumptionRequestDTO getDto() {
		return dto;
	}

	public void setDto(BrushCalorieOfConsumptionRequestDTO dto) {
		this.dto = dto;
	}

}

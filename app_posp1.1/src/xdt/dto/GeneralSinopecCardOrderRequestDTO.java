package xdt.dto;

import java.math.BigDecimal;

/**
 * 加油卡生成订单接口响应
 * 
 * @author lev12
 * 
 */
public class GeneralSinopecCardOrderRequestDTO {

	private String cardId;// 加油卡卡号
	private String gasCardTel;// 持卡人手机号码
	private String chargeType;// 加油卡类型 （1:中石化、2:中石油；默认为1）
	private String clientId;// 客户编号
	private BigDecimal rechargeAmt;// 充值金额
	private String payType;// 1.刷卡支付，2.第三方支付
	private String payChannel;// 1.支付宝，2.微信，3百度等 注：当payType为2时，必填
	private String brushType;// 刷卡类型：1音频刷卡，2蓝牙刷卡 注：当payType为1时，必填
	private BrushCalorieOfConsumptionRequestDTO dto;// 刷卡支付请求体，当为刷卡支付的时候必填

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getGasCardTel() {
		return gasCardTel;
	}

	public void setGasCardTel(String gasCardTel) {
		this.gasCardTel = gasCardTel;
	}

	public String getChargeType() {
		return chargeType;
	}

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
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
package xdt.dto;

/**
 * 商户收款 刷卡收款 app请求
 * wumeng 2015-5-19
 *  
 */
public class PayCardRequestDTO {
	
	//生成订单
	private String brushType;          //刷卡类型：1音频刷卡，2蓝牙刷卡
	private String rateType;		//刷卡费率类型
	private BrushCalorieOfConsumptionRequestDTO dto;                //刷卡卡信息
	
	//提交订单支付 （加上上面的）
	private String payPwd;          //支付密码
	private String orderNumber;        //订单号
	
	
	private String altLat;//经纬度（逗号隔开）
	private String gpsAddress;//gps获取的地址信息(中文)
	
	
	
	public String getBrushType() {
		return brushType;
	}
	public void setBrushType(String brushType) {
		this.brushType = brushType;
	}
	public String getPayPwd() {
		return payPwd;
	}
	public void setPayPwd(String payPwd) {
		this.payPwd = payPwd;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public BrushCalorieOfConsumptionRequestDTO getDto() {
		return dto;
	}
	public void setDto(BrushCalorieOfConsumptionRequestDTO dto) {
		this.dto = dto;
	}
	public String getRateType() {
		return rateType;
	}
	public void setRateType(String rateType) {
		this.rateType = rateType;
	}
	public String getAltLat() {
		return altLat;
	}
	public void setAltLat(String altLat) {
		this.altLat = altLat;
	}
	public String getGpsAddress() {
		return gpsAddress;
	}
	public void setGpsAddress(String gpsAddress) {
		this.gpsAddress = gpsAddress;
	}
	
	
	
}

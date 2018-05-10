package xdt.dto;

/**
 * 生成二维码 和 支付成功 判断   返回 app  DTO
 * wumeng 20150506
 */
public class MroducedTwoDimensionResponseDTO {

		
	private String retCode ;//返回码
	private String retMessage;//返回码信息 0生成二维码成功 1 生成二维码失败 100 系统异常   //支付成功 判断   返回码信息  0支付成功 1支付失败2 未支付100 系统异常

	private String orderNumber;//订单号
	private String twoDimensionImage;//二维码图片二进制格式
	private String twoDimensionContent;//二维码内容json格式
	
	
	
	
	//第一步到第二步使用
	private String payChannel;
	private String oAgentNo;//o单编号
	
	
	//百度
	private  String [] array;
	private String [] array1;
	
	//微信 和支付宝用
	private WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO;
	
	//移动和包使用
	private YDHBRequestDTO yDHBRequestDTO;
	
	
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
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getTwoDimensionImage() {
		return twoDimensionImage;
	}
	public void setTwoDimensionImage(String twoDimensionImage) {
		this.twoDimensionImage = twoDimensionImage;
	}
	public String getTwoDimensionContent() {
		return twoDimensionContent;
	}
	public void setTwoDimensionContent(String twoDimensionContent) {
		this.twoDimensionContent = twoDimensionContent;
	}
	public String getPayChannel() {
		return payChannel;
	}
	public void setPayChannel(String payChannel) {
		this.payChannel = payChannel;
	}
	public String[] getArray() {
		return array;
	}
	public void setArray(String[] array) {
		this.array = array;
	}
	public String[] getArray1() {
		return array1;
	}
	public void setArray1(String[] array1) {
		this.array1 = array1;
	}
	public WechatAndAlipayRequestDTO getWechatAndAlipayRequestDTO() {
		return wechatAndAlipayRequestDTO;
	}
	public void setWechatAndAlipayRequestDTO(WechatAndAlipayRequestDTO wechatAndAlipayRequestDTO) {
		this.wechatAndAlipayRequestDTO = wechatAndAlipayRequestDTO;
	}
	public YDHBRequestDTO getyDHBRequestDTO() {
		return yDHBRequestDTO;
	}
	public void setyDHBRequestDTO(YDHBRequestDTO yDHBRequestDTO) {
		this.yDHBRequestDTO = yDHBRequestDTO;
	}
	public String getoAgentNo() {
		return oAgentNo;
	}
	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}



}

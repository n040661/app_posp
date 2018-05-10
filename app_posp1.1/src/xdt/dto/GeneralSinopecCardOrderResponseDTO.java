package xdt.dto;

/**
 * 加油卡生成订单接口响应
 * 
 * @author lev12
 * 
 */
public class GeneralSinopecCardOrderResponseDTO {

	private Integer retCode;// 返回码

	private String retMessage;// 返回码信息 0成功 1 失败 100 系统异常

	private String orderNumber;// 订单号

	private String pageUrl;// 返回的URL，供skd调用的

	public Integer getRetCode() {
		return retCode;
	}

	public void setRetCode(Integer retCode) {
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

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

}
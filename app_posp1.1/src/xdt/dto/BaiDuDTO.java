package xdt.dto;


/**
 * 百度 调用 接口后返回信息 DTO
 * wumeng 20150506
 */
public class BaiDuDTO {
	//调用百度二维码生成使用  start
	
	//例如：{"ret":"0","msg":"OK","content":"https:\/\/www.baifubao.com\/o2o\/0\/s\/0?tinyurl=JFRhkc","token":""}
	private String ret;
	private String msg;
	private String content;//返回的http用来生成二维码 
	private String token;
	
	private String total_amount  ;//总金额
	//调用百度二维码生成使用  end

	//调用百度订单查询生成使用  start
	private String order_no;//订单号
	
	//调用百度订单查询生成使用  end
	
	
	
	public String getRet() {
		return ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(String totalAmount) {
		total_amount = totalAmount;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String orderNo) {
		order_no = orderNo;
	}
	
}

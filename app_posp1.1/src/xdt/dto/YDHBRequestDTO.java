package xdt.dto;

public class YDHBRequestDTO {
	
	private String buf;//请求报文
	private String characterSet;////编码格式   00--GBK;01--GB2312;02--UTF-8   可以为空默认00--GBK
	private String orderid;//订单号
	private String oAgentNo;//o单编号
	public String getBuf() {
		return buf;
	}
	public void setBuf(String buf) {
		this.buf = buf;
	}
	public String getCharacterSet() {
		return characterSet;
	}
	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getoAgentNo() {
		return oAgentNo;
	}
	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}
	
	
	
	
	

}

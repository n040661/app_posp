package xdt.dto.sxf;

import java.io.Serializable;


public class RequestMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String clientId;
	private String reqId;
	private String tranCd;
	private String version;
	private String orderNo;
	
	private String sign;
	/**请求业务数据*/
	private String reqData;
	
	private String resData;
	
	private String  resCode;
	
	private String resMsg;
	
	public String getOrderNo() {
		return orderNo;
	}
	
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getResMsg() {
		return resMsg;
	}

	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getTranCd() {
		return tranCd;
	}

	public void setTranCd(String tranCd) {
		this.tranCd = tranCd;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public String getReqData() {
		return reqData;
	}

	public void setReqData(String reqData) {
		this.reqData = reqData;
	}
	
	public String getResData() {
		return resData;
	}
	public void setResData(String resData) {
		this.resData = resData;
	}
}

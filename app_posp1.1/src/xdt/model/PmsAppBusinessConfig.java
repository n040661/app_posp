package xdt.model;

import java.math.BigDecimal;

public class PmsAppBusinessConfig {
	private BigDecimal id;

	private String businesscode; // 业务号

	private String businessname; // 业务名称

	private String imageurl; // 图片路径

	private String externalurl; // h5URL

	private String accno; // 账号(车易行h5)

	private String status; // 业务状态 0无效，1有效

	private String status1; // 商户业务状态 0无效，1有效

	private String extends1; // 是否在首页展示

	private String extends2; // 扩展字段2

	private String extends3; // 扩展字段3

	private String type;// 类型

	private String oAgentNo;// O单编号
	
	private String modulecode;// 业务编码

	private String message;// 提示信息

	private String message1; // 商户提示信息
	
	private String handleType;//1-原生处理,2-h5
	
	public String getHandleType() {
		return handleType;
	}

	public void setHandleType(String handleType) {
		this.handleType = handleType;
	}

	public String getMessage1() {
		return message1;
	}

	public void setMessage1(String message1) {
		this.message1 = message1;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getModulecode() {
		return modulecode;
	}

	public void setModulecode(String modulecode) {
		this.modulecode = modulecode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getoAgentNo() {
		return oAgentNo;
	}

	public void setoAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getBusinesscode() {
		return businesscode;
	}

	public void setBusinesscode(String businesscode) {
		this.businesscode = businesscode == null ? null : businesscode.trim();
	}

	public String getBusinessname() {
		return businessname;
	}

	public void setBusinessname(String businessname) {
		this.businessname = businessname == null ? null : businessname.trim();
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl == null ? null : imageurl.trim();
	}

	public String getExternalurl() {
		return externalurl;
	}

	public void setExternalurl(String externalurl) {
		this.externalurl = externalurl;
	}

	public String getAccno() {
		return accno;
	}

	public void setAccno(String accno) {
		this.accno = accno;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status == null ? null : status.trim();
	}

	public String getStatus1() {
		return status1;
	}

	public void setStatus1(String status1) {
		this.status1 = status1;
	}

	public String getExtends1() {
		return extends1;
	}

	public void setExtends1(String extends1) {
		this.extends1 = extends1 == null ? null : extends1.trim();
	}

	public String getExtends2() {
		return extends2;
	}

	public void setExtends2(String extends2) {
		this.extends2 = extends2 == null ? null : extends2.trim();
	}

	public String getExtends3() {
		return extends3;
	}

	public void setExtends3(String extends3) {
		this.extends3 = extends3 == null ? null : extends3.trim();
	}
}
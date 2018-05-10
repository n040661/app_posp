package xdt.dto.jsds;

import java.io.Serializable;

import xdt.util.UtilDate;

/**
 * @Description 江苏电商相关
 * @author Shiwen .Li
 * @date 2017年3月5日 下午1:51:49
 * @version V1.3.1
 */
public abstract class CommonDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 请求公共
	protected String groupId;// 请求方的合作编号
	protected String service;// 请求的交易服务码
	protected String signType;// 签名类型
	protected String sign;// 数据的签名字符串
	protected String datetime;// 系统时间（yyyyMMddHHmmss）
	private String merid; // 下游商户号

	// 响应公共
	protected String pl_service;// 请求的交易服务码
	protected String pl_signType;// 签名类型MD5
	protected String pl_sign;// 数据的签名字符串
	protected String pl_datetime;// 系统时间（yyyyMMddHHmmss）
	protected String pl_code;// 返回代码，详情见返回码说明
	protected String pl_message;// 返回消息，详细错误信息

	// 查询 回调响应参数
	public String orderNum;// 合作商订单号
	public String pl_orderNum;// 平台订单号
	public String pl_payState;// 交易状态
	public String pl_payMessage;// 交易说明
	public String pl_bankCardType;// 银行卡类型，CERDIT，DEBIT，UNKNOWN（目前仅微信支付类支持）

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getPl_service() {
		return pl_service;
	}

	public void setPl_service(String pl_service) {
		this.pl_service = pl_service;
	}

	public String getPl_signType() {
		return pl_signType;
	}

	public void setPl_signType(String pl_signType) {
		this.pl_signType = pl_signType;
	}

	public String getPl_sign() {
		return pl_sign;
	}

	public void setPl_sign(String pl_sign) {
		this.pl_sign = pl_sign;
	}

	public String getPl_datetime() {
		return pl_datetime;
	}

	public void setPl_datetime(String pl_datetime) {
		this.pl_datetime = pl_datetime;
	}

	public String getPl_code() {
		return pl_code;
	}

	public void setPl_code(String pl_code) {
		this.pl_code = pl_code;
	}

	public String getPl_message() {
		return pl_message;
	}

	public void setPl_message(String pl_message) {
		this.pl_message = pl_message;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getPl_orderNum() {
		return pl_orderNum;
	}

	public void setPl_orderNum(String pl_orderNum) {
		this.pl_orderNum = pl_orderNum;
	}

	public String getPl_payState() {
		return pl_payState;
	}

	public void setPl_payState(String pl_payState) {
		this.pl_payState = pl_payState;
	}

	public String getPl_payMessage() {
		return pl_payMessage;
	}

	public void setPl_payMessage(String pl_payMessage) {
		this.pl_payMessage = pl_payMessage;
	}

	public String getPl_bankCardType() {
		return pl_bankCardType;
	}

	public void setPl_bankCardType(String pl_bankCardType) {
		this.pl_bankCardType = pl_bankCardType;
	}

	public String getMerid() {
		return merid;
	}

	public void setMerid(String merid) {
		this.merid = merid;
	}
	@Override
	public String toString() {
		return "CommonDto [groupId=" + groupId + ", service=" + service + ", signType=" + signType + ", sign=" + sign
				+ ", datetime=" + datetime + ", merid=" + merid + ", pl_service=" + pl_service + ", pl_signType="
				+ pl_signType + ", pl_sign=" + pl_sign + ", pl_datetime=" + pl_datetime + ", pl_code=" + pl_code
				+ ", pl_message=" + pl_message + ", orderNum=" + orderNum + ", pl_orderNum=" + pl_orderNum
				+ ", pl_payState=" + pl_payState + ", pl_payMessage=" + pl_payMessage + ", pl_bankCardType="
				+ pl_bankCardType + "]";
	}


}

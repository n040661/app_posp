package xdt.dto.gateway.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.StringUtil;

/**
 * 
 * @Description 网关请求信息 
 * @author YanChao.Shang
 * @date 2017年12月25日 下午12:28:08 
 * @version V1.3.1
 */

public class GateWayRequestEntity {
	
	private String v_version; //版本号
	
	private String v_mid; //商户号
	
	private String v_oid; //订单号
	
	private String v_txnAmt;//交易金额
	
	private String v_notify_url; //回调地址
	
	private String v_url; //前台通知地址
	
	private String v_errorUrl; //错误页面
	
	private String v_bankAddr;//银行编码
	
	private String v_productName;//商品名称
	
	private String v_productNum;//商品数量
	
	private String v_productDesc; //商品描述
	
	private String v_cardType;//支付类型
	
	private String v_time;//交易时间
	
	private String v_expire_time; //订单有效时间
	
	private String v_currency;//支付币种
	
	private String v_channel;//渠道类型
	
	private String v_attach; //附加数据
	
	private String v_type;  //支付方式
	
	private String v_sign; //签名

	
	public String getV_version() {
		return v_version;
	}


	public void setV_version(String v_version) {
		this.v_version = v_version;
	}


	public String getV_mid() {
		return v_mid;
	}


	public void setV_mid(String v_mid) {
		this.v_mid = v_mid;
	}


	public String getV_oid() {
		return v_oid;
	}


	public void setV_oid(String v_oid) {
		this.v_oid = v_oid;
	}


	public String getV_txnAmt() {
		return v_txnAmt;
	}


	public void setV_txnAmt(String v_txnAmt) {
		this.v_txnAmt = v_txnAmt;
	}


	public String getV_notify_url() {
		return v_notify_url;
	}


	public void setV_notify_url(String v_notify_url) {
		this.v_notify_url = v_notify_url;
	}


	public String getV_url() {
		return v_url;
	}


	public void setV_url(String v_url) {
		this.v_url = v_url;
	}


	public String getV_errorUrl() {
		return v_errorUrl;
	}


	public void setV_errorUrl(String v_errorUrl) {
		this.v_errorUrl = v_errorUrl;
	}


	public String getV_bankAddr() {
		return v_bankAddr;
	}


	public void setV_bankAddr(String v_bankAddr) {
		this.v_bankAddr = v_bankAddr;
	}


	public String getV_productName() {
		return v_productName;
	}


	public void setV_productName(String v_productName) {
		this.v_productName = v_productName;
	}


	public String getV_productNum() {
		return v_productNum;
	}


	public void setV_productNum(String v_productNum) {
		this.v_productNum = v_productNum;
	}


	public String getV_productDesc() {
		return v_productDesc;
	}


	public void setV_productDesc(String v_productDesc) {
		this.v_productDesc = v_productDesc;
	}


	public String getV_cardType() {
		return v_cardType;
	}


	public void setV_cardType(String v_cardType) {
		this.v_cardType = v_cardType;
	}


	public String getV_time() {
		return v_time;
	}


	public void setV_time(String v_time) {
		this.v_time = v_time;
	}


	public String getV_expire_time() {
		return v_expire_time;
	}


	public void setV_expire_time(String v_expire_time) {
		this.v_expire_time = v_expire_time;
	}


	public String getV_currency() {
		return v_currency;
	}


	public void setV_currency(String v_currency) {
		this.v_currency = v_currency;
	}


	public String getV_channel() {
		return v_channel;
	}


	public void setV_channel(String v_channel) {
		this.v_channel = v_channel;
	}


	public String getV_attach() {
		return v_attach;
	}


	public void setV_attach(String v_attach) {
		this.v_attach = v_attach;
	}


	public String getV_type() {
		return v_type;
	}


	public void setV_type(String v_type) {
		this.v_type = v_type;
	}


	public String getV_sign() {
		return v_sign;
	}


	public void setV_sign(String v_sign) {
		this.v_sign = v_sign;
	}


}

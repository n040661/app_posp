package xdt.dto.nbs.alipay;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import xdt.dto.nbs.base.WechatRequestBase;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.StringUtil;

public class AlipayParamRequest extends WechatRequestBase{
	
	
	

	/**
	 * 支付宝请求公共参数
	 */
	private String service_type;//服务类型 1:ALIPAY_SCANNED（支付宝扫码）2：ALIPAY_MICRO（支付宝刷卡）3： ALIPAY_SERVICEWINDOW（支付宝服务窗）4:ALIPAY_REFUND(支付宝退款)5：ALIPAY_REVERSEORDER（支付宝撤销)6:ALIPAY_ORDERQUERY(支付宝查询)
//	                                    7:WECHAT_SCANNED（微信扫码）8：WECHAT_MICRO（微信刷卡）9： WECHAT_WEBPA（微信公众号）10:WECHAT_REFUND(微信退款)11：WECHAT_REVERSEORDER(微信撤销)12:WECHAT_CLOSEORDER（微信关单）13:WECHAT_ORDERQUERY(微信查询)
	private String mch_id;//商户号
	private String merchantId;//下游商户号
	private String out_trade_no;//商户订单号
	private int total_fee;//总金额
	private String subject;//订单标题
	private String body;//商品描述
	private String time_start;//订单生成时间
	private String time_expire;//订单超时时间
	private String device_info;//设备号
	private String notify_url;//通知地址
	private String nonce_str;//随机字符串
	private String sign;//签名
	
	
	private String attach;//商家数据包
	/**
	 * 支付宝扫码和刷卡请求参数
	 */
	private String op_user_id;//操作员
	private String store_id;//商户门店编号
	private String limit_pay;//指定支付方式
	/**
	 * 支付宝刷卡请求参数
	 */
	private String auth_code;//授权码
	private String detail;//商品详情
	private String scene;//支付场景
	/**
	 * 支付宝服务窗请求参数
	 */
	private String spbill_create_ip;//终端IP
	private String callback_url;//自定义跳转页面
	
	/**
	 * 支付宝退款请求参数
	 */
	private String transaction_id;//北农商订单号
	private String out_refund_no;//商户退款单号
	private String refund_fee;//申请退款金额
	private String refund_reason;//退款原因
	private String out_request_no;//请求编码
	
    
	public AlipayParamRequest() {
		super();
	}
    

	public AlipayParamRequest(String key,String service_type,String mch_id,String merchantId, String out_trade_no,
			int total_fee, String subject, String body, String time_start, String time_expire, String device_info,
			String notify_url, String nonce_str, String attach, String op_user_id, String store_id,
			String limit_pay, String auth_code, String detail, String scene, String spbill_create_ip,String callback_url, String transaction_id,
			String out_refund_no, String refund_fee, String refund_reason, String out_request_no, Logger log) {
		
        setService_type(service_type);
        setMch_id(mch_id);
        setMerchantId(merchantId);        
        setOut_trade_no(out_trade_no);
        setTotal_fee(total_fee);
        setSubject(subject);
        setBody(body);
        setTime_expire(time_expire);
        setTime_start(time_start);
        setDevice_info(device_info);
        setNotify_url(notify_url);
        setNonce_str(nonce_str);
        setAttach(attach);
        setOp_user_id(op_user_id);
        setStore_id(store_id);
        setLimit_pay(limit_pay);
        setAuth_code(auth_code);
        setDetail(detail);
        setScene(scene);
        setSpbill_create_ip(spbill_create_ip);
        setCallback_url(callback_url);
        setTransaction_id(transaction_id);
        setOut_refund_no(out_refund_no);
        setRefund_fee(refund_fee);
        setRefund_reason(refund_reason);
        setOut_refund_no(out_refund_no);
		String sign = SignatureUtil.getSign(toMap(), key, log);
	    setSign(sign);// 把签名数据设置到Sign这个属性中
	}


	public String getService_type() {
		return service_type;
	}


	public void setService_type(String service_type) {
		this.service_type = service_type;
	}


	public String getMch_id() {
		return mch_id;
	}


	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}


	public String getMerchantId() {
		return merchantId;
	}


	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}


	public String getOut_trade_no() {
		return out_trade_no;
	}


	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
   
	public int getTotal_fee() {
		return total_fee;
	}


	public void setTotal_fee(int total_fee) {
		this.total_fee = total_fee;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}


	public String getTime_start() {
		return time_start;
	}


	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}


	public String getTime_expire() {
		return time_expire;
	}


	public void setTime_expire(String time_expire) {
		this.time_expire = time_expire;
	}


	public String getDevice_info() {
		return device_info;
	}


	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}


	public String getNotify_url() {
		return notify_url;
	}


	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}


	public String getNonce_str() {
		return nonce_str;
	}


	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}


	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
	}


	public String getAttach() {
		return attach;
	}


	public void setAttach(String attach) {
		this.attach = attach;
	}


	public String getOp_user_id() {
		return op_user_id;
	}


	public void setOp_user_id(String op_user_id) {
		this.op_user_id = op_user_id;
	}


	public String getStore_id() {
		return store_id;
	}


	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}


	public String getLimit_pay() {
		return limit_pay;
	}


	public void setLimit_pay(String limit_pay) {
		this.limit_pay = limit_pay;
	}


	public String getAuth_code() {
		return auth_code;
	}


	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}


	public String getDetail() {
		return detail;
	}


	public void setDetail(String detail) {
		this.detail = detail;
	}


	public String getScene() {
		return scene;
	}


	public void setScene(String scene) {
		this.scene = scene;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}


	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}


	public String getCallback_url() {
		return callback_url;
	}


	public void setCallback_url(String callback_url) {
		this.callback_url = callback_url;
	}


	public String getTransaction_id() {
		return transaction_id;
	}


	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}


	public String getOut_refund_no() {
		return out_refund_no;
	}


	public void setOut_refund_no(String out_refund_no) {
		this.out_refund_no = out_refund_no;
	}


	public String getRefund_fee() {
		return refund_fee;
	}


	public void setRefund_fee(String refund_fee) {
		this.refund_fee = refund_fee;
	}


	public String getRefund_reason() {
		return refund_reason;
	}


	public void setRefund_reason(String refund_reason) {
		this.refund_reason = refund_reason;
	}


	public String getOut_request_no() {
		return out_request_no;
	}


	public void setOut_request_no(String out_request_no) {
		this.out_request_no = out_request_no;
	}   
	@Override
	public String toString() {
		return "AlipayParamRequest [getService_type()=" + getService_type() + ", getMch_id()=" + getMch_id()
				+ ", getMerchantId()=" + getMerchantId() + ", getOut_trade_no()=" + getOut_trade_no()
				+ ", getTotal_fee()=" + getTotal_fee() + ", getSubject()=" + getSubject() + ", getBody()=" + getBody()
				+ ", getTime_start()=" + getTime_start() + ", getTime_expire()=" + getTime_expire()
				+ ", getDevice_info()=" + getDevice_info() + ", getNotify_url()=" + getNotify_url()
				+ ", getNonce_str()=" + getNonce_str() + ", getSign()=" + getSign() + ", getAttach()=" + getAttach()
				+ ", getOp_user_id()=" + getOp_user_id() + ", getStore_id()=" + getStore_id() + ", getLimit_pay()="
				+ getLimit_pay() + ", getAuth_code()=" + getAuth_code() + ", getDetail()=" + getDetail()
				+ ", getScene()=" + getScene() + ", getCallback_url()=" + getCallback_url() + ", getTransaction_id()="
				+ getTransaction_id() + ", getOut_refund_no()=" + getOut_refund_no() + ", getRefund_fee()="
				+ getRefund_fee() + ", getRefund_reason()=" + getRefund_reason() + ", getOut_request_no()="
				+ getOut_request_no() + ", toMap()=" + toMap() + ", toString()=" + super.toString() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + "]";
	}
	public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);
                if (obj != null && StringUtil.isNotBlank(String.valueOf(obj))) {
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}

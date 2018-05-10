package xdt.dto.nbs.webpay;

import xdt.dto.nbs.AbstractBase;


/**
 * 下游商户公众号支付返回信息
 *
 * @author zhang.hui@pufubao.net
 * @version v1.0
 * @date 2016年10月14日 下午4:06:15
 */
@SuppressWarnings("serial")
public class WechatWebPayResponse extends AbstractBase {

    /**
     * 接口名称
     */
    private String service_type;
    /**
     * 返回码
     */
    private String return_code;
    /**
     * 返回信息
     */
    private String return_msg;

    // 协议返回的具体数据（以下字段在return_code 为SUCCESS 的时候有返回）
    /**
     * 公众号ID
     */
    private String appid;
    /**
     * 订单状态
     */
    private String trade_state;
    /**
     * 商户号
     */
    private String mch_id;
    /**
     * 设备号
     */
    private String device_info;
    /**
     * 随机字符串
     */
    private String nonce_str;
    /**
     * 签名
     */
    private String sign;
    /**
     * 业务结果
     */
    private String result_code;
    /**
     * 错误码
     */
    private String err_code;
    /**
     * 错误信息描述
     */
    private String err_code_des;

    // 业务返回的具体数据（以下字段在return_code 和result_code 都为SUCCESS 的时候有返回）
    /**
     * 交易类型
     */
    private String trade_type;
    /**
     * 用户标识
     */
    private String openid;
    /**
     * 微信订单号
     */
    private String wechat_transaction_id;
    /**
     * 农商行订单号
     */
    private String transaction_id;
    /**
     * 商户订单号
     */
    private String out_trade_no;
    /**
     * 总金额
     */
    private String total_fee;
    /**
     * 付款银行
     */
    private String bank_type;
    /**
     * 支付完成时间
     */
    private String time_end;
    /**
     * 是否关注公众账号
     */
    private String is_subscribe;
    /**
     * 货币种类
     */
    private String fee_type;
    /**
     * 附加信息
     */
    private String attach;
    
    private String alipay_transaction_id;
    
    

    public String getAlipay_transaction_id() {
		return alipay_transaction_id;
	}

	public void setAlipay_transaction_id(String alipay_transaction_id) {
		this.alipay_transaction_id = alipay_transaction_id;
	}

	public String getService_type() {
		return service_type;
	}

	public void setService_type(String service_type) {
		this.service_type = service_type;
	}

	public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
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

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_des() {
        return err_code_des;
    }

    public void setErr_code_des(String err_code_des) {
        this.err_code_des = err_code_des;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

	public String getTrade_state() {
		return trade_state;
	}

	public void setTrade_state(String trade_state) {
		this.trade_state = trade_state;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getWechat_transaction_id() {
		return wechat_transaction_id;
	}

	public void setWechat_transaction_id(String wechat_transaction_id) {
		this.wechat_transaction_id = wechat_transaction_id;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getBank_type() {
		return bank_type;
	}

	public void setBank_type(String bank_type) {
		this.bank_type = bank_type;
	}

	public String getTime_end() {
		return time_end;
	}

	public void setTime_end(String time_end) {
		this.time_end = time_end;
	}

	public String getIs_subscribe() {
		return is_subscribe;
	}

	public void setIs_subscribe(String is_subscribe) {
		this.is_subscribe = is_subscribe;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

}

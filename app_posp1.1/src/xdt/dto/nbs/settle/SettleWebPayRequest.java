package xdt.dto.nbs.settle;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import xdt.dto.nbs.base.WechatRequestBase;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.StringUtil;

public class SettleWebPayRequest extends WechatRequestBase {
	
	/**
     * 商户号ID
     */ 
    private String mch_id;  
    
	/**
     * 支付订单号
     */ 
    private String order_num;
    
	/**
     * 支付数量
     */ 
    private String order_count;
    
	/**
     * 商户流水号
     */ 
    private String out_trade_no;

	/**
     * 签名
     */ 
    private String sign;
    
    

	public SettleWebPayRequest(String key,String mch_id, String order_num, String order_count, String out_trade_no,Logger log) {
	    setMch_id(mch_id);
	    setOrder_count(order_count);
	    setOrder_num(order_num);
	    setOut_trade_no(out_trade_no);
	    String sign=SignatureUtil.getSign(toMap(), key, log);
	    setSign(sign);

	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getOrder_num() {
		return order_num;
	}

	public void setOrder_num(String order_num) {
		this.order_num = order_num;
	}

	public String getOrder_count() {
		return order_count;
	}

	public void setOrder_count(String order_count) {
		this.order_count = order_count;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
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

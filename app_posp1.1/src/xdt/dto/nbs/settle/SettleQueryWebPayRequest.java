package xdt.dto.nbs.settle;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import xdt.dto.nbs.base.WechatRequestBase;
import xdt.quickpay.nbs.common.util.SignatureUtil;
import xdt.quickpay.nbs.common.util.StringUtil;

public class SettleQueryWebPayRequest extends WechatRequestBase {
	
	/**
     * 商户号ID
     */ 
    private String mch_id;  
    
	/**
     * 支付订单号
     */ 
    private String settle_num;
    
	/**
     * 支付数量
     */ 
    private String out_trade_no;
    
	/**
     * 商户流水号
     */ 
    private String settle_mode;

	/**
     * 签名
     */ 
    private String sign;
    
    

	public SettleQueryWebPayRequest(String key,String mch_id, String settle_num, String out_trade_no, String settle_mode,Logger log) {
	    setMch_id(mch_id);
	    setSettle_num(settle_num);
	    setSettle_mode(settle_mode);
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

	public String getSettle_num() {
		return settle_num;
	}

	public void setSettle_num(String settle_num) {
		this.settle_num = settle_num;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getSettle_mode() {
		return settle_mode;
	}

	public void setSettle_mode(String settle_mode) {
		this.settle_mode = settle_mode;
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

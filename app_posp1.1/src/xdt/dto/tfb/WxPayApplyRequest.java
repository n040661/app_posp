package xdt.dto.tfb;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import xdt.dto.nbs.base.WechatRequestBase;
import xdt.quickpay.nbs.common.util.StringUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;

public class WxPayApplyRequest {

	private String sign_type;//签名方式
	private String ver;//接口版本
	private String input_charset;//字符集
	private String sign;//签名
	private String sign_key_index;//密钥序号
	//---------------------------------------
	private String spid;//商户号
	private String notify_url;//异步通知回调URL
	private String pay_show_url;//同步通知回调URL
	private String sp_billno;//商户订单号
	private String spbill_create_ip;//商户ip
	private String out_channel;//外接支付方式
	private String pay_type;//支付类型
	private String tran_time;//发起交易时间
	private String tran_amt;//交易金额
	private String cur_type;//币种类型
	private String pay_limit;//支付限制
	private String auth_code;//二维码
	private String item_name;//商品描述
	private String item_attach;//商品附加数据
	private String bank_mch_name;//三级商户名称，微信必填
	private String bank_mch_id;//三级商户ID，微信必填
	private String sp_udid;//终端设备id，qq钱包必填
	private String url;//异步url
	private String reUrl;//同步url
	private String type;
	
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getReUrl() {
		return reUrl;
	}
	public void setReUrl(String reUrl) {
		this.reUrl = reUrl;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getInput_charset() {
		return input_charset;
	}
	public void setInput_charset(String input_charset) {
		this.input_charset = input_charset;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSign_key_index() {
		return sign_key_index;
	}
	public void setSign_key_index(String sign_key_index) {
		this.sign_key_index = sign_key_index;
	}
	public String getSpid() {
		return spid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getPay_show_url() {
		return pay_show_url;
	}
	public void setPay_show_url(String pay_show_url) {
		this.pay_show_url = pay_show_url;
	}
	public String getSp_billno() {
		return sp_billno;
	}
	public void setSp_billno(String sp_billno) {
		this.sp_billno = sp_billno;
	}
	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}
	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}
	public String getOut_channel() {
		return out_channel;
	}
	public void setOut_channel(String out_channel) {
		this.out_channel = out_channel;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getTran_time() {
		return tran_time;
	}
	public void setTran_time(String tran_time) {
		this.tran_time = tran_time;
	}
	public String getTran_amt() {
		return tran_amt;
	}
	public void setTran_amt(String tran_amt) {
		this.tran_amt = tran_amt;
	}
	public String getCur_type() {
		return cur_type;
	}
	public void setCur_type(String cur_type) {
		this.cur_type = cur_type;
	}
	public String getPay_limit() {
		return pay_limit;
	}
	public void setPay_limit(String pay_limit) {
		this.pay_limit = pay_limit;
	}
	public String getAuth_code() {
		return auth_code;
	}
	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getItem_attach() {
		return item_attach;
	}
	public void setItem_attach(String item_attach) {
		this.item_attach = item_attach;
	}
	public String getBank_mch_name() {
		return bank_mch_name;
	}
	public void setBank_mch_name(String bank_mch_name) {
		this.bank_mch_name = bank_mch_name;
	}
	public String getBank_mch_id() {
		return bank_mch_id;
	}
	public void setBank_mch_id(String bank_mch_id) {
		this.bank_mch_id = bank_mch_id;
	}
	public String getSp_udid() {
		return sp_udid;
	}
	public void setSp_udid(String sp_udid) {
		this.sp_udid = sp_udid;
	}
/*	@Override
	public TreeMap<String, Object> toMap() {
		TreeMap<String, Object> map = new TreeMap<String, Object>();
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
*/
	@Override
	public String toString() {
		return "WxPayApplyRequest [sign_type=" + sign_type + ", ver=" + ver
				+ ", input_charset=" + input_charset + ", sign=" + sign
				+ ", sign_key_index=" + sign_key_index + ", spid=" + spid
				+ ", notify_url=" + notify_url + ", pay_show_url="
				+ pay_show_url + ", sp_billno=" + sp_billno
				+ ", spbill_create_ip=" + spbill_create_ip + ", out_channel="
				+ out_channel + ", pay_type=" + pay_type + ", tran_time="
				+ tran_time + ", tran_amt=" + tran_amt + ", cur_type="
				+ cur_type + ", pay_limit=" + pay_limit + ", auth_code="
				+ auth_code + ", item_name=" + item_name + ", item_attach="
				+ item_attach + ", bank_mch_name=" + bank_mch_name
				+ ", bank_mch_id=" + bank_mch_id + ", sp_udid=" + sp_udid + "]";
	}
	
	
	
}

package xdt.dto;

/**
 * Created with IntelliJ IDEA.
 * 百度支付回调参数
 * User: Jeff
 * Date: 15-5-7
 * Time: 下午7:48
 * To change this template use File | Settings | File Templates.
 */
public class BaiduBackRequestDTO {

    String sign_method;
    String total_amount;
    String fee_amount;
    String sp_no;
    String transport_amount;
    String extra;
    String input_charset;
    String bfb_order_no;
    String unit_count;
    String pay_time;
    String buyer_sp_username;
    String order_no;
    String currency;
    String sign;
    String pay_type;
    String pay_result;
    String bank_no;
    String unit_amount;
    String bfb_order_create_time;
    String version;

    public String getSign_method() {
        return sign_method;
    }

    public void setSign_method(String sign_method) {
        this.sign_method = sign_method;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getFee_amount() {
        return fee_amount;
    }

    public void setFee_amount(String fee_amount) {
        this.fee_amount = fee_amount;
    }

    public String getSp_no() {
        return sp_no;
    }

    public void setSp_no(String sp_no) {
        this.sp_no = sp_no;
    }

    public String getTransport_amount() {
        return transport_amount;
    }

    public void setTransport_amount(String transport_amount) {
        this.transport_amount = transport_amount;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getInput_charset() {
        return input_charset;
    }

    public void setInput_charset(String input_charset) {
        this.input_charset = input_charset;
    }

    public String getBfb_order_no() {
        return bfb_order_no;
    }

    public void setBfb_order_no(String bfb_order_no) {
        this.bfb_order_no = bfb_order_no;
    }

    public String getUnit_count() {
        return unit_count;
    }

    public void setUnit_count(String unit_count) {
        this.unit_count = unit_count;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public String getBuyer_sp_username() {
        return buyer_sp_username;
    }

    public void setBuyer_sp_username(String buyer_sp_username) {
        this.buyer_sp_username = buyer_sp_username;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getPay_result() {
        return pay_result;
    }

    public void setPay_result(String pay_result) {
        this.pay_result = pay_result;
    }

    public String getBank_no() {
        return bank_no;
    }

    public void setBank_no(String bank_no) {
        this.bank_no = bank_no;
    }

    public String getUnit_amount() {
        return unit_amount;
    }

    public void setUnit_amount(String unit_amount) {
        this.unit_amount = unit_amount;
    }

    public String getBfb_order_create_time() {
        return bfb_order_create_time;
    }

    public void setBfb_order_create_time(String bfb_order_create_time) {
        this.bfb_order_create_time = bfb_order_create_time;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "BaiduBackRequestDTO{" +
                "sign_method='" + sign_method + '\'' +
                ", total_amount='" + total_amount + '\'' +
                ", fee_amount='" + fee_amount + '\'' +
                ", sp_no='" + sp_no + '\'' +
                ", transport_amount='" + transport_amount + '\'' +
                ", extra='" + extra + '\'' +
                ", input_charset='" + input_charset + '\'' +
                ", bfb_order_no='" + bfb_order_no + '\'' +
                ", unit_count='" + unit_count + '\'' +
                ", pay_time='" + pay_time + '\'' +
                ", buyer_sp_username='" + buyer_sp_username + '\'' +
                ", order_no='" + order_no + '\'' +
                ", currency='" + currency + '\'' +
                ", sign='" + sign + '\'' +
                ", pay_type='" + pay_type + '\'' +
                ", pay_result='" + pay_result + '\'' +
                ", bank_no='" + bank_no + '\'' +
                ", unit_amount='" + unit_amount + '\'' +
                ", bfb_order_create_time='" + bfb_order_create_time + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}

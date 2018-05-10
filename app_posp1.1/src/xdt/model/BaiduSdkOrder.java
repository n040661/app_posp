package xdt.model;

/**
 * 百度sdk订单
 * User: Jeff
 * Date: 15-5-7
 * Time: 下午8:04
 * To change this template use File | Settings | File Templates.
 */
public class BaiduSdkOrder {

    Integer currency;//币种  默认1：人名币
    String extra;    //商户自定义数据，不超过255个字符
    String goods_desc;   //商品描述
    String goods_name;   //商品名称
    String goods_url;   //商品在商户网站上的url
    String input_charset; //请求参数的字符编码
    String order_create_time; //创建订单时间
    String order_no; //订单号，必须保证在商户系统内部唯一
    Integer pay_type; //支付方式
    String return_url; //百度钱包主动通知商户结果的urlb
    Integer service_code; //服务编号，目前必须为1
    String sign_method; //签名方法   1：md5 2:sha-1
    String sp_no; //百度钱包商户号
    String sp_request_type;//收银台类型 0 代表免登收银台 1 代表登录版收银台 2 代表统一收银台
    Integer total_amount; //总金额，以分为单位
    String transport_amount; //运费
    String unit_amount; //商品单价
    String unit_count; //商品数量
    String version;   //接口版本号
    String sign;//签名结果


    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getGoods_desc() {
        return goods_desc;
    }

    public void setGoods_desc(String goods_desc) {
        this.goods_desc = goods_desc;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_url() {
        return goods_url;
    }

    public void setGoods_url(String goods_url) {
        this.goods_url = goods_url;
    }

    public String getInput_charset() {
        return input_charset;
    }

    public void setInput_charset(String input_charset) {
        this.input_charset = input_charset;
    }

    public String getOrder_create_time() {
        return order_create_time;
    }

    public void setOrder_create_time(String order_create_time) {
        this.order_create_time = order_create_time;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public Integer getPay_type() {
        return pay_type;
    }

    public void setPay_type(Integer pay_type) {
        this.pay_type = pay_type;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public Integer getService_code() {
        return service_code;
    }

    public void setService_code(Integer service_code) {
        this.service_code = service_code;
    }

    public String getSign_method() {
        return sign_method;
    }

    public void setSign_method(String sign_method) {
        this.sign_method = sign_method;
    }

    public String getSp_no() {
        return sp_no;
    }

    public void setSp_no(String sp_no) {
        this.sp_no = sp_no;
    }

    public String getSp_request_type() {
        return sp_request_type;
    }

    public void setSp_request_type(String sp_request_type) {
        this.sp_request_type = sp_request_type;
    }

    public Integer getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Integer total_amount) {
        this.total_amount = total_amount;
    }

    public String getTransport_amount() {
        return transport_amount;
    }

    public void setTransport_amount(String transport_amount) {
        this.transport_amount = transport_amount;
    }

    public String getUnit_amount() {
        return unit_amount;
    }

    public void setUnit_amount(String unit_amount) {
        this.unit_amount = unit_amount;
    }

    public String getUnit_count() {
        return unit_count;
    }

    public void setUnit_count(String unit_count) {
        this.unit_count = unit_count;
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
}

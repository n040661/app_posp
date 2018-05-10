package xdt.quickpay.mobao;

/**
 * 摩宝支付验证接口返回对象
 * User: Jeff
 * Date: 16-3-11
 * Time: 上午10:05
 * To change this template use File | Settings | File Templates.
 */
public class MobaoPayValidateResponseDto {
    private String status;//交易状态      00：成功  01：失败 02：系统错误
    private String mercId; //商户号
    private String orderId;//商户订单号
    private String ksPayOrderId; //摩宝平台订单号
    private String chanlRefCode;//渠道响应码
    private String bankOrderId;//渠道订单号
    private String yzm;//手机验证码
    private String transDate;//交易日期
    private String  transTime;//交易时间
    private String refCode;//交易返回码
    private String refMsg;//交易返回信息说明

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMercId() {
        return mercId;
    }

    public void setMercId(String mercId) {
        this.mercId = mercId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getKsPayOrderId() {
        return ksPayOrderId;
    }

    public void setKsPayOrderId(String ksPayOrderId) {
        this.ksPayOrderId = ksPayOrderId;
    }

    public String getChanlRefCode() {
        return chanlRefCode;
    }

    public void setChanlRefCode(String chanlRefCode) {
        this.chanlRefCode = chanlRefCode;
    }

    public String getBankOrderId() {
        return bankOrderId;
    }

    public void setBankOrderId(String bankOrderId) {
        this.bankOrderId = bankOrderId;
    }

    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getRefMsg() {
        return refMsg;
    }

    public void setRefMsg(String refMsg) {
        this.refMsg = refMsg;
    }

    @Override
    public String toString() {
        return "MobaoPayValidateResponseDto{" +
                "status='" + status + '\'' +
                ", mercId='" + mercId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", ksPayOrderId='" + ksPayOrderId + '\'' +
                ", chanlRefCode='" + chanlRefCode + '\'' +
                ", bankOrderId='" + bankOrderId + '\'' +
                ", yzm='" + yzm + '\'' +
                ", transDate='" + transDate + '\'' +
                ", transTime='" + transTime + '\'' +
                ", refCode='" + refCode + '\'' +
                ", refMsg='" + refMsg + '\'' +
                '}';
    }
}

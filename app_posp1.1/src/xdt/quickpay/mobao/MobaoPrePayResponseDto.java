package xdt.quickpay.mobao;

/**
 *调用摩宝预支付接口返回的对象实体
 * User: Jeff
 * Date: 16-3-10
 * Time: 下午1:54
 * To change this template use File | Settings | File Templates.
 */
public class MobaoPrePayResponseDto {
    String  status;  //交易状态   00：成功 01：失败  02：系统错误
    String   merId; //商户号
    String  orderId;   //商户交易订单号
    String  ksPayOrderId;  //摩宝平台订单号
    String  chanlRefCode;  //渠道响应码
    String  bankOrderId;  //渠道订单号
    String  yzm;  // 手机验证码
    String  transDate; //交易日期
    String  transTime;   //交易时间
    String  refCode;    //交易返回码   01 成功，00失败
    String  refMsg;  //交易返回信息说明

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMerId() {
        return merId;
    }

    public void setMerId(String merId) {
        this.merId = merId;
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
        return "MobaoPrePayResponseDto{" +
                "status='" + status + '\'' +
                ", merId='" + merId + '\'' +
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

package xdt.quickpay.mobao;

/**
 * 摩宝查询订单的返回对象封装
 * User: Jeff
 * Date: 16-3-15
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
public class MobaoTransSearchResponseDto {

    String status;
    String mercId;
    String ksOrderId;
    String orderId;
    String refCode;  //‘00’成功 '01’预交易成功‘02’交易失败 03  交易处理中
    String refMsg;

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

    public String getKsOrderId() {
        return ksOrderId;
    }

    public void setKsOrderId(String ksOrderId) {
        this.ksOrderId = ksOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
        return "MobaoTransSearchResponseDto{" +
                "status='" + status + '\'' +
                ", mercId='" + mercId + '\'' +
                ", ksOrderId='" + ksOrderId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", refCode='" + refCode + '\'' +
                ", refMsg='" + refMsg + '\'' +
                '}';
    }
}

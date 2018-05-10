package xdt.dto;

/**
 * 商城快捷支付验证接口请求参数封装
 * User: Jeff
 * Date: 16-3-14
 * Time: 下午2:01
 * To change this template use File | Settings | File Templates.
 */
public class ShopOrderQuickPayRequestDTO {

     String orderId;//订单号
     String cardNo;//银行卡号
     String message;//短信信息
    String cerType;
    String cerNumber;
    String mobile;
    String cardByName;
    String cvv;
    String expireDate;//卡有效期 （贷记卡必填）yearmonth(例：2408)


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ShopOrderQuickPayRequestDTO{" +
                "orderId='" + orderId + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", message='" + message + '\'' +
                '}';
    }


    public String getCerType() {
        return cerType;
    }

    public void setCerType(String cerType) {
        this.cerType = cerType;
    }

    public String getCerNumber() {
        return cerNumber;
    }

    public void setCerNumber(String cerNumber) {
        this.cerNumber = cerNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCardByName() {
        return cardByName;
    }

    public void setCardByName(String cardByName) {
        this.cardByName = cardByName;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}

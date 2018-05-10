package xdt.dto;

/**
 * 商城快捷支付预支付短信重发请求实体
 * User: Jeff
 * Date: 16-3-14
 * Time: 下午2:01
 * To change this template use File | Settings | File Templates.
 */
public class ShopOrderPreReSendMsgRequestDTO {

     String orderId;//订单号
     String cardNo;//银行卡号


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
}

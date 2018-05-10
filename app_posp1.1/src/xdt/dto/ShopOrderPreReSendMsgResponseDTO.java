package xdt.dto;

/**
 * 商城快捷支付预支付短信重发请求实体
 * User: Jeff
 * Date: 16-3-14
 * Time: 下午2:01
 * To change this template use File | Settings | File Templates.
 */
public class ShopOrderPreReSendMsgResponseDTO {


    private String  orderStatus; //0:成功 1：失败

    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述


    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getRetCode() {
        return retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

}

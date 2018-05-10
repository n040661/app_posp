package xdt.dto;

/**
 * 转账生成订单返回封装类
 * User: Jeff
 * Date: 15-5-20
 * Time: 上午9:38
 * To change this template use File | Settings | File Templates.
 */
public class RemitProOrderResponseDTO {
    Integer retCode;//返回码
    String retMessage;//返回码 0生成订单成功  1 生成订单失败 100 系统异常
    String orderNumber;//订单号

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

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}

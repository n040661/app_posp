package xdt.dto;

/**
 * 商城快捷支付预支付接口返回参数封装
 * User: Jeff
 * Date: 16-3-14
 * Time: 下午2:01
 * To change this template use File | Settings | File Templates.
 */
public class ShopOrderQuickPrePayResponseDTO {

    private String  orderStatus; //0:成功 1：失败

    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述

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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "ShopOrderQuickPrePayResponseDTO{" +
                "orderStatus='" + orderStatus + '\'' +
                ", retCode=" + retCode +
                ", retMessage='" + retMessage + '\'' +
                '}';
    }
}

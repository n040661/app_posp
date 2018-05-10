package xdt.dto;

/**
 * 商城快捷支付预消费请求
 * User: Jeff
 * Date: 16-3-11
 * Time: 下午2:20
 * To change this template use File | Settings | File Templates.
 */
public class SubmitOrderNoCardPayResponseDTO {
    private Integer retCode;// 信息编号

    private String retMessage;// 信息描述

    private String orderNo;//订单编号

    private String orderAmt;//订单金额

    private String goodsName;//订单名称

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(String orderAmt) {
        this.orderAmt = orderAmt;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}

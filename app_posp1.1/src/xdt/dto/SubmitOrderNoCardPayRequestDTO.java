package xdt.dto;

import java.util.List;

/**
 * 商城快捷支付预消费请求
 * User: Jeff
 * Date: 16-3-11
 * Time: 下午2:20
 * To change this template use File | Settings | File Templates.
 */
public class SubmitOrderNoCardPayRequestDTO {

    String orderTotalAmt;// 订单总金额
    String orderRealTotalAmt;// 订单成交金额
    String receiveType; // 收货方式，0：上门自取， 1：物流配送
    String buyerMessage;  //   买家留言
    List<GoodsRequest> goodsList; // 商品列表
    String goodsID; // 商品編号
    String goodsNum; //    商品数量
    String receiveAddressId;// 收货地址ID
    private String altLat;//经纬度（逗号隔开）
    private String gpsAddress;//gps获取的地址信息(中文)


    public String getOrderTotalAmt() {
        return orderTotalAmt;
    }

    public void setOrderTotalAmt(String orderTotalAmt) {
        this.orderTotalAmt = orderTotalAmt;
    }

    public String getOrderRealTotalAmt() {
        return orderRealTotalAmt;
    }

    public void setOrderRealTotalAmt(String orderRealTotalAmt) {
        this.orderRealTotalAmt = orderRealTotalAmt;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public List<GoodsRequest> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsRequest> goodsList) {
        this.goodsList = goodsList;
    }

    public String getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(String goodsID) {
        this.goodsID = goodsID;
    }

    public String getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(String goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getReceiveAddressId() {
        return receiveAddressId;
    }

    public void setReceiveAddressId(String receiveAddressId) {
        this.receiveAddressId = receiveAddressId;
    }

    public String getAltLat() {
        return altLat;
    }

    public void setAltLat(String altLat) {
        this.altLat = altLat;
    }

    public String getGpsAddress() {
        return gpsAddress;
    }

    public void setGpsAddress(String gpsAddress) {
        this.gpsAddress = gpsAddress;
    }

    @Override
    public String toString() {
        return "SubmitOrderNoCardPayRequestDTO{" +
                "orderTotalAmt='" + orderTotalAmt + '\'' +
                ", orderRealTotalAmt='" + orderRealTotalAmt + '\'' +
                ", receiveType='" + receiveType + '\'' +
                ", buyerMessage='" + buyerMessage + '\'' +
                ", goodsList=" + goodsList +
                ", goodsID='" + goodsID + '\'' +
                ", goodsNum='" + goodsNum + '\'' +
                ", receiveAddressId='" + receiveAddressId + '\'' +
                ", altLat='" + altLat + '\'' +
                ", gpsAddress='" + gpsAddress + '\'' +
                '}';
    }
}

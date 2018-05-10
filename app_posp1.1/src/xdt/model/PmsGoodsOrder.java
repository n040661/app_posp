package xdt.model;


public class PmsGoodsOrder {

    private String orderNo;

    private String goodsId;

    private String goodsImageUrl;

    private String goodsPrice;

    private String goodsNum;

    private String totalAmt;

    public String getGoodsImageUrl() {
        return goodsImageUrl;
    }

    public void setGoodsImageUrl(String goodsImageUrl) {
        this.goodsImageUrl = goodsImageUrl == null ? null : goodsImageUrl.trim();
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice == null ? null : goodsPrice.trim();
    }

    public String getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(String goodsNum) {
        this.goodsNum = goodsNum == null ? null : goodsNum.trim();
    }

    public String getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(String totalAmt) {
        this.totalAmt = totalAmt == null ? null : totalAmt.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return "PmsGoodsOrder{" +
                "orderNo='" + orderNo + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", goodsImageUrl='" + goodsImageUrl + '\'' +
                ", goodsPrice='" + goodsPrice + '\'' +
                ", goodsNum='" + goodsNum + '\'' +
                ", totalAmt='" + totalAmt + '\'' +
                '}';
    }
}
package xdt.dto;

/**
 * 商品订单请求商品实体
 * User: Jeff
 * Date: 16-3-11
 * Time: 下午3:42
 * To change this template use File | Settings | File Templates.
 */
public class GoodsRequest {
    String goodsId; //商品編号
    Integer goodsNum; //商品数量

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }
}

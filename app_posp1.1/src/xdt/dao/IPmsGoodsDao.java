package xdt.dao;

import xdt.dto.GoodsRequest;
import xdt.model.PmsGoods;

import java.util.List;

public interface IPmsGoodsDao extends IBaseDao<PmsGoods> {
    /**
     * 获取传入列表的总价格
     * @param goodsRequests
     * @return
     */
    Integer getGoodsPriceSum(List<GoodsRequest> goodsRequests);

    /**
     * 获取商品信息
     * @param goodsIds
     * @return
     */
    List<PmsGoods> getPmsGoodsByIds(List<String> goodsIds);
}

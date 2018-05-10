package xdt.dao;

import xdt.model.PmsGoodsOrder;

import java.util.List;

public interface IPmsGoodsOrderDao extends IBaseDao<PmsGoodsOrder> {
    /**
     * 批量入库
     * @param pmsGoodsOrderList
     * @return
     */
      public int insertBatch(List<PmsGoodsOrder> pmsGoodsOrderList);
}

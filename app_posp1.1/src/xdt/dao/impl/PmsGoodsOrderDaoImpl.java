package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IPmsGoodsOrderDao;
import xdt.model.PmsGoodsOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PmsGoodsOrderDaoImpl extends BaseDaoImpl<PmsGoodsOrder> implements IPmsGoodsOrderDao {

    public static final String INSERTBATCH = "insertBatch";

    /**
     * 批量入库
     * @param pmsGoodsOrderList
     * @return
     */
    @Override
    public int insertBatch(List<PmsGoodsOrder> pmsGoodsOrderList) {
        String sql = this.getStatementId(INSERTBATCH);
        Map<String,List<PmsGoodsOrder>> paramMap = new HashMap<String, List<PmsGoodsOrder>>();
        paramMap.put("pmsGoodsOrderList",pmsGoodsOrderList);
        return sqlSession.insert(sql, paramMap);
    }
}

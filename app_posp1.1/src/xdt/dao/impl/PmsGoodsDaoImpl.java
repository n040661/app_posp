package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IPmsGoodsDao;
import xdt.dto.GoodsRequest;
import xdt.model.PmsGoods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PmsGoodsDaoImpl extends BaseDaoImpl<PmsGoods> implements IPmsGoodsDao {

    private static  final String GETGOODSPRICESUM = "getGoodsPriceSum";
    private static  final String GETGOODSBYIDS = "getGoodsByIds";
    /**
     * 获取传入参数所有商品的总价
     * @param goodsRequestList
     * @return
     */
    @Override
    public Integer getGoodsPriceSum(List<GoodsRequest> goodsRequestList){
        String sql = getStatementId(GETGOODSPRICESUM);
        Map<String,List<GoodsRequest>> paramMap = new HashMap<String, List<GoodsRequest>>();
        paramMap.put("goodsRequestList",goodsRequestList);
        return sqlSession.selectOne(sql,paramMap);
    }

    @Override
    public List<PmsGoods> getPmsGoodsByIds(List<String> goodsIds) {
        String sql = getStatementId(GETGOODSBYIDS);
        Map<String,List<String>> paramMap = new HashMap<String, List<String>>();
        paramMap.put("goodsIds",goodsIds);
        return sqlSession.selectList(sql,paramMap);
    }
}

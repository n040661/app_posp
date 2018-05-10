package xdt.dao.impl;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import xdt.dao.IPmsMerchantFeeDao;
import xdt.dao.IPospRouteInfoDAO;
import xdt.model.PmsMerchantFee;
import xdt.model.PospRouteInfo;

@Repository
public class PospRouteInfoDaoImpl extends BaseDaoImpl<PospRouteInfo> implements IPospRouteInfoDAO {
   
	
	private static final String GETBYMERCID = "getByMercId";
    
    private static final String QUERYMYALLROUTES = "queryMyAllRoutes";
    
    
    private static final String QUERYMYALLROUTESBYMER = "queryMyAllRoutesByMer";
    
    private static final String QUERYMYALLROUTESEXTRA = "queryMyAllRoutesExtra";
    
    private static final String QUERYMYALLSUBROUTES = "queryMyAllSubRoutes";
    
    /**
	 * 查询终端有效路由 根据商户ID
	 * @param postId
	 * @return
	 */
	public List queryMyAllRoutes(Long postId){
		String sql = getStatementId(QUERYMYALLROUTES);
		return sqlSession.selectList(sql, postId);
		
	}
	/**
	 * 根据上游商户号查询路由
	 * @param postId
	 * @return
	 */
	public List queryMyAllRoutesMer(Long postId){
		String sql = getStatementId("queryMyAllRoutesMer");
		return sqlSession.selectList(sql, postId);
		
	}
	
	/**
	 * 商户路由
	 * @param merNo
	 * @return
	 */
	public List<PospRouteInfo>  queryMyAllRoutesByMer(String  merNo){
		String sql = getStatementId(QUERYMYALLROUTESBYMER);
		return sqlSession.selectList(sql, merNo);
	}
	

	/**
	 * 商户路由(含机构商户单笔限额信息)
	 * @param merNo
	 * @return
	 */
	public List<PospRouteInfo> queryMyAllRoutesExtra(String  merNo){
		String sql = getStatementId(QUERYMYALLROUTESEXTRA);
		return sqlSession.selectList(sql, merNo);
	}
	
	/**
	 * 商户带统计信息 路由
	 * @param merNo
	 * @return
	 */
	public List<PospRouteInfo> queryMyAllSubRoutes(String  merNo){
		String sql = getStatementId(QUERYMYALLSUBROUTES);
		return sqlSession.selectList(sql, merNo);
		
	}
	
	
	/**
	 * 
	 */
	public PospRouteInfo getPospRouteInfo(Long id){
		
		String sql = this.getStatementId("selectByPrimaryKey");
        return sqlSession.selectOne(sql,id);
	}

    /**
     * 根据商户id获取通道费率
     * @param mercId
     * @return
     */
    public PmsMerchantFee getByMercId(String mercId) {
        String sql = this.getStatementId("selectByPrimaryKey");
        return sqlSession.selectOne(sql,mercId);
    }
    /**
     * 根据商户号查询通道信息
     * @param id
     * @return
     */
    public PospRouteInfo selectByPospRouteInfo(int ownerId) {
        String sql = this.getStatementId("selectByAll");
        return sqlSession.selectOne(sql,ownerId);
	}
    public int insertPospRouteInfo(PospRouteInfo info){
   	 String sql = this.getStatementId("insertPospRouteInfo");
   	 return sqlSession.insert(sql, info);
   }
}

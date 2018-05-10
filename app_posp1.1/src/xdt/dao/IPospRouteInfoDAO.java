package xdt.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import xdt.model.PospRouteInfo;
import xdt.model.PospTransInfo;

/**
 * 支付流水 User: Jeff Date: 15-5-22 Time: 下午2:23 To change this template use File |
 * Settings | File Templates.
 */
public interface IPospRouteInfoDAO extends IBaseDao<PospRouteInfo> {

	/**
	 * 查询终端有效路由
	 * @param postId
	 * @return
	 */
	public List queryMyAllRoutes(Long postId);
	
	/**
	 * 根据上游商户号查询路由
	 * @param postId
	 * @return
	 */
	public List queryMyAllRoutesMer(Long postId);
	/**
	 * 商户路由
	 * @param merNo
	 * @return
	 */
	public List queryMyAllRoutesByMer(String  merNo);
	

	/**
	 * 商户路由(含机构商户单笔限额信息)
	 * @param merNo
	 * @return
	 */
	public List queryMyAllRoutesExtra(String  merNo);
	
	/**
	 * 商户带统计信息 路由
	 * @param merNo
	 * @return
	 */
	public List queryMyAllSubRoutes(String  merNo);
	
	
	public PospRouteInfo getPospRouteInfo(Long id);
   
	/**
	 * 根据商户号查询商户信息
	 * @param id
	 * @return
	 */
	public PospRouteInfo selectByPospRouteInfo(int ownerId);
	
	/***
	 * 添加
	 */
	public int insertPospRouteInfo(PospRouteInfo info);

}

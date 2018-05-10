package xdt.dao.impl;
import java.util.List;

import xdt.dao.IViewKyChannelInfoDao;
import xdt.dao.impl.BaseDaoImpl;
import xdt.model.ViewKyChannelInfo;
import org.springframework.stereotype.Repository;

@Repository
public class ViewKyChannelInfoDaoImpl extends BaseDaoImpl<ViewKyChannelInfo> implements IViewKyChannelInfoDao {
	
	private final static String SELECTONE = "selectByBusinessnum";
	private final static String SELECTALLCHANNELINFO = "selectAllChannelInfo";
	
   /**
    * 检索通道信息
    */

	public ViewKyChannelInfo searchChannelInfo(String businessnum) throws Exception {
	   String sql = this.getStatementId(SELECTONE);
	   return sqlSession.selectOne(sql, businessnum);
	}
	
	/**
	 * 查询所有通道信息
	 */
	public List<ViewKyChannelInfo> selectAllChannelInfo() throws Exception {
	   String sql = this.getStatementId(SELECTALLCHANNELINFO);
	   return sqlSession.selectList(sql);
	}
}

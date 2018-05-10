package xdt.dao;

import java.util.List;

import xdt.dao.IBaseDao;
import xdt.model.ViewKyChannelInfo;

public interface IViewKyChannelInfoDao extends IBaseDao<ViewKyChannelInfo> {
	
	/**
	 * 检索通道信息
	 * @param businessnum
	 * @return
	 * @throws Exception
	 */
	public ViewKyChannelInfo searchChannelInfo(String businessnum) throws Exception;
	/**
	 * 查询所有通道信息
	 */
	public List<ViewKyChannelInfo> selectAllChannelInfo() throws Exception;

}

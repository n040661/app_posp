package xdt.dao;

import xdt.dao.IBaseDao;
import xdt.model.PmsMessage;

import java.util.List;

public interface IPmsMessageDao extends IBaseDao<PmsMessage>  {
	
	/**
	 * 短信通道信息更新
	 * @param pmsMessage
	 * @return
	 * @throws Exception
	 */
	public int smsChannelInfoUpdateByPmsMessage(PmsMessage pmsMessage) throws Exception;
	/**
	 * 查询列表 前20
	 * @param pmsMessage
	 * @return
	 * @throws Exception
	 */
	public List<PmsMessage> selectLists20(PmsMessage pmsMessage) throws Exception;

    /**
     *按照实体查询记录
     * @param pmsMessage
     * @return
     * @throws Exception
     */
    public List<PmsMessage> selectList(PmsMessage pmsMessage) throws Exception;
}

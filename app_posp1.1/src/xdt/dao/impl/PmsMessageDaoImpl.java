package xdt.dao.impl;
import org.springframework.stereotype.Repository;
import xdt.dao.IPmsMessageDao;
import xdt.model.PmsMessage;

import java.util.List;

@Repository
public class PmsMessageDaoImpl extends BaseDaoImpl<PmsMessage> implements IPmsMessageDao {
	
	//根据短信通道实体更新短信通道信息
	private static final String UPDATE = "updateByPmsMessage";
	private static final String SELECTLIST20 = "selectLists20";
	private static final String SELECTLIST = "selectList";

	/**
	 * 短信通道信息更新
	 */
	public int smsChannelInfoUpdateByPmsMessage(PmsMessage pmsMessage) throws Exception {
		return sqlSession.update(getStatementId(UPDATE), pmsMessage);
	}
	/**
	 * 查找前20条
	 */
	public List<PmsMessage> selectLists20(PmsMessage pmsMessage) throws Exception {
		return sqlSession.selectList(getStatementId(SELECTLIST20), pmsMessage);
	}
	/**
	 * 按照实体查找记录
	 */
	public List<PmsMessage> selectList(PmsMessage pmsMessage) throws Exception {
		return sqlSession.selectList(getStatementId(SELECTLIST), pmsMessage);
	}
}

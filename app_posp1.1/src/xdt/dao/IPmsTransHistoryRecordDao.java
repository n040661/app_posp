package xdt.dao;

import xdt.dao.IBaseDao;
import xdt.model.PmsTransHistoryRecord;

import java.util.HashMap;
import java.util.List;

public interface IPmsTransHistoryRecordDao extends IBaseDao<PmsTransHistoryRecord> {
	
	/**
	 * 检索交易历史记录信息
	 * @param hashMap
	 * @return
	 * @throws Exception
	 */
	public PmsTransHistoryRecord selectCardInfo(HashMap<String, String> hashMap) throws Exception;
	
	/**
	 * 检索交易历史记录列表
	 * @param hashMap
	 * @return
	 * @throws Exception
	 */
	public List<PmsTransHistoryRecord> searchHistoryRecord(HashMap<String, String> hashMap) throws Exception;
}

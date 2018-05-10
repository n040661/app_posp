package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface IPmsTransHistoryRecordService {
	
	/**
	 * 检索交易历史记录列表
	 * @param pageInfo
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public String searchTransHistoryRecordList(String pageInfo, HttpSession session) throws Exception;
	
	/**
	 * 检索交易历史记录列表异常
	 * @return
	 * @throws Exception
	 */
	public String searchTransHistoryRecordListException(HttpSession session) throws Exception;
	
	/**
	 * 检索交易可用次数
	 * @param bankCardInfo
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public String searchTransGetNumberOfAvailable(String bankCardInfo, HttpSession session, HttpServletRequest request) throws Exception;
	
    /**
     * 检索交易可用次数异常
     * @return
     * @throws Exception
     */
	public String searchTransGetNumberOfAvailableException(HttpSession session) throws Exception;

}

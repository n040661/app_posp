package xdt.dao;

import xdt.model.NewsInfo;
import xdt.util.PageView;

import java.util.List;

public interface INewsInfoDao extends IBaseDao<NewsInfo>{

    /**
     * 根据欧单编号查询
     * @param pageView
     * @return
     * @throws Exception
     */
    public PageView selectPageByOagentNo(PageView pageView);

    /**
     * 获取当前欧单下的消息总数
     * @param oagentNo
     * @return
     */
    public int countAllByOagentNo(String oagentNo);

    /**
     * 获取所有id列表
     * @param oAgentNo
     * @return
     */
    public List<String> selectAllIdsByOagentNo(String oAgentNo);

    /**
     * 获取当前欧单的登录提示信息
     * @param oAgentNo
     * @return
     */
    public NewsInfo selectLoginRemind(String oAgentNo);

}

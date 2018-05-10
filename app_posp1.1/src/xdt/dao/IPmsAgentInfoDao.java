package xdt.dao;

import xdt.model.PmsAgentInfo;

/**
 * 代理商DAO
 * User: Jeff
 * Date: 15-6-29
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
public interface IPmsAgentInfoDao  extends
        IBaseDao<PmsAgentInfo> {

    PmsAgentInfo selectByAgentNum(String agentNum);
    PmsAgentInfo selectOagentByMercNum(String mercNum);

    public PmsAgentInfo getOAgent(PmsAgentInfo p);

    /**
     *
     * @param oagentNo
     * @return
     */
    public PmsAgentInfo getOAgentByOagentNo(String oagentNo);

}

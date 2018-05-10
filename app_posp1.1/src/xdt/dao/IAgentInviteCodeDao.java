package xdt.dao;


import xdt.model.AgentInviteCode;
import xdt.model.AgentInviteCodePrimaryKey;

public interface IAgentInviteCodeDao extends IBaseDao<AgentInviteCode> {

    /**
     * 根据主键获取对象
     * @param agentInviteCodePrimaryKey
     * @return
     */
    AgentInviteCode getByPrimaryKeys(AgentInviteCodePrimaryKey agentInviteCodePrimaryKey);

}

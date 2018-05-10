package xdt.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import xdt.dao.IAgentInviteCodeDao;
import xdt.model.AgentInviteCode;
import xdt.model.AgentInviteCodePrimaryKey;

/**
 * 代理商邀请码dao
 * User: Jeff
 * Date: 16-1-20
 * Time: 上午10:23
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class AgentInviteCodeDaoImpl extends BaseDaoImpl<AgentInviteCode> implements IAgentInviteCodeDao {
    //查找一条记录
    private Logger logger = Logger.getLogger(AgentInviteCodeDaoImpl.class);
    public static final String  getByPrimaryKeys = "selectByPrimaryKey2";

    @Override
    public AgentInviteCode getByPrimaryKeys(AgentInviteCodePrimaryKey agentInviteCodePrimaryKey) {
        return sqlSession.selectOne(getByPrimaryKeys, agentInviteCodePrimaryKey);
    }
}

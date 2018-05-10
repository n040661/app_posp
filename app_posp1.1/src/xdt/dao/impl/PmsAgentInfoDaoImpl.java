package xdt.dao.impl;

import org.springframework.stereotype.Repository;
import xdt.dao.IPmsAgentInfoDao;
import xdt.model.PmsAgentInfo;

/**
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-6-29
 * Time: 下午2:32
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class PmsAgentInfoDaoImpl extends BaseDaoImpl<PmsAgentInfo>  implements IPmsAgentInfoDao  {
    public static final String SELECTBYAGENTNUM="selectByAgentNum";
    public static final String SELECTOAGENTBYMERCNUM="selectOagentByMercNum";
    public static final String GETOAGENT="getOAgent";
    public static final String GETOAGENTBYOAGENT="getOAgentByOagentNo";

    /**
     * 根据代理商编号查询代理商
     * @param agentNum
     * @return
     */
    @Override
    public PmsAgentInfo selectByAgentNum(String agentNum) {
        String sql = this.getStatementId(SELECTBYAGENTNUM);
        return sqlSession.selectOne(sql,agentNum);
    }

    @Override
    public PmsAgentInfo selectOagentByMercNum(String mercNum) {
        String sql = this.getStatementId(SELECTOAGENTBYMERCNUM);
        return sqlSession.selectOne(sql,mercNum);
    }

    @Override
	public PmsAgentInfo getOAgent(PmsAgentInfo p) {
		String sql = this.getStatementId(GETOAGENT);
		return sqlSession.selectOne(sql, p);
	}

    /**
     * 根据欧单编号查询欧单代理的信息
     * @param oagentNo
     * @return
     */
    @Override
    public PmsAgentInfo getOAgentByOagentNo(String oagentNo) {
        String sql = this.getStatementId(GETOAGENTBYOAGENT);
        return sqlSession.selectOne(sql, oagentNo);
    }
}

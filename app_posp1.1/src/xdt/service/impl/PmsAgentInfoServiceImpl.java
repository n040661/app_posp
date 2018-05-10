package xdt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xdt.dao.IPmsAgentInfoDao;
import xdt.model.PmsAgentInfo;
import xdt.service.IPmsAgentInfoService;

@Service
public class PmsAgentInfoServiceImpl extends BaseServiceImpl implements
		IPmsAgentInfoService {

	@Autowired
	private IPmsAgentInfoDao pmsagentinfoDao;

	/**
	 * 条件查询代理商 return List
	 * @throws Exception 
	 */
	public List<PmsAgentInfo> selectList(PmsAgentInfo obj) throws Exception {
		return this.pmsagentinfoDao.searchList(obj);
	}

	@Override
	public PmsAgentInfo selectByAgentNum(String agentNum) throws Exception {
		return this.pmsagentinfoDao.selectByAgentNum(agentNum);
	}

	//查询代理所属总代
	public PmsAgentInfo getOAgent(PmsAgentInfo p) throws Exception {
		return this.pmsagentinfoDao.getOAgent(p);
	}
}
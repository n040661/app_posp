package xdt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import xdt.model.PmsAgentInfo;
import xdt.util.PageView;

@Service
public interface IPmsAgentInfoService
{

  public List<PmsAgentInfo> selectList(PmsAgentInfo paramPmsAgentInfo) throws Exception;
  
  public PmsAgentInfo selectByAgentNum(String agentNum) throws Exception;
  
  public PmsAgentInfo getOAgent(PmsAgentInfo p) throws Exception;
  
}

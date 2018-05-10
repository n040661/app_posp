package xdt.service;

import java.util.Map;

import xdt.dto.lhzf.LhzfRequset;
import xdt.dto.lhzf.LhzfResponse;

public interface ILhzfService {

	Map<String, String> quickAgentPayH5(LhzfRequset lhzfRequset,Map<String, String> result);
	
	void update(LhzfResponse hfbResponse) throws Exception;
}

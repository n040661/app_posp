package xdt.service;

import java.util.Map;

import xdt.model.Jq;

public interface IJqService {

	Map<String, String> select(Jq jq);
	
	/**
	 * 长沙松顺数据认证接口
	 * 
	 * @param originalinfo
	 *            下游请求原始数据
	 * @return
	 * @throws Exception
	 */
	Map<String, String> selectJq(Jq jq) throws Exception;
}

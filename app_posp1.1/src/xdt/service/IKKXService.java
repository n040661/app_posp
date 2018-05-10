package xdt.service;

import java.util.Map;

import xdt.dto.kkx.KKXRequest;

public interface IKKXService {

	
	Map<String, String> pay(KKXRequest kkxRequest,Map<String, String> result);
}

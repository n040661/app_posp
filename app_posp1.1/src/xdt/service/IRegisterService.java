package xdt.service;

import org.slf4j.Logger;

import xdt.dto.nbs.register.Register;
import xdt.dto.nbs.register.RegisterResponse;
import xdt.model.ChannleMerchantConfigKey;

public interface IRegisterService {

	
	RegisterResponse inster(Register register) throws Exception;

	void insertPmsBusinessPos(String string, Register register, Logger log) throws Exception;

	void insertPmsBusinessInfo(String string, Register register, Logger log) throws Exception;
	
	void inserts(String string,Register register, Logger log) throws Exception;
	
	void insertPospRouteInfo(String string,Register register, Logger log) throws Exception;
	
	String select(Register register,Logger log) throws Exception;

	String update(Register register, Logger log) throws Exception;

	String merchantDownload(Register register, Logger log) throws Exception;
	
	String selectSettlementStatus(Register register, Logger log) throws Exception;
	
	/**
	 * 查询商户密钥信息
	 * 
	 * @param merchantId
	 *            商户号
	 * @return
	 * @throws Exception
	 */
	ChannleMerchantConfigKey getChannelConfigKey(String merchantId) throws Exception;
}

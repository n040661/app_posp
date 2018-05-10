package xdt.service;

public interface TAccRateService {
	/**
	 * 费率查询
	 * @return
	 * @throws Exception
	 */
	public String queryTaccRate()throws Exception;
	
	/**
	 * 费率查询异常
	 * @return
	 * @throws Exception
	 */
	public String queryTaccRateException()throws Exception;
}

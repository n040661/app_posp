package xdt.service;

public interface IPmsDictionaryService {

	/**
	 * 查询证件类型
	 * 
	 * @return
	 * @throws Exception
	 */
	public String cardTypeQuery() throws Exception;

	/**
	 * 查询证件类型异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String cardTypeQueryException() throws Exception;

}
package xdt.service;

import javax.servlet.http.HttpSession;

/**
 * @author lev12
 *
 */
public interface IPmsGoodsService {

	
	/**
	 * 商品列表查看
	 * @param param 
	 * 
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public String queryGoodsList(String param, HttpSession session) throws Exception;

	/**
	 * 商品列表查看异常
	 * 
	 * @return
	 * @throws Exception
	 */
	public String queryGoodsListException() throws Exception;
	
}

package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 交易类型
 * User: Jeff
 * Date: 15-5-26
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
public interface ITradeTypeService {
    /**
     * 交易类型列表
     * @param request
     * @param session
     * @return
     */
    public String tradeTypeList(HttpServletRequest request, HttpSession session) throws Exception;

}

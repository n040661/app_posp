package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 转账汇款
 * User: Jeff
 * Date: 15-5-19
 * Time: 下午7:41
 * To change this template use File | Settings | File Templates.
 */
public interface IRemitService {
    /**
     * 生产订单
     * @param payPhoneAccountInfo
     * @param session
     * @param request
     * @return
     */
    public String producedOrder(String payPhoneAccountInfo, HttpSession session, HttpServletRequest request);
}

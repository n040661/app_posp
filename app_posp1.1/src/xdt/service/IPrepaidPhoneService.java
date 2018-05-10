package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 话费充值服务类
 * Created with IntelliJ IDEA.
 * User: Jeff
 * Date: 15-5-8
 * Time: 下午2:49
 * To change this template use File | Settings | File Templates.
 */
public interface IPrepaidPhoneService {
    /**
     * 号段查询
     * @param phoneNumber
     * @param session
     * @return
     * @throws Exception
     */
    public String themRoughlyQuery(String phoneNumber,HttpSession session,HttpServletRequest request)throws Exception;

    /**
     * 号段查询异常
     * @return
     * @throws Exception
     */
    public String themRoughlyQueryException(HttpSession session)throws Exception;

    /**
     * 手机充值金额查询
     * @param phoneInfo
     * @return
     * @throws Exception
     */
    public String phoneMoneyQuery(String phoneInfo,HttpSession session)throws Exception;


    /**
     * 手机充值金额查询异常
     * @return
     * @throws Exception
     */
    public String phoneMoneyQueryException(HttpSession session)throws Exception;

    /**
     * 生成订单
     * @param phoneInfo
     * @param session
     * @return
     */
    public String producedOrder(String phoneInfo,HttpSession session)throws Exception;
}
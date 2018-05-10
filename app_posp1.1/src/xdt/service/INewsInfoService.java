package xdt.service;

import xdt.model.NewsInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 消息服务层
 * User: Jeff
 * Date: 15-12-11
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
public interface INewsInfoService {


    /**
     * 根据欧单编号查询
     * @param requestStr
     * @return
     * @throws Exception
     */
    public String  selectPageByOagentNo(String requestStr,HttpSession session,HttpServletRequest request ) throws  Exception;

    /**
     * 查询消息详情
     * @param requestStr
     * @param session
     * @param request
     * @return
     * @throws Exception
     */
    public String selectDetailByNewsId(String requestStr,HttpSession session,HttpServletRequest request ) throws  Exception;

    /**
     *判断当前用户是否有未读的消息
     * @param mercId
     * @return
     * @throws Exception
     */
    public String haveUnReadMsg(String mercId,String oAgentNo) throws  Exception;

    /**
     * 获取当前欧单的登录提示信息
     * @param oAgentNo
     * @return
     */
    public NewsInfo loginRemind(String oAgentNo);
}

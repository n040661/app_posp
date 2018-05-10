package xdt.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 分享服务层
 * User: Jeff
 * Date: 15-12-11
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
public interface IShareInfoService {

    String getByOagentNo(String phoneNumber,HttpSession session,HttpServletRequest request) throws Exception;
}

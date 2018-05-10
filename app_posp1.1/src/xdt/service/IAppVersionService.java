package xdt.service;

import javax.servlet.http.HttpSession;

/**
 * App版本服务层
 * User: Jeff
 * Date: 15-5-26
 * Time: 下午1:46
 * To change this template use File | Settings | File Templates.
 */
public interface IAppVersionService {

    /**
     * 获取最新的版本信息
     * @param requestData
     * @param session
     * @return
     */
    public String newestVersion(String requestData,HttpSession session) throws Exception;

}

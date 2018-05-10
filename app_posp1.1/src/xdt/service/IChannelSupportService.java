package xdt.service;

import javax.servlet.http.HttpSession;

/**
 * 快捷支付服务层
 * User: Jeff
 * Date: 16-3-9
 * Time: 下午2:29
 * To change this template use File | Settings | File Templates.
 */
public interface IChannelSupportService {
    /**
     * 获取通道支持的银行
     * @param session
     * @param request
     * @return
     */
    String  channelSupportBank(HttpSession session, String request);
    /**
     * 根据通道和卡号检索银行信息,如果不支持返回错误信息
     * @param session
     * @param request
     * @return
     */
    String  checkSupportBankInfoByChannelCard(HttpSession session, String request);


}

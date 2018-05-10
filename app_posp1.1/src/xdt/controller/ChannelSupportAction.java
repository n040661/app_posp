package xdt.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import xdt.service.IChannelSupportService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 通道支持业务
 * User: Jeff
 * Date: 16-3-9
 * Time: 下午2:27
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("channelSupport")
public class ChannelSupportAction extends BaseAction  {

    private Logger logger = Logger.getLogger(ChannelSupportAction.class);
    @Resource
    IChannelSupportService channelSupportService;
    /**
     * 查看渠道支持的银行卡列表
     * @param response
     * @param session
     */
    @RequestMapping("/supportBankList")
    public void checkLocalCardRecord(HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            outPrint(response, channelSupportService.channelSupportBank(session,param));
        } catch (Exception e) {
            setSession(session,request.getRemoteAddr(),true);
            logger.info("[app_exception]"+e.fillInStackTrace());
            e.printStackTrace();
        }
    }
    /**
     * 查询通道是否支持当前的银行卡
     * @param response
     * @param session
     */
    @RequestMapping("/checkSupportBankInfoByChannelCard")
    public void checkSupportBankInfoByChannelCard(HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            outPrint(response, channelSupportService.checkSupportBankInfoByChannelCard(session,param));
        } catch (Exception e) {
            setSession(session,request.getRemoteAddr(),true);
            logger.info("[app_exception]"+e.fillInStackTrace());
            e.printStackTrace();
        }
    }

}

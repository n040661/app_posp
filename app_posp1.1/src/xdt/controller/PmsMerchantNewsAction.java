package xdt.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import xdt.service.INewsInfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 分享
 * User: Jeff
 * Date: 15-12-11
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("pmsMerchantNewsAction")
public class PmsMerchantNewsAction extends BaseAction {

    @Resource
    private INewsInfoService newsInfoService;//手机充值服务层
    private Logger logger = Logger.getLogger(MobilePhoneRechargeAction.class);

    /**
     * 消息列表
     * @param requestParam
     * @param response
     * @param session
     */
    @RequestMapping("/queryNewsList")
    public void queryShare(String requestParam,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            String jsonString = newsInfoService.selectPageByOagentNo(param,session,request);
            outPrint(response, jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 消息详情
     * @param requestParam
     * @param response
     * @param session
     */
    @RequestMapping("/queryNewsDetail")
    public void queryNewsDetail(String requestParam,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            String jsonString = newsInfoService.selectDetailByNewsId(param,session,request);
            outPrint(response, jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package xdt.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import xdt.service.IShareInfoService;

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
@RequestMapping("merchantShareAction")
public class MerchantShareAction extends BaseAction {

    @Resource
    private IShareInfoService shareInfoService;//手机充值服务层
    private Logger logger = Logger.getLogger(MobilePhoneRechargeAction.class);

    /**
     * 号段查询
     * @param requestParam
     * @param response
     * @param session
     */
    @RequestMapping("/queryShare")
    public void queryShare(String requestParam,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        String param = requestClient(request);
        try {
            String jsonString = shareInfoService.getByOagentNo(param,session,request);
            outPrint(response, jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

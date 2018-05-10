package xdt.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xdt.service.IRemitService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 转账汇款
 * User: Jeff
 * Date: 15-5-19
 * Time: 下午7:16
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("remitAction")
public class RemitAction extends BaseAction {

    private Logger logger = Logger.getLogger(RemitAction.class);
    @Resource
    IRemitService remitService;
    /**
     * 生成订单
     * @param payPhoneAccountInfo
     * @param response
     * @param session
     * @param request
     */
    @RequestMapping("/producedOrder")
    public void producedOrder(String payPhoneAccountInfo, HttpServletResponse response, HttpSession session, HttpServletRequest request) {
        String param = requestClient(request);
        try {
            String jsonString = remitService.producedOrder(param, session, request);
            outPrint(response, jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

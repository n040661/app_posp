package xdt.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import xdt.service.ITradeTypeService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 交易类型
 * User: Jeff
 * Date: 15-5-26
 * Time: 上午10:43
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("tradeTypeAction")
public class TradeTypeAction extends  BaseAction {

    private Logger logger = Logger.getLogger(TradeTypeAction.class);

    @Resource
    ITradeTypeService tradeTypeService;
    /**
     * 交易类型列表
     * @param request
     * @param session
     * @param response
     * @return
     */
    @RequestMapping("tradeTypeList")
    public void tradeTypeList(HttpServletRequest request, HttpSession session, HttpServletResponse response){
        try {
            String jsonString = tradeTypeService.tradeTypeList(request,session);
            outPrint(response, jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

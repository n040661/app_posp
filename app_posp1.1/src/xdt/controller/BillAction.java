package xdt.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xdt.service.IBillService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 账单操作
 * User: Jeff
 * Date: 15-5-22
 * Time: 下午1:53
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("billAction")
public class BillAction extends BaseAction{

    @Resource
    IBillService  billService;
    private Logger logger = Logger.getLogger(MerchantCollectMoneyAction.class);
    @RequestMapping("/billList")
    public void billList( String billListRequestData, HttpServletResponse response, HttpSession session, HttpServletRequest request) {
        try {
            String param = requestClient(request);
            String jsonString = billService.billList(param,session);
            outPrint(response, jsonString);
        }catch (IOException e){
        }
    }

    @RequestMapping("/billDetail")
    public void billDetail(String billListRequestData, HttpServletResponse response, HttpSession session, HttpServletRequest request){
        try {
            String param = requestClient(request);
            String jsonString = billService.billDetail(param,session);
            outPrint(response, jsonString);
        }catch (IOException e){
        }
    }
    @RequestMapping("/billArriveList")
    public void billArriveList( String billListRequestData, HttpServletResponse response, HttpSession session, HttpServletRequest request){
        try {
            String param = requestClient(request);
            String jsonString = billService.billArriveList(param,session);
            outPrint(response, jsonString);
        }catch (IOException e){
        }
    }
}

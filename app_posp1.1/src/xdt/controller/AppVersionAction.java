package xdt.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import xdt.service.IAppVersionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * app版本管理
 * User: Jeff
 * Date: 15-5-26
 * Time: 下午2:44
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("appVersionAction")
public class AppVersionAction  extends BaseAction  {

    private Logger logger = Logger.getLogger(AppVersionAction.class);

    @Resource
    IAppVersionService appVersionService;

    @RequestMapping("newestAppVersion")
    public void newestAppVersion(String paramData, HttpServletResponse response, HttpSession session, HttpServletRequest request){
        try {
            String param = requestClient(request);
            String jsonString = appVersionService.newestVersion(param,session);
            outPrint(response, jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

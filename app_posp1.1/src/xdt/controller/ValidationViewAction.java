package xdt.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import xdt.model.PmsMessage;
import xdt.service.IPmsMessageService;
import xdt.service.impl.BaseServiceImpl;
import xdt.servlet.AppPospContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 验证码列表
 * Jeff
 */
@Controller
@RequestMapping("validationAction")
public class ValidationViewAction{
	@Resource
	private IPmsMessageService pmsMessageService;
    @Resource
    AppPospContext appPospContext;
    private Logger logger = Logger.getLogger(ValidationViewAction.class);

    @RequestMapping("/view")
	public ModelAndView view(String param,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        List<PmsMessage> msgList = null;
        try {
             msgList = pmsMessageService.selectList(param);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return new ModelAndView("validateCode","msgList",msgList);
    }

    @RequestMapping("/reloadChannelInfo")
	public ModelAndView reloadChannelInfo(String param,HttpServletResponse response,HttpSession session,HttpServletRequest request){
        List<PmsMessage> msgList = null;
        try {
            String   channelNO = AppPospContext.context.get(BaseServiceImpl.MOBAOCHANNELNUM+BaseServiceImpl.MOBAOPAY).getChannelNO();
            logger.info("刷新前："+channelNO);
            appPospContext.afterPropertiesSet();
            channelNO = AppPospContext.context.get(BaseServiceImpl.MOBAOCHANNELNUM+BaseServiceImpl.MOBAOPAY).getChannelNO();
            logger.info("刷新后："+channelNO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("validateCode","msgList",msgList);
    }


}

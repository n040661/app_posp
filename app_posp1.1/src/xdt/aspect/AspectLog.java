package xdt.aspect;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import xdt.model.SessionInfo;

/**
 * spring aop 日志记录
 * User: Jeff
 * Date: 15-5-19
 * Time: 上午9:53
 * To change this template use File | Settings | File Templates.
 */

@Aspect
@Component
public class AspectLog {

    private Logger logger = Logger.getLogger(AspectLog.class);
    @After(value = "execution(* org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter.handle(..))")
    public void aroundLog() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = (((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()).getSession();
        SessionInfo sessionInfo = (SessionInfo) session.getAttribute(SessionInfo.SESSIONINFO);
        String content="";
        if (sessionInfo != null) {
            Enumeration<String> enumeration = request.getParameterNames();
            while(enumeration.hasMoreElements()){
                String key = enumeration.nextElement();
                String value = request.getParameter(key);
                content+=key+"="+value+"&";
            }
            logger.info("用户：" + sessionInfo.getMobilephone() + "  请求连接:" + request.getRequestURI() +"  参数："+content);
        }
    }
}



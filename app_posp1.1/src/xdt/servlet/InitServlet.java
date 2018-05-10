package xdt.servlet;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import xdt.schedule.OrderBatchUpdateInitThread;

import javax.annotation.Resource;

/**
 * 自启动servlet初始化自个线程信息.
 * User: Jeff
 * Date: 15-5-13
 * Time: 下午2:05
 * To change this template use File | Settings | File Templates.
 */
@Component
public class InitServlet implements InitializingBean {

    @Resource
    OrderBatchUpdateInitThread orderBatchUpdateInitThread ;

    @Override
    public void afterPropertiesSet() throws Exception {
        //在这里启动线程
//       (new Thread(orderBatchUpdateInitThread)).start();
    }
}

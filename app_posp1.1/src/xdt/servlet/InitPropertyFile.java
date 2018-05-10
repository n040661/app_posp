package xdt.servlet;

import org.apache.log4j.Logger;

import xdt.dao.impl.PmsMerchantInfoDaoImpl;
import xdt.service.impl.BaseServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 重载配置文件
 * User: Jeff
 * Date: 16-3-8
 * Time: 下午6:19
 * To change this template use File | Settings | File Templates.
 */
public class InitPropertyFile extends HttpServlet {
    private Logger logger=Logger.getLogger(InitPropertyFile.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //读取配置文件信息
        logger.info(BaseServiceImpl.POSPBILLSEARCHURL);
        BaseServiceImpl.readCommonPropertiesFile();
        logger.info(BaseServiceImpl.POSPBILLSEARCHURL);
        logger.info("配置文件重载完成");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
}

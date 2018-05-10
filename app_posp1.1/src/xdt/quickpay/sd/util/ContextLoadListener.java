/**
 * Copyright : http://www.sandpay.com.cn , 2011-2014
 * Project : sandpay-cashier-webgateway
 * $Id$
 * $Revision$
 * Last Changed by pxl at 2016-12-28 上午10:49:46
 * $URL$
 * 
 * Change Log
 * Author      Change Date    Comments
 *-------------------------------------------------------------
 * pxl         2016-12-28        Initailized
 */
package xdt.quickpay.sd.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.sandpay.cashier.sdk.util.CertUtil;

/**
 * @author pan.xl
 *
 */
public class ContextLoadListener implements ServletContextListener {
	
	private static final Logger logger = LoggerFactory.getLogger(ContextLoadListener.class);

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		String publicKeyPath = "classpath:sand.cer";
		String privateKeyPath = "classpath:14270526.pfx";
		String keyPassword = "5TPM7C3CVM";

		logger.info("加载证书...");
		// 加载证书
		try {
			CertUtil.init(publicKeyPath, privateKeyPath, keyPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}

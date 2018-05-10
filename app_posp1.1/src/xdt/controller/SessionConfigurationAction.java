package xdt.controller;

import xdt.service.impl.BaseServiceImpl;
import org.apache.log4j.MDC;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Session配置
 * @author xiaomei
 *
 */
public class SessionConfigurationAction extends HttpServlet {
	
	/**
	 * 序列
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 启动服务时运行
	 */
	public void init(){
		try {
			//配置日期文件
			sessionConfigurationAction();
			//读取配置文件信息
			BaseServiceImpl.readCommonPropertiesFile();
			//加载手机产品信息
			/*java.util.Timer timer = new java.util.Timer(false);  
	        java.util.TimerTask task = new java.util.TimerTask(){  
	            @Override  
	            public void run() {
                	try {
						productQuery();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }             
	        };  
	        long delay = 30000;  
	        timer.schedule(task, delay);*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 配置项目启动时的日志session
	 * @throws java.net.UnknownHostException
	 */
	public static void sessionConfigurationAction() throws UnknownHostException{
		InetAddress addr = InetAddress.getLocalHost();
		MDC.put("ip", addr.getHostAddress());
		MDC.put("session","session");
		MDC.put("mobilePhone", "mobilePhone");
	}

	/**
	 * doGet
	 * @param request
	 * @param response
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);//重点啊
    }

	/**
	 * doPost
	 * @param request
	 * @param response
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  
        response.setContentType("text/html;charset=UTF-8");  
        PrintWriter out = response.getWriter();  
        out.println("<html>");  
        out.println("<head>");  
        out.println("<title>hello servlet</title>");  
        out.println("</head>");  
        out.println("<body>");  
        out.println("<h1>Hello I'm here!</h1>");  
        out.println("</body");  
        out.println("</html>");  
        out.close();  
    }
}
package xdt.baidu;


import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.log4j.Logger;



/**
 * Http请求发送       百度
 */
public class BDHttpClient {
	
	
	private static Logger logger = Logger.getLogger(BDHttpClient.class);
	
	
    /** 连接超时时间，由bean factory设置，缺省为8秒钟 */
    private int                        defaultConnectionTimeout            = 8000;

    /** 回应超时时间, 由bean factory设置，缺省为30秒钟 */
    private int                        defaultSoTimeout                    = 30000;

    /** 闲置连接超时时间, 由bean factory设置，缺省为60秒钟 */
    private int                        defaultIdleConnTimeout              = 60000;

    private int                        defaultMaxConnPerHost               = 30;

    private int                        defaultMaxTotalConn                 = 80;

    /** 默认等待HttpConnectionManager返回连接超时（只有在达到最大连接数时起作用）：1秒*/
    private static final long          defaultHttpConnectionManagerTimeout = 3 * 1000;

    /**
     * HTTP连接管理器，该连接管理器必须是线程安全的.
     */
    private HttpConnectionManager      connectionManager;

    private static BDHttpClient bDHttpClient   = new BDHttpClient();

    /**
     * 工厂方法
     * 
     * @return
     */
    public static BDHttpClient getInstance() {
        return bDHttpClient;
    }
    
	/**
     * 私有的构造方法
     */
    public BDHttpClient() {
        // 创建一个线程安全的HTTP连接池
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
        connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);

        IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        ict.addConnectionManager(connectionManager);
        ict.setConnectionTimeout(defaultIdleConnTimeout);

        ict.start();
    }
	
	
    /**
    * 执行Http请求
    * @param 给提交参数数值赋值   array
    * @param 请求的http地址   httpStr
    * @return   responseStr   百度返回
    */
   public String execute(String[] array,String[] array1,String httpStr,String oAgentNo) throws Exception{
	   
	   String result = "" ; 
	   
	   HttpClient httpClient =  new HttpClient(connectionManager);   

       // 设置连接超时
       httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(defaultConnectionTimeout);

       httpClient.getHttpConnectionManager().getParams().setSoTimeout(defaultSoTimeout);

       // 设置等待ConnectionManager释放connection的时间
       httpClient.getParams().setConnectionManagerTimeout(defaultHttpConnectionManagerTimeout);
       
	   String getURL = new BDUtil<Object>().create_baifubao_pay_order_url(array, array1, httpStr,oAgentNo);
	   
	   logger.info("调用接口上送的URL："+getURL);
	   
	   httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "gbk");
	  
	    // 设置 Http 连接超时为5秒    
	    httpClient.getHttpConnectionManager().getParams().setConnectionTimeout( 5000 );   
	     /* 生成 GetMethod 对象并设置参数 */    
	    GetMethod getMethod =  new  GetMethod(getURL);   
	    // 设置 get 请求超时为 5 秒    
	    getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,  5000 );   
	     
	    // 设置请求重试处理，用的是默认的重试处理：请求三次    
	
	    getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new  DefaultHttpMethodRetryHandler());         
	    
	    // 6、 执行 HTTP GET 请求
	     try {    
	         int  statusCode = httpClient.executeMethod(getMethod); 
	         /*  判断访问的状态码 */    
	         if (statusCode != HttpStatus.SC_OK){    
	        	 logger.debug( "HttpStatus.SC_OK :" + getMethod.getStatusLine());   
	          }
	           
	        // HTTP响应头部信息，这里简单打印                 	         
	        // 读取 HTTP 响应内容，这里简单打印网页内容    
            result = getMethod.getResponseBodyAsString();
            logger.info( "getMethod.getResponseBodyAsString()返回的XML原文:" +result+"XML原文返回值"+"<br/>");


      }catch (HttpException e) {    
    	  logger.debug( "HttpException异常，请检查代码" ,e);
      }finally  {    
         /* 释放连接 */    
    	  getMethod.releaseConnection();    
      }
		return result;    
	   
   }
    
    
    
    
}

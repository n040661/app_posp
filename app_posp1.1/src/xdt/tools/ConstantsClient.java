package xdt.tools;


/**
 * 接口通讯的基础参数配置；
 * 这些参数正常情况不需要做调整
 */
public class ConstantsClient {
  //----接口调用的一些固定常量----
  //签名数据编码
  public static final String PAYECO_DATA_ENCODE = "UTF-8";

  //连接超时，10秒
  public static final int CONNECT_TIME_OUT = 10000;
  
  //响应超时时间，60秒
  public static final int RESPONSE_TIME_OUT = 60000;
  
  //接口版本
  public static final String COMM_INTF_VERSION = "2.0.0";
  
	
}

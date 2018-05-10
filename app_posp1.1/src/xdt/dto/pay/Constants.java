package xdt.dto.pay;

/**
 * 密钥常量
 *
 */
public class Constants {
	public final static int CONNECT_TIMEOUT = 60; // 设置连接超时时间，单位秒
	public final static int READ_TIMEOUT = 120; // 设置读取超时时间，单位秒
	public final static int WRITE_TIMEOUT = 60; // 设置写的超时时间，单位秒

	public static final String TEST_SERVER_URL = "http://dev-api.suixince.com";

	public static String getServerUrl() {
		return TEST_SERVER_URL;
	}
}

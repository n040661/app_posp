package xdt.quickpay.wzf;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.unicompayment.mer.access.util.PropertiesUtil;

/**
 * 2014-10-22
 * @author 赵永鑫
 *
 */
/**
 * 
 * @author guodong
 * @date 2016年12月9日
 *
 */
public class SignUtil {
	
	public static String encodingCharset = "utf-8";
	
	private static Log logger = LogFactory.getLog(SignUtil.class);
	
	private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	/**
	 * 模拟商户签名
	 * @date 2016年12月9日
	 * @param SmerNo 商户号
	 * @param map	参数MAP集合
	 * @param charSet 编码方式，非对称加密编码方式必须为UTF-8
	 * @param SignType 签名方式
	 * @param prvKey 私钥 如果签名方式为非对称加密一定要传入参数
	 * @return 签名串
	 */
	public static String testMerSign(String SmerNo,Map<String, String> map,String charSet,String SignType,String prvKey){
		//非对称加密方式 ，RSA,SM2 等
		if (!"MD5".equalsIgnoreCase(SignType)) {
			return merSignWithRsaOrSm2(getOriginalData(map, null),SignType,prvKey);
		}
		
		//MD5 等摘要方式
		if("".equalsIgnoreCase(charSet)||charSet==null){
			charSet="UTF-8";
		}
		if("".equalsIgnoreCase(SignType)||SignType==null){
			SignType="MD5";
		}
//		String key="" ;
//		Connection conn = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//		String localMd5String="";
//		try {
			
//			Class.forName(PropertiesUtil.getValue("datasource.connection.driverclass"));// 实例化oracle数据库驱动程序(建立中间件)
//			String url = PropertiesUtil.getValue("datasource.connection.url");// @localhost为服务器名，sjzwish为数据库实例名
//			String username = PropertiesUtil.getValue("datasource.connection.username");
//			String password = PropertiesUtil.getValue("datasource.connection.password");
//			conn = DriverManager.getConnection(url, username, password);// 连接数据库，a代表帐户,a代表密码
//			stmt = conn.createStatement();// 提交sql语句,创建一个Statement对象来将SQL语句发送到数据库
//			// 查询数据用executeQuery
//			if (StringUtils.isNotBlank(reqSysNo)) {//内部系统MD5
//				rs = stmt.executeQuery("SELECT SIGNPASSWORD as mersignpassword FROM T_PAY_REQSYSTEM_INFO WHERE STATUS=1 and SYSNO ='"+reqSysNo+"'");
//			}else{//外部系统MD5
//				rs = stmt.executeQuery("select mersignpassword from UNIPAYUSER.T_PAY_MER_ENTERPRISE WHERE MERNO ='"+SmerNo+"'");// 执行查询,(ruby)为表名
//			}
//			while (rs.next()) {// 使当前记录指针定位到记录集的第一条记录
//				key=rs.getString("mersignpassword");
//				System.out.println("商户"+SmerNo+"--查询的key"+key);
//				
//			}
			
//			if(key==null||"".equals(key)){
//				return "";
//			}else{
//				 localMd5String = getHexSign(map, charSet, SignType, key);
//			}
		String localMd5String = getHexSign(map, charSet, SignType, prvKey);
			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				// 关闭数据库，结束进程
//				rs.close();
//				stmt.close();
//				conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		
		
		return localMd5String;
		
	}
	
	/**
	 * 模拟商户用非对称加密方式签名
	 * @author guodong
	 * @date 2016年12月9日
	 * @param originalData 原文
	 * @param signType	签名方式，签名算法
	 * @param prvKey 商户私钥
	 * @return
	 * 
	 */
	private static String merSignWithRsaOrSm2(String originalData, String signType, String prvKey) {
		String signMsg = "";
		 try {
//			 signMsg = SignUtils.simulateMerSign(originalData, signType, prvKey);
		} catch (Exception e) {
			logger.error("模拟商户用非对称加密方式签名-签名异常",e);
		}
		return signMsg;
	}
	/**
	 * AES算法加密数据
	 * @param data 待加密数据
	 * @param key  密钥
	 * @return 加密后的数据,若加密遇到异常时则返回<code>Encrypt Terminated</code>字符串
	 * */
	public static String testAESEncrypt(String SmerNo,String reqSysNo,String data,String charSet){

		if("".equalsIgnoreCase(charSet)||charSet==null){
			charSet="UTF-8";
		}
		String key="" ;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String AES="";
		try {

			Class.forName(PropertiesUtil.getValue("datasource.connection.driverclass"));// 实例化oracle数据库驱动程序(建立中间件)
			String url = PropertiesUtil.getValue("datasource.connection.url");// @localhost为服务器名，sjzwish为数据库实例名
			String username = PropertiesUtil.getValue("datasource.connection.username");
			String password = PropertiesUtil.getValue("datasource.connection.password");
			conn = DriverManager.getConnection(url, username, password);// 连接数据库，a代表帐户,a代表密码
			stmt = conn.createStatement();// 提交sql语句,创建一个Statement对象来将SQL语句发送到数据库
			// 查询数据用executeQuery
			if (StringUtils.isNotBlank(reqSysNo)) {//内部系统MD5
				rs = stmt.executeQuery("SELECT SIGNPASSWORD as mersignpassword FROM T_PAY_REQSYSTEM_INFO WHERE STATUS=1 and SYSNO ='"+reqSysNo+"'");
			}else{//外部系统MD5
				rs = stmt.executeQuery("select mersignpassword from UNIPAYUSER.T_PAY_MER_ENTERPRISE WHERE MERNO ='"+SmerNo+"'");// 执行查询,(ruby)为表名
			}
			while (rs.next()) {// 使当前记录指针定位到记录集的第一条记录
				key=rs.getString("mersignpassword");
				System.out.println("商户"+SmerNo+"--查询的key"+key);
				
			}// 1代表当前记录的第一个字段的值，可以写成字段名。
//			AES=SecurityUtil.genAESEncrypt(data, key);
			System.out.println("原数据"+data);
			AES=DesUtil.encrypt(data, key,charSet);
			System.out.println("加密数据后"+AES);
			String dAES=DesUtil.decrypt(AES, key,charSet);
			System.out.println("解密数据后"+dAES);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭数据库，结束进程
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		 //1代表当前记录的第一个字段的值，可以写成字段名。
//   AES=SecurityUtil.genAESEncrypt(data, key);
//	System.out.println("原数据"+data);
//	AES=DesUtil.encrypt(data, key,charSet);
//	System.out.println("加密数据后"+AES);
//	String dAES=DesUtil.decrypt(AES, key,charSet);
//	System.out.println("解密数据后"+dAES);
		return AES;
	}
	
	/**
	 * 对商户请求信息进行验签
	 * @param param
	 * @param charset
	 * @param algorithm
	 * @param signKey
	 * @return
	 */
	public static String getHexSign(Map<String, String> param, String charset, String algorithm, String signKey){
		if(charset==null||"".equals(charset)){
			charset="UTF-8";
		}
		if(algorithm==null||"".equals(algorithm)){
			algorithm="MD5";
		}
		
		String originalData = getOriginalData(param, signKey);
		String signMsg = getHexSign(originalData, charset, algorithm, true);
		logger.info("签名方式【"+algorithm+"】，字符集【"+charset+"】，摘要数据原串【 " + originalData.substring(0, originalData.length() - 9) + "*********" + "】 ");
		logger.info("生成的摘要signMsg:["+signMsg+"]");
		return signMsg;
	}
	
	/**
	 * 获取签名原文
	 * @author guodong
	 * @date 2016年12月4日
	 * @param param Map元数据集合
	 * @param signKey 有key（MD5）传值，无key（RSA）传null
	 * @return
	 * 
	 */
	public static String getOriginalData(Map<String, String> param,String signKey){
		StringBuilder sb = new StringBuilder();
		List<String> keys = new ArrayList<String>(param.keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = param.get(key);
			if (key.equalsIgnoreCase("cert") || key.equalsIgnoreCase("hmac")
					|| key.equalsIgnoreCase("signMsg") || value == null || value.length() == 0) {
				continue;
			}
			sb.append(key).append("=").append(value).append("|");
		}
		if (signKey != null ) {
			sb.append("key=").append(signKey);
		} else {
			sb.substring(0, sb.length()-1);
		}
		return sb.toString();
	}
	
	
	public static String getHexSign(String data, String charset, String algorithm, boolean toLowerCase){
	    //Used to build output as Hex
	    char[] DIGITS = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
	    //get byte[] from {@link TradePortalUtil#getBytes(String, String)}
	    byte[] dataBytes = getBytes(data, charset);
		byte[] algorithmData = null;
		try {
			//get an algorithm digest instance
			algorithmData = MessageDigest.getInstance(algorithm).digest(dataBytes);
		} catch (NoSuchAlgorithmException e) {
			logger.error("签名字符串[" + data + "]时发生异常:System doesn't support this algorithm[" + algorithm + "]");
			return "";
		}
		char[] respData = new char[algorithmData.length << 1];
		//two characters form the hex value
		for (int i = 0, j = 0; i < algorithmData.length; i++) {
			respData[j++] = DIGITS[(0xF0 & algorithmData[i]) >>> 4];
			respData[j++] = DIGITS[0x0F & algorithmData[i]];
		}
		return new String(respData);
	}
	
	public static byte[] getBytes(String data, String charset) {
		data = (data == null ? "" : data);
		if (isEmpty(charset)) {
			return data.getBytes();
		}
		try {
			return data.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			logger.error("将字符串[" + data + "]转为byte[]时发生异常:系统不支持该字符集[" + charset + "]");
			return data.getBytes();
		}
	}
	
	public static boolean isEmpty(String input) {
		return null == input || 0 == input.length() || 0 == input.replaceAll("\\s", "").length();
	}
	
	
	public static void main(String[] args) {
		/*Map<String, String> params = new HashMap<String, String>();
		params.put("interfaceVersion", "1.0.0.0");
		params.put("amount", "10");
		params.put("orderNo", "2011090100000001");
		params.put("orderDate", "2011090100000001");
		params.put("tranrst", "1");
		params.put("userNo", "20110901000000011234");
		params.put("respCode", "99999999");
		params.put("errDis", "123321123");
		params.put("signType", "MD5");
		
		System.out.println("网关签名数据："+SignUtil.perSign(params));
		System.out.println("验证充值签名："+SignUtil.perVerify("39417fdecf5fd7d647a0adfce8a9fa9c", params));*/
		StringBuffer tempBuffer = new StringBuffer();
		tempBuffer.append("name").append("=").append("jose").append(",");
		String str = tempBuffer.substring(0, tempBuffer.length() - 1);
		System.out.println(str);
		StringBuilder sb = new StringBuilder();
		sb.append("name=").append("josebruce,");
		sb.append("key=").append("AEVR2NO1VJNONRPHLA1DLFUCMLULQD1F");
		String md5Str = sb.toString();
		logger.info(" ************ MD5摘要数据原串【 " + md5Str.substring(0, md5Str.length() - 9) + "*********" + "】 ************ ");
		System.out.println(sb.toString());
		
	}
	
}

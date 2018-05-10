package xdt.pufa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;






import org.slf4j.LoggerFactory;

import xdt.common.security.XDTRSA;

/**
 * 浦发工具类
 * @Description 
 * @author Shiwen .Li
 * @date 2017年3月26日 下午5:00:55 
 * @version V1.3.1
 */
public class PufaSignUtil {
	
	private static  final String private_file_name="pracs8.key";
	private static  final  String public_file_name="pub.key";
	
	
	private static final org.slf4j.Logger logger=LoggerFactory.getLogger(PufaSignUtil.class);
	
	/**
	 * 
	 * @param data
	 * @param sign
	 * @return
	 */
	public static boolean verify(String data,String sign){
		return false;
	}
	public static String sign(String data) throws IOException, Exception{
		String sign=new String(XDTRSA.signature(XDTRSA.SHA1withRSA, data.getBytes(), getPrivateKey()));
		return sign;
	}
	
	public static RSAPublicKey getPublicKey() throws IOException, Exception{
		return XDTRSA.getRSAPublicKey(getKey(PufaSignUtil.class.getResource(public_file_name).getPath()));
	}
	public static RSAPrivateKey getPrivateKey() throws IOException, Exception{
		return XDTRSA.getRSAPrivateKey(getKey(PufaSignUtil.class.getResource(private_file_name).getPath()));
	}
	public static String getKey(String filename) throws IOException {
		// Read key from file
		String strKeyPEM = "";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = br.readLine()) != null) {
			strKeyPEM += line + "\n";
		}
		br.close();
		System.out.println("key####################");
		System.out.println(strKeyPEM);
		System.out.println("key####################");
		return strKeyPEM;
	}
	
	public static void main(String[] args) throws IOException, Exception {
		String str="hello";
		String sign=sign(str);
		
	}
}

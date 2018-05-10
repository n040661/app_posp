package xdt.dto.yf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class MD5FileUtil {
	public static String getMd5(String filePath) throws Exception{
		 String value = null;         
		 try {
			 File file = new File(filePath);
	         FileInputStream in = new FileInputStream(file);                                                       
		     try {                                                                                                     
		         MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());   
		         MessageDigest md5 = MessageDigest.getInstance("MD5");                                                 
		         md5.update(byteBuffer);                                                                               
		         BigInteger bi = new BigInteger(1, md5.digest());                                                      
		         value = bi.toString(16);                                                                              
		     } catch (Exception e) {                                                                                   
		         e.printStackTrace();                                                                                  
		     } finally {                                                                                               
		         if(null != in) {                                                                                  
		                 try {                                                                                         
			                 in.close();                                                                                   
			             } catch (IOException e) {                                                                         
			                 e.printStackTrace();                                                                          
			             }                                                                                                 
		         }                                                                                                     
		     }         
		} catch (Exception e) {
			throw e;
		}
	     return value;
	}

	public static String getFileMD5String(String fileName) throws Exception{
		if(fileName == null || "".equals(fileName.trim())) {
			throw new NullPointerException("filename cannot be null!");
		}
		File file = new File(fileName);
		if(!file.exists() || !file.isFile()) {
			throw new FileNotFoundException("file is a path or does not exist!");
		}
		// 拿到一个MD5转换器
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		FileInputStream in = new FileInputStream(file);
		FileChannel ch =in.getChannel();
		MappedByteBuffer byteBuffer =ch.map(FileChannel.MapMode.READ_ONLY, 0,file.length());
		messageDigest.update(byteBuffer);
		byteBuffer.clear();
		ch.close();
		in.close();
		return byteArrayToHex (messageDigest.digest());
	}

	private static String byteArrayToHex(byte[] byteArray) {

		// 首先初始化一个字符数组，用来存放每个16进制字符
		char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'a','b','c','d','e','f' };
		// new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
		char[] resultCharArray =new char[byteArray.length * 2];
		// 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b& 0xf];
		}
		// 字符数组组合成字符串返回
		return new String(resultCharArray);
	}
}

package xdt.dto.yf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Enumeration;

import com.yufusoft.payplatform.security.cipher.YufuCipher;
import com.yufusoft.payplatform.security.cipher.YufuCipherFactory;
import com.yufusoft.payplatform.security.cipher.holder.RSACerFileHolder;
import com.yufusoft.payplatform.security.cipher.holder.RSAPfxFileHolder;
import com.yufusoft.payplatform.security.constants.YufuRSACipherTypeEnum;

public class YufuCipherSupport {
	//private final static String merCertPath = "d:/000001110100000812.cer";
	//private final static String pfxPath = "d:/000001110100000812.pfx";
	//private final static String pfxPwd="654321";
	
	private static volatile YufuCipher cipher = null;
	private static volatile YufuCipherSupport instance = null;
	
	private YufuCipherSupport(){
	}
	
	public static  YufuCipher getCipherInstance(String merCertPath,String pfxPath,String pfxPwd) throws Exception{
		try {
			 if(cipher == null){
				 synchronized(YufuCipherSupport.class){
					 if(cipher == null){
						 RSACerFileHolder cerHolder = new RSACerFileHolder();
						 cerHolder.init(merCertPath);
						 
						 RSAPfxFileHolder pfxHolder = new RSAPfxFileHolder(pfxPwd);
						 pfxHolder.init(pfxPath);
						 
						 YufuCipherFactory cf = YufuCipherFactory.getInstance();
						 YufuCipher syf = cf.createWithRSA(YufuRSACipherTypeEnum.BIDIRECTIONAL_SIGNATURE_WITH_YUFU, cerHolder, pfxHolder);
						 cipher = syf;
					 }
				 }
			 }
		} catch (Exception e) {
			throw e;
		}
		return cipher;
	}
	
	public static  YufuCipherSupport getInstance(String merCertPath,String pfxPath,String pfxPwd) throws Exception{
		try {
			 if(instance == null){
				 synchronized(YufuCipherSupport.class){
					 if(instance == null){
						 instance = new YufuCipherSupport();
						 RSACerFileHolder cerHolder = new RSACerFileHolder();
						 cerHolder.init(merCertPath);
						 
						 RSAPfxFileHolder pfxHolder = new RSAPfxFileHolder(pfxPwd);
						 pfxHolder.init(pfxPath);
						 
						 YufuCipherFactory cf = YufuCipherFactory.getInstance();
						 YufuCipher syf = cf.createWithRSA(YufuRSACipherTypeEnum.BIDIRECTIONAL_ENCRYPTION_AND_SIGNATURE_WITH_YUFU, cerHolder, pfxHolder);
						 instance.cipher = syf;
					 }
				 }
			 }
		} catch (Exception e) {
			throw e;
		}
		return instance;
	}
	public static String getAlsformPfxByKeyStore(KeyStore keystore)/*     */ throws Exception
	/*     */ {
		/* 192 */ String keyAlias = null;
		/* 193 */ Enumeration enumas = keystore.aliases();
		/* 194 */ keyAlias = null;
		/* 195 */ if (enumas.hasMoreElements()) {
			/* 196 */ keyAlias = (String) enumas.nextElement();
			/*     */ }
		/* 198 */ return keyAlias;
		/*     */ }
	
	
}

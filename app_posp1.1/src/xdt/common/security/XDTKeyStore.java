package xdt.common.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XDTKeyStore {

	public static final String XDTRASPRIVATEKey = "xdtRasPrivateKey";
	public static final String XDTRASPUBLICKey = "xdtRasPublicKey";
	private static Map<String,String> encryptKeyMap=new HashMap<String, String>();
	private static Map<String,RSAPublicKey> publicKeyMap=new HashMap<String, RSAPublicKey>();
	private static Map<String,RSAPrivateKey> privateKeyMap=new HashMap<String, RSAPrivateKey>();
	public static List<String> agentList = new ArrayList<String>();
	
	private static String priKey  = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIvg0mkqFqNNUIBvZueljpk5l60wTesqHiUSvbVFBTreaG3s2kouNiwSPtfAjGeVcH680m0HA5GTnn1T9u1SX3H/20wseni2YcM8L1jGMNfg+NiG/qMUQYmS21cFdDyeNlByU7/VhmnfBVk1t1MvqoAGZviZg7aqoYFe/22p87TZAgMBAAECgYAol88YO+Gj9Iz3KN05Pn96kg8lcI48P9XNtnmdgHE7G049EG5s9qo78fOQ000rxl+GOC/s+gscFTX6cwKG7s/JrLlabIb5e7na7pnRrymHpwJRl+GPXxQZ4Q8sCaGHEJKxIXspVfZMPIOwzm16iEO+2iOvWP9sw6Eu2NGkzZJBsQJBAMANmq9DH2SHEja1Y611c1hFz5//8Imk8zL37POJJO8ZD4xl4uqtxOtSl5amDMLzPeCm5/thkkmif8BzWocvbQUCQQC6c+J8ZzoOQKFf3Xp82KTvFAVi8rNyuhROZx8PDqGwu5cRdKESUQsapzQBNzFuIBypi6hfXxRqEatTZ9TaOpDFAkAi4NWcyrXizdzy7pJBqDjgaR1B03tdpAU+1mPD/r4kZgq61q4VLKW8ju1UwKdEqGQubmFm99tXYBmPW+Ou9V/5AkB4ct5wITtm86eMR4aWWloKPHPSXRlB1yXBFGsDDh+0DStlUmOvcn1q5XL6Cg67prD9ckP+AG0urGVyRxRBOi5FAkEAg78MMKlihPF9iiwZuKDCcu3a1PmdcNs75UNW9mui9fYTStpKrMXemT6l39q8701fYiKdEYpulpcBvVqXhxHbKA==";
	//private static String priKey1 = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDGLrZ0lKmJWfnaByA8w04kFmw5rsRqaGGSFWHTyxM4sM4XASXUH0NUH/fXQIRRWkLAsKoiRQnSfB7BVpzXoZPAaHng9P0xPgx+ElKxKT5D0+Hanv3o8SYSRRImcjSBDIch788c/dvVH5264RBgi3qsDJe/lTiIBvTe3XeKrgcSVzueOOliTNHYaXlnfiXiDHs1k3EZ3f5ic1SsbT2NYmfdN++T0zI84CXwMLGIP15SQCkhWBD7jxcP3Bs3Lj+FHKrVjPk4ApdjfhY2KIz0TnFFESJ66xNwA7WfUDgvBff/f2qPjvs5zFLyYZT9l6oq246X8kzQ/fQT2eQM7xgl9LNZAgMBAAECggEAXzpk+8HPcStl7kkMSYZDTv3jRRhgUw0TDUHu/vkOpGf4r38isTnQfWTbD3h13C118+cv6UWpQZXYpWg79zUxrPvGzLyLyRSaph2h1HUP8UGnvgg106/EbnT4Erc/2V6utDTqHXEyJ8eIYBrzZqC+8lses7U9M8e79DcQ0PdseHfPTd0fRJBIdP+VONQ9CF9pHEUJIN+YfCJywSxXJwo0KQN5N3AxAz1bjXYugTVQ7UYCs3yNuQQaKPd/IoNRqF1zb2k4wqMApll0vi/vwpFpecxkvwASRqxI4dkfk4qSN5ibhpgC7uBLcuWFp7+9nGMIU/8x6daVb2qx9AI+j5GLMQKBgQDv4LtOeNipmOlBVf1hYNmMc+rngcSvWrGb7ra6/CqGmeJVQZtXSmBBzxg/rEzYRPJaRlIzBG3fvf+uE7jcElaFumg9LPmbvkBLmGOOM1afxfE8V/niL0WDgVx7cYBzpVNKXGur0U1EUkG2OgFRi17ErUwve/NfNrCv9phfaWCjhQKBgQDTgJUZBkBba3SoSC9i3/eA5DkU2ZMVM1rQS3RqhptMuw7DHV/W88fvStNMEivZtLQAcsuFdPsg/O3IFLuRQF0AiqGwUEx4XMi0TeKq40OhxIif2Oj4ruHjFTJg1UYxEO3Bujn0Q2iX3OZA14Z3exZaiWbgn0xrJPERTerO/wbGxQKBgQDN46uBSDJXJ1sV7DOmMFWpuxjlijmCdSq692K6DZSGoXBl0wD9JGaINbPPexkeC9ekanVFOsj/4PmitWTW55qB/6RW1dKMvfg7qlnRBPmV+3T6YVymqgJVRGRXw+0H1eAulDMKb4SNXrjuKs1U63X1A/W/T/ltQOjYr6TLSAxdbQKBgQDEuzX0sPvsDWfVoE9ziSCPo7n0yu0lKz+twIHpFIwgM17n3srnBptS2ibw+3YOayeDw0PMP6PYa2xCUAaodVYMyubbOdXnn2aRUGEm0351Wno7NWh+6Sz0LSNPLxK7USPWr4J40rrXh4IljlRljvib/nLTPVm30ATsM63WHSIQgQKBgQDO6gyDrce3WU7Mh4S8RNMZTGkA6Op0fsPX76szJDM5g8J0ZpXcn61Zd+55xI0h2tXc5tTlhxR26GW800J+vYecz1UqdoULWi4dk1iUNdVlldegIOWD1D2iLputdvVlUAE8oCeIBAfL1SVvd0nS2SUDrZR+8siyjmB0IThkBoFpZw==";
	private static String priKey1 = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIHkfIMi3KngKUjbfqA1d4Jdrgfa5Z3mh0n3In8MeOEUOXVn0HK+/YvUEp0Z6UgY2qZz1wLVJSk0jwc7waZ1Q/WYVmHGfaKUE//V7mqYT+8bIkWcZhtq5cyPy48hMFUpB/3F18rFBlVGNMA9M4lvLiSGp5SD4Yke0CblFovrs1TDAgMBAAECgYBxHv686fiIzRrHQ7jkZ4v78f/lAWLlf+WkqeDWCpOfWZCsycuEkwaW5wuK3A0I5/RL8Ch5ZA0n9Ij3gLgmcovKslAuwi0jehK0bCNYLYSbKNwOw4aJgRhZf2xI1j34xDmh6D6OVF3KHOw1qbCKIm7VxRMnlvT2x3MSu1SWR8LI8QJBAMDpmjjsaXf+3vsO4NR8IL334kc+i5qd1siH89c5RBBZJVRTlmVh5ATmGmaMmLgfg88MZ6tJb5zcuobNQqb3Q2sCQQCsXu2x0XIPFnVQesBes8FiwyV+zKhT2rQudqTakFybIwz8LsUesuMyd+ND9o0Xc5COlpMiKPRV+1yNHbsfWWIJAkEAosa6FhwNpm3NsqPMPB789a5rJVh2LaHPcOeebdVpRC4kXHInbiZaY3PRhQRPfNVCv/skophVcFOjpEkgW4g6PwJAX0IO5igzencU2iccTLnWc7gY7pw3oi/JR6w1qrwBEZMrR+qo8TA3Mtric9q7Wmx5RPOd2FD9xs6k9S+rC8Yf0QJAPjDP/NHTjZ+dv/f6+jV1ibHWVkpbfVRWW9zZHtLl3YWyLmePXc03mwtvr0S8gsctZPHrBZq1OYkdnRkit9zgXw==";
	
	private static String pubKey  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCL4NJpKhajTVCAb2bnpY6ZOZetME3rKh4lEr21RQU63mht7NpKLjYsEj7XwIxnlXB+vNJtBwORk559U/btUl9x/9tMLHp4tmHDPC9YxjDX4PjYhv6jFEGJkttXBXQ8njZQclO/1YZp3wVZNbdTL6qABmb4mYO2qqGBXv9tqfO02QIDAQAB";
	private static String pubKey1 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCB5HyDItyp4ClI236gNXeCXa4H2uWd5odJ9yJ/DHjhFDl1Z9Byvv2L1BKdGelIGNqmc9cC1SUpNI8HO8GmdUP1mFZhxn2ilBP/1e5qmE/vGyJFnGYbauXMj8uPITBVKQf9xdfKxQZVRjTAPTOJby4khqeUg+GJHtAm5RaL67NUwwIDAQAB";
	
	private static String priKey2="MIICXAIBAAKBgQDAebODJ3LqOjZzbqt3DCosuGRTi9Y4325FhccbK9bFxn4lGA3DbvgHP99Zk5WDaGi6vp3jhCCi8OQ1benqTNE0yKEiVyRUSO70DyvZXGWEZ8r5F7mKyk7VENbT8KQ1ndCPDEDRYhh22iPDoBPvel7JYsoc7ukDTgc5E/t0iNHg0wIDAQABAoGAA8MdOCYQP/XNVKpIHbaWKHOeOmfUUx5C6Ib54lUUCJ+dnZn/wt8rZ/Deq8CPvpbO3T+WlHjjrmuo+kZguRyxRh8pO3kuqfNimPExNhVAvFweZlSmbbuKLYLenHuQXAlNdXtN0mdQv+pgtIEnQ2fUXfzoKULHuSI7s6uOROwopWECQQDp93sLg5RAKXz2GRkXK+tQb9UzGpfuow1Y6NwH9jq3F0DVOxLhSx8gqFJ0QWz8k4ZkdX2Kv7INbFQas4Gk0LCxAkEA0pnwWv0NKgklmoqaNRLn29nAXGAfB6eSwBlDnV640gp8OfWdgF8uuyghdjXrlzg2jGPG+NkyfDD/GhFLvFJqwwJBAKISEyQ4s3t2wd9gJ2dLvyfrMA7EfzaJsHdEZxcE1Et8SibrUvrJpPhkYSmzIKIeN1xjnuF8RQTTY7dTXZ0kCyECQHPzo51FJCFXxedvlnXQQe4O1tVFavA4+BhiEJMtms6yTTFQJPaP8VfEQaAnEd5Bae8j5GIQm7Y698lvamZ2bicCQDIxoylk+WMWZTORR6UgZ8Wnbzb8h6hxwrwEX/tsFSMNM4P2d6ZzaFGxJEm2nvzku5Ezz24dJcNVwXHRSHVUnHo=";
	static{
		try {
			//privateKeyMap.put(XDTRASPRIVATEKey, XDTRSA.getRSAPrivateKey(priKey1));
			//publicKeyMap.put(XDTRASPUBLICKey, XDTRSA.getRSAPublicKey(pubKey1));
			agentList.add("");//默认
			agentList.add("100844");//付呗O单
			agentList.add("100744");//G刷O单
			InputStream is=null;
			try {
				String basePath = XDTKeyStore.class.getResource(XDTKeyStore.class.getSimpleName()+".class").getFile();
				basePath = java.net.URLDecoder.decode(basePath,"utf-8");//解决空格路径问题
				int index=basePath.indexOf("/WEB-INF/classes/");
				if (index > -1) {
					basePath = basePath.substring(0, index+1);
				}else {
					basePath = "/";
				}
				if((index = basePath.indexOf("file:")) >-1){
					basePath = basePath.substring(index+5);
				}
				for(String agent : agentList){
					String priStr = basePath+"WEB-INF/keystore/"+agent+"private_key.pem";
					File file = new File(priStr); 
					FileReader fr = new FileReader(file);
					BufferedReader br = new BufferedReader(fr);
					String str;
					StringBuilder sb = new StringBuilder();
					while ((str = br.readLine()) != null) {
						sb.append(str);
					}
					br.close();
					String key = sb.toString();
					privateKeyMap.put(agent+XDTRASPRIVATEKey, productPrivateKey(key));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				XDTStreamOperator.close(is);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public  static RSAPublicKey productPublicKey(String publicKey) throws Exception{  
	    byte[] keyBytes = XDTConverter.base64ToBytes(publicKey);  
	    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);  
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
	    return (RSAPublicKey) keyFactory.generatePublic(spec);  
	}  
	  
	public  static RSAPrivateKey productPrivateKey(String privateKey) throws Exception{  
	    byte[] keyBytes = XDTConverter.base64ToBytes(privateKey);  
	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);  
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
	    return (RSAPrivateKey) keyFactory.generatePrivate(spec);  
	} 
	
	public static RSAPublicKey getPublicKey(String key){
		return publicKeyMap.get(key);
	}

	public static RSAPrivateKey getPrivateKey(String key){
		RSAPrivateKey RSAPrivateKey= privateKeyMap.get(key);
		if(RSAPrivateKey == null) {
			//当取的O单私钥为空时，取默认私钥
			RSAPrivateKey = privateKeyMap.get(XDTRASPRIVATEKey);
		}
		return RSAPrivateKey;
	}

	public static String getEncryptKey(String key){
		return encryptKeyMap.get(key);
	}
	
	public static void main(String[] args){
		String str = "\"business\":\"{\"cr\":\"49BA6CF385BD4AA3952BDD3BFA35D7F9\"}";
		//String str = "X1IdkZKV6352QssPcdQFYhF0lBXOSml3fXdP4GotSjhIukTl60RydSqdofdWr8EAZvQfznApj/3s8OLeWjo3HneDCeloP1Fq4n/6vrTCb9NzBUsVGA5lEBEbcUvy84HBAjKLd4F1ueFiGKLyHCy6axwM1apSZRfD9YDGMtC1qL8=";
		System.out.println("--------1------------str:"+str);
		XDTKeyStore XDTKeyStore = new XDTKeyStore();
	    byte[] strByte;
	    String businessStr="";
		try {
			//XDTKeyStore.productPrivateKey(priKey);
			//XDTKeyStore.productPublicKey(pubKey1);
			/*strByte = XDTRSA.encrypt(privateKeyMap.get(XDTRASPRIVATEKey), str.trim().getBytes("utf-8"));
			strByte = XDTRSA.decrypt(publicKeyMap.get(XDTRASPUBLICKey), strByte);*/
			//String businessStr=XDTConverter.bytesToBase64(XDTRSA.encrypt(privateKeyMap.get(XDTRASPRIVATEKey),str.getBytes("utf-8")));
			//String businessStr=XDTConverter.bytesToBase64(str.getBytes("utf-8"));
			//businessStr = new String(XDTConverter.base64ToBytes("5L2g5aW977yM5pyL5Y+L"));
			strByte = XDTRSA.encrypt(privateKeyMap.get(XDTRASPRIVATEKey),"你好".getBytes("utf-8"));
			businessStr = new String(strByte);
			strByte = XDTRSA.decrypt(publicKeyMap.get(XDTRASPUBLICKey), strByte);
			//strByte=XDTRSA.decrypt(publicKeyMap.get(XDTRASPUBLICKey),XDTConverter.base64ToBytes(str));
			//strByte=XDTRSA.decrypt(privateKeyMap.get(XDTRASPRIVATEKey),XDTConverter.base64ToBytes(str));
			//strByte=XDTRSA.decrypt(privateKeyMap.get(XDTRASPRIVATEKey),str.getBytes());
			//strByte=XDT3Des.decrypt3DES(publicKeyMap.get(XDTRASPUBLICKey),XDTConverter.base64ToBytes(businessStr));
			
			//strByte = XDTRSA.encrypt(privateKeyMap.get(XDTRASPRIVATEKey), str.trim().getBytes("utf-8"));
			//strByte = XDTRSA.decrypt(publicKeyMap.get(XDTRASPUBLICKey), strByte);
			System.out.println("-------2------------str:"+businessStr.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}  
	    
	} 
}

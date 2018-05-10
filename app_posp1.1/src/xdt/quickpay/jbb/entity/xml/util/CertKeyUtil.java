package xdt.quickpay.jbb.entity.xml.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.commons.codec.binary.Base64;

public class CertKeyUtil {
	
	
	public static String getMerPrivateKey(String keystorefile ,String keypasswd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException{      
        KeyStore ks = KeyStore.getInstance("PKCS12");
        FileInputStream fin = new FileInputStream(keystorefile);
        ks.load(fin,keypasswd.toCharArray());
             
        Enumeration<String> alias = ks.aliases();
       
        String keyAlias = null;
        if (alias.hasMoreElements()) // we are readin just one certificate.
        {
            keyAlias = (String)alias.nextElement();
            //System.out.println("alias=[" + keyAlias + "]");
        }
        
        PrivateKey prikey = (PrivateKey)ks.getKey(keyAlias,keypasswd.toCharArray());
        String prikeyStr = Base64.encodeBase64String(prikey.getEncoded());	
        java.security.cert.Certificate cert = ks.getCertificate(keyAlias);
        
        PublicKey pubkey = cert.getPublicKey();
        String pubkeyStr = Base64.encodeBase64String(pubkey.getEncoded());	
                
        //System.out.println("私钥字符串："+prikeyStr);
        //System.out.println("公钥字符串："+pubkeyStr);
        
        return prikeyStr;
	}
	
	public static String getMerPublicKey(String certInfo)throws Exception{      
     byte[] byteCert = Base64.decodeBase64(certInfo);
     //转换成二进制�?
     ByteArrayInputStream bain = new ByteArrayInputStream(byteCert);
     CertificateFactory cf = CertificateFactory.getInstance("X.509");
     X509Certificate cert = (X509Certificate)cf.generateCertificate(bain);
    // String info = cert.getSubjectDN().getName();  
    // System.out.println("公证书信�?2�?+ Base64.encodeBase64String(cert.getEncoded()));

     String pubKeyStr = Base64.encodeBase64String(cert.getPublicKey().getEncoded());	        
    // PublicKey key2 = EctonRSAUtils.getPublicKey(Base64.decodeBase64(pubKeyStr));//
    // System.out.println("公钥字符串："+pubKeyStr);
     
     return pubKeyStr;
        
	}

	
	public static String getEctonPublicKey(String path )throws Exception{	
		InputStream inStream = null;
		try{
		    File file = new File(path);
		    inStream  = new FileInputStream(file);
		    CertificateFactory cf = CertificateFactory.getInstance("X.509");
		    X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
		    
	        String pubKeyStr = Base64.encodeBase64String(cert.getPublicKey().getEncoded());	        

			return pubKeyStr;			
		}finally {
            if (inStream != null) {
            	inStream.close();
            }
        }
		
	}
	
	public static String getEctonPrivateKey(String path,String pwd,String keyAlias )throws Exception{
        KeyStore ks = KeyStore.getInstance("JKS");
        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(path);
            ks.load(fis, pwd.toCharArray());
 
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, pwd.toCharArray());
            String prikeyStr = Base64.encodeBase64String(prikey.getEncoded());	
            return prikeyStr;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
	
	public static void main(String[] args) throws Exception {
		getEctonPublicKey("C:/keystore.jks");

	}
}

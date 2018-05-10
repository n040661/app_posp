package xdt.quickpay.jbb.util;

import cn.com.sof.SOF_SecurityEngineDeal;
import cn.com.sof.exception.SOR_Base64Exception;
import cn.com.sof.exception.SOR_CertificateException;
import cn.com.sof.exception.SOR_InvalidKeyException;
import cn.com.sof.exception.SOR_KeyStoreException;
import cn.com.sof.exception.SOR_NoSuchAlgorithmException;
import cn.com.sof.exception.SOR_NoSuchProviderException;
import cn.com.sof.exception.SOR_Pkcs7EncodeException;
import cn.com.sof.exception.SOR_Pkcs7SignException;
import cn.com.sof.exception.SOR_SecurityException;
import cn.com.sof.exception.SOR_SignDataException;
import cn.com.sof.exception.SOR_SignatureException;
import cn.com.sof.exception.SOR_UnrecoverableKeyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class etonepaySign {
	private SOF_SecurityEngineDeal sof;
	private static final Log LOG = LogFactory.getLog(etonepaySign.class);

	public etonepaySign(String privateKeyName, String privateKayConfigName) {
		try {
			this.sof = SOF_SecurityEngineDeal.GetInstance((String) privateKeyName, (String) privateKayConfigName);
		} catch (Exception e) {
			LOG.error((Object) ("易通签名客户端初始化失败：" + e.getMessage()));
		}
	}

	public String sign(String data) {
		String signedData = "";
		try {
			signedData = this.sof.SOF_SignDataByP7(data);
		} catch (SOR_SignDataException e) {
			e.printStackTrace();
		} catch (SOR_Pkcs7SignException e) {
			e.printStackTrace();
		} catch (SOR_Pkcs7EncodeException e) {
			e.printStackTrace();
		} catch (SOR_Base64Exception e) {
			e.printStackTrace();
		} catch (SOR_CertificateException e) {
			e.printStackTrace();
		} catch (SOR_InvalidKeyException e) {
			e.printStackTrace();
		} catch (SOR_KeyStoreException e) {
			e.printStackTrace();
		} catch (SOR_NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (SOR_UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (SOR_SecurityException e) {
			e.printStackTrace();
		} catch (SOR_NoSuchProviderException e) {
			e.printStackTrace();
		} catch (SOR_SignatureException e) {
			e.printStackTrace();
		}
		return signedData;
	}
}
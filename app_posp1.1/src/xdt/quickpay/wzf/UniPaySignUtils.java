package xdt.quickpay.wzf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ccit.security.bssp.CAUtility;
import ccit.security.bssp.common.TypeConstant;

/**
 * 商户接入签名验签 
 * @date 2016年12月18日
 */
public class UniPaySignUtils {
	private static Logger logger = LoggerFactory.getLogger(UniPaySignUtils.class);
	/**
	 * 商户参数签名
	 * @param reqMap 签名数据Map (注意：null 或 空值 或 一些特殊字符串不参与验签)
	 * @param signType 签名类型  SHA256_RSA，SM2_SM3
	 * @return
	 */
	public static String merSign(Map<String, String> reqMap, String signType){
		String signResult = null;
		if (!Constant.SIGN_TYPE_DICT.contains(signType)) {
			logger.info("不支持此签名类型:"+signType);
		}else if( Constant.SIGNTYPE_RSA_SHA256.equals(signType) || 
				Constant.SIGNTYPE_SM2_SM3.equals(signType)){
			 try {
				 signResult = rsaOrSm2Sign(getSignSourMsg(reqMap)+"|", signType);
			} catch (Exception e) {
				logger.error("商户用非对称加密方式签名-签名异常",e);
			}
		}
		return signResult;
	}
	
	/**
	 * 商户验签
	 * @author guodong
	 * @date 2016年12月28日
	 * @param reqMap 请求map参数 (注意：null 或 空值 或 一些特殊字符串不参与验签)
	 * @param signType 签名类型 SHA256_RSA，SM2_SM3
	 * @param cert 联通支付公司证书base64
	 * @return true 通过，false 不通过
	 * 
	 */
	public static boolean merVerify(Map<String, String> reqMap, String signType,String signMsg,String cert) {
		if (!Constant.SIGN_TYPE_DICT.contains(signType)) {
			logger.info("不支持此签名类型:"+signType);
			return false;
		}else{
			try {
				return rsaOrSm2Verify(UniPaySignUtils.getSignSourMsg(reqMap), signMsg, signType, cert);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	
	/**
	 * 待签名参数预处理，去空、排序
	 * @author guodong
	 * @date 2016年12月28日
	 * @param params 传入参数Map
	 * @return 真实参与签名的参数串
	 */
	private static String getSignSourMsg(Map<String, String> params) {
		params = filterNoParams(params);
		String signSource = createLinkString(params);
		logger.info("待签名参数串：" + signSource);
		return signSource;
	}
	
	/**
	 * 过滤无用参数,包括参数值为空及制定参数-hmac、signMsg、cert
	 * @author guodong
	 * @date 2016年12月28日
	 * @param params Map 带过滤的参数
	 * @return Map 过滤后的参数
	 * 
	 */
	private static Map<String, String> filterNoParams(Map<String, String> params) {
		Map<String, String> newParam = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (Constant.EXCEPT_SIGNMSG.equalsIgnoreCase(key) || Constant.EXCEPT_CERT.equalsIgnoreCase(key)
					|| Constant.EXCEPT_HMAC.equalsIgnoreCase(key)) {
				continue;
			}
			if (value == null||value.length()==0) {
				continue;
			}
			newParam.put(key, value);
		}
		return newParam;
	}
	

	/**
	 * 排序后生成待签名字符串
	 * @param params Map 待处理的参数
	 * @return String
	 */
	public static String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuilder prestr = new StringBuilder();
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			prestr.append(key).append("=").append(params.get(key)).append("|");
		}
		return prestr.substring(0, prestr.length()-1);
	}
	/**
	 * 模拟商户验签(RSA 或者 SM2)
	 * @author guodong
	 * @date 2016年12月21日
	 * @param originalData 原数据
	 * @param signMsg 签名信息
	 * @param algorithm 签名算法
	 * @param cert 证书
	 * @return
	 * @throws Exception
	 * 
	 */
	private static boolean rsaOrSm2Verify(String originalData,String signMsg,String algorithm, String cert) throws Exception{
		/**
		 * 0x00000103：SHA1_RSA签名算法
		 * 0x00000105：SHA256_RSA签名算法
		 * 0x00000104：SM3_SM2签名算法
		*/
		byte[] indata = originalData.getBytes(Constant.CHARSET_UTF8);
		byte[] certbytes = cert.getBytes(Constant.CHARSET_UTF8);
		byte[] signMsgbytes = signMsg.getBytes(Constant.CHARSET_UTF8);
		int verifyWithCert=1 ;
		if(Constant.SIGNTYPE_RSA_SHA256.equalsIgnoreCase(algorithm)){
			verifyWithCert = CAUtility.verifyWithCert(0x00000105, certbytes, indata, signMsgbytes);
		}else if(Constant.SIGNTYPE_SM2_SM3.equalsIgnoreCase(algorithm)){
			verifyWithCert = CAUtility.EccVerifySignByCert(0x00000104, certbytes, indata, signMsgbytes);
		}
		return verifyWithCert==0?true:false;
	}
	
	/**
	 * 模拟商户签名(RSA 或者 SM2)
	 * @author guodong
	 * @date 2016年12月9日
	 * @param originalData 原数据
	 * @param algorithm	签名算法
	 * @return 签名信息
	 * @throws Exception
	 * 
	 */
	private static String rsaOrSm2Sign(String originalData,String algorithm) throws Exception{
		
		System.out.println("待签名参数串1:"+originalData);
		byte[] indata = originalData.getBytes(Constant.CHARSET_UTF8);
		byte[] signBytes = null;
		signBytes = CAUtility.RsaSignByP12(TypeConstant.CA_SHA256WITHRSA, indata);
		return new String(CAUtility.base64Encode(signBytes));
	}
	
}

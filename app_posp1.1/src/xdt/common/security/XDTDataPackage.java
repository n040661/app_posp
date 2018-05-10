package xdt.common.security;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;


public class XDTDataPackage implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Logger logger = Logger.getLogger(XDTDataPackage.class);
	public static final int FLAG_CRYPT_NONE	=0; //不加密
	public static final int FLAG_CRYPT_3DES	=1;//3des加密
	public static final int FLAG_CRYPT_AES	=2; //AES加密
	public static final int FLAG_CRYPT_XDTKEY=3;//RAS加密
	
	public static final int FLAG_SIGN_NONE	=0; //不签名
	public static final int FLAG_SIGN_MD5	=1; //MD5签名
	public static final int FLAG_SIGN_SHA1	=2;//SHA1签名
	public static final int FLAG_SIGN_RAS	=3;//SHA1签名

	public static final String ERROR_CODE_1000 = "1000";
	public static final String ERROR_MSG_1000 = "analyzeError！";
	public static final String ERROR_CODE_1001 = "1001";
	public static final String ERROR_MSG_1001 = "analyzeSuccess！";
	//private static final RSAPrivateKey priKey =XDTKeyStore.getPrivateKey(XDTKeyStore.XDTRASPRIVATEKey);
	//private static final RSAPublicKey pubKey =XDTKeyStore.getPublicKey(XDTKeyStore.XDTRASPUBLICKey);
	
	
	/** The errCode .  包的错误码*/
	private String errCode = ERROR_CODE_1001;
	/** The errMsg .  包的错误信息*/
	private String errMsg = ERROR_MSG_1001;
	
	/** The crypt flag.  加密方式*/
	private int cryptFlag=FLAG_CRYPT_3DES;

	/** The hash flag.  计算散列值*/
	private int signFlag=FLAG_SIGN_SHA1;

	/** The hash.  hash=business+key*/
	private String sign;

	/** The business. */
	private byte[] business;

	private UserContext userContext;
	private int sessionState;
	private String tokenId;
	private String oAgentNo;
	
	public XDTDataPackage(UserContext userContext){
		this.userContext = userContext;
	}
	
	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public int getCryptFlag() {
		return cryptFlag;
	}

	public void setCryptFlag(int cryptFlag) {
		this.cryptFlag = cryptFlag;
	}



	public int getSignFlag() {
		return signFlag;
	}

	public void setSignFlag(int signFlag) {
		this.signFlag = signFlag;
	}

	public byte[] getBusiness() {
		return business;
	}

	public void setBusiness(byte[] business) {
		this.business = business;
	}


	public int getSessionState() {
		return sessionState;
	}

	public void setSessionState(int sessionState) {
		this.sessionState = sessionState;
	}

	
	public String getOAgentNo() {
		return oAgentNo;
	}

	public void setOAgentNo(String oAgentNo) {
		this.oAgentNo = oAgentNo;
	}

	/**
	 * To json string.发送信息内容，json字符串
	 * @return the string
	 */
	public String toJSONString(){
		Map<String,Object> map = new HashMap<String,Object>();
	    try {
	    	map.put("business", toJSONObjectString());
	    } catch (JSONException e) {
	    	errCode = ERROR_CODE_1000;
	    	errMsg = ERROR_MSG_1000;
	    	logger.error("errCode:"+errCode+",errMsg:"+errMsg);
	    	//e.printStackTrace();
	    }
	    map.put("errCode",errCode);
	    map.put("errMsg", errMsg);
	    map.put("cryptFlag", cryptFlag);
	    map.put("signFlag", signFlag);
	    map.put("sign", sign);
	    map.put("sessionState", sessionState);
	    map.put("tokenId", tokenId);
	    return JSON.toJSONString(map);
	}

	/**
	 * To json object. encrypt 加密
	 *
	 * @return the jSON object
	 */
	public String toJSONObjectString(){
		String businessStr = "";
		try{
			//加密不能为明文
			/*if(cryptFlag < 1){
				//加密不能为明文
				logger.error("数据错误!报文加密方式不能为明文,cryptFlag:"+cryptFlag);
				throw new Exception("数据错误!");
			}*/
			if (business!=null) {
				//业务数据加密
				switch (cryptFlag){
				case FLAG_CRYPT_NONE:
				{
					businessStr=new String(business,"UTF-8");
					break;
				}
				case FLAG_CRYPT_3DES:
				{
					String key1=getSRKey();
					String key2=getCRKey();
					String key3=getSessionKey();
					businessStr=XDTConverter.bytesToBase64(XDT3Des.encrypt(business, key1, key2, key3));
					break;
				}
				case FLAG_CRYPT_XDTKEY:
				{
					//服务器私钥加密
					businessStr=XDTConverter.bytesToBase64(XDTRSA.encrypt(XDTKeyStore.getPrivateKey(userContext.getOAgentNo()+XDTKeyStore.XDTRASPRIVATEKey),business));
					break;
				}
				default:
					//$ERROR:
					throw new Exception("加密错误！");
				}
				//计算散列值,签名 
				switch (signFlag) {
				case FLAG_SIGN_NONE:
					sign="";
					break;
				case FLAG_SIGN_MD5:
					sign=XDTConverter.bytesToHex(XDTHash.getHashByString(XDTHash.MD5, businessStr + getKey()));
					break;
				case FLAG_SIGN_SHA1:
					sign=XDTConverter.bytesToHex(XDTHash.getHashByString(XDTHash.SHA1, businessStr + getKey()));
					break;
					
				default:
					//$ERROR:
					throw new Exception("");
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return businessStr;
	}

	
	/**
	 * decrypt 解密
	 * @param jsonDataStr
	 * @return
	 * @throws Exception
	 */
	public JSONObject parseJSONString(String jsonDataStr) throws Exception {
		JSONObject jsonObject = JSON.parseObject(jsonDataStr);
	    try {
	        parseJSON(JSON.parseObject(jsonDataStr));
	        jsonObject.put("business", new String(business,"utf-8").trim());
	    } catch (Exception e) {
	    	errCode = ERROR_CODE_1000;
	    	errMsg = ERROR_MSG_1000;
	    	logger.error("errCode:"+errCode+",errMsg:"+errMsg);
	    	//e.printStackTrace();
	    }
	    jsonObject.put("errCode",errCode);
	    jsonObject.put("errMsg", errMsg);
	    jsonObject.put("cryptFlag", cryptFlag);
	    jsonObject.put("signFlag", signFlag);
	    jsonObject.put("sign", sign);
	    jsonObject.put("tokenId", tokenId);
	    jsonObject.put("oAgentNo", oAgentNo);
	    return jsonObject;
	}
	

	public void parseJSON(JSONObject jsonObject) throws Exception{
		try {
			cryptFlag=jsonObject.getInteger("cryptFlag")== null ? 0:jsonObject.getInteger("cryptFlag");
			signFlag=jsonObject.getInteger("signFlag")== null ? 0:jsonObject.getInteger("signFlag");
			sign=jsonObject.getString("sign") == null ? "":jsonObject.getString("sign");
			tokenId = jsonObject.getString("tokenId")== null ? "":jsonObject.getString("tokenId");
			oAgentNo = jsonObject.getString("oAgentNo")== null ? "":jsonObject.getString("oAgentNo");
			
			String businessStrTemp=jsonObject.getString("business");
			if (businessStrTemp != null && !businessStrTemp.equals("")) {
				businessStrTemp = businessStrTemp.trim();
				//验证散列值
				int hashType;
				switch (signFlag) {
				case FLAG_SIGN_NONE:
					hashType=XDTHash.NONE;
					break;
				case FLAG_SIGN_MD5:
					hashType=XDTHash.MD5;
					break;
				case FLAG_SIGN_SHA1:
					hashType=XDTHash.SHA1;
					break;
				default:
					//$ERROR:MP
					throw new Exception("");
				}
				if (hashType!=XDTHash.NONE) {
					//$ERROR:校验散列值
					if (!XDTConverter.bytesToHex(
							XDTHash.getHashByString(hashType,businessStrTemp+getKey())).equals(sign)){
						//$ERROR:MP==校验散列值失败==请联系客服
						throw new Exception("签名失败！");	
					}
				}
				//解析业务数据
				switch (cryptFlag) {
				case FLAG_CRYPT_NONE:
				{
					business=businessStrTemp.getBytes("UTF-8");
					break;
				}
				case FLAG_CRYPT_3DES:
				{
					String key1=getSRKey();
					String key2=getCRKey();
					String key3=getSessionKey();
					business=XDT3Des.decrypt(XDTConverter.base64ToBytes(businessStrTemp), key1, key2, key3);
					break;
				}
				case FLAG_CRYPT_AES:
				{
					business=businessStrTemp.getBytes("UTF-8");
					break;
				}
				case FLAG_CRYPT_XDTKEY:
				{
					//服务器私钥解密
					business=XDTRSA.decrypt(XDTKeyStore.getPrivateKey(oAgentNo+XDTKeyStore.XDTRASPRIVATEKey),XDTConverter.base64ToBytes(businessStrTemp));
					break;
				}
				default:
					//$ERROR:MP
					throw new Exception("加密错误！");
				}
			}else{
				business = "".getBytes();
			}
			
		}catch (Exception e) {
			//$ERROR:MP
			throw e;
		}
	}
	
	
	/**
	 * 获取business string.
	 *
	 * @return business
	 * @throws UnsupportedEncodingException 
	 */
	public String getBusinessStr() throws UnsupportedEncodingException {
		return new String(business,"UTF-8");
	}



	public String getSRKey() {
	    return this.userContext.getServerRandom().substring(4, 12);
	}
	
	public String getCRKey() {
	    return this.userContext.getClientRandom().substring(4, 12);
	 }
	
	public String getSessionKey() {
	    String sessionId = this.userContext.getSessionId();
	    sessionId = sessionId.substring(sessionId.length() - 9, sessionId.length() - 1);
	    return sessionId;
	 }

	public String getKey(){
		return getSRKey() + getCRKey()+ getSessionKey();
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	
	
}

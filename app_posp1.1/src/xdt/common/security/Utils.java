package xdt.common.security;

import java.util.Collections;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class);
	private Utils() {
	}

	private static TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {
	};
	
	/**
	 * 把HttpServletRequest中请求的数据转为Map
	 * @param request
	 * @param userContext
	 * @return
	 */
	public static Map<String, String> getRequestClient(HttpServletRequest request, UserContext userContext) {
		try {
			XDTDataPackage xdtdataPackage = new XDTDataPackage(userContext);
			JSONObject jsonObject = xdtdataPackage.parseJSONString(request.getParameter("requestData"));
			String jsonStr = jsonObject.toJSONString();
			logger.info("client data :"+ jsonStr);
			return JSON.parseObject(jsonStr, typeReference);
		} catch (Exception e) {
			logger.error("error : client data is null!--" + e.getMessage());
			return Collections.emptyMap();
		}

	}

	

	/**
	 * 把结果返还给客户端
	 * 
	 * @param request
	 * @param response
	 * @param content
	 */
	public static void responseClient(HttpServletRequest request, HttpServletResponse response, byte[] content,
			Integer cryptFlag, Integer signFlag) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		HttpSession session = request.getSession();
		UserContext userContext = (UserContext) session.getAttribute(UserContext.USERCONTEXT);
		int sessionState = 0 ;
		String tokenId = "";
		if (userContext == null) {
			response.setHeader("sessionState", String.valueOf(UserContext.SESSION_STATE_TIMEOUT));
			sessionState = UserContext.SESSION_STATE_TIMEOUT;
		} else {
			response.setHeader("sessionState", String.valueOf(userContext.getSessionState()));
			sessionState = userContext.getSessionState();
			tokenId = userContext.getTokenId();
		}
		XDTDataPackage dataPackage = new XDTDataPackage(userContext);
		dataPackage.setBusiness(content);
		
		dataPackage.setCryptFlag(cryptFlag);
		dataPackage.setSignFlag(signFlag);
		dataPackage.setSessionState(sessionState);
		dataPackage.setTokenId(tokenId);
		String res = dataPackage.toJSONString();
		try {
			ServletOutputStream os = response.getOutputStream();
			os.write(res.getBytes("UTF-8"));
			XDTStreamOperator.close(os);
			logger.info(" response data："+res);
		} catch (Throwable e) {
			logger.error("response data error:"+e);
		}
	}
	

	
	
	public static void main(String[] args){
		//jsonstr:{"business":"d3Nz5L2g5aW977yB5LuK5aSp5aW977yB","cryptFlag":1,"hashFlag":0}
		String str = "哈wss你好！今天好！sddfsdf";
		System.out.println("--0----------------jsonstr:"+str);
		UserContext userContext = new UserContext();
		//1 加密
		/*userContext.setServerRandom("1234567890123456");
		userContext.setClientRandom("1234567890123456");
		userContext.setSessionId("12345678901234567890");
		XDTDataPackage dataPackage = new XDTDataPackage(userContext);
		dataPackage.setCryptFlag(3);
		dataPackage.setSignFlag(0);
		try {
			dataPackage.setBusiness(str.trim().getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String jsonStr = dataPackage.toJSONString();
		System.out.println("--1-------en---------jsonstr:"+jsonStr);
		
		jsonStr = "FwZHdaGvpDUaJ8GRaE1H+fYeef/T9X2746cGKhyylNorITVV86Ck9Ce8JOStY1PhdFJpLGvv+cVsNf4Kb1+EnHVNSX5IYr6rjVaD4gmM5J/jmvvRL2Ef/VdaeIgrnZJkeu8ZTStxjzB2Cpws/b3WLdUJKf5cYS/+G7lQNKR2wms=";
		*/
		String jsonStr = "{\"business\":\"O5yoO8iU2psEbcTIfAglsSw+YV//GxCHbOdYT5taoVWYtwLO0ldEz8DV7P688scNCJUb2GVfnBFeVGhJKbSeLayNwOMVcNammLnOi3nnqiAWyyLKiFQ3rWvHZz6WBDT3XEvmVMX7sYByFccwHF5K/ADvjGUqs34QxmHk2mPGOko=\",\"cryptFlag\":1,\"hashFlag\":0,\"sign\":\"ss11\"}";
		String js = "O5yoO8iU2psEbcTIfAglsSw+YV//GxCHbOdYT5taoVWYtwLO0ldEz8DV7P688scNCJUb2GVfnBFeVGhJKbSeLayNwOMVcNammLnOi3nnqiAWyyLKiFQ3rWvHZz6WBDT3XEvmVMX7sYByFccwHF5K/ADvjGUqs34QxmHk2mPGOko=";
		//2  解密
		XDTDataPackage dataPackage1 = new XDTDataPackage(userContext);
		dataPackage1.setCryptFlag(3);
		dataPackage1.setSignFlag(1);
		try {
			//jsonStr = dataPackage1.parseJSONString(jsonStr).toJSONString();
			//byte[] business=XDTRSA.decrypt(XDTKeyStore.getPrivateKey(XDTKeyStore.XDTRASPRIVATEKey),XDTConverter.base64ToBytes(js));
			//System.out.println("---------js:"+new String(business,"utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("--2-------de---------jsonstr:"+jsonStr);
		/*String str1 = "{\"passWord\":\"4E9D68219E16161D1C33DE35E247B12C\",\"userName\":\"18211182737\"}";
		String str = "\"{\"attestationSign\":\"\",\"backReason\":\"\",\"retCode\":100,\"retMessage\":\"????\",\"shortName\":\"\",\"status\":\"\",\"userName\":\"\"}\"";
		MerchantLoginRequestDTO dto = new Gson().fromJson(str1, MerchantLoginRequestDTO.class);
		System.out.println("name："+dto.getUserName());*/
	}
}

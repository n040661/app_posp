package xdt.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import xdt.common.security.UserContext;
import xdt.common.security.Utils;
import xdt.common.security.XDTDataPackage;
import xdt.servlet.XDTHttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 过滤请求
 * @author liuliehui
 */
public class VerifyFilter implements Filter {
	Logger logger = Logger.getLogger(VerifyFilter.class);
	private static FilterConfig config;
	private static Map<String, String> notFilterUrlMap = new HashMap<String, String>();  
	private static Map<String, String> checkTokenIdUrlMap = new HashMap<String, String>(); 
	private String shakeHandsUrl = "shakeHands.action";
	private int sessionTime = 300;
	public static  int flag = 0; //0加密要用的格式，1开关以前数据格式
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
		FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		HttpSession session = request.getSession();
		UserContext userContext = (UserContext)session.getAttribute(UserContext.USERCONTEXT);
		String requestData = request.getParameter("requestData");
		String url = request.getRequestURI();
		logger.info("--userContext:"+JSON.toJSONString(userContext)+",requestData:"+requestData+",Url:"+url);
		boolean notFilterUrlFlag = notFilterUrl(url);
		Map<String,String> clientMap = null;
		if(!notFilterUrlFlag){
			if(url.indexOf(shakeHandsUrl) > -1){
				//处理握手
				userContext = shakeHandsMethod(request, response, session,
						requestData);
				logger.info("--shakeHandsUrl-userContext-:"+JSON.toJSONString(userContext));
				return;
			}else{
				//处理普通交易
				req  = generalTrans(req, request, response, session,
						userContext,clientMap);
				if(req.getAttribute("flag").equals("false")) return;
			}
			//$checkTokenID:start
			clientMap = (Map<String,String>)req.getAttribute("clientMap");
			String clientTokenID = "";
			if(clientMap != null){
				clientTokenID = clientMap.get("tokenId");
			}
			String serverTokenID = userContext.getTokenId();
			userContext.setTokenId(UUID.randomUUID().toString().replace("-", ""));
			session.setAttribute(UserContext.USERCONTEXT, userContext);
			session.setAttribute("__changed__", "");
			if(checkTokenIdUrl(url)){
				synchronized (this) {
					if(StringUtils.isEmpty(serverTokenID) || StringUtils.isEmpty(clientTokenID)){
						//$ERROR 101:该请求无效,请联系客服人员 
						Map<String,String> businessMap = new HashMap<String,String>();
						businessMap.put("retCode", "101");
						businessMap.put("retMessage", "无效请求！");
						Utils.responseClient(request, response, JSON.toJSONBytes(businessMap), 3, 1);
						return;
					}
					if(!serverTokenID.equals(clientTokenID)){
						//$ERROR 122:不能重复提交!
						Map<String,String> businessMap = new HashMap<String,String>();
						businessMap.put("retCode", "102");
						businessMap.put("retMessage", "不能重复提交！");
						Utils.responseClient(request, response, JSON.toJSONBytes(businessMap), 3, 1);
						return;
					}
				}
			}
			//$checkTokenID:end
		}
		logger.info("--generalTrans--requestdata:"+req.getParameter("requestData"));
		chain.doFilter(req, resp);
	}

	/**
	 * 处理普通交易
	 * @param req
	 * @param request
	 * @param response
	 * @param session
	 * @param userContext
	 * @return
	 */
	private ServletRequest generalTrans(ServletRequest req,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, UserContext userContext,Map<String,String> clientMap) {
		String requestData;
		if(userContext == null){
			logger.info("--generalTrans userContext is null");
			//直接返回，要从新握
			Utils.responseClient(request, response, JSON.toJSONBytes(new HashMap<String,String>()), 0, 0);
			req.setAttribute("flag", "false");
			return req;
		}
		clientMap = Utils.getRequestClient(request, userContext);
		String errCode = clientMap.get("errCode");
		//解包失败
		if(StringUtils.isBlank(errCode) || errCode.equals(XDTDataPackage.ERROR_CODE_1000)){
			logger.info("--generalTrans   解包失败  UserContext:"+JSON.toJSONString(userContext));
			userContext.setSessionState(UserContext.SESSION_STATE_NULLSESSION);
			session.setAttribute(UserContext.USERCONTEXT, userContext);
			Utils.responseClient(request, response, JSON.toJSONBytes(new HashMap<String,String>()), 3, 0);
			req.setAttribute("flag", "false");
			return req;
		}
		logger.info("--generalTrans   ok");
		requestData = JSON.toJSONString(clientMap.get("business"));
		 if(request instanceof HttpServletRequest){
			 req = new XDTHttpServletRequestWrapper((HttpServletRequest)request);
		 }
		 req.setAttribute("requestData", requestData);
		 req.setAttribute("clientMap", clientMap);
		 req.setAttribute("flag", "true");
		return req;
	}

	/**
	 * 处理握手
	 * @param request
	 * @param response
	 * @param session
	 * @param requestData
	 * @return
	 */
	private UserContext shakeHandsMethod(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			String requestData) {
		UserContext userContext;
		userContext = new UserContext();
		XDTDataPackage xdtdataPackage = new XDTDataPackage(userContext);
		try {
			Map<String,String> map = new HashMap<String,String>();
			JSONObject jsonObject = xdtdataPackage.parseJSONString(requestData);
			
			userContext.setClientType(jsonObject.getString("clientType").toLowerCase());
			userContext.setClientVersion(jsonObject.getString("clientVersion"));
			userContext.setDevicesId(jsonObject.getString("devicesId"));
			userContext.setDeviceModel(jsonObject.getString("deviceModel"));
			userContext.setDeviceVersion(jsonObject.getString("deviceVersion"));
			userContext.setOAgentNo(jsonObject.getString("oAgentNo"));
			
			String errCode = jsonObject.getString("errCode");
			//解包失败
			if(StringUtils.isBlank(errCode) || errCode.equals(XDTDataPackage.ERROR_CODE_1000)){
				userContext.setSessionState(UserContext.SESSION_STATE_NULLSESSION);
			}else{
				//解包成功
				String busTtr  = jsonObject.getString("business");
				JSONObject business  = JSON.parseObject(busTtr.trim());
				userContext.setClientRandom(business.getString("cr"));
				userContext.setSessionState(UserContext.SESSION_STATE_UNLOGIN);
				userContext.setSessionId(session.getId());
				userContext.setServerRandom(UUID.randomUUID().toString().replace("-", ""));
				
				session.setMaxInactiveInterval(sessionTime);
				map.put("sr", userContext.getServerRandom());
			}
			session.setAttribute(UserContext.USERCONTEXT,userContext);
			if(userContext.getClientType() != null && userContext.getClientType().equals("iphone")){
				Utils.responseClient(request, response, JSON.toJSONBytes(map), 3, 0);
			}else{
				Utils.responseClient(request, response, JSON.toJSONBytes(map), 3, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userContext;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		//不用过滤的url
		String notFilterUrl = config.getInitParameter("notFilterUrl");
		//握手的url
		String shakeHandsUrl = config.getInitParameter("shakeHandsUrl");
		//sesson超时时间
		String sessionTime = config.getInitParameter("sessionTime");
		//flag
		String flag = config.getInitParameter("flag");
		//检查防重发url
		String checkTokenIdUrl = config.getInitParameter("checkTokenIdUrl");
		
		if(notFilterUrl != null){
			String[] notFilterUrlArr = notFilterUrl.split(",");
			for(String url : notFilterUrlArr){
				this.notFilterUrlMap.put(url, url);
			}
		}
		if(shakeHandsUrl != null){
			this.shakeHandsUrl = shakeHandsUrl;
		}
		if(sessionTime != null){
			this.sessionTime = Integer.parseInt(sessionTime);
		}
		
		if(flag != null){
			this.flag = Integer.parseInt(flag);
		}
		if(checkTokenIdUrl != null){
			String[] checkTokenIdUrlArr = checkTokenIdUrl.split(",");
			for(String url : checkTokenIdUrlArr){
				this.checkTokenIdUrlMap.put(url, url);
			}
		}
		
	}
	
	/**
	 * 不用过滤的Url
	 * @param url
	 * @return boolean
	 */
	public static  boolean notFilterUrl(String url){
		for(Map.Entry<String, String> entry : notFilterUrlMap.entrySet())
			if(url.indexOf(entry.getValue()) > -1){
				return true;
			}
		return false;
	}
	
	/**
	 * 检查防重发的Url
	 * @param url
	 * @return boolean
	 */
	public static  boolean checkTokenIdUrl(String url){
		for(Map.Entry<String, String> entry : checkTokenIdUrlMap.entrySet())
			if(url.indexOf(entry.getValue()) > -1){
				return true;
			}
		return false;
	}
}

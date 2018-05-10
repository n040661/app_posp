package xdt.controller;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import xdt.common.security.UserContext;
import xdt.common.security.Utils;
import xdt.dto.hj.HJRequest;
import xdt.filter.VerifyFilter;
import xdt.model.ChannleMerchantConfigKey;
import xdt.model.SessionInfo;
import xdt.pufa.security.PuFaSignUtil;
import xdt.util.BeanToMapUtil;
import xdt.util.JsdsUtil;
import xdt.util.utils.MD5Utils;
import xdt.util.utils.RequestUtils;
import cn.beecloud.BCCache;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

/**
 * @author Jeff
 */
public class BaseAction {
	
	//json工具
	protected Gson gson=new Gson();
	
    public Logger logger = Logger.getLogger(BaseAction.class);
    /**
	 * 向客户端返回数据
	 * @param response 
	 * @param obj 返回的值
	 * @throws java.io.IOException
	 */
	public void outPrint(HttpServletResponse response,Object obj) throws IOException{
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String url = request.getRequestURI();
		HttpSession session = request.getSession();
		
		if(VerifyFilter.notFilterUrl(url)){
			response.setContentType("text/html;charset=UTF-8");
			response.setHeader("progma","no-cache");
			response.setHeader("Cache-Control","no-cache");
			PrintWriter out = response.getWriter();
			if(session != null){
				SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
				if(sessionInfo != null){
					if( obj != null && StringUtils.isNotBlank(obj.toString())){
						logger.info(" 用户："+sessionInfo.getMobilephone()+"  返回："+obj.toString());
					}
				}
			}
			out.print(obj);
			out.flush();
			out.close();
		}
		UserContext userContext = (UserContext)session.getAttribute(UserContext.USERCONTEXT);
		Utils.responseClient(request, response, obj.toString().getBytes("utf-8"), 1, 1);
		logger.info("-----------------------end----------------");
	}

	protected String requestClient(HttpServletRequest req){
		String requestData = req.getParameter("requestData");
		return requestData;
	}

	/**
	 * 记录日志信息
	 * @param ip ip地址
	 * @param session 会话信息
	 * @param flag false 登录前  true 登录后
	 * @return
	 */
	public void setSession(HttpSession session,String ip,boolean flag){
		String mobilePhone = "mobilePhone";
		if(flag){
			SessionInfo sessionInfo = (SessionInfo)session.getAttribute(SessionInfo.SESSIONINFO);
			mobilePhone = sessionInfo.getMobilephone();
		}
		MDC.put("ip",ip);
		MDC.put("session",session.getId().substring(0,10));
		MDC.put("mobilePhone",mobilePhone);
	}

	/**
	 * 把字段分解后再排序
	 */
	public static String convertString2Array(String str){
		String[]array = {};
		if (StringUtils.isNotBlank(str)){
			array = str.split(",");
		    Arrays.sort(array);
		    return array.toString();
		}
		return "";
	}

	/**
	 * 解析19pay下单返回码
	 * @param retCode
	 * @return
	 */
	public String parse19payRetCode(String retCode){
		String failReason = "";
		if(!retCode.equals("00000")){
			if(retCode.equals("00001")){
				failReason = "缺少必要参数";
				return failReason;
			}else if(retCode.equals("00002")){
				failReason = "不存在的接口商户";
				return failReason;
		    }else if(retCode.equals("00003")){
		    	failReason = "订单金额无效";
		    	return failReason;
			}else if(retCode.equals("00005")){
				failReason = "银行卡类型不支持";
				return failReason;
			}else if(retCode.equals("00006")){
				failReason = "账户类型不支持";
				return failReason;
			}else if(retCode.equals("00007")){
				failReason = "银行编码错误";
				return failReason;
			}else if(retCode.equals("00008")){
				failReason = "账户余额不足";
				return failReason;
			}else if(retCode.equals("00009")){
				failReason = "单笔最高金额限制";
				return failReason;
			}else if(retCode.equals("00010")){
				failReason = "单笔最低金额限制";
				return failReason;
			}else if(retCode.equals("00011")){
				failReason = "下单总金额限制";
				return failReason;
			}else if(retCode.equals("00012")){
				failReason = "下单总比数限制";
				return failReason;
			}else if(retCode.equals("00013")){
				failReason = "重复下单";
				return failReason;
			}else if(retCode.equals("00014")){
				failReason = "未找到对应订单";
				return failReason;
			}else if(retCode.equals("00015")){
				failReason = "后台通知地址不合法";
				return failReason;
			}else if(retCode.equals("00016")){
				failReason = "已完成订单总金额超限";
				return failReason;
			}else if(retCode.equals("00017")){
				failReason = "已完成订单总比数超限";
				return failReason;
			}else if(retCode.equals("00019")){
				failReason = "加密卡号或持卡人姓名不合法";
				return failReason;
			}else if(retCode.equals("00020")){
				failReason = "卡号与姓名不匹配";
				return failReason;
			}else if(retCode.equals("00021")){
				failReason = "卡BIN信息验证失败";
				return failReason;
			}else if(retCode.equals("00023")){
				failReason = "验证通道不匹配";
				return failReason;
			}else if(retCode.equals("00025")){
				failReason = "付款类型不合法";
				return failReason;
			}else if(retCode.equals("00026")){
				failReason = "交易类型不合法";
				return failReason;
			}else if(retCode.equals("00028")){
				failReason = "商户不支持当前通道";
				return failReason;
			}else if(retCode.equals("00029")){
				failReason = "安全信息校验无效";
				return failReason;
			}else if(retCode.equals("00030")){
				failReason = "订单有风险，不予通过";
				return failReason;
			}else if(retCode.equals("11000")){
				failReason = "hmac校验失败";
				return failReason;
			}else if(retCode.equals("11111")){
				failReason = "19pay系统异常";
				return failReason;
			}else{
				failReason = "未知错误";
				return failReason;
			}
		}
		return failReason;
	}

	/**
	 * 解析手机充值下单返回码
	 * @param resultno
	 * @return
	 * @throws java.io.IOException
	 */
	public String parsePrepaidPhoneRetCode(String resultno) throws IOException{
		String failReason = "";
		if(!resultno.equals("0000")){
			if(resultno.equals("0001")){
				failReason = "支付失败，19pay支付系统异常";
				return failReason;
			}else if(resultno.equals("0002")){
				failReason = "未知错误，请代理商联系19pay平台技术进行处理";
				return failReason;
			}else if(resultno.equals("0999")){
				failReason = "未开通直冲功能";
				return failReason;
			}else if(resultno.equals("1000")){
				failReason = "19pay下单失败";
				return failReason;
			}else if(resultno.equals("1001")){
				failReason = "传入参数不完整";
				return failReason;
			}else if(resultno.equals("1002")){
				failReason = "验证摘要串验证失败";
				return failReason;
			}else if(resultno.equals("1005")){
				//立即更新产品库
//				new SessionConfigurationAction().productQuery();
				failReason = "没有对应充值产品，请更新产品查询接口";
				return failReason;
			}else if(resultno.equals("1006")){
				failReason = "19pay系统异常";
				return failReason;
			}else if(resultno.equals("1007")){
				failReason = "账户余额不足";
				return failReason;
			}else if(resultno.equals("1008")){
				failReason = "此产品超出当天限额";
				return failReason;
			}else if(resultno.equals("1010")){
				failReason = "产品与手机号不匹配";
				return failReason;
			}else if(resultno.equals("1013")){
				failReason = "暂不可充值";
				return failReason;
			}else if(resultno.equals("1015")){
				failReason = "无法查到对应号段";
				return failReason;
			}else if(resultno.equals("1017")){
				failReason = "电信手机10秒内不能重复充值";
				return failReason;
			}else if(resultno.equals("1022")){
				failReason = "充值号码格式错误" ;
				return failReason;
			}else if(resultno.equals("1028")){
				failReason = "下单接口请求次数超限" ;
				return failReason;
			}else{
				failReason = "未知错误";
				return failReason;
			}
		}
		return failReason;
	}

	/**
	 * 解析加油卡充值下单返回码
	 * @param retCode
	 * @return
	 */
	public String parseRefuelingCardRetCode(String retCode){
		String failReason = "";
		if(!retCode.equals("1")){
			if(retCode.equals("0")){
				failReason = "已受理";
				return failReason;
			}else if(retCode.equals("2")){
				failReason = "失败";
				return failReason;
			}else if(retCode.equals("97")){
				failReason = "处理中";
				return failReason;
			}else if(retCode.equals("99")){
				failReason = "异常";
				return failReason;
			}else if(retCode.equals("4")){
				failReason = "权限验证失败";
				return failReason;
			}else if(retCode.equals("9")){
				failReason = "系统维护中";
				return failReason;
			}else if(retCode.equals("10")){
				failReason = "系统繁忙";
				return failReason;
			}else if(retCode.equals("11")){
				failReason = "余额不足";
				return failReason;
			}else if(retCode.equals("13")){
				failReason = "商户不存在或已锁定";
				return failReason;
			}else if(retCode.equals("14")){
				failReason = "请求信息有误";
				return failReason;
			}else if(retCode.equals("17")){
				failReason = "订单号重复";
				return failReason;
			}else if(retCode.equals("20")){
				failReason = "订单号已经提交过，不能重复提交";
				return failReason;
			}else{
				failReason = "未知错误";
				return failReason;
			}
		}
		return failReason;
	}
	/**
	 * 输出json
	 * @param response
	 * @param str
	 * @throws IOException
	 */
	public static void outString(HttpServletResponse response,Object str)
			throws IOException {
		Gson gson=new Gson();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(str);
		out.flush();
		out.close();
	}
	
	/**
	 * 输出字符
	 * @param response
	 * @param str
	 * @throws IOException
	 */
	public static void outString(HttpServletResponse response,String str)
			throws IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(str);
		out.flush();
		out.close();
	}
	
	public boolean signVerify(Object obj,String sign) throws Exception{
		 Map<String,String> temp=BeanToMapUtil.convertBean(obj);
		 if(PuFaSignUtil.signVerify(temp,sign)){
			 return true;
		 }else{
			 return false;
		 }
		 
	}
	protected boolean verifySign(String sign, String timestamp) {
        return verify(sign, BCCache.getAppID() + BCCache.getAppSecret(),timestamp, "UTF-8");		
	}

	private boolean verify(String sign, String text, String key,
			String input_charset) {
		text = text + key;
        String mysign = DigestUtils.md5Hex(getContentBytes(text, input_charset));
        long timeDifference = System.currentTimeMillis() - Long.valueOf(key);
        if (mysign.equals(sign) && timeDifference <= 300000) {
            return true;
        } else {
            return false;
        }
	}

	private byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
	}
	/**
	 * bean 转化为实体
	 * 
	 * @param bean
	 * @return
	 */
	public static HashMap<String, Object> beanToMap(Object bean) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (null == bean) {
			return map;
		}
		Class<?> clazz = bean.getClass();
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor descriptor : descriptors) {
			String propertyName = descriptor.getName();
			if (!"class".equals(propertyName)) {
				Method method = descriptor.getReadMethod();
				String result;
				try {
					result = (String) method.invoke(bean);
					if (null != result) {
						map.put(propertyName, result);
					} else {
						map.put(propertyName, "");
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return map;
	}
	

	/** 
     * 获取当前网络ip 
     * @param request 
     * @return 
     */  
    public String getIpAddr(HttpServletRequest request){  
        String ipAddress = request.getHeader("x-forwarded-for");  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getHeader("Proxy-Client-IP");  
            }  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getHeader("WL-Proxy-Client-IP");  
            }  
            if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
                ipAddress = request.getRemoteAddr();  
                if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){  
                    //根据网卡取本机配置的IP  
                    InetAddress inet=null;  
                    try {  
                        inet = InetAddress.getLocalHost();  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                    ipAddress= inet.getHostAddress();  
                }  
            }  
            //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
            if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15  
                if(ipAddress.indexOf(",")>0){  
                    ipAddress = ipAddress.substring(ipAddress.length(),ipAddress.indexOf(","));  
                }  
            }  
            return ipAddress;   
    }
}
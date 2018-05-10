<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%
	  String query = request.getParameter("query");
	 String[] list = query.split("\\|");

	
	String service_type = list[0];
	String mch_id = list[1];
	String out_trade_no = list[2];
	String total_fee = list[3];
	String subject = list[4];
	String body = list[5];
	String time_start = list[6];
	String time_expire = list[7];
	String device_info = list[8];
	String spbill_create_ip = list[9];
	String notify_url = list[10];
	String callback_url = list[11];
	String nonce_str = list[12];
	String sign = list[13]; 
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>支付宝服务窗</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/404/cmstop-error.css" />
<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>
<body>
	<form name="form" action="http://brcb.pufubao.net/gateway"
		method="post">
		<input type="text" name="service_type"
			value="<%=service_type%>" /> <input
			type="text" name="mch_id"
			value="<%=mch_id%>" /> <input type="text"
			name="out_trade_no"
			value="<%=out_trade_no%>" /> <input
			type="text" name="total_fee"
			value="<%=total_fee%>" /> <input
			type="text" name="subject"
			value="<%=subject%>" /> <input type="text"
			name="body" value="<%=body%>" /> <input
			type="text" name="device_info"
			value="<%=device_info%>" /> <input
			type="text" name="spbill_create_ip"
			value="<%=spbill_create_ip%>" /> <input
			type="text" name="time_start"
			value="<%=time_start%>" /> <input
			type="text" name="time_expire"
			value="<%=time_expire%>" /> <input
			type="text" name="notify_url"
			value="<%=notify_url%>" /> <input
			type="text" name="callback_url"
			value="<%=callback_url%>" /> <input
			type="text" name="nonce_str"
			value="<%=nonce_str%>" /> <input
			type="text" name="sign" value="<%=sign%>" />
	</form>
	<script type="text/javascript">
		/* 自动提交表单 */
		document.forms["form"].submit();
	</script>
</body>
</html>
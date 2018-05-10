<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户订单数据(模拟)</title>
</head>
<body>
	商户订单数据
	<form action="${pageContext.request.contextPath }/hddh/registerSign.action" method="post">
	    <input type="text" name="merid" value="100341512318531"> <br>
	    <input type="text" name="cooperatorUserId" value="<%=UtilDate.PayRandomOrder()%>">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
		<input type="text" name="cooperatorOrderId" value="<%=UtilDate.getOrderNum()%>">订单时间(不为空,一共14 位，格式为：年[4位]月[2 位]日[2 位]时[2 位]分[2 位]秒[2位])<br>
	    <input type="text" name="callBackUrl" value="http://60.28.24.164:8102/app_posp/TFBController/returnUrl.action">支付结果前台通知页面(不为空，必须是合法URL,字节数不超过256)<br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

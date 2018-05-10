<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" pageEncoding="UTF-8"%>
<%@page import="xdt.util.UtilDate"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户订单数据(模拟)</title>
</head>
<body>
	商户订单数据
	<form
		action="${pageContext.request.contextPath }/live/qrcode/paySign.action"
		method="post">
		<input type="text" name="service" value="cj001">服务类型<br> 
		<input type="text" name="merchantCode" value="100510112345708">商户号<br>
		<input type="text" name="orderNum" value="<%=System.currentTimeMillis()%>">订单号<br> 
		<input type="text" name="notifyUrl" value="http://60.28.24.164:8102/app_posp/test/qrcode/interface.action">回调地址<br> 
		<input type="text" name="merchantName" value="重庆小面" >商户名称<br>
		<input type="text" name="merchantNum" value="20170913114745">商户编号<br>
		<input type="text" name="commodityName" value="担担面">商户编号<br> 
		<input type="number" name="transMoney" value="100" >金额<br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

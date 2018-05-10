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
	<form action="<%=basePath %>/YBController/payScan.action" method="post">
	    <input type="text" name="merchantNo" value="100341512318531">商户号<br>
	    <input type="text" name="orderId" value="<%=UtilDate.PayRandomOrder()%>">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
	    <input type="text" name="orderAmount" value="0.01">订单金额<br>
		<input type="text" name="requestDate" value="<%=UtilDate.getOrderNum()%>">订单时间(不为空,一共14 位，格式为：年[4位]月[2 位]日[2 位]时[2 位]分[2 位]秒[2位])<br>
		<input type="text" name="timeoutExpress" value=""><br> 
		<input type="text" name="userType" value="MAC"><br>
		<input type="text" name="ext" value=""><br>
		<input type="text" name="directPayType" value=""><br>
		<input type="text" name="cardType" value="DEBIT"><br>
		<input type="text" name="userNo" value="123456"><br>
	    <input type="text" name="notityUrl" value="http://60.28.24.164:8102/app_posp/TFBController/returnUrl.action">支付结果前台通知页面(不为空，必须是合法URL,字节数不超过256)<br>
		<input type="text" name="redirectUrl" value="http://60.28.24.164:8102/app_posp/TFBController/returnUrl.action">支付结果后台通知地址(必须是合法URL,字节数不超过256)<br>
		<input type="text" name="paymentParamExt" value=""><br>
		<input type="text" name="goodsName" value="测试"><br>
		<input type="text" name="goodsDesc" value="测试"><br>
		<input type="text" name="industryParamExt" value=""><br>
		<input type="text" name="memo" value=""><br>
		<input type="text" name="riskParamExt" value=""><br>
		<input type="text" name="csUrl" value=""><br>
		<input type="text" name="timestamp" value=""><br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

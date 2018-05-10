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
	<form action="<%=basePath %>/YBController/pay.action" method="post">
	    <input type="text" name="merchantNo" value="${temp.merchantNo}"> <br>
	    <input type="text" name="orderId" value="${temp.orderId}"><br>
	    <input type="text" name="orderAmount" value="${temp.orderAmount}"><br>
	    <input type="text" name="requestDate" value="${temp.requestDate}"><br>
		<input type="text" name="timeoutExpress" value="${temp.timeoutExpress}"><br>
		<input type="text" name="userType" value="${temp.userType}"><br>
		<input type="text" name="ext" value="${temp.ext}"><br>
		<input type="text" name="directPayType" value="${temp.directPayType}"><br> 
		<input type="text" name="cardType" value="${temp.cardType}"><br>
		<input type="text" name="userNo" value="${temp.userNo}"><br>
		<input type="text" name="notityUrl" value="${temp.notityUrl}"><br>
		<input type="text" name="redirectUrl" value="${temp.redirectUrl}"><br>
		<input type="text" name="paymentParamExt" value="${temp.paymentParamExt}"><br>
	    <input type="text" name="goodsName" value="${temp.goodsName}"><br>
	    <input type="text" name="goodsDesc" value="${temp.goodsDesc}"><br>
		<input type="text" name="industryParamExt" value="${temp.industryParamExt}"><br>
		<input type="text" name="memo" value="${temp.memo}"><br>
		<input type="text" name="riskParamExt" value="${temp.riskParamExt}"><br>
		<input type="text" name="csUrl" value="${temp.csUrl}"><br>
		<input type="text" name="timestamp" value="${temp.timestamp}"><br>
		<input type="text" name="v_sign" value="${temp.v_sign}"><br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	<form action="${pageContext.request.contextPath }/quickPayAction/message.action" method="post">
	    <input type="text" name="v_mid" value="<%=request.getParameter("v_mid")%>"><br>
	    <input type="text" name="v_oid" value="<%=request.getParameter("v_mid")%>"><br>
	    <input type="text" name="v_txnAmt" value="<%=request.getParameter("v_txnAmt")%>"><br>
		<input type="text" name="v_time" value="<%=request.getParameter("v_time")%>"><br>
		<input type="text" name="v_code" value="<%=request.getParameter("v_code")%>"><br>
		<input type="text" name="v_msg" value="<%=request.getParameter("v_msg")%>"><br>
		<input type="text" name="v_sign" value="<%=request.getParameter("v_sign")%>"><br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

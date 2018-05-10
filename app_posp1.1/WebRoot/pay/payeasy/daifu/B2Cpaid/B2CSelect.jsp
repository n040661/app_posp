<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>B2C支付余额查询</title>
</head>
<body>
	<!--  <form id="test1" name="test1" method="post" action="https://210.73.90.235/merchant/virement/mer_payment_balance_check.jsp"> -->
	<!--  <form id="test1" name="test1" method="post" action="https://api.yizhifubj.com/merchant/virement/mer_payment_balance_check.jsp"> -->
	<!-- <form id="test1" name="test1" method="post" action="https://pay.yizhifubj.com/merchant/virement/mer_payment_balance_check.jsp">  -->
	<form id="test1" name="test1" method="post"
		action="${pageContext.request.contextPath }/payeasy/dimension.action">
		<table align="center">
			<tr>
				<td>商户编号</td>
				<td><input type="text" id="merchantId" name="merchantId"
					value="10012112721"></td>
			<tr>
				<td>提交</td>
				<td><input type="submit" name="submit" value="submit"></td>
			</tr>
		</table>
	</form>
</body>
</html>


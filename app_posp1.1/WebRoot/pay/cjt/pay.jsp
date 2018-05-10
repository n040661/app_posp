<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'confirm.jsp' starting page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

</head>

<body>
	<form id="form"
		action="${pageContext.request.contextPath }/cjt/congirmpay.action"
		method="post">
		<center>
			<table>
				 <tr>
					<td>商户号：</td>
					<td><input type="text" name="pid"
						value="100352154114708"></td>
				</tr>
				<tr>
					<td>订单号：</td>
					<td><input type="text" name="transactionid"
						value="${pay.transactionid}"></td>
				</tr>
				<tr>
					<td>验证码：</td>
					<td><input type="text" name="checknode"></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="submit"
						name="confirm" value="确认支付"></td>
				</tr>
			</table>
		</center>
	</form>
</body>
</html>

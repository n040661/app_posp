<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>B2C代付订单查询</title>
</head>
<body>

	<form id="test1" name="test1" method="post"
		action="${pageContext.request.contextPath }/payeasy/dimension.action">
		<table align="center">
			<tr>
				<td>商户编号</td>
				<td><input type="text" id="merchantId" name="merchantId"
					value="10012112721"></td>
			<tr>
				<td>客户标识</td>
				<td><input type="text" id="v_identity" name="v_identity"
					value="18901295220"></td>
			</tr>
			<tr>
				<td><input type="submit" name="submit" value="submit"></td>
			</tr>
		</table>
	</form>
</body>
</html>


<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>B2C批量代付</title>
</head>
<body>
	<form action="${pageContext.request.contextPath }/payeasy/dimension.action" method="post">
		<table align="center">
			<tr>
				<td>商户编号</td>
				<td><input type="text" id="merchantId" name="merchantId" value="10012112721"></td>
			</tr>
			<tr>
				<td>数据</td>
				<td><input type="text" id="v_data" name="v_data"
					value="1|1.00|13240-20170413-507189$6226220126775369|李鑫|中国民生银行北京上地支行|北京市|北京市|1.00|18902195061|305100001104" size="135"></td>
			</tr>
			<tr>
				<td>提交</td>
				<td><input type="submit" name="submit" value="submit"></td>
			</tr>
		</table>
	</form>
</body>
</html>


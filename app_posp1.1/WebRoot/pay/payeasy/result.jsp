<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<script type="text/javascript">
	var ss= ${v_mid};
	var dd =ss.sp
</script>
<body>
	<center>
		<form
			action="<%=request.getParameter("daifu_url") %>"
			method="post">

			<table>
				<tr>
					<th>商户号:</th>
					<td><input type="text" id="v_mid" name="v_mid"
						value="<%=request.getParameter("v_mid") %>"></td>
				</tr>
				<tr>
					<th>代付数据:</th>
					<td><input type="text" id="v_data" name="v_data"
						value="<%=request.getParameter("v_data") %>"></td>
				</tr>
				<tr>
					<th>签名:</th>
					<td><input type="text" id="v_mac" name="v_mac"
						value="<%=request.getParameter("v_mac") %>"></td>
				</tr>
				<tr>
					<th>版本号:</th>
					<td><input type="text" id="v_version" name="v_version"
						value="<%=request.getParameter("v_version") %>"></td>
				</tr>
				
				<tr>
					<td colspan="2"><input type="submit" value="代付"></td>
				</tr>
			</table>
		</form>
	</center>
</body>
</html>
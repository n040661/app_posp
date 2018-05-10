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

<title>测试</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<script src="<%=basePath%>/js/jquery-2.0.0.js"></script>
</head>

<body>
	<fieldset>
		<legend>测试列表</legend>
		<ul>
		<li><a href="<%=basePath%>/pay/hengfeng/token.jsp">获取Token</a></li>
		<li><a href="<%=basePath%>/pay/hengfeng/message.jsp">获取验证码</a></li>
		<li><a href="<%=basePath%>/pay/hengfeng/consume.jsp">支付</a></li>
		<li><a href="<%=basePath%>/pay/hengfeng/updateToken.jsp">更新Token</a></li>
		<li><a href="<%=basePath%>/pay/hengfeng/query.jsp">查询支付结果</a></li>
	</ul>
	</fieldset>

</body>
</html>

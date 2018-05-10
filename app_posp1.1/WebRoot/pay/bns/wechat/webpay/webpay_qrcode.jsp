<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=8">
<title>公众号支付入口</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/404/cmstop-error.css"
	type="text/css"></link>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.qrcode.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qrcode.js"></script>
<script type="text/javascript">
	$(function() {
		var codeUrl = $('#code_url').val();
		$('#qrcode').qrcode({width: 200,height: 200,correctLevel:0,text:codeUrl});   
	})
</script>
<body class="body-bg">
	<div align="center">
		<a href="${pageContext.request.contextPath}/index.jsp"
			style="margin: 50px 0 0 0;" class="btn">返回网站首页</a>
		<h1 style="margin-top: 10px;">打开微信扫一扫:</h1>
		<div id="qrcode" style="margin-top: 10px; text-align: center;"></div>
		<br /> <br />
		<textarea rows="3" cols="50" id="code_url" readonly="readonly">${url}</textarea>
	</div>
</body>
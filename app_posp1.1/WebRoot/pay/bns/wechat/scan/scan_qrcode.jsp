<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>扫码</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/404/cmstop-error.css" media="all">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.1.1.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.qrcode.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/qrcode.js"></script>
	<script type="text/javascript">
		$(function() {
			var codeUrl = $('#code_url').val();
			$('#qrcode').qrcode(codeUrl);
		})
	</script>
</head>
<body class="body-bg">

<div align="center">
	<a href="${pageContext.request.contextPath}/index.jsp" style="margin: 50px 0 0 0;" class="btn">返回网站首页</a>
	<h1 style="margin-top: 10px;">打开微信扫一扫进行支付:</h1>

	<div id="qrcode" style="margin-top: 10px; text-align: center;"></div>
	<br/> <br/>
	<table>
		<tr>
			<td>return_code</td>
			<td><b>${scan.return_code }</b></td>
		</tr>
		<tr>
			<td>return_msg</td>
			<td><b>${scan.return_msg }</b></td>
		</tr>
		<tr>
			<td>result_code</td>
			<td><b>${scan.result_code }</b></td>
		</tr>
		<tr>
			<td>code_url</td>
			<td><b>${scan.code_url }</b></td>
		</tr>
	</table>
	<input type="hidden" id="code_url" value="${scan.code_url}"/>
</div>
</body>
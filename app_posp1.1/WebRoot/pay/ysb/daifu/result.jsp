<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<!-- 引入JQuery -->
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-easyui-1.5.2/jquery-1.8.3.min.js"></script>
<link href="${pageContext.request.contextPath}/css/index.css"  type="text/css" rel="stylesheet"/>
</head>
<body>
<div style="font-size: 24px;color: red;">
<c:choose>
	<c:when test="${resultCode eq '0000'}">
		成功
	</c:when>
	<c:when test="${resultCode eq '1030'}">
		订单号重复
	</c:when>
	<c:when test="${resultCode eq '1000'}">
		签名错误
	</c:when>
	<c:otherwise>
		其他错误,响应码：${resultCode}
	</c:otherwise>
</c:choose>
</div>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<!-- 引入JQuery -->
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery-easyui-1.5.2/jquery-1.8.3.min.js"></script>
<link href="${pageContext.request.contextPath}/css/404/index.css"  type="text/css" rel="stylesheet"/>
</head>
<body>
<div style="font-size: 24px;color: red;">
<c:choose>
	<c:when test="${resultCode eq '0000'}">
		成功
	</c:when>
	<c:when test="${resultCode eq '1012'}">
		订单号重复
	</c:when>
	<c:when test="${resultCode eq '2001'}">
		子协议已经签订，无需重复签订
	</c:when>
	<c:when test="${resultCode eq '2010'}">
		交易不存在
	</c:when>
	<c:otherwise>
		其他错误${resultCode}
	</c:otherwise>
</c:choose>
</div>
</body>
</html>
<%@ page language="java" isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Compute error</title>
    </head>
    <body bgcolor="#FFFFFF">
        <div align="center">
            <br>
            <br>
            <h1>
                错误信息
            </h1>
            <hr>
            <p>
            <h3><%=exception.toString()%></h3>
            <br>
            <br>
            <br>
            <a href="javascript: history.back();">返回</a>
        </div>
    </body>
</html>

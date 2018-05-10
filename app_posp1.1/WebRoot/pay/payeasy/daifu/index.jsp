<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>B2C代付</title>
  </head>
  
  <body>
    <a href="<%=path%>/pay/payeasy/daifu/B2Cpaid/B2Cpaid.jsp">B2C批量代付(商户需开通代付功能)</a><br/>
    <a href="<%=path%>/pay/payeasy/daifu/B2Cpaid/B2CSelect.jsp">B2C余额查询接口(商户需开通代付余额查询功能)</a><br/>
    <a href="<%=path%>/pay/payeasy/daifu/B2Cpaid/B2CselByOid.jsp">B2C订单查询接口</a><br/><br/>
 
  </body>
</html>

<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="java.util.HashMap"%>
<%@page import="xdt.util.JsdsUtil"%>
<%@page import="java.util.Map" %>
<%
Map<String, String> param= new HashMap<String, String>();
  String key="d86842e5cc77486fbca7fa36498442da";
  String merchantId="10032045977";
  String tranTp="1";
  param.put("merchantId",merchantId);
  param.put("tranTp",tranTp);
  String signmsg=JsdsUtil.sign(param, key);
  
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
    <form action="${pageContext.request.contextPath }/balance/scan_param.action" id="form" method="post">
      <table>
         <tr>
          <td>商户号:</td>
          <td><input type="text" value="<%=merchantId %>" id="merchantId" name="merchantId"></td>
         </tr>
         <tr>
          <td>代付类型:</td>
          <td><input type="text" value="<%=tranTp %>" id="tranTp" name="tranTp"></td>
         </tr>
         <tr>
          <td>签名:</td>
          <td><input type="text" name="signmsg" value="<%=signmsg%>"></td>
         </tr>
         <tr><td colspan="2"><input type="submit" name="submit" value="确认"></td></tr>
      </table>
    </form>
</body>
</html>
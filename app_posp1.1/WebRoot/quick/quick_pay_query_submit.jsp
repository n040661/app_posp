<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户订单数据(模拟)</title>
</head>
<body onload="sharingPay()">
	<form id="pay_form" action="${pageContext.request.contextPath }/quickPayAction/query.action" method="post">
	    <input type="hidden" name="v_version" value="${temp.v_version}"> <br>
	    <input type="hidden" name="v_mid" value="${temp.v_mid}"><br>
	    <input type="hidden" name="v_oid" value="${temp.v_oid}"><br>
		<input type="hidden" name="v_type" value="${temp.v_type}"><br>
		<input type="hidden" name="v_sign" value="${temp.v_sign}"><br>
	</form>
</body>
<script type="text/javascript">
  function sharingPay(){
	  
	  document.all.pay_form.submit();
	  
  }
</script>
</html>

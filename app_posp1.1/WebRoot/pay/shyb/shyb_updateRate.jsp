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
<body>
	商户订单数据
	<form action="${pageContext.request.contextPath }/shyb_app/updateRateSign.action" method="post">
	    <input type="text" name="customerNumber" value="100341512318531"> <br>
	     <input type="text" name="subContractId" value="10020692379"> <br>
	    <select name="productType">
	      <option value="1" selected="selected">交易费率</option>
	      <option value="2">T1 自助结算费率</option>
	      <option value="3">T0 自助结算基本费率</option>
	    </select><br>
		<input type="text" name="rate" value="0.0042">费率<br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

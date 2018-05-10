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
	<form action="${pageContext.request.contextPath }/shyb_app/transferSign.action" method="post">
	    <input type="text" name="customerNumber" value="100341512318531"> <br>
	    <input type="text" name="subContractId" value="10021015108"> <br>
	    <input type="text" name="externalNo" value="<%=UtilDate.PayRandomOrders()%>">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
	    <select name="transferWay">
	      <option value="1" >T0 自助结算</option>
	      <option value="2" selected="selected">T1 自助结算</option>
	    </select><br>
		<input type="text" name="amount" value="10">金额<br>
		<input type="text" name="callBackUrl" value="http://60.28.24.164:8101/app_posp/pay/quick/index.jsp">回调地址<br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

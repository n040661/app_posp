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
	<form action="${pageContext.request.contextPath }/shyb_app/pay.action" method="post" enctype="multipart/form-data">
	    <input type="text" name="customerNumber" value="${temp.customerNumber}"> <br>
	    <input type="text" name="subContractId" value="${temp.subContractId}"><br>
	    <input type="text" name="requestId" value="${temp.requestId}"><br>
	    <input type="text" name="source" value="${temp.source}"><br>
		<input type="text" name="amount" value="${temp.amount}"><br>
		<input type="text" name="mobileNumber" value="${temp.mobileNumber}"><br>
		<input type="text" name="mcc" value="${temp.mcc}"><br>
		<input type="text" name="callBackUrl" value="${temp.callBackUrl}"><br>
		<input type="text" name="webCallBackUrl" value="${temp.webCallBackUrl}"><br>
		<input type="text" name="payerBankAccountNo" value="${temp.payerBankAccountNo}"><br>
		<input type="text" name="v_sign" value="${temp.v_sign}"><br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

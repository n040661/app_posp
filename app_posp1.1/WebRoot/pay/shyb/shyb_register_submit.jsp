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
	<form action="${pageContext.request.contextPath }/shyb_app/register.action" method="post" enctype="multipart/form-data">
	    <input type="text" name="customerNumber" value="${temp.customerNumber}"> <br>
	    <input type="text" name="mailStr" value="${temp.mailStr}"><br>
	    <input type="text" name="requestId" value="${temp.requestId}"><br>
	    <input type="text" name="customerType" value="${temp.customerType}"><br>
		<input type="text" name="businessLicence" value="${temp.businessLicence}"><br>
		<input type="text" name="bindMobile" value="${temp.bindMobile}"><br>
		<input type="text" name="signedName" value="${temp.signedName}"><br>
		<input type="text" name="linkMan" value="${temp.linkMan}"><br>
		<input type="text" name="idCard" value="${temp.idCard}"><br>
		<input type="text" name="legalPerson" value="${temp.legalPerson}"><br>
		<input type="text" name="minSettleAmount" value="${temp.minSettleAmount}"><br>
		<input type="text" name="riskReserveDay" value="${temp.riskReserveDay}"><br>
		<input type="text" name="bankAccountType" value="${temp.bankAccountType}"><br>
		<input type="text" name="bankAccountNumber" value="${temp.bankAccountNumber}"><br>
	    <input type="text" name="bankName" value="${temp.bankName}"><br>
		<input type="text" name="accountName" value="${temp.accountName}"><br>
		<input type="text" name="manualSettle" value="${temp.manualSettle}"><br>
		<input type="file" name="file">银行卡正面照<br>
		<input type="file" name="file">银行卡正面照<br>
		<input type="file" name="file">银行卡正面照<br>
		<input type="file" name="file">银行卡正面照<br>
		<input type="text" name="auditStatus" value="${temp.auditStatus}"><br>
		<input type="text" name="v_sign" value="${temp.v_sign}"><br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

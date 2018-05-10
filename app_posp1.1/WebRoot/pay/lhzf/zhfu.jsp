<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%
	request.setCharacterEncoding("UTF-8");
%>
</head>
<body>
<center><font>跳转中...</font></center><!-- style="display: none" -->
<form id='pay_form' name='pay_form' action='<%=request.getAttribute("requestUrl")%>' method='POST' style="display: none">
<input  name='idNo' value='<%=request.getAttribute("idNo")%>'>
<input  name='idType' value='<%=request.getAttribute("idType")%>'>
<input  name='payeeIdType' value='<%=request.getAttribute("payeeIdType")%>'>
<input  name='orderDesc' value='<%=request.getAttribute("orderDesc")%>'>
<input  name='remark' value='<%=request.getAttribute("remark")%>'>
<input  name='transInfo' value='<%=request.getAttribute("transInfo")%>'>
<input  name='idName' value='<%=request.getAttribute("idName")%>'>
<input  name='cardType' value='<%=request.getAttribute("cardType")%>'>
<input  name='transAmt' value='<%=request.getAttribute("transAmt")%>'>
<input  name='currency' value='<%=request.getAttribute("currency")%>'>
<input  name='sign' value='<%=request.getAttribute("sign")%>'>
<input  name='cardNo' value='<%=request.getAttribute("cardNo")%>'>
<input  name='serialNo' value='<%=request.getAttribute("serialNo")%>'>
<input  name='transDate' value='<%=request.getAttribute("transDate")%>'>
<input  name='orderNo' value='<%=request.getAttribute("orderNo")%>'>
<input  name='transId' value='<%=request.getAttribute("transId")%>'>
<input  name='transTime' value='<%=request.getAttribute("transTime")%>'>
<input  name='extraInfo' value='<%=request.getAttribute("extraInfo")%>'>
<input  name='payeeCardNo' value='<%=request.getAttribute("payeeCardNo")%>'>
<input  name='merKey' value='<%=request.getAttribute("merKey")%>'>
<input  name='notifyUrl' value='<%=request.getAttribute("notifyUrl")%>'>
<input  name='returnUrl' value='<%=request.getAttribute("returnUrl")%>'>
<input  name='mobileNo' value='<%=request.getAttribute("mobileNo")%>'>
<input  name='userRate' value='<%=request.getAttribute("userRate")%>'>
<input  name='userFee' value='<%=request.getAttribute("userFee")%>'>
<input  name='payeeCurrency' value='<%=request.getAttribute("payeeCurrency")%>'>
<input  name='bankCode' value='<%=request.getAttribute("bankCode")%>'>
<input  name='payeeBankCode' value='<%=request.getAttribute("payeeBankCode")%>'>
<input  name='payeeIdNo' value='<%=request.getAttribute("idNo")%>'>

<input  name='payeeIdName' value='<%=request.getAttribute("payeeIdName")%>'>
<input  name='payeeMobileNo' value='<%=request.getAttribute("payeeMobileNo")%>'>

</form>
<script language='javascript'>window.onload=function(){document.pay_form.submit();}</script>
</body></html>
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
	<form action="${pageContext.request.contextPath }/shyb_app/paySign.action" method="post">
	    <input type="text" name="customerNumber" value="100341512318531"> <br>
	    <input type="text" name="subContractId" value="10020692379"> <br>
	    <input type="text" name="requestId" value="<%=UtilDate.PayRandomOrders()%>">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
	    <select name="source">
	      <option value="D" >卡号收款</option>
	      <option value="B" selected="selected">店主代付</option>
	      <option value="S">短信收款</option>
	      <option value="T">二维码收款</option>
	    </select><br>
		<input type="text" name="amount" value="10">金额<br>
		<input type="text" name="mobileNumber" value="18902195076">手机号<br>
			    <select name="mcc">
	      <option value="5311" selected="selected">百货商店</option>
	      <option value="4511">航空公司</option>
	      <option value="4733">大型景区售票</option>
	    </select><br>
		<input type="text" name="callBackUrl" value="http://60.28.24.164:8101/app_posp/pay/quick/index.jsp">回调地址<br>
		<input type="text" name="webCallBackUrl" value="http://60.28.24.164:8101/app_posp/pay/quick/index.jsp">前台通知地址<br>	 
		<input type="text" name="payerBankAccountNo" value="6212260302030264816">卡号<br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

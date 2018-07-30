<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.quickpay.conformityQucikPay.util.UtilDate" pageEncoding="UTF-8"%>
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
	<form action="${pageContext.request.contextPath }/conformity/wap_pay_sign.action" method="post">
	    <input type="text" name="v_version" value="1.0.0.0"> <br>
	    <input type="text" name="v_mid" value="100341512318531">商户号<br>
	    <input type="text" name="v_oid" value="<%=UtilDate.PayRandomOrder()%>">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
	    <input type="text" name="v_txnAmt" value="1">订单金额<br>
		<input type="text" name="v_time" value="<%=UtilDate.getOrderNum()%>">订单时间(不为空,一共14 位，格式为：年[4位]月[2 位]日[2 位]时[2 位]分[2 位]秒[2位])<br>
		<input type="text" name="v_productDesc" value="1234567890">商品名称<br>
		<input type="text" name="v_card" value="6222380049724441">银行卡卡号<br>
		<select name="v_cardType">
			<option value="1" selected="selected">借记卡</option>
			<option value="2">贷记卡</option>
		</select><br>		 
		<select name="v_type">
			<option value="0" selected="selected">D0</option>
			<option value="1">T1</option>
		</select><br> 
		<input type="text" name="v_attach" value="123456789">附加码<br>
	    <input type="text" name="v_notify_url" value="http://60.28.24.164:8102/app_posp/TFBController/returnUrl.action">支付结果前台通知页面(不为空，必须是合法URL,字节数不超过256)<br>
		<input type="text" name="v_url" value="http://60.28.24.164:8102/app_posp/TFBController/returnUrl.action">支付结果后台通知地址(必须是合法URL,字节数不超过256)<br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

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
	<form action="${pageContext.request.contextPath }/clearPay/clear_pay_sign.action" method="post">
	    <input type="text" name="v_version" value="1.0.0.0"> <br>
	    <input type="text" name="v_mid" value="100341512318531">商户号<br>
	    <input type="text" name="v_oid" value="<%=UtilDate.PayRandomOrder()%>">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
	    <input type="text" name="v_txnAmt" value="0.2">交易金额<br>
		<input type="text" name="v_time" value="<%=UtilDate.getOrderNum()%>">交易时间(不为空,一共14 位，格式为：年[4位]月[2 位]日[2 位]时[2 位]分[2 位]秒[2位])<br>		
		<input type="text" name="v_cardNo" value="4984511138026651">交易卡号<br>
		<input type="text" name="v_realName" value="尚延超">帐户名<br>		 
		<input type="text" name="v_cert_no" value="410324199203231912">证件号<br>
		<input type="text" name="v_orderInfo" value="订单信息">订单信息<br>
		<input type="text" name="v_phone" value="18902195076">手机号<br>
	    <input type="text" name="v_notify_url" value="http://60.28.24.164:8102/app_posp/TFBController/returnUrl.action">支付结果前台通知页面(不为空，必须是合法URL,字节数不超过256)<br>
		<input type="text" name="v_url" value="http://60.28.24.164:8102/app_posp/TFBController/returnUrl.action">支付结果后台通知地址(必须是合法URL,字节数不超过256)<br>
		<input type="text" name="v_cvn2" value="698">卡背面三位数(信用卡是需要填写)<br>
		<input type="text" name="v_expired" value="0622">卡有效期(信用卡是需要填写)<br>
		<input type="text" name="v_attach" value="测试">附加码<br>
		<input type="text" name="v_userId" value="<%=System.currentTimeMillis()%>">userId<br>
		<input type="text" name="v_userFee" value="0.3">交易费率<br>
		<input type="text" name="v_settleCardNo" value="6212260302030264816">结算卡号<br>
		<input type="text" name="v_settleName" value="尚延超">结算帐户名<br>
		<input type="text" name="v_settlePmsBankNo" value="102110001181">接算卡联行号<br>		
		<input type="text" name="v_settleUserFee" value="0.5">接算手续费<br>	
		<input type="text" name="v_settlePhone" value="18902195076">结算手机号<br>		
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

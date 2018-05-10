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
	<form action="${pageContext.request.contextPath }/shyb_app/registerSign.action" method="post">
	    <input type="text" name="customerNumber" value="100341512318531"> <br>
	    <input type="text" name="mailStr" value="1815822346@qq.com">电子邮箱<br>
	    <input type="text" name="requestId" value="<%=UtilDate.PayRandomOrders()%>">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br>
	    <select name="customerType">
	      <option value="PERSON" selected="selected">个人</option>
	      <option value="ENTERPRISE">企业</option>
	      <option value="INDIVIDUAL">个体工商户</option>
	    </select><br>
		<input type="text" name="businessLicence" value="">营业执照号<br>
		<input type="text" name="bindMobile" value="18902195076">手机号<br>
		<input type="text" name="signedName" value="尚延超">签约名<br>
		<input type="text" name="linkMan" value="尚延超">推荐人<br>	 
		<input type="text" name="idCard" value="410324199203231912">证件号<br>
		<input type="text" name="legalPerson" value="尚延超">法人姓名<br>
		<input type="text" name="minSettleAmount" value="0.01">起结金额<br>
		<input type="text" name="riskReserveDay" value="0">结算周期<br>
			    <select name="bankAccountType">
	      <option value="PrivateCash" selected="selected">对私</option>
	      <option value="PublicCash">对公</option>
	    </select><br>
	    <input type="text" name="bankAccountNumber" value="6212260302030264816">银行卡号<br>
		<input type="text" name="bankName" value="工商银行">银行卡开户行<br>
		<input type="text" name="accountName" value="尚延超">开户名<br>
		<input type="text" name="manualSettle" value="">T1 自助结算<br>
		<input type="text" name="auditStatus" value="success">审核状态<br>
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

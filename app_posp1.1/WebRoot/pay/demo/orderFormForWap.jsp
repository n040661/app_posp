
<%@ page language="java" contentType="text/html; charset=UTF-8" import="xdt.quickpay.hengfeng.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商户订单数据(模拟)</title>
</head>
<body>
	商户订单数据
	<form action="${pageContext.request.contextPath }/quick/signForWap.action" method="post">
		<input type="text" name="pageurl" value="http://60.28.24.164:8101/app_posp/pay/demo//result.jsp">支付结果前台通知页面(不为空，必须是合法URL,字节数不超过256)<br>
		<input type="text" name="bgurl" value="http://60.28.24.164:8101/app_posp/index.jsp">支付结果后台通知地址(必须是合法URL,字节数不超过256)<br>
		<input type="text" name="pid" value="100341512318531">商户号<br>
		<input type="text" name="transactionid" value="<%=HFUtil.randomOrder() %>">商户订单号(不为空，只允许使用字母、数字、-、_,并以字母或数字开头，长度小于32，成功支付的订单不允许重复支付)<br> 
		<input type="text" name="orderamount" value="10.00">订单金额(不能为空，必须是大于0.00浮点数DECIMAL(10,2))<br>
		<input type="text" name="ordertime" value="<%=HFUtil.dateTime() %>">订单时间(不为空,一共14 位，格式为：年[4位]月[2 位]日[2 位]时[2 位]分[2 位]秒[2位])<br>
		<input type="text" name="productname" value="测试商品">产品名称(字节数不能超过256字节)<br> 
		<input type="text" name="productnum" value="1">商品数量(1到6位整型数字)<br>
		<input type="text" name="productdesc" value="测试商品">商品描述(字节数不能大于400)<br>
		<input type="text" name="bankno" value="6217000830000123038">银行卡号<br>
		<select name="paytype">
			<option value="13" selected="selected">手机wap支付</option>
		</select><br>
		<select name="bankid">
			<option value="">不填</option>
		</select><br> 
		<input type="submit" value="sharingPay">
	</form>
</body>
</html>

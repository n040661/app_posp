<%@ page language="java" contentType="text/html; charset=utf-8"
	import="xdt.quickpay.hengfeng.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>资金代收协议签约</title>
</head>
<body>

	<form
		action="${pageContext.request.contextPath}/dk/queryOrderStatusdaiKou.action"
		method="post">
		<table>
			<tr>
				<td>商户号:</td>
				<td><input type="text" value="10012341630" id="merchantId"
					name="merchantId"></td>
			</tr>
			<tr>
				<td>原订单号:</td>
				<td><input type="text" value="20171121104518768"
					id="orderId" name="orderId"></td>
			</tr>
			<tr>
				<td>原订单日期:</td>
				<td><input type="text" value="20171121" id="orderDate"
					name="orderDate"></td>
			</tr>
			<tr>
				<td>订单类型:</td>
				<td><select id="orderType" name="orderType">
						<option selected="selected" value="pay">支付</option>
						<option value="ref">退款</option>
				</select></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="确认" /></td>
			</tr>
		</table>
	</form>

</body>
</html>
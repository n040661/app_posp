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
		action="${pageContext.request.contextPath}/dk/refund.action"
		method="post">
		<table>
			<tr>
				<td>商户号:</td>
				<td><input type="text" value="10012341630" id="merchantId"
					name="merchantId"></td>
			</tr>
			<tr>
				<td>订单号:</td>
				<td><input type="text" value="<%=HFUtil.HFrandomOrder()%>"
					id="orderId" name="orderId"></td>
			</tr>
			<tr>
				<td>原商户订单号:</td>
				<td><input type="text" value="测试" id="orderNo"
					name="orderNo"></td>
			</tr>
			<tr>
				<td>退款金额:</td>
				<td><input type="text" value="500" id="amount" name="amount"></td>
			</tr>
			<tr>
				<td>退款原因:</td>
				<td><input type="text" value="测试" id="reason" name="reason"></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="确认" /></td>
			</tr>
		</table>
	</form>

</body>
</html>
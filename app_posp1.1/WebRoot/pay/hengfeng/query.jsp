<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="xdt.quickpay.hengfeng.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ryt全渠道PC/WAP跳转银联页面支付产品示例</title>

</head>
<body>
	<form id="form"
		action="${pageContext.request.contextPath }/hfquick/hfsignconsume.action"
		method="post">
		<center>
			<table>
				<tr>
					<td>商户号：</td>
					<td><input type="text" name="merchantId" value="10023112864"></td>
				</tr>
				<tr>
					<td>订单交易时间：</td>
					<td><input type="text" name="txnTime"
						value="20170925101816"></td>
				</tr>
				<tr>
					<td>订单号：</td>
					<td><input type="text" name="orderId"
						value="Q1709251018124181786"></td>
				</tr>
				<tr>
					<td>交易类型:</td>
					<td><input type="text" name="payType"
						value="10"></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><input type="submit"
						name="confirm" value="提交"></td>
				</tr>
			</table>
		</center>
	</form>
</body>
</html>